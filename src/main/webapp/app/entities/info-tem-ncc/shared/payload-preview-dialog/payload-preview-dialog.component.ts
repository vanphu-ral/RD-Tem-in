import { ChangeDetectionStrategy, Component, Inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";

export interface PayloadPreviewDialogData {
  title: string;
  /** Ví dụ: "POST /api/post-goods-receipt-po" */
  endpoint: string;
  /** Mô tả ngắn: số thùng, file... */
  note?: string;
  /** Nội dung hiển thị (JSON đã format hoặc CSV) */
  content: string;
}

/** Xem trước payload sẽ gửi (chế độ kiểm tra — không gọi API gửi) */
@Component({
  selector: "jhi-payload-preview-dialog",
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <h2 mat-dialog-title>{{ data.title }}</h2>
    <mat-dialog-content>
      <div class="pp-endpoint">{{ data.endpoint }}</div>
      <div class="pp-note" *ngIf="data.note">{{ data.note }}</div>
      <pre class="pp-content">{{ data.content }}</pre>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button type="button" (click)="copy()">
        <mat-icon>content_copy</mat-icon>
        {{ copied ? "Đã sao chép" : "Sao chép" }}
      </button>
      <button
        mat-raised-button
        color="primary"
        type="button"
        (click)="dialogRef.close()"
      >
        Đóng
      </button>
    </mat-dialog-actions>
  `,
  styles: [
    `
      .pp-endpoint {
        display: inline-block;
        margin-bottom: 6px;
        padding: 2px 8px;
        border-radius: 6px;
        background: #eef2ff;
        color: #3730a3;
        font-family: monospace;
        font-size: 12px;
      }
      .pp-note {
        margin-bottom: 8px;
        font-size: 12px;
        color: #6b7280;
      }
      .pp-content {
        margin: 0;
        max-height: 60vh;
        overflow: auto;
        padding: 10px 12px;
        border-radius: 8px;
        background: #0f172a;
        color: #e2e8f0;
        font-size: 12px;
        line-height: 1.45;
        white-space: pre;
      }
    `,
  ],
})
export class PayloadPreviewDialogComponent {
  copied = false;

  constructor(
    public dialogRef: MatDialogRef<PayloadPreviewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PayloadPreviewDialogData,
  ) {}

  copy(): void {
    void navigator.clipboard
      ?.writeText(this.data.content)
      .then(() => {
        this.copied = true;
        this.dialogRef.componentRef?.changeDetectorRef.markForCheck();
      })
      .catch(() => undefined);
  }
}
