import { Injectable } from "@angular/core";
import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { EMPTY, from, Observable, of, throwError } from "rxjs";
import { catchError, map, mergeMap } from "rxjs/operators";
import { environment } from "app/environments/environment.development";
import { PalletMngtService } from "app/entities/pallet-management/list/pallet-mngt.service";

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

  /** Không tồn tại (404 / rỗng) → null */
  getPalletDetail(serial: string): Observable<PalletMngtDetail | null> {
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
          this.getPalletDetail(serial).pipe(
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
  updateBox(box: PalletBoxRecord): Observable<boolean> {
    const body: Record<string, unknown> = { ...box };
    delete body["palletBoxMapping"];
    return this.http
      .put<unknown>(`${this.url}/vendor-label-infos/${box.id}`, body)
      .pipe(map((): boolean => true));
  }
}
