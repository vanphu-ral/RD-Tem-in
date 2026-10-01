import { Component, Inject, OnInit } from "@angular/core";
import { forkJoin, Observable, of } from "rxjs";
import { catchError, map } from "rxjs/operators";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from "@angular/material/dialog";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { WarehouseCacheService } from "app/entities/list-material/services/warehouse-cache.service";
import { CachedWarehouse } from "app/entities/list-material/services/warehouse-db";
import {
  ReceivingSuppliesService,
  SapOwhsDto,
} from "app/entities/generate-tem-in/service/receiving-supplies.service";
import {
  LotDetailDialogComponent,
  LotDetailDialogData,
  LotDetailRow,
} from "../../lot-detail-dialog/lot-detail-dialog.component";
import { AddMaterialItem, AddPoItem } from "../add-info-tem-ncc.component";
import {
  InfoTemNccService,
  toText,
  VendorLabelInfoDto,
} from "../../services/info-tem-ncc.service";

export type ExpiryMode = "month" | "year";

export interface ExpiryControls {
  expiryMode: ExpiryMode;
  expiryOffset: number | null;
  /** Date object ổn định cho mat-datepicker (tránh infinite CD). */
  mfgDate: Date | null;
}

export interface SummaryLotRow extends ExpiryControls {
  id: number;
  lotNumber: string;
  materialTypeCount: number;
  boxCount: number;
  quantity: number;
  manufacturingDate: string;
  expirationDate: string;
  location: string;
  warehouseCode: string;
  lastUpdated: string;
  boxes: SummaryBoxRow[];
  /** Mã PO (chế độ vật tư chưa có PO) */
  po: string;
}

export interface SummaryBoxRow extends ExpiryControls {
  id: number;
  boxCode: string;
  reelId: string;
  quantity: number;
  manufacturingDate: string;
  expirationDate: string;
  location: string;
  warehouseCode: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  msl: string;
  /** Mã PO (chế độ vật tư chưa có PO) */
  po: string;
  /** Bản ghi thùng gốc từ API */
  record?: VendorLabelInfoDto;
}

export interface SummarySapRow extends ExpiryControls {
  id: number;
  sapCode: string;
  partNumber: string;
  productName: string;
  totalQty: number;
  labelCount: number;
  lotCount: number;
  manufacturingDate: string;
  expirationDate: string;
  location: string;
  warehouseCode: string;
  lastUpdated: string;
  lots: SummaryLotRow[];
  /** Mã PO (chế độ vật tư chưa có PO) */
  po: string;
}

/** Dòng vật tư của đơn (sapPor1R1) — để gán thùng chưa có PO vào đúng dòng PO */
export interface UnassignedPoLine {
  id: number;
  poCode: string;
  sapCode: string;
  partNumber: string;
}

export interface MaterialSummaryDialogData {
  po: AddPoItem;
  vendorCode?: string;
  transactionId?: number;
  /** Có → chế độ "Vật tư chưa có PO": thêm cột PO để bổ sung */
  unassigned?: { poLines: UnassignedPoLine[] };
}

type EditableField =
  | "manufacturingDate"
  | "expirationDate"
  | "location"
  | "warehouseCode"
  | "po";

@Component({
  selector: "jhi-material-summary-dialog",
  templateUrl: "./material-summary-dialog.component.html",
  styleUrls: ["./material-summary-dialog.component.scss"],
  standalone: false,
})
export class MaterialSummaryDialogComponent implements OnInit {
  filterSap = "";
  filterPart = "";
  /** Mã thùng — scan QR thì tự cắt lấy phần trước dấu # (ReelID) */
  filterBox = "";

  readonly monthOptions = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
  readonly yearOptions = [1, 2, 3, 4, 5];

  rows: SummarySapRow[] = [];

  /** Autocomplete vị trí — tìm trong IndexedDB (WarehouseCacheService) */
  locationOptions: CachedWarehouse[] = [];
  isLoadingLocations = false;
  /** Autocomplete mã kho — /api/owhs */
  warehouseList: SapOwhsDto[] = [];
  filteredWarehouseList: SapOwhsDto[] = [];
  isLoadingWarehouses = false;
  /** Đang gửi PUT cập nhật thùng */
  isSaving = false;

