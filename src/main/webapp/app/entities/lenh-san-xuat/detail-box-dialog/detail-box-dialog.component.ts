import { Component, Inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from "@angular/material/dialog";
import { MatTableModule } from "@angular/material/table";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatPaginatorModule, PageEvent } from "@angular/material/paginator";
import { QRCodeComponent } from "angularx-qrcode";

export interface BoxDetailData {
  stt: number;
  maSanPham: string;
  lotNumber: string;
  vendor: string;
  tenSanPham: string;
  soLuongTrongThung: number;
  soLuongThung: number;
  soLuongSp: number;
  kho: string;
  serialBox: string;
}

export interface BoxInfoCard {
  label: string;
  value: string;
}

export interface BoxSubItem {
  stt: number;
  qrCode?: string;
  maThung: string;
  soLuong: number;
}

@Component({
  selector: "jhi-box-detail-dialog",
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatPaginatorModule,
    QRCodeComponent,
  ],
  templateUrl: "./detail-box-dialog.component.html",
  styleUrls: ["./detail-box-dialog.component.scss"],
})
export class DetailBoxDialogComponent implements OnInit {
  displayedColumns: string[] = ["stt", "maThung", "maThungCode", "soLuong"];

  infoCards: BoxInfoCard[] = [];
  boxSubItems: BoxSubItem[] = [];
  paginatedItems: BoxSubItem[] = [];

  // Pagination
  pageSize = 10;
  pageIndex = 0;
  totalItems = 0;

  // Summary
  soLuongThung: number = 0;
  tongSoLuong: number = 0;

  constructor(
    public dialogRef: MatDialogRef<DetailBoxDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: BoxDetailData,
  ) {}

  ngOnInit(): void {
    this.initializeInfoCards();
    this.generateBoxSubItems();
    this.updatePaginatedItems();
  }
  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.updatePaginatedItems();
  }

  onExport(): void {
    console.log("Xuất file chi tiết thùng");
    // Thực hiện logic xuất file Excel/CSV
  }

  onPrint(): void {
    console.log("In tem cho tất cả thùng");
    // Thực hiện logic in tem
  }

  onClose(): void {
    this.dialogRef.close();
  }
  private initializeInfoCards(): void {
    this.infoCards = [
      { label: "Lot Number", value: this.data.lotNumber },
      { label: "Vendor", value: this.data.vendor },
      { label: "Tên sản phẩm", value: this.data.tenSanPham },
      { label: "Kho", value: this.data.kho },
    ];
  }

  private generateBoxSubItems(): void {
    this.boxSubItems = [];

    // Mỗi BoxItem đã có serialBox, chỉ cần hiển thị lại
    const item: BoxSubItem = {
      stt: this.data.stt,
      maThung: this.data.serialBox,
      soLuong: this.data.soLuongTrongThung || 0,
      qrCode: this.data.serialBox, // nếu muốn hiển thị QR
    };

    this.boxSubItems.push(item);

    this.totalItems = this.boxSubItems.length;
    this.tongSoLuong = item.soLuong;
  }

  private generateBoxCode(index: number): string {
    // Tạo mã thùng theo format: B + timestamp + index
    const date = new Date();
    const year = date.getFullYear();
    const xuong = "01";
    const nganh = "04";
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const sequence = String(index).padStart(10, "0");

    return `B${xuong}${nganh}${year}${month}${day}${sequence}`;
  }

  private updatePaginatedItems(): void {
    const startIndex = this.pageIndex * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.paginatedItems = this.boxSubItems.slice(startIndex, endIndex);
  }
}
