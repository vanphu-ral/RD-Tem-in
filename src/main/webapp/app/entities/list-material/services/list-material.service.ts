import { Injectable } from "@angular/core";
import {
  BehaviorSubject,
  Observable,
  Subject,
  catchError,
  map,
  of,
  takeUntil,
  tap,
} from "rxjs";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { environment } from "app/environments/environment";
import * as XLSX from "xlsx";
import * as FileSaver from "file-saver";
import { MaterialItem } from "../dialog/list-material-update-dialog";
import { AccountService } from "app/core/auth/account.service";
import { ApplicationConfigService } from "app/core/config/application-config.service";

// #region Interfaces
export interface RawGraphQLLocation {
  locationId: string;
  locationName: string;
}
export interface UserSummary {
  id: string;
  username: string;
  email: string;
}

export interface RawGraphQLMaterial {
  select_update: boolean;
  checked: boolean;

  materialIdentifier: string;
  inventoryId: string;
  partNumber: string;

  lotNumber: string;
  userData4: string;
  quantity: number;
  availableQuantity: number;
  calculatedStatus: string;
  locationId: string;
  locationName: string;
  parentLocationId: string;
  lastLocationId: string;
  expirationDate: string;
  status: string;
  receivedDate: string;
  updatedDate: string;
  updatedBy: string;
  materialType: string;
  checkinDate: string;
}

export interface DataSumary {
  availableQuantity: string;
  locationName: string;
  partNumber: string;
  quantity: string;
  recordCount: string;
  lotNumber: string;
  userData4: string;
}

export interface ApiMaterialResponse {
  totalItems: number;
  inventories: any[];
}

export interface updateHistoryData {
  requestID: number;
  Status: string;
  RequestDate: string;
  User: string;
  approver: string;
  requestType: string;
  note?: string;
  timeAction: string;
}

export interface approvalHistoryDetailData {
  requestID: number;
  Status: string;
  RequestDate: string;
  approver: string;
  requestType: string;
  timeAction: string;
  "Material ID": number;
  "Part Number": string;
  "User data 4": string;
  State: string;
  Location: string;
}

export interface inventory_update_requests {
  id: number | null;
  requestCode: string;
  createdTime: string;
  updatedTime: string;
  requestedBy: string;
  approvedBy: string;
  status: string;
}

export interface inventory_update_requests_detail {
  id: number | null;
  materialId: string;
  updatedBy: string;
  createdTime: string;
  updatedTime: string;
  productCode: string;
  productName: string;
  quantity: string;
  quantityChange: string;
  type: string;
  locationId: string;
  locationName: string;
  expiredTime: string;
  status: string;
  requestId: string | null;
}
export interface inventory_update_requests_history_detail {
  id: number | null;
  materialId: string;
  requestedTime: string;
  approvedTime: string;
  requestedBy: string;
  approvedBy: string;
  oldLocation: string;
  newLocation: string;
  expiredTime: string;
  requestType: string;
  quantity: string;
  quantityChange: string;
  status: string;
  requestCode: string | null;
}

export interface UpdateRequestInfo {
  request: inventory_update_requests;
  detail: inventory_update_requests_detail[];
}

export interface UpdateInfo {
  request: inventory_update_requests;
  detail: inventory_update_requests_detail[];
}

interface MaterialsQueryResponse {
  getAllInventory: RawGraphQLMaterial[];
}

interface AllUpdateRequestQueryResponse {
  getAllInventoryUpdateRequests: inventory_update_requests[];
}

interface UpdateRequestDetailQueryResponse {
  getInventoryUpdateRequestsDetailByRequestId: inventory_update_requests_detail[];
}

interface LocationQueryResponse {
  allLocations: RawGraphQLLocation[];
}

// interface SumaryByPart {
//   "partNumber ": string;
//   "totalQuantity ": string;
//   totalAvailableQuantity: number;
//   recordCount: number;
//   details: Detail;
// }
// interface SumaryByUserData4 {
//   "partNumber ": string;
//   "userData4 ": string;
//   totalQuantity: number;
//   totalAvailableQuantity: number;
//   recordCount: 3;
//   details: Detail;
// }
// interface SumaryByLocation {
//   "locationName ": string;
//   "partNumber ": string;
//   totalQuantity: number;
//   totalAvailableQuantity: number;
//   recordCount: 3;
//   details: Detail;
// }
// interface SumaryByLot {
//   "partNumber ": string;
//   "lotNumber ": string;
//   totalQuantity: number;
//   totalAvailableQuantity: number;
//   recordCount: 3;
//   details: Detail;
// }

