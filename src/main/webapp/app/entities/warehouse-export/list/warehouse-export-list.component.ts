import {
  Component,
  HostListener,
  OnDestroy,
  OnInit,
  ViewChild,
} from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { MatTableDataSource, MatTableModule } from "@angular/material/table";
import { MatSort, MatSortModule } from "@angular/material/sort";
import { MatPaginatorModule, PageEvent } from "@angular/material/paginator";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatIconModule } from "@angular/material/icon";
import { MatTooltipModule } from "@angular/material/tooltip";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { AccountService } from "app/core/auth/account.service";
import { Subject, Subscription } from "rxjs";
import { debounceTime, take } from "rxjs/operators";
import {
  ExportRequestListPage,
  ExportRequestListRow,
  TRANSFER_SESSION_STATUS_LABELS,
  TransferSessionPage,
  TransferSessionRow,
  WarehouseExportService,
} from "../warehouse-export.service";
import {
  WarehouseExportDialogComponent,
  WarehouseExportDialogData,
} from "../warehouse-export-dialog.component";

const ALL_COLUMNS = [
  "select",
  "code",
  "proposer",
  "receivingUnit",
  "issuingUnit",
  "createdDate",
  "requestDate",
  "status",
  "warehouse",
  "description",
  "progress",
  "actions",
];

/**
 * Tab Đang xử lý: mỗi dòng = 1 phiên (gom nhiều phiếu) → chỉ các cột chung của phiên,
 * không có cột riêng của từng phiếu (người đề nghị, đơn vị, kho...)
 */
const SESSION_COLUMNS = [
  "requestCodes",
  "status",
  "note",
  "createdBy",
  "createdAt",
  "updatedAt",
  "actions",
];

/**
 * Lọc ở header tab Đang xử lý → query param của GET /warehouse-transfer-sessions
 * (key = tên param). Ngày dạng yyyy-MM-dd.
 */
interface SessionFilters {
  owtqDocEntry: string;
  status: string;
  note: string;
  createdBy: string;
  createdAt: string;
  updatedAt: string;
}

const EMPTY_SESSION_FILTERS: SessionFilters = {
  owtqDocEntry: "",
  status: "",
  note: "",
  createdBy: "",
  createdAt: "",
  updatedAt: "",
};

/**
 * Lọc ở header tab Tất cả phiếu (Xuất chuyển kho) → query param của GET /owtq
 * (key = tên param). Ngày dạng yyyy-MM-dd, text lọc theo chuỗi con (BE).
 */
interface RequestFilters {
  docEntry: string;
  docDate: string;
  docDueDate: string;
  /** Từ kho */
  filler: string;
  /** Đến kho */
  toWhsCode: string;
  /** Người giao */
  uPurNvGiao: string;
  /** Người nhận */
  uPurNvNhan: string;
  remark: string;
}

const EMPTY_REQUEST_FILTERS: RequestFilters = {
  docEntry: "",
  docDate: "",
  docDueDate: "",
  filler: "",
  toWhsCode: "",
  uPurNvGiao: "",
  uPurNvNhan: "",
  remark: "",
};

/** Màn hình hẹp → danh sách dạng card */
const MOBILE_BREAKPOINT = 768;

/** Trang danh sách đã cache của 1 tab */
interface TabPageCache<T> {
  pageIndex: number;
  pageSize: number;
  total: number;
  rows: T[];
}

/** Trạng thái phiên (BE) → nhãn hiển thị */
const SESSION_STATUS_LABELS: Record<string, string> =
  TRANSFER_SESSION_STATUS_LABELS;

/**
 * Tab danh sách (chỉ Xuất chuyển kho): Tất cả phiếu (SAP) / Đang xử lý (phiên xuất chuyển kho)
 * / Đã xuất (chưa có API, đang dùng dữ liệu mẫu)
 */
type ListTab = "all" | "processing" | "exported";

/** Cấu hình trang theo route: Xuất chuyển kho / Xuất kho khuyến nghị */
export interface WarehouseExportListRouteData {
  mode: "request" | "planning";
  title: string;
  /** Tên cột mã (Mã đề nghị / Mã lệnh) */
  codeLabel: string;
  searchPlaceholder: string;
}

