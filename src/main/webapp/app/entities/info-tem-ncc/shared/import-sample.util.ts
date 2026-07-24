import * as XLSX from "xlsx";

/**
 * Header file mẫu mau-import-reel — cột hữu ích (bỏ cột #),
 * ví dụ giá trị nằm trong ngoặc ở tên cột, không điền dòng data.
 */
export const MAU_IMPORT_REEL_HEADERS: string[] = [
  "Ngày (20260702)",
  "STT thùng (0025)",
  "Part Number (00012836_V1)",
  "Contract (RD-JEI26-19092)",
  "STT (1)",
  "SL vật tư (600)",
  "MFG (20260625)",
  "Số lô (20260625)",
  "Mã SAP (0001234)",
  "SL tem (500)",
  "Tên SP (HB-58B)",
  "PO (42061)",
];

function triggerBlobDownload(blob: Blob, fileName: string): void {
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = fileName;
  a.click();
  URL.revokeObjectURL(url);
}

function escapeCsvCell(v: string): string {
  if (/[",\n\r]/.test(v)) {
    return `"${v.replace(/"/g, '""')}"`;
  }
  return v;
}

export function downloadVendorImportSampleCsv(): void {
  // Chỉ header — mọi giá trị coi như text (CSV).
  const line = MAU_IMPORT_REEL_HEADERS.map(escapeCsvCell).join(",");
  const bom = "\uFEFF";
  const blob = new Blob([bom + line + "\r\n"], {
    type: "text/csv;charset=utf-8;",
  });
  triggerBlobDownload(blob, "mau-import-reel.csv");
}

export function downloadVendorImportSampleExcel(): void {
  const headers = MAU_IMPORT_REEL_HEADERS;
  const sheet: XLSX.WorkSheet = {};
  const maxDataRows = 100;

  headers.forEach((h, C) => {
    const addr = XLSX.utils.encode_cell({ r: 0, c: C });
    sheet[addr] = { t: "s", v: h, z: "@" };
  });

  // Ô trống định dạng Text — tránh Excel đổi 0025 → 25 khi người dùng nhập.
  for (let R = 1; R <= maxDataRows; R++) {
    for (let C = 0; C < headers.length; C++) {
      const addr = XLSX.utils.encode_cell({ r: R, c: C });
      sheet[addr] = { t: "s", v: "", z: "@" };
    }
  }

  sheet["!ref"] = XLSX.utils.encode_range({
    s: { r: 0, c: 0 },
    e: { r: maxDataRows, c: headers.length - 1 },
  });
  sheet["!cols"] = headers.map(() => ({ wch: 24 }));

  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, sheet, "Import");
  XLSX.writeFile(workbook, "mau-import-reel.xlsx");
}
