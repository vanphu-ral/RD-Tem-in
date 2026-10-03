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
import { forkJoin, Observable, of, take } from "rxjs";
import { catchError, map } from "rxjs/operators";
import { AccountService } from "app/core/auth/account.service";
import { NotificationService } from "../services/notification.service";
import { WarehouseCacheService } from "../services/warehouse-cache.service";
import {
  PalletBoxRecord,
  PalletMaterialService,
  PalletMngtDetail,
  PalletMngtRow,
  PalletSummary,
  sortPalletsWithBoxesFirst,
  summarizePallet,
} from "../services/pallet-material.service";
import { DialogContentExampleDialogComponent } from "../confirm-dialog/confirm-dialog.component";

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
  selected: boolean;
  /** Đã đánh dấu gỡ (chưa gửi) */
  pendingRemove: boolean;
  /** Thùng mới scan thêm (chưa gửi) */
  pendingAdd: boolean;
  /** Vị trí mới (chưa gửi) */
  newLocation: string | null;
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

  private currentUser = "";
  private locationSeq = 0;
  /** Đổi mỗi lần tải lại danh sách — bỏ kết quả tính số lượng của lần tải cũ */
  private summarySeq = 0;

  constructor(
    private palletService: PalletMaterialService,
    private warehouseCache: WarehouseCacheService,
    private notificationService: NotificationService,
    private accountService: AccountService,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef,
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
    return this.rows.some(
      (r) => r.pendingRemove || r.pendingAdd || r.newLocation !== null,
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
   * → mở pallet đó; không có trong danh sách → tra theo mã. Xong thì xóa ô, focus lại.
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
      if (ok) {
        this.closeScan();
        this.openPallet(target ? target.serialPallet : code);
        this.palletSearch = "";
      }
      this.focusPalletSearch();
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

  onExport(): void {
    this.notificationService.info("Xuất hàng — chưa có API, sẽ nối sau.");
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
          tasks.push(
            this.safe(
              this.palletService.addBoxToPallet(
                serial,
                r.reelId,
                this.currentUser,
              ),
            ),
          );
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
        tasks.push(
          this.safe(
            this.palletService.updateBox({
              ...r.record,
              subStorageUnit: r.newLocation,
            }),
          ),
        );
      }
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
      next: (rows) => {
        // Pallet đang có thùng (IN_USE) lên đầu
        this.pallets = sortPalletsWithBoxesFirst(rows, (p) => p);
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
   * (IN_USE, chưa có số) để điền vào thẻ, xong thì sắp xếp lại.
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
          this.pallets = sortPalletsWithBoxesFirst(this.pallets, (p) => p);
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

  private scanPallet(code: string): void {
    const found = this.pallets.find(
      (p) => (p.serialPallet ?? "").toLowerCase() === code.toLowerCase(),
    );
    this.confirmDiscard().subscribe((ok) => {
      if (!ok) {
        return;
      }
      this.openPallet(found ? found.serialPallet : code);
      this.closeScan();
    });
  }

  private stageAdd(reelId: string): void {
    const exists = this.rows.find(
      (r) => r.reelId.toLowerCase() === reelId.toLowerCase(),
    );
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
    this.rows = [
      {
        key: `new-${reelId}`,
        record: null,
        reelId,
        partNumber: "—",
        quantity: 0,
        location: "",
        lot: "—",
        status: "Chờ thêm",
        selected: false,
        pendingRemove: false,
        pendingAdd: true,
        newLocation: null,
      },
      ...this.rows,
    ];
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

  /** Vị trí áp cho thùng đang chọn; không chọn thùng nào → tất cả thùng trong pallet */
  private applyLocation(value: string): void {
    const location = value.trim();
    if (!location) {
      return;
    }
    const targets = this.selectedRows.length
      ? this.selectedRows
      : this.activeRows;
    if (!targets.length) {
      this.notificationService.warning("Pallet chưa có thùng nào.");
      return;
    }
    for (const r of targets) {
      r.newLocation = location === r.location ? null : location;
    }
    this.notificationService.info(
      `Vị trí "${location}" áp cho ${targets.length} thùng — bấm "Xác nhận cập nhật" để lưu.`,
    );
    this.closeScan();
  }

  private toRow(b: PalletBoxRecord): BoxRow {
    const reelId = String(b.reelId ?? "");
    return {
      key: `box-${b.id}`,
      record: b,
      reelId,
      partNumber: String(b.partNumber ?? "") || "—",
      quantity: Number(b.initialQuantity ?? 0),
      location: String(b.subStorageUnit ?? ""),
      lot: String(b.lot ?? "") || "—",
      status: String(b.status ?? "") || "Available",
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
  ): Observable<boolean> {
    return this.dialog
      .open(DialogContentExampleDialogComponent, {
        width: "420px",
        maxWidth: "92vw",
        autoFocus: false,
        data: { title, message, confirmText, cancelText: "Hủy" },
      })
      .afterClosed()
      .pipe(map((result) => result === true));
  }
}