/**
 * Trang Xuất kho (dùng chung cho Xuất chuyển kho / Xuất kho khuyến nghị). Chưa nhập mã → danh sách
 * phân trang từ API; nhập mã → Enter → tạo chip + tra mã đó, danh sách chỉ gồm các mã đã nhập.
 * Tích chọn → "Chi tiết xuất" (hoặc icon xem ở từng dòng) → màn Xuất kho với các yêu cầu đã chọn.
 */
@Component({
  selector: "jhi-warehouse-export-list",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatCheckboxModule,
    MatIconModule,
    MatTooltipModule,
  ],
  templateUrl: "./warehouse-export-list.component.html",
  styleUrls: ["./warehouse-export-list.component.scss"],
})
export class WarehouseExportListComponent implements OnInit, OnDestroy {
  /**
   * Cột bảng:
   * - Xuất kho khuyến nghị: tạm ẩn Tiến độ (chưa có dữ liệu)
   * - Xuất chuyển kho, tab Tất cả phiếu (SAP): ẩn Trạng thái
   * - Tab Đang xử lý dùng bảng phiên riêng (sessionColumns, không có Tiến độ)
   */
  get displayedColumns(): string[] {
    if (this.config.mode === "planning") {
      return ALL_COLUMNS.filter((c) => c !== "progress");
    }
    return this.tab === "all"
      ? ALL_COLUMNS.filter((c) => c !== "status")
      : ALL_COLUMNS;
  }

  readonly sessionColumns = SESSION_COLUMNS;
  /** Tab Đang xử lý: lọc ở header (gửi theo param) */
  sessionFilters: SessionFilters = { ...EMPTY_SESSION_FILTERS };
  /** Lọc header tab Tất cả phiếu (Xuất chuyển kho) */
  requestFilters: RequestFilters = { ...EMPTY_REQUEST_FILTERS };
  /** Lựa chọn lọc Trạng thái */
  readonly sessionStatusOptions = Object.entries(SESSION_STATUS_LABELS).map(
    ([value, label]) => ({ value, label }),
  );
  /** Tab "Đã xuất": tạm ẩn (chưa có API) — bật lại khi dùng */
  readonly showExportedTab = false;
  /** Mobile: hiển thị dạng card thay cho bảng */
  isMobile = this.detectMobile();
  /** Số mã đề nghị hiện trực tiếp trong ô, phần còn lại gộp thành "+n" */
  readonly sessionCodePreview = 2;

  dataSource = new MatTableDataSource<ExportRequestListRow>([]);
  /** Tab Đang xử lý: các phiên của trang hiện tại */
  sessionSource = new MatTableDataSource<TransferSessionRow>([]);
  search = "";
  /** Mã đã nhập (mỗi mã 1 chip) — danh sách bên dưới chỉ hiển thị các mã này */
  codes: string[] = [];
  /** Dropdown "Đã chọn N mã": mở / ô tìm / trang */
  pickerOpen = false;
  pickerSearch = "";
  pickerPage = 0;
  readonly pickerPageSize = 10;
  /** Mã đang tra (chưa có kết quả) */
  pending = new Set<string>();

  /** Tab đang mở + số phiếu đang xử lý (badge) */
  tab: ListTab = "all";
  processingCount = 0;

  /** Danh sách phân trang (khi chưa nhập mã nào) */
  total = 0;
  pageIndex = 0;
  pageSize = 20;
  readonly pageSizeOptions = [20, 50, 100];
  listLoading = false;
  /** Đang tạo phiên xuất chuyển kho (trước khi mở Chi tiết xuất) */
  creatingSession = false;

  /** Theo route (mặc định Xuất chuyển kho) */
  config: WarehouseExportListRouteData = {
    mode: "request",
    title: "Xuất chuyển kho",
    codeLabel: "Mã đề nghị",
    searchPlaceholder: "Nhập mã yêu cầu...",
  };

