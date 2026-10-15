import { Injectable } from "@angular/core";
import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { forkJoin, from, Observable, of, throwError } from "rxjs";
import {
  catchError,
  delay,
  map,
  mergeMap,
  shareReplay,
  switchMap,
  tap,
  toArray,
} from "rxjs/operators";
import { ApplicationConfigService } from "app/core/config/application-config.service";
import { environment } from "app/environments/environment";
import { ListMaterialService } from "app/entities/list-material/services/list-material.service";
import { PalletMngtService } from "app/entities/pallet-management/list/pallet-mngt.service";
import {
  ExportApproval,
  ExportHistoryFilter,
  ExportHistoryPage,
  ExportHistoryRecord,
  ExportPlannedReel,
  ExportReelInventory,
  ExportRequest,
  ExportRequestItem,
  ExportScannedBox,
  ExportScannedPallet,
} from "./warehouse-export.models";

/**
 * Dữ liệu cho màn Xuất kho.
 * TODO: chưa có API — đang trả dữ liệu mẫu; thay từng hàm bằng HTTP khi BE có API
 * (tra yêu cầu lĩnh vật tư, tra thùng theo ReelID, tra pallet, hoàn thành xuất kho).
 */
/** Lệnh sản xuất — GET /planning-work-orders, workOrder của /dnlnvl/full (chỉ các trường dùng tới) */
interface PlanningWorkOrderDto {
  woId?: string | null;
  sapWoId?: string | null;
  lotNumber?: string | null;
  productCode?: string | null;
  productName?: string | null;
  branchName?: string | null;
  groupName?: string | null;
  lineId?: string | null;
  createTime?: string | null;
  startTime?: string | null;
  scadaUserName?: string | null;
  scadaUserFullname?: string | null;
}

/** Trang Spring Data của GET /planning-work-orders (page tính từ 0) */
interface PlanningWorkOrderPageDto {
  content?: Array<PlanningWorkOrderDto | null> | null;
  totalElements?: number | null;
}

/** 1 cuộn được chỉ định trong 1 dòng đề nghị lĩnh (dnlnvl-detail-details) */
interface DnlnvlMaterialDto {
  dnlnvlDetailDetailId?: number | null;
  /** ReelID */
  materialName?: string | null;
  partNumberCode?: string | null;
  reserveQty?: number | string | null;
  deletedAt?: string | null;
  /** 1 = đã scan (khớp scannedCount / scannedQuantity của BE) */
  scanCheck?: number | string | null;
}

interface DnlnvlDetailDto {
  detail?: {
    partNumberCode?: string | null;
    /** SL cần của dòng (requiredQuantity của đề nghị = tổng estReqdQty) */
    estReqdQty?: number | string | null;
    deletedAt?: string | null;
  } | null;
  materials?: Array<DnlnvlMaterialDto | null> | null;
}

interface DnlnvlEntryDto {
  dnlnvl?: {
    dnlnvlId?: number | null;
    /** "0" = nháp (chưa gửi) */
    status?: string | null;
    deletedAt?: string | null;
  } | null;
  details?: Array<DnlnvlDetailDto | null> | null;
}

/** 1 phần tử của GET /dnlnvl/full?woId= */
interface DnlnvlFullDto {
  workOrder?: PlanningWorkOrderDto | null;
  /** Tổng hợp cả WO (BE tính): SL cần / số cuộn đã scan / SL đã scan */
  requiredQuantity?: number | null;
  scannedCount?: number | null;
  scannedQuantity?: number | null;
  dnlnvls?: Array<DnlnvlEntryDto | null> | null;
}

/** Số thùng tra /api/inventory song song khi scan pallet */
const PALLET_LOOKUP_CONCURRENCY = 6;

/** Header phiếu yêu cầu chuyển kho SAP — GET /owtq, /owtq/{docEntry} */
interface OwtqDto {
  docEntry: number;
  docDate?: string | null;
  docDueDate?: string | null;
  /** Kho xuất */
  filler?: string | null;
  /** Kho nhận */
  toWhsCode?: string | null;
  uPurNvGiao?: string | null;
  uPurNvNhan?: string | null;
  remark?: string | null;
  /** Dòng vật tư (WTQ1) — chỉ có ở GET /owtq/{docEntry} */
  lines?: Array<Wtq1Dto | null> | null;
}

/** 1 dòng vật tư của phiếu chuyển kho (WTQ1) */
interface Wtq1Dto {
  lineNum?: number | null;
  /** Mã vật tư SAP (vd 00103269) */
  itemCode?: string | null;
  dscription?: string | null;
  uPartNumber?: string | null;
  fromWhsCode?: string | null;
  whsCode?: string | null;
  quantity?: number | string | null;
  uomCode?: string | null;
}

/** Trang Spring Data của GET /owtq (page tính từ 0) */
interface OwtqPageDto {
  content?: OwtqDto[] | null;
  totalElements?: number | null;
}

/** 1 dòng danh sách yêu cầu xuất kho (trang Xuất kho) */
export interface ExportRequestListRow {
  code: string;
  proposer: string;
  /** Đơn vị lĩnh */
  receivingUnit: string;
  /** Đơn vị xuất */
  issuingUnit: string;
  /** dd/MM/yyyy */
  createdDate: string;
  /** dd/MM/yyyy */
  requestDate: string;
  status: string;
  warehouse: string;
  description: string;
  /** Tiến độ xuất (%) */
  progress: number;
}

/** 1 trang danh sách yêu cầu xuất kho */
export interface ExportRequestListPage {
  rows: ExportRequestListRow[];
  total: number;
}

/** 1 cuộn đang có trong kho của 1 part — dùng gợi ý lấy hàng theo vị trí */
export interface ExportStockReel {
  reelId: string;
  partId: string;
  partNumber: string;
  availableQty: number;
  lot: string;
  location: string;
  /** id vị trí — tra kho (khu vực) chứa vị trí */
  locationId: number;
  /** Ngày nhận (epoch giây) — sắp FIFO */
  receivedAt: number;
  /** dd/MM/yyyy */
  receivedDate: string;
  /** dd/MM/yyyy */
  expirationDate: string;
}

/** Kho (khu vực) chứa 1 vị trí — theo dashboard Tổng hợp kho */
export interface ExportWarehouseArea {
  /** Mã khu vực (vd RD-LED-05) */
  areaCode: string;
  /** Tên kho (vd Kho Nganh DTTD) */
  areaName: string;
}

/** Tra kho theo vị trí: theo locationId và theo tên vị trí (chữ thường) */
export interface ExportLocationAreaIndex {
  byId: Record<string, ExportWarehouseArea>;
  byName: Record<string, ExportWarehouseArea>;
  builtAt: number;
}

