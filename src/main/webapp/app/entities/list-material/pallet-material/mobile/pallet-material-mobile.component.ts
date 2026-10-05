import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  OnInit,
  ViewChild,
} from "@angular/core";
import { Router } from "@angular/router";
import { forkJoin, Observable, of, take } from "rxjs";
import { catchError, switchMap } from "rxjs/operators";
import { AccountService } from "app/core/auth/account.service";
import { NotificationService } from "../../services/notification.service";
import { WarehouseCacheService } from "../../services/warehouse-cache.service";
import {
  PalletBoxRecord,
  PalletMaterialService,
  PalletMngtDetail,
  PalletMngtRow,
  sortPalletsNewestFirst,
} from "../../services/pallet-material.service";

type MobileScreen = "list" | "detail" | "location" | "boxScan";
/** Màn scan thùng: thêm vào pallet / gỡ khỏi pallet / xuất hàng */
type BoxScanMode = "add" | "remove" | "export";

interface PalletCard {
  serial: string;
  row: PalletMngtRow | null;
  detail: PalletMngtDetail | null;
  checked: boolean;
}

interface ScannedBox {
  reelId: string;
  /** null khi thùng chưa thuộc pallet (chế độ thêm) */
  record: PalletBoxRecord | null;
}

interface ConfirmState {
  title: string;
  message: string;
  confirmText: string;
  /** Nhãn nút hủy (mặc định "Hủy") */
  cancelText?: string;
  danger: boolean;
  onConfirm: () => void;
  /** Bấm hủy / chạm ra ngoài */
  onCancel?: () => void;
}

@Component({
  selector: "jhi-pallet-material-mobile",
  templateUrl: "./pallet-material-mobile.component.html",
  styleUrls: ["./pallet-material-mobile.component.scss"],
  standalone: false,
})
export class PalletMaterialMobileComponent implements OnInit, AfterViewInit {
  /** Hiện chức năng Xuất hàng (gỡ thùng khỏi pallet + số lượng thùng = 0) */
  readonly showExport = true;

  @ViewChild("mainInputRef") mainInputRef?: ElementRef<HTMLInputElement>;

  screen: MobileScreen = "list";
  cards: PalletCard[] = [];
  palletInput = "";
  isLoading = false;
  isSaving = false;

  /** Pallet đang xem chi tiết / đang scan thùng */
  current: PalletCard | null = null;
  boxChecked = new Set<number>();

  boxScanMode: BoxScanMode = "remove";
  boxInput = "";
  scannedBoxes: ScannedBox[] = [];

  /** Chuyển vị trí: các pallet áp dụng + vị trí mới */
  locationTargets: PalletCard[] = [];
  newLocation = "";
  locationOptions: Array<{ locationName: string; locationFullName: string }> =
    [];

  confirmState: ConfirmState | null = null;

  private currentUser = "";
  private locationSeq = 0;
  /** Màn scan thùng mở từ Chi tiết pallet → Back quay về Chi tiết */
  private detailOrigin = false;

  constructor(
    private palletService: PalletMaterialService,
    private warehouseCache: WarehouseCacheService,
    private notificationService: NotificationService,
    private accountService: AccountService,
    private router: Router,
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
  }

  ngAfterViewInit(): void {
    this.focusMain();
  }

  // ==================== Getters ====================

  get headerTitle(): string {
    switch (this.screen) {
      case "detail":
        return "Chi tiết pallet";
      case "location":
        return "Xác nhận vị trí mới";
      case "boxScan":
        return this.boxScanTitle;
      default:
        return "Scan pallet";
    }
  }

  get boxScanTitle(): string {
    if (this.boxScanMode === "add") {
      return "Scan thêm thùng";
    }
    return this.boxScanMode === "export" ? "Scan xuất hàng" : "Scan gỡ thùng";
  }

  get boxScanHeading(): string {
    if (this.boxScanMode === "add") {
      return "Thêm thùng vào pallet";
    }
    return this.boxScanMode === "export" ? "Xuất hàng" : "Gỡ thùng khỏi pallet";
  }

  get boxScanConfirmText(): string {
    if (this.boxScanMode === "add") {
      return "Xác nhận thêm";
    }
    return this.boxScanMode === "export" ? "Xác nhận xuất hàng" : "Xác nhận gỡ";
  }

  get checkedCards(): PalletCard[] {
    return this.cards.filter((c) => c.checked);
  }

  get currentBoxes(): PalletBoxRecord[] {
    return this.current?.detail?.vendorLabelInfoList ?? [];
  }

