import { Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { DialogContentExampleDialogComponent } from "app/entities/list-material/confirm-dialog/confirm-dialog.component";
import { forkJoin, Observable, of } from "rxjs";
import { catchError, map } from "rxjs/operators";
import {
  CreatePalletDialogComponent,
  CreatePalletDialogData,
} from "./create-pallet-dialog/create-pallet-dialog.component";
import { PalletMngtService } from "./pallet-mngt.service";
import { PrintPalletDialogComponent } from "./print-pallet-dialog/print-pallet-dialog.component";
import {
  CreatePalletDialogResult,
  PalletBoxItem,
  PalletItem,
  PrintPalletDialogData,
} from "./pallet-management.model";

@Component({
  selector: "jhi-pallet-management",
  templateUrl: "./pallet-management.component.html",
  styleUrls: ["./pallet-management.component.scss"],
  standalone: false,
})
export class PalletManagementComponent implements OnInit {
  displayedColumns = [
    "stt",
    "actions",
    "serialPallet",
    "locationName",
    "numberOfBox",
    "totalQuantity",
    "createAt",
    "createBy",
    "status",
    "note",
    "updatedAt",
    "updatedBy",
  ];
  expandedDetailColumns = ["expandedDetail"];

  dataSource = new MatTableDataSource<PalletItem>([]);
  filterValues: Record<string, string> = {
    serialPallet: "",
    locationName: "",
    numberOfBox: "",
    totalQuantity: "",
    createAt: "",
    createBy: "",
    status: "",
    note: "",
    updatedAt: "",
    updatedBy: "",
  };

  expandedSerial: string | null = null;
  boxesBySerial: Record<string, PalletBoxItem[]> = {};
  boxLoadingSerials: Record<string, boolean> = {};
  boxErrorSerials: Record<string, boolean> = {};

  pageSize = 20;
  pageSizeOptions = [10, 20, 50, 100];
  filterExpanded = false;
  isLoading = false;
  /** Pallet đang xóa (chặn bấm lặp) */
  deletingIds = new Set<number>();

  @ViewChild(MatPaginator) set paginator(p: MatPaginator) {
    if (p) {
      this.dataSource.paginator = p;
    }
  }

  @ViewChild(MatSort) set sort(s: MatSort) {
    if (s) {
      this.dataSource.sort = s;
    }
  }

  constructor(
    private dialog: MatDialog,
    private palletMngtService: PalletMngtService,
    private notificationService: NotificationService,
  ) {}

  ngOnInit(): void {
    this.dataSource.filterPredicate = (row, filterJson) =>
      this.matchFilters(row, filterJson);
    this.loadPallets();
  }

  get nextSequence(): number {
    return this.dataSource.data.length + 1;
  }

  get mobileDataSource(): PalletItem[] {
    const data = this.dataSource.filteredData ?? this.dataSource.data ?? [];
    const pageIndex = this.dataSource.paginator?.pageIndex ?? 0;
    const pageSize = this.dataSource.paginator?.pageSize ?? this.pageSize;
    const start = pageIndex * pageSize;
    return data.slice(start, start + pageSize);
  }

  get extraFilterActiveCount(): number {
    const keys = [
      "locationName",
      "numberOfBox",
      "totalQuantity",
      "createAt",
      "createBy",
      "status",
      "note",
      "updatedAt",
      "updatedBy",
    ];
    return keys.filter((k) => (this.filterValues[k] ?? "").trim()).length;
  }

  loadPallets(): void {
    this.isLoading = true;
    this.palletMngtService.getAll().subscribe({
      next: (rows) => {
        // Ngày tạo mới nhất lên đầu (bấm tiêu đề cột vẫn sắp xếp lại được)
        this.dataSource.data = this.sortNewestFirst(
          Array.isArray(rows) ? rows : [],
        );
        this.isLoading = false;
      },
      error: () => {
        this.dataSource.data = [];
        this.isLoading = false;
        this.notificationService.error("Không tải được danh sách pallet.");
      },
    });
  }

  toggleFilterPanel(): void {
    this.filterExpanded = !this.filterExpanded;
  }

  clearAllFilters(): void {
    Object.keys(this.filterValues).forEach((k) => {
      this.filterValues[k] = "";
    });
    this.applyFilter();
  }

  getRowIndex(row: PalletItem): number {
    const index = this.dataSource.filteredData.indexOf(row);
    return index + 1;
  }

  /** Chọn ngày ở ô lọc → điền dd/MM/yyyy (lọc chứa theo ngày hiển thị) */
  onFilterDatePicked(key: string, value: Date | null): void {
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
    this.dataSource.paginator?.firstPage();
  }

  clearFilter(key: string): void {
    this.filterValues[key] = "";
    this.applyFilter();
  }

  displayText(value: string | number | null | undefined): string {
    if (value === null || value === undefined || value === "") {
      return "—";
    }
    return String(value);
  }

  formatDateTime(value: string | null): string {
    const raw = (value ?? "").trim();
    if (!raw) {
      return "—";
    }
    const date = new Date(raw);
    if (Number.isNaN(date.getTime())) {
      return raw;
    }
    const dd = String(date.getDate()).padStart(2, "0");
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const yyyy = date.getFullYear();
    const hh = String(date.getHours()).padStart(2, "0");
    const mi = String(date.getMinutes()).padStart(2, "0");
    return `${dd}/${mm}/${yyyy} ${hh}:${mi}`;
  }

  statusLabel(status: string | null): string {
    const raw = (status ?? "").trim();
    const key = raw.toUpperCase();
    if (key === "IN_USE" || key === "USING") {
      return "Đang sử dụng";
    }
    if (key === "UNUSED" || key === "NEW") {
      return "Chưa sử dụng";
    }
    return raw.length > 0 ? raw : "—";
  }

  isInUse(status: string | null): boolean {
    const key = (status ?? "").trim().toUpperCase();
    return key === "IN_USE" || key === "USING";
  }

  isUnused(status: string | null): boolean {
    const key = (status ?? "").trim().toUpperCase();
    return key === "UNUSED" || key === "NEW";
  }

  isExpanded(row: PalletItem): boolean {
    const serial = (row.serialPallet ?? "").trim();
    return serial.length > 0 && this.expandedSerial === serial;
  }

  toggleExpand(row: PalletItem, event?: Event): void {
    if (event) {
      event.stopPropagation();
    }
    const serial = (row.serialPallet ?? "").trim();
    if (!serial) {
      return;
    }
    if (this.expandedSerial === serial) {
      this.expandedSerial = null;
      return;
    }
    this.expandedSerial = serial;
    if (this.boxesBySerial[serial] || this.boxLoadingSerials[serial]) {
      return;
    }
    this.loadBoxes(serial);
  }

  getBoxes(row: PalletItem): PalletBoxItem[] {
    const serial = (row.serialPallet ?? "").trim();
    return this.boxesBySerial[serial] ?? [];
  }

  isBoxLoading(row: PalletItem): boolean {
    const serial = (row.serialPallet ?? "").trim();
    return !!this.boxLoadingSerials[serial];
  }

  hasBoxError(row: PalletItem): boolean {
    const serial = (row.serialPallet ?? "").trim();
    return !!this.boxErrorSerials[serial];
  }

  /** Ngày → dd/MM/yyyy. Nhận yyyyMMdd (vd 20261008), yyyy-MM-dd, ISO; không parse được → giữ nguyên */
  formatDateOnly(value: string | null | undefined): string {
    const raw = (value ?? "").trim();
    if (!raw) {
      return "—";
    }
    const pad = (n: number): string => String(n).padStart(2, "0");
    const ymd = /^(\d{4})-?(\d{2})-?(\d{2})$/.exec(
      raw.includes("T") ? raw.slice(0, 10) : raw,
    );
    if (ymd) {
      return `${ymd[3]}/${ymd[2]}/${ymd[1]}`;
    }
    const date = new Date(raw);
    if (Number.isNaN(date.getTime())) {
      return raw;
    }
    return `${pad(date.getDate())}/${pad(date.getMonth() + 1)}/${date.getFullYear()}`;
  }

  openCreateDialog(): void {
    const dialogRef = this.dialog.open(CreatePalletDialogComponent, {
      width: "640px",
      maxWidth: "95vw",
      disableClose: true,
      data: { nextSequence: this.nextSequence } as CreatePalletDialogData,
      panelClass: "create-pallet-dialog-panel",
    });

    dialogRef
      .afterClosed()
      .subscribe((result: CreatePalletDialogResult | undefined) => {
        if (!result || result.created.length === 0) {
          return;
        }
        const ids: number[] = [];
        for (const pallet of result.created) {
          ids.push(pallet.id);
        }
        this.loadPallets();
        if (result.saveAndPrint) {
          this.openPrintDialog(ids, result.created);
        }
      });
  }

  onPrintPalletList(): void {
    this.openPrintDialog();
  }

  /**
   * Xóa pallet: popup xác nhận → gỡ mọi thùng khỏi pallet (DELETE /pallet-box-mappings/{mappingId})
   * → xóa pallet (DELETE /pallet-mngts/{id}) → tải lại danh sách.
   * Gỡ thùng lỗi thì dừng, không xóa pallet. Thùng chỉ bị gỡ khỏi pallet, không bị xóa.
   */
  onDelete(row: PalletItem): void {
    if (this.deletingIds.has(row.id)) {
      return;
    }
    const serial = (row.serialPallet ?? "").trim();
    this.deletingIds.add(row.id);
    this.palletMngtService.getBoxMappingIds(serial).subscribe({
      next: (mappingIds) => {
        this.deletingIds.delete(row.id);
        const boxNote = mappingIds.length
          ? ` Pallet đang chứa ${mappingIds.length} thùng — các thùng sẽ được gỡ khỏi pallet (thùng không bị xóa).`
          : "";
        this.confirmDelete(
          `Xóa pallet ${serial}?${boxNote} Thao tác này không hoàn tác được.`,
        ).subscribe((ok) => {
          if (ok) {
            this.deletePallet(row, mappingIds);
          }
        });
      },
      error: () => {
        this.deletingIds.delete(row.id);
        this.notificationService.error(
          `Không kiểm tra được thùng của pallet ${serial} — chưa xóa.`,
        );
      },
    });
  }

  isDeleting(row: PalletItem): boolean {
    return this.deletingIds.has(row.id);
  }

  /** In từ 1 dòng → dialog In chỉ có đúng pallet đó */
  onPrint(row: PalletItem): void {
    this.openPrintDialog([row.id], [row]);
  }

  private loadBoxes(serial: string): void {
    this.boxLoadingSerials = { ...this.boxLoadingSerials, [serial]: true };
    this.boxErrorSerials = { ...this.boxErrorSerials, [serial]: false };
    this.palletMngtService.getBoxesBySerialPallet(serial).subscribe({
      next: (boxes) => {
        this.boxesBySerial = { ...this.boxesBySerial, [serial]: boxes };
        this.boxLoadingSerials = { ...this.boxLoadingSerials, [serial]: false };
      },
      error: () => {
        this.boxesBySerial = { ...this.boxesBySerial, [serial]: [] };
        this.boxLoadingSerials = { ...this.boxLoadingSerials, [serial]: false };
        this.boxErrorSerials = { ...this.boxErrorSerials, [serial]: true };
        this.notificationService.error(
          `Không tải được danh sách thùng của pallet ${serial}.`,
        );
      },
    });
  }

  private openPrintDialog(
    preselectedIds?: number[],
    pallets?: PalletItem[],
  ): void {
    this.dialog.open(PrintPalletDialogComponent, {
      width: "980px",
      maxWidth: "96vw",
      maxHeight: "92vh",
      disableClose: true,
      panelClass: "print-pallet-dialog-panel",
      data: {
        pallets: pallets ?? [...this.dataSource.data],
        preselectedIds,
      } as PrintPalletDialogData,
    });
  }

  private matchFilters(row: PalletItem, filterJson: string): boolean {
    let filters: Record<string, string> = {};
    try {
      filters = JSON.parse(filterJson || "{}");
    } catch {
      return true;
    }

    const includes = (
      value: string | number | null | undefined,
      key: string,
    ): boolean => {
      const term = (filters[key] ?? "").trim().toLowerCase();
      if (!term) {
        return true;
      }
      return String(value ?? "")
        .toLowerCase()
        .includes(term);
    };

    const statusTerm = (filters.status ?? "").trim().toLowerCase();
    const statusOk =
      !statusTerm ||
      this.statusLabel(row.status).toLowerCase().includes(statusTerm) ||
      String(row.status ?? "")
        .toLowerCase()
        .includes(statusTerm);

    return (
      includes(row.serialPallet, "serialPallet") &&
      includes(row.locationName, "locationName") &&
      includes(row.numberOfBox, "numberOfBox") &&
      includes(row.totalQuantity, "totalQuantity") &&
      includes(this.formatDateTime(row.createAt), "createAt") &&
      includes(row.createBy, "createBy") &&
      statusOk &&
      includes(row.note, "note") &&
      includes(this.formatDateTime(row.updatedAt), "updatedAt") &&
      includes(row.updatedBy, "updatedBy")
    );
  }

  private deletePallet(row: PalletItem, mappingIds: number[]): void {
    const serial = row.serialPallet;
    this.deletingIds.add(row.id);
    const removeBoxes$: Observable<boolean[]> = mappingIds.length
      ? forkJoin(
          mappingIds.map((id) =>
            this.palletMngtService.deleteBoxMapping(id).pipe(
              map((): boolean => true),
              catchError(() => of(false)),
            ),
          ),
        )
      : of([]);
    removeBoxes$.subscribe((results) => {
      const failed = results.filter((ok) => !ok).length;
      if (failed) {
        this.deletingIds.delete(row.id);
        this.notificationService.error(
          `Gỡ được ${results.length - failed}/${results.length} thùng, ${failed} thùng lỗi — chưa xóa pallet ${serial}.`,
        );
        this.loadPallets();
        return;
      }
      this.palletMngtService.deletePallet(row.id).subscribe({
        next: () => {
          this.deletingIds.delete(row.id);
          this.notificationService.success(
            results.length
              ? `Đã gỡ ${results.length} thùng và xóa pallet ${serial}.`
              : `Đã xóa pallet ${serial}.`,
          );
          this.loadPallets();
        },
        error: () => {
          this.deletingIds.delete(row.id);
          this.notificationService.error(
            results.length
              ? `Đã gỡ ${results.length} thùng nhưng xóa pallet ${serial} thất bại.`
              : `Xóa pallet ${serial} thất bại.`,
          );
          this.loadPallets();
        },
      });
    });
  }

  private confirmDelete(message: string): Observable<boolean> {
    return this.dialog
      .open(DialogContentExampleDialogComponent, {
        width: "420px",
        maxWidth: "92vw",
        autoFocus: false,
        data: {
          title: "Xác nhận xóa pallet",
          message,
          confirmText: "Xóa",
          cancelText: "Hủy",
        },
      })
      .afterClosed()
      .pipe(map((result) => result === true));
  }

  /** createAt giảm dần; cùng thời điểm / không có ngày tạo → id lớn hơn trước */
  private sortNewestFirst(rows: PalletItem[]): PalletItem[] {
    const time = (r: PalletItem): number => {
      const t = new Date(r.createAt ?? "").getTime();
      return Number.isNaN(t) ? 0 : t;
    };
    return [...rows].sort(
      (a, b) => time(b) - time(a) || Number(b.id ?? 0) - Number(a.id ?? 0),
    );
  }
}
