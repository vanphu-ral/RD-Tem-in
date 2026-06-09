import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, catchError, firstValueFrom, map, of } from "rxjs";
import { ApplicationConfigService } from "app/core/config/application-config.service";
import { WarehouseCacheService } from "app/entities/list-material/services/warehouse-cache.service";

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
  por1WhsCode?: string;
}

export interface SapPoInfoResponse {
  poInfo: SapPoInfoHeader | null;
  poDetails: SapPoInfoDetail[];
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
  private itemDataUrl =
    this.applicationConfigService.getEndpointFor("/api/item-data");
  private sapOitmUrl = `http://192.168.10.99:8085/api/sap-oitms`;
  // private sapOitmUrl =
  //   this.applicationConfigService.getEndpointFor("/api/sap-oitms");
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
    return this.http
      .get<ItemDataDto>(`${this.itemDataUrl}/${encodeURIComponent(itemCode)}`)
      .pipe(catchError(() => of(null)));
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

  /** Lấy tên & part number theo mã SAP từ /api/sap-oitms/{sapCode}. */
  getSapOitmBySapCode(sapCode: string): Observable<SapOitmDto | null> {
    return this.http
      .get<SapOitmDto>(`${this.sapOitmUrl}/${encodeURIComponent(sapCode)}`)
      .pipe(catchError(() => of(null)));
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
}
