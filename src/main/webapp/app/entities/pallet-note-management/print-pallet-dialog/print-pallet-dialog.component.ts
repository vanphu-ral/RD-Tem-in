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
import { PalletDialogItem } from "../wms-approve-dialog/wms-approve-dialog.component";
import { firstValueFrom } from "rxjs";
import { PlanningWorkOrderService } from "../service/planning-work-order.service";

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

interface PrintPage {
  left: PrintPalletData;
  right?: PrintPalletData;
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
  paperSize: "A4" | "A5" = "A4";
  isLoadingPdf = false;
  progressPdf = 0;

  constructor(
    public dialogRef: MatDialogRef<PrintPalletDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PrintPalletData | PrintPalletData[],
    private cdr: ChangeDetectorRef,
    private planningService: PlanningWorkOrderService,
  ) {
    console.log("PrintPalletDialog constructor");
  }

  ngOnInit(): void {
    this.initializePallets();
    this.createPages();

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

  createPages(): void {
    this.pages = [];

    if (this.paperSize === "A4") {
      for (let i = 0; i < this.pallets.length; i += 2) {
        const page: PrintPage = {
          left: this.pallets[i],
          right: this.pallets[i + 1] || undefined,
        };
        this.pages.push(page);
      }
    } else {
      for (let i = 0; i < this.pallets.length; i++) {
        const page: PrintPage = {
          left: this.pallets[i],
          right: undefined,
        };
        this.pages.push(page);
      }
    }

    this.totalPages = this.pages.length;
    this.displayPages = [...this.pages];

    console.log(
      `Created ${this.totalPages} pages (${this.paperSize}) from ${this.pallets.length} pallets`,
    );
  }

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
    console.log("=====================================\n");
  }

