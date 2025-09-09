import { Injectable } from "@angular/core";
import { db, CachedWarehouse } from "./warehouse-db";

@Injectable({ providedIn: "root" })
export class WarehouseCacheService {
  async saveAll(list: CachedWarehouse[]): Promise<void> {
    await db.warehouses.clear();
    await db.warehouses.bulkAdd(list);
  }

  async getAll(): Promise<CachedWarehouse[]> {
    return db.warehouses.toArray();
  }

  async searchByName(prefix: string): Promise<CachedWarehouse[]> {
    return db.warehouses
      .where("locationFullName")
      .startsWithIgnoreCase(prefix)
      .limit(50)
      .toArray();
  }

  updateOne(
    locationId: number,
    data: Partial<CachedWarehouse>,
  ): Promise<number> {
    return db.warehouses.update(locationId, data);
  }
}
