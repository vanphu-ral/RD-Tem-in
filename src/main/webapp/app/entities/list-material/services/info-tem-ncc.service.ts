import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "app/environments/environment.development";
import { CachedWarehouse } from "./warehouse-db";
import { RawGraphQLLocation } from "./list-material.service";

export interface RdMaterialAttribute {
  id: string;
  attributes: string;
  [key: string]: any;
}

export interface TemIdentificationScenarioPayload {
  vendorCode: string;
  vendorName: string;
  mappingConfig: string;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
}

export interface PoImportTemPayload {
  poNumber: string;
  vendorCode: string;
  vendorName: string;
  entryDate: string;
  storageUnit: string;
  temIdentificationScenarioId: number;
  mappingConfig: string;
  status: string;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
}

export interface TemScenarioResponse {
  id: number;
  vendorCode: string;
  vendorName: string;
  mappingConfig: string;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
}
export interface SapOcrd {
  id: number;
  cardCode: string;
  cardName: string;
  cardType: string;
  cardFName: string | null;
  groupCode: string;
  currency: string;
  licTradNum: string | null;
  addId: string | null;
  eMail: string | null;
}

export interface VendorTemDetail {
  id: number;
  reelId: string;
  partNumber: string;
  vendor: string;
  lot: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  initialQuantity: number;
  msdLevel: string;
  storageUnit: string;
  expirationDate: string;
  manufacturingDate: string;
  sapCode: string;
  vendorQrCode: string;
  status: string;
  createdBy: string;
  createdAt: string;
  sapName?: string;
  poDetailId?: number;
  panaSendStatus?: boolean | null;
}

export interface PoDetail {
  id: number;
  sapCode: string;
  sapName: string;
  quantityContainer: number;
  totalQuantity: number;
  partNumber: string;
  vendorTemDetails: VendorTemDetail[];
}

export interface ImportVendorTemTransaction {
  id: number;
  poNumber: string;
  vendorCode: string;
  vendorName: string;
  entryDate: string;
  storageUnit: string;
  temIdentificationScenarioId: number;
  mappingConfig: string;
  status: string;
  createdBy: string;
  createdAt: string;
  totalScanQuantity?: number | null;
  totalQuantityCalculated?: number | null;
  poImportTemId: number;
  poDetails: PoDetail[];
}

export interface PoImportTem {
  id: number;
  poNumber: string;
  vendorCode: string;
  vendorName: string;
  entryDate: string;
  storageUnit: string;
  quantityContainer: number;
  totalQuantity: number;
  status: string;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
  approver?: string;
  importVendorTemTransactions: ImportVendorTemTransaction[];
}
export interface Pagination {
  totalItems: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

export interface PoImportTemResponse {
  datas: PoImportTem[];
  pagination: Pagination;
}

//gui phe duyet
export interface ApproveVendorTemPayload {
  id: number;
  poNumber: string;
  vendorCode: string;
  vendorName: string;
  entryDate: string;
  storageUnit: string;
  temIdentificationScenarioId?: number | null;
  mappingConfig: string;
  status: string;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
  deletedBy: string;
  deletedAt: string;
  approver?: string;
  poImportTemId: number;
}
export interface CreateVendorTemDetailPayload {
  id?: number;
  reelId: string;
  partNumber: string;
  vendor: string;
  lot: string;

  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;

  initialQuantity: number;
  msdLevel: string;
  msdInitialFloorTime: string;
  msdBagSealDate: string;
  marketUsage: string;
  quantityOverride: number;
  shelfTime: string;
  spMaterialName: string;
  warningLimit: string;
  maximumLimit: string;
  comments: string;
  warmupTime: string;

  storageUnit: string;
  subStorageUnit: string;
  locationOverride: string;

  expirationDate: string;
  manufacturingDate: string;

