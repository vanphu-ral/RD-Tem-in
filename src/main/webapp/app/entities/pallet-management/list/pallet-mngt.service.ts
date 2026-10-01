import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
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
    const nestedKeys = [
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
