import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  HostListener,
  Inject,
  OnDestroy,
  Optional,
  ViewChild,
} from "@angular/core";
import { forkJoin, from, Observable, of, Subscription } from "rxjs";
import { catchError, map, mergeMap, switchMap, toArray } from "rxjs/operators";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatAutocompleteModule } from "@angular/material/autocomplete";
import { WarehouseCacheService } from "app/entities/list-material/services/warehouse-cache.service";
import { CachedWarehouse } from "app/entities/list-material/services/warehouse-db";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { extractReelIdFromScan } from "app/entities/list-material/services/scan-code.util";
import { openChoiceDialog } from "app/entities/info-tem-ncc/shared/choice-dialog/choice-dialog.component";
import { WarehouseExportService } from "./warehouse-export.service";
import { ExportHistoryDialogComponent } from "./export-history-dialog/export-history-dialog.component";
import {
  PickSuggestionDialogComponent,
  PickSuggestionDialogData,
} from "./pick-suggestion-dialog/pick-suggestion-dialog.component";
import {
  openExportCancel,
  openExportIncomplete,
} from "./export-confirm-dialog/export-confirm-dialog.component";
import {
  ExportApproval,
  ExportPlannedReel,
  ExportReelInventory,
  ExportRequest,
  ExportRequestItem,
  ExportScannedBox,
  ExportScannedPallet,
} from "./warehouse-export.models";

type MainTab = "request" | "planning";

/** Dữ liệu nhập / scan của 1 tab */
interface ExportWorkspace {
  requestCode: string;
  palletCode: string;
  boxCode: string;
  requests: ExportRequest[];
  boxes: ExportScannedBox[];
  pallets: ExportScannedPallet[];
  expanded: Set<string>;
  /** Thùng đã phân bổ cho từng dòng vật tư (tính lại sau mỗi lần scan / xóa) */
  allocation: Map<ExportRequestItem, ExportScannedBox[]>;
}

/** Mở màn Xuất kho từ danh sách: nạp sẵn các mã yêu cầu đã chọn */
export interface WarehouseExportDialogData {
  requestCodes?: string[];
  /** Mở từ menu nào: chỉ hiện đúng tab đó (request = Yêu cầu lĩnh vật tư) */
  mode?: "request" | "planning";
  /** Tiêu đề màn (vd Xuất chuyển kho / Xuất kho khuyến nghị) */
  title?: string;
}

/** Mobile: tab dưới đáy */
type MobileTab = "pick" | "scanned" | "send";

/** Mobile: chế độ của ô scan duy nhất */
type MobileScanMode = "box" | "pallet";

const MOBILE_SCAN_MODE_KEY = "warehouse-export.mobile-scan-mode";

/** Mobile: màn chính (nhập / scan) hoặc Chi tiết thông tin */
type MobileView = "main" | "detail";

const MOBILE_BREAKPOINT = 768;
type ScannedTab = "box" | "pallet";

/** Đối chiếu 1 thùng khuyến nghị (Planning) với tồn kho (Inventory) */
type ReelReadiness =
  | "loading"
  | "error"
  | "missing"
  | "mismatch"
  | "empty"
  | "short"
  | "ready";

const READINESS_LABELS: Record<ReelReadiness, string> = {
  loading: "Đang kiểm tra",
  error: "Lỗi tra cứu",
  missing: "Không có trong kho",
  mismatch: "Lệch part",
  // Thùng khuyến nghị có trong kho nhưng tồn = 0 → đã xuất rồi
  empty: "Đã xuất",
  short: "Thiếu tồn",
  ready: "Sẵn sàng",
};

/** Thùng khuyến nghị (Planning) kèm dòng vật tư chứa nó */
interface PlannedReelRef {
  item: ExportRequestItem;
  reel: ExportPlannedReel;
}

/** Kho duyệt khi gửi xác nhận xuất kho */
const APPROVAL_WAREHOUSES: Array<{ code: string; name: string }> = [
  { code: "01", name: "Kho linh kiện điện tử" },
  { code: "02", name: "Kho vật tư LED" },
  { code: "03", name: "Kho vật tư TBCS" },
  { code: "04", name: "Kho vật tư CLC" },
  { code: "05", name: "Kho ngành ĐTTĐ" },
  { code: "06", name: "Kho ngành LRSP LED 1" },
  { code: "07", name: "Kho ngành SMART" },
  { code: "08", name: "Kho ngành LED2" },
  { code: "10", name: "Kho cơ khí xưởng" },
  { code: "11", name: "Kho ngành CNPT" },
  { code: "12", name: "Kho vật tư Smart" },
  { code: "13", name: "Kho ngành CNPT 2" },
  { code: "14", name: "Kho chị Hòa 2" },
];

/** Kết quả tra 1 thùng scan ở tab Planning */
interface PlanningScanResult {
  reelId: string;
  box: ExportScannedBox | null;
  /** Lý do không thêm được */
  reason?:
    | "notInStock"
    | "notInWo"
    | "notPlanned"
    | "exported"
    | "scannedBefore"
    | "error";
  /** Thùng có trong danh sách khuyến nghị */
  planned: boolean;
}

/** Kho (khu vực) của 1 vị trí — cùng cấu trúc ExportWarehouseArea của service */
interface WarehouseAreaRef {
  areaCode: string;
  areaName: string;
}

/** Xuất chuyển kho: 1 vị trí đang có mã vật tư — cùng cấu trúc ExportSapLocation của service */
interface ItemStockLocation {
  areaCode: string;
  areaName: string;
  location: string;
  locationId: number;
  reels: number;
  qty: number;
}

/** Xuất chuyển kho: vị trí của 1 mã vật tư SAP (tra theo yêu cầu, không tự tra hết) */
interface ItemStockState {
  loading: boolean;
  error: boolean;
  locations: ItemStockLocation[];
  /** epoch ms lúc tra API */
  fetchedAt: number;
  /** Số kho tra lỗi (kết quả có thể thiếu) */
  failedAreas: number;
}

/** Bảng vị trí → kho — cùng cấu trúc ExportLocationAreaIndex của service */
interface AreaIndexRef {
  byId: Record<string, WarehouseAreaRef>;
  byName: Record<string, WarehouseAreaRef>;
  builtAt: number;
}

/** Bảng đối chiếu Planning — tầng 2: 1 part tại 1 vị trí (các thùng khuyến nghị ở đó) */
interface LocationPart {
  key: string;
  item: ExportRequestItem;
  reels: ExportPlannedReel[];
}

/** Bảng đối chiếu Planning — tầng 1: 1 vị trí trong kho của 1 mã SAP */
interface LocationGroup {
  key: string;
  areaCode: string;
  areaName: string;
  location: string;
  sapCode: string;
  /** Thùng không có trong kho / lỗi tra cứu (không có vị trí) */
  missing: boolean;
  /** Tổng tồn khả dụng của các thùng khuyến nghị tại vị trí */
  stock: number;
  reelCount: number;
  parts: LocationPart[];
}

/** Số request /inventory chạy song song khi kiểm tra tồn kho các thùng khuyến nghị */
const INVENTORY_CONCURRENCY = 6;

/** Khuyến nghị Planning — Gửi: số request PATCH chạy song song */
const SEND_CONCURRENCY = 4;

/** Xuất chuyển kho: số mã tra vị trí song song (đều tra trong 1 bảng chung, không gọi thêm API) */
const ITEM_STOCK_CONCURRENCY = 20;

/**
 * Xuất kho: nhập mã yêu cầu lĩnh vật tư → scan pallet / thùng → đối chiếu SL đã scan
 * với SL yêu cầu theo từng vật tư → Hoàn thành.
 */
