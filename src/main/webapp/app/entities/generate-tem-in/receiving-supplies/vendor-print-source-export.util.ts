import * as XLSX from "xlsx";

/** Header row khớp mẫu data_source.xlsx (Bartender). */
export const VENDOR_PRINT_SOURCE_HEADERS = [
  "SAPCode*\n(Định dang TEXT)",
  "Tên SP*\n(Định dang TEXT)",
  "PartNumber*\n(Định dang TEXT)",
  "Quantity*\n(Định dang SỐ NGUYÊN)",
  "Gross/Net Weigh\n(Định dang TEXT)",
  "Meas\n(Định dang TEXT)",
  "PO*\n(Định dang TEXT)",
  "Contract No\n(Định dang TEXT)",
  "Invoice no\n(Định dang TEXT)",
  "LOT/Batch no*\n(Định dang TEXT)",
  "MFG Date*\n(Định dang TEXT)",
  "Made in*\n(Định dang TEXT)",
  "Số TEM*\n(Định dang SỐ NGUYÊN)",
  "Vendor*\n(Định dang TEXT)",
  "Tên NCC\n(Định dang TEXT)",
  "UserData1\n(Định dang TEXT)",
  "UserData2\n(Định dang TEXT)",
  "UserData3\n(Định dang TEXT)",
  "UserData4\n(Định dang TEXT)",
  "StorageUnit\n(Định dang TEXT)",
  "ExpirationDate\n(Định dang TEXT)",
  "Ngày Về\n(Định dang TEXT)",
] as const;

export interface VendorPrintSourceExportInput {
  sapCode: string;
  productName: string;
  partNumber: string;
  quantity: number | null;
  grossNetWeight: string;
  meas: string;
  po: string;
  contractNo: string;
  invoiceNo: string;
  lotBatchNo: string;
  mfgDate: string;
  madeIn: string;
  temCount: number | null;
  vendor: string;
  vendorName: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  storageUnit: string;
  expirationDate: string;
  arrivalDate: string;
}

function cell(value: string | number | null | undefined): string | number {
  if (value == null) {
    return "";
  }
  return value;
}

export function buildVendorPrintSourceExportRows(
  rows: VendorPrintSourceExportInput[],
): (string | number)[][] {
  return rows.map((item) => [
    cell(item.sapCode),
    cell(item.productName),
    cell(item.partNumber),
    cell(item.quantity),
    cell(item.grossNetWeight),
    cell(item.meas),
    cell(item.po),
    cell(item.contractNo),
    cell(item.invoiceNo),
    cell(item.lotBatchNo),
    cell(item.mfgDate),
    cell(item.madeIn),
    cell(item.temCount),
    cell(item.vendor),
    cell(item.vendorName),
    cell(item.userData1),
    cell(item.userData2),
    cell(item.userData3),
    cell(item.userData4),
    cell(item.storageUnit),
    cell(item.expirationDate),
    cell(item.arrivalDate),
  ]);
}

export function generateVendorPrintSourceWorkbook(
  rows: VendorPrintSourceExportInput[],
): XLSX.WorkBook {
  const dataRows = buildVendorPrintSourceExportRows(rows);
  const sheet = XLSX.utils.aoa_to_sheet([
    [...VENDOR_PRINT_SOURCE_HEADERS],
    ...dataRows,
  ]);
  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, sheet, "Sheet1");
  return workbook;
}
