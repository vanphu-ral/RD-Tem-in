import {
  ChangeDetectorRef,
  Component,
  Inject,
  OnDestroy,
  OnInit,
} from "@angular/core";
import { BreakpointObserver } from "@angular/cdk/layout";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from "@angular/material/dialog";
import { DialogContentExampleDialogComponent } from "../../list/confirm-dialog/confirm-dialog.component";
import { MatAutocompleteTrigger } from "@angular/material/autocomplete";
import {
  ReceivingSuppliesService,
  SapOwhsDto,
  WarehouseLocation,
} from "app/entities/generate-tem-in/service/receiving-supplies.service";
import {
  CreateVendorTemDetailPayload,
  ManagerTemNccService,
} from "app/entities/list-material/services/info-tem-ncc.service";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { Subscription, catchError, forkJoin, map, of } from "rxjs";
import { ScannedItem } from "../scan-item-dialog.component";
import {
  AggregateEditRow,
  SCAN_AGGREGATE_COLUMNS,
  SCAN_ITEM_MISSING_FIELDS,
  ScanItemColumnDef,
  DEFAULT_MSL,
  DEFAULT_USERDATA,
  applyEditRowToScannedItem,
  buildDefaultUserData4,
  buildVendorTemBatchUpdatePayload,
  fromDateInputValue as fromDateInputValueUtil,
  scannedItemToEditRow,
  toDateInputValue as toDateInputValueUtil,
} from "../../shared/scan-item-columns.util";

/** Trường bắt buộc highlight trên mobile. */
const MOBILE_REQUIRED_KEYS = [
  "expirationDate",
  "locationOverride",
  "storageUnit",
] as const;

/** Breakpoint xem card mobile (máy tính bảng dọc / điện thoại). */
const MOBILE_BREAKPOINT = "(max-width: 960px)";

export interface ScanAggregateDialogData {
  items: ScannedItem[];
  poCode?: string;
  newItemCount: number;
  importVendorTemTransactionsId: number;
  productNameBySap?: Record<string, string>;
  productNameByPoDetailId?: Record<number, string>;
  currentUser?: string;
}

export interface ScanAggregateGroup {
  key: string;
  label: string;
  groupBy: "sap" | "part";
  rows: AggregateEditRow[];
  headerValues: Record<string, string>;
  autoFillDefaults: boolean;
  expanded: boolean;
}

@Component({
  selector: "jhi-scan-aggregate-dialog",
  templateUrl: "./scan-aggregate-dialog.component.html",
  styleUrls: ["./scan-aggregate-dialog.component.scss"],
})
export class ScanAggregateDialogComponent implements OnInit, OnDestroy {
  readonly largeListThreshold = 80;
  readonly groupPageSize = 40;
  readonly mobileRequiredKeys = MOBILE_REQUIRED_KEYS;

  columns: ScanItemColumnDef[] = SCAN_AGGREGATE_COLUMNS;
  /** Cột hiển thị trong lưới 2 cột mobile (bỏ itemName — nằm ở header nhóm). */
  mobileFields: ScanItemColumnDef[] = SCAN_AGGREGATE_COLUMNS.filter(
    (c) => c.key !== "itemName",
  );
  groups: ScanAggregateGroup[] = [];
  sourceById = new Map<string, ScannedItem>();

  /** Tìm kiếm thông minh (SAP / Part / ReelID). */
  smartSearch = "";
  filterSmartTerm = "";
  showAdvancedFilters = false;

  searchSapCode = "";
  searchPartNumber = "";
  searchReelId = "";
  filterSapTerm = "";
  filterPartTerm = "";
  filterReelTerm = "";

  isMobileView = false;

  filteredLocationOptions: WarehouseLocation[] = [];
  lastLocationSearchTerm = "";
  locationSearchSettled = false;
  locationSearchPending = false;
  sapWarehouseList: SapOwhsDto[] = [];
  filteredSapWarehouseList: SapOwhsDto[] = [];
  isLoadingSapWarehouses = false;
  isSaving = false;

