import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { firstValueFrom } from "rxjs";
import { ApplicationConfigService } from "app/core/config/application-config.service";
import { db, CachedWarehouse, WAREHOUSE_CACHE_VERSION } from "./warehouse-db";

interface LocationApiRow {
  id: number;
  locationName: string;
  locationFullName: string;
}

@Injectable({ providedIn: "root" })
export class WarehouseCacheService {
  private readonly apiLocations =
    this.applicationConfigService.getEndpointFor("/api/location");

  private syncInFlight: Promise<void> | null = null;

  constructor(
    private http: HttpClient,
    private applicationConfigService: ApplicationConfigService,
  ) {}

  /**
   * Đảm bảo IndexedDB đúng CACHE_VERSION và có dữ liệu.
   * Version lệch hoặc store trống → clear + tải full từ API.
   */
  async ensureSynced(force = false): Promise<void> {
    if (this.syncInFlight) {
      return this.syncInFlight;
    }
    this.syncInFlight = this.doEnsureSynced(force).finally(() => {
      this.syncInFlight = null;
    });
    return this.syncInFlight;
  }

  async saveAll(list: CachedWarehouse[]): Promise<void> {
    await db.warehouses.clear();
    if (list.length > 0) {
      await db.warehouses.bulkPut(list);
    }
    await db.meta.put({
      key: "cacheVersion",
      value: WAREHOUSE_CACHE_VERSION,
    });
  }

  async getAll(): Promise<CachedWarehouse[]> {
    return db.warehouses.toArray();
  }

  async count(): Promise<number> {
    return db.warehouses.count();
  }

  async getById(locationId: number): Promise<CachedWarehouse | undefined> {
    return db.warehouses.get(locationId);
  }

  async findExactByFullName(
    name: string,
  ): Promise<CachedWarehouse | undefined> {
    const trimmed = name.trim();
    if (!trimmed) {
      return undefined;
    }
    const exact = await db.warehouses
      .where("locationFullName")
      .equals(trimmed)
      .first();
    if (exact) {
      return exact;
    }
    const lower = trimmed.toLowerCase();
    return db.warehouses
      .filter((w) => (w.locationFullName ?? "").trim().toLowerCase() === lower)
      .first();
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
      // Indexed range query unavailable — fall through.
    }

    return db.warehouses
      .filter((w) =>
        (w.locationFullName ?? "").toLowerCase().includes(lowerKeyword),
      )
      .limit(50)
      .toArray();
  }

  updateOne(
    locationId: number,
    data: Partial<CachedWarehouse>,
  ): Promise<number> {
    return db.warehouses.update(locationId, data);
  }

  private async doEnsureSynced(force: boolean): Promise<void> {
    if (!force && (await this.isCacheCurrent())) {
      return;
    }

    await db.warehouses.clear();
    await db.meta.delete("cacheVersion");

    const data = await firstValueFrom(
      this.http.get<LocationApiRow[]>(this.apiLocations),
    );
    const mapped: CachedWarehouse[] = (data ?? []).map((loc) => ({
      locationId: loc.id,
      locationName: loc.locationName,
      locationFullName: loc.locationFullName,
    }));
    await this.saveAll(mapped);
  }

  private async isCacheCurrent(): Promise<boolean> {
    const [meta, count] = await Promise.all([
      db.meta.get("cacheVersion"),
      db.warehouses.count(),
    ]);
    return meta?.value === WAREHOUSE_CACHE_VERSION && count > 0;
  }
}
