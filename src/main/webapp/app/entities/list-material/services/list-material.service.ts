import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable, catchError, map, of, tap } from "rxjs";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { environment } from "app/environments/environment";
import * as XLSX from "xlsx";
import * as FileSaver from "file-saver";
import { MaterialItem } from "../dialog/list-material-update-dialog";
import { AccountService } from "app/core/auth/account.service";

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
// #endregion

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

  private restBaseUrl = environment.restApiBaseUrl;

  private apiMaterialUrl = this.restBaseUrl + "/api/inventory";
  private apiRequest = this.restBaseUrl + "/api/request";
  // private apiRequest = "http://localhost:8085" + "/api/request";

  private apiUrl_post_request_update = this.restBaseUrl + "/api/request";
  private apiUrl_post_update = this.restBaseUrl + "/api/request";

  private _updateManageData = new BehaviorSubject<inventory_update_requests[]>(
    [],
  );
  private apiRequestDetail = this.restBaseUrl + "/api/request/detail"; // + /requestCode : lấy chi tiết

  private apiRequestHistory = this.restBaseUrl + "/api/request/history"; // post lấy theo tháng, +/requestCode : lấy chi tiết

  private apiRequestHistoryDetail =
    this.restBaseUrl + "/api/request/history/detail";

  private _materialsDataFetchedOnce = false;
  private readonly defaultPageSize = 15;
  private _updatedInventoryIds = new Set<string>();
  // #endregion

  // #region Constructor
  constructor(
    private http: HttpClient,
    private accountService: AccountService,
  ) {
    this.loadSelectedIds();
    this.fetchMaterialsData(
      0,
      this.defaultPageSize,
      undefined,
      undefined,
      undefined,
      false,
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

  public fetchMaterialsData(
    offset: number,
    limit: number,
    filter?: string,
    sortBy?: string,
    sortDirection?: string,
    forceRefresh: boolean = false,
  ): void {
    if (
      !forceRefresh &&
      this._materialsDataFetchedOnce &&
      this._materialsData.getValue().length > 0
    ) {
      console.log(
        "MaterialService (HTTP): Skipping fetch, data already loaded and forceRefresh is false.",
      );
      return;
    }
    console.log(
      `MaterialService (HTTP): Fetching materials from ${this.apiMaterialUrl}. Force refresh: ${forceRefresh}`,
    );
    this.http.get<RawGraphQLMaterial[]>(this.apiMaterialUrl).subscribe({
      next: (apiData) => {
        if (Array.isArray(apiData)) {
          const selectedIds = this._selectedIds.value;
          const filteredData = apiData.filter(
            (item) => !this._updatedInventoryIds.has(item.inventoryId),
          );
          const mappedData = filteredData.map((rawItem) =>
            this.mapRawToMaterial(rawItem, selectedIds),
          );
          this._materialsData.next(mappedData);
          this._totalCount.next(mappedData.length);
          this._materialsDataFetchedOnce = true;
          console.log("typeof callback:", typeof apiData);
          console.log("bo hang da cap nhat", filteredData);
          console.log(
            `MaterialService (HTTP): Materials data ${forceRefresh ? "refreshed" : "loaded"}. Count:`,
            mappedData.length,
          );
        } else {
          console.warn(
            "MaterialService (HTTP): Materials API did not return an array. Received:",
            apiData,
          );
          this._materialsData.next([]);
          this._totalCount.next(0);
          this._materialsDataFetchedOnce = true;
        }
      },
      error: (err) => {
        console.error("MaterialService (HTTP): Error fetching materials:", err);
        this._materialsData.next([]);
        this._totalCount.next(0);
        this._materialsDataFetchedOnce = true;
      },
    });
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
    const updatedData = this._materialsData.value.map((item) =>
      item.inventoryId === materialId
        ? {
            ...item,
            checked: !item.checked,
            select_update: !item.select_update,
          }
        : item,
    );
    this._materialsData.next(updatedData);
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
            0,
            this.defaultPageSize,
            undefined,
            undefined,
            undefined,
            true,
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
          0,
          this.defaultPageSize,
          undefined,
          undefined,
          undefined,
          true,
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
          0,
          this.defaultPageSize,
          undefined,
          undefined,
          undefined,
          true,
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
    return this.http.get<any>(this.apiRequestHistory, { params: body });
  }

  private loadSelectedIds(): void {
    const savedIds = sessionStorage.getItem("selectedMaterialIds");
    if (savedIds) {
      this._selectedIds.next(JSON.parse(savedIds));
    }
  }

  private fetchAndInitializeData(): void {
    this.http.get<RawGraphQLMaterial[]>(this.apiMaterialUrl).subscribe({
      next: (apiData) => {
        const selectedIds = this._selectedIds.value;
        const updatedData = apiData.map((item) => ({
          ...item,
          checked: selectedIds.includes(item.inventoryId),
        }));
        this._materialsData.next(updatedData);
        console.log(
          "MaterialService (HTTP): Data successfully fetched and set to _materialsData:",
          updatedData,
        );
      },
      error: (err) => {
        console.error(
          "MaterialService (HTTP): Error fetching data from API:",
          err,
        );
        this._materialsData.next([]);
        this._totalCount.next(0);
      },
    });
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
