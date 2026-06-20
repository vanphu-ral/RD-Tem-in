import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import {
  Observable,
  catchError,
  concatMap,
  firstValueFrom,
  forkJoin,
  from,
  last,
  map,
  of,
  switchMap,
  take,
  throwError,
} from "rxjs";
import { ApplicationConfigService } from "app/core/config/application-config.service";
import { WarehouseCacheService } from "app/entities/list-material/services/warehouse-cache.service";
import { environment } from "app/environments/environment";
import { ExcelImportData } from "../models/list-product-of-request.model";

export interface SapOwhsDto {
  whsCode: string;
  whsName: string;
  createDate?: string;
  updateDate?: string;
  address2?: string;
  uwhskeeper?: string;
}

/** Sản phẩm từ test GraphQL — có WhsCode và sapSendStatus. */
export interface ReceivingProductRow {
  id: number;
  requestCreateTemId?: number;
  sapCode: string;
  productName: string;
  partNumber: string;
  lot: string;
  initialQuantity: number;
  temQuantity: number;
  vendor: string;
  vendorName?: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  storageUnit: string;
  expirationDate: string;
  manufacturingDate: string;
  arrivalDate: string;
  numberOfPrints?: number;
  UploadPanacim?: boolean;
  WhsCode?: string | null;
  sapSendStatus?: boolean | null;
}

export interface ReceivingProductSyncRow {
  productId?: number;
  item: ExcelImportData;
}

export interface ReceivingProductSyncOptions {
  rows: ReceivingProductSyncRow[];
  deletedProductIds?: number[];
}

interface GraphQlResponse<T> {
  data?: T;
  errors?: Array<{ message?: string }>;
}

interface UpdateResponse {
  success: boolean;
  message: string;
}

export interface SapPoInfoHeader {
  id?: number;
  oporCardCode?: string;
  oporCardName?: string;
  oporDocEntry?: string;
  oporDocNum?: string;
  oporComments?: string;
  prMapPo?: string;
}

export interface SapPoInfoDetail {
  id?: number;
  por1ItemCode?: string;
  por1Dscription?: string;
  por1LineVendor?: string;
  por1Quantity?: string;
  por1LineNum?: string;
  por1LineStatus?: string;
  por1UnitMsr?: string;
  por1UOMCode?: string;
  por1WhsCode?: string;
}

export interface SapPoInfoResponse {
  poInfo: SapPoInfoHeader | null;
  poDetails: SapPoInfoDetail[];
}

/** Bản ghi vật tư đã chọn theo PO (userData5). */
export interface ProductInPoStatusDto {
  id?: number;
  sapCode: string;
  productName: string;
  whsCode: string;
  userData5: string;
  createdAt?: string;
  createBy?: string;
  quantityByPo?: number;
  vendor?: string;
  vendorName?: string;
  uomcode?: string;
  listRequestCreateTemId?: number | null;
}

export interface ProductInPoStatusCreatePayload {
  sapCode: string;
  productName: string;
  whsCode: string;
  userData5: string;
  createdAt: string;
  createBy: string;
  quantityByPo: number;
  vendor: string;
  vendorName: string;
  uomcode: string;
  listRequestCreateTemId?: number | null;
}

export interface PoReconcileTableRow {
  sapCode: string;
  itemName: string;
  vendor: string;
  /** Tổng SL các lô con trên bảng. */
  totalQuantity: number;
}

export interface PoReconcileRowResult {
  sapCode: string;
  passed: boolean;
  errors: string[];
}

export interface ReceivingPoImportPayload {
  poNumber?: string | null;
  vendorCode: string;
  vendorName: string;
  entryDate?: string;
  storageUnit?: string;
  temIdentificationScenarioId?: number | null;
  mappingConfig?: string;
  status?: string;
  createdBy: string;
  createdAt?: string;
  updatedBy?: string;
  updatedAt?: string;
  note?: string;
}

export interface ReceivingPoDetail {
  id: number | null;
  sapCode: string;
  sapName: string;
  quantityContainer: number | null;
  totalQuantity: number | null;
  partNumber: string | null;
}

