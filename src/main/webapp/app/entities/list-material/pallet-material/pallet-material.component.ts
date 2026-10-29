import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  HostListener,
  OnInit,
  ViewChild,
} from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { forkJoin, from, Observable, of, take } from "rxjs";
import { catchError, map, shareReplay, switchMap } from "rxjs/operators";
import { AccountService } from "app/core/auth/account.service";
import { NotificationService } from "../services/notification.service";
import { WarehouseCacheService } from "../services/warehouse-cache.service";
import { normalizeLocationScan } from "../services/scan-code.util";
import {
  PalletBoxRecord,
  PalletMaterialService,
  PalletMngtDetail,
  PalletBoxMappingRow,
  PalletMngtRow,
  PalletSummary,
  sortPalletsNewestFirst,
  summarizePallet,
} from "../services/pallet-material.service";
import { DialogContentExampleDialogComponent } from "../confirm-dialog/confirm-dialog.component";
import {
  ListMaterialService,
  RawGraphQLMaterial,
} from "../services/list-material.service";

/** Kết quả đổi vị trí thùng (PalletMaterialService.updateBoxLocations) */
interface LocationUpdateResult {
  ok: boolean;
  failedReels: string[];
}

/** Chế độ ô scan: chọn pallet / chọn thùng / thêm thùng / gỡ thùng / cập nhật vị trí */
type ScanMode = "pallet" | "box" | "add" | "remove" | "location";

interface BoxRow {
  key: string;
  record: PalletBoxRecord | null;
  reelId: string;
  partNumber: string;
  /** Mã vật tư (sapCode / phần đầu userData4) + tên vật tư (item-data, như Danh sách vật tư) */
  itemCode: string;
  materialName: string;
  quantity: number;
  /** Số lượng mới (chưa gửi) */
  newQuantity: number | null;
  location: string;
  lot: string;
  status: string;
  /** Người cập nhật */
  updatedBy: string;
  /** Ngày sản xuất dd/MM/yyyy */
  mfgDate: string;
  /** Ngày nhận dd/MM/yyyy */
  receivedDate: string;
  selected: boolean;
  /** Đã đánh dấu gỡ (chưa gửi) */
  pendingRemove: boolean;
  /** Thùng mới scan thêm (chưa gửi) */
  pendingAdd: boolean;
  /** Vị trí mới (chưa gửi) */
  newLocation: string | null;
  /** Thông tin tồn kho (/api/inventory) — có sẵn khi thùng vừa quét thêm */
  inventory?: RawGraphQLMaterial;
  /** Thùng đang ở pallet khác, chọn "Chuyển" → gỡ liên kết cũ trước khi thêm vào pallet này */
  moveFrom?: { mappingId: number; serialPallet: string };
}

interface LocationOption {
  locationName: string;
  locationFullName: string;
}

@Component({
  selector: "jhi-pallet-material",
  templateUrl: "./pallet-material.component.html",
  styleUrls: ["./pallet-material.component.scss"],
  standalone: false,
})
export class PalletMaterialComponent implements OnInit, AfterViewInit {
  @ViewChild("scanInputRef") scanInputRef?: ElementRef<HTMLInputElement>;
  @ViewChild("palletSearchRef")
  palletSearchRef?: ElementRef<HTMLInputElement>;

  /** ≤ 768px → hiển thị view mobile riêng */
  isMobile = this.detectMobile();

  /** Hiện chức năng Xuất hàng (gỡ thùng khỏi pallet + số lượng thùng = 0) */
  readonly showExport = true;

  pallets: PalletMngtRow[] = [];
  palletSearch = "";
  isLoadingPallets = false;

  selectedSerial = "";
  detail: PalletMngtDetail | null = null;
  rows: BoxRow[] = [];
  isLoadingDetail = false;
  isSaving = false;

  scanMode: ScanMode | null = null;
  scanValue = "";
  locationOptions: LocationOption[] = [];

  /** Vị trí mới của pallet (chưa gửi) — đặt khi quét vị trí, kể cả pallet chưa có thùng */
  pendingPalletLocation: string | null = null;

  /** Số thùng đang tra cứu /api/inventory (vừa quét) */
  lookingUpCount = 0;

  private currentUser = "";
  private locationSeq = 0;
  /** ReelID đang tra cứu — chặn quét trùng khi chưa có kết quả */
  private lookingUpReels = new Set<string>();
  /** Đổi mỗi lần tải lại danh sách — bỏ kết quả tính số lượng của lần tải cũ */
  private summarySeq = 0;

