import {
  Component,
  Inject,
  OnInit,
  ViewChild,
  ViewChildren,
  QueryList,
  ElementRef,
  AfterViewChecked,
} from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { MatDatepicker } from "@angular/material/datepicker";
import { forkJoin, Observable, of } from "rxjs";
import { catchError, map } from "rxjs/operators";
import { AccountService } from "app/core/auth/account.service";
import {
  CreateVendorTemDetailPayload,
  ManagerTemNccService,
} from "app/entities/list-material/services/info-tem-ncc.service";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { WarehouseCacheService } from "app/entities/list-material/services/warehouse-cache.service";
import { buildDefaultUserData4 } from "../shared/scan-item-columns.util";
import { locationFields } from "../shared/box-location.util";
import {
  InfoTemNccService,
  toText,
  VendorLabelInfoDto,
} from "../services/info-tem-ncc.service";
import { isSendFlagOn } from "../services/vendor-label-send.service";

// ==================== INTERFACES ====================

export interface LotDetailRow {
  id: number;
  reelId: string;
  partNumber: string;
  vendor: string;
  lot: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  initialQuantity: number | string;
  msl: string;
  storageUnit: string;
  manufacturingDate: string;
  expirationDate: string;
  sapCode: string;
  sapName?: string;
  vendorQrCode?: string;
  status?: string;
  createdBy?: string;
  createdAt?: string;
  updatedBy?: string;
  poDetailId: number;
  importVendorTemTransactionsId: number;
  /**
   * Bản ghi thùng gốc (vendor-label-info). Có ở mọi dòng → Lưu = PUT /vendor-label-infos/{id};
   * không có → luồng cũ (batch vendor-tem-details).
   */
  record?: VendorLabelInfoDto;
  [key: string]: any;
}

/** Kiểu ô: text thường / số / ngày (có lịch) / vị trí kho (autocomplete IndexedDB) */
export type LotDetailCellType =
  | "text"
  | "number"
  | "date"
  | "location"
  | "status";

export interface ColumnDef {
  key: string;
  label: string;
  minWidth: number;
  editable: boolean;
  /** Tỉ lệ độ rộng cột (tính % theo tổng) */
  weight: number;
  type: LotDetailCellType;
}

export interface LotDetailDialogData {
  partNumber: string;
  manufacturingDate: string;
  rows: LotDetailRow[];
  /** Mã PO — dùng tự điền cột Mã PO (userData5). */
  poCode?: string;
}

interface EditingCell {
  row: number;
  col: string;
}

// ==================== COMPONENT ====================

@Component({
  selector: "jhi-lot-detail-dialog",
  templateUrl: "./lot-detail-dialog.component.html",
  styleUrls: ["./lot-detail-dialog.component.scss"],
})
export class LotDetailDialogComponent implements OnInit, AfterViewChecked {
  // weight: cột giá trị ngắn (SL, rank, MSL) hẹp; ReelID, tên hàng rộng — vừa 1 màn hình
  columns: ColumnDef[] = [
    {
      key: "reelId",
      label: "ReelId",
      minWidth: 0,
      editable: false,
      weight: 12,
      type: "text",
    },
    {
      key: "sapCode",
      label: "Mã SAP",
      minWidth: 0,
      editable: false,
      weight: 6,
      type: "text",
    },
    {
      key: "sapName",
      label: "Tên hàng hóa",
      minWidth: 0,
      editable: false,
      weight: 12,
      type: "text",
    },
    {
      key: "partNumber",
      label: "Part Number",
      minWidth: 0,
      editable: true,
      weight: 7,
      type: "text",
    },
    {
      key: "lot",
      label: "Lot",
      minWidth: 0,
      editable: true,
      weight: 7,
      type: "text",
    },
    {
      key: "vendor",
      label: "Vendor",
      minWidth: 0,
      editable: true,
      weight: 6,
      type: "text",
    },
    {
      key: "initialQuantity",
      label: "Số lượng",
      minWidth: 0,
      editable: true,
      weight: 4,
      type: "number",
    },
    {
      key: "userData1",
      label: "Rank Áp",
      minWidth: 0,
      editable: true,
      weight: 3.5,
      type: "text",
    },
    {
      key: "userData2",
      label: "Rank màu",
      minWidth: 0,
      editable: true,
      weight: 3.5,
      type: "text",
    },
    {
      key: "userData3",
      label: "Rank Quang",
      minWidth: 0,
      editable: true,
      weight: 3.5,
      type: "text",
    },
    {
      key: "userData4",
      label: "User Data 4",
      minWidth: 0,
      editable: true,
      weight: 7.5,
      type: "text",
    },
    {
      key: "userData5",
      label: "Mã PO",
      minWidth: 0,
      editable: true,
      weight: 6,
      type: "text",
    },
    {
      key: "msl",
      label: "MSL",
      minWidth: 0,
      editable: true,
      weight: 3,
      type: "text",
    },
    {
      key: "storageUnit",
      label: "Vị trí kho",
      minWidth: 0,
      editable: true,
      weight: 7,
      type: "location",
    },
    {
      key: "manufacturingDate",
      label: "NSX",
      minWidth: 0,
      editable: true,
      weight: 6.5,
      type: "date",
    },
    {
      key: "expirationDate",
      label: "HSD",
      minWidth: 0,
      editable: true,
      weight: 6.5,
      type: "date",
    },
    // Trạng thái gửi PanaCIM / SAP (chỉ xem)
    {
      key: "sendStatus",
      label: "Trạng thái",
      minWidth: 0,
      editable: false,
      weight: 7,
      type: "status",
    },
  ];