  private expandedSapIds = new Set<number>();
  private locationSearchSeq = 0;

  constructor(
    private dialogRef: MatDialogRef<MaterialSummaryDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: MaterialSummaryDialogData,
    private dialog: MatDialog,
    private notificationService: NotificationService,
    private warehouseCache: WarehouseCacheService,
    private receivingService: ReceivingSuppliesService,
    private infoTemNccService: InfoTemNccService,
  ) {}

  ngOnInit(): void {
    this.rows = this.buildRowsFromPo(this.data.po);
    if (this.rows.length) {
      this.expandedSapIds.add(this.rows[0].id);
    }
    this.loadPartNumbers();
    this.initLocationCache();
    this.loadWarehouses();
  }

  /** Label mã kho: "whsCode - whsName" */
  displayWarehouse = (code: string | null): string => {
    const c = toText(code);
    if (!c) {
      return "";
    }
    const whs = this.warehouseList.find((w) => w.whsCode === c);
    return whs ? `${whs.whsCode} - ${whs.whsName}` : c;
  };

  /** Gõ vị trí → tìm contains trong IndexedDB (đã sync) */
  onLocationSearch(keyword: string): void {
    const term = toText(keyword);
    const seq = ++this.locationSearchSeq;
    if (!term) {
      this.locationOptions = [];
      return;
    }
    void this.warehouseCache
      .searchByName(term)
      .then((list) => {
        if (seq === this.locationSearchSeq) {
          this.locationOptions = list;
        }
      })
      .catch(() => {
        if (seq === this.locationSearchSeq) {
          this.locationOptions = [];
        }
      });
  }

  /** Chọn vị trí ở hàng SAP → áp cho mọi lô / thùng bên dưới */
  onSapLocationSelected(sap: SummarySapRow, value: string): void {
    sap.location = toText(value);
    this.onSapFieldEnter(sap, "location");
  }

  /** Chọn vị trí ở hàng lô → áp cho mọi thùng trong lô */
  onLotLocationSelected(
    sap: SummarySapRow,
    lot: SummaryLotRow,
    value: string,
  ): void {
    lot.location = toText(value);
    this.onLotFieldEnter(sap, lot, "location");
  }

  onWarehouseSearch(keyword: string): void {
    const term = toText(keyword).toLowerCase();
    this.filteredWarehouseList = term
      ? this.warehouseList.filter(
          (w) =>
            w.whsCode.toLowerCase().includes(term) ||
            toText(w.whsName).toLowerCase().includes(term),
        )
      : [...this.warehouseList];
  }

  onSapWarehouseSelected(sap: SummarySapRow, code: string): void {
    sap.warehouseCode = toText(code);
    this.onSapFieldEnter(sap, "warehouseCode");
  }

  onLotWarehouseSelected(
    sap: SummarySapRow,
    lot: SummaryLotRow,
    code: string,
  ): void {
    lot.warehouseCode = toText(code);
    this.onLotFieldEnter(sap, lot, "warehouseCode");
  }

  get poCode(): string {
    return this.data.po?.poCode ?? "";
  }

  /** Chế độ bổ sung PO cho vật tư chưa có PO */
  get isUnassigned(): boolean {
    return !!this.data.unassigned;
  }

  /** Gợi ý PO: các PO của đơn có mã vật tư này đứng trước, lọc theo nội dung ô */
  poOptionsFor(sapCode: string, term: string): string[] {
    const lines = this.data.unassigned?.poLines ?? [];
    const code = toText(sapCode).toLowerCase();
    const withItem = lines
      .filter((l) => toText(l.sapCode).toLowerCase() === code)
      .map((l) => l.poCode);
    const all = lines.map((l) => l.poCode);
    const t = toText(term).toLowerCase();
    return [
      ...new Set([...withItem, ...all].map((p) => toText(p)).filter(Boolean)),
    ].filter((p) => !t || p.toLowerCase().includes(t));
  }

  onSapPoSelected(sap: SummarySapRow, value: string): void {
    sap.po = toText(value);
    this.onSapFieldEnter(sap, "po");
  }

  onLotPoSelected(sap: SummarySapRow, lot: SummaryLotRow, value: string): void {
    lot.po = toText(value);
    this.onLotFieldEnter(sap, lot, "po");
  }