export interface ReceivingPoImportResponse {
  poImportTem?: {
    id: number;
    poNumber: string;
    vendorCode: string;
    vendorName: string;
  };
  vendorTransaction?: {
    transaction: { id: number; storageUnit?: string };
    poDetails: ReceivingPoDetail[];
  };
  caseType?: string;
}

export interface ItemDataDto {
  itemCode?: string;
  itemName?: string;
}

export interface SapOitmDto {
  id?: number;
  itemCode?: string;
  itemName?: string;
  itmsGrpCod?: string;
  uPartNumber?: string | null;
}

export interface WarehouseLocation {
  id: number;
  locationName: string;
  locationFullName: string;
}

export interface PoReconcileRequest {
  poNumber: string;
  vendorCode: string;
  vendorName: string;
  sapCodes: string[];
  transactionId?: number | null;
  createdBy: string;
}

export interface PoReconcileResponse {
  matched: boolean;
  partialMatched?: boolean;
  message: string;
  poNumber?: string;
  matchedSapCodes?: string[];
  unmatchedSapCodes?: string[];
  missingFromPo?: string[];
  rowResults?: PoReconcileRowResult[];
}

/**
 * SAP map StorageUnit → U_Unit (SAP_GRP_L), giới hạn ~10 ký tự — ví dụ "RD", "19-B08-01".
 */
export const SAP_STORAGE_UNIT_MAX_LENGTH = 10;

/** Trả về thông báo lỗi nếu StorageUnit không hợp lệ cho SAP; null nếu OK. */
export function validateStorageUnitForSap(storageUnit: string): string | null {
  const trimmed = storageUnit.trim();
  if (!trimmed) {
    return "thiếu vị trí kho (StorageUnit)";
  }
  if (trimmed.length > SAP_STORAGE_UNIT_MAX_LENGTH) {
    return (
      `StorageUnit "${trimmed}" quá dài (${trimmed.length} ký tự). ` +
      `SAP chỉ nhận tối đa ${SAP_STORAGE_UNIT_MAX_LENGTH} ký tự ` +
      `(ví dụ: RD, 19-B08-01).`
    );
  }
  return null;
}

export function resolveHttpErrorMessage(
  err: unknown,
  fallback: string,
): string {
  if (err && typeof err === "object") {
    const httpErr = err as {
      error?: string | { detail?: string; title?: string; message?: string };
      message?: string;
    };
    if (typeof httpErr.error === "string" && httpErr.error.trim()) {
      return httpErr.error.trim();
    }
    const detail =
      typeof httpErr.error === "object"
        ? httpErr.error?.detail?.trim()
        : undefined;
    if (detail) {
      return detail;
    }
    const title =
      typeof httpErr.error === "object"
        ? httpErr.error?.title?.trim()
        : undefined;
    if (title && title !== "Internal Server Error") {
      return title;
    }
  }
  if (err instanceof Error && err.message?.trim()) {
    return err.message.trim();
  }
  return fallback;
}

export interface GoodsReceiptPoLine {
  Vendor: string;
  DocEntry: number;
  ItemCode: string;
  Quantity: number;
  LotNum: string;
  ReelID: string;
  PartNumber: string;
  ExpirationDate: string;
  ManufacturingDate: string;
  StorageUnit: string;
}

export interface GoodsReceiptPoPayload {
  OPDN: GoodsReceiptPoLine[];
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

@Injectable({
  providedIn: "root",
})
export class ReceivingSuppliesService {
  private baseUrl = this.applicationConfigService.getEndpointFor("api");
  private testurl = `http://192.168.68.77:8085/api`;
  private readonly graphqlUrl = environment.graphqlApiUrl;
  private sapOitmUrl =
    this.applicationConfigService.getEndpointFor("/api/sap-oitms");
  private isWarehouseInitDone = false;
  private isFetchingWarehouses = false;

  constructor(
    private http: HttpClient,
    private applicationConfigService: ApplicationConfigService,
    private warehouseCache: WarehouseCacheService,
  ) {}

