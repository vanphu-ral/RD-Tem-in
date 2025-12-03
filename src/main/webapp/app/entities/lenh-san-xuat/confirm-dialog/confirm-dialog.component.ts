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