  get sapCount(): number {
    return this.filteredRows.length;
  }

  get filteredRows(): SummarySapRow[] {
    const sap = this.filterSap.trim().toLowerCase();
    const part = this.filterPart.trim().toLowerCase();
    const box = this.boxTerm;

    return this.rows.filter((r) => {
      if (sap && !r.sapCode.toLowerCase().includes(sap)) {
        return false;
      }
      if (part && !r.partNumber.toLowerCase().includes(part)) {
        return false;
      }
      if (box && !this.getVisibleLots(r).length) {
        return false;
      }
      return true;
    });
  }

  private get boxTerm(): string {
    return this.filterBox.trim().toLowerCase();
  }

  get missingHint(): string {
    return "Thiếu: HSD, Vị trí, Mã kho, UserData1-4, MSL — điền ở hàng SAP hoặc Lô (áp cả nhánh bên dưới bằng Enter), hoặc sửa riêng từng dòng.";
  }

  toggleSap(row: SummarySapRow): void {
    if (this.expandedSapIds.has(row.id)) {
      this.expandedSapIds.delete(row.id);
    } else {
      this.expandedSapIds.add(row.id);
    }
  }

  isSapExpanded(row: SummarySapRow): boolean {
    // Đang lọc mã thùng → tự mở để thấy lô chứa thùng đó
    return this.expandedSapIds.has(row.id) || !!this.boxTerm;
  }

  /** Scan mã thùng dạng "ReelID#Part#..." → giữ phần trước dấu # đầu tiên */
  onBoxFilterChange(value: string): void {
    this.filterBox = (value ?? "").split("#")[0].trim();
  }

  /** Lô hiển thị: đang lọc mã thùng → chỉ lô có thùng khớp */
  getVisibleLots(sap: SummarySapRow): SummaryLotRow[] {
    const box = this.boxTerm;
    if (!box) {
      return sap.lots;
    }
    return sap.lots.filter((l) =>
      l.boxes.some((b) => b.boxCode.toLowerCase().includes(box)),
    );
  }

  isComplete(row: {
    manufacturingDate: string;
    expirationDate: string;
    location: string;
    warehouseCode: string;
  }): boolean {
    return !!(
      row.manufacturingDate?.trim() &&
      row.expirationDate?.trim() &&
      row.location?.trim() &&
      row.warehouseCode?.trim()
    );
  }

  /**
   * Lô đủ thông tin: các ô của lô đủ, hoặc mọi thùng trong lô đều đủ
   * (các thùng khác giá trị nhau thì ô của lô để trống nhưng thực tế vẫn đủ).
   */
  isLotComplete(lot: SummaryLotRow): boolean {
    if (this.isComplete(lot)) {
      return true;
    }
    return lot.boxes.length > 0 && lot.boxes.every((b) => this.isComplete(b));
  }

  /** Hàng mã SAP = tóm tắt các lô con: đủ khi mọi lô đều đủ */
  isSapComplete(sap: SummarySapRow): boolean {
    if (!sap.lots.length) {
      return this.isComplete(sap);
    }
    return sap.lots.every((l) => this.isLotComplete(l));
  }

  completeLotCount(sap: SummarySapRow): number {
    return sap.lots.filter((l) => this.isLotComplete(l)).length;
  }

  getExpiryOptions(mode: ExpiryMode): number[] {
    return mode === "year" ? this.yearOptions : this.monthOptions;
  }

  onSapMfgChange(sap: SummarySapRow, date: Date | null): void {
    if (this.isSameDay(sap.mfgDate, date)) {
      return;
    }
    sap.mfgDate = date;
    sap.manufacturingDate = date ? this.formatDisplayDate(date) : "";
    this.recalcExpiry(sap);
    // Payload gửi theo từng thùng → áp MFG (và HSD tính lại) xuống lô + thùng
    for (const lot of sap.lots) {
      this.applyMfg(lot, sap.manufacturingDate, sap);
      for (const box of lot.boxes) {
        this.applyMfg(box, sap.manufacturingDate, sap);
      }
    }
    sap.lastUpdated = this.nowText();
  }

