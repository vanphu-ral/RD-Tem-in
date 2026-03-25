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
        <button mat-icon-button (click)="dialogRef.close()">
          <mat-icon>close</mat-icon>
        </button>
      </div>
      <mat-divider></mat-divider>
      <div class="list-view-body">
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
      }
      .list-view-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 16px 20px;
        h2 {
          margin: 0;
          font-size: 16px;
          font-weight: 600;
        }
      }
      .list-view-body {
        flex: 1;
        overflow: hidden;
        padding: 12px 20px;
      }
      .list-view-footer {
        display: flex;
        justify-content: flex-end;
        padding: 12px 20px;
      }
      .table-scroll-wrapper {
        overflow: auto;
        height: 100%;
        &::-webkit-scrollbar {
          width: 6px;
          height: 6px;
        }
        &::-webkit-scrollbar-thumb {
          background: #ddd;
          border-radius: 3px;
        }
      }
      .scan-table {
        width: 100%;
        border-collapse: collapse;
        font-size: 13px;
        white-space: nowrap;
        thead tr {
          position: sticky;
          top: 0;
          background: #f1f3f4;
          z-index: 2;
          th {
            padding: 8px 10px;
            font-weight: 600;
            font-size: 12px;
            color: #5f6368;
            border-bottom: 2px solid #dadce0;
          }
        }
        tbody tr {
          border-bottom: 1px solid #e8eaed;
          &:hover {
            background: #f8f9fa;
          }
          td {
            padding: 7px 10px;
          }
        }
        .col-stt {
          width: 36px;
          text-align: center;
          color: #9aa0a6;
        }
        .col-time {
          min-width: 80px;
          color: #9aa0a6;
          font-size: 11px;
        }
        .cell-text {
          display: inline-block;
          max-width: 200px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          font-family: "Courier New", monospace;
          font-size: 12px;
        }
      }
    `,
  ],
})
export class ScanListViewDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ScanListViewDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: { scannedList: ScannedItem[]; lotColumns: any[] },
  ) {}
}
