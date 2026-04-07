import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { ScanRecord } from "../scan-to-wms.component";

@Component({
  selector: "jhi-mobile-detail-dialog",
  standalone: false,
  template: `
    <div class="mobile-dialog-wrapper">
      <div class="mobile-dialog-header">
        <button class="back-btn" (click)="close()">
          <mat-icon>chevron_left</mat-icon>
        </button>
        <span class="dialog-title">Tổng hợp theo WO</span>
        <span class="dialog-badge">{{ groupedRows.length }}</span>
      </div>

      <div class="mobile-dialog-body">
        <div class="wo-item" *ngFor="let row of groupedRows">
          <div class="wo-item-row">
            <span class="wo-code">{{ row.woCode }}</span>
            <span class="wo-qty">SL: {{ row.soLuong }}</span>
          </div>
          <div class="wo-item-row">
            <span class="wo-masp">{{ row.maSp }}</span>
          </div>
          <div class="wo-tensp">{{ row.tenSp }}</div>
        </div>

        <div class="empty-state" *ngIf="!groupedRows.length">
          Không có dữ liệu
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .mobile-dialog-wrapper {
        display: flex;
        flex-direction: column;
        height: 100vh;
        background: #f5f5f5;
      }

      .mobile-dialog-header {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 12px 16px;
        background: #fff;
        border-bottom: 1px solid #e0e0e0;
        position: sticky;
        top: 0;
        z-index: 10;
      }

      .back-btn {
        background: none;
        border: none;
        cursor: pointer;
        display: flex;
        align-items: center;
        padding: 4px;
        color: #212121;
      }

      .dialog-title {
        font-size: 17px;
        font-weight: 700;
        color: #212121;
        flex: 1;
      }

      .dialog-badge {
        background: #e8eaf6;
        color: #3949ab;
        font-size: 13px;
        font-weight: 600;
        border-radius: 12px;
        padding: 2px 9px;
      }

      .mobile-dialog-body {
        flex: 1;
        overflow-y: auto;
        padding: 12px 16px;
        display: flex;
        flex-direction: column;
        gap: 10px;
      }

      .wo-item {
        background: #fff;
        border-radius: 8px;
        border: 1px solid #e0e0e0;
        padding: 12px 14px;
        display: flex;
        flex-direction: column;
        gap: 4px;
      }

      .wo-item-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }

      .wo-code {
        color: #1976d2;
        font-weight: 600;
        font-size: 14px;
      }

      .wo-qty {
        color: #424242;
        font-size: 13px;
        font-weight: 500;
      }

      .wo-masp {
        color: #616161;
        font-size: 13px;
      }

      .wo-tensp {
        color: #424242;
        font-size: 13px;
      }

      .empty-state {
        text-align: center;
        color: #9e9e9e;
        padding: 40px 0;
        font-size: 14px;
      }
    `,
  ],
})
export class MobileDetailDialogComponent {
  groupedRows: {
    woCode: string;
    maSp: string;
    tenSp: string;
    soLuong: number;
  }[] = [];

  constructor(
    public dialogRef: MatDialogRef<MobileDetailDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { record: ScanRecord },
  ) {
    this.groupedRows = this.buildGroupedRows();
  }

  close(): void {
    this.dialogRef.close();
  }

  private buildGroupedRows(): {
    woCode: string;
    maSp: string;
    tenSp: string;
    soLuong: number;
  }[] {
    const map = new Map<
      string,
      { maSp: string; tenSp: string; soLuong: number }
    >();

    for (const pallet of this.data.record.raw.inboundWMSPallets ?? []) {
      const info = pallet.warehouseNoteInfo;
      if (!info) {
        continue;
      }

      const key = info.work_order_code ?? "—";
      const qty =
        pallet.listBox?.reduce((acc, b) => acc + (b.quantity ?? 0), 0) ?? 0;
      const existing = map.get(key);

      if (existing) {
        existing.soLuong += qty;
      } else {
        map.set(key, {
          maSp: info.sap_code ?? "—",
          tenSp: info.sap_name ?? "—",
          soLuong: qty,
        });
      }
    }

    return Array.from(map.entries()).map(([woCode, val]) => ({
      woCode,
      ...val,
    }));
  }
}
