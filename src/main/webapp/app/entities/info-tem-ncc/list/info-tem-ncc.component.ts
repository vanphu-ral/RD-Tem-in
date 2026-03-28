import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ChangeDetectorRef,
} from "@angular/core";
import { DatePipe } from "@angular/common";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatDialog } from "@angular/material/dialog";
import { Router } from "@angular/router";

import { DialogContentExampleDialogComponent } from "./confirm-dialog/confirm-dialog.component";
import { AlertService } from "app/core/util/alert.service";
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from "@angular/animations";
import { OrderSummaryDialogComponent } from "./order-summary-dialog/order-summary-dialog.component";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import {
  ImportVendorTemTransaction,
  ManagerTemNccService,
  PoImportTem,
} from "app/entities/list-material/services/info-tem-ncc.service";
import { WarehouseCacheService } from "app/entities/list-material/services/warehouse-cache.service";
import { CachedWarehouse } from "app/entities/list-material/services/warehouse-db";

// ==================== INTERFACES ====================

export interface TemNccItem {
  id: number;
  poCode: string;
  vendorName: string;
  arrivalDate: string; // ISO date string
  createdDate: string; // ISO datetime string
  createdBy: string;
  // warehouse: string;
  status: string;
  sessions?: SessionItem[];
  _raw?: PoImportTem;
}

export interface SessionItem {
  importDate: string;
  warehouse: string;
  status: string;
  totalQty: number;
  itemCount: number;
}

export interface FilterValues {
  poCode: string;
  vendorName: string;
  arrivalDateStr: string; // text filter "yyyy-MM-dd"
  arrivalDate: Date | null;
  createdDateStr: string; // text filter "yyyy-MM-dd"
  createdDate: Date | null;
  createdBy: string;
  warehouse: string;
  status: string;
}