  /** Tạo / truy vấn đơn nhập từ SAP PO. */
  processPoImport(
    payload: ReceivingPoImportPayload,
  ): Observable<ReceivingPoImportResponse | null> {
    return this.http
      .post<ReceivingPoImportResponse>(
        `${this.baseUrl}/po-import-tems/process`,
        payload,
      )
      .pipe(catchError(() => of(null)));
  }

  /** Bổ sung / cập nhật thông tin PO cho transaction hiện có. */
  updateTransactionWithPo(payload: {
    id: number;
    poNumber: string;
    updatedBy: string;
  }): Observable<ReceivingPoImportResponse | null> {
    return this.http
      .post<ReceivingPoImportResponse>(
        `${this.baseUrl}/po-import-tems/update-transaction`,
        payload,
      )
      .pipe(catchError(() => of(null)));
  }

  getItemDataByItemCode(itemCode: string): Observable<ItemDataDto | null> {
    return this.getSapOitmBySapCode(itemCode).pipe(
      map((data) =>
        data
          ? {
              itemCode: data.itemCode ?? itemCode.trim(),
              itemName: data.itemName ?? "",
            }
          : null,
      ),
    );
  }

  /** Ưu tiên IndexedDB — chỉ gọi API khi cache trống. */
  async initWarehouses(): Promise<void> {
    if (this.isWarehouseInitDone || this.isFetchingWarehouses) {
      return;
    }

    const cached = await this.warehouseCache.getAll();
    if (cached.length > 0) {
      this.isWarehouseInitDone = true;
      return;
    }

    this.isFetchingWarehouses = true;
    try {
      const data = await firstValueFrom(
        this.http
          .get<WarehouseLocation[]>(`${this.baseUrl}/location`)
          .pipe(catchError(() => of([]))),
      );

      if (data.length > 0) {
        await this.warehouseCache.saveAll(
          data.map((loc) => ({
            locationId: loc.id,
            locationName: loc.locationName,
            locationFullName: loc.locationFullName,
          })),
        );
      }
      this.isWarehouseInitDone = true;
    } finally {
      this.isFetchingWarehouses = false;
    }
  }

  /** Tìm vị trí trong IndexedDB (tối đa 50 kết quả). */
  async searchWarehouses(keyword: string): Promise<WarehouseLocation[]> {
    await this.initWarehouses();
    const results = await this.warehouseCache.searchByName(keyword);
    return results.map((w) => ({
      id: w.locationId,
      locationName: w.locationName,
      locationFullName: w.locationFullName,
    }));
  }

  /** Danh sách nhà cung cấp SAP (dùng cho autocomplete). */
  getSapOcrds(): Observable<SapOcrd[]> {
    return this.http
      .get<SapOcrd[]>(`${this.baseUrl}/sap-ocrds`)
      .pipe(catchError(() => of([])));
  }

  getItemName(itemCode: string): Observable<string> {
    return this.getItemDataByItemCode(itemCode).pipe(
      map((data) => data?.itemName ?? ""),
    );
  }

  /** POST nhập kho SAP theo PO (proxy qua backend — tránh CORS). */
  postGoodsReceiptPo(payload: GoodsReceiptPoPayload): Observable<string> {
    return this.http.post(
      this.applicationConfigService.getEndpointFor("api/post-goods-receipt-po"),
      payload,
      { responseType: "text" },
    );
  }

  /** GET /api/sap-po-info/{oporDocEntry} */
  getSapPoInfo(poNumber: string): Observable<SapPoInfoResponse | null> {
    const po = poNumber.trim();
    if (!po) {
      return of(null);
    }
    return this.http
      .get<SapPoInfoResponse>(
        `${this.baseUrl}/sap-po-info/${encodeURIComponent(po)}`,
      )
      .pipe(catchError(() => of(null)));
  }

