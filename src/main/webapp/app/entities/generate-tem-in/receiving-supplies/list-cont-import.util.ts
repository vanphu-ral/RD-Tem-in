import * as XLSX from "xlsx";

export interface ListContImportLine {
  sapCode: string;
  itemName: string;
  /** Số pallet cần → SỐ TEM */
  palletCount: number;
  /** Số vật tư / pallet → SỐ LƯỢNG */
  qtyPerPallet: number;
  /** Mã kho SAP (cột Mã Kho) — optional. */
  warehouseCode: string;
  sheetName: string;
  lineNo: number;
}

export interface ListContImportGroup {
  sapCode: string;
  itemName: string;
  lines: ListContImportLine[];
}

export interface ListContImportParseResult {
  groups: ListContImportGroup[];
  errors: string[];
  sheetCount: number;
  lineCount: number;
}

function normalizeHeaderKey(value: unknown): string {
  return String(value ?? "")
    .replace(/\r\n/g, "\n")
    .replace(/\n/g, " ")
    .toLowerCase()
    .replace(/\s+/g, " ")
    .trim();
}

function compactHeader(value: string): string {
  return value.replace(/\s+/g, "");
}

function isRdCodeHeader(key: string): boolean {
  return compactHeader(key) === "rdcode";
}

function isRdNameHeader(key: string): boolean {
  return compactHeader(key) === "rdname";
}

function isPalletNeededHeader(key: string): boolean {
  const compact = compactHeader(key);
  return (
    compact.includes("sốpalletcần") ||
    compact.includes("sopalletcan") ||
    key.includes("số pallet cần") ||
    key.includes("so pallet can")
  );
}

function isQtyPerPalletHeader(key: string): boolean {
  const compact = compactHeader(key);
  return (
    compact.includes("sốvậttư/pallet") ||
    compact.includes("sovattu/pallet") ||
    key.includes("số vật tư / pallet") ||
    key.includes("so vat tu / pallet") ||
    key.includes("số vật tư/pallet")
  );
}

function isWarehouseHeader(key: string): boolean {
  const compact = compactHeader(key);
  return (
    compact === "mãkho" ||
    compact === "makho" ||
    compact === "mãkhosap" ||
    compact === "makhosap" ||
    compact === "whscode" ||
    compact === "whs" ||
    compact === "warehouse" ||
    key.includes("mã kho") ||
    key.includes("ma kho")
  );
}

function cellToText(value: unknown): string {
  if (value == null) {
    return "";
  }
  if (typeof value === "number" && Number.isFinite(value)) {
    if (Number.isInteger(value)) {
      return String(value);
    }
    return String(value);
  }
  return String(value).trim();
}

/** Giữ leading zero nếu Excel trả số nguyên ngắn (thường mã SAP 8 số). */
function normalizeRdCode(raw: string): string {
  const trimmed = raw.trim();
  if (!trimmed) {
    return "";
  }
  if (/^\d+$/.test(trimmed) && trimmed.length < 8) {
    return trimmed.padStart(8, "0");
  }
  return trimmed;
}

/** Chuẩn hóa mã kho từ Excel (vd. "01 - Kho A" → "01", số 1 → "01"). */
function normalizeWarehouseFromCell(raw: string): string {
  let trimmed = raw.trim();
  if (!trimmed) {
    return "";
  }
  const dashIdx = trimmed.indexOf(" - ");
  if (dashIdx > 0) {
    trimmed = trimmed.substring(0, dashIdx).trim();
  }
  if (/^\d+$/.test(trimmed) && trimmed.length < 2) {
    return trimmed.padStart(2, "0");
  }
  return trimmed;
}

function parsePositiveNumber(raw: string): number {
  const n = Number(String(raw).replace(/,/g, "").trim());
  if (!Number.isFinite(n) || n < 0) {
    return 0;
  }
  return Math.round(n);
}

function findHeaderRow(matrix: unknown[][]): {
  headerRowIndex: number;
  rdCodeIdx: number;
  rdNameIdx: number;
  palletIdx: number;
  qtyPerPalletIdx: number;
  warehouseIdx: number;
} | null {
  for (let r = 0; r < Math.min(matrix.length, 40); r++) {
    const row = matrix[r] ?? [];
    let rdCodeIdx = -1;
    let rdNameIdx = -1;
    let palletIdx = -1;
    let qtyPerPalletIdx = -1;
    let warehouseIdx = -1;
    row.forEach((cell, idx) => {
      const key = normalizeHeaderKey(cell);
      if (!key) {
        return;
      }
      if (rdCodeIdx < 0 && isRdCodeHeader(key)) {
        rdCodeIdx = idx;
      }
      if (rdNameIdx < 0 && isRdNameHeader(key)) {
        rdNameIdx = idx;
      }
      if (palletIdx < 0 && isPalletNeededHeader(key)) {
        palletIdx = idx;
      }
      if (qtyPerPalletIdx < 0 && isQtyPerPalletHeader(key)) {
        qtyPerPalletIdx = idx;
      }
      if (warehouseIdx < 0 && isWarehouseHeader(key)) {
        warehouseIdx = idx;
      }
    });
    if (rdCodeIdx >= 0 && palletIdx >= 0 && qtyPerPalletIdx >= 0) {
      return {
        headerRowIndex: r,
        rdCodeIdx,
        rdNameIdx,
        palletIdx,
        qtyPerPalletIdx,
        warehouseIdx,
      };
    }
  }
  return null;
}

