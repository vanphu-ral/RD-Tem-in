import * as XLSX from "xlsx";

export type VendorReelImportReconcileStatus =
  | "matched"
  | "po_mismatch"
  | "sap_not_in_po";

export interface VendorReelLogImportRow {
  lineNo: number;
  reelId: string;
  boxStt: string;
  partNumber: string;
  quantity: number;
  mfgDate: string;
  lotNumber: string;
  sapCode: string;
  temQuantity: number;
  itemName: string;
  poNumber: string;
  madeIn: string;
  contractNo: string;
  reconcileStatus: VendorReelImportReconcileStatus;
  reconcileMessage: string;
}

export interface VendorReelLogParseResult {
  rows: VendorReelLogImportRow[];
  errors: string[];
}

/** Một tem/ReelID đã import từ file log — dùng khi tạo tem, không sinh ReelID mới. */
export interface VendorImportedReelEntry {
  reelId: string;
  qrCode: string;
  partNumber: string;
  poNumber: string;
  mfgDate: string;
  quantity: number;
  lotNumber: string;
}

export function toVendorImportedReelEntry(
  row: VendorReelLogImportRow,
): VendorImportedReelEntry {
  return {
    reelId: row.reelId,
    partNumber: row.partNumber.trim(),
    poNumber: row.poNumber.trim(),
    mfgDate: row.mfgDate,
    quantity: row.quantity,
    lotNumber: row.lotNumber,
    qrCode: buildVendorQrCode({
      reelId: row.reelId,
      partNumber: row.partNumber,
      poOrContract: row.poNumber,
      mfgDate: row.mfgDate,
      quantity: row.quantity,
      lotNumber: row.lotNumber,
    }),
  };
}

function parsePositiveInt(value: string): number {
  const n = Number.parseInt(value.trim(), 10);
  return Number.isFinite(n) && n >= 0 ? n : 0;
}

function parseYyyyMmDd(value: string): string {
  const digits = value.replace(/\D/g, "");
  return digits.length === 8 ? digits : "";
}

function deriveSapCode(explicitSap: string, partNumber: string): string {
  const sap = explicitSap.trim();
  if (sap) {
    return sap;
  }
  const part = partNumber.trim();
  if (!part) {
    return "";
  }
  const fromPart = part.split("_")[0]?.trim() ?? "";
  return fromPart;
}

/** ReelID = ngày + STT thùng (4 số) + mã PartNumber. */
export function buildVendorReelId(
  datePrefix: string,
  boxStt: string,
  partNumber: string,
): string {
  const date = datePrefix.replace(/\D/g, "").slice(0, 8);
  const stt = boxStt.replace(/\D/g, "").padStart(4, "0").slice(-4);
  const part = partNumber.trim();
  return `${date}${stt}${part}`;
}

/** QR tem NCC: ReelID#Partnumber#PO#ngày sx#Số lượng#số lô. */
export function buildVendorQrCode(input: {
  reelId: string;
  partNumber: string;
  poOrContract: string;
  mfgDate: string;
  quantity: number;
  lotNumber: string;
}): string {
  return [
    input.reelId.trim(),
    input.partNumber.trim(),
    input.poOrContract.trim(),
    input.mfgDate.trim(),
    String(input.quantity),
    input.lotNumber.trim(),
  ].join("#");
}

function reconcileImportRow(
  row: Omit<VendorReelLogImportRow, "reconcileStatus" | "reconcileMessage">,
  orderPo: string,
  poSapCodes: string[],
): Pick<VendorReelLogImportRow, "reconcileStatus" | "reconcileMessage"> {
  const normalizedOrderPo = orderPo.trim();
  const logPo = row.poNumber.trim();

  if (normalizedOrderPo && logPo && normalizedOrderPo !== logPo) {
    return {
      reconcileStatus: "po_mismatch",
      reconcileMessage: `PO file (${logPo}) khác PO đơn (${normalizedOrderPo})`,
    };
  }

  if (poSapCodes.length) {
    const sap = row.sapCode.trim();
    const normalized = sap.replace(/^0+/, "") || "0";
    const inPo = poSapCodes.some((code) => {
      const c = code.replace(/^0+/, "") || "0";
      return c === normalized;
    });
    if (!inPo) {
      return {
        reconcileStatus: "sap_not_in_po",
        reconcileMessage: `Mã SAP ${sap} không có trong PO hiện tại`,
      };
    }
  }

  return {
    reconcileStatus: "matched",
    reconcileMessage: "Khớp PO / SAP",
  };
}

