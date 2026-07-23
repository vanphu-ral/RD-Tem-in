import {
  Component,
  OnInit,
  OnDestroy,
  ViewChild,
  ElementRef,
  AfterViewInit,
  Inject,
  ChangeDetectorRef,
} from "@angular/core";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from "@angular/material/dialog";
import { PageEvent } from "@angular/material/paginator";
import { ScanListViewDialogComponent } from "./scan-list-view-dialog/scan-list-view-dialog.component";
import {
  CreateVendorTemDetailPayload,
  ManagerTemNccService,
} from "app/entities/list-material/services/info-tem-ncc.service";
import { AccountService } from "app/core/auth/account.service";
import {
  Observable,
  catchError,
  forkJoin,
  map,
  of,
  switchMap,
  take,
} from "rxjs";
import { MatAutocompleteTrigger } from "@angular/material/autocomplete";
import { WarehouseCacheService } from "app/entities/list-material/services/warehouse-cache.service";
import { CachedWarehouse } from "app/entities/list-material/services/warehouse-db";
import {
  parseVendorFullQrExcelArrayBuffer,
  parseVendorReelLogContent,
  toVendorImportedReelEntry,
  VendorReelLogImportRow,
  VendorReelLogParseResult,
} from "app/entities/generate-tem-in/receiving-supplies/vendor-reel-log-import.util";
import {
  isVendorQrFieldMapped,
  parseVendorQrByMappingConfig,
} from "../shared/vendor-qr-mapping.util";
import { ReceivingSuppliesService } from "app/entities/generate-tem-in/service/receiving-supplies.service";
import { ReelImportPreviewRow } from "../add-info-tem-ncc/import-reel-preview-dialog/import-reel-preview-dialog.component";
import { ScanAggregateDialogComponent } from "./scan-aggregate-dialog/scan-aggregate-dialog.component";
import {
  SCAN_ITEM_LIST_COLUMNS,
  getScanItemFieldValue,
} from "../shared/scan-item-columns.util";

export type ScanImportTab = "scan" | "import";

export interface ScannedItem {
  id: string;
  dbId?: number;
  reelId: string;
  partNumber: string;
  vendor: string;
  lot: string;

  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;

  initialQuantity: number;
  msdLevel: string;
  msdInitialFloorTime: string;
  msdBagSealDate: string;
  marketUsage: string;
  quantityOverride: number;
  shelfTime: string;
  spMaterialName: string;
  warningLimit: string;
  maximumLimit: string;
  comments: string;
  warmupTime: string;

  storageUnit: string;
  subStorageUnit: string;
  locationOverride: string;

  expirationDate: string;
  manufacturingDate: string;

  partClass: string;
  sapCode: string;
  vendorQrCode: string;
  status: string;

  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;

  poDetailId: number;
  importVendorTemTransactionsId: number;
  /** Đã có sẵn trên đơn khi mở dialog — không trả lại khi Tổng hợp. */
  fromOrder?: boolean;
}
export interface ScanDialogData {
  mappingConfig: {
    separator: string;
    fieldMappings: {
      position: number;
      nccFieldDesc: string;
      dataField: string;
    }[];
  } | null;
  arrivalDate?: string;
  warehouse?: string;
  approver?: string;
  importVendorTemTransactionsId: number;
  parentItems: {
    id: number;
    partNumber: string;
    sapCode: string;
    orderQty: number;
    materialName?: string;
  }[];
  vendorCode?: string;
  existingReelIds?: string[];
  /** Vật tư đã lưu trên đơn — hiển thị sẵn trong danh sách dialog. */
  existingItems?: ScannedItem[];
  poCode?: string;
  /** Đồng bộ lot trên đơn sau khi cập nhật trong dialog tổng hợp. */
  onItemsUpdated?: (items: ScannedItem[]) => void;
}

