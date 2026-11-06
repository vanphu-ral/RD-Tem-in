import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Inject,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  AbstractControl,
  FormControl,
  ReactiveFormsModule,
  ValidationErrors,
} from "@angular/forms";
import { MatAutocompleteModule } from "@angular/material/autocomplete";
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { combineLatest, Observable, of } from "rxjs";
import {
  catchError,
  distinctUntilChanged,
  map,
  shareReplay,
  startWith,
  take,
} from "rxjs/operators";
import { AccountService } from "app/core/auth/account.service";
import { toText } from "../../services/info-tem-ncc.service";
import { SsoUser, SsoUserService } from "../../services/sso-user.service";
import type { SendBoxEntry } from "../../services/vendor-label-send.service";

/** Kết quả khi bấm Xác nhận gửi */
export interface SendConfirmResult {
  /** sapUser của user được chọn (chỉ khi gửi SAP) → createdBy trong body gửi SAP; PanaCIM: "" */
  createdBy: string;
}

/** Số user tối đa hiện trong panel autocomplete */
const SSO_USER_LIMIT = 50;

/** "mã user - họ tên" (không có họ tên → chỉ mã user) */
export function ssoUserLabel(u: SsoUser | null | string): string {
  if (!u || typeof u === "string") {
    return u ?? "";
  }
  const name = ssoUserName(u);
  return name ? `${u.username} - ${name}` : u.username;
}

/** Họ tên user ("" nếu SSO không có họ tên) */
function ssoUserName(u: SsoUser): string {
  return [u.firstName, u.lastName].filter(Boolean).join(" ");
}

/** Chữ thường, bỏ dấu tiếng Việt — để tìm "nguyen" ra "Nguyễn" */
function normalize(s: string): string {
  return s
    .normalize("NFD")
    .replace(/\p{M}/gu, "")
    .replace(/đ/gi, "d")
    .toLowerCase();
}

/** Bắt buộc chọn 1 user trong danh sách (gõ chữ tự do không hợp lệ), user phải có sapUser */
function selectedUserValidator(c: AbstractControl): ValidationErrors | null {
  if (!c.value || typeof c.value !== "object") {
    return { required: true };
  }
  return (c.value as SsoUser).sapUser ? null : { noSapUser: true };
}

/**
 * Mở modal xác nhận gửi kèm thống kê: số thùng, số LOT (theo mã SAP + lot),
 * tổng số lượng của các thùng sẽ gửi, số thùng đã gửi bị bỏ qua → kết quả nếu Xác nhận
 * (null nếu Hủy). Gửi SAP: bắt buộc chọn user (createdBy), mặc định user đang đăng nhập.
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
        maxWidth: "420px",
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
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatAutocompleteModule,
    MatFormFieldModule,
    MatInputModule,
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
        <mat-form-field
          *ngIf="isSap"
          appearance="outline"
          class="sc-user"
          subscriptSizing="dynamic"
        >
          <mat-label>User gửi (người tạo)</mat-label>
          <input
            type="text"
            matInput
            [formControl]="userCtrl"
            [matAutocomplete]="userAuto"
            placeholder="Tìm theo mã user hoặc họ tên"
          />
          <mat-autocomplete
            #userAuto="matAutocomplete"
            [displayWith]="userLabel"
          >
            <mat-option *ngFor="let u of users$ | async" [value]="u">
              {{ userLabel(u) }}
            </mat-option>
          </mat-autocomplete>
          <mat-error *ngIf="userCtrl.hasError('required')">
            Chọn một user trong danh sách.
          </mat-error>
          <mat-error *ngIf="userCtrl.hasError('noSapUser')">
            User này chưa được khai báo tài khoản SAP (sap_user) trên SSO.
          </mat-error>
          <mat-hint *ngIf="loadError" class="sc-user-error">
            Không tải được danh sách user từ SSO.
          </mat-hint>
        </mat-form-field>
      </mat-dialog-content>
      <mat-dialog-actions align="end">
        <button mat-button type="button" (click)="dialogRef.close()">
          Hủy
        </button>
        <button
          mat-raised-button
          color="primary"
          type="button"
          [disabled]="isSap && userCtrl.invalid"
          (click)="confirm()"
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
      .sc-user {
        width: 100%;
        margin-top: 16px;
      }
      .sc-user-error {
        color: #b91c1c;
      }
    `,
  ],
})
export class SendConfirmDialogComponent {
  readonly isSap: boolean;
  readonly userLabel = ssoUserLabel;
  readonly userCtrl = new FormControl<SsoUser | string | null>(null, {
    validators: selectedUserValidator,
  });
  loadError = false;
  /** Gõ → lọc trong cache theo mã user / họ tên (không phân biệt hoa thường, dấu), tối đa 50 */
  readonly users$: Observable<SsoUser[]>;

  constructor(
    public dialogRef: MatDialogRef<
      SendConfirmDialogComponent,
      SendConfirmResult
    >,
    @Inject(MAT_DIALOG_DATA) public data: SendConfirmDialogData,
    ssoUserService: SsoUserService,
    accountService: AccountService,
    cdr: ChangeDetectorRef,
  ) {
    this.isSap = data.target === "SAP";
    // Danh sách user lấy từ cache (SsoUserService) — chỉ gọi Keycloak khi cache trống / hết hạn
    const allUsers$ = this.isSap
      ? ssoUserService.getAllUsers().pipe(
          catchError((err: unknown) => {
            console.error("Fetch SSO users error:", err);
            this.loadError = true;
            cdr.markForCheck();
            return of<SsoUser[]>([]);
          }),
          shareReplay(1),
        )
      : of<SsoUser[]>([]);
    this.users$ = combineLatest([
      allUsers$,
      this.userCtrl.valueChanges.pipe(
        startWith(""),
        map((v) => (typeof v === "string" ? normalize(v.trim()) : "")),
        distinctUntilChanged(),
      ),
    ]).pipe(
      map(([users, term]) =>
        (term
          ? users.filter(
              (u) =>
                normalize(u.username).includes(term) ||
                normalize(ssoUserName(u)).includes(term),
            )
          : users
        ).slice(0, SSO_USER_LIMIT),
      ),
    );
    if (this.isSap) {
      // Mặc định: user đang đăng nhập — lấy từ danh sách Keycloak để có sapUser
      combineLatest([accountService.identity().pipe(take(1)), allUsers$])
        .pipe(
          take(1),
          map(([account, users]) =>
            account
              ? (users.find((u) => u.username === account.login) ?? {
                  username: account.login,
                  firstName: account.firstName,
                  lastName: account.lastName,
                })
              : null,
          ),
        )
        .subscribe((user) => {
          if (user && !this.userCtrl.value) {
            this.userCtrl.setValue(user);
            this.userCtrl.markAsTouched();
            cdr.markForCheck();
          }
        });
    }
  }

  confirm(): void {
    const user = this.userCtrl.value;
    if (this.isSap && this.userCtrl.invalid) {
      return;
    }
    // createdBy = sapUser của user được chọn (validator đã đảm bảo có)
    this.dialogRef.close({
      createdBy: user && typeof user === "object" ? (user.sapUser ?? "") : "",
    });
  }
}