  /** GET /api/product-in-po-status/user-data5/{userData5} */
  getProductInPoStatusByUserData5(
    userData5: string,
  ): Observable<ProductInPoStatusDto[]> {
    const po = userData5.trim();
    if (!po) {
      return of([]);
    }
    return this.http
      .get<
        ProductInPoStatusDto | ProductInPoStatusDto[]
      >(`${this.baseUrl}/product-in-po-status/user-data5/${encodeURIComponent(po)}`)
      .pipe(
        map((res) => {
          if (!res) {
            return [];
          }
          return Array.isArray(res) ? res : [res];
        }),
        catchError(() => of([])),
      );
  }

  /** POST /api/product-in-po-status */
  createProductInPoStatus(
    payload: ProductInPoStatusCreatePayload,
  ): Observable<ProductInPoStatusDto | null> {
    return this.http
      .post<ProductInPoStatusDto>(
        `${this.baseUrl}/product-in-po-status`,
        payload,
      )
      .pipe(catchError(() => of(null)));
  }

  /**
   * Đối chiếu từng dòng cha trên bảng với poDetails từ sap-po-info.
   * So khớp: sapCode↔por1ItemCode, itemName↔por1Dscription, vendor↔por1LineVendor,
   * tổng SL lô con ↔ por1Quantity (không được vượt SL PO).
   */
  reconcilePoWithSapInfo(
    poNumber: string,
    tableRows: PoReconcileTableRow[],
  ): Observable<PoReconcileResponse> {
    const po = poNumber.trim();
    return this.getSapPoInfo(po).pipe(
      map((res) => this.buildReconcileResultFromSapPoInfo(po, tableRows, res)),
    );
  }

  /** Lấy tên & part number theo mã SAP từ /api/sap-oitms/itemCode/{itemCode}. */
  getSapOitmBySapCode(sapCode: string): Observable<SapOitmDto | null> {
    const code = sapCode.trim();
    if (!code) {
      return of(null);
    }
    return this.http
      .get<
        SapOitmDto[]
      >(`${this.sapOitmUrl}/itemCode/${encodeURIComponent(code)}`)
      .pipe(
        map((items) =>
          Array.isArray(items) && items.length > 0 ? items[0] : null,
        ),
        catchError(() => of(null)),
      );
  }

  /** Danh sách kho SAP. */
  getSapWarehouses(): Observable<SapOwhsDto[]> {
    return this.http.get<SapOwhsDto[]>(`${this.testurl}/owhs`).pipe(
      map((rows) => (rows ?? []).filter((r) => Boolean(r.whsCode?.trim()))),
      catchError(() => of([])),
    );
  }

  /** Lấy sản phẩm đơn từ GraphQL (WhsCode, sapSendStatus). */
  getProductsByRequestId(requestId: number): Observable<ReceivingProductRow[]> {
    const query = `
      query GetProductsByRequest($requestId: Int!) {
        getProductOfRequestByRequestId(requestId: $requestId) {
          id
          requestCreateTemId
          sapCode
          temQuantity
          partNumber
          productName
          lot
          initialQuantity
          vendor
          vendorName
          userData1
          userData2
          userData3
          userData4
          userData5
          storageUnit
          expirationDate
          manufacturingDate
          arrivalDate
          numberOfPrints
          UploadPanacim
          WhsCode
          sapSendStatus
        }
      }
    `;
    return this.postGraphql<{
      getProductOfRequestByRequestId: ReceivingProductRow[];
    }>(query, { requestId }).pipe(
      map((data) =>
        (data.getProductOfRequestByRequestId ?? []).map((p) => ({
          ...p,
          id: Number(p.id),
          initialQuantity: Number(p.initialQuantity ?? 0),
          temQuantity: Number(p.temQuantity ?? 0),
        })),
      ),
      catchError(() => of([])),
    );
  }

