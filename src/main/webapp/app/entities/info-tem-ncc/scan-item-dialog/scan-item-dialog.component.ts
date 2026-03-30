import {
  Component,
  OnInit,
  OnDestroy,
  ViewChild,
  ElementRef,
  AfterViewInit,
  Inject,
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
  parentItems: { id: number; partNumber: string; sapCode: string }[];
  vendorCode?: string;
  existingReelIds?: string[];
  poCode?: string;
}

@Component({
  selector: "jhi-scan-dialog",
  templateUrl: "./scan-item-dialog.component.html",
  styleUrls: ["./scan-item-dialog.component.scss"],
})
export class ScanItemDialogComponent
  implements OnInit, OnDestroy, AfterViewInit
{
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

  //danh sach kho
  warehouseOptions: CachedWarehouse[] = [];
  filteredWarehouseOptions: CachedWarehouse[] = [];
  isLoadingWarehouses = false;
  // existingReelIds?: string[];
  private inputBuffer = "";
  private bufferTimer: any = null;
  private readonly BUFFER_DELAY_MS = 100;
  private existingReelIds = new Set<string>();
  constructor(
    private dialogRef: MatDialogRef<ScanItemDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ScanDialogData | null,
    private dialog: MatDialog,
    private managerTemNccService: ManagerTemNccService,
    private accountService: AccountService,
    private warehouseCacheService: WarehouseCacheService,
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
  }
  openListDialog(): void {
    this.dialog.open(ScanListViewDialogComponent, {
      width: "95vw",
      maxWidth: "1400px",
      maxHeight: "90vh",
      data: { scannedList: this.scannedList, lotColumns: this.lotColumns },
    });
  }

  get totalScannedQty(): number {
    return this.scannedList.reduce(
      (sum, item) => sum + (Number(item.initialQuantity) || 0),
      0,
    );
  }
  ngAfterViewInit(): void {
    setTimeout(() => {
      this.focusInput();
    }, 200);
  }

  ngOnDestroy(): void {
    if (this.bufferTimer) {
      clearTimeout(this.bufferTimer);
    }
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
    if (event.key === "Enter") {
      event.preventDefault();
      this.submitScan();
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
  // Trong submitScan(), sau khi parse fieldMap, tìm row theo partNumber
  submitScan(): void {
    const rawCode = this.scanInput.trim();
    if (!rawCode) {
      this.errorMessage = "Vui lòng nhập mã để scan.";
      return;
    }

    if (!this.data) {
      this.errorMessage = "Thiếu thông tin dialog.";
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
          const camelKey = this.toCamelKey(fm.dataField);
          fieldMap[camelKey] = parts[fm.position] ?? "";
        });
    }

    // ← Tìm row khớp partNumber
    const scannedPartNumber = (fieldMap["partNumber"] ?? "").trim();
    const matchedRow = this.data.parentItems.find(
      (r) =>
        (r.partNumber ?? "").trim().toLowerCase() ===
        scannedPartNumber.toLowerCase(),
    );

    if (!matchedRow) {
      this.errorMessage = `Không tìm thấy vật tư với Part Number "${scannedPartNumber}" trong đơn hàng.`;
      this.scanInput = "";
      return;
    }

    //check dupplicate
    const duplicateInSession = this.scannedList.find(
      (item) => item.reelId === (fieldMap["reelId"] ?? "").trim(),
    );

    // check trung voi cac lan scan truoc (da luu vao db)
    const reelIdToCheck = (fieldMap["reelId"] ?? "").trim();
    const duplicateFromPrev =
      reelIdToCheck && this.existingReelIds.has(reelIdToCheck);

    if (duplicateInSession ?? duplicateFromPrev) {
      this.errorMessage = `ReelID "${reelIdToCheck}" đã được scan trước đó.`;
      if (duplicateInSession) {
        this.highlightDuplicate(duplicateInSession.id);
      }
      this.scanInput = "";
      return;
    }
    //end check dupplicate

    const cleanPartNumber = scannedPartNumber.replace(/[^a-zA-Z0-9]/g, "");
    const dateSource = fieldMap["manufacturingDate"] || this.data?.arrivalDate;
    let cleanDate = "";
    if (dateSource && /^\d{8}$/.test(String(dateSource))) {
      cleanDate = String(dateSource);
    }
    const lot = `${cleanPartNumber}${cleanDate}`;
    const now = new Date().toISOString();

    const payload: CreateVendorTemDetailPayload = {
      reelId: fieldMap["reelId"] ?? "",
      partNumber: scannedPartNumber,
      vendor: fieldMap["vendor"] ?? this.data?.vendorCode ?? "",
      lot,
      userData1: fieldMap["userData1"] || "NO",
      userData2: fieldMap["userData2"] || "NO",
      userData3: fieldMap["userData3"] || "NO",
      userData4: fieldMap["userData4"] || (matchedRow.sapCode ?? ""),
      userData5: fieldMap["userData5"] || (this.data?.poCode ?? ""),
      initialQuantity: Number(fieldMap["initialQuantity"]) || 0,
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
      sapCode: matchedRow.sapCode ?? "",
      vendorQrCode: rawCode,
      status: "NEW",
      createdBy: this.currentUser,
      createdAt: now,
      updatedBy: this.currentUser,
      updatedAt: now,
      poDetailId: matchedRow.id,
      importVendorTemTransactionsId: this.data.importVendorTemTransactionsId,
    };

    this.managerTemNccService.createVendorTemDetails(payload).subscribe({
      next: (res) => {
        const { id: _ignored, ...payloadWithoutId } = payload as any;
        const uiItem: ScannedItem = {
          id: this.generateId(),
          dbId: res?.id,
          ...payloadWithoutId,
        };
        this.existingReelIds.add(reelIdToCheck);
        this.scannedList = [uiItem, ...this.scannedList];
        this.scanInput = "";
        this.errorMessage = "";
        this.lastScannedCode = rawCode;
        setTimeout(() => this.focusInput(), 50);
      },
      error: () => {
        this.errorMessage = "Lưu dữ liệu scan thất bại.";
      },
    });
  }
  get uniquePartCount(): number {
    return new Set(this.scannedList.map((i) => i.partNumber)).size;
  }
  removeItem(id: string): void {
    this.scannedList = this.scannedList
      .filter((item) => item.id !== id)
      .map((item, idx) => ({
        ...item,
        index: this.scannedList.length - 1 - idx,
      }));
  }

  clearAll(): void {
    this.scannedList = [];
    this.lastScannedCode = "";
    this.errorMessage = "";
    this.focusInput();
  }

  onConfirm(): void {
    this.dialogRef.close({
      items: this.scannedList,
      warehouse: this.warehouse,
      approver: this.approver,
    });
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  get totalCount(): number {
    return this.scannedList.length;
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
}