  get checkedBoxes(): PalletBoxRecord[] {
    return this.currentBoxes.filter((b) => this.boxChecked.has(b.id));
  }

  // ==================== Card helpers (dùng trong template) ====================

  boxCount(c: PalletCard): number {
    return c.detail
      ? (c.detail.vendorLabelInfoList ?? []).length
      : Number(c.row?.numberOfBox ?? 0);
  }

  totalQty(c: PalletCard): number {
    return c.detail
      ? (c.detail.vendorLabelInfoList ?? []).reduce(
          (s, b) => s + Number(b.initialQuantity ?? 0),
          0,
        )
      : Number(c.row?.totalQuantity ?? 0);
  }

  /** Số mã vật tư (mã SAP / part khác nhau) — chỉ biết khi đã có chi tiết */
  materialCount(c: PalletCard): number | null {
    if (!c.detail) {
      return null;
    }
    return new Set(
      (c.detail.vendorLabelInfoList ?? []).map(
        (b) => String(b.sapCode ?? "") || String(b.partNumber ?? ""),
      ),
    ).size;
  }

  location(c: PalletCard | null): string {
    if (!c) {
      return "—";
    }
    const fromBoxes = new Set(
      (c.detail?.vendorLabelInfoList ?? [])
        .map((b) => String(b.subStorageUnit ?? ""))
        .filter(Boolean),
    );
    if (fromBoxes.size === 1) {
      return [...fromBoxes][0];
    }
    return c.detail?.locationName ?? c.row?.locationName ?? "—";
  }

  boxStatus(b: PalletBoxRecord): string {
    return String(b.status ?? "") || "Available";
  }

  // ==================== Màn danh sách ====================

  onBack(): void {
    if (this.screen === "list") {
      void this.router.navigate(["/list-material/update-list"]);
      return;
    }
    if (this.screen === "boxScan" && this.current && this.detailOrigin) {
      this.screen = "detail";
      return;
    }
    this.goList();
  }

  /** Áp dụng mã pallet (scan / nhập + Enter) → thêm vào danh sách */
  onApplyPallet(): void {
    const code = this.palletInput.trim();
    if (!code || this.isLoading) {
      return;
    }
    const existing = this.cards.find(
      (c) => c.serial.toLowerCase() === code.toLowerCase(),
    );
    this.isLoading = true;
    this.palletService
      .getPalletDetail(existing ? existing.serial : code)
      .subscribe({
        next: (detail) => {
          this.isLoading = false;
          this.palletInput = "";
          if (!detail) {
            // Không có pallet này → hỏi quét mã khác / tạo pallet
            this.askCreatePallet(code);
            return;
          }
          const card: PalletCard = existing ?? {
            serial: detail.serialPallet,
            row: null,
            detail: null,
            checked: false,
          };
          card.detail = detail;
          card.checked = true;
          this.cards = [card, ...this.cards.filter((c) => c !== card)];
          this.focusMain();
        },
        error: () => {
          this.isLoading = false;
          this.notificationService.error(`Không tải được pallet "${code}".`);
          this.focusMain();
        },
      });
  }

  /** Xem tất cả pallet trong danh sách quản lý */
  onViewAll(): void {
    this.isLoading = true;
    this.palletService.getPallets().subscribe({
      next: (rows) => {
        this.isLoading = false;
        for (const row of rows) {
          const found = this.cards.find((c) => c.serial === row.serialPallet);
          if (found) {
            found.row = row;
          } else {
            this.cards.push({
              serial: row.serialPallet,
              row,
              detail: null,
              checked: false,
            });
          }
        }
        this.sortCards();
        this.loadCardSummaries();
      },
      error: () => {
        this.isLoading = false;
        this.notificationService.error("Không tải được danh sách pallet.");
      },
    });
  }

  toggleCard(c: PalletCard): void {
    c.checked = !c.checked;
  }

  openDetail(c: PalletCard): void {
    this.withDetail([c], () => {
      this.current = c;
      this.boxChecked = new Set<number>();
      this.detailOrigin = true;
      this.screen = "detail";
    });
  }

  // ==================== Hành động theo pallet đã chọn ====================

  /** Thêm thùng / Scan gỡ thùng / Scan xuất hàng — cho 1 pallet */
  openBoxScan(mode: BoxScanMode, card?: PalletCard): void {
    const target = card ?? this.checkedCards[0];
    if (!target) {
      return;
    }
    this.withDetail([target], () => {
      this.detailOrigin = this.screen === "detail";
      this.current = target;
      this.boxScanMode = mode;
      this.boxInput = "";
      this.scannedBoxes = [];
      this.screen = "boxScan";
      this.focusMain();
    });
  }

