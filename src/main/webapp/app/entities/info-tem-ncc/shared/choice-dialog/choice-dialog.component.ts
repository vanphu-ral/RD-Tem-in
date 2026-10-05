import { ChangeDetectionStrategy, Component, Inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { MatIconModule } from "@angular/material/icon";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";

export type ChoiceDialogTone = "info" | "warning" | "danger";

export interface ChoiceDialogData {
  title: string;
  message: string;
  /** Phần nhấn mạnh (vd mã pallet) hiển thị riêng một dòng đậm */
  highlight?: string;
  confirmText: string;
  cancelText: string;
  /** Danh sách chi tiết (vd lý do) hiện dưới message */
  items?: string[];
  /** Nút lựa chọn thứ 3 (tùy chọn) — hiện giữa nút hủy và nút xác nhận */
  extraText?: string;
  tone?: ChoiceDialogTone;
}

/** Kết quả modal 3 lựa chọn */
export type ChoiceDialogResult = "confirm" | "extra" | "cancel";

/** Modal lựa chọn 2 nút — gọn, dễ bấm trên mobile */
@Component({
  selector: "jhi-choice-dialog",
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatIconModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="choice" [attr.data-tone]="tone">
      <div class="cd-icon">
        <mat-icon>{{ icon }}</mat-icon>
      </div>
      <h2 class="cd-title">{{ data.title }}</h2>
      <div class="cd-highlight" *ngIf="data.highlight">
        {{ data.highlight }}
      </div>
      <p class="cd-message">{{ data.message }}</p>
      <ul class="cd-items" *ngIf="data.items?.length">
        <li *ngFor="let item of data.items">{{ item }}</li>
      </ul>
      <div
        class="cd-actions"
        [class.cd-actions--three]="!!data.extraText"
        [class.cd-actions--one]="!data.cancelText && !data.extraText"
      >
        <button
          *ngIf="data.cancelText"
          type="button"
          class="cd-btn cd-cancel"
          (click)="close(false)"
        >
          {{ data.cancelText }}
        </button>
        <button
          *ngIf="data.extraText"
          type="button"
          class="cd-btn cd-extra"
          (click)="close('extra')"
        >
          {{ data.extraText }}
        </button>
        <button type="button" class="cd-btn cd-confirm" (click)="close(true)">
          {{ data.confirmText }}
        </button>
      </div>
    </div>
  `,
  styles: [
    `
      .choice {
        --tone: #2563eb;
        --tone-bg: #eff6ff;
        padding: 20px 18px 16px;
        text-align: center;
        font-family: Roboto, "Helvetica Neue", sans-serif;
      }
      .choice[data-tone="warning"] {
        --tone: #d97706;
        --tone-bg: #fffbeb;
      }
      .choice[data-tone="danger"] {
        --tone: #dc2626;
        --tone-bg: #fef2f2;
      }
      .cd-icon {
        width: 52px;
        height: 52px;
        margin: 0 auto 10px;
        border-radius: 50%;
        background: var(--tone-bg);
        color: var(--tone);
        display: flex;
        align-items: center;
        justify-content: center;
      }
      .cd-icon mat-icon {
        font-size: 28px;
        width: 28px;
        height: 28px;
      }
      .cd-title {
        margin: 0 0 8px;
        font-size: 17px;
        font-weight: 700;
        color: #111827;
        line-height: 1.3;
      }
      .cd-highlight {
        display: inline-block;
        max-width: 100%;
        margin-bottom: 8px;
        padding: 4px 10px;
        border-radius: 8px;
        background: #f3f4f6;
        color: #111827;
        font-family: monospace;
        font-size: 14px;
        font-weight: 700;
        word-break: break-all;
      }
      .cd-message {
        margin: 0 0 18px;
        font-size: 14px;
        line-height: 1.5;
        color: #4b5563;
      }
      .cd-actions {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 10px;
      }
      .cd-items {
        margin: -8px 0 16px;
        padding: 10px 12px 10px 28px;
        max-height: 40vh;
        overflow-y: auto;
        text-align: left;
        font-size: 13px;
        line-height: 1.5;
        color: #374151;
        background: #f9fafb;
        border-radius: 8px;
      }
      .cd-items li + li {
        margin-top: 4px;
      }
      /* Chỉ 1 nút (không có nút hủy) */
      .cd-actions--one {
        grid-template-columns: 1fr;
      }
      /* 3 nút: xếp dọc, nút chính lên đầu */
      .cd-actions--three {
        grid-template-columns: 1fr;
      }
      .cd-actions--three .cd-confirm {
        order: -2;
      }
      .cd-actions--three .cd-extra {
        order: -1;
      }
      .cd-btn {
        min-height: 46px;
        padding: 8px 10px;
        border-radius: 10px;
        font-size: 14px;
        font-weight: 600;
        line-height: 1.25;
        cursor: pointer;
      }
      .cd-cancel {
        border: 1.5px solid #d1d5db;
        background: #fff;
        color: #374151;
      }
      .cd-cancel:active {
        background: #f3f4f6;
      }
      .cd-extra {
        border: 1.5px solid var(--tone);
        background: var(--tone-bg);
        color: var(--tone);
      }
      .cd-extra:active {
        filter: brightness(0.95);
      }
      .cd-confirm {
        border: none;
        background: var(--tone);
        color: #fff;
      }
      .cd-confirm:active {
        filter: brightness(0.92);
      }
    `,
  ],
})
export class ChoiceDialogComponent {
  constructor(
    private dialogRef: MatDialogRef<ChoiceDialogComponent, boolean | "extra">,
    @Inject(MAT_DIALOG_DATA) public data: ChoiceDialogData,
  ) {}

  get tone(): ChoiceDialogTone {
    return this.data.tone ?? "info";
  }

  get icon(): string {
    if (this.tone === "danger") {
      return "delete_outline";
    }
    return this.tone === "warning" ? "inventory_2" : "help_outline";
  }

  close(result: boolean | "extra"): void {
    this.dialogRef.close(result);
  }
}

/** Mở modal lựa chọn → true nếu bấm nút xác nhận */
export function openChoiceDialog(
  dialog: MatDialog,
  data: ChoiceDialogData,
): Observable<boolean> {
  return dialog
    .open<ChoiceDialogComponent, ChoiceDialogData, boolean | "extra">(
      ChoiceDialogComponent,
      {
        width: "92vw",
        maxWidth: "380px",
        autoFocus: false,
        panelClass: "choice-dialog-panel",
        data,
      },
    )
    .afterClosed()
    .pipe(map((result) => result === true));
}

/** Mở modal 3 lựa chọn (có extraText) → "confirm" | "extra" | "cancel" */
export function openChoiceDialog3(
  dialog: MatDialog,
  data: ChoiceDialogData,
): Observable<ChoiceDialogResult> {
  return dialog
    .open<ChoiceDialogComponent, ChoiceDialogData, boolean | "extra">(
      ChoiceDialogComponent,
      {
        width: "92vw",
        maxWidth: "380px",
        autoFocus: false,
        panelClass: "choice-dialog-panel",
        data,
      },
    )
    .afterClosed()
    .pipe(
      map((result): ChoiceDialogResult => {
        if (result === "extra") {
          return "extra";
        }
        return result === true ? "confirm" : "cancel";
      }),
    );
}
