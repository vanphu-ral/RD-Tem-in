export interface ListRequestCreateTem {
  id?: number;
  vendor?: string;
  vendorName?: string;
  userData5?: string;
  numberProduction?: number;
  totalQuantity?: number;
  status?: string;
  createdBy?: string;
  createdDate?: string; // LocalDate from backend
  lastModifiedDate?: string; // LocalDate from backend
}

export interface ListRequestCreateTemRequest {
  vendor?: string;
  vendorName?: string;
  userData5?: string;
  createdBy?: string;
  numberProduction?: number;
  totalQuantity?: number;
  status?: string;
}