// interface Detail {
//   partnumber: string;
//   lotNumber: string;
//   userData4: string;
//   locationName: string;
//   availableQuantity: number;
//   receivedDate: string;
//   expirationDate: string;
//   status: string;
// }

export interface APISumaryResponse {
  totalItems: number;
  inventories: any[];
}
export interface APIDetailResponse {
  inventories: RawGraphQLMaterial[];
  totalItems: number;
}

@Injectable({
  providedIn: "root",
})
export class ListMaterialService {
  fileType =
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
  fileExtension = ".xlsx";

  public approvalHistoryDetailData$!: Observable<approvalHistoryDetailData[]>;
  public updateRequestData$!: Observable<inventory_update_requests[]>;
  public updateRequestDetail$!: Observable<inventory_update_requests_detail[]>;
  public updateHistoryData$!: Observable<updateHistoryData[]>;
  public totalCount$!: Observable<number>;
  public materialsData$!: Observable<RawGraphQLMaterial[]>;
  public selectedIds$!: Observable<string[]>;
  public locationsData$!: Observable<RawGraphQLLocation[]>;
  public sumaryData$: Observable<DataSumary[]>;
  //  lấy dữ liệu tổng hợp
  //  url api tong hop
  public apiSumaryByPart = this.applicationConfigService.getEndpointFor(
    "/api/inventory/group-by-part-number",
  );
  public apiSumaryByLot = this.applicationConfigService.getEndpointFor(
    "/api/inventory/group-by-lot-number",
  );
  public apiSumaryByUserData4 = this.applicationConfigService.getEndpointFor(
    "/api/inventory/group-by-user-data-4",
  );
  public apiSumaryByLocation = this.applicationConfigService.getEndpointFor(
    "/api/inventory/group-by-location-name",
  );
  public selectedItems$!: Observable<RawGraphQLMaterial[]>;

  private _summaryDataSource = new BehaviorSubject<DataSumary[]>([]);
  private _SumaryData = new BehaviorSubject<DataSumary[]>([]);
  // public get summaryData$(): Observable<DataSumary[]> {
  //   return this.summaryDataSource.asObservable();
  // }

  private _approvalHistoryDetailData = new BehaviorSubject<
    approvalHistoryDetailData[]
  >([]);
  private _allInventoryUpdateRequests = new BehaviorSubject<
    inventory_update_requests[]
  >([]);
  private _requestDetail = new BehaviorSubject<
    inventory_update_requests_detail[]
  >([]);
  private _updateHistoryData = new BehaviorSubject<updateHistoryData[]>([]);
  private _totalCount = new BehaviorSubject<number>(0);
  private _materialsData = new BehaviorSubject<RawGraphQLMaterial[]>([]);
  private _selectedIds = new BehaviorSubject<string[]>([]);
  private _materialsCache = new Map<string, RawGraphQLMaterial>();
  private _locationsData = new BehaviorSubject<RawGraphQLLocation[]>([]);
  private _selectedItems = new BehaviorSubject<RawGraphQLMaterial[]>([]);
  private destroy$ = new Subject<void>();

  // private restBaseUrl = environment.restApiBaseUrl;

  private apiMaterialUrl =
    this.applicationConfigService.getEndpointFor("/api/inventory");

  private apiRequest =
    this.applicationConfigService.getEndpointFor("/api/request");

  // private apiRequest = "http://localhost:8085" + "/api/request";
  //  private apiUrl_post_request_update = this.restBaseUrl + "/api/request";

  private apiUrl_post_request_update =
    this.applicationConfigService.getEndpointFor("api/request");

  private apiUrl_post_update =
    this.applicationConfigService.getEndpointFor("api/request");