/** Bảng vị trí → kho ít thay đổi: cache phiên 6 giờ */
const AREA_INDEX_TTL_MS = 6 * 60 * 60 * 1000;
const AREA_INDEX_KEY = "warehouse-export.location-areas";

/** Tồn kho theo part (gợi ý lấy hàng) + thời điểm tra */
export interface ExportStockResult {
  rows: ExportStockReel[];
  /** epoch ms lúc tra từ API */
  fetchedAt: number;
  /** Lấy từ cache (không gọi API) */
  fromCache: boolean;
}

/** 1 vị trí đang có 1 mã vật tư SAP (Xuất chuyển kho) */
export interface ExportSapLocation {
  areaCode: string;
  areaName: string;
  location: string;
  locationId: number;
  qty: number;
  reels: number;
}

export interface ExportSapLocationResult {
  locations: ExportSapLocation[];
  /** epoch ms lúc tra API */
  fetchedAt: number;
  fromCache: boolean;
  /** Số kho tra lỗi (kết quả có thể thiếu) */
  failedAreas: number;
}

/**
 * Bảng mã SAP → vị trí (mọi kho) — dựng 1 lần / phiên, nén khi lưu sessionStorage:
 * byCode[code] = [[chỉ số kho, locationName, locationId, SL, số cuộn], ...]
 */
interface SapLocationIndex {
  fetchedAt: number;
  failedAreas: number;
  areas: ExportWarehouseArea[];
  byCode: Record<string, Array<[number, string, number, number, number]>>;
}

const SAP_LOCATION_INDEX_KEY = "warehouse-export.sap-location-index";
/** Số kho tải song song khi dựng bảng mã SAP → vị trí */
const SAP_LOCATION_CONCURRENCY = 4;
/** Bấm tra lại liên tục: bảng mới dựng trong khoảng này thì dùng luôn, không gọi lại */
const SAP_LOCATION_MIN_REFRESH_MS = 60 * 1000;
/** previewLimit đủ lớn để 1 kho trả hết mọi mã (kho lớn nhất ~4.000 mã) */
const SAP_LOCATION_PREVIEW_LIMIT = 100000;

/** Cache tồn kho theo part trong phiên: hết hạn sau 15 phút / bấm Tải lại */
const STOCK_CACHE_TTL_MS = 15 * 60 * 1000;
const STOCK_CACHE_PREFIX = "warehouse-export.stock.";

/** Gợi ý lấy hàng: số dòng mỗi trang / số trang tối đa khi tra tồn kho theo part */
const STOCK_PAGE_SIZE = 500;
const STOCK_MAX_PAGES = 4;

@Injectable({ providedIn: "root" })
export class WarehouseExportService {
  private readonly mockRequests: ExportRequest[] = [
    {
      code: "YC2026100601",
      items: [
        {
          materialCode: "00058932",
          location: "A1-02-03",
          materialName: "Bu lông inox M8×40",
          requiredQty: 150,
          scannedQty: 0,
          warehouseCode: "02",
        },
        {
          materialCode: "00048945",
          location: "A1-02-04",
          materialName: "Vòng đệm phẳng M8",
          requiredQty: 300,
          scannedQty: 0,
          warehouseCode: "02",
        },
        {
          materialCode: "00002871",
          location: "B2-01-01",
          materialName: "Dây cáp điện 3×2.5mm",
          requiredQty: 120,
          scannedQty: 0,
          warehouseCode: "02",
        },
      ],
    },
    {
      code: "YC2026100602",
      items: [
        {
          materialCode: "00020081",
          location: "C1-05-02",
          materialName: "Thân nhôm máng M16-1.2m",
          requiredQty: 4000,
          scannedQty: 0,
          warehouseCode: "02",
        },
      ],
    },
  ];

  private readonly mockHistory: ExportHistoryRecord[] = this.buildMockHistory();

  /** Cache bảng vị trí → kho (bộ nhớ + sessionStorage) */
  private areaIndex: ExportLocationAreaIndex | null = null;
  private areaIndex$: Observable<ExportLocationAreaIndex> | null = null;

  /** Tên kho SAP (GET /api/owhs) — dùng chung trong phiên */
  private sapWarehouseNames$: Observable<Map<string, string>> | null = null;
  /** Danh sách kho (GET /api/location/area) — dùng chung trong phiên */
  private warehouseAreas$: Observable<ExportWarehouseArea[]> | null = null;
  /** Bảng mã SAP → vị trí (mọi kho) + lần tải đang chạy (nhiều mã gọi cùng lúc chỉ tải 1 lần) */
  private sapLocationIndex: SapLocationIndex | null = null;
  private sapLocationIndex$: Observable<SapLocationIndex> | null = null;

  /** Cache tồn kho theo part (key = partNumber chữ thường) — kèm sessionStorage */
  private readonly stockCache = new Map<
    string,
    { fetchedAt: number; rows: ExportStockReel[] }
  >();

  constructor(
    private http: HttpClient,
    private listMaterialService: ListMaterialService,
    private palletMngtService: PalletMngtService,
    private applicationConfigService: ApplicationConfigService,
  ) {}

  /**
   * Tra yêu cầu chuyển kho theo mã (docEntry SAP) — null nếu không có.
   * GET /owtq/{docEntry}: header + lines (WTQ1) → vật tư cần xuất.
   */
  getRequest(code: string): Observable<ExportRequest | null> {
    const key = code.trim().toLowerCase();
    const mock = this.mockRequests.find((r) => r.code.toLowerCase() === key);
    if (mock) {
      return of({
        ...mock,
        items: mock.items.map((i) => ({ ...i, scannedQty: 0 })),
      }).pipe(delay(150));
    }
    return this.getOwtq(code).pipe(
      map((dto) =>
        dto
          ? { code: String(dto.docEntry), items: this.toOwtqItems(dto) }
          : null,
      ),
    );
  }

  /**
   * Khuyến nghị Planning theo Work Order: GET {planningSapApiUrl}/dnlnvl/full?woId=.
   * Lấy các đề nghị lĩnh đã gửi (bỏ nháp status "0"), cuộn chưa bị xoá (deletedAt null).
   * Mỗi partNumberCode = 1 dòng vật tư; cuộn = thùng được chỉ định (materialName = ReelID,
   * reserveQty = SL). Cuộn trùng giữa các đề nghị chỉ tính 1 lần. Không có WO (404) → null.
   */
  getPlanningRequest(workOrder: string): Observable<ExportRequest | null> {
    const wo = workOrder.trim();
    return this.getDnlnvlFull(wo).pipe(
      map((full) => (full ? this.toPlanningRequest(full, wo) : null)),
    );
  }

