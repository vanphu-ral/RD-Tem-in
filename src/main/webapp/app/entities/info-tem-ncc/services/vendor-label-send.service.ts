import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { forkJoin, Observable, of, throwError } from "rxjs";
import { catchError, map, switchMap, take } from "rxjs/operators";
import { AccountService } from "app/core/auth/account.service";
import { Account } from "app/core/auth/account.model";
import {
  GoodsReceiptPoLine,
  GoodsReceiptPoPayload,
  ReceivingSuppliesService,
  validateStorageUnitForSap,
} from "app/entities/generate-tem-in/service/receiving-supplies.service";
import {
  generateTemPanacimCsvBlob,
  generateTemPanacimCsvContent,
  TemPanacimExportInput,
  toPanacimText,
} from "app/entities/generate-tem-in/receiving-supplies/tem-panacim-export.util";
import {
  DeliveryNotificationDetailDto,
  InfoTemNccService,
  SapPor1R1Dto,
  toText,
  VendorLabelInfoDto,
} from "./info-tem-ncc.service";
import { boxLocation } from "../shared/box-location.util";
import { buildOrderStatusPayload } from "../shared/order-completion.util";
import {
  buildDefaultUserData4,
  DEFAULT_USERDATA,
} from "../shared/scan-item-columns.util";

/** 1 thùng (vendor-label-info) cần gửi + thông tin vật tư / PO của nó */
export interface SendBoxEntry {
  record: VendorLabelInfoDto;
  /** Mã SAP (itemCode dòng PO) */
  sapCode: string;
  /** Part number OITM của vật tư (rỗng → dùng partNumber của thùng) */
  partNumber: string;
  /** Mã PO của vật tư (docEntry) */
  poCode: string;
  /** Vendor fallback khi thùng không có vendor */
  vendorCode: string;
}

/** Body gửi SAP qua gateway: đơn → PO → vật tư */
export interface SapGatewayPayload {
  noticeNo: string;
  deliveryNotificationId: number;
  vendorName: string;
  /** Mã NCC của đơn (đơn không lưu mã → lineVendor của dòng PO) */
  vendorCode: string;
  factory: string;
  entryDate: string;
  /** User SSO chọn ở màn xác nhận gửi SAP (mặc định user đang đăng nhập) */
  createdBy: string;
  /** Người đang đăng nhập bấm gửi */
  sentBy: string;
  pos: SapGatewayPo[];
}

export interface SapGatewayPo {
  docEntry: number;
  docNum: string;
  items: SapGatewayItem[];
}

export interface SapGatewayItem {
  lineNum: number;
  itemCode: string;
  itemName: string;
  uom: string;
  whsCode: string;
  /** SL theo PO */
  quantityPo: number;
  /** Số lô — 1 vật tư nhiều lô → lặp lại item, mỗi lô 1 item */
  lotNumber: string;
  /** Hạn sử dụng của lô (yyyy-MM-dd) */
  expiredDate: string;
  /** Tổng SL đã scan của vật tư trong lô này (mọi đợt) */
  quantityScanned: number;
  /** SL gửi đợt này = tổng SL các thùng gửi đợt này trong lô */
  quantitySend: number;
  /** Ngày giao hàng thực tế = thời điểm ấn gửi đợt này (ISO); khác entryDate = ngày tạo phiếu */
  deliveryDate: string;
}

/**
 * TẠM THỜI (test gateway): cho gửi SAP lại cả thùng đã gửi (sapSendStatus = true).
 * Test xong đặt lại false → chỉ gửi thùng chưa gửi như cũ.
 */
export const SAP_ALLOW_RESEND = true;

export interface SendResult {
  count: number;
  /** false nếu gửi thành công nhưng lưu trạng thái đã gửi thất bại */
  statusSaved: boolean;
}

/** Trạng thái gửi có thể là boolean / string / number */
export function isSendFlagOn(value: unknown): boolean {
  const t = toText(value).toLowerCase();
  return t !== "" && t !== "false" && t !== "0";
}

/**
 * Gửi SAP / PanaCIM cho thùng vendor-label-info — cùng logic & payload với
 * generate-tem-in/receiving-supplies (executeSendSap / uploadTemRowsToPanacim).
 */
