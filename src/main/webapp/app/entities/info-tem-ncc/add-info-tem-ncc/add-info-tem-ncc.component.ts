import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ChangeDetectorRef,
} from "@angular/core";
import { CommonModule, DatePipe } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from "@angular/animations";

import { MatTableModule, MatTableDataSource } from "@angular/material/table";
import { MatPaginator, MatPaginatorModule } from "@angular/material/paginator";
import { MatSort, MatSortModule } from "@angular/material/sort";
import { MatDialog } from "@angular/material/dialog";
import { HttpClient } from "@angular/common/http";

import { AlertService } from "app/core/util/alert.service";
import {
  LotDetailDialogComponent,
  LotDetailDialogData,
} from "../lot-detail-dialog/lot-detail-dialog.component";
import {
  ScanItemDialogComponent,
  ScannedItem,
} from "../scan-item-dialog/scan-item-dialog.component";
import {
  ApproveVendorTemPayload,
  CreateVendorTemDetailPayload,
  ManagerTemNccService,
  PoImportTem,
  PoImportTemPayload,
  TemScenarioResponse,
} from "app/entities/list-material/services/info-tem-ncc.service";
import {
  Observable,
  catchError,
  forkJoin,
  map,
  of,
  switchMap,
  take,
} from "rxjs";
import { AccountService } from "app/core/auth/account.service";
import { ActivatedRoute, Router } from "@angular/router";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { CachedWarehouse } from "app/entities/list-material/services/warehouse-db";
import { WarehouseCacheService } from "app/entities/list-material/services/warehouse-cache.service";
import { DialogContentExampleDialogComponent } from "../list/confirm-dialog/confirm-dialog.component";
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
import {
  GoodsReceiptPoLine,
  ReceivingSuppliesService,
  resolveHttpErrorMessage,
  validateStorageUnitForSap,
} from "app/entities/generate-tem-in/service/receiving-supplies.service";
import {
  generateTemPanacimCsvBlob,
  TemPanacimExportInput,
} from "app/entities/generate-tem-in/receiving-supplies/tem-panacim-export.util";
import { SEND_REQUIRED_FIELDS } from "../shared/scan-item-columns.util";
import {
  ImportReelPreviewDialogComponent,
  ReelImportPreviewDialogData,
  ReelImportPreviewRow,
} from "./import-reel-preview-dialog/import-reel-preview-dialog.component";

// ==================== INTERFACES ====================

export interface LotItem {
  vendorTemDetailId: number;
  lotNumber: string;
  boxCount: number;
  totalQty: number;
  details: [];
  [key: string]: any;
}

export interface ChildItem {
  sapCode: string;
  productName: string;
  partNumber: string;
  boxCount: number;
  totalQty: number;
  orderBoxCount: number;
  orderQty: number;
  lots: LotItem[];
}

export interface ParentItem {
  id: number;
  sapCode: string;
  materialName: string;
  partNumber: string;
  boxScan: number;
  totalQuantity: number;
  // quantityBoxOrder: number;
  totalQuantityOrder: number;
  lots: LotItem[];
}

export interface FilterValues {
  sapCode: string;
  materialName: string;
  partNumber: string;
}

export interface CreatePoImportTemResponse {
  poImportTem: any;
  vendorTransaction: {
    transaction: any;
    poDetails: {
      id: number | null;
      sapCode: string;
      sapName: string;
      quantityContainer: number | null;
      totalQuantity: number | null;
      partNumber: string | null;
    }[];
  };
  caseType: string;
}

// ==================== COMPONENT ====================

