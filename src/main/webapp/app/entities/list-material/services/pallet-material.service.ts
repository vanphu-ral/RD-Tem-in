import { Injectable } from "@angular/core";
import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { EMPTY, forkJoin, from, Observable, of, throwError } from "rxjs";
import { catchError, map, mergeMap, switchMap } from "rxjs/operators";
import { environment } from "app/environments/environment.development";
import { PalletMngtService } from "app/entities/pallet-management/list/pallet-mngt.service";
import {
  inventory_update_requests_detail,
  ListMaterialService,
  RawGraphQLMaterial,
} from "./list-material.service";
import { WarehouseCacheService } from "./warehouse-cache.service";

/** 1 thùng cần đổi vị trí (Quản lý pallet desktop / mobile) */
export interface PalletBoxLocationChange {
  reelId: string;
  /** Bản ghi thùng (vendor-label-info) trong chi tiết pallet — dùng khi thùng chưa có trong kho */
  record?: PalletBoxRecord | null;
  /** Vị trí mới (tên trong danh sách vị trí) */
  location: string;
  /** Tồn kho đã có sẵn (không cần GET /api/inventory lại) */
  inventory?: RawGraphQLMaterial;
}

/** Pallet trong danh sách quản lý (GET /pallet-mngts) */
export interface PalletMngtRow {
  id: number;
  serialPallet: string;
  locationName: string | null;
  numberOfBox: number | null;
  totalQuantity: number | null;
  status: string | null;
  note: string | null;
  createAt: string | null;
  createBy: string | null;
  updatedAt: string | null;
  updatedBy: string | null;
}

/** Liên kết thùng – pallet */
export interface PalletBoxMappingRow {
  id: number;
  serialPallet: string | null;
  reelIdBox: string | null;
}

/** Thùng (vendor-label-info) trong pallet */
export interface PalletBoxRecord {
  id: number;
  reelId: string | null;
  partNumber: string | null;
  lot: string | null;
  initialQuantity: number | null;
  storageUnit: string | null;
  subStorageUnit: string | null;
  status: string | null;
  sapCode: string | null;
  palletBoxMappingId: number | null;
  palletBoxMapping?: PalletBoxMappingRow | null;
  [key: string]: unknown;
}

/** Chi tiết pallet (GET /pallet-mngts/serial-pallet/{serial}) */
export interface PalletMngtDetail extends PalletMngtRow {
  vendorLabelInfoList: PalletBoxRecord[] | null;
}

/** Số thùng + tổng SL của 1 pallet */
export interface PalletSummary {
  serial: string;
  numberOfBox: number;
  totalQuantity: number;
}

/** Tính số thùng + tổng SL từ chi tiết pallet (API danh sách chưa trả 2 số này) */
export function summarizePallet(
  serial: string,
  detail: PalletMngtDetail | null,
): PalletSummary {
  const boxes = detail?.vendorLabelInfoList ?? [];
  return {
    serial,
    numberOfBox: boxes.length,
    totalQuantity: boxes.reduce(
      (s, b) => s + Number(b.initialQuantity ?? 0),
      0,
    ),
  };
}

/** Thời điểm tạo của pallet (createAt) → ms; không có / sai định dạng → 0 */
function createdTime(row: PalletMngtRow | null): number {
  const t = new Date(String(row?.createAt ?? "")).getTime();
  return Number.isNaN(t) ? 0 : t;
}

/** Sắp xếp: pallet tạo mới nhất lên đầu (cùng thời điểm → id lớn hơn trước) */
export function sortPalletsNewestFirst<T>(
  items: T[],
  rowOf: (item: T) => PalletMngtRow | null,
): T[] {
  return [...items].sort((a, b) => {
    const ra = rowOf(a);
    const rb = rowOf(b);
    return (
      createdTime(rb) - createdTime(ra) ||
      Number(rb?.id ?? 0) - Number(ra?.id ?? 0)
    );
  });
}

/** Kết quả tạo pallet: created = false nếu mã đã có (trả về mã đang có) */
export interface CreatePalletResult {
  created: boolean;
  serial: string;
}

