import { AccountService } from "app/core/auth/account.service";
import { take } from "rxjs";
import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ChangeDetectorRef,
} from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatDialog } from "@angular/material/dialog";
import { Router } from "@angular/router";
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from "@angular/animations";

import { DialogContentExampleDialogComponent } from "./confirm-dialog/confirm-dialog.component";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { PoImportTem } from "app/entities/list-material/services/info-tem-ncc.service";
import { ScanImportDialogComponent } from "../add-info-tem-ncc/scan-import-dialog/scan-import-dialog.component";
import { ScanImportDialogData } from "../add-info-tem-ncc/scan-import-dialog/scan-import.models";
import {
  SendSystemDialogComponent,
  SendSystemDialogData,
  SendSystemMaterial,
} from "./send-system-dialog/send-system-dialog.component";
import {
  DeliveryNotificationDto,
  InfoTemNccService,
} from "../services/info-tem-ncc.service";
import { PageEvent } from "@angular/material/paginator";
import {
  MiniPageState,
  slicePage,
} from "../shared/mini-pager/mini-pager.component";
import {
  DeliveryNotificationDetailDto,
  SapPor1R1Dto,
  toText,
  VendorLabelInfoDto,
} from "../services/info-tem-ncc.service";

/** Giữ export cũ cho OrderSummaryDialog / chỗ khác còn import. */
export interface SessionItem {
  importDate: string;
  warehouse: string;
  warehouseType: string;
  status: string;
  totalQty: number;
  totalScanQty: number;
  itemCount: number;
  transactionId: number;
  note: string;
}

export interface TemNccItem {
  id: number;
  poCode: string;
  vendorName: string;
  arrivalDate: string;
  createdDate: string;
  createdBy: string;
  warehouse: string;
  poComments: string;
  status: string;
  sessions?: SessionItem[];
  _raw?: PoImportTem;
  hasPanaSent?: boolean;
}

/** Cấp 3: vật tư trong PO */
export interface MaterialItem {
  id: number;
  materialCode: string;
  materialName: string;
  partNumber: string;
  warehouseCode: string;
  lotQuantity: number;
  poQuantity: number;
  receivedQuantity: number;
  palletCount: number;
  boxCount: number;
  importedBy: string;
  /** 0–100 */
  progress: number;
  /** Thùng thật (vendorLabelInfoList của API detail) */
  boxes: VendorLabelInfoDto[];
}

/** Cấp 2: PO trong đợt giao hàng */
export interface PoItem {
  /** Key duy nhất: `${deliveryId}-${docEntry}` */
  id: string;
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
  materials: MaterialItem[];
}

/** Cấp 1: Thông báo giao hàng */
export interface DeliveryNoticeItem {
  id: number;
  deliveryNotice: string;
  importDate: string;
  invoiceNumber: string;
  contractCode: string;
  vendorName: string;
  vehicleNumber: string;
  poCount: number;
  materialQuantity: number;
  status: "INCOMPLETE" | "COMPLETED";
  /** Nguồn tạo (giá trị gốc từ API, VD: "system", "appSmart") */
  source: string;
  pos: PoItem[];
  /** Bản ghi gốc từ API — dùng khi PUT (xóa mềm) */
  raw: DeliveryNotificationDto;
}

export interface FilterValues {
  deliveryNotice: string;
  importDate: string;
  invoiceNumber: string;
  contractCode: string;
  vendorName: string;
  vehicleNumber: string;
  poCount: string;
  materialQuantity: string;
  status: string;
  source: string;
}

/** Nhãn + màu badge cho các nguồn đã biết (key viết thường) */
const SOURCE_BADGES: Record<string, { label: string; bg: string; fg: string }> =
  {
    system: { label: "Hệ thống", bg: "#e0e7ff", fg: "#4338ca" },
    appsmart: { label: "AppSmart", bg: "#fce7f3", fg: "#be185d" },
  };

