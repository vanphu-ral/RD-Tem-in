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
    const trimmed = keyword.trim();
    if (!trimmed) {
      return [];
    }
    const lowerKeyword = trimmed.toLowerCase();

    try {
      const prefixMatches = await db.warehouses
        .where("locationFullName")
        .between(trimmed, `${trimmed}\uffff`, true, false)
        .limit(50)
        .toArray();
      if (prefixMatches.length > 0) {
        return prefixMatches;
      }
    } catch {
      // Indexed range query unavailable — fall through to full scan.
    }

    const allWarehouses = await db.warehouses.toArray();
    return allWarehouses
      .filter((w) =>
        (w.locationFullName ?? "").toLowerCase().includes(lowerKeyword),
      )
      .sort((a, b) => {
        const aFull = (a.locationFullName ?? "").toLowerCase();
        const bFull = (b.locationFullName ?? "").toLowerCase();
        const aRank = aFull.startsWith(lowerKeyword)
          ? 0
          : aFull.includes(lowerKeyword)
            ? 1
            : 2;
        const bRank = bFull.startsWith(lowerKeyword)
          ? 0
          : bFull.includes(lowerKeyword)
            ? 1
            : 2;
        return aRank - bRank || aFull.localeCompare(bFull);
      })
      .slice(0, 50);
  }

  updateOne(
    locationId: number,
    data: Partial<CachedWarehouse>,
  ): Promise<number> {
    return db.warehouses.update(locationId, data);
  }
}
