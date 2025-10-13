import { Component, OnInit, AfterViewInit, ViewChild } from "@angular/core";
import { CommonModule } from "@angular/common";
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

// Models and Services
import { ListRequestCreateTem } from "../models/list-request-create-tem.model";
import { GenerateTemInService } from "../service/generate-tem-in.service";

// Interfaces
interface TemMaterialItem {
  id: number;
  status: string;
  vendor: string;
  userData5: string;
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
    "vendor",
    "userData5",
    "createdDate",
    "createdBy",
    "numberProduction",
    "totalQuantity",
    "actions",
  ];

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

  // ViewChild
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private generateTemInService: GenerateTemInService) {}

  // ================== LIFECYCLE ==================
  ngOnInit(): void {
    this.loadRequests();
  }

  ngAfterViewInit(): void {
    // Filter đa trường
    this.dataSource.filterPredicate = (
      item: TemMaterialItem,
      filterJson: string,
    ): boolean => {
      const f: FilterOptions = JSON.parse(filterJson);

      // status
      const okStatus =
        !f.status || item.status.toLowerCase().includes(f.status.toLowerCase());

      // vendor
      const okVendor =
        !f.vendor || item.vendor.toLowerCase().includes(f.vendor.toLowerCase());

      // userData5
      const okUserData5 =
        !f.userData5 ||
        item.userData5.toLowerCase().includes(f.userData5.toLowerCase());

      // createdBy
      const okCreatedBy =
        !f.createdBy ||
        item.createdBy.toLowerCase().includes(f.createdBy.toLowerCase());

      // numberProduction
      const okNumberProduction =
        !f.numberProduction ||
        String(item.numberProduction).includes(f.numberProduction);

      // totalQuantity
      const okTotal =
        !f.totalQuantity ||
        String(item.totalQuantity).includes(f.totalQuantity);

      // createdDate (so sánh theo ngày, bỏ time)
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

    // Sort + Paginator cho client-side
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  applyFilters(): void {
    this.dataSource.filter = JSON.stringify(this.filters);
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
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
    this.applyFilters();
  }

  // ================== STATUS CHIP ==================
  statusColor(status: string): "primary" | "accent" | "warn" {
    switch ((status || "").toLowerCase()) {
      case "bản nháp":
      case "mới":
      case "new":
        return "primary";
      case "đang xử lý":
      case "processing":
        return "accent";
      case "đã nhập":
      case "hoàn tất":
      case "done":
        return "primary";
      case "lỗi":
      case "error":
        return "warn";
      default:
        return "accent";
    }
  }

  // ================== ACTIONS ==================
  onImport(): void {
    console.log("Import functionality");
  }

  onEdit(item: TemMaterialItem): void {
    console.log("Edit item:", item);
  }

  onDelete(item: TemMaterialItem): void {
    console.log("Delete item:", item);
  }

  onView(item: TemMaterialItem): void {
    console.log("View item:", item);
  }

  onPrint(item: TemMaterialItem): void {
    console.log("Print item:", item);
  }

  // ================== DATA LOADING (private) ==================
  private loadRequests(): void {
    this.isLoading = true;
    console.log("Loading requests from API...");

    this.generateTemInService.getAllRequests().subscribe({
      next: (response: ListRequestCreateTem[]) => {
        console.log("API Response received:", response);
        const data: TemMaterialItem[] = response.map(
          (item: ListRequestCreateTem) => ({
            id: item.id ?? 0,
            status: item.status ?? "",
            vendor: item.vendor ?? "",
            userData5: item.userData5 ?? "",
            createdDate: item.createdDate ?? "",
            createdBy: item.createdBy ?? "",
            numberProduction: item.numberProduction ?? 0,
            totalQuantity: item.totalQuantity ?? 0,
          }),
        );
        console.log("Mapped data:", data);
        this.dataSource.data = data;
        this.totalItems = response.length;
        this.isLoading = false;
      },
      error: (error) => {
        console.error("Error loading requests:", error);
        console.error("Error details:", {
          message: error.message,
          status: error.status,
          statusText: error.statusText,
          url: error.url,
        });
        // Handle database errors properly - show empty state with error message
        this.dataSource.data = [];
        this.totalItems = 0;
        this.isLoading = false;
        // TODO: Show user-friendly error message in UI
        console.warn(
          "Failed to load data from database. Please check your connection and try again.",
        );
      },
    });
  }

  // ================== HELPERS (private sau public) ==================
  private sameDate(a: Date | string, b: Date): boolean {
    const d1 = new Date(a);
    return (
      d1.getFullYear() === b.getFullYear() &&
      d1.getMonth() === b.getMonth() &&
      d1.getDate() === b.getDate()
    );
  }
}
