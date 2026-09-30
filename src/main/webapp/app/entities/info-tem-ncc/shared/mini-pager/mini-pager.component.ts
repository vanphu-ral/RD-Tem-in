import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { MatIconModule } from "@angular/material/icon";

export interface MiniPageState {
  pageIndex: number;
  pageSize: number;
}

/**
 * Phân trang nhỏ gọn cho bảng lồng: "1–10 / 25  [10▾]  ‹ 1/3 ›".
 * Ẩn khi tổng số dòng ≤ cỡ trang nhỏ nhất.
 */
@Component({
  selector: "jhi-mini-pager",
  standalone: true,
  imports: [CommonModule, MatIconModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="mini-pager" *ngIf="total > pageSizeOptions[0]">
      <span class="mp-range"
        >{{ rangeStart }}–{{ rangeEnd }} / {{ total }}</span
      >
      <select
        class="mp-size"
        [value]="pageSize"
        (change)="onSizeChange($any($event.target).value)"
        title="Số dòng / trang"
      >
        <option *ngFor="let s of pageSizeOptions" [value]="s">{{ s }}</option>
      </select>
      <button
        type="button"
        class="mp-btn"
        [disabled]="pageIndex === 0"
        (click)="go(pageIndex - 1)"
        aria-label="Trang trước"
      >
        <mat-icon>chevron_left</mat-icon>
      </button>
      <span class="mp-page">{{ pageIndex + 1 }}/{{ pageCount }}</span>
      <button
        type="button"
        class="mp-btn"
        [disabled]="pageIndex >= pageCount - 1"
        (click)="go(pageIndex + 1)"
        aria-label="Trang sau"
      >
        <mat-icon>chevron_right</mat-icon>
      </button>
    </div>
  `,
  styles: [
    `
      .mini-pager {
        display: flex;
        align-items: center;
        justify-content: flex-end;
        gap: 6px;
        padding: 4px 2px 0;
        font-size: 11px;
        color: #6b7280;
        user-select: none;
      }
      .mp-range {
        margin-right: 4px;
      }
      .mp-size {
        height: 22px;
        padding: 0 2px;
        border: 1px solid #d1d5db;
        border-radius: 4px;
        background: #fff;
        font-size: 11px;
        color: #374151;
        cursor: pointer;
      }
      .mp-btn {
        width: 24px;
        height: 24px;
        padding: 0;
        border: 1px solid #d1d5db;
        border-radius: 4px;
        background: #fff;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        color: #374151;
      }
      .mp-btn:hover:not(:disabled) {
        background: #eff6ff;
        border-color: #93c5fd;
      }
      .mp-btn:disabled {
        opacity: 0.4;
        cursor: default;
      }
      .mp-btn mat-icon {
        font-size: 18px;
        width: 18px;
        height: 18px;
      }
      .mp-page {
        min-width: 32px;
        text-align: center;
        font-weight: 600;
        color: #374151;
      }
    `,
  ],
})
export class MiniPagerComponent {
  @Input() total = 0;
  @Input() pageIndex = 0;
  @Input() pageSize = 5;
  @Input() pageSizeOptions: number[] = [5, 10, 20];
  @Output() pageChange = new EventEmitter<MiniPageState>();

  get pageCount(): number {
    return Math.max(1, Math.ceil(this.total / this.pageSize));
  }

  get rangeStart(): number {
    return this.total ? this.pageIndex * this.pageSize + 1 : 0;
  }

  get rangeEnd(): number {
    return Math.min(this.total, (this.pageIndex + 1) * this.pageSize);
  }

  go(index: number): void {
    const next = Math.min(Math.max(0, index), this.pageCount - 1);
    if (next !== this.pageIndex) {
      this.pageChange.emit({ pageIndex: next, pageSize: this.pageSize });
    }
  }

  onSizeChange(value: string): void {
    const size = Number(value) || this.pageSize;
    this.pageChange.emit({ pageIndex: 0, pageSize: size });
  }
}

/** Cắt 1 trang từ mảng theo trạng thái phân trang (tự kéo về trang cuối nếu vượt) */
export function slicePage<T>(rows: T[], state: MiniPageState): T[] {
  const pageCount = Math.max(1, Math.ceil(rows.length / state.pageSize));
  const index = Math.min(state.pageIndex, pageCount - 1);
  const start = index * state.pageSize;
  return rows.slice(start, start + state.pageSize);
}