  partClass: string;
  sapCode: string;
  vendorQrCode: string;
  status: string;

  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
  panaSendStatus?: boolean | null;
  poDetailId: number;
  importVendorTemTransactionsId: number;
}
// export interface RawGraphQLLocation {
//   id: number;
//   locationName: string;
//   locationFullName: string;
// }
@Injectable({
  providedIn: "root",
})
export class ManagerTemNccService {
  private baseUrl = environment.testApiUrl;
  private nhapKhoUrl = "http://192.168.10.99:3000/api";

  private itemDataUrl = "http://192.168.10.99:8001/api/v1";

  constructor(private http: HttpClient) {}

  //laydanh sach kho
  getWarehouses(): Observable<RawGraphQLLocation[]> {
    return this.http.get<RawGraphQLLocation[]>(
      `http://192.168.68.77:8085/api/location`,
    );
  }

  //kich ban tem
  getMaterialAttributes(): Observable<RdMaterialAttribute[]> {
    return this.http.get<RdMaterialAttribute[]>(
      `${this.baseUrl}/rd-material-attributes`,
    );
  }

  createTemIdentificationScenario(
    payload: TemIdentificationScenarioPayload,
  ): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/tem-identification-scenarios`,
      payload,
    );
  }

  deleteTemIdentificationScenario(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}/tem-identification-scenarios/${id}`,
    );
  }

  getSapOcrds(): Observable<SapOcrd[]> {
    return this.http.get<SapOcrd[]>(`${this.baseUrl}/sap-ocrds`);
  }

  //lay danh sach kich ban
  getTemIdentificationScenarios(): Observable<TemScenarioResponse[]> {
    return this.http.get<TemScenarioResponse[]>(
      `${this.baseUrl}/tem-identification-scenarios`,
    );
  }
  updateTemIdentificationScenario(
    id: number,
    payload: Partial<TemIdentificationScenarioPayload>,
  ): Observable<any> {
    return this.http.patch(
      `${this.baseUrl}/tem-identification-scenarios/${id}`,
      payload,
    );
  }

  //lay danh sach don nhap
  getPoImportTems(page: number, size: number): Observable<PoImportTemResponse> {
    return this.http.get<PoImportTemResponse>(
      `${this.baseUrl}/po-import-tems?page=${page}&size=${size}`,
    );
  }

  getPoImportTemDetail(id: number): Observable<PoImportTem> {
    return this.http.get<PoImportTem>(
      `${this.baseUrl}/po-import-tems/${id}/detail`,
    );
  }

  //tao don nhap tem
  createPoImportTem(payload: PoImportTemPayload): Observable<any> {
    return this.http.post(`${this.baseUrl}/po-import-tems/process`, payload);
  }
  getItemDataByItemCode(itemCode: string): Observable<string> {
    return this.http.get(`${this.itemDataUrl}/item-data/by-itemcode`, {
      params: { item_code: itemCode },
      responseType: "text",
    });
  }
  //xoa don nhap tem
  deleteTemPoImport(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/po-import-tems/${id}`);
  }

  //luu don scan
  createVendorTemDetails(
    payload: CreateVendorTemDetailPayload,
  ): Observable<any> {
    return this.http.post(`${this.baseUrl}/vendor-tem-details`, payload);
  }

  //cap nhat thong tin vat tu
  batchUpdateVendorTemDetails(
    payload: CreateVendorTemDetailPayload[],
  ): Observable<any> {
    return this.http.put(`${this.baseUrl}/vendor-tem-details/batch`, payload);
  }

  //gui phe duyet
  approveImportVendorTemTransaction(
    transactionId: number,
    payload: ApproveVendorTemPayload,
  ): Observable<any> {
    return this.http.put(
      `${this.baseUrl}/import-vendor-tem-transactions/${transactionId}`,
      payload,
    );
  }

  getPoImportTemById(id: number): Observable<PoImportTem> {
    return this.http.get<PoImportTem>(
      `${this.nhapKhoUrl}/po-import-tems/${id}`,
    );
  }

  getPoImportTemsAll(): Observable<PoImportTem[]> {
    return this.http.get<PoImportTem[]>(`${this.nhapKhoUrl}/po-import-tems`);
  }
}
