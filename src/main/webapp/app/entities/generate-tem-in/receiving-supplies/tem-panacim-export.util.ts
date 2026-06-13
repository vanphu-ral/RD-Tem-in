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
  return rows.map((item) => [
    item.reelId,
    item.partNumber,
    item.vendor,
    item.lot,
    item.userData1,
    item.userData2,
    item.userData3,
    item.userData4,
    item.userData5,
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
    item.storageUnit,
    "",
    "",
    formatExportDate(item.expirationDate, formatDateFn),
    formatExportDate(item.manufacturingDate, formatDateFn),
    "",
    item.sapCode,
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

export function generateTemPanacimExcelBlob(
  rows: TemPanacimExportInput[],
  formatDateFn: FormatExportDateFn,
): Blob {
  const dataRows = buildTemPanacimExportRows(rows, formatDateFn);
  const sheet = XLSX.utils.aoa_to_sheet([
    [...TEM_PANACIM_EXPORT_HEADERS],
    ...dataRows,
  ]);
  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, sheet, "TemExport");
  const buffer = XLSX.write(workbook, { bookType: "xlsx", type: "array" });
  return new Blob([buffer], {
    type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
  });
}