  openLocation(): void {
    const targets = this.checkedCards;
    if (!targets.length) {
      return;
    }
    this.withDetail(targets, () => {
      this.locationTargets = targets;
      this.newLocation = "";
      this.locationOptions = [];
      this.screen = "location";
      this.focusMain();
    });
  }

  /** Gỡ tất cả thùng của các pallet đã chọn */
  onRemoveAllBoxes(): void {
    const targets = this.checkedCards;
    if (!targets.length) {
      return;
    }
    this.withDetail(targets, () => {
      const boxes = targets.flatMap((c) => c.detail?.vendorLabelInfoList ?? []);
      if (!boxes.length) {
        this.notificationService.warning(
          "Các pallet đã chọn không có thùng nào.",
        );
        return;
      }
      this.askConfirm({
        title: "Gỡ tất cả thùng",
        message: `Gỡ ${boxes.length} thùng khỏi ${targets.length} pallet đã chọn?`,
        confirmText: "Gỡ tất cả",
        danger: true,
        onConfirm: () => this.removeBoxes(targets, boxes),
      });
    });
  }

  /** Xuất hàng mọi thùng của các pallet đã chọn */
  onExportPallets(): void {
    const targets = this.checkedCards;
    if (!targets.length) {
      return;
    }
    this.withDetail(targets, () => {
      const boxes = targets.flatMap((c) => c.detail?.vendorLabelInfoList ?? []);
      if (!boxes.length) {
        this.notificationService.warning(
          "Các pallet đã chọn không có thùng nào.",
        );
        return;
      }
      this.askConfirm({
        title: "Xác nhận xuất hàng",
        message: `Xuất hàng ${boxes.length} thùng của ${targets.length} pallet đã chọn? Thùng sẽ được gỡ khỏi pallet và số lượng về 0.`,
        confirmText: "Xuất hàng",
        danger: false,
        onConfirm: () => this.exportBoxes(targets, boxes),
      });
    });
  }

  // ==================== Màn chi tiết ====================

  toggleBox(b: PalletBoxRecord): void {
    if (this.boxChecked.has(b.id)) {
      this.boxChecked.delete(b.id);
    } else {
      this.boxChecked.add(b.id);
    }
    this.boxChecked = new Set(this.boxChecked);
  }

  onDetailRemove(): void {
    const boxes = this.checkedBoxes;
    const card = this.current;
    if (!card || !boxes.length) {
      this.notificationService.warning("Chọn thùng cần gỡ.");
      return;
    }
    this.askConfirm({
      title: "Gỡ thùng",
      message: `Gỡ ${boxes.length} thùng đã chọn khỏi pallet ${card.serial}?`,
      confirmText: "Gỡ thùng",
      danger: true,
      onConfirm: () => this.removeBoxes([card], boxes),
    });
  }

  onDetailExport(): void {
    const boxes = this.checkedBoxes;
    if (!boxes.length) {
      this.notificationService.warning("Chọn thùng cần xuất hàng.");
      return;
    }
    const card = this.current;
    if (!card) {
      return;
    }
    this.askConfirm({
      title: "Xác nhận xuất hàng",
      message: `Xuất hàng ${boxes.length} thùng đã chọn? Thùng sẽ được gỡ khỏi pallet và số lượng về 0.`,
      confirmText: "Xuất hàng",
      danger: false,
      onConfirm: () => this.exportBoxes([card], boxes),
    });
  }

  // ==================== Màn scan thùng ====================

  onScanBox(): void {
    const code = this.boxInput.trim().split("#")[0].trim();
    this.boxInput = "";
    this.focusMain();
    if (!code || !this.current) {
      return;
    }
    if (
      this.scannedBoxes.some(
        (s) => s.reelId.toLowerCase() === code.toLowerCase(),
      )
    ) {
      this.notificationService.info(
        `Thùng "${code}" đã có trong danh sách scan.`,
      );
      return;
    }
    const inPallet = this.currentBoxes.find(
      (b) => String(b.reelId ?? "").toLowerCase() === code.toLowerCase(),
    );
    if (this.boxScanMode === "add") {
      if (inPallet) {
        this.notificationService.warning(
          `Thùng "${code}" đã thuộc pallet này.`,
        );
        return;
      }
      this.scannedBoxes = [
        { reelId: code, record: null },
        ...this.scannedBoxes,
      ];
      return;
    }
    if (!inPallet) {
      this.notificationService.warning(
        `Thùng "${code}" không thuộc pallet ${this.current.serial}.`,
      );
      return;
    }
    this.scannedBoxes = [
      { reelId: code, record: inPallet },
      ...this.scannedBoxes,
    ];
  }

