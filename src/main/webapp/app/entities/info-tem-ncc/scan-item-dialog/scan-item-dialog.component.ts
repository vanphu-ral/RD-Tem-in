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
import { ScanListViewDialogComponent } from "./scan-list-view-dialog/scan-list-view-dialog.component";
import {
  CreateVendorTemDetailPayload,
  ManagerTemNccService,
} from "app/entities/list-material/services/info-tem-ncc.service";
import { AccountService } from "app/core/auth/account.service";
import { take } from "rxjs";
import { WarehouseCacheService } from "app/entities/list-material/services/warehouse-cache.service";
import { CachedWarehouse } from "app/entities/list-material/services/warehouse-db";

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
  }[];
  vendorCode?: string;
  existingReelIds?: string[];
  poCode?: string;
}

@Component({
  selector: "jhi-scan-dialog",
  templateUrl: "./scan-item-dialog.component.html",
  styleUrls: ["./scan-item-dialog.component.scss"],
})
export class ScanItemDialogComponent implements OnInit, AfterViewInit {
  @ViewChild("scanInput") scanInputRef!: ElementRef<HTMLInputElement>;

  scanInput = "";
  scannedList: ScannedItem[] = [];
  errorMessage = "";
  isInputFocused = false;
  lastScannedCode = "";
  duplicateHighlightId: string | null = null;

  warehouse = "";
  approver = "";
  isMobile = false;

  totalCount = 0;
  totalScannedQty = 0;
  uniquePartCount = 0;
  scanStatusSummary: { type: "ok" | "over" | "under" | null; message: string } =
    { type: null, message: "" };

  lotColumns = [
    { key: "lot", label: "Lot", minWidth: 130 },
    { key: "reelId", label: "ReelId", minWidth: 190 },
    { key: "partNumber", label: "Part Number", minWidth: 140 },
    { key: "vendor", label: "Vendor", minWidth: 110 },
    { key: "boxCount", label: "Số thùng", minWidth: 90 },
    { key: "totalQty", label: "Tổng SL", minWidth: 90 },
    { key: "initialQuantity", label: "Quantity", minWidth: 100 },
    { key: "userData1", label: "Userdata1", minWidth: 110 },
    { key: "userData2", label: "Userdata2", minWidth: 110 },
    { key: "userData3", label: "Userdata3", minWidth: 110 },
    { key: "userData4", label: "Userdata4", minWidth: 110 },
    { key: "userData5", label: "Userdata5", minWidth: 110 },
    { key: "msl", label: "MSL", minWidth: 80 },
    { key: "storageUnit", label: "StorageUnit", minWidth: 120 },
    { key: "manufacturingDate", label: "ManufacturingDate", minWidth: 150 },
    { key: "expirationDate", label: "ExpirationDate", minWidth: 150 },
  ];
  currentUser = "unknown";
  failedItems: ScannedItem[] = [];
  //danh sach kho
  warehouseOptions: CachedWarehouse[] = [];
  filteredWarehouseOptions: CachedWarehouse[] = [];
  isLoadingWarehouses = false;
  // existingReelIds?: string[];
  private rawQueue: string[] = [];
  private processingQueue = false;
  private existingReelIds = new Set<string>();
  private pendingSaves = new Map<string, CreateVendorTemDetailPayload>();

