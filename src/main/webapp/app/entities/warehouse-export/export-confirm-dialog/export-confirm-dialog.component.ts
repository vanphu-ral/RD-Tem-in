import { Component, Inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";

/** 1 vật tư còn thiếu */
export interface ExportShortItem {
  materialCode: string;
  requiredQty: number;
  scannedQty: number;
}

/** 1 yêu cầu chưa đủ SL + các vật tư còn thiếu */
export interface ExportShortRequest {
  code: string;
  items: ExportShortItem[];
}

/** Popup "Không thể hoàn thành xuất kho" — chỉ được xuất khi scan đủ SL mọi yêu cầu */
export interface ExportIncompleteData {
  mode: "incomplete";
  requiredTotal: number;
  scannedTotal: number;
  requests: ExportShortRequest[];
}

/** Popup "Xác nhận hủy" — dữ liệu đã scan sẽ bị xóa */
export interface ExportCancelData {
  mode: "cancel";
  requestCodes: string[];
  boxCount: number;
  scannedQty: number;
}

export type ExportConfirmData = ExportIncompleteData | ExportCancelData;

/** Mở popup chặn hoàn thành (chỉ có nút "Quay lại scan") */
export function openExportIncomplete(
  dialog: MatDialog,
  data: Omit<ExportIncompleteData, "mode">,
): Observable<void> {
  return dialog
    .open(ExportConfirmDialogComponent, {
      width: "92vw",
      maxWidth: "620px",
      maxHeight: "90vh",
      autoFocus: false,
      panelClass: "export-confirm-dialog-panel",
      data: { mode: "incomplete", ...data } as ExportConfirmData,
    })
    .afterClosed()
    .pipe(map(() => undefined));
}

/** Mở popup xác nhận hủy → true nếu "Xác nhận hủy" */
export function openExportCancel(
  dialog: MatDialog,
  data: Omit<ExportCancelData, "mode">,
): Observable<boolean> {
  return dialog
    .open(ExportConfirmDialogComponent, {
      width: "92vw",
      maxWidth: "620px",
      maxHeight: "90vh",
      autoFocus: false,
      panelClass: "export-confirm-dialog-panel",
      data: { mode: "cancel", ...data } as ExportConfirmData,
    })
    .afterClosed()
    .pipe(map((result) => result === true));
}

@Component({
  selector: "jhi-export-confirm-dialog",
  standalone: true,
  imports: [CommonModule, MatDialogModule],
  templateUrl: "./export-confirm-dialog.component.html",
  styleUrls: ["./export-confirm-dialog.component.scss"],
})
export class ExportConfirmDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ExportConfirmDialogComponent, boolean>,
    @Inject(MAT_DIALOG_DATA) public data: ExportConfirmData,
  ) {}

  get incomplete(): ExportIncompleteData | null {
    return this.data.mode === "incomplete" ? this.data : null;
  }

  get cancel(): ExportCancelData | null {
    return this.data.mode === "cancel" ? this.data : null;
  }

  shortOf(i: ExportShortItem): number {
    return Math.max(0, i.requiredQty - i.scannedQty);
  }

  close(result: boolean): void {
    this.dialogRef.close(result);
  }
}