export interface VendorReelLogParseOptions {
  /** Mặc định true — bỏ qua dòng thiếu SAP. Set false khi import ở màn nhập TEM NCC. */
  requireSap?: boolean;
}

function parseLogLine(
  line: string,
  lineNo: number,
  orderPo: string,
  poSapCodes: string[],
  options: VendorReelLogParseOptions = {},
): { row?: VendorReelLogImportRow; error?: string } {
  const trimmed = line.trim();
  if (!trimmed) {
    return {};
  }

  const tabParts = trimmed.split("\t");
  const commaParts = trimmed.split(",");
  const useTab = tabParts.length >= commaParts.length;
  const parts = useTab ? tabParts : commaParts;
  const format: "legacy_tab" | "document_010_csv" = useTab
    ? "legacy_tab"
    : "document_010_csv";

  if (parts.length < 12) {
    return {
      error: `Dòng ${lineNo}: không đủ cột (cần file log Bartender).`,
    };
  }

  const datePrefix = parts[0]?.trim() ?? "";
  const boxStt = parts[1]?.trim() ?? "";
  const partNumber = parts[2]?.trim() ?? "";

  let quantity = 0;
  let mfgDate = "";
  let lotNumber = "";
  let sapCode = "";
  let temQuantity = 0;
  let itemName = "";
  let poNumber = "";
  let madeIn = "";
  let contractNo = "";

  if (format === "legacy_tab") {
    quantity = parsePositiveInt(parts[parts.length - 8] ?? "0");
    mfgDate = parseYyyyMmDd(parts[parts.length - 7] ?? "");
    lotNumber = parseYyyyMmDd(parts[parts.length - 6] ?? "") || mfgDate;
    sapCode = (parts[parts.length - 5] ?? "").trim();
    temQuantity = parsePositiveInt(parts[parts.length - 4] ?? "0");
    itemName = (parts[parts.length - 3] ?? "").trim();
    poNumber = (parts[parts.length - 2] ?? "").trim();
    madeIn = (parts[parts.length - 9] ?? "").trim();
    contractNo = (parts[24] ?? "").trim();
  } else {
    // Mẫu CSV mới:
    // A) Document1_010 (≤21 cột):
    // [0]=date,[1]=box,[2]=part,[9]=contract,[10]=stt,[11]=qtyVậtTư (không dùng),
    // [12]=mfg,[13]=lot,[14]=sapCode,[15]=qtyTem,[16]=item,[17]=po
    // B) label_template_01_011 (≥22 cột):
    // [0]=date,[1]=box,[2]=part,[8]=stt,[13]=mfg,[14]=lot,[16]=po,[17]=part,
    // [18]=qty,[19]=sap,[20]=temQty,[21]=item
    if (parts.length >= 22) {
      contractNo = (parts[9] ?? "").trim();
      mfgDate = parseYyyyMmDd(parts[13] ?? "");
      lotNumber = parseYyyyMmDd(parts[14] ?? "") || mfgDate;
      const partFromRow = (parts[17] ?? "").trim() || partNumber;
      quantity = parsePositiveInt(parts[18] ?? "0");
      sapCode = deriveSapCode((parts[19] ?? "").trim(), partFromRow);
      temQuantity = parsePositiveInt(parts[20] ?? "0");
      itemName = (parts[21] ?? "").trim();
      poNumber = (parts[16] ?? "").trim();
    } else {
      contractNo = (parts[9] ?? "").trim();
      mfgDate = parseYyyyMmDd(parts[12] ?? "");
      lotNumber = parseYyyyMmDd(parts[13] ?? "") || mfgDate;
      // Cột [14] là mã SAP thật trong file Document1_010 (vd: 00012836 / 00012837),
      // không dùng để cắt từ PartNumber (parts[2] = 00012836_V1).
      sapCode = deriveSapCode((parts[14] ?? "").trim(), partNumber);
      // [15]=số lượng tem (vd: 500); [11]=số lượng vật tư (vd: 600) — không dùng làm SL import.
      quantity = parsePositiveInt(parts[15] ?? "0");
      temQuantity = quantity;
      itemName = (parts[16] ?? "").trim();
      poNumber = (parts[17] ?? "").trim();
    }
    madeIn = "";
  }

  if (!datePrefix || !boxStt || !partNumber) {
    return {
      error: `Dòng ${lineNo}: thiếu thông tin ReelID (ngày/STT/PartNumber).`,
    };
  }

  if ((options.requireSap ?? true) && !sapCode) {
    return { error: `Dòng ${lineNo}: thiếu mã SAP.` };
  }

  const reelId = buildVendorReelId(datePrefix, boxStt, partNumber);
  const base = {
    lineNo,
    reelId,
    boxStt,
    partNumber,
    quantity,
    mfgDate,
    lotNumber,
    sapCode,
    temQuantity: temQuantity > 0 ? temQuantity : 1,
    itemName,
    poNumber,
    madeIn,
    contractNo,
  };
  const reconcile = reconcileImportRow(base, orderPo, poSapCodes);

  return {
    row: {
      ...base,
      ...reconcile,
    },
  };
}