@Injectable({ providedIn: "root" })
export class VendorLabelSendService {
  constructor(
    private http: HttpClient,
    private receivingService: ReceivingSuppliesService,
    private infoTemNccService: InfoTemNccService,
    private accountService: AccountService,
  ) {}

  /**
   * Gửi SAP qua gateway: dựng body từ chi tiết đơn mới nhất (bỏ thùng đã gửi ở nơi khác) →
   * POST post-goods-receipt-po/gateway → đánh dấu sapSendStatus = true cho các thùng đợt này.
   */
  sendSapGateway(
    entries: SendBoxEntry[],
    createdBy: string,
  ): Observable<SendResult> {
    const orderId = entries[0]?.record.deliveryNotificationId ?? null;
    if (orderId === null) {
      return throwError(() => new Error("Thùng chưa thuộc đơn giao hàng nào."));
    }
    const account$: Observable<Account | null> = this.accountService
      .identity()
      .pipe(take(1));
    return account$.pipe(
      switchMap((account) =>
        this.sendSapGatewayAs(
          orderId,
          entries,
          account?.login ?? "unknown",
          createdBy,
        ),
      ),
    );
  }

  /** Dựng body gửi gateway (không POST); thùng thiếu / sai vị trí → lỗi, không gửi */
  buildSapGatewayPayload(
    deliveryNotificationId: number,
    entries: SendBoxEntry[],
    user: string,
    createdBy: string,
  ): Observable<{
    payload: SapGatewayPayload;
    sending: SendBoxEntry[];
    detail: DeliveryNotificationDetailDto;
  }> {
    const invalid = entries.filter((e) =>
      validateStorageUnitForSap(boxLocation(e.record)),
    );
    if (invalid.length) {
      const sample = invalid
        .slice(0, 3)
        .map((e) => toText(e.record.reelId))
        .join(", ");
      return throwError(
        () =>
          new Error(
            `Còn ${invalid.length} thùng chưa gán vị trí hợp lệ (${sample}${invalid.length > 3 ? "…" : ""}) — gán vị trí trước khi gửi SAP.`,
          ),
      );
    }
    return this.infoTemNccService
      .getDeliveryNotificationDetail(deliveryNotificationId)
      .pipe(
        map((detail) => {
          const lines = detail.sapPor1R1List ?? [];
          const unsent = new Set(
            lines
              .flatMap((l) => l.vendorLabelInfoList ?? [])
              .filter((b) => SAP_ALLOW_RESEND || !isSendFlagOn(b.sapSendStatus))
              .map((b) => b.id),
          );
          const sending = entries.filter((e) => unsent.has(e.record.id));
          if (!sending.length) {
            throw new Error("Các thùng đã được gửi SAP trước đó.");
          }
          return {
            payload: this.toGatewayPayload(
              detail,
              lines,
              sending,
              user,
              createdBy,
            ),
            sending,
            detail,
          };
        }),
      );
  }

  /**
   * Gửi SAP: lấy DocEntry theo PO (sap-po-info) → POST post-goods-receipt-po { OPDN }
   * (1 dòng / thùng) → đánh dấu sapSendStatus = true cho các thùng.
   */
  sendSap(entries: SendBoxEntry[]): Observable<SendResult> {
    return this.buildSapPayload(entries).pipe(
      switchMap((payload) => this.receivingService.postGoodsReceiptPo(payload)),
      switchMap(() =>
        this.markSent(entries, { sapSendStatus: true }).pipe(
          map((statusSaved) => ({ count: entries.length, statusSaved })),
        ),
      ),
    );
  }

