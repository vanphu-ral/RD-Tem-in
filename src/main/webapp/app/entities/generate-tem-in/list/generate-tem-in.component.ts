import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ChangeDetectorRef,
} from "@angular/core";
import { CommonModule, DatePipe } from "@angular/common";
import { FormsModule } from "@angular/forms";

// Angular Material
import { MatTableModule, MatTableDataSource } from "@angular/material/table";
import { MatPaginator, MatPaginatorModule } from "@angular/material/paginator";
import { MatSort, MatSortModule } from "@angular/material/sort";
import { MatButtonModule } from "@angular/material/button";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatChipsModule } from "@angular/material/chips";
import { MatDividerModule } from "@angular/material/divider";
import { MatIconModule } from "@angular/material/icon";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";
import { MatDialog } from "@angular/material/dialog";

// Models and Services
import { ListRequestCreateTem } from "../models/list-request-create-tem.model";
import { GenerateTemInService } from "../service/generate-tem-in.service";
import { DialogContentExampleDialogComponent } from "./confirm-dialog/confirm-dialog.component";
import { PreviewOrderDialogComponent } from "./preview-order-dialog/preview-order-dialog.component";
import { AlertService } from "app/core/util/alert.service";

// Interfaces
interface TemMaterialItem {
  id: number;
  status: string;
  vendor: string;
  userData5: string;
  vendorName: string;
  createdDate: string; // LocalDate from backend as string
  createdBy: string;
  numberProduction: number;
  totalQuantity: number;
}

interface FilterOptions {
  status: string;
  vendor: string;
  userData5: string;
  createdDate: Date | null; // dùng Date để lọc ngày dễ hơn
  createdBy: string;
  numberProduction: string;
  totalQuantity: string;
}

@Component({
  selector: "jhi-generate-tem-in",
  standalone: false,
  templateUrl: "./generate-tem-in.component.html",
  styleUrls: ["./generate-tem-in.component.scss"],
})
export class GenerateTemInComponent implements OnInit, AfterViewInit {
  // Columns hiển thị
  displayedColumns: string[] = [
    "status",
    "userData5",
    "vendor",
    "vendorName",
    "createdDate",
    "createdBy",
    "numberProduction",
    "totalQuantity",
    "actions",
  ];
  statusOptions = ["Đã tạo mã QR", "Bản nháp"];
  filterValues = {
    status: "",
    vendor: "",
    vendorName: "",
    userData5: "",
    createdBy: "",
    createdDate: "",
  };

  dataSource = new MatTableDataSource<TemMaterialItem>([]);
  totalItems = 0;
  isLoading = false;

