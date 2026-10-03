import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ChangeDetectorRef,
} from "@angular/core";
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from "@angular/animations";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { forkJoin, Observable, of, take } from "rxjs";
import { map } from "rxjs/operators";
import {
  DeliveryNotificationDetailDto,
  DELIVERY_SOURCE_SYSTEM,
  DeliveryNotificationDto,
  InfoTemNccService,
  SapPor1BatchItem,
  SapPor1R1Dto,
  VendorLabelInfoDto,
  toText,
} from "../services/info-tem-ncc.service";
import {
  isSendFlagOn,
  SendBoxEntry,
  VendorLabelSendService,
} from "../services/vendor-label-send.service";
import { openSendConfirm } from "../shared/send-confirm-dialog/send-confirm-dialog.component";
import {
  PoImportDialogComponent,
  PoImportDialogData,
  PoImportSelection,
} from "./po-import-dialog/po-import-dialog.component";
import { OrderWorkspaceData } from "./order-summary-panel/order-summary-panel.component";
import {
  MiniPageState,
  slicePage,
} from "../shared/mini-pager/mini-pager.component";
import {
  PayloadPreviewDialogComponent,
  PayloadPreviewDialogData,
} from "../shared/payload-preview-dialog/payload-preview-dialog.component";
import { resolveHttpErrorMessage } from "app/entities/generate-tem-in/service/receiving-supplies.service";
import { AlertService } from "app/core/util/alert.service";
import { AccountService } from "app/core/auth/account.service";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import {
  ManagerTemNccService,
  SapOcrd,
  TemScenarioResponse,
} from "app/entities/list-material/services/info-tem-ncc.service";
import {
  LotDetailDialogComponent,
  LotDetailDialogData,
} from "../lot-detail-dialog/lot-detail-dialog.component";
import {
  parseVendorQrByMappingConfig,
  VendorQrMappingConfig,
} from "../shared/vendor-qr-mapping.util";
import {
  CreatePalletDialogComponent,
  CreatePalletDialogData,
} from "app/entities/pallet-management/list/create-pallet-dialog/create-pallet-dialog.component";
import { PrintPalletDialogComponent } from "app/entities/pallet-management/list/print-pallet-dialog/print-pallet-dialog.component";
import {
  CreatePalletDialogResult,
  PalletItem,
  PrintPalletDialogData,
} from "app/entities/pallet-management/list/pallet-management.model";
import {
  MaterialSummaryDialogComponent,
  MaterialSummaryDialogData,
  UnassignedPoLine,
} from "./material-summary-dialog/material-summary-dialog.component";
import { ScanImportDialogComponent } from "./scan-import-dialog/scan-import-dialog.component";
import { ScanImportDialogData } from "./scan-import-dialog/scan-import.models";
import { boxLocation } from "../shared/box-location.util";

/** Cấp 3 – lot */
export interface AddLotItem {
  id: number;
  lotNumber: string;
  warehouseCode: string;
  quantity: number;
  boxCount: number;
  manufacturingDate: string;
  expirationDate: string;
  /** raw fields for lot detail dialog */
  reelId?: string;
  partNumber?: string;
  vendor?: string;
  msl?: string;
  /** Các thùng (vendorLabelInfo) thuộc lot — từ API detail */
  boxes?: VendorLabelInfoDto[];
}

/** Cấp 2 – vật tư */
export interface AddMaterialItem {
  id: number;
  materialCode: string;
  materialName: string;
  partNumber: string;
  warehouseCode: string;
  lotCount: number;
  poQuantity: number;
  receivedQuantity: number;
  palletCount: number;
  boxCount: number;
  importedBy: string;
  scannedProgress: number;
  sentProgress: number;
  lots: AddLotItem[];
}

/** Cấp 1 – PO */
export interface AddPoItem {
  id: number;
  poCode: string;
  warehouseKeeper: string;
  vendorCode: string;
  vendorName: string;
  vehicleNumber: string;
  invoiceNumber: string;
  contractCode: string;
  importDate: string;
  importBatch: number | null;
  materialTypeCount: number;
  totalQuantity: number;
  status: "IMPORTING" | "WAITING";
  materials: AddMaterialItem[];
}

export interface PoFilterValues {
  poCode: string;
  warehouseKeeper: string;
  vehicleNumber: string;
  contractCode: string;
  status: string;
}

export interface MaterialFilterValues {
  materialCode: string;
  materialName: string;
  partNumber: string;
  warehouseCode: string;
}