  removeScanned(s: ScannedBox): void {
    this.scannedBoxes = this.scannedBoxes.filter((x) => x !== s);
  }

  onCancelBoxScan(): void {
    this.scannedBoxes = [];
    this.onBack();
  }

  onConfirmBoxScan(): void {
    const card = this.current;
    if (!card || !this.scannedBoxes.length) {
      this.notificationService.warning("Chưa scan thùng nào.");
      return;
    }
    if (this.boxScanMode === "export") {
      const records = this.scannedBoxes
        .map((s) => s.record)
        .filter((r): r is PalletBoxRecord => r !== null);
      this.askConfirm({
        title: "Xác nhận xuất hàng",
        message: `Xuất hàng ${records.length} thùng đã scan? Thùng sẽ được gỡ khỏi pallet và số lượng về 0.`,
        confirmText: "Xuất hàng",
        danger: false,
        onConfirm: () =>
          this.exportBoxes([card], records, () => (this.scannedBoxes = [])),
      });
      return;
    }
    if (this.boxScanMode === "remove") {
      const records = this.scannedBoxes
        .map((s) => s.record)
        .filter((r): r is PalletBoxRecord => r !== null);
      this.removeBoxes([card], records, () => (this.scannedBoxes = []));
      return;
    }
    this.addBoxes(
      card,
      this.scannedBoxes.map((s) => s.reelId),
    );
  }

  // ==================== Màn chuyển vị trí ====================

