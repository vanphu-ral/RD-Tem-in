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
import { MatSnackBar } from "@angular/material/snack-bar";
import * as XLSX from "xlsx";
import { ProductionOrder } from "../add-new/add-new-lenh-san-xuat.component";

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
  ghiChu: string;
  isBtp?: boolean;
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
  @ViewChildren("qrElem", { read: ElementRef })
  qrElements!: QueryList<ElementRef<HTMLElement>>;

  infoCards: BoxInfoCard[] = [];
  boxSubItems: BoxSubItem[] = [];
  paginatedItems: BoxSubItem[] = [];

  pageSize = 10;
  pageIndex = 0;
  totalItems = 0;

  soLuongThung: number = 0;
  tongSoLuong: number = 0;

  productionOrders: ProductionOrder[] = [];
  constructor(
    public dialogRef: MatDialogRef<DetailBoxDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: BoxDetailData,
    private snackBar: MatSnackBar,
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
    setTimeout(() => {
      this._exportPdfInternal();
    }, 300);
  }

  exportExcelFromDialog(useCurrentPage = false): void {
    const sourceItems: BoxSubItem[] = useCurrentPage
      ? this.paginatedItems
      : this.boxSubItems || [];

    if (!sourceItems || sourceItems.length === 0) {
      this.snackBar.open("Không có dữ liệu để xuất Excel", "Đóng", {
        duration: 3000,
      });
      return;
    }

    // Header (kiểu rõ ràng)
    const headers: string[] = [
      "NumberOfPlanning",
      "ItemCode",
      "ProductName",
      "SapWo",
      "Version",
      "TimeRecieved",
      "ReelID",
      "PartNumber",
      "Vendor",
      "QuantityOfPackage",
      "MFGDate",
      "ProductionShilt",
      "OpName",
      "Comments",
      "Comments2",
      "StorageUnit",
      "TP/NK",
      "Rank",
      "QrCode",
    ];

    // Lấy thông tin chung
    const po = (this as any).productionOrders?.[0] ?? null;
    const version = po?.version ?? "";
    const partNumber = this.data?.maSanPham ?? "";
    const productName = this.data?.tenSanPham ?? "";
    const lotNumber = this.data?.lotNumber ?? "";
    const vendor = this.data?.vendor ?? "";
    const serialBox = this.data?.serialBox ?? "";
    const storageFromBox = this.data?.kho ?? "";

    // Map rows với kiểu rõ ràng (mỗi row là mảng string|number)
    const rows: (string | number)[][] = sourceItems.map(
      (it: BoxSubItem): (string | number)[] => {
        const reelId = String(it.maThung ?? "");
        const qrPayload =
          it.qrCode && String(it.qrCode).trim() ? String(it.qrCode) : reelId;

        const numberOfPlanning = "";
        const itemCode = String(partNumber || (po?.maSAP ?? ""));
        const productNameRow = String(productName || (po?.tenHangHoa ?? ""));
        const sapWo = String(po?.maLenhSanXuat ?? this.data?.lotNumber ?? "");
        const timeReceived = String(po?.manufacturingDate ?? "");
        const partNum = `${itemCode}${String(version ?? "") ? "_V" + String(version ?? "") : ""}`;
        const vendorRow = String(vendor || (po?.vendor ?? ""));
        const quantityOfPackage: string | number =
          po?.tongSoLuong != null ? String(po.tongSoLuong) : "";
        const mfgDate = String(po?.manufacturingDate ?? "");
        const productionShift = String(po?.to ?? "");
        const opName = "";
        const comments = String(this.data?.ghiChu ?? "");
        const comments2 = "";
        const storageUnit = String(storageFromBox || "");
        const tpNk = "";
        const rank = "";
        const qrCodeField = qrPayload;

        return [
          numberOfPlanning,
          itemCode,
          productNameRow,
          sapWo,
          String(version ?? ""),
          timeReceived,
          reelId,
          partNum,
          vendorRow,
          quantityOfPackage,
          mfgDate,
          productionShift,
          opName,
          comments,
          comments2,
          storageUnit,
          tpNk,
          rank,
          qrCodeField,
        ];
      },
    );

    // Tạo AOА data với kiểu rõ ràng và ép kiểu khi gọi thư viện
    const aoaData: (string | number)[][] = [headers, ...rows];

    // thay cho: XLSX.utils.aoa_to_sheet(aoaData as XLSX.AOA);
    const worksheet: XLSX.WorkSheet = XLSX.utils.aoa_to_sheet(aoaData as any);

    const workbook: XLSX.WorkBook = {
      Sheets: { BoxDetail: worksheet },
      SheetNames: ["BoxDetail"],
    };

    const fileName = `BoxDetail_${serialBox || "export"}_${new Date().toISOString().slice(0, 10)}.xlsx`;
    XLSX.writeFile(workbook, fileName);
  }
  private getDataUrlFromRenderedImg(index: number): string | null {
    // Lấy ElementRef đã được gõ kiểu
    const qrArray = this.qrElements.toArray();
    const qrElem = qrArray[index];
    if (!qrElem) {
      return null;
    }

    // nativeElement là HTMLElement; querySelector trả về Element | null
    const native = qrElem.nativeElement as HTMLElement;
    const canvas = native.querySelector("canvas") as HTMLCanvasElement | null;
    if (!canvas) {
      return null;
    }

    // toDataURL trả về string — an toàn để trả về
    const dataUrl = canvas.toDataURL("image/png");
    return dataUrl;
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

  private _exportPdfInternal(): void {
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
        continue;
      }

      if (y + qrSize + margin > pageHeight) {
        doc.addPage();
        x = margin;
        y = margin;
      }

      doc.addImage(dataUrl, "PNG", x, y, qrSize, qrSize);

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
}