export function parseVendorReelLogContent(
  content: string,
  orderPo = "",
  poSapCodes: string[] = [],
  options: VendorReelLogParseOptions = {},
): VendorReelLogParseResult {
  const rows: VendorReelLogImportRow[] = [];
  const errors: string[] = [];
  const lines = content.replace(/\r\n/g, "\n").split("\n");
  let startIndex = 0;
  if (lines.length && isDocument010HeaderLine(lines[0] ?? "")) {
    startIndex = 1;
  }

  for (let index = startIndex; index < lines.length; index++) {
    const parsed = parseLogLine(
      lines[index],
      index + 1,
      orderPo,
      poSapCodes,
      options,
    );
    if (parsed.error) {
      errors.push(parsed.error);
      continue;
    }
    if (parsed.row) {
      rows.push(parsed.row);
    }
  }

  if (!rows.length && !errors.length) {
    errors.push("File log trống hoặc không có dòng hợp lệ.");
  }

  return { rows, errors };
}

/** Bỏ phần ví dụ trong ngoặc: "Mã SAP (0001234)" → "mã sap". */
export function stripImportHeaderExample(value: unknown): string {
  return normalizeExcelHeader(value)
    .replace(/\s*\([^)]*\)\s*/g, " ")
    .replace(/\s+/g, " ")
    .trim();
}

function normalizeExcelHeader(value: unknown): string {
  return String(value ?? "")
    .replace(/\s+/g, " ")
    .trim()
    .toLowerCase();
}

function cellToText(value: unknown): string {
  if (value == null) {
    return "";
  }
  if (value instanceof Date) {
    const y = value.getFullYear();
    const m = String(value.getMonth() + 1).padStart(2, "0");
    const d = String(value.getDate()).padStart(2, "0");
    return `${y}${m}${d}`;
  }
  return String(value).trim();
}

/** Đọc ô Excel ưu tiên text hiển thị (giữ 0 đầu nếu format Text). */
function cellToImportText(cell: XLSX.CellObject | undefined): string {
  if (!cell) {
    return "";
  }
  if (cell.t === "s") {
    return String(cell.v ?? "").trim();
  }
  if (cell.w != null && String(cell.w).trim() !== "") {
    return String(cell.w).trim();
  }
  if (typeof cell.v === "number" && Number.isFinite(cell.v)) {
    if (Number.isInteger(cell.v)) {
      return String(cell.v);
    }
    return String(cell.v);
  }
  if (cell.v instanceof Date) {
    return cellToText(cell.v);
  }
  return String(cell.v ?? "").trim();
}