@Component({
  selector: "jhi-info-tem-ncc",
  standalone: false,
  templateUrl: "./add-info-tem-ncc.component.html",
  styleUrls: ["./add-info-tem-ncc.component.scss"],
  animations: [
    trigger("detailExpand", [
      state(
        "collapsed",
        style({ height: "0px", minHeight: "0", overflow: "hidden" }),
      ),
      state("expanded", style({ height: "*", overflow: "hidden" })),
      transition(
        "expanded <=> collapsed",
        animate("220ms cubic-bezier(0.4, 0.0, 0.2, 1)"),
      ),
    ]),
  ],
})
export class AddInfoTemNccComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = [
    "expand",
    "sapCode",
    "materialName",
    "partNumber",
    "boxScan",
    "totalQuantity",
    // "quantityBoxOrder",
    "totalQuantityOrder",
    "actions",
  ];
  isScanning = false;
  isSendingSap = false;
  isSendingPanacim = false;
  scanMessage = "";
  lotColumns = [
    { key: "lotNumber", label: "Lot", minWidth: 130 },
    { key: "reelId", label: "ReelId", minWidth: 190 },
    { key: "partNumber", label: "Part Number", minWidth: 140 },
    { key: "vendor", label: "Vendor", minWidth: 110 },
    // { key: "boxCount", label: "Số thùng", minWidth: 90 },
    // { key: "totalQty", label: "Tổng SL", minWidth: 90 },
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

  statusOptions = ["Đã tạo mã QR", "Bản nháp"];

  lotBulkValues: { [key: string]: any } = {};

  lotEditingCell: { rowId: number; lotIdx: number; col: string } | null = null;

  filterValues: FilterValues = {
    sapCode: "",
    materialName: "",
    partNumber: "",
  };

  orderInfo = {
    poCode: "",
    vendorName: "",
    vendorCode: "",
    arrivalDate: null as Date | null,
    importScenario: "",
    warehouse: "",
    approver: "",
    note: "",
  };

  dataSource = new MatTableDataSource<ParentItem>([]);
  totalItems = 0;
  isLoading = false;

  scenarioOptions: TemScenarioResponse[] = [];
  filteredScenarioOptions: TemScenarioResponse[] = [];
  selectedScenario: TemScenarioResponse | null = null;
  isLoadingScenarios = false;
  activeMappingConfig: {
    separator: string;
    fieldMappings: {
      position: number;
      nccFieldDesc: string;
      dataField: string;
    }[];
  } | null = null;

  //select vat tu
  selectedLots = new Set<string>();
  draftLots: LotItem[] = []; // lot scan tu do, chua co poDetail

  //truyen po
  poOptions: PoImportTem[] = [];
  filteredPoOptions: PoImportTem[] = [];
  isLoadingPo = false;
  currentUser = "unknown";

  //danh sach kho
  warehouseOptions = [
    { value: "", label: "--" },
    { value: "RD-Warehouse", label: "RD-Warehouse - RD-Warehouse" },
    { value: "RD-LED-19", label: "RD-LED-19 - Kho LKDT thủ công" },
    { value: "RD-LED-03", label: "RD-LED-03 - Kho vật tư TBCS" },
    { value: "RD-LED-02", label: "RD-LED-02 - Kho C Ha" },
    { value: "05-A01-01", label: "RD-LED-05 - Kho ngành DTTD" },
    { value: "RD-LED-12", label: "RD-LED-12 - Kho A Hai" },
    { value: "RD-LED-17", label: "RD-LED-17 - Kho C Huong-SKD" },
    { value: "RD-LED-09", label: "RD-LED-09 - Kho vật tư Xuất khẩu" },
    { value: "RD-LED-07", label: "RD-LED-07 - Kho ngành LED1" },
    { value: "RD-LED-06", label: "RD-LED-06 - Kho ngành LED1" },
    { value: "RD-LED-08", label: "RD-LED-08 - Kho ngành SMART" },
    { value: "RD-LED-11", label: "RD-LED-11 - Kho ngành CNPT" },
    {
      value: "RD-BTP-MODUL-DRV",
      label: "RD-BTP-MODUL-DRV - Kho BTP MODULE DRV",
    },
    { value: "LR-LED1", label: "LR-LED1 - Kho-Ngành-LED1" },
    { value: "LR-LED2", label: "LR-LED2 - Kho-Ngành-LED2" },
    { value: "CNPT-01", label: "CNPT-01 - Kho-Ngành-CNPT" },
    { value: "LR-SMART", label: "LR-SMART - Kho-Ngành-SMART" },
    { value: "RD-TT-18", label: "RD-TT-18 - Kho TT R&D" },
  ];

  filteredWarehouseOptions = [...this.warehouseOptions];

  //nếu dùng kho con thì dùng cái dưới này
  // warehouseOptions: CachedWarehouse[] = [];
  // filteredWarehouseOptions: CachedWarehouse[] = [];
  isLoadingWarehouses = false;
  isImportingReel = false;

  // warehouseFilteredMap: { [rowId: number]: CachedWarehouse[] } = {};
  warehouseFilteredMap: {
    [rowId: number]: { value: string; label: string }[];
  } = {};

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  private readonly PO_DISPLAY_LIMIT = 50;
  /** Tracks which parent rows are expanded */
  private currentPoDetailId: number | null = null;
  private expandedRows = new Set<number>();

  private currentTransactionId: number | null = null;
  private currentTemScenarioId: number | null = null;
  private currentMappingConfig: string = "";

  /** Tracks which child items are expanded */
  private expandedChildren = new Set<string>();

  private expandedMobileRows = new Set<number>();

  //check trung reelId
  private scannedReelIds = new Set<string>();

  private readonly SCENARIO_DISPLAY_LIMIT = 50;
  constructor(
    private dialog: MatDialog,
    private alertService: AlertService,
    private datePipe: DatePipe,
    private cdr: ChangeDetectorRef,
    private managerTemNccService: ManagerTemNccService,
    private accountService: AccountService,
    private route: ActivatedRoute,
    private router: Router,
    private notificationService: NotificationService,
    private warehouseCacheService: WarehouseCacheService,
    private receivingSuppliesService: ReceivingSuppliesService,
    private http: HttpClient,
  ) {}

  // ==================== LIFECYCLE ====================

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(take(1))
      .subscribe((account) => {
        this.currentUser = account?.login ?? "unknown";
      });
    const idParam = this.route.snapshot.paramMap.get("id");
    this.loadScenarios(() => {
      if (idParam) {
        this.loadDetailById(+idParam);
      }
    });
    // this.loadWarehouseOptions();
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.dataSource.filterPredicate = this.buildFilterPredicate();
  }

  // ==================== EXPAND LOGIC ====================
  onQtyInput(event: Event, lot: LotItem, field: string): void {
    const val = Number((event.target as HTMLInputElement).value);
    if (val < 0) {
      (event.target as HTMLInputElement).value = "0";
      lot[field] = 0;
      this.notificationService.warning("Không được nhập số âm.");
    }
  }
  toggleRow(row: ParentItem): void {
    if (this.expandedRows.has(row.id)) {
      this.expandedRows.delete(row.id);
    } else {
      this.expandedRows.add(row.id);
    }
  }

  toggleMobileRow(item: ParentItem): void {
    if (this.expandedMobileRows.has(item.id)) {
      this.expandedMobileRows.delete(item.id);
    } else {
      this.expandedMobileRows.add(item.id);
    }
  }

  isMobileExpanded(item: ParentItem): boolean {
    return this.expandedMobileRows.has(item.id);
  }

  onScenarioSearch(value: string): void {
    const lower = (value ?? "").toLowerCase().trim();
    const filtered = lower
      ? this.scenarioOptions.filter(
          (s) =>
            s.vendorName.toLowerCase().includes(lower) ||
            s.vendorCode.toLowerCase().includes(lower),
        )
      : this.scenarioOptions;
    this.filteredScenarioOptions = filtered.slice(
      0,
      this.SCENARIO_DISPLAY_LIMIT,
    );
  }

  onScenarioSelected(scenario: TemScenarioResponse): void {
    this.selectedScenario = scenario;
    this.orderInfo.importScenario = `${scenario.vendorCode} - ${scenario.vendorName}`;

    try {
      this.activeMappingConfig = JSON.parse(scenario.mappingConfig);
    } catch {
      this.activeMappingConfig = null;
    }
  }

  displayScenario(scenario: TemScenarioResponse | null): string {
    if (!scenario) {
      return "";
    }
    return `${scenario.vendorCode} - ${scenario.vendorName}`;
  }

  onApply(): void {
    if (!this.selectedScenario) {
      this.alertService.addAlert({
        type: "warning",
        message: "Vui lòng chọn kịch bản nhập TEM.",
      });
      return;
    }

    // Da co transaction (dang o don cu) va co PO moi → update PO, khong tao moi
    if (this.currentTransactionId && this.orderInfo.poCode?.trim()) {
      this.applyPoToExistingTransaction();
      return;
    }

    // Chua co transaction → tao don moi
    this.createNewPoImportTem();
  }

  onScan(): void {
    if (!this.currentTransactionId) {
      this.alertService.addAlert({
        type: "warning",
        message: "Chưa có transaction.",
      });
      return;
    }

    // Map parentItems gọn để truyền vào dialog
    const parentItems = this.dataSource.data.map((r) => ({
      id: r.id,
      partNumber: r.partNumber,
      sapCode: r.sapCode,
      orderQty: r.totalQuantityOrder,
      materialName: r.materialName,
    }));

    const existingItems = this.collectExistingScannedItems();

    const isMobileScan = window.innerWidth <= 600;
    const dialogRef = this.dialog.open(ScanItemDialogComponent, {
      width: isMobileScan ? "100vw" : "90vw",
      maxWidth: isMobileScan ? "100vw" : "1400px",
      height: isMobileScan ? "100vh" : "92vh",
      maxHeight: isMobileScan ? "100dvh" : "92vh",
      panelClass: isMobileScan
        ? ["scan-dialog-panel", "scan-dialog-panel--mobile"]
        : ["scan-dialog-panel", "scan-dialog-panel--desktop"],
      autoFocus: false,
      disableClose: true,
      data: {
        mappingConfig: this.activeMappingConfig,
        arrivalDate: this.orderInfo.arrivalDate,
        warehouse: this.orderInfo.warehouse,
        approver: this.orderInfo.approver,
        importVendorTemTransactionsId: this.currentTransactionId,
        vendorCode:
          this.selectedScenario?.vendorCode ?? this.orderInfo.vendorCode,
        parentItems,
        existingReelIds: Array.from(this.scannedReelIds),
        existingItems,
        poCode: this.orderInfo.poCode,
        onItemsUpdated: (items: ScannedItem[]) =>
          this.syncLotsFromScannedItems(items),
      },
    });

    dialogRef.afterClosed().subscribe(
      (
        result: {
          items: ScannedItem[];
          warehouse: string;
          approver: string;
        } | null,
      ) => {
        if (!result) {
          return;
        }

        this.orderInfo.warehouse = result.warehouse;
        this.orderInfo.approver = result.approver;
        // Đồng bộ lại (idempotent) — phòng trường hợp đóng trước khi callback realtime kịp chạy.
        this.syncLotsFromScannedItems(result.items ?? []);
        this.cdr.detectChanges();
      },
    );
  }

  openReelImportFilePicker(input: HTMLInputElement): void {
    if (!this.currentTransactionId) {
      this.alertService.addAlert({
        type: "warning",
        message: "Vui lòng Apply đơn hàng trước khi import ReelID.",
      });
      return;
    }
    if (!this.activeMappingConfig) {
      this.alertService.addAlert({
        type: "warning",
        message: "Vui lòng chọn kịch bản nhập TEM để nhận diện mã QR.",
      });
      return;
    }
    input.value = "";
    input.click();
  }

  onReelImportFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }

    this.isImportingReel = true;
    input.value = "";
    const poSapCodes = this.dataSource.data
      .map((row) => (row.sapCode ?? "").trim())
      .filter(Boolean);
    const isExcel = /\.(xlsx|xls)$/i.test(file.name);
    const fileName = file.name;

    const reader = new FileReader();
    reader.onload = () => {
      try {
        let parsed: VendorReelLogParseResult;
        if (isExcel) {
          parsed = parseVendorFullQrExcelArrayBuffer(
            reader.result as ArrayBuffer,
            this.orderInfo.poCode ?? "",
            poSapCodes,
            { requireSap: false },
          );
        } else {
          parsed = parseVendorReelLogContent(
            String(reader.result ?? ""),
            this.orderInfo.poCode ?? "",
            poSapCodes,
            { requireSap: false },
          );
        }

        if (!parsed.rows.length) {
          this.isImportingReel = false;
          this.notificationService.error(
            parsed.errors[0] ?? "File không có dòng ReelID hợp lệ.",
          );
          return;
        }

        const poCode = (this.orderInfo.poCode ?? "").trim();
        this.resolveSapCodeByPartFromPo(poCode).subscribe({
          next: (partToSap) => {
            const previewRows = this.buildReelImportPreviewRows(
              parsed.rows,
              partToSap,
            );
            if (!previewRows.length) {
              this.isImportingReel = false;
              this.notificationService.error(
                "Không dựng được dữ liệu preview từ file import.",
              );
              return;
            }

            const dialogRef = this.dialog.open(
              ImportReelPreviewDialogComponent,
              {
                width: "96vw",
                maxWidth: "96vw",
                height: "92vh",
                maxHeight: "92vh",
                panelClass: "import-reel-preview-panel",
                disableClose: true,
                autoFocus: false,
                data: {
                  fileName,
                  rows: previewRows,
                  errors: parsed.errors,
                } as ReelImportPreviewDialogData,
              },
            );

            this.isImportingReel = false;
            this.cdr.markForCheck();

            dialogRef
              .afterClosed()
              .subscribe((confirmedRows: ReelImportPreviewRow[] | null) => {
                if (!confirmedRows?.length) {
                  return;
                }
                this.isImportingReel = true;
                this.applyReelImportPreviewRows(confirmedRows, parsed.errors);
              });
          },
          error: () => {
            this.isImportingReel = false;
            this.notificationService.error(
              "Không lấy được thông tin SAP từ PO để preview import.",
            );
          },
        });
      } catch {
        this.isImportingReel = false;
        this.notificationService.error("Không đọc được file import ReelID.");
      }
    };
    reader.onerror = () => {
      this.isImportingReel = false;
      this.notificationService.error("Đọc file thất bại.");
    };

    if (isExcel) {
      reader.readAsArrayBuffer(file);
    } else {
      reader.readAsText(file);
    }
  }

  onCancelScan(): void {
    this.isScanning = false;
    this.scanMessage = "";
  }
  // onLotWarehouseSearch(rowId: number, keyword: string): void {
  //   if (!keyword?.trim()) {
  //     this.warehouseCacheService.getAll().then(list => {
  //       this.warehouseFilteredMap[rowId] = list.slice(0, 50);
  //     });
  //     return;
  //   }
  //   this.warehouseCacheService.searchByName(keyword).then(result => {
  //     this.warehouseFilteredMap[rowId] = result;
  //   });
  // }

  // onLotWarehouseSelected(row: ParentItem, colKey: string, warehouse: CachedWarehouse): void {
  //   this.lotBulkValues[row.id + '_' + colKey] = warehouse.locationFullName;
  //   this.warehouseFilteredMap[row.id] = [];
  // }

  // getLotWarehouseOptions(rowId: number): CachedWarehouse[] {
  //   return this.warehouseFilteredMap[rowId] ?? [];
  // }

  onSaveLots(row: ParentItem): void {
    const now = new Date().toISOString();

    const invalidLot = row.lots.find(
      (lot) =>
        Number(lot.boxCount) < 0 ||
        Number(lot.totalQty) < 0 ||
        Number(lot.initialQuantity) < 0,
    );

    if (invalidLot) {
      this.notificationService.warning(
        "Không được nhập số âm cho Số thùng, Tổng SL hoặc Quantity.",
      );
      return;
    }

    const payload: CreateVendorTemDetailPayload[] = row.lots
      .filter((lot) => !!lot.vendorTemDetailId)
      .map((lot) => ({
        id: lot.vendorTemDetailId,
        reelId: lot.reelId ?? "",
        partNumber: lot.partNumber ?? "",
        vendor: lot.vendor ?? "",
        lot: lot.lotNumber ?? "",
        userData1: lot.userData1 ?? "",
        userData2: lot.userData2 ?? "",
        userData3: lot.userData3 ?? "",
        userData4: lot.userData4 ?? "",
        userData5: lot.userData5 ?? "",
        initialQuantity: Number(lot.initialQuantity) || 0,
        msdLevel: lot.msl ?? "",
        msdInitialFloorTime: "",
        msdBagSealDate: "",
        marketUsage: "",
        quantityOverride: 0,
        shelfTime: "",
        spMaterialName: "",
        warningLimit: "",
        maximumLimit: "",
        comments: "",
        warmupTime: "",
        storageUnit: lot.storageUnit ?? "",
        subStorageUnit: "",
        locationOverride: "",
        expirationDate: lot.expirationDate ?? "",
        manufacturingDate: lot.manufacturingDate ?? "",
        partClass: "",
        sapCode: lot.sapCode ?? "",
        vendorQrCode: lot.vendorQrCode ?? "",
        status: lot.status ?? "NEW",
        createdBy: lot.createdBy ?? this.currentUser,
        createdAt: lot.createdAt ?? now,
        updatedBy: this.currentUser,
        updatedAt: now,
        poDetailId: lot.poDetailId,
        importVendorTemTransactionsId: lot.importVendorTemTransactionsId,
      }));

    if (!payload.length) {
      this.notificationService.warning("Không có vật tư nào để lưu.");
      return;
    }

    this.managerTemNccService.batchUpdateVendorTemDetails(payload).subscribe({
      next: () => {
        this.notificationService.success("Cập nhật vật tư thành công.");
      },
      error: () => {
        this.notificationService.error("Cập nhật vật tư thất bại.");
      },
    });
  }
  hasSelectableLots(row: ParentItem): boolean {
    return row.lots?.some((l) => !this.isLotPending(l)) ?? false;
  }
  isLotPending(lot: LotItem): boolean {
    return (lot.status ?? "").toUpperCase() === "PENDING";
  }
  hasLots(row: ParentItem): boolean {
    return row.lots && row.lots.length > 0;
  }
  // onWarehouseSelected(warehouse: CachedWarehouse): void {
  //   this.orderInfo.warehouse = warehouse.locationFullName;
  // }

  // displayWarehouse(w: CachedWarehouse | string | null): string {
  //   if (!w) {
  //     return "";
  //   }
  //   if (typeof w === "string") {
  //     return w;
  //   }
  //   return w.locationFullName;
  // }
  // onWarehouseSearch(keyword: string): void {
  //   if (!keyword?.trim()) {
  //     this.filteredWarehouseOptions = this.warehouseOptions.slice(0, 50);
  //     return;
  //   }
  //   this.warehouseCacheService.searchByName(keyword).then((result) => {
  //     this.filteredWarehouseOptions = result;
  //   });
  // }
  onWarehouseSearch(keyword: string): void {
    if (!keyword?.trim()) {
      this.filteredWarehouseOptions = [...this.warehouseOptions];
      return;
    }
    const lower = keyword.toLowerCase();
    this.filteredWarehouseOptions = this.warehouseOptions.filter(
      (w) =>
        w.label.toLowerCase().includes(lower) ||
        w.value.toLowerCase().includes(lower),
    );
  }

  onWarehouseSelected(warehouse: { value: string; label: string }): void {
    this.orderInfo.warehouse = warehouse.value;
  }

  displayWarehouse(
    w: { value: string; label: string } | string | null,
  ): string {
    if (!w) {
      return "";
    }
    if (typeof w === "string") {
      return w;
    }
    return w.label;
  }

  onLotWarehouseSearch(rowId: number, keyword: string): void {
    if (!keyword?.trim()) {
      this.warehouseFilteredMap[rowId] = [...this.warehouseOptions];
      return;
    }
    const lower = keyword.toLowerCase();
    this.warehouseFilteredMap[rowId] = this.warehouseOptions.filter(
      (w) =>
        w.label.toLowerCase().includes(lower) ||
        w.value.toLowerCase().includes(lower),
    );
  }

  onLotWarehouseSelected(
    row: ParentItem,
    colKey: string,
    warehouse: { value: string; label: string },
  ): void {
    this.lotBulkValues[row.id + "_" + colKey] = warehouse.value;
    this.warehouseFilteredMap[row.id] = [];
  }

  getLotWarehouseOptions(rowId: number): { value: string; label: string }[] {
    return this.warehouseFilteredMap[rowId] ?? [];
  }
  //select vat tu
  get selectedLotCount(): number {
    return this.selectedLots.size;
  }

  getLotKey(rowId: number, lotIdx: number): string {
    return `${rowId}_${lotIdx}`;
  }

  isLotSelected(rowId: number, lotIdx: number): boolean {
    return this.selectedLots.has(this.getLotKey(rowId, lotIdx));
  }

  // chi select duoc nhung lot chua PENDING
  toggleLotSelect(rowId: number, lotIdx: number): void {
    const row = this.dataSource.data.find((r) => r.id === rowId);
    if (row && this.isLotPending(row.lots[lotIdx])) {
      return;
    }

    const key = this.getLotKey(rowId, lotIdx);
    if (this.selectedLots.has(key)) {
      this.selectedLots.delete(key);
    } else {
      this.selectedLots.add(key);
    }
  }

  // "tat ca" chi tinh nhung lot chua PENDING
  isAllLotsSelected(row: ParentItem): boolean {
    const selectableLots = row.lots?.filter((l) => !this.isLotPending(l));
    if (!selectableLots?.length) {
      return false;
    }
    return selectableLots.every((_, i) => {
      const realIdx = row.lots.indexOf(selectableLots[i]);
      return this.isLotSelected(row.id, realIdx);
    });
  }

  isSomeLotsSelected(row: ParentItem): boolean {
    const selectableLots = row.lots?.filter((l) => !this.isLotPending(l));
    if (!selectableLots?.length) {
      return false;
    }
    return (
      selectableLots.some((_, i) => {
        const realIdx = row.lots.indexOf(selectableLots[i]);
        return this.isLotSelected(row.id, realIdx);
      }) && !this.isAllLotsSelected(row)
    );
  }

  // toggleAll chi tac dong len nhung lot chua PENDING
  toggleAllLots(row: ParentItem): void {
    const allSelected = this.isAllLotsSelected(row);
    row.lots.forEach((lot, i) => {
      if (this.isLotPending(lot)) {
        return;
      } // bo qua lot da PENDING
      const key = this.getLotKey(row.id, i);
      if (allSelected) {
        this.selectedLots.delete(key);
      } else {
        this.selectedLots.add(key);
      }
    });
  }

  // getSelectedLotItems chi lay nhung lot chua PENDING
  getSelectedLotItems(): { row: ParentItem; lot: LotItem }[] {
    const result: { row: ParentItem; lot: LotItem }[] = [];
    this.dataSource.data.forEach((row) => {
      row.lots?.forEach((lot, i) => {
        if (this.isLotSelected(row.id, i) && !this.isLotPending(lot)) {
          result.push({ row, lot });
        }
      });
    });
    return result;
  }

  onSubmitApproval(): void {
    if (!this.currentTransactionId) {
      this.notificationService.warning(
        "Không xác định được transaction để phê duyệt.",
      );
      return;
    }

    if (!this.orderInfo.approver) {
      this.notificationService.warning("Vui lòng chọn người duyệt.");
      return;
    }

    const selectedItems = this.getSelectedLotItems();
    if (selectedItems.length === 0) {
      this.notificationService.warning(
        "Vui lòng chọn ít nhất một LOT để phê duyệt.",
      );
      return;
    }

    this.dialog
      .open(DialogContentExampleDialogComponent, {
        width: "400px",
        data: {
          title: "Xác nhận gửi phê duyệt",
          message: `Bạn có chắc chắn muốn gửi phê duyệt ${selectedItems.length} vật tư đã chọn?`,
          confirmText: "Gửi phê duyệt",
          cancelText: "Hủy",
        },
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed) {
          return;
        }
        this.executeSubmitApproval(selectedItems);
      });
  }

  isExpanded(row: ParentItem): boolean {
    return this.expandedRows.has(row.id);
  }
  applyLotColumn(row: ParentItem, colKey: string): void {
    const value = this.lotBulkValues[row.id + "_" + colKey];
    if (value === "" || value === null || value === undefined) {
      return;
    }

    const numericCols = ["boxCount", "totalQty", "initialQuantity"];
    if (numericCols.includes(colKey) && Number(value) < 0) {
      this.notificationService.warning("Không được nhập số âm.");
      this.lotBulkValues[row.id + "_" + colKey] = 0;
      return;
    }

    row.lots = row.lots.map((lot) => ({ ...lot, [colKey]: value }));
  }

  startLotEdit(rowId: number, lotIdx: number, col: string): void {
    this.lotEditingCell = { rowId, lotIdx, col };
  }

  stopLotEdit(): void {
    this.lotEditingCell = null;
  }
  toggleChild(child: ChildItem): void {
    const key = child.sapCode + "_" + child.partNumber;
    if (this.expandedChildren.has(key)) {
      this.expandedChildren.delete(key);
    } else {
      this.expandedChildren.add(key);
    }
  }

  isChildExpanded(child: ChildItem): boolean {
    const key = child.sapCode + "_" + child.partNumber;
    return this.expandedChildren.has(key);
  }

  // ==================== FILTER ====================

  applyFilter(): void {
    this.dataSource.filterPredicate = this.buildFilterPredicate();
    this.dataSource.filter = JSON.stringify(this.filterValues);
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  applyDateFilter(): void {
    this.applyFilter();
  }

  // ==================== MOBILE DATA ====================

  get mobileDataSource(): ParentItem[] {
    const data = this.dataSource.filteredData ?? this.dataSource.data ?? [];
    if (!this.paginator) {
      return data;
    }
    const start = this.paginator.pageIndex * this.paginator.pageSize;
    return data.slice(start, start + this.paginator.pageSize);
  }

  // ==================== STATUS COLOR ====================

  get orphanDraftLots(): LotItem[] {
    return this.draftLots;
  }
  statusColor(status: string): { [key: string]: string } {
    switch ((status ?? "").toLowerCase().trim()) {
      case "bản nháp":
        return {
          backgroundColor: "#FFF9B0",
          color: "#7A6A00",
          border: "1px solid #F0E68C",
        };
      case "đã tạo mã qr":
      case "đã tạo mã":
      case "đã gen mã":
        return {
          backgroundColor: "#2be265",
          color: "#1B5E35",
          border: "1px solid #A3D9B8",
        };
      default:
        return {
          backgroundColor: "#E0E0E0",
          color: "#333",
          border: "1px solid #CCC",
        };
    }
  }

  // ==================== ACTIONS ====================

  onDelete(item: ParentItem): void {}

  onEditChild(child: ChildItem): void {
    // Implement edit child logic
  }

  onViewLot(lot: LotItem): void {
    this.dialog.open(LotDetailDialogComponent, {
      width: "100vw",
      height: "100vh",
      maxWidth: "100vw",
      maxHeight: "100vh",
      panelClass: "lot-detail-dialog-panel",
      data: {
        partNumber: lot.lotNumber,
        manufacturingDate: "20250603",
        rows: lot.details ?? [],
      } as LotDetailDialogData,
    });
  }

  canSendSapParent(row: ParentItem): boolean {
    if (this.isSendingSap) {
      return false;
    }
    return (row.lots ?? []).some((lot) => this.canSendSapLot(lot));
  }

  canSendPanacimParent(row: ParentItem): boolean {
    if (this.isSendingPanacim) {
      return false;
    }
    return (row.lots ?? []).some((lot) => this.canSendPanacimLot(lot));
  }

  onSendSapParent(row: ParentItem): void {
    if (this.isSendingSap) {
      return;
    }
    const lots = (row.lots ?? []).filter((lot) => this.canSendSapLot(lot));
    if (!lots.length) {
      this.notificationService.warning(
        "Không có vật tư đủ điều kiện gửi SAP (cần PO hợp lệ trên Userdata5 và chưa gửi).",
      );
      return;
    }
    const missingMsg = this.buildMissingFieldsMessage(row, lots);
    if (missingMsg) {
      this.showMissingFieldsDialog(missingMsg);
      return;
    }
    this.dialog
      .open(DialogContentExampleDialogComponent, {
        width: "420px",
        data: {
          title: "Xác nhận gửi SAP",
          message: `Bạn có chắc muốn gửi ${lots.length} vật tư SAP của mã ${row.sapCode}?`,
          confirmText: "Gửi SAP",
          cancelText: "Hủy",
        },
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed) {
          return;
        }
        this.executeSendSap(row, lots);
      });
  }

  onSendPanacimParent(row: ParentItem): void {
    if (this.isSendingPanacim) {
      return;
    }
    if (!this.canSendPanacimParent(row)) {
      return;
    }
    const lots = (row.lots ?? []).filter((lot) => this.canSendPanacimLot(lot));
    if (!lots.length) {
      this.notificationService.warning(
        "Không có vật tư đủ điều kiện gửi PanaCIM.",
      );
      return;
    }
    const missingMsg = this.buildMissingFieldsMessage(row, lots);
    if (missingMsg) {
      this.showMissingFieldsDialog(missingMsg);
      return;
    }
    this.dialog
      .open(DialogContentExampleDialogComponent, {
        width: "420px",
        data: {
          title: "Xác nhận gửi PanaCIM",
          message: `Bạn có chắc muốn gửi ${lots.length} vật tư mã SAP ${row.sapCode} đến PanaCIM?`,
          confirmText: "Gửi PanaCIM",
          cancelText: "Hủy",
        },
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed) {
          return;
        }
        this.executeSendPanacim(row, lots);
      });
  }

  // ==================== PRIVATE ====================

  private canSendSapLot(lot: LotItem): boolean {
    if (lot.sapSent === true) {
      return false;
    }
    if (!(lot.reelId ?? "").trim()) {
      return false;
    }
    return this.lotHasValidPo(lot);
  }

  private canSendPanacimLot(lot: LotItem): boolean {
    return (
      Boolean((lot.reelId ?? "").trim()) &&
      Boolean(lot.vendorTemDetailId) &&
      lot.panaSendStatus !== true
    );
  }

  private lotHasValidPo(lot: LotItem): boolean {
    const po = this.getLotPoNumber(lot);
    return Boolean(po) && po !== "-";
  }

  private getLotPoNumber(lot: LotItem): string {
    const fromLot = String(lot.userData5 ?? "").trim();
    if (fromLot) {
      return fromLot;
    }
    return (this.orderInfo.poCode ?? "").trim();
  }

  private getEffectiveSapWarehouse(lot: LotItem): string {
    return String(lot.storageUnit ?? this.orderInfo.warehouse ?? "").trim();
  }

  /** Kiểm tra đủ cột theo chuẩn dialog tổng hợp. Trả về message lỗi hoặc null. */
  private buildMissingFieldsMessage(
    parent: ParentItem,
    lots: LotItem[],
  ): string | null {
    const issues: string[] = [];
    lots.forEach((lot) => {
      const missing = this.getMissingSendFields(parent, lot);
      if (!missing.length) {
        return;
      }
      const sap = String(lot.sapCode || parent.sapCode || "—").trim();
      const reel = String(lot.reelId ?? "").trim() || "—";
      issues.push(`• SAP ${sap} / ReelID ${reel}: thiếu ${missing.join(", ")}`);
    });
    if (!issues.length) {
      return null;
    }
    const maxShow = 15;
    const shown = issues.slice(0, maxShow);
    const more =
      issues.length > maxShow
        ? `\n… và ${issues.length - maxShow} vật tư khác.`
        : "";
    return (
      `Không thể gửi — chưa đủ thông tin theo chuẩn dialog Tổng hợp.\n` +
      `Vui lòng điền đủ các trường còn thiếu rồi thử lại:\n\n` +
      `${shown.join("\n")}${more}`
    );
  }

  private showMissingFieldsDialog(message: string): void {
    this.notificationService.error(
      "Không thể gửi — còn vật tư thiếu thông tin. Xem chi tiết trong hộp thoại.",
    );
    this.dialog.open(DialogContentExampleDialogComponent, {
      width: "560px",
      maxHeight: "80vh",
      data: {
        title: "Thiếu thông tin — không thể gửi",
        message,
        confirmText: "Đã hiểu",
        cancelText: "Đóng",
      },
    });
  }

  private getMissingSendFields(parent: ParentItem, lot: LotItem): string[] {
    const values: Record<string, string | number | null | undefined> = {
      reelId: lot.reelId,
      partNumber: lot.partNumber || parent.partNumber,
      itemName: parent.materialName,
      sapCode: lot.sapCode || parent.sapCode,
      vendor: lot.vendor || this.orderInfo.vendorCode,
      initialQuantity: lot.initialQuantity,
      manufacturingDate: lot.manufacturingDate,
      lotNumber: lot.lotNumber,
      expirationDate: lot.expirationDate,
      locationOverride: lot.locationOverride,
      storageUnit: lot.storageUnit || this.orderInfo.warehouse,
      userData1: lot.userData1,
      userData2: lot.userData2,
      userData3: lot.userData3,
      userData4: lot.userData4,
      userData5: lot.userData5 || this.orderInfo.poCode,
      msl: lot.msl,
    };

    return SEND_REQUIRED_FIELDS.filter((field) => {
      const raw = values[field.key];
      if (field.key === "initialQuantity") {
        const n = Number(raw);
        return !Number.isFinite(n) || n <= 0;
      }
      return !String(raw ?? "").trim();
    }).map((field) => field.label);
  }

  private executeSendSap(parent: ParentItem, lots: LotItem[]): void {
    for (const lot of lots) {
      const storageUnit = this.getEffectiveSapWarehouse(lot);
      const sapUnitError = validateStorageUnitForSap(storageUnit);
      if (sapUnitError) {
        const label = `${parent.sapCode}${lot.lotNumber ? ` / lô ${lot.lotNumber}` : ""}`;
        this.notificationService.error(`${label}: ${sapUnitError}`);
        return;
      }
    }

    const poNumbers = [...new Set(lots.map((lot) => this.getLotPoNumber(lot)))];
    this.isSendingSap = true;
    this.cdr.markForCheck();

    forkJoin(
      poNumbers.map((po) =>
        this.receivingSuppliesService.getSapPoInfo(po).pipe(
          map((res) => ({
            po,
            docEntry: Number.parseInt(res?.poInfo?.oporDocEntry ?? "", 10),
          })),
        ),
      ),
    )
      .pipe(
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
          const opdn: GoodsReceiptPoLine[] = lots.flatMap((lot) => {
            const po = this.getLotPoNumber(lot);
            const docEntry = docEntryByPo.get(po)!;
            return this.buildOpdnLines(parent, lot, docEntry);
          });
          if (!opdn.length) {
            throw new Error("Không có dữ liệu tem để gửi SAP.");
          }
          return this.receivingSuppliesService.postGoodsReceiptPo({
            OPDN: opdn,
          });
        }),
      )
      .subscribe({
        next: () => {
          lots.forEach((lot) => {
            lot.sapSent = true;
          });
          this.dataSource.data = [...this.dataSource.data];
          this.isSendingSap = false;
          this.notificationService.success(
            `Đã gửi SAP thành công (${lots.length} vật tư).`,
          );
          this.cdr.markForCheck();
        },
        error: (err: unknown) => {
          this.isSendingSap = false;
          this.notificationService.error(
            resolveHttpErrorMessage(err, "Gửi SAP thất bại."),
          );
          this.cdr.markForCheck();
        },
      });
  }

  private buildOpdnLines(
    parent: ParentItem,
    lot: LotItem,
    docEntry: number,
  ): GoodsReceiptPoLine[] {
    const reelId = String(lot.reelId ?? "").trim();
    if (!reelId) {
      return [];
    }
    const vendor = String(lot.vendor || this.orderInfo.vendorCode || "").trim();
    const storageUnit = this.getEffectiveSapWarehouse(lot);
    const quantity = Number(lot.initialQuantity ?? lot.totalQty ?? 1) || 1;
    const partNumber = String(parent.partNumber || lot.partNumber || "").trim();

    return [
      {
        Vendor: vendor,
        DocEntry: docEntry,
        ItemCode: String(parent.sapCode ?? lot.sapCode ?? "").trim(),
        Quantity: quantity,
        LotNum: String(lot.lotNumber ?? "").trim(),
        ReelID: reelId,
        PartNumber: partNumber,
        ExpirationDate: this.formatSapDateTime(lot.expirationDate),
        ManufacturingDate: this.formatSapDateTime(lot.manufacturingDate),
        StorageUnit: storageUnit,
      },
    ];
  }

  private formatSapDateTime(value: string | Date | null | undefined): string {
    if (!value) {
      return "0001-01-01T00:00:00";
    }
    if (value instanceof Date) {
      if (Number.isNaN(value.getTime())) {
        return "0001-01-01T00:00:00";
      }
      const y = value.getFullYear();
      const m = String(value.getMonth() + 1).padStart(2, "0");
      const d = String(value.getDate()).padStart(2, "0");
      return `${y}-${m}-${d}T00:00:00`;
    }
    const digits = String(value).replace(/\D/g, "");
    if (digits.length === 8) {
      return `${digits.slice(0, 4)}-${digits.slice(4, 6)}-${digits.slice(6, 8)}T00:00:00`;
    }
    return "0001-01-01T00:00:00";
  }

  private executeSendPanacim(parent: ParentItem, lots: LotItem[]): void {
    const exportRows = this.toPanacimExportRows(parent, lots);
    if (!exportRows.length) {
      this.notificationService.warning("Chưa có mã tem. Vui lòng scan trước.");
      return;
    }
    const fileName = `CSV_UP_Panacim_${parent.sapCode}_${new Date().toISOString().split("T")[0]}.csv`;
    const blob = generateTemPanacimCsvBlob(exportRows, (date) =>
      this.formatDateForPanacimExport(date),
    );
    const formData = new FormData();
    formData.append("file", blob, fileName);

    this.isSendingPanacim = true;
    this.cdr.markForCheck();

    this.http
      .post<{
        success?: boolean;
        message?: string;
      }>("/api/csv-upload", formData)
      .subscribe({
        next: (response) => {
          this.isSendingPanacim = false;
          if (response?.success) {
            this.notificationService.success(
              `Đã gửi ${exportRows.length} bản ghi tới PanaCIM thành công!`,
            );
            this.updatePanaSendStatus(lots);
          } else {
            this.notificationService.error(
              `Lỗi: ${response?.message ?? "Gửi PanaCIM thất bại."}`,
            );
          }
          this.cdr.markForCheck();
        },
        error: (error) => {
          this.isSendingPanacim = false;
          this.notificationService.error(
            `Lỗi khi gửi: ${error.error?.message ?? error.message ?? "Không thể kết nối đến server"}`,
          );
          this.cdr.markForCheck();
        },
      });
  }

  private toPanacimExportRows(
    parent: ParentItem,
    lots: LotItem[],
  ): TemPanacimExportInput[] {
    return lots
      .filter((lot) => Boolean((lot.reelId ?? "").trim()))
      .map((lot) => ({
        reelId: String(lot.reelId ?? "").trim(),
        partNumber: String(lot.partNumber || parent.partNumber || "").trim(),
        vendor: String(lot.vendor || this.orderInfo.vendorCode || "").trim(),
        lot: String(lot.lotNumber ?? "").trim(),
        userData1: String(lot.userData1 ?? "").trim(),
        userData2: String(lot.userData2 ?? "").trim(),
        userData3: String(lot.userData3 ?? "").trim(),
        userData4: String(lot.userData4 ?? "").trim(),
        userData5: String(lot.userData5 ?? "").trim(),
        initialQuantity: Number(lot.initialQuantity) || null,
        storageUnit: String(lot.storageUnit ?? "").trim(),
        expirationDate: lot.expirationDate ?? null,
        manufacturingDate: lot.manufacturingDate ?? null,
        sapCode: String(lot.sapCode || parent.sapCode || "").trim(),
      }));
  }

  private formatDateForPanacimExport(value: Date | string | null): string {
    if (!value) {
      return "";
    }
    if (value instanceof Date) {
      if (Number.isNaN(value.getTime())) {
        return "";
      }
      const y = value.getFullYear();
      const m = String(value.getMonth() + 1).padStart(2, "0");
      const d = String(value.getDate()).padStart(2, "0");
      return `${y}-${m}-${d}`;
    }
    return String(value).trim();
  }

  private updatePanaSendStatus(lots: LotItem[]): void {
    const now = new Date().toISOString();
    const payloads: CreateVendorTemDetailPayload[] = lots
      .filter((lot) => !!lot.vendorTemDetailId)
      .map((lot) => ({
        id: lot.vendorTemDetailId,
        reelId: lot.reelId ?? "",
        partNumber: lot.partNumber ?? "",
        vendor: lot.vendor ?? "",
        lot: lot.lotNumber ?? "",
        userData1: lot.userData1 ?? "",
        userData2: lot.userData2 ?? "",
        userData3: lot.userData3 ?? "",
        userData4: lot.userData4 ?? "",
        userData5: lot.userData5 ?? "",
        initialQuantity: Number(lot.initialQuantity) || 0,
        msdLevel: lot.msl ?? "",
        msdInitialFloorTime: "",
        msdBagSealDate: "",
        marketUsage: "",
        quantityOverride: 0,
        shelfTime: "",
        spMaterialName: "",
        warningLimit: "",
        maximumLimit: "",
        comments: "",
        warmupTime: "",
        storageUnit: lot.storageUnit ?? "",
        subStorageUnit: "",
        locationOverride: lot.locationOverride ?? "",
        expirationDate: lot.expirationDate ?? "",
        manufacturingDate: lot.manufacturingDate ?? "",
        partClass: "",
        sapCode: lot.sapCode ?? "",
        vendorQrCode: lot.vendorQrCode ?? "",
        status: lot.status ?? "NEW",
        createdBy: lot.createdBy ?? this.currentUser,
        createdAt: lot.createdAt ?? now,
        updatedBy: this.currentUser,
        updatedAt: now,
        poDetailId: lot.poDetailId,
        importVendorTemTransactionsId:
          lot.importVendorTemTransactionsId ?? this.currentTransactionId ?? 0,
        panaSendStatus: true,
      }));

    lots.forEach((lot) => {
      lot.panaSendStatus = true;
    });
    this.dataSource.data = [...this.dataSource.data];

    if (!payloads.length) {
      return;
    }

    this.managerTemNccService.batchUpdateVendorTemDetails(payloads).subscribe({
      next: () => {
        this.cdr.markForCheck();
      },
      error: () => {
        this.notificationService.warning(
          "Đã gửi PanaCIM nhưng lưu trạng thái thất bại.",
        );
      },
    });
  }

  private performDelete(item: ParentItem): void {
    // Call service to delete, then reload
  }

  private loadData(): void {
    // Example mock data — replace with real service call
  }

  private buildFilterPredicate() {
    return (item: ParentItem, filterJson: string): boolean => {
      const f: FilterValues = JSON.parse(filterJson);
      const match = (field: string, query: string): boolean =>
        !query || (field ?? "").toLowerCase().includes(query.toLowerCase());
      return (
        match(item.sapCode, f.sapCode) &&
        match(item.materialName, f.materialName) &&
        match(item.partNumber, f.partNumber)
      );
    };
  }

  private sameDate(a: Date | string, b: Date): boolean {
    const d = new Date(a);
    return (
      d.getFullYear() === b.getFullYear() &&
      d.getMonth() === b.getMonth() &&
      d.getDate() === b.getDate()
    );
  }

  private loadScenarios(onDone?: () => void): void {
    this.isLoadingScenarios = true;
    this.managerTemNccService.getTemIdentificationScenarios().subscribe({
      next: (data) => {
        this.scenarioOptions = data;
        this.filteredScenarioOptions = data.slice(
          0,
          this.SCENARIO_DISPLAY_LIMIT,
        );
        this.isLoadingScenarios = false;
        onDone?.();
      },
      error: () => {
        this.isLoadingScenarios = false;
        onDone?.();
      },
    });
  }

  private loadDetailById(id: number): void {
    this.isLoading = true;
    this.managerTemNccService.getPoImportTemDetail(id).subscribe({
      next: (detail) => {
        this.orderInfo.poCode = detail.poNumber;
        this.orderInfo.vendorName = detail.vendorName;
        this.orderInfo.vendorCode = detail.vendorCode;
        this.orderInfo.arrivalDate = detail.entryDate
          ? new Date(detail.entryDate)
          : null;
        this.orderInfo.note = detail.note ?? "";

        const stateTransactionId = history.state?.transactionId as
          | number
          | undefined;
        const transaction = stateTransactionId
          ? detail.importVendorTemTransactions?.find(
              (t) => t.id === stateTransactionId,
            )
          : detail.importVendorTemTransactions?.[0];

        if (!transaction) {
          this.isLoading = false;
          return;
        }

        this.orderInfo.warehouse = transaction.storageUnit ?? "";
        this.currentTransactionId = transaction.id;
        this.currentTemScenarioId =
          transaction.temIdentificationScenarioId ?? null;
        this.currentMappingConfig = transaction.mappingConfig ?? "";
        const noPoDetails = transaction.noPoVendorTemDetails ?? [];
        if (noPoDetails.length > 0) {
          this.draftLots = noPoDetails.map((v: any) => ({
            vendorTemDetailId: v.id,
            lotNumber: v.lot,
            reelId: v.reelId,
            partNumber: v.partNumber,
            vendor: v.vendor,
            boxCount: 1,
            totalQty: v.initialQuantity,
            initialQuantity: v.initialQuantity,
            userData1: v.userData1,
            userData2: v.userData2,
            userData3: v.userData3,
            userData4: v.userData4,
            userData5: v.userData5,
            msl: v.msdLevel,
            storageUnit: v.storageUnit,
            manufacturingDate: v.manufacturingDate,
            expirationDate: v.expirationDate,
            locationOverride: v.locationOverride,
            sapCode: v.sapCode,
            vendorQrCode: v.vendorQrCode,
            status: v.status,
            createdBy: v.createdBy,
            createdAt: v.createdAt,
            poDetailId: v.poDetailId ?? null,
            importVendorTemTransactionsId: transaction.id,
            details: [] as [],
          }));
        }
        if (transaction.mappingConfig) {
          try {
            this.activeMappingConfig = JSON.parse(transaction.mappingConfig);
          } catch {
            //de trong
          }

          const matched = this.scenarioOptions.find(
            (s) => s.id === transaction.temIdentificationScenarioId,
          );
          if (matched) {
            this.selectedScenario = matched;
            this.orderInfo.importScenario = `${matched.vendorCode} - ${matched.vendorName}`;
          }
        }

        const lots: LotItem[] = (transaction.poDetails ?? []).flatMap((pd) =>
          (pd.vendorTemDetails ?? []).map((v) => ({
            vendorTemDetailId: v.id,
            lotNumber: v.lot,
            reelId: v.reelId,
            partNumber: v.partNumber,
            vendor: v.vendor,
            boxCount: 1,
            totalQty: v.initialQuantity,
            initialQuantity: v.initialQuantity,
            userData1: v.userData1,
            userData2: v.userData2,
            userData3: v.userData3,
            userData4: v.userData4,
            userData5: v.userData5,
            msl: v.msdLevel,
            storageUnit: v.storageUnit,
            manufacturingDate: v.manufacturingDate,
            expirationDate: v.expirationDate,
            locationOverride: String(
              (v as { locationOverride?: string | null }).locationOverride ??
                "",
            ),
            sapCode: v.sapCode,
            vendorQrCode: v.vendorQrCode,
            status: v.status,
            createdBy: v.createdBy,
            createdAt: v.createdAt,
            poDetailId: pd.id,
            importVendorTemTransactionsId: transaction.id,
            panaSendStatus: v.panaSendStatus === true,
            sapSent: false,
            details: [] as [],
          })),
        );

        const parentItems: ParentItem[] = (transaction.poDetails ?? []).map(
          (pd, idx) => {
            const pdLots = lots.filter((l) => l.poDetailId === pd.id);
            const scannedTotalQty = pdLots.reduce(
              (sum, l) => sum + (Number(l.initialQuantity) || 0),
              0,
            );

            return {
              id: pd.id ?? idx,
              sapCode: pd.sapCode,
              materialName: pd.sapName,
              partNumber: pd.partNumber ?? "",
              boxScan: pdLots.length,
              totalQuantity: scannedTotalQty,
              totalQuantityOrder: pd.totalQuantity ?? 0,
              lots: pdLots,
            };
          },
        );

        this.dataSource.data = parentItems;
        this.totalItems = parentItems.length;
        this.enrichPartNumberForParents(parentItems);
        this.enrichPoQuantitiesFromSap(detail.poNumber);
        lots.forEach((l) => {
          if (l.reelId) {
            this.scannedReelIds.add(l.reelId);
          }
        });
        this.matchOrphanLots(parentItems);

        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoading = false;
        this.alertService.addAlert({
          type: "danger",
          message: "Không thể tải chi tiết đơn hàng.",
        });
      },
    });
  }

  private executeSubmitApproval(
    selectedItems: { row: ParentItem; lot: LotItem }[],
  ): void {
    const poImportTemId = +this.route.snapshot.paramMap.get("id")!;
    const transactionId = this.currentTransactionId!;
    const now = new Date().toISOString();

    const approvePayload: ApproveVendorTemPayload = {
      id: transactionId,
      poNumber: this.orderInfo.poCode,
      vendorCode: this.orderInfo.vendorCode,
      vendorName: this.orderInfo.vendorName,
      entryDate: this.orderInfo.arrivalDate
        ? (this.datePipe.transform(this.orderInfo.arrivalDate, "yyyy-MM-dd") ??
          "")
        : "",
      storageUnit: this.orderInfo.warehouse,
      temIdentificationScenarioId:
        this.selectedScenario?.id ?? this.currentTemScenarioId ?? null,
      mappingConfig: this.selectedScenario?.mappingConfig ?? "",
      status: "PENDING",
      createdBy: this.currentUser,
      createdAt: now,
      updatedBy: this.currentUser,
      updatedAt: now,
      deletedBy: "",
      deletedAt: "",
      approver: this.orderInfo.approver,
      poImportTemId,
    };

    const detailPayloads: CreateVendorTemDetailPayload[] = selectedItems
      .filter(({ lot }) => lot.vendorTemDetailId != null)
      .map(({ lot }) => ({
        id: lot.vendorTemDetailId,
        reelId: lot.reelId ?? "",
        partNumber: lot.partNumber ?? "",
        vendor: lot.vendor ?? "",
        lot: lot.lotNumber ?? "",
        userData1: lot.userData1 ?? "",
        userData2: lot.userData2 ?? "",
        userData3: lot.userData3 ?? "",
        userData4: lot.userData4 ?? "",
        userData5: lot.userData5 ?? "",
        initialQuantity: lot.initialQuantity ?? 0,
        msdLevel: lot.msl ?? "",
        msdInitialFloorTime: "",
        msdBagSealDate: "",
        marketUsage: "",
        quantityOverride: 0,
        shelfTime: "",
        spMaterialName: "",
        warningLimit: "",
        maximumLimit: "",
        comments: "",
        warmupTime: "",
        storageUnit: lot.storageUnit ?? "",
        subStorageUnit: "",
        locationOverride: "",
        expirationDate: lot.expirationDate ?? "",
        manufacturingDate: lot.manufacturingDate ?? "",
        partClass: "",
        sapCode: lot.sapCode ?? "",
        vendorQrCode: lot.vendorQrCode ?? "",
        status: "PENDING",
        createdBy: lot.createdBy ?? this.currentUser,
        createdAt: lot.createdAt ?? now,
        updatedBy: this.currentUser,
        updatedAt: now,
        poDetailId: lot.poDetailId,
        importVendorTemTransactionsId: lot.importVendorTemTransactionsId,
      }));

    this.isLoading = true;

    forkJoin({
      approve: this.managerTemNccService.approveImportVendorTemTransaction(
        transactionId,
        approvePayload,
      ),
      details:
        detailPayloads.length > 0
          ? this.managerTemNccService.batchUpdateVendorTemDetails(
              detailPayloads,
            )
          : of(null),
    }).subscribe({
      next: () => {
        this.isLoading = false;
        this.notificationService.success("Gửi phê duyệt thành công.");
        const approvedIds = new Set(detailPayloads.map((d) => d.id));
        this.dataSource.data = this.dataSource.data.map((row) => ({
          ...row,
          lots: row.lots.map((lot) =>
            approvedIds.has(lot.vendorTemDetailId)
              ? { ...lot, status: "PENDING" }
              : lot,
          ),
        }));
        this.selectedLots.clear();
        this.loadDetailById(poImportTemId);
      },
      error: () => {
        this.isLoading = false;
        this.notificationService.error("Gửi phê duyệt thất bại.");
      },
    });
  }

  private extractPartNumber(raw: string): string {
    if (!raw) {
      return "";
    }

    const cleaned = raw.trim().replace(/^"+|"+$/g, "");

    const parts = cleaned.split("|");

    return parts.length ? parts[parts.length - 1].trim() : "";
  }

  private enrichPartNumberForParents(items: ParentItem[]): void {
    items.forEach((item) => {
      if (!item.partNumber && item.lots.length > 0) {
        item.partNumber = item.lots[0].partNumber ?? "";
      }
    });
    this.dataSource.data = [...this.dataSource.data];
    this.cdr.markForCheck();
  }
  // private loadWarehouseOptions(): void {
  //   this.isLoadingWarehouses = true;
  //   this.warehouseCacheService.getAll().then((list) => {
  //     this.warehouseOptions = list;
  //     this.filteredWarehouseOptions = list.slice(0, 50);
  //     this.isLoadingWarehouses = false;
  //   });
  // }
  private matchOrphanLots(parentItems: ParentItem[]): void {
    const orphanLots = [...this.draftLots];
    if (!orphanLots.length) {
      return;
    }

    const now = new Date().toISOString();
    const payloads: CreateVendorTemDetailPayload[] = [];

    orphanLots.forEach((lot) => {
      const matched = parentItems.find(
        (p) =>
          (p.partNumber ?? "").toLowerCase() ===
          (lot.partNumber ?? "").toLowerCase(),
      );
      if (!matched) {
        return;
      }

      // Cap nhat lot trong dataSource
      matched.lots = matched.lots.filter((l) => l !== lot);
      lot.poDetailId = matched.id;
      lot.sapCode = matched.sapCode;
      lot.status = "NEW";
      matched.lots.push(lot);
      matched.boxScan += 1;
      matched.totalQuantity += Number(lot.initialQuantity) || 0;

      payloads.push({
        id: lot.vendorTemDetailId,
        reelId: lot.reelId ?? "",
        partNumber: lot.partNumber ?? "",
        vendor: lot.vendor ?? "",
        lot: lot.lotNumber ?? "",
        userData1: lot.userData1 ?? "",
        userData2: lot.userData2 ?? "",
        userData3: lot.userData3 ?? "",
        userData4: lot.userData4 ?? "",
        userData5: lot.userData5 ?? "",
        initialQuantity: Number(lot.initialQuantity) || 0,
        msdLevel: lot.msl ?? "",
        msdInitialFloorTime: "",
        msdBagSealDate: "",
        marketUsage: "",
        quantityOverride: 0,
        shelfTime: "",
        spMaterialName: "",
        warningLimit: "",
        maximumLimit: "",
        comments: "",
        warmupTime: "",
        storageUnit: lot.storageUnit ?? "",
        subStorageUnit: "",
        locationOverride: "",
        expirationDate: lot.expirationDate ?? "",
        manufacturingDate: lot.manufacturingDate ?? "",
        partClass: "",
        sapCode: matched.sapCode,
        vendorQrCode: lot.vendorQrCode ?? "",
        status: "NEW",
        createdBy: lot.createdBy ?? this.currentUser,
        createdAt: lot.createdAt ?? now,
        updatedBy: this.currentUser,
        updatedAt: now,
        poDetailId: matched.id,
        importVendorTemTransactionsId: lot.importVendorTemTransactionsId,
      });
    });

    if (!payloads.length) {
      return;
    }

    this.managerTemNccService.batchUpdateVendorTemDetails(payloads).subscribe({
      next: () => {
        this.draftLots = [];
        this.dataSource.data = [...this.dataSource.data];
        this.notificationService.success(
          `Đã ghép ${payloads.length} mã scan tạm vào đơn hàng.`,
        );
        this.cdr.markForCheck();
      },
      error: () => {
        this.notificationService.error("Ghép mã scan tạm thất bại.");
      },
    });
  }
  private buildReelImportPreviewRows(
    rows: VendorReelLogImportRow[],
    partToSap: Map<string, string>,
  ): ReelImportPreviewRow[] {
    if (!this.activeMappingConfig) {
      return [];
    }

    const parentItems = this.dataSource.data.map((r) => ({
      id: r.id,
      partNumber: r.partNumber,
      sapCode: r.sapCode,
    }));

    const previewRows: ReelImportPreviewRow[] = [];

    rows.forEach((row, index) => {
      const imported = toVendorImportedReelEntry(row);
      const qrCode = imported.qrCode;
      const fieldMap = parseVendorQrByMappingConfig(
        qrCode,
        this.activeMappingConfig!,
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
      const lotFromMapping = (fieldMap["lotNumber"] ?? "").trim();
      const qtyRaw = (fieldMap["initialQuantity"] ?? "").trim();
      const qtyFromMapping = Number(qtyRaw);
      const initialQuantity =
        qtyRaw !== "" && Number.isFinite(qtyFromMapping)
          ? qtyFromMapping
          : imported.quantity;

      let cleanDate = "";
      const dateSource =
        manufacturingDate ||
        (this.orderInfo.arrivalDate
          ? this.datePipe.transform(this.orderInfo.arrivalDate, "yyyyMMdd")
          : "");
      if (dateSource && /^\d{8}$/.test(String(dateSource))) {
        cleanDate = String(dateSource);
      }

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
      const vendor = this.firstNonEmpty(
        fieldMap["vendor"],
        this.selectedScenario?.vendorCode,
        this.orderInfo.vendorCode,
      );
      const lotNumber = this.firstNonEmpty(
        lotFromMapping,
        partNumber && cleanDate ? `${partNumber}${cleanDate}` : "",
        imported.lotNumber,
      );
      const poNumber = this.firstNonEmpty(
        fieldMap["poNumber"],
        imported.poNumber,
        this.orderInfo.poCode,
      );
      // Mapped values from QR nếu có; mặc định userData1-4/MSL/HSD để trống
      // (chỉ điền khi user tích "Tự điền mặc định" trên dialog).
      const mappedUserData1 = isVendorQrFieldMapped(
        this.activeMappingConfig,
        "userData1",
      )
        ? (fieldMap["userData1"] ?? "").trim()
        : "";
      const mappedUserData2 = isVendorQrFieldMapped(
        this.activeMappingConfig,
        "userData2",
      )
        ? (fieldMap["userData2"] ?? "").trim()
        : "";
      const mappedUserData3 = isVendorQrFieldMapped(
        this.activeMappingConfig,
        "userData3",
      )
        ? (fieldMap["userData3"] ?? "").trim()
        : "";
      const mappedUserData4 = isVendorQrFieldMapped(
        this.activeMappingConfig,
        "userData4",
      )
        ? (fieldMap["userData4"] ?? "").trim()
        : "";
      const mappedMsl = isVendorQrFieldMapped(this.activeMappingConfig, "msl")
        ? (fieldMap["msl"] ?? "").trim()
        : "";
      const mappedExpiration = isVendorQrFieldMapped(
        this.activeMappingConfig,
        "expirationDate",
      )
        ? (fieldMap["expirationDate"] ?? "").trim()
        : "";

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
        expirationDate: mappedExpiration,
        locationOverride: "",
        storageUnit: this.firstNonEmpty(fieldMap["storageUnit"]),
        sapCode: resolvedSap,
        vendor,
        userData1: mappedUserData1,
        userData2: mappedUserData2,
        userData3: mappedUserData3,
        userData4: mappedUserData4,
        userData5: this.firstNonEmpty(
          fieldMap["userData5"],
          fieldMap["poNumber"],
          imported.poNumber,
          this.orderInfo.poCode,
        ),
        msl: mappedMsl,
        vendorQrCode: qrCode,
      });
    });

    return previewRows;
  }

  private applyReelImportPreviewRows(
    rows: ReelImportPreviewRow[],
    parseErrors: string[],
  ): void {
    if (!this.currentTransactionId || !this.activeMappingConfig) {
      this.isImportingReel = false;
      return;
    }

    const parentItems = this.dataSource.data.map((r) => ({
      id: r.id,
      partNumber: r.partNumber,
      sapCode: r.sapCode,
      orderQty: r.totalQuantityOrder,
    }));
    const hasParents = parentItems.length > 0;
    const now = new Date().toISOString();
    const skipMessages: string[] = [...parseErrors];
    const payloads: CreateVendorTemDetailPayload[] = [];

    for (const row of rows) {
      const reelId = (row.reelId ?? "").trim();
      const partNumber = (row.partNumber ?? "").trim();
      const manufacturingDate = (row.manufacturingDate ?? "").trim();
      const initialQuantity = Number(row.initialQuantity) || 0;

      if (!reelId) {
        skipMessages.push(`Dòng ${row.lineNo}: thiếu ReelID.`);
        continue;
      }
      if (this.scannedReelIds.has(reelId)) {
        skipMessages.push(`ReelID "${reelId}" đã tồn tại — bỏ qua.`);
        continue;
      }

      const matchedRow = parentItems.find(
        (r) =>
          (r.partNumber ?? "").trim().toLowerCase() ===
            partNumber.toLowerCase() ||
          (!!(row.sapCode ?? "").trim() &&
            (r.sapCode ?? "").trim().toLowerCase() ===
              (row.sapCode ?? "").trim().toLowerCase()),
      );
      if (hasParents && partNumber && !matchedRow) {
        skipMessages.push(
          `ReelID "${reelId}": Part Number "${partNumber}" không có trong đơn.`,
        );
        continue;
      }

      const isOrphan = !matchedRow;
      const lot = this.firstNonEmpty(
        row.lotNumber,
        partNumber && manufacturingDate
          ? `${partNumber}${manufacturingDate}`
          : "",
      );

      const payload: CreateVendorTemDetailPayload = {
        reelId,
        partNumber,
        vendor: (row.vendor ?? "").trim(),
        lot,
        userData1: (row.userData1 ?? "").trim(),
        userData2: (row.userData2 ?? "").trim(),
        userData3: (row.userData3 ?? "").trim(),
        userData4: (row.userData4 ?? "").trim(),
        userData5: this.firstNonEmpty(
          row.userData5,
          row.poNumber,
          this.orderInfo.poCode,
        ),
        initialQuantity,
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
        storageUnit: (row.storageUnit ?? "").trim(),
        subStorageUnit: "",
        locationOverride: (row.locationOverride ?? "").trim(),
        manufacturingDate,
        expirationDate: (row.expirationDate ?? "").trim(),
        partClass: "",
        sapCode: (row.sapCode ?? "").trim(),
        vendorQrCode: (row.vendorQrCode ?? "").trim(),
        status: isOrphan ? "DRAFT" : "NEW",
        createdBy: this.currentUser,
        createdAt: now,
        updatedBy: this.currentUser,
        updatedAt: now,
        poDetailId: matchedRow?.id ?? (null as any),
        importVendorTemTransactionsId: this.currentTransactionId!,
      };

      this.scannedReelIds.add(reelId);
      payloads.push(payload);
    }

    if (!payloads.length) {
      this.isImportingReel = false;
      this.notificationService.warning(
        skipMessages[0] ?? "Không có dòng nào để import.",
      );
      this.cdr.markForCheck();
      return;
    }

    forkJoin(
      payloads.map((payload) =>
        this.managerTemNccService.createVendorTemDetails(payload).pipe(
          map((res) => ({ ok: true as const, payload, res })),
          catchError(() => {
            if (payload.reelId) {
              this.scannedReelIds.delete(payload.reelId);
            }
            return of({
              ok: false as const,
              payload,
              res: null,
            });
          }),
        ),
      ),
    ).subscribe({
      next: (results) => {
        const successItems = results.filter((r) => r.ok);
        const failCount = results.length - successItems.length;
        const matchedLots: { poDetailId: number; lot: LotItem }[] = [];
        const orphanLots: LotItem[] = [];

        successItems.forEach(({ payload, res }) => {
          const lot: LotItem = {
            vendorTemDetailId: res?.id ?? 0,
            lotNumber: payload.lot,
            reelId: payload.reelId,
            partNumber: payload.partNumber,
            vendor: payload.vendor,
            boxCount: 1,
            totalQty: payload.initialQuantity,
            initialQuantity: payload.initialQuantity,
            userData1: payload.userData1,
            userData2: payload.userData2,
            userData3: payload.userData3,
            userData4: payload.userData4,
            userData5: payload.userData5,
            msl: payload.msdLevel,
            storageUnit: payload.storageUnit,
            manufacturingDate: payload.manufacturingDate,
            expirationDate: payload.expirationDate,
            sapCode: payload.sapCode,
            vendorQrCode: payload.vendorQrCode,
            status: payload.status,
            createdBy: payload.createdBy,
            createdAt: payload.createdAt,
            poDetailId: payload.poDetailId,
            importVendorTemTransactionsId:
              payload.importVendorTemTransactionsId,
            details: [],
          };

          if (payload.poDetailId) {
            matchedLots.push({ poDetailId: payload.poDetailId, lot });
          } else {
            orphanLots.push(lot);
          }
        });

        if (matchedLots.length) {
          this.dataSource.data = this.dataSource.data.map((row) => {
            const related = matchedLots.filter((m) => m.poDetailId === row.id);
            if (!related.length) {
              return row;
            }
            const addedQty = related.reduce(
              (sum, m) => sum + (Number(m.lot.initialQuantity) || 0),
              0,
            );
            return {
              ...row,
              boxScan: row.boxScan + related.length,
              totalQuantity: row.totalQuantity + addedQty,
              lots: [...row.lots, ...related.map((m) => m.lot)],
            };
          });
        }

        if (orphanLots.length) {
          this.draftLots = [...orphanLots, ...this.draftLots];
        }

        this.isImportingReel = false;
        const msg = `Đã import ${successItems.length}/${payloads.length} ReelID.`;
        if (failCount || skipMessages.length) {
          this.notificationService.warning(
            `${msg} Bỏ qua ${failCount + skipMessages.length} dòng.`,
          );
        } else {
          this.notificationService.success(msg);
        }
        this.cdr.detectChanges();
      },
      error: () => {
        this.isImportingReel = false;
        this.notificationService.error("Import ReelID thất bại.");
        this.cdr.markForCheck();
      },
    });
  }

  /** Map partNumber (lowercase) → mã SAP lấy từ PO + OITM part-numbers. */
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

  private applyPoToExistingTransaction(): void {
    this.isLoadingPo = true;
    const poImportTemId = +this.route.snapshot.paramMap.get("id")!;

    this.managerTemNccService
      .updateTransactionWithPo({
        id: this.currentTransactionId!,
        poNumber: this.orderInfo.poCode,
        updatedBy: this.currentUser,
      })
      .subscribe({
        next: () => {
          this.isLoadingPo = false;
          this.alertService.addAlert({
            type: "success",
            message: "Cập nhật PO thành công, đang tải dữ liệu...",
          });
          this.loadDetailById(poImportTemId);
        },
        error: () => {
          this.isLoadingPo = false;
          this.alertService.addAlert({
            type: "danger",
            message: "Cập nhật PO thất bại.",
          });
        },
      });
  }

  private createNewPoImportTem(): void {
    const now = new Date().toISOString();
    const payload: PoImportTemPayload = {
      poNumber: this.orderInfo.poCode ?? "",
      vendorCode: this.selectedScenario!.vendorCode,
      vendorName: this.selectedScenario!.vendorName,
      entryDate: this.orderInfo.arrivalDate
        ? (this.datePipe.transform(this.orderInfo.arrivalDate, "yyyy-MM-dd") ??
          "")
        : "",
      storageUnit: this.orderInfo.warehouse,
      temIdentificationScenarioId: this.selectedScenario!.id,
      mappingConfig: this.selectedScenario!.mappingConfig,
      status: "NEW",
      createdBy: this.currentUser,
      createdAt: now,
      updatedBy: "",
      updatedAt: now,
      note: this.orderInfo.note ?? "",
    };

    this.isLoadingPo = true;
    this.managerTemNccService.createPoImportTem(payload).subscribe({
      next: (res: CreatePoImportTemResponse) => {
        const poDetails = res?.vendorTransaction?.poDetails ?? [];
        const transaction = res?.vendorTransaction?.transaction;
        const poImportTemId = res?.poImportTem?.id;

        if (transaction?.id) {
          this.currentTransactionId = transaction.id;
        }

        if (poImportTemId) {
          this.router.navigate(
            ["/info-tem-ncc/add-info-tem-ncc", poImportTemId],
            { replaceUrl: true },
          );
        }

        const parentItems: ParentItem[] = poDetails.map((detail, idx) => ({
          id: detail.id ?? idx,
          sapCode: detail.sapCode,
          materialName: detail.sapName,
          partNumber: detail.partNumber ?? "",
          boxScan: 0,
          totalQuantity: detail.totalQuantity ?? 0,
          totalQuantityOrder: detail.totalQuantity ?? 0,
          lots: [],
        }));

        this.dataSource.data = parentItems;
        this.totalItems = parentItems.length;
        this.selectedLots.clear();
        this.matchOrphanLots(parentItems);
        this.enrichPoQuantitiesFromSap(this.orderInfo.poCode);
        this.isLoadingPo = false;
        this.alertService.addAlert({
          type: "success",
          message: "Tạo đơn nhập TEM thành công.",
        });
      },
      error: () => {
        this.isLoadingPo = false;
        this.alertService.addAlert({
          type: "danger",
          message: "Tạo đơn thất bại.",
        });
      },
    });
  }

  /**
   * Điền cột "Số lượng trong PO" từ por1Quantity của /api/sap-po-info/{PO}
   * (map theo por1ItemCode ↔ sapCode).
   */
  private enrichPoQuantitiesFromSap(poCode?: string | null): void {
    const po = (poCode ?? this.orderInfo.poCode ?? "").trim();
    if (!po || !this.dataSource.data.length) {
      return;
    }

    this.receivingSuppliesService.getSapPoInfo(po).subscribe({
      next: (res) => {
        const qtyBySap = new Map<string, number>();
        (res?.poDetails ?? []).forEach((d) => {
          const sap = (d.por1ItemCode ?? "").trim();
          if (!sap) {
            return;
          }
          const qty = Number.parseFloat(String(d.por1Quantity ?? "0")) || 0;
          qtyBySap.set(sap, (qtyBySap.get(sap) ?? 0) + qty);
        });

        if (!qtyBySap.size) {
          return;
        }

        this.dataSource.data = this.dataSource.data.map((row) => {
          const sap = (row.sapCode ?? "").trim();
          if (!sap || !qtyBySap.has(sap)) {
            return row;
          }
          return {
            ...row,
            totalQuantityOrder: qtyBySap.get(sap) ?? row.totalQuantityOrder,
          };
        });
        this.cdr.markForCheck();
      },
      error: () => {
        // Giữ số lượng hiện có nếu không lấy được PO info
      },
    });
  }

  /** Gom lot đã có trên đơn (+ draft) để hiển thị sẵn trong dialog Scan/Import. */
  private collectExistingScannedItems(): ScannedItem[] {
    const fromParents = this.dataSource.data.flatMap((row) =>
      (row.lots ?? []).map((lot) => this.mapLotToScannedItem(lot)),
    );
    const fromDrafts = (this.draftLots ?? []).map((lot) =>
      this.mapLotToScannedItem(lot),
    );
    const merged = [...fromParents, ...fromDrafts];
    const seen = new Set<string>();
    return merged.filter((item) => {
      const reelId = (item.reelId ?? "").trim();
      if (!reelId || seen.has(reelId)) {
        return false;
      }
      seen.add(reelId);
      return true;
    });
  }

  /** Cập nhật / thêm lot trên đơn sau scan-import (không cần F5). */
  private syncLotsFromScannedItems(items: ScannedItem[]): void {
    if (!items?.length) {
      return;
    }

    const toLot = (s: ScannedItem): LotItem => ({
      vendorTemDetailId: s.dbId ?? 0,
      lotNumber: s.lot,
      reelId: s.reelId,
      partNumber: s.partNumber,
      vendor: s.vendor,
      boxCount: 1,
      totalQty: s.initialQuantity,
      initialQuantity: s.initialQuantity,
      userData1: s.userData1,
      userData2: s.userData2,
      userData3: s.userData3,
      userData4: s.userData4,
      userData5: s.userData5,
      msl: s.msdLevel,
      storageUnit: s.storageUnit,
      locationOverride: s.locationOverride,
      manufacturingDate: s.manufacturingDate,
      expirationDate: s.expirationDate,
      sapCode: s.sapCode,
      vendorQrCode: s.vendorQrCode,
      status: s.status,
      createdBy: s.createdBy,
      createdAt: s.createdAt,
      poDetailId: s.poDetailId,
      importVendorTemTransactionsId: s.importVendorTemTransactionsId,
      details: [],
    });

    const patchLotFields = (lot: LotItem, match: ScannedItem): LotItem => ({
      ...lot,
      vendorTemDetailId: match.dbId ?? lot.vendorTemDetailId,
      lotNumber: match.lot,
      reelId: match.reelId,
      partNumber: match.partNumber,
      vendor: match.vendor,
      initialQuantity: match.initialQuantity,
      totalQty: match.initialQuantity,
      userData1: match.userData1,
      userData2: match.userData2,
      userData3: match.userData3,
      userData4: match.userData4,
      userData5: match.userData5,
      msl: match.msdLevel,
      storageUnit: match.storageUnit,
      locationOverride: match.locationOverride,
      expirationDate: match.expirationDate,
      manufacturingDate: match.manufacturingDate,
      sapCode: match.sapCode,
      vendorQrCode: match.vendorQrCode,
      status: match.status,
      poDetailId: match.poDetailId ?? lot.poDetailId,
    });

    const findExistingLot = (
      lots: LotItem[],
      item: ScannedItem,
    ): LotItem | undefined => {
      if (item.dbId) {
        const byId = lots.find((l) => l.vendorTemDetailId === item.dbId);
        if (byId) {
          return byId;
        }
      }
      const reelId = (item.reelId ?? "").trim();
      if (!reelId) {
        return undefined;
      }
      return lots.find((l) => (l.reelId ?? "").trim() === reelId);
    };

    const resolveParentId = (item: ScannedItem): number | null => {
      const rawId = Number(item.poDetailId);
      if (Number.isFinite(rawId) && rawId > 0) {
        if (this.dataSource.data.some((p) => Number(p.id) === rawId)) {
          return rawId;
        }
      }
      const part = (item.partNumber ?? "").trim().toLowerCase();
      if (part) {
        const byPart = this.dataSource.data.find(
          (p) => (p.partNumber ?? "").trim().toLowerCase() === part,
        );
        if (byPart) {
          return byPart.id;
        }
      }
      const sap =
        (item.sapCode ?? "").trim().toLowerCase().replace(/^0+/, "") || "";
      if (sap) {
        const bySap = this.dataSource.data.find((p) => {
          const ps =
            (p.sapCode ?? "").trim().toLowerCase().replace(/^0+/, "") || "";
          return !!ps && ps === sap;
        });
        if (bySap) {
          return bySap.id;
        }
      }
      return null;
    };

    const parents = this.dataSource.data.map((row) => ({
      ...row,
      lots: [...(row.lots ?? [])],
    }));
    const draftLots = [...(this.draftLots ?? [])];
    const orphanToAdd: LotItem[] = [];

    items.forEach((item) => {
      const reelId = (item.reelId ?? "").trim();
      if (reelId) {
        this.scannedReelIds.add(reelId);
      }

      const parentId = resolveParentId(item);
      if (parentId != null) {
        const parent = parents.find((p) => Number(p.id) === Number(parentId));
        if (!parent) {
          return;
        }
        const existing = findExistingLot(parent.lots, item);
        if (existing) {
          Object.assign(existing, patchLotFields(existing, item));
          existing.poDetailId = parentId;
        } else {
          // Có thể đang nằm trong draft — gỡ khỏi draft trước khi thêm vào parent.
          const draftIdx = draftLots.findIndex((l) => {
            if (item.dbId && l.vendorTemDetailId === item.dbId) {
              return true;
            }
            return !!reelId && (l.reelId ?? "").trim() === reelId;
          });
          if (draftIdx >= 0) {
            draftLots.splice(draftIdx, 1);
          }
          const lot = toLot({ ...item, poDetailId: parentId });
          parent.lots.push(lot);
        }
        return;
      }

      // Không thuộc dòng PO nào → bỏ qua (không đưa vào draft / tổng hợp).
      if (parents.length > 0) {
        if (reelId) {
          this.scannedReelIds.delete(reelId);
        }
        return;
      }

      // Không có PO trên đơn: giữ draft
      const existingDraft = findExistingLot(draftLots, item);
      if (existingDraft) {
        Object.assign(existingDraft, patchLotFields(existingDraft, item));
      } else if (!findExistingLot(orphanToAdd, item)) {
        orphanToAdd.push(toLot(item));
      }
    });

    this.dataSource.data = parents.map((row) => {
      const lots = row.lots ?? [];
      const totalQuantity = lots.reduce(
        (sum, l) => sum + (Number(l.initialQuantity) || 0),
        0,
      );
      return {
        ...row,
        lots,
        boxScan: lots.length,
        totalQuantity,
      };
    });
    this.draftLots = [...orphanToAdd, ...draftLots];
    this.dataSource.data = [...this.dataSource.data];
    this.cdr.markForCheck();
  }

  private mapLotToScannedItem(lot: LotItem): ScannedItem {
    const reelId = (lot.reelId ?? "").trim();
    const parentRow = this.dataSource.data.find((p) => p.id === lot.poDetailId);
    const materialName =
      (parentRow?.materialName ?? "").trim() ||
      (
        this.dataSource.data.find(
          (p) =>
            (p.sapCode ?? "").trim() &&
            (p.sapCode ?? "").trim() === (lot.sapCode ?? "").trim(),
        )?.materialName ?? ""
      ).trim();
    return {
      id: `order-${lot.vendorTemDetailId ?? reelId}`,
      dbId: lot.vendorTemDetailId ?? undefined,
      reelId,
      partNumber: lot.partNumber ?? "",
      vendor: lot.vendor ?? "",
      lot: lot.lotNumber ?? "",
      userData1: lot.userData1 ?? "",
      userData2: lot.userData2 ?? "",
      userData3: lot.userData3 ?? "",
      userData4: lot.userData4 ?? "",
      userData5: lot.userData5 ?? "",
      initialQuantity: Number(lot.initialQuantity) || 0,
      msdLevel: lot.msl ?? "",
      msdInitialFloorTime: "",
      msdBagSealDate: "",
      marketUsage: "",
      quantityOverride: Number(lot.totalQty) || 0,
      shelfTime: "",
      spMaterialName: materialName,
      warningLimit: "",
      maximumLimit: "",
      comments: "",
      warmupTime: "",
      storageUnit: lot.storageUnit ?? "",
      subStorageUnit: "",
      locationOverride: lot.locationOverride ?? "",
      expirationDate: lot.expirationDate ?? "",
      manufacturingDate: lot.manufacturingDate ?? "",
      partClass: "",
      sapCode: lot.sapCode ?? "",
      vendorQrCode: lot.vendorQrCode ?? "",
      status: lot.status ?? "NEW",
      createdBy: lot.createdBy ?? "",
      createdAt: lot.createdAt ?? new Date().toISOString(),
      updatedBy: "",
      updatedAt: "",
      poDetailId: lot.poDetailId ?? (null as any),
      importVendorTemTransactionsId:
        lot.importVendorTemTransactionsId ?? this.currentTransactionId ?? 0,
      fromOrder: true,
    };
  }
}