  /**
   * Dựng payload gửi SAP (không POST): validate StorageUnit / PO → GET sap-po-info lấy DocEntry
   * → { OPDN: [...] } đúng như sẽ gửi tới post-goods-receipt-po.
   */
  buildSapPayload(entries: SendBoxEntry[]): Observable<GoodsReceiptPoPayload> {
    for (const e of entries) {
      const sapUnitError = validateStorageUnitForSap(boxLocation(e.record));
      if (sapUnitError) {
        const label = `${e.sapCode}${e.record.lot ? ` / lô ${toText(e.record.lot)}` : ""}`;
        return throwError(() => new Error(`${label}: ${sapUnitError}`));
      }
      if (!toText(e.poCode) || toText(e.poCode) === "-") {
        return throwError(
          () =>
            new Error(`Thùng ${toText(e.record.reelId)} chưa có PO hợp lệ.`),
        );
      }
    }
    const poNumbers = [...new Set(entries.map((e) => toText(e.poCode)))];
    return forkJoin(
      poNumbers.map((po) =>
        this.receivingService.getSapPoInfo(po).pipe(
          map((res) => ({
            po,
            docEntry: Number.parseInt(res?.poInfo?.oporDocEntry ?? "", 10),
          })),
        ),
      ),
    ).pipe(
      switchMap((poRows) => {
        const docEntryByPo = new Map(
          poRows.map((row) => [row.po, row.docEntry]),
        );
        for (const po of poNumbers) {
          const docEntry = docEntryByPo.get(po);
          if (!docEntry || Number.isNaN(docEntry)) {
            throw new Error(
              `Không lấy được DocEntry từ PO ${po}. Kiểm tra lại mã PO.`,
            );
          }
        }
        const opdn: GoodsReceiptPoLine[] = entries.map((e) =>
          this.buildOpdnLine(e, docEntryByPo.get(toText(e.poCode))!),
        );
        if (!opdn.length) {
          throw new Error("Không có dữ liệu tem để gửi SAP.");
        }
        return of<GoodsReceiptPoPayload>({ OPDN: opdn });
      }),
    );
  }

  /**
   * Gửi PanaCIM: dựng CSV (mẫu CSV_UP_Panacim) → POST /api/csv-upload (FormData "file")
   * → response.success thì đánh dấu panaSendStatus = true.
   */
  sendPanacim(
    entries: SendBoxEntry[],
    fileTag: string,
  ): Observable<SendResult> {
    return this.withCompleteDetailRecords(entries).pipe(
      switchMap((fresh) => this.postPanacim(fresh, fileTag)),
    );
  }

  /** Dựng nội dung CSV gửi PanaCIM (không gửi) — đúng file sẽ upload lên /api/csv-upload */
  buildPanacimCsv(
    entries: SendBoxEntry[],
    fileTag: string,
  ): { fileName: string; content: string; rowCount: number } {
    const rows = this.toPanacimRows(entries);
    return {
      fileName: `CSV_UP_Panacim_${this.toSafeFileTag(fileTag)}_${new Date().toISOString().split("T")[0]}.csv`,
      content: generateTemPanacimCsvContent(rows, (date) =>
        this.formatDateYyyyMmDd(date),
      ),
      rowCount: rows.length,
    };
  }

  /**
   * Lấy bản ghi thùng mới nhất từ chi tiết đơn; thùng thiếu Vị trí kho / NSX / HSD
   * → lỗi liệt kê thùng + trường thiếu, không gửi.
   */
  private withCompleteDetailRecords(
    entries: SendBoxEntry[],
  ): Observable<SendBoxEntry[]> {
    const orderIds = [
      ...new Set(entries.map((e) => e.record.deliveryNotificationId ?? null)),
    ];
    if (!orderIds.length || orderIds.includes(null)) {
      return throwError(() => new Error("Thùng chưa thuộc đơn giao hàng nào."));
    }
    return forkJoin(
      orderIds.map((id) =>
        this.infoTemNccService.getDeliveryNotificationDetail(id!),
      ),
    ).pipe(
      map((details) => {
        const byId = new Map(
          details
            .flatMap((d) => d.sapPor1R1List ?? [])
            .flatMap((l) => l.vendorLabelInfoList ?? [])
            .map((b) => [b.id, b]),
        );
        const problems: string[] = [];
        const fresh = entries.map((e) => {
          const rec = byId.get(e.record.id);
          const reel = toText(e.record.reelId);
          if (!rec) {
            problems.push(`${reel}: không có trong chi tiết đơn`);
            return e;
          }
          const missing = [
            boxLocation(rec) ? "" : "Vị trí kho",
            toText(rec.manufacturingDate) ? "" : "NSX",
            toText(rec.expirationDate) ? "" : "HSD",
          ].filter(Boolean);
          if (missing.length) {
            problems.push(`${reel}: thiếu ${missing.join(", ")}`);
          }
          return { ...e, record: rec };
        });
        if (problems.length) {
          const shown = problems.slice(0, 10).join("; ");
          throw new Error(
            `Còn ${problems.length} thùng thiếu thông tin, chưa gửi PanaCIM — ${shown}${problems.length > 10 ? "; …" : ""}`,
          );
        }
        return fresh;
      }),
    );
  }

