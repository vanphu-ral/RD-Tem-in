import { Injectable } from "@angular/core";
import * as XLSX from "xlsx";
import { ExcelImportData } from "../models/list-product-of-request.model";

export interface GroupedExcelData {
  groupKey: string; // Format: "PO_Code|Vendor"
  poCode: string;
  vendor: string;
  tenNCC: string;
  products: ExcelImportData[];
}

export interface ValidationResult {
  isValid: boolean;
  totalRows: number;
  validRows: number;
  invalidRows: number;
  errors: string[];
}

// Các cột bắt buộc theo đúng thứ tự
// const REQUIRED_COLUMNS = [
//   "SAPCode",
//   "Tên SP",
//   "PartNumber",
//   "LOT",
//   "Số TEM",
//   "InitialQuantity",
//   "Vendor",
//   "Tên NCC",
//   "UserData1",
//   "UserData2",
//   "UserData3",
//   "UserData4",
//   "UserData5",
//   "StorageUnit",
//   "ExpirationDate",
//   "ManufacturingDate",
//   "Ngày Về",
// ];

const REQUIRED_COLUMNS = [
  "SAPCode*\n(Định dang TEXT)\n0001201",
  "Tên SP\n(Định dang TEXT)\nNắp nhựa bảo vệ MPL1062 50W",
  "PartNumber\n(Định dang TEXT)\n10000003255",
  "LOT\n(Định dang TEXT)\n20251025",
  "Số TEM\n(Định dang SỐ NGUYÊN)\n20",
  "InitialQuantity\n(Định dang SỐ NGUYÊN)\n3000",
  "Vendor*\n(Định dang TEXT)\nV904000181",
  "Tên NCC\n(Định dang TEXT)\nZhejiang Yankon Mega Mega",
  "UserData1\n(Định dang TEXT)\nNO",
  "UserData2\n(Định dang TEXT)\nNO",
  "UserData3\n(Định dang TEXT)\nNO",
  "UserData4\n(Định dang TEXT)\n00045127-121025",
  "UserData5(PO)*\n(Định dang TEXT)\n235645",
  "StorageUnit\n(Định dang TEXT)\nRD",
  "ExpirationDate\n(Định dang TEXT)\nddmmyyyy",
  "ManufacturingDate\n(Định dang TEXT)\nddmmyyyy",
  "Ngày Về\n(Định dang TEXT)\nddmmyyyy",
];