/** Header mẫu mau-import-reel (Ngày + Part + SAP/PO, không có cột #). */
export function isMauImportReelHeaderLine(line: string): boolean {
  const parts = (line ?? "").split(",");
  if (parts.length < 8) {
    return false;
  }
  if (/^\d{8}$/.test((parts[0] ?? "").trim())) {
    return false;
  }
  const headers = parts.map((p) => stripImportHeaderExample(p));
  const joined = headers.join(" ");
  const hasNgay = headers.some(
    (h) => h.includes("ngày") || h.includes("ngay") || h === "date",
  );
  const hasPart = headers.some(
    (h) => h.includes("part number") || h === "partnumber" || h === "part",
  );
  const hasSapOrPo =
    headers.some(
      (h) => h.includes("mã sap") || h.includes("ma sap") || h === "sap",
    ) ||
    headers.some((h) => h === "po") ||
    joined.includes("sl tem");
  const hasHashOnlyCols = parts.filter((p) => p.trim() === "#").length >= 3;
  return hasNgay && hasPart && hasSapOrPo && !hasHashOnlyCols;
}

export function isMauImportReelHeaderRow(
  row: (string | number | Date | null | undefined)[] | null | undefined,
): boolean {
  return isMauImportReelHeaderLine(
    (row ?? []).map((c) => cellToText(c)).join(","),
  );
}

/**
 * Parse CSV/Excel mau-import-reel (header có ví dụ trong ngoặc, không có cột #).
 */
export function parseMauImportReelFromMatrix(
  matrix: (string | number | Date | null)[][],
  orderPo = "",
  poSapCodes: string[] = [],
  options: VendorReelLogParseOptions = {},
  sheet?: XLSX.WorkSheet | null,
): VendorReelLogParseResult {
  const rows: VendorReelLogImportRow[] = [];
  const errors: string[] = [];

  if (!matrix.length) {
    return { rows, errors: ["File trống."] };
  }

  const headerRowIndex = matrix.findIndex((row) =>
    isMauImportReelHeaderRow(row),
  );
  if (headerRowIndex < 0) {
    return {
      rows,
      errors: [
        "Không tìm thấy header mau-import-reel (Ngày, Part Number, Mã SAP, PO, ...).",
      ],
    };
  }

  const headers = (matrix[headerRowIndex] ?? []).map((cell) =>
    stripImportHeaderExample(cell),
  );
  const findCol = (...aliases: string[]): number =>
    headers.findIndex((h) => aliases.some((a) => h === a || h.includes(a)));
  const findColExact = (...names: string[]): number =>
    headers.findIndex((h) => names.includes(h));

  const colDate = findCol("ngày", "ngay", "date");
  const colBox = findCol("stt thùng", "stt thung");
  const colPart = findCol("part number", "partnumber");
  const colContract = findCol("contract", "số hđ", "so hd");
  const colMfg = findCol("mfg", "mfgdate");
  const colLot = findCol("số lô", "so lo", "lot");
  // Ưu tiên khớp đúng tên cột SAP — tránh alias "sap" khớp nhầm.
  let colSap = findColExact("mã sap", "ma sap", "sapcode", "sap");
  if (colSap < 0) {
    colSap = findCol("mã sap", "ma sap", "sapcode");
  }
  const colQtyTem = findCol("sl tem", "số lượng tem", "quantity");
  const colItem = findCol("tên sp", "ten sp", "itemname", "item");
  const colPo = findColExact("po") >= 0 ? findColExact("po") : findCol("po");

  const readCell = (rowIndex: number, colIndex: number): string => {
    if (colIndex < 0) {
      return "";
    }
    if (sheet) {
      const addr = XLSX.utils.encode_cell({ r: rowIndex, c: colIndex });
      return cellToImportText(sheet[addr]);
    }
    return cellToText((matrix[rowIndex] ?? [])[colIndex]);
  };

  for (let i = headerRowIndex + 1; i < matrix.length; i++) {
    const lineNo = i + 1;
    const datePrefix = readCell(i, colDate);
    const boxSttRaw = readCell(i, colBox);
    const partNumber = readCell(i, colPart);
    if (!datePrefix && !boxSttRaw && !partNumber) {
      continue;
    }
    if (!datePrefix || !boxSttRaw || !partNumber) {
      errors.push(`Dòng ${lineNo}: thiếu Ngày / STT thùng / Part Number.`);
      continue;
    }

    const mfgDate = parseYyyyMmDd(readCell(i, colMfg));
    const lotNumber = parseYyyyMmDd(readCell(i, colLot)) || mfgDate;
    const sapRaw = readCell(i, colSap);
    const sapCode = deriveSapCode(sapRaw, partNumber);
    const quantity = parsePositiveInt(readCell(i, colQtyTem));
    const itemName = readCell(i, colItem);
    const poNumber = readCell(i, colPo);
    const contractNo = readCell(i, colContract);
    const boxStt =
      boxSttRaw.replace(/\D/g, "").padStart(4, "0").slice(-4) || boxSttRaw;

    if ((options.requireSap ?? true) && !sapCode) {
      errors.push(`Dòng ${lineNo}: thiếu mã SAP.`);
      continue;
    }

    const reelId = buildVendorReelId(datePrefix, boxStt, partNumber);
    const base = {
      lineNo,
      reelId,
      boxStt,
      partNumber,
      quantity,
      mfgDate,
      lotNumber,
      sapCode,
      temQuantity: quantity > 0 ? quantity : 1,
      itemName,
      poNumber,
      madeIn: "",
      contractNo,
    };
    const reconcile = reconcileImportRow(base, orderPo, poSapCodes);
    rows.push({ ...base, ...reconcile });
  }

  if (!rows.length && !errors.length) {
    errors.push("File không có dòng dữ liệu (chỉ có header).");
  }
  return { rows, errors };
}

