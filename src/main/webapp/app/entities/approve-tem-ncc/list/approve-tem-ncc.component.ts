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
import { ActivatedRoute, Router } from "@angular/router";
import { SelectionModel } from "@angular/cdk/collections";

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
  ApproveVendorTemPayload,
  CreateVendorTemDetailPayload,
  ImportVendorTemTransaction,
  ManagerTemNccService,
  PoImportTem,
} from "app/entities/list-material/services/info-tem-ncc.service";
import { forkJoin, of } from "rxjs";
import { StatusBadgeService } from "app/entities/list-material/services/status-badge.service";

export interface TemNccItem {
  id: number;
  poCode: string;
  vendorName: string;
  arrivalDate: string;
  createdDate: string;
  createdBy: string;
  warehouse: string;
  status: string;
  sessions?: SessionItem[];
  _raw?: PoImportTem;
}

export interface SessionItem {
  importDate: string;
  warehouse: string;
  warehouseType: string;
  status: string;
  totalQty: number;
  totalScanQty: number;
  itemCount: number;
  transactionId: number;
}

export interface FilterValues {
  poCode: string;
  vendorName: string;
  arrivalDateStr: string;
  arrivalDate: Date | null;
  createdDateStr: string;
  createdDate: Date | null;
  createdBy: string;
  // warehouse: string;
  status: string;
}