  /**
   * Thông tin 1 thùng trong kho theo ReelID — GET /api/inventory/{reelId}
   * (API scan của Danh sách vật tư). Không có trong kho (404 / rỗng) → null.
   */
  getReelInventory(reelId: string): Observable<ExportReelInventory | null> {
    return this.listMaterialService
      .getInventoryScanById(reelId)
      .pipe(
        map((raw) =>
          raw
            ? this.toReelInventory(raw as unknown as Record<string, unknown>)
            : null,
        ),
      );
  }

  /**
   * Mã thùng (ReelID) trên 1 pallet — GET /api/pallet-mngts/serial-pallet/{serialPallet}
   * (cùng API chi tiết pallet của Quản lý pallet). Pallet không tồn tại (404) → null.
   */
  getPalletReelIds(serialPallet: string): Observable<string[] | null> {
    return this.palletMngtService.getBoxesBySerialPallet(serialPallet).pipe(
      map((boxes) => [
        ...new Set(boxes.map((b) => (b.reelId ?? "").trim()).filter(Boolean)),
      ]),
      catchError((err: unknown) =>
        err instanceof HttpErrorResponse && err.status === 404
          ? of(null)
          : throwError(() => err),
      ),
    );
  }

  /**
   * Cuộn đang có trong kho của 1 part (Available, tồn > 0) — dùng gợi ý lấy hàng theo vị trí.
   * POST /api/inventory (API tìm kiếm của Danh sách vật tư; pageNumber bắt đầu từ 1).
   * Lấy tối đa STOCK_MAX_PAGES trang × STOCK_PAGE_SIZE dòng, các trang sau tải song song.
   * Cache theo phiên (bộ nhớ + sessionStorage, 15 phút): part đã tra trả về ngay;
   * forceRefresh = true (nút Tải lại) → tra lại API.
   */
  getStockByPart(
    partNumber: string,
    forceRefresh = false,
  ): Observable<ExportStockResult> {
    const key = partNumber.trim().toLowerCase();
    const cached = forceRefresh ? null : this.readStockCache(key);
    if (cached) {
      return of({ ...cached, fromCache: true });
    }
    return this.fetchStock({ partNumber }).pipe(
      map((rows: ExportStockReel[]): ExportStockResult => {
        const entry = { fetchedAt: Date.now(), rows };
        this.writeStockCache(key, entry);
        return { ...entry, fromCache: false };
      }),
    );
  }

  /**
   * Vị trí đang có 1 mã vật tư SAP (Xuất chuyển kho) — tra trong bảng mã SAP → vị trí
   * dựng 1 lần cho mọi kho (xem getSapLocationIndex). Tra bao nhiêu mã cũng không gọi thêm API.
   * forceRefresh = true → dựng lại bảng (nhiều mã bấm cùng lúc vẫn chỉ dựng 1 lần).
   */
  getSapLocations(
    sapCode: string,
    forceRefresh = false,
  ): Observable<ExportSapLocationResult> {
    const key = sapCode.trim().toLowerCase();
    const wasCached = !forceRefresh && this.isSapLocationIndexFresh();
    return this.getSapLocationIndex(forceRefresh).pipe(
      map((index) => ({
        fetchedAt: index.fetchedAt,
        failedAreas: index.failedAreas,
        fromCache: wasCached,
        locations: (index.byCode[key] ?? [])
          .map(
            ([
              areaIdx,
              location,
              locationId,
              qty,
              reels,
            ]): ExportSapLocation => ({
              areaCode: index.areas[areaIdx]?.areaCode ?? "",
              areaName: index.areas[areaIdx]?.areaName ?? "",
              location,
              locationId,
              qty,
              reels,
            }),
          )
          .sort(
            (x, y) =>
              x.areaCode.localeCompare(y.areaCode) ||
              x.location.localeCompare(y.location),
          ),
      })),
    );
  }

  /**
   * Bảng vị trí → kho (khu vực), giống dashboard Tổng hợp kho:
   * GET /api/location/area (danh sách kho) → GET
   * /api/inventory/warehouse-summary/area-locations?areaCode=… cho từng kho (song song).
   * Cache theo phiên 6 giờ; nhiều nơi gọi cùng lúc chỉ tải 1 lần.
   */
  getLocationAreaIndex(): Observable<ExportLocationAreaIndex> {
    const fresh = (x: ExportLocationAreaIndex | null): boolean =>
      !!x && Date.now() - x.builtAt < AREA_INDEX_TTL_MS;
    if (this.areaIndex && fresh(this.areaIndex)) {
      return of(this.areaIndex);
    }
    try {
      const raw = sessionStorage.getItem(AREA_INDEX_KEY);
      const stored = raw ? (JSON.parse(raw) as ExportLocationAreaIndex) : null;
      if (stored && fresh(stored)) {
        this.areaIndex = stored;
        return of(stored);
      }
    } catch {
      // bỏ qua — tải lại từ API
    }
    if (!this.areaIndex$) {
      this.areaIndex$ = this.fetchLocationAreaIndex().pipe(
        map((index: ExportLocationAreaIndex): ExportLocationAreaIndex => {
          this.areaIndex = index;
          this.areaIndex$ = null;
          try {
            sessionStorage.setItem(AREA_INDEX_KEY, JSON.stringify(index));
          } catch {
            // đầy quota → chỉ cache bộ nhớ
          }
          return index;
        }),
        catchError((err: unknown) => {
          this.areaIndex$ = null;
          return throwError(() => err);
        }),
        shareReplay(1),
      );
    }
    return this.areaIndex$;
  }

  /**
   * Danh sách yêu cầu xuất kho, phân trang (pageIndex từ 0).
   * - Xuất chuyển kho: GET {planningSapApiUrl}/owtq?page=&size= (mới nhất trước, page từ 0).
   * - Xuất kho khuyến nghị: GET {planningSapApiUrl}/planning-work-orders?page=&size= (page từ 0).
   */
  getExportRequestList(
    search: string,
    pageIndex: number,
    pageSize: number,
    kind: "request" | "planning" = "request",
  ): Observable<ExportRequestListPage> {
    if (kind === "request") {
      return forkJoin([
        this.http.get<OwtqPageDto>(`${environment.planningSapApiUrl}/owtq`, {
          params: { page: pageIndex, size: pageSize },
        }),
        this.getSapWarehouseNames(),
      ]).pipe(
        map(([page, names]) => ({
          rows: (page.content ?? []).map((dto) => this.toOwtqRow(dto, names)),
          total: page.totalElements ?? 0,
        })),
      );
    }
    return this.http
      .get<PlanningWorkOrderPageDto>(
        `${environment.planningSapApiUrl}/planning-work-orders`,
        { params: { page: pageIndex, size: pageSize } },
      )
      .pipe(
        map((page) => ({
          rows: (page.content ?? [])
            .filter((wo): wo is PlanningWorkOrderDto => !!wo?.woId)
            .map((wo) => this.toWorkOrderRow(wo)),
          total: page.totalElements ?? 0,
        })),
      );
  }

