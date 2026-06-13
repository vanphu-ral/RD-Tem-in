import * as XLSX from "xlsx";

export interface PartNumberPlImportRow {
  sapCode: string;
  dInvCode: string;
}

export type PartNumberReconcileStatus =
  | "matched"
  | "mismatch"
  | "missing_in_import";

export interface PartNumberReconcileRowResult {
  sapCode: string;
  itemName: string;
  tablePartNumber: string;
  importPartNumber: string;
  status: PartNumberReconcileStatus;
  message: string;
}

export interface PartNumberReconcileResult {
  matched: boolean;
  partialMatched: boolean;
  message: string;
  rows: PartNumberReconcileRowResult[];
  /** SAP → part number từ file import (để áp dụng). */
  importPartBySap: Record<string, string>;
}

export interface TablePartRow {
  sapCode: string;
  itemName: string;
  partNumber: string;
}

export interface PartNumberImportParseMeta {
  sheetName: string;
  headerRowIndex: number;
  partNumberHeader: string;
  sapCodeHeader: string;
}

function normalizeSapCode(code: string): string {
  const trimmed = code.trim();
  const stripped = trimmed.replace(/^0+/, "");
  return stripped || "0";
}

function normalizePartNumber(value: string): string {
  return value.trim();
}

/** Chuẩn hóa tên cột: lowercase, gộp khoảng trắng. */
function headerKey(value: unknown): string {
  return String(value ?? "")
    .toLowerCase()
    .replace(/\s+/g, " ")
    .trim();
}

/** Cột part number: DInvCode hoặc từ khóa part number. */
function isPartNumberHeader(key: string): boolean {
  const compact = key.replace(/\s+/g, "");
  if (compact.includes("dinvcode")) {
    return true;
  }
  return (
    key === "part number" ||
    compact === "partnumber" ||
    key === "part no" ||
    compact === "partno" ||
    key === "part no." ||
    key === "part #" ||
    key === "part" ||
    key === "mã part" ||
    key === "ma part"
  );
}

/** Cột mã SAP: RDCode / RD code / RD CODE... (mọi kiểu viết hoa thường). */
function isSapCodeHeader(key: string): boolean {
  const compact = key.replace(/\s+/g, "");
  return compact === "rdcode";
}

function partNumberColumnScore(key: string): number {
  const compact = key.replace(/\s+/g, "");
  if (compact === "dinvcode" || compact === "newdinvcode") {
    return 100;
  }
  if (compact.includes("dinvcode")) {
    return 90;
  }
  if (key === "part number" || compact === "partnumber") {
    return 80;
  }
  if (isPartNumberHeader(key)) {
    return 50;
  }
  return -1;
}

function sapCodeColumnScore(key: string): number {
  const compact = key.replace(/\s+/g, "");
  if (compact === "rdcode") {
    return 100;
  }
  return -1;
}

function findHeaderRowIndex(rows: unknown[][]): number {
  for (let i = 0; i < rows.length; i++) {
    const keys = rows[i].map((cell) => headerKey(cell));
    const hasPart = keys.some((k) => isPartNumberHeader(k));
    const hasSap = keys.some((k) => isSapCodeHeader(k));
    if (hasPart && hasSap) {
      return i;
    }
  }
  return -1;
}

function resolveColumnIndexes(headerRow: unknown[]): {
  partNumberIdx: number;
  sapCodeIdx: number;
  partNumberHeader: string;
  sapCodeHeader: string;
} {
  let partNumberIdx = -1;
  let sapCodeIdx = -1;
  let bestPartScore = -1;
  let bestSapScore = -1;
  let partNumberHeader = "";
  let sapCodeHeader = "";

  headerRow.forEach((cell, idx) => {
    const key = headerKey(cell);
    const partScore = partNumberColumnScore(key);
    if (partScore > bestPartScore) {
      bestPartScore = partScore;
      partNumberIdx = idx;
      partNumberHeader = String(cell ?? "").trim();
    }
    const sapScore = sapCodeColumnScore(key);
    if (sapScore > bestSapScore) {
      bestSapScore = sapScore;
      sapCodeIdx = idx;
      sapCodeHeader = String(cell ?? "").trim();
    }
  });

  return { partNumberIdx, sapCodeIdx, partNumberHeader, sapCodeHeader };
}