  onLotMfgChange(
    sap: SummarySapRow,
    lot: SummaryLotRow,
    date: Date | null,
  ): void {
    if (this.isSameDay(lot.mfgDate, date)) {
      return;
    }
    lot.mfgDate = date;
    lot.manufacturingDate = date ? this.formatDisplayDate(date) : "";
    this.recalcExpiry(lot);
    for (const box of lot.boxes) {
      this.applyMfg(box, lot.manufacturingDate, lot);
    }
    sap.lastUpdated = this.nowText();
    lot.lastUpdated = sap.lastUpdated;
  }

  setExpiryMode(
    row: ExpiryControls & { manufacturingDate: string; expirationDate: string },
    mode: ExpiryMode,
  ): void {
    row.expiryMode = mode;
    row.expiryOffset = null;
    // không xóa HSD đã có — chỉ khi chọn lại offset mới tính
  }

  /**
   * Chọn số tháng/năm HSD ở hàng SAP hoặc lô → tính HSD cho hàng đó và áp xuống
   * các lô / thùng bên dưới (mỗi dòng tính từ MFG của chính nó) — payload gửi theo thùng.
   */
  onExpiryOffsetChange(
    row: SummarySapRow | SummaryLotRow,
    offset: number | null,
  ): void {
    row.expiryOffset = offset;
    this.recalcExpiry(row);
    if (offset == null) {
      return;
    }
    const lots: SummaryLotRow[] = "lots" in row ? row.lots : [];
    const boxes: SummaryBoxRow[] =
      "lots" in row ? row.lots.flatMap((l) => l.boxes) : row.boxes;
    for (const target of [...lots, ...boxes]) {
      target.expiryMode = row.expiryMode;
      target.expiryOffset = offset;
      if (!toText(target.manufacturingDate)) {
        // Dòng chưa có MFG → tính theo MFG của hàng cha
        target.manufacturingDate = row.manufacturingDate;
        target.mfgDate = this.parseDisplayDate(row.manufacturingDate);
      }
      this.recalcExpiry(target);
    }
  }

  onSapFieldEnter(sap: SummarySapRow, field: EditableField): void {
    const value = (sap[field] ?? "").trim();
    sap.lots.forEach((lot) => {
      lot[field] = value;
      if (field === "manufacturingDate") {
        lot.mfgDate = this.parseDisplayDate(value);
        lot.expiryMode = sap.expiryMode;
        lot.expiryOffset = sap.expiryOffset;
        this.recalcExpiry(lot);
      }
      if (field === "expirationDate") {
        lot.expiryOffset = sap.expiryOffset;
        lot.expiryMode = sap.expiryMode;
      }
      lot.boxes.forEach((b) => {
        b[field] = value;
        if (field === "manufacturingDate") {
          b.mfgDate = this.parseDisplayDate(value);
          b.expiryMode = sap.expiryMode;
          b.expiryOffset = sap.expiryOffset;
          this.recalcExpiry(b);
        }
        if (field === "expirationDate") {
          b.expiryMode = sap.expiryMode;
          b.expiryOffset = sap.expiryOffset;
        }
      });
    });
    sap.lastUpdated = this.nowText();
    this.notificationService.success(
      `Đã áp dụng ${this.fieldLabel(field)} cho ${sap.lots.length} lô.`,
    );
  }

  onLotFieldEnter(
    sap: SummarySapRow,
    lot: SummaryLotRow,
    field: EditableField,
  ): void {
    const value = (lot[field] ?? "").trim();
    lot.boxes.forEach((b) => {
      b[field] = value;
      if (field === "manufacturingDate") {
        b.mfgDate = this.parseDisplayDate(value);
        b.expiryMode = lot.expiryMode;
        b.expiryOffset = lot.expiryOffset;
        this.recalcExpiry(b);
      }
      if (field === "expirationDate") {
        b.expiryMode = lot.expiryMode;
        b.expiryOffset = lot.expiryOffset;
      }
    });
    sap.lastUpdated = this.nowText();
    lot.lastUpdated = sap.lastUpdated;
  }