@Injectable({
  providedIn: "root",
})
export class ExcelParserService {
  constructor() {}
  parseExcelFile(
    file: File,
  ): Promise<{ data: ExcelImportData[]; validationResult: ValidationResult }> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = (e: ProgressEvent<FileReader>) => {
        try {
          if (!e.target?.result) {
            reject(new Error("Không thể đọc dữ liệu từ file"));
            return;
          }

          const data = new Uint8Array(e.target.result as ArrayBuffer);
          const workbook = XLSX.read(data, { type: "array" });

          // Kiểm tra xem file có sheet nào không
          if (
            !workbook.SheetNames ||
            !Array.isArray(workbook.SheetNames) ||
            workbook.SheetNames.length === 0
          ) {
            reject(
              new Error(
                "File Excel không có sheet nào hoặc định dạng không đúng",
              ),
            );
            return;
          }

          const sheetName = workbook.SheetNames[0];
          if (!sheetName) {
            reject(new Error("Tên sheet không hợp lệ"));
            return;
          }

          const worksheet = workbook.Sheets[sheetName];

          // Kiểm tra sheet có dữ liệu không
          if (!worksheet) {
            reject(
              new Error("Sheet không có dữ liệu hoặc định dạng không đúng"),
            );
            return;
          }

          const jsonData: any[] = XLSX.utils.sheet_to_json(worksheet, {
            header: 1,
          });

          if (!jsonData || !Array.isArray(jsonData) || jsonData.length < 2) {
            reject(new Error("File Excel không có dữ liệu hoặc chỉ có header"));
            return;
          }

          // Kiểm tra header có đúng format không
          try {
            if (!this.validateHeader(jsonData[0])) {
              reject(
                new Error(
                  "Định dạng file không đúng. Vui lòng sử dụng template chuẩn.",
                ),
              );
              return;
            }
          } catch {
            reject(new Error("Lỗi kiểm tra định dạng file. Vui lòng thử lại."));
            return;
          }

          const parsedData: ExcelImportData[] = [];

          for (let i = 1; i < jsonData.length; i++) {
            const row = jsonData[i];
            const rowNumber = i + 1;

            if (!row || !Array.isArray(row) || row.length === 0 || !row[0]) {
              const errorMessage = `Dòng ${rowNumber} trống hoặc không có dữ liệu. Không thể import file với dữ liệu không hợp lệ.`;
              reject(new Error(errorMessage));
              return;
            }

            const excelRow: ExcelImportData = {
              sapCode: this.getCellValue(row[0]),
              tenSP: this.getCellValue(row[1]),
              partNumber: this.getCellValue(row[2]),
              lot: this.getCellValue(row[3]),
              temQuantity: this.parseNumber(row[4]),
              initialQuantity: this.parseNumber(row[5]),
              vendor: this.getCellValue(row[6]),
              tenNCC: this.getCellValue(row[7]),
              userData1: this.getCellValue(row[8]),
              userData2: this.getCellValue(row[9]),
              userData3: this.getCellValue(row[10]),
              userData4: this.getCellValue(row[11]),
              userData5: this.getCellValue(row[12]),
              storageUnit: this.getCellValue(row[13]),
              expirationDate: this.parseDateAsText(row[14]),
              manufacturingDate: this.parseDateAsText(row[15]),
              arrivalDate: this.parseDateAsText(row[16]),
            };

            try {
              // Thu thập lỗi validation cho dòng này
              const rowErrors = this.getRowValidationErrors(
                excelRow,
                rowNumber,
              );

              if (rowErrors.length === 0 && this.validateExcelRow(excelRow)) {
                parsedData.push(excelRow);
              } else {
                const errorDetails = rowErrors.join(", ");
                const errorMessage = `Dòng ${rowNumber}: ${errorDetails}. Không thể import file với dữ liệu không hợp lệ.`;
                reject(new Error(errorMessage));
                return;
              }
            } catch (error) {
              const errorMessage = `Lỗi xử lý dòng ${rowNumber}: ${error instanceof Error ? error.message : "Dữ liệu không hợp lệ"}`;
              reject(new Error(errorMessage));
              return;
            }
          }

          try {
            if (!parsedData || parsedData.length === 0) {
              const errorMessage =
                "File Excel không có dữ liệu hợp lệ để import.";
              reject(new Error(errorMessage));
              return;
            }
          } catch {
            reject(new Error("Lỗi kiểm tra dữ liệu hợp lệ"));
            return;
          }

          try {
            const validationResult: ValidationResult = {
              isValid: true,
              totalRows: parsedData.length,
              validRows: parsedData.length,
              invalidRows: 0,
              errors: [],
            };

            resolve({ data: parsedData, validationResult });
          } catch {
            reject(new Error("Lỗi tạo kết quả validation"));
          }
        } catch (error: unknown) {
          try {
            const message =
              error instanceof Error
                ? error.message
                : typeof error === "string"
                  ? error
                  : JSON.stringify(error);

            // Thông báo lỗi chi tiết hơn
            if (message.includes("Unsupported file")) {
              reject(
                new Error(
                  "Định dạng file không được hỗ trợ. Chỉ chấp nhận file .xlsx và .xls",
                ),
              );
            } else if (message.includes("ZIP")) {
              reject(new Error("File Excel bị hỏng hoặc không đúng định dạng"));
            } else {
              reject(new Error(`Lỗi xử lý file: ${message}`));
            }
          } catch {
            reject(new Error("Lỗi không xác định khi xử lý file Excel"));
          }
        }
      };

      reader.onerror = () => {
        try {
          reject(
            new Error("Không thể đọc file. Vui lòng kiểm tra lại file Excel"),
          );
        } catch {
          reject(new Error("Lỗi không xác định khi đọc file"));
        }
      };

