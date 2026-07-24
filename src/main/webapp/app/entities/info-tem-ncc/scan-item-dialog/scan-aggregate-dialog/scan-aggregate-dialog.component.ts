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
  SCAN_ITEM_MISSING_FIELDS,
  DEFAULT_MSL,
  DEFAULT_USERDATA,
  applyEditRowToScannedItem,
  buildDefaultUserData4,
  buildVendorTemBatchUpdatePayload,
  fromDateInputValue as fromDateInputValueUtil,
  toDateInputValue as toDateInputValueUtil,
} from "../../shared/scan-item-columns.util";
import {
  AggregateLotGroup,
  AggregateMaterialGroup,
  buildAggregateTree,
  flattenTreeRows,
  isFieldMixed,
  isSinglePoMode,
  materialRows,
  sumQty,
  uniformFieldValue,
  writeFieldToRows,
} from "../../shared/aggregate-tree.util";
import {
  ScanAggregateReelDialogComponent,
  ScanAggregateReelDialogData,
} from "./scan-aggregate-reel-dialog.component";

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

@Component({
  selector: "jhi-scan-aggregate-dialog",
  templateUrl: "./scan-aggregate-dialog.component.html",
  styleUrls: ["./scan-aggregate-dialog.component.scss"],
})
export class ScanAggregateDialogComponent implements OnInit, OnDestroy {
  readonly largeListThreshold = 80;

  materials: AggregateMaterialGroup[] = [];
  sourceById = new Map<string, ScannedItem>();
  singlePoMode = true;

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
  isSaving = false;

  filteredLocationOptions: WarehouseLocation[] = [];
  lastLocationSearchTerm = "";
  locationSearchSettled = false;
  locationSearchPending = false;
  sapWarehouseList: SapOwhsDto[] = [];
  filteredSapWarehouseList: SapOwhsDto[] = [];
  isLoadingSapWarehouses = false;

