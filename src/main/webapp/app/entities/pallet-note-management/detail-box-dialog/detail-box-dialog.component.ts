import {
  Component,
  ElementRef,
  Inject,
  OnInit,
  QueryList,
  ViewChildren,
} from "@angular/core";
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
import jsPDF from "jspdf";

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
  isMobile = false;
  @ViewChildren("qrWrap", { read: ElementRef }) qrWraps!: QueryList<ElementRef>;

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
    this.isMobile = window.innerWidth <= 768;
    window.addEventListener("resize", () => {
      this.isMobile = window.innerWidth <= 768;
    });
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

  exportQRCodesPdfCurrentPage(): void {
    const items = this.paginatedItems;
    if (!items.length) {
      return;
    }

    const doc = new jsPDF({
      unit: "mm",
      format: "a4",
      orientation: "portrait",
    });
    const pageWidth = 210,
      pageHeight = 297,
      margin = 10;
    const cols = 5,
      gap = 6;
    const contentWidth = pageWidth - margin * 2;
    const qrSize = (contentWidth - gap * (cols - 1)) / cols;
    const rowHeight = qrSize + 8;

    let x = margin,
      y = margin;

    for (let i = 0; i < items.length; i++) {
      const dataUrl = this.getDataUrlFromRenderedImg(i);
      if (!dataUrl) {
        console.warn("Không tìm thấy img QR cho item index", i);
        continue;
      }

      if (y + qrSize + margin > pageHeight) {
        doc.addPage();
        x = margin;
        y = margin;
      }

      doc.addImage(dataUrl, "PNG", x, y, qrSize, qrSize);

      // label
      const label = items[i].maThung ?? "";
      doc.setFontSize(8);
      const textX = x + Math.max(0, (qrSize - doc.getTextWidth(label)) / 2);
      doc.text(label, textX, y + qrSize + 4);

      x += qrSize + gap;
      if (i % cols === cols - 1) {
        x = margin;
        y += rowHeight;
      }
    }

    doc.save(`qr-boxes-${new Date().toISOString().slice(0, 10)}.pdf`);
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

  private getDataUrlFromRenderedImg(indexInPage: number): string | null {
    const wraps = this.qrWraps.toArray();
    const wrap = wraps[indexInPage];
    if (!wrap) {
      return null;
    }
    const img = wrap.nativeElement.querySelector(
      "img",
    ) as HTMLImageElement | null;
    if (img && img.src && img.src.startsWith("data:")) {
      return img.src;
    }
    return null;
  }
}