  /** Xem các thùng thật trong lô (vendorLabelInfoList) */
  onViewLotBoxes(sap: SummarySapRow, lot: SummaryLotRow): void {
    if (!lot.boxes.length) {
      this.notificationService.warning(
        `Lô "${lot.lotNumber}" chưa có thùng nào.`,
      );
      return;
    }
    const rows: LotDetailRow[] = lot.boxes.map((b, idx) => {
      const rec = b.record;
      return {
        id: b.id,
        reelId: b.reelId,
        partNumber: sap.partNumber || toText(rec?.partNumber),
        vendor: toText(rec?.vendor) || (this.data.vendorCode ?? ""),
        lot: toText(rec?.lot) || lot.lotNumber,
        userData1: b.userData1,
        userData2: b.userData2,
        userData3: b.userData3,
        userData4: b.userData4,
        userData5: toText(rec?.userData5),
        initialQuantity: b.quantity,
        msl: b.msl,
        storageUnit: b.warehouseCode,
        manufacturingDate: b.manufacturingDate,
        expirationDate: b.expirationDate,
        sapCode: toText(rec?.sapCode) || sap.sapCode,
        sapName: sap.productName,
        vendorQrCode: toText(rec?.vendorQrCode),
        status: toText(rec?.status),
        createdBy: toText(rec?.createdBy),
        createdAt: toText(rec?.createdAt),
        updatedBy: toText(rec?.updatedBy),
        poDetailId: sap.id,
        importVendorTemTransactionsId: this.data.transactionId ?? 1,
        boxCode: b.boxCode,
        location: b.location,
        _idx: idx,
      };
    });

    const dialogData: LotDetailDialogData = {
      partNumber: sap.partNumber,
      manufacturingDate: lot.manufacturingDate || sap.manufacturingDate || "—",
      poCode: this.poCode,
      rows,
    };

    this.dialog.open(LotDetailDialogComponent, {
      width: "98vw",
      maxWidth: "98vw",
      height: "92vh",
      maxHeight: "92vh",
      panelClass: "lot-detail-dialog-panel",
      autoFocus: false,
      data: dialogData,
    });
  }

  onCancel(): void {
    if (this.isSaving) {
      return;
    }
    this.dialogRef.close(null);
  }

  /**
   * PUT /vendor-label-infos/{id} cho từng thùng có thay đổi — gửi đầy đủ bản ghi gốc,
   * ghi đè các trường sửa trong dialog (MFG, HSD, vị trí, mã kho, UserData1-4, MSL).
   */
  onConfirm(): void {
    if (this.isSaving) {
      return;
    }
    const payloads: VendorLabelInfoDto[] = [];
    for (const sap of this.rows) {
      for (const lot of sap.lots) {
        for (const box of lot.boxes) {
          const payload = this.buildUpdatePayload(box);
          if (payload) {
            payloads.push(payload);
          }
        }
      }
    }
    if (!payloads.length) {
      this.notificationService.info("Không có thùng nào thay đổi.");
      return;
    }

    const requests: Array<Observable<boolean>> = payloads.map((payload) =>
      this.infoTemNccService.updateVendorLabelInfo(payload).pipe(
        map(() => true),
        catchError(() => of(false)),
      ),
    );
    this.isSaving = true;
    forkJoin(requests).subscribe((results) => {
      this.isSaving = false;
      const ok = results.filter(Boolean).length;
      const failed = results.length - ok;
      if (!ok) {
        this.notificationService.error(
          `Cập nhật thất bại ${failed} thùng — vui lòng thử lại.`,
        );
        return;
      }
      if (failed) {
        this.notificationService.warning(
          `Cập nhật ${ok}/${results.length} thùng thành công, ${failed} thùng lỗi.`,
        );
        return;
      }
      const unmatched = this.isUnassigned
        ? payloads.filter((p) => toText(p.userData5) && !p.sapPor1Id).length
        : 0;
      if (unmatched) {
        this.notificationService.warning(
          `Cập nhật ${ok} thùng; ${unmatched} thùng có PO không chứa mã vật tư đó nên vẫn ở "Vật tư chưa có PO".`,
        );
      } else {
        this.notificationService.success(`Cập nhật thành công ${ok} thùng.`);
      }
      this.dialogRef.close({ updated: ok });
    });
  }

  /** Gán MFG cho dòng con; nếu hàng cha đang chọn HSD theo tháng/năm thì tính lại HSD */
  private applyMfg(
    target: SummaryLotRow | SummaryBoxRow,
    mfg: string,
    parent: ExpiryControls,
  ): void {
    target.manufacturingDate = mfg;
    target.mfgDate = this.parseDisplayDate(mfg);
    if (parent.expiryOffset != null) {
      target.expiryMode = parent.expiryMode;
      target.expiryOffset = parent.expiryOffset;
      this.recalcExpiry(target);
    }
  }