  private postPanacim(
    entries: SendBoxEntry[],
    fileTag: string,
  ): Observable<SendResult> {
    const { fileName, rowCount } = this.buildPanacimCsv(entries, fileTag);
    const blob = generateTemPanacimCsvBlob(
      this.toPanacimRows(entries),
      (date) => this.formatDateYyyyMmDd(date),
    );
    const formData = new FormData();
    formData.append("file", blob, fileName);

    return this.http
      .post<{
        success?: boolean;
        message?: string;
      }>("/api/csv-upload", formData)
      .pipe(
        switchMap((response) => {
          if (!response?.success) {
            throw new Error(
              `Lỗi: ${response?.message ?? "Gửi PanaCIM thất bại."}`,
            );
          }
          return this.markSent(entries, { panaSendStatus: true }).pipe(
            map((statusSaved) => ({ count: rowCount, statusSaved })),
          );
        }),
      );
  }

  private toPanacimRows(entries: SendBoxEntry[]): TemPanacimExportInput[] {
    // Trống → mặc định giống dialog chi tiết lô (lot-detail-dialog) để khớp dữ liệu đang hiển thị
    return entries.map((e) => ({
      reelId: toText(e.record.reelId),
      partNumber: toText(e.partNumber) || toText(e.record.partNumber),
      vendor: toText(e.record.vendor) || toText(e.vendorCode),
      lot: toText(e.record.lot),
      userData1: toText(e.record.userData1) || DEFAULT_USERDATA,
      userData2: toText(e.record.userData2) || DEFAULT_USERDATA,
      userData3: toText(e.record.userData3) || DEFAULT_USERDATA,
      userData4: toText(e.record.userData4) || this.defaultUserData4(e),
      userData5: toText(e.record.userData5) || toText(e.poCode),
      initialQuantity:
        e.record.initialQuantity === null ||
        e.record.initialQuantity === undefined
          ? null
          : Number(e.record.initialQuantity),
      // StorageUnit PanaCIM = vị trí (như tem bên receiving-supplies)
      storageUnit: boxLocation(e.record),
      expirationDate: toText(e.record.expirationDate) || null,
      manufacturingDate: toText(e.record.manufacturingDate) || null,
      sapCode: toText(e.record.sapCode) || toText(e.sapCode),
    }));
  }

  /**
   * Tag trong tên file: bỏ dấu + thay ký tự không hợp lệ (vd số thông báo "272/KDVT-RĐ" → "272_KDVT-RD").
   * "/" trong tên file khiến backend ghi vào thư mục con không tồn tại trên share.
   */
  private toSafeFileTag(fileTag: string): string {
    return (
      toPanacimText(fileTag)
        .replace(/[^A-Za-z0-9._-]+/g, "_")
        .replace(/^_+|_+$/g, "") || "ALL"
    );
  }

  /** UD4 mặc định = Mã SAP + "-" + MFG (ddMMyyyy) — như lot-detail-dialog.buildUserData4 */
  private defaultUserData4(e: SendBoxEntry): string {
    const mfg = this.parseDate(e.record.manufacturingDate);
    const ymd = mfg ? this.formatDateYyyyMmDd(mfg).replace(/-/g, "") : "";
    return buildDefaultUserData4({
      sapCode: toText(e.record.sapCode) || toText(e.sapCode),
      manufacturingDate: ymd,
    });
  }

  /** PUT từng thùng với cờ đã gửi → true nếu lưu được hết */
  private markSent(
    entries: SendBoxEntry[],
    patch: Partial<VendorLabelInfoDto>,
  ): Observable<boolean> {
    const updates = entries
      .filter((e) => e.record.id)
      .map((e) =>
        this.infoTemNccService
          .updateVendorLabelInfo({
            ...e.record,
            ...patch,
            palletBoxMapping: undefined,
          })
          .pipe(
            map(() => {
              Object.assign(e.record, patch);
              return true;
            }),
            catchError(() => of(false)),
          ),
      );
    if (!updates.length) {
      return of(true);
    }
    return forkJoin(updates).pipe(map((results) => results.every(Boolean)));
  }

