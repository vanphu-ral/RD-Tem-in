import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { forkJoin, Observable, of, throwError } from "rxjs";
import { catchError, map, switchMap } from "rxjs/operators";
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
} from "app/entities/generate-tem-in/receiving-supplies/tem-panacim-export.util";
import {
  InfoTemNccService,
  toText,
  VendorLabelInfoDto,
} from "./info-tem-ncc.service";
import { boxLocation } from "../shared/box-location.util";

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
  ) {}

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

  /** Dựng nội dung CSV gửi PanaCIM (không gửi) — đúng file sẽ upload lên /api/csv-upload */
  buildPanacimCsv(
    entries: SendBoxEntry[],
    fileTag: string,
  ): { fileName: string; content: string; rowCount: number } {
    const rows = this.toPanacimRows(entries);
    return {
      fileName: `CSV_UP_Panacim_${fileTag}_${new Date().toISOString().split("T")[0]}.csv`,
      content: generateTemPanacimCsvContent(rows, (date) =>
        this.formatDateYyyyMmDd(date),
      ),
      rowCount: rows.length,
    };
  }

  private toPanacimRows(entries: SendBoxEntry[]): TemPanacimExportInput[] {
    return entries.map((e) => ({
      reelId: toText(e.record.reelId),
      partNumber: toText(e.partNumber) || toText(e.record.partNumber),
      vendor: toText(e.record.vendor) || toText(e.vendorCode),
      lot: toText(e.record.lot),
      userData1: toText(e.record.userData1),
      userData2: toText(e.record.userData2),
      userData3: toText(e.record.userData3),
      userData4: toText(e.record.userData4),
      userData5: toText(e.record.userData5),
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