export function parseMauImportReelExcelArrayBuffer(
  buffer: ArrayBuffer,
  orderPo = "",
  poSapCodes: string[] = [],
  options: VendorReelLogParseOptions = {},
): VendorReelLogParseResult {
  try {
    const workbook = XLSX.read(buffer, {
      type: "array",
      cellDates: true,
      cellText: true,
    });
    const sheetName = workbook.SheetNames[0];
    if (!sheetName) {
      return { rows: [], errors: ["File Excel không có sheet dữ liệu."] };
    }
    const sheet = workbook.Sheets[sheetName];
    const matrix = XLSX.utils.sheet_to_json<(string | number | Date | null)[]>(
      sheet,
      { header: 1, defval: "", raw: false },
    );
    return parseMauImportReelFromMatrix(
      matrix,
      orderPo,
      poSapCodes,
      options,
      sheet,
    );
  } catch {
    return { rows: [], errors: ["Không đọc được file Excel mau-import-reel."] };
  }
}

export function parseMauImportReelCsvContent(
  content: string,
  orderPo = "",
  poSapCodes: string[] = [],
  options: VendorReelLogParseOptions = {},
): VendorReelLogParseResult {
  try {
    const workbook = XLSX.read(content, {
      type: "string",
      raw: false,
      FS: ",",
    });
    const sheetName = workbook.SheetNames[0];
    if (!sheetName) {
      return { rows: [], errors: ["File CSV trống."] };
    }
    const sheet = workbook.Sheets[sheetName];
    const matrix = XLSX.utils.sheet_to_json<(string | number | Date | null)[]>(
      sheet,
      { header: 1, defval: "", raw: false },
    );
    return parseMauImportReelFromMatrix(
      matrix,
      orderPo,
      poSapCodes,
      options,
      sheet,
    );
  } catch {
    return { rows: [], errors: ["Không đọc được file CSV mau-import-reel."] };
  }
}