  private apiSumaryDetail = this.applicationConfigService.getEndpointFor(
    "/api/inventory/detail",
    // );
    // private apiSumaryDetail = this.applicationConfigService.getEndpointFor(
    //   "/api/inventory",
  );
  private apiRequestDetail = this.applicationConfigService.getEndpointFor(
    "/api/request/detail",
  ); // + /requestCode : lấy chi tiết

  private apiRequestHistory = this.applicationConfigService.getEndpointFor(
    "/api/request/history",
  ); // post lấy theo tháng, +/requestCode : lấy chi tiết

  private apiRequestHistoryDetail =
    this.applicationConfigService.getEndpointFor("/api/request/history/detail");
  private apiLocations =
    this.applicationConfigService.getEndpointFor("/api/location");

  private _materialsDataFetchedOnce = false;
  private readonly defaultPageSize = 20;

  private _updatedInventoryIds = new Set<string>();

  private _updateManageData = new BehaviorSubject<inventory_update_requests[]>(
    [],
  );

  constructor(
    private http: HttpClient,
    private accountService: AccountService,
    private applicationConfigService: ApplicationConfigService,
  ) {
    this.selectedItems$ = this._selectedItems.asObservable();
    const saved = sessionStorage.getItem("selectedMaterialIds");
    if (saved) {
      try {
        this._selectedIds.next(JSON.parse(saved));
      } catch {
        /* empty */
      }
    }
    this.refreshSelectedItems();
    this.fetchLocations();
    this.loadSelectedIds();
    this.fetchMaterialsData(1, this.defaultPageSize)
      .pipe(takeUntil(this.destroy$))
      .subscribe();

    this.fetchAllInventoryUpdateRequests();
    this.approvalHistoryDetailData$ =
      this._approvalHistoryDetailData.asObservable();
    this.updateRequestData$ = this._allInventoryUpdateRequests.asObservable();
    this.updateRequestDetail$ = this._requestDetail.asObservable();
    this.updateHistoryData$ = this._updateHistoryData.asObservable();
    this.totalCount$ = this._totalCount.asObservable();
    this.materialsData$ = this._materialsData.asObservable();
    this.selectedIds$ = this._selectedIds.asObservable();
    this.locationsData$ = this._locationsData.asObservable();
    this.sumaryData$ = this._SumaryData.asObservable();
  }

  public exportExcel(jsonData: any[], fileName: string): void {
    const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(jsonData);
    const wb: XLSX.WorkBook = { Sheets: { data: ws }, SheetNames: ["data"] };
    const excelBuffer: any = XLSX.write(wb, {
      bookType: "xlsx",
      type: "array",
    });
    this.saveExcelFile(excelBuffer, fileName);
  }

  // lấy dữ liệu trang tổng hợp
  // fetchDataSumary(apiUrl: string, body: any): Observable<APISumaryResponse> {
  //   console.log(body);
  //   console.log(apiUrl);
  //   return this.http.post<APISumaryResponse>(apiUrl, body).pipe(
  //     tap((response) => {
  //       console.log(response);
  //       if (response?.inventories) {
  //         this._totalCount.next(response.totalItems);
  //         this._SumaryData.next(response.inventories);
  //       } else {
  //         this._SumaryData.next([]);
  //       }
  //     }),
  //     catchError((err) => {
  //       console.error("Lỗi khi lấy dữ liệu cho chế độ ", err);
  //       this._SumaryData.next([]);
  //       return of({ inventories: [], totalItems: 0 } as APISumaryResponse);
  //     }),
  //   );
  // }
  fetchDataSumary(apiUrl: string, body: any): Observable<APISumaryResponse> {
    console.log(body, apiUrl);
    return this.http.post<APISumaryResponse>(apiUrl, body).pipe(
      catchError((err) => {
        console.error("Lỗi khi lấy dữ liệu summary: ", err);

        return of({ inventories: [], totalItems: 0 } as APISumaryResponse);
      }),
    );
  }
  //  lấy dư liệu cho trang danh sach

