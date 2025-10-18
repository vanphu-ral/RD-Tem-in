import { Injectable } from "@angular/core";
import * as XLSX from "xlsx";
import { ExcelImportData } from "../models/list-product-of-request.model";

export interface GroupedExcelData {
  groupKey: string; // Format: "PO_Code|Vendor"
  poCode: string;
  vendor: string;
  products: ExcelImportData[];
}

@Injectable({
  providedIn: "root",
})
export class ExcelParserService {
  constructor() {}
  parseExcelFile(file: File): Promise<ExcelImportData[]> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = (e: ProgressEvent<FileReader>) => {
        try {
          const data = new Uint8Array(e.target?.result as ArrayBuffer);
          const workbook = XLSX.read(data, { type: "array" });

          const sheetName = workbook.SheetNames[0];
          const worksheet = workbook.Sheets[sheetName];

          const jsonData: any[] = XLSX.utils.sheet_to_json(worksheet, {
            header: 1,
          });

          const parsedData: ExcelImportData[] = [];

          for (let i = 1; i < jsonData.length; i++) {
            const row = jsonData[i];

            if (!row || row.length === 0 || !row[0]) {
              continue;
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
              expirationDate: this.parseDate(row[14]),
              manufacturingDate: this.parseDate(row[15]),
              arrivalDate: this.parseDate(row[16]),
            };

            if (this.validateExcelRow(excelRow)) {
              parsedData.push(excelRow);
            }
          }

          resolve(parsedData);
        } catch (error: unknown) {
          const message =
            error instanceof Error
              ? error.message
              : typeof error === "string"
                ? error
                : JSON.stringify(error);
          reject(new Error(`Lỗi: ${message}`));
        }
      };

      reader.onerror = () => {
        reject(new Error("Error reading file"));
      };

      reader.readAsArrayBuffer(file);
    });
  }

  /**
   * Group parsed Excel data by userData5 (PO Code) and Vendor
   */
  groupDataByPOAndVendor(data: ExcelImportData[]): GroupedExcelData[] {
    const groups = new Map<string, GroupedExcelData>();

    data.forEach((item) => {
      const poCode = item.userData5 || "UNKNOWN_PO";
      const vendor = item.vendor || "UNKNOWN_VENDOR";
      const groupKey = `${poCode}|${vendor}`;

      if (!groups.has(groupKey)) {
        groups.set(groupKey, {
          groupKey,
          poCode,
          vendor,
          products: [],
        });
      }

      groups.get(groupKey)!.products.push(item);
    });

    return Array.from(groups.values());
  }

  /**
   * Generate sample Excel data for testing
   * (Đặt PUBLIC METHOD này trước các PRIVATE METHOD để thỏa @typescript-eslint/member-ordering)
   */

  /**
   * Get cell value as string
   */
  private getCellValue(cell: unknown): string {
    if (cell === null || cell === undefined) {
      return "";
    }
    return String(cell).trim();
  }

  /**
   * Parse number value
   */
  private parseNumber(value: unknown): number {
    if (value === null || value === undefined || value === "") {
      return 0;
    }
    const num = Number(value);
    return Number.isNaN(num) ? 0 : num;
  }

  private parseDate(value: unknown): string {
    if (!value) {
      return new Date().toISOString().split("T")[0];
    }

    // Handle string dates
    if (typeof value === "string") {
      const parsed = new Date(value);
      if (!Number.isNaN(parsed.getTime())) {
        return parsed.toISOString().split("T")[0];
      }
    }
    return new Date().toISOString().split("T")[0];
  }

  private validateExcelRow(row: ExcelImportData): boolean {
    return !!(
      row.sapCode &&
      row.partNumber &&
      row.lot &&
      row.vendor &&
      row.userData1 &&
      row.temQuantity > 0 &&
      row.initialQuantity > 0
    );
  }
}
