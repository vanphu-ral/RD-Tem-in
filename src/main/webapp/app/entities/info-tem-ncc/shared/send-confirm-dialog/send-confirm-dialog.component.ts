import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Inject,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatIconModule } from "@angular/material/icon";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { combineLatest, Observable, of } from "rxjs";
import { catchError, map, take } from "rxjs/operators";
import { AccountService } from "app/core/auth/account.service";
import { resolveHttpErrorMessage } from "app/entities/generate-tem-in/service/receiving-supplies.service";
import { toText } from "../../services/info-tem-ncc.service";
import {
  SapTokenProfile,
  SsoUserService,
} from "../../services/sso-user.service";
import {
  IQC_WHS_CODE,
  SapSendStage,
  SapWarehouseSendResult,
  SendBoxEntry,
  VendorLabelSendService,
} from "../../services/vendor-label-send.service";

/** Kết quả khi bấm Xác nhận gửi */
export interface SendConfirmResult {
  /** Gửi SAP: dialog đã chuyển kho IQC + gửi xong → kết quả; PanaCIM: không có (bên gọi tự gửi) */
  sapResult?: SapWarehouseSendResult;
}

/**
 * Mở modal xác nhận gửi kèm thống kê: số thùng, số LOT (theo mã SAP + lot),
 * tổng số lượng của các thùng sẽ gửi, số thùng đã gửi bị bỏ qua → kết quả nếu Xác nhận
 * (null nếu Hủy).
 * Gửi SAP: người gửi = user đang đăng nhập (sapUser, whsCode lấy từ token); chỉ gửi vật tư kho
 * của mình + vật tư IQC được chọn (chuyển về kho mình); dialog tự chuyển kho + gửi rồi trả kết
 * quả (sapResult).
 * scopeLabel: tiêu đề phạm vi gửi thay cho "PO …" (vd gửi cả đơn); các thùng thuộc
 * nhiều PO → hiện thêm bảng số thùng / số lượng theo từng PO (PanaCIM).
 */
export function openSendConfirm(
  dialog: MatDialog,
  target: string,
  poCode: string,
  entries: SendBoxEntry[],
  skippedCount: number,
  scopeLabel?: string,
): Observable<SendConfirmResult | null> {
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
    entries,
    boxCount: entries.length,
    lotCount: lots.size,
    totalQty: entries.reduce(
      (s, e) => s + Number(e.record.initialQuantity ?? 0),
      0,
    ),
    skippedCount,
  };
  return dialog
    .open<SendConfirmDialogComponent, SendConfirmDialogData, SendConfirmResult>(
      SendConfirmDialogComponent,
      {
        width: "92vw",
        maxWidth: target === "SAP" ? "980px" : "420px",
        autoFocus: false,
        data,
      },
    )
    .afterClosed()
    .pipe(map((result) => result ?? null));
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
  /** Các thùng chưa gửi (gửi SAP: chọn theo vật tư rồi gửi ngay trong dialog) */
  entries: SendBoxEntry[];
}

export interface SendConfirmPoRow {
  poCode: string;
  boxCount: number;
  totalQty: number;
}

/** Gửi SAP: 1 dòng = 1 vật tư (dòng PO) */
interface SapMaterialRow {
  key: string;
  poCode: string;
  sapCode: string;
  materialName: string;
  whsCode: string;
  boxCount: number;
  totalQty: number;
  entries: SendBoxEntry[];
  /** own: kho của mình · iqc: kho chung (chọn giữ IQC / chuyển về kho mình) · other: kho khác, khoá */
  kind: "own" | "iqc" | "other";
  selected: boolean;
  /** Kho gửi SAP: own = kho mình; iqc = IQC (mặc định, giữ nguyên) hoặc kho mình (chuyển kho) */
  targetWhs: string;
}

