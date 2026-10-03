import * as XLSX from "xlsx";

export const TEM_PANACIM_EXPORT_HEADERS = [
  "ReelID",
  "PartNumber",
  "Vendor",
  "Lot",
  "UserData1",
  "UserData2",
  "UserData3",
  "UserData4",
  "UserData5",
  "InitialQuantity",
  "MSDLevel",
  "MSDInitialFloorTime",
  "MSDBagSealDate",
  "MarketUsage",
  "QuantityOverride",
  "ShelfTime",
  "SPMaterialName",
  "WarningLimit",
  "MaximumLimit",
  "Comments",
  "WarmupTime",
  "StorageUnit",
  "SubStorageUnit",
  "LocationOverride",
  "ExpirationDate",
  "ManufacturingDate",
  "Partclass",
  "SAPCode",
] as const;

export interface TemPanacimExportInput {
  reelId: string;
  partNumber: string;
  vendor: string;
  lot: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  initialQuantity: number | null;
  storageUnit: string;
  expirationDate: Date | string | null;
  manufacturingDate: Date | string | null;
  sapCode: string;
}

type FormatExportDateFn = (value: Date | string | null) => string;

/**
 * PanaCIM không nhận ký tự tiếng Việt (vd "Đ") — bỏ dấu trước khi đưa vào CSV:
 * "272/KDVT-RĐ" → "272/KDVT-RD", "Vị trí" → "Vi tri". Ký tự không dấu giữ nguyên.
 */
export function toPanacimText(
  value: string | number | null | undefined,
): string {
  return String(value ?? "")
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/Đ/g, "D")
    .replace(/đ/g, "d");
}

function formatExportDate(
  value: Date | string | null,
  formatDateFn: FormatExportDateFn,
): string {
  if (!value) {
    return "";
  }
  if (value instanceof Date) {
    return formatDateFn(value).replace(/-/g, "");
  }
  const trimmed = value.trim();
  if (!trimmed) {
    return "";
  }
  return trimmed.replace(/-/g, "");
}

export function buildTemPanacimExportRows(
  rows: TemPanacimExportInput[],
  formatDateFn: FormatExportDateFn,
): (string | number)[][] {
  const t = toPanacimText;
  return rows.map((item) => [
    t(item.reelId),
    t(item.partNumber),
    t(item.vendor),
    t(item.lot),
    t(item.userData1),
    t(item.userData2),
    t(item.userData3),
    t(item.userData4),
    t(item.userData5),
    item.initialQuantity ?? "",
    "",
    "",
    "",
    "",
    "1",
    "",
    "",
    "",
    "",
    "",
    "",
    t(item.storageUnit),
    "",
    "",
    formatExportDate(item.expirationDate, formatDateFn),
    formatExportDate(item.manufacturingDate, formatDateFn),
    "",
    t(item.sapCode),
  ]);
}

export function generateTemPanacimCsvContent(
  rows: TemPanacimExportInput[],
  formatDateFn: FormatExportDateFn,
): string {
  const dataRows = buildTemPanacimExportRows(rows, formatDateFn);
  return (
    "\ufeff" +
    [TEM_PANACIM_EXPORT_HEADERS, ...dataRows]
      .map((row) => row.map((cell) => cell ?? "").join(","))
      .join("\n")
  );
}

export function generateTemPanacimCsvBlob(
  rows: TemPanacimExportInput[],
  formatDateFn: FormatExportDateFn,
): Blob {
  return new Blob([generateTemPanacimCsvContent(rows, formatDateFn)], {
    type: "text/csv;charset=utf-8;",
  });
}

/** Mẫu ReelData — xuất Excel sau khi đã tạo tem. */
export const TEM_REEL_DATA_EXPORT_HEADERS = [
  "NumberOfPlanning",
  "ItemCode",
  "ProductName",
  "SapWo",
  "Lot",
  "Version",
  "TimeRecieved",
  "ReelID",
  "PartNumber",
  "Vendor",
  "QuantityOfPackage",
  "MFGDate",
  "ProductionShilt",
  "OpName",
  "Comments",
  "Comments2",
  "StorageUnit",
  "TP/NK",
  "Rank",
  "entryDate",
  "ExpirationDate",
  "PO",
  "userData1",
  "userData2",
  "userData3",
  "QrCode",
] as const;

export interface TemReelDataExportInput {
  /** Số lượng của lô vật tư. */
  numberOfPlanning: number | null;
  itemCode: string;
  productName: string;
  sapWo: string;
  lot: string;
  version: string;
  timeReceived: Date | string | null;
  reelId: string;
  partNumber: string;
  vendor: string;
  quantityOfPackage: number | null;
  mfgDate: Date | string | null;
  productionShift: string;
  opName: string;
  comments: string;
  comments2: string;
  storageUnit: string;
  tpNk: string;
  rank: string;
  entryDate: Date | string | null;
  expirationDate: Date | string | null;
  po: string;
  userData1: string;
  userData2: string;
  userData3: string;
  qrCode: string;
}

export function buildTemReelDataExportRows(
  rows: TemReelDataExportInput[],
  formatDateFn: FormatExportDateFn,
): (string | number)[][] {
  return rows.map((item) => [
    item.numberOfPlanning ?? "",
    item.itemCode,
    item.productName,
    item.sapWo,
    item.lot,
    item.version,
    formatExportDate(item.timeReceived, formatDateFn),
    item.reelId,
    item.partNumber,
    item.vendor,
    item.quantityOfPackage ?? "",
    formatExportDate(item.mfgDate, formatDateFn),
    item.productionShift,
    item.opName,
    item.comments,
    item.comments2,
    item.storageUnit,
    item.tpNk,
    item.rank,
    formatExportDate(item.entryDate, formatDateFn),
    formatExportDate(item.expirationDate, formatDateFn),
    item.po,
    item.userData1,
    item.userData2,
    item.userData3,
    item.qrCode,
  ]);
}

export function generateTemReelDataWorkbook(
  rows: TemReelDataExportInput[],
  formatDateFn: FormatExportDateFn,
): XLSX.WorkBook {
  const dataRows = buildTemReelDataExportRows(rows, formatDateFn);
  const sheet = XLSX.utils.aoa_to_sheet([
    [...TEM_REEL_DATA_EXPORT_HEADERS],
    ...dataRows,
  ]);
  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, sheet, "ReelData");
  return workbook;
}