  rows: LotDetailRow[] = [];

  /** Gợi ý vị trí kho (IndexedDB) cho ô đang gõ */
  locationOptions: string[] = [];
  isSaving = false;
  /** Lưu qua PUT /vendor-label-infos (mọi dòng có bản ghi thùng gốc) */
  recordMode = false;
  /** Ngày đang hiện trên lịch (theo ô vừa bấm) */
  pickerStartAt: Date | null = null;
  /** Giá trị của ô ẩn gắn lịch — reset sau mỗi lần chọn để chọn lại cùng ngày vẫn nhận */
  pickerValue: Date | null = null;

  /** Holds bulk-apply values per column key */
  bulkValues: { [key: string]: any } = {};

  editingCell: EditingCell | null = null;
  @ViewChild("datePicker") datePicker?: MatDatepicker<Date>;
  @ViewChildren("cellInput") cellInputs!: QueryList<
    ElementRef<HTMLInputElement>
  >;
  private pendingFocus = false;
  private originalValue: any = null;
  private readonly totalWeight = this.columns.reduce((s, c) => s + c.weight, 0);
  /** Ô đang chọn ngày: dòng i hoặc ô áp dụng cả cột (row = -1) */
  private dateTarget: { row: number; key: string } | null = null;
  private locationSearchSeq = 0;

  constructor(
    public dialogRef: MatDialogRef<LotDetailDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: LotDetailDialogData,
    private managerTemNccService: ManagerTemNccService,
    private accountService: AccountService,
    private notificationService: NotificationService,
    private infoTemNccService: InfoTemNccService,
    private warehouseCache: WarehouseCacheService,
  ) {}

  ngOnInit(): void {
    const poCode = (this.data.poCode ?? "").trim();
    this.recordMode =
      this.data.rows.length > 0 && this.data.rows.every((r) => !!r.record?.id);
    this.rows = this.data.rows.map((r) => {
      const row = { ...r };
      if (this.recordMode) {
        // Ngày hiển thị dd/MM/yyyy (lưu lại yyyyMMdd)
        row.manufacturingDate = this.toDisplayDate(row.manufacturingDate);
        row.expirationDate = this.toDisplayDate(row.expirationDate);
      }
      row.userData1 = (row.userData1 ?? "").trim() || "NO";
      row.userData2 = (row.userData2 ?? "").trim() || "NO";
      row.userData3 = (row.userData3 ?? "").trim() || "NO";
      row.userData4 = (row.userData4 ?? "").trim() || this.buildUserData4(row);
      row.userData5 = (row.userData5 ?? "").trim() || poCode;
      row.msl = (row.msl ?? "").trim() || "1";
      return row;
    });

    this.columns.forEach((col) => (this.bulkValues[col.key] = ""));
    void this.warehouseCache.ensureSynced().catch(() => undefined);
  }

  /** Đã gửi PanaCIM (panaSendStatus) — ưu tiên bản ghi thùng gốc */
  isPanaSent(row: LotDetailRow): boolean {
    return isSendFlagOn(row.record?.panaSendStatus ?? row.panaSendStatus);
  }

  /** Đã gửi SAP (sapSendStatus) — ưu tiên bản ghi thùng gốc */
  isSapSent(row: LotDetailRow): boolean {
    return isSendFlagOn(row.record?.sapSendStatus ?? row.sapSendStatus);
  }

  colWidth(col: ColumnDef): number {
    return (col.weight / this.totalWeight) * 100;
  }

  // ==================== VỊ TRÍ KHO (autocomplete IndexedDB) ====================