/** Dòng tiêu đề mẫu cũ Document1_010 (có header Ngày + Part Number). */
export function isDocument010HeaderLine(line: string): boolean {
  const trimmed = (line ?? "").trim();
  if (!trimmed) {
    return false;
  }
  if (isMauImportReelHeaderLine(trimmed)) {
    return false;
  }
  const parts = trimmed.split(",");
  if (parts.length < 12) {
    return false;
  }
  if (/^\d{8}$/.test((parts[0] ?? "").trim())) {
    return false;
  }
  const h0 = stripImportHeaderExample(parts[0]);
  const h2 = stripImportHeaderExample(parts[2]);
  const looksLikeDateHeader =
    h0.includes("ngày") || h0.includes("ngay") || h0 === "date";
  const looksLikePartHeader =
    h2.includes("part") || h2 === "partnumber" || h2.includes("part number");
  return looksLikeDateHeader && looksLikePartHeader;
}

export function isDocument010HeaderRow(
  row: (string | number | Date | null | undefined)[] | null | undefined,
): boolean {
  return isDocument010HeaderLine(
    (row ?? []).map((c) => cellToText(c)).join(","),
  );
}

function looksLikeDocument010DataRow(
  row: (string | number | Date | null | undefined)[] | null | undefined,
): boolean {
  const cells = row ?? [];
  if (cells.length < 12) {
    return false;
  }
  const date = cellToText(cells[0]);
  const box = cellToText(cells[1]);
  const part = cellToText(cells[2]);
  return /^\d{8}$/.test(date) && !!box && !!part;
}

/**
 * Parse Excel mẫu Document1_010 / mau-import-reel.
 */
export function parseVendorDocument010ExcelArrayBuffer(
  buffer: ArrayBuffer,
  orderPo = "",
  poSapCodes: string[] = [],
  options: VendorReelLogParseOptions = {},
): VendorReelLogParseResult {
  const rows: VendorReelLogImportRow[] = [];
  const errors: string[] = [];
  try {
    const workbook = XLSX.read(buffer, {
      type: "array",
      cellDates: true,
      cellText: true,
    });
    const sheetName = workbook.SheetNames[0];
    if (!sheetName) {
      return { rows, errors: ["File Excel không có sheet dữ liệu."] };
    }
    const sheet = workbook.Sheets[sheetName];
    const matrix = XLSX.utils.sheet_to_json<(string | number | Date | null)[]>(
      sheet,
      { header: 1, defval: "", raw: false },
    );
    if (!matrix.length) {
      return { rows, errors: ["File Excel trống."] };
    }

    if (isMauImportReelHeaderRow(matrix[0])) {
      return parseMauImportReelFromMatrix(
        matrix,
        orderPo,
        poSapCodes,
        options,
        sheet,
      );
    }

    let startIndex = 0;
    if (isDocument010HeaderRow(matrix[0])) {
      startIndex = 1;
    } else if (!looksLikeDocument010DataRow(matrix[0])) {
      return {
        rows,
        errors: [
          "Không nhận dạng mẫu import (cần cột Ngày, STT thùng, Part Number, ...).",
        ],
      };
    }

    for (let i = startIndex; i < matrix.length; i++) {
      const cols = (matrix[i] ?? []).length;
      let allEmpty = true;
      const parts: string[] = [];
      for (let c = 0; c < Math.max(cols, 18); c++) {
        const text = cellToImportText(
          sheet[XLSX.utils.encode_cell({ r: i, c })],
        );
        if (text) {
          allEmpty = false;
        }
        parts.push(text);
      }
      if (allEmpty) {
        continue;
      }
      const parsed = parseLogLine(
        parts.join(","),
        i + 1,
        orderPo,
        poSapCodes,
        options,
      );
      if (parsed.error) {
        errors.push(parsed.error);
        continue;
      }
      if (parsed.row) {
        rows.push(parsed.row);
      }
    }

    if (!rows.length && !errors.length) {
      errors.push("File không có dòng import hợp lệ.");
    }
  } catch {
    errors.push("Không đọc được file Excel import.");
  }
  return { rows, errors };
}