@Component({
  selector: "jhi-warehouse-export-dialog",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatAutocompleteModule,
  ],
  templateUrl: "./warehouse-export-dialog.component.html",
  styleUrls: ["./warehouse-export-dialog.component.scss"],
})
export class WarehouseExportDialogComponent
  implements AfterViewInit, OnDestroy
{
  @ViewChild("requestInput") requestInput?: ElementRef<HTMLInputElement>;
  @ViewChild("palletInput") palletInput?: ElementRef<HTMLInputElement>;
  @ViewChild("boxInput") boxInput?: ElementRef<HTMLInputElement>;
  @ViewChild("mobileScanInput")
  mobileScanInput?: ElementRef<HTMLInputElement>;

  mainTab: MainTab = "request";
  scannedTab: ScannedTab = "box";

  isBusy = false;
  isCompleting = false;
  /** Đang gửi (nút Gửi) */
  isSending = false;

  /** Chọn thông tin gửi: kho duyệt, người duyệt, người quản lý kho */
  readonly approvalWarehouses = APPROVAL_WAREHOUSES;
  approval: ExportApproval = {
    warehouseCode: "",
    approver: "",
    warehouseManager: "",
  };
  approvers: string[] = [];
  /** Gợi ý vị trí (IndexedDB) cho ô Vị trí của thùng đã scan */
  locationOptions: CachedWarehouse[] = [];
  /** Ô vị trí ở header cột — áp cho mọi thùng đã scan */
  bulkLocation = "";

  /** Mobile: tab đang mở, chế độ + mã của ô scan, gợi ý quét lại khi nhầm chế độ */
  mobileTab: MobileTab = "pick";
  mobileScanMode: MobileScanMode = this.loadMobileScanMode();
  mobileScanCode = "";
  scanRetry: { code: string; mode: MobileScanMode } | null = null;
  /** Mobile — Cần lấy: ẩn vị trí đã lấy đủ */
  hideDoneLocations = false;
  /** Xuất chuyển kho: số chip vị trí hiện ở dòng vật tư (còn lại xem khi mở dòng) */
  readonly LOCATION_CHIP_LIMIT = 4;
  /** Mobile — bottom sheet danh sách thùng của 1 part tại 1 vị trí */
  sheet: { r: ExportRequest; part: LocationPart } | null = null;
  /** Mobile — menu ⋯ / ô thêm mã */
  mobileMenuOpen = false;
  showAddCode = false;

  /** Màn hình hẹp → giao diện mobile */
  isMobile = this.detectMobile();
  mobileView: MobileView = "main";

  /** Mỗi tab (Yêu cầu lĩnh vật tư / Khuyến nghị Planning) giữ dữ liệu scan riêng */
  /** Đang tra tồn kho các thùng khuyến nghị */
  private inventorySubs: Subscription[] = [];
  /** Xuất chuyển kho: tồn kho theo mã SAP (key chữ thường) */
  private itemStocks = new Map<string, ItemStockState>();
  /** Bảng đối chiếu: vị trí / part đang mở (key = mã WO | key nhóm) */
  private openNodes = new Set<string>();
  /** Bảng vị trí → kho (tải 1 lần, cache phiên) */
  private areaIndex: AreaIndexRef | null = null;

  /** Mobile — Chi tiết: vật tư đang mở danh sách thùng */
  private expandedItems = new Set<string>();

  private readonly workspaces: Record<MainTab, ExportWorkspace> = {
    request: this.emptyWorkspace(),
    planning: this.emptyWorkspace(),
  };

  constructor(
    private dialogRef: MatDialogRef<WarehouseExportDialogComponent, boolean>,
    private dialog: MatDialog,
    private exportService: WarehouseExportService,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef,
    private warehouseCache: WarehouseCacheService,
    @Optional()
    @Inject(MAT_DIALOG_DATA)
    private dialogData: WarehouseExportDialogData | null,
  ) {}

  /** Tiêu đề màn theo menu mở */
  get dialogTitle(): string {
    return this.dialogData?.title ?? "Xuất kho";
  }

  /** Mở từ menu cố định 1 chế độ → chỉ hiện tab đó */
  get lockedTab(): MainTab | null {
    return this.dialogData?.mode ?? null;
  }

  ngAfterViewInit(): void {
    if (this.lockedTab) {
      this.mainTab = this.lockedTab;
    }
    this.focus(this.requestInput);
    const codes = (this.dialogData?.requestCodes ?? [])
      .map((x) => x.trim())
      .filter(Boolean);
    if (codes.length) {
      setTimeout(() => this.preloadRequests(codes));
    }
    this.exportService.getApprovers().subscribe({
      next: (list) => {
        this.approvers = list;
      },
      error: () => {
        this.notificationService.error("Không tải được danh sách người duyệt.");
      },
    });
    void this.warehouseCache.ensureSynced().catch(() => undefined);
  }

  ngOnDestroy(): void {
    this.inventorySubs.forEach((sub) => sub.unsubscribe());
  }

  /** Dữ liệu của tab đang mở */
  get ws(): ExportWorkspace {
    return this.workspaces[this.mainTab];
  }

  get isPlanning(): boolean {
    return this.mainTab === "planning";
  }

  /** Tên mã đầu vào theo tab */
  get codeNoun(): string {
    return this.isPlanning ? "Work Order" : "yêu cầu";
  }

  get totalScannedBoxes(): number {
    return (
      this.workspaces.request.boxes.length +
      this.workspaces.planning.boxes.length
    );
  }

  @HostListener("window:resize")
  onResize(): void {
    this.isMobile = this.detectMobile();
  }

  get tabLabel(): string {
    return this.isPlanning ? "Khuyến nghị Planning" : "Yêu cầu lĩnh vật tư";
  }

  /** Đối chiếu thùng khuyến nghị (Planning) với kho (Inventory) */
  reelReadiness(i: ExportRequestItem, reel: ExportPlannedReel): ReelReadiness {
    if (reel.inventoryError) {
      return "error";
    }
    if (reel.inventory === undefined) {
      return "loading";
    }
    const inv = reel.inventory;
    if (!inv) {
      return "missing";
    }
    // Đúng part: partNumber kho = mã part Planning (name), hoặc partId kho = partNumberCode
    const samePart =
      inv.partNumber.toLowerCase() === i.materialCode.toLowerCase() ||
      this.partCodesOf(i, reel).includes(inv.partId.toLowerCase());
    if (!samePart) {
      return "mismatch";
    }
    if (inv.availableQty <= 0) {
      return "empty";
    }
    return inv.availableQty < reel.quantity ? "short" : "ready";
  }

  readinessLabel(state: ReelReadiness): string {
    return READINESS_LABELS[state];
  }

  /** Còn thùng đang tra kho → hiện loader thay cho bảng đối chiếu */
  isCheckingInventory(r: ExportRequest): boolean {
    const st = this.reelStats(r);
    return st.checked < st.total;
  }

  inventoryPercent(r: ExportRequest): number {
    const st = this.reelStats(r);
    return st.total ? Math.round((st.checked / st.total) * 100) : 100;
  }

  /** Số thùng theo từng kết quả đối chiếu của 1 part (bỏ trạng thái = 0) */
  itemReadiness(
    i: ExportRequestItem,
  ): Array<{ state: ReelReadiness; label: string; count: number }> {
    return this.readinessCounts(i, i.reels ?? []);
  }

  /** Đếm thùng theo kết quả đối chiếu (bỏ trạng thái = 0) */
  readinessCounts(
    i: ExportRequestItem,
    reels: ExportPlannedReel[],
  ): Array<{ state: ReelReadiness; label: string; count: number }> {
    const counts = new Map<ReelReadiness, number>();
    for (const reel of reels) {
      const state = this.reelReadiness(i, reel);
      counts.set(state, (counts.get(state) ?? 0) + 1);
    }
    return (Object.keys(READINESS_LABELS) as ReelReadiness[])
      .filter((st) => counts.get(st))
      .reverse()
      .map((state) => ({
        state,
        label: READINESS_LABELS[state],
        count: counts.get(state) ?? 0,
      }));
  }

  /** Kho (khu vực) của 1 vị trí: theo locationId, không có thì theo tên / phần trước " - " */
  areaOf(locationId: number, locationName: string): WarehouseAreaRef | null {
    const index = this.areaIndex;
    if (!index) {
      return null;
    }
    const byId = locationId ? index.byId[String(locationId)] : undefined;
    if (byId) {
      return byId;
    }
    const name = (locationName ?? "").trim().toLowerCase();
    return (
      index.byName[name] ?? index.byName[name.split(" - ")[0].trim()] ?? null
    );
  }

  get isAreaIndexReady(): boolean {
    return !!this.areaIndex;
  }

  /**
   * Bảng đối chiếu Planning (3 tầng): vị trí trong kho (kho · vị trí · mã SAP · tồn)
   * → part tại vị trí đó → từng thùng khuyến nghị. Chỉ dùng thông tin kho của các thùng
   * khuyến nghị (đã tra /inventory), không gọi thêm API.
   */
  locationGroups(r: ExportRequest): LocationGroup[] {
    const groups = new Map<string, LocationGroup>();
    for (const item of r.items) {
      for (const reel of item.reels ?? []) {
        const inv = reel.inventory;
        const area = inv ? this.areaOf(inv.locationId, inv.location) : null;
        const location = inv
          ? inv.location || "Chưa có vị trí"
          : reel.inventoryError
            ? "Lỗi tra kho"
            : "Không có trong kho";
        const sapCode = inv?.sapCode ?? "";
        const key = `${area?.areaCode ?? ""}|${location}|${sapCode}`;
        const group = groups.get(key) ?? {
          key,
          areaCode: area?.areaCode ?? "",
          areaName: area?.areaName ?? "",
          location,
          sapCode,
          missing: !inv,
          stock: 0,
          reelCount: 0,
          parts: [],
        };
        group.stock += inv?.availableQty ?? 0;
        group.reelCount++;
        let part = group.parts.find((x) => x.item === item);
        if (!part) {
          part = {
            key: `${key}|${item.partCode ?? item.materialCode}`,
            item,
            reels: [],
          };
          group.parts.push(part);
        }
        part.reels.push(reel);
        groups.set(key, group);
      }
    }
    // Part cần xuất nhưng Planning chưa chỉ định thùng nào
    const noReel = r.items.filter((i) => !i.reels?.length);
    if (noReel.length) {
      const key = "|no-reel|";
      groups.set(key, {
        key,
        areaCode: "",
        areaName: "",
        location: "Chưa có thùng khuyến nghị",
        sapCode: "",
        missing: true,
        stock: 0,
        reelCount: 0,
        parts: noReel.map((item) => ({
          key: `${key}|${item.partCode ?? item.materialCode}`,
          item,
          reels: [],
        })),
      });
    }
    return [...groups.values()].sort(
      (a, b) =>
        Number(a.missing) - Number(b.missing) ||
        a.areaCode.localeCompare(b.areaCode) ||
        a.location.localeCompare(b.location),
    );
  }

  /** Mở / đóng 1 vị trí hoặc 1 part trong bảng đối chiếu */
  toggleNode(r: ExportRequest, key: string): void {
    const id = `${r.code}|${key}`;
    if (this.openNodes.has(id)) {
      this.openNodes.delete(id);
    } else {
      this.openNodes.add(id);
    }
  }

  isNodeOpen(r: ExportRequest, key: string): boolean {
    return this.openNodes.has(`${r.code}|${key}`);
  }

  /** Xuất chuyển kho: key mở rộng 1 dòng vật tư (bảng thùng đã scan của vật tư) */
  itemKey(i: ExportRequestItem): string {
    return `item:${i.materialCode}`;
  }

  /** Xuất chuyển kho: trạng thái tra tồn kho của mã vật tư (undefined = chưa tra) */
  itemStock(i: ExportRequestItem): ItemStockState | undefined {
    return this.itemStocks.get(i.materialCode.toLowerCase());
  }

  /** Tổng tồn của mã vật tư (các vị trí đã tra) */
  itemStockQty(i: ExportRequestItem): number {
    return this.itemStockLocations(i).reduce((s, x) => s + x.qty, 0);
  }

  /** Các vị trí đang có mã vật tư, sắp theo kho → vị trí */
  itemStockLocations(i: ExportRequestItem): ItemStockLocation[] {
    return this.itemStock(i)?.locations ?? [];
  }

  /** Mở / đóng dòng vật tư — mở lần đầu thì tra vị trí của mã đó */
  toggleItem(r: ExportRequest, i: ExportRequestItem): void {
    this.toggleNode(r, this.itemKey(i));
    if (this.isNodeOpen(r, this.itemKey(i)) && !this.itemStock(i)) {
      this.loadItemStocks([i.materialCode]);
    }
  }

  /** Tra vị trí 1 mã vật tư (nút ở dòng, không mở rộng dòng) */
  lookupItemStock(i: ExportRequestItem, event?: Event, force = false): void {
    event?.stopPropagation();
    this.loadItemStocks([i.materialCode], force);
  }

  /** Tra vị trí mọi vật tư của yêu cầu (mã đã tra → tra lại, bỏ cache) */
  refreshItemStocks(r: ExportRequest, event?: Event): void {
    event?.stopPropagation();
    this.loadItemStocks(
      r.items.map((i) => i.materialCode),
      true,
    );
  }

  trackNode(_: number, node: { key: string }): string {
    return node.key;
  }

  /** SL khuyến nghị của part tại vị trí */
  partPlanned(p: LocationPart): number {
    return p.reels.reduce((sum, reel) => sum + reel.quantity, 0);
  }

  /** SL cần của part (cả WO, = tổng estReqdQty − đã xuất) */
  partRequired(p: LocationPart): number {
    return this.targetQty(p.item);
  }

  /** SL còn thiếu của part (cả WO) */
  partNeed(p: LocationPart): number {
    return Math.max(0, this.targetQty(p.item) - p.item.scannedQty);
  }

  /**
   * SL đã scan của các thùng khuyến nghị tại vị trí: thùng BE đã ghi nhận scan (SL khuyến nghị)
   * + thùng vừa scan ở màn hình này (SL xuất).
   */
  partScanned(p: LocationPart): number {
    const ids = new Set(p.reels.map((reel) => reel.reelId.toLowerCase()));
    const local = this.ws.boxes.filter((b) => ids.has(b.reelId.toLowerCase()));
    const localIds = new Set(local.map((b) => b.reelId.toLowerCase()));
    const before = p.reels
      .filter(
        (reel) =>
          reel.scannedBefore && !localIds.has(reel.reelId.toLowerCase()),
      )
      .reduce((sum, reel) => sum + reel.quantity, 0);
    return before + local.reduce((sum, b) => sum + b.quantity, 0);
  }

  /** Trạng thái theo cả WO: part đã scan đủ SL cần chưa */
  partState(p: LocationPart): "done" | "partial" | "empty" {
    return this.itemState(p.item);
  }

  partReelProgress(p: LocationPart): string {
    return `${p.reels.filter((reel) => reel.scanned).length}/${p.reels.length}`;
  }

  partStockQty(p: LocationPart): number {
    return p.reels.reduce(
      (sum, reel) => sum + (reel.inventory?.availableQty ?? 0),
      0,
    );
  }

  /** Số thùng theo kết quả đối chiếu của part tại vị trí */
  partReadiness(
    p: LocationPart,
  ): Array<{ state: ReelReadiness; label: string; count: number }> {
    return this.readinessCounts(p.item, p.reels);
  }

  /** Số thùng khuyến nghị / đã tra kho xong / sẵn sàng của 1 yêu cầu */
  reelStats(r: ExportRequest): {
    total: number;
    checked: number;
    ready: number;
    exported: number;
  } {
    let total = 0;
    let checked = 0;
    let ready = 0;
    let exported = 0;
    for (const i of r.items) {
      for (const reel of i.reels ?? []) {
        total++;
        const state = this.reelReadiness(i, reel);
        if (state !== "loading") {
          checked++;
        }
        if (state === "ready") {
          ready++;
        }
        if (state === "empty") {
          exported++;
        }
      }
    }
    return { total, checked, ready, exported };
  }

  /** Số thùng sẵn sàng / tổng thùng của 1 vật tư */
  itemReadyText(i: ExportRequestItem): string {
    const reels = i.reels ?? [];
    const ready = reels.filter(
      (reel) => this.reelReadiness(i, reel) === "ready",
    ).length;
    return `${ready}/${reels.length}`;
  }

  hasReels(r: ExportRequest): boolean {
    return r.items.some((i) => !!i.reels?.length);
  }

  /** Khuyến nghị Planning: số thùng đã scan / số thùng khuyến nghị của 1 vật tư */
  reelProgress(i: ExportRequestItem): string {
    const reels = i.reels ?? [];
    return `${reels.filter((x) => x.scanned).length}/${reels.length}`;
  }

  /** Mobile: thùng đã phân bổ cho 1 dòng vật tư */
  boxesOf(i: ExportRequestItem): ExportScannedBox[] {
    return this.ws.allocation.get(i) ?? [];
  }

  /** Mobile — Chi tiết: mở / đóng danh sách thùng của 1 vật tư */
  toggleItemDetail(r: ExportRequest, i: ExportRequestItem): void {
    const key = `${r.code}|${i.materialCode}`;
    if (this.expandedItems.has(key)) {
      this.expandedItems.delete(key);
    } else {
      this.expandedItems.add(key);
    }
  }

  isItemDetailOpen(r: ExportRequest, i: ExportRequestItem): boolean {
    return this.expandedItems.has(`${r.code}|${i.materialCode}`);
  }

  openMobileDetail(): void {
    if (!this.ws.requests.length) {
      this.notificationService.warning(
        `Chưa có ${this.codeNoun} nào — nhập mã ${this.codeNoun} trước.`,
      );
      return;
    }
    this.mobileView = "detail";
  }

  closeMobileDetail(): void {
    this.mobileView = "main";
    this.focus(this.boxInput);
  }

  /** Mobile — Hoàn thành: đủ SL → popup "Xác nhận Xuất hàng" → lưu */
  onMobileComplete(): void {
    if (this.isCompleting) {
      return;
    }
    if (!this.ws.requests.length || !this.ws.boxes.length) {
      this.notificationService.error(
        `Cần nhập mã ${this.codeNoun} và scan ít nhất 1 thùng.`,
      );
      return;
    }
    if (this.ws.requests.some((r) => !this.isEnough(r))) {
      this.notificationService.error("Chưa đủ số lượng yêu cầu");
      return;
    }
    this.confirmAndSubmit();
  }

  // ==================== MOBILE (ô scan có chế độ + 3 tab) ====================

  setMobileScanMode(mode: MobileScanMode): void {
    this.mobileScanMode = mode;
    this.scanRetry = null;
    try {
      sessionStorage.setItem(MOBILE_SCAN_MODE_KEY, mode);
    } catch {
      // không lưu được → chỉ giữ trong phiên hiện tại
    }
    this.focus(this.mobileScanInput);
  }

  /** Enter ở ô scan mobile → xử lý theo chế độ đang chọn */
  onMobileScanEnter(): void {
    const code = this.mobileScanCode.trim();
    if (!code || this.isBusy) {
      return;
    }
    this.scanRetry = null;
    this.mobileScanCode = "";
    this.runMobileScan(code, this.mobileScanMode);
  }

  /** Quét nhầm chế độ → thử lại chính mã vừa quét với chế độ còn lại */
  retryScanAsOther(): void {
    const retry = this.scanRetry;
    if (!retry) {
      return;
    }
    this.scanRetry = null;
    this.mobileScanMode = retry.mode;
    this.runMobileScan(retry.code, retry.mode);
  }

  setMobileTab(tab: MobileTab): void {
    this.mobileTab = tab;
    this.mobileMenuOpen = false;
    if (tab !== "send") {
      this.focus(this.mobileScanInput);
    }
  }

  /** Tổng SL cần xuất / đã scan của tab đang mở (thanh tiến độ trên cùng) */
  get mobileRequiredTotal(): number {
    return this.ws.requests.reduce((s, r) => s + this.requiredTotal(r), 0);
  }

  get mobileScannedTotal(): number {
    return this.ws.requests.reduce((s, r) => s + this.scannedTotal(r), 0);
  }

  get mobilePercent(): number {
    const total = this.mobileRequiredTotal;
    return total ? Math.round((this.mobileScannedTotal / total) * 100) : 0;
  }

  get mobileScannedQty(): number {
    return this.ws.boxes.reduce((s, b) => s + b.quantity, 0);
  }

  /** Vị trí đã lấy đủ (mọi part tại đó đủ SL cần xuất) */
  isGroupDone(g: LocationGroup): boolean {
    return g.parts.every((p) => this.partState(p) === "done");
  }

  /** Số thùng khuyến nghị đã scan / tổng tại 1 vị trí */
  groupReelProgress(g: LocationGroup): string {
    const reels = g.parts.flatMap((p) => p.reels);
    return `${reels.filter((x) => x.scanned).length}/${reels.length}`;
  }

  /** Mobile — Gửi: vật tư còn thiếu (theo SL cần xuất) */
  get mobileMissing(): Array<{
    code: string;
    part: string;
    need: number;
    scanned: number;
  }> {
    return this.ws.requests.flatMap((r) =>
      r.items
        .filter((i) => i.scannedQty < this.targetQty(i))
        .map((i) => ({
          code: r.code,
          part: i.materialCode,
          need: this.targetQty(i),
          scanned: i.scannedQty,
        })),
    );
  }

  openPartSheet(r: ExportRequest, part: LocationPart): void {
    this.sheet = { r, part };
  }

  closeSheet(): void {
    this.sheet = null;
    this.focus(this.mobileScanInput);
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  openAddCode(): void {
    this.mobileMenuOpen = false;
    this.showAddCode = true;
  }

  onMobileAddCode(): void {
    this.onRequestEnter();
    this.showAddCode = false;
  }

  /** Mobile ⋯: Gợi ý lấy hàng cho mọi WO đang mở */
  openMobilePick(): void {
    this.mobileMenuOpen = false;
    const r = this.ws.requests.find((x) => this.hasReels(x));
    if (r) {
      this.openPickSuggestion(r);
    }
  }

  selectTab(tab: MainTab): void {
    if (this.mainTab === tab || (this.lockedTab && tab !== this.lockedTab)) {
      return;
    }
    this.mainTab = tab;
    this.scannedTab = "box";
    this.focus(this.ws.requests.length ? this.boxInput : this.requestInput);
  }

  // ==================== YÊU CẦU ====================

  onRequestEnter(): void {
    const code = this.ws.requestCode.trim();
    if (!code || this.isBusy) {
      return;
    }
    if (
      this.ws.requests.some((r) => r.code.toLowerCase() === code.toLowerCase())
    ) {
      this.notificationService.warning(
        `Yêu cầu "${code}" đã có trong danh sách.`,
      );
      this.ws.requestCode = "";
      return;
    }
    this.isBusy = true;
    const lookup$ = this.isPlanning
      ? this.exportService.getPlanningRequest(code)
      : this.exportService.getRequest(code);
    lookup$.subscribe({
      next: (req) => {
        this.isBusy = false;
        this.ws.requestCode = "";
        if (!req) {
          this.notificationService.error(
            `Không tìm thấy ${this.codeNoun} "${code}".`,
          );
          this.focus(this.requestInput);
          return;
        }
        this.addLoadedRequest(req);
        this.focus(this.boxInput);
      },
      error: () => {
        this.isBusy = false;
        this.notificationService.error(
          `Lỗi khi tra ${this.codeNoun} "${code}".`,
        );
      },
    });
  }

  toggleRequest(r: ExportRequest): void {
    if (this.ws.expanded.has(r.code)) {
      this.ws.expanded.delete(r.code);
    } else {
      this.ws.expanded.add(r.code);
    }
  }

  isExpanded(r: ExportRequest): boolean {
    return this.ws.expanded.has(r.code);
  }

  /**
   * Khuyến nghị Planning: SL của các thùng khuyến nghị đã xuất (có trong kho, tồn = 0).
   * Yêu cầu lĩnh vật tư: 0.
   */
  exportedQty(i: ExportRequestItem): number {
    return (i.reels ?? [])
      .filter(
        (reel) =>
          !reel.scannedBefore &&
          !!reel.inventory &&
          reel.inventory.availableQty <= 0,
      )
      .reduce((sum, reel) => sum + reel.quantity, 0);
  }

  /** SL cần xuất = SL yêu cầu − SL đã xuất */
  targetQty(i: ExportRequestItem): number {
    return Math.max(0, i.requiredQty - this.exportedQty(i));
  }

  isEnough(r: ExportRequest): boolean {
    return r.items.every((i) => i.scannedQty >= this.targetQty(i));
  }

  requiredTotal(r: ExportRequest): number {
    return r.items.reduce((s, i) => s + this.targetQty(i), 0);
  }

  /** SL đã scan tính vào tiến độ (mỗi vật tư tối đa bằng SL cần xuất) */
  scannedTotal(r: ExportRequest): number {
    return r.items.reduce(
      (s, i) => s + Math.min(i.scannedQty, this.targetQty(i)),
      0,
    );
  }

  itemState(i: ExportRequestItem): "done" | "partial" | "empty" {
    if (i.scannedQty >= this.targetQty(i)) {
      return "done";
    }
    return i.scannedQty > 0 ? "partial" : "empty";
  }

  // ==================== THÙNG ĐÃ SCAN: SỬA SL / VỊ TRÍ ====================

  /** Sửa SL xuất của thùng: không âm, không vượt tồn kho (nếu biết) */
  onBoxQtyChange(b: ExportScannedBox, value: number | null): void {
    let qty = Number(value);
    if (!Number.isFinite(qty) || qty < 0) {
      qty = 0;
    }
    if (b.inventoryQty !== undefined && qty > b.inventoryQty) {
      this.notificationService.warning(
        `SL xuất thùng "${b.reelId}" không được vượt tồn kho (${b.inventoryQty}).`,
      );
      qty = b.inventoryQty;
    }
    b.quantity = qty;
    this.recalcScanned();
  }

  /** Gõ vị trí → tìm trong IndexedDB */
  onLocationSearch(term: string): void {
    const keyword = (term ?? "").trim();
    if (!keyword) {
      this.locationOptions = [];
      return;
    }
    void this.warehouseCache
      .searchByName(keyword)
      .then((list) => {
        this.locationOptions = list.slice(0, 30);
        this.cdr.markForCheck();
      })
      .catch(() => {
        this.locationOptions = [];
      });
  }

  onLocationSelected(b: ExportScannedBox, value: string): void {
    b.location = value;
  }

  /** Áp vị trí ở header cho cả cột (mọi thùng đã scan của tab đang mở) */
  applyLocationToAll(value?: string): void {
    const location = (value ?? this.bulkLocation).trim();
    if (!location || !this.ws.boxes.length) {
      return;
    }
    this.bulkLocation = location;
    for (const b of this.ws.boxes) {
      b.location = location;
    }
    for (const pallet of this.ws.pallets) {
      pallet.location = location;
    }
    this.notificationService.success(
      `Đã áp vị trí "${location}" cho ${this.ws.boxes.length} thùng.`,
    );
  }

  // ==================== SCAN ====================

  onPalletEnter(): void {
    const code = this.ws.palletCode.trim();
    if (!code || this.isBusy || !this.ensureRequest()) {
      return;
    }
    if (
      this.ws.pallets.some(
        (p) => p.palletCode.toLowerCase() === code.toLowerCase(),
      )
    ) {
      this.notificationService.warning(`Pallet "${code}" đã được scan.`);
      this.ws.palletCode = "";
      return;
    }
    if (this.isPlanning) {
      this.scanPlannedPallet(code);
      return;
    }
    this.isBusy = true;
    this.exportService.getPallet(code).subscribe({
      next: (pallet) => {
        this.isBusy = false;
        this.ws.palletCode = "";
        if (!pallet) {
          this.notificationService.error(
            `Mã pallet không tồn tại trong hệ thống ("${code}").`,
          );
          this.setScanRetry(code, "box");
          this.scanFeedback(false);
          this.focus(this.palletInput);
          return;
        }
        const scanned = new Set(
          this.ws.boxes.map((b) => b.reelId.toLowerCase()),
        );
        const fresh = pallet.boxes.filter(
          (b) => !scanned.has(b.reelId.toLowerCase()),
        );
        const usable = fresh.filter(
          (b) => b.quantity > 0 && this.findItems(b.materialCode).length,
        );
        const skipped =
          Math.max(pallet.reelCount ?? 0, pallet.boxes.length) - usable.length;
        if (!usable.length) {
          this.notificationService.warning(
            !pallet.boxes.length
              ? `Pallet "${pallet.palletCode}" không có thùng nào trong kho.`
              : fresh.length
                ? `Pallet "${pallet.palletCode}" không có thùng nào thuộc danh sách yêu cầu (hoặc đã hết tồn).`
                : `Các thùng của pallet "${pallet.palletCode}" đã được scan.`,
          );
          this.scanFeedback(false);
          this.focus(this.palletInput);
          return;
        }
        this.ws.pallets = [...this.ws.pallets, { ...pallet, boxes: usable }];
        this.ws.boxes = [...this.ws.boxes, ...usable];
        this.scanFeedback(true);
        this.recalcScanned();
        this.notificationService.success(
          `Đã thêm pallet "${pallet.palletCode}" (${usable.length} thùng)` +
            (skipped
              ? `, bỏ qua ${skipped} thùng đã scan / không thuộc yêu cầu / hết tồn / không có trong kho.`
              : "."),
        );
        this.focus(this.palletInput);
      },
      error: () => {
        this.isBusy = false;
        this.notificationService.error(`Lỗi khi tra pallet "${code}".`);
      },
    });
  }

  onBoxEnter(): void {
    const reelId = extractReelIdFromScan(this.ws.boxCode);
    if (!reelId || this.isBusy || !this.ensureRequest()) {
      return;
    }
    if (
      this.ws.boxes.some((b) => b.reelId.toLowerCase() === reelId.toLowerCase())
    ) {
      this.notificationService.warning(`Thùng "${reelId}" đã được scan.`);
      this.ws.boxCode = "";
      return;
    }
    if (this.isPlanning) {
      this.scanPlannedReel(reelId);
      return;
    }
    this.isBusy = true;
    this.exportService.getBox(reelId).subscribe({
      next: (box) => {
        this.isBusy = false;
        this.ws.boxCode = "";
        if (!box) {
          this.notificationService.error(
            `Mã thùng không tồn tại trong hệ thống ("${reelId}").`,
          );
          this.setScanRetry(reelId, "pallet");
          this.scanFeedback(false);
        } else if (box.quantity <= 0) {
          this.notificationService.warning(
            `Thùng "${reelId}" không còn tồn khả dụng` +
              (box.inventoryStatus ? ` (${box.inventoryStatus}).` : "."),
          );
          this.scanFeedback(false);
        } else if (!this.findItems(box.materialCode).length) {
          this.notificationService.warning(
            `Thùng chứa vật tư không tồn tại trong danh sách (${box.materialCode || "không rõ mã SAP"}).`,
          );
          this.scanFeedback(false);
        } else {
          this.scanFeedback(true);
          this.ws.boxes = [...this.ws.boxes, box];
          this.scannedTab = "box";
          this.recalcScanned();
        }
        this.focus(this.boxInput);
      },
      error: () => {
        this.isBusy = false;
        this.notificationService.error(`Lỗi khi tra thùng "${reelId}".`);
      },
    });
  }

  removeBox(box: ExportScannedBox): void {
    this.ws.boxes = this.ws.boxes.filter((b) => b !== box);
    this.ws.pallets = this.ws.pallets
      .map((p) => ({
        ...p,
        boxes: p.boxes.filter((b) => b.reelId !== box.reelId),
      }))
      .filter((p) => p.boxes.length > 0);
    this.recalcScanned();
  }

  removePallet(pallet: ExportScannedPallet): void {
    const ids = new Set(pallet.boxes.map((b) => b.reelId));
    this.ws.boxes = this.ws.boxes.filter((b) => !ids.has(b.reelId));
    this.ws.pallets = this.ws.pallets.filter((p) => p !== pallet);
    this.recalcScanned();
  }

  palletQty(p: ExportScannedPallet): number {
    return p.boxes.reduce((s, b) => s + b.quantity, 0);
  }

  // ==================== HÀNH ĐỘNG ====================

  /**
   * Khuyến nghị Planning — Gợi ý lấy hàng theo vị trí: các part còn thiếu
   * (Cần xuất − Đã scan) → tra cuộn đang có trong kho, chọn đủ SL, gom theo vị trí.
   */
  openPickSuggestion(r: ExportRequest, event?: Event): void {
    event?.stopPropagation();
    const parts = r.items
      .filter((i) => !!i.reels)
      .map((i) => ({
        partNumber: i.materialCode,
        needQty: Math.max(0, this.targetQty(i) - i.scannedQty),
      }))
      .filter((p) => p.needQty > 0);
    if (!parts.length) {
      this.notificationService.info(
        `${r.code}: đã scan đủ, không cần lấy thêm.`,
      );
      return;
    }
    this.dialog.open<PickSuggestionDialogComponent, PickSuggestionDialogData>(
      PickSuggestionDialogComponent,
      {
        width: "960px",
        maxWidth: "96vw",
        autoFocus: false,
        data: {
          title: r.code,
          parts,
          excludeReelIds: this.ws.boxes.map((b) => b.reelId),
        },
      },
    );
  }

  /** Mở trang Lịch sử xuất kho (component riêng) */
  onHistory(): void {
    this.dialog.open(ExportHistoryDialogComponent, {
      width: "100vw",
      height: "100vh",
      maxWidth: "100vw",
      maxHeight: "100vh",
      panelClass: "warehouse-export-dialog-panel",
      autoFocus: false,
    });
  }

  /**
   * Hủy: có yêu cầu / dữ liệu đã scan (ở bất kỳ tab nào) → popup "Xác nhận hủy"
   * (toàn bộ dữ liệu scan bị xóa); không có gì → đóng luôn.
   */
  onCancel(): void {
    const all = [this.workspaces.request, this.workspaces.planning];
    const requestCodes = all.flatMap((w) => w.requests.map((r) => r.code));
    const boxes = all.flatMap((w) => w.boxes);
    if (!requestCodes.length && !boxes.length) {
      this.dialogRef.close(false);
      return;
    }
    openExportCancel(this.dialog, {
      requestCodes,
      boxCount: boxes.length,
      scannedQty: boxes.reduce((s, b) => s + b.quantity, 0),
    }).subscribe((ok) => {
      if (ok) {
        this.dialogRef.close(false);
      }
    });
  }

  /**
   * Gửi: gửi thông tin đã scan cho người duyệt (chưa xuất kho, chưa đóng màn hình).
   * Không bắt buộc scan đủ SL — chỉ cần có yêu cầu, ít nhất 1 thùng và thông tin gửi.
   */
  onSend(): void {
    if (this.isSending || this.isCompleting) {
      return;
    }
    if (!this.ws.requests.length || !this.ws.boxes.length) {
      this.notificationService.warning(
        `Cần nhập mã ${this.codeNoun} và scan ít nhất 1 thùng trước khi gửi.`,
      );
      return;
    }
    if (this.isPlanning) {
      this.sendPlanningScans();
      return;
    }
    if (!this.ensureApproval()) {
      return;
    }
    this.isSending = true;
    this.exportService
      .send(this.ws.requests, this.ws.boxes, this.approval)
      .subscribe({
        next: (ok) => {
          this.isSending = false;
          if (ok) {
            this.notificationService.success(
              `Đã gửi ${this.ws.requests.length} ${this.codeNoun} (${this.ws.boxes.length} thùng) cho ${this.approval.approver}.`,
            );
          } else {
            this.notificationService.error("Gửi không thành công.");
          }
        },
        error: () => {
          this.isSending = false;
          this.notificationService.error("Lỗi khi gửi.");
        },
      });
  }

  onComplete(): void {
    if (this.isCompleting) {
      return;
    }
    if (!this.ws.requests.length || !this.ws.boxes.length) {
      this.notificationService.warning(
        "Cần nhập mã yêu cầu và scan ít nhất 1 thùng trước khi hoàn thành.",
      );
      return;
    }
    // Chỉ được xuất kho khi scan đủ SL của tất cả yêu cầu
    const missing = this.ws.requests.filter((r) => !this.isEnough(r));
    if (missing.length) {
      openExportIncomplete(this.dialog, {
        requiredTotal: this.ws.requests.reduce(
          (s, r) => s + this.requiredTotal(r),
          0,
        ),
        scannedTotal: this.ws.requests.reduce(
          (s, r) => s + this.scannedTotal(r),
          0,
        ),
        requests: missing.map((r) => ({
          code: r.code,
          items: r.items
            .filter((i) => i.scannedQty < this.targetQty(i))
            .map((i) => ({
              materialCode: i.materialCode,
              requiredQty: this.targetQty(i),
              scannedQty: i.scannedQty,
            })),
        })),
      }).subscribe(() => this.focus(this.boxInput));
      return;
    }
    this.confirmAndSubmit();
  }

  trackBox(_: number, b: ExportScannedBox): string {
    return b.reelId;
  }

  // ==================== NỘI BỘ ====================

  /** Chưa có yêu cầu → nhắc nhập mã yêu cầu trước */
  /** Thiếu thông tin gửi (kho duyệt / người duyệt / người quản lý kho) → báo + false */
  private ensureApproval(): boolean {
    const missing = [
      this.approval.warehouseCode ? "" : "kho duyệt",
      this.approval.approver ? "" : "người duyệt",
      this.approval.warehouseManager ? "" : "người quản lý kho",
    ].filter(Boolean);
    if (missing.length) {
      this.notificationService.warning(
        `Chọn ${missing.join(", ")} trước khi xuất.`,
      );
      return false;
    }
    if (
      !this.approvers.includes(this.approval.approver) ||
      !this.approvers.includes(this.approval.warehouseManager)
    ) {
      this.notificationService.warning(
        "Người duyệt / người quản lý kho phải chọn trong danh sách.",
      );
      return false;
    }
    return true;
  }

  /**
   * Khuyến nghị Planning — Gửi: popup xác nhận → PATCH /dnlnvl-detail-details cho từng thùng
   * vừa scan (thùng khuyến nghị chưa scan: scanCheck 0 / null). Thùng gửi thành công được
   * đánh dấu "đã scan trước" và bỏ khỏi danh sách đã scan; thùng lỗi giữ lại để gửi lại.
   */
  private sendPlanningScans(): void {
    const targets = this.ws.boxes
      .map((box) => ({ box, planned: this.findPlannedReel(box.reelId) }))
      .filter(
        (
          x,
        ): x is {
          box: ExportScannedBox;
          planned: { item: ExportRequestItem; reel: ExportPlannedReel };
        } =>
          !!x.planned &&
          !x.planned.reel.scannedBefore &&
          !!x.planned.reel.detailDetailId,
      );
    if (!targets.length) {
      this.notificationService.warning(
        "Không có thùng mới nào để gửi (thùng phải thuộc khuyến nghị và chưa scan trước đó).",
      );
      return;
    }
    const skipped = this.ws.boxes.length - targets.length;
    openChoiceDialog(this.dialog, {
      title: "Xác nhận gửi",
      message:
        `Gửi cập nhật đã scan cho ${targets.length} thùng ` +
        `của ${this.ws.requests.map((r) => r.code).join(", ")}?` +
        (skipped
          ? ` (${skipped} thùng không thuộc khuyến nghị sẽ không được gửi.)`
          : ""),
      confirmText: "Gửi",
      cancelText: "Hủy",
      tone: "info",
    }).subscribe((ok) => {
      if (ok) {
        this.runPlanningSend(targets);
      }
    });
  }

  private runPlanningSend(
    targets: Array<{
      box: ExportScannedBox;
      planned: { item: ExportRequestItem; reel: ExportPlannedReel };
    }>,
  ): void {
    this.isSending = true;
    from(targets)
      .pipe(
        mergeMap(
          (t) =>
            this.exportService
              .markPlanningReelScanned(t.planned.reel.detailDetailId!)
              .pipe(
                map(() => ({ t, ok: true })),
                catchError(() => of({ t, ok: false })),
              ),
          SEND_CONCURRENCY,
        ),
        toArray(),
      )
      .subscribe((results) => {
        this.isSending = false;
        const sent = results.filter((x) => x.ok).map((x) => x.t);
        const failed = results.filter((x) => !x.ok).map((x) => x.t);
        // Thùng đã gửi → BE đã ghi nhận scan: đánh dấu + bỏ khỏi danh sách vừa scan
        const sentIds = new Set(sent.map((t) => t.box.reelId.toLowerCase()));
        for (const t of sent) {
          t.planned.reel.scannedBefore = true;
        }
        this.ws.boxes = this.ws.boxes.filter(
          (b) => !sentIds.has(b.reelId.toLowerCase()),
        );
        this.ws.pallets = this.ws.pallets
          .map((p) => ({
            ...p,
            boxes: p.boxes.filter((b) => !sentIds.has(b.reelId.toLowerCase())),
          }))
          .filter((p) => p.boxes.length > 0);
        this.recalcScanned();
        if (sent.length) {
          this.notificationService.success(`Đã gửi ${sent.length} thùng.`);
        }
        if (failed.length) {
          this.notificationService.error(
            `Gửi lỗi ${failed.length} thùng: ${failed
              .slice(0, 5)
              .map((t) => t.box.reelId)
              .join(
                ", ",
              )}${failed.length > 5 ? "…" : ""} — bấm Gửi để thử lại.`,
          );
        }
      });
  }

  /** Đủ thông tin gửi → popup "Xác nhận xuất" → lưu */
  private confirmAndSubmit(): void {
    if (!this.ensureApproval()) {
      return;
    }
    const wh = APPROVAL_WAREHOUSES.find(
      (w) => w.code === this.approval.warehouseCode,
    );
    openChoiceDialog(this.dialog, {
      title: "Xác nhận Xuất hàng",
      message:
        `Xuất ${this.ws.boxes.length} thùng cho ${this.ws.requests.length} yêu cầu. ` +
        `Kho duyệt: ${wh ? `${wh.code} - ${wh.name}` : this.approval.warehouseCode}, ` +
        `người duyệt: ${this.approval.approver}, quản lý kho: ${this.approval.warehouseManager}.`,
      confirmText: "Xác nhận",
      cancelText: "Hủy",
      tone: "info",
    }).subscribe((ok) => {
      if (ok) {
        this.submitComplete();
      }
    });
  }

  /** Nạp sẵn các yêu cầu đã chọn ở danh sách (tab Yêu cầu lĩnh vật tư) */
  private preloadRequests(codes: string[]): void {
    this.mainTab = this.lockedTab ?? "request";
    this.isBusy = true;
    forkJoin(
      codes.map((code) =>
        (this.isPlanning
          ? this.exportService.getPlanningRequest(code)
          : this.exportService.getRequest(code)
        ).pipe(catchError(() => of(null as ExportRequest | null))),
      ),
    ).subscribe((list: Array<ExportRequest | null>) => {
      this.isBusy = false;
      const missing: string[] = [];
      list.forEach((req, idx) => {
        if (!req) {
          missing.push(codes[idx]);
          return;
        }
        if (!this.ws.requests.some((r) => r.code === req.code)) {
          this.addLoadedRequest(req);
        }
      });
      if (missing.length) {
        this.notificationService.warning(
          `Không tìm thấy ${this.codeNoun}: ${missing.join(", ")}.`,
        );
      }
      this.focus(this.boxInput);
    });
  }

  /** Thêm 1 yêu cầu / WO đã tra vào tab đang mở (WO Planning: tra kho từng thùng) */
  private addLoadedRequest(req: ExportRequest): void {
    this.ws.requests = [...this.ws.requests, req];
    this.ws.expanded.add(req.code);
    this.recalcScanned();
    if (req.items.some((i) => i.reels?.length)) {
      this.loadReelInventories(req);
      this.ensureAreaIndex();
    } else if (req.items.length) {
      // Xuất chuyển kho: vị trí mọi mã lấy từ 1 bảng chung (tải 1 lần / phiên)
      this.loadItemStocks(req.items.map((i) => i.materialCode));
    }
  }

  /** Xuất chuyển kho: tra vị trí của các mã vật tư — song song có giới hạn */
  private loadItemStocks(materialCodes: string[], forceRefresh = false): void {
    const codes = [
      ...new Set(materialCodes.map((c) => c.trim()).filter(Boolean)),
    ].filter((code) => !this.itemStocks.get(code.toLowerCase())?.loading);
    if (!codes.length) {
      return;
    }
    for (const code of codes) {
      const prev = this.itemStocks.get(code.toLowerCase());
      this.itemStocks.set(code.toLowerCase(), {
        loading: true,
        error: false,
        locations: prev?.locations ?? [],
        fetchedAt: prev?.fetchedAt ?? 0,
        failedAreas: 0,
      });
    }
    const sub = from(codes)
      .pipe(
        mergeMap(
          (code) =>
            this.exportService.getSapLocations(code, forceRefresh).pipe(
              map(
                (res: {
                  locations: ItemStockLocation[];
                  fetchedAt: number;
                  failedAreas: number;
                }) => ({ code, res }),
              ),
              catchError(() =>
                of({
                  code,
                  res: null as {
                    locations: ItemStockLocation[];
                    fetchedAt: number;
                    failedAreas: number;
                  } | null,
                }),
              ),
            ),
          ITEM_STOCK_CONCURRENCY,
        ),
      )
      .subscribe(({ code, res }) => {
        const prev = this.itemStocks.get(code.toLowerCase());
        this.itemStocks.set(code.toLowerCase(), {
          loading: false,
          error: !res,
          locations: res?.locations ?? prev?.locations ?? [],
          fetchedAt: res?.fetchedAt ?? prev?.fetchedAt ?? 0,
          failedAreas: res?.failedAreas ?? 0,
        });
        this.cdr.markForCheck();
      });
    this.inventorySubs.push(sub);
  }

  private runMobileScan(code: string, mode: MobileScanMode): void {
    if (mode === "pallet") {
      this.ws.palletCode = code;
      this.onPalletEnter();
    } else {
      this.ws.boxCode = code;
      this.onBoxEnter();
    }
  }

  /** Mobile: gợi ý quét lại bằng chế độ còn lại (không tự chuyển) */
  private setScanRetry(code: string, mode: MobileScanMode): void {
    if (this.isMobile) {
      this.scanRetry = { code, mode };
    }
  }

  /** Phản hồi sau mỗi lần scan trên mobile: âm thanh + rung */
  private scanFeedback(ok: boolean): void {
    if (!this.isMobile) {
      return;
    }
    try {
      navigator.vibrate?.(ok ? 60 : [80, 60, 80]);
    } catch {
      // thiết bị không hỗ trợ rung
    }
    try {
      const audio = new Audio(
        ok
          ? "content/images/successed-295058.mp3"
          : "content/images/beep_warning.mp3",
      );
      void audio.play().catch(() => undefined);
    } catch {
      // trình duyệt chặn âm thanh
    }
  }

  private loadMobileScanMode(): MobileScanMode {
    try {
      return sessionStorage.getItem(MOBILE_SCAN_MODE_KEY) === "pallet"
        ? "pallet"
        : "box";
    } catch {
      return "box";
    }
  }

  /** Lưu xuất kho của tab đang mở */
  private submitComplete(): void {
    this.isCompleting = true;
    this.exportService
      .complete(this.ws.requests, this.ws.boxes, this.approval)
      .subscribe({
        next: (done) => {
          this.isCompleting = false;
          if (!done) {
            this.notificationService.error("Xuất hàng thất bại.");
            return;
          }
          this.notificationService.success("Xuất hàng thành công");
          this.dialogRef.close(true);
        },
        error: () => {
          this.isCompleting = false;
          this.notificationService.error("Xuất hàng thất bại.");
        },
      });
  }

  private detectMobile(): boolean {
    return (
      typeof window !== "undefined" && window.innerWidth <= MOBILE_BREAKPOINT
    );
  }

  private ensureRequest(): boolean {
    if (this.ws.requests.length) {
      return true;
    }
    this.notificationService.warning(
      `Nhập mã ${this.codeNoun} trước khi scan.`,
    );
    this.ws.palletCode = "";
    this.ws.boxCode = "";
    this.focus(this.requestInput);
    return false;
  }

  /**
   * Khuyến nghị Planning — scan 1 thùng: tra kho, thùng có trong khuyến nghị thì tích chọn,
   * không có nhưng cùng part với WO thì bổ sung để xuất cho đủ SL khuyến nghị.
   */
  private scanPlannedReel(reelId: string): void {
    this.ws.boxCode = "";
    this.isBusy = true;
    this.resolvePlanningReel(reelId).subscribe((res) => {
      this.isBusy = false;
      if (!res.box) {
        this.notificationService.error(this.planningRejectText(res));
        if (res.reason === "notInStock") {
          this.setScanRetry(res.reelId, "pallet");
        }
        this.scanFeedback(false);
      } else {
        this.scanFeedback(true);
        this.ws.boxes = [...this.ws.boxes, res.box];
        this.scannedTab = "box";
        this.recalcScanned();
        if (res.box.supplementary) {
          this.notificationService.info(
            `Thùng "${res.reelId}" không có trong khuyến nghị — đã bổ sung cho part ${res.box.materialCode}.`,
          );
        }
      }
      this.focus(this.boxInput);
    });
  }

  /**
   * Khuyến nghị Planning — scan pallet: lấy mã thùng trên pallet
   * (/api/pallet-mngts/serial-pallet/{serial}) rồi xử lý từng thùng như scan thùng.
   */
  private scanPlannedPallet(code: string): void {
    this.isBusy = true;
    this.ws.palletCode = "";
    this.exportService
      .getPalletReelIds(code)
      .pipe(
        switchMap((reelIds) => {
          if (reelIds === null) {
            return of(null);
          }
          const scanned = new Set(
            this.ws.boxes.map((b) => b.reelId.toLowerCase()),
          );
          const fresh = reelIds.filter((id) => !scanned.has(id.toLowerCase()));
          const alreadyScanned = reelIds.length - fresh.length;
          const lookups: Array<Observable<PlanningScanResult>> = fresh.map(
            (id: string): Observable<PlanningScanResult> =>
              this.resolvePlanningReel(id),
          );
          return (lookups.length ? forkJoin(lookups) : of([])).pipe(
            map((list: PlanningScanResult[]) => ({
              total: reelIds.length,
              alreadyScanned,
              list,
            })),
          );
        }),
      )
      .subscribe({
        next: (result) => {
          this.isBusy = false;
          if (!result) {
            this.notificationService.error(
              `Mã pallet không tồn tại trong hệ thống ("${code}").`,
            );
            this.setScanRetry(code, "box");
            this.scanFeedback(false);
            this.focus(this.palletInput);
            return;
          }
          if (!result.total) {
            this.notificationService.warning(
              `Pallet "${code}" không có thùng nào.`,
            );
            this.focus(this.palletInput);
            return;
          }
          const added = result.list
            .filter((x) => !!x.box)
            .map((x) => ({ ...x.box!, palletCode: code }));
          const count = (reason: PlanningScanResult["reason"]): number =>
            result.list.filter((x) => x.reason === reason).length;
          const skipped = [
            result.alreadyScanned ? `${result.alreadyScanned} đã scan` : "",
            count("notInWo")
              ? `${count("notInWo")} không thuộc part của WO`
              : "",
            count("notPlanned")
              ? `${count("notPlanned")} không có trong khuyến nghị`
              : "",
            count("scannedBefore")
              ? `${count("scannedBefore")} đã scan trước đó`
              : "",
            count("notInStock")
              ? `${count("notInStock")} không có trong kho`
              : "",
            count("exported") ? `${count("exported")} đã xuất / hết tồn` : "",
            count("error") ? `${count("error")} lỗi tra kho` : "",
          ].filter(Boolean);
          if (!added.length) {
            this.notificationService.error(
              count("notInWo") + count("notPlanned") === result.total
                ? `Pallet không có thùng nào trong khuyến nghị của WO ("${code}").`
                : `Pallet "${code}" không có thùng nào thêm được (${skipped.join(", ")}).`,
            );
            this.focus(this.palletInput);
            return;
          }
          this.ws.pallets = [
            ...this.ws.pallets,
            { palletCode: code, location: added[0].location, boxes: added },
          ];
          this.ws.boxes = [...this.ws.boxes, ...added];
          this.scanFeedback(true);
          this.scannedTab = "box";
          this.recalcScanned();
          const extra = added.filter((b) => b.supplementary).length;
          this.notificationService.success(
            `Pallet "${code}": đã thêm ${added.length}/${result.total} thùng` +
              (extra ? `, ${extra} thùng bổ sung ngoài khuyến nghị` : "") +
              (skipped.length ? ` (bỏ qua ${skipped.join(", ")}).` : "."),
          );
          this.focus(this.palletInput);
        },
        error: () => {
          this.isBusy = false;
          this.notificationService.error(`Lỗi khi tra pallet "${code}".`);
          this.focus(this.palletInput);
        },
      });
  }

  /**
   * Tra 1 thùng scan ở tab Planning → thùng xuất (hoặc lý do không thêm được):
   *  - không có trong kho → notInStock; tồn = 0 → exported (đã xuất)
   *  - có trong khuyến nghị → dùng dòng vật tư của khuyến nghị
   *  - không có → tìm part cùng WO (partNumber = mã part, hoặc partId = partNumberCode),
   *    không có part → notInWo; có → bổ sung (supplementary)
   */
  private resolvePlanningReel(reelId: string): Observable<PlanningScanResult> {
    const planned = this.findPlannedReel(reelId);
    if (!planned) {
      // Chỉ nhận thùng có trong khuyến nghị (mới có dnlnvlDetailDetailId để gửi cập nhật)
      return of({ reelId, box: null, reason: "notPlanned", planned: false });
    }
    if (planned.reel.scannedBefore) {
      return of({
        reelId: planned.reel.reelId,
        box: null,
        reason: "scannedBefore",
        planned: true,
      });
    }
    const inv$: Observable<ExportReelInventory | null> =
      planned &&
      planned.reel.inventory !== undefined &&
      !planned.reel.inventoryError
        ? of(planned.reel.inventory)
        : this.exportService.getReelInventory(planned?.reel.reelId ?? reelId);
    return inv$.pipe(
      map((inv: ExportReelInventory | null): PlanningScanResult => {
        if (planned) {
          planned.reel.inventory = inv;
          planned.reel.inventoryError = false;
        }
        const id = planned?.reel.reelId ?? reelId;
        if (!inv) {
          return {
            reelId: id,
            box: null,
            reason: "notInStock",
            planned: !!planned,
          };
        }
        if (inv.availableQty <= 0) {
          return {
            reelId: id,
            box: null,
            reason: "exported",
            planned: !!planned,
          };
        }
        const item = planned?.item ?? this.findItemByInventory(inv);
        if (!item) {
          return { reelId: id, box: null, reason: "notInWo", planned: false };
        }
        return {
          reelId: id,
          planned: !!planned,
          box: {
            reelId: id,
            materialCode: item.materialCode,
            quantity: inv.availableQty,
            lot: inv.lot,
            mfgDate: inv.manufacturingDate,
            palletCode: "",
            location: inv.location,
            plannedQty: planned?.reel.quantity,
            supplementary: !planned,
            inventoryQty: inv.availableQty,
            expirationDate: inv.expirationDate,
            inventoryStatus: inv.status,
          },
        };
      }),
      catchError(
        (): Observable<PlanningScanResult> =>
          of({ reelId, box: null, reason: "error", planned: !!planned }),
      ),
    );
  }

  private planningRejectText(res: PlanningScanResult): string {
    switch (res.reason) {
      case "notInStock":
        return `Mã thùng không tồn tại trong hệ thống ("${res.reelId}").`;
      case "exported":
        return `Thùng "${res.reelId}" đã xuất (tồn kho = 0).`;
      case "notPlanned":
        return `Thùng "${res.reelId}" không có trong khuyến nghị của WO.`;
      case "scannedBefore":
        return `Thùng "${res.reelId}" đã được scan trước đó (đã ghi nhận trên hệ thống).`;
      case "notInWo":
        return `Thùng chứa vật tư không tồn tại trong danh sách ("${res.reelId}").`;
      default:
        return `Lỗi khi tra tồn kho thùng "${res.reelId}".`;
    }
  }

  /** partNumberCode hợp lệ của 1 dòng part (part chính + part thay thế + part của cuộn), chữ thường */
  private partCodesOf(
    i: ExportRequestItem,
    reel?: ExportPlannedReel,
  ): string[] {
    return [i.partCode, ...(i.altPartCodes ?? []), reel?.partCode]
      .filter((c): c is string => !!c)
      .map((c) => c.toLowerCase());
  }

  /** Dòng vật tư (part) của WO khớp với thùng trong kho */
  private findItemByInventory(
    inv: ExportReelInventory,
  ): ExportRequestItem | undefined {
    const partNumber = inv.partNumber.toLowerCase();
    const partId = inv.partId.toLowerCase();
    for (const r of this.ws.requests) {
      const item = r.items.find(
        (i) =>
          !!i.reels &&
          ((!!partNumber && i.materialCode.toLowerCase() === partNumber) ||
            (!!partId && this.partCodesOf(i).includes(partId))),
      );
      if (item) {
        return item;
      }
    }
    return undefined;
  }

  /** Tra tồn kho mọi thùng khuyến nghị của 1 yêu cầu (song song có giới hạn) */
  private loadReelInventories(req: ExportRequest): void {
    const reels = req.items.flatMap((item) =>
      (item.reels ?? []).map((reel) => ({ item, reel })),
    );
    const sub = from(reels)
      .pipe(
        mergeMap(
          ({ item, reel }) =>
            this.exportService.getReelInventory(reel.reelId).pipe(
              map((inv) => ({ item, reel, inv, error: false })),
              catchError(() =>
                of({
                  item,
                  reel,
                  inv: undefined as ExportReelInventory | null | undefined,
                  error: true,
                }),
              ),
            ),
          INVENTORY_CONCURRENCY,
        ),
      )
      .subscribe(({ item, reel, inv, error }) => {
        reel.inventory = error ? undefined : inv;
        reel.inventoryError = error;
        // dnlnvl chỉ có partNumberCode → lấy part number (vd 1N4148W) + mã SAP từ kho
        if (
          inv &&
          item.materialCode === item.partCode &&
          inv.partNumber &&
          inv.partId.toLowerCase() === (item.partCode ?? "").toLowerCase()
        ) {
          item.materialCode = inv.partNumber;
          item.materialName ||= inv.sapCode;
        }
        this.cdr.markForCheck();
      });
    this.inventorySubs.push(sub);
  }

  /** Tải bảng vị trí → kho (1 lần / phiên) */
  private ensureAreaIndex(): void {
    if (this.areaIndex) {
      return;
    }
    this.exportService.getLocationAreaIndex().subscribe({
      next: (index: AreaIndexRef) => {
        this.areaIndex = index;
        this.cdr.markForCheck();
      },
      error: () => {
        this.notificationService.warning(
          "Không tải được danh sách kho / vị trí — cột Kho sẽ để trống.",
        );
      },
    });
  }

  /** Thùng khuyến nghị (Planning) theo ReelID */
  private findPlannedReel(
    reelId: string,
  ): { item: ExportRequestItem; reel: ExportPlannedReel } | null {
    const id = reelId.toLowerCase();
    for (const r of this.ws.requests) {
      for (const item of r.items) {
        const reel = item.reels?.find((x) => x.reelId.toLowerCase() === id);
        if (reel) {
          return { item, reel };
        }
      }
    }
    return null;
  }

  private findPlanningItem(partCode: string): ExportRequestItem | undefined {
    const code = partCode.toLowerCase();
    for (const r of this.ws.requests) {
      const item = r.items.find(
        (i) => !!i.reels && i.materialCode.toLowerCase() === code,
      );
      if (item) {
        return item;
      }
    }
    return undefined;
  }

  private findItems(materialCode: string): ExportRequestItem[] {
    const code = materialCode.toLowerCase();
    return this.ws.requests.flatMap((r) =>
      r.items.filter((i) => i.materialCode.toLowerCase() === code),
    );
  }

  /**
   * Phân bổ SL các thùng đã scan vào vật tư của các yêu cầu: lấp đầy yêu cầu thêm
   * trước, dư thì cộng vào dòng cuối cùng cùng vật tư.
   */
  private recalcScanned(): void {
    for (const r of this.ws.requests) {
      for (const i of r.items) {
        i.scannedQty = 0;
      }
    }
    const allocation = new Map<ExportRequestItem, ExportScannedBox[]>();
    // Khuyến nghị Planning: tích thùng khuyến nghị đã scan, SL đã scan = SL khuyến nghị
    const scannedIds = new Set(
      this.ws.boxes.map((b) => b.reelId.toLowerCase()),
    );
    for (const r of this.ws.requests) {
      for (const item of r.items) {
        for (const reel of item.reels ?? []) {
          const local = scannedIds.has(reel.reelId.toLowerCase());
          reel.scanned = !!reel.scannedBefore || local;
          // Thùng BE đã ghi nhận scan → tính vào SL đã scan của part
          if (reel.scannedBefore && !local) {
            item.scannedQty += reel.quantity;
          }
        }
      }
    }
    for (const b of this.ws.boxes) {
      // Planning: thùng khuyến nghị → part của khuyến nghị; thùng bổ sung → part cùng mã
      const planned = this.findPlannedReel(b.reelId);
      const planningItem =
        planned?.item ??
        (b.supplementary ? this.findPlanningItem(b.materialCode) : undefined);
      if (planningItem) {
        planningItem.scannedQty += b.quantity;
        allocation.set(planningItem, [
          ...(allocation.get(planningItem) ?? []),
          b,
        ]);
        continue;
      }
      const items = this.findItems(b.materialCode);
      const target =
        items.find((i) => i.scannedQty < i.requiredQty) ??
        items[items.length - 1];
      if (target) {
        target.scannedQty += b.quantity;
        allocation.set(target, [...(allocation.get(target) ?? []), b]);
      }
    }
    this.ws.allocation = allocation;
    this.cdr.markForCheck();
  }

  private emptyWorkspace(): ExportWorkspace {
    return {
      requestCode: "",
      palletCode: "",
      boxCode: "",
      requests: [],
      boxes: [],
      pallets: [],
      expanded: new Set<string>(),
      allocation: new Map<ExportRequestItem, ExportScannedBox[]>(),
    };
  }

  private focus(ref?: ElementRef<HTMLInputElement>): void {
    // Mobile chỉ có 1 ô scan → luôn focus về ô đó (trừ khi đang mở bottom sheet / tab Gửi)
    const target =
      this.isMobile && !this.sheet && this.mobileTab !== "send"
        ? (this.mobileScanInput ?? ref)
        : ref;
    setTimeout(() => target?.nativeElement.focus(), 0);
  }
}