  onLocationSearch(value: unknown): void {
    const term = toText(value);
    const seq = ++this.locationSearchSeq;
    if (!term) {
      this.locationOptions = [];
      return;
    }
    void this.warehouseCache
      .searchByName(term)
      .then((list) => {
        if (seq === this.locationSearchSeq) {
          this.locationOptions = [
            ...new Set(
              list
                .map(
                  (w) => toText(w.locationFullName) || toText(w.locationName),
                )
                .filter(Boolean),
            ),
          ];
        }
      })
      .catch(() => {
        if (seq === this.locationSearchSeq) {
          this.locationOptions = [];
        }
      });
  }

  /** Chọn vị trí từ gợi ý ở ô đang sửa → ghi vào ô rồi đóng ô sửa */
  onCellLocationPicked(value: string): void {
    const cell = this.editingCell;
    if (cell) {
      this.rows[cell.row][cell.col] = toText(value);
    }
    this.stopEdit();
  }

  // ==================== CHỌN NGÀY (NSX / HSD) ====================

  /** Mở lịch cho 1 ô ngày (row = -1: ô áp dụng cả cột) */
  openDatePicker(row: number, key: string, event?: Event): void {
    event?.stopPropagation();
    this.dateTarget = { row, key };
    const current = row < 0 ? this.bulkValues[key] : this.rows[row]?.[key];
    this.pickerStartAt = this.parseAnyDate(toText(current));
    this.datePicker?.open();
  }

  onDatePicked(value: Date | null): void {
    const target = this.dateTarget;
    this.dateTarget = null;
    this.pickerValue = null;
    if (!target || !value) {
      return;
    }
    const text = this.formatPickedDate(value);
    if (target.row < 0) {
      this.bulkValues[target.key] = text;
      this.applyColumn(target.key);
      return;
    }
    const row = this.rows[target.row];
    row[target.key] = text;
    if (target.key === "manufacturingDate") {
      row.userData4 = this.buildUserData4(row);
    }
  }

  ngAfterViewChecked(): void {
    if (this.pendingFocus && this.cellInputs.length > 0) {
      const input = this.cellInputs.last?.nativeElement;
      if (input) {
        input.focus();
        input.select();
        this.pendingFocus = false;
      }
    }
  }

  // ==================== BULK APPLY ====================

  applyColumn(key: string): void {
    const value = this.bulkValues[key];
    if (value === "" || value === null || value === undefined) {
      return;
    }
    // Chặn số âm cho cột số lượng
    if (key === "initialQuantity" && Number(value) < 0) {
      this.notificationService.warning("Không được nhập số âm.");
      this.bulkValues[key] = 0;
      return;
    }
    this.rows = this.rows.map((row) => {
      const next = { ...row, [key]: value };
      if (key === "sapCode" || key === "manufacturingDate") {
        next.userData4 = this.buildUserData4(next);
      }
      return next;
    });
  }

  // ==================== INLINE EDIT ====================

  startEdit(rowIndex: number, colKey: string): void {
    this.originalValue = this.rows[rowIndex][colKey];
    this.editingCell = { row: rowIndex, col: colKey };
    this.pendingFocus = true;
  }
  onQtyInput(event: Event, row: LotDetailRow, field: string): void {
    const val = Number((event.target as HTMLInputElement).value);
    if (val < 0) {
      (event.target as HTMLInputElement).value = "0";
      row[field] = 0;
      this.notificationService.warning("Không được nhập số âm.");
    }
  }
  stopEdit(): void {
    if (this.editingCell) {
      const row = this.rows[this.editingCell.row];
      if (
        this.editingCell.col === "sapCode" ||
        this.editingCell.col === "manufacturingDate"
      ) {
        row.userData4 = this.buildUserData4(row);
      }
    }
    this.editingCell = null;
    this.originalValue = null;
  }

  cancelEdit(rowIndex: number, colKey: string): void {
    if (this.originalValue !== null) {
      this.rows[rowIndex][colKey] = this.originalValue;
    }
    this.stopEdit();
  }

  moveEditNext(rowIndex: number, colKey: string, event: KeyboardEvent): void {
    event.preventDefault();
    const editableCols = this.columns.filter((c) => c.editable);
    const editableIdx = editableCols.findIndex(
      (c) => (c.key as string) === colKey,
    );

    if (editableIdx < editableCols.length - 1) {
      this.startEdit(rowIndex, editableCols[editableIdx + 1].key as string);
    } else if (rowIndex < this.rows.length - 1) {
      this.startEdit(rowIndex + 1, editableCols[0].key as string);
    } else {
      this.stopEdit();
    }
  }

  // ==================== DIALOG ACTIONS ====================

