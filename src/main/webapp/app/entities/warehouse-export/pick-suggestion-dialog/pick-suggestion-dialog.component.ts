import { Component, Inject, OnDestroy, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { from, of, Subscription } from "rxjs";
import { catchError, map, mergeMap } from "rxjs/operators";
import {
  ExportStockReel,
  ExportStockResult,
  WarehouseExportService,
} from "../warehouse-export.service";

/** 1 part cần gợi ý lấy hàng */
export interface PickSuggestionPart {
  /** Mã part (name của Planning, = partNumber kho) */
  partNumber: string;
  /** SL còn thiếu = Cần xuất − Đã scan */
  needQty: number;
}

export interface PickSuggestionDialogData {
  /** Mã WO — hiển thị tiêu đề */
  title: string;
  parts: PickSuggestionPart[];
  /** ReelID đã scan — không gợi ý lại */
  excludeReelIds: string[];
}

/** Chiến lược chọn cuộn */
type PickMode = "fifo" | "fewest";

interface PickGroup {
  location: string;
  reels: ExportStockReel[];
  qty: number;
}

interface PartSuggestion {
  partNumber: string;
  needQty: number;
  loading: boolean;
  error: boolean;
  /** Mọi cuộn khả dụng của part (đã loại cuộn đã scan) */
  stock: ExportStockReel[];
  /** Kết quả chọn theo chiến lược hiện tại, gom theo vị trí */
  groups: PickGroup[];
  pickedQty: number;
  stockQty: number;
  /** Thời điểm tra tồn (epoch ms) */
  fetchedAt: number;
  fromCache: boolean;
}

/** Kết quả tra tồn rỗng — dùng khi API lỗi */
const EMPTY_STOCK: ExportStockResult = {
  rows: [],
  fetchedAt: 0,
  fromCache: false,
};

/** Số part tra tồn kho song song */
const PART_CONCURRENCY = 3;

/**
 * Gợi ý lấy hàng theo vị trí (Khuyến nghị Planning): với mỗi part còn thiếu, tra các cuộn
 * đang có trong kho (Available), chọn đủ SL theo FIFO (nhận trước xuất trước) hoặc ít vị trí
 * nhất, gom theo vị trí để phụ kho đi lấy 1 vòng — cuộn nào cùng part cũng scan được.
 */
@Component({
  selector: "jhi-pick-suggestion-dialog",
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule],
  templateUrl: "./pick-suggestion-dialog.component.html",
  styleUrls: ["./pick-suggestion-dialog.component.scss"],
})
export class PickSuggestionDialogComponent implements OnInit, OnDestroy {
  mode: PickMode = "fifo";
  parts: PartSuggestion[] = [];

  private expanded = new Set<string>();
  private sub?: Subscription;

  constructor(
    private dialogRef: MatDialogRef<PickSuggestionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PickSuggestionDialogData,
    private exportService: WarehouseExportService,
  ) {}

  ngOnInit(): void {
    this.parts = this.data.parts.map((p) => ({
      partNumber: p.partNumber,
      needQty: p.needQty,
      loading: true,
      error: false,
      stock: [],
      groups: [],
      pickedQty: 0,
      stockQty: 0,
      fetchedAt: 0,
      fromCache: false,
    }));
    this.load();
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  get loadingCount(): number {
    return this.parts.filter((p) => p.loading).length;
  }

  get isLoading(): boolean {
    return this.loadingCount > 0;
  }

  /** Tổng số vị trí cần đi */
  get locationCount(): number {
    return new Set(this.parts.flatMap((p) => p.groups.map((g) => g.location)))
      .size;
  }

  setMode(mode: PickMode): void {
    if (this.mode === mode) {
      return;
    }
    this.mode = mode;
    this.parts.forEach((p) => this.pick(p));
  }

  toggleGroup(p: PartSuggestion, g: PickGroup): void {
    const key = `${p.partNumber}|${g.location}`;
    if (this.expanded.has(key)) {
      this.expanded.delete(key);
    } else {
      this.expanded.add(key);
    }
  }

  isGroupOpen(p: PartSuggestion, g: PickGroup): boolean {
    return this.expanded.has(`${p.partNumber}|${g.location}`);
  }

  /** Tải lại: bỏ cache, tra lại tồn kho từ API */
  reload(): void {
    this.parts.forEach((p) => {
      p.loading = true;
      p.error = false;
    });
    this.load(true);
  }

  close(): void {
    this.dialogRef.close();
  }

  /** HH:mm dd/MM của lần tra tồn */
  fetchedText(p: PartSuggestion): string {
    if (!p.fetchedAt) {
      return "";
    }
    const d = new Date(p.fetchedAt);
    const pad = (n: number): string => String(n).padStart(2, "0");
    return `${pad(d.getHours())}:${pad(d.getMinutes())} ${pad(d.getDate())}/${pad(d.getMonth() + 1)}`;
  }

  private load(forceRefresh = false): void {
    this.sub?.unsubscribe();
    const exclude = new Set(
      this.data.excludeReelIds.map((id) => id.toLowerCase()),
    );
    this.sub = from(this.parts)
      .pipe(
        mergeMap(
          (p) =>
            this.exportService.getStockByPart(p.partNumber, forceRefresh).pipe(
              map((res: ExportStockResult) => ({ p, res, error: false })),
              // Lỗi tra tồn → kết quả rỗng + cờ lỗi (không dùng union với null)
              catchError(() => of({ p, res: EMPTY_STOCK, error: true })),
            ),
          PART_CONCURRENCY,
        ),
      )
      .subscribe(({ p, res, error }) => {
        p.loading = false;
        p.error = error;
        p.fetchedAt = res.fetchedAt;
        p.fromCache = res.fromCache;
        p.stock = res.rows.filter((r) => !exclude.has(r.reelId.toLowerCase()));
        p.stockQty = p.stock.reduce((s, r) => s + r.availableQty, 0);
        this.pick(p);
      });
  }

  /** Chọn cuộn đủ SL còn thiếu theo chiến lược, gom theo vị trí */
  private pick(p: PartSuggestion): void {
    const fifo = (a: ExportStockReel, b: ExportStockReel): number =>
      a.receivedAt - b.receivedAt || a.reelId.localeCompare(b.reelId);
    let ordered: ExportStockReel[];
    if (this.mode === "fifo") {
      ordered = [...p.stock].sort(fifo);
    } else {
      // Ít vị trí nhất: vị trí có nhiều hàng trước, trong vị trí vẫn FIFO
      const byLoc = this.groupByLocation(p.stock);
      byLoc.sort((a, b) => b.qty - a.qty);
      ordered = byLoc.flatMap((g) => [...g.reels].sort(fifo));
    }
    const picked: ExportStockReel[] = [];
    let sum = 0;
    for (const reel of ordered) {
      if (sum >= p.needQty) {
        break;
      }
      picked.push(reel);
      sum += reel.availableQty;
    }
    p.pickedQty = sum;
    p.groups = this.groupByLocation(picked).sort((a, b) =>
      a.location.localeCompare(b.location),
    );
  }

  private groupByLocation(reels: ExportStockReel[]): PickGroup[] {
    const groups = new Map<string, PickGroup>();
    for (const reel of reels) {
      const g = groups.get(reel.location) ?? {
        location: reel.location,
        reels: [],
        qty: 0,
      };
      g.reels.push(reel);
      g.qty += reel.availableQty;
      groups.set(reel.location, g);
    }
    return [...groups.values()];
  }
}