/** Bảng màu cho nguồn chưa khai báo — chọn ổn định theo tên nguồn */
const SOURCE_FALLBACK_COLORS: { bg: string; fg: string }[] = [
  { bg: "#ccfbf1", fg: "#0f766e" },
  { bg: "#fef3c7", fg: "#b45309" },
  { bg: "#ede9fe", fg: "#6d28d9" },
  { bg: "#e0f2fe", fg: "#0369a1" },
  { bg: "#fee2e2", fg: "#b91c1c" },
  { bg: "#ecfccb", fg: "#4d7c0f" },
];

@Component({
  selector: "jhi-info-tem-ncc",
  standalone: false,
  templateUrl: "./info-tem-ncc.component.html",
  styleUrls: ["./info-tem-ncc.component.scss"],
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
export class InfoTemNccComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = [
    "stt",
    "actions",
    "deliveryNotice",
    "importDate",
    "invoiceNumber",
    "contractCode",
    "vendorName",
    "vehicleNumber",
    "poCount",
    "materialQuantity",
    "source",
    "status",
  ];
  isLoading = false;
  totalItems = 0;
  pageIndex = 0;

  dataSource = new MatTableDataSource<DeliveryNoticeItem>([]);
  pageSize = 20;
  pageSizeOptions = [10, 20, 50];
  /** Cỡ trang cho bảng lồng (lớp 2 PO, lớp 3 vật tư) */
  readonly nestedPageSizeOptions = [5, 10, 20];

  filterValues: FilterValues = {
    deliveryNotice: "",
    importDate: "",
    invoiceNumber: "",
    contractCode: "",
    vendorName: "",
    vehicleNumber: "",
    poCount: "",
    materialQuantity: "",
    status: "",
    source: "",
  };
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  /** Mobile: mở rộng bộ lọc */
  mobileFilterExpanded = false;
  /** Mobile: đơn cha đang xem chi tiết (null = list) */
  selectedDelivery: DeliveryNoticeItem | null = null;

  detailLoadingIds: Record<number, boolean> = {};
  detailErrorIds: Record<number, boolean> = {};
  private detailLoadedIds = new Set<number>();

  /** Phân trang lớp 2 (PO theo đơn) và lớp 3 (vật tư theo PO) */
  private poPageStates = new Map<number, MiniPageState>();
  private materialPageStates = new Map<string, MiniPageState>();
  /** Lọc lớp 3 theo từng PO */
  private materialFilters = new Map<
    string,
    { code: string; name: string; part: string }
  >();
  /** PO đã gọi lấy part-numbers (chỉ gọi khi expand PO lần đầu) */
  private partLoadedPoIds = new Set<string>();

  private expandedDeliveryIds = new Set<number>();
  private expandedPoIds = new Set<string>();
  private readonly mobileBreakpoint = 768;

  constructor(
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef,
    private notificationService: NotificationService,
    private router: Router,
    private infoTemNccService: InfoTemNccService,
    private accountService: AccountService,
  ) {}

  ngOnInit(): void {
    this.dataSource.filterPredicate = this.buildFilterPredicate();
    this.loadData();
  }
  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
  }

  loadData(): void {
    this.isLoading = true;
    this.infoTemNccService
      .getDeliveryNotifications(this.pageIndex, this.pageSize)
      .subscribe({
        next: ({ items, total }) => {
          // Sắp xếp lại phía FE phòng khi backend bỏ qua tham số sort
          this.dataSource.data = [...items]
            .sort(
              (a, b) =>
                this.createdTime(b) - this.createdTime(a) || b.id - a.id,
            )
            .map((dto) => this.mapDelivery(dto));
          this.totalItems = total;
          this.isLoading = false;
          this.expandedDeliveryIds.clear();
          this.expandedPoIds = new Set<string>();
          this.detailLoadedIds.clear();
          this.poPageStates.clear();
          this.materialPageStates.clear();
          this.materialFilters.clear();
          this.partLoadedPoIds.clear();
        },
        error: () => {
          this.dataSource.data = [];
          this.totalItems = 0;
          this.isLoading = false;
          this.notificationService.error(
            "Không tải được danh sách thông báo giao hàng.",
          );
        },
      });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadData();
  }

  get isMobile(): boolean {
    return (
      typeof window !== "undefined" &&
      window.innerWidth <= this.mobileBreakpoint
    );
  }

  get mobileDeliveries(): DeliveryNoticeItem[] {
    return this.dataSource.filteredData;
  }

  toggleMobileFilter(): void {
    this.mobileFilterExpanded = !this.mobileFilterExpanded;
  }

  openMobileDetail(row: DeliveryNoticeItem): void {
    this.selectedDelivery = row;
    this.expandedPoIds = new Set<string>();
    if (row.pos.length) {
      this.expandedPoIds.add(row.pos[0].id);
      this.loadPartNumbers(row.pos[0]);
      return;
    }
    this.ensureDetail(row, true);
  }

  closeMobileDetail(): void {
    this.selectedDelivery = null;
  }

  onMobileSearch(): void {
    this.applyFilter();
  }

  onStartWarehouse(): void {
    const delivery = this.selectedDelivery;
    if (!delivery) {
      void this.router.navigate(["/info-tem-ncc/add-info-tem-ncc"]);
      return;
    }
    // Chưa có danh sách PO / vật tư của đơn → dialog không phân bổ được thùng vào PO
    if (this.isDetailLoading(delivery)) {
      this.notificationService.info(
        "Đang tải chi tiết đơn — vui lòng đợi giây lát rồi bấm lại.",
      );
      return;
    }
    if (
      this.hasDetailError(delivery) ||
      !this.detailLoadedIds.has(delivery.id)
    ) {
      this.notificationService.warning(
        "Chưa tải được chi tiết đơn — đang tải lại, vui lòng bấm lại sau.",
      );
      this.ensureDetail(delivery);
      return;
    }

    const isMobile =
      typeof window !== "undefined" &&
      window.innerWidth <= this.mobileBreakpoint;
    const data: ScanImportDialogData = {
      deliveryNotice: delivery.deliveryNotice,
      vehicleNumber: delivery.vehicleNumber,
      contractCode: delivery.contractCode,
      vendorCode: delivery.pos[0]?.vendorCode ?? "",
      scenarioCode: delivery.pos[0]?.vendorCode ?? "",
      warehouse: delivery.pos[0]?.warehouseKeeper,
      poCode: delivery.pos[0]?.poCode,
      deliveryNotificationId: delivery.id,
      arrivalDate: delivery.importDate,
      // id vật tư = sapPor1Id
      parentItems: delivery.pos.flatMap((po) =>
        po.materials.map((m) => ({
          id: m.id,
          partNumber: m.partNumber,
          sapCode: m.materialCode,
          orderQty: m.poQuantity,
          materialName: m.materialName,
          poCode: po.poCode,
        })),
      ),
    };

    this.dialog.open(ScanImportDialogComponent, {
      width: isMobile ? "100vw" : "96vw",
      maxWidth: isMobile ? "100vw" : "1400px",
      height: isMobile ? "100vh" : "90vh",
      maxHeight: isMobile ? "100dvh" : "92vh",
      panelClass: isMobile
        ? ["scan-import-dialog-panel", "scan-import-dialog-panel--mobile"]
        : ["scan-import-dialog-panel"],
      autoFocus: false,
      disableClose: true,
      data,
    });
  }

  onImportSystem(po: PoItem, event?: Event): void {
    event?.stopPropagation();
    const isMobile =
      typeof window !== "undefined" &&
      window.innerWidth <= this.mobileBreakpoint;
    const data: SendSystemDialogData = {
      poCode: po.poCode,
      warehouseKeeper: po.warehouseKeeper,
      vendorName: po.vendorName,
      vehicleNumber: po.vehicleNumber,
      materialCount: po.materials.length,
      materials: this.buildSendSystemMaterials(po),
    };
    this.dialog
      .open(SendSystemDialogComponent, {
        width: isMobile ? "100vw" : "560px",
        maxWidth: isMobile ? "100vw" : "96vw",
        height: isMobile ? "100vh" : "90vh",
        maxHeight: isMobile ? "100dvh" : "92vh",
        panelClass: isMobile
          ? ["send-system-dialog-panel", "send-system-dialog-panel--mobile"]
          : ["send-system-dialog-panel"],
        autoFocus: false,
        disableClose: true,
        data,
      })
      .afterClosed()
      .subscribe((result: { changed?: boolean } | undefined) => {
        // Đã gửi SAP / PanaCIM → tải lại chi tiết đơn để cập nhật trạng thái
        const delivery = this.selectedDelivery;
        if (result?.changed && delivery) {
          this.detailLoadedIds.delete(delivery.id);
          this.partLoadedPoIds.clear();
          this.ensureDetail(delivery, true);
        }
      });
  }

  mobileDeliveryStatus(status: DeliveryNoticeItem["status"]): string {
    return status === "COMPLETED" ? "Đã hoàn thành" : "Chờ nhập";
  }

  /**
   * Chưa nhận thùng nào → Chờ nhập; nhận chưa đủ SL PO → Đang nhập;
   * đủ SL → theo gửi SAP / PanaCIM: còn thùng chưa gửi → "Chưa gửi …"; gửi hết → "Đã gửi SAP, PanaCIM".
   */
  mobilePoStatus(po: PoItem): string {
    const total = this.poTotalQty(po);
    const received = this.poReceivedQty(po);
    if (received <= 0) {
      return "Chờ nhập";
    }
    if (received < total) {
      return "Đang nhập";
    }
    // Đủ SL → theo trạng thái gửi SAP / PanaCIM của mọi thùng
    const boxes = po.materials.flatMap((m) => m.boxes);
    const sapDone = boxes.every((b) => this.isSapSent(b.sapSendStatus));
    const panaDone = boxes.every((b) => this.isSapSent(b.panaSendStatus));
    if (sapDone && panaDone) {
      return "Đã gửi SAP, PanaCIM";
    }
    if (!sapDone && !panaDone) {
      return "Chưa gửi SAP, PanaCIM";
    }
    return sapDone ? "Chưa gửi PanaCIM" : "Chưa gửi SAP";
  }

  mobilePoStatusClass(po: PoItem): string {
    const label = this.mobilePoStatus(po);
    if (label === "Chờ nhập") {
      return "waiting";
    }
    if (label.startsWith("Chưa gửi")) {
      return "sap";
    }
    if (label.startsWith("Đã gửi")) {
      return "done";
    }
    return "importing";
  }

  mobileMaterialStatus(m: MaterialItem): string {
    if (m.progress > 100) {
      return "Vượt SL";
    }
    if (m.progress >= 100) {
      return "Đủ SL";
    }
    if (m.progress > 0) {
      return "Đang nhập";
    }
    return "Chưa nhập";
  }

  mobileMaterialStatusClass(m: MaterialItem): string {
    if (m.progress >= 100) {
      return "done";
    }
    if (m.progress > 0) {
      return "importing";
    }
    return "pending";
  }

  poReceivedQty(po: PoItem): number {
    return po.materials.reduce((s, m) => s + (m.receivedQuantity || 0), 0);
  }

  poTotalQty(po: PoItem): number {
    return po.materials.reduce((s, m) => s + (m.poQuantity || 0), 0);
  }

  poProgressPercent(po: PoItem): number {
    const total = this.poTotalQty(po);
    if (!total) {
      return 0;
    }
    return Math.min(100, Math.round((this.poReceivedQty(po) / total) * 100));
  }

  formatDisplayDate(value: string): string {
    const raw = (value ?? "").trim();
    const iso = /^(\d{4})-(\d{2})-(\d{2})/.exec(raw);
    if (iso) {
      return `${iso[3]}/${iso[2]}/${iso[1]}`;
    }
    return raw;
  }

  /** Chọn ngày ở ô lọc → điền dd/MM/yyyy (lọc chứa theo ngày hiển thị) */
  onFilterDatePicked(key: keyof FilterValues, value: Date | null): void {
    if (!value) {
      return;
    }
    const pad = (n: number): string => String(n).padStart(2, "0");
    this.filterValues[key] =
      `${pad(value.getDate())}/${pad(value.getMonth() + 1)}/${value.getFullYear()}`;
    this.applyFilter();
  }

  applyFilter(): void {
    this.dataSource.filter = JSON.stringify(this.filterValues);
  }

  // ---------- Lớp 2: PO ----------
  getPoPageState(row: DeliveryNoticeItem): MiniPageState {
    let st = this.poPageStates.get(row.id);
    if (!st) {
      st = { pageIndex: 0, pageSize: this.nestedPageSizeOptions[0] };
      this.poPageStates.set(row.id, st);
    }
    return st;
  }

  pagedPos(row: DeliveryNoticeItem): PoItem[] {
    return slicePage(row.pos, this.getPoPageState(row));
  }

  onPoPage(row: DeliveryNoticeItem, pageState: MiniPageState): void {
    this.poPageStates.set(row.id, pageState);
  }

  // ---------- Lớp 3: vật tư trong PO ----------
  getMaterialFilter(po: PoItem): { code: string; name: string; part: string } {
    let f = this.materialFilters.get(po.id);
    if (!f) {
      f = { code: "", name: "", part: "" };
      this.materialFilters.set(po.id, f);
    }
    return f;
  }

  onMaterialFilterChange(po: PoItem): void {
    const st = this.getMaterialPageState(po);
    this.materialPageStates.set(po.id, { ...st, pageIndex: 0 });
  }

  filteredMaterials(po: PoItem): MaterialItem[] {
    const f = this.getMaterialFilter(po);
    const has = (value: string, term: string): boolean =>
      !term.trim() ||
      toText(value).toLowerCase().includes(term.trim().toLowerCase());
    return po.materials.filter(
      (m) =>
        has(m.materialCode, f.code) &&
        has(m.materialName, f.name) &&
        has(m.partNumber, f.part),
    );
  }

  getMaterialPageState(po: PoItem): MiniPageState {
    let st = this.materialPageStates.get(po.id);
    if (!st) {
      st = { pageIndex: 0, pageSize: this.nestedPageSizeOptions[0] };
      this.materialPageStates.set(po.id, st);
    }
    return st;
  }

  pagedMaterials(po: PoItem): MaterialItem[] {
    return slicePage(this.filteredMaterials(po), this.getMaterialPageState(po));
  }

  onMaterialPage(po: PoItem, pageState: MiniPageState): void {
    this.materialPageStates.set(po.id, pageState);
  }

  /** STT liên tục qua các trang */
  materialRowNo(po: PoItem, indexInPage: number): number {
    const st = this.getMaterialPageState(po);
    return st.pageIndex * st.pageSize + indexInPage + 1;
  }

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

  getRowIndex(row: DeliveryNoticeItem): number {
    const index = this.dataSource.filteredData.indexOf(row);
    return this.pageIndex * this.pageSize + index + 1;
  }

  toggleDelivery(row: DeliveryNoticeItem): void {
    if (this.expandedDeliveryIds.has(row.id)) {
      this.expandedDeliveryIds.delete(row.id);
      return;
    }
    this.expandedDeliveryIds.add(row.id);
    this.ensureDetail(row);
  }

  isDetailLoading(row: DeliveryNoticeItem): boolean {
    return !!this.detailLoadingIds[row.id];
  }
  hasDetailError(row: DeliveryNoticeItem): boolean {
    return !!this.detailErrorIds[row.id];
  }

  displayText(v: string | number | null | undefined): string {
    return v === null || v === undefined || v === "" ? "—" : String(v);
  }

  isDeliveryExpanded(row: DeliveryNoticeItem): boolean {
    return this.expandedDeliveryIds.has(row.id);
  }

  togglePo(po: PoItem, event?: Event): void {
    event?.stopPropagation();
    if (this.expandedPoIds.has(po.id)) {
      this.expandedPoIds.delete(po.id);
    } else {
      this.expandedPoIds.add(po.id);
      this.loadPartNumbers(po);
    }
    this.expandedPoIds = new Set(this.expandedPoIds);
  }

  isPoExpanded(po: PoItem): boolean {
    return this.expandedPoIds.has(po.id);
  }

  deliveryStatusLabel(status: DeliveryNoticeItem["status"]): string {
    return status === "COMPLETED" ? "Đã hoàn thành" : "Chưa hoàn thành";
  }

  sourceLabel(source: string): string {
    const raw = toText(source);
    if (!raw) {
      return "—";
    }
    return SOURCE_BADGES[raw.toLowerCase()]?.label ?? raw;
  }

  sourceBadgeStyle(source: string): { background: string; color: string } {
    const key = toText(source).toLowerCase();
    if (!key) {
      return { background: "#f1f5f9", color: "#64748b" };
    }
    const known = SOURCE_BADGES[key];
    if (known) {
      return { background: known.bg, color: known.fg };
    }
    let hash = 0;
    for (let i = 0; i < key.length; i++) {
      hash = (hash * 31 + key.charCodeAt(i)) >>> 0;
    }
    const c = SOURCE_FALLBACK_COLORS[hash % SOURCE_FALLBACK_COLORS.length];
    return { background: c.bg, color: c.fg };
  }

  poStatusLabel(status: PoItem["status"]): string {
    return status === "IMPORTING" ? "Đang nhập" : "Chờ nhập";
  }

  progressClass(progress: number): string {
    if (progress > 100) {
      return "over";
    }
    if (progress >= 100) {
      return "done";
    }
    if (progress > 0) {
      return "partial";
    }
    return "empty";
  }

  onScan(row: DeliveryNoticeItem, event?: Event): void {
    event?.stopPropagation();
    this.notificationService.info(`In phiếu: ${row.deliveryNotice} (mock)`);
  }

  onDelete(row: DeliveryNoticeItem, event?: Event): void {
    event?.stopPropagation();
    const dialogRef = this.dialog.open(DialogContentExampleDialogComponent, {
      width: "400px",
      data: {
        title: "Xác nhận xóa",
        message: `Bạn có chắc chắn muốn xóa ${row.deliveryNotice}?`,
        confirmText: "Xóa",
        cancelText: "Hủy",
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== true) {
        return;
      }
      // Xóa mềm: PUT /delivery-notifications/{id} với deletedAt / deletedBy
      this.accountService
        .getAuthenticationState()
        .pipe(take(1))
        .subscribe((account) => {
          const payload: DeliveryNotificationDto = {
            ...row.raw,
            deletedAt: new Date().toISOString(),
            deletedBy: account?.login ?? "",
          };
          this.infoTemNccService.updateDeliveryNotification(payload).subscribe({
            next: () => {
              this.expandedDeliveryIds.delete(row.id);
              this.notificationService.success(
                `Đã xóa đơn ${row.deliveryNotice || row.id}.`,
              );
              this.loadData();
            },
            error: () => {
              this.notificationService.error("Xóa đơn thất bại.");
            },
          });
        });
    });
  }

  /** createdAt → ms (không có / sai định dạng → 0) */
  private createdTime(dto: DeliveryNotificationDto): number {
    const t = new Date(toText(dto.createdAt)).getTime();
    return Number.isNaN(t) ? 0 : t;
  }

  private mapDelivery(dto: DeliveryNotificationDto): DeliveryNoticeItem {
    const status = toText(dto.status).toUpperCase();
    return {
      id: dto.id,
      deliveryNotice: dto.deliveryNotificationCode ?? "",
      importDate: toText(dto.entryDate),
      invoiceNumber: dto.invoiceNumber ?? "",
      contractCode: dto.contractCode ?? "",
      vendorName: dto.vendorName ?? "",
      vehicleNumber: dto.contNo ?? "",
      poCount: dto.numberOfPo ?? 0,
      materialQuantity: dto.numberOfItem ?? 0,
      status: status === "COMPLETED" ? "COMPLETED" : "INCOMPLETE",
      source: toText(dto.source),
      pos: [], // API list chưa trả về, cần load khi expand
      raw: dto,
    };
  }
  private buildFilterPredicate() {
    return (item: DeliveryNoticeItem, filterJson: string): boolean => {
      let f: FilterValues;
      try {
        f = JSON.parse(filterJson || "{}");
      } catch {
        return true;
      }

      const includes = (value: string | number, query: string): boolean => {
        const term = (query ?? "").trim().toLowerCase();
        if (!term) {
          return true;
        }
        return String(value ?? "")
          .toLowerCase()
          .includes(term);
      };

      const statusText = this.deliveryStatusLabel(item.status);
      const mobileStatus = this.mobileDeliveryStatus(item.status);
      const statusOk =
        includes(statusText, f.status) || includes(mobileStatus, f.status);
      return (
        includes(item.deliveryNotice, f.deliveryNotice) &&
        (includes(item.importDate, f.importDate) ||
          includes(this.formatDateTime(item.importDate), f.importDate)) &&
        includes(item.invoiceNumber, f.invoiceNumber) &&
        includes(item.contractCode, f.contractCode) &&
        includes(item.vendorName, f.vendorName) &&
        includes(item.vehicleNumber, f.vehicleNumber) &&
        includes(item.poCount, f.poCount) &&
        includes(item.materialQuantity, f.materialQuantity) &&
        (includes(item.source, f.source) ||
          includes(this.sourceLabel(item.source), f.source)) &&
        statusOk
      );
    };
  }
  /** Gọi API detail nếu chưa tải; gán kết quả vào row.pos */
  private ensureDetail(row: DeliveryNoticeItem, expandFirstPo = false): void {
    const id = row.id;
    if (this.detailLoadedIds.has(id) || this.detailLoadingIds[id]) {
      return;
    }
    this.detailLoadingIds = { ...this.detailLoadingIds, [id]: true };
    this.detailErrorIds = { ...this.detailErrorIds, [id]: false };
    this.infoTemNccService.getDeliveryNotificationDetail(id).subscribe({
      next: (detail) => {
        const pos = this.mapPos(detail);
        row.pos = pos;
        row.poCount = pos.length;
        row.materialQuantity = pos.reduce((s, p) => s + p.materialTypeCount, 0);
        this.detailLoadedIds.add(id);
        this.detailLoadingIds = { ...this.detailLoadingIds, [id]: false };
        if (expandFirstPo && pos.length) {
          this.expandedPoIds = new Set([pos[0].id]);
          this.loadPartNumbers(pos[0]);
        }
        this.cdr.markForCheck();
      },
      error: () => {
        this.detailLoadingIds = { ...this.detailLoadingIds, [id]: false };
        this.detailErrorIds = { ...this.detailErrorIds, [id]: true };
        this.notificationService.error(
          "Không tải được chi tiết thông báo giao hàng.",
        );
      },
    });
  }

  /** Gom sapPor1R1List theo docEntry (mã PO) → mỗi PO chứa các dòng vật tư */
  private mapPos(detail: DeliveryNotificationDetailDto): PoItem[] {
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

    const pos: PoItem[] = [];
    groups.forEach((lines, docEntry) => {
      const materials = lines.map((l) => this.mapMaterial(l));
      const totalQuantity = materials.reduce((s, m) => s + m.poQuantity, 0);
      const received = materials.reduce((s, m) => s + m.receivedQuantity, 0);
      pos.push({
        id: `${detail.id}-${docEntry}`,
        poCode: docEntry || "—",
        warehouseKeeper: this.distinctJoin(lines.map((l) => l.whsCode)),
        vendorCode: toText(detail.vendorCode) || toText(detail.vendorName),
        vendorName: toText(detail.vendorName),
        vehicleNumber: detail.contNo ?? "",
        invoiceNumber: detail.invoiceNumber ?? "",
        contractCode: detail.contractCode ?? "",
        importDate: toText(detail.entryDate),
        importBatch: null,
        materialTypeCount: new Set(lines.map((l) => l.itemCode ?? "")).size,
        totalQuantity,
        status: received > 0 ? "IMPORTING" : "WAITING",
        materials,
      });
    });
    return pos;
  }

  private mapMaterial(l: SapPor1R1Dto): MaterialItem {
    const reels = l.vendorLabelInfoList ?? [];
    const poQuantity = Number(l.quantity ?? 0);
    const receivedQuantity = reels.reduce(
      (s, r) => s + Number(r.initialQuantity ?? 0),
      0,
    );
    const distinctCount = (values: unknown[]): number =>
      new Set(values.map((v) => toText(v)).filter(Boolean)).size;
    return {
      id: l.id,
      materialCode: l.itemCode ?? "",
      materialName: l.dscription ?? "",
      partNumber: "", // lấy từ API OITM theo mã SAP, xem loadPartNumbers()
      warehouseCode: l.whsCode ?? "",
      lotQuantity: distinctCount(reels.map((r) => r.lot)),
      poQuantity,
      receivedQuantity,
      palletCount: distinctCount(reels.map((r) => r.serialPallet)),
      // mỗi bản ghi vendorLabelInfo = 1 thùng
      boxCount: reels.length,
      importedBy: this.distinctJoin(reels.map((r) => r.createdBy)),
      // Cho phép vượt 100% (nhận dư so với SL PO)
      progress: poQuantity
        ? Math.round((receivedQuantity / poQuantity) * 100)
        : 0,
      boxes: reels,
    };
  }

  /**
   * Gọi khi expand PO (lớp vật tư): mỗi mã SAP khác nhau gọi API part-numbers 1 lần,
   * gán cho các vật tư cùng mã. Service cache theo mã nên PO khác cùng mã không gọi lại.
   */
  private loadPartNumbers(po: PoItem): void {
    if (this.partLoadedPoIds.has(po.id)) {
      return;
    }
    this.partLoadedPoIds.add(po.id);
    const bySapCode = new Map<string, MaterialItem[]>();
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
          }
          this.cdr.markForCheck();
        });
    });
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
  /** Vật tư của PO → danh sách thùng thật (1 dòng = 1 thùng: LOT, ReelID, SL, đã gửi SAP) */
  private buildSendSystemMaterials(po: PoItem): SendSystemMaterial[] {
    return po.materials.map((m, i) => {
      let status: SendSystemMaterial["status"] = "waiting";
      if (m.poQuantity > 0 && m.receivedQuantity >= m.poQuantity) {
        status = "enough";
      } else if (m.receivedQuantity > 0) {
        status = "importing";
      }
      const lots: SendSystemMaterial["lots"] = m.boxes.map((b) => ({
        id: `box-${b.id}`,
        lotNumber: toText(b.lot) || "—",
        reelId: toText(b.reelId),
        quantity: Number(b.initialQuantity ?? 0),
        sent: this.isSapSent(b.sapSendStatus),
        panaSent: this.isSapSent(b.panaSendStatus),
        selected: false,
        record: b,
      }));
      return {
        id: String(m.id),
        materialName: m.materialName,
        materialCode: m.materialCode,
        partNumber: m.partNumber,
        warehouseCode: m.warehouseCode,
        receivedQty: m.receivedQuantity,
        poQty: m.poQuantity,
        boxCount: m.boxes.length,
        status,
        selected: false,
        expanded: i === 0 && lots.length > 0,
        lots,
      };
    });
  }

  /** sapSendStatus có thể là boolean / string / number */
  private isSapSent(value: unknown): boolean {
    const t = toText(value).toLowerCase();
    return t !== "" && t !== "false" && t !== "0";
  }
}
