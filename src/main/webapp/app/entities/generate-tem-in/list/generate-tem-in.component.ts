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

  constructor(
    private generateTemInService: GenerateTemInService,
    private dialog: MatDialog,
    private alertService: AlertService,
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

  statusColor(status: string): "primary" | "accent" | "warn" {
    switch ((status || "").toLowerCase()) {
      case "bản nháp":
        return "primary";
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

  onImport(): void {
    console.log("Import functionality");
  }

  onEdit(item: TemMaterialItem): void {
    console.log("Edit item:", item);
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
      console.log("Preview dialog closed");
    });
  }

  onPrint(item: TemMaterialItem): void {
    console.log("Print item:", item);
  }

  private performDelete(item: TemMaterialItem): void {
    this.isLoading = true;
    this.generateTemInService.deleteRequest(item.id).subscribe({
      next: () => {
        console.log(`Successfully deleted request ${item.id}`);
        // Reload the data after successful deletion
        this.loadRequests();
      },
      error: (error) => {
        console.error("Error deleting request:", error);
        this.isLoading = false;
        this.alertService.addAlert({
          type: "danger",
          message: `Lỗi khi xóa yêu cầu: ${error.message || "Không thể xóa yêu cầu này"}`,
          timeout: 5000,
          toast: true,
        });
      },
    });
  }

  private loadRequests(): void {
    this.isLoading = true;
    this.generateTemInService.getAllRequests().subscribe({
      next: (response: ListRequestCreateTem[]) => {
        console.log("Dữ liệu nhận từ gql: ", response);
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
        this.dataSource.data = [];
        this.totalItems = 0;
        this.isLoading = false;
        console.warn("Failed to load data from database.");
      },
    });
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