  constructor(
    private palletService: PalletMaterialService,
    private warehouseCache: WarehouseCacheService,
    private notificationService: NotificationService,
    private accountService: AccountService,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef,
    private materialService: ListMaterialService,
  ) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(take(1))
      .subscribe((account) => {
        this.currentUser = account?.login ?? "";
      });
    void this.warehouseCache.ensureSynced().catch(() => undefined);
    this.loadPallets();
  }

  ngAfterViewInit(): void {
    // Vào trang → focus ô tìm mã pallet để scan luôn (mobile có view riêng)
    if (!this.isMobile) {
      this.focusPalletSearch();
    }
  }

  @HostListener("window:resize")
  onResize(): void {
    this.isMobile = this.detectMobile();
  }

  // ==================== Getters ====================

  get filteredPallets(): PalletMngtRow[] {
    const term = this.palletSearch.trim().toLowerCase();
    return term
      ? this.pallets.filter((p) =>
          (p.serialPallet ?? "").toLowerCase().includes(term),
        )
      : this.pallets;
  }

  /** Các thùng còn trong pallet sau khi áp thay đổi (không tính thùng chờ gỡ) */
  get activeRows(): BoxRow[] {
    return this.rows.filter((r) => !r.pendingRemove);
  }

  get activeQty(): number {
    return this.activeRows.reduce((s, r) => s + r.quantity, 0);
  }

  get selectedRows(): BoxRow[] {
    return this.rows.filter((r) => r.selected && !r.pendingRemove);
  }

  get allSelected(): boolean {
    const rows = this.activeRows;
    return rows.length > 0 && rows.every((r) => r.selected);
  }

  get someSelected(): boolean {
    return this.selectedRows.length > 0 && !this.allSelected;
  }

  get hasChanges(): boolean {
    return (
      this.pendingPalletLocation !== null ||
      this.rows.some(
        (r) =>
          r.pendingRemove ||
          r.pendingAdd ||
          r.newLocation !== null ||
          r.newQuantity !== null,
      )
    );
  }

  get changeSummary(): string {
    const remove = this.rows.filter(
      (r) => r.pendingRemove && !r.pendingAdd,
    ).length;
    const add = this.rows.filter(
      (r) => r.pendingAdd && !r.pendingRemove,
    ).length;
    const loc = this.rows.filter(
      (r) => r.newLocation !== null && !r.pendingRemove && !r.pendingAdd,
    ).length;
    const qty = this.rows.filter(
      (r) => r.newQuantity !== null && !r.pendingRemove,
    ).length;
    const parts: string[] = [];
    if (this.pendingPalletLocation !== null && !loc) {
      parts.push(`đổi vị trí pallet → ${this.pendingPalletLocation}`);
    }
    if (remove) {
      parts.push(`gỡ ${remove} thùng`);
    }
    if (add) {
      parts.push(`thêm ${add} thùng`);
    }
    if (loc) {
      parts.push(`đổi vị trí ${loc} thùng`);
    }
    if (qty) {
      parts.push(`sửa số lượng ${qty} thùng`);
    }
    return parts.join(" · ");
  }

  get palletLocation(): string {
    if (this.pendingPalletLocation !== null) {
      return this.pendingPalletLocation;
    }
    const locs = new Set(
      this.activeRows.map((r) => r.newLocation ?? r.location).filter(Boolean),
    );
    if (locs.size === 1) {
      return [...locs][0];
    }
    return (
      this.detail?.locationName ?? (locs.size > 1 ? "Nhiều vị trí" : "Chưa gán")
    );
  }

  get scanPlaceholder(): string {
    switch (this.scanMode) {
      case "pallet":
        return "Quét / nhập mã pallet rồi Enter...";
      case "box":
        return "Quét mã thùng → mở pallet chứa thùng và chọn thùng đó...";
      case "add":
        return "Quét mã thùng để thêm vào pallet...";
      case "remove":
        return "Quét mã thùng để gỡ khỏi pallet...";
      case "location":
        return this.selectedRows.length
          ? `Quét / chọn vị trí cho ${this.selectedRows.length} thùng đã chọn...`
          : "Quét / chọn vị trí cho tất cả thùng...";
      default:
        return "";
    }
  }

  get scanTitle(): string {
    switch (this.scanMode) {
      case "pallet":
        return "Scan mã pallet";
      case "box":
        return "Scan mã thùng";
      case "add":
        return "Scan thêm thùng";
      case "remove":
        return "Scan thùng để gỡ";
      case "location":
        return "Cập nhật vị trí";
      default:
        return "";
    }
  }

  // ==================== Pallet list ====================

  onSelectPallet(p: PalletMngtRow): void {
    if (p.serialPallet === this.selectedSerial) {
      return;
    }
    this.confirmDiscard().subscribe((ok) => {
      if (ok) {
        // Chọn pallet → tự tích chọn tất cả thùng trong pallet
        this.openPallet(p.serialPallet, true);
      }
    });
  }

  /**
   * Enter ở ô tìm pallet (máy scan tự gửi Enter): khớp đúng mã hoặc chỉ còn 1 kết quả
   * → mở pallet đó; không có trong danh sách → popup [Quét mã khác] / [Tạo pallet].
   */
  onPalletSearchEnter(): void {
    const code = this.palletSearch.trim();
    if (!code) {
      return;
    }
    const lower = code.toLowerCase();
    const exact = this.pallets.find(
      (p) => (p.serialPallet ?? "").toLowerCase() === lower,
    );
    const list = this.filteredPallets;
    const target = exact ?? (list.length === 1 ? list[0] : null);
    this.confirmDiscard().subscribe((ok) => {
      if (!ok) {
        this.focusPalletSearch();
        return;
      }
      this.palletSearch = "";
      if (target) {
        this.closeScan();
        this.openPallet(target.serialPallet, true);
        this.focusPalletSearch();
        return;
      }
      // Không có trong danh sách → hỏi quét mã khác / tạo pallet
      this.askCreatePallet(code, () => this.focusPalletSearch());
    });
  }

  // ==================== Scan bar ====================

  openScan(mode: ScanMode): void {
    if (mode !== "pallet" && mode !== "box" && !this.detail) {
      this.notificationService.warning("Chọn hoặc quét một pallet trước.");
      return;
    }
    this.scanMode = this.scanMode === mode ? null : mode;
    this.scanValue = "";
    this.locationOptions = [];
    setTimeout(() => this.scanInputRef?.nativeElement?.focus(), 30);
  }

  /** Chip Pallet / Thùng trên thanh scan: đổi chế độ (không đóng thanh scan) */
  switchScanMode(mode: ScanMode): void {
    if (this.scanMode === mode) {
      return;
    }
    this.scanMode = mode;
    this.scanValue = "";
    this.locationOptions = [];
    setTimeout(() => this.scanInputRef?.nativeElement?.focus(), 30);
  }

  closeScan(): void {
    this.scanMode = null;
    this.scanValue = "";
    this.locationOptions = [];
  }

  onScanInput(value: string): void {
    if (this.scanMode !== "location") {
      return;
    }
    // Chuỗi scan vị trí: bỏ "LO", -SLSLOT/-SLRACK, ký tự rác cuối (như ô Quét kho)
    const term = normalizeLocationScan(value);
    if (/^LO/i.test(value.trim())) {
      // Chuỗi scan có tiền tố "LO" → hiện lại mã đã cắt trong ô (sau nhịp ngModel hiện tại)
      setTimeout(() => {
        if (this.scanValue === value) {
          this.scanValue = term;
          this.cdr.markForCheck();
        }
      });
    }
    const seq = ++this.locationSeq;
    if (!term) {
      this.locationOptions = [];
      return;
    }
    void this.warehouseCache
      .searchByName(term)
      .then((list) => {
        if (seq === this.locationSeq) {
          this.locationOptions = list.map((w) => ({
            locationName: w.locationName,
            locationFullName: w.locationFullName,
          }));
          this.cdr.markForCheck();
        }
      })
      .catch(() => undefined);
  }

  onScanEnter(): void {
    const raw = this.scanValue.trim();
    if (!raw) {
      return;
    }
    // Mã thùng dạng "ReelID#Part#..." → lấy phần trước dấu #
    const code = raw.split("#")[0].trim();
    switch (this.scanMode) {
      case "pallet":
        this.scanPallet(code);
        break;
      case "box":
        this.scanBox(code);
        break;
      case "add":
        this.stageAdd(code);
        break;
      case "remove":
        this.stageRemoveByReel(code);
        break;
      case "location":
        this.applyLocation(raw);
        break;
      default:
        break;
    }
    this.scanValue = "";
    this.locationOptions = [];
    setTimeout(() => this.scanInputRef?.nativeElement?.focus(), 0);
  }

  onLocationPicked(value: string): void {
    this.applyLocation(value);
    this.scanValue = "";
    this.locationOptions = [];
  }

  // ==================== Box actions (chưa gửi — bấm Xác nhận mới lưu) ====================

  toggleAll(checked: boolean): void {
    for (const r of this.activeRows) {
      r.selected = checked;
    }
  }

  /** Sửa số lượng trong thùng — bấm "Xác nhận cập nhật" mới gửi (api/request như đổi vị trí) */
  onQuantityChange(row: BoxRow, value: number | string | null): void {
    const qty = Number(value);
    if (value === null || value === "" || !Number.isFinite(qty) || qty < 0) {
      row.newQuantity = null;
      return;
    }
    row.newQuantity = qty === row.quantity ? null : Math.floor(qty);
  }

  stageRemove(row: BoxRow): void {
    if (row.pendingAdd) {
      // Thùng mới thêm chưa gửi → bỏ khỏi danh sách luôn
      this.rows = this.rows.filter((r) => r !== row);
      return;
    }
    row.pendingRemove = true;
    row.selected = false;
  }

  undoRemove(row: BoxRow): void {
    row.pendingRemove = false;
  }

  stageRemoveSelected(): void {
    const rows = this.selectedRows;
    if (!rows.length) {
      this.notificationService.warning("Chưa chọn thùng nào.");
      return;
    }
    rows.forEach((r) => this.stageRemove(r));
  }

  stageRemoveAll(): void {
    const rows = this.activeRows;
    if (!rows.length) {
      return;
    }
    this.confirm(
      "Gỡ tất cả thùng",
      `Đánh dấu gỡ ${rows.length} thùng khỏi pallet ${this.selectedSerial}? Bấm "Xác nhận cập nhật" để lưu.`,
      "Gỡ tất cả",
    ).subscribe((ok) => {
      if (ok) {
        [...rows].forEach((r) => this.stageRemove(r));
      }
    });
  }

  /**
   * Xuất hàng: thùng đã chọn (không chọn → tất cả thùng trong pallet) được gỡ khỏi pallet
   * và đặt số lượng = 0. Gửi ngay sau khi xác nhận (không chờ "Xác nhận cập nhật").
   */
  onExport(): void {
    if (!this.detail || this.isSaving) {
      return;
    }
    if (this.hasChanges) {
      this.notificationService.warning(
        `Pallet ${this.selectedSerial} có thay đổi chưa lưu (${this.changeSummary}) — xác nhận cập nhật hoặc hủy trước khi xuất hàng.`,
      );
      return;
    }
    const bySelection = this.selectedRows.length > 0;
    const rows = (bySelection ? this.selectedRows : this.activeRows).filter(
      (r) => !!r.record,
    );
    if (!rows.length) {
      this.notificationService.warning(
        "Pallet chưa có thùng nào để xuất hàng.",
      );
      return;
    }
    const serial = this.selectedSerial;
    this.confirm(
      "Xác nhận xuất hàng",
      `Xuất hàng ${rows.length} thùng ${bySelection ? "đã chọn" : "trong pallet"} ${serial}? Thùng sẽ được gỡ khỏi pallet và số lượng về 0.`,
      "Xuất hàng",
    ).subscribe((ok) => {
      if (ok) {
        this.exportRows(serial, rows);
      }
    });
  }

  onCancel(): void {
    if (!this.hasChanges) {
      return;
    }
    this.confirmDiscard().subscribe((ok) => {
      if (ok && this.selectedSerial) {
        this.openPallet(this.selectedSerial);
      }
    });
  }

  /** Gửi thay đổi: DELETE mapping (gỡ), POST mapping (thêm), PUT thùng (vị trí), PUT pallet (trạng thái, vị trí) */
  onConfirm(): void {
    if (!this.detail || !this.hasChanges || this.isSaving) {
      return;
    }
    const serial = this.selectedSerial;
    const tasks: Array<Observable<boolean>> = [];
    // Thùng không có palletBoxMappingId trong chi tiết pallet → tra GET /pallet-box-mappings (1 lần)
    const mappings$ = this.lazyBoxMappings();
    const unresolved: string[] = [];

    for (const r of this.rows) {
      if (r.pendingAdd) {
        if (!r.pendingRemove) {
          tasks.push(this.addRowToPallet(serial, r));
        }
        continue;
      }
      if (r.pendingRemove) {
        tasks.push(
          this.removeRowFromPallet(serial, r, mappings$).pipe(
            map((res): boolean => {
              if (res === "notFound") {
                unresolved.push(r.reelId || r.key);
              }
              return res === true;
            }),
          ),
        );
        continue;
      }
    }

    // Thùng đổi vị trí / số lượng (kể cả thùng vừa quét thêm): inventory trước, 404 → tem NCC
    const moved = this.rows.filter(
      (r) =>
        (r.newLocation !== null || r.newQuantity !== null) && !r.pendingRemove,
    );
    const locationFailed: string[] = [];
    if (moved.length) {
      tasks.push(
        this.palletService
          .updateBoxLocations(
            moved.map((r) => ({
              reelId: r.reelId,
              record: r.record,
              location: r.newLocation,
              quantity: r.newQuantity,
              inventory: r.inventory,
            })),
            this.currentUser,
          )
          .pipe(
            map((res: LocationUpdateResult): boolean => {
              locationFailed.push(...res.failedReels);
              return res.ok;
            }),
          ),
      );
    }

    const remaining = this.activeRows.length;
    const location = this.palletLocation;
    const d = this.detail;
    tasks.push(
      this.safe(
        this.palletService.updatePallet({
          id: d.id,
          serialPallet: d.serialPallet,
          locationName:
            location && location !== "Chưa gán" && location !== "Nhiều vị trí"
              ? location
              : d.locationName,
          status: remaining > 0 ? "IN_USE" : "UNUSED",
          note: d.note,
          createAt: d.createAt,
          createBy: d.createBy,
          updatedAt: new Date().toISOString(),
          updatedBy: this.currentUser,
        }),
      ),
    );

    this.isSaving = true;
    forkJoin(tasks).subscribe((results: boolean[]) => {
      this.isSaving = false;
      const ok = results.filter(Boolean).length;
      const failed = results.length - ok;
      if (!failed) {
        this.notificationService.success(`Đã cập nhật pallet ${serial}.`);
      } else if (!ok) {
        this.notificationService.error(`Cập nhật pallet ${serial} thất bại.`);
      } else {
        this.notificationService.warning(
          `Cập nhật ${ok}/${results.length} thao tác, ${failed} thao tác lỗi.`,
        );
      }
      if (locationFailed.length) {
        this.notificationService.error(
          `Không cập nhật được vị trí / số lượng ${locationFailed.length} thùng: ${locationFailed
            .slice(0, 5)
            .join(", ")}${locationFailed.length > 5 ? "…" : ""}`,
        );
      }
      if (unresolved.length) {
        this.notificationService.error(
          `Không tìm thấy liên kết pallet để gỡ ${unresolved.length} thùng: ${unresolved
            .slice(0, 5)
            .join(", ")}${unresolved.length > 5 ? "…" : ""}`,
        );
      }
      this.pendingPalletLocation = null;
      this.openPallet(serial);
      this.loadPallets();
    });
  }

  trackPallet(_: number, p: PalletMngtRow): number {
    return p.id;
  }

  trackRow(_: number, r: BoxRow): string {
    return r.key;
  }

  formatDateTime(value: string | null | undefined): string {
    if (!value) {
      return "—";
    }
    const d = new Date(value);
    if (Number.isNaN(d.getTime())) {
      return value;
    }
    const pad = (n: number): string => String(n).padStart(2, "0");
    return `${pad(d.getHours())}:${pad(d.getMinutes())} ${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()}`;
  }

  // ==================== Private ====================

  private loadPallets(): void {
    this.isLoadingPallets = true;
    this.palletService.getPallets().subscribe({
      next: (rows: PalletMngtRow[]) => {
        // Pallet tạo mới nhất lên đầu
        this.pallets = sortPalletsNewestFirst<PalletMngtRow>(
          rows,
          (p: PalletMngtRow): PalletMngtRow => p,
        );
        this.isLoadingPallets = false;
        if (!this.selectedSerial && this.pallets.length) {
          this.openPallet(this.pallets[0].serialPallet);
        }
        this.loadSummaries();
      },
      error: () => {
        this.isLoadingPallets = false;
        this.notificationService.error("Không tải được danh sách pallet.");
      },
    });
  }

  private openPallet(
    serial: string,
    selectAll = false,
    selectReels: string[] = [],
  ): void {
    this.selectedSerial = serial;
    this.pendingPalletLocation = null;
    this.isLoadingDetail = true;
    this.palletService.getPalletDetail(serial).subscribe({
      next: (detail) => {
        this.isLoadingDetail = false;
        this.detail = detail;
        // Cập nhật luôn số thùng / tổng SL của thẻ pallet đang mở
        this.applySummary(summarizePallet(serial, detail));
        this.rows = (detail?.vendorLabelInfoList ?? []).map((b, i) =>
          this.toRow(b, i),
        );
        if (selectAll) {
          this.rows.forEach((r) => (r.selected = true));
        } else if (selectReels.length) {
          const reels = new Set(selectReels.map((x) => x.toLowerCase()));
          this.rows.forEach(
            (r) => (r.selected = reels.has(r.reelId.toLowerCase())),
          );
        }
        this.loadMaterialNames(this.rows);
        if (!detail) {
          this.notificationService.warning(
            `Không tìm thấy pallet "${serial}".`,
          );
        }
      },
      error: () => {
        this.isLoadingDetail = false;
        this.detail = null;
        this.rows = [];
        this.notificationService.error(
          `Không tải được chi tiết pallet "${serial}".`,
        );
      },
    });
  }

  /**
   * API danh sách không trả số thùng / tổng SL → tải chi tiết các pallet đang có thùng
   * (IN_USE, chưa có số) để điền vào thẻ.
   */
  private loadSummaries(): void {
    const seq = ++this.summarySeq;
    const serials = this.pallets
      .filter(
        (p) =>
          String(p.status ?? "").toUpperCase() === "IN_USE" &&
          (p.numberOfBox === null || p.totalQuantity === null),
      )
      .map((p) => p.serialPallet);
    if (!serials.length) {
      return;
    }
    this.palletService.loadPalletSummaries(serials).subscribe({
      next: (summary) => {
        if (seq === this.summarySeq) {
          this.applySummary(summary);
        }
      },
      complete: () => {
        if (seq === this.summarySeq) {
          this.cdr.markForCheck();
        }
      },
    });
  }

  /** Ghi số thùng / tổng SL vào thẻ pallet tương ứng */
  private applySummary(summary: PalletSummary): void {
    const row = this.pallets.find((p) => p.serialPallet === summary.serial);
    if (!row) {
      return;
    }
    row.numberOfBox = summary.numberOfBox;
    row.totalQuantity = summary.totalQuantity;
    this.cdr.markForCheck();
  }

  /**
   * Nút "Scan mã pallet": có trong danh sách → mở; không có → popup
   * [Quét mã khác] / [Tạo pallet].
   */
  /**
   * Scan mã thùng: thùng thuộc pallet đang mở → tích chọn; thuộc pallet khác → mở pallet đó và
   * chọn thùng. Ô scan giữ chế độ thùng để quét liên tục; sửa SL / vị trí rồi Xác nhận cập nhật.
   */
  private scanBox(code: string): void {
    const reel = code.toLowerCase();
    const row = this.rows.find((r) => r.reelId.toLowerCase() === reel);
    if (row) {
      if (row.pendingRemove) {
        this.notificationService.warning(`Thùng "${code}" đang chờ gỡ.`);
        return;
      }
      if (row.selected) {
        this.notificationService.info(`Thùng "${code}" đã được chọn.`);
        return;
      }
      row.selected = true;
      this.notificationService.success(`Đã chọn thùng "${code}".`);
      return;
    }
    this.lookingUpCount++;
    this.palletService.findPalletSerialOfReel(code).subscribe({
      next: (serial: string | null) => {
        this.lookingUpCount--;
        if (!serial) {
          this.notificationService.error(
            `Thùng "${code}" không thuộc pallet nào.`,
          );
          this.focusScanInput();
          return;
        }
        this.confirmDiscard().subscribe((ok) => {
          if (ok) {
            this.openPallet(serial, false, [code]);
            this.notificationService.success(
              `Thùng "${code}" thuộc pallet ${serial}.`,
            );
          }
          this.focusScanInput();
        });
      },
      error: () => {
        this.lookingUpCount--;
        this.notificationService.error(
          `Không tra được pallet của thùng "${code}".`,
        );
        this.focusScanInput();
      },
    });
  }

  private scanPallet(code: string): void {
    const found = this.findPallet(code);
    this.confirmDiscard().subscribe((ok) => {
      if (!ok) {
        return;
      }
      if (found) {
        // Scan pallet → tự tích chọn tất cả thùng trong pallet
        this.openPallet(found.serialPallet, true);
        this.closeScan();
        return;
      }
      this.askCreatePallet(code, () => this.focusScanInput());
    });
  }

  /**
   * Mã pallet không có trong danh sách → popup [Quét mã khác] / [Tạo pallet].
   * Tạo: kiểm tra trùng rồi POST /pallet-mngts (như Quản lý pallet), xong mở pallet.
   */
  private askCreatePallet(code: string, onScanOther: () => void): void {
    this.confirm(
      "Không có mã pallet này",
      `Pallet "${code}" không có trong danh sách. Quét mã khác hoặc tạo pallet mới với mã này?`,
      "Tạo pallet",
      "Quét mã khác",
    ).subscribe((create) => {
      if (!create) {
        onScanOther();
        return;
      }
      this.isSaving = true;
      this.palletService
        .createPalletIfAbsent(code, this.currentUser)
        .subscribe({
          next: (res) => {
            this.isSaving = false;
            if (res.created) {
              this.notificationService.success(
                `Đã tạo pallet "${res.serial}".`,
              );
            } else {
              this.notificationService.warning(
                `Pallet "${res.serial}" đã có trong danh sách — mở pallet này.`,
              );
            }
            this.loadPallets();
            this.openPallet(res.serial);
            this.closeScan();
          },
          error: () => {
            this.isSaving = false;
            this.notificationService.error(`Tạo pallet "${code}" thất bại.`);
            onScanOther();
          },
        });
    });
  }

  private findPallet(code: string): PalletMngtRow | undefined {
    const lower = code.trim().toLowerCase();
    return this.pallets.find(
      (p) => (p.serialPallet ?? "").toLowerCase() === lower,
    );
  }

  /** Giữ ô scan (chế độ pallet) để quét mã khác */
  private focusScanInput(): void {
    this.scanValue = "";
    setTimeout(() => this.scanInputRef?.nativeElement?.focus(), 30);
  }

  /**
   * Quét thùng để thêm (giống list-material/update): ReelID = phần trước dấu # →
   * GET /api/inventory/{reelId} lấy thông tin thùng → thêm dòng "Chờ thêm".
   * Bấm "Xác nhận cập nhật" mới POST pallet-box-mappings.
   */
  private stageAdd(reelId: string): void {
    const lower = reelId.toLowerCase();
    const exists = this.rows.find((r) => r.reelId.toLowerCase() === lower);
    if (exists) {
      if (exists.pendingRemove) {
        exists.pendingRemove = false;
        this.notificationService.info(`Đã bỏ gỡ thùng "${reelId}".`);
      } else {
        this.notificationService.warning(
          `Thùng "${reelId}" đã có trong pallet.`,
        );
      }
      return;
    }
    if (this.lookingUpReels.has(lower)) {
      return;
    }
    const serial = this.selectedSerial;
    this.lookingUpReels.add(lower);
    this.lookingUpCount++;
    this.materialService.fetchMaterialById(reelId).subscribe({
      next: (inv) => {
        this.finishLookup(lower);
        // Đã chuyển sang pallet khác trong lúc chờ → bỏ kết quả
        if (serial !== this.selectedSerial) {
          return;
        }
        if (!inv) {
          this.notificationService.error(`Không tìm thấy thùng: ${reelId}`);
          return;
        }
        if (this.rows.some((r) => r.reelId.toLowerCase() === lower)) {
          return;
        }
        this.checkOtherPallet(serial, reelId, inv);
      },
      error: () => {
        this.finishLookup(lower);
        this.notificationService.error(`Lỗi khi tìm thùng: ${reelId}`);
      },
    });
  }

  /**
   * Thùng đang ở pallet khác → popup [Quét thùng khác] / [Chuyển sang pallet này].
   * Chuyển: khi "Xác nhận cập nhật" sẽ gỡ thùng khỏi pallet cũ rồi thêm vào pallet này.
   */
  private checkOtherPallet(
    serial: string,
    reelId: string,
    inv: RawGraphQLMaterial,
  ): void {
    const lower = reelId.toLowerCase();
    this.lookingUpReels.add(lower);
    this.lookingUpCount++;
    this.palletService.getBoxMappings().subscribe({
      next: (mappings: PalletBoxMappingRow[]) => {
        this.finishLookup(lower);
        if (serial !== this.selectedSerial) {
          return;
        }
        const other = mappings.find(
          (m) =>
            String(m.reelIdBox ?? "").toLowerCase() === lower &&
            String(m.serialPallet ?? "").toLowerCase() !== serial.toLowerCase(),
        );
        if (!other) {
          this.pushInventoryRow(reelId, inv);
          return;
        }
        const fromSerial = String(other.serialPallet ?? "");
        this.confirm(
          "Thùng đang ở pallet khác",
          `Thùng "${reelId}" đang ở pallet ${fromSerial}. Chuyển thùng này sang pallet ${serial}?`,
          `Chuyển sang ${serial}`,
          "Quét thùng khác",
        ).subscribe((move) => {
          if (move && serial === this.selectedSerial) {
            this.pushInventoryRow(reelId, inv, {
              mappingId: Number(other.id),
              serialPallet: fromSerial,
            });
          }
          setTimeout(() => this.scanInputRef?.nativeElement?.focus(), 30);
        });
      },
      error: () => {
        this.finishLookup(lower);
        this.notificationService.error(
          `Không kiểm tra được pallet của thùng "${reelId}" — chưa thêm.`,
        );
      },
    });
  }

  private pushInventoryRow(
    reelId: string,
    inv: RawGraphQLMaterial,
    moveFrom?: { mappingId: number; serialPallet: string },
  ): void {
    if (
      this.rows.some((r) => r.reelId.toLowerCase() === reelId.toLowerCase())
    ) {
      return;
    }
    const row = this.toInventoryRow(reelId, inv);
    row.moveFrom = moveFrom;
    this.rows = [row, ...this.rows];
    if (moveFrom) {
      this.notificationService.info(
        `Thùng "${reelId}" sẽ được chuyển từ pallet ${moveFrom.serialPallet} — bấm "Xác nhận cập nhật" để lưu.`,
      );
    }
    this.cdr.markForCheck();
  }

  private finishLookup(lowerReelId: string): void {
    this.lookingUpReels.delete(lowerReelId);
    this.lookingUpCount = Math.max(0, this.lookingUpCount - 1);
    this.cdr.markForCheck();
  }

  /** Dòng "Chờ thêm" từ thông tin tồn kho (/api/inventory) */
  private toInventoryRow(reelId: string, inv: RawGraphQLMaterial): BoxRow {
    const raw = inv as RawGraphQLMaterial & {
      manufacturingDate?: string | number | null;
      initialQuantity?: number | null;
    };
    return {
      key: `new-${reelId}`,
      record: null,
      reelId: String(inv.materialIdentifier ?? "") || reelId,
      partNumber: String(inv.partNumber ?? "") || "—",
      itemCode: this.palletService.itemCodeOf({ userData4: inv.userData4 }),
      materialName: "",
      quantity: Number(inv.quantity ?? raw.initialQuantity ?? 0),
      newQuantity: null,
      location: String(inv.locationName ?? ""),
      lot: String(inv.lotNumber ?? "") || "—",
      status: "Chờ thêm",
      updatedBy: String(inv.updatedBy ?? ""),
      mfgDate: this.formatDay(raw.manufacturingDate),
      receivedDate: this.formatDay(inv.receivedDate),
      selected: false,
      pendingRemove: false,
      pendingAdd: true,
      // Vị trí thùng phải giống pallet → khác thì đổi theo vị trí pallet khi xác nhận
      newLocation: this.followPalletLocation(String(inv.locationName ?? "")),
      inventory: inv,
    };
  }

  /** Vị trí pallet (đang đặt / đã lưu); khác vị trí thùng → trả vị trí pallet, giống / chưa có → null */
  private followPalletLocation(boxLocation: string): string | null {
    const pallet = (
      this.pendingPalletLocation ??
      this.detail?.locationName ??
      ""
    ).trim();
    if (!pallet || pallet.toLowerCase() === boxLocation.trim().toLowerCase()) {
      return null;
    }
    return pallet;
  }

  /**
   * Ngày → dd/MM/yyyy. Nhận: timestamp giây (vd "1776038400") hoặc mili-giây,
   * yyyyMMdd (vd "20261008"), chuỗi ISO. Không parse được → "—".
   */
  private formatDay(value: unknown): string {
    const text = String(value ?? "").trim();
    if (!text) {
      return "—";
    }
    let d: Date | null = null;
    if (/^\d{8}$/.test(text) && Number(text.slice(0, 4)) > 1900) {
      d = new Date(
        Number(text.slice(0, 4)),
        Number(text.slice(4, 6)) - 1,
        Number(text.slice(6, 8)),
      );
    } else if (/^\d+$/.test(text)) {
      const n = Number(text);
      // < 1e12 → giây (timestamp Unix), còn lại → mili-giây
      d = new Date(n < 1e12 ? n * 1000 : n);
    } else {
      d = new Date(text);
    }
    if (Number.isNaN(d.getTime())) {
      return text;
    }
    const pad = (n: number): string => String(n).padStart(2, "0");
    return `${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()}`;
  }

  private stageRemoveByReel(reelId: string): void {
    const row = this.rows.find(
      (r) => r.reelId.toLowerCase() === reelId.toLowerCase(),
    );
    if (!row) {
      this.notificationService.warning(
        `Thùng "${reelId}" không thuộc pallet ${this.selectedSerial}.`,
      );
      return;
    }
    if (row.pendingRemove) {
      this.notificationService.info(`Thùng "${reelId}" đã được đánh dấu gỡ.`);
      return;
    }
    this.stageRemove(row);
    this.notificationService.success(`Đánh dấu gỡ thùng "${reelId}".`);
  }

  /**
   * Quét / chọn vị trí: phải có trong danh sách vị trí (IndexedDB).
   * Chọn thùng → áp cho thùng đã chọn; không chọn → cả pallet + mọi thùng trong pallet
   * (pallet chưa có thùng vẫn đổi được vị trí pallet). Bấm "Xác nhận cập nhật" mới gửi.
   */
  private applyLocation(value: string): void {
    const input = normalizeLocationScan(value);
    if (!input) {
      return;
    }
    void this.findLocation(input)
      .then((loc) => {
        if (!loc) {
          this.notificationService.error(
            `Vị trí "${input}" không có trong danh sách vị trí.`,
          );
          return;
        }
        const location = loc.locationFullName || loc.locationName;
        const bySelection = this.selectedRows.length > 0;
        const targets = bySelection ? this.selectedRows : this.activeRows;
        for (const r of targets) {
          r.newLocation = location === r.location ? null : location;
        }
        if (!bySelection) {
          this.pendingPalletLocation =
            location === (this.detail?.locationName ?? "") && !targets.length
              ? null
              : location;
        }
        this.notificationService.info(
          targets.length
            ? `Vị trí "${location}" áp cho ${targets.length} thùng${bySelection ? "" : " và pallet"} — bấm "Xác nhận cập nhật" để lưu.`
            : `Vị trí pallet → "${location}" — bấm "Xác nhận cập nhật" để lưu.`,
        );
        this.closeScan();
        this.cdr.markForCheck();
      })
      .catch(() => {
        this.notificationService.error("Không kiểm tra được danh sách vị trí.");
      });
  }

  /** Vị trí khớp đúng tên / tên đầy đủ trong danh sách vị trí (IndexedDB) */
  private async findLocation(value: string): Promise<{
    locationId: number;
    locationName: string;
    locationFullName: string;
  } | null> {
    const lower = value.toLowerCase();
    await this.warehouseCache.ensureSynced();
    const list = await this.warehouseCache.searchByName(value);
    const found = list.find(
      (w) =>
        String(w.locationFullName ?? "").toLowerCase() === lower ||
        String(w.locationName ?? "").toLowerCase() === lower,
    );
    return found
      ? {
          locationId: Number(found.locationId),
          locationName: String(found.locationName ?? ""),
          locationFullName: String(found.locationFullName ?? ""),
        }
      : null;
  }

  /** Tên vật tư theo mã của các thùng (cùng nguồn / cache với Danh sách vật tư) */
  private loadMaterialNames(rows: BoxRow[]): void {
    const codes = rows.map((r) => r.itemCode).filter(Boolean);
    if (!codes.length) {
      return;
    }
    this.palletService
      .getMaterialNames(codes)
      .subscribe((names: Map<string, string>) => {
        for (const r of rows) {
          r.materialName = names.get(r.itemCode) ?? "";
        }
        this.cdr.markForCheck();
      });
  }

  private toRow(b: PalletBoxRecord, index = 0): BoxRow {
    // Thùng chỉ có liên kết pallet (không có tem NCC): ReelID lấy từ mapping
    const reelId = String(b.reelId ?? b.palletBoxMapping?.reelIdBox ?? "");
    const mappingId = this.mappingIdOf(b);
    return {
      key: b.id
        ? `box-${b.id}`
        : mappingId
          ? `map-${mappingId}`
          : `row-${index}-${reelId}`,
      record: b,
      reelId,
      partNumber: String(b.partNumber ?? "") || "—",
      itemCode: this.palletService.itemCodeOf(b),
      materialName: "",
      quantity: Number(b.initialQuantity ?? 0),
      newQuantity: null,
      // Vị trí kho: storageUnit (dữ liệu cũ nằm ở subStorageUnit)
      location:
        String(b.subStorageUnit ?? "").trim() ||
        String(b.storageUnit ?? "").trim(),
      lot: String(b.lot ?? "") || "—",
      status: String(b.status ?? "") || "Available",
      updatedBy: String(b.updatedBy ?? "") || String(b.createdBy ?? ""),
      mfgDate: this.formatDay(b.manufacturingDate),
      receivedDate: this.formatDay(b.createdAt),
      selected: false,
      pendingRemove: false,
      pendingAdd: false,
      newLocation: null,
    };
  }

  private detectMobile(): boolean {
    return typeof window !== "undefined" && window.innerWidth <= 768;
  }

  private focusPalletSearch(): void {
    setTimeout(() => {
      this.palletSearchRef?.nativeElement?.focus();
      this.palletSearchRef?.nativeElement?.select();
    }, 0);
  }

  private safe(request: Observable<boolean>): Observable<boolean> {
    return request.pipe(catchError(() => of(false)));
  }

  /** Có thay đổi chưa lưu → hỏi bỏ thay đổi */
  private confirmDiscard(): Observable<boolean> {
    if (!this.hasChanges) {
      return of(true);
    }
    return this.confirm(
      "Bỏ thay đổi",
      `Pallet ${this.selectedSerial} có thay đổi chưa lưu (${this.changeSummary}). Bỏ các thay đổi này?`,
      "Bỏ thay đổi",
    );
  }

  private confirm(
    title: string,
    message: string,
    confirmText: string,
    cancelText = "Hủy",
  ): Observable<boolean> {
    return this.dialog
      .open(DialogContentExampleDialogComponent, {
        width: "420px",
        maxWidth: "92vw",
        autoFocus: false,
        data: { title, message, confirmText, cancelText },
      })
      .afterClosed()
      .pipe(map((result) => result === true));
  }

  /** Xuất hàng các thùng; pallet không còn thùng → UNUSED; xong tải lại pallet + danh sách */
  private exportRows(serial: string, rows: BoxRow[]): void {
    const d = this.detail;
    if (!d) {
      return;
    }
    const mappings$ = this.lazyBoxMappings();
    const tasks: Array<Observable<boolean>> = rows.map((r) =>
      this.exportOneBox(serial, r, mappings$),
    );
    if (rows.length >= this.activeRows.length) {
      tasks.push(
        this.safe(
          this.palletService.updatePallet({
            id: d.id,
            serialPallet: d.serialPallet,
            locationName: d.locationName,
            status: "UNUSED",
            note: d.note,
            createAt: d.createAt,
            createBy: d.createBy,
            updatedAt: new Date().toISOString(),
            updatedBy: this.currentUser,
          }),
        ),
      );
    }
    this.isSaving = true;
    forkJoin(tasks).subscribe((results: boolean[]) => {
      this.isSaving = false;
      const boxResults = results.slice(0, rows.length);
      const ok = boxResults.filter(Boolean).length;
      const failed = boxResults.length - ok;
      if (!failed) {
        this.notificationService.success(
          `Đã xuất hàng ${ok} thùng khỏi pallet ${serial}.`,
        );
      } else if (!ok) {
        this.notificationService.error(`Xuất hàng thất bại ${failed} thùng.`);
      } else {
        this.notificationService.warning(
          `Xuất hàng ${ok}/${boxResults.length} thùng, ${failed} thùng lỗi.`,
        );
      }
      this.openPallet(serial);
      this.loadPallets();
    });
  }

  /**
   * Xuất hàng 1 thùng: gỡ khỏi pallet (DELETE /pallet-box-mappings/{mappingId})
   * rồi đặt số lượng = 0 (PUT /vendor-label-infos/{id}). Gỡ lỗi thì không đổi số lượng.
   */
  private exportOneBox(
    serial: string,
    r: BoxRow,
    mappings$: Observable<PalletBoxMappingRow[]>,
  ): Observable<boolean> {
    const box = r.record;
    return this.removeRowFromPallet(serial, r, mappings$).pipe(
      switchMap((removed): Observable<boolean> => {
        if (removed !== true) {
          return of(false);
        }
        // Thùng chỉ có mapping (không có tem NCC) → không có bản ghi để đặt SL = 0
        if (!box?.id) {
          return of(true);
        }
        return this.palletService.updateBox({
          ...box,
          initialQuantity: 0,
          palletBoxMappingId: null,
        });
      }),
      catchError(() => of(false)),
    );
  }

  /** id liên kết pallet–thùng có sẵn trong chi tiết pallet (0 = không có) */
  private mappingIdOf(b: PalletBoxRecord | null | undefined): number {
    return Number(b?.palletBoxMapping?.id ?? b?.palletBoxMappingId ?? 0) || 0;
  }

  /** GET /pallet-box-mappings — chỉ gọi khi cần, dùng chung trong 1 lần cập nhật */
  private lazyBoxMappings(): Observable<PalletBoxMappingRow[]> {
    return this.palletService.getBoxMappings().pipe(
      catchError(() => of([] as PalletBoxMappingRow[])),
      shareReplay(1),
    );
  }

  /**
   * Gỡ 1 thùng khỏi pallet: DELETE /pallet-box-mappings/{id}. id lấy từ chi tiết pallet; không có
   * (API chi tiết trả palletBoxMappingId = null) → tra theo serialPallet + ReelID.
   * true = đã gỡ, false = lỗi API, "notFound" = không tìm thấy liên kết.
   */
  private removeRowFromPallet(
    serial: string,
    r: BoxRow,
    mappings$: Observable<PalletBoxMappingRow[]>,
  ): Observable<boolean | "notFound"> {
    const direct = this.mappingIdOf(r.record);
    const reel = r.reelId.trim().toLowerCase();
    const id$: Observable<number> =
      direct > 0
        ? of(direct)
        : mappings$.pipe(
            map(
              (list) =>
                list.find(
                  (m) =>
                    (m.serialPallet ?? "").trim() === serial.trim() &&
                    !!reel &&
                    (m.reelIdBox ?? "").trim().toLowerCase() === reel,
                )?.id ?? 0,
            ),
          );
    return id$.pipe(
      switchMap(
        (id): Observable<boolean | "notFound"> =>
          id > 0
            ? this.palletService.removeBoxFromPallet(id)
            : of("notFound" as const),
      ),
      catchError(() => of(false)),
    );
  }

  /**
   * Thêm thùng vào pallet (POST /pallet-box-mappings). Thùng chuyển từ pallet khác → gỡ liên kết
   * cũ trước (DELETE), pallet cũ không còn thùng → UNUSED.
   */
  private addRowToPallet(serial: string, r: BoxRow): Observable<boolean> {
    const add$ = (): Observable<boolean> =>
      this.palletService.addBoxToPallet(serial, r.reelId, this.currentUser);
    const move = r.moveFrom;
    if (!move) {
      return this.safe(add$());
    }
    return this.palletService.removeBoxFromPallet(move.mappingId).pipe(
      switchMap((): Observable<boolean> => add$()),
      switchMap((ok: boolean): Observable<boolean> => {
        this.releasePalletIfEmpty(move.serialPallet);
        return of(ok);
      }),
      catchError(() => of(false)),
    );
  }

  /** Pallet cũ (sau khi chuyển thùng đi) không còn thùng → PUT trạng thái UNUSED */
  private releasePalletIfEmpty(serial: string): void {
    this.palletService.getPalletDetail(serial).subscribe({
      next: (d: PalletMngtDetail | null) => {
        if (!d || (d.vendorLabelInfoList ?? []).length) {
          return;
        }
        this.palletService
          .updatePallet({
            id: d.id,
            serialPallet: d.serialPallet,
            locationName: d.locationName,
            status: "UNUSED",
            note: d.note,
            createAt: d.createAt,
            createBy: d.createBy,
            updatedAt: new Date().toISOString(),
            updatedBy: this.currentUser,
          })
          .subscribe({ error: () => undefined });
      },
      error: () => undefined,
    });
  }
}
