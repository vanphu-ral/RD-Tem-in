import { Injectable } from "@angular/core";
import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Observable, of, throwError } from "rxjs";
import { catchError, map } from "rxjs/operators";
import { environment } from "app/environments/environment.development";
import {
  PalletBoxItem,
  PalletItem,
  PalletMngtCreatePayload,
  PalletMngtUpdatePayload,
} from "./pallet-management.model";

@Injectable({
  providedIn: "root",
})
export class PalletMngtService {
  // private readonly url = `${environment.testApiUrl}/pallet-mngts`;
  private readonly url = `${environment.baseInTemApiUrl}/pallet-mngts`;
  private readonly mappingUrl = `${environment.baseInTemApiUrl}/pallet-box-mappings`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<PalletItem[]> {
    return this.http.get<PalletItem[]>(this.url);
  }

  createPallet(payload: PalletMngtCreatePayload): Observable<PalletItem> {
    return this.http
      .post<PalletItem | null>(this.url, payload)
      .pipe(map((row) => this.normalizeCreated(row, payload)));
  }

  /** PUT /api/pallet-mngts/{id} — cập nhật pallet (trạng thái UNUSED / IN_USE...) */
  updatePallet(payload: PalletMngtUpdatePayload): Observable<unknown> {
    return this.http.put<unknown>(`${this.url}/${payload.id}`, payload);
  }

  /** DELETE /api/pallet-mngts/{id} — xóa pallet */
  deletePallet(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  /** DELETE /api/pallet-box-mappings/{id} — gỡ 1 thùng khỏi pallet (id = palletBoxMapping.id) */
  deleteBoxMapping(mappingId: number): Observable<void> {
    return this.http.delete<void>(`${this.mappingUrl}/${mappingId}`);
  }

  /**
   * id liên kết thùng–pallet (palletBoxMapping.id) của mọi thùng trong pallet —
   * từ GET /pallet-mngts/serial-pallet/{serial}. Pallet không tồn tại (404) → [].
   */
  getBoxMappingIds(serialPallet: string): Observable<number[]> {
    const serial = (serialPallet ?? "").trim();
    return this.http
      .get<unknown>(`${this.url}/serial-pallet/${encodeURIComponent(serial)}`)
      .pipe(
        map((raw): number[] => {
          const rec = this.asRecord(raw);
          const list = Array.isArray(rec?.["vendorLabelInfoList"])
            ? (rec["vendorLabelInfoList"] as unknown[])
            : [];
          const ids: number[] = [];
          for (const item of list) {
            const box = this.asRecord(item);
            const mapping = this.asRecord(box?.["palletBoxMapping"]);
            const id = Number(mapping?.["id"] ?? box?.["palletBoxMappingId"]);
            if (Number.isFinite(id) && id > 0) {
              ids.push(id);
            }
          }
          return ids;
        }),
        catchError((err: unknown) =>
          err instanceof HttpErrorResponse && err.status === 404
            ? of([])
            : throwError(() => err),
        ),
      );
  }

  getBoxesBySerialPallet(serialPallet: string): Observable<PalletBoxItem[]> {
    const serial = (serialPallet ?? "").trim();
    return this.http
      .get<unknown>(`${this.url}/serial-pallet/${encodeURIComponent(serial)}`)
      .pipe(map((raw) => this.extractBoxes(raw)));
  }

  private normalizeCreated(
    row: PalletItem | null,
    payload: PalletMngtCreatePayload,
  ): PalletItem {
    return {
      id: row?.id ?? 0,
      serialPallet: row?.serialPallet ?? payload.serialPallet,
      locationName: row?.locationName ?? null,
      numberOfBox: row?.numberOfBox ?? null,
      totalQuantity: row?.totalQuantity ?? null,
      status: row?.status ?? payload.status,
      note: row?.note ?? payload.note,
      createAt: row?.createAt ?? payload.createAt,
      createBy: row?.createBy ?? payload.createBy,
      updatedAt: row?.updatedAt ?? payload.updatedAt,
      updatedBy: row?.updatedBy ?? payload.updatedBy,
    };
  }

  private extractBoxes(raw: unknown): PalletBoxItem[] {
    if (Array.isArray(raw)) {
      return this.mapBoxList(raw);
    }
    const rec = this.asRecord(raw);
    if (!rec) {
      return [];
    }
    // GET /pallet-mngts/serial-pallet/{serial} trả thùng trong vendorLabelInfoList
    const nestedKeys = [
      "vendorLabelInfoList",
      "boxes",
      "details",
      "items",
      "data",
      "content",
      "palletBoxes",
      "serialBoxes",
    ];
    for (const key of nestedKeys) {
      const nested = rec[key];
      if (Array.isArray(nested)) {
        return this.mapBoxList(nested);
      }
    }
    if (this.hasBoxShape(rec)) {
      return this.mapBoxList([rec]);
    }
    return [];
  }

  private mapBoxList(rows: unknown[]): PalletBoxItem[] {
    const boxes: PalletBoxItem[] = [];
    for (const row of rows) {
      const rec = this.asRecord(row);
      if (!rec) {
        continue;
      }
      boxes.push({
        reelId: this.pickString(rec, [
          "reelId",
          "reelid",
          "reel_id",
          "serialBox",
          "serial_box",
        ]),
        productName: this.pickString(rec, [
          "productName",
          "product_name",
          "sapName",
          "sap_name",
          "materialName",
          "material_name",
          "spMaterialName",
        ]),
        partNumber: this.pickString(rec, [
          "partNumber",
          "part_number",
          "partNo",
          "part_no",
        ]),
        lotNumber: this.pickString(rec, [
          "lotNumber",
          "lot_number",
          "lot",
          "soLo",
          "so_lo",
        ]),
        quantity: this.pickNumber(rec, [
          "initialQuantity",
          "quantity",
          "qty",
          "totalQuantity",
          "total_quantity",
        ]),
        productionDate: this.pickString(rec, [
          "productionDate",
          "production_date",
          "ngaySanXuat",
          "ngay_san_xuat",
          "manufacturedDate",
          "manufactured_date",
          "manufacturingDate",
        ]),
        expiryDate: this.pickString(rec, [
          "expiryDate",
          "expiry_date",
          "hanSuDung",
          "han_su_dung",
          "expirationDate",
          "expiration_date",
        ]),
      });
    }
    return boxes;
  }

  private hasBoxShape(rec: Record<string, unknown>): boolean {
    return (
      this.pickString(rec, [
        "reelId",
        "reelid",
        "reel_id",
        "serialBox",
        "serial_box",
      ]).length > 0
    );
  }

  private asRecord(value: unknown): Record<string, unknown> | null {
    if (value === null || typeof value !== "object" || Array.isArray(value)) {
      return null;
    }
    const rec: Record<string, unknown> = {};
    Object.assign(rec, value);
    return rec;
  }

  private pickString(rec: Record<string, unknown>, keys: string[]): string {
    for (const key of keys) {
      const value = rec[key];
      if (value === null || value === undefined) {
        continue;
      }
      const text = String(value).trim();
      if (text !== "") {
        return text;
      }
    }
    return "";
  }

  private pickNumber(
    rec: Record<string, unknown>,
    keys: string[],
  ): number | null {
    for (const key of keys) {
      const value = rec[key];
      if (value === null || value === undefined || value === "") {
        continue;
      }
      const num = Number(value);
      if (!Number.isNaN(num)) {
        return num;
      }
    }
    return null;
  }
}
