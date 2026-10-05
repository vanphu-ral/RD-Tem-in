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
import { catchError, map, switchMap } from "rxjs/operators";
import { AccountService } from "app/core/auth/account.service";
import { NotificationService } from "../services/notification.service";
import { WarehouseCacheService } from "../services/warehouse-cache.service";
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
  inventory_update_requests_detail,
  ListMaterialService,
  RawGraphQLMaterial,
} from "../services/list-material.service";

/** Chế độ ô scan: chọn pallet / thêm thùng / gỡ thùng / cập nhật vị trí */
type ScanMode = "pallet" | "add" | "remove" | "location";

interface BoxRow {
  key: string;
  record: PalletBoxRecord | null;
  reelId: string;
  partNumber: string;
  quantity: number;
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
        (r) => r.pendingRemove || r.pendingAdd || r.newLocation !== null,
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
        this.openPallet(p.serialPallet);
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
        this.openPallet(target.serialPallet);
        this.focusPalletSearch();
        return;
      }
      // Không có trong danh sách → hỏi quét mã khác / tạo pallet
      this.askCreatePallet(code, () => this.focusPalletSearch());
    });
  }

  // ==================== Scan bar ====================

  openScan(mode: ScanMode): void {
    if (mode !== "pallet" && !this.detail) {
      this.notificationService.warning("Chọn hoặc quét một pallet trước.");
      return;
    }
    this.scanMode = this.scanMode === mode ? null : mode;
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
    const term = value.trim();
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

    for (const r of this.rows) {
      if (r.pendingAdd) {
        if (!r.pendingRemove) {
          tasks.push(this.addRowToPallet(serial, r));
        }
        continue;
      }
      if (r.pendingRemove) {
        const mappingId = Number(
          r.record?.palletBoxMapping?.id ?? r.record?.palletBoxMappingId ?? 0,
        );
        tasks.push(
          mappingId > 0
            ? this.safe(this.palletService.removeBoxFromPallet(mappingId))
            : of(false),
        );
        continue;
      }
      if (r.newLocation !== null && r.record) {
        // storageUnit = vị trí kho, subStorageUnit trống (cùng quy ước Nhập tem NCC)
        tasks.push(
          this.safe(
            this.palletService.updateBox({
              ...r.record,
              storageUnit: r.newLocation,
              subStorageUnit: null,
            }),
          ),
        );
      }
    }

    // Thùng đổi vị trí (kể cả thùng vừa quét thêm) → cập nhật thêm vị trí tồn kho (DB inventory)
    const moved = this.rows.filter(
      (r) => r.newLocation !== null && !r.pendingRemove,
    );
    if (moved.length) {
      tasks.push(this.updateInventoryLocations(moved));
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

  private openPallet(serial: string): void {
    this.selectedSerial = serial;
    this.pendingPalletLocation = null;
    this.isLoadingDetail = true;
    this.palletService.getPalletDetail(serial).subscribe({
      next: (detail) => {
        this.isLoadingDetail = false;
        this.detail = detail;
        // Cập nhật luôn số thùng / tổng SL của thẻ pallet đang mở
        this.applySummary(summarizePallet(serial, detail));
        this.rows = (detail?.vendorLabelInfoList ?? []).map((b) =>
          this.toRow(b),
        );
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
  private scanPallet(code: string): void {
    const found = this.findPallet(code);
    this.confirmDiscard().subscribe((ok) => {
      if (!ok) {
        return;
      }
      if (found) {
        this.openPallet(found.serialPallet);
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
      quantity: Number(inv.quantity ?? raw.initialQuantity ?? 0),
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
    const input = value.trim();
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

  /**
   * Cập nhật vị trí tồn kho (DB inventory — khác DB vendor-label-info) cho các thùng đổi vị trí:
   * như dialog cập nhật vật tư (list-material/dialog) — 1 request MOVE tự phê duyệt
   * (POST api/request, status APPROVE). Thùng chưa có thông tin tồn kho → GET /api/inventory/{reelId}.
   */
  private updateInventoryLocations(rows: BoxRow[]): Observable<boolean> {
    const locations = [...new Set(rows.map((r) => r.newLocation ?? ""))];
    const inventories$ = forkJoin(
      rows.map((r) =>
        r.inventory
          ? of(r.inventory)
          : this.materialService
              .fetchMaterialById(r.reelId)
              .pipe(catchError(() => of(undefined))),
      ),
    );
    const locationIds$ = from(
      Promise.all(locations.map((name) => this.findLocation(name))),
    );
    return forkJoin([inventories$, locationIds$]).pipe(
      switchMap(([inventories, locs]) => {
        const idByName = new Map<string, string>();
        locations.forEach((name, i) => {
          const loc = locs[i];
          if (loc) {
            idByName.set(name, String(loc.locationId));
          }
        });
        const detail: inventory_update_requests_detail[] = [];
        rows.forEach((r, i) => {
          const inv = inventories[i];
          const name = r.newLocation ?? "";
          if (!inv || !idByName.has(name)) {
            return;
          }
          const qty = String(inv.quantity ?? r.quantity ?? "");
          detail.push({
            id: inv.inventoryId != null ? Number(inv.inventoryId) : null,
            materialId: String(inv.materialIdentifier ?? r.reelId),
            updatedBy: this.currentUser,
            createdTime: "",
            updatedTime: "",
            productCode: String(inv.partNumber ?? ""),
            productName: String(inv.partNumber ?? ""),
            quantity: qty,
            quantityChange: qty,
            type: "MOVE",
            locationId: idByName.get(name) ?? "",
            locationName: name,
            expiredTime: String(inv.expirationDate ?? ""),
            status: "APPROVE",
            requestId: null,
          });
        });
        if (!detail.length) {
          return of(false);
        }
        return this.materialService
          .postAutoApprovedUpdate(detail, this.currentUser)
          .pipe(map((): boolean => detail.length === rows.length));
      }),
      catchError(() => of(false)),
    );
  }

  private toRow(b: PalletBoxRecord): BoxRow {
    const reelId = String(b.reelId ?? "");
    return {
      key: `box-${b.id}`,
      record: b,
      reelId,
      partNumber: String(b.partNumber ?? "") || "—",
      quantity: Number(b.initialQuantity ?? 0),
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
    const tasks: Array<Observable<boolean>> = rows.map((r) =>
      r.record ? this.exportOneBox(r.record) : of(false),
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
  private exportOneBox(box: PalletBoxRecord): Observable<boolean> {
    const mappingId = Number(
      box.palletBoxMapping?.id ?? box.palletBoxMappingId ?? 0,
    );
    const remove$: Observable<boolean> =
      mappingId > 0
        ? this.palletService.removeBoxFromPallet(mappingId)
        : of(true);
    return remove$.pipe(
      switchMap(
        (): Observable<boolean> =>
          this.palletService.updateBox({
            ...box,
            initialQuantity: 0,
            palletBoxMappingId: null,
          }),
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