function findImportSheet(workbook: XLSX.WorkBook): {
  sheet: XLSX.WorkSheet;
  sheetName: string;
  headerRowIndex: number;
  partNumberIdx: number;
  sapCodeIdx: number;
  partNumberHeader: string;
  sapCodeHeader: string;
} | null {
  const orderedNames = [
    ...workbook.SheetNames.filter((n) => headerKey(n).includes("pl")),
    ...workbook.SheetNames.filter((n) => !headerKey(n).includes("pl")),
  ];

  for (const sheetName of orderedNames) {
    const sheet = workbook.Sheets[sheetName];
    if (!sheet) {
      continue;
    }
    const rows = XLSX.utils.sheet_to_json<unknown[]>(sheet, {
      header: 1,
      defval: "",
      raw: false,
    });
    const headerIdx = findHeaderRowIndex(rows);
    if (headerIdx < 0) {
      continue;
    }
    const resolved = resolveColumnIndexes(rows[headerIdx]);
    if (resolved.partNumberIdx < 0 || resolved.sapCodeIdx < 0) {
      continue;
    }
    return {
      sheet,
      sheetName,
      headerRowIndex: headerIdx,
      partNumberIdx: resolved.partNumberIdx,
      sapCodeIdx: resolved.sapCodeIdx,
      partNumberHeader: resolved.partNumberHeader,
      sapCodeHeader: resolved.sapCodeHeader,
    };
  }
  return null;
}

function parseRowsFromSheet(
  rows: unknown[][],
  headerIdx: number,
  partNumberIdx: number,
  sapCodeIdx: number,
): PartNumberPlImportRow[] {
  const bySap = new Map<string, PartNumberPlImportRow>();
  const conflicts: string[] = [];

  for (let i = headerIdx + 1; i < rows.length; i++) {
    const row = rows[i];
    const sapRaw = String(row[sapCodeIdx] ?? "").trim();
    const partRaw = String(row[partNumberIdx] ?? "").trim();
    if (!sapRaw || !partRaw) {
      continue;
    }
    const sapKey = normalizeSapCode(sapRaw);
    const existing = bySap.get(sapKey);
    if (existing) {
      if (
        normalizePartNumber(existing.dInvCode) !== normalizePartNumber(partRaw)
      ) {
        conflicts.push(`${sapRaw}: "${existing.dInvCode}" và "${partRaw}"`);
      }
      continue;
    }
    bySap.set(sapKey, {
      sapCode: sapRaw,
      dInvCode: partRaw,
    });
  }

  if (conflicts.length) {
    throw new Error(
      `File import có mã SAP trùng nhưng part number khác nhau: ${conflicts.slice(0, 3).join("; ")}${conflicts.length > 3 ? "..." : ""}`,
    );
  }

  return [...bySap.values()];
}

/**
 * Đọc file Excel — tự tìm sheet có cột part (DInvCode / Part number)
 * và cột mã SAP (RDCode / RD code / RD CODE...).
 */
export function parsePartNumberPlFile(
  file: File,
): Promise<PartNumberPlImportRow[]> {
  return parsePartNumberImportFile(file).then((r) => r.rows);
}

export function parsePartNumberImportFile(
  file: File,
): Promise<{ rows: PartNumberPlImportRow[]; meta: PartNumberImportParseMeta }> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = (event) => {
      try {
        const buffer = event.target?.result;
        if (!buffer) {
          reject(new Error("Không đọc được file."));
          return;
        }
        const workbook = XLSX.read(buffer, { type: "array" });
        const found = findImportSheet(workbook);
        if (!found) {
          reject(
            new Error(
              "Không tìm thấy sheet có cột part number (DInvCode / Part number) và cột mã SAP (RDCode / RD code).",
            ),
          );
          return;
        }

        const rows = XLSX.utils.sheet_to_json<unknown[]>(found.sheet, {
          header: 1,
          defval: "",
          raw: false,
        });

        const importRows = parseRowsFromSheet(
          rows,
          found.headerRowIndex,
          found.partNumberIdx,
          found.sapCodeIdx,
        );

        if (!importRows.length) {
          reject(
            new Error(
              `Sheet "${found.sheetName}" không có dòng dữ liệu hợp lệ.`,
            ),
          );
          return;
        }

        resolve({
          rows: importRows,
          meta: {
            sheetName: found.sheetName,
            headerRowIndex: found.headerRowIndex,
            partNumberHeader: found.partNumberHeader,
            sapCodeHeader: found.sapCodeHeader,
          },
        });
      } catch (err) {
        reject(
          err instanceof Error ? err : new Error("Không thể đọc file Excel."),
        );
      }
    };
    reader.onerror = () => reject(new Error("Không đọc được file."));
    reader.readAsArrayBuffer(file);
  });
}