  onSave(): void {
    if (this.isSaving) {
      return;
    }
    const invalidQty = this.rows.find((row) => Number(row.initialQuantity) < 0);
    if (invalidQty) {
      this.notificationService.warning(
        "Không được nhập số âm cho trường Quantity.",
      );
      return;
    }
    // Vị trí kho phải có trong danh sách vị trí (IndexedDB)
    const locations = [
      ...new Set(this.rows.map((r) => toText(r.storageUnit)).filter(Boolean)),
    ];
    this.isSaving = true;
    this.findInvalidLocations(locations)
      .then((invalid) => {
        if (invalid.length) {
          this.isSaving = false;
          this.notificationService.error(
            `Vị trí không có trong danh sách vị trí: ${invalid.join(", ")}`,
          );
          return;
        }
        if (this.recordMode) {
          this.saveRecords();
        } else {
          this.saveLegacy();
        }
      })
      .catch(() => {
        this.isSaving = false;
        this.notificationService.error("Không kiểm tra được danh sách vị trí.");
      });
  }

  onClose(): void {
    this.dialogRef.close(null);
  }
  /** User Data 4 = Mã SAP + "-" + MFGDate (ddMMyyyy). */
  private buildUserData4(row: {
    sapCode?: string;
    manufacturingDate?: string;
  }): string {
    const sap = (row.sapCode ?? "").trim();
    const digits = String(row.manufacturingDate ?? "").replace(/\D/g, "");
    let ymd = digits;
    if (digits.length === 8 && Number(digits.slice(0, 4)) <= 1900) {
      // ddMMyyyy -> yyyyMMdd
      ymd = `${digits.slice(4, 8)}${digits.slice(2, 4)}${digits.slice(0, 2)}`;
    }
    return buildDefaultUserData4({
      sapCode: sap,
      manufacturingDate: ymd,
    });
  }

  /** PUT /vendor-label-infos/{id} cho từng thùng có thay đổi (giữ nguyên các trường khác) */
  private saveRecords(): void {
    const orNull = (v: unknown): string | null => toText(v) || null;
    const keys: Array<keyof VendorLabelInfoDto> = [
      "reelId",
      "sapCode",
      "partNumber",
      "lot",
      "vendor",
      "initialQuantity",
      "userData1",
      "userData2",
      "userData3",
      "userData4",
      "userData5",
      "msdLevel",
      "storageUnit",
      "subStorageUnit",
      "manufacturingDate",
      "expirationDate",
    ];
    const payloads: VendorLabelInfoDto[] = [];
    for (const row of this.rows) {
      const rec = row.record;
      if (!rec?.id) {
        continue;
      }
      const payload: VendorLabelInfoDto = {
        ...rec,
        reelId: orNull(row.reelId) ?? rec.reelId,
        sapCode: orNull(row.sapCode),
        partNumber: orNull(row.partNumber),
        lot: orNull(row.lot),
        vendor: orNull(row.vendor),
        initialQuantity: Number(row.initialQuantity) || 0,
        userData1: orNull(row.userData1),
        userData2: orNull(row.userData2),
        userData3: orNull(row.userData3),
        userData4: orNull(row.userData4),
        userData5: orNull(row.userData5),
        msdLevel: orNull(row.msl),
        // storageUnit = vị trí kho, subStorageUnit trống
        ...locationFields(toText(row.storageUnit)),
        manufacturingDate: orNull(this.toApiDate(row.manufacturingDate)),
        expirationDate: orNull(this.toApiDate(row.expirationDate)),
        palletBoxMapping: undefined,
      };
      if (keys.some((k) => toText(payload[k]) !== toText(rec[k]))) {
        payloads.push(payload);
      }
    }
    if (!payloads.length) {
      this.isSaving = false;
      this.notificationService.info("Không có thùng nào thay đổi.");
      return;
    }
    const requests: Array<Observable<VendorLabelInfoDto | null>> = payloads.map(
      (payload) =>
        this.infoTemNccService.updateVendorLabelInfo(payload).pipe(
          map(
            (saved): VendorLabelInfoDto => ({ ...payload, ...(saved ?? {}) }),
          ),
          catchError(() => of(null)),
        ),
    );
    forkJoin(requests).subscribe((results) => {
      this.isSaving = false;
      const saved = results.filter((r): r is VendorLabelInfoDto => !!r);
      const failed = results.length - saved.length;
      if (!saved.length) {
        this.notificationService.error(
          `Cập nhật thất bại ${failed} thùng — vui lòng thử lại.`,
        );
        return;
      }
      if (failed) {
        this.notificationService.warning(
          `Cập nhật ${saved.length}/${results.length} thùng thành công, ${failed} thùng lỗi.`,
        );
      } else {
        this.notificationService.success(
          `Cập nhật thành công ${saved.length} thùng.`,
        );
      }
      // Trả về các bản ghi đã lưu để màn gọi cập nhật / tải lại
      this.dialogRef.close(saved);
    });
  }