  /**
   * Tra 1 yêu cầu xuất theo mã — dùng cho ô nhập mã → Enter → chip ở trang danh sách.
   * null nếu không tìm thấy.
   * - Xuất chuyển kho: GET {planningSapApiUrl}/owtq/{docEntry}.
   * - Xuất kho khuyến nghị: GET {planningSapApiUrl}/dnlnvl/full?woId= (lấy workOrder).
   */
  findExportRequestByCode(
    code: string,
    kind: "request" | "planning" = "request",
  ): Observable<ExportRequestListRow | null> {
    if (kind === "request") {
      return forkJoin([this.getOwtq(code), this.getSapWarehouseNames()]).pipe(
        map(([dto, names]) => (dto ? this.toOwtqRow(dto, names) : null)),
      );
    }
    return this.getDnlnvlFull(code.trim()).pipe(
      map((full) => {
        const wo = full?.find((x) => x.workOrder?.woId)?.workOrder;
        return wo ? this.toWorkOrderRow(wo) : null;
      }),
    );
  }

  /**
   * Tra thùng theo ReelID — GET /api/inventory/{reelId}. null nếu không có trong kho.
   * materialCode = mã SAP (phần đầu userData4) → đối chiếu với itemCode của phiếu SAP.
   * SL xuất mặc định = tồn khả dụng (availableQuantity).
   */
  getBox(reelId: string, palletCode = ""): Observable<ExportScannedBox | null> {
    return this.getReelInventory(reelId).pipe(
      map((inv) => (inv ? this.toScannedBox(reelId, inv, palletCode) : null)),
    );
  }

  /**
   * Tra pallet: GET /api/pallet-mngts/serial-pallet/{serialPallet} lấy ReelID trên pallet,
   * rồi tra từng ReelID qua /api/inventory (song song tối đa PALLET_LOOKUP_CONCURRENCY).
   * Pallet không tồn tại → null; thùng không có trong kho bị bỏ qua (reelCount vẫn đếm).
   */
  getPallet(palletCode: string): Observable<ExportScannedPallet | null> {
    const code = palletCode.trim();
    return this.getPalletReelIds(code).pipe(
      switchMap((ids) => {
        if (ids === null) {
          return of(null);
        }
        if (!ids.length) {
          return of({
            palletCode: code,
            location: "",
            boxes: [],
            reelCount: 0,
          });
        }
        return from(ids).pipe(
          mergeMap(
            (id) => this.getBox(id, code).pipe(catchError(() => of(null))),
            PALLET_LOOKUP_CONCURRENCY,
          ),
          toArray(),
          map((found) => {
            const order = new Map(
              ids.map((id, idx) => [id.toLowerCase(), idx]),
            );
            const boxes = found
              .filter((b): b is ExportScannedBox => b !== null)
              .sort(
                (x, y) =>
                  (order.get(x.reelId.toLowerCase()) ?? 0) -
                  (order.get(y.reelId.toLowerCase()) ?? 0),
              );
            return {
              palletCode: code,
              location: boxes.find((b) => b.location)?.location ?? "",
              boxes,
              reelCount: ids.length,
            };
          }),
        );
      }),
    );
  }

  /** Gửi thông tin đã scan cho người duyệt — TODO: chưa có API, đang trả dữ liệu mẫu */
  send(
    requests: ExportRequest[],
    boxes: ExportScannedBox[],
    approval: ExportApproval,
  ): Observable<boolean> {
    return of(
      requests.length > 0 && boxes.length > 0 && !!approval.approver,
    ).pipe(delay(300));
  }

  /**
   * Khuyến nghị Planning — Gửi: cập nhật 1 thùng đã scan.
   * PATCH {planningSapApiUrl}/dnlnvl-detail-details
   * body [{ dnlnvlDetailDetailId, scanCheck: 1, warehouseStatus: 0 }]
   */
  markPlanningReelScanned(detailDetailId: number): Observable<void> {
    return this.http
      .patch(`${environment.planningSapApiUrl}/dnlnvl-detail-details`, [
        {
          dnlnvlDetailDetailId: detailDetailId,
          scanCheck: 1,
          warehouseStatus: 0,
        },
      ])
      .pipe(map(() => undefined));
  }

  /** Hoàn thành xuất kho — TODO: gọi API lưu phiếu xuất */
  complete(
    requests: ExportRequest[],
    boxes: ExportScannedBox[],
    approval: ExportApproval,
  ): Observable<boolean> {
    return of(
      requests.length > 0 && boxes.length > 0 && !!approval.approver,
    ).pipe(delay(200));
  }

  /** Người duyệt / người quản lý kho — cùng danh sách người duyệt của Danh sách vật tư */
  getApprovers(): Observable<string[]> {
    return this.listMaterialService
      .getApprovers()
      .pipe(map((list) => list.map((u) => u.username).filter(Boolean)));
  }

  /**
   * Lịch sử xuất kho theo bộ lọc + phân trang (page bắt đầu từ 0).
   * TODO: API lịch sử xuất kho — đang lọc trên dữ liệu mẫu.
   */
  getHistory(
    filter: ExportHistoryFilter,
    page: number,
    size: number,
  ): Observable<ExportHistoryPage> {
    const term = filter.requestCode.trim().toLowerCase();
    const filtered = this.mockHistory.filter((h) => {
      const day = h.exportedAt.slice(0, 10);
      return (
        (!term || h.requestCode.toLowerCase().includes(term)) &&
        (!filter.fromDate || day >= filter.fromDate) &&
        (!filter.toDate || day <= filter.toDate) &&
        (!filter.warehouse || h.warehouse === filter.warehouse) &&
        (!filter.operator || h.operator === filter.operator)
      );
    });
    const qty = (h: ExportHistoryRecord): number =>
      h.items.reduce((sum, i) => sum + i.exportedQty, 0);
    return of({
      items: filtered.slice(page * size, page * size + size),
      total: filtered.length,
      slipCount: filtered.length,
      requestCount: new Set(filtered.map((h) => h.requestCode)).size,
      totalQty: filtered.reduce((sum, h) => sum + qty(h), 0),
    }).pipe(delay(150));
  }