  /** Bản ghi gốc + giá trị đang sửa; không đổi gì → null (không gửi) */
  private buildUpdatePayload(box: SummaryBoxRow): VendorLabelInfoDto | null {
    const rec = box.record;
    if (!rec?.id) {
      return null;
    }
    const orNull = (v: string): string | null => (toText(v) ? toText(v) : null);
    const payload: VendorLabelInfoDto = {
      ...rec,
      manufacturingDate: orNull(this.toApiDate(box.manufacturingDate)),
      expirationDate: orNull(this.toApiDate(box.expirationDate)),
      subStorageUnit: orNull(box.location),
      storageUnit: orNull(box.warehouseCode),
      userData1: orNull(box.userData1),
      userData2: orNull(box.userData2),
      userData3: orNull(box.userData3),
      userData4: orNull(box.userData4),
      msdLevel: orNull(box.msl),
      palletBoxMapping: undefined,
    };
    if (this.isUnassigned) {
      // PO bổ sung → userData5 + gán vào dòng PO của đơn có cùng mã vật tư
      const po = toText(box.po);
      payload.userData5 = orNull(po);
      payload.sapPor1Id =
        this.resolvePoLineId(po, rec) ?? rec.sapPor1Id ?? null;
    }
    const keys: Array<keyof VendorLabelInfoDto> = [
      "manufacturingDate",
      "expirationDate",
      "subStorageUnit",
      "storageUnit",
      "userData1",
      "userData2",
      "userData3",
      "userData4",
      "msdLevel",
      "userData5",
      "sapPor1Id",
    ];
    const changed = keys.some((k) => toText(payload[k]) !== toText(rec[k]));
    return changed ? payload : null;
  }

  /** Dòng PO của đơn: đúng PO + cùng mã SAP (hoặc part) với thùng */
  private resolvePoLineId(po: string, rec: VendorLabelInfoDto): number | null {
    if (!po) {
      return null;
    }
    const lines = (this.data.unassigned?.poLines ?? []).filter(
      (l) => toText(l.poCode).toLowerCase() === po.toLowerCase(),
    );
    const sap = toText(rec.sapCode).toLowerCase();
    const part = toText(rec.partNumber).toLowerCase();
    const line =
      lines.find((l) => sap && toText(l.sapCode).toLowerCase() === sap) ??
      lines.find(
        (l) =>
          part &&
          toText(l.partNumber)
            .toLowerCase()
            .split(",")
            .map((p) => p.trim())
            .includes(part),
      );
    return line ? line.id : null;
  }

  /** dd/MM/yyyy (hiển thị) → yyyyMMdd (định dạng đang lưu khi scan) */
  private toApiDate(value: string): string {
    const d = this.parseDisplayDate(value);
    if (!d) {
      return toText(value);
    }
    const pad = (n: number): string => String(n).padStart(2, "0");
    return `${d.getFullYear()}${pad(d.getMonth() + 1)}${pad(d.getDate())}`;
  }

  /**
   * Lấy part number OITM theo mã SAP cho các dòng chưa có part.
   * Mỗi mã SAP gọi 1 lần (service cache — mã đã lấy ở trang ngoài không gọi lại).
   */
  private loadPartNumbers(): void {
    const bySapCode = new Map<string, SummarySapRow[]>();
    for (const row of this.rows) {
      const code = toText(row.sapCode);
      if (!code || toText(row.partNumber)) {
        continue;
      }
      bySapCode.set(code, [...(bySapCode.get(code) ?? []), row]);
    }
    bySapCode.forEach((rows, code) => {
      this.infoTemNccService
        .getPartNumbersBySapCode(code)
        .subscribe((parts) => {
          const partNumber = parts.join(", ");
          for (const row of rows) {
            row.partNumber = partNumber;
          }
          // Đồng bộ ngược về vật tư ở trang ngoài để lần sau không gọi lại
          for (const m of this.data.po?.materials ?? []) {
            if (toText(m.materialCode) === code && !toText(m.partNumber)) {
              m.partNumber = partNumber;
            }
          }
        });
    });
  }

