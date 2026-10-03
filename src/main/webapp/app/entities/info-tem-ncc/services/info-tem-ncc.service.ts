import { Injectable } from "@angular/core";
import {
  HttpClient,
  HttpErrorResponse,
  HttpParams,
} from "@angular/common/http";
import { Observable, of, throwError } from "rxjs";
import { catchError, map, shareReplay } from "rxjs/operators";
import { environment } from "app/environments/environment.development";

/** Chuẩn hóa giá trị API (string/number/boolean/null) về string đã trim */
export function toText(value: unknown): string {
  return value === null || value === undefined ? "" : String(value).trim();
}

export interface DeliveryNotificationDto {
  id: number;
  deliveryNotificationCode: string | null;
  invoiceNumber: string | null;
  contractCode: string | null;
  /** Mã NCC — chỉ có khi backend đã thêm cột vendor_code */
  vendorCode?: string | null;
  vendorName: string | null;
  contNo: string | null;
  entryDate: string | null;
  numberOfPo: number | null;
  /** Số vật tư (dòng sapPor1R1) trong đơn */
  numberOfItem?: number | null;
  status: string | null;
  /** Nguồn tạo đơn: "system", "appSmart", ... */
  source?: string | null;
  deletedAt: string | null;
  deletedBy: string | null;
  createdBy: string | null;
  createdAt: string | null;
}

/** 1 dòng PO lưu vào đơn — POST /sap-por-1-r-1-s/batch */
export interface SapPor1BatchItem {
  lineNum: string | null;
  baseRef: string | null;
  baseEntry: string | null;
  baseLine: string | null;
  lineStatus: string | null;
  itemCode: string | null;
  dscription: string | null;
  quantity: number;
  shipDate: string | null;
  price: string | null;
  currency: string | null;
  discPrcnt: string | null;
  totalSumSy: string | null;
  openSumSys: string | null;
  invntSttus: string | null;
  baseDocNum: string | null;
  getuTenkythuat: string | null;
  getuSo: string | null;
  getuMCode: string | null;
  docEntry: string | null;
  totalFrgn: number | null;
  vatGroup: string | null;
  uomCode: string | null;
  unitMsr: string | null;
  lineVendor: string | null;
  trgetEntry: string | null;
  lineTotal: number | null;
  vatPrcnt: number | null;
  priceAfVat: number | null;
  whsCode: string | null;
  deliveryNotificationId: number;
}

/** Payload POST /delivery-notifications */
export interface CreateDeliveryNotificationPayload {
  deliveryNotificationCode: string;
  invoiceNumber: string;
  contractCode: string;
  /** Tên NCC (cardName) — POST không gửi vendorCode (backend báo 400) */
  vendorName: string;
  contNo: string;
  entryDate: string;
  numberOfPo: number;
  /** Số vật tư (dòng sapPor1R1) trong đơn */
  numberOfItem: number;
  status: string;
  /** Nguồn tạo đơn: "system" (web này), "appSmart", ... */
  source: string;
  createdBy: string;
  createdAt: string;
}

/** Nguồn tạo của đơn tạo từ web này */
export const DELIVERY_SOURCE_SYSTEM = "system";

export interface DeliveryNotificationPage {
  items: DeliveryNotificationDto[];
  total: number;
}
/** Bản ghi thùng (vendor-label-info) backend trả về */
export interface VendorLabelInfoDto extends CreateVendorLabelInfoPayload {
  id: number;
  serialPallet?: string | null;
  palletBoxMappingId?: number | null;
  /** Có khi thùng thuộc pallet (response POST /vendor-label-infos/pallet) */
  palletBoxMapping?: PalletBoxMappingDto | null;
}

export interface PalletBoxMappingDto {
  id: number;
  serialPallet: string | null;
  reelIdBox: string | null;
  createAt: string | null;
  createBy: string | null;
  palletMngt: unknown;
}

/** Payload PUT /pallet-mngts/{id} */
export interface PalletMngtPutPayload {
  id: number;
  serialPallet: string;
  locationName: string | null;
  status: string;
  note: string | null;
  createAt: string | null;
  createBy: string | null;
  updatedAt: string;
  updatedBy: string;
}

/** Chi tiết pallet (GET /pallet-mngts/serial-pallet/{serial}) */
export interface PalletDetailDto {
  id: number;
  serialPallet: string | null;
  locationName: string | null;
  numberOfBox: number | null;
  totalQuantity: number | null;
  status: string | null;
  note: string | null;
  createAt?: string | null;
  createBy?: string | null;
  updatedAt?: string | null;
  updatedBy?: string | null;
  vendorLabelInfoList: VendorLabelInfoDto[] | null;
}