  /** Danh sách kho / người thực hiện cho bộ lọc */
  getHistoryOptions(): Observable<{
    warehouses: string[];
    operators: string[];
  }> {
    const uniq = (values: string[]): string[] => [...new Set(values)].sort();
    return of({
      warehouses: uniq(this.mockHistory.map((h) => h.warehouse)),
      operators: uniq(this.mockHistory.map((h) => h.operator)),
    });
  }

  /** GET /dnlnvl/full?woId= — null nếu WO không tồn tại (404) */
  private getDnlnvlFull(woId: string): Observable<DnlnvlFullDto[] | null> {
    if (!woId) {
      return of(null);
    }
    return this.http
      .get<
        DnlnvlFullDto[] | null
      >(`${environment.planningSapApiUrl}/dnlnvl/full`, { params: { woId } })
      .pipe(
        map((res) => (res?.length ? res : null)),
        catchError((err: unknown) =>
          err instanceof HttpErrorResponse && err.status === 404
            ? of(null)
            : throwError(() => err),
        ),
      );
  }

  /**
   * Lệnh → dòng danh sách. API chưa có trạng thái / tiến độ xuất kho → mặc định Chờ xuất, 0%.
   */
  private toWorkOrderRow(wo: PlanningWorkOrderDto): ExportRequestListRow {
    return {
      code: wo.woId ?? "",
      proposer: wo.scadaUserFullname ?? wo.scadaUserName ?? "",
      receivingUnit: wo.groupName ?? "",
      issuingUnit: wo.branchName ?? "",
      createdDate: this.formatSapDate(wo.createTime),
      requestDate: this.formatSapDate(wo.startTime),
      status: "Chờ xuất",
      warehouse: wo.lineId ?? "",
      description: [wo.productCode, wo.productName].filter(Boolean).join(" - "),
      progress: 0,
    };
  }

  /**
   * dnlnvl/full → yêu cầu xuất của WO. Mã part hiển thị tạm = partNumberCode (API không có
   * tên part) — màn Xuất kho cập nhật thành partNumber của kho khi tra tồn các thùng.
   */
  private toPlanningRequest(
    full: DnlnvlFullDto[],
    workOrder: string,
  ): ExportRequest | null {
    const text = (v: unknown): string =>
      v === null || v === undefined ? "" : String(v).trim();
    const items = new Map<string, ExportRequestItem>();
    const seenReels = new Set<string>();
    const wo = full.find((x) => x.workOrder)?.workOrder ?? null;
    for (const entry of full) {
      for (const dn of entry.dnlnvls ?? []) {
        // Bỏ đề nghị nháp (chưa gửi) / đã xoá
        if (!dn || text(dn.dnlnvl?.status) === "0" || dn.dnlnvl?.deletedAt) {
          continue;
        }
        for (const d of dn.details ?? []) {
          // Mỗi part của dòng đề nghị (detail.partNumberCode) = 1 dòng vật tư;
          // SL cần = tổng estReqdQty (khớp requiredQuantity của BE)
          const code = text(d?.detail?.partNumberCode);
          if (!d || !code || d.detail?.deletedAt) {
            continue;
          }
          const item = items.get(code) ?? {
            materialCode: code,
            materialName: "",
            partCode: code,
            altPartCodes: [] as string[],
            requiredQty: 0,
            scannedQty: 0,
            warehouseCode: "",
            reels: [] as ExportPlannedReel[],
          };
          item.requiredQty += Number(d.detail?.estReqdQty) || 0;
          items.set(code, item);
          for (const mat of d.materials ?? []) {
            const reelId = text(mat?.materialName);
            if (
              !mat ||
              !!mat.deletedAt ||
              !reelId ||
              seenReels.has(reelId.toLowerCase())
            ) {
              continue;
            }
            seenReels.add(reelId.toLowerCase());
            // Cuộn có thể là part thay thế (partNumberCode khác part của dòng)
            const reelPart = text(mat.partNumberCode) || code;
            if (reelPart !== code && !item.altPartCodes!.includes(reelPart)) {
              item.altPartCodes!.push(reelPart);
            }
            const scannedBefore = Number(mat.scanCheck) === 1;
            item.reels!.push({
              reelId,
              detailDetailId: Number(mat.dnlnvlDetailDetailId) || undefined,
              partCode: reelPart,
              quantity: Number(mat.reserveQty) || 0,
              scanned: scannedBefore,
              scannedBefore,
            });
          }
        }
      }
    }
    if (!items.size) {
      return null;
    }
    // Tổng hợp WO do BE tính (requiredQuantity / scannedCount / scannedQuantity)
    const summary = full.find(
      (x) => x.requiredQuantity !== null && x.requiredQuantity !== undefined,
    );
    return {
      code: text(wo?.woId) || workOrder,
      // Mã SAP thành phẩm (vd 00059486)
      sapCode: text(wo?.productCode),
      lotNumber: text(wo?.lotNumber),
      sapWo: text(wo?.sapWoId),
      planningSummary: summary
        ? {
            requiredQty: Number(summary.requiredQuantity) || 0,
            scannedCount: Number(summary.scannedCount) || 0,
            scannedQty: Number(summary.scannedQuantity) || 0,
          }
        : undefined,
      items: [...items.values()],
    };
  }

  /** Tra tồn kho theo partNumber (equals) */
  private fetchStock(filter: {
    partNumber: string;
  }): Observable<ExportStockReel[]> {
    const page$ = (
      pageNumber: number,
    ): Observable<{ totalItems: number; rows: ExportStockReel[] }> =>
      this.http
        .post<{
          totalItems?: number;
          inventories?: Array<Record<string, unknown>>;
        } | null>(
          this.applicationConfigService.getEndpointFor("/api/inventory"),
          this.stockSearchBody(filter, pageNumber),
        )
        .pipe(
          map((res) => ({
            totalItems: Number(res?.totalItems) || 0,
            rows: (res?.inventories ?? []).map((raw) => this.toStockReel(raw)),
          })),
        );
    return page$(1).pipe(
      switchMap((first) => {
        const pages = Math.min(
          STOCK_MAX_PAGES,
          Math.ceil(first.totalItems / STOCK_PAGE_SIZE),
        );
        if (pages <= 1) {
          return of(first.rows);
        }
        const rest = Array.from({ length: pages - 1 }, (_, k) => page$(k + 2));
        return forkJoin(rest).pipe(
          map((more) => [...first.rows, ...more.flatMap((m) => m.rows)]),
        );
      }),
      map((rows) =>
        rows.filter(
          (r) =>
            r.availableQty > 0 &&
            r.partNumber.toLowerCase() ===
              filter.partNumber.trim().toLowerCase(),
        ),
      ),
    );
  }