  /** Luồng cũ: batch vendor-tem-details */
  private saveLegacy(): void {
    const now = new Date().toISOString();

    const payload: CreateVendorTemDetailPayload[] = this.rows.map((row) => ({
      id: row.id,
      reelId: row.reelId ?? "",
      partNumber: row.partNumber ?? "",
      vendor: row.vendor ?? "",
      lot: row.lot ?? "",
      userData1: row.userData1 ?? "",
      userData2: row.userData2 ?? "",
      userData3: row.userData3 ?? "",
      userData4: row.userData4 ?? "",
      userData5: row.userData5 ?? "",
      initialQuantity: Number(row.initialQuantity) || 0,
      msdLevel: row.msl ?? "",
      msdInitialFloorTime: row.msdInitialFloorTime ?? "",
      msdBagSealDate: row.msdBagSealDate ?? "",
      marketUsage: row.marketUsage ?? "",
      quantityOverride: Number(row.quantityOverride) || 0,
      shelfTime: row.shelfTime ?? "",
      spMaterialName: row.spMaterialName ?? "",
      warningLimit: row.warningLimit ?? "",
      maximumLimit: row.maximumLimit ?? "",
      comments: row.comments ?? "",
      warmupTime: row.warmupTime ?? "",
      storageUnit: row.storageUnit ?? "",
      subStorageUnit: row.subStorageUnit ?? "",
      locationOverride: row.locationOverride ?? "",
      expirationDate: row.expirationDate ?? "",
      manufacturingDate: row.manufacturingDate ?? "",
      partClass: row.partClass ?? "",
      sapCode: row.sapCode ?? "",
      vendorQrCode: row.vendorQrCode ?? "",
      status: row.status ?? "NEW",
      createdBy: row.createdBy ?? "",
      createdAt: row.createdAt ?? now,
      updatedBy: row.updatedBy ?? "",
      updatedAt: now,
      poDetailId: row.poDetailId,
      importVendorTemTransactionsId: row.importVendorTemTransactionsId,
    }));

    this.managerTemNccService.batchUpdateVendorTemDetails(payload).subscribe({
      next: () => {
        this.isSaving = false;
        this.notificationService.success("Cập nhật vật tư thành công.");
        this.dialogRef.close(true);
      },
      error: () => {
        this.isSaving = false;
        this.notificationService.error("Cập nhật vật tư thất bại.");
      },
    });
  }

  /** Các vị trí không có trong danh sách vị trí (IndexedDB, khớp đúng tên / tên đầy đủ) */
  private async findInvalidLocations(values: string[]): Promise<string[]> {
    if (!values.length) {
      return [];
    }
    await this.warehouseCache.ensureSynced();
    const invalid: string[] = [];
    for (const value of values) {
      const lower = value.toLowerCase();
      const list = await this.warehouseCache.searchByName(value);
      const found = list.some(
        (w) =>
          toText(w.locationFullName).toLowerCase() === lower ||
          toText(w.locationName).toLowerCase() === lower,
      );
      if (!found) {
        invalid.push(value);
      }
    }
    return invalid;
  }

  /** Ngày chọn từ lịch: dd/MM/yyyy (luồng thùng) hoặc yyyyMMdd (luồng cũ) */
  private formatPickedDate(d: Date): string {
    const pad = (n: number): string => String(n).padStart(2, "0");
    return this.recordMode
      ? `${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()}`
      : `${d.getFullYear()}${pad(d.getMonth() + 1)}${pad(d.getDate())}`;
  }

  /** yyyyMMdd / yyyy-MM-dd / dd/MM/yyyy → dd/MM/yyyy (không parse được thì giữ nguyên) */
  private toDisplayDate(value: unknown): string {
    const d = this.parseAnyDate(toText(value));
    if (!d) {
      return toText(value);
    }
    const pad = (n: number): string => String(n).padStart(2, "0");
    return `${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()}`;
  }

  /** → yyyyMMdd (định dạng lưu của thùng); không parse được thì giữ nguyên */
  private toApiDate(value: unknown): string {
    const d = this.parseAnyDate(toText(value));
    if (!d) {
      return toText(value);
    }
    const pad = (n: number): string => String(n).padStart(2, "0");
    return `${d.getFullYear()}${pad(d.getMonth() + 1)}${pad(d.getDate())}`;
  }

  private parseAnyDate(raw: string): Date | null {
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
