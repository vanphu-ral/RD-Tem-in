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
import { catchError, forkJoin, map, of, switchMap, take } from "rxjs";
import { AccountService } from "app/core/auth/account.service";
import { ActivatedRoute, Router } from "@angular/router";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { CachedWarehouse } from "app/entities/list-material/services/warehouse-db";
import { WarehouseCacheService } from "app/entities/list-material/services/warehouse-cache.service";

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
  ];
  isScanning = false;
  scanMessage = "";
  lotColumns = [
    { key: "lotNumber", label: "Lot", minWidth: 130 },
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

  //truyen po
  poOptions: PoImportTem[] = [];
  filteredPoOptions: PoImportTem[] = [];
  isLoadingPo = false;
  currentUser = "unknown";

  //danh sach kho
  warehouseOptions: CachedWarehouse[] = [];
  filteredWarehouseOptions: CachedWarehouse[] = [];
  isLoadingWarehouses = false;

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
    this.loadWarehouseOptions();
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.dataSource.filterPredicate = this.buildFilterPredicate();
  }

  // ==================== EXPAND LOGIC ====================

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
    if (!this.orderInfo.poCode || !this.selectedScenario) {
      this.alertService.addAlert({
        type: "warning",
        message: "Vui lòng nhập đầy đủ mã PO và kịch bản nhập TEM.",
      });
      return;
    }
    const currtentUser = this.currentUser;
    const now = new Date().toISOString();
    const payload: PoImportTemPayload = {
      poNumber: this.orderInfo.poCode,
      vendorCode: this.selectedScenario.vendorCode,
      vendorName: this.selectedScenario.vendorName,
      entryDate: this.orderInfo.arrivalDate
        ? (this.datePipe.transform(this.orderInfo.arrivalDate, "yyyy-MM-dd") ??
          "")
        : "",
      storageUnit: this.orderInfo.warehouse,
      temIdentificationScenarioId: this.selectedScenario.id,
      mappingConfig: this.selectedScenario.mappingConfig,
      status: "NEW",
      createdBy: currtentUser,
      createdAt: now,
      updatedBy: "",
      updatedAt: now,
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

        const requests = poDetails.map((detail) =>
          this.managerTemNccService.getItemDataByItemCode(detail.sapCode).pipe(
            map((raw) => ({
              ...detail,
              partNumber: this.extractPartNumber(raw),
            })),
            catchError(() => of({ ...detail, partNumber: "" })),
          ),
        );

        forkJoin(requests).subscribe({
          next: (detailsWithPartNumber) => {
            const parentItems: ParentItem[] = detailsWithPartNumber.map(
              (detail, idx) => ({
                id: detail.id ?? idx,
                sapCode: detail.sapCode,
                materialName: detail.sapName,
                partNumber: detail.partNumber,
                boxScan: 0,
                totalQuantity: detail.totalQuantity ?? 0,
                // quantityBoxOrder: detail.quantityContainer ?? 0,
                totalQuantityOrder: detail.totalQuantity ?? 0,
                lots: [],
              }),
            );

            this.dataSource.data = parentItems;
            this.totalItems = parentItems.length;
            this.selectedLots.clear();

            this.isLoadingPo = false;
            this.alertService.addAlert({
              type: "success",
              message: "Tạo đơn nhập TEM thành công.",
            });
          },
          error: () => {
            this.isLoadingPo = false;
            this.alertService.addAlert({
              type: "warning",
              message: "Tạo đơn thành công nhưng không lấy được Part Number.",
            });
          },
        });
      },
    });
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
    }));

    const dialogRef = this.dialog.open(ScanItemDialogComponent, {
      width: "95vw",
      maxWidth: "1400px",
      panelClass: "scan-dialog-panel",
      autoFocus: false,
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

        result.items.forEach((s: ScannedItem) => {
          if (s.reelId) {
            this.scannedReelIds.add(s.reelId);
          }
        });

        this.orderInfo.warehouse = result.warehouse;
        this.orderInfo.approver = result.approver;

        const scannedItems: ScannedItem[] = result.items;
        this.dataSource.data = this.dataSource.data.map((row) => {
          const relatedScans = scannedItems.filter(
            (s) => s.poDetailId === row.id,
          );
          if (relatedScans.length === 0) {
            return row;
          }

          const addedQty = relatedScans.reduce(
            (sum, s) => sum + (Number(s.initialQuantity) || 0),
            0,
          );

          // map them lot moi vao
          const newLots: LotItem[] = relatedScans.map((s) => ({
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
          }));
          return {
            ...row,
            boxScan: row.boxScan + relatedScans.length,
            totalQuantity: row.totalQuantity + addedQty,
            lots: [...row.lots, ...newLots],
          };
        });

        this.cdr.detectChanges();
      },
    );
  }
  onCancelScan(): void {
    this.isScanning = false;
    this.scanMessage = "";
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
  onWarehouseSelected(warehouse: CachedWarehouse): void {
    this.orderInfo.warehouse = warehouse.locationFullName;
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
    console.log("selectedItems:", selectedItems);
    console.log(
      "lots co vendorTemDetailId:",
      selectedItems.filter(({ lot }) => !!lot.vendorTemDetailId),
    );
    if (selectedItems.length === 0) {
      this.notificationService.warning(
        "Vui lòng chọn ít nhất một LOT để phê duyệt.",
      );
      return;
    }

    const poImportTemId = +this.route.snapshot.paramMap.get("id")!;
    const transactionId = this.currentTransactionId;
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
      poImportTemId: poImportTemId,
    };

    //payload cho vat tu
    const detailPayloads: CreateVendorTemDetailPayload[] = selectedItems
      .filter(
        ({ lot }) =>
          lot.vendorTemDetailId != null && lot.vendorTemDetailId !== undefined,
      )
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
          lots: row.lots.map((lot) => {
            if (approvedIds.has(lot.vendorTemDetailId)) {
              return { ...lot, status: "PENDING" };
            }
            return lot;
          }),
        }));

        // xoa selection
        this.selectedLots.clear();
        this.loadDetailById(poImportTemId);
      },
      error: () => {
        this.isLoading = false;
        this.notificationService.error("Gửi phê duyệt thất bại.");
      },
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

  // ==================== PRIVATE ====================

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
        this.orderInfo.warehouse = detail.storageUnit;
        this.orderInfo.arrivalDate = detail.entryDate
          ? new Date(detail.entryDate)
          : null;

        const transaction = detail.importVendorTemTransactions?.[0];
        if (transaction) {
          this.currentTransactionId = transaction.id;
          this.currentTemScenarioId =
            transaction.temIdentificationScenarioId ?? null;
          this.currentMappingConfig = transaction.mappingConfig ?? "";
          if (transaction.mappingConfig) {
            try {
              this.activeMappingConfig = JSON.parse(transaction.mappingConfig);
            } catch {
              /*Tam thoi de trong */
            }

            const matched = this.scenarioOptions.find(
              (s) => s.id === transaction.temIdentificationScenarioId,
            );
            if (matched) {
              this.selectedScenario = matched;
              this.orderInfo.importScenario = `${matched.vendorCode} - ${matched.vendorName}`;
            }
          }

          const parentItems: ParentItem[] = (transaction.poDetails ?? []).map(
            (pd, idx) => ({
              id: pd.id ?? idx,
              sapCode: pd.sapCode,
              materialName: pd.sapName,
              partNumber: pd.partNumber ?? "",
              boxScan: pd.vendorTemDetails?.length ?? 0,
              totalQuantity: pd.totalQuantity ?? 0,
              // quantityBoxOrder: pd.quantityContainer ?? 0,
              totalQuantityOrder: pd.totalQuantity ?? 0,
              lots: (pd.vendorTemDetails ?? []).map((v) => ({
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
                sapCode: v.sapCode,
                vendorQrCode: v.vendorQrCode,
                status: v.status,
                createdBy: v.createdBy,
                createdAt: v.createdAt,
                poDetailId: pd.id,
                importVendorTemTransactionsId: transaction.id,
                details: [],
              })),
            }),
          );

          this.dataSource.data = parentItems;
          this.totalItems = parentItems.length;
          this.enrichPartNumberForParents(parentItems);
          parentItems.forEach((p) =>
            p.lots.forEach((l: any) => {
              if (l.reelId) {
                this.scannedReelIds.add(l.reelId);
              }
            }),
          );
        }

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

  private extractPartNumber(raw: string): string {
    if (!raw) {
      return "";
    }

    const cleaned = raw.trim().replace(/^"+|"+$/g, "");

    const parts = cleaned.split("|");

    return parts.length ? parts[parts.length - 1].trim() : "";
  }

  private enrichPartNumberForParents(items: ParentItem[]): void {
    const needFetch = items.filter((i) => !i.partNumber);

    if (!needFetch.length) {
      return;
    }

    const requests = needFetch.map((item) =>
      this.managerTemNccService.getItemDataByItemCode(item.sapCode).pipe(
        map((raw) => ({
          item,
          partNumber: this.extractPartNumber(raw),
        })),
        catchError(() => of({ item, partNumber: "" })),
      ),
    );

    forkJoin(requests).subscribe({
      next: (results) => {
        results.forEach((r) => {
          r.item.partNumber = r.partNumber;
        });

        this.dataSource.data = [...this.dataSource.data];
        this.cdr.markForCheck();
      },
      error: () => {
        console.warn("Không thể enrich partNumber");
      },
    });
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
