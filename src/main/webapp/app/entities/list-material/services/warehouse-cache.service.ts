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

interface SearchIndexRow {
  locationId: number;
  locationName: string;
  locationFullName: string;
  /** locationFullName + locationName, đã lower-case — dùng cho contains. */
  searchText: string;
}

@Injectable({ providedIn: "root" })
export class WarehouseCacheService {
  private readonly apiLocations =
    this.applicationConfigService.getEndpointFor("/api/location");

  private syncInFlight: Promise<void> | null = null;
  /** Index tìm kiếm trong RAM — load 1 lần sau sync, không hydrate vào Angular component. */
  private searchIndex: SearchIndexRow[] = [];

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
    this.setSearchIndex(list);
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
    await this.ensureSearchIndex();
    const lower = trimmed.toLowerCase();
    const found = this.searchIndex.find(
      (w) => (w.locationFullName ?? "").trim().toLowerCase() === lower,
    );
    return found
      ? {
          locationId: found.locationId,
          locationName: found.locationName,
          locationFullName: found.locationFullName,
        }
      : undefined;
  }

  /**
   * Contains + không phân biệt hoa thường trên index RAM.
   * Ví dụ: "zone" khớp "SMT-ZONE-01". Ưu tiên kết quả bắt đầu bằng keyword.
   */
  async searchByName(keyword: string): Promise<CachedWarehouse[]> {
    const trimmed = keyword.trim();
    if (!trimmed) {
      return [];
    }
    await this.ensureSearchIndex();

    const lowerKeyword = trimmed.toLowerCase();
    const matched = this.searchIndex.filter((w) =>
      w.searchText.includes(lowerKeyword),
    );

    matched.sort((a, b) => {
      const aFull = (a.locationFullName ?? "").toLowerCase();
      const bFull = (b.locationFullName ?? "").toLowerCase();
      const aRank = aFull.startsWith(lowerKeyword) ? 0 : 1;
      const bRank = bFull.startsWith(lowerKeyword) ? 0 : 1;
      return aRank - bRank || aFull.localeCompare(bFull);
    });

    return matched.slice(0, 50).map((w) => ({
      locationId: w.locationId,
      locationName: w.locationName,
      locationFullName: w.locationFullName,
    }));
  }

  updateOne(
    locationId: number,
    data: Partial<CachedWarehouse>,
  ): Promise<number> {
    this.searchIndex = [];
    return db.warehouses.update(locationId, data);
  }

  private async doEnsureSynced(force: boolean): Promise<void> {
    if (!force && (await this.isCacheCurrent())) {
      await this.ensureSearchIndex();
      return;
    }

    await db.warehouses.clear();
    await db.meta.delete("cacheVersion");
    this.searchIndex = [];

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

  private async ensureSearchIndex(): Promise<void> {
    if (this.searchIndex.length > 0) {
      return;
    }
    const rows = await db.warehouses.toArray();
    this.setSearchIndex(rows);
  }

  private setSearchIndex(list: CachedWarehouse[]): void {
    this.searchIndex = list.map((w) => ({
      locationId: w.locationId,
      locationName: w.locationName,
      locationFullName: w.locationFullName,
      searchText:
        `${w.locationFullName ?? ""} ${w.locationName ?? ""}`.toLowerCase(),
    }));
  }
}
