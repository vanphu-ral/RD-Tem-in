import JsBarcode from "jsbarcode";
import jsPDF from "jspdf";
import QRCode from "qrcode";
import { PalletItem, PalletLabelSize } from "./pallet-management.model";

export interface PalletPrintOptions {
  pallets: PalletItem[];
  labelSize: PalletLabelSize;
  labelsPerRow: number;
}

const PRINT_ROOT_ID = "pallet-label-print-root";
const PRINT_STYLE_ID = "pallet-label-print-style";

function escapeHtml(value: string): string {
  return String(value ?? "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

async function toQrDataUrl(text: string): Promise<string> {
  return QRCode.toDataURL(text, {
    width: 280,
    margin: 1,
    errorCorrectionLevel: "M",
  });
}

function toBarcodeDataUrl(text: string): string {
  const canvas = document.createElement("canvas");
  JsBarcode(canvas, text, {
    format: "CODE128",
    lineColor: "#000",
    width: 2,
    height: 70,
    displayValue: false,
    margin: 6,
  });
  return canvas.toDataURL("image/png");
}

function buildPrintCss(
  labelSize: PalletLabelSize,
  labelsPerRow: number,
): string {
  const isQr = labelSize === "100x100";
  const perRow = isQr ? Math.min(2, Math.max(1, labelsPerRow)) : 1;
  const labelWidth = isQr ? "100mm" : "100mm";
  const labelHeight = isQr ? "100mm" : "40mm";

  return `
    #${PRINT_ROOT_ID} {
      display: none;
    }

    @media print {
      @page { size: A4; margin: 8mm; }

      body * {
        visibility: hidden !important;
      }

      #${PRINT_ROOT_ID},
      #${PRINT_ROOT_ID} * {
        visibility: visible !important;
      }

      #${PRINT_ROOT_ID} {
        display: block !important;
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
        margin: 0;
        padding: 0;
        background: #fff;
        z-index: 99999;
      }

      .cdk-overlay-container,
      .cdk-global-overlay-wrapper,
      .mat-mdc-dialog-container {
        display: none !important;
      }

      #${PRINT_ROOT_ID} .sheet {
        display: flex;
        flex-wrap: wrap;
        gap: 4mm;
        align-content: flex-start;
      }

      #${PRINT_ROOT_ID} .label {
        width: calc((100% - ${(perRow - 1) * 4}mm) / ${perRow});
        max-width: ${labelWidth};
        height: ${labelHeight};
        border: 1px solid #222;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        padding: 3mm;
        page-break-inside: avoid;
        overflow: hidden;
        box-sizing: border-box;
      }

      #${PRINT_ROOT_ID} .label img {
        max-width: 100%;
        max-height: ${isQr ? "72mm" : "22mm"};
        object-fit: contain;
      }

      #${PRINT_ROOT_ID} .code {
        margin-top: 2mm;
        font-size: ${isQr ? "11px" : "12px"};
        font-weight: 700;
        text-align: center;
        word-break: break-all;
        line-height: 1.25;
        font-family: Arial, Helvetica, sans-serif;
      }
    }
  `;
}

function ensurePrintStyle(
  labelSize: PalletLabelSize,
  labelsPerRow: number,
): void {
  let styleEl = document.getElementById(
    PRINT_STYLE_ID,
  ) as HTMLStyleElement | null;
  if (!styleEl) {
    styleEl = document.createElement("style");
    styleEl.id = PRINT_STYLE_ID;
    document.head.appendChild(styleEl);
  }
  styleEl.textContent = buildPrintCss(labelSize, labelsPerRow);
}

function cleanupPrintRoot(): void {
  document.getElementById(PRINT_ROOT_ID)?.remove();
}

/** In ngay trên trang hiện tại (giống Ctrl+P), không mở popup. */
export async function printPalletLabels(
  options: PalletPrintOptions,
): Promise<void> {
  const { pallets, labelSize, labelsPerRow } = options;
  if (!pallets.length) {
    return;
  }

  const isQr = labelSize === "100x100";
  const cards: string[] = [];

  for (const pallet of pallets) {
    const code = pallet.serialPallet ?? "";
    const img = isQr ? await toQrDataUrl(code) : toBarcodeDataUrl(code);
    cards.push(`
      <div class="label">
        <img src="${img}" alt="${escapeHtml(code)}" />
        <div class="code">${escapeHtml(code)}</div>
      </div>
    `);
  }

  cleanupPrintRoot();
  ensurePrintStyle(labelSize, labelsPerRow);

  const root = document.createElement("div");
  root.id = PRINT_ROOT_ID;
  root.innerHTML = `<div class="sheet">${cards.join("")}</div>`;
  document.body.appendChild(root);

  const onAfterPrint = (): void => {
    cleanupPrintRoot();
    window.removeEventListener("afterprint", onAfterPrint);
  };
  window.addEventListener("afterprint", onAfterPrint);

  // Đợi ảnh decode xong rồi mới mở hộp thoại in hệ thống
  await Promise.all(
    Array.from(root.querySelectorAll("img")).map((img) =>
      img.complete
        ? Promise.resolve()
        : new Promise<void>((resolve) => {
            img.onload = () => resolve();
            img.onerror = () => resolve();
          }),
    ),
  );

  window.print();
}

/** Ảnh barcode kèm kích thước gốc (để giữ tỉ lệ khi đặt vào PDF) */
function toBarcodeImage(text: string): { url: string; w: number; h: number } {
  const canvas = document.createElement("canvas");
  JsBarcode(canvas, text, {
    format: "CODE128",
    lineColor: "#000",
    width: 2,
    height: 70,
    displayValue: false,
    margin: 6,
  });
  return {
    url: canvas.toDataURL("image/png"),
    w: canvas.width,
    h: canvas.height,
  };
}

/**
 * Xuất tem pallet ra file PDF (A4, lề 8mm, khoảng cách 4mm — cùng bố cục với bản in)
 * và tải xuống luôn, không mở hộp thoại in.
 */
export async function exportPalletLabelsPdf(
  options: PalletPrintOptions,
  fileName = `tem-pallet-${new Date().toISOString().slice(0, 10)}.pdf`,
): Promise<void> {
  const { pallets, labelSize, labelsPerRow } = options;
  if (!pallets.length) {
    return;
  }
  const isQr = labelSize === "100x100";
  const perRow = isQr ? Math.min(2, Math.max(1, labelsPerRow)) : 1;

  const doc = new jsPDF({ unit: "mm", format: "a4", orientation: "portrait" });
  const pageW = doc.internal.pageSize.getWidth();
  const pageH = doc.internal.pageSize.getHeight();
  const margin = 8;
  const gap = 4;
  const usableW = pageW - margin * 2;
  const labelW = Math.min(100, (usableW - (perRow - 1) * gap) / perRow);
  const labelH = isQr ? labelW : 40;
  const pad = 3;
  const textH = 6;

  let x = margin;
  let y = margin;
  let col = 0;

  for (const pallet of pallets) {
    const code = pallet.serialPallet ?? "";
    if (y + labelH > pageH - margin) {
      doc.addPage();
      y = margin;
      x = margin;
      col = 0;
    }

    doc.setDrawColor(34, 34, 34);
    doc.setLineWidth(0.3);
    doc.rect(x, y, labelW, labelH);

    const boxW = labelW - pad * 2;
    const boxH = labelH - pad * 2 - textH;
    if (isQr) {
      const url = await toQrDataUrl(code);
      const size = Math.min(boxW, boxH, 72);
      doc.addImage(
        url,
        "PNG",
        x + (labelW - size) / 2,
        y + pad + (boxH - size) / 2,
        size,
        size,
      );
    } else {
      const img = toBarcodeImage(code);
      const ratio = img.w / img.h;
      let w = boxW;
      let h = w / ratio;
      if (h > Math.min(boxH, 22)) {
        h = Math.min(boxH, 22);
        w = h * ratio;
      }
      doc.addImage(
        img.url,
        "PNG",
        x + (labelW - w) / 2,
        y + pad + (boxH - h) / 2,
        w,
        h,
      );
    }

    doc.setFont("helvetica", "bold");
    doc.setFontSize(isQr ? 11 : 12);
    doc.text(code, x + labelW / 2, y + labelH - pad - 1.5, {
      align: "center",
      maxWidth: boxW,
    });

    col++;
    if (col >= perRow) {
      col = 0;
      x = margin;
      y += labelH + gap;
    } else {
      x += labelW + gap;
    }
  }

  doc.save(fileName);
}