      reader.readAsArrayBuffer(file);
    });
  }

  groupDataByPOAndVendor(data: ExcelImportData[]): GroupedExcelData[] {
    const groups = new Map<string, GroupedExcelData>();

    data.forEach((item) => {
      try {
        const poCode = item.userData5 || "UNKNOWN_PO";
        const vendor = item.vendor || "UNKNOWN_VENDOR";
        const groupKey = `${poCode}|${vendor}`;

        if (!groups.has(groupKey)) {
          groups.set(groupKey, {
            groupKey,
            poCode,
            vendor,
            tenNCC: "",
            products: [],
          });
        }

        groups.get(groupKey)!.products.push(item);
      } catch {
        // Bỏ qua item có lỗi
      }
    });

    try {
      const result = Array.from(groups.values()).map((group) => {
        try {
          const tenNCC =
            group.products.find((p) => p.tenNCC)?.tenNCC ?? "UNKNOWN_VENDOR";
          return {
            ...group,
            tenNCC,
          };
        } catch {
          return {
            ...group,
            tenNCC: "UNKNOWN_VENDOR",
          };
        }
      });

      return result;
    } catch {
      return [];
    }
  }

  private validateHeader(headerRow: any[]): boolean {
    if (!headerRow || !Array.isArray(headerRow)) {
      return false;
    }

    return (
      headerRow.length >= REQUIRED_COLUMNS.length &&
      Boolean(headerRow[0]) && // Có cột đầu tiên (SAPCode)
      Boolean(headerRow[6])
    ); // Có cột thứ 7 (Vendor)
  }

  private getCellValue(cell: unknown): string {
    if (cell === null || cell === undefined) {
      return "";
    }
    try {
      return String(cell).trim();
    } catch {
      return "";
    }
  }

  private parseNumber(value: unknown): number {
    if (value === null || value === undefined || value === "") {
      return 0;
    }
    try {
      const num = Number(value);
      return Number.isNaN(num) ? 0 : num;
    } catch {
      return 0;
    }
  }

  private parseDate(value: unknown): string {
    if (!value) {
      return new Date().toISOString().split("T")[0];
    }

    try {
      // Handle string dates
      if (typeof value === "string") {
        const parsed = new Date(value);
        if (!Number.isNaN(parsed.getTime())) {
          return parsed.toISOString().split("T")[0];
        }
      }

      // Handle number dates (Excel timestamp)
      if (typeof value === "number") {
        const parsed = new Date((value - 25569) * 86400 * 1000); // Excel date conversion
        if (!Number.isNaN(parsed.getTime())) {
          return parsed.toISOString().split("T")[0];
        }
      }

      return new Date().toISOString().split("T")[0];
    } catch {
      return new Date().toISOString().split("T")[0];
    }
  }

  private parseDateAsText(value: unknown): string {
    if (!value) {
      return "";
    }

    try {
      // Convert to string and remove any whitespace
      const dateStr = String(value).trim();

      // If it's already in ddmmyyyy format (8 digits), return as is
      if (/^\d{8}$/.test(dateStr)) {
        return dateStr;
      }

      // If it's a number (Excel serial date), convert to ddmmyyyy format
      if (typeof value === "number") {
        const parsed = new Date((value - 25569) * 86400 * 1000);
        if (!Number.isNaN(parsed.getTime())) {
          const day = String(parsed.getDate()).padStart(2, "0");
          const month = String(parsed.getMonth() + 1).padStart(2, "0");
          const year = parsed.getFullYear();
          return `${day}${month}${year}`;
        }
      }

      // Return the string value as is
      return dateStr;
    } catch {
      return "";
    }
  }

  private validateExcelRow(row: ExcelImportData): boolean {
    try {
      return !!(
        row.sapCode &&
        row.partNumber &&
        row.vendor &&
        row.userData5 &&
        row.initialQuantity > 0
      );
    } catch {
      return false;
    }
  }

  private getRowValidationErrors(
    row: ExcelImportData,
    rowNumber: number,
  ): string[] {
    const errors: string[] = [];

    try {
      if (!row.sapCode || row.sapCode.trim() === "") {
        errors.push(`SAP Code trống`);
      }
      if (!row.userData5 || row.userData5.trim() === "") {
        errors.push(`Mã PO trống`);
      }
      if (!row.partNumber || row.partNumber.trim() === "") {
        errors.push(`Part Number trống`);
      }

      if (!row.vendor || row.vendor.trim() === "") {
        errors.push(`Vendor trống`);
      }

      if (row.initialQuantity <= 0) {
        errors.push(`Số lượng ≤ 0 (Yêu cầu > 0)`);
      }
    } catch {
      errors.push(`Dữ liệu dòng không hợp lệ`);
    }

    return errors;
  }
}
