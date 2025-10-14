import { Component, Inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from "@angular/material/dialog";
import { MatButtonModule } from "@angular/material/button";
import { MatTableModule, MatTableDataSource } from "@angular/material/table";
import { MatCardModule } from "@angular/material/card";
import { MatChipsModule } from "@angular/material/chips";
import { MatIconModule } from "@angular/material/icon";
import { MatDividerModule } from "@angular/material/divider";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";

import { ListRequestCreateTem } from "../../models/list-request-create-tem.model";
import { ListProductOfRequest } from "../../models/list-product-of-request.model";
import { GenerateTemInService } from "../../service/generate-tem-in.service";

export interface PreviewOrderDialogData {
  request: ListRequestCreateTem;
}

@Component({
  selector: "jhi-preview-order-dialog",
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatTableModule,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatDividerModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: "./preview-order-dialog.component.html",
  styleUrls: ["./preview-order-dialog.component.scss"],
})
export class PreviewOrderDialogComponent implements OnInit {
  productsDataSource = new MatTableDataSource<ListProductOfRequest>([]);
  displayedColumns: string[] = [
    "sapCode",
    "partNumber",
    "lot",
    "initialQuantity",
    "temQuantity",
    "vendor",
    "expirationDate",
    "manufacturingDate",
  ];

  isLoading = false;
  error: string | null = null;

  constructor(
    public dialogRef: MatDialogRef<PreviewOrderDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PreviewOrderDialogData,
    private generateTemInService: GenerateTemInService,
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  onClose(): void {
    this.dialogRef.close();
  }

  getStatusColor(status: string): string {
    switch ((status ?? "").toLowerCase()) {
      case "bản nháp":
      case "draft":
        return "primary";
      case "đã nhập":
      case "hoàn tất":
      case "done":
        return "accent";
      case "lỗi":
      case "error":
        return "warn";
      default:
        return "basic";
    }
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) {
      return "";
    }
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString("vi-VN", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
      });
    } catch {
      return dateString;
    }
  }

  getTotalProducts(): number {
    return this.productsDataSource.data.length;
  }

  getTotalQuantity(): number {
    return this.productsDataSource.data.reduce(
      (sum, product) => sum + (product.temQuantity ?? 0),
      0,
    );
  }

  private loadProducts(): void {
    if (!this.data.request.id) {
      this.error = "Không tìm thấy lệnh tạo TEM yêu cầu";
      return;
    }

    this.isLoading = true;
    this.error = null;

    this.generateTemInService
      .getProductsByRequestId(this.data.request.id)
      .subscribe({
        next: (products) => {
          this.productsDataSource.data = products;
          this.isLoading = false;
        },
        error: (error) => {
          console.error("Error loading products:", error);
          this.error = "Không thể tải danh sách sản phẩm";
          this.isLoading = false;
        },
      });
  }
}
