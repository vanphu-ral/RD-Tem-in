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
  type?: boolean;
  entryDate?: string; // LocalDate from backend
  lastModifiedDate?: string; // LocalDate from backend
  /** Mã kho SAP (GraphQL: WhsCode). */
  whsCode?: string;
  WhsCode?: string;
}

export interface GetRequestsQueryParams {
  status?: string;
  vendor?: string;
  vendorName?: string;
  userData5?: string;
  createdBy?: string;
  createdDate?: string;
  page?: number;
  size?: number;
}

export interface ListRequestCreateTemPage {
  content: ListRequestCreateTem[];
  totalElements: number;
  page: number;
  size: number;
  totalPages: number;
}

export interface ListRequestCreateTemRequest {
  vendor?: string;
  vendorName?: string;
  userData5?: string;
  createdBy?: string;
  numberProduction?: number;
  totalQuantity?: number;
  status?: string;
  createdDate?: string;
  type?: boolean;
  entryDate?: string;
}