  private locationSearchSeq = 0;
  private locationSearchTimer: ReturnType<typeof setTimeout> | null = null;
  private filterDebounceTimer: ReturnType<typeof setTimeout> | null = null;
  private activeLocationTrigger: MatAutocompleteTrigger | null = null;
  private initialSnapshot = new Map<string, string>();
  private groupPageIndex = new Map<string, number>();
  private breakpointSub: Subscription | null = null;

  constructor(
    private dialogRef: MatDialogRef<
      ScanAggregateDialogComponent,
      { confirmed: boolean; items: ScannedItem[] } | null
    >,
    private dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: ScanAggregateDialogData,
    private receivingService: ReceivingSuppliesService,
    private managerTemNccService: ManagerTemNccService,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef,
    private breakpointObserver: BreakpointObserver,
  ) {}

  ngOnInit(): void {
    (this.data.items ?? []).forEach((item) => {
      this.sourceById.set(item.id, { ...item });
    });
    this.groups = this.buildGroups(this.data.items ?? []);
    this.enrichProductNames();
    void this.receivingService.initWarehouses();
    this.loadSapWarehouses();
    this.breakpointSub = this.breakpointObserver
      .observe(MOBILE_BREAKPOINT)
      .subscribe((state) => {
        this.isMobileView = state.matches;
        this.cdr.markForCheck();
      });
  }

  ngOnDestroy(): void {
    if (this.locationSearchTimer) {
      clearTimeout(this.locationSearchTimer);
      this.locationSearchTimer = null;
    }
    if (this.filterDebounceTimer) {
      clearTimeout(this.filterDebounceTimer);
      this.filterDebounceTimer = null;
    }
    this.breakpointSub?.unsubscribe();
    this.breakpointSub = null;
  }

  get isLargeList(): boolean {
    return this.totalReelCount > this.largeListThreshold;
  }

  get hasChanges(): boolean {
    return this.groups.some((group) =>
      group.rows.some(
        (row) => this.initialSnapshot.get(row.id) !== this.serializeRow(row),
      ),
    );
  }

  get totalReelCount(): number {
    return this.data.items?.length ?? 0;
  }

  get totalQty(): number {
    return (this.data.items ?? []).reduce(
      (sum, item) => sum + (Number(item.initialQuantity) || 0),
      0,
    );
  }

  get visibleGroups(): ScanAggregateGroup[] {
    if (!this.hasAnyFilterTerm()) {
      return this.groups;
    }
    return this.groups.filter(
      (group) => this.getFilteredGroupRows(group).length > 0,
    );
  }

  get visibleGroupCount(): number {
    return this.visibleGroups.length;
  }

  get visibleRowCount(): number {
    return this.visibleGroups.reduce(
      (sum, g) => sum + this.getFilteredGroupRows(g).length,
      0,
    );
  }

  get hasActiveGroupFilter(): boolean {
    return (
      Boolean(this.smartSearch.trim()) ||
      Boolean(this.searchSapCode.trim()) ||
      Boolean(this.searchPartNumber.trim()) ||
      Boolean(this.searchReelId.trim())
    );
  }

  get globalMissingLabels(): string[] {
    const labels = new Set<string>();
    this.visibleGroups.forEach((g) => {
      this.getGroupMissingFields(g).forEach((f) => labels.add(f.label));
    });
    return Array.from(labels);
  }

  /** Gợi ý datalist — HSD đã có trong danh sách. */
  get expirationSuggestions(): string[] {
    return this.collectFieldSuggestions("expirationDate");
  }

  /** Gợi ý datalist — vị trí. */
  get locationSuggestions(): string[] {
    const fromRows = this.collectFieldSuggestions("locationOverride");
    const fromSearch = this.filteredLocationOptions
      .map((w) => (w.locationName || w.locationFullName || "").trim())
      .filter(Boolean);
    return Array.from(new Set([...fromRows, ...fromSearch])).slice(0, 40);
  }

  /** Gợi ý datalist — mã kho. */
  get storageSuggestions(): string[] {
    const fromRows = this.collectFieldSuggestions("storageUnit");
    const fromWhs = this.sapWarehouseList.map((w) => w.whsCode).filter(Boolean);
    return Array.from(new Set([...fromRows, ...fromWhs])).slice(0, 40);
  }

