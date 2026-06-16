import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ChangeDetectorRef,
} from "@angular/core";
import { DatePipe } from "@angular/common";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatDialog } from "@angular/material/dialog";

import { ListRequestCreateTem } from "../models/list-request-create-tem.model";
import { GenerateTemInService } from "../service/generate-tem-in.service";
import { DialogContentExampleDialogComponent } from "./confirm-dialog/confirm-dialog.component";
import { PreviewOrderDialogComponent } from "./preview-order-dialog/preview-order-dialog.component";
import { AlertService } from "app/core/util/alert.service";

interface TemMaterialItem {
  id: number;
  status: string;
  vendor: string;
  userData5: string;
  vendorName: string;
  createdDate: string;
  createdBy: string;
  numberProduction: number;
  totalQuantity: number;
  whsCode: string;
}

@Component({
  selector: "jhi-generate-tem-in",
  standalone: false,
  templateUrl: "./generate-tem-in.component.html",
  styleUrls: ["./generate-tem-in.component.scss"],
})
export class GenerateTemInComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = [
    "status",
    "userData5",
    "whsCode",
    "vendor",
    "vendorName",
    "createdDate",
    "createdBy",
    "numberProduction",
    "totalQuantity",
    "actions",
  ];
  statusOptions = ["Đã tạo mã QR", "Bản nháp", "chưa có PO"];
  filterValues = {
    status: "",
    vendor: "",
    vendorName: "",
    userData5: "",
    whsCode: "",
    createdBy: "",
    createdDate: null as Date | null,
  };

  dataSource = new MatTableDataSource<TemMaterialItem>([]);
  totalItems = 0;
  pageIndex = 0;
  pageSize = 25;
  isLoading = false;
  mobileItems: TemMaterialItem[] = [];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private generateTemInService: GenerateTemInService,
    private dialog: MatDialog,
    private alertService: AlertService,
    private datePipe: DatePipe,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.loadRequests();
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
  }

  get mobileDataSource(): TemMaterialItem[] {
    return this.dataSource.data ?? [];
  }

  applyFilter(): void {
    this.pageIndex = 0;
    this.loadRequests();
  }

  applyDateFilter(): void {
    this.pageIndex = 0;
    this.loadRequests();
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadRequests();
  }

  statusColor(status: string): { [key: string]: string } {
    const normalized = (status || "").toLowerCase().trim();
    switch (normalized) {
      case "bản nháp":
        return {
          backgroundColor: "#FFF9B0",
          color: "#7A6A00",
          border: "1px solid #F0E68C",
        };
      case "đã tạo mã qr":
      case "đã tạo mã":
      case "đã gen mã":
        return {
          backgroundColor: "#2be265ff",
          color: "#3F6D52",
          border: "1px solid #A3D9B8",
        };
      case "chưa có po":
        return {
          backgroundColor: "#E0F2FE",
          color: "#0369A1",
          border: "1px solid #7DD3FC",
        };
      default:
        return {
          backgroundColor: "#E0E0E0",
          color: "#333",
          border: "1px solid #CCC",
        };
    }
  }

  isReceivingDraft(item: TemMaterialItem): boolean {
    const status = (item.status ?? "").toLowerCase().trim();
    return status === "chưa có po" || (item.userData5 ?? "").trim() === "-";
  }

  getDetailRoute(item: TemMaterialItem): (string | number)[] {
    return ["receiving-supplies", item.id];
  }

  getDetailTooltip(item: TemMaterialItem): string {
    return "Tiếp tục nhập vật tư";
  }

  onDelete(item: TemMaterialItem): void {
    const dialogRef = this.dialog.open(DialogContentExampleDialogComponent, {
      width: "400px",
      data: {
        title: "Xác nhận xóa",
        message: "Bạn có chắc chắn muốn xóa yêu cầu tạo tem này?",
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

  onView(item: TemMaterialItem): void {
    this.dialog.open(PreviewOrderDialogComponent, {
      width: "1000px",
      maxWidth: "90vw",
      data: {
        request: item,
      },
    });
  }

  private performDelete(item: TemMaterialItem): void {
    this.isLoading = true;
    this.generateTemInService.deleteRequest(item.id).subscribe({
      next: () => {
        this.loadRequests();
      },
      error: (error) => {
        console.error("Error deleting request:", error);
        this.alertService.addAlert({
          type: "danger",
          message: `Lỗi khi xóa yêu cầu: ${error.message || "Không thể xóa yêu cầu này"}`,
          timeout: 5000,
          toast: true,
        });
      },
      complete: () => {
        this.isLoading = false;
      },
    });
  }

  private loadRequests(): void {
    this.isLoading = true;
    const createdDateStr = this.filterValues.createdDate
      ? (this.datePipe.transform(this.filterValues.createdDate, "yyyy-MM-dd") ??
        undefined)
      : undefined;
    const whsCode = this.filterValues.whsCode.trim() || undefined;
    const clientWhsFilter = Boolean(whsCode);

    this.generateTemInService
      .getAllRequests({
        status: this.filterValues.status || undefined,
        vendor: this.filterValues.vendor || undefined,
        vendorName: this.filterValues.vendorName || undefined,
        userData5: this.filterValues.userData5 || undefined,
        whsCode,
        createdBy: this.filterValues.createdBy || undefined,
        createdDate: createdDateStr,
        page: clientWhsFilter ? 0 : this.pageIndex,
        size: clientWhsFilter ? 5000 : this.pageSize,
      })
      .subscribe({
        next: (page) => {
          let data: TemMaterialItem[] = (page.content ?? []).map(
            (item: ListRequestCreateTem) => this.mapRequestItem(item),
          );
          let total = page.totalElements ?? 0;

          if (clientWhsFilter) {
            const start = this.pageIndex * this.pageSize;
            total = data.length;
            data = data.slice(start, start + this.pageSize);
          }

          this.dataSource.data = data;
          this.mobileItems = data;
          this.totalItems = total;
          this.pageIndex = clientWhsFilter
            ? this.pageIndex
            : (page.page ?? this.pageIndex);
          this.pageSize = clientWhsFilter
            ? this.pageSize
            : (page.size ?? this.pageSize);
          this.isLoading = false;
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error("Error loading requests:", error);
          this.dataSource.data = [];
          this.mobileItems = [];
          this.totalItems = 0;
          this.isLoading = false;
        },
      });
  }

  private mapRequestItem(item: ListRequestCreateTem): TemMaterialItem {
    return {
      id: item.id ?? 0,
      status: item.status ?? "",
      vendor: item.vendor ?? "",
      vendorName: item.vendorName ?? "",
      userData5: item.userData5 ?? "",
      createdDate: item.createdDate ?? "",
      createdBy: item.createdBy ?? "",
      numberProduction: item.numberProduction ?? 0,
      totalQuantity: item.totalQuantity ?? 0,
      whsCode: item.whsCode ?? "",
    };
  }
}