@Component({
  selector: "jhi-add-info-tem-ncc",
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
  displayedColumns = [
    "expand",
    "poCode",
    "warehouseKeeper",
    "vendorCode",
    "vendorName",
    "vehicleNumber",
    "invoiceNumber",
    "contractCode",
    "importDate",
    "importBatch",
    "materialTypeCount",
    "totalQuantity",
    "status",
    "actions",
  ];

  orderInfo = {
    deliveryNotice: "",
    vendorCode: "",
    vendorName: "",
    arrivalDate: null as Date | null,
    invoiceNumber: "",
    contractCode: "",
    importScenario: "",
    warehouse: "",
    approver: "",
    /** Số xe (contNo) */
    contNo: "",
  };

  poFilter: PoFilterValues = {
    poCode: "",
    warehouseKeeper: "",
    vehicleNumber: "",
    contractCode: "",
    status: "",
  };

  dataSource = new MatTableDataSource<AddPoItem>([]);
  pageSize = 25;
  pageSizeOptions = [10, 25, 50];
  /** Cỡ trang cho bảng lồng (vật tư trong PO, lô trong vật tư) */
  readonly nestedPageSizeOptions = [5, 10, 20];

  vendorOptions: SapOcrd[] = [];
  filteredVendorOptions: SapOcrd[] = [];
  isLoadingVendors = false;

  scenarioOptions: TemScenarioResponse[] = [];
  filteredScenarioOptions: TemScenarioResponse[] = [];
  selectedScenario: TemScenarioResponse | null = null;
  isLoadingScenarios = false;
  activeMappingConfig: VendorQrMappingConfig | null = null;

  warehouseOptions = [
    { value: "", label: "--" },
    { value: "RD-Warehouse", label: "RD-Warehouse - RD-Warehouse" },
    { value: "RD-LED-19", label: "RD-LED-19 - Kho LKDT thủ công" },
    { value: "RD-LED-03", label: "RD-LED-03 - Kho vật tư TBCS" },
    { value: "RD-LED-02", label: "RD-LED-02 - Kho C Ha" },
    { value: "01", label: "01 - Kho chính" },
    { value: "02", label: "02 - Kho phụ" },
  ];
  filteredWarehouseOptions = [...this.warehouseOptions];
  isLoadingWarehouses = false;

  currentUser = "unknown";

  /** id đơn (delivery notification) khi vào chi tiết; null = tạo mới */
  deliveryId: number | null = null;
  isLoadingDetail = false;
  isSendingSap = false;
  isSendingPanacim = false;
  isSavingOrder = false;
  /** Số mã vật tư có thùng chưa xác định PO (sapPor1Id rỗng / không thuộc đơn) */
  unassignedMaterialCount = 0;
  /** PO đã gọi lấy part-numbers (chỉ gọi khi expand PO lần đầu) */
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  private partLoadedPoIds = new Set<number>();
  /** Thùng đã scan nhưng chưa thuộc dòng PO nào của đơn */
  private unassignedBoxes: VendorLabelInfoDto[] = [];
  /** NCC lưu trong đơn (đơn cũ: vendorName chứa mã; đơn mới: tên, có thể kèm vendorCode) */
  private rawOrderVendor: { code: string; name: string } | null = null;
  /** Bản ghi đơn đang mở (từ API detail) — giữ các trường không sửa trên form khi PUT */
  private loadedOrder: DeliveryNotificationDto | null = null;
  /** Phân trang: vật tư theo PO (key = id PO), lô theo vật tư (key = id vật tư) */
  private materialPageStates = new Map<number, MiniPageState>();
  private lotPageStates = new Map<number, MiniPageState>();
  /** Ô lọc bảng con: vật tư theo PO (key = po.id), lot theo vật tư (key = material.id) */
  private materialFilters = new Map<number, MaterialFilterValues>();
  private lotFilters = new Map<number, { lot: string }>();

  private expandedPoIds = new Set<number>();
  private expandedMaterialIds = new Set<number>();
  private readonly SCENARIO_DISPLAY_LIMIT = 50;
  private readonly VENDOR_DISPLAY_LIMIT = 50;
  /** Mock transaction id — mở scan dialog khi chưa nối API tạo đơn. */
  private mockTransactionId = 1;
  private nextPalletSequence = 1;

  constructor(
    private dialog: MatDialog,
    private alertService: AlertService,
    private cdr: ChangeDetectorRef,
    private managerTemNccService: ManagerTemNccService,
    private accountService: AccountService,
    private notificationService: NotificationService,
    private route: ActivatedRoute,
    private router: Router,
    private infoTemNccService: InfoTemNccService,
    private vendorLabelSendService: VendorLabelSendService,
  ) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(take(1))
      .subscribe((account) => {
        this.currentUser = account?.login ?? "unknown";
        this.orderInfo.approver = this.currentUser;
      });

    this.loadVendors();
    this.loadScenarios();
    this.dataSource.filterPredicate = this.buildPoFilterPredicate();

    const idParam = Number(this.route.snapshot.paramMap.get("id"));
    if (Number.isFinite(idParam) && idParam > 0) {
      this.deliveryId = idParam;
      this.loadDetail(idParam);
    }
  }

  /**
   * Chưa có id → Lưu: POST /delivery-notifications → chuyển sang URL đơn vừa tạo.
   * Đã có id → Cập nhật: PUT /delivery-notifications/{id}.
   */
  onSaveOrder(): void {
    if (this.isSavingOrder) {
      return;
    }
    if (this.deliveryId !== null) {
      this.updateOrder(this.deliveryId);
      return;
    }
    const code = (this.orderInfo.deliveryNotice ?? "").trim();
    const vendor =
      (this.orderInfo.vendorCode ?? "").trim() ||
      (this.orderInfo.vendorName ?? "").trim();
    if (!vendor) {
      this.notificationService.warning("Vui lòng chọn Nhà cung cấp.");
      return;
    }
    const now = new Date().toISOString();
    const arrival = this.orderInfo.arrivalDate;
    this.isSavingOrder = true;
    this.infoTemNccService
      .createDeliveryNotification({
        deliveryNotificationCode: code,
        invoiceNumber: (this.orderInfo.invoiceNumber ?? "").trim(),
        contractCode: (this.orderInfo.contractCode ?? "").trim(),
        vendorName: this.orderVendorName(),
        contNo: (this.orderInfo.contNo ?? "").trim(),
        entryDate:
          arrival instanceof Date && !isNaN(arrival.getTime())
            ? arrival.toISOString()
            : now,
        numberOfPo: 0,
        numberOfItem: 0,
        status: "New",
        source: DELIVERY_SOURCE_SYSTEM,
        createdBy: this.currentUser,
        createdAt: now,
      })
      .subscribe({
        next: (created) => {
          this.isSavingOrder = false;
          const id = Number(created?.id);
          if (!id) {
            this.notificationService.warning(
              "Đã lưu nhưng không nhận được id đơn mới.",
            );
            return;
          }
          this.notificationService.success("Tạo mới đơn thành công.");
          void this.router.navigate(["/info-tem-ncc/add-info-tem-ncc", id], {
            replaceUrl: true,
          });
        },
        error: () => {
          this.isSavingOrder = false;
          this.notificationService.error("Lưu đơn thất bại.");
        },
      });
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  // ==================== HEADER APIs ====================

  onVendorSearch(value: string): void {
    const lower = (value ?? "").toLowerCase().trim();
    const filtered = lower
      ? this.vendorOptions.filter(
          (v) =>
            (v.cardName ?? "").toLowerCase().includes(lower) ||
            (v.cardCode ?? "").toLowerCase().includes(lower),
        )
      : this.vendorOptions;
    this.filteredVendorOptions = filtered.slice(0, this.VENDOR_DISPLAY_LIMIT);
  }

  onVendorSelected(vendor: SapOcrd): void {
    this.orderInfo.vendorName = vendor.cardName ?? "";
    this.orderInfo.vendorCode = vendor.cardCode ?? "";
  }

  displayVendorCode = (value: SapOcrd | string | null): string => {
    if (!value) {
      return "";
    }
    if (typeof value === "string") {
      return value;
    }
    return value.cardCode ?? "";
  };

  displayVendorName = (value: SapOcrd | string | null): string => {
    if (!value) {
      return "";
    }
    if (typeof value === "string") {
      return value;
    }
    return value.cardName ?? "";
  };

  onVendorCodeSearch(value: string): void {
    this.onVendorSearch(value);
  }

  onScenarioSearch(value: string): void {
    const lower = (value ?? "").toLowerCase().trim();
    const filtered = lower
      ? this.scenarioOptions.filter(
          (s) =>
            (s.vendorName ?? "").toLowerCase().includes(lower) ||
            (s.vendorCode ?? "").toLowerCase().includes(lower),
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

  displayScenario = (scenario: TemScenarioResponse | string | null): string => {
    if (!scenario) {
      return "";
    }
    if (typeof scenario === "string") {
      return scenario;
    }
    return `${scenario.vendorCode} - ${scenario.vendorName}`;
  };

  onWarehouseSearch(keyword: string): void {
    if (!keyword?.trim()) {
      this.filteredWarehouseOptions = [...this.warehouseOptions];
      return;
    }
    const lower = keyword.toLowerCase();
    this.filteredWarehouseOptions = this.warehouseOptions.filter((w) =>
      (w.label ?? "").toLowerCase().includes(lower),
    );
  }

  onWarehouseSelected(warehouse: { value: string; label: string }): void {
    this.orderInfo.warehouse = warehouse.value;
  }

  displayWarehouse = (
    w: { value: string; label: string } | string | null,
  ): string => {
    if (!w) {
      return "";
    }
    if (typeof w === "string") {
      return w;
    }
    return w.label;
  };

  // ==================== FILTER / EXPAND ====================

  applyPoFilter(): void {
    this.dataSource.filter = JSON.stringify(this.poFilter);
    this.dataSource.paginator?.firstPage();
  }

  /** Ô lọc vật tư riêng của từng PO (lọc PO nào chỉ áp cho bảng con của PO đó) */
  getMaterialFilter(po: AddPoItem): MaterialFilterValues {
    let f = this.materialFilters.get(po.id);
    if (!f) {
      f = {
        materialCode: "",
        materialName: "",
        partNumber: "",
        warehouseCode: "",
      };
      this.materialFilters.set(po.id, f);
    }
    return f;
  }

  /** Ô lọc lot riêng của từng vật tư */
  getLotFilter(m: AddMaterialItem): { lot: string } {
    let f = this.lotFilters.get(m.id);
    if (!f) {
      f = { lot: "" };
      this.lotFilters.set(m.id, f);
    }
    return f;
  }

  getFilteredMaterials(po: AddPoItem): AddMaterialItem[] {
    const f = this.getMaterialFilter(po);
    return (po.materials ?? []).filter((m) => {
      const includes = (val: string, q: string): boolean => {
        const term = (q ?? "").trim().toLowerCase();
        return !term || (val ?? "").toLowerCase().includes(term);
      };
      return (
        includes(m.materialCode, f.materialCode) &&
        includes(m.materialName, f.materialName) &&
        includes(m.partNumber, f.partNumber) &&
        includes(m.warehouseCode, f.warehouseCode)
      );
    });
  }

  // ---------- Phân trang bảng vật tư (trong PO) ----------
  getMaterialPageState(po: AddPoItem): MiniPageState {
    let st = this.materialPageStates.get(po.id);
    if (!st) {
      st = { pageIndex: 0, pageSize: this.nestedPageSizeOptions[0] };
      this.materialPageStates.set(po.id, st);
    }
    return st;
  }

  pagedMaterials(po: AddPoItem): AddMaterialItem[] {
    return slicePage(
      this.getFilteredMaterials(po),
      this.getMaterialPageState(po),
    );
  }

  onMaterialPage(po: AddPoItem, pageState: MiniPageState): void {
    this.materialPageStates.set(po.id, pageState);
  }

  /** Đổi ô lọc vật tư của 1 PO → bảng con của PO đó về trang đầu */
  onMaterialFilterChange(po: AddPoItem): void {
    this.materialPageStates.delete(po.id);
  }

  materialRowNo(po: AddPoItem, indexInPage: number): number {
    const st = this.getMaterialPageState(po);
    return st.pageIndex * st.pageSize + indexInPage + 1;
  }

  // ---------- Phân trang bảng lô (trong vật tư) ----------
  getLotPageState(m: AddMaterialItem): MiniPageState {
    let st = this.lotPageStates.get(m.id);
    if (!st) {
      st = { pageIndex: 0, pageSize: this.nestedPageSizeOptions[0] };
      this.lotPageStates.set(m.id, st);
    }
    return st;
  }

  pagedLots(m: AddMaterialItem): AddLotItem[] {
    return slicePage(this.getFilteredLots(m), this.getLotPageState(m));
  }

  onLotPage(m: AddMaterialItem, pageState: MiniPageState): void {
    this.lotPageStates.set(m.id, pageState);
  }

  /** Đổi ô lọc lot của 1 vật tư → bảng lô của vật tư đó về trang đầu */
  onLotFilterChange(m: AddMaterialItem): void {
    this.lotPageStates.delete(m.id);
  }

  getFilteredLots(material: AddMaterialItem): AddLotItem[] {
    const term = (this.getLotFilter(material).lot ?? "").trim().toLowerCase();
    if (!term) {
      return material.lots ?? [];
    }
    return (material.lots ?? []).filter((l) =>
      (l.lotNumber ?? "").toLowerCase().includes(term),
    );
  }

  togglePo(row: AddPoItem, event?: Event): void {
    event?.stopPropagation();
    if (this.expandedPoIds.has(row.id)) {
      this.expandedPoIds.delete(row.id);
    } else {
      this.expandedPoIds.add(row.id);
      this.loadPartNumbers(row);
    }
  }

  isPoExpanded(row: AddPoItem): boolean {
    return this.expandedPoIds.has(row.id);
  }

  toggleMaterial(m: AddMaterialItem, event?: Event): void {
    event?.stopPropagation();
    if (this.expandedMaterialIds.has(m.id)) {
      this.expandedMaterialIds.delete(m.id);
    } else {
      this.expandedMaterialIds.add(m.id);
    }
  }

  isMaterialExpanded(m: AddMaterialItem): boolean {
    return this.expandedMaterialIds.has(m.id);
  }

  poStatusLabel(status: AddPoItem["status"]): string {
    return status === "IMPORTING" ? "Đang nhập" : "Chờ nhập";
  }

  progressClass(progress: number): string {
    if (progress >= 100) {
      return "done";
    }
    if (progress > 0) {
      return "partial";
    }
    return "empty";
  }

  // ==================== ACTIONS ====================

  onComplete(): void {
    this.notificationService.info("Hoàn thành đơn (mock) — sẽ nối API sau.");
  }

  onManagePallet(): void {
    const dialogRef = this.dialog.open(CreatePalletDialogComponent, {
      width: "640px",
      maxWidth: "95vw",
      disableClose: true,
      data: {
        nextSequence: this.nextPalletSequence,
      } as CreatePalletDialogData,
      panelClass: "create-pallet-dialog-panel",
    });

    dialogRef
      .afterClosed()
      .subscribe((result: CreatePalletDialogResult | undefined) => {
        if (!result || result.created.length === 0) {
          return;
        }

        this.nextPalletSequence =
          result.sequenceStart + Math.max(1, result.quantity);

        if (result.saveAndPrint) {
          this.openPrintPalletDialog(result.created);
        }
      });
  }

  /** Nhập PO: chọn vật tư từ 1 hoặc nhiều PO → thêm vào bảng (trạng thái Chờ nhập) */
  /** ISO → dd/MM/yyyy HH:mm (giờ địa phương); không parse được thì trả nguyên */
  formatDateTime(value: string | null | undefined): string {
    const raw = toText(value);
    if (!raw) {
      return "";
    }
    const d = new Date(raw);
    if (Number.isNaN(d.getTime())) {
      return raw;
    }
    const pad = (n: number): string => String(n).padStart(2, "0");
    return `${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }

  onImportPo(): void {
    if (this.deliveryId === null) {
      this.notificationService.warning("Vui lòng Lưu đơn trước khi nhập PO.");
      return;
    }
    const existingKeys = this.dataSource.data.flatMap((po) =>
      po.materials.map((m) => `${po.poCode}|${m.materialCode}`),
    );
    this.dialog
      .open<PoImportDialogComponent, PoImportDialogData, PoImportSelection[]>(
        PoImportDialogComponent,
        {
          width: "98vw",
          maxWidth: "98vw",
          height: "94vh",
          maxHeight: "94vh",
          autoFocus: false,
          disableClose: true,
          data: {
            existingKeys,
            vendorCode: this.orderInfo.vendorCode,
            vendorName: this.resolveOrderVendorName(),
          },
        },
      )
      .afterClosed()
      .subscribe((selections) => {
        if (selections?.length) {
          this.saveImportedPos(selections);
        }
      });
  }

  onScan(): void {
    // id vật tư = sapPor1Id (khi vào từ chi tiết đơn)
    const parentItems = this.dataSource.data.flatMap((po) =>
      po.materials
        .filter((m) => m.id > 0)
        .map((m) => ({
          id: m.id,
          partNumber: m.partNumber,
          sapCode: m.materialCode,
          orderQty: m.poQuantity,
          materialName: m.materialName,
          poCode: po.poCode,
        })),
    );
    const existingReelIds = this.dataSource.data.flatMap((po) =>
      po.materials.flatMap((m) =>
        m.lots.flatMap((l) => (l.boxes ?? []).map((b) => toText(b.reelId))),
      ),
    );

    const isMobile = typeof window !== "undefined" && window.innerWidth <= 600;
    const dialogData: ScanImportDialogData = {
      mappingConfig: this.activeMappingConfig,
      poCode: this.dataSource.data[0]?.poCode ?? "",
      deliveryNotice: toText(this.orderInfo.deliveryNotice),
      vehicleNumber: toText(this.orderInfo.contNo),
      contractCode: toText(this.orderInfo.contractCode),
      vendorCode:
        (this.orderInfo.vendorCode ?? "").trim() ||
        (this.selectedScenario?.vendorCode ?? "").trim(),
      warehouse: this.orderInfo.warehouse,
      scenarioCode: this.selectedScenario?.vendorCode,
      parentItems,
      deliveryNotificationId: this.deliveryId,
      arrivalDate: this.orderInfo.arrivalDate,
      existingReelIds: existingReelIds.filter(Boolean),
      // Màn Scan desktop: panel Tổng hợp vật tư của cả đơn
      loadOrderPos:
        this.deliveryId !== null ? () => this.buildWorkspaceData() : undefined,
    };

    this.dialog
      .open(ScanImportDialogComponent, {
        // Desktop: màn Scan full màn (scan + kết quả + tổng hợp)
        width: "100vw",
        maxWidth: "100vw",
        height: isMobile ? "100vh" : "100vh",
        maxHeight: isMobile ? "100dvh" : "100vh",
        panelClass: isMobile
          ? ["scan-import-dialog-panel", "scan-import-dialog-panel--mobile"]
          : ["scan-import-dialog-panel", "scan-import-dialog-panel--workspace"],
        autoFocus: false,
        disableClose: true,
        data: dialogData,
      })
      .afterClosed()
      .subscribe((result) => {
        // Thùng đã lưu ngay khi scan → tải lại chi tiết đơn để cập nhật bảng
        if (this.deliveryId !== null) {
          this.loadDetail(this.deliveryId);
          return;
        }
        if (!result) {
          return;
        }
        const total =
          (result.boxRows?.length ?? 0) + (result.palletRows?.length ?? 0);
        if (!total) {
          return;
        }
        this.notificationService.success(
          `Đã xác nhận ${total} dòng từ Scan/Import.`,
        );
      });
  }

  onViewLot(lot: AddLotItem, material: AddMaterialItem): void {
    const poCode = this.dataSource.data.find((p) =>
      p.materials.some((m) => m.id === material.id),
    )?.poCode;
    const partNumber = lot.partNumber ?? material.partNumber;
    const boxRows: LotDetailDialogData["rows"] = (lot.boxes ?? []).map((b) => ({
      id: b.id,
      reelId: b.reelId ?? "",
      partNumber: partNumber || (b.partNumber ?? ""),
      vendor: b.vendor ?? "",
      lot: b.lot ?? lot.lotNumber,
      userData1: b.userData1 ?? "",
      userData2: b.userData2 ?? "",
      userData3: b.userData3 ?? "",
      userData4: b.userData4 ?? "",
      userData5: b.userData5 ?? "",
      initialQuantity: b.initialQuantity ?? 0,
      msl: b.msdLevel ?? "",
      // Vị trí kho của thùng
      storageUnit: boxLocation(b),
      manufacturingDate: this.toDateText(b.manufacturingDate),
      expirationDate: this.toDateText(b.expirationDate),
      sapCode: b.sapCode ?? material.materialCode,
      sapName: material.materialName,
      vendorQrCode: b.vendorQrCode ?? "",
      status: b.status ?? "",
      createdBy: b.createdBy ?? "",
      createdAt: b.createdAt ?? "",
      updatedBy: b.updatedBy ?? "",
      poDetailId: material.id,
      importVendorTemTransactionsId: this.deliveryId ?? this.mockTransactionId,
      // Bản ghi thùng gốc → dialog lưu bằng PUT /vendor-label-infos/{id}
      record: b,
    }));
    const data: LotDetailDialogData = {
      partNumber,
      manufacturingDate: lot.manufacturingDate,
      poCode,
      rows: boxRows.length
        ? boxRows
        : [
            {
              id: lot.id,
              reelId: lot.reelId ?? "",
              partNumber: lot.partNumber ?? material.partNumber,
              vendor: lot.vendor ?? this.orderInfo.vendorCode,
              lot: lot.lotNumber,
              userData1: "",
              userData2: "",
              userData3: "",
              userData4: "",
              userData5: "",
              initialQuantity: lot.quantity,
              msl: lot.msl ?? "",
              storageUnit: lot.warehouseCode,
              manufacturingDate: lot.manufacturingDate,
              expirationDate: lot.expirationDate,
              sapCode: material.materialCode,
              sapName: material.materialName,
              poDetailId: material.id,
              importVendorTemTransactionsId: this.mockTransactionId,
            },
          ],
    };
    this.dialog
      .open(LotDetailDialogComponent, {
        width: "98vw",
        maxWidth: "98vw",
        height: "92vh",
        maxHeight: "92vh",
        panelClass: "lot-detail-dialog-panel",
        autoFocus: false,
        data,
      })
      .afterClosed()
      .subscribe((result: unknown) => {
        // Đã lưu thùng → tải lại chi tiết đơn
        if (result && this.deliveryId !== null) {
          this.loadDetail(this.deliveryId);
        }
      });
  }

  onSendWMS(row: AddPoItem, event?: Event): void {
    this.notificationService.success("Đang phát triển");
  }

  /** Gửi PanaCIM các thùng chưa gửi của PO (giống receiving-supplies: CSV → /api/csv-upload) */
  onUploadPanaCIM(row: AddPoItem, event?: Event): void {
    event?.stopPropagation();
    if (this.isSendingPanacim) {
      return;
    }
    // Chỉ thùng panaSendStatus khác true; thùng đã gửi bỏ qua
    const all = this.collectSendEntries(row, () => true);
    const entries = all.filter((e) => !isSendFlagOn(e.record.panaSendStatus));
    if (!entries.length) {
      this.notificationService.warning(
        all.length
          ? `Tất cả ${all.length} thùng của PO ${row.poCode} đã gửi PanaCIM.`
          : "PO chưa có thùng nào được scan.",
      );
      return;
    }
    openSendConfirm(
      this.dialog,
      "PanaCIM",
      row.poCode,
      entries,
      all.length - entries.length,
    ).subscribe((ok) => {
      if (!ok) {
        return;
      }
      // TẠM TẮT gửi thật
      this.isSendingPanacim = true;
      this.vendorLabelSendService.sendPanacim(entries, row.poCode).subscribe({
        next: (res) => {
          this.isSendingPanacim = false;
          this.notifySendResult(res.count, res.statusSaved, "PanaCIM");
          this.reloadDetail();
        },
        error: (err: unknown) => {
          this.isSendingPanacim = false;
          this.notificationService.error(
            resolveHttpErrorMessage(err, "Gửi PanaCIM thất bại."),
          );
        },
      });
      // const csv = this.vendorLabelSendService.buildPanacimCsv(
      //   entries,
      //   row.poCode,
      // );
      // this.openPayloadPreview({
      //   title: "Payload gửi PanaCIM (xem trước — chưa gửi)",
      //   endpoint: 'POST /api/csv-upload (multipart/form-data, field "file")',
      //   note: `File: ${csv.fileName} · ${csv.rowCount} dòng (thùng)`,
      //   content: csv.content.replace(/^\ufeff/, ""),
      // });
    });
  }
  /** Mở Tổng hợp vật tư cho các thùng chưa có PO → bổ sung PO → thùng chuyển vào PO tương ứng */
  onOpenUnassigned(): void {
    if (!this.unassignedBoxes.length) {
      return;
    }
    const pseudoPo = this.buildUnassignedPo(this.unassignedBoxes);
    const poLines = this.buildPoLines(this.dataSource.data);
    this.dialog
      .open(MaterialSummaryDialogComponent, {
        width: "98vw",
        maxWidth: "98vw",
        height: "92vh",
        maxHeight: "92vh",
        panelClass: "material-summary-dialog-panel",
        autoFocus: false,
        disableClose: true,
        data: {
          po: pseudoPo,
          vendorCode: this.orderInfo.vendorCode,
          transactionId: this.mockTransactionId,
          unassigned: { poLines },
        } as MaterialSummaryDialogData,
      })
      .afterClosed()
      .subscribe((result: { updated?: number } | null | undefined) => {
        if (result?.updated && this.deliveryId !== null) {
          this.loadDetail(this.deliveryId);
        }
      });
  }

  onEditInfo(row: AddPoItem, event?: Event): void {
    event?.stopPropagation();
    this.dialog
      .open(MaterialSummaryDialogComponent, {
        width: "98vw",
        maxWidth: "98vw",
        height: "92vh",
        maxHeight: "92vh",
        panelClass: "material-summary-dialog-panel",
        autoFocus: false,
        disableClose: true,
        data: {
          po: row,
          vendorCode: this.orderInfo.vendorCode || row.vendorCode,
          transactionId: this.mockTransactionId,
        } as MaterialSummaryDialogData,
      })
      .afterClosed()
      .subscribe((result: { updated?: number } | null | undefined) => {
        // Đã PUT cập nhật thùng → tải lại chi tiết đơn
        if (result?.updated && this.deliveryId !== null) {
          this.loadDetail(this.deliveryId);
        }
      });
  }

  /** Gửi SAP các thùng chưa gửi của PO (giống receiving-supplies: post-goods-receipt-po) */
  onSendSAP(row: AddPoItem, event?: Event): void {
    event?.stopPropagation();
    if (this.isSendingSap) {
      return;
    }
    // Chỉ thùng sapSendStatus khác true; thùng đã gửi bỏ qua
    const all = this.collectSendEntries(row, () => true);
    const entries = all.filter((e) => !isSendFlagOn(e.record.sapSendStatus));
    if (!entries.length) {
      this.notificationService.warning(
        all.length
          ? `Tất cả ${all.length} thùng của PO ${row.poCode} đã gửi SAP.`
          : "PO chưa có thùng nào được scan.",
      );
      return;
    }
    openSendConfirm(
      this.dialog,
      "SAP",
      row.poCode,
      entries,
      all.length - entries.length,
    ).subscribe((ok) => {
      if (!ok) {
        return;
      }
      // TẠM TẮT gửi thật
      this.isSendingSap = true;
      this.vendorLabelSendService.sendSap(entries).subscribe({
        next: (res) => {
          this.isSendingSap = false;
          this.notifySendResult(res.count, res.statusSaved, "SAP");
          this.reloadDetail();
        },
        error: (err: unknown) => {
          this.isSendingSap = false;
          this.notificationService.error(
            resolveHttpErrorMessage(err, "Gửi SAP thất bại."),
          );
        },
      });
      // gửi test payload
      // this.isSendingSap = true;
      // this.vendorLabelSendService.buildSapPayload(entries).subscribe({
      //   next: (payload) => {
      //     this.isSendingSap = false;
      //     this.openPayloadPreview({
      //       title: "Payload gửi SAP (xem trước — chưa gửi)",
      //       endpoint: "POST /api/post-goods-receipt-po",
      //       note: `${payload.OPDN.length} dòng OPDN (thùng) · PO ${row.poCode}`,
      //       content: JSON.stringify(payload, null, 2),
      //     });
      //   },
      //   error: (err: unknown) => {
      //     this.isSendingSap = false;
      //     this.notificationService.error(
      //       resolveHttpErrorMessage(err, "Không dựng được payload SAP."),
      //     );
      //   },
      // });
    });
  }

  /** Expose mapping helper for potential future QR parse outside dialog. */
  parseQr(raw: string): Record<string, string> | null {
    if (!this.activeMappingConfig) {
      return null;
    }
    return parseVendorQrByMappingConfig(raw, this.activeMappingConfig);
  }
  private openPrintPalletDialog(pallets: PalletItem[]): void {
    this.dialog.open(PrintPalletDialogComponent, {
      width: "980px",
      maxWidth: "96vw",
      maxHeight: "92vh",
      disableClose: true,
      panelClass: "print-pallet-dialog-panel",
      data: {
        pallets,
        preselectedIds: pallets.map((p) => p.id),
      } as PrintPalletDialogData,
    });
  }
  private loadVendors(): void {
    this.isLoadingVendors = true;
    this.managerTemNccService.getSapOcrds().subscribe({
      next: (data) => {
        this.vendorOptions = data;
        this.applyOrderVendor();
        this.filteredVendorOptions = data.slice(0, this.VENDOR_DISPLAY_LIMIT);
        this.isLoadingVendors = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoadingVendors = false;
      },
    });
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

  private buildPoFilterPredicate() {
    return (item: AddPoItem, filterJson: string): boolean => {
      let f: PoFilterValues;
      try {
        f = JSON.parse(filterJson || "{}");
      } catch {
        return true;
      }
      const includes = (value: string | number, query: string): boolean => {
        const term = (query ?? "").trim().toLowerCase();
        return (
          !term ||
          String(value ?? "")
            .toLowerCase()
            .includes(term)
        );
      };
      return (
        includes(item.poCode, f.poCode) &&
        includes(item.warehouseKeeper, f.warehouseKeeper) &&
        includes(item.vehicleNumber, f.vehicleNumber) &&
        includes(item.contractCode, f.contractCode) &&
        includes(this.poStatusLabel(item.status), f.status)
      );
    };
  }

  /** Thùng của PO (có id) thỏa điều kiện → entry gửi SAP / PanaCIM */
  private collectSendEntries(
    po: AddPoItem,
    predicate: (box: VendorLabelInfoDto) => boolean,
  ): SendBoxEntry[] {
    return po.materials.flatMap((m) =>
      m.lots.flatMap((lot) =>
        (lot.boxes ?? [])
          .filter((b) => b.id && predicate(b))
          .map((b) => ({
            record: b,
            sapCode: m.materialCode,
            partNumber: m.partNumber,
            poCode: po.poCode,
            vendorCode: this.orderInfo.vendorCode,
          })),
      ),
    );
  }

  /** Hiển thị payload sẽ gửi (chế độ kiểm tra) */
  private openPayloadPreview(data: PayloadPreviewDialogData): void {
    this.dialog.open(PayloadPreviewDialogComponent, {
      width: "900px",
      maxWidth: "96vw",
      autoFocus: false,
      data,
    });
  }

  private notifySendResult(
    count: number,
    statusSaved: boolean,
    target: string,
  ): void {
    if (statusSaved) {
      this.notificationService.success(
        `Đã gửi ${target} thành công (${count} thùng).`,
      );
    } else {
      this.notificationService.warning(
        `Đã gửi ${target} (${count} thùng) nhưng lưu trạng thái đã gửi thất bại.`,
      );
    }
  }

  private reloadDetail(): void {
    if (this.deliveryId !== null) {
      this.loadDetail(this.deliveryId);
    }
  }

  /**
   * Lưu vật tư đã chọn: POST /sap-por-1-r-1-s/batch (mỗi vật tư 1 dòng PO, gắn deliveryNotificationId)
   * → thành công thì tải lại chi tiết đơn để bảng hiện dữ liệu thật.
   */
  private saveImportedPos(selections: PoImportSelection[]): void {
    const deliveryId = this.deliveryId;
    if (deliveryId === null) {
      return;
    }
    const items: SapPor1BatchItem[] = selections.flatMap((sel) =>
      sel.rows.map((r) => ({
        ...r.payload,
        // Mã kho có thể đã sửa trong dialog
        whsCode: toText(r.whsCode) || r.payload.whsCode,
        deliveryNotificationId: deliveryId,
      })),
    );
    if (!items.length) {
      return;
    }
    this.isLoadingDetail = true;
    this.infoTemNccService.createSapPor1Batch(items).subscribe({
      next: () => {
        this.notificationService.success(
          `Đã thêm ${items.length} vật tư từ ${selections.length} PO vào đơn.`,
        );
        this.loadDetail(deliveryId, true);
      },
      error: () => {
        this.isLoadingDetail = false;
        this.notificationService.error("Lưu vật tư PO vào đơn thất bại.");
      },
    });
  }

  /** Tên NCC của đơn: theo danh sách NCC SAP (orderInfo.vendorName có thể chỉ là mã) */
  private resolveOrderVendorName(): string {
    const code = toText(this.orderInfo.vendorCode).toLowerCase();
    const found = this.vendorOptions.find(
      (v) => toText(v.cardCode).toLowerCase() === code,
    );
    const name = toText(found?.cardName) || toText(this.orderInfo.vendorName);
    return name.toLowerCase() === code ? "" : name;
  }

  private updateOrder(id: number): void {
    const vendor =
      (this.orderInfo.vendorCode ?? "").trim() ||
      (this.orderInfo.vendorName ?? "").trim();
    if (!vendor) {
      this.notificationService.warning("Vui lòng chọn Nhà cung cấp.");
      return;
    }
    const base = this.loadedOrder;
    const detailBase = base as DeliveryNotificationDetailDto | null;
    const arrival = this.orderInfo.arrivalDate;
    const payload: DeliveryNotificationDto = {
      id,
      deliveryNotificationCode: (this.orderInfo.deliveryNotice ?? "").trim(),
      invoiceNumber: (this.orderInfo.invoiceNumber ?? "").trim(),
      contractCode: (this.orderInfo.contractCode ?? "").trim(),
      // vendorCode: this.orderVendorCode(),
      vendorName: this.orderVendorName(),
      contNo: (this.orderInfo.contNo ?? "").trim(),
      entryDate:
        arrival instanceof Date && !isNaN(arrival.getTime())
          ? arrival.toISOString()
          : (base?.entryDate ?? null),
      numberOfPo: base?.numberOfPo ?? null,
      numberOfItem: base?.numberOfItem ?? null,
      ...(detailBase?.sapPor1R1List ? this.orderCounts(detailBase) : {}),
      status: base?.status ?? "New",
      source: base?.source ?? DELIVERY_SOURCE_SYSTEM,
      deletedAt: base?.deletedAt ?? null,
      deletedBy: base?.deletedBy ?? null,
      createdBy: base?.createdBy ?? null,
      createdAt: base?.createdAt ?? null,
    };
    this.isSavingOrder = true;
    this.infoTemNccService.updateDeliveryNotification(payload).subscribe({
      next: (saved) => {
        this.isSavingOrder = false;
        this.loadedOrder = { ...payload, ...(saved ?? {}) };
        this.notificationService.success("Cập nhật đơn thành công.");
      },
      error: () => {
        this.isSavingOrder = false;
        this.notificationService.error("Cập nhật đơn thất bại.");
      },
    });
  }

  /** Mã NCC gửi đi: mã đã chọn; nếu ô mã đang chứa tên thì tra lại theo danh sách NCC */
  private orderVendorCode(): string {
    const value = toText(this.orderInfo.vendorCode);
    const found =
      this.findVendor(value) ?? this.findVendor(this.orderInfo.vendorName);
    return toText(found?.cardCode) || value;
  }

  /** Tên NCC gửi đi: tên theo danh sách NCC SAP, không có thì tên đang hiển thị */
  private orderVendorName(): string {
    const found =
      this.findVendor(this.orderInfo.vendorCode) ??
      this.findVendor(this.orderInfo.vendorName);
    return (
      toText(found?.cardName) ||
      toText(this.orderInfo.vendorName) ||
      this.orderVendorCode()
    );
  }

  /** Tìm NCC SAP theo mã hoặc theo tên (không phân biệt hoa thường) */
  private findVendor(value: unknown): SapOcrd | undefined {
    const v = toText(value).toLowerCase();
    if (!v) {
      return undefined;
    }
    return (
      this.vendorOptions.find((o) => toText(o.cardCode).toLowerCase() === v) ??
      this.vendorOptions.find((o) => toText(o.cardName).toLowerCase() === v)
    );
  }

  /**
   * Đổ NCC của đơn vào form + bảng PO: ưu tiên vendorCode; đơn cũ lưu mã trong vendorName,
   * đơn mới lưu tên → tra danh sách NCC SAP để ra đúng cặp mã / tên.
   */
  private applyOrderVendor(): void {
    const raw = this.rawOrderVendor;
    if (!raw) {
      return;
    }
    const found = this.findVendor(raw.code) ?? this.findVendor(raw.name);
    this.orderInfo.vendorCode = toText(found?.cardCode) || raw.code || raw.name;
    this.orderInfo.vendorName = toText(found?.cardName) || raw.name;
    for (const po of this.dataSource.data) {
      po.vendorCode = this.orderInfo.vendorCode;
      po.vendorName = this.orderInfo.vendorName;
    }
    this.cdr.markForCheck();
  }

  /** Thùng của đơn không thuộc dòng PO nào (sapPor1Id rỗng hoặc ngoài đơn) */
  private loadUnassignedBoxes(
    deliveryId: number,
    detail: DeliveryNotificationDetailDto,
  ): void {
    const lineIds = new Set((detail.sapPor1R1List ?? []).map((l) => l.id));
    this.infoTemNccService.getVendorLabelInfosByDelivery(deliveryId).subscribe({
      next: (boxes) => {
        this.unassignedBoxes = boxes.filter(
          (b) =>
            b.sapPor1Id === null ||
            b.sapPor1Id === undefined ||
            !lineIds.has(b.sapPor1Id),
        );
        this.unassignedMaterialCount = new Set(
          this.unassignedBoxes.map(
            (b) => toText(b.sapCode) || toText(b.partNumber),
          ),
        ).size;
        this.cdr.markForCheck();
      },
      error: () => {
        this.unassignedBoxes = [];
        this.unassignedMaterialCount = 0;
      },
    });
  }

  /** Dữ liệu cả đơn cho panel Tổng hợp trong màn Scan: detail (PO / vật tư / thùng) + thùng chưa có PO */
  private buildWorkspaceData(): Observable<unknown> {
    const id = this.deliveryId;
    if (id === null) {
      return of(null);
    }
    return forkJoin({
      detail: this.infoTemNccService.getDeliveryNotificationDetail(id),
      boxes: this.infoTemNccService.getVendorLabelInfosByDelivery(id),
    }).pipe(
      map(({ detail, boxes }) => {
        const pos = this.mapPos(detail);
        const lineIds = new Set((detail.sapPor1R1List ?? []).map((l) => l.id));
        const orphans = boxes.filter(
          (b) =>
            b.sapPor1Id === null ||
            b.sapPor1Id === undefined ||
            !lineIds.has(b.sapPor1Id),
        );
        const data: OrderWorkspaceData = {
          pos,
          unassigned: orphans.length ? this.buildUnassignedPo(orphans) : null,
          poLines: this.buildPoLines(pos),
          vendorCode: this.orderInfo.vendorCode,
          transactionId: this.mockTransactionId,
        };
        return data;
      }),
    );
  }

  /** Thùng chưa có PO → PO giả "Chưa có PO" (gom theo mã SAP, dùng lại mapMaterial) */
  private buildUnassignedPo(boxesIn: VendorLabelInfoDto[]): AddPoItem {
    // Gom thùng theo mã SAP → mỗi mã 1 "vật tư" (dựng như dòng PO giả để dùng lại mapMaterial)
    const bySap = new Map<string, VendorLabelInfoDto[]>();
    for (const b of boxesIn) {
      const key = toText(b.sapCode) || toText(b.partNumber) || "—";
      bySap.set(key, [...(bySap.get(key) ?? []), b]);
    }
    let seq = 0;
    const materials: AddMaterialItem[] = [];
    bySap.forEach((boxes, sapCode) => {
      seq -= 1;
      const m = this.mapMaterial({
        id: seq,
        lineNum: null,
        itemCode: sapCode,
        dscription: "",
        quantity: 0,
        whsCode: null,
        unitMsr: null,
        price: null,
        currency: null,
        docEntry: null,
        vendorLabelInfoList: boxes,
      });
      m.partNumber = this.distinctJoin(boxes.map((b) => b.partNumber));
      materials.push(m);
    });
    return {
      id: -1,
      poCode: "Hàng chờ vật tư",
      warehouseKeeper: "",
      vendorCode: this.orderInfo.vendorCode,
      vendorName: this.orderInfo.vendorName,
      vehicleNumber: this.orderInfo.contNo,
      invoiceNumber: this.orderInfo.invoiceNumber,
      contractCode: this.orderInfo.contractCode,
      importDate: "",
      importBatch: null,
      materialTypeCount: materials.length,
      totalQuantity: 0,
      status: "WAITING",
      materials,
    };
  }

  /** Dòng vật tư của đơn (sapPor1R1) để gán thùng chưa có PO */
  private buildPoLines(pos: AddPoItem[]): UnassignedPoLine[] {
    return pos.flatMap((po) =>
      po.materials
        .filter((m) => m.id > 0)
        .map((m) => ({
          id: m.id,
          poCode: po.poCode,
          sapCode: m.materialCode,
          partNumber: m.partNumber,
        })),
    );
  }

  // ==================== DETAIL (API) ====================

  /** Số PO (theo docEntry) và số vật tư (dòng sapPor1R1) hiện có trong đơn */
  private orderCounts(
    detail: DeliveryNotificationDetailDto,
  ): Pick<DeliveryNotificationDto, "numberOfPo" | "numberOfItem"> {
    const lines = detail.sapPor1R1List ?? [];
    return {
      numberOfPo: new Set(lines.map((l) => toText(l.docEntry))).size,
      numberOfItem: lines.length,
    };
  }

  /**
   * PUT /delivery-notifications/{id} để thông tin đơn ở list khớp với PO/vật tư thực tế.
   * Bỏ qua nếu số liệu đã đúng.
   */
  private syncOrderCounts(detail: DeliveryNotificationDetailDto): void {
    const counts = this.orderCounts(detail);
    if (
      detail.numberOfPo === counts.numberOfPo &&
      detail.numberOfItem === counts.numberOfItem
    ) {
      return;
    }
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const { sapPor1R1List, ...order } = detail;
    const payload: DeliveryNotificationDto = { ...order, ...counts };
    this.infoTemNccService.updateDeliveryNotification(payload).subscribe({
      next: (saved) => {
        this.loadedOrder = { ...detail, ...payload, ...(saved ?? {}) };
      },
      error: () => {
        this.notificationService.warning(
          "Đã lưu vật tư nhưng cập nhật số PO/vật tư của đơn thất bại.",
        );
      },
    });
  }

  private loadDetail(id: number, syncCounts = false): void {
    this.isLoadingDetail = true;
    this.infoTemNccService.getDeliveryNotificationDetail(id).subscribe({
      next: (detail) => {
        this.loadedOrder = detail;
        this.orderInfo.deliveryNotice = detail.deliveryNotificationCode ?? "";
        this.rawOrderVendor = {
          code: toText(detail.vendorCode),
          name: toText(detail.vendorName),
        };
        this.applyOrderVendor();
        this.orderInfo.arrivalDate = detail.entryDate
          ? new Date(detail.entryDate)
          : null;
        this.orderInfo.invoiceNumber = detail.invoiceNumber ?? "";
        this.orderInfo.contractCode = detail.contractCode ?? "";
        this.orderInfo.contNo = detail.contNo ?? "";

        this.expandedPoIds.clear();
        this.expandedMaterialIds.clear();
        this.partLoadedPoIds.clear();
        this.materialPageStates.clear();
        this.lotPageStates.clear();
        this.materialFilters.clear();
        this.lotFilters.clear();
        this.dataSource.data = this.mapPos(detail);
        this.loadUnassignedBoxes(id, detail);
        if (syncCounts) {
          this.syncOrderCounts(detail);
        }
        this.isLoadingDetail = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.dataSource.data = [];
        this.isLoadingDetail = false;
        this.notificationService.error(
          "Không tải được chi tiết thông báo giao hàng.",
        );
      },
    });
  }

  /** Gom sapPor1R1List theo docEntry (mã PO) */
  private mapPos(detail: DeliveryNotificationDetailDto): AddPoItem[] {
    const groups = new Map<string, SapPor1R1Dto[]>();
    for (const line of detail.sapPor1R1List ?? []) {
      const key = toText(line.docEntry);
      const list = groups.get(key);
      if (list) {
        list.push(line);
      } else {
        groups.set(key, [line]);
      }
    }

    const pos: AddPoItem[] = [];
    let poSeq = 0;
    groups.forEach((lines, docEntry) => {
      const materials = lines.map((l) => this.mapMaterial(l));
      const received = materials.reduce((s, m) => s + m.receivedQuantity, 0);
      pos.push({
        id: ++poSeq,
        poCode: docEntry || "—",
        warehouseKeeper: this.distinctJoin(lines.map((l) => l.whsCode)),
        vendorCode: this.orderInfo.vendorCode,
        vendorName: this.orderInfo.vendorName,
        vehicleNumber: detail.contNo ?? "",
        invoiceNumber: detail.invoiceNumber ?? "",
        contractCode: detail.contractCode ?? "",
        importDate: toText(detail.entryDate),
        importBatch: null,
        materialTypeCount: new Set(lines.map((l) => l.itemCode ?? "")).size,
        totalQuantity: materials.reduce((s, m) => s + m.poQuantity, 0),
        status: received > 0 ? "IMPORTING" : "WAITING",
        materials,
      });
    });
    return pos;
  }

  /** 1 dòng sapPor1R1 = 1 vật tư; vendorLabelInfoList gom theo lot, mỗi bản ghi = 1 thùng */
  private mapMaterial(l: SapPor1R1Dto): AddMaterialItem {
    const boxes = l.vendorLabelInfoList ?? [];
    const poQuantity = Number(l.quantity ?? 0);
    const sumQty = (list: VendorLabelInfoDto[]): number =>
      list.reduce((s, b) => s + Number(b.initialQuantity ?? 0), 0);
    const receivedQuantity = sumQty(boxes);
    const sentQuantity = sumQty(
      boxes.filter((b) => this.isSent(b.sapSendStatus)),
    );
    const percent = (v: number): number =>
      poQuantity ? Math.min(100, Math.round((v / poQuantity) * 100)) : 0;

    const byLot = new Map<string, VendorLabelInfoDto[]>();
    for (const b of boxes) {
      const key = toText(b.lot);
      const list = byLot.get(key);
      if (list) {
        list.push(b);
      } else {
        byLot.set(key, [b]);
      }
    }
    const lots: AddLotItem[] = [];
    byLot.forEach((lotBoxes, lotNumber) => {
      lots.push({
        id: lotBoxes[0].id,
        lotNumber: lotNumber || "—",
        // Mã kho SAP = whsCode của dòng vật tư trong PO
        warehouseCode: l.whsCode ?? "",
        quantity: sumQty(lotBoxes),
        boxCount: lotBoxes.length,
        manufacturingDate: this.distinctJoin(
          lotBoxes.map((b) => this.toDateText(b.manufacturingDate)),
        ),
        expirationDate: this.distinctJoin(
          lotBoxes.map((b) => this.toDateText(b.expirationDate)),
        ),
        vendor: lotBoxes[0].vendor ?? undefined,
        msl: lotBoxes[0].msdLevel ?? undefined,
        boxes: lotBoxes,
      });
    });

    return {
      id: l.id,
      materialCode: l.itemCode ?? "",
      materialName: l.dscription ?? "",
      partNumber: "", // lấy từ API OITM theo mã SAP khi expand PO
      warehouseCode: l.whsCode ?? "",
      lotCount: lots.length,
      poQuantity,
      receivedQuantity,
      palletCount: new Set(
        boxes.map((b) => toText(b.serialPallet)).filter(Boolean),
      ).size,
      boxCount: boxes.length,
      importedBy: this.distinctJoin(boxes.map((b) => b.createdBy)),
      scannedProgress: percent(receivedQuantity),
      sentProgress: percent(sentQuantity),
      lots,
    };
  }

  /** Gọi khi expand PO: mỗi mã SAP gọi API part-numbers 1 lần (service có cache) */
  private loadPartNumbers(po: AddPoItem): void {
    if (this.deliveryId === null || this.partLoadedPoIds.has(po.id)) {
      return;
    }
    this.partLoadedPoIds.add(po.id);
    const bySapCode = new Map<string, AddMaterialItem[]>();
    for (const m of po.materials) {
      const code = toText(m.materialCode);
      if (!code) {
        continue;
      }
      const list = bySapCode.get(code);
      if (list) {
        list.push(m);
      } else {
        bySapCode.set(code, [m]);
      }
    }
    bySapCode.forEach((materials, code) => {
      this.infoTemNccService
        .getPartNumbersBySapCode(code)
        .subscribe((parts) => {
          const partNumber = parts.join(", ");
          for (const m of materials) {
            m.partNumber = partNumber;
            for (const lot of m.lots) {
              lot.partNumber = partNumber;
            }
          }
          this.cdr.markForCheck();
        });
    });
  }

  private toDateText(value: unknown): string {
    return toText(value).slice(0, 10);
  }

  /** sapSendStatus có thể là string/boolean/number — rỗng, false, 0 = chưa gửi */
  private isSent(value: unknown): boolean {
    const t = toText(value).toLowerCase();
    return t !== "" && t !== "false" && t !== "0";
  }

  private distinctJoin(values: unknown[]): string {
    const set = new Set<string>();
    for (const v of values) {
      const t = toText(v);
      if (t) {
        set.add(t);
      }
    }
    return Array.from(set).join(", ");
  }
}