  /**
   * ===== PRINT WITH QR CODE CONVERSION =====
   * Đợi QR render xong → Convert canvas to image TRƯỚC KHI clone
   */
  async onPrint(): Promise<void> {
    await new Promise((resolve) => setTimeout(resolve, 2000));

    // ===== BƯỚC 2: CONVERT TẤT CẢ CANVAS → IMAGE TRƯỚC KHI CLONE =====
    const printArea = document.querySelector(".print-content") as HTMLElement;
    if (!printArea) {
      return;
    }

    const originalCanvases = Array.from(printArea.querySelectorAll("canvas"));
    const imageDataList: Array<{
      canvas: HTMLCanvasElement;
      dataUrl: string;
      parent: Node;
    }> = [];

    // Convert all canvas to data URLs
    for (const canvas of originalCanvases) {
      try {
        if (canvas.width === 0 || canvas.height === 0) {
          console.warn("Empty canvas detected, skipping");
          continue;
        }

        const dataUrl = canvas.toDataURL("image/png");
        if (canvas.parentNode) {
          imageDataList.push({ canvas, dataUrl, parent: canvas.parentNode });
        }
      } catch (err) {
        console.error("Canvas conversion failed:", err);
      }
    }

    // Replace canvas with img temporarily
    const tempImages: HTMLImageElement[] = [];
    imageDataList.forEach(({ canvas, dataUrl, parent }) => {
      const img = document.createElement("img");
      img.src = dataUrl;
      img.width = canvas.width;
      img.height = canvas.height;
      img.style.cssText = canvas.style.cssText;
      img.className = canvas.className;

      parent.replaceChild(img, canvas);
      tempImages.push(img);
    });

    // ===== BƯỚC 3: CHỜ IMG LOAD XONG =====
    await Promise.all(
      tempImages.map((img) => {
        if (img.complete) {
          return Promise.resolve();
        }
        return new Promise((resolve) => {
          img.onload = resolve;
          img.onerror = resolve;
        });
      }),
    );

    // ===== BƯỚC 4: CLONE SAU KHI ĐÃ CÓ IMAGE =====
    const clone = printArea.cloneNode(true) as HTMLElement;
    const palletCards = Array.from(
      clone.querySelectorAll(".pallet-card"),
    ) as HTMLElement[];
    if (palletCards.length > 0 && !clone.querySelector(".print-page")) {
      const pagesHtml = palletCards
        .map((card) => `<div class="print-page">${card.outerHTML}</div>`)
        .join("\n");
      clone.innerHTML = pagesHtml;
    }

    // ===== BƯỚC 5: RESTORE CANVAS (để không ảnh hưởng preview) =====
    imageDataList.forEach(({ canvas, parent }, index) => {
      const img = tempImages[index];
      parent.replaceChild(canvas, img);
    });
    // ===== BƯỚC 6: COLLECT STYLES =====
    let styles = "";
    for (const sheet of Array.from(document.styleSheets)) {
      try {
        if (sheet.cssRules) {
          for (const rule of Array.from(sheet.cssRules)) {
            styles += rule.cssText + "\n";
          }
        }
      } catch (e) {
        // Cross-origin stylesheet - skip
      }
    }

    // ===== BƯỚC 7: TẠO PRINT HTML =====
    const isA5 = this.paperSize === "A5";
    const printHTML = `
      <!DOCTYPE html>
      <html lang="vi">
      <head>
        <meta charset="utf-8">
        <title>Phiếu Thông Tin Pallet - ${this.paperSize}</title>
        <style>
          ${styles}
          
          /* Reset */
          * { 
            box-sizing: border-box;
            margin: 0;
            padding: 0;
          }
          html, body {
            width: 148.5mm !important;
            height: 210mm !important;
            margin: 0 !important;
            padding: 0 !important;
            background: white !important;
            -webkit-print-color-adjust: exact !important;
            print-color-adjust: exact !important;
            zoom: 1 !important;
            -webkit-transform: none !important;
          }
          body { 
            margin: 0 !important;
            padding: 0 !important;
            background: white;
            font-family: Arial, sans-serif;
          }
          
          /* Print Content */
          .print-content { 
            width: 148.5mm !important;
            margin: 0 !important;
            padding: 0 !important;
            display: block !important;
          }
          
          /* Print Page - A4 Mode */
          ${
            !isA5
              ? `
          .print-page {
            width: 297mm !important;
            height: 210mm !important;
            display: flex !important;
            flex-direction: row !important;
            gap: 0 !important;
            margin: 0 !important;
            padding: 0 !important;
            page-break-after: always !important;
            page-break-inside: avoid !important;
          }
          
          .pallet-card {
            width: 148.5mm !important;
            height: 210mm !important;
            padding: 8mm !important;
            margin: 0 !important;
          }
          `
              : ""
          }
          
          /* Print Page - A5 Mode */
          ${
            isA5
              ? `
          .print-page {
            width: 148.5mm !important;
  height: 210mm !important;
  margin: 0 !important;
  padding: 0 !important;
  page-break-after: always !important;
  page-break-inside: avoid !important;
  box-sizing: border-box !important;
  display: block !important;
          }
          
         .pallet-card {
            width: 100% !important;
  height: 100% !important;
  margin: 0 !important;
  padding: 8mm !important; /* lề nội bộ nếu cần */
  box-sizing: border-box !important;
  background: white !important;
  display: flex !important;
  flex-direction: column !important;
  justify-content: flex-start !important;
          }
          `
              : ""
          }
          
          .print-page:last-child { 
            page-break-after: avoid !important;
          }
          
          /* Pallet Card */
          .pallet-card {
            border: 2px solid #000 !important;
            box-shadow: none !important;
            page-break-inside: avoid !important;
            background: white !important;
            box-sizing: border-box !important;
            display: flex !important;
            flex-direction: column !important;
          }
          
          /* QR Code Wrapper */
          .qr-code-wrapper {
            display: flex !important;
            align-items: center !important;
            justify-content: center !important;
            background: white !important;
            border: 2px solid #000 !important;
          }
          
          .qr-code-wrapper img {
            display: block !important;
            max-width: 100% !important;
            max-height: 100% !important;
            object-fit: contain !important;
          }
          
          /* Force Colors */
          * { 
            -webkit-print-color-adjust: exact !important;
            print-color-adjust: exact !important;
            color-adjust: exact !important;
          }
          
          /* Page Setup */
          @page {
            size: ${isA5 ? "A5 portrait" : "A4 landscape"};
            margin: 0 !important;
          }
        </style>
      </head>
      <body>
        <div class="print-content ${isA5 ? "a5-mode" : ""}">
          ${clone.innerHTML}
        </div>
        
       <script>
        (function() {
          function mmToPx(mm) { return mm * (96 / 25.4); } // ~3.7795 px/mm

          const isA5 = ${isA5 ? "true" : "false"};
          const targetWidthMm = isA5 ? 148.5 : 297;
          const targetHeightMm = isA5 ? 210 : 210;
          const targetWidthPx = mmToPx(targetWidthMm);
          const targetHeightPx = mmToPx(targetHeightMm);

          // CHỈ DÙNG CHO A5: đặt theo thực tế bạn thấy vừa khít
          const scaleAdjustmentA5 = 0.565; // bạn đã thử và thấy vừa
          const scaleAdjustmentA4 = 1;    // A4 không scale

          function getAvailableWidth() {
            return document.documentElement.clientWidth || window.innerWidth || targetWidthPx;
          }

          function applyScaleAndPrint() {
            const content = document.querySelector('.print-content');
            const firstPage = document.querySelector('.print-page') || content;
            if (!content || !firstPage) {
              console.warn('No print content/pages found, printing raw');
              window.print();
              return;
            }

            // Thiết lập cơ sở để layout ổn định
            document.documentElement.style.width = targetWidthPx + 'px';
            document.documentElement.style.height = targetHeightPx + 'px';
            document.body.style.width = targetWidthPx + 'px';
            document.body.style.height = 'auto';
            document.body.style.margin = '0';

            // Đo kích thước thực tế của một print-page
            const pageRect = firstPage.getBoundingClientRect();
            const contentWidth = pageRect.width || firstPage.offsetWidth || targetWidthPx;

            // Đo vùng in khả dụng
            const availableWidth = getAvailableWidth();

            // Tính scale cơ bản
            let scale = availableWidth / contentWidth;

            // Áp adjustment riêng cho A5, A4 giữ 1
            const adjustment = isA5 ? scaleAdjustmentA5 : scaleAdjustmentA4;
            scale = scale * adjustment;

            // Nếu là A4, reset mọi transform để giữ nguyên hiển thị
            if (!isA5) {
              content.style.transform = '';
              content.style.transformOrigin = '';
              content.style.width = contentWidth + 'px';
            } else {
              // A5: áp transform
              if (!isFinite(scale) || scale <= 0) scale = 1;
              // giới hạn nếu cần
              scale = Math.min(Math.max(scale, 0.2), 3);
              content.style.transformOrigin = 'top left';
              content.style.transform = 'scale(' + scale + ')';
              content.style.width = contentWidth + 'px';
            }

            console.log('print debug:', { isA5, targetWidthPx, contentWidth, availableWidth, scale, adjustment });

            // In sau delay nhỏ để transform áp dụng
            setTimeout(() => window.print(), 250);
          }

          // Chờ ảnh load xong rồi scale + print
          window.onload = function() {
            const imgs = Array.from(document.images);
            if (imgs.length === 0) {
              setTimeout(applyScaleAndPrint, 100);
              return;
            }
            Promise.all(imgs.map(img => img.complete ? Promise.resolve() : new Promise(r => { img.onload = r; img.onerror = r; })))
              .then(() => setTimeout(applyScaleAndPrint, 120));
          };

          window.onafterprint = function() { window.close(); };
        })();
      </script>

      </body>
      </html>
    `;

    // ===== BƯỚC 8: MỞ PRINT WINDOW =====
    console.log("Opening print window...");
    const printWindow = window.open("", "_blank", "width=1200,height=900");

    if (printWindow) {
      printWindow.document.write(printHTML);
      printWindow.document.close();
      console.log("Print window opened successfully");
    } else {
      alert("Vui lòng cho phép popup để in!");
    }
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

    const isA5 = this.paperSize === "A5";
    const pdfOrientation = isA5 ? "portrait" : "landscape";
    const pdfFormat = isA5 ? [148, 210] : [297, 210];

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
        if (cards.length >= 1) {
          const card = cards[0];
          const canvas = await html2canvas(card, { scale: 2, useCORS: true });
          const imgData = canvas.toDataURL("image/jpeg", 0.8);
          pdf.addImage(imgData, "JPEG", 0, 0, pageWidth, pageHeight);
        }
      } else {
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
  private buildPrintStatusPayload(
    selectedPallets: PalletDialogItem[],
    currentUser: string,
  ): Array<{ id: number; print_status: boolean; updated_by: string }> {
    return selectedPallets.map((p) => ({
      id: p.id,
      print_status: true,
      updated_by: currentUser ?? "system",
    }));
  }
}
