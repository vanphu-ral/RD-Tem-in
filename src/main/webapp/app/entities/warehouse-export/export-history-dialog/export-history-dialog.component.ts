import { Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { WarehouseExportService } from "../warehouse-export.service";
import {
  ExportHistoryFilter,
  ExportHistoryItem,
  ExportHistoryRecord,
} from "../warehouse-export.models";

type DetailTab = "box" | "pallet";

/** Pallet trong 1 vật tư của phiếu xuất (gom từ các thùng) */
interface HistoryPalletRow {
  palletCode: string;
  boxCount: number;
  totalQty: number;
  location: string;
}

const PAGE_SIZE = 5;

/**
 * Lịch sử xuất kho: thống kê + bộ lọc + bảng 3 cấp
 * (phiếu xuất theo mã yêu cầu → vật tư → thùng / pallet), phân trang.
 */
@Component({
  selector: "jhi-export-history-dialog",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: "./export-history-dialog.component.html",
  styleUrls: ["./export-history-dialog.component.scss"],
})
export class ExportHistoryDialogComponent implements OnInit {
  filter: ExportHistoryFilter = this.defaultFilter();
  warehouses: string[] = [];
  operators: string[] = [];

  records: ExportHistoryRecord[] = [];
  total = 0;
  slipCount = 0;
  requestCount = 0;
  totalQty = 0;
  pageIndex = 0;
  readonly pageSize = PAGE_SIZE;
  isLoading = false;

  private expandedRecords = new Set<number>();
  private expandedItems = new Set<string>();
  private itemTabs = new Map<string, DetailTab>();

  constructor(
    private dialogRef: MatDialogRef<ExportHistoryDialogComponent>,
    private exportService: WarehouseExportService,
    private notificationService: NotificationService,
  ) {}

  ngOnInit(): void {
    this.exportService.getHistoryOptions().subscribe((opts) => {
      this.warehouses = opts.warehouses;
      this.operators = opts.operators;
    });
    this.load();
  }

  // ==================== LỌC / PHÂN TRANG ====================

  onSearch(): void {
    if (
      this.filter.fromDate &&
      this.filter.toDate &&
      this.filter.fromDate > this.filter.toDate
    ) {
      this.notificationService.warning(
        "Từ ngày phải trước hoặc bằng đến ngày.",
      );
      return;
    }
    this.pageIndex = 0;
    this.load();
  }

  onReset(): void {
    this.filter = this.defaultFilter();
    this.pageIndex = 0;
    this.load();
  }

  get pageCount(): number {
    return Math.max(1, Math.ceil(this.total / this.pageSize));
  }

  get rangeText(): string {
    if (!this.total) {
      return "Không có phiếu";
    }
    const from = this.pageIndex * this.pageSize + 1;
    const to = Math.min(this.total, from + this.pageSize - 1);
    return `Hiển thị ${from}–${to} / ${this.total} phiếu`;
  }

  /** Số trang hiển thị: 1 … (trang hiện tại ±1) … cuối; null = dấu "…" */
  get pageButtons(): Array<number | null> {
    const last = this.pageCount - 1;
    const pages = new Set<number>([0, last]);
    for (let p = this.pageIndex - 1; p <= this.pageIndex + 1; p++) {
      if (p >= 0 && p <= last) {
        pages.add(p);
      }
    }
    if (this.pageIndex <= 1) {
      [1, 2].filter((p) => p <= last).forEach((p) => pages.add(p));
    }
    const sorted = [...pages].sort((a, b) => a - b);
    const result: Array<number | null> = [];
    sorted.forEach((p, i) => {
      if (i > 0 && p - sorted[i - 1] > 1) {
        result.push(null);
      }
      result.push(p);
    });
    return result;
  }

  goPage(page: number): void {
    if (page < 0 || page >= this.pageCount || page === this.pageIndex) {
      return;
    }
    this.pageIndex = page;
    this.load();
  }

  // ==================== BẢNG ====================

  toggleRecord(r: ExportHistoryRecord): void {
    if (this.expandedRecords.has(r.id)) {
      this.expandedRecords.delete(r.id);
    } else {
      this.expandedRecords.add(r.id);
    }
  }

  isRecordExpanded(r: ExportHistoryRecord): boolean {
    return this.expandedRecords.has(r.id);
  }

  toggleItem(r: ExportHistoryRecord, i: ExportHistoryItem): void {
    const key = this.itemKey(r, i);
    if (this.expandedItems.has(key)) {
      this.expandedItems.delete(key);
    } else {
      this.expandedItems.add(key);
    }
  }

  isItemExpanded(r: ExportHistoryRecord, i: ExportHistoryItem): boolean {
    return this.expandedItems.has(this.itemKey(r, i));
  }

  itemTab(r: ExportHistoryRecord, i: ExportHistoryItem): DetailTab {
    return this.itemTabs.get(this.itemKey(r, i)) ?? "box";
  }

  setItemTab(
    r: ExportHistoryRecord,
    i: ExportHistoryItem,
    tab: DetailTab,
  ): void {
    this.itemTabs.set(this.itemKey(r, i), tab);
  }

  recordQty(r: ExportHistoryRecord): number {
    return r.items.reduce((s, i) => s + i.exportedQty, 0);
  }

  palletRows(i: ExportHistoryItem): HistoryPalletRow[] {
    const map = new Map<string, HistoryPalletRow>();
    for (const b of i.boxes) {
      if (!b.palletCode) {
        continue;
      }
      const row = map.get(b.palletCode) ?? {
        palletCode: b.palletCode,
        boxCount: 0,
        totalQty: 0,
        location: b.location,
      };
      row.boxCount++;
      row.totalQty += b.quantity;
      map.set(b.palletCode, row);
    }
    return [...map.values()];
  }

  /** ISO → dd/MM/yyyy HH:mm */
  formatDateTime(iso: string): string {
    const m = /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})/.exec(iso);
    return m ? `${m[3]}/${m[2]}/${m[1]} ${m[4]}:${m[5]}` : iso;
  }

  trackRecord(_: number, r: ExportHistoryRecord): number {
    return r.id;
  }

  onClose(): void {
    this.dialogRef.close();
  }

  // ==================== NỘI BỘ ====================

  private load(): void {
    this.isLoading = true;
    this.exportService
      .getHistory(this.filter, this.pageIndex, this.pageSize)
      .subscribe({
        next: (page) => {
          this.isLoading = false;
          this.records = page.items;
          this.total = page.total;
          this.slipCount = page.slipCount;
          this.requestCount = page.requestCount;
          this.totalQty = page.totalQty;
          // Mở sẵn phiếu đầu trang
          this.expandedRecords = new Set(
            page.items.length ? [page.items[0].id] : [],
          );
        },
        error: () => {
          this.isLoading = false;
          this.notificationService.error("Không tải được lịch sử xuất kho.");
        },
      });
  }

  private itemKey(r: ExportHistoryRecord, i: ExportHistoryItem): string {
    return `${r.id}|${i.materialCode}`;
  }

  /** Mặc định 7 ngày gần nhất */
  private defaultFilter(): ExportHistoryFilter {
    const iso = (d: Date): string =>
      `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
    const today = new Date();
    const from = new Date(today);
    from.setDate(today.getDate() - 7);
    return {
      requestCode: "",
      fromDate: iso(from),
      toDate: iso(today),
      warehouse: "",
      operator: "",
    };
  }
}