  // ── Debounce cache ──
  private cacheFlushTimer: ReturnType<typeof setTimeout> | null = null;
  constructor(
    private dialogRef: MatDialogRef<ScanItemDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ScanDialogData | null,
    private dialog: MatDialog,
    private managerTemNccService: ManagerTemNccService,
    private accountService: AccountService,
    private warehouseCacheService: WarehouseCacheService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.dialogRef.disableClose = true;
    this.isMobile = window.innerWidth <= 600;
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
    this.loadFromCache();
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
    return (this.data?.parentItems ?? []).reduce(
      (sum, p) => sum + (p.orderQty ?? 0),
      0,
    );
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
  onWarehouseSearch(keyword: string): void {
    if (!keyword?.trim()) {
      this.filteredWarehouseOptions = this.warehouseOptions.slice(0, 50);
      return;
    }
    this.warehouseCacheService.searchByName(keyword).then((result) => {
      this.filteredWarehouseOptions = result;
    });
  }

  clearAll(): void {
    this.scannedList = [];
    this.failedItems = [];
    this.lastScannedCode = "";
    this.errorMessage = "";
    if (this.cacheFlushTimer) {
      clearTimeout(this.cacheFlushTimer);
      this.cacheFlushTimer = null;
    }
    sessionStorage.removeItem(this.cacheKey);
    this.updateStats();
    this.cdr.markForCheck();
    this.focusInput();
  }

  onConfirm(): void {
    if (this.cacheFlushTimer) {
      clearTimeout(this.cacheFlushTimer);
      this.cacheFlushTimer = null;
    }
    sessionStorage.removeItem(this.cacheKey);
    this.dialogRef.close({
      items: this.scannedList,
      warehouse: this.warehouse,
      approver: this.approver,
    });
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
    this.warehouseCacheService.getAll().then((list) => {
      this.warehouseOptions = list;
      this.filteredWarehouseOptions = list.slice(0, 50);
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

    const dateSource = fieldMap["manufacturingDate"] || this.data?.arrivalDate;
    let cleanDate = "";
    if (dateSource && /^\d{8}$/.test(String(dateSource))) {
      cleanDate = String(dateSource);
    }

    const now = new Date().toISOString();
    const tempId = this.generateId();

    const payload: CreateVendorTemDetailPayload = {
      reelId: reelIdToCheck,
      partNumber: scannedPartNumber,
      vendor: fieldMap["vendor"] ?? this.data?.vendorCode ?? "",
      lot: `${scannedPartNumber}${cleanDate}`,
      userData1: fieldMap["userData1"] || "NO",
      userData2: fieldMap["userData2"] || "NO",
      userData3: fieldMap["userData3"] || "NO",
      userData4: fieldMap["userData4"] || (matchedRow?.sapCode ?? ""),
      userData5: fieldMap["userData5"] || (this.data?.poCode ?? ""),
      initialQuantity: parsedQty,
      msdLevel: fieldMap["msdLevel"] ?? "",
      msdInitialFloorTime: "",
      msdBagSealDate: "",
      marketUsage: "",
      quantityOverride: Number(fieldMap["quantityOverride"]) || 0,
      shelfTime: "",
      spMaterialName: "",
      warningLimit: "",
      maximumLimit: "",
      comments: "",
      warmupTime: "",
      storageUnit: fieldMap["storageUnit"] || this.warehouse || "",
      subStorageUnit: "",
      locationOverride: "",
      manufacturingDate: fieldMap["manufacturingDate"] ?? "",
      expirationDate: fieldMap["expirationDate"] ?? "",
      partClass: "",
      sapCode: matchedRow?.sapCode ?? "",
      vendorQrCode: rawCode,
      status: isOrphan ? "DRAFT" : "NEW", // phan biet scan tu do
      createdBy: this.currentUser,
      createdAt: now,
      updatedBy: this.currentUser,
      updatedAt: now,
      poDetailId: matchedRow?.id ?? (null as any), // null neu chua co PO
      importVendorTemTransactionsId: this.data.importVendorTemTransactionsId,
    };

    const uiItem: ScannedItem = {
      id: tempId,
      dbId: undefined,
      ...payload,
    } as any;
    this.existingReelIds.add(reelIdToCheck);
    // unshift thay vì spread — mutate trực tiếp, không copy array
    this.scannedList.unshift(uiItem);
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
          scannedList: this.scannedList,
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
          scannedList: this.scannedList,
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
      this.scannedList = cached.scannedList ?? [];
      this.failedItems = cached.failedItems ?? [];
      this.lastScannedCode = cached.lastScannedCode ?? "";
      // Sync lại existingReelIds từ cache
      this.scannedList.forEach((i) => {
        if (i.reelId) {
          this.existingReelIds.add(i.reelId);
        }
      });
    } catch {
      //code
    }
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

    if (this.scannedList.length === 0) {
      this.scanStatusSummary = { type: null, message: "" };
      return;
    }
    const totalOrder = this.totalOrderQty;
    const scanned = this.totalScannedQty;
    if (scanned === totalOrder) {
      this.scanStatusSummary = {
        type: "ok",
        message: `Đã scan đủ số lượng (${scanned}/${totalOrder})`,
      };
    } else if (scanned > totalOrder) {
      this.scanStatusSummary = {
        type: "over",
        message: `Vượt quá số lượng — đã scan ${scanned}, đơn hàng ${totalOrder}`,
      };
    } else {
      this.scanStatusSummary = {
        type: "under",
        message: `Chưa đủ — đã scan ${scanned}/${totalOrder}`,
      };
    }
  }
}
