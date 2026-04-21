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
export interface PrintPalletDTO {
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
  printStatus?: boolean;
  productType?: string;
  version?: string;
  maSAP?: string;
  woId?: string;
  erpWo?: string;
  maLenhSanXuat?: string;
  totalProductsOnPallet?: number;
  // KHÔNG có scannedBoxes
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
  private baseUrl = environment.baseInTemApiUrl; //http://192.168.10.99:9040/api
  // private baseUrl = "http://192.168.10.99:8085/api";
  private apiUrl = `${this.baseUrl}/pallet-infor-details`;
  constructor(private http: HttpClient) {}
  private static toDTO(pallet: PrintPalletData): PrintPalletDTO {
    const { scannedBoxes, ...dto } = pallet;
    return dto;
  }

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
    const request = {
      pallets: pallets.map((p) => PalletService.toDTO(p)),
      paperSize,
    };
    return this.http.post(url, request, {
      responseType: "blob",
      observe: "response",
    });
  }

  exportPalletsToPdf(
    pallets: PrintPalletData[],
    paperSize: string = "A4",
  ): Observable<HttpResponse<Blob>> {
    const url = `${this.apiUrl}/export-pdf`;
    const request = {
      pallets: pallets.map((p) => PalletService.toDTO(p)),
      paperSize,
    };
    return this.http.post(url, request, {
      responseType: "blob",
      observe: "response",
    });
  }

  downloadPalletsPdf(
    pallets: PrintPalletData[],
    paperSize: string = "A4",
  ): Observable<HttpResponse<Blob>> {
    const url = `${this.apiUrl}/export-pdf`;
    const request = {
      pallets: pallets.map((p) => PalletService.toDTO(p)),
      paperSize,
    };
    return this.http.post(url, request, {
      responseType: "blob",
      observe: "response",
      headers: { Accept: "application/pdf" },
    });
  }

  downloadDomesticPalletsPdf(
    pallets: PrintPalletData[],
    paperSize: string = "A5",
  ): Observable<HttpResponse<Blob>> {
    const url = `${this.apiUrl}/export-pdf-nd`;
    const body = {
      pallets: pallets.map((p) => ({
        id: p.id,
        customerName: p.khachHang,
        serialPallet: p.serialPallet,
        sapName: p.tenSanPham,
        sapCode: p.maSAP ?? "",
        poNumber: p.poNumber,
        itemNoSku: p.itemNoSku,
        dateCode: p.dateCode,
        soQdsx: p.soQdsx,
        productionDate: p.ngaySanXuat,
        inspectorName: p.nguoiKiemTra,
        inspectionResult: p.ketQuaKiemTra,
        branch: p.nganh ?? p.led2 ?? "",
        groupName: p.to ?? p.lpl2 ?? "",
        quantityPerBox: p.slThung,
        numBoxActual: Number(p.soLuongBaoNgoaiThungGiaPallet),
        totalQuantity: p.totalProductsOnPallet ?? p.soLuongCaiDatPallet,
        thuTuGiaPallet: p.thuTuGiaPallet,
        note: p.note,
        serialBox: p.serialBox,
        qty: p.qty,
        date: p.date,
        printStatus: p.printStatus ?? false,
        productType: p.productType ?? "Thành phẩm",
        version: p.version ?? "",
        workOrderCode: p.erpWo ?? p.woId ?? "",
        lotNumber: p.lot ?? "",
      })),
      paperSize: paperSize,
    };

    return this.http.post(url, body, {
      responseType: "blob",
      observe: "response",
      headers: { Accept: "application/pdf" },
    });
  }
}
