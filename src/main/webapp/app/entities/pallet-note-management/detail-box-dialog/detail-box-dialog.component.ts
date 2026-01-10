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
import { PlanningWorkOrderService } from "../service/planning-work-order.service";
import QRCode from "@zxing/library/esm/core/qrcode/encoder/QRCode";

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
  productionOrders?: any[];
  po?: any;
}

export interface BoxInfoCard {
  label: string;
  value: string;
}

export interface BoxSubItem {
  id?: number;
  stt: number;
  qrCode?: string;
  maThung: string;
  soLuong: number;

  reelID?: string;
  partNumber?: string;
  initialQuantity?: number;
  manufacturingDate?: string;
  expirationDate?: string;
  userData1?: string; // NumberOfPlanning
  userData2?: string;
  userData3?: string; // ProductName
  userData4?: string; // ItemCode
  userData5?: string; // SapWo
  vendor?: string;
  storageUnit?: string;
  TPNK?: string;
  rank?: string;
  note?: string; // Comments2
  comments?: string; // Comments
  qrCodeFull?: string;
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
  @ViewChildren("qrRef", { read: ElementRef })
  qrElements!: QueryList<ElementRef>;
  // qrElements!: QueryList<ElementRef<HTMLElement>>;
  po: any = null;
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
    private planningService: PlanningWorkOrderService,
  ) {}

  ngOnInit(): void {
    this.isMobile = window.innerWidth <= 768;
    window.addEventListener("resize", () => {
      this.isMobile = window.innerWidth <= 768;
    });
    this.boxSubItems = this.data?.subItems ?? [];
    this.paginatedItems = [...this.boxSubItems];
    this.po = this.data?.po ?? null;
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
    // NOTE: đảm bảo boxSubItems đã được "enriched" và có kiểu đầy đủ BoxSubItemFull
    const sourceItems = (
      useCurrentPage ? (this.paginatedItems ?? []) : (this.boxSubItems ?? [])
    ) as BoxSubItem[];

    if (!sourceItems || sourceItems.length === 0) {
      this.snackBar.open("Không có dữ liệu để xuất Excel", "Đóng", {
        duration: 3000,
      });
      return;
    }

    const headers: string[] = [
      "NumberOfPlanning",
      "ItemCode",
      "ProductName",
      "SapWo",
      "Lot",
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

    const po = this.po ?? null;
    const version = this.toStr(po?.version ?? "");
    const itemCodeFromDialog = this.toStr(
      this.data?.maSanPham ?? po?.maSAP ?? "",
    );
    const productNameFromDialog = this.toStr(
      this.data?.tenSanPham ?? po?.tenHangHoa ?? "",
    );
    const sapWo = this.toStr(po?.maLenhSanXuat ?? this.data?.lotNumber ?? "");
    const storageUnitFromDialog = this.toStr(
      this.data?.kho ?? po?.storage_code ?? "",
    );
    const vendorFromDialog = this.toStr(
      this.data?.vendor ?? po?.vendor ?? "RD",
    );
    const serialBox = this.toStr(this.data?.serialBox ?? "");

    // rows có kiểu rõ ràng (string|number)[][]
    const rows: (string | number)[][] = sourceItems.map((it: BoxSubItem) => {
      // Lấy giá trị an toàn, ép kiểu rõ ràng
      const numberOfPlanning = this.toStr(
        it.userData1 ?? this.data?.soLuongSp ?? po?.tongSoLuong ?? "",
      );
      const itemCode =
        itemCodeFromDialog || this.toStr(it.userData4 ?? po?.maSAP ?? "");
      const productName =
        productNameFromDialog ||
        this.toStr(it.userData3 ?? po?.tenHangHoa ?? "");
      const lot = this.toStr(this.data?.lotNumber ?? "");
      const timeReceived = this.toStr(
        it.manufacturingDate ?? po?.manufacturingDate ?? "",
      );
      const reelId = this.toStr(it.reelID ?? it.maThung ?? "");
      const partNumber = this.toStr(
        it.partNumber ??
          (itemCode ? `${itemCode}${version ? "_V" + version : ""}` : ""),
      );
      const vendorRow = this.toStr(it.vendor ?? vendorFromDialog);
      const quantityOfPackage = this.toNum(
        it.initialQuantity ?? it.soLuong ?? 0,
      );
      const mfgDate = this.toStr(
        it.manufacturingDate ?? po?.manufacturingDate ?? "",
      );
      const productionShift = this.toStr(po?.to ?? "");
      const comments = this.toStr(it.comments ?? this.data?.ghiChu ?? "");
      const comments2 = this.toStr(it.note ?? "");
      const storageUnit = this.toStr(
        it.storageUnit ?? storageUnitFromDialog ?? "",
      );
      const tpNk = this.toStr(it.TPNK ?? "");
      const rank = this.toStr(it.rank ?? "");
      const qr = this.toStr(it.qrCodeFull ?? it.qrCode ?? "");

      const row: (string | number)[] = [
        numberOfPlanning,
        itemCode,
        productName,
        sapWo,
        lot,
        version,
        timeReceived,
        reelId,
        partNumber,
        vendorRow,
        quantityOfPackage,
        mfgDate,
        productionShift,
        "", // OpName (giữ rỗng nếu không có)
        comments,
        comments2,
        storageUnit,
        tpNk,
        rank,
        qr,
      ];

      return row;
    });

    const aoaData: (string | number)[][] = [headers, ...rows];
    const worksheet: XLSX.WorkSheet = XLSX.utils.aoa_to_sheet(aoaData);
    const workbook: XLSX.WorkBook = {
      Sheets: { BoxDetail: worksheet },
      SheetNames: ["BoxDetail"],
    };
    const fileName = `BoxDetail_${serialBox || "export"}_${new Date().toISOString().slice(0, 10)}.xlsx`;
    XLSX.writeFile(workbook, fileName);

    const payload = sourceItems
      .filter((it) => it.id !== undefined && it.id !== null)
      .map((it) => ({ id: it.id as number, print_status: true }));
    if (payload.length > 0) {
      this.planningService.updatePrintBtpDetails(payload).subscribe({
        next: () => {
          this.snackBar.open("Đã cập nhật trạng thái in", "Đóng", {
            duration: 3000,
          });
        },
        error: (err) => {
          console.error("Lỗi cập nhật trạng thái in:", err);
          this.snackBar.open("Cập nhật trạng thái in thất bại", "Đóng", {
            duration: 3000,
          });
        },
      });
    }
  }

  private getDataUrlFromRenderedImg(index: number): string | null {
    try {
      // Tìm wrapper theo data-qr-index
      const wrapperByAttr = document.querySelector(
        `.qr-wrapper[data-qr-index="${index}"]`,
      ) as HTMLElement | null;

      if (wrapperByAttr) {
        // ⭐ Ưu tiên lấy canvas từ .qr-zoom (220px hoặc cao hơn)
        const zoomCanvas = wrapperByAttr.querySelector(
          ".qr-zoom canvas",
        ) as HTMLCanvasElement | null;

        if (zoomCanvas) {
          // ⭐ Tạo canvas mới với độ phân giải cao gấp 5 lần
          const highResCanvas = document.createElement("canvas");
          const scale = 5; // Tăng gấp 5 lần độ phân giải

          highResCanvas.width = zoomCanvas.width * scale;
          highResCanvas.height = zoomCanvas.height * scale;

          const ctx = highResCanvas.getContext("2d");
          if (ctx) {
            // ⭐ Tắt image smoothing để pixel sắc nét
            ctx.imageSmoothingEnabled = false;
            // @ts-expect-error - Legacy browser support
            ctx.mozImageSmoothingEnabled = false;
            // @ts-expect-error - Legacy browser support
            ctx.webkitImageSmoothingEnabled = false;
            // @ts-expect-error - Legacy browser support
            ctx.msImageSmoothingEnabled = false;

            // Scale và vẽ lại
            ctx.scale(scale, scale);
            ctx.drawImage(zoomCanvas, 0, 0);

            try {
              return highResCanvas.toDataURL("image/png");
            } catch (err) {
              console.warn("highRes canvas.toDataURL failed", err);
            }
          }
        }

        // Fallback: canvas thường
        const normalCanvas = wrapperByAttr.querySelector(
          "canvas",
        ) as HTMLCanvasElement | null;

        if (normalCanvas) {
          return normalCanvas.toDataURL("image/png");
        }
      }

      return null;
    } catch (err) {
      console.error("getDataUrlFromRenderedImg error:", err);
      return null;
    }
  }

  private waitForCanvases(timeoutMs = 3000): Promise<void> {
    return new Promise((resolve) => {
      const start = Date.now();
      const check = (): void => {
        const canvases = document.querySelectorAll(".qr-wrapper canvas");
        if (canvases.length > 0) {
          resolve();
          return;
        }
        if (Date.now() - start > timeoutMs) {
          resolve();
          return;
        }
        setTimeout(check, 100);
      };
      check();
    });
  }

  private toStr(v: unknown): string {
    if (v === null || v === undefined) {
      return "";
    }
    return String(v);
  }

  // Helper chuyển về number an toàn (trả 0 nếu không phải số)
  private toNum(v: unknown): number {
    const n = Number(v);
    return Number.isFinite(n) ? n : 0;
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

  private async _exportPdfInternal(): Promise<void> {
    const items = this.paginatedItems;
    if (!items || items.length === 0) {
      return;
    }

    await this.waitForCanvases(5000);

    const doc = new jsPDF({
      unit: "mm",
      format: "a4",
      orientation: "portrait",
      compress: false,
    });

    const pageWidth = 210;
    const pageHeight = 297;
    const margin = 8;

    const cols = 4;
    const rowsPerPage = 5;
    const itemsPerPage = cols * rowsPerPage;

    const gapX = 5;

    const contentWidth = pageWidth - margin * 2;
    const usableHeight = pageHeight - margin * 2;

    const cellWidth = (contentWidth - gapX * (cols - 1)) / cols;
    const cellHeight = usableHeight / rowsPerPage;

    const qrSize = Math.min(cellWidth, cellHeight * 0.65);

    const qtyFontSize = 18;
    const labelFontSize = 8;

    const truncateToWidth = (
      text: string,
      maxWidth: number,
      fontSize: number,
    ): string => {
      doc.setFontSize(fontSize);
      if (doc.getTextWidth(text) <= maxWidth) {
        return text;
      }

      let low = 0;
      let high = text.length;
      let best = "";
      while (low < high) {
        const mid = Math.floor((low + high) / 2);
        const candidate = text.slice(0, mid);
        if (doc.getTextWidth(candidate) <= maxWidth) {
          best = candidate;
          low = mid + 1;
        } else {
          high = mid;
        }
      }
      return best || text.slice(0, 1);
    };

    for (let i = 0; i < items.length; i++) {
      // sang trang mới sau mỗi 20 QR
      if (i > 0 && i % itemsPerPage === 0) {
        doc.addPage();
      }

      const indexInPage = i % itemsPerPage;
      const row = Math.floor(indexInPage / cols);
      const col = indexInPage % cols;

      const xCell = margin + col * (cellWidth + gapX);
      const yCell = margin + row * cellHeight;

      // lấy QR từ DOM trước
      let dataUrl: string | null = this.getDataUrlFromRenderedImg(i);

      if (!dataUrl) {
        try {
          const text =
            (this.data?.isBtp
              ? (items[i].qrCode ?? items[i].maThung)
              : items[i].maThung) ?? "";
          dataUrl = await (QRCode as any).toDataURL(String(text), {
            width: 500,
          });
        } catch (err) {
          console.warn("QR generation fallback failed:", items[i], err);
          continue;
        }
      }

      if (!dataUrl) {
        continue;
      }

      // QR căn giữa trong cell
      const xQR = xCell + (cellWidth - qrSize) / 2;
      const yQR = yCell + 4;

      doc.addImage(dataUrl, "PNG", xQR, yQR, qrSize, qrSize);

      const qtyRaw = String(items[i].soLuong ?? "");
      const labelRaw = String(items[i].maThung ?? "");

      // số lượng
      doc.setFontSize(qtyFontSize);
      const qtyWidth = doc.getTextWidth(qtyRaw);
      doc.text(qtyRaw, xCell + (cellWidth - qtyWidth) / 2, yQR + qrSize + 6);

      // mã thùng
      const truncatedLabel = truncateToWidth(
        labelRaw,
        cellWidth,
        labelFontSize,
      );
      doc.setFontSize(labelFontSize);
      const labelWidth = doc.getTextWidth(truncatedLabel);
      doc.text(
        truncatedLabel,
        xCell + (cellWidth - labelWidth) / 2,
        yQR + qrSize + 11,
      );
    }

    doc.save(`qr-boxes-${new Date().toISOString().slice(0, 10)}.pdf`);
  }
}
