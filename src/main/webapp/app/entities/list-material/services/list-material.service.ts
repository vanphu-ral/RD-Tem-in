import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable, catchError, map, of, tap } from "rxjs";
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
  private _locationsData = new BehaviorSubject<RawGraphQLLocation[]>([]);

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
    const saved = sessionStorage.getItem("selectedMaterialIds");
    if (saved) {
      try {
        this._selectedIds.next(JSON.parse(saved));
      } catch {
        /* empty */
      }
    }

    this.fetchLocations();
    this.loadSelectedIds();
    this.fetchMaterialsData(
      1,
      this.defaultPageSize,
      // false,
    );

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

  // lấy dư liệu cho trang danh sách
  // public fetchMaterialsData(
  //   pageIndex: number, // số trang hiện tại
  //   limit: number, // số lượng mỗi trang
  //   // forceRefresh: boolean = true,
  // ): Observable<ApiMaterialResponse> {

  //   // body kèm với search filter
  //   const body: { [key: string]: any } = {
  //     materialIdentifier: "",
  //     status: "",
  //     partNumber: "",
  //     quantity: null,
  //     availableQuantity: null,
  //     lotNumber: "",
  //     userData4: "",
  //     locationName: "",
  //     expirationDate: "",
  //     pageNumber: pageIndex,
  //     itemPerPage: limit,
  //   };

  //   console.log("log api material moi", body);
  //   return this.http.post<ApiMaterialResponse>(this.apiMaterialUrl, body).pipe(
  //     tap((response) => {
  //       console.log("log response", response);
  //       if (response?.inventories && Array.isArray(response.inventories)) {
  //         this._totalCount.next(response.totalItems);
  //         const selectedIds = this._selectedIds.value;
  //         const filteredData = response.inventories.filter(
  //           (item) => !this._updatedInventoryIds.has(item.inventoryId),
  //         );
  //         const mappedData = filteredData.map((rawItem) =>
  //           this.mapRawToMaterial(rawItem, selectedIds),
  //         );
  //         this._materialsData.next(mappedData);
  //         // this._totalCount.next(response.totalItems); // Đã gọi ở trên
  //         this._materialsDataFetchedOnce = true;
  //         console.log("typeof callback:", typeof response.inventories);
  //         console.log("bo hang da cap nhat", filteredData);
  //         console.log(
  //           // `MaterialService (HTTP): Materials data ${forceRefresh ? "refreshed" : "loaded"}. Count:`,
  //           mappedData.length,
  //         );
  //       } else {
  //         console.warn(
  //           "MaterialService (HTTP): Materials API did not return an array. Received:",
  //           response,
  //         );
  //         this._materialsData.next([]);
  //         this._totalCount.next(0);
  //         this._materialsDataFetchedOnce = true;
  //       }
  //     }),
  //     catchError((err) => {
  //       console.error("MaterialService (HTTP): Error fetching materials:", err);
  //       this._materialsData.next([]);
  //       this._totalCount.next(0);
  //       this._materialsDataFetchedOnce = true;
  //       // Quan trọng: Trả về một Observable mới (ví dụ: of()) để luồng không bị ngắt
  //       return of({ totalItems: 0, inventories: [] } as ApiMaterialResponse);
  //     }),
  //   );
  // }
  fetchMaterialsData(
    pageIndex: number, // Số trang (bắt đầu từ 0 hoặc 1 tùy BE)
    limit: number, // Số lượng item/trang
    filters?: {
      // Filter tùy chọn (có thể không gửi)
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
        console.log("API Response invent:", response);
        if (response?.inventories) {
          this._totalCount.next(response.totalItems);
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

  public toggleItemSelection(materialId: string): void {
    const currentIds = this._selectedIds.value;
    const newIds = currentIds.includes(materialId)
      ? currentIds.filter((id) => id !== materialId)
      : [...currentIds, materialId];

    this._selectedIds.next(newIds);
    sessionStorage.setItem("selectedMaterialIds", JSON.stringify(newIds));

    const updated = this.applySelectionFlags(this._materialsData.value, newIds);
    this._materialsData.next(updated);
  }

  public removeItemFromUpdate(materialId: string): void {
    const currentIds = this._selectedIds.value;
    const newIds = currentIds.filter((id) => id !== materialId);
    this._selectedIds.next(newIds);
    sessionStorage.setItem("selectedMaterialIds", JSON.stringify(newIds));
    const updatedData = this._materialsData.value.map((item) =>
      item.inventoryId === materialId
        ? { ...item, checked: false, select_update: false }
        : item,
    );
    this._materialsData.next(updatedData);
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
    return this.selectedIds$;
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
          // After a successful update, force a refresh of the main materials list
          this.fetchMaterialsData(
            1,
            this.defaultPageSize,
            // true,
          );
          this.fetchAllInventoryUpdateRequests(); // Also refresh the list of update requests
        }),
        // Consider adding catchError here if specific error handling for this POST is needed
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
  // history
  getMaterialHistoryByRange(body: {
    startTime: string;
    endTime: string;
  }): Observable<any> {
    return this.http.post(this.apiRequestHistory, body);
  }

  // #region Private methods
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
