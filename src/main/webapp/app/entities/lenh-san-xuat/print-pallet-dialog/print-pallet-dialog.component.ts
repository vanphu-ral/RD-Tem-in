import { Component, Inject, OnInit } from "@angular/core";
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
  ],
  templateUrl: "./print-pallet-dialog.component.html",
  styleUrls: ["./print-pallet-dialog.component.scss"],
})
export class PrintPalletDialogComponent implements OnInit {
  pallets: PrintPalletData[] = [];
  pages: PrintPage[] = []; // ===== THÊM PAGES ARRAY =====
  totalPages = 0;
  isMultiMode = false;

  constructor(
    public dialogRef: MatDialogRef<PrintPalletDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PrintPalletData | PrintPalletData[],
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
    const pages = Array.from(
      document.querySelectorAll(".print-page"),
    ) as HTMLElement[];
    if (pages.length === 0) {
      console.error("Không tìm thấy .print-page");
      return;
    }

    const pdf = new jsPDF("landscape", "mm", "a4");
    const pageWidth = pdf.internal.pageSize.getWidth(); // 297mm
    const pageHeight = pdf.internal.pageSize.getHeight(); // 210mm
    const a5Width = 148.5; // mm
    const a5Height = 210; // mm

    // Tắt box-shadow để render canvas sắc nét (khôi phục sau)
    const cleanupStyles: Array<() => void> = [];
    const disableEffects = (el: HTMLElement): void => {
      const prevBoxShadow: string = el.style.boxShadow;
      const prevTransform: string = el.style.transform;
      el.style.boxShadow = "none";
      el.style.transform = "none";
      cleanupStyles.push((): void => {
        el.style.boxShadow = prevBoxShadow;
        el.style.transform = prevTransform;
      });
    };

    const renderPageToPdf = async (
      pageEl: HTMLElement,
      isFirst: boolean,
    ): Promise<void> => {
      const cards = Array.from(
        pageEl.querySelectorAll(".pallet-card"),
      ) as HTMLElement[];

      // Thêm trang mới (trang đầu tiên không cần addPage)
      if (!isFirst) {
        pdf.addPage("a4", "landscape");
      }

      // Trường hợp 2 phiếu: chụp nguyên trang
      if (cards.length >= 2) {
        disableEffects(pageEl);
        const canvas = await html2canvas(pageEl, { scale: 2, useCORS: true });
        const imgData = canvas.toDataURL("image/png");
        const imgPixelWidth = canvas.width;
        const imgPixelHeight = canvas.height;
        const imgMmWidth = pageWidth;
        const imgMmHeight = imgPixelHeight * (pageWidth / imgPixelWidth);
        pdf.addImage(imgData, "PNG", 0, 0, imgMmWidth, imgMmHeight);
        return;
      }

      // Trường hợp phiếu lẻ: chụp trực tiếp pallet-card và đặt đúng nửa A4
      if (cards.length === 1) {
        const card = cards[0];
        disableEffects(card);
        const canvas = await html2canvas(card, { scale: 2, useCORS: true });
        const imgData = canvas.toDataURL("image/png");

        // Chọn vị trí: trái hoặc phải. Ở đây đặt TRÁI, nếu muốn giữa thì x = (pageWidth - a5Width)/2
        const xLeft = 0; // đặt bên trái
        // const xCenter = (pageWidth - a5Width) / 2; // nếu muốn căn giữa
        const yTop = 0;

        pdf.addImage(imgData, "PNG", xLeft, yTop, a5Width, a5Height);
        return;
      }

      // Nếu không có phiếu (bất thường), bỏ qua
      console.warn("Trang không có pallet-card, bỏ qua.");
    };

    const generatePdf = async (): Promise<void> => {
      // Đảm bảo font web đã sẵn sàng (nếu trình duyệt hỗ trợ)
      if (
        (document as any).fonts &&
        typeof (document as any).fonts.ready !== "undefined"
      ) {
        await (document as any).fonts.ready;
      }

      for (let i = 0; i < pages.length; i++) {
        await renderPageToPdf(pages[i], i === 0);
      }

      // Khôi phục styles
      cleanupStyles.forEach((fn: () => void): void => fn());

      pdf.save("phieu-thong-tin.pdf");
    };

    void generatePdf();
  }
}