// ==================== COMPONENT ====================

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
    "poCode",
    "vendorName",
    "arrivalDate",
    "createdDate",
    "createdBy",
    // "warehouse",
    "status",
  ];

  dataSource = new MatTableDataSource<TemNccItem>([]);
  pageSize = 20;
  isLoading = false;

  filterValues: FilterValues = {
    poCode: "",
    vendorName: "",
    arrivalDateStr: "",
    arrivalDate: null,
    createdDateStr: "",
    createdDate: null,
    createdBy: "",
    warehouse: "",
    status: "",
  };
  loadingDetailIds = new Set<number>();

  currentPage = 0;
  totalItems: number = 0;
  totalPages: number = 0;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  readonly SERVER_PAGE_SIZE = 20;
  readonly sessionPageSize = 5;
  private isWarehouseLoaded = false;
  private isFetchingWarehouses = false;
  private detailCache = new Map<number, PoImportTem>();
  private sessionPages = new Map<number, number>();
  private expandedRows = new Set<number>();
  private expandedSessions = new Set<string>();
  private expandedMobileRows = new Set<number>();
  constructor(
    private dialog: MatDialog,
    private alertService: AlertService,
    private datePipe: DatePipe,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private managerTemNccService: ManagerTemNccService,
    private notificationService: NotificationService,
    private warehouseCacheService: WarehouseCacheService,
  ) {}

  // ==================== LIFECYCLE ====================

  ngOnInit(): void {
    this.loadData();
    void this.initWarehouseCache();
  }
  onViewDetail(row: TemNccItem): void {
    this.router.navigate(["/info-tem-ncc/info-tem-ncc-detail"], {
      state: { data: row._raw },
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.filterPredicate = this.buildFilterPredicate();

    // lang nghe thay doi trang va size
    this.paginator.page.subscribe((event) => {
      this.pageSize = event.pageSize;
      this.loadData(event.pageIndex, event.pageSize);
    });

    // KHONG gan paginator vao dataSource vi phan trang la server-side
    // this.dataSource.paginator = this.paginator; <- xoa dong nay
  }

  // ==================== FILTER ====================

  applyFilter(): void {
    this.dataSource.filterPredicate = this.buildFilterPredicate();
    this.dataSource.filter = JSON.stringify(this.filterValues);
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  toggleRow(row: TemNccItem): void {
    if (this.expandedRows.has(row.id)) {
      this.expandedRows.delete(row.id);
    } else {
      this.expandedRows.add(row.id);
      this.loadDetailIfNeeded(row);
    }
  }

  isLoadingDetail(row: TemNccItem): boolean {
    return this.loadingDetailIds.has(row.id);
  }

  isExpanded(row: TemNccItem): boolean {
    return this.expandedRows.has(row.id);
  }
  toggleMobileRow(item: TemNccItem): void {
    if (this.expandedMobileRows.has(item.id)) {
      this.expandedMobileRows.delete(item.id);
    } else {
      this.expandedMobileRows.add(item.id);
    }
  }

  isMobileExpanded(item: TemNccItem): boolean {
    return this.expandedMobileRows.has(item.id);
  }
  // ==================== PAGINATION HELPER ====================

  getRowIndex(row: TemNccItem): number {
    const pageIndex = this.paginator?.pageIndex ?? 0;
    const pageSize = this.paginator?.pageSize ?? this.pageSize;
    const idxInPage = this.dataSource.data.findIndex((r) => r.id === row.id);
    return pageIndex * pageSize + idxInPage + 1;
  }

  get mobileDataSource(): TemNccItem[] {
    return this.dataSource.filteredData ?? this.dataSource.data ?? [];
  }

  getSessionPage(row: TemNccItem): number {
    return this.sessionPages.get(row.id) ?? 0;
  }

  setSessionPage(row: TemNccItem, page: number): void {
    this.sessionPages.set(row.id, page);
  }

  getSessionTotalPages(row: TemNccItem): number {
    return Math.ceil((row.sessions?.length ?? 0) / this.sessionPageSize);
  }

  getPagedSessions(row: TemNccItem): SessionItem[] {
    const page = this.getSessionPage(row);
    const start = page * this.sessionPageSize;
    return (row.sessions ?? []).slice(start, start + this.sessionPageSize);
  }

  getSessionIndex(row: TemNccItem, indexOnPage: number): number {
    return this.getSessionPage(row) * this.sessionPageSize + indexOnPage + 1;
  }

  getSessionPageStart(row: TemNccItem): number {
    return this.getSessionPage(row) * this.sessionPageSize + 1;
  }

  getSessionPageEnd(row: TemNccItem): number {
    return Math.min(
      (this.getSessionPage(row) + 1) * this.sessionPageSize,
      row.sessions?.length ?? 0,
    );
  }

  // ==================== ACTIONS ====================
  onInfo(item: TemNccItem): void {
    this.dialog.open(OrderSummaryDialogComponent, {
      width: "640px",
      maxWidth: "95vw",
      data: { item },
      panelClass: "summary-dialog-panel",
    });
  }
  onAdd(): void {
    this.router.navigate(["./new"], { relativeTo: null });
    // Adjust route to match your routing config, e.g.:
    // this.router.navigate(['tem-ncc/new']);
  }

  onView(item: TemNccItem): void {
    // Adjust route, e.g.:
    this.router.navigate(["info-tem-ncc-detail"]);
  }

  onDelete(item: TemNccItem): void {
    const dialogRef = this.dialog.open(DialogContentExampleDialogComponent, {
      width: "400px",
      data: {
        title: "Xác nhận xóa",
        message: "Bạn có chắc chắn muốn xóa bản ghi này?",
        confirmText: "Xóa",
        cancelText: "Hủy",
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.managerTemNccService
          .deleteTemPoImport(Number(result.id))
          .subscribe({
            next: () => {
              this.notificationService.success("Xóa kịch bản thành công!");
              this.loadData();
              this.cdr.detectChanges();
            },
            error: () => {
              this.notificationService.error("Xóa kịch bản thất bại!");
            },
          });
      }
    });
  }

  private loadData(page = 0, size = this.SERVER_PAGE_SIZE): void {
    this.isLoading = true;
    this.managerTemNccService.getPoImportTems(page, size).subscribe({
      next: (response) => {
        this.dataSource.data = response.datas.map((item: PoImportTem) =>
          this.mapToTemNccItem(item),
        );
        this.totalItems = response.pagination.totalItems;
        this.totalPages = response.pagination.totalPages;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.notificationService.error("Không thể tải danh sách đơn hàng!");
        this.isLoading = false;
      },
    });
  }

  private buildFilterPredicate() {
    return (item: TemNccItem, filterJson: string): boolean => {
      const f: FilterValues = JSON.parse(filterJson);

      const match = (
        field: string | null | undefined,
        query: string,
      ): boolean =>
        !query || (field ?? "").toLowerCase().includes(query.toLowerCase());

      return (
        match(item.poCode, f.poCode) &&
        match(item.vendorName, f.vendorName) &&
        match(item.createdBy, f.createdBy) &&
        // match(item.warehouse, f.warehouse) &&
        match(item.status, f.status) &&
        match(
          this.datePipe.transform(item.arrivalDate, "yyyy-MM-dd") ?? "",
          f.arrivalDateStr,
        ) &&
        match(
          this.datePipe.transform(item.createdDate, "yyyy-MM-dd") ?? "",
          f.createdDateStr,
        )
      );
    };
  }
  private mapToTemNccItem(item: PoImportTem): TemNccItem {
    return {
      id: item.id,
      poCode: item.poNumber,
      vendorName: item.vendorName,
      arrivalDate: item.entryDate,
      createdDate: item.createdAt,
      createdBy: item.createdBy,
      status: item.status,
      sessions: (item.importVendorTemTransactions ?? []).map((t) =>
        this.mapToSessionItem(t),
      ),
      _raw: item,
    };
  }

  private mapToSessionItem(t: ImportVendorTemTransaction): SessionItem {
    const totalQty = t.poDetails.reduce(
      (sum, d) => sum + (d.totalQuantity ?? 0),
      0,
    );
    const itemCount = t.poDetails.reduce(
      (sum, d) => sum + (d.vendorTemDetails?.length ?? 0),
      0,
    );
    return {
      importDate: t.entryDate ?? t.createdAt,
      warehouse: t.storageUnit,
      status: t.status,
      totalQty,
      itemCount,
    };
  }

  private loadDetailIfNeeded(row: TemNccItem): void {
    if (this.detailCache.has(row.id)) {
      this.applyDetailToRow(row, this.detailCache.get(row.id)!);
      return;
    }

    this.loadingDetailIds.add(row.id);
    this.managerTemNccService.getPoImportTemDetail(row.id).subscribe({
      next: (detail) => {
        this.detailCache.set(row.id, detail);
        this.applyDetailToRow(row, detail);
        this.loadingDetailIds.delete(row.id);
        this.cdr.markForCheck();
      },
      error: () => {
        this.loadingDetailIds.delete(row.id);
        this.notificationService.error("Không thể tải chi tiết đơn hàng!");
      },
    });
  }
  private applyDetailToRow(row: TemNccItem, detail: PoImportTem): void {
    const sessions = (detail.importVendorTemTransactions ?? []).map((t) =>
      this.mapToSessionItem(t),
    );
    // cập nhật sessions vào row trong dataSource
    const idx = this.dataSource.data.findIndex((r) => r.id === row.id);
    if (idx >= 0) {
      const updated = { ...this.dataSource.data[idx], sessions, _raw: detail };
      const newData = [...this.dataSource.data];
      newData[idx] = updated;
      this.dataSource.data = newData;
    }
  }

  private async initWarehouseCache(): Promise<void> {
    if (this.isWarehouseLoaded || this.isFetchingWarehouses) {
      return;
    }

    const cached = await this.warehouseCacheService.getAll();

    if (cached.length > 0) {
      this.isWarehouseLoaded = true;
      return;
    }

    this.isFetchingWarehouses = true;
    this.managerTemNccService.getWarehouses().subscribe({
      next: (list) => {
        const mapped: CachedWarehouse[] = list.map((loc) => ({
          locationId: loc.id,
          locationName: loc.locationName,
          locationFullName: loc.locationFullName,
        }));
        this.warehouseCacheService
          .saveAll(mapped)
          .then(() => {
            this.isWarehouseLoaded = true;
            this.isFetchingWarehouses = false;
          })
          .catch(() => {
            this.isFetchingWarehouses = false;
          });
      },
      error: () => {
        console.warn("Khong the cache danh sach kho");
        this.isFetchingWarehouses = false;
      },
    });
  }
}
