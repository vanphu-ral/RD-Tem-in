import { Component, OnInit, ViewChild } from "@angular/core";
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
import {
  ExportRequestListPage,
  ExportRequestListRow,
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
export class WarehouseExportListComponent implements OnInit {
  /** Cột bảng — Xuất kho khuyến nghị tạm ẩn Tiến độ (chưa có dữ liệu) */
  get displayedColumns(): string[] {
    return this.config.mode === "planning"
      ? ALL_COLUMNS.filter((c) => c !== "progress")
      : ALL_COLUMNS;
  }

  dataSource = new MatTableDataSource<ExportRequestListRow>([]);
  search = "";
  /** Mã đã nhập (mỗi mã 1 chip) — danh sách bên dưới chỉ hiển thị các mã này */
  codes: string[] = [];
  /** Mã đang tra (chưa có kết quả) */
  pending = new Set<string>();

  /** Danh sách phân trang (khi chưa nhập mã nào) */
  total = 0;
  pageIndex = 0;
  pageSize = 20;
  readonly pageSizeOptions = [20, 50, 100];
  listLoading = false;

  /** Theo route (mặc định Xuất chuyển kho) */
  config: WarehouseExportListRouteData = {
    mode: "request",
    title: "Xuất chuyển kho",
    codeLabel: "Mã đề nghị",
    searchPlaceholder: "Nhập mã yêu cầu...",
  };

  /** Mã đã tích */
  private selected = new Set<string>();
  /** Dòng của trang hiện tại / dòng của các chip */
  private pageRows: ExportRequestListRow[] = [];
  private chipRows: ExportRequestListRow[] = [];

  constructor(
    private exportService: WarehouseExportService,
    private dialog: MatDialog,
    private notificationService: NotificationService,
    private route: ActivatedRoute,
  ) {
    const data = this.route.snapshot
      .data as Partial<WarehouseExportListRouteData>;
    if (data.mode) {
      this.config = { ...this.config, ...data } as WarehouseExportListRouteData;
    }
  }

  @ViewChild(MatSort) set sort(sort: MatSort | undefined) {
    if (sort) {
      this.dataSource.sort = sort;
    }
  }

  ngOnInit(): void {
    this.loadPage();
  }

  get selectedCount(): number {
    return this.selected.size;
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
          this.refreshView();
        },
        error: () => {
          this.pending.delete(code);
          this.notificationService.error(`Không tra được mã "${code}".`);
        },
      });
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
    this.openDetail([...this.selected]);
  }

  /** Icon xem ở 1 dòng: mở màn Xuất kho với yêu cầu đó */
  openRowDetail(row: ExportRequestListRow): void {
    this.openDetail([row.code]);
  }

  statusClass(status: string): string {
    const s = status.toLowerCase();
    if (s.includes("đã xuất")) {
      return "done";
    }
    if (s.includes("đang")) {
      return "doing";
    }
    return "waiting";
  }

  trackRow(_: number, row: ExportRequestListRow): string {
    return row.code;
  }

  private openDetail(requestCodes: string[]): void {
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
            mode: this.config.mode,
            title: this.config.title,
          },
        },
      )
      .afterClosed()
      .subscribe((done) => {
        if (done) {
          // Đã xuất xong → bỏ chip/dòng của các mã đã xuất + tải lại trang danh sách
          requestCodes.forEach((code) => this.removeCode(code));
          this.loadPage();
        }
      });
  }

  /** Có chip → chỉ hiện các mã đã nhập; chưa có → trang danh sách hiện tại */
  private refreshView(): void {
    this.dataSource.data = this.codes.length ? this.chipRows : this.pageRows;
  }

  private loadPage(): void {
    this.listLoading = true;
    this.exportService
      .getExportRequestList("", this.pageIndex, this.pageSize, this.config.mode)
      .subscribe({
        next: (page: ExportRequestListPage) => {
          this.listLoading = false;
          this.pageRows = page.rows;
          this.total = page.total;
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