  createRequestAndProducts(
    vendor: string,
    vendorName: string,
    userData5: string,
    createdBy: string,
    products: ExcelImportData[],
    whsCode = "",
  ): Observable<{ requestId: number; products: ReceivingProductRow[] }> {
    const mutation = `
      mutation CreateRequestAndProducts($input: CreateRequestWithProductsInput!) {
        createRequestAndProducts(input: $input) {
          requestId
          products {
            id
            requestCreateTemId
            sapCode
            productName
            temQuantity
            partNumber
            lot
            initialQuantity
            vendor
            vendorName
            userData1
            userData2
            userData3
            userData4
            userData5
            storageUnit
            expirationDate
            manufacturingDate
            arrivalDate
            numberOfPrints
            WhsCode
            sapSendStatus
          }
          message
        }
      }
    `;
    const productInputs = products.map((item) =>
      this.mapExcelToCreateInput(item),
    );
    return this.postGraphql<{
      createRequestAndProducts: {
        requestId: number;
        products: ReceivingProductRow[];
      };
    }>(mutation, {
      input: {
        vendor,
        vendorName,
        userData5,
        createdBy,
        createdDate: new Date().toISOString().slice(0, 10),
        WhsCode: whsCode.trim(),
        products: productInputs,
      },
    }).pipe(
      switchMap((data) => {
        const result = data.createRequestAndProducts;
        const requestId = Number(result.requestId);
        return this.syncProductExtensions(
          requestId,
          result.products ?? [],
          products,
        ).pipe(
          map((synced) => ({
            requestId,
            products: synced,
          })),
        );
      }),
    );
  }

  updateRequestProducts(
    requestId: number,
    products: ExcelImportData[],
    syncOptions?: ReceivingProductSyncOptions,
  ): Observable<ReceivingProductRow[]> {
    const syncRows = syncOptions?.rows ?? [];
    const deletedIds = syncOptions?.deletedProductIds ?? [];
    if (syncRows.length || deletedIds.length) {
      return this.syncRequestProducts(requestId, syncRows, deletedIds);
    }

    const mutation = `
      mutation UpdateRequestProducts($requestId: Int!, $products: [CreateProductInput!]!) {
        updateRequestProducts(requestId: $requestId, products: $products) {
          id
        }
      }
    `;
    const productInputs = products.map((item) =>
      this.mapExcelToCreateInput(item),
    );
    return this.postGraphql<{
      updateRequestProducts: Array<{ id: number }>;
    }>(mutation, { requestId, products: productInputs }).pipe(
      switchMap(() => this.getProductsByRequestId(requestId).pipe(take(1))),
      switchMap((saved) =>
        this.syncProductExtensions(requestId, saved, products),
      ),
    );
  }

  syncSavedProductsForGenerate(
    requestId: number,
    updates: Array<{ productId: number; item: ExcelImportData }>,
  ): Observable<void> {
    if (!updates.length) {
      return throwError(
        () => new Error("Không có sản phẩm để cập nhật trước khi tạo mã."),
      );
    }
    const calls = updates.map(({ productId, item }) =>
      this.updateProductOnTest(productId, requestId, item),
    );
    return forkJoin(calls).pipe(map(() => undefined));
  }

  updateSapSendStatus(
    productId: number,
    requestId: number,
    item: ExcelImportData,
    sapSendStatus: boolean,
  ): Observable<void> {
    return this.updateProductOnTest(productId, requestId, {
      ...item,
      sapSendStatus,
    });
  }

