import QRCode from "qrcode";
import {
  getVendorNccPaperDimensions,
  VENDOR_NCC_SHIPPING_ICONS_SRC,
  VendorNccLabelData,
  VendorNccPaperSize,
} from "./vendor-ncc-label-print.util";

/**
 * In tem NCC bằng 1 tài liệu HTML độc lập: HTML + CSS của tem viết sẵn ở đây (đơn vị mm),
 * QR sinh thành ảnh, không dùng CSS / DOM của app (CSS in của app — vd Bootstrap
 * `@page { size: a3 }`, `body { min-width: 992px }` — làm sai khổ giấy và co tem).
 * Mỗi tem = 1 trang, khổ trang = khổ tem (40x100 → 100×40mm, 100x100, A5 ngang).
 */

function esc(value: unknown): string {
  return String(value ?? "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

async function qrDataUrl(text: string): Promise<string> {
  if (!text) {
    return "";
  }
  try {
    return await QRCode.toDataURL(text, {
      errorCorrectionLevel: "M",
      margin: 0,
      width: 512,
    });
  } catch {
    return "";
  }
}

function iconsUrl(): string {
  return new URL(VENDOR_NCC_SHIPPING_ICONS_SRC, document.baseURI).href;
}

function stripLabelHtml(l: VendorNccLabelData, qr: string): string {
  const field = (key: string, value: string): string =>
    `<div class="field"><span class="key">${key}</span><span class="val">${value}</span></div>`;
  return `
<div class="label strip">
  <div class="qr">${qr ? `<img src="${qr}" alt="QR" />` : ""}</div>
  <div class="info">
    <div class="name">${esc(l.vendorItemName)}</div>
    ${field("Mã part:", esc(l.partNumber))}
    ${field("Số lượng:", `${esc(l.quantity)} PCS`)}
    ${field("Khối lượng:", l.weight ? `${esc(l.weight)}&nbsp;KG` : "")}
    ${field("Hóa đơn:", esc(l.invoiceNo))}
    ${field("Số thùng:", esc(l.boxNo))}
    ${field("Nhà cung cấp:", esc(l.vendorName))}
    ${field("Xuất xứ:", esc(l.madeIn))}
  </div>
  <div class="side">
    <div class="extra">
      ${field("Màu sắc:", esc(l.color))}
      ${field("Người vận hành:", esc(l.operator))}
    </div>
    <img class="icons" src="${iconsUrl()}" alt="" />
  </div>
</div>`;
}

function sheetLabelHtml(l: VendorNccLabelData, qr: string): string {
  return `
<div class="label sheet">
  <table>
    <colgroup>
      <col class="c1" /><col class="c2" /><col class="c3" /><col class="c4" />
    </colgroup>
    <tr class="head">
      <td class="qr-cell">${qr ? `<img src="${qr}" alt="QR" />` : ""}</td>
      <td class="codes" colspan="3">
        <div class="code part">${esc(l.partNumber)}</div>
        <div class="code name">${esc(l.vendorItemName)}</div>
      </td>
    </tr>
    <tr><td class="k">Số lượng:</td><td colspan="2">${esc(l.quantity)}</td><td class="u">PCS</td></tr>
    <tr><td class="k">Khối lượng:</td><td colspan="2">${esc(l.weight)}</td><td class="u">KG</td></tr>
    <tr><td class="k">Kích thước:</td><td colspan="2">${esc(l.meas)}</td><td class="u">cm</td></tr>
    <tr><td class="k">Số PO:</td><td>${esc(l.poNo)}</td><td class="k">Số hóa đơn:</td><td>${esc(l.invoiceNo)}</td></tr>
    <tr><td class="k">Màu sắc:</td><td>${esc(l.color)}</td><td class="k">Người vận hành:</td><td>${esc(l.operator)}</td></tr>
    <tr class="tail"><td class="k">Số thùng:</td><td colspan="3">${esc(l.boxNo)}</td></tr>
    <tr class="tail"><td class="k">Nhà cung cấp:</td><td colspan="3">${esc(l.vendorName)}</td></tr>
    <tr class="tail"><td class="k">Xuất xứ:</td><td colspan="3">${esc(l.madeIn)}</td></tr>
  </table>
  <div class="icons-box"><img src="${iconsUrl()}" alt="" /></div>
</div>`;
}

/** CSS tem theo khổ — toàn bộ đơn vị mm/pt, không phụ thuộc CSS app */
function labelCss(paperSize: VendorNccPaperSize): string {
  const { widthMm, heightMm } = getVendorNccPaperDimensions(paperSize);
  const a5 = paperSize === "A5";
  // Khổ sheet: 100x100 / A5
  const headH = a5 ? 42 : 28;
  const qrMax = a5 ? 36 : 24;
  const codeFont = a5 ? 18 : 11;
  const cellFont = a5 ? 12 : 8;
  const keyFont = a5 ? 12 : 7;
  const cellPad = a5 ? "1.5mm 2.5mm" : "0.8mm 1.5mm";
  const iconBox = a5 ? 40.3 : 25;
  return `
@page { size: ${widthMm}mm ${heightMm}mm; margin: 0; }
* { box-sizing: border-box; -webkit-print-color-adjust: exact; print-color-adjust: exact; }
html, body { margin: 0; padding: 0; background: #fff; }
body { font-family: Arial, Helvetica, sans-serif; color: #000; }
.page {
  width: ${widthMm}mm; height: ${heightMm}mm; overflow: hidden;
  page-break-after: always; break-after: page;
}
.page:last-child { page-break-after: auto; break-after: auto; }
.label { width: 100%; height: 100%; border: 0.4mm solid #000; overflow: hidden; background: #fff; }

/* ===== 40x100 (dải ngang 100×40mm) ===== */
.strip { display: flex; flex-direction: row; align-items: stretch; gap: 1.2mm; padding: 1mm; }
.strip .qr { width: 23mm; flex: 0 0 23mm; display: flex; align-items: center; justify-content: center; }
.strip .qr img { width: 23mm; height: 23mm; display: block; image-rendering: pixelated; }
.strip .info { flex: 1 1 auto; min-width: 0; display: flex; flex-direction: column; justify-content: center; overflow: hidden; padding: 0.4mm 0.5mm 0.4mm 0; }
.strip .name {
  font-size: 7.5pt; font-weight: 700; line-height: 1.12; margin-bottom: 1.2mm;
  display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 3; overflow: hidden; word-break: break-word;
}
.strip .field { display: flex; gap: 2px; font-size: 6.5pt; line-height: 1.15; }
.strip .field .key { font-weight: 600; min-width: 12mm; flex-shrink: 0; }
.strip .field .val { min-width: 0; word-break: break-word; }
.strip .side { width: 24mm; flex: 0 0 24mm; display: flex; flex-direction: column; justify-content: center; gap: 0.3mm; }
.strip .extra .field { flex-wrap: wrap; font-size: 6pt; }
.strip .extra .field .key { min-width: 0; }
.strip .side .icons { width: 22mm; height: auto; display: block; }

/* ===== 100x100 / A5 (bảng) ===== */
.sheet { position: relative; }
.sheet table { width: 100%; height: 100%; border-collapse: collapse; table-layout: fixed; }
.sheet col.c1 { width: 23%; } .sheet col.c2 { width: 27%; } .sheet col.c3 { width: 28%; } .sheet col.c4 { width: 22%; }
.sheet td { border: 0.25mm solid #000; padding: ${cellPad}; vertical-align: middle; font-size: ${cellFont}pt; line-height: 1.2; word-break: break-word; }
.sheet tr.head { height: ${headH}mm; }
.sheet td.qr-cell { padding: 1.5mm; text-align: center; }
.sheet td.qr-cell img { width: ${qrMax}mm; height: ${qrMax}mm; max-width: 100%; display: block; margin: 0 auto; image-rendering: pixelated; }
.sheet td.codes { padding: 0; }
.sheet .code { font-size: ${codeFont}pt; font-weight: 700; text-align: center; padding: 1.5mm 2mm; line-height: 1.25; }
.sheet .code.part { word-break: break-all; }
.sheet .code + .code { border-top: 0.25mm solid #000; }
.sheet td.k { font-size: ${keyFont}pt; font-weight: 500; white-space: nowrap; }
.sheet td.u { text-align: center; white-space: nowrap; }
.sheet tr.tail td:last-child { padding-right: ${iconBox + 2}mm; }
.sheet .icons-box {
  position: absolute; right: 0; bottom: 0; width: ${iconBox}mm; height: ${iconBox}mm;
  background: #fff; border-left: 0.25mm solid #000; border-top: 0.25mm solid #000; overflow: hidden;
}
.sheet .icons-box img { width: 100%; height: 100%; object-fit: contain; display: block; }
`;
}

/** Dựng toàn bộ tài liệu in (mỗi tem 1 trang) */
export async function buildVendorNccPrintDocument(
  labels: VendorNccLabelData[],
  paperSize: VendorNccPaperSize,
): Promise<string> {
  const strip = paperSize === "40x100";
  const pages = await Promise.all(
    labels.map(async (l) => {
      const qr = await qrDataUrl(l.qrCode);
      const html = strip ? stripLabelHtml(l, qr) : sheetLabelHtml(l, qr);
      return `<div class="page">${html}</div>`;
    }),
  );
  return `<!doctype html>
<html>
<head>
<meta charset="utf-8" />
<title>In tem NCC</title>
<style>${labelCss(paperSize)}</style>
</head>
<body>${pages.join("")}</body>
</html>`;
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
 * In tem trong iframe ẩn chỉ chứa tài liệu tem. iframe gỡ khi đóng hộp thoại in
 * (hoặc sau 60s nếu trình duyệt không bắn afterprint).
 */
export async function printVendorNccLabels(
  labels: VendorNccLabelData[],
  paperSize: VendorNccPaperSize,
): Promise<void> {
  if (!labels.length) {
    return;
  }
  const html = await buildVendorNccPrintDocument(labels, paperSize);

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