  /** Mã đã tích */
  private selected = new Set<string>();
  /** Gõ ô lọc → chờ gõ xong mới gọi API */
  private sessionFilter$ = new Subject<void>();
  private requestFilter$ = new Subject<void>();
  private subscriptions = new Subscription();
  /** Dòng của trang hiện tại / dòng của các chip */
  private pageRows: ExportRequestListRow[] = [];
  private chipRows: ExportRequestListRow[] = [];

  constructor(
    private exportService: WarehouseExportService,
    private dialog: MatDialog,
    private notificationService: NotificationService,
    private route: ActivatedRoute,
    private accountService: AccountService,
  ) {
    const data = this.route.snapshot
      .data as Partial<WarehouseExportListRouteData>;
    if (data.mode) {
      this.config = { ...this.config, ...data } as WarehouseExportListRouteData;
    }
  }

  @HostListener("window:resize")
  onResize(): void {
    this.isMobile = this.detectMobile();
  }

  @ViewChild(MatSort) set sort(sort: MatSort | undefined) {
    if (sort) {
      this.dataSource.sort = sort;
      this.sessionSource.sort = sort;
    }
  }

  ngOnInit(): void {
    this.subscriptions.add(
      this.sessionFilter$.pipe(debounceTime(400)).subscribe(() => {
        this.pageIndex = 0;
        this.loadPage(true);
      }),
    );
    this.subscriptions.add(
      this.requestFilter$.pipe(debounceTime(400)).subscribe(() => {
        this.pageIndex = 0;
        this.loadPage(true);
      }),
    );
    this.sessionSource.sortingDataAccessor = (row, column) => {
      switch (column) {
        case "createdAt":
          return row.createdAtRaw;
        case "updatedAt":
          return row.updatedAtRaw;
        default:
          return (row as unknown as Record<string, string | number>)[column];
      }
    };
    // Quay lại trang: mở lại tab lần trước, dùng dữ liệu đã cache
    const lastTab = this.exportService.lastListTab.get(this.config.mode);
    if (
      this.showTabs &&
      lastTab &&
      (lastTab !== "exported" || this.showExportedTab)
    ) {
      this.tab = lastTab as ListTab;
    }
    this.restorePaging();
    this.loadPage();
    this.loadProcessingCount();
  }

  /** Tab chỉ có ở Xuất chuyển kho */
  get showTabs(): boolean {
    return this.config.mode === "request";
  }

  /** Lọc theo chip chỉ áp dụng cho tab Tất cả phiếu */
  get isChipFiltered(): boolean {
    return this.tab === "all" && this.codes.length > 0;
  }

