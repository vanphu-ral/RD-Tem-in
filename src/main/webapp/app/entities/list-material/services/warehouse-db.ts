import Dexie, { Table } from "dexie";

/**
 * Tăng số này rồi deploy FE khi dữ liệu Location trên DB đã đổi
 * (hoặc khi đổi cấu trúc cache) — mọi máy sẽ tự clear IndexedDB và tải lại.
 */
export const WAREHOUSE_CACHE_VERSION = 2;

export interface CachedWarehouse {
  locationId: number;
  locationName: string;
  locationFullName: string;
}

export interface WarehouseCacheMeta {
  key: string;
  value: number;
}

export class WarehouseDB extends Dexie {
  warehouses!: Table<CachedWarehouse, number>;
  meta!: Table<WarehouseCacheMeta, string>;

  constructor() {
    super("WarehouseDB");
    this.version(1).stores({
      warehouses: "locationId,locationName,locationFullName",
    });
    this.version(2)
      .stores({
        warehouses: "locationId,locationName,locationFullName",
        meta: "key",
      })
      .upgrade(async (tx) => {
        await tx.table("warehouses").clear();
      });
  }
}

export const db = new WarehouseDB();