  /** Danh sách kho — GET /api/location/area (1 lần / phiên) */
  private getWarehouseAreas(): Observable<ExportWarehouseArea[]> {
    if (!this.warehouseAreas$) {
      const text = (v: unknown): string =>
        v === null || v === undefined ? "" : String(v).trim();
      this.warehouseAreas$ = this.http
        .get<Array<
          Record<string, unknown>
        > | null>(this.applicationConfigService.getEndpointFor("/api/location/area"))
        .pipe(
          map((areas) =>
            (areas ?? [])
              .map(
                (a): ExportWarehouseArea => ({
                  areaCode: text(a["areaName"]),
                  areaName: text(a["areaDescription"]) || text(a["areaName"]),
                }),
              )
              .filter((a) => !!a.areaCode),
          ),
          catchError((err: unknown) => {
            this.warehouseAreas$ = null;
            return throwError(() => err);
          }),
          shareReplay(1),
        );
    }
    return this.warehouseAreas$;
  }

  private isSapLocationIndexFresh(): boolean {
    const index = this.sapLocationIndex ?? this.readSapLocationIndex();
    return !!index && Date.now() - index.fetchedAt < STOCK_CACHE_TTL_MS;
  }

  /**
   * Bảng mã SAP → vị trí của mọi kho: GET /api/inventory/warehouse-summary/area-material-groups
   * ?areaCode=…&materialSearch=&searchField=sap cho từng kho (mỗi kho 1 request, trả mọi mã +
   * locationSummaries). ~20 request / ~5 s / phiên thay vì 20 request cho mỗi mã.
   * Cache bộ nhớ + sessionStorage 15 phút; nhiều nơi gọi cùng lúc chỉ tải 1 lần.
   */
  private getSapLocationIndex(
    forceRefresh = false,
  ): Observable<SapLocationIndex> {
    if (this.sapLocationIndex$) {
      return this.sapLocationIndex$;
    }
    const current = this.sapLocationIndex ?? this.readSapLocationIndex();
    const age = current ? Date.now() - current.fetchedAt : Infinity;
    if (
      current &&
      (forceRefresh
        ? age < SAP_LOCATION_MIN_REFRESH_MS
        : age < STOCK_CACHE_TTL_MS)
    ) {
      this.sapLocationIndex = current;
      return of(current);
    }
    this.sapLocationIndex$ = this.getWarehouseAreas().pipe(
      switchMap((areas) =>
        from(areas.map((area, idx) => ({ area, idx }))).pipe(
          mergeMap(
            ({ area, idx }) =>
              this.http
                .get<{
                  groups?: Array<{
                    itemCode?: string | null;
                    locationSummaries?: Array<{
                      locationId?: number | null;
                      locationName?: string | null;
                      quantity?: number | null;
                      materialIdentifierCount?: number | null;
                    } | null> | null;
                  } | null> | null;
                } | null>(
                  this.applicationConfigService.getEndpointFor(
                    "/api/inventory/warehouse-summary/area-material-groups",
                  ),
                  {
                    params: {
                      areaCode: area.areaCode,
                      areaName: "",
                      materialSearch: "",
                      searchField: "sap",
                      // Lấy đủ mọi mã của kho (mặc định chỉ 100)
                      previewLimit: SAP_LOCATION_PREVIEW_LIMIT,
                    },
                  },
                )
                .pipe(
                  map((res) => ({ idx, groups: res?.groups ?? [] })),
                  catchError(() => of(null)),
                ),
            SAP_LOCATION_CONCURRENCY,
          ),
          toArray(),
          map((parts): SapLocationIndex => {
            const byCode: SapLocationIndex["byCode"] = {};
            for (const part of parts) {
              for (const g of part?.groups ?? []) {
                const code = (g?.itemCode ?? "").trim().toLowerCase();
                if (!code || !part) {
                  continue;
                }
                for (const l of g?.locationSummaries ?? []) {
                  const qty = Number(l?.quantity) || 0;
                  if (!l || qty <= 0) {
                    continue;
                  }
                  (byCode[code] ??= []).push([
                    part.idx,
                    (l.locationName ?? "").trim(),
                    Number(l.locationId) || 0,
                    qty,
                    Number(l.materialIdentifierCount) || 0,
                  ]);
                }
              }
            }
            const failedAreas = parts.filter((p) => p === null).length;
            if (parts.length && failedAreas === parts.length) {
              throw new Error("Không tra được vị trí");
            }
            return { fetchedAt: Date.now(), failedAreas, areas, byCode };
          }),
        ),
      ),
      tap((index) => {
        this.sapLocationIndex = index;
        this.sapLocationIndex$ = null;
        if (!index.failedAreas) {
          this.writeSapLocationIndex(index);
        }
      }),
      catchError((err: unknown) => {
        this.sapLocationIndex$ = null;
        return throwError(() => err);
      }),
      shareReplay(1),
    );
    return this.sapLocationIndex$;
  }

  private readSapLocationIndex(): SapLocationIndex | null {
    try {
      const raw = sessionStorage.getItem(SAP_LOCATION_INDEX_KEY);
      const stored = raw ? (JSON.parse(raw) as SapLocationIndex) : null;
      return stored && Array.isArray(stored.areas) && stored.byCode
        ? stored
        : null;
    } catch {
      return null;
    }
  }

  private writeSapLocationIndex(index: SapLocationIndex): void {
    try {
      sessionStorage.setItem(SAP_LOCATION_INDEX_KEY, JSON.stringify(index));
    } catch {
      // Đầy quota / bị chặn → vẫn còn cache bộ nhớ
    }
  }

