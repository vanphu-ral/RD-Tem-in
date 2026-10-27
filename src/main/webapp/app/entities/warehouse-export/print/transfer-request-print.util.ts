/**
 * In phiếu "ĐỀ NGHỊ LĨNH VẬT TƯ" (theo mẫu phiếu yêu cầu chuyển kho nội bộ SAP):
 * A4 ngang, mọi yêu cầu của chi tiết xuất in chung 1 phiếu (mỗi dòng ghi rõ mã yêu cầu,
 * kho hàng đi / đến). In trong iframe ẩn, tài liệu tự chứa CSS — không phụ thuộc CSS của ứng dụng.
 */

export const TRANSFER_PRINT_COMPANY =
  "Công ty CP Bóng Đèn Phích Nước Rạng Đông";

/** 1 dòng vật tư trên phiếu */
export interface TransferPrintLine {
  /** Mã yêu cầu (docEntry) */
  requestCode: string;
  itemCode: string;
  itemName: string;
  partNumber: string;
  /** Các lô đã scan của vật tư — mỗi lô 1 dòng (Số lô / Ngày SX / Thực xuất riêng) */
  batches: TransferPrintBatch[];
  uom: string;
  /** Số lượng đề xuất (SL yêu cầu của phiếu SAP) */
  requestedQty: number;
  /** Tồn kho — null = không có số liệu (để trống) */
  stockQty: number | null;
  /** Thực xuất = SL đã scan */
  issuedQty: number;
  fromLabel: string;
  toLabel: string;
  note: string;
}

/** 1 lô của vật tư: SL thực xuất = tổng SL các thùng đã scan của lô đó */
export interface TransferPrintBatch {
  lot: string;
  mfgDate: string;
  qty: number;
}

/** Phiếu in (chung mọi yêu cầu) */
export interface TransferPrintDoc {
  /** Ngày giao hàng (nhiều yêu cầu khác ngày → nối bằng ", ") */
  deliveryDate: string;
  lines: TransferPrintLine[];
}

const esc = (v: unknown): string =>
  String(v ?? "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");

/** 1234.5 → "1,234.50" (cùng định dạng số trên phiếu SAP) */
const num = (v: number | null): string =>
  v === null
    ? ""
    : v.toLocaleString("en-US", {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      });

const CSS = `
@page {
  size: A4 landscape;
  margin: 12mm 14mm 16mm;
  /* Số trang do trình duyệt đánh (phiếu dài tự sang trang) */
  @bottom-right {
    content: "Page " counter(page) " of " counter(pages);
    width: 35mm;
    border-top: 0.25mm solid #000;
    font-family: Arial, Helvetica, sans-serif;
    font-size: 9pt;
    font-style: italic;
    text-align: right;
    vertical-align: top;
    padding-top: 1mm;
  }
}
* { box-sizing: border-box; }
html, body { margin: 0; padding: 0; }
body {
  font-family: Arial, "Helvetica Neue", Helvetica, sans-serif;
  font-size: 8pt;
  color: #000;
  -webkit-print-color-adjust: exact;
  print-color-adjust: exact;
}
/* Nội dung cách lề thêm một chút */
.page { padding: 2mm 4mm; }
.title {
  margin: 0;
  padding-bottom: 2mm;
  border-bottom: 0.4mm solid #2b7a78;
  text-align: center;
  font-size: 15pt;
  font-weight: 700;
  letter-spacing: 0.2pt;
}
.meta {
  display: flex;
  justify-content: space-between;
  align-items: end;
  padding: 2mm 0;
  border-bottom: 0.4mm solid #2b7a78;
  font-size: 10pt;
}
.meta .company { font-weight: 700; }
.meta .kv { display: flex; gap: 6mm; }
table {
  width: 100%;
  margin-top: 4mm;
  border-collapse: collapse;
  table-layout: fixed;
}
th, td {
  border: 0.25mm solid #000;
  padding: 0.8mm 1mm;
  line-height: 1.25;
  vertical-align: top;
}
th {
  font-weight: 700;
  text-align: center;
  vertical-align: middle;
}
td.c { text-align: center; }
td.r { text-align: right; white-space: nowrap; }
td.nw { white-space: nowrap; }
td.wrap { overflow-wrap: anywhere; }
thead { display: table-header-group; }
tr { page-break-inside: avoid; break-inside: avoid; }
/* Các dòng lô của 1 vật tư không bị tách sang 2 trang */
tbody.item { page-break-inside: avoid; break-inside: avoid; }
tbody.item tr.sub td { border-top-style: dashed; }
.total {
  display: flex;
  justify-content: flex-end;
  gap: 14mm;
  margin-top: 3mm;
}
.total .box {
  display: flex;
  justify-content: space-between;
  width: 70mm;
  padding: 0 2mm 1mm;
  border-bottom: 0.4mm solid #2b7a78;
}
.sign {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  margin-top: 4mm;
  text-align: center;
}
`;

