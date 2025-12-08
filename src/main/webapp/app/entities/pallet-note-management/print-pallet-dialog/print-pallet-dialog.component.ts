import { ChangeDetectorRef, Component, Inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from "@angular/material/dialog";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { QRCodeComponent } from "angularx-qrcode";
import jsPDF from "jspdf";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import html2canvas from "html2canvas";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { FormsModule } from "@angular/forms";

export interface PrintPalletData {
  khachHang: string;
  serialPallet: string;
  tenSanPham: string;
  poNumber: string;
  itemNoSku: string;
  nganh: string;
  led2: string;
  soQdsx: string;
  to: string;
  lpl2: string;
  ngaySanXuat: string;
  dateCode: string;
  soLuongCaiDatPallet: number;
  thuTuGiaPallet: number;
  soLuongBaoNgoaiThungGiaPallet: string;
  slThung: number;
  note: string;
  nguoiKiemTra: string;
  ketQuaKiemTra: string;
  productCode: string;
  serialBox: string;
  qty: number;
  lot: string;
  date: string;
  scannedBoxes?: string[];
}

// ===== INTERFACE CHO MỖI TRANG (2 PHIẾU HOẶC 1 PHIẾU) =====
interface PrintPage {
  left: PrintPalletData;
  right?: PrintPalletData; // Optional cho trang lẻ hoặc A5 mode
}

@Component({
  selector: "jhi-print-pallet-dialog",
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatSelectModule,
    QRCodeComponent,
    MatInputModule,
    MatProgressBarModule,
    FormsModule,
  ],
  templateUrl: "./print-pallet-dialog.component.html",
  styleUrls: ["./print-pallet-dialog.component.scss"],
})
export class PrintPalletDialogComponent implements OnInit {
  pallets: PrintPalletData[] = [];
  pages: PrintPage[] = [];
  totalPages = 0;
  displayPages: PrintPage[] = [];
  isMultiMode = false;

  // ===== PAPER SIZE SELECTION =====
  paperSize: "A4" | "A5" = "A4";

  // Loader
  isLoadingPdf = false;
  progressPdf = 0;

  constructor(
    public dialogRef: MatDialogRef<PrintPalletDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PrintPalletData | PrintPalletData[],
    private cdr: ChangeDetectorRef,
  ) {
    console.log("PrintPalletDialog constructor");
  }

  ngOnInit(): void {
    this.initializePallets();
    this.createPages();

    // ===== CHỜ QR CODE RENDER =====
    setTimeout(() => {
      this.debugPrintData();
    }, 500);
  }

  initializePallets(): void {
    if (Array.isArray(this.data)) {
      this.pallets = this.data;
      this.isMultiMode = true;
      console.log(`Multi-mode: ${this.pallets.length} pallets`);
    } else {
      this.pallets = [this.data];
      this.isMultiMode = false;
      console.log("Single-mode: 1 pallet");
    }

    if (this.pallets.length === 0) {
      console.error("No pallets to print!");
    }
  }

  // ===== TẠO PAGES DỰA TRÊN PAPER SIZE =====
  createPages(): void {
    this.pages = [];

    if (this.paperSize === "A4") {
      // A4 mode: 2 phiếu/trang
      for (let i = 0; i < this.pallets.length; i += 2) {
        const page: PrintPage = {
          left: this.pallets[i],
          right: this.pallets[i + 1] || undefined,
        };
        this.pages.push(page);
      }
    } else {
      // A5 mode: 1 phiếu/trang
      for (let i = 0; i < this.pallets.length; i++) {
        const page: PrintPage = {
          left: this.pallets[i],
          right: undefined, // Không có phiếu bên phải trong A5 mode
        };
        this.pages.push(page);
      }
    }

    this.totalPages = this.pages.length;
    this.displayPages = [...this.pages];

    console.log(
      `Created ${this.totalPages} pages (${this.paperSize}) from ${this.pallets.length} pallets`,
    );
    this.pages.forEach((page, idx) => {
      console.log(
        `  Page ${idx + 1}: LEFT=${page.left.serialPallet}, RIGHT=${page.right?.serialPallet ?? "empty"}`,
      );
    });
  }

  // ===== HANDLE PAPER SIZE CHANGE =====
  onPaperSizeChange(): void {
    console.log(`Paper size changed to: ${this.paperSize}`);
    this.createPages();
    this.cdr.detectChanges();
  }

