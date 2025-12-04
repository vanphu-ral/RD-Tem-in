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

// ===== INTERFACE CHO MỖI TRANG (2 PHIẾU) =====
interface PrintPage {
  left: PrintPalletData;
  right?: PrintPalletData; // Optional cho trang lẻ
}

@Component({
  selector: "jhi-print-pallet-dialog",
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    QRCodeComponent,
    MatProgressBarModule,
  ],
  templateUrl: "./print-pallet-dialog.component.html",
  styleUrls: ["./print-pallet-dialog.component.scss"],
})
export class PrintPalletDialogComponent implements OnInit {
  pallets: PrintPalletData[] = [];
  pages: PrintPage[] = []; // ===== THÊM PAGES ARRAY =====
  totalPages = 0;
  isMultiMode = false;
  //loader
  isLoadingPdf = false;
  progressPdf = 0;
  constructor(
    public dialogRef: MatDialogRef<PrintPalletDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PrintPalletData | PrintPalletData[],
    private cdr: ChangeDetectorRef,
  ) {
    console.log("📄 PrintPalletDialog constructor");
  }

  ngOnInit(): void {
    this.initializePallets();
    this.createPages(); // ===== TẠO PAGES STRUCTURE =====

    // ===== CHỜ QR CODE RENDER =====
    setTimeout(() => {
      this.debugPrintData();
    }, 500);
  }

  initializePallets(): void {
    if (Array.isArray(this.data)) {
      this.pallets = this.data;
      this.isMultiMode = true;
      console.log(` Multi-mode: ${this.pallets.length} pallets`);
    } else {
      this.pallets = [this.data];
      this.isMultiMode = false;
      console.log(" Single-mode: 1 pallet");
    }

    if (this.pallets.length === 0) {
      console.error("No pallets to print!");
    }
  }

  // ===== TẠO PAGES: MỖI PAGE CÓ 2 PHIẾU (LEFT + RIGHT) =====
  createPages(): void {
    this.pages = [];

    for (let i = 0; i < this.pallets.length; i += 2) {
      const page: PrintPage = {
        left: this.pallets[i],
        right: this.pallets[i + 1] || undefined, // undefined nếu là phiếu lẻ
      };

      this.pages.push(page);
    }

    this.totalPages = this.pages.length;

    console.log(
      ` Created ${this.totalPages} pages from ${this.pallets.length} pallets`,
    );
    this.pages.forEach((page, idx) => {
      console.log(
        `  Page ${idx + 1}: LEFT=${page.left.serialPallet}, RIGHT=${page.right?.serialPallet ?? "empty"}`,
      );
    });
  }

  debugPrintData(): void {
    console.log("\n=====  PRINT DIALOG DATA =====");
    console.log(`Mode: ${this.isMultiMode ? "MULTI" : "SINGLE"}`);
    console.log(`Total pallets: ${this.pallets.length}`);
    console.log(`Total pages: ${this.totalPages}`);
    console.log("=====================================");

    this.pages.forEach((page, pageIndex) => {
      console.log(` Page ${pageIndex + 1}/${this.totalPages}:`);
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
    console.log(` Printing ${this.pallets.length} pallet(s)...`);
    console.log(` Total pages: ${this.totalPages}`);

    // ===== CHỜ ĐẢM BẢO TẤT CẢ QR CODE ĐÃ RENDER =====
    setTimeout(() => {
      console.log(" Opening print dialog...");
      window.print();
    }, 800); // Tăng lên 800ms để chắc chắn
  }

  onClose(): void {
    console.log(" Closing print dialog");
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

    const pdf = new jsPDF("landscape", "mm", "a4");
    const pageWidth = pdf.internal.pageSize.getWidth();
    const a5Width = 148.5;
    const a5Height = 210;

    const renderPageToPdf = async (
      pageEl: HTMLElement,
      isFirst: boolean,
    ): Promise<void> => {
      const cards = Array.from(
        pageEl.querySelectorAll(".pallet-card"),
      ) as HTMLElement[];
      if (!isFirst) {
        pdf.addPage("a4", "landscape");
      }

      if (cards.length >= 2) {
        const canvas = await html2canvas(pageEl, { scale: 2, useCORS: true });
        const imgData = canvas.toDataURL("image/jpeg", 0.7);
        const imgPixelWidth = canvas.width;
        const imgPixelHeight = canvas.height;
        const imgMmWidth = pageWidth;
        const imgMmHeight = imgPixelHeight * (pageWidth / imgPixelWidth);
        pdf.addImage(imgData, "JPEG", 0, 0, imgMmWidth, imgMmHeight);
      } else if (cards.length === 1) {
        const card = cards[0];
        const canvas = await html2canvas(card, { scale: 2, useCORS: true });
        const imgData = canvas.toDataURL("image/jpeg", 0.7);
        pdf.addImage(imgData, "JPEG", 0, 0, a5Width, a5Height);
      }
    };

    const generatePdf = async (): Promise<void> => {
      for (let i = 0; i < pages.length; i++) {
        await renderPageToPdf(pages[i], i === 0);
        // cập nhật tiến độ sau mỗi trang
        this.progressPdf = Math.round(((i + 1) / pages.length) * 100);
      }

      pdf.save("phieu-thong-tin.pdf");
      this.isLoadingPdf = false;
    };

    void generatePdf().catch(() => {
      this.isLoadingPdf = false;
    });
  }
}