  fetchMaterialsData(
    pageIndex: number,
    limit: number, // Số lượng item/trang
    filters?: {
      materialIdentifier?: string;
      status?: string;
      partNumber?: string;
      quantity?: number | null;
      availableQuantity?: number | null;
      lotNumber?: string;
      userData4?: string;
      locationName?: string;
      expirationDate?: string;
    },
  ): Observable<ApiMaterialResponse> {
    const body = {
      materialIdentifier: filters?.materialIdentifier ?? "",
      status: filters?.status ?? "",
      partNumber: filters?.partNumber ?? "",
      quantity: filters?.quantity ?? null,
      availableQuantity: filters?.availableQuantity ?? null,
      lotNumber: filters?.lotNumber ?? "",
      userData4: filters?.userData4 ?? "",
      locationName: filters?.locationName ?? "",
      expirationDate: filters?.expirationDate ?? "",
      pageNumber: pageIndex, //  pageIndex + 1
      itemPerPage: limit,
    };

    console.log("Request Body:", JSON.stringify(body, null, 2));

    return this.http.post<ApiMaterialResponse>(this.apiMaterialUrl, body).pipe(
      tap((response) => {
        this.cachePage(response.inventories);
        console.log("API Response invent:", response);
        if (response?.inventories) {
          this._totalCount.next(response.totalItems);
          this.refreshSelectedItems();
          const mappedData = response.inventories
            .filter((item) => !this._updatedInventoryIds.has(item.inventoryId))
            .map((rawItem) =>
              this.mapRawToMaterial(rawItem, this._selectedIds.value),
            );
          this._materialsData.next(mappedData);
        }
      }),
      catchError((err) => {
        console.error("API Error:", err);
        this._materialsData.next([]);
        this._totalCount.next(0);
        return of({ totalItems: 0, inventories: [] });
      }),
    );
  }

  public fetchAllInventoryUpdateRequests(): void {
    console.log(`Goi API  ${this.apiRequest}`);

    this.http.get<inventory_update_requests[]>(this.apiRequest).subscribe({
      next: (data) => {
        if (Array.isArray(data)) {
          this._allInventoryUpdateRequests.next(data);
          console.log(
            "MaterialService (HTTP): All inventory update requests data successfully fetched:",
            data,
          );
        } else {
          this._allInventoryUpdateRequests.next([]);
        }
      },
      error: (err) => {
        console.error(
          "MaterialService (HTTP): Error fetching all inventory update requests:",
          err,
        );
        this._allInventoryUpdateRequests.next([]);
      },
    });
  }

  public clearSelection(): void {
    this._selectedIds.next([]);
    sessionStorage.removeItem("selectedMaterialIds");
    const cleared = this._materialsData.value.map((item) => ({
      ...item,
      checked: false,
      select_update: false,
    }));
    this._materialsData.next(cleared);
    console.log("[Service] clearSelection → newIds:", this._selectedIds.value);
    this.refreshSelectedItems();
  }

  public toggleItemSelection(materialId: string): void {
    const ids = this._selectedIds.value;
    const newIds = ids.includes(materialId)
      ? ids.filter((x) => x !== materialId)
      : [...ids, materialId];
    this._selectedIds.next(newIds);
    sessionStorage.setItem("selectedMaterialIds", JSON.stringify(newIds));
    console.log(
      "[Service] toggleItemSelection → newIds:",
      this._selectedIds.value,
    );
    this.refreshSelectedItems();
  }
  selectItems(ids: string[]): void {
    const set = new Set([...this._selectedIds.value, ...ids]);
    const newIds = Array.from(set);
    this._selectedIds.next(newIds);
    sessionStorage.setItem("selectedMaterialIds", JSON.stringify(newIds));
    console.log("[Service] selectItems → newIds:", this._selectedIds.value);
    this.refreshSelectedItems();
  }
  deselectItems(ids: string[]): void {
    const newIds = this._selectedIds.value.filter((x) => !ids.includes(x));
    this._selectedIds.next(newIds);
    sessionStorage.setItem("selectedMaterialIds", JSON.stringify(newIds));
    console.log("[Service] deselectItems → newIds:", this._selectedIds.value);
    this.refreshSelectedItems();
  }
  mergeChecked(page: RawGraphQLMaterial[]): RawGraphQLMaterial[] {
    const ids = this._selectedIds.value;
    return page.map((item) => ({
      ...item,
      checked: ids.includes(item.inventoryId),
    }));
  }
  updatePageData(pageItems: RawGraphQLMaterial[]): void {
    console.log("[Service] caching page items:", pageItems.length);
    pageItems.forEach((item) =>
      this._materialsCache.set(item.inventoryId, item),
    );
  }
  getPageWithSelection(pageItems: RawGraphQLMaterial[]): RawGraphQLMaterial[] {
    const ids = this._selectedIds.value;
    return pageItems.map((item) => ({
      ...item,
      checked: ids.includes(item.inventoryId),
    }));
  }