function renderDoc(d: TransferPrintDoc): string {
  // Mỗi vật tư 1 nhóm dòng: các ô chung (rowspan) + mỗi lô 1 dòng (Số lô / Ngày SX / Thực xuất)
  const groups = d.lines
    .map((l, i) => {
      const batches: TransferPrintBatch[] = l.batches.length
        ? l.batches
        : [{ lot: "", mfgDate: "", qty: l.issuedQty }];
      const span = batches.length > 1 ? ` rowspan="${batches.length}"` : "";
      const lotCells = (b: TransferPrintBatch): string => `
        <td class="c wrap">${esc(b.lot)}</td>
        <td class="c nw">${esc(b.mfgDate)}</td>`;
      const tail = (b: TransferPrintBatch): string => `
        <td class="r">${num(b.qty)}</td>`;
      const first = batches[0];
      const rest = batches
        .slice(1)
        .map(
          (b) => `
      <tr class="sub">${lotCells(b)}${tail(b)}</tr>`,
        )
        .join("");
      return `
    <tbody class="item">
      <tr>
        <td class="c"${span}>${i + 1}</td>
        <td class="c"${span}>${esc(l.requestCode)}</td>
        <td class="c"${span}>${esc(l.itemCode)}</td>
        <td${span}>${esc(l.itemName)}</td>
        <td class="c wrap"${span}>${esc(l.partNumber).replace(/,/g, ",<wbr />")}</td>
        ${lotCells(first)}
        <td class="c"${span}>${esc(l.uom)}</td>
        <td class="r"${span}>${num(l.requestedQty)}</td>
        <td class="r"${span}>${num(l.stockQty)}</td>
        ${tail(first)}
        <td${span}>${esc(l.fromLabel)}</td>
        <td${span}>${esc(l.toLabel)}</td>
        <td${span}>${esc(l.note)}</td>
      </tr>${rest}
    </tbody>`;
    })
    .join("");
  const requested = d.lines.reduce((s, l) => s + l.requestedQty, 0);
  const issued = d.lines.reduce((s, l) => s + l.issuedQty, 0);
  return `
  <section class="page">
    <h1 class="title">ĐỀ NGHỊ LĨNH VẬT TƯ</h1>
    <div class="meta">
      <div class="company">${esc(TRANSFER_PRINT_COMPANY)}</div>
      <div class="kv"><span>Ngày giao hàng:</span><span>${esc(d.deliveryDate)}</span></div>
    </div>
    <table>
      <colgroup><col style="width:3%" /><col style="width:5.5%" /><col style="width:6.3%" /><col style="width:12.7%" /><col style="width:9%" /><col style="width:9.5%" /><col style="width:6.5%" /><col style="width:3.5%" /><col style="width:6.5%" /><col style="width:7%" /><col style="width:6%" /><col style="width:7.2%" /><col style="width:11.3%" /><col style="width:6%" /></colgroup>
      <thead>
        <tr>
          <th>STT</th><th>Mã<br />yêu cầu</th><th>Mã<br />hàng hóa</th><th>Tên hàng</th>
          <th>Part<br />Number</th><th>Số lô</th><th>Ngày SX</th><th>Đvt</th>
          <th>Số lượng<br />đề xuất</th><th>Tồn kho<br />xưởng</th><th>Thực xuất</th>
          <th>Kho hàng đi</th><th>Kho hàng đến</th><th>Diễn giải</th>
        </tr>
      </thead>
      ${groups}
    </table>
    <div class="total">
      <div class="box"><span>Tổng cộng:</span><span>${num(requested)}</span></div>
      <div class="box"><span>Tổng thực xuất:</span><span>${num(issued)}</span></div>
    </div>
    <div class="sign">
      <div>Trưởng ngành</div><div>Thủ kho ngành</div><div>Vật tư ngành</div><div>Thủ kho xưởng</div>
    </div>
  </section>`;
}

export function buildTransferRequestDocument(doc: TransferPrintDoc): string {
  return `<!doctype html><html lang="vi"><head><meta charset="utf-8" />
<title>Đề nghị lĩnh vật tư</title><style>${CSS}</style></head><body>${renderDoc(doc)}</body></html>`;
}

/** In phiếu trong iframe ẩn; iframe gỡ khi đóng hộp thoại in (hoặc sau 60s) */
export function printTransferRequests(doc: TransferPrintDoc): void {
  if (!doc.lines.length) {
    return;
  }
  const iframe = document.createElement("iframe");
  iframe.setAttribute("aria-hidden", "true");
  iframe.style.cssText =
    "position:fixed;right:0;bottom:0;width:0;height:0;border:0;visibility:hidden;";
  document.body.appendChild(iframe);
  const frameDoc = iframe.contentDocument;
  const win = iframe.contentWindow;
  if (!frameDoc || !win) {
    iframe.remove();
    return;
  }
  frameDoc.open();
  frameDoc.write(buildTransferRequestDocument(doc));
  frameDoc.close();

  let removed = false;
  const cleanup = (): void => {
    if (!removed) {
      removed = true;
      iframe.remove();
    }
  };
  win.addEventListener("afterprint", cleanup);
  // Đợi trình duyệt dựng xong tài liệu rồi mới in
  setTimeout(() => {
    win.focus();
    win.print();
  }, 150);
  setTimeout(cleanup, 60000);
}
