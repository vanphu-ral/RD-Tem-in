import { Injectable } from "@angular/core";
import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Observable, of, throwError } from "rxjs";
import { catchError, map } from "rxjs/operators";
import { environment } from "app/environments/environment.development";

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

  constructor(private http: HttpClient) {}

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