  public getDetailSumary(
    pageIndex: number,
    limit: number,
    filters?: {
      partNumber?: string;
      locationName?: string;
      userData4?: string;
      lotNumber?: string;
    },
  ): Observable<APIDetailResponse> {
    console.log("Đang gọi API lấy chi tiết summary...");
    const url = this.apiSumaryDetail;
    const safeFilters = filters ?? {};
    const body = {
      partNumber: filters?.partNumber ?? "",
      locationName: filters?.locationName ?? "",
      userData4: filters?.userData4 ?? "",
      lotNumber: filters?.lotNumber ?? "",
      pageNumber: pageIndex + 1,
      itemPerPage: limit,
    };
    console.log("body api sumary chi tiet: ", body);
    return this.http.post<APIDetailResponse>(url, body).pipe(
      tap((response) => console.log("API chi tiết trả về:", response)),
      catchError((err): Observable<APIDetailResponse> => {
        console.error("Lỗi khi lấy chi tiết summary:", err);
        return of({ inventories: [], totalItems: 0 });
      }),
    );
  }

  public removeItemFromUpdate(materialId: string): void {
    const newIds = this._selectedIds.value.filter((id) => id !== materialId);
    this._selectedIds.next(newIds);
    sessionStorage.setItem("selectedMaterialIds", JSON.stringify(newIds));
    this.refreshSelectedItems();
  }

  getRequestHistoryDetailsById(
    id: number | null,
  ): Observable<inventory_update_requests_history_detail[]> {
    console.log("dang goi api history detail");
    if (id === null) {
      console.error(
        "MaterialService (HTTP): Cannot fetch history details with a null ID.",
      );
      return of<inventory_update_requests_history_detail[]>([]);
    }

    const url = `${this.apiRequestHistoryDetail}/${id}`;
    console.log(
      `MaterialService (HTTP): Fetching request history details from ${url}`,
    );

    return this.http.get<inventory_update_requests_history_detail[]>(url).pipe(
      tap((data) =>
        console.log(
          `MaterialService (HTTP): History details for request ${id} successfully fetched:`,
          data,
        ),
      ),
      catchError(
        (err): Observable<inventory_update_requests_history_detail[]> => {
          console.error(
            `MaterialService (HTTP): Error fetching request history details for ID ${id}:`,
            err,
          );

          return of<inventory_update_requests_history_detail[]>([]);
        },
      ),
    );
  }

  getRequestHistoryDetailsByRequestCode(
    requestCode: string | null,
  ): Observable<inventory_update_requests_history_detail[]> {
    if (requestCode === null) {
      console.error(
        "MaterialService (HTTP): Cannot fetch history details with a null ID.",
      );
      return of<inventory_update_requests_history_detail[]>([]);
    }

    const url = `${this.apiRequestHistory}/${requestCode}`;
    console.log(
      `MaterialService (HTTP): Fetching request history details from ${url}`,
    );

    return this.http.get<inventory_update_requests_history_detail[]>(url).pipe(
      tap((data) =>
        console.log(
          `MaterialService (HTTP): History details for request ${requestCode} successfully fetched:`,
          data,
        ),
      ),
      catchError(
        (err): Observable<inventory_update_requests_history_detail[]> => {
          console.error(
            `MaterialService (HTTP): Error fetching request history details for ID ${requestCode}:`,
            err,
          );

          return of<inventory_update_requests_history_detail[]>([]);
        },
      ),
    );
  }
  public clearAllSelections(): void {
    this._selectedIds.next([]);
    sessionStorage.removeItem("selectedMaterialIds");
    const cleared = this._materialsData.value.map((item) => ({
      ...item,
      checked: false,
      select_update: false,
    }));
    this._materialsData.next(cleared);
  }