/** Dòng đầu có header bảng import (ReelID / Part / FULL QRCODE). */

export function isVendorImportTableHeaderLine(line: string): boolean {
  const h = normalizeExcelHeader(line.replace(/[;,|\t]/g, " "));
  const hasReel = h.includes("reelid") || h.includes("reel id");
  const hasPart = h.includes("partnumber") || h.includes("part number");
  const hasFullQr = h.includes("full qrcode") || h.includes("fullqrcode");
  return hasFullQr || (hasReel && hasPart);
}

function findImportHeaderRowIndex(
  matrix: (string | number | Date | null)[][],
): number {
  return matrix.findIndex((row) =>
    (row ?? []).some((cell) => {
      const h = normalizeExcelHeader(cell);
      return (
        h.includes("full qrcode") ||
        h === "fullqrcode" ||
        h === "reelid" ||
        h === "reel id" ||
        h === "part number" ||
        h === "partnumber"
      );
    }),
  );
}

function parseVendorImportMatrix(
  matrix: (string | number | Date | null)[][],
  orderPo = "",
  poSapCodes: string[] = [],
  options: VendorReelLogParseOptions = {},
): VendorReelLogParseResult {
  const rows: VendorReelLogImportRow[] = [];
  const errors: string[] = [];

  if (!matrix.length) {
    return { rows, errors: ["File trống."] };
  }

  const headerRowIndex = findImportHeaderRowIndex(matrix);
  if (headerRowIndex < 0) {
    return {
      rows,
      errors: [
        "Không tìm thấy dòng tiêu đề cột (ReelID / Part Number / FULL QRCODE). Tải file mẫu để xem đúng format.",
      ],
    };
  }

  const headers = (matrix[headerRowIndex] ?? []).map((cell) =>
    normalizeExcelHeader(cell),
  );
  const findCol = (...aliases: string[]): number =>
    headers.findIndex((h) => aliases.some((a) => h === a || h.includes(a)));

  const colFullQr = findCol("full qrcode", "fullqrcode");
  const colReelId = findCol("reelid", "reel id");
  const colPart = findCol("partnumber", "part number");
  const colPo = findCol("po", "userdata5");
  const colMfg = findCol("mfgdate", "mfg date", "manufacturing", "mfg");
  const colQty = findCol(
    "quantityofpackage",
    "initialquantity",
    "số lượng",
    "so luong",
    "quantity",
    "sl",
  );
  const colLot = findCol("lotnumber", "số lô", "so lo", "lot");
  const colItem = findCol("tên sp", "ten sp", "itemname", "item name");
  const colSap = findCol("mã sap", "ma sap", "sapcode", "sap");

  for (let i = headerRowIndex + 1; i < matrix.length; i++) {
    const lineNo = i + 1;
    const row = matrix[i] ?? [];
    const allEmpty = row.every((cell) => cellToText(cell) === "");
    if (allEmpty) {
      continue;
    }

    let fullQr =
      colFullQr >= 0 ? cellToText(row[colFullQr]).replace(/\s+/g, "") : "";
    const reelIdCol = colReelId >= 0 ? cellToText(row[colReelId]) : "";
    const partCol = colPart >= 0 ? cellToText(row[colPart]) : "";
    const poCol = colPo >= 0 ? cellToText(row[colPo]) : "";
    const mfgCol = parseYyyyMmDd(colMfg >= 0 ? cellToText(row[colMfg]) : "");
    const qtyCol = parsePositiveInt(
      colQty >= 0 ? cellToText(row[colQty]) : "0",
    );
    const lotCol = colLot >= 0 ? cellToText(row[colLot]) : "";
    const itemName = colItem >= 0 ? cellToText(row[colItem]) : "";
    const sapFromCol = colSap >= 0 ? cellToText(row[colSap]) : "";

    if (!fullQr) {
      if (!reelIdCol || !partCol) {
        errors.push(
          `Dòng ${lineNo}: thiếu FULL QRCODE và không đủ ReelID/Part Number để dựng QR.`,
        );
        continue;
      }
      fullQr = buildVendorQrCode({
        reelId: reelIdCol,
        partNumber: partCol,
        poOrContract: poCol,
        mfgDate: mfgCol,
        quantity: qtyCol,
        lotNumber: lotCol,
      });
    }

    const parts = fullQr.split("#").map((p) => p.trim());
    const reelId = (parts[0] || reelIdCol).trim();
    const partNumber = (parts[1] || partCol).trim();
    const poNumber = (parts[2] || poCol).trim();
    const mfgDate = parseYyyyMmDd(parts[3] || mfgCol);
    const quantity = parsePositiveInt(parts[4] || String(qtyCol));
    const lotNumber = (parts[5] || lotCol || mfgDate).trim();

    if (!reelId || !partNumber) {
      errors.push(`Dòng ${lineNo}: thiếu ReelID/Part Number.`);
      continue;
    }

    const sapCode = deriveSapCode(sapFromCol, partNumber);
    if ((options.requireSap ?? true) && !sapCode) {
      errors.push(`Dòng ${lineNo}: thiếu mã SAP.`);
      continue;
    }

    const base = {
      lineNo,
      reelId,
      boxStt: "",
      partNumber,
      quantity,
      mfgDate,
      lotNumber,
      sapCode,
      temQuantity: 1,
      itemName,
      poNumber,
      madeIn: "",
      contractNo: "",
    };
    const reconcile = reconcileImportRow(base, orderPo, poSapCodes);
    rows.push({
      ...base,
      ...reconcile,
    });
  }

  if (!rows.length && !errors.length) {
    errors.push("File không có dòng dữ liệu hợp lệ.");
  }

  return { rows, errors };
}