/** Payload PUT /pallet-mngts/{id} */
export interface PalletMngtPut {
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

@Injectable({ providedIn: "root" })
export class PalletMaterialService {
  // private readonly url = `${environment.testApiUrl}`;
  private readonly url = `${environment.baseInTemApiUrl}`;

  constructor(
    private http: HttpClient,
    private palletMngtService: PalletMngtService,
    private materialService: ListMaterialService,
    private warehouseCache: WarehouseCacheService,
  ) {}

  /**
   * Tạo pallet mới với mã quét được (POST /pallet-mngts — như Quản lý pallet):
   * tải lại danh sách để kiểm tra trùng mã (không phân biệt hoa thường);
   * trùng → không tạo, trả về mã đang có; không trùng → tạo trạng thái UNUSED.
   */
  createPalletIfAbsent(
    code: string,
    user: string,
  ): Observable<CreatePalletResult> {
    const serial = code.trim();
    return this.getPallets().pipe(
      mergeMap((rows): Observable<CreatePalletResult> => {
        const dup = rows.find(
          (p) => (p.serialPallet ?? "").toLowerCase() === serial.toLowerCase(),
        );
        if (dup) {
          return of({ created: false, serial: dup.serialPallet });
        }
        const now = new Date().toISOString();
        return this.palletMngtService
          .createPallet({
            serialPallet: serial,
            status: "UNUSED",
            note: "",
            createAt: now,
            createBy: user,
            updatedAt: now,
            updatedBy: user,
          })
          .pipe(map((): CreatePalletResult => ({ created: true, serial })));
      }),
    );
  }

  getPallets(): Observable<PalletMngtRow[]> {
    return this.http
      .get<PalletMngtRow[]>(`${this.url}/pallet-mngts`)
      .pipe(map((rows) => (Array.isArray(rows) ? rows : [])));
  }

  /**
   * Chi tiết pallet để thao tác (gỡ / xuất / đổi vị trí). Không tồn tại (404 / rỗng) → null.
   * Chuẩn hoá thùng vì API chi tiết trả thiếu:
   *  - thùng có tem NCC nhưng palletBoxMappingId = null → tra GET /pallet-box-mappings
   *    theo serialPallet + ReelID để có id gỡ (DELETE /pallet-box-mappings/{id});
   *  - thùng chỉ có liên kết (không có tem NCC: id / reelId = null) → reelId lấy từ
   *    palletBoxMapping.reelIdBox, id = -mappingId (id tạm, chỉ để phân biệt các dòng).
   */
  getPalletDetail(serial: string): Observable<PalletMngtDetail | null> {
    return this.fetchPalletDetail(serial).pipe(
      switchMap((detail) => (detail ? this.withBoxMappings(detail) : of(null))),
    );
  }

  /**
   * Số thùng + tổng SL cho các pallet (gọi chi tiết từng pallet, tối đa `concurrency`
   * request song song). Pallet lỗi khi tải bị bỏ qua. Emit lần lượt khi có kết quả.
   */
  loadPalletSummaries(
    serials: string[],
    concurrency = 4,
  ): Observable<PalletSummary> {
    return from(serials).pipe(
      mergeMap(
        (serial) =>
          this.fetchPalletDetail(serial).pipe(
            map((detail) => summarizePallet(serial, detail)),
            catchError(() => EMPTY),
          ),
        concurrency,
      ),
    );
  }

  updatePallet(payload: PalletMngtPut): Observable<boolean> {
    return this.http
      .put<unknown>(`${this.url}/pallet-mngts/${payload.id}`, payload)
      .pipe(map((): boolean => true));
  }

  /**
   * Toàn bộ liên kết thùng–pallet (GET /pallet-box-mappings — backend chưa có lọc theo ReelID).
   * Dùng để biết thùng vừa quét đang nằm ở pallet nào.
   */
  getBoxMappings(): Observable<PalletBoxMappingRow[]> {
    return this.http
      .get<PalletBoxMappingRow[]>(`${this.url}/pallet-box-mappings`)
      .pipe(map((rows) => (Array.isArray(rows) ? rows : [])));
  }