  onLocationInput(value: string): void {
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

  /** Quét vị trí (máy scan gửi Enter) → kiểm tra có trong danh sách vị trí ngay */
  onLocationEnter(): void {
    const input = this.newLocation.trim();
    if (!input) {
      return;
    }
    void this.findLocation(input)
      .then((found) => {
        if (!found) {
          this.notificationService.error(
            `Vị trí "${input}" không có trong danh sách vị trí.`,
          );
          this.newLocation = "";
          this.locationOptions = [];
          this.focusMain();
          return;
        }
        this.newLocation = found;
        this.locationOptions = [];
        this.cdr.markForCheck();
      })
      .catch(() => {
        this.notificationService.error("Không kiểm tra được danh sách vị trí.");
      });
  }

  onRescanLocation(): void {
    this.newLocation = "";
    this.locationOptions = [];
    this.focusMain();
  }

  /**
   * Vị trí phải có trong danh sách vị trí (IndexedDB) → PUT từng thùng (storageUnit = vị trí,
   * subStorageUnit trống) + PUT pallet (locationName).
   */
  onConfirmLocation(): void {
    const input = this.newLocation.trim();
    if (!input) {
      this.notificationService.warning("Quét hoặc chọn vị trí mới.");
      this.focusMain();
      return;
    }
    void this.findLocation(input)
      .then((found) => {
        if (!found) {
          this.notificationService.error(
            `Vị trí "${input}" không có trong danh sách vị trí.`,
          );
          this.newLocation = "";
          this.focusMain();
          return;
        }
        this.newLocation = found;
        this.saveLocation(found);
      })
      .catch(() => {
        this.notificationService.error("Không kiểm tra được danh sách vị trí.");
      });
  }

  // ==================== Confirm modal ====================

  onConfirmModal(): void {
    const state = this.confirmState;
    this.confirmState = null;
    state?.onConfirm();
  }

  closeConfirm(): void {
    const state = this.confirmState;
    this.confirmState = null;
    state?.onCancel?.();
  }

  trackCard(_: number, c: PalletCard): string {
    return c.serial;
  }

  // ==================== Private ====================
  private goList(): void {
    this.screen = "list";
    this.current = null;
    this.scannedBoxes = [];
    this.focusMain();
  }

  private askConfirm(state: ConfirmState): void {
    this.confirmState = state;
  }

  /** Xuất hàng: gỡ thùng khỏi pallet + số lượng = 0; pallet không còn thùng → UNUSED */
  private exportBoxes(
    cards: PalletCard[],
    boxes: PalletBoxRecord[],
    after?: () => void,
  ): void {
    const tasks: Array<Observable<boolean>> = boxes.map(
      (b: PalletBoxRecord): Observable<boolean> => this.exportOneBox(b),
    );
    const exportIds = new Set(boxes.map((b) => b.id));
    for (const c of cards) {
      const left = (c.detail?.vendorLabelInfoList ?? []).filter(
        (b) => !exportIds.has(b.id),
      );
      if (c.detail && !left.length) {
        tasks.push(this.safe(this.updatePallet(c.detail, "UNUSED")));
      }
    }
    this.runTasks(tasks, "Xuất hàng thành công!", cards, () => {
      this.boxChecked = new Set<number>();
      after?.();
    });
  }

  /** Đảm bảo các pallet đã có chi tiết (danh sách thùng) rồi chạy tiếp */
  private withDetail(cards: PalletCard[], then: () => void): void {
    const missing = cards.filter((c) => !c.detail);
    if (!missing.length) {
      then();
      return;
    }
    this.isLoading = true;
    forkJoin(
      missing.map((c) =>
        this.palletService
          .getPalletDetail(c.serial)
          .pipe(catchError(() => of(null))),
      ),
    ).subscribe((details) => {
      this.isLoading = false;
      details.forEach((d, i) => (missing[i].detail = d));
      if (missing.some((c) => !c.detail)) {
        this.notificationService.error("Không tải được chi tiết pallet.");
        return;
      }
      then();
    });
  }

  /** DELETE mapping từng thùng; pallet không còn thùng → UNUSED */
  private removeBoxes(
    cards: PalletCard[],
    boxes: PalletBoxRecord[],
    after?: () => void,
  ): void {
    const tasks: Array<Observable<boolean>> = [];
    for (const b of boxes) {
      const id = Number(b.palletBoxMapping?.id ?? b.palletBoxMappingId ?? 0);
      tasks.push(
        id > 0
          ? this.safe(this.palletService.removeBoxFromPallet(id))
          : of(false),
      );
    }
    const removeIds = new Set(boxes.map((b) => b.id));
    for (const c of cards) {
      const left = (c.detail?.vendorLabelInfoList ?? []).filter(
        (b) => !removeIds.has(b.id),
      );
      if (c.detail && !left.length) {
        tasks.push(this.safe(this.updatePallet(c.detail, "UNUSED")));
      }
    }
    this.runTasks(tasks, "Cập nhật thành công!", cards, () => {
      this.boxChecked = new Set<number>();
      after?.();
    });
  }

  /** POST mapping thùng vào pallet; pallet → IN_USE */
  private addBoxes(card: PalletCard, reelIds: string[]): void {
    const tasks: Array<Observable<boolean>> = reelIds.map((r) =>
      this.safe(
        this.palletService.addBoxToPallet(card.serial, r, this.currentUser),
      ),
    );
    if (card.detail) {
      tasks.push(this.safe(this.updatePallet(card.detail, "IN_USE")));
    }
    this.runTasks(tasks, "Cập nhật thành công!", [card], () => {
      this.scannedBoxes = [];
    });
  }

  private updatePallet(
    d: PalletMngtDetail,
    status?: string,
    locationName?: string,
  ): Observable<boolean> {
    const hasBoxes = (d.vendorLabelInfoList ?? []).length > 0;
    return this.palletService.updatePallet({
      id: d.id,
      serialPallet: d.serialPallet,
      locationName: locationName ?? d.locationName,
      status: status ?? (hasBoxes ? "IN_USE" : "UNUSED"),
      note: d.note,
      createAt: d.createAt,
      createBy: d.createBy,
      updatedAt: new Date().toISOString(),
      updatedBy: this.currentUser,
    });
  }

  /** Chạy song song, báo kết quả, tải lại chi tiết các pallet liên quan */
  private runTasks(
    tasks: Array<Observable<boolean>>,
    successText: string,
    cards: PalletCard[],
    after: () => void,
  ): void {
    if (!tasks.length || this.isSaving) {
      return;
    }
    this.isSaving = true;
    forkJoin(tasks).subscribe((results: boolean[]) => {
      this.isSaving = false;
      const ok = results.filter(Boolean).length;
      const failed = results.length - ok;
      if (!failed) {
        this.notificationService.success(successText);
      } else if (!ok) {
        this.notificationService.error("Cập nhật thất bại.");
      } else {
        this.notificationService.warning(
          `Cập nhật ${ok}/${results.length}, ${failed} lỗi.`,
        );
      }
      this.refreshCards(cards);
      after();
    });
  }

  private refreshCards(cards: PalletCard[]): void {
    for (const c of cards) {
      this.palletService
        .getPalletDetail(c.serial)
        .pipe(catchError(() => of(null)))
        .subscribe((d) => {
          if (d) {
            c.detail = d;
            this.cards = [...this.cards];
          }
        });
    }
  }

  private safe(request: Observable<boolean>): Observable<boolean> {
    return request.pipe(catchError(() => of(false)));
  }

  private focusMain(): void {
    setTimeout(() => {
      this.mainInputRef?.nativeElement?.focus();
    }, 50);
  }

  /** Pallet tạo mới nhất lên đầu */
  private sortCards(): void {
    this.cards = sortPalletsNewestFirst<PalletCard>(
      this.cards,
      (c: PalletCard): PalletMngtRow | null => c.row ?? c.detail,
    );
  }

  /**
   * API danh sách không trả số thùng / tổng SL → tải chi tiết các pallet IN_USE chưa có số
   * để điền vào thẻ.
   */
  private loadCardSummaries(): void {
    const serials = this.cards
      .filter(
        (c) =>
          !c.detail &&
          !!c.row &&
          String(c.row.status ?? "").toUpperCase() === "IN_USE" &&
          (c.row.numberOfBox === null || c.row.totalQuantity === null),
      )
      .map((c) => c.serial);
    if (!serials.length) {
      return;
    }
    this.palletService.loadPalletSummaries(serials).subscribe({
      next: (summary) => {
        const card = this.cards.find((c) => c.serial === summary.serial);
        if (card?.row) {
          card.row = {
            ...card.row,
            numberOfBox: summary.numberOfBox,
            totalQuantity: summary.totalQuantity,
          };
        }
      },
    });
  }

  /**
   * Mã pallet không có → popup [Quét mã khác] / [Tạo pallet]. Tạo: kiểm tra trùng rồi
   * POST /pallet-mngts (như Quản lý pallet), xong thêm pallet vào danh sách và chọn nó.
   */
  private askCreatePallet(code: string): void {
    this.askConfirm({
      title: "Không có mã pallet này",
      message: `Pallet "${code}" không có trong danh sách. Quét mã khác hoặc tạo pallet mới với mã này?`,
      confirmText: "Tạo pallet",
      cancelText: "Quét mã khác",
      danger: false,
      onCancel: () => this.focusMain(),
      onConfirm: () => {
        this.isLoading = true;
        this.palletService
          .createPalletIfAbsent(code, this.currentUser)
          .subscribe({
            next: (res) => {
              this.isLoading = false;
              if (res.created) {
                this.notificationService.success(
                  `Đã tạo pallet "${res.serial}".`,
                );
              } else {
                this.notificationService.warning(
                  `Pallet "${res.serial}" đã có trong danh sách.`,
                );
              }
              // Nạp pallet vừa tạo / đang có như khi quét mã hợp lệ
              this.palletInput = res.serial;
              this.onApplyPallet();
            },
            error: () => {
              this.isLoading = false;
              this.notificationService.error(`Tạo pallet "${code}" thất bại.`);
              this.focusMain();
            },
          });
      },
    });
  }

  /** Vị trí khớp đúng tên / tên đầy đủ trong danh sách vị trí (IndexedDB) → tên đầy đủ; không có → null */
  private async findLocation(value: string): Promise<string | null> {
    const lower = value.toLowerCase();
    await this.warehouseCache.ensureSynced();
    const list = await this.warehouseCache.searchByName(value);
    const found = list.find(
      (w) =>
        String(w.locationFullName ?? "").toLowerCase() === lower ||
        String(w.locationName ?? "").toLowerCase() === lower,
    );
    return found
      ? String(found.locationFullName ?? "") || String(found.locationName ?? "")
      : null;
  }

  private saveLocation(location: string): void {
    const tasks: Array<Observable<boolean>> = [];
    for (const c of this.locationTargets) {
      for (const b of c.detail?.vendorLabelInfoList ?? []) {
        tasks.push(
          this.safe(
            this.palletService.updateBox({
              ...b,
              storageUnit: location,
              subStorageUnit: null,
            }),
          ),
        );
      }
      if (c.detail) {
        tasks.push(this.safe(this.updatePallet(c.detail, undefined, location)));
      }
    }
    this.runTasks(tasks, "Cập nhật thành công!", this.locationTargets, () =>
      this.goList(),
    );
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
}
