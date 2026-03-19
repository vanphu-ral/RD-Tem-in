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
import { SelectionModel } from "@angular/cdk/collections";

// ==================== INTERFACES ====================

export interface TemNccItem {
  id: number;
  poCode: string;
  vendorName: string;
  arrivalDate: string; // ISO date string
  createdDate: string; // ISO datetime string
  createdBy: string;
  warehouse: string;
  status: string;
  sessions?: SessionItem[];
}

export interface SessionItem {
  importDate: string;
  warehouse: string;
  warehouseType: string;
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
    "warehouse",
    "status",
  ];

  dataSource = new MatTableDataSource<TemNccItem>([]);
  totalItems = 0;
  pageSize = 10;
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
  // key: rowId_sessionIndex (global), value: checked
  sessionSelection = new Map<string, SelectionModel<number>>();
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  readonly sessionPageSize = 5;
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
  ) {}

  // ==================== LIFECYCLE ====================

  ngOnInit(): void {
    this.loadData();
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.dataSource.filterPredicate = this.buildFilterPredicate();
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
    }
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

  // tạo hoặc lấy SelectionModel cho từng row
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

  onApprove(row: TemNccItem): void {
    const selected = this.getSelectedSessions(row);
    if (selected.length === 0) {
      return;
    }
    // TODO: gọi service phê duyệt với selected
    console.log("Approve:", selected);
  }

  onReject(row: TemNccItem): void {
    const selected = this.getSelectedSessions(row);
    if (selected.length === 0) {
      return;
    }
    // TODO: gọi service từ chối với selected
    console.log("Reject:", selected);
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
        this.performDelete(item);
      }
    });
  }

  // ==================== PRIVATE ====================

  private performDelete(item: TemNccItem): void {
    // Call service to delete, then reload
    // this.service.delete(item.id).subscribe(() => this.loadData());
  }

  private loadData(): void {
    // Replace with real service call, e.g.:
    // this.service.getAll().subscribe(data => { this.dataSource.data = data; this.totalItems = data.length; });

    // Mock data matching the screenshot
    const mock: TemNccItem[] = [
      {
        id: 1,
        poCode: "124578",
        vendorName: "SS",
        arrivalDate: "2026-01-23",
        createdDate: "2026-02-27T18:33:17",
        createdBy: "rd00013",
        warehouse: "RD-01",
        status: "Đang nhập",
        sessions: [
          {
            importDate: "2024-03-15",
            warehouse: "RD-LED-05",
            warehouseType: "Thành phẩm",
            totalQty: 2000,
            itemCount: 2,
          },
          {
            importDate: "2024-03-16",
            warehouse: "RD-LED-04",
            warehouseType: "Thành phẩm",
            totalQty: 2000,
            itemCount: 2,
          },
        ],
      },
      {
        id: 2,
        poCode: "125845",
        vendorName: "Thanh An",
        arrivalDate: "2026-01-23",
        createdDate: "2026-02-27T18:33:17",
        createdBy: "rd02358",
        warehouse: "RD-01",
        status: "Đang nhập",
        sessions: [
          {
            importDate: "2024-03-15",
            warehouse: "Kho A",
            warehouseType: "Thành phẩm",
            totalQty: 2000,
            itemCount: 2,
          },
        ],
      },
      {
        id: 3,
        poCode: "125482",
        vendorName: ".",
        arrivalDate: "2026-01-23",
        createdDate: "2026-02-27T18:33:17",
        createdBy: "rd05125",
        warehouse: "RD-01",
        status: "Đang nhập",
        sessions: [
          {
            importDate: "2024-03-15",
            warehouse: "Kho A",
            warehouseType: "Thành phẩm",
            totalQty: 2000,
            itemCount: 2,
          },
        ],
      },
      {
        id: 4,
        poCode: "125482",
        vendorName: ".",
        arrivalDate: "2026-01-23",
        createdDate: "2026-02-27T18:33:17",
        createdBy: "rd01584",
        warehouse: "RD-01",
        status: "Chờ duyệt",
        sessions: [
          {
            importDate: "2024-03-15",
            warehouse: "Kho A",
            warehouseType: "Thành phẩm",
            totalQty: 2000,
            itemCount: 2,
          },
        ],
      },
      {
        id: 5,
        poCode: "124548",
        vendorName: ".",
        arrivalDate: "2026-01-23",
        createdDate: "2026-02-27T18:33:17",
        createdBy: "rd01548",
        warehouse: "RD-01",
        status: "Chờ duyệt",
        sessions: [
          {
            importDate: "2024-03-15",
            warehouse: "Kho A",
            warehouseType: "Thành phẩm",
            totalQty: 2000,
            itemCount: 2,
          },
        ],
      },
      {
        id: 6,
        poCode: "125482",
        vendorName: ".",
        arrivalDate: "2026-01-23",
        createdDate: "2026-02-27T18:33:17",
        createdBy: "rd02452",
        warehouse: "RD-01",
        status: "Chờ duyệt",
        sessions: [
          {
            importDate: "2024-03-15",
            warehouse: "Kho A",
            warehouseType: "Thành phẩm",
            totalQty: 2000,
            itemCount: 2,
          },
        ],
      },
      {
        id: 7,
        poCode: "12548",
        vendorName: ".",
        arrivalDate: "2026-01-23",
        createdDate: "2026-02-27T18:33:17",
        createdBy: "rd01548",
        warehouse: "RD-02",
        status: "Đã phê duyệt",
        sessions: [
          {
            importDate: "2024-03-15",
            warehouse: "Kho A",
            warehouseType: "Thành phẩm",
            totalQty: 2000,
            itemCount: 2,
          },
        ],
      },
      {
        id: 8,
        poCode: "12548",
        vendorName: ".",
        arrivalDate: "2026-01-23",
        createdDate: "2026-02-27T18:33:17",
        createdBy: "rd02462",
        warehouse: "RD-01",
        status: "Đã phê duyệt",
        sessions: [
          {
            importDate: "2024-03-15",
            warehouse: "Kho A",
            warehouseType: "Thành phẩm",
            totalQty: 2000,
            itemCount: 2,
          },
        ],
      },
      {
        id: 9,
        poCode: "35659",
        vendorName: ".",
        arrivalDate: "2026-01-23",
        createdDate: "2026-02-27T18:33:17",
        createdBy: "rd04574",
        warehouse: "RD-01",
        status: "Đã gửi panacim",
        sessions: [
          {
            importDate: "2024-03-15",
            warehouse: "Kho A",
            warehouseType: "Thành phẩm",
            totalQty: 2000,
            itemCount: 2,
          },
        ],
      },
      {
        id: 10,
        poCode: "35974",
        vendorName: ".",
        arrivalDate: "2026-01-23",
        createdDate: "2026-02-27T18:33:17",
        createdBy: "rd05714",
        warehouse: "RD-01",
        status: "Đã gửi panacim",
        sessions: [
          {
            importDate: "2024-03-15",
            warehouse: "Kho A",
            warehouseType: "Thành phẩm",
            totalQty: 2000,
            itemCount: 2,
          },
        ],
      },
    ];

    this.dataSource.data = mock;
    this.totalItems = mock.length;
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
        match(item.warehouse, f.warehouse) &&
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
}
