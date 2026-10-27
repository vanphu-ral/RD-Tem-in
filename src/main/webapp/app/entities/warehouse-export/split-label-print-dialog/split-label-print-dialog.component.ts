import { Component, Inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { Observable } from "rxjs";
import { runLabelPrint } from "app/entities/generate-tem-in/receiving-supplies/receiving-label-print.util";
import { RdLabelComponent } from "../rd-label/rd-label.component";
import { SplitPrintLabel } from "../rd-label/rd-label.model";

const MODAL_ID = "splitLabelPrintModal";

/** Preview + in tem tách theo mẫu tem Rạng Đông (khổ 102.5 × 35 mm) */
@Component({
  selector: "jhi-split-label-print-dialog",
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    RdLabelComponent,
  ],
  templateUrl: "./split-label-print-dialog.component.html",
  styleUrls: ["./split-label-print-dialog.component.scss"],
})
export class SplitLabelPrintDialogComponent {
  readonly modalId = MODAL_ID;

  constructor(
    private dialogRef: MatDialogRef<SplitLabelPrintDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public labels: SplitPrintLabel[],
  ) {}

  print(): void {
    runLabelPrint(MODAL_ID, () => this.close());
  }

  close(): void {
    this.dialogRef.close();
  }

  trackByLabel(index: number, label: SplitPrintLabel): string {
    return `${label.id}-${index}`;
  }
}

/** Mở preview in tem RD → phát 1 lần khi dialog đóng */
export function openSplitLabelPrintDialog(
  dialog: MatDialog,
  labels: SplitPrintLabel[],
): Observable<unknown> {
  return dialog
    .open(SplitLabelPrintDialogComponent, {
      width: "100vw",
      height: "100vh",
      maxWidth: "100vw",
      maxHeight: "100vh",
      panelClass: "split-label-print-dialog-panel",
      autoFocus: false,
      data: labels,
    })
    .afterClosed();
}