  clearGroupFilters(): void {
    this.smartSearch = "";
    this.searchSapCode = "";
    this.searchPartNumber = "";
    this.searchReelId = "";
    this.filterSmartTerm = "";
    this.filterSapTerm = "";
    this.filterPartTerm = "";
    this.filterReelTerm = "";
    this.resetGroupPages();
    this.cdr.markForCheck();
  }

  toggleAdvancedFilters(): void {
    this.showAdvancedFilters = !this.showAdvancedFilters;
  }

  scheduleFilterUpdate(): void {
    if (this.filterDebounceTimer) {
      clearTimeout(this.filterDebounceTimer);
    }
    this.filterDebounceTimer = setTimeout(() => {
      this.filterSmartTerm = this.smartSearch.trim().toLowerCase();
      this.filterSapTerm = this.searchSapCode.trim().toLowerCase();
      this.filterPartTerm = this.searchPartNumber.trim().toLowerCase();
      this.filterReelTerm = this.searchReelId.trim().toLowerCase();
      this.resetGroupPages();
      this.cdr.markForCheck();
    }, 250);
  }

  /** Lọc dòng trong nhóm theo bộ lọc đang active. */
  getFilteredGroupRows(group: ScanAggregateGroup): AggregateEditRow[] {
    if (!this.hasAnyFilterTerm()) {
      return group.rows;
    }
    return group.rows.filter((row) => this.rowMatchesFilters(row));
  }

  getGroupPageRows(group: ScanAggregateGroup): AggregateEditRow[] {
    if (!group.expanded) {
      return [];
    }
    const rows = this.getFilteredGroupRows(group);
    const page = this.groupPageIndex.get(group.key) ?? 0;
    const start = page * this.groupPageSize;
    return rows.slice(start, start + this.groupPageSize);
  }

  getGroupPageIndex(group: ScanAggregateGroup): number {
    return this.groupPageIndex.get(group.key) ?? 0;
  }

  getGroupPageCount(group: ScanAggregateGroup): number {
    return Math.max(
      1,
      Math.ceil(this.getFilteredGroupRows(group).length / this.groupPageSize),
    );
  }

  prevGroupPage(group: ScanAggregateGroup): void {
    const current = this.getGroupPageIndex(group);
    if (current > 0) {
      this.groupPageIndex.set(group.key, current - 1);
      this.cdr.markForCheck();
    }
  }

  nextGroupPage(group: ScanAggregateGroup): void {
    const current = this.getGroupPageIndex(group);
    const maxPage = this.getGroupPageCount(group) - 1;
    if (current < maxPage) {
      this.groupPageIndex.set(group.key, current + 1);
      this.cdr.markForCheck();
    }
  }

  getGroupMissingFields(
    group: ScanAggregateGroup,
  ): { key: string; label: string }[] {
    return SCAN_ITEM_MISSING_FIELDS.filter((field) =>
      group.rows.some((row) => !String(row[field.key] ?? "").trim()),
    ).map((field) => ({ key: field.key, label: field.label }));
  }

  isFieldMissing(row: AggregateEditRow, key: string): boolean {
    if (!SCAN_ITEM_MISSING_FIELDS.some((f) => f.key === key)) {
      return false;
    }
    return !String(row[key] ?? "").trim();
  }

  /** Header nhóm thiếu giá trị trong khi còn dòng thiếu field đó. */
  isGroupHeaderMissing(group: ScanAggregateGroup, key: string): boolean {
    if (String(group.headerValues[key] ?? "").trim()) {
      return false;
    }
    return this.getGroupMissingFields(group).some((f) => f.key === key);
  }

  isMobileRequiredKey(key: string): boolean {
    return (MOBILE_REQUIRED_KEYS as readonly string[]).includes(key);
  }

  isDateColumn(key: string): boolean {
    return key === "manufacturingDate" || key === "expirationDate";
  }

  toDateInputValue(value: string | number | null | undefined): string {
    const text = value == null ? "" : String(value);
    return toDateInputValueUtil(text);
  }

  fromDateInputValue(value: string | null | undefined): string {
    return fromDateInputValueUtil(value);
  }