  private buildReconcileResultFromSapPoInfo(
    poNumber: string,
    tableRows: PoReconcileTableRow[],
    res: SapPoInfoResponse | null,
  ): PoReconcileResponse {
    if (!res?.poInfo || !res.poDetails?.length) {
      return {
        matched: false,
        message: `Không tìm thấy PO ${poNumber} hoặc PO không có dòng chi tiết.`,
        poNumber,
      };
    }

    const poDetails = res.poDetails;
    const poDetailBySap = new Map<string, SapPoInfoDetail>();
    poDetails.forEach((d) => {
      const code = (d.por1ItemCode ?? "").trim();
      if (code) {
        poDetailBySap.set(this.normalizeSapCode(code), d);
        poDetailBySap.set(code, d);
      }
    });

    const rowResults: PoReconcileRowResult[] = [];
    const matchedSapCodes: string[] = [];
    const unmatchedSapCodes: string[] = [];

    tableRows.forEach((row) => {
      const sapCode = row.sapCode.trim();
      if (!sapCode) {
        return;
      }

      const poLine =
        poDetailBySap.get(sapCode) ??
        poDetailBySap.get(this.normalizeSapCode(sapCode));

      if (!poLine) {
        unmatchedSapCodes.push(sapCode);
        rowResults.push({
          sapCode,
          passed: false,
          errors: [`Mã SAP ${sapCode} không có trong PO ${poNumber}.`],
        });
        return;
      }

      const errors: string[] = [];
      const poItemCode = (poLine.por1ItemCode ?? "").trim();
      if (
        sapCode !== poItemCode &&
        this.normalizeSapCode(sapCode) !== this.normalizeSapCode(poItemCode)
      ) {
        errors.push(`Mã SAP: bảng "${sapCode}" ≠ PO "${poItemCode}"`);
      }

      const tableName = this.normalizeText(row.itemName);
      const poName = this.normalizeText(poLine.por1Dscription ?? "");
      if (tableName && poName && tableName !== poName) {
        errors.push(
          `Tên vật tư: bảng "${row.itemName}" ≠ PO "${poLine.por1Dscription}"`,
        );
      }

      const tableVendor = this.normalizeText(row.vendor);
      const poVendor = this.normalizeText(poLine.por1LineVendor ?? "");
      if (tableVendor && poVendor && tableVendor !== poVendor) {
        errors.push(
          `Mã vendor: bảng "${row.vendor}" ≠ PO "${poLine.por1LineVendor}"`,
        );
      }

      const poQty = Number.parseFloat(poLine.por1Quantity ?? "0") || 0;
      const tableQty = row.totalQuantity ?? 0;
      if (tableQty > poQty) {
        errors.push(
          `Số lượng: tổng trên bảng (${this.formatQuantity(tableQty)}) vượt quá SL PO (${this.formatQuantity(poQty)}). Yêu cầu sửa lại.`,
        );
      }

      const passed = errors.length === 0;
      rowResults.push({ sapCode, passed, errors });
      if (passed) {
        matchedSapCodes.push(sapCode);
      }
    });

    const tableCodeSet = new Set(
      tableRows
        .map((r) => this.normalizeSapCode(r.sapCode.trim()))
        .filter(Boolean),
    );
    const missingFromPo = poDetails
      .map((d) => (d.por1ItemCode ?? "").trim())
      .filter((code) => code && !tableCodeSet.has(this.normalizeSapCode(code)));

    const passedCount = matchedSapCodes.length;
    const failedCount = rowResults.filter((r) => !r.passed).length;
    const matched = failedCount === 0 && passedCount > 0;
    const partialMatched = passedCount > 0 && failedCount > 0;

    let message: string;
    if (matched) {
      message = `Đối chiếu thành công: ${passedCount}/${tableRows.length} mã khớp PO ${poNumber}.`;
    } else if (partialMatched) {
      message = `Đối chiếu một phần: ${passedCount} pass, ${failedCount} chưa pass (tổng ${tableRows.length} mã trên bảng).`;
    } else {
      message = `Đối chiếu thất bại: ${failedCount} mã chưa khớp PO ${poNumber}.`;
    }

    return {
      matched,
      partialMatched,
      message,
      poNumber,
      matchedSapCodes,
      unmatchedSapCodes,
      missingFromPo,
      rowResults,
    };
  }

  private normalizeSapCode(code: string): string {
    const trimmed = code.trim();
    const stripped = trimmed.replace(/^0+/, "");
    return stripped || "0";
  }

  private normalizeText(value: string): string {
    return (value ?? "").trim().replace(/\s+/g, " ").toLowerCase();
  }

  private formatQuantity(value: number): string {
    if (Number.isInteger(value)) {
      return String(value);
    }
    return value.toLocaleString("vi-VN", { maximumFractionDigits: 4 });
  }