  /** IndexedDB chưa có / lệch version → tải /api/location 1 lần; có rồi thì dùng luôn */
  private initLocationCache(): void {
    this.isLoadingLocations = true;
    void this.warehouseCache
      .ensureSynced()
      .catch(() => {
        this.notificationService.error("Không tải được danh sách vị trí.");
      })
      .finally(() => {
        this.isLoadingLocations = false;
      });
  }

  private loadWarehouses(): void {
    this.isLoadingWarehouses = true;
    this.receivingService.getSapWarehouses().subscribe({
      next: (list) => {
        this.warehouseList = list;
        this.filteredWarehouseList = [...list];
        this.isLoadingWarehouses = false;
      },
      error: () => {
        this.isLoadingWarehouses = false;
      },
    });
  }

  private fieldLabel(field: string): string {
    switch (field) {
      case "manufacturingDate":
        return "MFG";
      case "expirationDate":
        return "HSD";
      case "location":
        return "Vị trí";
      case "warehouseCode":
        return "Mã kho";
      case "po":
        return "PO";
      default:
        return field;
    }
  }

  private recalcExpiry(
    row: ExpiryControls & { manufacturingDate: string; expirationDate: string },
  ): void {
    if (row.expiryOffset == null || row.expiryOffset <= 0) {
      return;
    }
    const base =
      this.parseDisplayDate(row.manufacturingDate) ?? this.startOfToday();
    const result = new Date(base.getTime());
    if (row.expiryMode === "year") {
      result.setFullYear(result.getFullYear() + row.expiryOffset);
    } else {
      result.setMonth(result.getMonth() + row.expiryOffset);
    }
    row.expirationDate = this.formatDisplayDate(result);
  }

  private parseDisplayDate(value: string | null | undefined): Date | null {
    const raw = (value ?? "").trim();
    if (!raw) {
      return null;
    }
    // dd/MM/yyyy
    const slash = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(raw);
    if (slash) {
      const d = Number(slash[1]);
      const m = Number(slash[2]) - 1;
      const y = Number(slash[3]);
      const date = new Date(y, m, d);
      return Number.isNaN(date.getTime()) ? null : date;
    }
    // yyyy-MM-dd
    const iso = /^(\d{4})-(\d{2})-(\d{2})$/.exec(raw);
    if (iso) {
      const date = new Date(Number(iso[1]), Number(iso[2]) - 1, Number(iso[3]));
      return Number.isNaN(date.getTime()) ? null : date;
    }
    const digits = raw.replace(/\D/g, "");
    if (digits.length === 8) {
      if (Number(digits.slice(0, 4)) > 1900) {
        return new Date(
          Number(digits.slice(0, 4)),
          Number(digits.slice(4, 6)) - 1,
          Number(digits.slice(6, 8)),
        );
      }
      return new Date(
        Number(digits.slice(4, 8)),
        Number(digits.slice(2, 4)) - 1,
        Number(digits.slice(0, 2)),
      );
    }
    return null;
  }

  private formatDisplayDate(date: Date): string {
    const pad = (n: number): string => String(n).padStart(2, "0");
    return `${pad(date.getDate())}/${pad(date.getMonth() + 1)}/${date.getFullYear()}`;
  }

  private toDisplayDate(value: string): string {
    const d = this.parseDisplayDate(value);
    return d ? this.formatDisplayDate(d) : (value ?? "").trim();
  }

  private startOfToday(): Date {
    const d = new Date();
    return new Date(d.getFullYear(), d.getMonth(), d.getDate());
  }

  private isSameDay(
    a: Date | null | undefined,
    b: Date | null | undefined,
  ): boolean {
    if (!a && !b) {
      return true;
    }
    if (!a || !b) {
      return false;
    }
    return (
      a.getFullYear() === b.getFullYear() &&
      a.getMonth() === b.getMonth() &&
      a.getDate() === b.getDate()
    );
  }