  private fetchLocationAreaIndex(): Observable<ExportLocationAreaIndex> {
    const text = (v: unknown): string =>
      v === null || v === undefined ? "" : String(v).trim();
    return this.http
      .get<Array<
        Record<string, unknown>
      > | null>(this.applicationConfigService.getEndpointFor("/api/location/area"))
      .pipe(
        switchMap((areas) => {
          const list = (areas ?? [])
            .map(
              (a): ExportWarehouseArea => ({
                areaCode: text(a["areaName"]),
                areaName: text(a["areaDescription"]) || text(a["areaName"]),
              }),
            )
            .filter((a) => !!a.areaCode);
          const calls = list.map((area) =>
            this.http
              .get<{ locations?: Array<Record<string, unknown>> } | null>(
                this.applicationConfigService.getEndpointFor(
                  "/api/inventory/warehouse-summary/area-locations",
                ),
                {
                  params: {
                    areaCode: area.areaCode,
                    // areaName phải là tên kho hoặc rỗng (truyền mã → không ra vị trí)
                    areaName: "",
                    materialType: "",
                    locationSearch: "",
                  },
                },
              )
              .pipe(
                map((res) => ({ area, locations: res?.locations ?? [] })),
                catchError(() =>
                  of({ area, locations: [] as Array<Record<string, unknown>> }),
                ),
              ),
          );
          return calls.length ? forkJoin(calls) : of([]);
        }),
        map((rows): ExportLocationAreaIndex => {
          const index: ExportLocationAreaIndex = {
            byId: {},
            byName: {},
            builtAt: Date.now(),
          };
          for (const { area, locations } of rows) {
            for (const loc of locations) {
              const id = text(loc["locationId"]);
              if (id) {
                index.byId[id] = area;
              }
              for (const name of [
                text(loc["locationName"]),
                text(loc["locationFullName"]),
              ]) {
                if (name) {
                  index.byName[name.toLowerCase()] = area;
                }
              }
            }
          }
          return index;
        }),
      );
  }

  private readStockCache(
    key: string,
  ): { fetchedAt: number; rows: ExportStockReel[] } | null {
    const fresh = (entry: { fetchedAt: number } | null | undefined): boolean =>
      !!entry && Date.now() - entry.fetchedAt < STOCK_CACHE_TTL_MS;
    const mem = this.stockCache.get(key);
    if (mem && fresh(mem)) {
      return mem;
    }
    try {
      const raw = sessionStorage.getItem(STOCK_CACHE_PREFIX + key);
      const stored = raw
        ? (JSON.parse(raw) as { fetchedAt: number; rows: ExportStockReel[] })
        : null;
      if (stored && fresh(stored) && Array.isArray(stored.rows)) {
        this.stockCache.set(key, stored);
        return stored;
      }
    } catch {
      // sessionStorage không dùng được → chỉ cache bộ nhớ
    }
    return null;
  }

  private writeStockCache(
    key: string,
    entry: { fetchedAt: number; rows: ExportStockReel[] },
  ): void {
    this.stockCache.set(key, entry);
    try {
      sessionStorage.setItem(STOCK_CACHE_PREFIX + key, JSON.stringify(entry));
    } catch {
      // Đầy quota / bị chặn → vẫn còn cache bộ nhớ
    }
  }

  /** Body tìm kiếm tồn kho theo part (cùng cấu trúc với Danh sách vật tư) */
  private stockSearchBody(
    filter: { partNumber: string },
    pageNumber: number,
  ): Record<string, unknown> {
    const textKeys = [
      "materialIdentifier",
      "status",
      "lotNumber",
      "userData4",
      "userData5",
      "locationName",
      "expirationDate",
      "updatedDate",
      "trackingType",
      "updatedBy",
      "manufacturingDate",
      "materialType",
      "checkinDate",
      "receivedDate",
      "rankAp",
      "rankQuang",
      "rankMau",
      "materialName",
    ];
    const body: Record<string, unknown> = {};
    for (const key of textKeys) {
      body[key] = "";
      body[`${key}Mode`] = "contains";
    }
    return {
      ...body,
      partNumber: filter.partNumber.trim(),
      partNumberMode: "equals",
      calculatedStatus: "Available",
      calculatedStatusMode: "equals",
      quantity: null,
      quantityMode: "contains",
      availableQuantity: null,
      availableQuantityMode: "contains",
      pageNumber,
      itemPerPage: STOCK_PAGE_SIZE,
    };
  }

  private toStockReel(raw: Record<string, unknown>): ExportStockReel {
    const text = (v: unknown): string =>
      v === null || v === undefined ? "" : String(v).trim();
    const received = Number(raw["receivedDate"]);
    return {
      reelId: text(raw["materialIdentifier"]),
      partId: text(raw["partId"]),
      partNumber: text(raw["partNumber"]),
      availableQty: Number(raw["availableQuantity"]) || 0,
      lot: text(raw["lotNumber"]),
      location: text(raw["locationName"]) || "Chưa có vị trí",
      locationId: Number(raw["locationId"]) || 0,
      receivedAt: Number.isFinite(received) ? received : 0,
      receivedDate: this.formatInventoryDate(text(raw["receivedDate"])),
      expirationDate: this.formatInventoryDate(text(raw["expirationDate"])),
    };
  }

  private toScannedBox(
    reelId: string,
    inv: ExportReelInventory,
    palletCode: string,
  ): ExportScannedBox {
    const stock = inv.availableQty || inv.quantity;
    return {
      reelId,
      materialCode: inv.sapCode,
      quantity: stock,
      lot: inv.lot,
      mfgDate: inv.manufacturingDate,
      palletCode,
      location: inv.location,
      inventoryQty: stock,
      expirationDate: inv.expirationDate,
      inventoryStatus: inv.status,
      partNumber: inv.partNumber,
    };
  }

  private toReelInventory(raw: Record<string, unknown>): ExportReelInventory {
    const text = (v: unknown): string =>
      v === null || v === undefined ? "" : String(v).trim();
    const num = (v: unknown): number => {
      const n = Number(v);
      return Number.isFinite(n) ? n : 0;
    };
    return {
      partId: text(raw["partId"]),
      partNumber: text(raw["partNumber"]),
      availableQty: num(raw["availableQuantity"]),
      quantity: num(raw["quantity"]),
      initialQty: num(raw["initialQuantity"]),
      status: text(raw["calculatedStatus"]) || text(raw["status"]),
      location: text(raw["locationFullName"]) || text(raw["locationName"]),
      locationId: Number(raw["locationId"]) || 0,
      sapCode: text(raw["userData4"]).split("-")[0].trim(),
      lot: text(raw["lotNumber"]),
      expirationDate: this.formatInventoryDate(text(raw["expirationDate"])),
      manufacturingDate: this.formatInventoryDate(
        text(raw["manufacturingDate"]),
      ),
    };
  }

  /** HSD từ /inventory là epoch (giây hoặc mili giây) → dd/MM/yyyy; dạng khác giữ nguyên */
  private formatInventoryDate(value: string): string {
    if (!/^\d{9,13}$/.test(value)) {
      return value;
    }
    const n = Number(value);
    const d = new Date(value.length > 11 ? n : n * 1000);
    if (Number.isNaN(d.getTime())) {
      return value;
    }
    const pad = (x: number): string => String(x).padStart(2, "0");
    return `${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()}`;
  }

