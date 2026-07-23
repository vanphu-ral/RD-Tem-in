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

  lines.forEach((line, index) => {
    const parsed = parseLogLine(line, index + 1, orderPo, poSapCodes, options);
    if (parsed.error) {
      errors.push(parsed.error);
      return;
    }
    if (parsed.row) {
      rows.push(parsed.row);
    }
  });

  if (!rows.length && !errors.length) {
    errors.push("File log trống hoặc không có dòng hợp lệ.");
  }

  return { rows, errors };
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

/**
 * Parse mẫu Excel NCC có cột FULL QRCODE
 * (ReelID | PartNumber | PO | MFGDate | QuantityOfPackage | Lot | FULL QRCODE).
 */
export function parseVendorFullQrExcelArrayBuffer(
  buffer: ArrayBuffer,
  orderPo = "",
  poSapCodes: string[] = [],
  options: VendorReelLogParseOptions = {},
): VendorReelLogParseResult {
  const rows: VendorReelLogImportRow[] = [];
  const errors: string[] = [];

  try {
    const workbook = XLSX.read(buffer, { type: "array", cellDates: true });
    const sheetName = workbook.SheetNames[0];
    if (!sheetName) {
      return { rows, errors: ["File Excel không có sheet dữ liệu."] };
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

    if (!matrix.length) {
      return { rows, errors: ["File Excel trống."] };
    }

    const headerRowIndex = matrix.findIndex((row) =>
      (row ?? []).some((cell) => {
        const h = normalizeExcelHeader(cell);
        return h.includes("full qrcode") || h === "fullqrcode";
      }),
    );

    if (headerRowIndex < 0) {
      return {
        rows,
        errors: [
          "Không tìm thấy cột FULL QRCODE trong file Excel. Kiểm tra lại mẫu import.",
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
    const colPo = findCol("po");
    const colMfg = findCol("mfgdate", "mfg date", "manufacturing");
    const colQty = findCol("quantityofpackage", "quantity");
    const colLot = findCol("lot");

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

      if (!fullQr) {
        if (!reelIdCol || !partCol) {
          errors.push(
            `Dòng ${lineNo}: thiếu FULL QRCODE và không đủ ReelID/PartNumber để dựng QR.`,
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
        errors.push(`Dòng ${lineNo}: FULL QRCODE thiếu ReelID/PartNumber.`);
        continue;
      }

      const sapCode = deriveSapCode("", partNumber);
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
        itemName: "",
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
      errors.push("File Excel không có dòng FULL QRCODE hợp lệ.");
    }
  } catch {
    errors.push("Không đọc được file Excel. Kiểm tra định dạng .xlsx/.xls.");
  }

  return { rows, errors };
}
