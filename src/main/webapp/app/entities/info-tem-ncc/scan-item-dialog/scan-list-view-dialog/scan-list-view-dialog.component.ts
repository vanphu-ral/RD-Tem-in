import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { ScannedItem } from "../scan-item-dialog.component";

@Component({
  selector: "jhi-scan-list-view-dialog",
  standalone: false,
  template: `
    <div class="list-view-wrapper">
      <div class="list-view-header">
        <h2>Danh sách đã scan ({{ data.scannedList.length }})</h2>
        <button class="close-btn" mat-icon-button (click)="dialogRef.close()">
          <mat-icon>close</mat-icon>
        </button>
      </div>
      <mat-divider></mat-divider>
      <div class="list-view-body">
        <div class="failed-section" *ngIf="(data.failedItems || []).length > 0">
          <div class="failed-header">
            <mat-icon>error_outline</mat-icon>
            {{ data.failedItems?.length }} mã lưu thất bại — scan lại các mã
            sau:
          </div>
          <div class="table-scroll-wrapper" style="max-height: 160px">
            <table class="scan-table">
              <thead>
                <tr>
                  <th>#</th>
                  <th>ReelId</th>
                  <th>Part Number</th>
                  <th>Lot</th>
                  <th>Quantity</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  *ngFor="let item of data.failedItems; let i = index"
                  class="row-error"
                >
                  <td class="col-stt">{{ i + 1 }}</td>
                  <td>
                    <span class="cell-text" [title]="item.reelId">{{
                      item.reelId
                    }}</span>
                  </td>
                  <td>
                    <span class="cell-text">{{ item.partNumber }}</span>
                  </td>
                  <td>
                    <span class="cell-text">{{ item.lot }}</span>
                  </td>
                  <td>{{ item.initialQuantity }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div class="table-scroll-wrapper">
          <table class="scan-table">
            <thead>
              <tr>
                <th class="col-stt">#</th>
                <th class="col-time">Thời gian</th>
                <th
                  *ngFor="let col of data.lotColumns"
                  [style.min-width.px]="col.minWidth"
                >
                  {{ col.label }}
                </th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let item of data.scannedList; let i = index">
                <td class="col-stt">{{ i + 1 }}</td>
                <td class="col-time">
                  {{ item.createdAt | date: "HH:mm:ss" }}
                </td>
                <td *ngFor="let col of data.lotColumns">
                  <span class="cell-text" [title]="$any(item)[col.key]">
                    {{ $any(item)[col.key] }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <mat-divider></mat-divider>
      <div class="list-view-footer">
        <button mat-raised-button color="primary" (click)="dialogRef.close()">
          Đóng
        </button>
      </div>
    </div>
  `,
  styles: [
    `
      .list-view-wrapper {
        display: flex;
        flex-direction: column;
        height: 90vh;
        background: #fff;
        border-radius: 12px;
        overflow: hidden;
      }
      .close-btn {
        padding: 5px 0px;
      }
      .failed-section {
        border-bottom: 1px solid #f5c6c2;
        background: #fff8f8;
        flex-shrink: 0;
      }

      .failed-header {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 8px 16px;
        font-size: 12px;
        font-weight: 600;
        color: #c5221f;

        mat-icon {
          font-size: 16px;
          width: 16px;
          height: 16px;
        }
      }

      .row-error {
        background: #fff8f8 !important;

        td {
          color: #c5221f !important;
        }
      }
      .list-view-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 14px 20px;
        border-bottom: 1px solid #e8eaed;
        flex-shrink: 0;

        h2 {
          margin: 0;
          font-size: 14px;
          font-weight: 600;
          color: #202124;
        }

        button {
          color: #70757a;
          width: 30px;
          height: 30px;

          mat-icon {
            font-size: 18px;
          }
        }
      }

      .list-view-body {
        flex: 1;
        overflow: hidden;
        padding: 0;
      }

      .list-view-footer {
        display: flex;
        justify-content: flex-end;
        padding: 10px 16px;
        border-top: 1px solid #e8eaed;
        flex-shrink: 0;

        button {
          height: 34px;
          font-size: 13px;
          border-radius: 6px;
        }
      }

      .table-scroll-wrapper {
        overflow: auto;
        height: 100%;

        &::-webkit-scrollbar {
          width: 5px;
          height: 5px;
        }

        &::-webkit-scrollbar-thumb {
          background: #dadce0;
          border-radius: 3px;
        }
      }

      .scan-table {
        width: 100%;
        border-collapse: collapse;
        font-size: 12px;
        white-space: nowrap;

        thead tr {
          position: sticky;
          top: 0;
          background: #f8f9fa;
          z-index: 2;

          th {
            padding: 7px 10px;
            font-weight: 600;
            font-size: 11px;
            color: #5f6368;
            border-bottom: 1.5px solid #e8eaed;
            text-align: left;
            white-space: nowrap;
          }
        }

        tbody tr {
          border-bottom: 1px solid #f1f3f4;

          &:hover {
            background: #f8f9fa;
          }

          td {
            padding: 6px 10px;
            color: #202124;
            vertical-align: middle;
          }
        }

        .col-stt {
          width: 32px;
          text-align: center;
          color: #9aa0a6;
          font-size: 11px;
          font-weight: 600;
        }

        .col-time {
          min-width: 70px;
          color: #9aa0a6;
          font-size: 11px;
        }

        .cell-text {
          display: inline-block;
          max-width: 180px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          // font-family: 'Courier New', monospace;
          font-size: 14px;
          color: #000;
        }
      }
    `,
  ],
})
export class ScanListViewDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ScanListViewDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      scannedList: ScannedItem[];
      failedItems?: ScannedItem[];
      lotColumns: any[];
    },
  ) {}
}