  getGroupSapCode(group: ScanAggregateGroup): string {
    if (group.groupBy === "sap") {
      return group.key.replace(/^sap:/i, "");
    }
    const first = group.rows.find((r) => String(r.sapCode ?? "").trim());
    return String(first?.sapCode ?? "").trim() || "—";
  }

  getGroupTotalQty(group: ScanAggregateGroup): number {
    return this.getFilteredGroupRows(group).reduce(
      (sum, row) => sum + (Number(row.initialQuantity) || 0),
      0,
    );
  }

  getGroupItemName(group: ScanAggregateGroup): string {
    const named = group.rows.find((r) => String(r.itemName ?? "").trim());
    return String(named?.itemName ?? "").trim();
  }

  getGroupPo(group: ScanAggregateGroup): string {
    const withPo = group.rows.find((r) => String(r.userData5 ?? "").trim());
    return (
      String(withPo?.userData5 ?? "").trim() ||
      String(this.data.poCode ?? "").trim()
    );
  }

  getRowDisplayIndex(group: ScanAggregateGroup, row: AggregateEditRow): number {
    const idx = this.getFilteredGroupRows(group).findIndex(
      (r) => r.id === row.id,
    );
    return idx >= 0 ? idx + 1 : 0;
  }

  getDatalistId(key: string): string {
    if (key === "expirationDate") {
      return "agg-dl-hsd";
    }
    if (key === "locationOverride") {
      return "agg-dl-location";
    }
    if (key === "storageUnit") {
      return "agg-dl-storage";
    }
    return "";
  }

  /** Áp dụng giá trị header nhóm + mặc định UD/MSL cho cả nhóm. */
  applyGroupBatch(group: ScanAggregateGroup): void {
    Object.entries(group.headerValues).forEach(([key, value]) => {
      const trimmed = String(value ?? "").trim();
      if (!trimmed) {
        return;
      }
      this.onHeaderValueChange(group, key, String(value));
    });
    group.rows.forEach((row) => this.applyDefaultFields(row));
    group.autoFillDefaults = true;
    this.notificationService.success(
      `Đã áp dụng nhóm cho ${group.rows.length} reel.`,
    );
    this.cdr.markForCheck();
  }

  /**
   * Điền nhanh toàn bộ: lấy giá trị phổ biến nhất cho HSD / Vị trí / Mã kho,
   * rồi điền vào các ô còn trống + mặc định UD/MSL.
   */
  fillAllMissingQuick(): void {
    let filled = 0;
    MOBILE_REQUIRED_KEYS.forEach((key) => {
      const common = this.getMostCommonValue(key);
      if (!common) {
        return;
      }
      this.groups.forEach((group) => {
        if (!String(group.headerValues[key] ?? "").trim()) {
          group.headerValues[key] = common;
        }
        group.rows.forEach((row) => {
          if (!String(row[key] ?? "").trim()) {
            row[key] = common;
            filled += 1;
          }
        });
      });
    });
    this.groups.forEach((group) => {
      group.rows.forEach((row) => this.applyDefaultFields(row));
      group.autoFillDefaults = true;
    });
    if (filled > 0) {
      this.notificationService.success(
        `Đã điền nhanh ${filled} ô thiếu (HSD / Vị trí / Mã kho) + mặc định UD/MSL.`,
      );
    } else {
      this.notificationService.success(
        "Đã áp dụng mặc định UD/MSL. Không còn ô HSD/Vị trí/Mã kho trống để copy.",
      );
    }
    this.cdr.markForCheck();
  }

  toggleExpand(group: ScanAggregateGroup): void {
    group.expanded = !group.expanded;
  }

  onHeaderValueChange(
    group: ScanAggregateGroup,
    key: string,
    value: string,
  ): void {
    group.headerValues[key] = value;
    const trimmed = (value ?? "").trim();
    if (trimmed === "") {
      return;
    }
    group.rows.forEach((row) => {
      if (key === "initialQuantity") {
        const n = Number(value);
        row.initialQuantity = Number.isFinite(n) ? n : 0;
      } else {
        row[key] = value;
      }
    });
  }