  private sendSapGatewayAs(
    deliveryNotificationId: number,
    entries: SendBoxEntry[],
    user: string,
    createdBy: string,
  ): Observable<SendResult> {
    return this.buildSapGatewayPayload(
      deliveryNotificationId,
      entries,
      user,
      createdBy,
    ).pipe(
      switchMap(({ payload, sending, detail }): Observable<SendResult> => {
        const post$: Observable<string> =
          this.receivingService.postGoodsReceiptGateway(payload);
        return post$.pipe(
          switchMap(
            (): Observable<SendResult> =>
              forkJoin([
                this.markSent(sending, { sapSendStatus: true }),
                this.countSapSubmission(detail),
              ]).pipe(
                map(
                  ([boxesSaved, countSaved]): SendResult => ({
                    count: sending.length,
                    statusSaved: boxesSaved && countSaved,
                  }),
                ),
              ),
          ),
        );
      }),
    );
  }

  /** PUT đơn cha tăng numberOfSapSubmissions thêm 1 → true nếu lưu được */
  private countSapSubmission(
    detail: DeliveryNotificationDetailDto,
  ): Observable<boolean> {
    const payload = buildOrderStatusPayload(detail, toText(detail.status));
    payload.status = detail.status;
    payload.numberOfSapSubmissions =
      (Number(detail.numberOfSapSubmissions) || 0) + 1;
    return this.infoTemNccService.updateDeliveryNotification(payload).pipe(
      map((): boolean => true),
      catchError(() => of(false)),
    );
  }

  /**
   * SAP: cùng mã vật tư + số lô chỉ được 1 HSD. Lô (của thùng gửi đợt này) có thùng mang
   * HSD khác nhau / thiếu HSD → lỗi, liệt kê để người dùng sửa HSD chung cho lô.
   */
  private validateLotExpiry(
    lines: SapPor1R1Dto[],
    sending: SendBoxEntry[],
  ): void {
    const sendingIds = new Set(sending.map((e) => e.record.id));
    const problems: string[] = [];
    for (const line of lines) {
      const boxes = line.vendorLabelInfoList ?? [];
      const lots = new Set(
        boxes.filter((b) => sendingIds.has(b.id)).map((b) => toText(b.lot)),
      );
      for (const lot of lots) {
        const dates = new Set(
          boxes
            .filter((b) => toText(b.lot) === lot)
            .map((b) =>
              this.formatDateYyyyMmDd(this.parseDate(b.expirationDate)),
            ),
        );
        const label = `${toText(line.itemCode)} / lô ${lot || "(trống)"}`;
        if (dates.has("")) {
          problems.push(`${label}: có thùng chưa có HSD`);
        } else if (dates.size > 1) {
          problems.push(
            `${label}: ${dates.size} HSD (${[...dates].join(", ")})`,
          );
        }
      }
    }
    if (problems.length) {
      throw new Error(
        `Mỗi lô chỉ được 1 hạn sử dụng khi gửi SAP — sửa HSD chung cho lô rồi gửi lại: ${problems.join("; ")}`,
      );
    }
  }

