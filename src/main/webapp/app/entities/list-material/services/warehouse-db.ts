import Dexie, { Table } from "dexie";

export interface CachedWarehouse {
  locationId: number;
  locationName: string;
  locationFullName: string;
}

export class WarehouseDB extends Dexie {
  warehouses!: Table<CachedWarehouse, number>;

  constructor() {
    super("WarehouseDB");
    this.version(1).stores({
      warehouses: "locationId,locationName,locationFullName",
    });
  }
}

export const db = new WarehouseDB();
