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
import { ActivatedRoute } from "@angular/router";
import { Observable, take } from "rxjs";
import { map } from "rxjs/operators";
import {
  DeliveryNotificationDetailDto,
  InfoTemNccService,
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
} from "./material-summary-dialog/material-summary-dialog.component";
import { ScanImportDialogComponent } from "./scan-import-dialog/scan-import-dialog.component";
import { ScanImportDialogData } from "./scan-import-dialog/scan-import.models";

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
    deliveryNotice: "TB-2026-0715-01",
    vendorCode: "",
    vendorName: "",
    arrivalDate: null as Date | null,
    invoiceNumber: "",
    contractCode: "",
    importScenario: "",
    warehouse: "",
    approver: "",
  };

  poFilter: PoFilterValues = {
    poCode: "",
    warehouseKeeper: "",
    vehicleNumber: "",
    contractCode: "",
    status: "",
  };

  materialFilter: MaterialFilterValues = {
    materialCode: "",
    materialName: "",
    partNumber: "",
    warehouseCode: "",
  };

  lotFilter = "";

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
  /** PO đã gọi lấy part-numbers (chỉ gọi khi expand PO lần đầu) */
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  private partLoadedPoIds = new Set<number>();
  /** Phân trang: vật tư theo PO (key = id PO), lô theo vật tư (key = id vật tư) */
  private materialPageStates = new Map<number, MiniPageState>();
  private lotPageStates = new Map<number, MiniPageState>();

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

  getFilteredMaterials(po: AddPoItem): AddMaterialItem[] {
    const f = this.materialFilter;
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

  /** Đổi ô lọc vật tư → về trang đầu */
  onMaterialFilterChange(): void {
    this.materialPageStates.clear();
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

  onLotFilterChange(): void {
    this.lotPageStates.clear();
  }

  getFilteredLots(material: AddMaterialItem): AddLotItem[] {
    const term = (this.lotFilter ?? "").trim().toLowerCase();
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
        this.notificationService.success(
          `Đã tạo ${result.created.length} pallet.`,
        );

        if (result.saveAndPrint) {
          this.openPrintPalletDialog(result.created);
        }
      });
  }

  onScan(): void {
    if (!this.selectedScenario || !this.activeMappingConfig) {
      this.notificationService.warning(
        "Chưa chọn kịch bản nhập TEM — vẫn mở Scan/Import (nhận diện QR sẽ hạn chế).",
      );
    }

    // id vật tư = sapPor1Id (khi vào từ chi tiết đơn)
    const parentItems = this.dataSource.data.flatMap((po) =>
      po.materials.map((m) => ({
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
      vendorCode:
        (this.orderInfo.vendorCode ?? "").trim() ||
        (this.selectedScenario?.vendorCode ?? "").trim(),
      warehouse: this.orderInfo.warehouse,
      scenarioCode: this.selectedScenario?.vendorCode,
      parentItems,
      deliveryNotificationId: this.deliveryId,
      arrivalDate: this.orderInfo.arrivalDate,
      existingReelIds: existingReelIds.filter(Boolean),
    };

    this.dialog
      .open(ScanImportDialogComponent, {
        width: isMobile ? "100vw" : "96vw",
        maxWidth: isMobile ? "100vw" : "1400px",
        height: isMobile ? "100vh" : "90vh",
        maxHeight: isMobile ? "100dvh" : "92vh",
        panelClass: isMobile
          ? ["scan-import-dialog-panel", "scan-import-dialog-panel--mobile"]
          : ["scan-import-dialog-panel"],
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
      storageUnit: b.storageUnit ?? "",
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
    this.dialog.open(LotDetailDialogComponent, {
      width: "98vw",
      maxWidth: "98vw",
      height: "92vh",
      maxHeight: "92vh",
      panelClass: "lot-detail-dialog-panel",
      autoFocus: false,
      data,
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
      // this.isSendingPanacim = true;
      // this.vendorLabelSendService.sendPanacim(entries, row.poCode).subscribe({
      //   next: (res) => {
      //     this.isSendingPanacim = false;
      //     this.notifySendResult(res.count, res.statusSaved, "PanaCIM");
      //     this.reloadDetail();
      //   },
      //   error: (err: unknown) => {
      //     this.isSendingPanacim = false;
      //     this.notificationService.error(
      //       resolveHttpErrorMessage(err, "Gửi PanaCIM thất bại."),
      //     );
      //   },
      // });
      const csv = this.vendorLabelSendService.buildPanacimCsv(
        entries,
        row.poCode,
      );
      this.openPayloadPreview({
        title: "Payload gửi PanaCIM (xem trước — chưa gửi)",
        endpoint: 'POST /api/csv-upload (multipart/form-data, field "file")',
        note: `File: ${csv.fileName} · ${csv.rowCount} dòng (thùng)`,
        content: csv.content.replace(/^\ufeff/, ""),
      });
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
      // this.isSendingSap = true;
      // this.vendorLabelSendService.sendSap(entries).subscribe({
      //   next: (res) => {
      //     this.isSendingSap = false;
      //     this.notifySendResult(res.count, res.statusSaved, "SAP");
      //     this.reloadDetail();
      //   },
      //   error: (err: unknown) => {
      //     this.isSendingSap = false;
      //     this.notificationService.error(
      //       resolveHttpErrorMessage(err, "Gửi SAP thất bại."),
      //     );
      //   },
      // });
      // gửi test payload
      this.isSendingSap = true;
      this.vendorLabelSendService.buildSapPayload(entries).subscribe({
        next: (payload) => {
          this.isSendingSap = false;
          this.openPayloadPreview({
            title: "Payload gửi SAP (xem trước — chưa gửi)",
            endpoint: "POST /api/post-goods-receipt-po",
            note: `${payload.OPDN.length} dòng OPDN (thùng) · PO ${row.poCode}`,
            content: JSON.stringify(payload, null, 2),
          });
        },
        error: (err: unknown) => {
          this.isSendingSap = false;
          this.notificationService.error(
            resolveHttpErrorMessage(err, "Không dựng được payload SAP."),
          );
        },
      });
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

  // ==================== DETAIL (API) ====================

  private loadDetail(id: number): void {
    this.isLoadingDetail = true;
    this.infoTemNccService.getDeliveryNotificationDetail(id).subscribe({
      next: (detail) => {
        this.orderInfo.deliveryNotice = detail.deliveryNotificationCode ?? "";
        this.orderInfo.vendorCode = detail.vendorName ?? "";
        this.orderInfo.vendorName = detail.vendorName ?? "";
        this.orderInfo.arrivalDate = detail.entryDate
          ? new Date(detail.entryDate)
          : null;
        this.orderInfo.invoiceNumber = detail.invoiceNumber ?? "";
        this.orderInfo.contractCode = detail.contractCode ?? "";

        this.expandedPoIds.clear();
        this.expandedMaterialIds.clear();
        this.partLoadedPoIds.clear();
        this.materialPageStates.clear();
        this.lotPageStates.clear();
        this.dataSource.data = this.mapPos(detail);
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
        vendorCode: detail.vendorName ?? "",
        vendorName: detail.vendorName ?? "",
        vehicleNumber: detail.contNo ?? "",
        invoiceNumber: detail.invoiceNumber ?? "",
        contractCode: detail.contractCode ?? "",
        importDate: toText(detail.entryDate).slice(0, 10),
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
        warehouseCode:
          this.distinctJoin(lotBoxes.map((b) => b.storageUnit)) ||
          (l.whsCode ?? ""),
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