  filters: FilterOptions = {
    status: "",
    vendor: "",
    userData5: "",
    createdDate: null,
    createdBy: "",
    numberProduction: "",
    totalQuantity: "",
  };
  mobileItems: TemMaterialItem[] = [];
  // ViewChild
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private generateTemInService: GenerateTemInService,
    private dialog: MatDialog,
    private alertService: AlertService,
    private datePipe: DatePipe,
    private cdr: ChangeDetectorRef,
  ) {}

  // ================== LIFECYCLE ==================
  ngOnInit(): void {
    this.loadRequests();
  }

  ngAfterViewInit(): void {
    this.dataSource.filterPredicate = (
      item: TemMaterialItem,
      filterJson: string,
    ): boolean => {
      const f: FilterOptions = JSON.parse(filterJson);

      const okStatus =
        !f.status || item.status.toLowerCase().includes(f.status.toLowerCase());

      const okVendor =
        !f.vendor || item.vendor.toLowerCase().includes(f.vendor.toLowerCase());

      const okUserData5 =
        !f.userData5 ||
        item.userData5.toLowerCase().includes(f.userData5.toLowerCase());

      const okCreatedBy =
        !f.createdBy ||
        item.createdBy.toLowerCase().includes(f.createdBy.toLowerCase());

      const okNumberProduction =
        !f.numberProduction ||
        String(item.numberProduction).includes(f.numberProduction);

      const okTotal =
        !f.totalQuantity ||
        String(item.totalQuantity).includes(f.totalQuantity);
      const okDate =
        !f.createdDate || this.sameDate(item.createdDate, f.createdDate);

      return (
        okStatus &&
        okVendor &&
        okUserData5 &&
        okCreatedBy &&
        okNumberProduction &&
        okTotal &&
        okDate
      );
    };

    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.updateMobileItems();
  }
  get mobileDataSource(): TemMaterialItem[] {
    if (!this.dataSource) {
      return [];
    }

    const data = this.dataSource.filteredData || this.dataSource.data || [];

    // Nếu chưa có paginator, trả về tất cả data
    if (!this.paginator) {
      return data;
    }

    // Nếu có paginator, chỉ trả về data của trang hiện tại
    const startIndex = this.paginator.pageIndex * this.paginator.pageSize;
    const endIndex = startIndex + this.paginator.pageSize;

    return data.slice(startIndex, endIndex);
  }

  applyFilter(): void {
    this.dataSource.filterPredicate = (
      data: TemMaterialItem,
      filter: string,
    ) => {
      const filters = JSON.parse(filter);
      return (
        (filters.status === "" || data.status === filters.status) &&
        data.vendor.toLowerCase().includes(filters.vendor.toLowerCase()) &&
        data.vendorName
          .toLowerCase()
          .includes(filters.vendorName.toLowerCase()) &&
        data.userData5
          .toLowerCase()
          .includes(filters.userData5.toLowerCase()) &&
        data.createdBy.toLowerCase().includes(filters.createdBy.toLowerCase())
      );
    };

    this.dataSource.filter = JSON.stringify(this.filterValues);
    this.mobileItems = this.dataSource.filteredData;
    this.updateMobileItems();
  }
  applyDateFilter(): void {
    const selectedDate = this.filterValues.createdDate;
    if (selectedDate) {
      const formatted = this.datePipe.transform(selectedDate, "dd/MM/yyyy");
      this.dataSource.filterPredicate = (data, filter) => {
        const dataDate = this.datePipe.transform(
          data.createdDate,
          "dd/MM/yyyy",
        );
        return dataDate === formatted;
      };
      this.dataSource.filter = formatted ?? "";
    } else {
      this.dataSource.filter = "";
    }
  }

  clearFilters(): void {
    this.filters = {
      status: "",
      vendor: "",
      userData5: "",
      createdDate: null,
      createdBy: "",
      numberProduction: "",
      totalQuantity: "",
    };
    this.applyFilter();
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
      default:
        return {
          backgroundColor: "#E0E0E0",
          color: "#333",
          border: "1px solid #CCC",
        };
    }
  }

  onImport(): void {
    // console.log("Import functionality");
  }

  onEdit(item: TemMaterialItem): void {
    // console.log("Edit item:", item);
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
    const dialogRef = this.dialog.open(PreviewOrderDialogComponent, {
      width: "1000px",
      maxWidth: "90vw",
      data: {
        request: item,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      // Handle any actions after dialog closes if needed
      // console.log("Preview dialog closed");
    });
  }

  onPrint(item: TemMaterialItem): void {
    // console.log("Print item:", item);
  }

  private performDelete(item: TemMaterialItem): void {
    this.isLoading = true;
    this.generateTemInService.deleteRequest(item.id).subscribe({
      next: (response) => {
        // console.log(`Deleted request ${item.id}: ${response.message}`);
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
    this.generateTemInService.getAllRequests().subscribe({
      next: (response: ListRequestCreateTem[]) => {
        // console.log("Dữ liệu nhận từ gql: ", response);
        const data: TemMaterialItem[] = response.map(
          (item: ListRequestCreateTem) => ({
            id: item.id ?? 0,
            status: item.status ?? "",
            vendor: item.vendor ?? "",
            vendorName: item.vendorName ?? "",
            userData5: item.userData5 ?? "",
            createdDate: item.createdDate ?? "",
            createdBy: item.createdBy ?? "",
            numberProduction: item.numberProduction ?? 0,
            totalQuantity: item.totalQuantity ?? 0,
          }),
        );
        // console.log("Mapped data:", data);
        // Sort data by createdDate in descending order (newest first)
        data.sort((a, b) => {
          const dateA = new Date(a.createdDate);
          const dateB = new Date(b.createdDate);
          return dateB.getTime() - dateA.getTime();
        });
        this.dataSource.data = data;
        this.mobileItems = data;
        this.totalItems = response.length;
        this.isLoading = false;
        this.dataSource.paginator = this.paginator;
        this.dataSource.filter = JSON.stringify(this.filterValues);
        this.updateMobileItems();
        console.log("filteredData:", this.dataSource.filteredData);
        console.log("mobileDataSource:", this.mobileDataSource);
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error("Error loading requests:", error);
        console.error("Error details:", {
          message: error.message,
          status: error.status,
          statusText: error.statusText,
          url: error.url,
        });
        this.dataSource.data = [];
        this.mobileItems = [];
        this.totalItems = 0;
        this.isLoading = false;
        console.warn("Failed to load data from database.");
      },
    });
  }
  private updateMobileItems(): void {
    const data = this.dataSource.filteredData || this.dataSource.data || [];
    const startIndex =
      this.paginator?.pageIndex * this.paginator?.pageSize || 0;
    const endIndex = startIndex + (this.paginator?.pageSize || 25);
    this.mobileItems = data.slice(startIndex, endIndex);
    this.cdr.detectChanges();
  }
  private sameDate(a: Date | string, b: Date): boolean {
    const d1 = new Date(a);
    return (
      d1.getFullYear() === b.getFullYear() &&
      d1.getMonth() === b.getMonth() &&
      d1.getDate() === b.getDate()
    );
  }
}
