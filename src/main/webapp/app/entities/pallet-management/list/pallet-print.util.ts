import JsBarcode from "jsbarcode";
import jsPDF from "jspdf";
import QRCode from "qrcode";
import { PalletItem, PalletLabelSize } from "./pallet-management.model";

export interface PalletPrintOptions {
  pallets: PalletItem[];
  labelSize: PalletLabelSize;
}

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

/** Khổ tem (mm) = khổ 1 trang in: QR 100×100, barcode rộng 100 × cao 40 */
function labelPageSize(labelSize: PalletLabelSize): { w: number; h: number } {
  return labelSize === "100x100" ? { w: 100, h: 100 } : { w: 100, h: 40 };
}

/**
 * CSS tài liệu in: mỗi tem pallet là 1 trang, khổ trang = khổ tem, không lề.
 * Chạy trong iframe riêng — không dính CSS in của app (Bootstrap `@page { size: a3 }`,
 * `body { min-width: 992px }` làm sai khổ trang và co tem).
 */
function buildPrintCss(labelSize: PalletLabelSize): string {
  const isQr = labelSize === "100x100";
  const { w, h } = labelPageSize(labelSize);
  return `
@page { size: ${w}mm ${h}mm; margin: 0; }
* { box-sizing: border-box; -webkit-print-color-adjust: exact; print-color-adjust: exact; }
html, body { margin: 0; padding: 0; background: #fff; }
.label {
  width: ${w}mm; height: ${h}mm; overflow: hidden;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: ${isQr ? "4mm" : "2mm 3mm"};
  page-break-after: always; break-after: page;
}
.label:last-child { page-break-after: auto; break-after: auto; }
.label img { display: block; ${isQr ? "width: 78mm; height: 78mm;" : "width: 94mm; height: 26mm;"} object-fit: contain; }
.code {
  margin-top: ${isQr ? "2mm" : "1mm"}; font: 700 ${isQr ? "14pt" : "11pt"}/1.2 Arial, Helvetica, sans-serif;
  text-align: center; word-break: break-all;
}
`;
}

/** Chờ mọi ảnh trong tài liệu tải xong (lỗi cũng bỏ qua) */
function waitImages(doc: Document): Promise<void> {
  const images = Array.from(doc.images).filter((img) => !img.complete);
  return Promise.all(
    images.map(
      (img) =>
        new Promise<void>((resolve) => {
          img.addEventListener("load", () => resolve(), { once: true });
          img.addEventListener("error", () => resolve(), { once: true });
        }),
    ),
  ).then(() => undefined);
}

/**
 * In tem pallet trong iframe ẩn chỉ chứa tài liệu tem (mỗi tem 1 trang).
 * iframe gỡ khi đóng hộp thoại in (hoặc sau 60s nếu trình duyệt không bắn afterprint).
 */
export async function printPalletLabels(
  options: PalletPrintOptions,
): Promise<void> {
  const { pallets, labelSize } = options;
  if (!pallets.length) {
    return;
  }

  const isQr = labelSize === "100x100";
  const cards: string[] = [];
  for (const pallet of pallets) {
    const code = pallet.serialPallet ?? "";
    const img = isQr ? await toQrDataUrl(code) : toBarcodeDataUrl(code);
    cards.push(
      `<div class="label"><img src="${img}" alt="${escapeHtml(code)}" /><div class="code">${escapeHtml(code)}</div></div>`,
    );
  }
  const html = `<!doctype html>
<html>
<head>
<meta charset="utf-8" />
<title>In tem pallet</title>
<style>${buildPrintCss(labelSize)}</style>
</head>
<body>${cards.join("")}</body>
</html>`;

  const iframe = document.createElement("iframe");
  iframe.setAttribute("aria-hidden", "true");
  iframe.style.cssText =
    "position:fixed;right:0;bottom:0;width:0;height:0;border:0;visibility:hidden;";
  document.body.appendChild(iframe);
  const doc = iframe.contentDocument;
  const win = iframe.contentWindow;
  if (!doc || !win) {
    iframe.remove();
    return;
  }
  doc.open();
  doc.write(html);
  doc.close();
  await waitImages(doc);

  let removed = false;
  const cleanup = (): void => {
    if (!removed) {
      removed = true;
      iframe.remove();
    }
  };
  win.addEventListener("afterprint", cleanup);
  win.focus();
  win.print();
  setTimeout(cleanup, 60000);
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
 * Xuất tem pallet ra file PDF — mỗi tem 1 trang, khổ trang = khổ tem (cùng bố cục với bản in)
 * và tải xuống luôn, không mở hộp thoại in.
 */
export async function exportPalletLabelsPdf(
  options: PalletPrintOptions,
  fileName = `tem-pallet-${new Date().toISOString().slice(0, 10)}.pdf`,
): Promise<void> {
  const { pallets, labelSize } = options;
  if (!pallets.length) {
    return;
  }
  const isQr = labelSize === "100x100";
  const { w: labelW, h: labelH } = labelPageSize(labelSize);
  const orientation = labelW > labelH ? "landscape" : "portrait";

  const doc = new jsPDF({ unit: "mm", format: [labelW, labelH], orientation });
  const pad = 3;
  const textH = 6;
  const boxW = labelW - pad * 2;
  const boxH = labelH - pad * 2 - textH;

  for (let i = 0; i < pallets.length; i++) {
    const code = pallets[i].serialPallet ?? "";
    if (i > 0) {
      doc.addPage([labelW, labelH], orientation);
    }

    if (isQr) {
      const url = await toQrDataUrl(code);
      const size = Math.min(boxW, boxH, 80);
      doc.addImage(
        url,
        "PNG",
        (labelW - size) / 2,
        pad + (boxH - size) / 2,
        size,
        size,
      );
    } else {
      const img = toBarcodeImage(code);
      const ratio = img.w / img.h;
      let w = boxW;
      let h = w / ratio;
      if (h > Math.min(boxH, 24)) {
        h = Math.min(boxH, 24);
        w = h * ratio;
      }
      doc.addImage(
        img.url,
        "PNG",
        (labelW - w) / 2,
        pad + (boxH - h) / 2,
        w,
        h,
      );
    }

    doc.setFont("helvetica", "bold");
    doc.setFontSize(isQr ? 14 : 12);
    doc.text(code, labelW / 2, labelH - pad - 1, {
      align: "center",
      maxWidth: boxW,
    });
  }

  doc.save(fileName);
}
