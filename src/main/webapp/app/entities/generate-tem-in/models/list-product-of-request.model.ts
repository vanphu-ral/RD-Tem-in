export interface ListProductOfRequest {
  id?: number;
  requestCreateTemId?: number;
  sapCode?: string;
  productName?: string;
  temQuantity?: number;
  partNumber?: string;
  lot?: string;
  initialQuantity?: number;
  vendor?: string;
  userData1?: string;
  userData2?: string;
  userData3?: string;
  userData4?: string;
  userData5?: string;
  storageUnit?: string;
  expirationDate?: string; // LocalDate from backend
  manufacturingDate?: string; // LocalDate from backend
  arrivalDate?: string; // LocalDate from backend
  numberOfPrints?: number;
  createdDate?: string; // LocalDate from backend
  lastModifiedDate?: string; // LocalDate from backend
}

export interface ListProductOfRequestRequest {
  requestCreateTemId?: number;
  sapCode?: string;
  temQuantity?: number;
  partNumber?: string;
  lot?: string;
  initialQuantity?: number;
  vendor?: string;
  vendorName?: string;
  userData1?: string;
  userData2?: string;
  userData3?: string;
  userData4?: string;
  userData5?: string;
  storageUnit?: string;
  expirationDate?: Date;
  manufacturingDate?: Date;
  arrivalDate?: Date;
  numberOfPrints?: number;
}

export interface ExcelImportData {
  sapCode: string;
  tenSP: string;
  partNumber: string;
  lot: string;
  temQuantity: number;
  initialQuantity: number;
  vendor: string;
  tenNCC: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  storageUnit?: string;
  expirationDate: string;
  manufacturingDate: string;
  arrivalDate: string;
}