export interface SapPor1R1Dto {
  id: number;
  lineNum: string | null;
  itemCode: string | null;
  dscription: string | null;
  quantity: number | null;
  whsCode: string | null;
  unitMsr: string | null;
  price: string | null;
  currency: string | null;
  /** Mã PO (DocEntry SAP) */
  docEntry: string | null;
  vendorLabelInfoList: VendorLabelInfoDto[] | null;
}

/** Payload POST /vendor-label-infos — 1 bản ghi = 1 thùng scan được */
export interface CreateVendorLabelInfoPayload {
  reelId: string | null;
  partNumber: string | null;
  vendor: string | null;
  lot: string | null;
  userData1: string | null;
  userData2: string | null;
  userData3: string | null;
  userData4: string | null;
  userData5: string | null;
  initialQuantity: number | null;
  msdLevel: string | null;
  msdInitialFloorTime: string | null;
  msdBagSealDate: string | null;
  marketUsage: string | null;
  quantityOverride: number | null;
  shelfTime: string | null;
  spMaterialName: string | null;
  warningLimit: string | null;
  maximumLimit: string | null;
  comments: string | null;
  warmupTime: string | null;
  storageUnit: string | null;
  subStorageUnit: string | null;
  locationOverride: string | null;
  expirationDate: string | null;
  manufacturingDate: string | null;
  partClass: string | null;
  sapCode: string | null;
  vendorQrCode: string | null;
  status: string | null;
  createdBy: string | null;
  createdAt: string | null;
  updatedBy: string | null;
  updatedAt: string | null;
  vendorAdditionalData: string | null;
  panaSendStatus: boolean | null;
  sapSendStatus: boolean | null;
  deliveryNotificationId: number | null;
  sapPor1Id: number | null;
}

/** Payload POST /vendor-label-infos/pallet — các thùng thuộc 1 pallet */
export interface CreatePalletVendorLabelInfoPayload {
  serialPallet: string;
  vendorLabelInfoList: CreateVendorLabelInfoPayload[];
}

/** Response POST /vendor-label-infos/pallet */
export interface PalletVendorLabelInfoResponse {
  serialPallet: string | null;
  vendorLabelInfoList: VendorLabelInfoDto[] | null;
}

export interface DeliveryNotificationDetailDto extends DeliveryNotificationDto {
  sapPor1R1List: SapPor1R1Dto[] | null;
}
@Injectable({
  providedIn: "root",
})
export class InfoTemNccService {
  // private readonly url = `${environment.testApiUrl}`;
  private readonly url = `${environment.baseInTemApiUrl}`;
  private readonly urlTI = `${environment.baseInTemApiUrl}`;
  /** Cache part number theo mã SAP — mỗi mã chỉ gọi API 1 lần */
  private readonly partNumbersCache = new Map<string, Observable<string[]>>();

  constructor(private http: HttpClient) {}

  getDeliveryNotifications(
    page: number,
    size: number,
  ): Observable<DeliveryNotificationPage> {
    // Mặc định đơn mới nhất lên đầu (sort theo createdAt giảm dần, id phụ)
    const params = new HttpParams()
      .set("page", page)
      .set("size", size)
      .append("sort", "createdAt,desc")
      .append("sort", "id,desc");
    return this.http
      .get<DeliveryNotificationDto[]>(`${this.url}/delivery-notifications`, {
        params,
        observe: "response",
      })
      .pipe(
        map((res) => {
          const items = Array.isArray(res.body) ? res.body : [];
          const header = Number(res.headers.get("X-Total-Count"));
          return {
            items,
            total:
              Number.isFinite(header) && header > 0 ? header : items.length,
          };
        }),
      );
  }

  /** POST /sap-por-1-r-1-s/batch — lưu các dòng vật tư PO vào đơn */
  createSapPor1Batch(items: SapPor1BatchItem[]): Observable<boolean> {
    return this.http
      .post<unknown>(`${this.url}/sap-por-1-r-1-s/batch`, items)
      .pipe(map((): boolean => true));
  }

  /** PUT /delivery-notifications/{id} — cập nhật đơn (kể cả xóa mềm: deletedAt / deletedBy) */
  updateDeliveryNotification(
    payload: DeliveryNotificationDto,
  ): Observable<DeliveryNotificationDto> {
    return this.http.put<DeliveryNotificationDto>(
      `${this.url}/delivery-notifications/${payload.id}`,
      payload,
    );
  }