export function reconcilePartNumbersFromPl(
  tableRows: TablePartRow[],
  importRows: PartNumberPlImportRow[],
): PartNumberReconcileResult {
  const importBySap = new Map<string, PartNumberPlImportRow>();
  importRows.forEach((row) => {
    importBySap.set(normalizeSapCode(row.sapCode), row);
  });

  const results: PartNumberReconcileRowResult[] = [];
  const importPartBySap: Record<string, string> = {};
  let matchedCount = 0;
  let mismatchCount = 0;
  let missingCount = 0;

  const seenSap = new Set<string>();
  tableRows.forEach((tableRow) => {
    const sapKey = normalizeSapCode(tableRow.sapCode);
    if (seenSap.has(sapKey)) {
      return;
    }
    seenSap.add(sapKey);

    const importRow = importBySap.get(sapKey);
    const tablePart = normalizePartNumber(tableRow.partNumber);

    if (!importRow) {
      missingCount += 1;
      results.push({
        sapCode: tableRow.sapCode,
        itemName: tableRow.itemName,
        tablePartNumber: tablePart,
        importPartNumber: "",
        status: "missing_in_import",
        message: "Không có mã SAP này trong file import.",
      });
      return;
    }

    const importPart = normalizePartNumber(importRow.dInvCode);
    importPartBySap[tableRow.sapCode.trim()] = importRow.dInvCode;

    if (!tablePart || tablePart === importPart) {
      matchedCount += 1;
      results.push({
        sapCode: tableRow.sapCode,
        itemName: tableRow.itemName,
        tablePartNumber: tablePart || "—",
        importPartNumber: importPart,
        status: "matched",
        message: "Khớp mã SAP và mã part.",
      });
      return;
    }

    mismatchCount += 1;
    results.push({
      sapCode: tableRow.sapCode,
      itemName: tableRow.itemName,
      tablePartNumber: tablePart,
      importPartNumber: importPart,
      status: "mismatch",
      message: `Khác mã part — bảng: "${tablePart}", file: "${importPart}".`,
    });
  });

  const allMatched = mismatchCount === 0 && missingCount === 0;
  const partialMatched = matchedCount > 0 && !allMatched;

  let message = "";
  if (allMatched) {
    message = `Đối chiếu OK — ${matchedCount} mã SAP khớp partnumber.`;
  } else if (partialMatched) {
    message = `Đối chiếu một phần — khớp: ${matchedCount}, khác part: ${mismatchCount}, thiếu trong file: ${missingCount}.`;
  } else {
    message = `Đối chiếu thất bại — khác part: ${mismatchCount}, thiếu trong file: ${missingCount}.`;
  }

  return {
    matched: allMatched,
    partialMatched,
    message,
    rows: results,
    importPartBySap,
  };
}

export function applyImportedPartNumbers(
  tableRows: TablePartRow[],
  importPartBySap: Record<string, string>,
  onlyMismatch = true,
): number {
  let applied = 0;
  tableRows.forEach((row) => {
    const sap = row.sapCode.trim();
    const importPart = importPartBySap[sap];
    if (!importPart) {
      const normalized = normalizeSapCode(sap);
      const fallback = Object.entries(importPartBySap).find(
        ([key]) => normalizeSapCode(key) === normalized,
      );
      if (!fallback) {
        return;
      }
      const [, part] = fallback;
      const current = normalizePartNumber(row.partNumber);
      if (onlyMismatch && current === normalizePartNumber(part)) {
        return;
      }
      row.partNumber = part;
      applied += 1;
      return;
    }
    const current = normalizePartNumber(row.partNumber);
    if (onlyMismatch && current === normalizePartNumber(importPart)) {
      return;
    }
    row.partNumber = importPart;
    applied += 1;
  });
  return applied;
}
