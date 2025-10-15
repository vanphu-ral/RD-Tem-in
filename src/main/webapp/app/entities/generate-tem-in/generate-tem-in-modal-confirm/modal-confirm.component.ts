import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";

@Component({
  selector: "jhi-jhi-generate-tem-in-confirm-dialog",
  template: `
    <h2 mat-dialog-title>{{ data.title }}</h2>
    <mat-dialog-content>{{ data.message }}</mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Hủy</button>
      <button mat-raised-button color="primary" (click)="onConfirm()">
        Xác nhận
      </button>
    </mat-dialog-actions>
  `,
})
export class GenerateTemInConfirmDialogComponent {
  constructor(
    private dialogRef: MatDialogRef<GenerateTemInConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { title?: string; message?: string },
  ) {}

  onConfirm(): void {
    this.dialogRef.close(true);
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
