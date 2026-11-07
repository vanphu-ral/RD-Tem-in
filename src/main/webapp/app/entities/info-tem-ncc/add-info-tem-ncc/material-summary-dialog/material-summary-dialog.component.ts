import {
  Component,
  EventEmitter,
  Inject,
  Input,
  OnInit,
  Optional,
  Output,
} from "@angular/core";
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
import {
  clearQueueReason,
  countQueueReasons,
  QUEUE_REASON_HINTS,
  QUEUE_REASON_LABELS,
  QueueReason,
  queueReasonOf,
} from "../../shared/queue-reason.util";
import { boxLocation, locationFields } from "../../shared/box-location.util";
import {
  SplitBoxDialogComponent,
  SplitBoxDialogData,
  SplitBoxDialogResult,
} from "../split-box-dialog/split-box-dialog.component";
import { openVendorNccLabelPrint } from "../../shared/split-label-print.util";
import { autoExpiryDates } from "../../shared/auto-expiry.util";
import {
  ReceivingSuppliesService,
  SapOwhsDto,
} from "app/entities/generate-tem-in/service/receiving-supplies.service";

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
  /** SL theo PO của dòng (0 = không giới hạn) */
  orderQty: number;
  /** SL đã nhận vào dòng trong đơn này */
  receivedQty: number;
}

export interface MaterialSummaryDialogData {
  po: AddPoItem;
  vendorCode?: string;
  transactionId?: number;
  /** Có → chế độ "Vật tư chưa có PO": thêm cột PO để bổ sung */
  unassigned?: { poLines: UnassignedPoLine[] };
}

/** Dòng cần nhấp sáng khi nhúng trong màn Scan */
export interface SummaryHighlight {
  sapCode: string;
  lot: string;
  seq: number;
}

