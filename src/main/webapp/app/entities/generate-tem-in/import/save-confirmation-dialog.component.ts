import { Component, Inject } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";

export interface SaveConfirmationData {
  groupName: string;
  totalGroups: number;
}

@Component({
  selector: "jhi-save-confirmation-dialog",
  template: `
    <h2 mat-dialog-title>Xác nhận lưu thông tin</h2>
    <mat-dialog-content>
      <p>
        File import có <strong>{{ data.totalGroups }}</strong> nhóm PO khác
        nhau.
      </p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">
        <mat-icon>close</mat-icon>
        Hủy
      </button>
      <button mat-raised-button color="primary" (click)="onSaveSingle()">
        <mat-icon>save</mat-icon>
        Lưu bảng này
      </button>
      <button mat-raised-button color="primary" (click)="onSaveAll()">
        <mat-icon>save_alt</mat-icon>
        Lưu tất cả
      </button>
    </mat-dialog-actions>
  `,
  styles: [
    `
      .info-box {
        background-color: #f5f5f5;
        padding: 16px;
        border-radius: 8px;
        margin-top: 16px;
      }
      .info-box p {
        margin: 8px 0;
      }
      mat-dialog-actions {
        gap: 8px;
      }
      mat-dialog-actions button {
        display: flex;
        align-items: center;
        gap: 8px;
      }
    `,
  ],
})
export class SaveConfirmationDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<SaveConfirmationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SaveConfirmationData,
  ) {}

  onCancel(): void {
    this.dialogRef.close(null);
  }

  onSaveSingle(): void {
    this.dialogRef.close("single");
  }

  onSaveAll(): void {
    this.dialogRef.close("all");
  }
}
