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

  async searchByName(keyword: string): Promise<CachedWarehouse[]> {
    const lowerKeyword = keyword.toLowerCase();
    const allWarehouses = await db.warehouses.toArray();

    return allWarehouses
      .filter((w) => w.locationFullName.toLowerCase().includes(lowerKeyword))
      .slice(0, 50);
  }

  updateOne(
    locationId: number,
    data: Partial<CachedWarehouse>,
  ): Promise<number> {
    return db.warehouses.update(locationId, data);
  }
}