  /** POST /delivery-notifications — tạo mới đơn (thông báo giao hàng) */
  createDeliveryNotification(
    payload: CreateDeliveryNotificationPayload,
  ): Observable<DeliveryNotificationDto> {
    return this.http.post<DeliveryNotificationDto>(
      `${this.url}/delivery-notifications`,
      payload,
    );
  }

  getDeliveryNotificationDetail(
    id: number,
  ): Observable<DeliveryNotificationDetailDto> {
    return this.http.get<DeliveryNotificationDetailDto>(
      `${this.url}/delivery-notifications/${id}/detail`,
    );
  }

  createVendorLabelInfo(
    payload: CreateVendorLabelInfoPayload,
  ): Observable<VendorLabelInfoDto> {
    return this.http.post<VendorLabelInfoDto>(
      `${this.url}/vendor-label-infos`,
      payload,
    );
  }

  /** Các thùng đã scan của đơn */
  getVendorLabelInfosByDelivery(
    deliveryId: number,
  ): Observable<VendorLabelInfoDto[]> {
    return this.http
      .get<
        VendorLabelInfoDto[] | null
      >(`${this.url}/delivery-notifications/${deliveryId}/vendor-label-infos`)
      .pipe(map((res) => (Array.isArray(res) ? res : [])));
  }

  /** Cập nhật 1 thùng (vd: gán vị trí vào subStorageUnit) */
  updateVendorLabelInfo(
    payload: VendorLabelInfoDto,
  ): Observable<VendorLabelInfoDto> {
    return this.http.put<VendorLabelInfoDto>(
      `${this.url}/vendor-label-infos/${payload.id}`,
      payload,
    );
  }

  /**
   * Chi tiết pallet theo mã (kèm các thùng). Không tồn tại (404 / body rỗng) → null;
   * lỗi khác ném ra để nơi gọi báo lỗi.
   */
  getPalletBySerial(serialPallet: string): Observable<PalletDetailDto | null> {
    const serial = toText(serialPallet);
    return this.http
      .get<PalletDetailDto | null>(
        `${this.url}/pallet-mngts/serial-pallet/${encodeURIComponent(serial)}`,
      )
      .pipe(
        map((res) => (res && res.id ? res : null)),
        catchError((err: unknown) =>
          err instanceof HttpErrorResponse && err.status === 404
            ? of(null)
            : throwError(() => err),
        ),
      );
  }

  /** PUT /pallet-mngts/{id} — cập nhật pallet (trạng thái UNUSED / IN_USE) */
  updatePalletMngt(payload: PalletMngtPutPayload): Observable<boolean> {
    return this.http
      .put<unknown>(`${this.url}/pallet-mngts/${payload.id}`, payload)
      .pipe(map((): boolean => true));
  }

  /** Gỡ 1 thùng khỏi pallet — id = palletBoxMapping.id của thùng */
  deletePalletBoxMapping(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/pallet-box-mappings/${id}`);
  }

  deleteVendorLabelInfo(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/vendor-label-infos/${id}`);
  }

  /** Lưu các thùng thuộc pallet; trả về các thùng đã lưu (có id + palletBoxMapping) */
  createPalletVendorLabelInfos(
    payload: CreatePalletVendorLabelInfoPayload,
  ): Observable<VendorLabelInfoDto[]> {
    return this.http
      .post<PalletVendorLabelInfoResponse>(
        `${this.url}/vendor-label-infos/pallet`,
        payload,
      )
      .pipe(map((res) => res?.vendorLabelInfoList ?? []));
  }

  getPartNumbersBySapCode(sapCode: string): Observable<string[]> {
    const code = (sapCode ?? "").trim();
    if (!code) {
      return of([]);
    }
    let cached = this.partNumbersCache.get(code);
    if (!cached) {
      cached = this.http
        .get<
          string[]
        >(`${this.urlTI}/sap-oitms/itemCode/${encodeURIComponent(code)}/part-numbers`)
        .pipe(
          map((parts) =>
            (parts ?? []).map((p) => String(p).trim()).filter(Boolean),
          ),
          catchError(() => {
            // Lỗi thì bỏ khỏi cache để lần sau thử gọi lại
            this.partNumbersCache.delete(code);
            return of<string[]>([]);
          }),
          shareReplay(1),
        );
      this.partNumbersCache.set(code, cached);
    }
    return cached;
  }
}