  public getDataUpdateRequest(): Observable<inventory_update_requests[]> {
    return this.updateRequestData$.pipe(
      map((requests: any[]) =>
        requests.map((req) => {
          let numericId: number | null = null;
          if (req.id !== null && req.id !== undefined) {
            const parsedId = parseInt(String(req.id), 10);
            if (!isNaN(parsedId)) {
              numericId = parsedId;
            } else {
              console.warn(
                `MaterialService: Failed to parse ID '${req.id}' for requestCode '${req.requestCode}'. ID will be set to null.`,
              );
            }
          }
          return { ...req, id: numericId } as inventory_update_requests;
        }),
      ),
    );
  }

  public getRequestDetailsById(
    id: number | null,
  ): Observable<inventory_update_requests_detail[]> {
    console.log("dang goi api");
    if (id === null) {
      console.error(
        "MaterialService (HTTP): Cannot fetch request details with a null ID.",
      );
      return of([]);
    }
    const url = `${this.apiRequestDetail}/${id}`;
    console.log(`MaterialService (HTTP): Fetching request details from ${url}`);

    return this.http.get<inventory_update_requests_detail[]>(url).pipe(
      tap((data) =>
        console.log(
          `MaterialService (HTTP): Details for request ${id} successfully fetched:`,
          data,
        ),
      ),
      catchError((err) => {
        console.error(
          `MaterialService (HTTP): Error fetching request details for ID ${id}:`,
          err,
        );
        return of([]);
      }),
    );
  }

  public getItemsForUpdate(): Observable<RawGraphQLMaterial[]> {
    return this.materialsData$.pipe(
      map((materials) =>
        materials.filter((material) => material.checked === true),
      ),
    );
  }
  public uncheckItemsAfterUpdate(ids: string[]): void {
    const updatedData = this._materialsData.value.map((item) =>
      ids.includes(item.inventoryId)
        ? { ...item, checked: false, select_update: false }
        : item,
    );
    this._materialsData.next(updatedData);
    // Cập nhật selectedIds luôn nếu cần
    const newSelectedIds = this._selectedIds.value.filter(
      (id) => !ids.includes(id),
    );
    this._selectedIds.next(newSelectedIds);
    sessionStorage.setItem(
      "selectedMaterialIds",
      JSON.stringify(newSelectedIds),
    );
  }

  public getData(): Observable<RawGraphQLMaterial[]> {
    return this.materialsData$;
  }

  public getSelectedIds(): Observable<string[]> {
    return this._selectedIds.asObservable();
  }

  public addItem(item: RawGraphQLMaterial): void {
    const currentData = this._materialsData.value;
    this._materialsData.next([...currentData, item]);
  }

  public removeItem(inventoryIdToRemove: string): void {
    const filteredData = this._materialsData.value.filter(
      (item) => item.inventoryId !== inventoryIdToRemove,
    );
    this._materialsData.next(filteredData);
    const currentSelectedIds = this._selectedIds.value;
    if (currentSelectedIds.includes(inventoryIdToRemove)) {
      const newSelectedIds = currentSelectedIds.filter(
        (selectedId) => selectedId !== inventoryIdToRemove,
      );
      this._selectedIds.next(newSelectedIds);
      sessionStorage.setItem(
        "selectedMaterialIds",
        JSON.stringify(newSelectedIds),
      );
    }
  }

