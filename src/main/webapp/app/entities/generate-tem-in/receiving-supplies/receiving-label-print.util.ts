import JsBarcode from "jsbarcode";

export interface LabelBarcodeSource {
  id: string;
  arrivalDate: string;
  userData5: string;
}

export function generateLabelBarcodes(labels: LabelBarcodeSource[]): void {
  labels.forEach((label, i) => {
    const uniqueId = `${label.id}-${i}`;
    const nvTarget = document.getElementById(`barcode-nv-${uniqueId}`);
    const poTarget = document.getElementById(`barcode-po-${uniqueId}`);

    const rawDate = label.arrivalDate ?? "000000";
    const compactDate = rawDate.replace(/-/g, "").slice(2);

    if (nvTarget) {
      try {
        JsBarcode(nvTarget, compactDate ?? "000000", {
          format: "CODE128",
          lineColor: "#000",
          width: 2,
          height: 25,
          displayValue: false,
          margin: 0,
        });
      } catch (e) {
        console.error(`Barcode NV error at ${i}:`, e);
      }
    }

    if (poTarget) {
      try {
        JsBarcode(poTarget, label.userData5 ?? "00000", {
          format: "CODE128",
          lineColor: "#000",
          width: 2,
          height: 25,
          displayValue: false,
          margin: 0,
        });
      } catch (e) {
        console.error(`Barcode PO error at ${i}:`, e);
      }
    }
  });
}

const LABEL_PRINT_CSS = `
    @media print {
      @page {
        size: 102.5mm 35mm;
        margin: 0;
      }

      * {
        margin: 0;
        padding: 0;
      }

      body {
        margin: 0 !important;
        padding: 0 !important;
        width: 102.5mm !important;
        height: 35mm !important;
      }

      body > * {
        display: none !important;
      }

      #printPreviewClone {
        display: block !important;
        visibility: visible !important;
        position: static !important;
        margin: 0 !important;
        padding: 0 !important;
        overflow: visible !important;
        width: 102.5mm !important;
        height: auto !important;
      }

      #printPreviewClone,
      #printPreviewClone * {
        visibility: visible !important;
      }

      #printPreviewClone .label {
        width: 102.5mm !important;
        height: 35mm !important;
        margin: 0 !important;
        padding: 1mm 3mm !important;
        position: relative !important;
        overflow: hidden !important;
        background: white !important;
        page-break-after: always !important;
        page-break-inside: avoid !important;
        display: flex !important;
        flex-direction: column !important;
      }

      #printPreviewClone .label:last-of-type {
        page-break-after: avoid !important;
      }

      #printPreviewClone .label-content {
        position: relative !important;
        width: 100% !important;
        flex: 1 !important;
        padding: 0.8mm !important;
        gap: 5mm !important;
        transform: none !important;
        background: white !important;
        box-sizing: border-box !important;
        display: flex !important;
      }

      #printPreviewClone .col-left {
        width: 22mm !important;
        gap: 1mm !important;
        padding: 1mm !important;
        display: flex !important;
        flex-direction: column !important;
      }

      #printPreviewClone .qr-box {
        width: 18mm !important;
        height: 18mm !important;
        border: 0.3px solid #000 !important;
        border-radius: 3px !important;
        padding: 1mm !important;
        display: flex !important;
        align-items: center !important;
        justify-content: center !important;
      }

      #printPreviewClone .qr-box qrcode,
      #printPreviewClone .qr-box canvas {
        width: 16mm !important;
        height: 16mm !important;
        max-width: 16mm !important;
        max-height: 16mm !important;
      }

      #printPreviewClone .msd-info {
        font-size: 5.5pt !important;
        line-height: 1.1 !important;
      }

      #printPreviewClone .msd-title {
        font-size: 6pt !important;
        margin-bottom: 0.3mm !important;
      }

      #printPreviewClone .msd-item {
        font-size: 6.5pt !important;
        line-height: 1.15 !important;
        font-weight: bold !important;
      }

      #printPreviewClone .col-center {
        flex: 1 !important;
        gap: 1mm !important;
        display: flex !important;
        flex-direction: column !important;
      }

      #printPreviewClone .logo-box {
        height: 5mm !important;
      }

      #printPreviewClone .logo-img {
        height: 4.5mm !important;
      }

      #printPreviewClone .sap-code {
        font-size: 16pt !important;
        line-height: 1 !important;
      }

      #printPreviewClone .part-number {
        font-size: 10pt !important;
        line-height: 1 !important;
      }

      #printPreviewClone .reel-id-tem {
        font-size: 8pt !important;
        line-height: 1 !important;
      }

      #printPreviewClone .info-grid-tem {
        gap: 0.2mm !important;
        line-height: 1 !important;
        display: flex !important;
        flex-direction: column !important;
      }

      #printPreviewClone .info-row-tem {
        font-size: 6pt !important;
        line-height: 1.1 !important;
        gap: 1mm !important;
        display: flex !important;
      }

      #printPreviewClone .info-label-tem {
        min-width: 10mm !important;
        font-weight: bold !important;
      }

      #printPreviewClone .qty-big {
        font-size: 9pt !important;
        font-weight: bold !important;
      }

      #printPreviewClone .info-value-tem {
        font-size: 7pt !important;
        font-weight: bold !important;
      }

      #printPreviewClone .col-right {
        width: 28mm !important;
        gap: 0.3mm !important;
        display: flex !important;
        flex-direction: column !important;
      }

      #printPreviewClone .rank-section {
        gap: 0.2mm !important;
        display: flex !important;
        flex-direction: column !important;
      }

      #printPreviewClone .rank-item-small {
        font-size: 5.5pt !important;
        line-height: 1.1 !important;
        font-weight: bold !important;
        display: flex !important;
        justify-content: space-between !important;
      }

      #printPreviewClone .storage-unit {
        font-size: 6pt !important;
        padding: 0.3mm 0.5mm !important;
        line-height: 1.2 !important;
      }

      #printPreviewClone .lot-info {
        font-size: 5.5pt !important;
        line-height: 1.1 !important;
        display: flex !important;
        gap: 1mm !important;
      }

      #printPreviewClone .barcode-box {
        padding: 0.5mm !important;
        gap: 0.2mm !important;
        max-height: 15.5mm !important;
        border: 0.5px solid #333 !important;
        display: flex !important;
        flex-direction: column !important;
      }

      #printPreviewClone .bc-item-compact {
        gap: 0.2mm !important;
        display: flex !important;
        flex-direction: column !important;
      }

      #printPreviewClone .bc-line {
        display: flex !important;
        align-items: center !important;
        gap: 0.3mm !important;
      }

      #printPreviewClone .bc-label {
        font-size: 5.5pt !important;
        line-height: 1 !important;
        font-weight: bold !important;
      }

      #printPreviewClone .bc-code {
        font-size: 5.5pt !important;
        line-height: 1 !important;
      }

      #printPreviewClone .bc-svg {
        height: 3.5mm !important;
        flex: 1 !important;
      }

      #printPreviewClone .bc-svg svg {
        height: 100% !important;
        width: auto !important;
        max-width: 100% !important;
        object-fit: contain !important;
      }

      #printPreviewClone .product-name-small {
        display: block !important;
        visibility: visible !important;
        font-size: 7pt !important;
        line-height: 0.4 !important;
        padding: 0.5mm 1mm !important;
        margin: 0 !important;
        width: 100% !important;
        box-sizing: border-box !important;
        overflow: hidden !important;
        text-overflow: ellipsis !important;
        white-space: nowrap !important;
        background: white !important;
      }

      #printPreviewClone * {
        print-color-adjust: exact !important;
        -webkit-print-color-adjust: exact !important;
      }

      #printPreviewClone canvas,
      #printPreviewClone qrcode,
      #printPreviewClone qrcode canvas,
      #printPreviewClone svg,
      #printPreviewClone img {
        visibility: visible !important;
        display: block !important;
        opacity: 1 !important;
        print-color-adjust: exact !important;
        -webkit-print-color-adjust: exact !important;
      }

      body>*:not(#printPreviewClone) {
        display: none !important;
      }
    }
  `;