function parseSheet(
  sheet: XLSX.WorkSheet,
  sheetName: string,
): { lines: ListContImportLine[]; error?: string } {
  const matrix = XLSX.utils.sheet_to_json<unknown[]>(sheet, {
    header: 1,
    defval: "",
    raw: false,
  });
  if (!matrix.length) {
    return { lines: [] };
  }
  const header = findHeaderRow(matrix);
  if (!header) {
    return {
      lines: [],
      error: `Sheet "${sheetName}": không tìm thấy cột RDCode / Số pallet cần / Số vật tư / pallet.`,
    };
  }

  const lines: ListContImportLine[] = [];
  for (let i = header.headerRowIndex + 1; i < matrix.length; i++) {
    const row = matrix[i] ?? [];
    const sapCode = normalizeRdCode(cellToText(row[header.rdCodeIdx]));
    if (!sapCode) {
      continue;
    }
    const itemName =
      header.rdNameIdx >= 0 ? cellToText(row[header.rdNameIdx]) : "";
    const palletCount = parsePositiveNumber(cellToText(row[header.palletIdx]));
    const qtyPerPallet = parsePositiveNumber(
      cellToText(row[header.qtyPerPalletIdx]),
    );
    const warehouseCode =
      header.warehouseIdx >= 0
        ? normalizeWarehouseFromCell(cellToText(row[header.warehouseIdx]))
        : "";
    lines.push({
      sapCode,
      itemName,
      palletCount,
      qtyPerPallet,
      warehouseCode,
      sheetName,
      lineNo: i + 1,
    });
  }
  return { lines };
}

function normalizeSapKey(code: string): string {
  const trimmed = code.trim();
  const stripped = trimmed.replace(/^0+/, "");
  return stripped || "0";
}

/**
 * Đọc file List Cont — chỉ sheet đầu.
 * Gom theo RDCode; mỗi dòng file là một line (có thể thành nhiều lô).
 * Cột Mã Kho (optional) → điền mã kho SAP theo từng dòng.
 */
export function parseListContImportFile(
  buffer: ArrayBuffer,
): ListContImportParseResult {
  const workbook = XLSX.read(buffer, {
    type: "array",
    cellDates: true,
    cellText: true,
  });
  const errors: string[] = [];
  const firstSheetName = workbook.SheetNames[0];
  if (!firstSheetName) {
    return {
      groups: [],
      errors: ["File Excel không có sheet dữ liệu."],
      sheetCount: 0,
      lineCount: 0,
    };
  }

  const sheet = workbook.Sheets[firstSheetName];
  if (!sheet) {
    return {
      groups: [],
      errors: [`Không đọc được sheet "${firstSheetName}".`],
      sheetCount: 0,
      lineCount: 0,
    };
  }

  const parsed = parseSheet(sheet, firstSheetName);
  if (parsed.error) {
    return {
      groups: [],
      errors: [parsed.error],
      sheetCount: 0,
      lineCount: 0,
    };
  }
  if (!parsed.lines.length) {
    return {
      groups: [],
      errors: [
        `Sheet "${firstSheetName}" không có dòng dữ liệu có RDCode, Số pallet cần và Số vật tư / pallet.`,
      ],
      sheetCount: 0,
      lineCount: 0,
    };
  }

  const groupMap = new Map<string, ListContImportGroup>();
  parsed.lines.forEach((line) => {
    const key = normalizeSapKey(line.sapCode);
    const existing = groupMap.get(key);
    if (!existing) {
      groupMap.set(key, {
        sapCode: line.sapCode,
        itemName: line.itemName,
        lines: [line],
      });
      return;
    }
    if (!existing.itemName && line.itemName) {
      existing.itemName = line.itemName;
    }
    existing.lines.push(line);
  });

  return {
    groups: Array.from(groupMap.values()),
    errors,
    sheetCount: 1,
    lineCount: parsed.lines.length,
  };
}