  private syncRequestProducts(
    requestId: number,
    syncRows: ReceivingProductSyncRow[],
    deletedProductIds: number[],
  ): Observable<ReceivingProductRow[]> {
    const toDelete = [
      ...new Set(deletedProductIds.filter((id) => !Number.isNaN(id) && id > 0)),
    ];
    const newItems = syncRows.filter((r) => !r.productId).map((r) => r.item);
    const deleteStep$ = toDelete.length
      ? forkJoin(
          toDelete.map((id) =>
            this.deleteProductOnTest(id).pipe(
              map((body) => {
                if (!body?.success) {
                  throw new Error(
                    body?.message ?? `Xóa sản phẩm #${id} thất bại`,
                  );
                }
              }),
            ),
          ),
        )
      : of([]);

    return deleteStep$.pipe(
      switchMap(() => {
        const updateOps = syncRows
          .filter((r) => r.productId)
          .map((r) =>
            this.updateProductOnTest(r.productId!, requestId, r.item),
          );
        const updateStep$ = updateOps.length ? forkJoin(updateOps) : of([]);

        return updateStep$.pipe(
          switchMap(() => {
            if (!newItems.length) {
              return of(null);
            }
            return from(newItems).pipe(
              concatMap((item) => this.createProductOnTest(requestId, item)),
              last(),
            );
          }),
          switchMap(() => this.getProductsByRequestId(requestId).pipe(take(1))),
        );
      }),
      catchError((err: unknown) =>
        throwError(() => {
          const base = err instanceof Error ? err.message : "Lỗi khi lưu đơn.";
          if (newItems.length) {
            return new Error(
              `Tạo lô mới thất bại. Các lô cũ có thể đã được cập nhật trước đó. ${base}`,
            );
          }
          return new Error(base);
        }),
      ),
    );
  }

  private syncProductExtensions(
    requestId: number,
    saved: ReceivingProductRow[],
    items: ExcelImportData[],
  ): Observable<ReceivingProductRow[]> {
    const ops = saved.map((product, index) => {
      const item = items[index];
      if (!item) {
        return of(undefined);
      }
      return this.updateProductOnTest(Number(product.id), requestId, item);
    });
    if (!ops.length) {
      return of(saved);
    }
    return forkJoin(ops).pipe(
      switchMap(() => this.getProductsByRequestId(requestId).pipe(take(1))),
    );
  }

  private createProductOnTest(
    requestId: number,
    item: ExcelImportData,
  ): Observable<ReceivingProductRow> {
    const mutation = `
      mutation CreateProduct($input: CreateProductInput!) {
        createProduct(input: $input) {
          id
        }
      }
    `;
    const input = {
      ...this.mapExcelToCreateInput(item),
      requestCreateTemId: requestId,
    };
    return this.postGraphql<{ createProduct: { id: number } }>(mutation, {
      input,
    }).pipe(
      switchMap((data) => {
        const id = Number(data.createProduct.id);
        return this.updateProductOnTest(id, requestId, item).pipe(
          switchMap(() =>
            this.getProductsByRequestId(requestId).pipe(
              take(1),
              map((rows) => rows.find((r) => Number(r.id) === id)!),
            ),
          ),
        );
      }),
    );
  }

  private updateProductOnTest(
    productId: number,
    requestId: number,
    item: ExcelImportData,
  ): Observable<void> {
    const mutation = `
      mutation UpdateProductOfRequest($input: UpdateProductInput!) {
        updateProductOfRequest(input: $input) {
          success
          message
        }
      }
    `;
    const input = this.buildUpdateProductInput(productId, requestId, item);
    return this.postGraphql<{
      updateProductOfRequest: UpdateResponse;
    }>(mutation, { input }).pipe(
      map((data) => {
        const body = data.updateProductOfRequest;
        if (!body?.success) {
          throw new Error(
            body?.message ?? `Cập nhật sản phẩm #${productId} thất bại`,
          );
        }
      }),
    );
  }