  /** Gỡ thùng khỏi pallet — id = palletBoxMapping.id */
  removeBoxFromPallet(mappingId: number): Observable<boolean> {
    return this.http
      .delete<unknown>(`${this.url}/pallet-box-mappings/${mappingId}`)
      .pipe(map((): boolean => true));
  }

  /** Thêm thùng (theo ReelID) vào pallet */
  addBoxToPallet(
    serialPallet: string,
    reelId: string,
    user: string,
  ): Observable<boolean> {
    return this.http
      .post<unknown>(`${this.url}/pallet-box-mappings`, {
        serialPallet,
        reelIdBox: reelId,
        createAt: new Date().toISOString(),
        createBy: user,
      })
      .pipe(map((): boolean => true));
  }

  /** PUT /vendor-label-infos/{id} — gửi lại nguyên bản ghi thùng */
  /**
   * Đổi vị trí các thùng, từng thùng:
   *  1. GET /api/inventory/{ReelID} có → cập nhật tồn kho như dialog cập nhật vật tư
   *     (POST api/request, MOVE, tự phê duyệt).
   *  2. Không có trong kho (404) → PUT /vendor-label-infos/{id} (bản ghi thùng của chi tiết pallet,
   *     storageUnit = vị trí mới, subStorageUnit trống).
   *  3. Vẫn lỗi / vị trí không có trong danh sách → ReelID vào failedReels.
   */
  updateBoxLocations(
    boxes: PalletBoxLocationChange[],
    currentUser: string,
  ): Observable<{ ok: boolean; failedReels: string[] }> {
    if (!boxes.length) {
      return of({ ok: true, failedReels: [] });
    }
    const failedReels: string[] = [];
    const fail = (b: PalletBoxLocationChange): void => {
      failedReels.push(b.reelId || String(b.record?.id ?? "?"));
    };
    type InvLookup = { inv: RawGraphQLMaterial | undefined; notFound: boolean };
    const inventories$: Observable<InvLookup[]> = forkJoin(
      boxes.map(
        (b): Observable<InvLookup> =>
          b.inventory
            ? of({ inv: b.inventory, notFound: false })
            : this.materialService.fetchMaterialById(b.reelId).pipe(
                // fetchMaterialById: 404 → undefined, lỗi khác → throw
                map((inv): InvLookup => ({ inv, notFound: !inv })),
                catchError(() => of({ inv: undefined, notFound: false })),
              ),
      ),
    );
    const names = [...new Set(boxes.map((b) => b.location))];
    const locationIds$ = from(
      Promise.all(names.map((n) => this.findLocationId(n))),
    );
    return forkJoin([inventories$, locationIds$]).pipe(
      switchMap(([lookups, ids]) => {
        const idByName = new Map<string, number>();
        names.forEach((n, i) => {
          const id = ids[i];
          if (id) {
            idByName.set(n, id);
          }
        });
        const detail: inventory_update_requests_detail[] = [];
        const inInventory: PalletBoxLocationChange[] = [];
        const labelTasks: Array<Observable<boolean>> = [];
        boxes.forEach((b, i) => {
          const { inv, notFound } = lookups[i];
          if (inv) {
            const locationId = idByName.get(b.location);
            if (!locationId) {
              fail(b);
              return;
            }
            const qty = String(inv.quantity ?? "");
            inInventory.push(b);
            detail.push({
              id: inv.inventoryId != null ? Number(inv.inventoryId) : null,
              materialId: String(inv.materialIdentifier ?? b.reelId),
              updatedBy: currentUser,
              createdTime: "",
              updatedTime: "",
              productCode: String(inv.partNumber ?? ""),
              productName: String(inv.partNumber ?? ""),
              quantity: qty,
              quantityChange: qty,
              type: "MOVE",
              locationId: String(locationId),
              locationName: b.location,
              expiredTime: String(inv.expirationDate ?? ""),
              status: "APPROVE",
              requestId: null,
            });
            return;
          }
          // Chưa có trong kho (404) → cập nhật bản ghi thùng (tem NCC)
          if (notFound && b.record && Number(b.record.id) > 0) {
            labelTasks.push(
              this.updateBox({
                ...b.record,
                // storageUnit = vị trí kho, subStorageUnit trống (cùng quy ước Nhập tem NCC)
                storageUnit: b.location,
                subStorageUnit: null,
              }).pipe(
                catchError(() => of(false)),
                map((ok: boolean): boolean => {
                  if (!ok) {
                    fail(b);
                  }
                  return ok;
                }),
              ),
            );
            return;
          }
          fail(b);
        });
        const inventory$: Observable<boolean> = detail.length
          ? this.materialService
              .postAutoApprovedUpdate(detail, currentUser)
              .pipe(
                map((): boolean => true),
                catchError(() => {
                  inInventory.forEach(fail);
                  return of(false);
                }),
              )
          : of(true);
        return forkJoin([inventory$, ...labelTasks]);
      }),
      map(() => ({ ok: failedReels.length === 0, failedReels })),
      catchError(() => {
        boxes.forEach(fail);
        return of({ ok: false, failedReels });
      }),
    );
  }

