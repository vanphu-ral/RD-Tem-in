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
import { forkJoin, from, Observable, of, Subscription, throwError } from "rxjs";
import {
  catchError,
  concatMap,
  map,
  mergeMap,
  switchMap,
  take,
  toArray,
} from "rxjs/operators";
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
import { AccountService } from "app/core/auth/account.service";
import { ListMaterialService } from "app/entities/list-material/services/list-material.service";
import { CachedWarehouse } from "app/entities/list-material/services/warehouse-db";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { extractReelIdFromScan } from "app/entities/list-material/services/scan-code.util";
import {
  openChoiceDialog,
  openChoiceDialog3,
} from "app/entities/info-tem-ncc/shared/choice-dialog/choice-dialog.component";
import {
  TRANSFER_SESSION_STATUS,
  TRANSFER_SESSION_STATUS_LABELS,
  splitFromComment,
  TransferSessionBox,
  TransferSessionPallet,
  WarehouseExportService,
} from "./warehouse-export.service";
import {
  printTransferRequests,
  TransferPrintDoc,
} from "./print/transfer-request-print.util";
import { openSplitReelDialog } from "./split-reel-dialog/split-reel-dialog.component";
import { VendorNccPrintDialogComponent } from "app/entities/generate-tem-in/receiving-supplies/vendor-ncc-print-dialog/vendor-ncc-print-dialog.component";
import {
  buildSplitInventoryPayload,
  SPLIT_BUSINESS_TYPE,
  SplitInventoryService,
} from "app/entities/info-tem-ncc/shared/split-inventory.service";
import { openSplitLabelPrintDialog } from "./split-label-print-dialog/split-label-print-dialog.component";
import { SplitPrintLabel, toLabelDate } from "./rd-label/rd-label.model";
import { ExportHistoryDialogComponent } from "./export-history-dialog/export-history-dialog.component";
import {
  PickSuggestionDialogComponent,
  PickSuggestionDialogData,
} from "./pick-suggestion-dialog/pick-suggestion-dialog.component";
import { openExportCancel } from "./export-confirm-dialog/export-confirm-dialog.component";
import {
  ExportPlannedReel,
  ExportPlanningSummary,
  ExportReelInventory,
  ExportRequest,
  ExportRequestItem,
  ExportScannedBox,
  ExportScannedPallet,
} from "./warehouse-export.models";

type MainTab = "request" | "planning";

/** id bản ghi phiên vừa lưu trước khi tách (null = không lưu mới) — gỡ lại khi tách lỗi */
interface SplitSessionSave {
  parentId: number | null;
  childId: number | null;
}

/** Dữ liệu nhập / scan của 1 tab */
interface ExportWorkspace {
  palletCode: string;
  boxCode: string;
  requests: ExportRequest[];
  boxes: ExportScannedBox[];
  pallets: ExportScannedPallet[];
  expanded: Set<string>;
  /** Thùng đã phân bổ cho từng dòng vật tư (tính lại sau mỗi lần scan / xóa) */
  allocation: Map<ExportRequestItem, ExportScannedBox[]>;
}

/** Bản ghi phiên xuất chuyển kho — gửi lại khi PUT /warehouse-transfer-sessions/{id} */
interface TransferSessionRecord {
  id?: number | null;
  status?: string | null;
  note?: string | null;
  createdAt?: string | null;
  createdBy?: string | null;
  updatedAt?: string | null;
  updatedBy?: string | null;
  deletedAt?: string | null;
  deletedBy?: string | null;
}

/** Mở màn Xuất kho từ danh sách: nạp sẵn các mã yêu cầu đã chọn */
export interface WarehouseExportDialogData {
  requestCodes?: string[];
  /**
   * Xuất chuyển kho: id phiên (POST /warehouse-transfer-sessions) — có thì nạp phiếu + thùng
   * đã scan theo chi tiết phiên thay cho requestCodes
   */
  sessionId?: number;
  /** Mở từ menu nào: chỉ hiện đúng tab đó (request = Yêu cầu lĩnh vật tư) */
  mode?: "request" | "planning";
  /** Tiêu đề màn (vd Xuất chuyển kho / Xuất kho khuyến nghị) */
  title?: string;
}

/** Mobile: tab dưới đáy */
type MobileTab = "pick" | "scanned" | "send";

/** Mobile: chế độ của ô scan duy nhất */
type MobileScanMode = "box" | "pallet";

/** Số thùng tra song song khi nạp lại phiên */
const SESSION_BOX_CONCURRENCY = 6;

const MOBILE_SCAN_MODE_KEY = "warehouse-export.mobile-scan-mode";

/** Mobile: màn chính (nhập / scan) hoặc Chi tiết thông tin */
type MobileView = "main" | "detail";

const MOBILE_BREAKPOINT = 768;
type ScannedTab = "box" | "pallet";

/** Đối chiếu 1 thùng khuyến nghị (Planning) với tồn kho (Inventory) */
type ReelReadiness =
  | "scanned"
  | "unchecked"
  | "error"
  | "missing"
  | "mismatch"
  | "empty"
  | "short"
  | "ready";