  onHeaderDateChange(
    group: ScanAggregateGroup,
    key: string,
    dateInputValue: string,
  ): void {
    this.onHeaderValueChange(
      group,
      key,
      this.fromDateInputValue(dateInputValue),
    );
  }

  onAutoFillToggle(group: ScanAggregateGroup, checked: boolean): void {
    group.autoFillDefaults = checked;
    if (checked) {
      group.rows.forEach((row) => this.applyDefaultFields(row));
    } else {
      group.rows.forEach((row) => this.clearDefaultFields(row));
    }
  }

  onCellChange(
    group: ScanAggregateGroup,
    row: AggregateEditRow,
    key: string,
  ): void {
    if (key === "initialQuantity") {
      const n = Number(row.initialQuantity);
      if (Number.isFinite(n) && n < 0) {
        row.initialQuantity = 0;
      }
    }
    void group;
    void row;
  }

  onRowDateChange(
    row: AggregateEditRow,
    key: string,
    dateInputValue: string,
  ): void {
    row[key] = this.fromDateInputValue(dateInputValue);
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  onConfirm(): void {
    if (!this.hasChanges || this.isSaving) {
      return;
    }

    const invalidRow = this.groups
      .flatMap((g) => g.rows)
      .find((row) => Number(row.initialQuantity) < 0);
    if (invalidRow) {
      this.notificationService.warning(
        "Không được nhập số âm cho trường Số lượng.",
      );
      return;
    }

    const changedRows = this.getChangedRows();
    const rowsToSave = changedRows.filter((row) => {
      const source = this.sourceById.get(row.id);
      return Boolean(source?.dbId);
    });

    if (!rowsToSave.length) {
      this.notificationService.warning(
        "Không có vật tư đã lưu để cập nhật. Chỉnh sửa dòng đã có trên hệ thống.",
      );
      return;
    }

    const payload = rowsToSave.map((row) => this.buildUpdatePayload(row));
    const confirmRef = this.dialog.open(DialogContentExampleDialogComponent, {
      width: "420px",
      autoFocus: false,
      data: {
        title: "Xác nhận cập nhật",
        message: `Bạn có chắc muốn cập nhật ${rowsToSave.length} vật tư đã chỉnh sửa?`,
        confirmText: "Cập nhật",
        cancelText: "Hủy",
      },
    });
    confirmRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }
      this.executeSave(payload);
    });
  }

  trackByGroup(_: number, group: ScanAggregateGroup): string {
    return group.key;
  }

  trackByRow(_: number, row: AggregateEditRow): string {
    return row.id;
  }

  onLocationSearch(
    keyword: string,
    trigger?: MatAutocompleteTrigger | null,
  ): void {
    if (this.locationSearchTimer) {
      clearTimeout(this.locationSearchTimer);
    }
    if (trigger) {
      this.activeLocationTrigger = trigger;
    }
    const term = (keyword ?? "").trim();
    if (!term) {
      this.locationSearchSeq += 1;
      this.filteredLocationOptions = [];
      this.lastLocationSearchTerm = "";
      this.locationSearchSettled = false;
      this.locationSearchPending = false;
      this.cdr.markForCheck();
      return;
    }
    this.locationSearchPending = true;
    this.locationSearchSettled = false;
    this.locationSearchTimer = setTimeout(() => {
      const seq = ++this.locationSearchSeq;
      void this.receivingService
        .searchWarehouses(term)
        .then((list) => {
          if (seq !== this.locationSearchSeq) {
            return;
          }
          this.filteredLocationOptions = list;
          this.lastLocationSearchTerm = term;
          this.locationSearchPending = false;
          this.locationSearchSettled = true;
          this.cdr.detectChanges();
          setTimeout(() => {
            const panelTrigger = this.activeLocationTrigger;
            if (panelTrigger && !panelTrigger.panelOpen) {
              panelTrigger.openPanel();
            }
          });
        })
        .catch(() => {
          if (seq !== this.locationSearchSeq) {
            return;
          }
          this.filteredLocationOptions = [];
          this.lastLocationSearchTerm = term;
          this.locationSearchPending = false;
          this.locationSearchSettled = true;
          this.cdr.detectChanges();
        });
    }, 200);
  }

  showLocationNoResults(): boolean {
    return (
      this.locationSearchSettled &&
      !this.locationSearchPending &&
      Boolean(this.lastLocationSearchTerm) &&
      this.filteredLocationOptions.length === 0
    );
  }

  onHeaderLocationSelected(
    group: ScanAggregateGroup,
    selected: string | WarehouseLocation,
  ): void {
    const name = this.resolveLocationName(selected);
    group.headerValues["locationOverride"] = name;
    if (!name) {
      return;
    }
    group.rows.forEach((row) => {
      row.locationOverride = name;
    });
  }

  onRowLocationSelected(
    row: AggregateEditRow,
    selected: string | WarehouseLocation,
  ): void {
    row.locationOverride = this.resolveLocationName(selected);
  }

  onSapWarehouseSearch(keyword: string): void {
    if (!this.sapWarehouseList.length && !this.isLoadingSapWarehouses) {
      this.loadSapWarehouses(keyword);
      return;
    }
    const term = (keyword ?? "").trim().toLowerCase();
    this.filteredSapWarehouseList = term
      ? this.sapWarehouseList.filter(
          (whs) =>
            whs.whsCode.toLowerCase().includes(term) ||
            whs.whsName.toLowerCase().includes(term),
        )
      : [...this.sapWarehouseList];
  }

  displaySapWarehouse = (code: string | null): string => {
    if (!code) {
      return "";
    }
    const whs = this.sapWarehouseList.find((w) => w.whsCode === code);
    return whs ? `${whs.whsCode} - ${whs.whsName}` : code;
  };

  onHeaderSapWarehouseSelected(group: ScanAggregateGroup, code: string): void {
    const normalized = (code ?? "").trim();
    group.headerValues["storageUnit"] = normalized;
    if (!normalized) {
      return;
    }
    group.rows.forEach((row) => {
      row.storageUnit = normalized;
    });
  }

  onRowSapWarehouseSelected(row: AggregateEditRow, code: string): void {
    row.storageUnit = (code ?? "").trim();
  }

  private hasAnyFilterTerm(): boolean {
    return Boolean(
      this.filterSmartTerm ||
        this.filterSapTerm ||
        this.filterPartTerm ||
        this.filterReelTerm,
    );
  }

  private rowMatchesFilters(row: AggregateEditRow): boolean {
    const sap = String(row.sapCode ?? "").toLowerCase();
    const part = String(row.partNumber ?? "").toLowerCase();
    const reel = String(row.reelId ?? "").toLowerCase();

    if (this.filterSmartTerm) {
      const t = this.filterSmartTerm;
      if (!sap.includes(t) && !part.includes(t) && !reel.includes(t)) {
        return false;
      }
    }
    if (this.filterSapTerm && !sap.includes(this.filterSapTerm)) {
      return false;
    }
    if (this.filterPartTerm && !part.includes(this.filterPartTerm)) {
      return false;
    }
    if (this.filterReelTerm && !reel.includes(this.filterReelTerm)) {
      return false;
    }
    return true;
  }

  private collectFieldSuggestions(key: string): string[] {
    const seen = new Set<string>();
    const out: string[] = [];
    this.groups.forEach((group) => {
      group.rows.forEach((row) => {
        const val = String(row[key] ?? "").trim();
        if (val && !seen.has(val)) {
          seen.add(val);
          out.push(val);
        }
      });
    });
    return out.slice(0, 40);
  }

  private getMostCommonValue(key: string): string {
    const counts = new Map<string, number>();
    this.groups.forEach((group) => {
      group.rows.forEach((row) => {
        const val = String(row[key] ?? "").trim();
        if (!val) {
          return;
        }
        counts.set(val, (counts.get(val) ?? 0) + 1);
      });
    });
    let best = "";
    let bestCount = 0;
    counts.forEach((count, val) => {
      if (count > bestCount) {
        best = val;
        bestCount = count;
      }
    });
    return best;
  }

  private executeSave(payload: CreateVendorTemDetailPayload[]): void {
    this.isSaving = true;
    this.managerTemNccService.batchUpdateVendorTemDetails(payload).subscribe({
      next: () => {
        this.isSaving = false;
        this.notificationService.success(
          `Cập nhật thành công ${payload.length} vật tư.`,
        );
        const items = this.buildScannedItemsFromGroups();
        items.forEach((item) => {
          this.sourceById.set(item.id, { ...item });
        });
        this.captureSnapshot();
        this.dialogRef.close({ confirmed: true, items });
      },
      error: (err) => {
        this.isSaving = false;
        const detail =
          err?.error?.message ??
          err?.error?.detail ??
          err?.message ??
          "Không thể kết nối máy chủ.";
        this.notificationService.error(`Cập nhật vật tư thất bại: ${detail}`);
        this.cdr.markForCheck();
      },
    });
  }

  private buildGroups(items: ScannedItem[]): ScanAggregateGroup[] {
    const collapseByDefault = items.length > this.largeListThreshold;
    const groupMap = new Map<string, ScanAggregateGroup>();
    items.forEach((item) => {
      const editRow = scannedItemToEditRow(item);
      const sap = (editRow.sapCode ?? "").trim();
      const part = (editRow.partNumber ?? "").trim();
      const groupBy: "sap" | "part" = sap ? "sap" : "part";
      const rawKey = sap || part || `unknown-${item.id}`;
      const key = `${groupBy}:${rawKey}`;

      let group = groupMap.get(key);
      if (!group) {
        group = {
          key,
          label:
            groupBy === "sap"
              ? `Mã SAP: ${rawKey}`
              : `Mã PartNumber: ${rawKey}`,
          groupBy,
          rows: [],
          headerValues: this.emptyHeaderValues(),
          autoFillDefaults: false,
          expanded: !collapseByDefault,
        };
        groupMap.set(key, group);
      }
      group.rows.push(editRow);
    });
    return Array.from(groupMap.values());
  }

  private resetGroupPages(): void {
    this.groupPageIndex.clear();
  }

  private emptyHeaderValues(): Record<string, string> {
    const values: Record<string, string> = {};
    this.columns.forEach((col) => {
      if (col.headerEditable) {
        values[col.key] = "";
      }
    });
    return values;
  }

  private applyDefaultFields(row: AggregateEditRow): void {
    if (!String(row.userData1 ?? "").trim()) {
      row.userData1 = DEFAULT_USERDATA;
    }
    if (!String(row.userData2 ?? "").trim()) {
      row.userData2 = DEFAULT_USERDATA;
    }
    if (!String(row.userData3 ?? "").trim()) {
      row.userData3 = DEFAULT_USERDATA;
    }
    if (!String(row.userData4 ?? "").trim()) {
      row.userData4 = buildDefaultUserData4(row);
    }
    if (!String(row.msl ?? "").trim()) {
      row.msl = DEFAULT_MSL;
    }
  }

  private clearDefaultFields(row: AggregateEditRow): void {
    if (row.userData1 === DEFAULT_USERDATA) {
      row.userData1 = "";
    }
    if (row.userData2 === DEFAULT_USERDATA) {
      row.userData2 = "";
    }
    if (row.userData3 === DEFAULT_USERDATA) {
      row.userData3 = "";
    }
    if (
      row.userData4 === buildDefaultUserData4(row) ||
      row.userData4 === DEFAULT_USERDATA
    ) {
      row.userData4 = "";
    }
    if (row.msl === DEFAULT_MSL) {
      row.msl = "";
    }
  }

  private buildScannedItemsFromGroups(): ScannedItem[] {
    const editedById = new Map<string, AggregateEditRow>();
    this.groups.forEach((group) => {
      group.rows.forEach((row) => editedById.set(row.id, row));
    });
    return (this.data.items ?? []).map((item) => {
      const edited = editedById.get(item.id);
      if (!edited) {
        return item;
      }
      const source = this.sourceById.get(item.id) ?? item;
      return applyEditRowToScannedItem(edited, source);
    });
  }

  private resolveLocationName(selected: string | WarehouseLocation): string {
    if (selected == null) {
      return "";
    }
    if (typeof selected !== "string") {
      return (selected.locationName ?? selected.locationFullName ?? "").trim();
    }
    const trimmed = selected.trim();
    const matched = this.filteredLocationOptions.find(
      (w) => w.locationFullName === trimmed || w.locationName === trimmed,
    );
    return matched?.locationName ?? trimmed;
  }

  private loadSapWarehouses(keyword = ""): void {
    if (this.isLoadingSapWarehouses) {
      return;
    }
    this.isLoadingSapWarehouses = true;
    this.receivingService.getSapWarehouses().subscribe({
      next: (data) => {
        this.sapWarehouseList = data;
        this.onSapWarehouseSearch(keyword);
        this.isLoadingSapWarehouses = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoadingSapWarehouses = false;
        this.cdr.markForCheck();
      },
    });
  }

  private enrichProductNames(): void {
    const nameBySap = this.data.productNameBySap ?? {};
    const nameByPoDetailId = this.data.productNameByPoDetailId ?? {};

    this.groups.forEach((group) => {
      group.rows.forEach((row) => {
        if (String(row.itemName ?? "").trim()) {
          return;
        }
        const source = this.sourceById.get(row.id);
        const sap = (row.sapCode ?? "").trim();
        const poDetailId = source?.poDetailId;
        if (sap && nameBySap[sap]) {
          row.itemName = nameBySap[sap];
        } else if (poDetailId && nameByPoDetailId[poDetailId]) {
          row.itemName = nameByPoDetailId[poDetailId];
        }
      });
    });

    const sapCodes = new Set<string>();
    this.groups.forEach((group) => {
      group.rows.forEach((row) => {
        if (String(row.itemName ?? "").trim()) {
          return;
        }
        const sap = (row.sapCode ?? "").trim();
        if (sap) {
          sapCodes.add(sap);
        }
      });
    });

    if (!sapCodes.size) {
      this.captureSnapshot();
      return;
    }

    forkJoin(
      [...sapCodes].map((code) =>
        this.receivingService.getItemName(code).pipe(
          map((name) => ({ code, name: (name ?? "").trim() })),
          catchError(() => of({ code, name: "" })),
        ),
      ),
    ).subscribe({
      next: (results) => {
        const apiNames = new Map(
          results.filter((r) => r.name).map((r) => [r.code, r.name]),
        );
        this.groups.forEach((group) => {
          group.rows.forEach((row) => {
            if (String(row.itemName ?? "").trim()) {
              return;
            }
            const sap = (row.sapCode ?? "").trim();
            const name = apiNames.get(sap);
            if (name) {
              row.itemName = name;
            }
          });
        });
        this.captureSnapshot();
        this.cdr.markForCheck();
      },
      error: () => {
        this.captureSnapshot();
        this.cdr.markForCheck();
      },
    });
  }

  private captureSnapshot(): void {
    this.initialSnapshot.clear();
    this.groups.forEach((group) => {
      group.rows.forEach((row) => {
        this.initialSnapshot.set(row.id, this.serializeRow(row));
      });
    });
  }

  private serializeRow(row: AggregateEditRow): string {
    const { id: _id, ...rest } = row;
    return JSON.stringify(rest);
  }

  private getChangedRows(): AggregateEditRow[] {
    const changed: AggregateEditRow[] = [];
    this.groups.forEach((group) => {
      group.rows.forEach((row) => {
        if (this.initialSnapshot.get(row.id) !== this.serializeRow(row)) {
          changed.push(row);
        }
      });
    });
    return changed;
  }

  private buildUpdatePayload(
    row: AggregateEditRow,
  ): CreateVendorTemDetailPayload {
    const source = this.sourceById.get(row.id)!;
    return buildVendorTemBatchUpdatePayload(source.dbId!, row, {
      vendor: row.vendor ?? source.vendor ?? "",
      vendorQrCode: source.vendorQrCode ?? "",
      status: source.status ?? "NEW",
      createdBy: source.createdBy ?? "",
      createdAt: source.createdAt ?? new Date().toISOString(),
      poDetailId: source.poDetailId,
      importVendorTemTransactionsId:
        source.importVendorTemTransactionsId ||
        this.data.importVendorTemTransactionsId,
      currentUser: this.data.currentUser ?? "unknown",
    });
  }
}