@Component({
  selector: "jhi-approve-tem-ncc",
  standalone: false,
  templateUrl: "./approve-tem-ncc.component.html",
  styleUrls: ["./approve-tem-ncc.component.scss"],
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
export class ApproveTemNccComponent implements OnInit, AfterViewInit {
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
    // warehouse: "",
    status: "",
  };

  sessionSelection = new Map<string, SelectionModel<number>>();
  loadingDetailIds = new Set<number>();

  currentPage = 0;
  totalItems = 0;
  totalPages = 0;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  readonly SERVER_PAGE_SIZE = 20;
  readonly sessionPageSize = 5;

  private detailCache = new Map<number, PoImportTem>();
  private sessionPages = new Map<number, number>();
  private expandedRows = new Set<number>();
  private expandedMobileRows = new Set<number>();

  constructor(
    private dialog: MatDialog,
    private alertService: AlertService,
    private datePipe: DatePipe,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private managerTemNccService: ManagerTemNccService,
    private notificationService: NotificationService,
    private route: ActivatedRoute,
    public statusBadgeService: StatusBadgeService,
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.filterPredicate = this.buildFilterPredicate();
    this.paginator.page.subscribe((event) => {
      this.pageSize = event.pageSize;
      this.loadData(event.pageIndex, event.pageSize);
    });
  }

  // ==================== FILTER ====================

  applyFilter(): void {
    this.dataSource.filterPredicate = this.buildFilterPredicate();
    this.dataSource.filter = JSON.stringify(this.filterValues);
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  // ==================== EXPAND ====================

  toggleRow(row: TemNccItem): void {
    if (this.expandedRows.has(row.id)) {
      this.expandedRows.delete(row.id);
    } else {
      this.expandedRows.add(row.id);
      this.loadDetailIfNeeded(row);
    }
  }

  isExpanded(row: TemNccItem): boolean {
    return this.expandedRows.has(row.id);
  }

  isLoadingDetail(row: TemNccItem): boolean {
    return this.loadingDetailIds.has(row.id);
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

  // ==================== PAGINATION ====================

  getRowIndex(row: TemNccItem): number {
    const pageIndex = this.paginator?.pageIndex ?? 0;
    const pageSize = this.paginator?.pageSize ?? this.pageSize;
    const allFiltered = this.dataSource.filteredData ?? this.dataSource.data;
    const idxInFiltered = allFiltered.findIndex((r) => r.id === row.id);
    return idxInFiltered - pageIndex * pageSize + 1;
  }

  get mobileDataSource(): TemNccItem[] {
    const data = this.dataSource.filteredData ?? this.dataSource.data ?? [];
    const start =
      (this.paginator?.pageIndex ?? 0) *
      (this.paginator?.pageSize ?? this.pageSize);
    return data.slice(
      start,
      start + (this.paginator?.pageSize ?? this.pageSize),
    );
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
    return (row.sessions ?? []).slice(
      page * this.sessionPageSize,
      (page + 1) * this.sessionPageSize,
    );
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

  // ==================== SELECTION ====================

  getSessionSelection(row: TemNccItem): SelectionModel<number> {
    if (!this.sessionSelection.has(String(row.id))) {
      this.sessionSelection.set(
        String(row.id),
        new SelectionModel<number>(true, []),
      );
    }
    return this.sessionSelection.get(String(row.id))!;
  }

  isSessionSelected(row: TemNccItem, index: number): boolean {
    return this.getSessionSelection(row).isSelected(index);
  }

  toggleSessionSelect(row: TemNccItem, index: number): void {
    this.getSessionSelection(row).toggle(index);
  }

  isAllSessionsSelected(row: TemNccItem): boolean {
    const sel = this.getSessionSelection(row);
    const total = row.sessions?.length ?? 0;
    return total > 0 && sel.selected.length === total;
  }

  isSomeSessionSelected(row: TemNccItem): boolean {
    const sel = this.getSessionSelection(row);
    return sel.selected.length > 0 && !this.isAllSessionsSelected(row);
  }

  toggleAllSessions(row: TemNccItem): void {
    if (this.isAllSessionsSelected(row)) {
      this.getSessionSelection(row).clear();
    } else {
      const allIndexes = (row.sessions ?? []).map((_, i) => i);
      this.getSessionSelection(row).select(...allIndexes);
    }
  }

  getSelectedSessions(row: TemNccItem): SessionItem[] {
    const selected = this.getSessionSelection(row).selected;
    return (row.sessions ?? []).filter((_, i) => selected.includes(i));
  }

  // ==================== ACTIONS ====================

  onInfo(item: TemNccItem): void {
    if (!item.sessions?.length && !this.loadingDetailIds.has(item.id)) {
      this.loadingDetailIds.add(item.id);

      const cached = this.detailCache.get(item.id);
      if (cached) {
        this.applyDetailToRow(item, cached);
        this.openSummaryDialog(item);
        return;
      }

      this.managerTemNccService.getPoImportTemDetail(item.id).subscribe({
        next: (detail) => {
          this.detailCache.set(item.id, detail);
          this.applyDetailToRow(item, detail);
          this.loadingDetailIds.delete(item.id);

          // lay lai item moi nhat tu dataSource sau khi apply
          const updated =
            this.dataSource.data.find((r) => r.id === item.id) ?? item;
          this.openSummaryDialog(updated);
        },
        error: () => {
          this.loadingDetailIds.delete(item.id);
          this.notificationService.error("Không thể tải chi tiết đơn hàng.");
        },
      });
      return;
    }

    this.openSummaryDialog(item);
  }

  onViewDetail(row: TemNccItem, session: SessionItem): void {
    this.router.navigate(["/approve-tem-ncc/approve-tem-ncc-detail"], {
      state: {
        data: row._raw,
        transactionId: session.transactionId,
      },
    });
  }

  onApprove(row: TemNccItem): void {
    const selected = this.getSelectedSessions(row);
    if (selected.length === 0) {
      return;
    }

    const dialogRef = this.dialog.open(DialogContentExampleDialogComponent, {
      width: "400px",
      data: {
        title: "Xác nhận phê duyệt",
        message: `Bạn có chắc chắn muốn phê duyệt ${selected.length} đợt nhập đã chọn?`,
        confirmText: "Phê duyệt",
        cancelText: "Hủy",
      },
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }
      this.callApproveApiForSessions(row, selected, "APPROVED");
    });
  }

  onReject(row: TemNccItem): void {
    const selected = this.getSelectedSessions(row);
    if (selected.length === 0) {
      return;
    }

    const dialogRef = this.dialog.open(DialogContentExampleDialogComponent, {
      width: "400px",
      data: {
        title: "Xác nhận từ chối",
        message: `Bạn có chắc chắn muốn từ chối ${selected.length} đợt nhập đã chọn?`,
        confirmText: "Từ chối",
        cancelText: "Hủy",
      },
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }
      this.callApproveApiForSessions(row, selected, "REJECTED");
    });
  }

  onDelete(item: TemNccItem): void {
    const dialogRef = this.dialog.open(DialogContentExampleDialogComponent, {
      width: "400px",
      data: {
        title: "Xác nhận xóa",
        message: "Bạn có chắc chắn muốn xóa?",
        confirmText: "Xóa",
        cancelText: "Hủy",
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        /* performDelete */
      }
    });
  }

  // ==================== PRIVATE ====================

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
    const idx = this.dataSource.data.findIndex((r) => r.id === row.id);
    if (idx >= 0) {
      const newData = [...this.dataSource.data];
      newData[idx] = { ...newData[idx], sessions, _raw: detail };
      this.dataSource.data = newData;
    }
  }

  private mapToTemNccItem(item: PoImportTem): TemNccItem {
    return {
      id: item.id,
      poCode: item.poNumber,
      vendorName: item.vendorName,
      arrivalDate: item.entryDate,
      createdDate: item.createdAt,
      createdBy: item.createdBy,
      warehouse: item.storageUnit ?? "",
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
      (sum, d) =>
        sum +
        (d.vendorTemDetails?.filter(
          (v) => (v.status ?? "").toUpperCase() === "PENDING",
        ).length ?? 0),
      0,
    );
    return {
      importDate: t.entryDate ?? t.createdAt,
      warehouse: t.storageUnit,
      warehouseType: "",
      status: t.status,
      totalQty, // so luong trong don
      totalScanQty: t.totalScanQuantity ?? 0, // so luong da scan thuc te
      itemCount,
      transactionId: t.id,
    };
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

  private callApproveApiForSessions(
    row: TemNccItem,
    sessions: SessionItem[],
    status: "APPROVED" | "REJECTED",
  ): void {
    const raw = row._raw;
    if (!raw) {
      this.notificationService.error("Không có dữ liệu đơn hàng.");
      return;
    }

    const now = new Date().toISOString();
    const poImportTemId = row.id; // dung row.id thay vi route param

    const calls = sessions
      .map((session) => {
        const transaction = raw.importVendorTemTransactions?.find(
          (t) => t.id === session.transactionId,
        );
        if (!transaction) {
          return null;
        }

        const approvePayload: ApproveVendorTemPayload = {
          id: session.transactionId,
          poNumber: raw.poNumber,
          vendorCode: raw.vendorCode ?? "",
          vendorName: raw.vendorName,
          entryDate: raw.entryDate ?? "",
          storageUnit: transaction.storageUnit ?? "",
          mappingConfig: transaction.mappingConfig ?? "",
          status,
          createdBy: transaction.createdBy ?? "",
          createdAt: transaction.createdAt ?? now,
          updatedBy: "",
          updatedAt: now,
          deletedBy: null as any,
          deletedAt: null as any,
          approver: raw.approver ?? "",
          poImportTemId,
        };

        // collect tat ca vendorTemDetails trong transaction nay
        const allDetails = (transaction.poDetails ?? []).flatMap((pd) =>
          (pd.vendorTemDetails ?? [])
            .filter((v) => (v.status ?? "").toUpperCase() === "PENDING")
            .map((v) => ({ v, poDetailId: pd.id })),
        );

        const detailPayloads: CreateVendorTemDetailPayload[] = allDetails
          .filter(({ v }) => !!v.id)
          .map(({ v, poDetailId }) => ({
            id: v.id,
            reelId: v.reelId ?? "",
            partNumber: v.partNumber ?? "",
            vendor: v.vendor ?? "",
            lot: v.lot ?? "",
            userData1: v.userData1 ?? "",
            userData2: v.userData2 ?? "",
            userData3: v.userData3 ?? "",
            userData4: v.userData4 ?? "",
            userData5: v.userData5 ?? "",
            initialQuantity: v.initialQuantity ?? 0,
            msdLevel: v.msdLevel ?? "",
            msdInitialFloorTime: "",
            msdBagSealDate: "",
            marketUsage: "",
            quantityOverride: 0,
            shelfTime: "",
            spMaterialName: "",
            warningLimit: "",
            maximumLimit: "",
            comments: "",
            warmupTime: "",
            storageUnit: v.storageUnit ?? "",
            subStorageUnit: "",
            locationOverride: "",
            expirationDate: v.expirationDate ?? "",
            manufacturingDate: v.manufacturingDate ?? "",
            partClass: "",
            sapCode: v.sapCode ?? "",
            vendorQrCode: v.vendorQrCode ?? "",
            status,
            createdBy: v.createdBy ?? "",
            createdAt: v.createdAt ?? now,
            updatedBy: "",
            updatedAt: now,
            poDetailId,
            importVendorTemTransactionsId: session.transactionId,
          }));

        return forkJoin({
          approve: this.managerTemNccService.approveImportVendorTemTransaction(
            session.transactionId,
            approvePayload,
          ),
          details:
            detailPayloads.length > 0
              ? this.managerTemNccService.batchUpdateVendorTemDetails(
                  detailPayloads,
                )
              : of(null),
        });
      })
      .filter(Boolean);

    if (!calls.length) {
      return;
    }

    this.isLoading = true;

    forkJoin(calls as any[]).subscribe({
      next: () => {
        this.isLoading = false;
        const msg =
          status === "APPROVED"
            ? "Phê duyệt thành công."
            : "Từ chối thành công.";
        this.notificationService.success(msg);
        this.getSessionSelection(row).clear();
        this.detailCache.delete(row.id);
        this.loadDetailIfNeeded(row);
      },
      error: () => {
        this.isLoading = false;
        const msg =
          status === "APPROVED" ? "Phê duyệt thất bại." : "Từ chối thất bại.";
        this.notificationService.error(msg);
      },
    });
  }
  private openSummaryDialog(item: TemNccItem): void {
    this.dialog.open(OrderSummaryDialogComponent, {
      width: "640px",
      maxWidth: "95vw",
      data: { item },
      panelClass: "summary-dialog-panel",
    });
  }
}
