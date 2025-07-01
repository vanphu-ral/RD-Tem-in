import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ListMaterialUpdateDialogComponent } from '../dialog/list-material-update-dialog';

export interface ConfirmDialogData {
  title?: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
}

@Component({
  selector: 'jhi-dialog-content-example-dialog',
  templateUrl: './confirm-dialog.component.html',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DialogContentExampleDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<DialogContentExampleDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmDialogData,
  ) {}

  onNoClick(): void {
    this.dialogRef.close(false);
  }
}
