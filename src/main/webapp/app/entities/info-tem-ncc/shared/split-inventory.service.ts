import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { environment } from "app/environments/environment";

/**
 * Lịch sử tách tem — POST /split-inventories (cùng BE với phiên xuất chuyển kho).
 * Test trên BE 192.168.10.99 thì đổi tạm sang `http://192.168.10.99:8085/api/split-inventories`.
 */
const SPLIT_INVENTORY_URL = `${environment.baseInTemApiUrl}/split-inventories`;

/** Nghiệp vụ phát sinh tách tem (businessType) */
export const SPLIT_BUSINESS_TYPE = {
  /** Xuất chuyển kho — businessSourceId = mã lệnh chuyển kho (owtqDocEntry) */
  transfer: "Chuyển kho",
  /** Nhập tem NCC — businessSourceId = mã đơn nhập kho */
  vendorLabel: "Nhập tem NCC",
} as const;

export type SplitBusinessType =
  (typeof SPLIT_BUSINESS_TYPE)[keyof typeof SPLIT_BUSINESS_TYPE];

/** Payload POST /split-inventories */
export interface SplitInventoryPayload {
  businessSourceId: number | null;
  businessType: SplitBusinessType;
  reelId: string;
  parentMaterialIdentifier: string;
  partNumber: string | null;
  vendor: string | null;
  lot: string | null;
  userData1: string | null;
  userData2: string | null;
  userData3: string | null;
  userData4: string | null;
  userData5: string | null;
  initialQuantity: number;
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
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
  printStatus: boolean;
}

/** Các trường văn bản lấy nguyên tên từ bản ghi nguồn (tồn kho PanaCIM / vendor-label-info) */
const TEXT_FIELDS = [
  "partNumber",
  "vendor",
  "lot",
  "userData1",
  "userData2",
  "userData3",
  "userData4",
  "userData5",
  "msdLevel",
  "msdInitialFloorTime",
  "msdBagSealDate",
  "marketUsage",
  "shelfTime",
  "spMaterialName",
  "warningLimit",
  "maximumLimit",
  "warmupTime",
  "storageUnit",
  "subStorageUnit",
  "expirationDate",
  "manufacturingDate",
  "partClass",
  "sapCode",
  "vendorQrCode",
  "status",
] as const;

type SplitTextField = (typeof TEXT_FIELDS)[number];

/**
 * Dựng payload từ các bản ghi nguồn (ưu tiên bản ghi đầu: tem tách, sau đó tem gốc) +
 * thông tin nghiệp vụ; overrides ghi đè sau cùng.
 */
export function buildSplitInventoryPayload(params: {
  sources: ReadonlyArray<Record<string, unknown> | null | undefined>;
  businessSourceId: number | null;
  businessType: SplitBusinessType;
  reelId: string;
  parentReelId: string;
  quantity: number;
  user: string;
  overrides?: Partial<Pick<SplitInventoryPayload, SplitTextField>> &
    Partial<
      Pick<
        SplitInventoryPayload,
        "locationOverride" | "comments" | "printStatus" | "quantityOverride"
      >
    >;
}): SplitInventoryPayload {
  const pick = (key: string): string | null => {
    for (const src of params.sources) {
      const v = src?.[key];
      if (v !== null && v !== undefined && String(v).trim() !== "") {
        return String(v).trim();
      }
    }
    return null;
  };
  const text = Object.fromEntries(
    TEXT_FIELDS.map((key) => [key, pick(key)]),
  ) as Record<SplitTextField, string | null>;
  const now = new Date().toISOString();
  return {
    ...text,
    businessSourceId: params.businessSourceId,
    businessType: params.businessType,
    reelId: params.reelId,
    parentMaterialIdentifier: params.parentReelId,
    initialQuantity: params.quantity,
    quantityOverride: null,
    locationOverride: null,
    comments: `Tách từ ${params.parentReelId}`,
    createdBy: params.user,
    createdAt: now,
    updatedBy: params.user,
    updatedAt: now,
    printStatus: false,
    ...params.overrides,
  };
}

/** 1 bản ghi tem tách (GET /split-inventories) */
export interface SplitInventoryRecord
  extends Omit<SplitInventoryPayload, "businessType"> {
  id: number;
  businessType: string | null;
}

/** Lọc danh sách tem tách — JHipster criteria phía BE */
export interface SplitInventoryQuery {
  page: number;
  size: number;
  /** null = tất cả */
  printStatus?: boolean | null;
  businessType?: string | null;
  /** Tìm chứa (contains) theo 1 trường */
  searchField?: "reelId" | "parentMaterialIdentifier" | "sapCode" | "lot";
  search?: string;
  /** Mã lệnh chuyển kho / mã đơn nhập (equals) */
  businessSourceId?: number | null;
}

@Injectable({ providedIn: "root" })
export class SplitInventoryService {
  constructor(private http: HttpClient) {}

  /** Lưu lịch sử 1 lần tách tem (gọi sau khi tách thành công) */
  save(payload: SplitInventoryPayload): Observable<unknown> {
    return this.http.post(SPLIT_INVENTORY_URL, payload);
  }

  /** Cập nhật trạng thái in — PATCH /split-inventories/{id} (chỉ gửi các trường thay đổi) */
  updatePrintStatus(
    id: number,
    printStatus: boolean,
    user: string,
  ): Observable<unknown> {
    return this.http.patch(
      `${SPLIT_INVENTORY_URL}/${id}`,
      {
        id,
        printStatus,
        updatedBy: user,
        updatedAt: new Date().toISOString(),
      },
      { headers: { "Content-Type": "application/merge-patch+json" } },
    );
  }

  /** Danh sách tem tách (mới nhất trước) + tổng số bản ghi (X-Total-Count) */
  query(
    q: SplitInventoryQuery,
  ): Observable<{ items: SplitInventoryRecord[]; total: number }> {
    let params = new HttpParams()
      .set("page", q.page)
      .set("size", q.size)
      .set("sort", "id,desc");
    if (q.printStatus != null) {
      params = params.set("printStatus.equals", q.printStatus);
    }
    if (q.businessType) {
      params = params.set("businessType.equals", q.businessType);
    }
    const search = (q.search ?? "").trim();
    if (search && q.searchField) {
      params = params.set(`${q.searchField}.contains`, search);
    }
    if (q.businessSourceId != null) {
      params = params.set("businessSourceId.equals", q.businessSourceId);
    }
    return this.http
      .get<SplitInventoryRecord[]>(SPLIT_INVENTORY_URL, {
        params,
        observe: "response",
      })
      .pipe(
        map((res) => {
          const items = res.body ?? [];
          const total = Number(res.headers.get("X-Total-Count"));
          return {
            items,
            total: Number.isFinite(total) && total > 0 ? total : items.length,
          };
        }),
      );
  }
}