  private locationSearchSeq = 0;
  private locationSearchTimer: ReturnType<typeof setTimeout> | null = null;
  private filterDebounceTimer: ReturnType<typeof setTimeout> | null = null;
  private activeLocationTrigger: MatAutocompleteTrigger | null = null;
  private initialSnapshot = new Map<string, string>();
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
    this.rebuildTree();
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
    }
    if (this.filterDebounceTimer) {
      clearTimeout(this.filterDebounceTimer);
    }
    this.breakpointSub?.unsubscribe();
  }

  get isLargeList(): boolean {
    return this.totalReelCount > this.largeListThreshold;
  }

  get totalReelCount(): number {
    return this.data.items?.length ?? 0;
  }

  get totalQty(): number {
    return sumQty(flattenTreeRows(this.materials));
  }

  get headerPoCode(): string {
    return String(this.data.poCode ?? "").trim();
  }

  get hasChanges(): boolean {
    return flattenTreeRows(this.materials).some(
      (row) => this.initialSnapshot.get(row.id) !== this.serializeRow(row),
    );
  }

  get visibleMaterials(): AggregateMaterialGroup[] {
    if (!this.hasAnyFilterTerm()) {
      return this.materials;
    }
    return this.materials.filter((m) => this.getVisibleLots(m).length > 0);
  }

  get visibleGroupCount(): number {
    return this.visibleMaterials.length;
  }

  get visibleRowCount(): number {
    return this.visibleMaterials.reduce(
      (sum, m) =>
        sum + this.getVisibleLots(m).reduce((s, lot) => s + lot.rows.length, 0),
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
    flattenTreeRows(this.visibleMaterials).forEach((row) => {
      SCAN_ITEM_MISSING_FIELDS.forEach((f) => {
        if (!String(row[f.key] ?? "").trim()) {
          labels.add(f.label);
        }
      });
    });
    return Array.from(labels);
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
    this.clearLotHighlights();
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
      this.applyReelIdExpandHighlight();
      this.cdr.markForCheck();
    }, 250);
  }

  getVisibleLots(material: AggregateMaterialGroup): AggregateLotGroup[] {
    if (!this.hasAnyFilterTerm()) {
      return material.lots;
    }
    return material.lots.filter((lot) =>
      lot.rows.some((row) => this.rowMatchesFilters(row, material)),
    );
  }

  getFilteredLotRows(
    material: AggregateMaterialGroup,
    lot: AggregateLotGroup,
  ): AggregateEditRow[] {
    if (!this.hasAnyFilterTerm()) {
      return lot.rows;
    }
    return lot.rows.filter((row) => this.rowMatchesFilters(row, material));
  }

  materialQty(material: AggregateMaterialGroup): number {
    return sumQty(materialRows(material));
  }

  materialReelCount(material: AggregateMaterialGroup): number {
    return materialRows(material).length;
  }

  getMaterialRows(material: AggregateMaterialGroup): AggregateEditRow[] {
    return materialRows(material);
  }

  lotQty(lot: AggregateLotGroup): number {
    return sumQty(lot.rows);
  }

  toggleExpand(material: AggregateMaterialGroup): void {
    material.expanded = !material.expanded;
  }

  displayCascadeValue(rows: AggregateEditRow[], field: string): string {
    if (isFieldMixed(rows, field)) {
      return "";
    }
    return uniformFieldValue(rows, field) ?? "";
  }

  isCascadeMixed(rows: AggregateEditRow[], field: string): boolean {
    return isFieldMixed(rows, field);
  }

  cascadePlaceholder(rows: AggregateEditRow[], field: string): string {
    return isFieldMixed(rows, field) ? "Nhiều giá trị" : "";
  }

  isMaterialMissing(material: AggregateMaterialGroup): boolean {
    return materialRows(material).some((row) =>
      SCAN_ITEM_MISSING_FIELDS.some((f) => !String(row[f.key] ?? "").trim()),
    );
  }

  materialMissingCount(material: AggregateMaterialGroup): number {
    return materialRows(material).filter((row) =>
      SCAN_ITEM_MISSING_FIELDS.some((f) => !String(row[f.key] ?? "").trim()),
    ).length;
  }

  materialMissingLabels(material: AggregateMaterialGroup): string[] {
    const labels = new Set<string>();
    materialRows(material).forEach((row) => {
      SCAN_ITEM_MISSING_FIELDS.forEach((f) => {
        if (!String(row[f.key] ?? "").trim()) {
          labels.add(f.label);
        }
      });
    });
    return Array.from(labels);
  }

  lotMissingLabels(lot: AggregateLotGroup): string[] {
    const labels = new Set<string>();
    lot.rows.forEach((row) => {
      SCAN_ITEM_MISSING_FIELDS.forEach((f) => {
        if (!String(row[f.key] ?? "").trim()) {
          labels.add(f.label);
        }
      });
    });
    return Array.from(labels);
  }

  onMaterialFieldChange(
    material: AggregateMaterialGroup,
    field: string,
    value: string,
  ): void {
    const trimmed = (value ?? "").trim();
    if (!trimmed && this.cascadePlaceholder(materialRows(material), field)) {
      return;
    }
    writeFieldToRows(materialRows(material), field, value);
    if (field === "manufacturingDate") {
      materialRows(material).forEach((row) => {
        row.userData4 = buildDefaultUserData4(row);
      });
    }
  }

  onMaterialDateChange(
    material: AggregateMaterialGroup,
    field: string,
    dateInput: string,
  ): void {
    this.onMaterialFieldChange(
      material,
      field,
      fromDateInputValueUtil(dateInput),
    );
  }

  onLotFieldChange(lot: AggregateLotGroup, field: string, value: string): void {
    const trimmed = (value ?? "").trim();
    if (!trimmed && this.cascadePlaceholder(lot.rows, field)) {
      return;
    }
    writeFieldToRows(lot.rows, field, value);
    if (field === "manufacturingDate") {
      lot.rows.forEach((row) => {
        row.userData4 = buildDefaultUserData4(row);
      });
    }
  }

  onLotDateChange(
    lot: AggregateLotGroup,
    field: string,
    dateInput: string,
  ): void {
    this.onLotFieldChange(lot, field, fromDateInputValueUtil(dateInput));
  }

  onAutoFillToggle(material: AggregateMaterialGroup, checked: boolean): void {
    material.autoFillDefaults = checked;
    const rows = materialRows(material);
    if (checked) {
      rows.forEach((row) => this.applyDefaultFields(row));
    } else {
      rows.forEach((row) => this.clearDefaultFields(row));
    }
  }

  toDateInputValue(value: string | number | null | undefined): string {
    return toDateInputValueUtil(value == null ? "" : String(value));
  }

  openReelDialog(
    material: AggregateMaterialGroup,
    lot: AggregateLotGroup,
  ): void {
    const rows = this.getFilteredLotRows(material, lot);
    const data: ScanAggregateReelDialogData = {
      rows,
      sapCode: material.sapCode,
      partNumber: material.partNumber,
      itemName: material.itemName,
      lotNumber: lot.lotNumber,
      poCode: lot.poCode || this.headerPoCode,
      vendor: lot.vendor,
      msl: material.msl,
      isMobile: this.isMobileView,
    };
    const ref = this.dialog.open(ScanAggregateReelDialogComponent, {
      width: "100vw",
      maxWidth: "100vw",
      height: "100vh",
      maxHeight: "100dvh",
      panelClass: this.isMobileView
        ? ["scan-aggregate-reel-panel", "scan-aggregate-reel-panel-mobile"]
        : ["scan-aggregate-reel-panel", "scan-aggregate-reel-panel-desktop"],
      autoFocus: false,
      data,
    });
    ref.afterClosed().subscribe((updated: AggregateEditRow[] | null) => {
      if (!updated?.length) {
        return;
      }
      const byId = new Map(updated.map((r) => [r.id, r]));
      lot.rows = lot.rows.map((row) => {
        const next = byId.get(row.id);
        return next ? { ...next } : row;
      });
      // Nếu sửa số lô trong dialog → rebuild cây
      this.rebuildTreePreservingEdits();
      this.cdr.markForCheck();
    });
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  onConfirm(): void {
    if (!this.hasChanges || this.isSaving) {
      return;
    }
    const allRows = flattenTreeRows(this.materials);
    const invalidRow = allRows.find((row) => Number(row.initialQuantity) < 0);
    if (invalidRow) {
      this.notificationService.warning(
        "Không được nhập số âm cho trường Số lượng.",
      );
      return;
    }
    const changedRows = allRows.filter(
      (row) => this.initialSnapshot.get(row.id) !== this.serializeRow(row),
    );
    const rowsToSave = changedRows.filter((row) =>
      Boolean(this.sourceById.get(row.id)?.dbId),
    );
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

  trackByMaterial(_: number, m: AggregateMaterialGroup): string {
    return m.key;
  }

  trackByLot(_: number, lot: AggregateLotGroup): string {
    return lot.key;
  }

  // ---- location / warehouse ----
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
            const t = this.activeLocationTrigger;
            if (t && !t.panelOpen) {
              t.openPanel();
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

  onMaterialLocationSelected(
    material: AggregateMaterialGroup,
    selected: string | WarehouseLocation,
  ): void {
    const name = this.resolveLocationName(selected);
    writeFieldToRows(materialRows(material), "locationOverride", name);
  }

  onLotLocationSelected(
    lot: AggregateLotGroup,
    selected: string | WarehouseLocation,
  ): void {
    const name = this.resolveLocationName(selected);
    writeFieldToRows(lot.rows, "locationOverride", name);
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

  onMaterialWarehouseSelected(
    material: AggregateMaterialGroup,
    code: string,
  ): void {
    writeFieldToRows(
      materialRows(material),
      "storageUnit",
      (code ?? "").trim(),
    );
  }

  onLotWarehouseSelected(lot: AggregateLotGroup, code: string): void {
    writeFieldToRows(lot.rows, "storageUnit", (code ?? "").trim());
  }

  private rebuildTree(): void {
    this.materials = buildAggregateTree(this.data.items ?? [], {
      collapseByDefault:
        (this.data.items?.length ?? 0) > this.largeListThreshold,
      dialogPoCode: this.data.poCode,
    });
    this.singlePoMode = isSinglePoMode(this.materials, this.data.poCode);
    this.captureSnapshot();
  }

  /** Rebuild cây từ rows đang edit (sau khi đổi số lô trong dialog reel). */
  private rebuildTreePreservingEdits(): void {
    const edited = flattenTreeRows(this.materials);
    const expandedKeys = new Set(
      this.materials.filter((m) => m.expanded).map((m) => m.key),
    );
    const asItems: ScannedItem[] = edited.map((row) => {
      const source = this.sourceById.get(row.id) ?? ({} as ScannedItem);
      return applyEditRowToScannedItem(row, {
        ...source,
        id: row.id,
        reelId: row.reelId,
      } as ScannedItem);
    });
    this.materials = buildAggregateTree(asItems, {
      collapseByDefault: false,
      dialogPoCode: this.data.poCode,
    });
    this.materials.forEach((m) => {
      m.expanded = expandedKeys.has(m.key);
    });
    this.singlePoMode = isSinglePoMode(this.materials, this.data.poCode);
  }

  private hasAnyFilterTerm(): boolean {
    return Boolean(
      this.filterSmartTerm ||
        this.filterSapTerm ||
        this.filterPartTerm ||
        this.filterReelTerm,
    );
  }

  private rowMatchesFilters(
    row: AggregateEditRow,
    material: AggregateMaterialGroup,
  ): boolean {
    const sap = String(row.sapCode || material.sapCode || "").toLowerCase();
    const part = String(
      row.partNumber || material.partNumber || "",
    ).toLowerCase();
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

  private applyReelIdExpandHighlight(): void {
    this.clearLotHighlights();
    if (!this.filterReelTerm) {
      return;
    }
    this.materials.forEach((m) => {
      m.lots.forEach((lot) => {
        const hit = lot.rows.some((r) =>
          String(r.reelId ?? "")
            .toLowerCase()
            .includes(this.filterReelTerm),
        );
        if (hit) {
          lot.highlighted = true;
          m.expanded = true;
        }
      });
    });
  }

  private clearLotHighlights(): void {
    this.materials.forEach((m) => {
      m.lots.forEach((lot) => {
        lot.highlighted = false;
      });
    });
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

  private executeSave(payload: CreateVendorTemDetailPayload[]): void {
    this.isSaving = true;
    this.managerTemNccService.batchUpdateVendorTemDetails(payload).subscribe({
      next: () => {
        this.isSaving = false;
        this.notificationService.success(
          `Cập nhật thành công ${payload.length} vật tư.`,
        );
        const items = this.buildScannedItemsFromTree();
        items.forEach((item) => this.sourceById.set(item.id, { ...item }));
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

  private buildScannedItemsFromTree(): ScannedItem[] {
    const editedById = new Map(
      flattenTreeRows(this.materials).map((r) => [r.id, r]),
    );
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

    this.materials.forEach((m) => {
      materialRows(m).forEach((row) => {
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
      if (!m.itemName) {
        const named = materialRows(m).find((r) =>
          String(r.itemName ?? "").trim(),
        );
        if (named) {
          m.itemName = String(named.itemName).trim();
        }
      }
    });

    const sapCodes = new Set<string>();
    this.materials.forEach((m) => {
      materialRows(m).forEach((row) => {
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
        this.materials.forEach((m) => {
          materialRows(m).forEach((row) => {
            if (String(row.itemName ?? "").trim()) {
              return;
            }
            const name = apiNames.get((row.sapCode ?? "").trim());
            if (name) {
              row.itemName = name;
            }
          });
          if (!m.itemName) {
            const named = materialRows(m).find((r) =>
              String(r.itemName ?? "").trim(),
            );
            if (named) {
              m.itemName = String(named.itemName).trim();
            }
          }
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
    flattenTreeRows(this.materials).forEach((row) => {
      this.initialSnapshot.set(row.id, this.serializeRow(row));
    });
  }

  private serializeRow(row: AggregateEditRow): string {
    const { id: _id, ...rest } = row;
    return JSON.stringify(rest);
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
