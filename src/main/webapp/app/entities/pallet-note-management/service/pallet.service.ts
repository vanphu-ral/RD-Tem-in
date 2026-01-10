import { Injectable } from "@angular/core";
import { HttpClient, HttpResponse } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "app/environments/environment";

export interface PrintPalletData {
  id?: number;
  khachHang: string;
  serialPallet: string;
  tenSanPham: string;
  poNumber: string;
  itemNoSku: string;
  nganh: string;
  led2: string;
  soQdsx: string;
  to: string;
  lpl2: string;
  ngaySanXuat: string;
  dateCode: string;
  soLuongCaiDatPallet: number;
  thuTuGiaPallet: number;
  soLuongBaoNgoaiThungGiaPallet: string;
  slThung: number;
  note: string;
  nguoiKiemTra: string;
  ketQuaKiemTra: string;
  productCode: string;
  serialBox: string;
  qty: number;
  lot: string;
  date: string;
  scannedBoxes?: string[];
  printStatus?: boolean;
  productType?: string;
  version?: string;
  maSAP?: string;
  woId?: string;
  erpWo?: string;
  maLenhSanXuat?: string;
  totalProductsOnPallet?: number;
}

/**
 * Request DTO for print PDF - matches backend PalletPrintRequest
 */
export interface PrintPalletRequest {
  pallets: PrintPalletData[];
  paperSize: string; // "A4" or "A5"
}

@Injectable({
  providedIn: "root",
})
export class PalletService {
  private baseUrl = environment.baseInTemApiUrl;
  private apiUrl = `${this.baseUrl}/pallet-infor-details`;

  constructor(private http: HttpClient) {}

  /**
   * Gọi API in pallet và nhận về file PDF binary (blob)
   * @param pallets Mảng dữ liệu pallet cần in
   * @param paperSize Kích thước giấy ("A4" hoặc "A5"), mặc định là "A4"
   * @returns Observable với HttpResponse chứa blob PDF
   */
  printPallets(
    pallets: PrintPalletData[],
    paperSize: string = "A4",
  ): Observable<HttpResponse<Blob>> {
    const url = `${this.apiUrl}/print-pdf`;
    const request: PrintPalletRequest = {
      pallets: pallets,
      paperSize: paperSize,
    };
    return this.http.post(url, request, {
      responseType: "blob",
      observe: "response",
    });
  }

  /**
   * Xuất PDF mà không in (tải về)
   * @param pallets Mảng dữ liệu pallet cần xuất PDF
   * @param paperSize Kích thước giấy ("A4" hoặc "A5"), mặc định là "A4"
   * @returns Observable với HttpResponse chứa blob PDF
   */
  exportPalletsToPdf(
    pallets: PrintPalletData[],
    paperSize: string = "A4",
  ): Observable<HttpResponse<Blob>> {
    const url = `${this.apiUrl}/export-pdf`;
    const request: PrintPalletRequest = {
      pallets: pallets,
      paperSize: paperSize,
    };
    return this.http.post(url, request, {
      responseType: "blob",
      observe: "response",
    });
  }

  /**
   * Tải PDF về máy thay vì chỉ mở blob
   * @param pallets Mảng dữ liệu pallet cần tải PDF
   * @param paperSize Kích thước giấy ("A4" hoặc "A5"), mặc định là "A4"
   * @returns Observable với HttpResponse chứa blob PDF
   */
  downloadPalletsPdf(
    pallets: PrintPalletData[],
    paperSize: string = "A4",
  ): Observable<HttpResponse<Blob>> {
    const url = `${this.apiUrl}/export-pdf`;
    const request: PrintPalletRequest = {
      pallets: pallets,
      paperSize: paperSize,
    };
    return this.http.post(url, request, {
      responseType: "blob",
      observe: "response",
      headers: {
        Accept: "application/pdf",
      },
    });
  }
}