/**
 * Parse mẫu Excel NCC có cột FULL QRCODE
 * (ReelID | Part Number | ... | FULL QRCODE) — khớp file mẫu tải về.
 */
export function parseVendorFullQrExcelArrayBuffer(
  buffer: ArrayBuffer,
  orderPo = "",
  poSapCodes: string[] = [],
  options: VendorReelLogParseOptions = {},
): VendorReelLogParseResult {
  try {
    const workbook = XLSX.read(buffer, { type: "array", cellDates: true });
    const sheetName = workbook.SheetNames[0];
    if (!sheetName) {
      return { rows: [], errors: ["File Excel không có sheet dữ liệu."] };
    }

    const sheet = workbook.Sheets[sheetName];
    const matrix = XLSX.utils.sheet_to_json<(string | number | Date | null)[]>(
      sheet,
      {
        header: 1,
        defval: "",
        raw: true,
      },
    );

    return parseVendorImportMatrix(matrix, orderPo, poSapCodes, options);
  } catch {
    return {
      rows: [],
      errors: ["Không đọc được file Excel. Kiểm tra định dạng .xlsx/.xls."],
    };
  }
}

/** Parse CSV/TXT dạng bảng có dòng tiêu đề cột (file mẫu). */
export function parseVendorImportCsvTableContent(
  content: string,
  orderPo = "",
  poSapCodes: string[] = [],
  options: VendorReelLogParseOptions = {},
): VendorReelLogParseResult {
  try {
    const workbook = XLSX.read(content, {
      type: "string",
      raw: false,
      FS: ",",
    });
    const sheetName = workbook.SheetNames[0];
    if (!sheetName) {
      return { rows: [], errors: ["File CSV trống."] };
    }
    const matrix = XLSX.utils.sheet_to_json<(string | number | Date | null)[]>(
      workbook.Sheets[sheetName],
      {
        header: 1,
        defval: "",
        raw: false,
      },
    );
    return parseVendorImportMatrix(matrix, orderPo, poSapCodes, options);
  } catch {
    return {
      rows: [],
      errors: [
        "Không đọc được file CSV. Kiểm tra định dạng hoặc tải file mẫu.",
      ],
    };
  }
}
