import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from "@angular/core";
import { Observable, Subscription } from "rxjs";
import { toText } from "../../services/info-tem-ncc.service";
import { AddPoItem } from "../add-info-tem-ncc.component";
import {
  MaterialSummaryDialogData,
  SummaryHighlight,
  UnassignedPoLine,
} from "../material-summary-dialog/material-summary-dialog.component";
import { boxLocation } from "../../shared/box-location.util";

/** Dữ liệu cả đơn cho panel tổng hợp (trang detail dựng từ API detail + thùng của đơn) */
export interface OrderWorkspaceData {
  pos: AddPoItem[];
  /** Vật tư chưa có PO (PO giả) — null nếu không có */
  unassigned: AddPoItem | null;
  poLines: UnassignedPoLine[];
  vendorCode: string;
  transactionId: number;
}

/** Thùng vừa scan — để panel tự chuyển sang PO và nhấp sáng dòng */
export interface ScanFollowSignal {
  poCode: string;
  sapCode: string;
  lot: string;
  seq: number;
}

interface PoCard {
  key: string;
  poCode: string;
  received: number;
  total: number;
  materialCount: number;
  boxCount: number;
  incomplete: boolean;
}

const UNASSIGNED_KEY = "__unassigned__";

/**
 * Panel tổng hợp vật tư của cả đơn trong màn Scan (desktop):
 * thanh chọn PO (+ "Chưa có PO") → bảng Tổng hợp vật tư của PO đang chọn (2 lớp: vật tư → lô).
 */
@Component({
  selector: "jhi-order-summary-panel",
  templateUrl: "./order-summary-panel.component.html",
  styleUrls: ["./order-summary-panel.component.scss"],
  standalone: false,
})
export class OrderSummaryPanelComponent implements OnDestroy {
  /** Hàm tải dữ liệu cả đơn (trả OrderWorkspaceData) */
  @Input() loader: (() => Observable<unknown>) | null = null;
  @Output() saved = new EventEmitter<number>();

  data: OrderWorkspaceData | null = null;
  cards: PoCard[] = [];
  unassignedCount = 0;
  selectedKey = "";
  /** Tự chuyển sang PO của thùng vừa scan */
  followScan = true;
  isLoading = false;
  panelData: MaterialSummaryDialogData | null = null;
  highlight: SummaryHighlight | null = null;

  private loadSub: Subscription | null = null;
  private reloadTimer: ReturnType<typeof setTimeout> | null = null;
  private lastToken = -1;

  /** Đổi token → tải lại (gom các lần scan liên tiếp, tải sau 600ms) */
  @Input() set refreshToken(value: number) {
    if (value === this.lastToken) {
      return;
    }
    const first = this.lastToken < 0;
    this.lastToken = value;
    if (first) {
      this.reload();
      return;
    }
    if (this.reloadTimer) {
      clearTimeout(this.reloadTimer);
    }
    this.reloadTimer = setTimeout(() => this.reload(), 600);
  }

  @Input() set follow(signal: ScanFollowSignal | null) {
    if (!signal || !this.followScan) {
      return;
    }
    const key = signal.poCode ? signal.poCode : UNASSIGNED_KEY;
    if (key !== this.selectedKey) {
      this.selectedKey = key;
      this.buildPanelData();
    }
    this.highlight = {
      sapCode: signal.sapCode,
      lot: signal.lot,
      seq: signal.seq,
    };
  }

  get isUnassignedSelected(): boolean {
    return this.selectedKey === UNASSIGNED_KEY;
  }

  ngOnDestroy(): void {
    this.loadSub?.unsubscribe();
    if (this.reloadTimer) {
      clearTimeout(this.reloadTimer);
    }
  }

  selectPo(key: string): void {
    if (key === this.selectedKey) {
      return;
    }
    this.selectedKey = key;
    this.highlight = null;
    this.buildPanelData();
  }

  selectUnassigned(): void {
    if (this.unassignedCount) {
      this.selectPo(UNASSIGNED_KEY);
    }
  }

  percent(card: PoCard): number {
    return card.total
      ? Math.min(100, Math.round((card.received / card.total) * 100))
      : 0;
  }

  onSummarySaved(count: number): void {
    this.saved.emit(count);
    this.reload();
  }

  trackCard(_: number, c: PoCard): string {
    return c.key;
  }

  reload(): void {
    if (!this.loader) {
      return;
    }
    this.loadSub?.unsubscribe();
    this.isLoading = true;
    this.loadSub = this.loader().subscribe({
      next: (raw) => {
        this.isLoading = false;
        this.applyData(raw as OrderWorkspaceData);
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  private applyData(data: OrderWorkspaceData): void {
    this.data = data;
    this.cards = data.pos.map((po) => this.toCard(po));
    this.unassignedCount = data.unassigned?.materials.length ?? 0;
    const keys = new Set(this.cards.map((c) => c.key));
    if (this.unassignedCount) {
      keys.add(UNASSIGNED_KEY);
    }
    if (!keys.has(this.selectedKey)) {
      this.selectedKey =
        this.cards[0]?.key ?? (this.unassignedCount ? UNASSIGNED_KEY : "");
    }
    this.buildPanelData();
  }

  private buildPanelData(): void {
    const data = this.data;
    if (!data) {
      this.panelData = null;
      return;
    }
    if (this.selectedKey === UNASSIGNED_KEY) {
      this.panelData = data.unassigned
        ? {
            po: data.unassigned,
            vendorCode: data.vendorCode,
            transactionId: data.transactionId,
            unassigned: { poLines: data.poLines },
          }
        : null;
      return;
    }
    const po = data.pos.find((p) => p.poCode === this.selectedKey);
    this.panelData = po
      ? { po, vendorCode: data.vendorCode, transactionId: data.transactionId }
      : null;
  }

  private toCard(po: AddPoItem): PoCard {
    const boxes = po.materials.flatMap((m) =>
      m.lots.flatMap((l) => l.boxes ?? []),
    );
    const incomplete = boxes.some(
      (b) =>
        !toText(b.manufacturingDate) ||
        !toText(b.expirationDate) ||
        !boxLocation(b),
    );
    return {
      key: po.poCode,
      poCode: po.poCode,
      received: po.materials.reduce((s, m) => s + (m.receivedQuantity || 0), 0),
      total: po.materials.reduce((s, m) => s + (m.poQuantity || 0), 0),
      materialCount: po.materials.length,
      boxCount: boxes.length,
      incomplete,
    };
  }
}
