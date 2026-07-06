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

function parseLogLine(
  line: string,
  lineNo: number,
  orderPo: string,
  poSapCodes: string[],
): { row?: VendorReelLogImportRow; error?: string } {
  const trimmed = line.trim();
  if (!trimmed) {
    return {};
  }

  const parts = trimmed.split("\t");
  if (parts.length < 12) {
    return {
      error: `Dòng ${lineNo}: không đủ cột (cần file log Bartender tab-separated).`,
    };
  }

  const datePrefix = parts[0]?.trim() ?? "";
  const boxStt = parts[1]?.trim() ?? "";
  const partNumber = parts[2]?.trim() ?? "";
  const quantity = parsePositiveInt(parts[parts.length - 8] ?? "0");
  const mfgDate = parseYyyyMmDd(parts[parts.length - 7] ?? "");
  const lotNumber = parseYyyyMmDd(parts[parts.length - 6] ?? "") || mfgDate;
  const sapCode = (parts[parts.length - 5] ?? "").trim();
  const temQuantity = parsePositiveInt(parts[parts.length - 4] ?? "0");
  const itemName = (parts[parts.length - 3] ?? "").trim();
  const poNumber = (parts[parts.length - 2] ?? "").trim();
  const madeIn = (parts[parts.length - 9] ?? "").trim();
  const contractNo = (parts[24] ?? "").trim();

  if (!datePrefix || !boxStt || !partNumber) {
    return {
      error: `Dòng ${lineNo}: thiếu thông tin ReelID (ngày/STT/PartNumber).`,
    };
  }

  if (!sapCode) {
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
): VendorReelLogParseResult {
  const rows: VendorReelLogImportRow[] = [];
  const errors: string[] = [];
  const lines = content.replace(/\r\n/g, "\n").split("\n");

  lines.forEach((line, index) => {
    const parsed = parseLogLine(line, index + 1, orderPo, poSapCodes);
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