  private deleteProductOnTest(
    productId: number,
  ): Observable<UpdateResponse | null> {
    const mutation = `
      mutation DeleteProduct($productId: Int!) {
        deleteProduct(productId: $productId) {
          success
          message
        }
      }
    `;
    return this.postGraphql<{ deleteProduct: UpdateResponse }>(mutation, {
      productId,
    }).pipe(
      map((data) => data.deleteProduct),
      catchError(() => of(null)),
    );
  }

  private buildUpdateProductInput(
    productId: number,
    requestId: number,
    item: ExcelImportData,
  ): Record<string, unknown> {
    return {
      id: productId,
      requestCreateTemId: requestId,
      sapCode: item.sapCode.trim(),
      productName: item.tenSP.trim(),
      partNumber: item.partNumber.trim(),
      lot: item.lot.trim(),
      temQuantity: item.temQuantity,
      initialQuantity: item.initialQuantity,
      vendor: item.vendor.trim(),
      vendorName: item.tenNCC.trim(),
      userData1: item.userData1 ?? "",
      userData2: item.userData2 ?? "",
      userData3: item.userData3 ?? "",
      userData4: item.userData4 ?? "",
      userData5: item.userData5 ?? "",
      storageUnit: item.storageUnit ?? "",
      expirationDate: this.formatDateForProductUpdate(item.expirationDate),
      manufacturingDate: this.formatDateForProductUpdate(
        item.manufacturingDate,
      ),
      arrivalDate: this.formatDateForProductUpdate(item.arrivalDate),
      numberOfPrints: 0,
      WhsCode: item.whsCode?.trim() ?? "",
      sapSendStatus: item.sapSendStatus ?? false,
    };
  }

  private mapExcelToCreateInput(
    item: ExcelImportData,
  ): Record<string, unknown> {
    return {
      sapCode: item.sapCode.trim(),
      productName: item.tenSP?.trim() ?? "",
      temQuantity: item.temQuantity,
      partNumber: item.partNumber.trim(),
      lot: item.lot.trim(),
      initialQuantity: item.initialQuantity,
      vendor: item.vendor.trim(),
      vendorName: item.tenNCC.trim(),
      userData1: item.userData1 ?? "",
      userData2: item.userData2 ?? "",
      userData3: item.userData3 ?? "",
      userData4: item.userData4 ?? "",
      userData5: item.userData5 ?? "",
      storageUnit: item.storageUnit ?? "",
      expirationDate: this.formatDateFromExcel(item.expirationDate),
      manufacturingDate: this.formatDateFromExcel(item.manufacturingDate),
      arrivalDate: this.formatDateFromExcel(item.arrivalDate),
    };
  }

  private formatDateForProductUpdate(dateStr: string): string {
    const iso = this.formatDateFromExcel(dateStr);
    if (!iso) {
      return "";
    }
    return iso.includes("T") ? iso : `${iso}T00:00:00`;
  }

  private formatDateFromExcel(dateStr: string): string {
    if (!dateStr?.trim()) {
      return "";
    }
    const cleaned = dateStr.trim();
    if (/^\d{8}$/.test(cleaned)) {
      const day = cleaned.substring(0, 2);
      const month = cleaned.substring(2, 4);
      const year = cleaned.substring(4, 8);
      return `${year}-${month}-${day}`;
    }
    if (/^\d{4}-\d{2}-\d{2}$/.test(cleaned)) {
      return cleaned;
    }
    if (/^\d{2}\/\d{2}\/\d{4}$/.test(cleaned)) {
      const parts = cleaned.split("/");
      return `${parts[2]}-${parts[1]}-${parts[0]}`;
    }
    return cleaned;
  }

  private postGraphql<T>(
    query: string,
    variables?: Record<string, unknown>,
  ): Observable<T> {
    return this.http
      .post<GraphQlResponse<T>>(this.graphqlUrl, { query, variables })
      .pipe(
        map((res) => {
          if (res.errors?.length) {
            throw new Error(
              res.errors.map((e) => e.message ?? "GraphQL error").join("; "),
            );
          }
          if (!res.data) {
            throw new Error("GraphQL không trả dữ liệu.");
          }
          return res.data;
        }),
      );
  }
}