  /** GET /owtq/{docEntry} — null nếu mã không phải số hoặc không tồn tại (404) */
  private getOwtq(code: string): Observable<OwtqDto | null> {
    const docEntry = code.trim();
    if (!/^\d+$/.test(docEntry)) {
      return of(null);
    }
    return this.http
      .get<OwtqDto | null>(`${environment.planningSapApiUrl}/owtq/${docEntry}`)
      .pipe(
        map((dto) => (dto?.docEntry ? dto : null)),
        catchError((err: HttpErrorResponse) =>
          err.status === 404 ? of(null) : throwError(() => err),
        ),
      );
  }

  /** OWTQ → dòng danh sách. SAP không có trạng thái / tiến độ xuất → mặc định Chờ xuất, 0% */
  /**
   * Tên kho SAP theo mã (whsCode → whsName) — GET /api/owhs, tải 1 lần / phiên.
   * Lỗi → bảng rỗng (danh sách vẫn hiện mã kho), lần sau gọi lại.
   */
  private getSapWarehouseNames(): Observable<Map<string, string>> {
    if (!this.sapWarehouseNames$) {
      this.sapWarehouseNames$ = this.http
        .get<Array<{
          whsCode?: string | null;
          whsName?: string | null;
        } | null> | null>(
          this.applicationConfigService.getEndpointFor("/api/owhs"),
        )
        .pipe(
          map(
            (rows) =>
              new Map(
                (rows ?? [])
                  .filter((r) => !!r?.whsCode?.trim())
                  .map((r): [string, string] => [
                    (r?.whsCode ?? "").trim().toLowerCase(),
                    (r?.whsName ?? "").trim(),
                  ]),
              ),
          ),
          catchError(() => {
            this.sapWarehouseNames$ = null;
            return of(new Map<string, string>());
          }),
          shareReplay(1),
        );
    }
    return this.sapWarehouseNames$;
  }

  /** Mã kho SAP → "01 - Kho linh kiện điện tử" (không có tên → giữ mã) */
  private sapWarehouseLabel(
    code: string | null | undefined,
    names: Map<string, string>,
  ): string {
    const c = (code ?? "").trim();
    const name = names.get(c.toLowerCase());
    return c && name ? `${c} - ${name}` : c;
  }

  private toOwtqRow(
    dto: OwtqDto,
    names: Map<string, string>,
  ): ExportRequestListRow {
    return {
      code: String(dto.docEntry),
      proposer: dto.uPurNvGiao ?? "",
      receivingUnit: dto.uPurNvNhan ?? "",
      issuingUnit: this.sapWarehouseLabel(dto.filler, names),
      createdDate: this.formatSapDate(dto.docDate),
      requestDate: this.formatSapDate(dto.docDueDate),
      status: "Chờ xuất",
      warehouse: this.sapWarehouseLabel(dto.toWhsCode, names),
      description: dto.remark ?? "",
      progress: 0,
    };
  }

  /**
   * Lines WTQ1 → vật tư của yêu cầu. materialCode = itemCode (mã SAP — khớp phần đầu
   * userData4 của thùng). Cùng 1 mã ở nhiều dòng → gộp SL.
   */
  private toOwtqItems(dto: OwtqDto): ExportRequestItem[] {
    const byCode = new Map<string, ExportRequestItem>();
    for (const line of dto.lines ?? []) {
      const code = (line?.itemCode ?? "").trim();
      if (!line || !code) {
        continue;
      }
      const qty = Number(line.quantity) || 0;
      const existing = byCode.get(code);
      if (existing) {
        existing.requiredQty += qty;
        continue;
      }
      byCode.set(code, {
        materialCode: code,
        materialName: line.dscription ?? "",
        requiredQty: qty,
        scannedQty: 0,
        warehouseCode: line.fromWhsCode ?? dto.filler ?? "",
        partNumbers: line.uPartNumber ?? undefined,
        uom: line.uomCode ?? undefined,
      });
    }
    return [...byCode.values()];
  }

  /** "2026-10-12T00:00:00" → "12/10/2026" */
  private formatSapDate(value?: string | null): string {
    const m = /^(\d{4})-(\d{2})-(\d{2})/.exec(value ?? "");
    return m ? `${m[3]}/${m[2]}/${m[1]}` : "";
  }

  private buildMockHistory(): ExportHistoryRecord[] {
    const people = ["Nguyễn Văn A", "Trần Thị B", "Lê Văn C", "Phạm Thị D"];
    const names = [
      "Bu lông inox M8×40",
      "Vòng đệm phẳng M8",
      "Dây cáp điện 3×2.5mm",
      "Thân nhôm máng M16-1.2m",
      "Ốc vít M4×12",
      "Tụ điện 10uF",
    ];
    const list: ExportHistoryRecord[] = [];
    for (let n = 0; n < 28; n++) {
      const day = 6 - Math.floor(n / 3);
      const date = new Date(
        2026,
        9,
        Math.max(1, day),
        9 + (n % 8),
        (n * 17) % 60,
      );
      const itemCount = 1 + (n % 4);
      const items = Array.from({ length: itemCount }, (_, k) => {
        const code = String(58932 + n * 7 + k).padStart(8, "0");
        const boxCount = 1 + ((n + k) % 3);
        const perBox = 20 * (1 + ((n + k) % 5));
        const boxes: ExportScannedBox[] = Array.from(
          { length: boxCount },
          (__, b) => ({
            reelId: `2026100500${String(n * 10 + k).padStart(3, "0")}${b + 1}${code}`,
            materialCode: code,
            quantity: perBox,
            lot: `2026100${(b % 9) + 1}`,
            mfgDate: "14/08/2026",
            palletCode:
              b < 2 && k === 0
                ? `P0001202609${String(20 + n).padStart(2, "0")}`
                : "",
            location: `0${(n % 3) + 1}-A0${b + 1}`,
          }),
        );
        const exported = boxes.reduce((sum, b) => sum + b.quantity, 0);
        return {
          materialCode: code,
          materialName: names[(n + k) % names.length],
          requiredQty: exported,
          exportedQty: exported,
          warehouseCode: `0${(n % 3) + 1}`,
          boxes,
        };
      });
      list.push({
        id: n + 1,
        requestCode: `YC202610${String(601 - n * 3).padStart(4, "0")}`,
        exportedAt: `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}T${String(date.getHours()).padStart(2, "0")}:${String(date.getMinutes()).padStart(2, "0")}:00`,
        operator: people[n % people.length],
        warehouse: `Kho 0${(n % 3) + 1}`,
        items,
      });
    }
    return list;
  }
}