/** Modal xác nhận gửi SAP / PanaCIM kèm thống kê lần gửi */
@Component({
  selector: "jhi-send-confirm-dialog",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatCheckboxModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
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

        <!-- ===== PanaCIM ===== -->
        <ng-container *ngIf="!isSap">
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
          <table class="sc-table" *ngIf="data.poRows.length">
            <thead>
              <tr>
                <th>PO ({{ data.poRows.length }})</th>
                <th class="num">Thùng</th>
                <th class="num">Số lượng</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let r of data.poRows">
                <td>{{ r.poCode }}</td>
                <td class="num">{{ r.boxCount | number }}</td>
                <td class="num">{{ r.totalQty | number }}</td>
              </tr>
            </tbody>
          </table>
        </ng-container>

        <!-- ===== SAP: chọn vật tư theo kho của người gửi ===== -->
        <ng-container *ngIf="isSap">
          <p class="sc-info" *ngIf="myWhs === null">
            <mat-spinner diameter="16"></mat-spinner>
            Đang tải kho của tài khoản…
          </p>
          <p class="sc-block" *ngIf="myWhs === ''">
            <mat-icon>block</mat-icon>
            Tài khoản của bạn chưa được gán kho nên không gửi SAP được. Liên hệ
            quản trị hệ thống để được gán kho.
          </p>

          <ng-container *ngIf="myWhs">
            <p class="sc-whs-note">
              Kho của bạn: <strong>{{ myWhs }}</strong
              >. Chỉ gửi được vật tư kho {{ myWhs }} và vật tư kho chung
              {{ iqc }} (chọn giữ kho {{ iqc }} hoặc chuyển về kho {{ myWhs }}).
              Mỗi kho gửi thành 1 phiếu SAP riêng.
            </p>
            <table class="sc-table sc-mat-table">
              <thead>
                <tr>
                  <th class="sc-check">
                    <mat-checkbox
                      color="primary"
                      [checked]="allSelected"
                      [indeterminate]="someSelected"
                      [disabled]="sending || !selectableRows.length"
                      (change)="toggleAll($event.checked)"
                    ></mat-checkbox>
                  </th>
                  <th>PO</th>
                  <th>Mã vật tư</th>
                  <th>Tên vật tư</th>
                  <th>Kho</th>
                  <th class="num">Thùng</th>
                  <th class="num">Số lượng</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  *ngFor="let r of rows"
                  [class.sc-row-other]="r.kind === 'other'"
                  [class.sc-row-on]="r.selected"
                >
                  <td class="sc-check">
                    <mat-checkbox
                      color="primary"
                      [(ngModel)]="r.selected"
                      [ngModelOptions]="{ standalone: true }"
                      [disabled]="sending || r.kind === 'other'"
                    ></mat-checkbox>
                  </td>
                  <td>{{ r.poCode || "—" }}</td>
                  <td class="sc-mono">{{ r.sapCode }}</td>
                  <td class="sc-name">{{ r.materialName || "—" }}</td>
                  <td>
                    <ng-container [ngSwitch]="r.kind">
                      <span *ngSwitchCase="'own'" class="sc-whs own">{{
                        r.whsCode
                      }}</span>
                      <select
                        *ngSwitchCase="'iqc'"
                        class="sc-whs-select"
                        [class.moving]="r.targetWhs !== iqc"
                        [(ngModel)]="r.targetWhs"
                        [ngModelOptions]="{ standalone: true }"
                        [disabled]="sending || !r.selected"
                      >
                        <option [ngValue]="iqc">Giữ kho {{ iqc }}</option>
                        <option [ngValue]="myWhs">
                          {{ iqc }} → kho {{ myWhs }}
                        </option>
                      </select>
                      <span *ngSwitchDefault class="sc-whs other">
                        {{ r.whsCode || "(trống)" }} · không thuộc kho của bạn
                      </span>
                    </ng-container>
                  </td>
                  <td class="num">{{ r.boxCount | number }}</td>
                  <td class="num">{{ r.totalQty | number }}</td>
                </tr>
              </tbody>
            </table>

            <div class="sc-stats sc-stats-sap">
              <div class="sc-stat">
                <span class="sc-val">{{ selectedRows.length | number }}</span>
                <span class="sc-lbl">Vật tư gửi</span>
              </div>
              <div class="sc-stat">
                <span class="sc-val">{{ selectedBoxCount | number }}</span>
                <span class="sc-lbl">Thùng</span>
              </div>
              <div class="sc-stat">
                <span class="sc-val">{{ selectedQty | number }}</span>
                <span class="sc-lbl">Tổng số lượng</span>
              </div>
              <div class="sc-stat">
                <span class="sc-val">{{ movingCount | number }}</span>
                <span class="sc-lbl">Chuyển {{ iqc }} → {{ myWhs }}</span>
              </div>
            </div>
            <p class="sc-whs-note sc-req-note" *ngIf="requestGroups.length">
              Sẽ gửi <strong>{{ requestGroups.length }}</strong> phiếu SAP:
              <ng-container *ngFor="let g of requestGroups; let last = last">
                kho <strong>{{ g.whsCode }}</strong> ({{ g.count }} vật tư){{
                  last ? "" : ","
                }}
              </ng-container>
            </p>

            <div class="sc-user">
              Người gửi: <strong>{{ senderLabel }}</strong>
              <ng-container *ngIf="sapUser">
                · Tài khoản SAP: <strong>{{ sapUser }}</strong>
              </ng-container>
            </div>
            <p class="sc-block" *ngIf="!sapUser">
              <mat-icon>error</mat-icon>
              Tài khoản đang đăng nhập không có thông tin tài khoản SAP nên
              không gửi SAP được. Liên hệ quản trị hệ thống để được gán tài
              khoản SAP.
            </p>
          </ng-container>

          <div class="sc-progress" *ngIf="sending">
            <mat-spinner diameter="20"></mat-spinner>
            {{
              stage === "moving"
                ? "Đang chuyển " +
                  movingCount +
                  " vật tư " +
                  iqc +
                  " sang kho " +
                  myWhs +
                  "…"
                : "Đang gửi SAP…"
            }}
          </div>
          <p class="sc-error" *ngIf="errorMsg">{{ errorMsg }}</p>
        </ng-container>

        <p class="sc-skip" *ngIf="data.skippedCount > 0">
          <mat-icon>info</mat-icon>
          Bỏ qua {{ data.skippedCount | number }} thùng đã gửi
          {{ data.target }}.
        </p>
      </mat-dialog-content>
      <mat-dialog-actions align="end">
        <button
          mat-button
          type="button"
          [disabled]="sending"
          (click)="dialogRef.close()"
        >
          Hủy
        </button>
        <button
          mat-raised-button
          color="primary"
          type="button"
          [disabled]="isSap && !canSendSap"
          (click)="confirm()"
        >
          {{ sending ? "Đang gửi..." : "Xác nhận gửi" }}
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
      .sc-stats-sap {
        grid-template-columns: repeat(4, minmax(0, 1fr));
        margin-top: 12px;
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
      .sc-table {
        width: 100%;
        margin-top: 12px;
        border-collapse: collapse;
        font-size: 12px;
      }
      .sc-table th,
      .sc-table td {
        padding: 5px 8px;
        border-bottom: 1px solid #e5e7eb;
        text-align: left;
      }
      .sc-table th.num,
      .sc-table td.num {
        text-align: right;
      }
      .sc-table th {
        color: #6b7280;
        font-weight: 600;
        background: #f9fafb;
      }
      .sc-check {
        width: 40px;
      }
      .sc-mono {
        font-family: "SFMono-Regular", Consolas, monospace;
      }
      .sc-name {
        min-width: 180px;
        white-space: normal;
        word-break: break-word;
      }
      .sc-row-on {
        background: #f0f9ff;
      }
      .sc-row-other td {
        color: #9ca3af;
      }
      .sc-whs {
        display: inline-flex;
        align-items: center;
        gap: 4px;
        padding: 1px 8px;
        border-radius: 999px;
        font-size: 11px;
        font-weight: 600;
      }
      .sc-whs mat-icon {
        width: 14px;
        height: 14px;
        font-size: 14px;
      }
      .sc-whs.own {
        background: #dcfce7;
        color: #15803d;
      }
      .sc-whs.iqc {
        background: #fef3c7;
        color: #b45309;
      }
      .sc-whs-select {
        padding: 2px 6px;
        border: 1px solid #fcd34d;
        border-radius: 6px;
        background: #fef3c7;
        color: #b45309;
        font-size: 11px;
        font-weight: 600;
      }
      .sc-whs-select.moving {
        border-color: #86efac;
        background: #dcfce7;
        color: #15803d;
      }
      .sc-whs-select:disabled {
        opacity: 0.6;
      }
      .sc-req-note {
        margin-top: 8px;
      }
      .sc-whs.other {
        background: #f3f4f6;
        color: #6b7280;
        font-weight: 500;
      }
      .sc-whs-note,
      .sc-info {
        margin: 0;
        font-size: 12px;
        color: #4b5563;
      }
      .sc-info,
      .sc-progress {
        display: flex;
        align-items: center;
        gap: 8px;
      }
      .sc-block {
        display: flex;
        gap: 6px;
        margin: 0;
        padding: 10px 12px;
        border-radius: 8px;
        background: #fef2f2;
        color: #b91c1c;
        font-size: 13px;
      }
      .sc-progress {
        margin-top: 12px;
        padding: 10px 12px;
        border-radius: 8px;
        background: #eff6ff;
        color: #1d4ed8;
        font-size: 13px;
        font-weight: 600;
      }
      .sc-error {
        margin: 8px 0 0;
        font-size: 12px;
        color: #b91c1c;
      }
      .sc-skip {
        margin: 12px 0 0;
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 12px;
        color: #b45309;
      }
      .sc-skip mat-icon,
      .sc-block mat-icon {
        font-size: 16px;
        width: 16px;
        height: 16px;
        flex-shrink: 0;
      }
      .sc-user {
        margin: 14px 0 8px;
        font-size: 13px;
        color: #374151;
      }
    `,
  ],
})
export class SendConfirmDialogComponent {
  readonly isSap: boolean;
  readonly iqc = IQC_WHS_CODE;
  /** Kho của người đăng nhập (token): null = đang tải, "" = chưa gán kho (chặn gửi SAP) */
  myWhs: string | null = null;
  /** Tài khoản SAP của người đăng nhập (token) — trống thì chặn gửi SAP */
  sapUser = "";
  /** "mã user - họ tên" của người đăng nhập (người gửi) */
  senderLabel = "";
  /** Gửi SAP: vật tư theo dòng PO */
  rows: SapMaterialRow[] = [];
  sending = false;
  stage: SapSendStage | null = null;
  errorMsg = "";

  constructor(
    public dialogRef: MatDialogRef<
      SendConfirmDialogComponent,
      SendConfirmResult
    >,
    @Inject(MAT_DIALOG_DATA) public data: SendConfirmDialogData,
    ssoUserService: SsoUserService,
    accountService: AccountService,
    private sendService: VendorLabelSendService,
    private cdr: ChangeDetectorRef,
  ) {
    this.isSap = data.target === "SAP";
    if (!this.isSap) {
      return;
    }
    // Người gửi = user đang đăng nhập; sapUser + kho lấy từ token → dựng bảng vật tư
    combineLatest([
      accountService.identity().pipe(take(1)),
      ssoUserService
        .getCurrentSapProfile()
        .pipe(
          catchError(() => of<SapTokenProfile>({ sapUser: "", whsCode: "" })),
        ),
    ])
      .pipe(take(1))
      .subscribe(([account, profile]) => {
        const name = [account?.firstName, account?.lastName]
          .filter(Boolean)
          .join(" ");
        const login = account?.login ?? "";
        this.senderLabel = name ? `${login} - ${name}` : login;
        this.sapUser = profile.sapUser;
        this.myWhs = profile.whsCode;
        this.rows = this.myWhs ? this.buildRows(this.myWhs) : [];
        this.cdr.markForCheck();
      });
  }

  get selectableRows(): SapMaterialRow[] {
    return this.rows.filter((r) => r.kind !== "other");
  }

  get selectedRows(): SapMaterialRow[] {
    return this.rows.filter((r) => r.selected);
  }

  get allSelected(): boolean {
    const rows = this.selectableRows;
    return rows.length > 0 && rows.every((r) => r.selected);
  }

  get someSelected(): boolean {
    return this.selectedRows.length > 0 && !this.allSelected;
  }

  get selectedBoxCount(): number {
    return this.selectedRows.reduce((s, r) => s + r.boxCount, 0);
  }

  get selectedQty(): number {
    return this.selectedRows.reduce((s, r) => s + r.totalQty, 0);
  }

  /** Số vật tư IQC được chọn — sẽ chuyển về kho của mình */
  get movingCount(): number {
    return this.movingRows.length;
  }

  /** Các phiếu SAP sẽ gửi: gom vật tư đã chọn theo kho gửi (kho mình / IQC) */
  get requestGroups(): { whsCode: string; count: number }[] {
    const byWhs = new Map<string, number>();
    for (const r of this.selectedRows) {
      byWhs.set(r.targetWhs, (byWhs.get(r.targetWhs) ?? 0) + 1);
    }
    return [...byWhs].map(([whsCode, count]) => ({ whsCode, count }));
  }

  /** Vật tư IQC được chọn chuyển về kho của mình */
  private get movingRows(): SapMaterialRow[] {
    return this.selectedRows.filter(
      (r) => r.kind === "iqc" && r.targetWhs !== IQC_WHS_CODE,
    );
  }

  get canSendSap(): boolean {
    return (
      !!this.myWhs &&
      !this.sending &&
      !!this.sapUser &&
      this.selectedRows.length > 0
    );
  }

  toggleAll(checked: boolean): void {
    for (const r of this.selectableRows) {
      r.selected = checked;
    }
  }

  confirm(): void {
    if (!this.isSap) {
      this.dialogRef.close({});
      return;
    }
    if (!this.canSendSap) {
      return;
    }
    const entries = this.selectedRows.flatMap((r) => r.entries);
    const moveLineIds = new Set(
      this.movingRows
        .flatMap((r) => r.entries.map((e) => e.record.sapPor1Id ?? null))
        .filter((id): id is number => id !== null),
    );
    this.sending = true;
    this.stage = null;
    this.errorMsg = "";
    this.dialogRef.disableClose = true;
    this.sendService
      .sendSapToWarehouse(
        entries,
        this.myWhs!,
        this.sapUser,
        moveLineIds,
        (stage) => {
          this.stage = stage;
          this.cdr.markForCheck();
        },
      )
      .subscribe({
        next: (res) => {
          this.dialogRef.disableClose = false;
          this.dialogRef.close({ sapResult: res });
        },
        error: (err: unknown) => {
          this.sending = false;
          this.stage = null;
          this.dialogRef.disableClose = false;
          this.errorMsg = resolveHttpErrorMessage(err, "Gửi SAP thất bại.");
          this.cdr.markForCheck();
        },
      });
  }

  /** Gom thùng theo dòng vật tư (sapPor1Id) — kho mình chọn sẵn, IQC để người dùng chọn */
  private buildRows(myWhs: string): SapMaterialRow[] {
    const byLine = new Map<string, SapMaterialRow>();
    for (const e of this.data.entries) {
      const key = String(
        e.record.sapPor1Id ?? `${toText(e.poCode)}|${toText(e.sapCode)}`,
      );
      const whs = toText(e.whsCode);
      const kind: SapMaterialRow["kind"] =
        whs === myWhs ? "own" : whs === IQC_WHS_CODE ? "iqc" : "other";
      const row = byLine.get(key) ?? {
        key,
        poCode: toText(e.poCode),
        sapCode: toText(e.sapCode),
        materialName: toText(e.materialName),
        whsCode: whs,
        boxCount: 0,
        totalQty: 0,
        entries: [],
        kind,
        selected: kind === "own",
        targetWhs: kind === "own" ? myWhs : whs,
      };
      row.boxCount++;
      row.totalQty += Number(e.record.initialQuantity ?? 0);
      row.entries.push(e);
      byLine.set(key, row);
    }
    const order = { own: 0, iqc: 1, other: 2 };
    return [...byLine.values()].sort((a, b) => order[a.kind] - order[b.kind]);
  }
}