const READINESS_LABELS: Record<ReelReadiness, string> = {
  // BE đã ghi nhận scan (dnlnvl/full: materials[].scanCheck = 1)
  scanned: "Đã scan",
  // Chưa scan → chưa tra /inventory (SL theo Planning = SL tồn của thùng)
  unchecked: "Chưa scan",
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

/** Lỗi vị trí khi Xác nhận xuất (thông báo hiện thẳng cho người dùng) */
class LocationError extends Error {}

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
  @ViewChild("palletInput") palletInput?: ElementRef<HTMLInputElement>;
  @ViewChild("boxInput") boxInput?: ElementRef<HTMLInputElement>;
  @ViewChild("mobileScanInput")
  mobileScanInput?: ElementRef<HTMLInputElement>;

  mainTab: MainTab = "request";
  scannedTab: ScannedTab = "box";
  /** Tab Pallet: mã pallet (viết thường) đang mở rộng xem thùng */
  expandedPallets = new Set<string>();

  isBusy = false;
  isCompleting = false;
  /** Đang tra tồn kho trước khi in Đề nghị lĩnh */
  isPrinting = false;
  /** Đang xoá mềm phiên (nút Hủy) */
  isCancelling = false;
  /** Đang xoá thùng khỏi phiên */
  isDeletingBoxes = false;
  /** Đang gửi (nút Gửi) */
  isSending = false;

  /** Gợi ý vị trí (IndexedDB) cho ô Vị trí của thùng đã scan */
  locationOptions: CachedWarehouse[] = [];
  /** Ô vị trí ở header cột — áp cho mọi thùng đã scan */
  bulkLocation = "";
  /** Yêu cầu đang mở dialog "Vị trí có vật tư" */
  locModalRequest: ExportRequest | null = null;
  /** Xuất chuyển kho: danh sách kho (khu vực) — đối chiếu whsCode SAP → kho (vd 05 → RD-LED-05) */
  warehouseAreas: WarehouseAreaRef[] = [];

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

  /** Planning: thùng đã Gửi nhưng tổng hợp WO (BE) chưa tải lại — vẫn tính phần FE */
  private summaryRefreshing = new Set<ExportScannedBox>();
  /** SL xuất nhập sai của từng thùng (chưa nhận) */
  private qtyErrors = new Map<ExportScannedBox, string>();
  /** Xuất chuyển kho theo phiên: thùng đang POST lưu vào phiên */
  private savingBoxes = new Set<ExportScannedBox>();
  /** Xuất chuyển kho: bản ghi phiên (từ chi tiết phiên) — PUT trạng thái / xoá mềm */
  private session: TransferSessionRecord | null = null;
  /** Đang PUT trạng thái "đang xuất" (tránh gọi trùng khi scan liên tục) */
  private markingExporting = false;
  /**
   * Thùng đã lưu ở BE của phiên: reelId (viết thường) → id bản ghi warehouse-transfer-details
   * (null = chưa biết id). Gồm cả thùng không tra lại được tồn kho (không hiện trên bảng).
   */
  private savedDetailIds = new Map<string, number | null>();
  /**
   * Thùng đã lưu trong phiên nhưng không tra lại được tồn kho (không còn trong kho)
   * → coi như đã xuất (không xoá được), key = reelId viết thường.
   */
  private unrestoredReelKeys = new Set<string>();
  /** Bản ghi thùng đã lưu của phiên (từ chi tiết phiên) — dựng lại thùng khi mở lại phiên */
  private sessionBoxes = new Map<string, TransferSessionBox>();
  /** Pallet đã scan của phiên (từ chi tiết phiên) — dựng lại tab Pallet khi mở lại phiên */
  private sessionPallets: TransferSessionPallet[] = [];
  /**
   * Xuất chuyển kho: SL / số thùng đã scan của phiếu ở CÁC PHIÊN KHÁC (không có trên màn hình) =
   * summary BE (gộp mọi phiên) − thùng đã lưu của phiên đang mở, chốt lúc tải summary.
   * GET /warehouse-transfer-details/summary/{owtqDocEntry}. Key = mã phiếu.
   */
  private priorScans = new Map<string, { qty: number; boxes: number }>();

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
    private accountService: AccountService,
    private listMaterialService: ListMaterialService,
    private splitInventoryService: SplitInventoryService,
    @Optional()
    @Inject(MAT_DIALOG_DATA)
    private dialogData: WarehouseExportDialogData | null,
  ) {}

  /** Tiêu đề màn theo menu mở */
  get dialogTitle(): string {
    return this.dialogData?.title ?? "Xuất kho";
  }

  /** Xuất chuyển kho theo phiên: scan xong tự lưu thùng vào phiên (không dùng nút Gửi) */
  get hasSession(): boolean {
    return !this.isPlanning && this.dialogData?.sessionId != null;
  }

  /** Kho (khu vực) đang chứa thùng — theo vị trí hiện tại trong tồn kho */
  currentAreaOf(b: ExportScannedBox): WarehouseAreaRef | null {
    const locationId = Number(b.inventoryRaw?.["locationId"]) || 0;
    return this.areaOf(locationId, b.sourceLocation ?? b.location ?? "");
  }

  /** Thùng đang nằm ở kho chuyển đến (kho nhận) của nó */
  isAtDestination(b: ExportScannedBox): boolean {
    const dest = this.destAreaOf(b);
    const current = this.currentAreaOf(b);
    return !!dest && !!current && dest.areaCode === current.areaCode;
  }

  /**
   * Xuất chuyển kho theo phiên — thùng đã lưu được coi là ĐÃ XUẤT khi: đang ở kho nhận,
   * hoặc hết tồn (đã dùng / chuyển tiếp). Nghiêng về "đã xuất" để không xoá mất lịch sử.
   */
  isBoxExported(b: ExportScannedBox): boolean {
    if (!this.isSavedInSession(b.reelId)) {
      return false;
    }
    return (b.inventoryQty ?? 0) <= 0 || this.isAtDestination(b);
  }

  /** Số thùng đã lưu ở BE của phiên */
  get savedBoxCount(): number {
    return this.savedDetailIds.size;
  }

  /**
   * Được hủy (xoá) phiên: phiên chưa có thùng nào đã lưu và không có thùng đang lưu.
   * Theo số thùng thực có, không theo status (PUT "đang xuất" có thể lỗi).
   * Không theo phiên (Gửi / Khuyến nghị) → luôn được hủy như cũ.
   */
  get canCancelSession(): boolean {
    return (
      !this.hasSession ||
      (this.savedDetailIds.size === 0 && this.savingBoxes.size === 0)
    );
  }

  /** Mở từ menu cố định 1 chế độ → chỉ hiện tab đó */
  get lockedTab(): MainTab | null {
    return this.dialogData?.mode ?? null;
  }

  ngAfterViewInit(): void {
    if (this.lockedTab) {
      this.mainTab = this.lockedTab;
    }
    this.focus(this.boxInput);
    const codes = (this.dialogData?.requestCodes ?? [])
      .map((x) => x.trim())
      .filter(Boolean);
    const sessionId = this.dialogData?.sessionId;
    if (sessionId != null) {
      setTimeout(() => this.loadSession(sessionId, codes));
    } else if (codes.length) {
      setTimeout(() => this.preloadRequests(codes));
    }
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
    // scanCheck = 1 → đã scan, không cần đối chiếu kho nữa
    if (reel.scannedBefore) {
      return "scanned";
    }
    if (reel.inventoryError) {
      return "error";
    }
    if (reel.inventory === undefined) {
      return "unchecked";
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
            : inv === null
              ? "Không có trong kho"
              : "Chưa xác định vị trí (chưa scan)";
        const sapCode = inv?.sapCode ?? "";
        const key = `${area?.areaCode ?? ""}|${location}|${sapCode}`;
        const group = groups.get(key) ?? {
          key,
          areaCode: area?.areaCode ?? "",
          areaName: area?.areaName ?? "",
          location,
          sapCode,
          missing: inv === null || !!reel.inventoryError,
          stock: 0,
          reelCount: 0,
          parts: [],
        };
        // Chưa scan → SL theo Planning (đã là SL tồn của thùng)
        group.stock += inv
          ? inv.availableQty
          : inv === null
            ? 0
            : reel.quantity;
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

  /**
   * Các vị trí đang có mã vật tư, sắp theo kho → vị trí.
   * Xuất chuyển kho: chỉ vị trí thuộc kho xuất của dòng vật tư (whsCode 19 → kho mã đuôi -19).
   */
  itemStockLocations(i: ExportRequestItem): ItemStockLocation[] {
    const locations = this.itemStock(i)?.locations ?? [];
    const fromWhs = (i.warehouseCode ?? "").trim();
    if (this.isPlanning || !fromWhs) {
      return locations;
    }
    return locations.filter(
      (loc) => (loc.areaCode ?? "").split("-").pop()?.trim() === fromWhs,
    );
  }

  /** Dialog "Vị trí có vật tư": mọi vị trí đang có mã vật tư (mọi kho, không lọc kho xuất) */
  itemAllLocations(i: ExportRequestItem): ItemStockLocation[] {
    return this.itemStock(i)?.locations ?? [];
  }

  /** Tổng tồn của mã vật tư ở mọi kho */
  itemAllStockQty(i: ExportRequestItem): number {
    return this.itemAllLocations(i).reduce((s, x) => s + x.qty, 0);
  }

  /** Vị trí thuộc kho xuất của dòng vật tư (đánh dấu trong dialog) */
  isFromWhsLocation(i: ExportRequestItem, loc: ItemStockLocation): boolean {
    const fromWhs = (i.warehouseCode ?? "").trim();
    return (
      !!fromWhs && (loc.areaCode ?? "").split("-").pop()?.trim() === fromWhs
    );
  }

  /** Kho (khu vực) theo mã kho SAP: 05 → kho có mã đuôi -05 (RD-LED-05) */
  areaByWhsCode(whsCode: string | undefined | null): WarehouseAreaRef | null {
    const whs = (whsCode ?? "").trim();
    if (!whs) {
      return null;
    }
    return (
      this.warehouseAreas.find(
        (a) => a.areaCode.split("-").pop()?.trim() === whs,
      ) ?? null
    );
  }

  /** Nhãn kho theo mã kho SAP: "19 · RD-LED-19 · Kho LKDT thủ công" */
  whsLabel(whsCode: string | undefined | null): string {
    const whs = (whsCode ?? "").trim();
    if (!whs) {
      return "—";
    }
    const area = this.areaByWhsCode(whs);
    if (!area) {
      return `Kho ${whs}`;
    }
    return area.areaName && area.areaName !== area.areaCode
      ? `${area.areaCode} · ${area.areaName}`
      : area.areaCode;
  }

  /** Mở dialog "Vị trí có vật tư": tra vị trí (kho xuất) các mã chưa tra */
  openItemLocations(r: ExportRequest, event?: Event): void {
    event?.stopPropagation();
    this.locModalRequest = r;
    this.loadItemStocks(
      r.items.filter((i) => !this.itemStock(i)).map((i) => i.materialCode),
    );
  }

  closeItemLocations(): void {
    this.locModalRequest = null;
  }

  /** Kho xuất chung của các vật tư trong yêu cầu (khác nhau → rỗng) */
  requestFromWhs(r: ExportRequest): string {
    const codes = [
      ...new Set(r.items.map((i) => (i.warehouseCode ?? "").trim())),
    ];
    return codes.length === 1 ? codes[0] : "";
  }

  /**
   * Xuất chuyển kho — "Xuất đề nghị lĩnh": in phiếu ĐỀ NGHỊ LĨNH VẬT TƯ (A4 ngang, theo mẫu SAP),
   * mọi yêu cầu chung 1 phiếu. Thực xuất = SL đã scan; Số lô / Ngày SX theo các thùng đã scan;
   * Tồn kho = tổng tồn đã tra (chưa tra → trống).
   */
  onPrintRequests(): void {
    this.mobileMenuOpen = false;
    const requests = this.ws.requests.filter((r) => r.items.length);
    if (!requests.length) {
      this.notificationService.warning(
        `Chưa có ${this.codeNoun} nào để xuất đề nghị lĩnh.`,
      );
      return;
    }
    if (this.isPrinting) {
      return;
    }
    // Tồn kho: mã chưa tra xong → tra trước (bảng mã SAP → vị trí, tải 1 lần / phiên) rồi mới in
    const missing = [
      ...new Set(
        requests
          .flatMap((r) => r.items)
          .filter((i) => {
            const st = this.itemStock(i);
            return !st || st.loading || st.error;
          })
          .map((i) => i.materialCode.trim())
          .filter(Boolean),
      ),
    ];
    if (!missing.length) {
      this.buildAndPrint(requests, new Map<string, number>());
      return;
    }
    this.isPrinting = true;
    forkJoin(
      missing.map((code) =>
        this.exportService.getSapLocations(code).pipe(
          map(
            (res: { locations: Array<{ qty: number }> }): [string, number] => [
              code.toLowerCase(),
              res.locations.reduce((s, l) => s + l.qty, 0),
            ],
          ),
          catchError(() => of<[string, number] | null>(null)),
        ),
      ),
    ).subscribe((pairs) => {
      this.isPrinting = false;
      const stock = new Map<string, number>();
      for (const p of pairs) {
        if (p) {
          stock.set(p[0], p[1]);
        }
      }
      if (stock.size < missing.length) {
        this.notificationService.warning(
          "Không tra được tồn kho của một số vật tư — cột Tồn kho xưởng để trống.",
        );
      }
      this.buildAndPrint(requests, stock);
    });
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

  /**
   * Bảng Planning (desktop): mỗi part của WO 1 dòng (không nhóm theo vị trí) —
   * mở dòng → các thùng khuyến nghị của part.
   */
  planningParts(r: ExportRequest): LocationPart[] {
    return r.items.map((item) => ({
      key: `part:${item.partCode ?? item.materialCode}`,
      item,
      reels: item.reels ?? [],
    }));
  }

  /**
   * Planning: phần scan ở FE mà BE chưa ghi nhận (chưa Gửi, hoặc vừa Gửi chưa tải lại tổng hợp)
   * của 1 WO — số thùng + SL.
   */
  planningPending(r: ExportRequest): { count: number; qty: number } {
    let count = 0;
    let qty = 0;
    for (const i of r.items) {
      for (const b of this.boxesOf(i)) {
        if (!b.sent || this.summaryRefreshing.has(b)) {
          count++;
          qty += b.quantity;
        }
      }
    }
    return { count, qty };
  }

  /** % tiến độ BE đã ghi nhận (tối đa 100) */
  planningBePercent(r: ExportRequest): number {
    const ps = r.planningSummary;
    return ps?.requiredQty
      ? Math.min(100, (ps.scannedQty / ps.requiredQty) * 100)
      : 0;
  }

  /** % phần scan ở FE chưa ghi nhận (nối tiếp phần BE, tổng tối đa 100) */
  planningPendingPercent(r: ExportRequest): number {
    const ps = r.planningSummary;
    if (!ps?.requiredQty) {
      return 0;
    }
    const pending = (this.planningPending(r).qty / ps.requiredQty) * 100;
    return Math.max(0, Math.min(100 - this.planningBePercent(r), pending));
  }

  /** Kho (khu vực) các thùng của part đã tra kho (khi scan) */
  partAreas(p: LocationPart): string[] {
    const names = new Set<string>();
    for (const reel of p.reels) {
      const inv = reel.inventory;
      const area = inv ? this.areaOf(inv.locationId, inv.location) : null;
      if (area) {
        names.add(area.areaName || area.areaCode);
      }
    }
    return [...names];
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
      (sum, reel) =>
        sum +
        (reel.inventory
          ? reel.inventory.availableQty
          : reel.inventory === null
            ? 0
            : reel.quantity),
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
    unchecked: number;
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
        if (state !== "unchecked") {
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
    return { total, checked, unchecked: total - checked, ready, exported };
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
    if (!this.ensureAllSent()) {
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

  /** Mobile ⋯: Gợi ý lấy hàng cho mọi WO đang mở */
  openMobilePick(): void {
    this.mobileMenuOpen = false;
    const r =
      this.ws.requests.find((x) => this.hasReels(x)) ?? this.ws.requests[0];
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
    this.focus(this.boxInput);
  }

  // ==================== YÊU CẦU ====================

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

  // ==================== TỔNG QUAN (cột trái, Xuất chuyển kho) ====================

  /** Hiện khối Tổng quan dưới ô scan: Xuất chuyển kho, đã có yêu cầu */
  get showOverview(): boolean {
    return !this.isPlanning && this.ws.requests.length > 0;
  }

  /** Số phiên (mở từ danh sách theo phiên) — không theo phiên → null */
  get overviewSessionId(): number | null {
    return this.hasSession ? (this.dialogData?.sessionId ?? null) : null;
  }

  /** Nhãn trạng thái phiên (Mới tạo / Đang xuất / Hoàn thành) */
  get overviewSessionStatus(): string {
    const status = (this.session?.status ?? "").toLowerCase();
    const labels: Record<string, string> = TRANSFER_SESSION_STATUS_LABELS;
    const label: string | undefined = labels[status];
    return status ? (label ?? status) : "";
  }

  /** Tổng hợp cả phiên: SL đã scan / SL cần, số thùng, số phiếu đủ SL */
  get overviewTotals(): {
    required: number;
    scanned: number;
    boxes: number;
    percent: number;
    enough: number;
  } {
    let required = 0;
    let scanned = 0;
    let boxes = 0;
    let enough = 0;
    for (const r of this.ws.requests) {
      required += this.requiredTotal(r);
      scanned += Math.min(this.progressScanned(r), this.requiredTotal(r));
      boxes +=
        this.summaryBoxCount(r) ??
        r.items.reduce((s, i) => s + this.boxesOf(i).length, 0);
      if (this.isEnough(r)) {
        enough += 1;
      }
    }
    return {
      required,
      scanned,
      boxes,
      percent: required ? Math.round((scanned / required) * 100) : 0,
      enough,
    };
  }

  /** Kho xuất → kho nhận của phiếu (mã kho ngắn, nhiều kho cách nhau dấu phẩy) */
  requestRoute(r: ExportRequest): { from: string; to: string } {
    const short = (codes: Array<string | undefined>): string =>
      [...new Set(codes.map((c) => (c ?? "").trim()).filter(Boolean))]
        .map((c) => this.areaByWhsCode(c)?.areaCode ?? `Kho ${c}`)
        .join(", ") || "—";
    return {
      from: short(r.items.map((i) => i.warehouseCode)),
      to: short(r.items.map((i) => i.toWhsCode)),
    };
  }

  /** Bấm 1 phiếu ở Tổng quan → mở phiếu đó ở Danh sách yêu cầu và cuộn tới */
  revealRequest(r: ExportRequest): void {
    this.ws.expanded.add(r.code);
    setTimeout(() =>
      document
        .getElementById(`we-req-${r.code}`)
        ?.scrollIntoView({ behavior: "smooth", block: "nearest" }),
    );
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

  /**
   * Tiến độ đã scan của phiếu ở đầu "Danh sách yêu cầu":
   * Xuất chuyển kho có summary → SL đã lưu ở BE (mọi phiên) + thùng vừa scan chưa lưu;
   * chưa có summary / Khuyến nghị Planning → tính theo thùng đang có trên màn hình.
   */
  progressScanned(r: ExportRequest): number {
    // Phiếu 1 vật tư: phần phiên khác đã cộng vào SL đã scan của vật tư (recalcScanned)
    const prior =
      r.items.length > 1 ? (this.priorScans.get(r.code)?.qty ?? 0) : 0;
    return this.scannedTotal(r) + prior;
  }

  /** Tổng số thùng đã scan của phiếu (phiên này + phiên khác) — chưa có summary → null */
  summaryBoxCount(r: ExportRequest): number | null {
    const prior = this.priorScans.get(r.code);
    if (!prior) {
      return null;
    }
    const local = r.items.reduce((s, i) => s + this.boxesOf(i).length, 0);
    return local + prior.boxes;
  }

  /** Số thùng của vật tư: thùng trên màn hình + thùng phiên khác (phiếu 1 vật tư) */
  itemBoxCount(r: ExportRequest, i: ExportRequestItem): number {
    const prior =
      r.items.length === 1 ? (this.priorScans.get(r.code)?.boxes ?? 0) : 0;
    return this.boxesOf(i).length + prior;
  }

  /** Số thùng / SL đã scan ở phiên khác của phiếu 1 vật tư (hiện chú thích) */
  priorScanOf(r: ExportRequest): { qty: number; boxes: number } | null {
    const prior = r.items.length === 1 ? this.priorScans.get(r.code) : null;
    return prior && (prior.qty > 0 || prior.boxes > 0) ? prior : null;
  }

  /** % thanh tiến độ (tối đa 100) */
  progressPercent(r: ExportRequest): number {
    const required = this.requiredTotal(r);
    return required
      ? Math.min(100, (this.progressScanned(r) / required) * 100)
      : 0;
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

  /**
   * Sửa SL xuất của thùng: trống / âm / 0 / lớn hơn SL trong thùng → báo đỏ dưới ô,
   * không nhận giá trị (SL thùng giữ giá trị hợp lệ trước đó).
   */
  onBoxQtyChange(b: ExportScannedBox, value: number | string | null): void {
    const error = this.validateBoxQty(b, value);
    if (error) {
      this.qtyErrors.set(b, error);
      return;
    }
    this.qtyErrors.delete(b);
    b.quantity = Number(value);
    this.recalcScanned();
  }

  /** Lỗi SL xuất đang nhập của thùng (hiển thị dưới ô) */
  qtyError(b: ExportScannedBox): string | null {
    return this.qtyErrors.get(b) ?? null;
  }

  /**
   * Gõ vị trí → tìm trong IndexedDB. Xuất chuyển kho: chỉ gợi ý vị trí thuộc kho chuyển đến
   * (của thùng b, hoặc kho chung của mọi thùng khi áp cho cả cột).
   */
  onLocationSearch(term: string, b?: ExportScannedBox): void {
    const keyword = (term ?? "").trim();
    if (!keyword) {
      this.locationOptions = [];
      return;
    }
    const dest = this.isPlanning
      ? null
      : b
        ? this.destAreaOf(b)
        : this.commonDestArea();
    void this.warehouseCache
      .searchByName(keyword)
      .then((list) => {
        this.locationOptions = (
          dest && this.areaIndex
            ? list.filter(
                (w) =>
                  this.areaOf(
                    Number(w.locationId),
                    w.locationFullName || w.locationName,
                  )?.areaCode === dest.areaCode,
              )
            : list
        ).slice(0, 30);
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
    // Thùng đã xuất giữ nguyên vị trí đã chuyển đến
    for (const b of this.ws.boxes) {
      if (!this.isBoxExported(b)) {
        b.location = location;
      }
    }
    for (const pallet of this.ws.pallets) {
      pallet.location = location;
    }
    const invalid = this.ws.boxes.filter((b) => this.locationIssue(b));
    if (invalid.length) {
      this.notificationService.warning(
        `${this.locationIssue(invalid[0])}` +
          (invalid.length > 1 ? ` (${invalid.length} thùng)` : ""),
      );
      return;
    }
    this.notificationService.success(
      `Đã áp vị trí "${location}" cho ${this.ws.boxes.length} thùng.`,
    );
  }

  /** Xuất chuyển kho: kho chuyển đến của thùng — whsCode SAP (05) → kho có mã đuôi -05 (RD-LED-05) */
  destAreaOf(b: ExportScannedBox): WarehouseAreaRef | null {
    return this.areaByWhsCode(b.destWhsCode);
  }

  /** Màu badge trạng thái tồn kho của thùng (calculatedStatus) */
  stockStatusTone(status: string | undefined): "ok" | "bad" | "warn" {
    const s = (status ?? "").toLowerCase();
    if (s === "available") {
      return "ok";
    }
    if (/expired|consumed|reject|hold|block|quarantine/.test(s)) {
      return "bad";
    }
    return "warn";
  }

  /** Nhãn cột Kho chuyển đến */
  destAreaLabel(b: ExportScannedBox): string {
    const area = this.destAreaOf(b);
    if (area) {
      return area.areaName && area.areaName !== area.areaCode
        ? `${area.areaCode} · ${area.areaName}`
        : area.areaCode;
    }
    return b.destWhsCode ? `Kho ${b.destWhsCode}` : "—";
  }

  /**
   * Xuất chuyển kho: vị trí nhập không có / không thuộc kho chuyển đến → câu báo lỗi
   * (đối chiếu bảng vị trí → kho, tải từ area-locations). Trống / chưa tải bảng → null.
   */
  locationIssue(b: ExportScannedBox): string | null {
    const loc = (b.location ?? "").trim();
    if (this.isPlanning || !loc || !this.areaIndex) {
      return null;
    }
    const area = this.areaOf(0, loc);
    if (!area) {
      return `Vị trí "${loc}" không tồn tại.`;
    }
    const dest = this.destAreaOf(b);
    if (dest && area.areaCode !== dest.areaCode) {
      return `Vị trí "${loc}" thuộc kho ${area.areaCode}, không thuộc kho chuyển đến ${dest.areaCode}.`;
    }
    return null;
  }

  /** Rời ô vị trí: sai kho → báo ngay */
  onBoxLocationCommit(b: ExportScannedBox): void {
    const issue = this.locationIssue(b);
    if (issue) {
      this.notificationService.warning(`Thùng ${b.reelId}: ${issue}`);
    }
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
        const matched = fresh.filter(
          (b) => b.quantity > 0 && this.findItems(b.materialCode).length,
        );
        // Thùng đã ở kho nhận → không cần chuyển kho, không thêm vào phiên
        const atDest = matched.filter((b) =>
          this.alreadyAtDestinationMessage(b),
        );
        const usable = matched.filter((b) => !atDest.includes(b));
        if (atDest.length) {
          this.notificationService.warning(
            `${atDest.length} thùng của pallet "${pallet.palletCode}" đã ở kho nhận nên không thêm vào phiên: ` +
              `${atDest
                .slice(0, 3)
                .map((b) => b.reelId)
                .join(", ")}${atDest.length > 3 ? "…" : ""}.`,
          );
        }
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
        this.prepareTransferBoxes(usable);
        const scannedPallet: ExportScannedPallet = { ...pallet, boxes: usable };
        this.ws.pallets = [...this.ws.pallets, scannedPallet];
        this.ws.boxes = [...this.ws.boxes, ...usable];
        this.scanFeedback(true);
        this.recalcScanned();
        this.saveSessionBoxes();
        this.saveScanPallet(scannedPallet);
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
        } else if (this.alreadyAtDestinationMessage(box)) {
          this.notificationService.warning(
            this.alreadyAtDestinationMessage(box)!,
          );
          this.scanFeedback(false);
        } else {
          this.acceptScannedBox(box);
          return;
        }
        this.focus(this.boxInput);
      },
      error: () => {
        this.isBusy = false;
        this.notificationService.error(`Lỗi khi tra thùng "${reelId}".`);
      },
    });
  }

  /** Xuất chuyển kho: thùng xuất ít hơn SL trong thùng mà chưa tách tem phần dư */
  needsSplit(b: ExportScannedBox): boolean {
    return (
      !this.isPlanning &&
      !b.split &&
      b.inventoryQty !== undefined &&
      b.quantity > 0 &&
      b.quantity < b.inventoryQty &&
      !this.isBoxExported(b)
    );
  }

  /** Xuất chuyển kho: thùng còn tách tem được (chưa tách, chưa xuất, SL trong thùng > 1) */
  canSplit(b: ExportScannedBox): boolean {
    return (
      !this.isPlanning &&
      !b.split &&
      !this.isBoxExported(b) &&
      (b.inventoryQty ?? b.quantity) > 1
    );
  }

  /**
   * Nút "Tách tem" ở dòng thùng — người dùng chủ động tách lúc nào cũng được.
   * Gợi ý SL xuất: SL đang xuất nếu ít hơn SL trong thùng; còn lại = SL còn cần của mã vật tư
   * (tính cả thùng này), tối đa SL trong thùng.
   */
  onSplitBox(b: ExportScannedBox): void {
    const boxQty = b.inventoryQty ?? b.quantity;
    const need = this.remainingNeed(b.materialCode) + b.quantity;
    const suggested =
      b.quantity < boxQty ? b.quantity : Math.min(Math.max(1, need), boxQty);
    this.openSplit(
      b,
      suggested,
      () => {
        this.recalcScanned();
      },
      need,
    );
  }

  /** In lại tem tách (kể cả mã tách khôi phục khi mở lại phiên) — tra tồn kho tem tách để lấy dữ liệu mới nhất */
  reprintSplit(box: ExportScannedBox, template: "rd" | "vendor"): void {
    const split = box.split;
    if (!split) {
      return;
    }
    if (template === "vendor") {
      this.printSplitLabel(box);
      return;
    }
    this.isBusy = true;
    this.exportService
      .getBox(split.reelId)
      .pipe(catchError(() => of(null)))
      .subscribe((child: ExportScannedBox | null) => {
        this.isBusy = false;
        this.printSplitLabelRd(box, child);
        this.cdr.markForCheck();
      });
  }

  /** Bỏ thùng: thùng đã lưu vào phiên → xác nhận rồi DELETE ở BE; chưa lưu → bỏ khỏi bảng */
  removeBox(box: ExportScannedBox): void {
    if (this.isBoxExported(box)) {
      this.warnExportedNotDeletable([box.reelId]);
      return;
    }
    if (!this.isSavedInSession(box.reelId)) {
      this.removeLocalBoxes([box.reelId]);
      return;
    }
    this.confirmDeleteSessionBoxes(
      [box.reelId],
      "Xoá thùng khỏi phiên",
      `Xoá thùng ${box.reelId} đã lưu trong phiên?`,
    );
  }

  /** Bỏ pallet (và các thùng trên pallet): có thùng đã lưu vào phiên → xác nhận rồi DELETE */
  removePallet(pallet: ExportScannedPallet): void {
    const reelIds = pallet.boxes.map((b) => b.reelId);
    if (!reelIds.length) {
      // Pallet đã lưu nhưng không còn thùng nào trên bảng → chỉ xoá bản ghi pallet
      this.ws.pallets = this.ws.pallets.filter((p) => p !== pallet);
      this.dropScanPallet(pallet);
      return;
    }
    const exported = reelIds.filter((id) => this.isReelExported(id));
    if (exported.length) {
      this.warnExportedNotDeletable(exported);
      return;
    }
    if (!reelIds.some((id) => this.isSavedInSession(id))) {
      this.ws.pallets = this.ws.pallets.filter((p) => p !== pallet);
      this.removeLocalBoxes(reelIds);
      return;
    }
    this.confirmDeleteSessionBoxes(
      reelIds,
      "Xoá pallet khỏi phiên",
      `Xoá pallet ${pallet.palletCode} (${reelIds.length} thùng) đã lưu trong phiên?`,
    );
  }

  /**
   * Xoá tất cả thùng của phiên: DELETE lần lượt từng thùng đã lưu
   * (kể cả thùng không tra lại được tồn kho), thùng chưa lưu bỏ khỏi bảng.
   */
  onDeleteAllBoxes(): void {
    const all = [
      ...new Set([
        ...this.ws.boxes.map((b) => this.reelKey(b.reelId)),
        ...this.savedDetailIds.keys(),
      ]),
    ];
    // Thùng đã xuất giữ lại (lịch sử) — chỉ xoá thùng chưa xuất
    const reelIds = all.filter((id) => !this.isReelExported(id));
    const exportedCount = all.length - reelIds.length;
    if (!reelIds.length) {
      if (exportedCount) {
        this.notificationService.warning(
          `Cả ${exportedCount} thùng của phiên đều đã xuất — không xoá được (giữ lịch sử xuất).`,
        );
      }
      return;
    }
    this.confirmDeleteSessionBoxes(
      reelIds,
      "Xoá tất cả thùng",
      `Xoá ${reelIds.length} thùng chưa xuất của phiên? Thùng đã lưu sẽ bị xoá khỏi phiên.` +
        (exportedCount
          ? ` ${exportedCount} thùng đã xuất được giữ lại, phiên sẽ không hủy được.`
          : ""),
    );
  }

  palletQty(p: ExportScannedPallet): number {
    return p.boxes.reduce((s, b) => s + b.quantity, 0);
  }

  /** Tab Pallet: mở / đóng danh sách thùng trên pallet */
  togglePallet(p: ExportScannedPallet): void {
    const key = this.reelKey(p.palletCode);
    if (!this.expandedPallets.delete(key)) {
      this.expandedPallets.add(key);
    }
  }

  isPalletExpanded(p: ExportScannedPallet): boolean {
    return this.expandedPallets.has(this.reelKey(p.palletCode));
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
  /**
   * Nút đóng (X / ←):
   *  - Phiên chưa có thùng nào → hỏi "Hủy phiên" / "Đóng, giữ phiên".
   *  - Phiên đã có thùng / không theo phiên → chỉ đóng, giữ nguyên dữ liệu đã lưu.
   */
  onClose(): void {
    if (this.isCancelling || this.isDeletingBoxes) {
      return;
    }
    if (!this.hasSession || !this.canCancelSession) {
      this.dialogRef.close(false);
      return;
    }
    openChoiceDialog3(this.dialog, {
      title: "Đóng phiên xuất",
      message:
        `Phiên #${this.dialogData?.sessionId ?? ""} chưa có thùng nào. ` +
        "Hủy phiên này hay đóng và giữ phiên để scan sau?",
      confirmText: "Hủy phiên",
      extraText: "Đóng, giữ phiên",
      cancelText: "Ở lại",
      tone: "warning",
    }).subscribe((choice) => {
      if (choice === "confirm") {
        this.deleteSession();
      } else if (choice === "extra") {
        this.dialogRef.close(false);
      }
    });
  }

  /**
   * Nút Hủy (cạnh Xác nhận xuất): popup xác nhận hủy → có phiên thì xoá mềm phiên
   * (PUT deletedAt / deletedBy) rồi đóng.
   */
  onCancel(): void {
    if (this.isCompleting || this.isCancelling || this.isDeletingBoxes) {
      return;
    }
    if (!this.canCancelSession) {
      this.notificationService.warning(
        "Phiên đã có thùng — xoá hết thùng trong phiên trước khi hủy phiên.",
      );
      return;
    }
    const all = [this.workspaces.request, this.workspaces.planning];
    const requestCodes = all.flatMap((w) => w.requests.map((r) => r.code));
    const boxes = all.flatMap((w) => w.boxes);
    openExportCancel(this.dialog, {
      requestCodes,
      boxCount: boxes.length,
      scannedQty: boxes.reduce((s, b) => s + b.quantity, 0),
    }).subscribe((ok) => {
      if (!ok) {
        return;
      }
      if (!this.hasSession) {
        this.dialogRef.close(false);
        return;
      }
      this.deleteSession();
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
    if (this.hasSession) {
      this.saveSessionBoxes();
      return;
    }
    this.isSending = true;
    this.exportService.send(this.ws.requests, this.ws.boxes).subscribe({
      next: (ok) => {
        this.isSending = false;
        if (ok) {
          this.notificationService.success(
            `Đã gửi ${this.ws.requests.length} ${this.codeNoun} (${this.ws.boxes.length} thùng).`,
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

  /** Thùng đang lưu vào phiên */
  isBoxSaving(box: ExportScannedBox): boolean {
    return this.savingBoxes.has(box);
  }

  /**
   * Xuất chuyển kho theo phiên: tự lưu ngay sau khi scan (thùng / pallet) —
   * POST /warehouse-transfer-details từng thùng chưa lưu (song song có giới hạn).
   * Thùng lưu lỗi → bỏ khỏi danh sách, báo người dùng scan lại thùng đó.
   */
  saveSessionBoxes(): void {
    const sessionId = this.dialogData?.sessionId;
    if (!this.hasSession || sessionId == null) {
      return;
    }
    const targets = this.ws.boxes.filter(
      (b) => !b.sent && !this.savingBoxes.has(b),
    );
    if (!targets.length) {
      return;
    }
    targets.forEach((b) => this.savingBoxes.add(b));
    this.accountService
      .identity()
      .pipe(
        take(1),
        switchMap((account) =>
          from(targets).pipe(
            mergeMap(
              (box) =>
                this.exportService
                  .saveTransferDetail(
                    sessionId,
                    box,
                    this.transferLinkOf(box),
                    account?.login ?? "unknown",
                  )
                  .pipe(
                    map((id) => ({ box, ok: true, id })),
                    catchError(() => of({ box, ok: false, id: null })),
                  ),
              SESSION_BOX_CONCURRENCY,
            ),
            toArray(),
          ),
        ),
      )
      .subscribe((results) => {
        results.forEach((r) => {
          this.savingBoxes.delete(r.box);
          r.box.sent = r.ok;
          if (r.ok) {
            this.savedDetailIds.set(this.reelKey(r.box.reelId), r.id);
          }
        });
        if (results.some((r) => r.ok)) {
          this.markSessionExporting();
          this.loadRequestSummaries(this.ws.requests.map((r) => r.code));
        }
        const failed = results.filter((r) => !r.ok).map((r) => r.box.reelId);
        if (failed.length) {
          // Không giữ thùng lưu lỗi trên bảng — người dùng scan lại thùng đó
          this.removeLocalBoxes(failed);
          this.notificationService.error(
            `Không lưu được ${failed.length} thùng vào phiên: ${failed.slice(0, 5).join(", ")}` +
              `${failed.length > 5 ? "…" : ""} — đã bỏ khỏi danh sách, vui lòng scan lại.`,
          );
        }
        this.cdr.markForCheck();
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
    const unsplit = this.ws.boxes.filter((b) => this.needsSplit(b));
    if (unsplit.length) {
      this.notificationService.warning(
        `Còn ${unsplit.length} thùng xuất ít hơn SL trong thùng nhưng chưa tách tem phần dư ` +
          `(${unsplit
            .slice(0, 3)
            .map((b) => b.reelId)
            .join(
              ", ",
            )}${unsplit.length > 3 ? "…" : ""}) — bấm "Tách tem" ở dòng thùng.`,
      );
      return;
    }
    if (!this.ensureAllSent()) {
      return;
    }
    // Không bắt buộc scan đủ SL — thùng đã scan đúng mã thì xuất (cập nhật vị trí) bấy nhiêu thùng
    this.confirmAndSubmit();
  }

  trackBox(_: number, b: ExportScannedBox): string {
    return b.reelId;
  }

  // ==================== NỘI BỘ ====================

  /** Chưa có yêu cầu → nhắc nhập mã yêu cầu trước */
  /** Thiếu thông tin gửi (kho duyệt / người duyệt / người quản lý kho) → báo + false */
  /**
   * Trước khi xuất: mọi thùng đã scan phải nhập / chọn vị trí ở ô Vị trí.
   * Xuất chuyển kho: vị trí còn phải thuộc đúng kho chuyển đến.
   */
  private ensureBoxLocations(): boolean {
    const badQty = this.ws.boxes.filter((b) => this.qtyErrors.has(b));
    if (badQty.length) {
      this.notificationService.warning(
        `SL xuất không hợp lệ ở ${badQty.length} thùng (${badQty
          .slice(0, 3)
          .map((b) => `${b.reelId}: ${this.qtyErrors.get(b)}`)
          .join("; ")}) — sửa lại trước khi xuất.`,
      );
      return false;
    }
    if (!this.isPlanning) {
      return this.ensureTransferLocations();
    }
    const empty = this.ws.boxes.filter((b) => !b.location.trim());
    if (empty.length) {
      this.notificationService.warning(
        `Chưa nhập vị trí cho ${empty.length} thùng (${empty
          .slice(0, 3)
          .map((b) => b.reelId)
          .join(", ")}${empty.length > 3 ? "…" : ""}).`,
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
          !x.box.sent &&
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
    const skipped =
      this.ws.boxes.filter((b) => !b.sent).length - targets.length;
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
        // Thùng đã gửi → BE đã ghi nhận scan; giữ trong danh sách để Xác nhận xuất
        for (const t of sent) {
          t.planned.reel.scannedBefore = true;
          t.box.sent = true;
          this.summaryRefreshing.add(t.box);
        }
        this.recalcScanned();
        if (sent.length) {
          this.refreshPlanningSummaries();
        }
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

  /**
   * Planning: sau khi Gửi → lấy lại tổng hợp WO (BE) để thanh tiến độ về đúng số BE,
   * phần FE vừa gửi chuyển thành phần BE (đồng màu).
   */
  private refreshPlanningSummaries(): void {
    const requests = [...this.ws.requests];
    forkJoin(
      requests.map(
        (r): Observable<ExportPlanningSummary | null | undefined> =>
          (
            this.exportService.getPlanningSummary(
              r.code,
            ) as Observable<ExportPlanningSummary | null>
          ).pipe(catchError(() => of(undefined))),
      ),
    ).subscribe((summaries) => {
      summaries.forEach((s, idx) => {
        if (s) {
          requests[idx].planningSummary = s as ExportPlanningSummary;
        }
      });
      if (summaries.some((s) => s === undefined)) {
        this.notificationService.warning(
          "Chưa tải lại được tiến độ WO từ BE — mở lại chi tiết để cập nhật.",
        );
      }
      this.summaryRefreshing.clear();
      this.cdr.markForCheck();
    });
  }

  /** Đã nhập vị trí cho mọi thùng → popup "Xác nhận xuất" → cập nhật vị trí */
  private confirmAndSubmit(): void {
    if (!this.ensureBoxLocations()) {
      return;
    }
    openChoiceDialog(this.dialog, {
      title: "Xác nhận Xuất hàng",
      message:
        `Xuất ${this.ws.boxes.length} thùng cho ${this.ws.requests.length} ${this.codeNoun} ` +
        `và cập nhật vị trí các thùng theo ô Vị trí đã nhập.`,
      confirmText: "Xác nhận",
      cancelText: "Hủy",
      tone: "info",
    }).subscribe((ok) => {
      if (ok) {
        this.submitComplete();
      }
    });
  }

  /**
   * Xuất chuyển kho: thùng đang nằm sẵn ở kho nhận → câu báo (không cho thêm vào phiên,
   * vì chuyển kho thùng này vô nghĩa và sẽ bị nhận nhầm là "đã xuất"). Không phải → null.
   */
  private alreadyAtDestinationMessage(box: ExportScannedBox): string | null {
    if (this.isPlanning) {
      return null;
    }
    const destWhs = this.findItems(box.materialCode)[0]?.toWhsCode;
    const dest = this.areaByWhsCode(destWhs);
    const locationId = Number(box.inventoryRaw?.["locationId"]) || 0;
    const current = this.areaOf(locationId, box.sourceLocation ?? box.location);
    if (!dest || !current || dest.areaCode !== current.areaCode) {
      return null;
    }
    const loc = box.sourceLocation ?? box.location;
    return (
      `Thùng "${box.reelId}" đang ở vị trí "${loc || "—"}" thuộc kho nhận ${dest.areaCode} ` +
      "— đã ở đúng kho cần chuyển đến nên không cần xuất, không thêm vào phiên."
    );
  }

  /** Thùng đã lưu được coi là đã xuất (cả thùng không còn tra được trong kho) */
  private isReelExported(reelId: string): boolean {
    const key = this.reelKey(reelId);
    if (this.unrestoredReelKeys.has(key)) {
      return true;
    }
    const box = this.ws.boxes.find((b) => this.reelKey(b.reelId) === key);
    return !!box && this.isBoxExported(box);
  }

  /** Thùng còn phải xuất (bỏ thùng đã xuất ở lần trước — tránh chuyển kho 2 lần) */
  private get pendingExportBoxes(): ExportScannedBox[] {
    return this.ws.boxes.filter((b) => !this.isBoxExported(b));
  }

  private warnExportedNotDeletable(reelIds: string[]): void {
    this.notificationService.warning(
      `Thùng ${reelIds.slice(0, 3).join(", ")}${reelIds.length > 3 ? "…" : ""} ` +
        "đã xuất (đang ở kho nhận / đã hết tồn) — không xoá được để giữ lịch sử xuất.",
    );
  }

  /** Tải summary SL đã scan của các phiếu (Xuất chuyển kho) — lỗi thì giữ cách tính cũ */
  private loadRequestSummaries(codes: string[]): void {
    if (this.isPlanning) {
      return;
    }
    for (const code of codes) {
      this.exportService.getTransferDetailSummary(code).subscribe((s) => {
        if (!s) {
          this.priorScans.delete(code);
        } else {
          // Thùng đã lưu của phiên đang mở (đã nằm trong summary) → trừ ra để không tính 2 lần
          const docEntry = Number(code);
          const mine = this.ws.boxes.filter(
            (b) => b.sent && this.transferLinkOf(b).owtqDocEntry === docEntry,
          );
          this.priorScans.set(code, {
            qty: Math.max(
              0,
              (Number(s.totalQuantity) || 0) -
                mine.reduce((sum, b) => sum + b.quantity, 0),
            ),
            boxes: Math.max(0, (Number(s.numberOfRecord) || 0) - mine.length),
          });
        }
        this.recalcScanned();
      });
    }
  }

  private reelKey(reelId: string): string {
    return reelId.trim().toLowerCase();
  }

  private isSavedInSession(reelId: string): boolean {
    return this.hasSession && this.savedDetailIds.has(this.reelKey(reelId));
  }

  private setSavedDetails(
    boxes: Array<{ id: number | null; reelId: string }>,
  ): void {
    this.savedDetailIds = new Map(
      boxes.map(
        (b) => [this.reelKey(b.reelId), b.id] as [string, number | null],
      ),
    );
  }

  /** Bỏ thùng khỏi bảng (và khỏi pallet chứa nó) */
  private removeLocalBoxes(reelIds: string[]): void {
    const keys = new Set(reelIds.map((id) => this.reelKey(id)));
    const keep = (b: ExportScannedBox): boolean =>
      !keys.has(this.reelKey(b.reelId));
    this.ws.boxes = this.ws.boxes.filter(keep);
    // Giữ nguyên object pallet (POST scan-pallets đang chờ dùng để đối chiếu)
    const emptied: ExportScannedPallet[] = [];
    for (const p of this.ws.pallets) {
      const before = p.boxes.length;
      p.boxes = p.boxes.filter(keep);
      if (before > 0 && !p.boxes.length) {
        emptied.push(p);
      }
    }
    if (emptied.length) {
      // Pallet hết thùng → bỏ khỏi phiên (DELETE /scan-pallets/{id})
      this.ws.pallets = this.ws.pallets.filter((p) => !emptied.includes(p));
      emptied.forEach((p) => this.dropScanPallet(p));
    }
    this.recalcScanned();
  }

  private confirmDeleteSessionBoxes(
    reelIds: string[],
    title: string,
    message: string,
  ): void {
    if (this.isDeletingBoxes || this.isCompleting || this.isCancelling) {
      return;
    }
    const keys = new Set(reelIds.map((id) => this.reelKey(id)));
    const saving = this.ws.boxes.some(
      (b) => this.savingBoxes.has(b) && keys.has(this.reelKey(b.reelId)),
    );
    if (saving) {
      this.notificationService.info(
        "Thùng đang lưu vào phiên — đợi lưu xong rồi xoá.",
      );
      return;
    }
    openChoiceDialog(this.dialog, {
      title,
      message,
      confirmText: "Xoá",
      cancelText: "Không",
      tone: "danger",
    }).subscribe((ok) => {
      if (ok) {
        this.deleteSessionBoxes(reelIds);
      }
    });
  }

  /**
   * DELETE /warehouse-transfer-details/{id} lần lượt từng thùng đã lưu; thùng chưa lưu bỏ khỏi bảng.
   * Thùng chưa biết id → tải lại chi tiết phiên lấy id trước. Xoá hết thùng → trả phiên về "mới tạo".
   */
  private deleteSessionBoxes(reelIds: string[]): void {
    const saved = reelIds.filter((id) => this.isSavedInSession(id));
    const unsaved = reelIds.filter((id) => !this.isSavedInSession(id));
    const sessionId = this.dialogData?.sessionId;
    const needIds = saved.some(
      (id) => this.savedDetailIds.get(this.reelKey(id)) == null,
    );
    const ids$: Observable<unknown> =
      needIds && sessionId != null
        ? this.exportService
            .getTransferSessionDetail(sessionId)
            .pipe(map((detail) => this.setSavedDetails(detail.boxes)))
        : of(null);

    this.isDeletingBoxes = true;
    ids$
      .pipe(
        switchMap(() =>
          from(saved).pipe(
            concatMap((reelId) => {
              const id = this.savedDetailIds.get(this.reelKey(reelId));
              if (id == null) {
                return of({ reelId, ok: false });
              }
              return this.exportService.deleteTransferDetail(id).pipe(
                map(() => ({ reelId, ok: true })),
                catchError(() => of({ reelId, ok: false })),
              );
            }),
            toArray(),
          ),
        ),
      )
      .subscribe({
        next: (results) => {
          this.isDeletingBoxes = false;
          const deleted = results.filter((r) => r.ok).map((r) => r.reelId);
          const failed = results.filter((r) => !r.ok).map((r) => r.reelId);
          deleted.forEach((id) => {
            this.savedDetailIds.delete(this.reelKey(id));
            this.unrestoredReelKeys.delete(this.reelKey(id));
          });
          this.removeLocalBoxes([...deleted, ...unsaved]);
          if (deleted.length) {
            this.notificationService.success(
              `Đã xoá ${deleted.length} thùng khỏi phiên.`,
            );
          }
          if (failed.length) {
            this.notificationService.error(
              `Không xoá được ${failed.length} thùng: ${failed.slice(0, 5).join(", ")}` +
                `${failed.length > 5 ? "…" : ""} — thử lại.`,
            );
          }
          this.resetSessionIfEmpty();
          if (deleted.length) {
            this.loadRequestSummaries(this.ws.requests.map((r) => r.code));
          }
          this.cdr.markForCheck();
        },
        error: () => {
          this.isDeletingBoxes = false;
          this.notificationService.error(
            "Không tải được thông tin thùng của phiên — chưa xoá, thử lại.",
          );
          this.cdr.markForCheck();
        },
      });
  }

  /** Phiên không còn thùng nào → trả trạng thái về "mới tạo" (khớp dữ liệu) */
  private resetSessionIfEmpty(): void {
    if (
      !this.hasSession ||
      !this.session ||
      this.savedDetailIds.size > 0 ||
      this.session.status === TRANSFER_SESSION_STATUS.NEW
    ) {
      return;
    }
    this.updateSession({ status: TRANSFER_SESSION_STATUS.NEW }).subscribe({
      error: () => {
        this.notificationService.warning(
          "Đã xoá hết thùng nhưng chưa cập nhật được trạng thái phiên (mới tạo).",
        );
      },
    });
  }

  /**
   * Xuất chuyển kho theo phiên: GET chi tiết phiên → các phiếu (owtqSessions → /owtq/{docEntry})
   * + các thùng đã scan (details). Lỗi → nạp theo mã đã chọn ở danh sách.
   */
  private loadSession(sessionId: number, fallbackCodes: string[]): void {
    this.mainTab = this.lockedTab ?? "request";
    this.isBusy = true;
    this.exportService.getTransferSessionDetail(sessionId).subscribe({
      next: (detail) => {
        this.isBusy = false;
        this.session = detail.session as TransferSessionRecord | null;
        this.setSavedDetails(detail.boxes);
        this.sessionBoxes = new Map(
          detail.boxes.map((b) => [this.reelKey(b.reelId), b]),
        );
        this.sessionPallets = detail.pallets;
        const codes = detail.requestCodes.length
          ? detail.requestCodes
          : fallbackCodes;
        if (codes.length) {
          this.preloadRequests(codes, detail.reelIds);
        }
      },
      error: () => {
        this.isBusy = false;
        this.notificationService.error(
          `Không tải được chi tiết phiên xuất chuyển kho #${sessionId}.`,
        );
        if (fallbackCodes.length) {
          this.preloadRequests(fallbackCodes);
        }
      },
    });
  }

  /** PUT trạng thái / xoá mềm phiên (theo user đang đăng nhập) */
  private updateSession(
    changes: Partial<TransferSessionRecord>,
  ): Observable<TransferSessionRecord> {
    const session = this.session!;
    return this.accountService.identity().pipe(
      take(1),
      switchMap(
        (account): Observable<TransferSessionRecord> =>
          this.exportService.updateTransferSession(
            session,
            changes,
            account?.login ?? "unknown",
          ) as Observable<TransferSessionRecord>,
      ),
      map((updated: TransferSessionRecord) => {
        this.session = updated;
        return updated;
      }),
    );
  }

  /** Đã lưu thùng vào phiên → phiên đang "mới tạo" chuyển sang "đang xuất" */
  private markSessionExporting(): void {
    if (
      !this.hasSession ||
      !this.session ||
      this.markingExporting ||
      this.session.status !== TRANSFER_SESSION_STATUS.NEW
    ) {
      return;
    }
    this.markingExporting = true;
    this.updateSession({ status: TRANSFER_SESSION_STATUS.EXPORTING }).subscribe(
      {
        next: () => {
          this.markingExporting = false;
        },
        error: () => {
          this.markingExporting = false;
          this.notificationService.warning(
            "Không cập nhật được trạng thái phiên (đang xuất).",
          );
        },
      },
    );
  }

  /**
   * Xác nhận xuất xong: đủ SL mọi phiếu → phiên "hoàn thành", chưa đủ → "đang xuất".
   * Không có phiên → bỏ qua.
   */
  private finishSessionStatus(): Observable<unknown> {
    if (!this.hasSession || !this.session) {
      return of(null);
    }
    const full =
      this.ws.requests.length > 0 &&
      this.ws.requests.every((r) => this.isEnough(r));
    const status = full
      ? TRANSFER_SESSION_STATUS.COMPLETED
      : TRANSFER_SESSION_STATUS.EXPORTING;
    if (this.session.status === status) {
      return of(null);
    }
    return this.updateSession({ status }).pipe(
      catchError(() => {
        this.notificationService.warning(
          "Đã xuất kho nhưng chưa cập nhật được trạng thái phiên.",
        );
        return of(null);
      }),
    );
  }

  /** Hủy phiên: xoá mềm (deletedAt / deletedBy) rồi đóng màn hình */
  private deleteSession(): void {
    if (!this.session) {
      this.notificationService.error(
        "Chưa tải được thông tin phiên — không hủy được, thử lại.",
      );
      return;
    }
    this.isCancelling = true;
    this.accountService
      .identity()
      .pipe(
        take(1),
        switchMap((account) =>
          this.updateSession({
            deletedAt: new Date().toISOString(),
            deletedBy: account?.login ?? "unknown",
          }),
        ),
      )
      .subscribe({
        next: () => {
          this.isCancelling = false;
          this.notificationService.success(
            `Đã hủy phiên xuất chuyển kho #${this.session?.id ?? ""}.`,
          );
          this.dialogRef.close(true);
        },
        error: () => {
          this.isCancelling = false;
          this.notificationService.error("Hủy phiên thất bại — thử lại.");
        },
      });
  }

  /**
   * Xuất chuyển kho theo phiên: lưu pallet vừa scan — POST /scan-pallets
   * (businessSourceId = id phiên, businessType "Chuyển kho"). Lỗi chỉ cảnh báo, thùng vẫn giữ.
   */
  private saveScanPallet(pallet: ExportScannedPallet): void {
    const sessionId = this.dialogData?.sessionId;
    if (!this.hasSession || sessionId == null) {
      return;
    }
    this.exportService.saveScanPallet(pallet.palletCode, sessionId).subscribe({
      next: (id) => {
        pallet.scanId = id;
        // Pallet bị bỏ khi đang lưu → xoá luôn bản ghi vừa tạo
        if (!this.ws.pallets.includes(pallet)) {
          this.dropScanPallet(pallet);
        }
      },
      error: () => {
        this.notificationService.warning(
          `Chưa lưu được thông tin pallet "${pallet.palletCode}" vào phiên (các thùng vẫn đã lưu).`,
        );
      },
    });
  }

  /** Bỏ pallet khỏi phiên — DELETE /scan-pallets/{id} (chưa biết id → tra theo mã pallet + phiên) */
  private dropScanPallet(pallet: ExportScannedPallet): void {
    const sessionId = this.dialogData?.sessionId;
    // scanId undefined = chưa lưu / đang POST (POST xong sẽ tự xoá nếu pallet đã bị bỏ)
    if (!this.hasSession || sessionId == null || pallet.scanId === undefined) {
      return;
    }
    const id$: Observable<number | null> =
      pallet.scanId !== null
        ? of(pallet.scanId)
        : this.exportService.findScanPalletId(pallet.palletCode, sessionId);
    id$
      .pipe(
        switchMap(
          (id): Observable<void> =>
            id === null
              ? throwError(() => new Error("scan-pallet not found"))
              : this.exportService.deleteScanPallet(id),
        ),
      )
      .subscribe({
        next: () => {
          const key = this.reelKey(pallet.palletCode);
          this.sessionPallets = this.sessionPallets.filter(
            (p) => this.reelKey(p.palletCode) !== key,
          );
        },
        error: () => {
          this.notificationService.warning(
            `Chưa xoá được pallet "${pallet.palletCode}" khỏi phiên — thử lại sau.`,
          );
        },
      });
  }

  /**
   * Mở lại phiên: dựng tab Pallet từ scannedPallets của chi tiết phiên — thùng trên pallet là
   * chính các thùng đã khôi phục ở tab Thùng (cùng object, hiện ở cả 2 tab).
   */
  private attachSessionPallets(): void {
    if (!this.sessionPallets.length) {
      return;
    }
    const byReel = new Map(
      this.ws.boxes.map((b) => [this.reelKey(b.reelId), b]),
    );
    const added: ExportScannedPallet[] = [];
    for (const sp of this.sessionPallets) {
      const key = this.reelKey(sp.palletCode);
      if (this.ws.pallets.some((p) => this.reelKey(p.palletCode) === key)) {
        continue;
      }
      const boxes = sp.reelIds
        .map((id) => byReel.get(this.reelKey(id)))
        .filter((b): b is ExportScannedBox => !!b);
      boxes.forEach((b) => (b.palletCode = b.palletCode || sp.palletCode));
      added.push({
        palletCode: sp.palletCode,
        location: boxes[0]?.sourceLocation ?? "",
        boxes,
        reelCount: sp.reelIds.length,
        scanId: sp.id,
      });
    }
    if (added.length) {
      this.ws.pallets = [...this.ws.pallets, ...added];
    }
  }

  /** Nạp lại các thùng đã scan của phiên (tra lại tồn kho từng thùng) */
  private restoreBoxes(reelIds: string[]): void {
    const ids = reelIds.filter(
      (id) =>
        !this.ws.boxes.some((b) => b.reelId.toLowerCase() === id.toLowerCase()),
    );
    if (!ids.length) {
      this.attachSessionPallets();
      return;
    }
    this.isBusy = true;
    from(ids)
      .pipe(
        mergeMap(
          (id) =>
            this.exportService.getBox(id).pipe(
              map((box) => ({ id, box })),
              catchError(() =>
                of({ id, box: null as ExportScannedBox | null }),
              ),
            ),
          SESSION_BOX_CONCURRENCY,
        ),
        toArray(),
      )
      .subscribe((list) => {
        this.isBusy = false;
        const byId = new Map(list.map((x) => [x.id, x.box]));
        const found = ids
          .map((id) => byId.get(id))
          .filter((b): b is ExportScannedBox => !!b);
        // Thùng của phiên đã lưu ở BE → không gửi lại
        found.forEach((b) => (b.sent = true));
        this.prepareTransferBoxes(found);
        // Khôi phục SL xuất / vị trí chuyển đến đã lưu của phiên
        for (const b of found) {
          const saved = this.sessionBoxes.get(this.reelKey(b.reelId));
          if (saved?.quantity) {
            b.quantity = saved.quantity;
          }
          if (saved?.location && !b.location) {
            b.location = saved.location;
          }
        }
        const missing = ids.filter((id) => !byId.get(id));
        // Không còn trong kho (đã xuất / đã dùng) → dựng từ bản ghi đã lưu, vẫn hiện ở bảng
        const rebuilt = missing
          .map((id) => this.sessionBoxes.get(this.reelKey(id)))
          .filter((b): b is TransferSessionBox => !!b)
          .map((b) => this.exportService.sessionBoxToScannedBox(b));
        this.prepareTransferBoxes(rebuilt);
        if (this.hasSession) {
          missing.forEach((id) =>
            this.unrestoredReelKeys.add(this.reelKey(id)),
          );
        }
        const restored = [...found, ...rebuilt];
        if (restored.length) {
          this.ws.boxes = [...this.ws.boxes, ...restored];
          this.scannedTab = "box";
          this.attachSavedSplits();
          this.recalcScanned();
        }
        this.attachSessionPallets();
        const lost = missing.length - rebuilt.length;
        if (lost > 0) {
          this.notificationService.warning(
            `Không tra được ${lost} thùng đã scan của phiên: ${missing
              .filter((id) => !this.sessionBoxes.has(this.reelKey(id)))
              .join(", ")}.`,
          );
        }
        this.cdr.markForCheck();
      });
  }

  /** Nạp sẵn các yêu cầu đã chọn ở danh sách (tab Yêu cầu lĩnh vật tư) */
  private preloadRequests(codes: string[], reelIds: string[] = []): void {
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
      this.restoreBoxes(reelIds);
      this.focus(this.boxInput);
    });
  }

  /** Thêm 1 yêu cầu / WO đã tra vào tab đang mở (WO Planning: tra kho từng thùng) */
  private addLoadedRequest(req: ExportRequest): void {
    this.ws.requests = [...this.ws.requests, req];
    this.ws.expanded.add(req.code);
    this.recalcScanned();
    // Chọn theo chế độ đang mở (WO Planning có thể chưa có cuộn khuyến nghị nào)
    if (this.isPlanning) {
      // Không tra /inventory từng thùng khuyến nghị (SL Planning = SL tồn) — chỉ tra khi scan
      this.ensureAreaIndex();
    } else if (req.items.length) {
      this.loadRequestSummaries([req.code]);
      // Xuất chuyển kho: vị trí mọi mã lấy từ 1 bảng chung (tải 1 lần / phiên)
      this.loadItemStocks(req.items.map((i) => i.materialCode));
      this.ensureWarehouseAreas();
      this.ensureAreaIndex();
    }
  }

  /** Xuất chuyển kho: tải danh sách kho 1 lần (đối chiếu kho chuyển đến) */
  private ensureWarehouseAreas(): void {
    if (this.warehouseAreas.length) {
      return;
    }
    this.exportService.getWarehouseAreaList().subscribe({
      next: (areas) => {
        this.warehouseAreas = areas;
        this.cdr.markForCheck();
      },
      error: () => {
        this.notificationService.warning(
          "Không tải được danh sách kho — chưa đối chiếu được kho chuyển đến.",
        );
      },
    });
  }

  /**
   * Xuất chuyển kho — thùng mới thêm: gắn kho chuyển đến (whsCode của dòng vật tư),
   * giữ vị trí hiện tại ở sourceLocation, ô Vị trí để trống cho nhập vị trí mới.
   */
  private prepareTransferBoxes(boxes: ExportScannedBox[]): void {
    if (this.isPlanning) {
      return;
    }
    for (const b of boxes) {
      b.destWhsCode = this.findItems(b.materialCode)[0]?.toWhsCode;
      if (b.sourceLocation === undefined) {
        b.sourceLocation = b.location;
        b.location = "";
      }
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

  /** Khuyến nghị Planning: mọi thùng đã scan phải được Gửi trước khi Xác nhận xuất */
  private ensureAllSent(): boolean {
    if (!this.isPlanning) {
      return true;
    }
    const unsent = this.ws.boxes.filter((b) => !b.sent);
    if (unsent.length) {
      this.notificationService.warning(
        `Còn ${unsent.length} thùng chưa Gửi (${unsent
          .slice(0, 3)
          .map((b) => b.reelId)
          .join(
            ", ",
          )}${unsent.length > 3 ? "…" : ""}) — bấm Gửi trước khi Xác nhận xuất.`,
      );
      return false;
    }
    return true;
  }

  /** Vị trí (tên) → locationId theo danh sách vị trí đã cache (IndexedDB) */
  private async resolveLocation(
    name: string,
  ): Promise<{ locationId: number; locationName: string } | null> {
    const value = name.trim();
    if (!value) {
      return null;
    }
    const lower = value.toLowerCase();
    await this.warehouseCache.ensureSynced();
    const exact = await this.warehouseCache.findExactByFullName(value);
    const found =
      exact ??
      (await this.warehouseCache.searchByName(value)).find(
        (w) =>
          (w.locationFullName ?? "").trim().toLowerCase() === lower ||
          (w.locationName ?? "").trim().toLowerCase() === lower,
      );
    return found
      ? {
          locationId: Number(found.locationId),
          locationName: found.locationFullName || found.locationName || value,
        }
      : null;
  }

  /**
   * Xác nhận xuất (Khuyến nghị Planning + Xuất chuyển kho): tra locationId các vị trí →
   * POST api/request (cùng API dialog cập nhật vật tư) cập nhật SL xuất + vị trí từng thùng đã scan.
   */
  private submitInventoryUpdate(): void {
    // Bỏ thùng đã xuất ở lần trước (đang ở kho nhận) — tránh chuyển kho 2 lần
    const boxes = [...this.pendingExportBoxes];
    if (!boxes.length) {
      this.notificationService.warning(
        "Các thùng đã scan đều đã xuất — scan thêm thùng để xuất tiếp.",
      );
      return;
    }
    const noInventory = boxes.filter((b) => !b.inventoryId);
    if (noInventory.length) {
      this.notificationService.error(
        `Thiếu thông tin tồn kho của ${noInventory.length} thùng (${noInventory
          .slice(0, 3)
          .map((b) => b.reelId)
          .join(", ")}) — bỏ thùng và scan lại.`,
      );
      return;
    }
    this.isCompleting = true;
    const names = [...new Set(boxes.map((b) => b.location.trim()))];
    from(Promise.all(names.map((n) => this.resolveLocation(n))))
      .pipe(
        switchMap((locs) => {
          const byName = new Map(names.map((n, i) => [n, locs[i]]));
          const missing = boxes.filter((b) => !byName.get(b.location.trim()));
          if (missing.length) {
            throw new LocationError(
              `Vị trí không hợp lệ / chưa nhập ở ${missing.length} thùng (${missing
                .slice(0, 3)
                .map((b) => `${b.reelId}: "${b.location || "trống"}"`)
                .join(", ")}) — chọn vị trí trong danh sách gợi ý.`,
            );
          }
          const update$: Observable<unknown> = this.accountService
            .identity()
            .pipe(
              take(1),
              switchMap((account): Observable<unknown> => {
                const user = account?.login ?? "unknown";
                // Cùng payload dialog cập nhật vật tư: MOVE, tự phê duyệt
                return this.listMaterialService.postAutoApprovedUpdate(
                  boxes.map((box) => {
                    const loc = byName.get(box.location.trim())!;
                    return {
                      id: box.inventoryId ?? null,
                      materialId: box.reelId,
                      updatedBy: user,
                      createdTime: "",
                      updatedTime: "",
                      productCode: box.partNumber ?? box.materialCode,
                      productName: box.partNumber ?? box.materialCode,
                      // SL hiện tại của thùng → SL xuất
                      quantity: String(box.inventoryQty ?? box.quantity),
                      quantityChange: String(box.quantity),
                      type: "MOVE",
                      locationId: String(loc.locationId),
                      locationName: loc.locationName,
                      expiredTime: box.expirationRaw ?? "",
                      status: "APPROVE",
                      requestId: null,
                    };
                  }),
                  user,
                );
              }),
            );
          return update$;
        }),
      )
      .subscribe({
        next: () => {
          this.notificationService.success(
            `Xuất kho thành công: đã cập nhật ${boxes.length} thùng.`,
          );
          this.finishSessionStatus().subscribe(() => {
            this.isCompleting = false;
            this.dialogRef.close(true);
          });
        },
        error: (err: unknown) => {
          this.isCompleting = false;
          this.notificationService.error(
            err instanceof LocationError
              ? err.message
              : "Cập nhật tồn kho thất bại — chưa xuất, thử lại.",
          );
        },
      });
  }

  /** Lưu xuất kho của tab đang mở */
  private submitComplete(): void {
    this.submitInventoryUpdate();
  }

  /** Kho chuyển đến chung của mọi thùng đã scan (khác nhau → null) */
  private commonDestArea(): WarehouseAreaRef | null {
    const areas = this.ws.boxes.map((b) => this.destAreaOf(b));
    const first = areas[0] ?? null;
    return first && areas.every((a) => a?.areaCode === first.areaCode)
      ? first
      : null;
  }

  /**
   * Xuất chuyển kho: mọi thùng đã scan phải có vị trí thuộc đúng kho chuyển đến
   * (không bắt buộc scan đủ SL — có bao nhiêu thùng cập nhật bấy nhiêu).
   */
  private ensureTransferLocations(): boolean {
    if (!this.areaIndex || !this.warehouseAreas.length) {
      this.ensureAreaIndex();
      this.ensureWarehouseAreas();
      this.notificationService.warning(
        "Đang tải danh sách kho / vị trí — thử lại sau giây lát.",
      );
      return false;
    }
    const pending = this.pendingExportBoxes;
    const empty = pending.filter((b) => !b.location.trim());
    if (empty.length) {
      this.notificationService.warning(
        `Chưa nhập vị trí cho ${empty.length} thùng (${empty
          .slice(0, 3)
          .map((b) => b.reelId)
          .join(", ")}${empty.length > 3 ? "…" : ""}).`,
      );
      return false;
    }
    const noDest = pending.filter((b) => !this.destAreaOf(b));
    if (noDest.length) {
      this.notificationService.warning(
        `Không xác định được kho chuyển đến của ${noDest.length} thùng (${noDest
          .slice(0, 3)
          .map((b) => `${b.reelId}: kho ${b.destWhsCode ?? "?"}`)
          .join(", ")}).`,
      );
      return false;
    }
    const invalid = pending.filter((b) => this.locationIssue(b));
    if (invalid.length) {
      this.notificationService.warning(
        `Thùng ${invalid[0].reelId}: ${this.locationIssue(invalid[0])}` +
          (invalid.length > 1
            ? ` (+${invalid.length - 1} thùng sai vị trí)`
            : ""),
      );
      return false;
    }
    return true;
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
      `Chưa có ${this.codeNoun} — mở Chi tiết xuất từ danh sách.`,
    );
    this.ws.palletCode = "";
    this.ws.boxCode = "";
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
          this.applyReelInventory(planned.item, inv);
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
            partNumber: inv.partNumber,
            inventoryId: inv.inventoryId ?? undefined,
            expirationRaw: inv.expirationRaw,
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

  /**
   * Thùng khuyến nghị vừa tra kho (khi scan): dnlnvl chỉ có partNumberCode → lấy part number
   * (vd 1N4148W) + mã SAP từ kho cho dòng vật tư.
   */
  private applyReelInventory(
    item: ExportRequestItem,
    inv: ExportReelInventory | null,
  ): void {
    if (
      inv &&
      item.materialCode === item.partCode &&
      inv.partNumber &&
      inv.partId.toLowerCase() === (item.partCode ?? "").toLowerCase()
    ) {
      item.materialCode = inv.partNumber;
      item.materialName ||= inv.sapCode;
    }
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

  /**
   * Xuất chuyển kho: phiếu (owtqDocEntry = mã yêu cầu) + dòng WTQ1 mà thùng được phân vào
   * (theo ws.allocation — lấp đầy phiếu thêm trước). Chưa phân bổ → phiếu đầu tiên có mã vật tư.
   */
  private transferLinkOf(box: ExportScannedBox): {
    owtqDocEntry?: number;
    lineNum?: number;
  } {
    let item: ExportRequestItem | undefined;
    for (const [i, boxes] of this.ws.allocation) {
      if (boxes.includes(box)) {
        item = i;
        break;
      }
    }
    item ??= this.findItems(box.materialCode)[0];
    const request = item
      ? this.ws.requests.find((r) => r.items.includes(item!))
      : undefined;
    const docEntry = Number(request?.code);
    return {
      owtqDocEntry:
        Number.isFinite(docEntry) && docEntry > 0 ? docEntry : undefined,
      lineNum: item?.lineNum,
    };
  }

  /** Kiểm tra SL xuất nhập vào ô của thùng → câu báo lỗi, hợp lệ → null */
  private validateBoxQty(
    b: ExportScannedBox,
    value: number | string | null,
  ): string | null {
    if (value === null || value === "" || value === undefined) {
      return "Nhập số lượng xuất.";
    }
    const qty = Number(value);
    if (!Number.isFinite(qty)) {
      return "Số lượng không hợp lệ.";
    }
    if (qty < 0) {
      return "Số lượng không được là số âm.";
    }
    if (qty === 0) {
      return "Số lượng phải lớn hơn 0.";
    }
    if (b.inventoryQty !== undefined && qty > b.inventoryQty) {
      return `Không được lớn hơn SL trong thùng (${b.inventoryQty.toLocaleString("vi-VN")}).`;
    }
    return null;
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
      // Xuất chuyển kho, phiếu 1 vật tư: SL đã scan ở các phiên khác thuộc về vật tư đó
      if (!this.isPlanning && r.items.length === 1) {
        r.items[0].scannedQty = this.priorScans.get(r.code)?.qty ?? 0;
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

  /**
   * Thùng scan hợp lệ: nhiều hơn SL còn cần của mã vật tư (mọi yêu cầu trong phiên) → mở tách tem
   * (tem gốc đi theo SL xuất, phần dư sinh tem mới ở lại kho); đủ / thiếu → thêm luôn.
   */
  private acceptScannedBox(box: ExportScannedBox): void {
    const need = this.remainingNeed(box.materialCode);
    if (!this.isPlanning && need > 0 && box.quantity > need) {
      this.openSplit(box, need, () => this.addScannedBox(box));
      return;
    }
    this.addScannedBox(box);
  }

  private addScannedBox(box: ExportScannedBox): void {
    this.scanFeedback(true);
    this.prepareTransferBoxes([box]);
    this.ws.boxes = [...this.ws.boxes, box];
    this.scannedTab = "box";
    this.recalcScanned();
    this.saveSessionBoxes();
    this.focus(this.boxInput);
  }

  /** SL còn cần của 1 mã vật tư trên mọi yêu cầu đang mở (cần xuất − đã scan) */
  private remainingNeed(materialCode: string): number {
    return this.findItems(materialCode).reduce(
      (s, i) => s + Math.max(0, this.targetQty(i) - i.scannedQty),
      0,
    );
  }

  /**
   * Dialog tách tem: xác nhận → SL xuất = SL theo tem gốc; có phần dư → tạo tem tách ở vị trí
   * cũ rồi in ngay / đưa vào hàng chờ in. Hủy → không làm gì (thùng mới scan không được thêm).
   */
  private openSplit(
    box: ExportScannedBox,
    suggestedQty: number,
    onAccepted: () => void,
    needQty = suggestedQty,
  ): void {
    const boxQty = box.inventoryQty ?? box.quantity;
    const location = box.sourceLocation ?? box.location;
    openSplitReelDialog(this.dialog, {
      reelId: box.reelId,
      materialCode: box.materialCode,
      materialName: this.findItems(box.materialCode)[0]?.materialName ?? "",
      partNumber: box.partNumber ?? "",
      lot: box.lot,
      mfgDate: box.mfgDate,
      location,
      boxQty,
      suggestedQty,
      needQty,
      takenReelIds: this.ws.boxes.flatMap((b) =>
        b.split ? [b.reelId, b.split.reelId] : [b.reelId],
      ),
      rdLabel: this.buildRdLabel(box, null, "", 0),
    }).subscribe(
      (
        res:
          | {
              exportQty: number;
              remainderQty: number;
              newReelId: string | null;
              printNow: boolean;
              printTemplate: "rd" | "vendor";
            }
          | undefined,
      ) => {
        if (!res) {
          this.notificationService.info(
            `Đã hủy — thùng "${box.reelId}" chưa được thêm / tách.`,
          );
          this.focus(this.boxInput);
          return;
        }
        box.quantity = res.exportQty;
        if (!res.newReelId || res.remainderQty <= 0) {
          onAccepted();
          return;
        }
        const newReelId = res.newReelId;
        this.isBusy = true;
        // Lưu vào phiên trước (thùng cha + mã tách) → lưu được mới gửi PanaCIM tách
        this.persistSplitInSession(box, newReelId, res.remainderQty)
          .pipe(
            switchMap((saved) =>
              this.exportService
                .splitReelOnPana({
                  reelId: box.reelId,
                  splitQty: res.remainderQty,
                  newReelId,
                  comment:
                    `Tách tem xuất chuyển kho` +
                    (this.dialogData?.sessionId != null
                      ? ` - phiên #${this.dialogData.sessionId}`
                      : ""),
                })
                .pipe(
                  catchError(() =>
                    of({
                      newBox: null as ExportScannedBox | null,
                      error: `Lỗi khi tách tem thùng "${box.reelId}".` as
                        | string
                        | null,
                    }),
                  ),
                  map((out) => ({ saved, out })),
                ),
            ),
          )
          .subscribe({
            next: ({
              saved,
              out,
            }: {
              saved: SplitSessionSave;
              out: {
                newBox: ExportScannedBox | null;
                error: string | null;
                uncertain?: boolean;
              };
            }) => {
              this.isBusy = false;
              if (out.error !== null || !out.newBox) {
                // PanaCIM chắc chắn chưa tách → gỡ các bản ghi vừa lưu vào phiên;
                // đã gửi nhưng chưa tra được mã mới → giữ lại để mở lại phiên vẫn thấy
                if (!out.uncertain) {
                  this.rollbackSplitSave(box, saved);
                }
                this.notificationService.error(
                  out.error ??
                    `Tách tem thùng "${box.reelId}" không thành công.`,
                );
                this.focus(this.boxInput);
                this.cdr.markForCheck();
                return;
              }
              // Tem gốc giữ phần xuất đi (tồn sau tách = SL xuất)
              box.inventoryQty = res.exportQty;
              box.split = {
                reelId: newReelId,
                quantity: res.remainderQty,
                location,
                printStatus: res.printNow ? undefined : "queued",
              };
              onAccepted();
              if (saved.parentId != null || saved.childId != null) {
                this.markSessionExporting();
                this.loadRequestSummaries(this.ws.requests.map((r) => r.code));
              }
              this.saveSplitInventory(box, out.newBox);
              this.notificationService.success(
                `Đã tách: tem gốc ${box.reelId} xuất ${res.exportQty.toLocaleString("vi-VN")}, ` +
                  `tem mới ${newReelId} (${res.remainderQty.toLocaleString("vi-VN")}) ở lại ${location || "kho"}.`,
              );
              if (res.printNow && res.printTemplate === "rd") {
                this.printSplitLabelRd(box, out.newBox);
              } else if (res.printNow) {
                this.printSplitLabel(box);
              } else {
                this.queueSplitLabel(box);
              }
            },
            error: () => {
              this.isBusy = false;
              this.notificationService.error(
                `Chưa lưu được mã tách ${newReelId} vào phiên — chưa tách tem thùng "${box.reelId}", vui lòng thử lại.`,
              );
              this.focus(this.boxInput);
              this.cdr.markForCheck();
            },
          });
      },
    );
  }

  /**
   * Trước khi tách trên PanaCIM: lưu vào phiên (POST /warehouse-transfer-details)
   * 1) thùng cha nếu chưa lưu (SL xuất = SL theo tem gốc), 2) mã tách để tham chiếu: SL xuất = 0,
   * không vị trí chuyển đến, initialQuantity = SL tách, parentMaterialIdentifier + comments
   * SPLIT_FROM = ReelID thùng cha → mở lại phiên vẫn thấy và in lại được.
   * Không có phiên → không lưu gì. Lỗi → Observable lỗi (chưa tách); bản ghi đã lưu được gỡ lại.
   */
  private persistSplitInSession(
    parent: ExportScannedBox,
    newReelId: string,
    remainderQty: number,
  ): Observable<SplitSessionSave> {
    const sessionId = this.dialogData?.sessionId;
    const none: SplitSessionSave = { parentId: null, childId: null };
    if (!this.hasSession || sessionId == null) {
      return of(none);
    }
    const child: ExportScannedBox = {
      ...parent,
      reelId: newReelId,
      quantity: 0,
      location: "",
      split: undefined,
      sent: undefined,
    };
    return this.accountService.identity().pipe(
      take(1),
      switchMap((account) => {
        const user = account?.login ?? "unknown";
        const saveParent$: Observable<number | null | undefined> = parent.sent
          ? of(undefined)
          : this.exportService.saveTransferDetail(
              sessionId,
              parent,
              this.transferLinkOf(parent),
              user,
            );
        return saveParent$.pipe(
          switchMap((parentId) => {
            const saved: SplitSessionSave = {
              // undefined = thùng cha đã có trong phiên từ trước (không gỡ khi rollback)
              parentId: parentId === undefined ? null : parentId,
              childId: null,
            };
            if (parentId !== undefined) {
              parent.sent = true;
              this.savedDetailIds.set(this.reelKey(parent.reelId), parentId);
            }
            return this.exportService
              .saveTransferDetail(
                sessionId,
                child,
                this.transferLinkOf(parent),
                user,
                {
                  parentMaterialIdentifier: parent.reelId,
                  comments: splitFromComment(parent.reelId),
                  initialQuantity: remainderQty,
                },
              )
              .pipe(
                map((childId) => {
                  this.savedDetailIds.set(this.reelKey(newReelId), childId);
                  return { ...saved, childId };
                }),
                catchError((err: unknown) => {
                  this.rollbackSplitSave(parent, saved);
                  return throwError(() => err);
                }),
              );
          }),
        );
      }),
    );
  }

  /**
   * Tách thành công trên PanaCIM → lưu lịch sử tách (POST /split-inventories), nghiệp vụ
   * "Chuyển kho", businessSourceId = mã lệnh chuyển kho (owtqDocEntry). Lỗi chỉ cảnh báo.
   */
  private saveSplitInventory(
    parent: ExportScannedBox,
    child: ExportScannedBox,
  ): void {
    const split = parent.split;
    if (!split) {
      return;
    }
    this.accountService
      .identity()
      .pipe(
        take(1),
        switchMap((account) =>
          this.splitInventoryService.save(
            buildSplitInventoryPayload({
              sources: [child.inventoryRaw, parent.inventoryRaw],
              businessSourceId:
                this.transferLinkOf(parent).owtqDocEntry ?? null,
              businessType: SPLIT_BUSINESS_TYPE.transfer,
              reelId: split.reelId,
              parentReelId: parent.reelId,
              quantity: split.quantity,
              user: account?.login ?? "unknown",
              overrides: {
                lot: child.lot || parent.lot || null,
                partNumber: child.partNumber ?? parent.partNumber ?? null,
                sapCode: parent.materialCode || null,
                locationOverride: split.location || null,
              },
            }),
          ),
        ),
      )
      .subscribe({
        error: () =>
          this.notificationService.warning(
            `Đã tách nhưng chưa lưu được lịch sử tách tem ${split.reelId}.`,
          ),
      });
  }

  /** Tách không thành công → xoá bản ghi vừa lưu vào phiên (mã tách, thùng cha nếu vừa lưu mới) */
  private rollbackSplitSave(
    parent: ExportScannedBox,
    saved: SplitSessionSave,
  ): void {
    const ids: number[] = [];
    if (saved.childId != null) {
      ids.push(saved.childId);
    }
    if (saved.parentId != null) {
      ids.push(saved.parentId);
      parent.sent = false;
      this.savedDetailIds.delete(this.reelKey(parent.reelId));
    }
    ids.forEach((id) =>
      this.exportService
        .deleteTransferDetail(id)
        .pipe(catchError(() => of(undefined)))
        .subscribe(),
    );
  }

  /** Mở lại phiên: gắn các mã tách đã lưu (parentMaterialIdentifier) vào thùng cha */
  private attachSavedSplits(): void {
    for (const saved of this.sessionBoxes.values()) {
      if (!saved.parentReelId) {
        continue;
      }
      const parent = this.ws.boxes.find(
        (b) => this.reelKey(b.reelId) === this.reelKey(saved.parentReelId),
      );
      if (parent && !parent.split) {
        parent.split = {
          reelId: saved.reelId,
          quantity: saved.initialQuantity,
          location: parent.sourceLocation ?? "",
        };
      }
    }
  }

  /** In tem tách ngay — dialog In tem nhà cung cấp (như tách thùng ở Nhập tem NCC) */
  private printSplitLabel(box: ExportScannedBox): void {
    const split = box.split;
    if (!split) {
      return;
    }
    this.dialog.open(VendorNccPrintDialogComponent, {
      width: "100vw",
      height: "100vh",
      maxWidth: "100vw",
      maxHeight: "100vh",
      panelClass: "vendor-ncc-print-dialog-panel",
      data: {
        labels: [
          {
            id: split.reelId,
            reelId: split.reelId,
            qrCode: split.reelId,
            sapCode: box.materialCode,
            partNumber: box.partNumber ?? "",
            vendorItemName:
              this.findItems(box.materialCode)[0]?.materialName ?? "",
            quantity: split.quantity,
            weight: "",
            meas: "",
            poNo: "",
            invoiceNo: "",
            boxNo: "",
            vendorName: "",
            madeIn: "",
            color: "",
            operator: "",
            grossNetWeight: "",
            contractNo: "",
            batchNo: box.lot,
            mfgDate: box.mfgDate,
          },
        ],
        requestLabel: "tach-tem-xuat",
      },
    });
  }

  /** In tem tách theo mẫu Rạng Đông (giống Preview In Tem ở Nhận vật tư) */
  private printSplitLabelRd(
    box: ExportScannedBox,
    child: ExportScannedBox | null,
  ): void {
    const split = box.split;
    if (!split) {
      return;
    }
    openSplitLabelPrintDialog(this.dialog, [
      this.buildRdLabel(box, child, split.reelId, split.quantity),
    ]);
  }

  /**
   * Tem mẫu Rạng Đông cho tem tách: thông tin lấy từ bản ghi tồn kho của tem tách (vừa tạo
   * trên PanaCIM), thiếu thì lấy theo tem gốc. child = null → chỉ theo tem gốc (preview).
   */
  private buildRdLabel(
    box: ExportScannedBox,
    child: ExportScannedBox | null,
    reelId: string,
    quantity: number,
  ): SplitPrintLabel {
    const raws = [child?.inventoryRaw ?? {}, box.inventoryRaw ?? {}];
    const field = (key: string): string => {
      for (const raw of raws) {
        const v = raw[key];
        const text = v === null || v === undefined ? "" : String(v).trim();
        if (text) {
          return text;
        }
      }
      return "";
    };
    return {
      id: reelId,
      qrCode: reelId,
      reelId,
      sapCode: box.materialCode,
      productName:
        this.findItems(box.materialCode)[0]?.materialName ??
        field("materialName"),
      partNumber: child?.partNumber ?? box.partNumber ?? field("partNumber"),
      lot: child?.lot ?? box.lot,
      initialQuantity: quantity,
      vendor: field("vendor"),
      userData1: field("userData1"),
      userData2: field("userData2"),
      userData3: field("userData3"),
      userData5: field("userData5"),
      storageUnit: field("storageUnit"),
      manufacturingDate: toLabelDate(field("manufacturingDate") || box.mfgDate),
      expirationDate: toLabelDate(field("expirationDate")),
      arrivalDate: toLabelDate(field("receivedDate")),
    };
  }

  /** Lưu tem tách vào hàng chờ in (in hàng loạt sau ở khu máy in) */
  private queueSplitLabel(box: ExportScannedBox): void {
    const split = box.split;
    if (!split) {
      return;
    }
    this.exportService
      .queueSplitLabel({
        reelId: split.reelId,
        originalReelId: box.reelId,
        quantity: split.quantity,
        location: split.location,
        materialCode: box.materialCode,
      })
      .subscribe({
        next: (ok: boolean) => {
          if (ok) {
            this.notificationService.info(
              `Tem ${split.reelId} đã vào hàng chờ in.`,
            );
          } else {
            this.notificationService.error(
              `Không đưa được tem ${split.reelId} vào hàng chờ in.`,
            );
          }
        },
        error: () => {
          this.notificationService.error(
            `Lỗi khi đưa tem ${split.reelId} vào hàng chờ in.`,
          );
        },
      });
  }

  /** Gom thùng đã scan theo lô + ngày SX → mỗi lô 1 dòng trên phiếu (SL = tổng SL các thùng) */
  private batchesOf(
    boxes: ExportScannedBox[],
  ): Array<{ lot: string; mfgDate: string; qty: number }> {
    const byLot = new Map<
      string,
      { lot: string; mfgDate: string; qty: number }
    >();
    for (const b of boxes) {
      const lot = (b.lot ?? "").trim();
      const mfgDate = (b.mfgDate ?? "").trim();
      const key = `${lot.toLowerCase()}|${mfgDate}`;
      const batch = byLot.get(key) ?? { lot, mfgDate, qty: 0 };
      batch.qty += Number(b.quantity) || 0;
      byLot.set(key, batch);
    }
    return [...byLot.values()];
  }

  /** Dựng phiếu Đề nghị lĩnh vật tư rồi in (fetchedStock: tồn vừa tra cho mã chưa có) */
  private buildAndPrint(
    requests: ExportRequest[],
    fetchedStock: Map<string, number>,
  ): void {
    const uniq = (values: string[]): string[] => [
      ...new Set(values.map((v) => v.trim()).filter(Boolean)),
    ];
    // Đầu phiếu OWTQ (deliveryDate / fromLabel / toLabel) — khai báo tại chỗ
    const infoOf = (
      r: ExportRequest,
    ): { deliveryDate: string; fromLabel: string; toLabel: string } =>
      (
        r as {
          transferInfo?: {
            deliveryDate: string;
            fromLabel: string;
            toLabel: string;
          };
        }
      ).transferInfo ?? { deliveryDate: "", fromLabel: "", toLabel: "" };
    const doc: TransferPrintDoc = {
      deliveryDate: uniq(requests.map((r) => infoOf(r).deliveryDate)).join(
        ", ",
      ),
      lines: requests.flatMap((r) =>
        r.items.map((i) => {
          const stock = this.itemStock(i);
          const boxes = this.boxesOf(i);
          return {
            requestCode: r.code,
            itemCode: i.materialCode,
            itemName: i.materialName,
            partNumber: i.partNumbers ?? "",
            batches: this.batchesOf(boxes),
            uom: i.uom ?? "",
            requestedQty: i.requiredQty,
            stockQty:
              stock && !stock.loading && !stock.error
                ? this.itemStockQty(i)
                : (fetchedStock.get(i.materialCode.trim().toLowerCase()) ??
                  null),
            issuedQty: i.scannedQty,
            fromLabel: infoOf(r).fromLabel,
            toLabel: infoOf(r).toLabel,
            note: "",
          };
        }),
      ),
    };
    printTransferRequests(doc);
  }

  private emptyWorkspace(): ExportWorkspace {
    return {
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