export function runLabelPrint(
  modalId: string,
  onAfterPrint?: () => void,
): void {
  const modal = document.getElementById(modalId);
  if (!modal) {
    console.error("Modal not found!");
    return;
  }

  const container = modal.querySelector(".print-preview-container");
  if (!container) {
    console.error("Container not found!");
    return;
  }

  const printContainer = container.cloneNode(true) as HTMLElement;
  printContainer.id = "printPreviewClone";

  const originalCanvases = container.querySelectorAll("canvas");
  const clonedCanvases = printContainer.querySelectorAll("canvas");

  originalCanvases.forEach((originalCanvas, index) => {
    const clonedCanvas = clonedCanvases[index] as HTMLCanvasElement;
    const ctx = clonedCanvas.getContext("2d");
    if (ctx) {
      clonedCanvas.width = originalCanvas.width;
      clonedCanvas.height = originalCanvas.height;
      ctx.drawImage(originalCanvas, 0, 0);
    }
  });

  printContainer.style.display = "none";
  modal.style.display = "none";
  document.body.appendChild(printContainer);

  const style = document.createElement("style");
  style.id = "temp-print-style";
  style.textContent = LABEL_PRINT_CSS;
  document.head.appendChild(style);

  setTimeout(() => {
    window.print();

    const cleanup = (): void => {
      printContainer.remove();
      style.remove();
      modal.style.display = "block";
      window.removeEventListener("afterprint", cleanup);
    };

    window.addEventListener("afterprint", cleanup);
    setTimeout(cleanup, 3000);
    onAfterPrint?.();
  }, 500);
}
