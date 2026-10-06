import { ChangeDetectionStrategy, Component, Inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { toText } from "../../services/info-tem-ncc.service";
import type { SendBoxEntry } from "../../services/vendor-label-send.service";

/**
 * Mở modal xác nhận gửi kèm thống kê: số thùng, số LOT (theo mã SAP + lot),
 * tổng số lượng của các thùng sẽ gửi, số thùng đã gửi bị bỏ qua → true nếu Xác nhận.
 * scopeLabel: tiêu đề phạm vi gửi thay cho "PO …" (vd gửi cả đơn); các thùng thuộc
 * nhiều PO → hiện thêm bảng số thùng / số lượng theo từng PO.
 */
export function openSendConfirm(
  dialog: MatDialog,
  target: string,
  poCode: string,
  entries: SendBoxEntry[],
  skippedCount: number,
  scopeLabel?: string,
): Observable<boolean> {
  const lots = new Set(
    entries.map((e) => `${toText(e.sapCode)}|${toText(e.record.lot)}`),
  );
  const byPo = new Map<string, SendConfirmPoRow>();
  for (const e of entries) {
    const code = toText(e.poCode) || "—";
    const row = byPo.get(code) ?? { poCode: code, boxCount: 0, totalQty: 0 };
    row.boxCount++;
    row.totalQty += Number(e.record.initialQuantity ?? 0);
    byPo.set(code, row);
  }
  const data: SendConfirmDialogData = {
    target,
    poCode,
    scopeLabel,
    poRows: byPo.size > 1 ? [...byPo.values()] : [],
    boxCount: entries.length,
    lotCount: lots.size,
    totalQty: entries.reduce(
      (s, e) => s + Number(e.record.initialQuantity ?? 0),
      0,
    ),
    skippedCount,
  };
  return dialog
    .open<SendConfirmDialogComponent, SendConfirmDialogData, boolean>(
      SendConfirmDialogComponent,
      {
        width: "92vw",
        maxWidth: "420px",
        autoFocus: false,
        data,
      },
    )
    .afterClosed()
    .pipe(map((result) => result === true));
}

export interface SendConfirmDialogData {
  /** "SAP" | "PanaCIM" */
  target: string;
  poCode: string;
  /** Số thùng sẽ gửi lần này (chưa gửi) */
  boxCount: number;
  /** Số LOT khác nhau trong các thùng sẽ gửi */
  lotCount: number;
  /** Tổng số lượng các thùng sẽ gửi */
  totalQty: number;
  /** Số thùng đã gửi trước đó — bỏ qua */
  skippedCount: number;
  /** Tiêu đề phạm vi gửi (vd "Cả đơn …") — không có thì hiện "PO {poCode}" */
  scopeLabel?: string;
  /** Thống kê theo PO — chỉ có khi các thùng thuộc nhiều PO */
  poRows: SendConfirmPoRow[];
}

export interface SendConfirmPoRow {
  poCode: string;
  boxCount: number;
  totalQty: number;
}

/** Modal xác nhận gửi SAP / PanaCIM kèm thống kê lần gửi */
@Component({
  selector: "jhi-send-confirm-dialog",
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="send-confirm">
      <h2 mat-dialog-title>Xác nhận gửi {{ data.target }}</h2>
      <mat-dialog-content>
        <p class="sc-sub">
          <ng-container *ngIf="data.scopeLabel; else poScope">
            <strong>{{ data.scopeLabel }}</strong>
          </ng-container>
          <ng-template #poScope>
            PO <strong>{{ data.poCode || "—" }}</strong>
          </ng-template>
          · chỉ gửi các thùng chưa gửi
          {{ data.target }}
        </p>
        <div class="sc-stats">
          <div class="sc-stat">
            <span class="sc-val">{{ data.boxCount | number }}</span>
            <span class="sc-lbl">Thùng</span>
          </div>
          <div class="sc-stat">
            <span class="sc-val">{{ data.lotCount | number }}</span>
            <span class="sc-lbl">LOT</span>
          </div>
          <div class="sc-stat">
            <span class="sc-val">{{ data.totalQty | number }}</span>
            <span class="sc-lbl">Tổng số lượng</span>
          </div>
        </div>
        <table class="sc-po-table" *ngIf="data.poRows.length">
          <thead>
            <tr>
              <th>PO ({{ data.poRows.length }})</th>
              <th>Thùng</th>
              <th>Số lượng</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let r of data.poRows">
              <td>{{ r.poCode }}</td>
              <td>{{ r.boxCount | number }}</td>
              <td>{{ r.totalQty | number }}</td>
            </tr>
          </tbody>
        </table>
        <p class="sc-skip" *ngIf="data.skippedCount > 0">
          <mat-icon>info</mat-icon>
          Bỏ qua {{ data.skippedCount | number }} thùng đã gửi
          {{ data.target }}.
        </p>
      </mat-dialog-content>
      <mat-dialog-actions align="end">
        <button mat-button type="button" (click)="dialogRef.close(false)">
          Hủy
        </button>
        <button
          mat-raised-button
          color="primary"
          type="button"
          (click)="dialogRef.close(true)"
        >
          Xác nhận gửi
        </button>
      </mat-dialog-actions>
    </div>
  `,
  styles: [
    `
      .sc-sub {
        margin: 0 0 12px;
        font-size: 13px;
        color: #4b5563;
      }
      .sc-stats {
        display: grid;
        grid-template-columns: repeat(3, minmax(0, 1fr));
        gap: 8px;
      }
      .sc-stat {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 2px;
        padding: 10px 6px;
        border-radius: 10px;
        background: #eff6ff;
        border: 1px solid #bfdbfe;
        min-width: 0;
      }
      .sc-val {
        font-size: 18px;
        font-weight: 700;
        color: #1d4ed8;
        word-break: break-all;
        text-align: center;
      }
      .sc-lbl {
        font-size: 11px;
        color: #6b7280;
        text-align: center;
      }
      .sc-po-table {
        width: 100%;
        margin-top: 12px;
        border-collapse: collapse;
        font-size: 12px;
      }
      .sc-po-table th,
      .sc-po-table td {
        padding: 5px 8px;
        border-bottom: 1px solid #e5e7eb;
        text-align: right;
      }
      .sc-po-table th:first-child,
      .sc-po-table td:first-child {
        text-align: left;
      }
      .sc-po-table th {
        color: #6b7280;
        font-weight: 600;
        background: #f9fafb;
      }
      .sc-skip {
        margin: 12px 0 0;
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 12px;
        color: #b45309;
      }
      .sc-skip mat-icon {
        font-size: 16px;
        width: 16px;
        height: 16px;
      }
    `,
  ],
})
export class SendConfirmDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<SendConfirmDialogComponent, boolean>,
    @Inject(MAT_DIALOG_DATA) public data: SendConfirmDialogData,
  ) {}
}