  //  tạo dữ liệu gửi rq update
  public postInventoryUpdateRequest(
    dialogData: {
      updatedItems: MaterialItem[];
      selectedWarehouse?: string | null;
      approvers: string[];
    },
    currentUser: string,
  ): Observable<any> {
    const requestCode = `REQ-${Date.now()}`;
    const currentTime = new Date().toISOString();
    const requestHeader: inventory_update_requests = {
      id: null,
      requestCode: requestCode,
      createdTime: currentTime,
      updatedTime: currentTime,
      requestedBy: currentUser,
      approvedBy: dialogData.approvers ? dialogData.approvers.join(", ") : "",
      status: "PENDING",
    };
    const requestDetails: inventory_update_requests_detail[] =
      dialogData.updatedItems.map((item) => ({
        id: null,
        materialId: item.materialIdentifier,
        updatedBy: currentUser,
        createdTime: currentTime,
        updatedTime: currentTime,
        productCode: item.partNumber,
        productName: item.partNumber,
        quantity: String(item.quantity),
        quantityChange: String(item.quantityChange),
        type: item.extendExpiration ? "EXTEND" : "MOVE",
        locationId: item.locationId ?? "",
        locationName: this.getLocationNameById(item.locationId) ?? "",
        status: item.calculatedStatus,
        requestId: null,
        expiredTime: item.expirationDate ? String(item.expirationDate) : "",
      }));
    const payload: UpdateRequestInfo = {
      request: requestHeader,
      detail: requestDetails,
    };
    console.log("Payload gửi lên:", payload);
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
    });
    return this.http
      .post(this.apiUrl_post_request_update, payload, { headers })
      .pipe(
        tap(() => {
          console.log(
            "MaterialService: Inventory update request successful. Refreshing materials data.",
          );

          this.fetchMaterialsData(
            1,
            this.defaultPageSize,
            // true,
          );
          this.fetchAllInventoryUpdateRequests();
        }),
      );
  }

  // gửi dữ liệu update vào db
  public postApproveInventoryUpdate(
    parentRequestId: number | null,
    dialogData: {
      updatedItems: inventory_update_requests_detail[];
      selectedWarehouse?: RawGraphQLLocation | string | null;
      approvers: string[];
    },
    currentUser: string,
  ): Observable<any> {
    const requestCode = `REQ-${Date.now()}`;
    const currentTime = new Date().toISOString();
    const requestHeader: inventory_update_requests = {
      id: parentRequestId,
      requestCode: requestCode,
      createdTime: currentTime,
      updatedTime: currentTime,
      requestedBy: currentUser,
      approvedBy: dialogData.approvers ? dialogData.approvers.join(", ") : "",
      status: "APPROVE",
    };
    const requestDetails: inventory_update_requests_detail[] =
      dialogData.updatedItems.map((item) => ({
        id: item.id,
        materialId: String(item.materialId ?? ""),
        updatedBy: currentUser,
        createdTime: currentTime,
        updatedTime: currentTime,
        productCode: item.productCode,
        productName: item.productName,
        quantity: String(item.quantity),
        type: item.type,
        locationId: item.locationId ?? "",
        locationName: this.getLocationNameById(item.locationId) ?? "",
        status: item.status,
        requestId: null,
        quantityChange: String(item.quantityChange),
        expiredTime: item.expiredTime || "",
      }));
    const payload: UpdateInfo = {
      request: requestHeader,
      detail: requestDetails,
    };
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
    });
    return this.http.post(this.apiUrl_post_update, payload, { headers }).pipe(
      tap(() => {
        console.log(
          "MaterialService: Approve inventory update successful. Refreshing materials data.",
        );
        this.fetchMaterialsData(
          1,
          this.defaultPageSize,
          // true,
        );
        this.fetchAllInventoryUpdateRequests();
      }),
    );
  }
  // Từ chối cập nhật vào db
  public postRejectInventoryUpdate(
    parentRequestId: number | null,
    dialogData: {
      updatedItems: inventory_update_requests_detail[];
      selectedWarehouse?: RawGraphQLLocation | string | null;
      approvers: string[];
    },
    currentUser: string,
  ): Observable<any> {
    const requestCode = `REQ-${Date.now()}`;
    const currentTime = new Date().toISOString();
    const requestHeader: inventory_update_requests = {
      id: parentRequestId,
      requestCode: requestCode,
      createdTime: currentTime,
      updatedTime: currentTime,
      requestedBy: currentUser,
      approvedBy: dialogData.approvers ? dialogData.approvers.join(", ") : "",
      status: "REJECT",
      // approve | reject
    };
    const requestDetails: inventory_update_requests_detail[] =
      dialogData.updatedItems.map((item) => ({
        id: item.id,
        materialId: String(item.materialId ?? ""),
        updatedBy: currentUser,
        createdTime: currentTime,
        updatedTime: currentTime,
        productCode: item.productCode,
        productName: item.productName,
        quantity: String(item.quantity),
        type: item.type,
        locationId: item.locationId ?? "",
        locationName: this.getLocationNameById(item.locationId) ?? "",
        status: "REJECT",
        requestId: null,
        quantityChange: String(item.quantityChange),
        expiredTime: item.expiredTime || "",
      }));
    const payload: UpdateInfo = {
      request: requestHeader,
      detail: requestDetails,
    };
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
    });
    return this.http.post(this.apiUrl_post_update, payload, { headers }).pipe(
      tap(() => {
        console.log(
          "MaterialService: Reject inventory update successful. Refreshing materials data.",
        );
        this.fetchMaterialsData(
          1,
          this.defaultPageSize,
          // true,
        );
        this.fetchAllInventoryUpdateRequests();
      }),
    );
  }
  public getApprovers(): Observable<UserSummary[]> {
    return this.http.get<UserSummary[]>("/api/approvers").pipe(
      tap((users) => console.log("Approvers fetched:", users)),
      catchError((err) => {
        console.error("Fetch approvers error:", err);
        return of([] as UserSummary[]);
      }),
    );
  }
  // history
  getMaterialHistoryByRange(body: {
    startTime: string;
    endTime: string;
  }): Observable<any> {
    return this.http.post(this.apiRequestHistory, body);
  }

  // #region Private methods
  private refreshSelectedItems(): void {
    const items = this._selectedIds.value
      .map((id) => this._materialsCache.get(id))
      .filter((i): i is RawGraphQLMaterial => !!i);
    console.log("[Service] refreshSelectedItems →", items);
    this._selectedItems.next(items);
  }
  private cachePage(items: RawGraphQLMaterial[]): void {
    items.forEach((i) => this._materialsCache.set(i.inventoryId, i));
    console.log("[Service] cachePage → cache size:", this._materialsCache.size);
  }
  private applySelectionFlags(
    raws: RawGraphQLMaterial[],
    selectedIds: string[],
  ): RawGraphQLMaterial[] {
    const set = new Set(selectedIds);
    return raws.map((m) => ({
      ...m,
      checked: set.has(m.inventoryId),
      select_update: set.has(m.inventoryId),
    }));
  }

  private fetchLocations(): void {
    this.http.get<RawGraphQLLocation[]>(this.apiLocations).subscribe({
      next: (data) => {
        this._locationsData.next(data);
        console.log(
          "MaterialService (HTTP): Locations data successfully fetched:",
          data,
        );
      },
      error: (err) => {
        console.error("Loi lay api locations:", err);
        this._locationsData.next([]);
      },
    });
  }

  private loadSelectedIds(): void {
    const savedIds = sessionStorage.getItem("selectedMaterialIds");
    if (savedIds) {
      this._selectedIds.next(JSON.parse(savedIds));
    }
  }

  private mapRawToMaterial(
    raw: RawGraphQLMaterial,
    selectedIds: string[],
  ): RawGraphQLMaterial {
    const locations: RawGraphQLLocation[] = this._locationsData.getValue();
    const locationMap = new Map<string, string>();
    locations.forEach((loc) => {
      let id = loc.locationId;
      if (typeof id !== "string") {
        id = String(id);
      }
      id = id.trim().toLowerCase();
      locationMap.set(id, loc.locationName);
    });
    let rawLocationId = raw.locationId;
    if (typeof rawLocationId !== "string") {
      rawLocationId = String(rawLocationId);
    }
    rawLocationId = rawLocationId.trim().toLowerCase();
    return {
      ...raw,
      inventoryId: raw.inventoryId,
      checked: selectedIds.includes(raw.inventoryId),
      select_update: false,
      locationName: locationMap.get(rawLocationId) ?? "Unknown",
    };
  }

  private getLocationNameById(locationId: string | null): string | null {
    if (!locationId) {
      return null;
    }
    const location = this._locationsData.value.find(
      (loc) => loc.locationId === locationId,
    );
    return location ? location.locationName : null;
  }

  private saveExcelFile(buffer: any, fileName: string): void {
    const data: Blob = new Blob([buffer], { type: this.fileType });
    FileSaver.saveAs(data, fileName + this.fileExtension);
  }

  // #endregion
}
