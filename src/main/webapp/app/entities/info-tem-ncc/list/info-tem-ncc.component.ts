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

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

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

  // ==================== PAGINATION HELPER ====================

  getRowIndex(indexOnPage: number): number {
    const pageIndex = this.paginator?.pageIndex ?? 0;
    const pageSize = this.paginator?.pageSize ?? this.pageSize;
    return pageIndex * pageSize + indexOnPage + 1;
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

  // ==================== ACTIONS ====================

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
