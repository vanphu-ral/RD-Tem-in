import { ChangeDetectorRef, Component, Inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialog,
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
import { MatSnackBar } from "@angular/material/snack-bar";
import { AccountService } from "app/core/auth/account.service";
import {
  ConfirmDialogComponent,
  ConfirmDialogData,
} from "../confirm-dialog/confirm-dialog.component";

export interface PrintPalletData {
  id?: number;
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
  printStatus?: boolean;
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
  unprintedPages: PrintPage[] = [];

  constructor(
    public dialogRef: MatDialogRef<PrintPalletDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PrintPalletData | PrintPalletData[],
    private cdr: ChangeDetectorRef,
    private planningService: PlanningWorkOrderService,
    private snackBar: MatSnackBar,
    private accountService: AccountService,
    private dialog: MatDialog,
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
  isNotScanned(pallet: PrintPalletData): boolean {
    return pallet.serialPallet === pallet.serialBox;
  }
  getWatermarkText(pallet: PrintPalletData): string {
    if (pallet.printStatus) {
      return "ĐÃ IN";
    }
    if (this.isNotScanned(pallet)) {
      return "CHƯA SCAN";
    }
    return "";
  }

  shouldShowWatermark(pallet: PrintPalletData): boolean {
    return pallet.printStatus === true || this.isNotScanned(pallet);
  }
  /**
   * ===== PRINT WITH QR CODE CONVERSION =====
   * Đợi QR render xong → Convert canvas to image TRƯỚC KHI clone
   */
  async onPrint(): Promise<void> {
    const unprintedPallets = this.pallets.filter(
      // (p) => !p.printStatus && !this.isNotScanned(p),
      (p) => !this.isNotScanned(p),
    );

    if (unprintedPallets.length === 0) {
      this.snackBar.open(
        "Không có phiếu nào cần in!(Các phiếu đã in hoặc chưa scan sẽ không được in)",
        "Đóng",
        { duration: 3000 },
      );
      return;
    }

    // Tạo pages tạm thời chỉ với phiếu chưa in
    const tempPages: PrintPage[] = [];
    if (this.paperSize === "A4") {
      for (let i = 0; i < unprintedPallets.length; i += 2) {
        tempPages.push({
          left: unprintedPallets[i],
          right: unprintedPallets[i + 1] || undefined,
        });
      }
    } else {
      for (let i = 0; i < unprintedPallets.length; i++) {
        tempPages.push({
          left: unprintedPallets[i],
          right: undefined,
        });
      }
    }

    // Lưu displayPages gốc
    const originalPages = this.displayPages;

    // Thay thế tạm thời bằng pages chưa in
    this.displayPages = tempPages;
    this.cdr.detectChanges();

    // Đợi render
    await new Promise((resolve) => setTimeout(resolve, 2000));

    const printArea = document.querySelector(".print-content") as HTMLElement;
    if (!printArea) {
      this.displayPages = originalPages;
      this.cdr.detectChanges();
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

    // Chờ IMG load xong
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

    // Clone sau khi đã có image
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

    // Restore canvas
    imageDataList.forEach(({ canvas, parent }, index) => {
      const img = tempImages[index];
      parent.replaceChild(canvas, img);
    });

    // Khôi phục displayPages gốc ngay sau khi clone xong
    this.displayPages = originalPages;
    this.cdr.detectChanges();

    // Collect styles
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

    // Tạo print HTML
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
            height: 208mm !important;
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
          .watermark-overlay {
            display: none !important;
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

      // Khi popup đóng thì gửi message về app chính
      printWindow.onunload = () => {
        window.postMessage({ type: "PRINT_DONE" }, window.location.origin);
      };

      // Lắng nghe message trong app chính
      const handler = (event: MessageEvent): void => {
        if (event.origin !== window.location.origin) {
          return;
        }
        if (event.data?.type === "PRINT_DONE") {
          const dialogData: ConfirmDialogData = {
            title: "Bạn đã in chưa?",
            message: "Chọn Xong để cập nhật trạng thái!",
            confirmText: "Xong",
            cancelText: "Hủy",
            type: "info",
          };

          const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: "500px",
            data: dialogData,
            disableClose: false,
          });

          dialogRef.afterClosed().subscribe((confirmed: boolean) => {
            if (confirmed) {
              void this.updatePrintStatusAfterPrint(unprintedPallets);
            }
          });

          // bỏ listener sau khi xử lý
          window.removeEventListener("message", handler);
        }
      };
      window.addEventListener("message", handler);
    } else {
      alert("Vui lòng cho phép popup để in!");
    }
  }

  onClose(): void {
    console.log("Closing print dialog");
    this.dialogRef.close();
  }

  async onExportPdf(): Promise<void> {
    this.isLoadingPdf = true;
    this.progressPdf = 0;

    const unprintedPallets = this.pallets.filter((p) => !this.isNotScanned(p));

    if (unprintedPallets.length === 0) {
      this.snackBar.open(
        "Không có phiếu nào cần xuất PDF! (Các phiếu đã in hoặc chưa scan sẽ không được xuất)",
        "Đóng",
        { duration: 3000 },
      );
      this.isLoadingPdf = false;
      return;
    }

    // Tạo pages tạm thời
    const tempPages: PrintPage[] = [];
    if (this.paperSize === "A4") {
      for (let i = 0; i < unprintedPallets.length; i += 2) {
        tempPages.push({
          left: unprintedPallets[i],
          right: unprintedPallets[i + 1] || undefined,
        });
      }
    } else {
      for (let i = 0; i < unprintedPallets.length; i++) {
        tempPages.push({
          left: unprintedPallets[i],
          right: undefined,
        });
      }
    }

    // Lưu displayPages gốc và thay thế tạm thời
    const originalPages = this.displayPages;
    this.displayPages = tempPages;
    this.cdr.detectChanges();

    // Đợi render xong và convert QR
    await new Promise((resolve) => setTimeout(resolve, 500));
    await this.convertQRCodesToImages();

    const printContent = document.querySelector(".print-content");
    printContent?.classList.add("exporting-pdf");

    // Ẩn watermark-overlay trước khi chụp
    const watermarkEls = Array.from(
      document.querySelectorAll(".watermark-overlay"),
    ) as HTMLElement[];
    const watermarkOldDisplays: string[] = [];
    watermarkEls.forEach((el, idx) => {
      watermarkOldDisplays[idx] = el.style.display;
      el.style.display = "none";
    });

    // helper: mm -> px (approx, dùng 96 DPI baseline và devicePixelRatio)
    function mmToPx(mm: number): number {
      return Math.round((mm / 25.4) * 96 * (window.devicePixelRatio || 1));
    }

    // Lấy các trang hiện tại
    const pageElements = Array.from(
      document.querySelectorAll(".print-page"),
    ) as HTMLElement[];
    const singleA4SingleTicket =
      this.paperSize === "A4" && pageElements.length === 1;

    // Lưu style đã thay đổi để khôi phục
    const savedStyles: {
      el: HTMLElement;
      styles: Partial<CSSStyleDeclaration>;
    }[] = [];

    try {
      const isA5 = this.paperSize === "A5";
      const pdfOrientation = isA5 ? "portrait" : "landscape";
      const pdfFormat = isA5 ? [148, 210] : [297, 210];

      const pdf = new jsPDF(pdfOrientation as any, "mm", pdfFormat as any);
      const pageWidth = pdf.internal.pageSize.getWidth();
      const pageHeight = pdf.internal.pageSize.getHeight();

      console.log(`Starting PDF export: ${pageElements.length} pages`);
      const cleanupStyles: Array<() => void> = [];
      const disableEffects = (el: HTMLElement): void => {
        const prevBoxShadow = el.style.boxShadow;
        const prevTransform = el.style.transform;
        el.style.boxShadow = "none";
        el.style.transform = "none";
        cleanupStyles.push(() => {
          el.style.boxShadow = prevBoxShadow;
          el.style.transform = prevTransform;
        });
      };

      for (let i = 0; i < pageElements.length; i++) {
        const pageEl = pageElements[i];

        if (i > 0) {
          pdf.addPage(pdfFormat as any, pdfOrientation as any);
        }

        // Lấy số card trên page hiện tại
        const cards = Array.from(
          pageEl.querySelectorAll(".pallet-card"),
        ) as HTMLElement[];
        console.log(`Page ${i}: found ${cards.length} .pallet-card`);

        // Trường hợp 2 hoặc nhiều phiếu: chụp nguyên page
        if (cards.length >= 2) {
          // tắt hiệu ứng nếu cần (nếu bạn đã có cleanupStyles/disableEffects)
          // disableEffects(pageEl);
          const canvas = await html2canvas(pageEl, {
            scale: 2,
            useCORS: true,
            logging: false,
            backgroundColor: "#ffffff",
            imageTimeout: 0,
          });
          const imgData = canvas.toDataURL("image/png");
          const imgPixelWidth = canvas.width;
          const imgPixelHeight = canvas.height;
          const imgMmWidth = pageWidth;
          const imgMmHeight = imgPixelHeight * (pageWidth / imgPixelWidth);
          pdf.addImage(imgData, "PNG", 0, 0, imgMmWidth, imgMmHeight);
          canvas.remove();
          continue;
        }

        // Trường hợp đúng 1 phiếu trên page và paperSize là A4: chụp card và vẽ nửa A4
        if (cards.length === 1 && this.paperSize === "A4") {
          const card = cards[0];
          // tắt hiệu ứng tạm thời (giống code cũ)
          const prevBoxShadow = card.style.boxShadow;
          const prevTransform = card.style.transform;
          card.style.boxShadow = "none";
          card.style.transform = "none";

          try {
            const canvas = await html2canvas(card, {
              scale: 2,
              useCORS: true,
              logging: false,
              backgroundColor: "#ffffff",
              imageTimeout: 0,
            });

            console.log(
              "canvas px",
              canvas.width,
              canvas.height,
              "page mm",
              pageWidth,
              pageHeight,
            );

            const imgData = canvas.toDataURL("image/png");

            // Vẽ ảnh vào nửa A4 (giữ chiều cao A4) — giống code cũ
            const a5Width = pageWidth / 2; // mm
            const a5Height = pageHeight; // mm
            const xLeft = 0; // đặt bên trái; nếu muốn căn giữa nửa trang: (pageWidth - a5Width)/2
            const yTop = 0;

            pdf.addImage(imgData, "PNG", xLeft, yTop, a5Width, a5Height);

            canvas.remove();
          } catch (err) {
            console.error("html2canvas error on single card", err);
          } finally {
            // khôi phục style
            card.style.boxShadow = prevBoxShadow;
            card.style.transform = prevTransform;
          }

          continue;
        }

        // --- KẾT THÚC xử lý singleA4SingleTicket ---
        else {
          // Trường hợp bình thường: chụp toàn pageEl
          // (nếu pageEl có nội dung 2 phiếu, html2canvas sẽ chụp cả)
          const originalWidth = pageEl.style.width;
          const originalHeight = pageEl.style.height;

          // (Không ép width toàn trang ở đây, để tránh scale không mong muốn)
          const canvas = await html2canvas(pageEl, {
            scale: 2,
            useCORS: true,
            logging: false,
            allowTaint: true,
            backgroundColor: "#ffffff",
            imageTimeout: 0,
            removeContainer: true,
            windowWidth: document.documentElement.scrollWidth,
            windowHeight: document.documentElement.scrollHeight,
          });

          // khôi phục style (nếu có thay đổi)
          pageEl.style.width = originalWidth;
          pageEl.style.height = originalHeight;

          const imgData = canvas.toDataURL("image/jpeg", 0.85);

          if (isA5) {
            pdf.addImage(imgData, "JPEG", 0, 0, pageWidth, pageHeight);
          } else {
            // A4: scale theo chiều ngang toàn trang
            const imgPixelWidth = canvas.width;
            const imgPixelHeight = canvas.height;
            const imgMmWidth = pageWidth;
            const imgMmHeight = imgPixelHeight * (pageWidth / imgPixelWidth);
            pdf.addImage(imgData, "JPEG", 0, 0, imgMmWidth, imgMmHeight);
          }

          canvas.remove();
        }

        // Cập nhật progress
        this.progressPdf = Math.round(((i + 1) / pageElements.length) * 100);
        this.cdr.detectChanges();
      }

      // Save PDF
      const fileName = `phieu-thong-tin-${this.paperSize}-${Date.now()}.pdf`;
      pdf.save(fileName);
      console.log(`PDF saved: ${fileName}`);

      // Cập nhật print status
      await this.updatePrintStatusAfterPrint(unprintedPallets);
    } catch (err) {
      console.error("Error generating PDF:", err);
      this.snackBar.open("Lỗi khi xuất PDF", "Đóng", { duration: 3000 });
    } finally {
      // Khôi phục watermark
      watermarkEls.forEach((el, idx) => {
        el.style.display = watermarkOldDisplays[idx] ?? "";
      });

      // Khôi phục các style đã lưu
      for (const s of savedStyles) {
        if (s.styles.width !== undefined) {
          s.el.style.width = s.styles.width as string;
        }
        if (s.styles.display !== undefined) {
          s.el.style.display = s.styles.display as string;
        }
        if (s.styles.transform !== undefined) {
          s.el.style.transform = s.styles.transform as string;
        }
        if (s.styles.margin !== undefined) {
          s.el.style.margin = s.styles.margin as string;
        }
        if (s.styles.boxSizing !== undefined) {
          s.el.style.boxSizing = s.styles.boxSizing as string;
        }
      }

      // Khôi phục displayPages gốc
      this.displayPages = originalPages;
      this.isLoadingPdf = false;
      this.progressPdf = 0;
      this.cdr.detectChanges();
      printContent?.classList.remove("exporting-pdf");
    }
  }
  private async convertQRCodesToImages(): Promise<void> {
    const qrCanvases = Array.from(
      document.querySelectorAll(".print-content canvas"),
    ) as HTMLCanvasElement[];

    for (const canvas of qrCanvases) {
      try {
        if (canvas.width === 0 || canvas.height === 0) {
          continue;
        }

        const dataUrl = canvas.toDataURL("image/png");
        const img = document.createElement("img");
        img.src = dataUrl;
        img.width = canvas.width;
        img.height = canvas.height;
        img.style.cssText = canvas.style.cssText;
        img.className = canvas.className;

        if (canvas.parentNode) {
          canvas.parentNode.replaceChild(img, canvas);
        }
      } catch (err) {
        console.error("QR conversion failed:", err);
      }
    }

    // Đợi images load
    const images = Array.from(document.querySelectorAll(".print-content img"));
    await Promise.all(
      images.map((img: any) => {
        if (img.complete) {
          return Promise.resolve();
        }
        return new Promise((resolve) => {
          img.onload = resolve;
          img.onerror = resolve;
        });
      }),
    );
  }

  private async updatePrintStatusAfterPrint(
    pallets: PrintPalletData[],
  ): Promise<void> {
    try {
      const currentUser = this.accountService.isAuthenticated()
        ? this.accountService["userIdentity"]?.login
        : "unknown";
      const payload = pallets
        .filter((p) => p.id != null)
        .map((p) => ({
          id: p.id!,
          print_status: true,
          updated_by: currentUser ?? "unknown",
        }));
      const missingId = pallets.filter((p) => p.id == null);
      if (missingId.length > 0) {
        console.error("Pallet thiếu ID:", missingId);
        throw new Error(
          "Không thể cập nhật trạng thái in vì có pallet thiếu ID",
        );
      }

      await firstValueFrom(
        this.planningService.updatePalletPrintStatus(payload),
      );

      // Cập nhật printStatus trong local data
      pallets.forEach((p) => {
        p.printStatus = true;
      });

      console.log(`Updated print status for ${pallets.length} pallets`);
      this.snackBar.open(
        `Đã cập nhật trạng thái in cho ${pallets.length} phiếu`,
        "Đóng",
        {
          duration: 2000,
        },
      );
    } catch (error) {
      console.error("Error updating print status:", error);
      this.snackBar.open("Lỗi khi cập nhật trạng thái in", "Đóng", {
        duration: 3000,
      });
    }
  }
}