  private toGatewayPayload(
    detail: DeliveryNotificationDetailDto,
    lines: SapPor1R1Dto[],
    sending: SendBoxEntry[],
    user: string,
    createdBy: string,
  ): SapGatewayPayload {
    this.validateLotExpiry(lines, sending);
    const entryById = new Map(sending.map((e) => [e.record.id, e]));
    const pos = new Map<string, SapGatewayPo>();
    const deliveryDate = new Date().toISOString();
    for (const line of lines) {
      const allBoxes = line.vendorLabelInfoList ?? [];
      const lineEntries = allBoxes
        .map((b) => entryById.get(b.id))
        .filter((e): e is SendBoxEntry => !!e);
      if (!lineEntries.length) {
        continue;
      }
      const docEntry = Number.parseInt(toText(line.docEntry), 10);
      if (Number.isNaN(docEntry)) {
        throw new Error(
          `Dòng vật tư ${toText(line.itemCode)} chưa có DocEntry của PO.`,
        );
      }
      const key = String(docEntry);
      const po: SapGatewayPo = pos.get(key) ?? {
        docEntry,
        docNum: toText(line.baseDocNum),
        items: [],
      };
      pos.set(key, po);
      // Vật tư có nhiều lô → mỗi số lô là 1 item, chỉ lô có thùng gửi đợt này
      // (SAP: 1 số lô chỉ 1 HSD — đã kiểm tra ở validateLotExpiry)
      const lotKey = (b: VendorLabelInfoDto): string => toText(b.lot);
      const sendingLots = new Map<string, VendorLabelInfoDto[]>();
      for (const e of lineEntries) {
        const k = lotKey(e.record);
        sendingLots.set(k, [...(sendingLots.get(k) ?? []), e.record]);
      }
      const sumQty = (list: VendorLabelInfoDto[]): number =>
        list.reduce((sum, b) => sum + Number(b.initialQuantity ?? 0), 0);
      sendingLots.forEach((lotBoxes, k) => {
        po.items.push({
          lineNum: Number(line.lineNum ?? 0),
          itemCode: toText(line.itemCode),
          itemName: toText(line.dscription),
          uom: toText(line.uomCode),
          whsCode: toText(line.whsCode),
          quantityPo: Number(line.quantity ?? 0),
          lotNumber: toText(lotBoxes[0].lot),
          expiredDate: this.formatDateYyyyMmDd(
            this.parseDate(lotBoxes[0].expirationDate),
          ),
          quantityScanned: sumQty(allBoxes.filter((b) => lotKey(b) === k)),
          quantitySend: sumQty(lotBoxes),
          deliveryDate,
        });
      });
    }
    return {
      noticeNo: toText(detail.deliveryNotificationCode),
      deliveryNotificationId: detail.id,
      vendorName: toText(detail.vendorName),
      // Mã NCC của đơn → đơn cũ chưa lưu mã: vendor trên tem → lineVendor của PO (có thể là NCC khác)
      vendorCode:
        toText(detail.vendorCode) ||
        toText(sending.find((e) => toText(e.record.vendor))?.record.vendor) ||
        toText(lines.find((l) => toText(l.lineVendor))?.lineVendor),
      factory: toText(detail.factory),
      entryDate: toText(detail.entryDate),
      createdBy,
      sentBy: user,
      pos: [...pos.values()],
    };
  }

  private buildOpdnLine(e: SendBoxEntry, docEntry: number): GoodsReceiptPoLine {
    const rec = e.record;
    return {
      Vendor: toText(rec.vendor) || toText(e.vendorCode),
      DocEntry: docEntry,
      ItemCode: toText(e.sapCode) || toText(rec.sapCode),
      Quantity: Number(rec.initialQuantity ?? 1),
      LotNum: toText(rec.lot),
      ReelID: toText(rec.reelId),
      PartNumber: toText(e.partNumber) || toText(rec.partNumber),
      ExpirationDate: this.formatSapDateTime(
        this.parseDate(rec.expirationDate),
      ),
      ManufacturingDate: this.formatSapDateTime(
        this.parseDate(rec.manufacturingDate),
      ),
      StorageUnit: boxLocation(rec),
    };
  }

  /** Giống receiving-supplies: yyyy-MM-ddT00:00:00, không có ngày → 0001-01-01T00:00:00 */
  private formatSapDateTime(date: Date | null): string {
    if (!date) {
      return "0001-01-01T00:00:00";
    }
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, "0");
    const d = String(date.getDate()).padStart(2, "0");
    return `${y}-${m}-${d}T00:00:00`;
  }

  private formatDateYyyyMmDd(value: Date | string | null): string {
    if (!value) {
      return "";
    }
    if (value instanceof Date) {
      const y = value.getFullYear();
      const m = String(value.getMonth() + 1).padStart(2, "0");
      const d = String(value.getDate()).padStart(2, "0");
      return `${y}-${m}-${d}`;
    }
    return value.trim();
  }

  /** yyyyMMdd / yyyy-MM-dd / dd/MM/yyyy → Date */
  private parseDate(value: unknown): Date | null {
    const raw = toText(value);
    if (!raw) {
      return null;
    }
    const slash = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(raw);
    if (slash) {
      return new Date(Number(slash[3]), Number(slash[2]) - 1, Number(slash[1]));
    }
    const digits = raw.replace(/\D/g, "");
    if (digits.length >= 8 && Number(digits.slice(0, 4)) > 1900) {
      return new Date(
        Number(digits.slice(0, 4)),
        Number(digits.slice(4, 6)) - 1,
        Number(digits.slice(6, 8)),
      );
    }
    return null;
  }
}