  debugPrintData(): void {
    console.log("\n===== PRINT DIALOG DATA =====");
    console.log(`Mode: ${this.isMultiMode ? "MULTI" : "SINGLE"}`);
    console.log(`Paper Size: ${this.paperSize}`);
    console.log(`Total pallets: ${this.pallets.length}`);
    console.log(`Total pages: ${this.totalPages}`);
    console.log("=====================================");

    this.pages.forEach((page, pageIndex) => {
      console.log(`Page ${pageIndex + 1}/${this.totalPages}:`);
      console.log(
        `  LEFT: ${page.left.serialPallet} (${page.left.soLuongCaiDatPallet} SP)`,
      );
      if (page.right) {
        console.log(
          `  RIGHT: ${page.right.serialPallet} (${page.right.soLuongCaiDatPallet} SP)`,
        );
      } else {
        console.log(`  RIGHT: (empty)`);
      }
    });

    console.log("\n=====================================\n");
  }

  onPrint(): void {
    console.log(`Printing ${this.pallets.length} pallet(s)...`);
    console.log(`Paper size: ${this.paperSize}`);
    console.log(`Total pages: ${this.totalPages}`);

    // Apply body class for print styling
    if (this.paperSize === "A5") {
      document.body.classList.add("print-a5-mode");
    } else {
      document.body.classList.remove("print-a5-mode");
    }

    // ===== CHỜ ĐẢM BẢO TẤT CẢ QR CODE ĐÃ RENDER =====
    setTimeout(() => {
      console.log("Opening print dialog...");
      window.print();

      // Clean up after print
      setTimeout(() => {
        document.body.classList.remove("print-a5-mode");
      }, 100);
    }, 800);
  }

  onClose(): void {
    console.log("Closing print dialog");
    this.dialogRef.close();
  }

  onExportPdf(): void {
    this.isLoadingPdf = true;
    this.progressPdf = 0;

    const pages = Array.from(
      document.querySelectorAll(".print-page"),
    ) as HTMLElement[];

    if (pages.length === 0) {
      console.error("Không tìm thấy .print-page");
      this.isLoadingPdf = false;
      return;
    }

    // ===== XÁC ĐỊNH FORMAT DỰA TRÊN PAPER SIZE =====
    const isA5 = this.paperSize === "A5";
    const pdfOrientation = isA5 ? "portrait" : "landscape";
    const pdfFormat = isA5 ? [148, 210] : [297, 210]; // [width, height] in mm

    const pdf = new jsPDF(pdfOrientation as any, "mm", pdfFormat as any);
    const pageWidth = pdf.internal.pageSize.getWidth();
    const pageHeight = pdf.internal.pageSize.getHeight();

    console.log(
      `Exporting to PDF (${this.paperSize}): ${pageWidth}x${pageHeight}mm`,
    );

    const renderPageToPdf = async (
      pageEl: HTMLElement,
      isFirst: boolean,
    ): Promise<void> => {
      const cards = Array.from(
        pageEl.querySelectorAll(".pallet-card"),
      ) as HTMLElement[];

      if (!isFirst) {
        pdf.addPage(pdfFormat as any, pdfOrientation as any);
      }

      if (isA5) {
        // A5 mode: chỉ render 1 card
        if (cards.length >= 1) {
          const card = cards[0];
          const canvas = await html2canvas(card, { scale: 2, useCORS: true });
          const imgData = canvas.toDataURL("image/jpeg", 0.8);
          pdf.addImage(imgData, "JPEG", 0, 0, pageWidth, pageHeight);
        }
      } else {
        // A4 mode: render cả trang (2 cards)
        if (cards.length >= 1) {
          const canvas = await html2canvas(pageEl, { scale: 2, useCORS: true });
          const imgData = canvas.toDataURL("image/jpeg", 0.8);
          const imgPixelWidth = canvas.width;
          const imgPixelHeight = canvas.height;
          const imgMmWidth = pageWidth;
          const imgMmHeight = imgPixelHeight * (pageWidth / imgPixelWidth);
          pdf.addImage(imgData, "JPEG", 0, 0, imgMmWidth, imgMmHeight);
        }
      }
    };

    const generatePdf = async (): Promise<void> => {
      for (let i = 0; i < pages.length; i++) {
        await renderPageToPdf(pages[i], i === 0);
        // Cập nhật tiến độ sau mỗi trang
        this.progressPdf = Math.round(((i + 1) / pages.length) * 100);
        this.cdr.detectChanges();
      }

      const fileName = `phieu-thong-tin-${this.paperSize}-${Date.now()}.pdf`;
      pdf.save(fileName);
      console.log(`PDF saved: ${fileName}`);

      this.isLoadingPdf = false;
      this.progressPdf = 0;
      this.cdr.detectChanges();
    };

    void generatePdf().catch((error) => {
      console.error("Error generating PDF:", error);
      this.isLoadingPdf = false;
      this.progressPdf = 0;
      this.cdr.detectChanges();
    });
  }
}