  private nowText(): string {
    const d = new Date();
    const pad = (n: number): string => String(n).padStart(2, "0");
    return `${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }

  /** Dựng bảng từ dữ liệu thật: vật tư → lô → thùng (vendorLabelInfoList của API detail) */
  private buildRowsFromPo(po: AddPoItem): SummarySapRow[] {
    const materials: AddMaterialItem[] = po?.materials ?? [];
    return materials.map((m) => {
      const lots: SummaryLotRow[] = (m.lots ?? []).map((lot) => {
        const boxes: SummaryBoxRow[] = (lot.boxes ?? []).map((b) =>
          this.toBoxRow(b),
        );
        const lotMfg = this.commonValue(boxes.map((b) => b.manufacturingDate));
        return {
          id: lot.id,
          lotNumber: lot.lotNumber,
          materialTypeCount: 1,
          boxCount: boxes.length,
          quantity: boxes.reduce((s, b) => s + (b.quantity || 0), 0),
          manufacturingDate: lotMfg,
          expirationDate: this.commonValue(boxes.map((b) => b.expirationDate)),
          location: this.commonValue(boxes.map((b) => b.location)),
          warehouseCode: this.commonValue(boxes.map((b) => b.warehouseCode)),
          lastUpdated: this.latestUpdate(lot.boxes ?? []),
          boxes,
          po: this.commonValue(boxes.map((b) => b.po)),
          expiryMode: "month" as ExpiryMode,
          expiryOffset: null,
          mfgDate: this.parseDisplayDate(lotMfg),
        };
      });

      const mfg = this.commonValue(lots.map((l) => l.manufacturingDate));
      return {
        id: m.id,
        sapCode: m.materialCode,
        partNumber: m.partNumber,
        productName: m.materialName,
        totalQty: lots.reduce((s, l) => s + (l.quantity || 0), 0),
        labelCount: lots.reduce((s, l) => s + (l.boxCount || 0), 0),
        lotCount: lots.length,
        manufacturingDate: mfg,
        expirationDate: this.commonValue(lots.map((l) => l.expirationDate)),
        location: this.commonValue(lots.map((l) => l.location)),
        warehouseCode: this.commonValue(lots.map((l) => l.warehouseCode)),
        lastUpdated: this.latestUpdate(
          (m.lots ?? []).flatMap((l) => l.boxes ?? []),
        ),
        lots,
        po: this.commonValue(lots.map((l) => l.po)),
        expiryMode: "month" as ExpiryMode,
        expiryOffset: null,
        mfgDate: this.parseDisplayDate(mfg),
      };
    });
  }

  /** 1 bản ghi vendor-label-info = 1 thùng; mã thùng = ReelID */
  private toBoxRow(b: VendorLabelInfoDto): SummaryBoxRow {
    const mfg = this.toDisplayDate(toText(b.manufacturingDate));
    return {
      id: b.id,
      boxCode: toText(b.reelId),
      reelId: toText(b.reelId),
      quantity: Number(b.initialQuantity ?? 0),
      manufacturingDate: mfg,
      expirationDate: this.toDisplayDate(toText(b.expirationDate)),
      location: toText(b.subStorageUnit),
      warehouseCode: toText(b.storageUnit),
      userData1: toText(b.userData1),
      userData2: toText(b.userData2),
      userData3: toText(b.userData3),
      userData4: toText(b.userData4),
      msl: toText(b.msdLevel),
      expiryMode: "month",
      expiryOffset: null,
      mfgDate: this.parseDisplayDate(mfg),
      po: toText(b.userData5),
      record: b,
    };
  }

  /** Giá trị chung nếu mọi dòng giống nhau; khác nhau / không có → rỗng */
  private commonValue(values: string[]): string {
    const set = new Set(values.map((v) => toText(v)));
    return set.size === 1 ? [...set][0] : "";
  }

  /** Thời điểm cập nhật mới nhất (updatedAt / createdAt) của các thùng */
  private latestUpdate(boxes: VendorLabelInfoDto[]): string {
    let latest: Date | null = null;
    for (const b of boxes) {
      const raw = toText(b.updatedAt) || toText(b.createdAt);
      const d = raw ? new Date(raw) : null;
      if (d && !Number.isNaN(d.getTime()) && (!latest || d > latest)) {
        latest = d;
      }
    }
    if (!latest) {
      return "—";
    }
    const pad = (n: number): string => String(n).padStart(2, "0");
    return `${pad(latest.getDate())}/${pad(latest.getMonth() + 1)}/${latest.getFullYear()} ${pad(latest.getHours())}:${pad(latest.getMinutes())}`;
  }
}
