import { Component, Inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from "@angular/material/dialog";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";

export interface ConfirmDialogData {
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  type?: "warning" | "danger" | "info";
}

@Component({
  selector: "jhi-confirm-dialog",
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule],
  template: `
    <div class="confirm-dialog">
      <div class="dialog-header" [ngClass]="data.type || 'warning'">
        <mat-icon>
          {{
            data.type === "danger"
              ? "warning"
              : data.type === "info"
                ? "info"
                : "help_outline"
          }}
        </mat-icon>
        <h2>{{ data.title }}</h2>
      </div>

      <div class="dialog-content">
        <p>{{ data.message }}</p>
      </div>

      <div class="dialog-actions">
        <button mat-button (click)="onCancel()">
          {{ data.cancelText || "Hủy" }}
        </button>
        <button
          mat-raised-button
          [color]="data.type === 'danger' ? 'warn' : 'primary'"
          (click)="onConfirm()"
        >
          {{ data.confirmText || "Xác nhận" }}
        </button>
      </div>
    </div>
  `,
  styles: [
    `
      .confirm-dialog {
        min-width: 400px;
        max-width: 600px;
        width: 100%; // cho phép co giãn theo màn hình
        box-sizing: border-box;
      }

      .dialog-header {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 20px;
        border-radius: 8px 8px 0 0;

        &.warning {
          background: #fff3cd;
          color: #856404;
        }

        &.danger {
          background: #f8d7da;
          color: #721c24;
        }

        &.info {
          background: #d1ecf1;
          color: #0c5460;
        }

        mat-icon {
          font-size: 32px;
          width: 32px;
          height: 32px;
        }

        h2 {
          margin: 0;
          font-size: 20px;
          font-weight: 600;
        }
      }

      .dialog-content {
        padding: 24px;

        p {
          margin: 0;
          font-size: 16px;
          line-height: 1.5;
          color: #333;
        }
      }

      .dialog-actions {
        display: flex;
        justify-content: flex-end;
        gap: 12px;
        padding: 16px 24px;
        border-top: 1px solid #e0e0e0;
      }
      @media screen and (max-width: 768px) {
        .confirm-dialog {
          min-width: auto;
          max-width: 100%; // chiếm 100% chiều ngang màn hình
          width: 100%;
        }

        .dialog-header {
          padding: 16px;
          h2 {
            font-size: 18px;
          }
          mat-icon {
            font-size: 28px;
          }
        }

        .dialog-content {
          padding: 16px;
          p {
            font-size: 14px;
          }
        }

        .dialog-actions {
          flex-direction: column; // nút xuống hàng
          align-items: stretch;
          gap: 8px;
          padding: 12px 16px;
          button {
            width: 100%;
          }
        }
      }

      /* Mobile nhỏ hơn 480px */
      @media screen and (max-width: 480px) {
        .confirm-dialog {
          max-width: 100%;
          width: 100%;
        }

        .dialog-header h2 {
          font-size: 16px;
        }

        .dialog-content p {
          font-size: 13px;
        }
      }
    `,
  ],
})
export class ConfirmDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmDialogData,
  ) {}

  onConfirm(): void {
    this.dialogRef.close(true);
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