  selectTab(tab: ListTab): void {
    if (this.tab === tab) {
      return;
    }
    this.tab = tab;
    this.exportService.lastListTab.set(this.config.mode, tab);
    this.pageRows = [];
    this.sessionSource.data = [];
    this.total = 0;
    this.restorePaging();
    this.refreshView();
    this.loadPage();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  /** Ô lọc header tab Tất cả phiếu: chỉ Xuất chuyển kho (GET /owtq nhận param lọc) */
  get showRequestFilters(): boolean {
    return this.config.mode === "request" && this.tab === "all";
  }

  /** Có đang lọc ở header tab Tất cả phiếu */
  get hasRequestFilter(): boolean {
    return (
      this.showRequestFilters &&
      Object.values(this.requestFilters).some((v) => v.trim() !== "")
    );
  }

  /** Đổi ô lọc header (tab Tất cả phiếu) → tải lại trang đầu theo bộ lọc */
  onRequestFilterChange(): void {
    this.requestFilter$.next();
  }

  clearRequestFilters(): void {
    this.requestFilters = { ...EMPTY_REQUEST_FILTERS };
    this.pageIndex = 0;
    this.loadPage(true);
  }

  /** Có đang lọc ở header tab Đang xử lý */
  get hasSessionFilter(): boolean {
    return Object.values(this.sessionFilters).some((v) => v.trim() !== "");
  }

  /** Đổi ô lọc header (tab Đang xử lý) → tải lại trang đầu theo bộ lọc */
  onSessionFilterChange(): void {
    this.sessionFilter$.next();
  }

  clearSessionFilters(): void {
    this.sessionFilters = { ...EMPTY_SESSION_FILTERS };
    this.pageIndex = 0;
    this.loadPage(true);
  }

  /** Làm mới: gọi lại API danh sách của tab đang mở (+ số phiên đang xử lý) */
  refresh(): void {
    if (this.isLoading) {
      return;
    }
    this.loadPage(true);
    this.loadProcessingCount(true);
  }

  get selectedCount(): number {
    return this.selected.size;
  }

  /** Cột đang hiện ở tab hiện tại (card mobile dùng chung quy tắc ẩn/hiện của bảng) */
  hasColumn(column: string): boolean {
    return this.displayedColumns.includes(column);
  }

  /** Số dòng đang hiển thị (theo tab) */
  get rowCount(): number {
    return this.tab === "processing"
      ? this.sessionSource.data.length
      : this.dataSource.data.length;
  }

  get isLoading(): boolean {
    return this.pending.size > 0 || this.listLoading;
  }

  onPage(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadPage();
  }

  /** Enter ở ô nhập mã → tra mã, tìm thấy thì thêm chip + dòng (tự tích chọn) */
  onSearchEnter(): void {
    const code = this.search.trim();
    if (!code) {
      return;
    }
    const key = code.toLowerCase();
    if (
      this.codes.some((c) => c.toLowerCase() === key) ||
      [...this.pending].some((c) => c.toLowerCase() === key)
    ) {
      this.notificationService.warning(`Mã "${code}" đã được nhập.`);
      this.search = "";
      return;
    }
    this.search = "";
    this.pending.add(code);
    this.exportService
      .findExportRequestByCode(code, this.config.mode)
      .subscribe({
        next: (row: ExportRequestListRow | null) => {
          this.pending.delete(code);
          if (!row) {
            this.notificationService.error(
              `Không tìm thấy ${this.config.codeLabel.toLowerCase()} "${code}".`,
            );
            return;
          }
          if (this.codes.includes(row.code)) {
            return;
          }
          this.codes = [...this.codes, row.code];
          this.selected.add(row.code);
          this.chipRows = [...this.chipRows, row];
          this.loadProgress([row]);
          this.refreshView();
        },
        error: () => {
          this.pending.delete(code);
          this.notificationService.error(`Không tra được mã "${code}".`);
        },
      });
  }

  /** Mã trong dropdown: mã đã nhập + Enter, rồi tới các mã đã tích chọn khác */
  get pickedCodes(): string[] {
    return [
      ...this.codes,
      ...[...this.selected].filter((c) => !this.codes.includes(c)),
    ];
  }

  get pickerFiltered(): string[] {
    const term = this.pickerSearch.trim().toLowerCase();
    return term
      ? this.pickedCodes.filter((c) => c.toLowerCase().includes(term))
      : this.pickedCodes;
  }

  get pickerPageCount(): number {
    return Math.max(
      1,
      Math.ceil(this.pickerFiltered.length / this.pickerPageSize),
    );
  }

  get pickerPageItems(): string[] {
    const page = Math.min(this.pickerPage, this.pickerPageCount - 1);
    return this.pickerFiltered.slice(
      page * this.pickerPageSize,
      (page + 1) * this.pickerPageSize,
    );
  }

  togglePicker(): void {
    this.pickerOpen = !this.pickerOpen;
    this.pickerSearch = "";
    this.pickerPage = 0;
  }

  onPickerSearch(): void {
    this.pickerPage = 0;
  }

  setPickerPage(delta: number): void {
    this.pickerPage = Math.min(
      Math.max(0, this.pickerPage + delta),
      this.pickerPageCount - 1,
    );
  }

  isCodeSelected(code: string): boolean {
    return this.selected.has(code);
  }

  isEnteredCode(code: string): boolean {
    return this.codes.includes(code);
  }

  /** Tích / bỏ tích 1 mã trong dropdown (cùng trạng thái chọn với bảng) */
  togglePickedCode(code: string, checked: boolean): void {
    if (checked) {
      this.selected.add(code);
    } else {
      this.selected.delete(code);
    }
  }

  /** Bỏ chọn tất cả mã đang lọc trong dropdown */
  unpickAll(): void {
    this.pickerFiltered.forEach((c) => this.togglePickedCode(c, false));
  }

  /** Bỏ 1 chip → bỏ dòng + bỏ chọn của mã đó */
  removeCode(code: string): void {
    this.codes = this.codes.filter((c) => c !== code);
    this.selected.delete(code);
    this.chipRows = this.chipRows.filter((r) => r.code !== code);
    this.refreshView();
  }

  clearCodes(): void {
    this.codes.forEach((c) => this.selected.delete(c));
    this.codes = [];
    this.chipRows = [];
    this.refreshView();
  }

  isSelected(row: ExportRequestListRow): boolean {
    return this.selected.has(row.code);
  }

  toggleRow(row: ExportRequestListRow, checked: boolean): void {
    if (checked) {
      this.selected.add(row.code);
    } else {
      this.selected.delete(row.code);
    }
  }

  get allPageSelected(): boolean {
    const rows = this.dataSource.data;
    return rows.length > 0 && rows.every((r) => this.selected.has(r.code));
  }

  get somePageSelected(): boolean {
    const n = this.dataSource.data.filter((r) =>
      this.selected.has(r.code),
    ).length;
    return n > 0 && n < this.dataSource.data.length;
  }

  toggleAllPage(checked: boolean): void {
    for (const row of this.dataSource.data) {
      this.toggleRow(row, checked);
    }
  }

  /** "Chi tiết xuất": mở màn Xuất kho với các yêu cầu đã tích */
  openSelectedDetail(): void {
    if (!this.selected.size) {
      this.notificationService.warning(
        `Chọn ít nhất 1 ${this.config.codeLabel.toLowerCase()} để xem chi tiết xuất.`,
      );
      return;
    }
    this.openRequests([...this.selected]);
  }

  /** Icon xem ở 1 dòng: mở màn Xuất kho với yêu cầu đó */
  openRowDetail(row: ExportRequestListRow): void {
    this.openRequests([row.code]);
  }

  statusClass(status: string): string {
    const s = status.toLowerCase();
    if (s.includes("đã xuất")) {
      return "done";
    }
    if (s.includes("đang") || s.includes("đã gửi")) {
      return "doing";
    }
    return "waiting";
  }

  trackRow(_: number, row: ExportRequestListRow): string {
    return row.code;
  }

  trackSession(_: number, row: TransferSessionRow): number {
    return row.id;
  }

  sessionStatusLabel(status: string): string {
    return SESSION_STATUS_LABELS[status.toLowerCase()] ?? status;
  }

  /** Màu badge trạng thái phiên: mới tạo / đang xuất / hoàn thành / khác */
  sessionStatusTone(status: string): string {
    const s = (status ?? "").toLowerCase();
    return s === "new" || s === "exporting" || s === "completed" ? s : "other";
  }

  /** Icon xem ở 1 phiên: mở màn Xuất kho theo chi tiết phiên */
  openSessionDetail(row: TransferSessionRow): void {
    this.openDetail([], row.id);
  }

  /**
   * Xuất chuyển kho, tab Tất cả phiếu: tạo phiên (gom các phiếu đã chọn) rồi mở màn Xuất kho
   * theo phiên. Các trường hợp khác mở thẳng theo mã.
   */
  private openRequests(requestCodes: string[]): void {
    if (this.config.mode !== "request" || this.tab !== "all") {
      this.openDetail(requestCodes);
      return;
    }
    if (this.creatingSession) {
      return;
    }
    this.creatingSession = true;
    this.accountService
      .identity()
      .pipe(take(1))
      .subscribe((account) => {
        this.exportService
          .createTransferSession(requestCodes, account?.login ?? "unknown")
          .subscribe({
            next: (sessionId) => {
              this.creatingSession = false;
              // Có phiên mới → tab Đang xử lý cần tải lại khi mở
              this.exportService.listCache.delete(this.cacheKey("processing"));
              this.exportService.processingCountCache = null;
              this.openDetail(requestCodes, sessionId);
            },
            error: () => {
              this.creatingSession = false;
              this.notificationService.error(
                "Không tạo được phiên xuất chuyển kho.",
              );
            },
          });
      });
  }

  private openDetail(requestCodes: string[], sessionId?: number): void {
    this.dialog
      .open<WarehouseExportDialogComponent, WarehouseExportDialogData, boolean>(
        WarehouseExportDialogComponent,
        {
          width: "100vw",
          height: "100vh",
          maxWidth: "100vw",
          maxHeight: "100vh",
          panelClass: "warehouse-export-dialog-panel",
          autoFocus: false,
          disableClose: true,
          data: {
            requestCodes,
            sessionId,
            mode: this.config.mode,
            title: this.config.title,
          },
        },
      )
      .afterClosed()
      .subscribe((done) => {
        // Đóng chi tiết xuất (xuất xong / hủy phiên / đóng) → về danh sách chính, bỏ mã đã chọn
        this.resetPicks();
        if (sessionId != null && !done) {
          // Phiên có thể đã đổi trạng thái (đã scan thùng) → tab Đang xử lý tải lại khi mở
          this.exportService.listCache.delete(this.cacheKey("processing"));
          if (this.tab === "processing") {
            this.loadPage(true);
          }
        }
        if (done) {
          // Đã xuất xong / hủy phiên → tải lại trang danh sách
          this.clearListCache();
          this.loadPage(true);
          this.loadProcessingCount(true);
        }
      });
  }

  /** Bỏ hết mã đã nhập + mã đã tích, đóng dropdown → bảng về danh sách chính */
  private resetPicks(): void {
    this.codes = [];
    this.chipRows = [];
    this.selected.clear();
    this.pickerOpen = false;
    this.pickerSearch = "";
    this.pickerPage = 0;
    this.refreshView();
  }

  /** Có chip → chỉ hiện các mã đã nhập; chưa có → trang danh sách hiện tại */
  private refreshView(): void {
    this.dataSource.data = this.isChipFiltered ? this.chipRows : this.pageRows;
  }

  /** Badge "Đang xử lý (n)" — chỉ hiện khi BE trả totalElements. Có cache thì không gọi lại */
  private loadProcessingCount(force = false): void {
    if (!this.showTabs) {
      return;
    }
    const cached = this.exportService.processingCountCache;
    if (!force && cached !== null) {
      this.processingCount = cached;
      return;
    }
    this.exportService.getTransferSessionList(0, 1).subscribe({
      next: (page: TransferSessionPage) => {
        this.processingCount = page.total ?? 0;
        this.exportService.processingCountCache = this.processingCount;
      },
      error: () => {
        this.processingCount = 0;
      },
    });
  }

  /**
   * Xuất chuyển kho, tab Tất cả phiếu: sau khi có danh sách phiếu → tra summary từng phiếu để
   * điền cột Tiến độ (điền thẳng vào dòng nên dữ liệu cache cũng có tiến độ).
   */
  private loadProgress(rows: ExportRequestListRow[]): void {
    if (this.config.mode !== "request" || !rows.length) {
      return;
    }
    this.exportService.fillTransferProgress(rows).subscribe(() => {
      // Dòng đã đổi giá trị → vẽ lại bảng (sort theo Tiến độ dùng giá trị mới)
      this.dataSource.data = [...this.dataSource.data];
    });
  }

  /** Tab Đang xử lý đang lọc → không dùng cache (cache chỉ giữ danh sách chưa lọc) */
  private isFilteredSessions(): boolean {
    return this.tab === "processing" && this.hasSessionFilter;
  }

  private detectMobile(): boolean {
    return (
      typeof window !== "undefined" && window.innerWidth <= MOBILE_BREAKPOINT
    );
  }

  private cacheKey(tab: ListTab = this.tab): string {
    return `${this.config.mode}:${tab}`;
  }

  /** Trang đã cache của tab đang mở — đúng trang / cỡ trang hiện tại mới dùng */
  private cachedPage<T>(): TabPageCache<T> | null {
    if (this.isFilteredSessions() || this.hasRequestFilter) {
      return null;
    }
    const cached = this.exportService.listCache.get(this.cacheKey()) as
      | TabPageCache<T>
      | undefined;
    return cached &&
      cached.pageIndex === this.pageIndex &&
      cached.pageSize === this.pageSize
      ? cached
      : null;
  }

  private storePage<T>(rows: T[], total: number): void {
    if (this.isFilteredSessions()) {
      return;
    }
    this.exportService.listCache.set(this.cacheKey(), {
      pageIndex: this.pageIndex,
      pageSize: this.pageSize,
      total,
      rows,
    });
  }

  /** Mở lại tab: về đúng trang đã xem lần trước (chưa có thì trang đầu) */
  private restorePaging(): void {
    const cached = this.exportService.listCache.get(this.cacheKey());
    this.pageIndex = cached?.pageIndex ?? 0;
    this.pageSize = cached?.pageSize ?? this.pageSize;
  }

  /** Xuất xong: dữ liệu mọi tab của trang này đã cũ */
  private clearListCache(): void {
    for (const key of [...this.exportService.listCache.keys()]) {
      if (key.startsWith(`${this.config.mode}:`)) {
        this.exportService.listCache.delete(key);
      }
    }
    this.exportService.processingCountCache = null;
  }

  /** Tab Đang xử lý: danh sách phiên */
  private loadSessions(force: boolean): void {
    const cached = force ? null : this.cachedPage<TransferSessionRow>();
    if (cached) {
      this.sessionSource.data = cached.rows;
      this.total = cached.total;
      return;
    }
    this.listLoading = true;
    const { pageIndex, pageSize } = this;
    this.exportService
      .getTransferSessionList(pageIndex, pageSize, { ...this.sessionFilters })
      .subscribe({
        next: (page: TransferSessionPage) => {
          if (this.tab !== "processing") {
            return;
          }
          this.listLoading = false;
          // Mã phiếu: Criteria BE chưa có → lọc tại FE trong trang vừa tải
          const owtq = this.sessionFilters.owtqDocEntry.trim().toLowerCase();
          const rows = owtq
            ? page.rows.filter((r) =>
                r.requestCodes.some((c) => c.toLowerCase().includes(owtq)),
              )
            : page.rows;
          this.sessionSource.data = rows;
          // BE chưa trả totalElements → trang đầy thì cho sang trang sau
          this.total =
            page.total ??
            pageIndex * pageSize +
              page.rows.length +
              (page.rows.length === pageSize ? 1 : 0);
          this.storePage(rows, this.total);
        },
        error: () => {
          this.listLoading = false;
          this.sessionSource.data = [];
          this.total = 0;
          this.notificationService.error(
            "Không tải được danh sách phiên xuất chuyển kho.",
          );
        },
      });
  }

  /** Tải trang danh sách của tab đang mở — đã cache thì dùng lại (force = gọi lại API) */
  private loadPage(force = false): void {
    if (this.tab === "processing") {
      this.loadSessions(force);
      return;
    }
    const cached = force ? null : this.cachedPage<ExportRequestListRow>();
    if (cached) {
      this.pageRows = cached.rows;
      this.total = cached.total;
      this.refreshView();
      return;
    }
    this.listLoading = true;
    const tab = this.tab;
    const page$ =
      tab === "all"
        ? this.exportService.getExportRequestList(
            "",
            this.pageIndex,
            this.pageSize,
            this.config.mode,
            this.showRequestFilters ? { ...this.requestFilters } : {},
          )
        : this.exportService.getExportTrackingList(
            tab,
            this.pageIndex,
            this.pageSize,
          );
    page$.subscribe({
      next: (page: ExportRequestListPage) => {
        if (tab !== this.tab) {
          return;
        }
        this.listLoading = false;
        this.pageRows = page.rows;
        this.total = page.total;
        this.storePage(page.rows, page.total);
        if (tab === "all") {
          this.loadProgress(page.rows);
        }
        this.refreshView();
      },
      error: () => {
        this.listLoading = false;
        this.pageRows = [];
        this.total = 0;
        this.refreshView();
        this.notificationService.error(
          `Không tải được danh sách ${this.config.title.toLowerCase()}.`,
        );
      },
    });
  }
}
