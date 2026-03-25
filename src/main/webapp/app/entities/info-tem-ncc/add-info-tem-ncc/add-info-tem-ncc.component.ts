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
import { switchMap, take } from "rxjs";
import { AccountService } from "app/core/auth/account.service";
import { ActivatedRoute } from "@angular/router";
import { NotificationService } from "app/entities/list-material/services/notification.service";

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
  quantityBoxOrder: number;
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
    "quantityBoxOrder",
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

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  private readonly PO_DISPLAY_LIMIT = 50;
  /** Tracks which parent rows are expanded */
  private currentPoDetailId: number | null = null;
  private expandedRows = new Set<number>();

  private currentTransactionId: number | null = null;

  /** Tracks which child items are expanded */
  private expandedChildren = new Set<string>();

  private expandedMobileRows = new Set<number>();

  private readonly SCENARIO_DISPLAY_LIMIT = 50;
  constructor(
    private dialog: MatDialog,
    private alertService: AlertService,
    private datePipe: DatePipe,
    private cdr: ChangeDetectorRef,
    private managerTemNccService: ManagerTemNccService,
    private accountService: AccountService,
    private route: ActivatedRoute,
    private notificationService: NotificationService,
  ) {}

  // ==================== LIFECYCLE ====================

  ngOnInit(): void {
    this.loadScenarios();

    this.accountService
      .getAuthenticationState()
      .pipe(take(1))
      .subscribe((account) => {
        this.currentUser = account?.login ?? "unknown";
      });
    const idParam = this.route.snapshot.paramMap.get("id");
    if (idParam) {
      this.loadDetailById(+idParam);
    } else {
      // this.loadData();
    }
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

        if (transaction?.id) {
          this.currentTransactionId = transaction.id;
        }

        const parentItems: ParentItem[] = poDetails.map((detail, idx) => ({
          id: detail.id ?? idx,
          sapCode: detail.sapCode,
          materialName: detail.sapName,
          partNumber: detail.partNumber ?? "",
          boxScan: 0,
          totalQuantity: detail.totalQuantity ?? 0,
          quantityBoxOrder: detail.quantityContainer ?? 0,
          totalQuantityOrder: detail.totalQuantity ?? 0,
          lots: [],
        }));

        this.dataSource.data = parentItems;
        this.totalItems = parentItems.length;
        this.selectedLots.clear();

        if (transaction?.mappingConfig) {
          try {
            this.activeMappingConfig = JSON.parse(transaction.mappingConfig);
          } catch {
            /* khong lam gi */
          }
        }

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
          message: "Tạo đơn nhập TEM thất bại.",
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
        parentItems, // ← truyền danh sách rows
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result) {
        return;
      }
      this.orderInfo.warehouse = result.warehouse;
      this.orderInfo.approver = result.approver;
      if (this.currentTransactionId) {
        this.loadDetailById(this.currentTransactionId);
      }
    });
  }
  onCancelScan(): void {
    this.isScanning = false;
    this.scanMessage = "";
  }
  hasLots(row: ParentItem): boolean {
    return row.lots && row.lots.length > 0;
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

  toggleLotSelect(rowId: number, lotIdx: number): void {
    const key = this.getLotKey(rowId, lotIdx);
    if (this.selectedLots.has(key)) {
      this.selectedLots.delete(key);
    } else {
      this.selectedLots.add(key);
    }
  }

  isAllLotsSelected(row: ParentItem): boolean {
    if (!row.lots?.length) {
      return false;
    }
    return row.lots.every((_, i) => this.isLotSelected(row.id, i));
  }

  isSomeLotsSelected(row: ParentItem): boolean {
    if (!row.lots?.length) {
      return false;
    }
    return (
      row.lots.some((_, i) => this.isLotSelected(row.id, i)) &&
      !this.isAllLotsSelected(row)
    );
  }

  toggleAllLots(row: ParentItem): void {
    if (this.isAllLotsSelected(row)) {
      row.lots.forEach((_, i) =>
        this.selectedLots.delete(this.getLotKey(row.id, i)),
      );
    } else {
      row.lots.forEach((_, i) =>
        this.selectedLots.add(this.getLotKey(row.id, i)),
      );
    }
  }

  getSelectedLotItems(): { row: ParentItem; lot: LotItem }[] {
    const result: { row: ParentItem; lot: LotItem }[] = [];
    this.dataSource.data.forEach((row) => {
      row.lots?.forEach((lot, i) => {
        if (this.isLotSelected(row.id, i)) {
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

    const poImportTemId = +this.route.snapshot.paramMap.get("id")!;
    const now = new Date().toISOString();

    const payload: ApproveVendorTemPayload = {
      id: poImportTemId,
      poNumber: this.orderInfo.poCode,
      vendorCode: this.orderInfo.vendorCode,
      vendorName: this.orderInfo.vendorName,
      entryDate: this.orderInfo.arrivalDate
        ? (this.datePipe.transform(this.orderInfo.arrivalDate, "yyyy-MM-dd") ??
          "")
        : "",
      storageUnit: this.orderInfo.warehouse,
      temIdentificationScenarioId: this.selectedScenario?.id ?? 0,
      mappingConfig: this.selectedScenario?.mappingConfig ?? "",
      status: "PENDING",
      createdBy: this.currentUser,
      createdAt: now,
      updatedBy: this.currentUser,
      updatedAt: now,
      deletedBy: "",
      deletedAt: "",
    };

    this.isLoading = true;

    this.managerTemNccService
      .approveImportVendorTemTransaction(poImportTemId, payload)
      .subscribe({
        next: () => {
          this.isLoading = false;
          this.notificationService.success("Gửi phê duyệt thành công.");
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

  private loadScenarios(): void {
    this.isLoadingScenarios = true;
    this.managerTemNccService.getTemIdentificationScenarios().subscribe({
      next: (data) => {
        this.scenarioOptions = data;
        this.filteredScenarioOptions = data.slice(
          0,
          this.SCENARIO_DISPLAY_LIMIT,
        );
        this.isLoadingScenarios = false;
      },
      error: () => {
        this.isLoadingScenarios = false;
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
          if (transaction.mappingConfig) {
            try {
              this.activeMappingConfig = JSON.parse(transaction.mappingConfig);
            } catch {
              /*Tam thoi de trong */
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
              quantityBoxOrder: pd.quantityContainer ?? 0,
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
}