/** PO rỗng — khi chưa có dữ liệu */
const EMPTY_PO: AddPoItem = {
  id: 0,
  poCode: "",
  warehouseKeeper: "",
  vendorCode: "",
  vendorName: "",
  vehicleNumber: "",
  invoiceNumber: "",
  contractCode: "",
  importDate: "",
  importBatch: null,
  materialTypeCount: 0,
  totalQuantity: 0,
  scannedQuantity: 0,
  sentQuantity: 0,
  scannedProgress: 0,
  sentProgress: 0,
  status: "WAITING",
  materials: [],
};

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

  /** Nhúng trong màn Scan (không phải dialog riêng): ẩn header, Lưu không đóng */
  @Input() embedded = false;
  /** Báo màn ngoài sau khi lưu thành công (chế độ nhúng) */
  @Output() saved = new EventEmitter<number>();

  data: MaterialSummaryDialogData;

  /** Autocomplete vị trí — tìm trong IndexedDB (WarehouseCacheService) */
  locationOptions: CachedWarehouse[] = [];
  isLoadingLocations = false;
  /** Danh sách kho SAP (/owhs) cho ô Mã kho ở hàng vật tư — tải khi focus lần đầu */
  sapWarehouses: SapOwhsDto[] = [];
  filteredSapWarehouses: SapOwhsDto[] = [];
  isLoadingSapWarehouses = false;
  /** Đang gửi PUT cập nhật thùng */
  isSaving = false;
  /** Số thùng đã lưu từ dialog "Cập nhật thông tin vật tư" (để báo màn gọi tải lại) */
  lotDialogUpdated = 0;

  private expandedSapIds = new Set<number>();
  private locationSearchSeq = 0;
  private initialized = false;
  /** Dòng cần nhấp sáng (thùng vừa scan): mã SAP + lô */
  private highlightTarget: SummaryHighlight | null = null;
  private highlightTimer: ReturnType<typeof setTimeout> | null = null;

  constructor(
    @Optional()
    private dialogRef: MatDialogRef<MaterialSummaryDialogComponent> | null,
    @Optional() @Inject(MAT_DIALOG_DATA) data: MaterialSummaryDialogData | null,
    private dialog: MatDialog,
    private notificationService: NotificationService,
    private warehouseCache: WarehouseCacheService,
    private infoTemNccService: InfoTemNccService,
    private receivingService: ReceivingSuppliesService,
  ) {
    this.data = data ?? { po: EMPTY_PO };
  }

  /** Chế độ nhúng: đổi dữ liệu (đổi PO / tải lại sau khi scan) — giữ phần đang sửa chưa lưu */
  @Input() set panelData(value: MaterialSummaryDialogData | null) {
    if (!value) {
      return;
    }
    this.data = value;
    if (this.initialized) {
      this.rebuildPreservingEdits();
    }
  }

  /** Chế độ nhúng: nhấp sáng + mở dòng của thùng vừa scan */
  @Input() set highlight(value: SummaryHighlight | null) {
    if (!value) {
      return;
    }
    this.highlightTarget = value;
    this.expandForHighlight();
    if (this.highlightTimer) {
      clearTimeout(this.highlightTimer);
    }
    this.highlightTimer = setTimeout(() => {
      this.highlightTarget = null;
      this.highlightTimer = null;
    }, 3500);
  }

  ngOnInit(): void {
    // Mặc định thu gọn — chỉ mở khi bấm (hoặc nhấp sáng thùng vừa scan)
    this.rows = this.buildRowsFromPo(this.data.po);
    this.expandForHighlight();
    this.initialized = true;
    this.loadPartNumbers();
    this.initLocationCache();
  }

  /** Dòng lô của thùng vừa scan → nhấp sáng */
  isHighlighted(sap: SummarySapRow, lot?: SummaryLotRow): boolean {
    const h = this.highlightTarget;
    if (!h) {
      return false;
    }
    const sameSap =
      toText(sap.sapCode).toLowerCase() === toText(h.sapCode).toLowerCase();
    if (!lot) {
      return sameSap;
    }
    return sameSap && toText(lot.lotNumber) === toText(h.lot);
  }

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

  /** Số cột của bảng (chế độ hàng chờ thêm cột PO + Lý do) */
  get columnCount(): number {
    return this.isUnassigned ? 15 : 13;
  }

  /** Số cột của bảng lô trong vùng mở rộng */
  get lotColumnCount(): number {
    return this.isUnassigned ? 11 : 9;
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

  /** Đủ thông tin: MFG, HSD, vị trí kho (mã kho SAP theo dòng PO, không tính theo thùng) */
  isComplete(row: {
    manufacturingDate: string;
    expirationDate: string;
    location: string;
  }): boolean {
    return !!(
      row.manufacturingDate?.trim() &&
      row.expirationDate?.trim() &&
      row.location?.trim()
    );
  }

  /**
   * Lô đủ thông tin: các ô của lô đủ, hoặc mọi thùng trong lô đều đủ
   * (các thùng khác giá trị nhau thì ô của lô để trống nhưng thực tế vẫn đủ).
   */
  /** Hàng chờ: số thùng theo lý do (thừa SL / thiếu PO) của 1 lô */
  lotQueueReasons(
    lot: SummaryLotRow,
  ): Array<{ reason: QueueReason; count: number }> {
    if (!this.isUnassigned) {
      return [];
    }
    return countQueueReasons(
      lot.boxes.flatMap((b) => (b.record ? [b.record] : [])),
    );
  }

  /** Hàng chờ: số thùng theo lý do của cả mã SAP */
  sapQueueReasons(
    sap: SummarySapRow,
  ): Array<{ reason: QueueReason; count: number }> {
    if (!this.isUnassigned) {
      return [];
    }
    return countQueueReasons(
      sap.lots.flatMap((l) =>
        l.boxes.flatMap((b) => (b.record ? [b.record] : [])),
      ),
    );
  }

  queueReasonLabel(reason: QueueReason): string {
    return QUEUE_REASON_LABELS[reason];
  }

  queueReasonHint(reason: QueueReason): string {
    return QUEUE_REASON_HINTS[reason];
  }

  /** Hàng chờ: thùng Thừa SL trong lô — tách được */
  overflowBoxes(lot: SummaryLotRow): VendorLabelInfoDto[] {
    if (!this.isUnassigned) {
      return [];
    }
    return lot.boxes.flatMap((b) =>
      b.record && queueReasonOf(b.record) === "overflow" ? [b.record] : [],
    );
  }

  /** Hàng chờ: thùng "Dư tách" (phần dư tách ra) trong lô — in lại tem được */
  remainderBoxes(lot: SummaryLotRow): VendorLabelInfoDto[] {
    if (!this.isUnassigned) {
      return [];
    }
    return lot.boxes.flatMap((b) =>
      b.record && queueReasonOf(b.record) === "splitRemainder"
        ? [b.record]
        : [],
    );
  }

  /** In tem các thùng "Dư tách" của lô (dialog In tem nhà cung cấp) */
  onPrintRemainders(sap: SummarySapRow, lot: SummaryLotRow): void {
    openVendorNccLabelPrint(this.dialog, this.remainderBoxes(lot), {
      sapCode: sap.sapCode,
      partNumber: sap.partNumber,
      materialName: sap.productName,
      vendorName: this.data.po?.vendorName,
      invoiceNumber: this.data.po?.invoiceNumber,
      contractCode: this.data.po?.contractCode,
    });
  }

  /**
   * Tách thùng Thừa SL của lô: phần vừa đủ vào PO, phần dư thành thùng mới ở hàng chờ.
   * Xong → thay thùng gốc bằng thùng dư trong bảng, báo màn ngoài tải lại.
   */
  onSplitLot(sap: SummarySapRow, lot: SummaryLotRow): void {
    const boxes = this.overflowBoxes(lot);
    if (!boxes.length) {
      return;
    }
    const code = toText(sap.sapCode).toLowerCase();
    const lines = (this.data.unassigned?.poLines ?? []).filter(
      (l) => toText(l.sapCode).toLowerCase() === code,
    );
    const takenReelIds = (this.data.po?.materials ?? []).flatMap((m) =>
      (m.lots ?? []).flatMap((l) =>
        (l.boxes ?? []).map((b) => toText(b.reelId)),
      ),
    );
    const data: SplitBoxDialogData = {
      sapCode: sap.sapCode,
      partNumber: sap.partNumber,
      materialName: sap.productName,
      boxes,
      poLines: lines.map((l) => ({
        id: l.id,
        poCode: l.poCode,
        orderQty: l.orderQty,
        receivedQty: l.receivedQty,
      })),
      takenReelIds,
      vendorName: this.data.po?.vendorName,
      invoiceNumber: this.data.po?.invoiceNumber,
      contractCode: this.data.po?.contractCode,
    };
    this.dialog
      .open<SplitBoxDialogComponent, SplitBoxDialogData, SplitBoxDialogResult>(
        SplitBoxDialogComponent,
        {
          width: "960px",
          maxWidth: "96vw",
          maxHeight: "90vh",
          autoFocus: false,
          disableClose: true,
          data,
        },
      )
      .afterClosed()
      .subscribe((result) => {
        const results = result?.results ?? [];
        if (results.length) {
          this.onBoxesSplit(results);
        }
      });
  }

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
    for (const box of lot.boxes) {
      box.manufacturingDate = lot.manufacturingDate;
      box.mfgDate = this.parseDisplayDate(lot.manufacturingDate);
    }
    if (lot.expiryOffset != null) {
      this.recalcLotExpiry(lot, lot.manufacturingDate);
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
   * Chọn số tháng/năm HSD ở hàng SAP hoặc lô → tính HSD theo TỪNG LÔ rồi gán chung cho
   * mọi thùng trong lô (SAP: 1 số lô chỉ 1 HSD). Lô có thùng khác MFG → tính từ MFG
   * sớm nhất của lô, không tính riêng từng thùng.
   */
  onExpiryOffsetChange(
    row: SummarySapRow | SummaryLotRow,
    offset: number | null,
  ): void {
    row.expiryOffset = offset;
    if (offset == null) {
      return;
    }
    const lots: SummaryLotRow[] = "lots" in row ? row.lots : [row];
    for (const lot of lots) {
      lot.expiryMode = row.expiryMode;
      lot.expiryOffset = offset;
      this.recalcLotExpiry(lot, row.manufacturingDate);
    }
    if ("lots" in row) {
      row.expirationDate = this.commonValue(
        row.lots.map((l) => l.expirationDate),
      );
    }
  }

  onSapFieldEnter(sap: SummarySapRow, field: EditableField): void {
    if (field !== "location") {
      this.applySapField(sap, field);
      return;
    }
    // Vị trí phải có trong danh sách vị trí; sai → báo lỗi, trả về giá trị cũ
    const boxes = sap.lots.flatMap((l) => l.boxes);
    this.withValidLocation(
      sap.location,
      (location) => {
        sap.location = location;
        this.applySapField(sap, field);
      },
      () => {
        sap.location = this.commonValue(boxes.map((b) => b.location));
      },
    );
  }

  onLotFieldEnter(
    sap: SummarySapRow,
    lot: SummaryLotRow,
    field: EditableField,
  ): void {
    if (field !== "location") {
      this.applyLotField(sap, lot, field);
      return;
    }
    this.withValidLocation(
      lot.location,
      (location) => {
        lot.location = location;
        this.applyLotField(sap, lot, field);
      },
      () => {
        lot.location = this.commonValue(lot.boxes.map((b) => b.location));
      },
    );
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
        storageUnit: b.location,
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
        // Bản ghi thùng gốc → dialog lưu bằng PUT /vendor-label-infos/{id}
        record: rec,
      };
    });

    const dialogData: LotDetailDialogData = {
      partNumber: sap.partNumber,
      manufacturingDate: lot.manufacturingDate || sap.manufacturingDate || "—",
      poCode: this.poCode,
      rows,
    };

    this.dialog
      .open(LotDetailDialogComponent, {
        width: "98vw",
        maxWidth: "98vw",
        height: "92vh",
        maxHeight: "92vh",
        panelClass: "lot-detail-dialog-panel",
        autoFocus: false,
        data: dialogData,
      })
      .afterClosed()
      .subscribe((result: unknown) => {
        if (Array.isArray(result) && result.length) {
          this.onLotBoxesSaved(result as VendorLabelInfoDto[]);
        }
      });
  }

  /** id các thùng đang sửa trên bảng mà chưa lưu (MFG, HSD, vị trí, Rank, UserData, MSL, PO) */
  unsavedBoxIds(): number[] {
    return this.rows.flatMap((sap) =>
      sap.lots.flatMap((lot) =>
        lot.boxes.flatMap((box) =>
          box.record?.id && this.buildUpdatePayload(box) ? [box.record.id] : [],
        ),
      ),
    );
  }

  /** Số dòng vật tư đổi mã kho SAP mà chưa lưu */
  unsavedWhsCount(): number {
    return this.rows.filter((sap) => this.isWhsChanged(sap)).length;
  }

  /** Mã kho của dòng vật tư khác mã kho đã lưu (dòng PO) */
  isWhsChanged(sap: SummarySapRow): boolean {
    if (this.isUnassigned) {
      return false;
    }
    const saved = this.data.po?.materials.find((m) => m.id === sap.id);
    return !!saved && toText(sap.warehouseCode) !== toText(saved.warehouseCode);
  }

  /** Gõ / focus ô Mã kho → lọc theo mã hoặc tên kho (tải /owhs lần đầu) */
  onSapWarehouseSearch(keyword: string): void {
    if (!this.sapWarehouses.length) {
      // Service tải 1 lần / trang (lỗi 3 lần thì thôi) → ở đây chỉ gọi khi chưa có danh sách,
      // không tự gọi lại sau khi nhận về (tránh lặp vô hạn khi lỗi / rỗng)
      if (!this.isLoadingSapWarehouses) {
        this.isLoadingSapWarehouses = true;
        this.receivingService.getSapWarehouses().subscribe((list) => {
          this.isLoadingSapWarehouses = false;
          this.sapWarehouses = list;
          this.filterSapWarehouses(keyword);
        });
      }
      return;
    }
    this.filterSapWarehouses(keyword);
  }

  /** Lọc danh sách kho đã tải theo mã / tên */
  filterSapWarehouses(keyword: string): void {
    const term = toText(keyword).toLowerCase();
    this.filteredSapWarehouses = term
      ? this.sapWarehouses.filter(
          (w) =>
            toText(w.whsCode).toLowerCase().includes(term) ||
            toText(w.whsName).toLowerCase().includes(term),
        )
      : [...this.sapWarehouses];
  }

  /** Hiển thị "whsCode - whsName" (chưa tải danh sách → chỉ mã) */
  displaySapWarehouse = (code: string | null): string => {
    const c = toText(code);
    const whs = this.sapWarehouses.find((w) => w.whsCode === c);
    return whs ? `${whs.whsCode} - ${whs.whsName}` : c;
  };

  /**
   * Rời ô Mã kho sau khi gõ tay → trả về text hiển thị lại trong ô.
   * Có danh sách kho: chỉ nhận mã có trong danh sách (gõ "mã" hoặc "mã - tên"), sai → báo + giữ giá trị cũ.
   * Không tải được danh sách: nhận nguyên mã đã gõ (người dùng tự biết mã kho).
   */
  onSapWarehouseBlur(sap: SummarySapRow, input: HTMLInputElement): void {
    // Đợi 1 nhịp: bấm chọn option thì optionSelected chạy trước (ô đã hiện "mã - tên" mới),
    // tránh báo nhầm phần đang gõ dở là mã sai
    setTimeout(() => {
      input.value = this.onSapWarehouseTyped(sap, input.value);
    }, 150);
  }

  onSapWarehouseTyped(sap: SummarySapRow, text: string): string {
    const typed = toText(text);
    const current = toText(sap.warehouseCode);
    if (!typed || typed === this.displaySapWarehouse(current)) {
      return this.displaySapWarehouse(current);
    }
    const code = toText(typed.split(" - ")[0]);
    if (this.sapWarehouses.length) {
      const found = this.sapWarehouses.find(
        (w) => toText(w.whsCode).toLowerCase() === code.toLowerCase(),
      );
      if (!found) {
        this.notificationService.warning(
          `Mã kho "${code}" không có trong danh sách kho SAP.`,
        );
        return this.displaySapWarehouse(current);
      }
      this.applyWhs(sap, found.whsCode);
    } else {
      this.applyWhs(sap, code);
    }
    return this.displaySapWarehouse(sap.warehouseCode);
  }

  /** Chọn kho ở hàng vật tư → áp cho lô / thùng bên dưới (hiển thị); bấm cập nhật mới PATCH */
  onSapWarehouseSelected(sap: SummarySapRow, code: string): void {
    this.applyWhs(sap, toText(code));
  }

  onCancel(): void {
    if (this.isSaving) {
      return;
    }
    // Đã lưu thùng ở dialog con → báo màn gọi tải lại
    this.dialogRef?.close(
      this.lotDialogUpdated ? { updated: this.lotDialogUpdated } : null,
    );
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
    // Dòng vật tư đổi mã kho SAP → PATCH /sap-por-1-r-1-s/{id}
    const whsRows = this.rows.filter((sap) => this.isWhsChanged(sap));
    if (!payloads.length && !whsRows.length) {
      this.notificationService.info("Không có thay đổi nào.");
      return;
    }

    const requests: Array<Observable<boolean>> = [
      ...payloads.map((payload) =>
        this.infoTemNccService.updateVendorLabelInfo(payload).pipe(
          map(() => true),
          catchError(() => of(false)),
        ),
      ),
      ...whsRows.map((sap) =>
        this.infoTemNccService
          .patchSapPor1WhsCode(sap.id, toText(sap.warehouseCode))
          .pipe(
            map(() => {
              // Đã lưu → coi mã kho mới là giá trị gốc (hết "chưa lưu")
              const m = this.data.po?.materials.find((x) => x.id === sap.id);
              if (m) {
                m.warehouseCode = toText(sap.warehouseCode);
              }
              return true;
            }),
            catchError(() => of(false)),
          ),
      ),
    ];
    this.isSaving = true;
    forkJoin(requests).subscribe((results) => {
      this.isSaving = false;
      const ok = results.filter(Boolean).length;
      const failed = results.length - ok;
      if (!ok) {
        this.notificationService.error(
          `Cập nhật thất bại ${failed} mục — vui lòng thử lại.`,
        );
        return;
      }
      if (failed) {
        this.notificationService.warning(
          `Cập nhật ${ok}/${results.length} mục thành công, ${failed} mục lỗi.`,
        );
        return;
      }
      const unmatched = this.isUnassigned
        ? payloads.filter((p) => toText(p.userData5) && !p.sapPor1Id).length
        : 0;
      if (unmatched) {
        this.notificationService.warning(
          `Cập nhật ${ok} thùng; ${unmatched} thùng có PO không chứa mã vật tư đó nên vẫn ở "Hàng chờ vật tư".`,
        );
      } else {
        const parts = [
          payloads.length ? `${payloads.length} thùng` : "",
          whsRows.length ? `${whsRows.length} mã kho` : "",
        ].filter(Boolean);
        this.notificationService.success(
          `Cập nhật thành công ${parts.join(", ")}.`,
        );
      }
      if (this.embedded) {
        this.saved.emit(ok);
      } else {
        this.dialogRef?.close({ updated: ok });
      }
    });
  }

  /**
   * Dựng lại bảng từ dữ liệu mới nhưng giữ các thùng đang sửa dở (chưa lưu):
   * so theo id bản ghi thùng, áp lại giá trị đang sửa rồi tính lại hàng lô / SAP.
   */
  private rebuildPreservingEdits(beforeRebuild?: () => void): void {
    const pending = new Map<number, SummaryBoxRow>();
    for (const sap of this.rows) {
      for (const lot of sap.lots) {
        for (const box of lot.boxes) {
          const id = box.record?.id;
          if (id && this.buildUpdatePayload(box)) {
            pending.set(id, box);
          }
        }
      }
    }
    const pendingWhs = new Map(
      this.rows
        .filter((sap) => this.isWhsChanged(sap))
        .map((sap) => [sap.id, sap.warehouseCode]),
    );
    beforeRebuild?.();
    this.rows = this.buildRowsFromPo(this.data.po);
    for (const sap of this.rows) {
      const whs = pendingWhs.get(sap.id);
      if (whs !== undefined) {
        this.applyWhs(sap, whs);
      }
    }
    for (const sap of this.rows) {
      let sapTouched = false;
      for (const lot of sap.lots) {
        let lotTouched = false;
        for (const box of lot.boxes) {
          const old = box.record?.id ? pending.get(box.record.id) : undefined;
          if (!old) {
            continue;
          }
          box.manufacturingDate = old.manufacturingDate;
          box.expirationDate = old.expirationDate;
          box.location = old.location;
          box.warehouseCode = old.warehouseCode;
          box.userData1 = old.userData1;
          box.userData2 = old.userData2;
          box.userData3 = old.userData3;
          box.userData4 = old.userData4;
          box.msl = old.msl;
          box.po = old.po;
          box.mfgDate = old.mfgDate;
          box.expiryMode = old.expiryMode;
          box.expiryOffset = old.expiryOffset;
          lotTouched = true;
        }
        if (lotTouched) {
          sapTouched = true;
          this.refreshAggregate(lot, lot.boxes);
        }
      }
      if (sapTouched) {
        this.refreshAggregate(sap, sap.lots);
      }
    }
    this.expandForHighlight();
    this.loadPartNumbers();
  }

  /**
   * Dialog "Cập nhật thông tin vật tư" đã PUT các thùng → ghi bản ghi mới vào dữ liệu
   * (giữ các ô đang sửa dở ở bảng này), báo màn ngoài tải lại.
   */
  /** Đã tách thùng: thùng gốc rời hàng chờ (vào PO), thùng dư mới thế chỗ trong lô */
  private onBoxesSplit(results: SplitBoxDialogResult["results"]): void {
    const byOriginal = new Map(results.map((r) => [r.original.id, r]));
    this.rebuildPreservingEdits(() => {
      for (const m of this.data.po?.materials ?? []) {
        for (const l of m.lots ?? []) {
          l.boxes = (l.boxes ?? []).map(
            (b) => byOriginal.get(b.id)?.remainder ?? b,
          );
        }
      }
    });
    // SL đã nhận của dòng PO — lần tách sau tính đúng SL còn nhận
    for (const r of results) {
      const line = this.data.unassigned?.poLines.find(
        (l) => l.id === r.poLineId,
      );
      if (line) {
        line.receivedQty += r.intoPoQty;
      }
    }
    this.lotDialogUpdated += results.length;
    if (this.embedded) {
      this.saved.emit(results.length);
    }
  }

  private onLotBoxesSaved(saved: VendorLabelInfoDto[]): void {
    const byId = new Map(saved.map((r) => [r.id, r]));
    this.rebuildPreservingEdits(() => {
      for (const m of this.data.po?.materials ?? []) {
        for (const l of m.lots ?? []) {
          for (const b of l.boxes ?? []) {
            const next = byId.get(b.id);
            if (next) {
              Object.assign(b, next);
            }
          }
        }
      }
    });
    this.lotDialogUpdated += saved.length;
    if (this.embedded) {
      this.saved.emit(saved.length);
    }
  }

  /** Gán mã kho SAP cho hàng vật tư + lô + thùng bên dưới (mã kho theo dòng PO) */
  private applyWhs(sap: SummarySapRow, whsCode: string): void {
    sap.warehouseCode = whsCode;
    for (const lot of sap.lots) {
      lot.warehouseCode = whsCode;
      for (const box of lot.boxes) {
        box.warehouseCode = whsCode;
      }
    }
  }

  /** Tính lại giá trị chung (MFG, HSD, vị trí, mã kho, PO) của hàng cha từ các dòng con */
  private refreshAggregate(
    target: SummaryLotRow | SummarySapRow,
    children: Array<SummaryLotRow | SummaryBoxRow>,
  ): void {
    target.manufacturingDate = this.commonValue(
      children.map((c) => c.manufacturingDate),
    );
    target.expirationDate = this.commonValue(
      children.map((c) => c.expirationDate),
    );
    target.location = this.commonValue(children.map((c) => c.location));
    target.warehouseCode = this.commonValue(
      children.map((c) => c.warehouseCode),
    );
    target.po = this.commonValue(children.map((c) => c.po));
    target.mfgDate = this.parseDisplayDate(target.manufacturingDate);
  }

  private expandForHighlight(): void {
    const h = this.highlightTarget;
    if (!h) {
      return;
    }
    for (const sap of this.rows) {
      if (
        toText(sap.sapCode).toLowerCase() === toText(h.sapCode).toLowerCase()
      ) {
        this.expandedSapIds.add(sap.id);
      }
    }
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
      // storageUnit = vị trí kho, subStorageUnit trống; mã kho SAP theo dòng PO
      ...locationFields(box.location),
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
      // Đã gán PO → bỏ mã lý do hàng chờ ở status
      if (payload.sapPor1Id) {
        Object.assign(payload, clearQueueReason(rec));
      }
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
    // Màn Scan: thùng chưa có HSD → tự điền NSX + 2 năm (chưa lưu, bấm Lưu mới PUT)
    const autoHsd = this.embedded
      ? autoExpiryDates(
          materials.flatMap((m) =>
            (m.lots ?? []).flatMap((l) => l.boxes ?? []),
          ),
        )
      : new Map<number, string>();
    return materials.map((m) => {
      const lots: SummaryLotRow[] = (m.lots ?? []).map((lot) => {
        // Mã kho SAP = whsCode của dòng vật tư trong PO
        const boxes: SummaryBoxRow[] = (lot.boxes ?? []).map((b) =>
          this.toBoxRow(b, m.warehouseCode, autoHsd.get(b.id)),
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
          warehouseCode: toText(m.warehouseCode),
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
        warehouseCode: toText(m.warehouseCode),
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
  private toBoxRow(
    b: VendorLabelInfoDto,
    whsCode: string,
    autoHsd?: string,
  ): SummaryBoxRow {
    const mfg = this.toDisplayDate(toText(b.manufacturingDate));
    return {
      id: b.id,
      boxCode: toText(b.reelId),
      reelId: toText(b.reelId),
      quantity: Number(b.initialQuantity ?? 0),
      manufacturingDate: mfg,
      expirationDate: this.toDisplayDate(
        toText(b.expirationDate) || (autoHsd ?? ""),
      ),
      location: boxLocation(b),
      warehouseCode: toText(whsCode),
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

  private applySapField(sap: SummarySapRow, field: EditableField): void {
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

  private applyLotField(
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

  /**
   * Kiểm tra vị trí có trong danh sách vị trí (IndexedDB, khớp đúng tên / tên đầy đủ).
   * Rỗng → cho qua (xóa vị trí); có → onValid(tên đầy đủ); không có → báo lỗi + onInvalid().
   */
  private withValidLocation(
    raw: string,
    onValid: (location: string) => void,
    onInvalid: () => void,
  ): void {
    const value = toText(raw);
    if (!value) {
      onValid("");
      return;
    }
    const lower = value.toLowerCase();
    void this.warehouseCache
      .ensureSynced()
      .then(() => this.warehouseCache.searchByName(value))
      .then((list) => {
        const found = list.find(
          (w) =>
            toText(w.locationFullName).toLowerCase() === lower ||
            toText(w.locationName).toLowerCase() === lower,
        );
        if (found) {
          onValid(toText(found.locationFullName) || toText(found.locationName));
          return;
        }
        this.notificationService.error(
          `Vị trí "${value}" không có trong danh sách vị trí.`,
        );
        onInvalid();
      })
      .catch(() => {
        this.notificationService.error("Không kiểm tra được danh sách vị trí.");
        onInvalid();
      });
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

  /** HSD của lô = MFG lô (chung / sớm nhất của thùng / của hàng cha) + offset → gán cho mọi thùng */
  private recalcLotExpiry(lot: SummaryLotRow, fallbackMfg: string): void {
    const base =
      toText(lot.manufacturingDate) ||
      this.earliestDisplayDate(lot.boxes.map((b) => b.manufacturingDate)) ||
      toText(fallbackMfg);
    const probe: ExpiryControls & {
      manufacturingDate: string;
      expirationDate: string;
    } = {
      expiryMode: lot.expiryMode,
      expiryOffset: lot.expiryOffset,
      mfgDate: null,
      manufacturingDate: base,
      expirationDate: lot.expirationDate,
    };
    this.recalcExpiry(probe);
    lot.expirationDate = probe.expirationDate;
    for (const box of lot.boxes) {
      box.expiryMode = lot.expiryMode;
      box.expiryOffset = lot.expiryOffset;
      box.expirationDate = lot.expirationDate;
    }
  }

  /** Ngày sớm nhất (dd/MM/yyyy) trong danh sách; không có → "" */
  private earliestDisplayDate(values: string[]): string {
    const dates = values
      .map((v) => this.parseDisplayDate(v))
      .filter((d): d is Date => !!d);
    if (!dates.length) {
      return "";
    }
    return this.formatDisplayDate(
      new Date(Math.min(...dates.map((d) => d.getTime()))),
    );
  }
}
