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
  subItems: BoxSubItem[];
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

  pageSize = 10;
  pageIndex = 0;
  totalItems = 0;

  soLuongThung: number = 0;
  tongSoLuong: number = 0;

  constructor(
    public dialogRef: MatDialogRef<DetailBoxDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: BoxDetailData,
  ) {}

  ngOnInit(): void {
    this.initializeInfoCards();
    this.loadBoxSubItems();
    this.calculateSummary();
    this.updatePaginatedItems();
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.updatePaginatedItems();
  }

  onExport(): void {
    console.log("Xuất file chi tiết thùng");
  }

  onPrint(): void {
    console.log("In tem cho tất cả thùng");
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

  //  HÀM MỚI: Load từ data.subItems
  private loadBoxSubItems(): void {
    // Sử dụng subItems từ data đã truyền vào
    this.boxSubItems = this.data.subItems || [];
    this.totalItems = this.boxSubItems.length;

    console.log(" Loaded subItems:", this.boxSubItems.length);
    console.log(" SubItems data:", this.boxSubItems);
  }

  //  HÀM MỚI: Tính tổng
  private calculateSummary(): void {
    this.soLuongThung = this.boxSubItems.length;
    this.tongSoLuong = this.boxSubItems.reduce(
      (sum, item) => sum + (item.soLuong || 0),
      0,
    );

    console.log(" Số lượng thùng:", this.soLuongThung);
    console.log(" Tổng số lượng:", this.tongSoLuong);
  }

  private updatePaginatedItems(): void {
    const startIndex = this.pageIndex * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.paginatedItems = this.boxSubItems.slice(startIndex, endIndex);
  }
}