@Component({
  selector: "jhi-scan-dialog",
  templateUrl: "./scan-item-dialog.component.html",
  styleUrls: ["./scan-item-dialog.component.scss"],
})
export class ScanItemDialogComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  @ViewChild("scanInputRef") scanInputRef!: ElementRef<HTMLInputElement>;
  @ViewChild("reelImportInput")
  reelImportInputRef!: ElementRef<HTMLInputElement>;

  activeTab: ScanImportTab = "scan";
  scanInput = "";
  scannedList: ScannedItem[] = [];
  errorMessage = "";
  importErrorMessage = "";
  isInputFocused = false;
  lastScannedCode = "";
  duplicateHighlightId: string | null = null;
  isImportingReel = false;
  isImportDragging = false;

  warehouse = "";
  approver = "";
  isMobile = false;

  totalCount = 0;
  totalScannedQty = 0;
  uniquePartCount = 0;
  scanStatusSummary: { type: "ok" | "over" | "under" | null; message: string } =
    { type: null, message: "" };

  pageIndex = 0;
  pageSize = 10;
  pageSizeOptions = [5, 10, 20, 50];

  /** Cột hiển thị danh sách scan/import. */
  displayColumns = SCAN_ITEM_LIST_COLUMNS;

  lotColumns = SCAN_ITEM_LIST_COLUMNS.map((col) => ({
    key: col.key,
    label: col.label,
    minWidth: 110,
  }));
  currentUser = "unknown";
  failedItems: ScannedItem[] = [];
  warehouseOptions: CachedWarehouse[] = [];
  filteredWarehouseOptions: CachedWarehouse[] = [];
  isLoadingWarehouses = false;
  private rawQueue: string[] = [];
  private processingQueue = false;
  private existingReelIds = new Set<string>();
  /** ReelID đã có trên đơn lúc mở dialog. */
  private preloadedReelIds = new Set<string>();
  private pendingSaves = new Map<string, CreateVendorTemDetailPayload>();
  private cacheFlushTimer: ReturnType<typeof setTimeout> | null = null;
  /** SL theo mã SAP từ sap-po-info (por1Quantity). Không có PO → map rỗng, không cảnh báo. */
  private poQtyBySapCode = new Map<string, number>();
  private hasPoQuantityCheck = false;

  constructor(
    private dialogRef: MatDialogRef<ScanItemDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ScanDialogData | null,
    private dialog: MatDialog,
    private managerTemNccService: ManagerTemNccService,
    private accountService: AccountService,
    private warehouseCacheService: WarehouseCacheService,
    private receivingSuppliesService: ReceivingSuppliesService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.dialogRef.disableClose = true;
    this.updateIsMobile();
    window.addEventListener("resize", this.onWindowResize);
    this.pageSize = this.isMobile ? 5 : 10;
    this.warehouse = this.data?.warehouse ?? "";
    (this.data?.existingReelIds ?? []).forEach((id) =>
      this.existingReelIds.add(id),
    );
    this.accountService
      .getAuthenticationState()
      .pipe(take(1))
      .subscribe((account) => {
        this.currentUser = account?.login ?? "unknown";
      });
    this.approver = this.data?.approver ?? "";
    this.loadWarehouseOptions();
    this.seedExistingOrderItems();
    this.loadFromCache();
    this.loadPoQuantities();
    this.updateStats();
  }

  ngOnDestroy(): void {
    window.removeEventListener("resize", this.onWindowResize);
    if (this.cacheFlushTimer) {
      clearTimeout(this.cacheFlushTimer);
      this.cacheFlushTimer = null;
    }
  }

  get pagedItems(): ScannedItem[] {
    const start = this.pageIndex * this.pageSize;
    return this.scannedList.slice(start, start + this.pageSize);
  }

  get newItemCount(): number {
    return this.scannedList.filter(
      (item) => !item.fromOrder && !this.preloadedReelIds.has(item.reelId),
    ).length;
  }

  get existingOrderCount(): number {
    return this.scannedList.filter(
      (item) =>
        (item.fromOrder ?? false) || this.preloadedReelIds.has(item.reelId),
    ).length;
  }

  isExistingOnOrder(item: ScannedItem): boolean {
    return (item.fromOrder ?? false) || this.preloadedReelIds.has(item.reelId);
  }

  setActiveTab(tab: ScanImportTab): void {
    this.activeTab = tab;
    this.errorMessage = "";
    this.importErrorMessage = "";
    if (tab === "scan") {
      setTimeout(() => this.focusInput(), 120);
    }
  }

  getDisplayValue(item: ScannedItem, key: string): string | number {
    return getScanItemFieldValue(item, key);
  }

  onPage(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
  }

  openListDialog(): void {
    this.dialog.open(ScanListViewDialogComponent, {
      width: "95vw",
      maxWidth: "1400px",
      maxHeight: "90vh",
      data: {
        scannedList: this.scannedList,
        lotColumns: this.lotColumns,
        failedItems: this.failedItems,
      },
    });
  }

  get errorCount(): number {
    return this.failedItems.length;
  }

  get totalOrderQty(): number {
    let total = 0;
    this.poQtyBySapCode.forEach((qty) => {
      total += qty;
    });
    return total;
  }

  // So sánh tổng scan vs tổng đơn (gộp cả existing + session hiện tại)
  get totalScannedIncludingExisting(): number {
    return this.existingReelIds.size + this.scannedList.length;
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.focusInput();
    }, 200);
  }

  focusInput(): void {
    this.scanInputRef?.nativeElement?.focus();
  }

  onInputFocus(): void {
    this.isInputFocused = true;
  }

  onInputBlur(): void {
    this.isInputFocused = false;
  }

  onScanInputChange(value: string): void {
    this.scanInput = value;
    this.errorMessage = "";
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key !== "Enter") {
      return;
    }
    event.preventDefault();

    const el = event.target as HTMLInputElement;
    const val = el.value.trim();

    // Reset DOM input NGAY LẬP TỨC
    el.value = "";
    this.scanInput = "";

    if (val) {
      this.rawQueue.push(val);
      this.drainQueue();
    }
  }
  onWarehouseSelected(warehouse: CachedWarehouse): void {
    this.warehouse = warehouse.locationFullName;
  }

  displayWarehouse(w: CachedWarehouse | string | null): string {
    if (!w) {
      return "";
    }
    if (typeof w === "string") {
      return w;
    }
    return w.locationFullName;
  }
  onWarehouseSearch(
    keyword: string,
    trigger?: MatAutocompleteTrigger | null,
  ): void {
    if (!keyword?.trim()) {
      this.filteredWarehouseOptions = [];
      return;
    }
    this.warehouseCacheService.searchByName(keyword).then((result) => {
      this.filteredWarehouseOptions = result;
      this.cdr.detectChanges();
      setTimeout(() => {
        if (trigger && !trigger.panelOpen) {
          trigger.openPanel();
        }
      });
    });
  }

  clearAll(): void {
    // Chỉ xóa mã mới trong phiên — giữ vật tư đã có trên đơn.
    this.scannedList = this.scannedList.filter(
      (item) =>
        (item.fromOrder ?? false) || this.preloadedReelIds.has(item.reelId),
    );
    this.failedItems = [];
    this.lastScannedCode = "";
    this.errorMessage = "";
    this.importErrorMessage = "";
    this.pageIndex = 0;
    if (this.cacheFlushTimer) {
      clearTimeout(this.cacheFlushTimer);
      this.cacheFlushTimer = null;
    }
    sessionStorage.removeItem(this.cacheKey);
    this.updateStats();
    this.cdr.markForCheck();
    if (this.activeTab === "scan") {
      this.focusInput();
    }
  }

  onConfirm(): void {
    if (!this.scannedList.length) {
      return;
    }
    const newItems = this.scannedList.filter(
      (item) => !item.fromOrder && !this.preloadedReelIds.has(item.reelId),
    );
    const productNameBySap: Record<string, string> = {};
    const productNameByPoDetailId: Record<number, string> = {};
    (this.data?.parentItems ?? []).forEach((p) => {
      const name = (p.materialName ?? "").trim();
      const sap = (p.sapCode ?? "").trim();
      if (sap && name) {
        productNameBySap[sap] = name;
      }
      if (p.id && name) {
        productNameByPoDetailId[p.id] = name;
      }
    });

    const aggregateRef = this.dialog.open(ScanAggregateDialogComponent, {
      width: "100vw",
      maxWidth: "100vw",
      height: "100vh",
      maxHeight: "100vh",
      panelClass: "scan-aggregate-panel",
      disableClose: true,
      autoFocus: false,
      data: {
        items: [...this.scannedList],
        poCode: this.data?.poCode,
        newItemCount: newItems.length,
        importVendorTemTransactionsId:
          this.data?.importVendorTemTransactionsId ?? 0,
        productNameBySap,
        productNameByPoDetailId,
        currentUser: this.currentUser,
      },
    });

    aggregateRef.afterClosed().subscribe(
      (
        result: {
          confirmed: boolean;
          items: ScannedItem[];
        } | null,
      ) => {
        if (!result?.confirmed) {
          return;
        }
        this.mergeScannedItems(result.items);
        this.data?.onItemsUpdated?.(result.items);
        this.cdr.markForCheck();
      },
    );
  }
  removeItem(id: string): void {
    this.scannedList = this.scannedList.filter((item) => item.id !== id);
    this.updateStats();
    this.saveToCache();
    this.cdr.markForCheck();
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  onFinish(): void {
    const itemsToApply = this.scannedList.filter(
      (item) => !item.fromOrder && !this.preloadedReelIds.has(item.reelId),
    );
    if (this.cacheFlushTimer) {
      clearTimeout(this.cacheFlushTimer);
      this.cacheFlushTimer = null;
    }
    sessionStorage.removeItem(this.cacheKey);
    this.dialogRef.close({
      items: itemsToApply,
      warehouse: this.warehouse,
      approver: this.approver,
    });
  }

  openImportFilePicker(): void {
    if (!this.data?.importVendorTemTransactionsId) {
      this.importErrorMessage =
        "Chưa có transaction — vui lòng Apply đơn trước.";
      return;
    }
    if (!this.data?.mappingConfig) {
      this.importErrorMessage =
        "Vui lòng chọn kịch bản nhập TEM trước khi import.";
      return;
    }
    const input = this.reelImportInputRef?.nativeElement;
    if (input) {
      input.value = "";
      input.click();
    }
  }

  onImportDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isImportDragging = true;
  }

  onImportDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isImportDragging = false;
  }

  onImportDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isImportDragging = false;
    const file = event.dataTransfer?.files?.[0];
    if (file) {
      this.handleImportFile(file);
    }
  }

  onImportFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = "";
    if (file) {
      this.handleImportFile(file);
    }
  }

  private updateIsMobile(): void {
    this.isMobile = window.innerWidth <= 600;
  }

  private readonly onWindowResize = (): void => {
    this.updateIsMobile();
  };

  private handleImportFile(file: File): void {
    if (!this.data?.mappingConfig) {
      this.importErrorMessage =
        "Vui lòng chọn kịch bản nhập TEM trước khi import.";
      return;
    }
    this.isImportingReel = true;
    this.importErrorMessage = "";
    const poSapCodes = (this.data?.parentItems ?? [])
      .map((row) => (row.sapCode ?? "").trim())
      .filter(Boolean);
    const isExcel = /\.(xlsx|xls)$/i.test(file.name);
    const reader = new FileReader();
    reader.onload = () => {
      try {
        let parsed: VendorReelLogParseResult;
        if (isExcel) {
          parsed = parseVendorFullQrExcelArrayBuffer(
            reader.result as ArrayBuffer,
            this.data?.poCode ?? "",
            poSapCodes,
            { requireSap: false },
          );
        } else {
          parsed = parseVendorReelLogContent(
            String(reader.result ?? ""),
            this.data?.poCode ?? "",
            poSapCodes,
            { requireSap: false },
          );
        }

        if (!parsed.rows.length) {
          this.isImportingReel = false;
          this.importErrorMessage =
            parsed.errors[0] ?? "File không có dòng ReelID hợp lệ.";
          this.cdr.markForCheck();
          return;
        }

        this.resolveSapCodeByPartFromPo(
          (this.data?.poCode ?? "").trim(),
        ).subscribe({
          next: (partToSap) => {
            const previewRows = this.buildReelImportPreviewRows(
              parsed.rows,
              partToSap,
            );
            if (!previewRows.length) {
              this.isImportingReel = false;
              this.importErrorMessage =
                "Không dựng được dữ liệu import từ file.";
              this.cdr.markForCheck();
              return;
            }

            if (parsed.errors.length) {
              this.importErrorMessage = parsed.errors.slice(0, 3).join(" · ");
            }

            this.applyImportPreviewRows(previewRows);
          },
          error: () => {
            this.isImportingReel = false;
            this.importErrorMessage =
              "Không lấy được thông tin SAP từ PO để import.";
            this.cdr.markForCheck();
          },
        });
      } catch {
        this.isImportingReel = false;
        this.importErrorMessage = "Không đọc được file import ReelID.";
        this.cdr.markForCheck();
      }
    };
    reader.onerror = () => {
      this.isImportingReel = false;
      this.importErrorMessage = "Đọc file thất bại.";
      this.cdr.markForCheck();
    };

    if (isExcel) {
      reader.readAsArrayBuffer(file);
    } else {
      reader.readAsText(file);
    }
  }

  private applyImportPreviewRows(rows: ReelImportPreviewRow[]): void {
    if (!this.data?.importVendorTemTransactionsId) {
      return;
    }
    this.isImportingReel = true;
    const parentItems = this.data.parentItems ?? [];
    const now = new Date().toISOString();
    const payloads: Array<{
      tempId: string;
      payload: CreateVendorTemDetailPayload;
    }> = [];

    for (const row of rows) {
      const reelId = (row.reelId ?? "").trim();
      const partNumber = (row.partNumber ?? "").trim();
      if (!reelId) {
        continue;
      }
      if (
        this.scannedList.some((i) => i.reelId === reelId) ||
        this.existingReelIds.has(reelId)
      ) {
        continue;
      }

      const rowSap = (row.sapCode ?? "").trim().toLowerCase();
      const matchedRow = parentItems.find((r) => {
        const parentSap = (r.sapCode ?? "").trim().toLowerCase();
        if (rowSap && parentSap && rowSap === parentSap) {
          return true;
        }
        return (
          !!partNumber &&
          (r.partNumber ?? "").trim().toLowerCase() === partNumber.toLowerCase()
        );
      });
      if (parentItems.length && (partNumber || rowSap) && !matchedRow) {
        continue;
      }

      const manufacturingDate = (row.manufacturingDate ?? "").trim();
      const lot =
        (row.lotNumber ?? "").trim() ||
        (partNumber && manufacturingDate
          ? `${partNumber}${manufacturingDate}`
          : "");
      const tempId = this.generateId();
      const payload: CreateVendorTemDetailPayload = {
        reelId,
        partNumber,
        vendor: (row.vendor ?? "").trim() || (this.data?.vendorCode ?? ""),
        lot,
        userData1: (row.userData1 ?? "").trim(),
        userData2: (row.userData2 ?? "").trim(),
        userData3: (row.userData3 ?? "").trim(),
        userData4: (row.userData4 ?? "").trim(),
        userData5: (row.userData5 ?? "").trim(),
        initialQuantity: Number(row.initialQuantity) || 0,
        msdLevel: (row.msl ?? "").trim(),
        msdInitialFloorTime: "",
        msdBagSealDate: "",
        marketUsage: "",
        quantityOverride: 0,
        shelfTime: "",
        spMaterialName: (row.itemName ?? "").trim(),
        warningLimit: "",
        maximumLimit: "",
        comments: "",
        warmupTime: "",
        storageUnit: (row.storageUnit ?? "").trim() || this.warehouse,
        subStorageUnit: "",
        locationOverride: (row.locationOverride ?? "").trim(),
        manufacturingDate,
        expirationDate: (row.expirationDate ?? "").trim(),
        partClass: "",
        sapCode: (row.sapCode ?? "").trim(),
        vendorQrCode: (row.vendorQrCode ?? "").trim(),
        status: matchedRow ? "NEW" : "DRAFT",
        createdBy: this.currentUser,
        createdAt: now,
        updatedBy: this.currentUser,
        updatedAt: now,
        poDetailId: matchedRow?.id ?? (null as any),
        importVendorTemTransactionsId: this.data.importVendorTemTransactionsId,
      };
      payloads.push({ tempId, payload });
      this.existingReelIds.add(reelId);
    }

    if (!payloads.length) {
      this.isImportingReel = false;
      this.importErrorMessage = "Không có dòng hợp lệ để import.";
      this.cdr.markForCheck();
      return;
    }

    forkJoin(
      payloads.map(({ tempId, payload }) =>
        this.managerTemNccService.createVendorTemDetails(payload).pipe(
          map((res) => ({ ok: true as const, tempId, payload, res })),
          catchError(() => {
            this.existingReelIds.delete(payload.reelId);
            return of({
              ok: false as const,
              tempId,
              payload,
              res: null,
            });
          }),
        ),
      ),
    ).subscribe({
      next: (results) => {
        const successItems = results.filter((r) => r.ok);
        // Import: giữ thứ tự file, block mới lên đầu danh sách.
        const importedItems: ScannedItem[] = successItems.map(
          ({ payload, res }) =>
            ({
              id: this.generateId(),
              dbId: res?.id,
              ...payload,
            }) as ScannedItem,
        );
        this.prependScannedItems(importedItems);
        this.isImportingReel = false;
        this.importErrorMessage = "";
        this.lastScannedCode = `Đã import ${successItems.length}/${payloads.length} ReelID`;
        this.pageIndex = 0;
        this.updateStats();
        this.saveToCache();
        this.cdr.markForCheck();
      },
      error: () => {
        this.isImportingReel = false;
        this.importErrorMessage = "Import ReelID thất bại.";
        this.cdr.markForCheck();
      },
    });
  }

  private buildReelImportPreviewRows(
    rows: VendorReelLogImportRow[],
    partToSap: Map<string, string>,
  ): ReelImportPreviewRow[] {
    if (!this.data?.mappingConfig) {
      return [];
    }
    const parentItems = this.data.parentItems ?? [];
    const previewRows: ReelImportPreviewRow[] = [];

    rows.forEach((row, index) => {
      const imported = toVendorImportedReelEntry(row);
      const qrCode = imported.qrCode;
      const fieldMap = parseVendorQrByMappingConfig(
        qrCode,
        this.data!.mappingConfig!,
      );
      const reelId = this.firstNonEmpty(fieldMap["reelId"], imported.reelId);
      const partNumber = this.firstNonEmpty(
        fieldMap["partNumber"],
        imported.partNumber,
      );
      const manufacturingDate = this.firstNonEmpty(
        fieldMap["manufacturingDate"],
        imported.mfgDate,
      );
      const qtyRaw = (fieldMap["initialQuantity"] ?? "").trim();
      const qtyFromMapping = Number(qtyRaw);
      const initialQuantity =
        qtyRaw !== "" && Number.isFinite(qtyFromMapping)
          ? qtyFromMapping
          : imported.quantity;
      const matchedRow = parentItems.find(
        (r) =>
          (r.partNumber ?? "").trim().toLowerCase() ===
          partNumber.toLowerCase(),
      );
      // Ưu tiên mã SAP có sẵn trong file/QR; chỉ fallback map Part→SAP khi file thiếu SAP.
      const resolvedSap = this.firstNonEmpty(
        fieldMap["sapCode"],
        row.sapCode,
        partToSap.get(partNumber.toLowerCase()),
        matchedRow?.sapCode,
      );
      const lotNumber = this.firstNonEmpty(
        fieldMap["lotNumber"],
        imported.lotNumber,
        partNumber && manufacturingDate
          ? `${partNumber}${manufacturingDate}`
          : "",
      );
      const poNumber = this.firstNonEmpty(
        fieldMap["poNumber"],
        imported.poNumber,
        this.data?.poCode,
      );

      previewRows.push({
        uid: `import-${row.lineNo}-${index}-${reelId || "empty"}`,
        lineNo: row.lineNo,
        reelId,
        partNumber,
        initialQuantity: Number.isFinite(initialQuantity) ? initialQuantity : 0,
        poNumber,
        manufacturingDate,
        lotNumber,
        itemName: (row.itemName ?? "").trim(),
        expirationDate: isVendorQrFieldMapped(
          this.data!.mappingConfig,
          "expirationDate",
        )
          ? (fieldMap["expirationDate"] ?? "").trim()
          : "",
        locationOverride: "",
        storageUnit: this.firstNonEmpty(fieldMap["storageUnit"]),
        sapCode: resolvedSap,
        vendor: this.firstNonEmpty(fieldMap["vendor"], this.data?.vendorCode),
        userData1: isVendorQrFieldMapped(this.data!.mappingConfig, "userData1")
          ? (fieldMap["userData1"] ?? "").trim()
          : "",
        userData2: isVendorQrFieldMapped(this.data!.mappingConfig, "userData2")
          ? (fieldMap["userData2"] ?? "").trim()
          : "",
        userData3: isVendorQrFieldMapped(this.data!.mappingConfig, "userData3")
          ? (fieldMap["userData3"] ?? "").trim()
          : "",
        userData4: isVendorQrFieldMapped(this.data!.mappingConfig, "userData4")
          ? (fieldMap["userData4"] ?? "").trim()
          : "",
        userData5: isVendorQrFieldMapped(this.data!.mappingConfig, "userData5")
          ? (fieldMap["userData5"] ?? "").trim()
          : isVendorQrFieldMapped(this.data!.mappingConfig, "poNumber")
            ? this.firstNonEmpty(fieldMap["poNumber"], imported.poNumber)
            : "",
        msl: isVendorQrFieldMapped(this.data!.mappingConfig, "msl")
          ? (fieldMap["msl"] ?? "").trim()
          : "",
        vendorQrCode: qrCode,
      });
    });

    return previewRows;
  }

  private resolveSapCodeByPartFromPo(
    poCode: string,
  ): Observable<Map<string, string>> {
    if (!poCode) {
      return of(new Map<string, string>());
    }
    return this.receivingSuppliesService.getSapPoInfo(poCode).pipe(
      switchMap((res) => {
        const sapCodes = Array.from(
          new Set(
            (res?.poDetails ?? [])
              .map((d) => (d.por1ItemCode ?? "").trim())
              .filter(Boolean),
          ),
        );
        if (!sapCodes.length) {
          return of(new Map<string, string>());
        }
        return forkJoin(
          sapCodes.map((sapCode) =>
            this.receivingSuppliesService
              .getOitmPartNumbersBySapCode(sapCode)
              .pipe(
                map((parts) => ({ sapCode, parts })),
                catchError(() => of({ sapCode, parts: [] as string[] })),
              ),
          ),
        ).pipe(
          map((rows) => {
            const partToSap = new Map<string, string>();
            rows.forEach(({ sapCode, parts }) => {
              parts.forEach((part) => {
                const key = part.trim().toLowerCase();
                if (key && !partToSap.has(key)) {
                  partToSap.set(key, sapCode);
                }
              });
            });
            return partToSap;
          }),
        );
      }),
      catchError(() => of(new Map<string, string>())),
    );
  }

  private firstNonEmpty(...values: Array<string | null | undefined>): string {
    for (const value of values) {
      const trimmed = (value ?? "").trim();
      if (trimmed) {
        return trimmed;
      }
    }
    return "";
  }

  private highlightDuplicate(id: string): void {
    this.duplicateHighlightId = id;
    setTimeout(() => {
      this.duplicateHighlightId = null;
    }, 2000);
  }

  private generateId(): string {
    return (
      Math.random().toString(36).substring(2, 10) + Date.now().toString(36)
    );
  }

  // map dataField từ API về camelCase key của ScannedItem
  private toCamelKey(dataField: string): string {
    const mapping: Record<string, string> = {
      ReelID: "reelId",
      PartNumber: "partNumber",
      Lot: "lotNumber",
      Vendor: "vendor",
      InitialQuantity: "initialQuantity",
      UserData1: "userData1",
      UserData2: "userData2",
      UserData3: "userData3",
      UserData4: "userData4",
      UserData5: "userData5",
      MSDLevel: "msl",
      StorageUnit: "storageUnit",
      ManufacturingDate: "manufacturingDate",
      ExpirationDate: "expirationDate",
      QuantityOverride: "totalQty",

      "Mã ReelID": "reelId",
      "Mã Part number": "partNumber",
      "SAP Code": "storageUnit",
      "Initial quantity": "initialQuantity",
      "Quantity Override": "totalQty",
      "Storage Unit": "storageUnit",
      "Lot Number": "lotNumber",
    };
    return mapping[dataField] ?? dataField;
  }

  private loadWarehouseOptions(): void {
    this.isLoadingWarehouses = true;
    this.warehouseCacheService
      .ensureSynced()
      .then(() => {
        this.warehouseOptions = [];
        this.filteredWarehouseOptions = [];
        this.isLoadingWarehouses = false;
      })
      .catch(() => {
        this.isLoadingWarehouses = false;
      });
  }
  private drainQueue(): void {
    if (this.processingQueue) {
      return;
    }
    this.processingQueue = true;
    this.processNext();
  }
  private processNext(): void {
    if (this.rawQueue.length === 0) {
      this.processingQueue = false;
      // Render + tính stats 1 lần duy nhất sau khi hết queue
      this.updateStats();
      this.cdr.markForCheck();
      return;
    }
    const rawCode = this.rawQueue.shift()!;
    this.processRawCodeSilent(rawCode);
    Promise.resolve().then(() => this.processNext());
  }
  private processRawCodeSilent(rawCode: string): void {
    if (!this.data) {
      return;
    }

    const mappingConfig = this.data.mappingConfig;
    const fieldMap: Record<string, string> = {};

    if (mappingConfig) {
      const separator = mappingConfig.separator ?? "|";
      const parts = rawCode.split(separator);
      mappingConfig.fieldMappings
        .filter((fm) => fm.dataField && fm.dataField !== "Không lấy")
        .forEach((fm) => {
          fieldMap[this.toCamelKey(fm.dataField)] = parts[fm.position] ?? "";
        });
    }

    const scannedPartNumber = (fieldMap["partNumber"] ?? "").trim();
    const matchedRow = this.data.parentItems.find(
      (r) =>
        (r.partNumber ?? "").trim().toLowerCase() ===
        scannedPartNumber.toLowerCase(),
    );

    const isOrphan = !matchedRow;

    // Nếu có parentItems (có PO) mà không tìm thấy part → báo lỗi, không lưu
    if (
      !matchedRow &&
      scannedPartNumber !== "" &&
      (this.data.parentItems ?? []).length > 0
    ) {
      this.errorMessage = `Không tìm thấy Part Number "${scannedPartNumber}" trong đơn hàng.`;
      this.cdr.markForCheck();
      return;
    }

    const reelIdToCheck = (fieldMap["reelId"] ?? "").trim();

    if (
      this.scannedList.some((i) => i.reelId === reelIdToCheck) ||
      this.existingReelIds.has(reelIdToCheck)
    ) {
      this.errorMessage = `ReelID "${reelIdToCheck}" đã được scan trước đó.`;
      this.cdr.markForCheck();
      return;
    }
    const parsedQty = Number(fieldMap["initialQuantity"]) || 0;
    if (parsedQty <= 0) {
      this.errorMessage = `Số lượng không hợp lệ (${parsedQty}) — mã "${reelIdToCheck}" không được lưu.`;
      this.cdr.markForCheck();
      return;
    }

    const manufacturingDate = isVendorQrFieldMapped(
      mappingConfig,
      "manufacturingDate",
    )
      ? (fieldMap["manufacturingDate"] ?? "").trim()
      : "";
    let cleanDate = "";
    if (manufacturingDate && /^\d{8}$/.test(manufacturingDate)) {
      cleanDate = manufacturingDate;
    } else if (
      isVendorQrFieldMapped(mappingConfig, "manufacturingDate") &&
      this.data?.arrivalDate &&
      /^\d{8}$/.test(String(this.data.arrivalDate))
    ) {
      cleanDate = String(this.data.arrivalDate);
    }
    const lotFromMapping = isVendorQrFieldMapped(mappingConfig, "lotNumber")
      ? (fieldMap["lotNumber"] ?? fieldMap["lot"] ?? "").trim()
      : "";
    const lotNumber =
      lotFromMapping ||
      (scannedPartNumber && cleanDate
        ? `${scannedPartNumber}${cleanDate}`
        : "");

    const now = new Date().toISOString();
    const tempId = this.generateId();

    const payload: CreateVendorTemDetailPayload = {
      reelId: reelIdToCheck,
      partNumber: scannedPartNumber,
      vendor: isVendorQrFieldMapped(mappingConfig, "vendor")
        ? (fieldMap["vendor"] ?? "").trim()
        : (this.data?.vendorCode ?? ""),
      lot: lotNumber,
      userData1: isVendorQrFieldMapped(mappingConfig, "userData1")
        ? (fieldMap["userData1"] ?? "").trim()
        : "",
      userData2: isVendorQrFieldMapped(mappingConfig, "userData2")
        ? (fieldMap["userData2"] ?? "").trim()
        : "",
      userData3: isVendorQrFieldMapped(mappingConfig, "userData3")
        ? (fieldMap["userData3"] ?? "").trim()
        : "",
      userData4: isVendorQrFieldMapped(mappingConfig, "userData4")
        ? (fieldMap["userData4"] ?? "").trim()
        : "",
      userData5: isVendorQrFieldMapped(mappingConfig, "userData5")
        ? this.firstNonEmpty(fieldMap["userData5"], fieldMap["poNumber"])
        : "",
      initialQuantity: parsedQty,
      msdLevel: isVendorQrFieldMapped(mappingConfig, "msl")
        ? (fieldMap["msl"] ?? fieldMap["msdLevel"] ?? "").trim()
        : "",
      msdInitialFloorTime: "",
      msdBagSealDate: "",
      marketUsage: "",
      quantityOverride: Number(fieldMap["quantityOverride"]) || 0,
      shelfTime: "",
      spMaterialName: isVendorQrFieldMapped(mappingConfig, "spMaterialName")
        ? (fieldMap["spMaterialName"] ?? "").trim()
        : (matchedRow?.materialName ?? "").trim(),
      warningLimit: "",
      maximumLimit: "",
      comments: "",
      warmupTime: "",
      storageUnit: isVendorQrFieldMapped(mappingConfig, "storageUnit")
        ? (fieldMap["storageUnit"] ?? "").trim()
        : "",
      subStorageUnit: "",
      locationOverride: isVendorQrFieldMapped(mappingConfig, "locationOverride")
        ? (fieldMap["locationOverride"] ?? "").trim()
        : "",
      manufacturingDate,
      expirationDate: isVendorQrFieldMapped(mappingConfig, "expirationDate")
        ? (fieldMap["expirationDate"] ?? "").trim()
        : "",
      partClass: "",
      sapCode: matchedRow?.sapCode ?? "",
      vendorQrCode: rawCode,
      status: isOrphan ? "DRAFT" : "NEW",
      createdBy: this.currentUser,
      createdAt: now,
      updatedBy: this.currentUser,
      updatedAt: now,
      poDetailId: matchedRow?.id ?? (null as any),
      importVendorTemTransactionsId: this.data.importVendorTemTransactionsId,
    };

    const uiItem: ScannedItem = {
      id: tempId,
      dbId: undefined,
      ...payload,
    } as any;
    this.existingReelIds.add(reelIdToCheck);
    this.prependScannedItem(uiItem);
    this.errorMessage = "";
    this.lastScannedCode = rawCode;

    // Debounce cache — không ghi ngay
    this.scheduleCacheFlush();

    this.saveToApi(tempId, payload);
  }
  private scheduleCacheFlush(): void {
    if (this.cacheFlushTimer) {
      return;
    } // timer đang chạy, bỏ qua
    this.cacheFlushTimer = setTimeout(() => {
      this.flushCache();
      this.cacheFlushTimer = null;
    }, 300);
  }
  private flushCache(): void {
    try {
      sessionStorage.setItem(
        this.cacheKey,
        JSON.stringify({
          scannedList: this.scannedList.filter((i) => !i.fromOrder),
          failedItems: this.failedItems,
          lastScannedCode: this.lastScannedCode,
        }),
      );
    } catch {
      //catch
    }
  }
  private saveToApi(
    tempId: string,
    payload: CreateVendorTemDetailPayload,
  ): void {
    this.managerTemNccService.createVendorTemDetails(payload).subscribe({
      next: (res) => {
        const idx = this.scannedList.findIndex((i) => i.id === tempId);
        if (idx !== -1) {
          // Mutate trực tiếp thay vì map() tạo array mới
          this.scannedList[idx] = { ...this.scannedList[idx], dbId: res?.id };
        }
        this.saveToCache();
        // Không cần markForCheck vì dbId không hiển thị trực tiếp trên UI
      },
      error: () => {
        const failed = this.scannedList.find((i) => i.id === tempId);
        if (failed) {
          if (failed.reelId) {
            this.existingReelIds.delete(failed.reelId);
          }
          this.failedItems.unshift(failed);
        }
        this.scannedList = this.scannedList.filter((i) => i.id !== tempId);
        this.saveToCache();
        // Lỗi API cần cập nhật UI
        this.updateStats();
        this.cdr.markForCheck();
      },
    });
  }
  private get cacheKey(): string {
    return `scan_cache_${this.data?.importVendorTemTransactionsId ?? "unknown"}`;
  }
  private saveToCache(): void {
    try {
      sessionStorage.setItem(
        this.cacheKey,
        JSON.stringify({
          scannedList: this.scannedList.filter((i) => !i.fromOrder),
          failedItems: this.failedItems,
          lastScannedCode: this.lastScannedCode,
        }),
      );
    } catch {
      //code
    }
  }

  private loadFromCache(): void {
    try {
      const raw = sessionStorage.getItem(this.cacheKey);
      if (!raw) {
        return;
      }
      const cached = JSON.parse(raw);
      const cachedList: ScannedItem[] = cached.scannedList ?? [];
      const knownReelIds = new Set(
        this.scannedList.map((i) => (i.reelId ?? "").trim()).filter(Boolean),
      );
      const toAdd = cachedList.filter((item) => {
        const reelId = (item.reelId ?? "").trim();
        if (!reelId || knownReelIds.has(reelId)) {
          return false;
        }
        knownReelIds.add(reelId);
        this.existingReelIds.add(reelId);
        return true;
      });
      // Cache (mã chưa tổng hợp) đưa lên đầu, giữ vật tư đã có trên đơn phía sau.
      this.scannedList = [...toAdd, ...this.scannedList];
      this.failedItems = cached.failedItems ?? [];
      this.lastScannedCode = cached.lastScannedCode ?? "";
    } catch {
      //code
    }
  }

  private seedExistingOrderItems(): void {
    const existing = (this.data?.existingItems ?? [])
      .filter((item) => !!(item.reelId ?? "").trim())
      .map((item) => ({
        ...item,
        fromOrder: true,
        id: item.id ?? `order-${item.dbId ?? item.reelId}`,
      }));
    if (!existing.length) {
      return;
    }
    this.scannedList = existing;
    existing.forEach((item) => {
      if (item.reelId) {
        this.existingReelIds.add(item.reelId);
        this.preloadedReelIds.add(item.reelId);
      }
    });
  }

  /** Lấy por1Quantity từ sap-po-info theo mã PO; không có PO → bỏ qua cảnh báo. */
  private loadPoQuantities(): void {
    const poCode = (this.data?.poCode ?? "").trim();
    if (!poCode) {
      this.hasPoQuantityCheck = false;
      this.poQtyBySapCode.clear();
      this.updateStats();
      return;
    }
    this.receivingSuppliesService.getSapPoInfo(poCode).subscribe({
      next: (res) => {
        const qtyMap = new Map<string, number>();
        (res?.poDetails ?? []).forEach((d) => {
          const sap = (d.por1ItemCode ?? "").trim();
          if (!sap) {
            return;
          }
          const qty = Number.parseFloat(d.por1Quantity ?? "0") || 0;
          qtyMap.set(sap, (qtyMap.get(sap) ?? 0) + qty);
        });
        this.poQtyBySapCode = qtyMap;
        this.hasPoQuantityCheck = qtyMap.size > 0;
        this.updateStats();
        this.cdr.markForCheck();
      },
      error: () => {
        this.hasPoQuantityCheck = false;
        this.poQtyBySapCode.clear();
        this.updateStats();
        this.cdr.markForCheck();
      },
    });
  }

  /** Đưa mã mới lên đầu; giữ vật tư đã có trên đơn ở cuối. */
  private prependScannedItem(item: ScannedItem): void {
    this.prependScannedItems([item]);
  }

  private prependScannedItems(items: ScannedItem[]): void {
    if (!items.length) {
      return;
    }
    const sessionItems = this.scannedList.filter(
      (i) => !this.isExistingOnOrder(i),
    );
    const orderItems = this.scannedList.filter((i) =>
      this.isExistingOnOrder(i),
    );
    // items[0] = mới nhất trong batch → lên đầu danh sách
    this.scannedList = [...items, ...sessionItems, ...orderItems];
    this.pageIndex = 0;
  }

  private updateStats(): void {
    this.totalCount = this.scannedList.length;
    this.totalScannedQty = this.scannedList.reduce(
      (sum, item) => sum + (Number(item.initialQuantity) || 0),
      0,
    );
    this.uniquePartCount = new Set(
      this.scannedList.map((i) => i.partNumber),
    ).size;

    const maxPage = Math.max(
      0,
      Math.ceil(this.scannedList.length / this.pageSize) - 1,
    );
    if (this.pageIndex > maxPage) {
      this.pageIndex = maxPage;
    }

    // Không có PO / không lấy được por1Quantity → không cảnh báo, cho scan tự do.
    if (!this.hasPoQuantityCheck || this.scannedList.length === 0) {
      this.scanStatusSummary = { type: null, message: "" };
      return;
    }

    const scannedBySap = new Map<string, number>();
    this.scannedList.forEach((item) => {
      const sap = (item.sapCode ?? "").trim();
      if (!sap) {
        return;
      }
      scannedBySap.set(
        sap,
        (scannedBySap.get(sap) ?? 0) + (Number(item.initialQuantity) || 0),
      );
    });

    const overLines: string[] = [];
    scannedBySap.forEach((scannedQty, sap) => {
      const poQty = this.poQtyBySapCode.get(sap);
      if (poQty == null) {
        return;
      }
      if (scannedQty > poQty) {
        overLines.push(`${sap}: ${scannedQty}/${poQty}`);
      }
    });

    if (overLines.length) {
      this.scanStatusSummary = {
        type: "over",
        message: `Vượt quá số lượng PO — ${overLines.join("; ")}`,
      };
      return;
    }

    // Có PO nhưng chưa vượt từng mã SAP; nếu tổng scan vẫn vượt tổng PO (thiếu sapCode) thì báo tổng.
    const totalOrder = this.totalOrderQty;
    const scanned = this.totalScannedQty;
    if (scanned > totalOrder && totalOrder > 0) {
      this.scanStatusSummary = {
        type: "over",
        message: `Vượt quá số lượng PO — đã scan ${scanned}, PO ${totalOrder}`,
      };
      return;
    }

    this.scanStatusSummary = { type: null, message: "" };
  }

  private mergeScannedItems(items: ScannedItem[]): void {
    const byId = new Map(items.map((item) => [item.id, item]));
    this.scannedList = this.scannedList.map(
      (item) => byId.get(item.id) ?? item,
    );
    this.updateStats();
    this.saveToCache();
    this.cdr.markForCheck();
  }
}