  updateBox(box: PalletBoxRecord): Observable<boolean> {
    // Thùng chỉ có liên kết pallet (id tạm ≤ 0, không có tem NCC) → không có bản ghi để PUT
    if (!(Number(box.id) > 0)) {
      return of(true);
    }
    const body: Record<string, unknown> = { ...box };
    delete body["palletBoxMapping"];
    return this.http
      .put<unknown>(`${this.url}/vendor-label-infos/${box.id}`, body)
      .pipe(map((): boolean => true));
  }

  /** GET /pallet-mngts/serial-pallet/{serial} — dữ liệu gốc (404 / rỗng → null) */
  private fetchPalletDetail(
    serial: string,
  ): Observable<PalletMngtDetail | null> {
    return this.http
      .get<PalletMngtDetail | null>(
        `${this.url}/pallet-mngts/serial-pallet/${encodeURIComponent(serial.trim())}`,
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

  /** Điền ReelID / id liên kết còn thiếu của các thùng trong chi tiết pallet */
  private withBoxMappings(
    detail: PalletMngtDetail,
  ): Observable<PalletMngtDetail> {
    const mappingIdOf = (b: PalletBoxRecord): number =>
      Number(b.palletBoxMapping?.id ?? b.palletBoxMappingId ?? 0) || 0;
    const boxes: PalletBoxRecord[] = (detail.vendorLabelInfoList ?? []).map(
      (b) => {
        const mappingId = mappingIdOf(b);
        return {
          ...b,
          reelId: b.reelId ?? b.palletBoxMapping?.reelIdBox ?? null,
          id: b.id ?? (mappingId ? -mappingId : 0),
        };
      },
    );
    const needLookup = boxes.some((b) => !mappingIdOf(b) && !!b.reelId);
    if (!needLookup) {
      return of({ ...detail, vendorLabelInfoList: boxes });
    }
    const serial = (detail.serialPallet ?? "").trim();
    return this.getBoxMappings().pipe(
      catchError(() => of([] as PalletBoxMappingRow[])),
      map((mappings) => {
        const byReel = new Map(
          mappings
            .filter((m) => (m.serialPallet ?? "").trim() === serial)
            .map((m) => [(m.reelIdBox ?? "").trim().toLowerCase(), m.id]),
        );
        return {
          ...detail,
          vendorLabelInfoList: boxes.map((b) => {
            if (mappingIdOf(b)) {
              return b;
            }
            const id = byReel.get((b.reelId ?? "").trim().toLowerCase());
            return id ? { ...b, palletBoxMappingId: id } : b;
          }),
        };
      }),
    );
  }

  /** Vị trí (tên / tên đầy đủ) → locationId theo danh sách vị trí (IndexedDB); không có → null */
  private async findLocationId(value: string): Promise<number | null> {
    const lower = value.trim().toLowerCase();
    if (!lower) {
      return null;
    }
    await this.warehouseCache.ensureSynced();
    const list = await this.warehouseCache.searchByName(value.trim());
    const found = list.find(
      (w) =>
        String(w.locationFullName ?? "").toLowerCase() === lower ||
        String(w.locationName ?? "").toLowerCase() === lower,
    );
    return found ? Number(found.locationId) || null : null;
  }
}
