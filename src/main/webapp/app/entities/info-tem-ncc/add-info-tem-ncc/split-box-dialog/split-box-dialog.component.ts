import { Component, Inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatTooltipModule } from "@angular/material/tooltip";
import { Observable, of, throwError } from "rxjs";
import { catchError, switchMap, take } from "rxjs/operators";
import { AccountService } from "app/core/auth/account.service";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { resolveHttpErrorMessage } from "app/entities/generate-tem-in/service/receiving-supplies.service";
import {
  CreateVendorLabelInfoPayload,
  InfoTemNccService,
  toText,
  VendorLabelInfoDto,
} from "../../services/info-tem-ncc.service";
import {
  clearQueueReason,
  queueReasonCode,
} from "../../shared/queue-reason.util";
import {
  buildSplitQrCode,
  buildSplitReelId,
} from "../../shared/box-split.util";
import { openVendorNccLabelPrint } from "../../shared/split-label-print.util";

/** Dòng PO của đơn có thể nhận thùng (cùng mã vật tư) — khai báo local tránh import vòng */
export interface SplitPoLine {
  id: number;
  poCode: string;
  /** SL theo PO (0 = không giới hạn) */
  orderQty: number;
  /** SL đã nhận vào dòng trong đơn */
  receivedQty: number;
}

export interface SplitBoxDialogData {
  sapCode: string;
  partNumber: string;
  materialName: string;
  /** Thùng Thừa SL cần tách */
  boxes: VendorLabelInfoDto[];
  poLines: SplitPoLine[];
  /** ReelID đã có (tránh sinh ReelID tách trùng) */
  takenReelIds: string[];
  /** Thông tin đơn — in tem */
  vendorName?: string;
  invoiceNumber?: string;
  contractCode?: string;
}

/** 1 lần tách thành công: thùng gốc (đã vào PO) + thùng dư mới (hàng chờ) */
export interface SplitBoxResult {
  original: VendorLabelInfoDto;
  remainder: VendorLabelInfoDto;
  poLineId: number;
  intoPoQty: number;
}

export interface SplitBoxDialogResult {
  results: SplitBoxResult[];
}

interface SplitRow {
  record: VendorLabelInfoDto;
  quantity: number;
  lineId: number | null;
  /** SL dư tách ra */
  excess: number | null;
  state: "idle" | "saving" | "done";
  remainder?: VendorLabelInfoDto;
}

/**
 * Tách thùng Thừa SL ở "Hàng chờ vật tư": nhập SL dư → thùng gốc giữ ReelID, SL còn
 * (SL - dư) và vào PO; phần dư thành thùng mới (ReelID theo thời điểm tách, clone thông
 * tin thùng gốc), không gán PO, nằm lại hàng chờ của đơn (badge "Dư tách"), có thể in tem.
 */
@Component({
  selector: "jhi-split-box-dialog",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
  ],
  templateUrl: "./split-box-dialog.component.html",
  styleUrls: ["./split-box-dialog.component.scss"],
})
export class SplitBoxDialogComponent implements OnInit {
  rows: SplitRow[] = [];
  lines: SplitPoLine[] = [];

  private results: SplitBoxResult[] = [];
  private taken = new Set<string>();
  private currentUser = "";

  constructor(
    private dialogRef: MatDialogRef<
      SplitBoxDialogComponent,
      SplitBoxDialogResult
    >,
    @Inject(MAT_DIALOG_DATA) public data: SplitBoxDialogData,
    private dialog: MatDialog,
    private infoTemNccService: InfoTemNccService,
    private notificationService: NotificationService,
    private accountService: AccountService,
  ) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(take(1))
      .subscribe((account) => {
        this.currentUser = account?.login ?? "";
      });
    // Bản sao — SL đã nhận cộng dần sau mỗi lần tách trong dialog
    this.lines = this.data.poLines.map((l) => ({ ...l }));
    this.taken = new Set(this.data.takenReelIds);
    this.rows = this.data.boxes.map((record) => {
      const quantity = Number(record.initialQuantity ?? 0);
      const line = this.defaultLine(record);
      const remaining = line ? this.remaining(line) : 0;
      return {
        record,
        quantity,
        lineId: line?.id ?? null,
        excess:
          Number.isFinite(remaining) && remaining > 0 && remaining < quantity
            ? quantity - remaining
            : null,
        state: "idle",
      };
    });
  }

  get isBusy(): boolean {
    return this.rows.some((r) => r.state === "saving");
  }

  get doneRows(): SplitRow[] {
    return this.rows.filter((r) => r.state === "done");
  }

  lineOf(row: SplitRow): SplitPoLine | undefined {
    return this.lines.find((l) => l.id === row.lineId);
  }

  /** SL PO còn nhận được (dòng SL = 0 → không giới hạn) */
  remaining(line: SplitPoLine): number {
    return line.orderQty > 0
      ? Math.max(0, line.orderQty - line.receivedQty)
      : Number.POSITIVE_INFINITY;
  }

  remainingText(line: SplitPoLine | undefined): string {
    if (!line) {
      return "—";
    }
    const r = this.remaining(line);
    return Number.isFinite(r) ? r.toLocaleString("vi-VN") : "Không giới hạn";
  }

  /** SL thùng gốc giữ lại để vào PO */
  intoPo(row: SplitRow): number | null {
    const excess = Number(row.excess);
    return row.excess === null || !Number.isFinite(excess)
      ? null
      : row.quantity - excess;
  }

  /** Lỗi nhập của dòng — rỗng nếu hợp lệ */
  validate(row: SplitRow): string {
    const line = this.lineOf(row);
    if (!line) {
      return "Chọn PO nhận thùng";
    }
    const remaining = this.remaining(line);
    if (remaining <= 0) {
      return `PO ${line.poCode} đã đủ SL`;
    }
    const excess = Number(row.excess);
    if (row.excess === null || !Number.isInteger(excess)) {
      return "Nhập SL dư (số nguyên)";
    }
    if (excess <= 0 || excess >= row.quantity) {
      return `SL dư phải từ 1 đến ${row.quantity - 1}`;
    }
    const into = row.quantity - excess;
    if (into > remaining) {
      return `SL vào PO (${into}) vượt SL còn nhận (${remaining}) — tăng SL dư lên ít nhất ${row.quantity - remaining}`;
    }
    return "";
  }

  onSplit(row: SplitRow): void {
    const line = this.lineOf(row);
    if (row.state !== "idle" || !line || this.validate(row)) {
      return;
    }
    const rec = row.record;
    const excess = Number(row.excess);
    const into = row.quantity - excess;
    const originalReel = toText(rec.reelId);
    const newReel = buildSplitReelId(originalReel, new Date(), this.taken);
    const qr = buildSplitQrCode(
      toText(rec.vendorQrCode),
      originalReel,
      newReel,
      row.quantity,
      excess,
    );

    // Thùng dư: clone thùng gốc, khác ReelID / SL / QR, không gán PO, nằm ở hàng chờ
    const {
      id: _id,
      serialPallet: _serialPallet,
      palletBoxMappingId: _mappingId,
      palletBoxMapping: _mapping,
      ...base
    } = rec;
    const remainderPayload: CreateVendorLabelInfoPayload = {
      ...base,
      reelId: newReel,
      initialQuantity: excess,
      quantityOverride: null,
      vendorQrCode: qr.qrCode,
      userData5: null,
      sapPor1Id: null,
      status: queueReasonCode("splitRemainder"),
      comments: `Tách từ ${originalReel}`.slice(0, 50),
      panaSendStatus: null,
      sapSendStatus: null,
      createdBy: this.currentUser || rec.createdBy,
      createdAt: null,
      updatedBy: null,
      updatedAt: null,
    };
    // Thùng gốc: giữ ReelID, SL = SL - dư, vào PO, bỏ lý do hàng chờ
    const originalPayload: VendorLabelInfoDto = {
      ...rec,
      initialQuantity: into,
      sapPor1Id: line.id,
      userData5: line.poCode,
      ...clearQueueReason(rec),
      palletBoxMapping: undefined,
    };

    row.state = "saving";
    this.taken.add(newReel);
    this.infoTemNccService
      .createVendorLabelInfo(remainderPayload)
      .pipe(
        switchMap((created) =>
          this.infoTemNccService.updateVendorLabelInfo(originalPayload).pipe(
            catchError((err: unknown) =>
              // Cập nhật thùng gốc lỗi → xóa thùng dư vừa tạo (không để thừa SL)
              this.rollback(created).pipe(
                switchMap(() => throwError(() => err)),
              ),
            ),
            switchMap((updated) => of({ created, updated })),
          ),
        ),
      )
      .subscribe({
        next: ({ created, updated }) => {
          const remainder: VendorLabelInfoDto = {
            ...remainderPayload,
            ...created,
          };
          row.state = "done";
          row.remainder = remainder;
          line.receivedQty += into;
          this.results.push({
            original: { ...originalPayload, ...(updated ?? {}) },
            remainder,
            poLineId: line.id,
            intoPoQty: into,
          });
          this.notificationService.success(
            `Đã tách thùng "${originalReel}": ${into} vào PO ${line.poCode}, ${excess} sang thùng mới "${newReel}".`,
          );
          if (!qr.quantityReplaced) {
            this.notificationService.warning(
              `Không xác định được SL trên mã QR của thùng gốc — QR thùng "${newReel}" chỉ đổi ReelID, kiểm tra lại trước khi in.`,
            );
          }
        },
        error: (err: unknown) => {
          row.state = "idle";
          this.taken.delete(newReel);
          this.notificationService.error(
            resolveHttpErrorMessage(
              err,
              `Tách thùng "${originalReel}" thất bại.`,
            ),
          );
        },
      });
  }

  /** In tem thùng dư — 1 thùng hoặc tất cả thùng vừa tách */
  onPrint(rows: SplitRow[]): void {
    openVendorNccLabelPrint(
      this.dialog,
      rows.flatMap((r) => (r.remainder ? [r.remainder] : [])),
      {
        sapCode: this.data.sapCode,
        partNumber: this.data.partNumber,
        materialName: this.data.materialName,
        vendorName: this.data.vendorName,
        invoiceNumber: this.data.invoiceNumber,
        contractCode: this.data.contractCode,
        operator: this.currentUser,
      },
    );
  }

  onClose(): void {
    if (this.isBusy) {
      return;
    }
    this.dialogRef.close({ results: this.results });
  }

  private rollback(created: VendorLabelInfoDto | null): Observable<unknown> {
    return created?.id
      ? this.infoTemNccService
          .deleteVendorLabelInfo(created.id)
          .pipe(catchError(() => of(null)))
      : of(null);
  }

  /** PO chọn sẵn: PO đã ghi trên thùng (lúc scan), không có thì PO còn nhận nhiều nhất */
  private defaultLine(rec: VendorLabelInfoDto): SplitPoLine | undefined {
    const po = toText(rec.userData5).toLowerCase();
    const byPo = po
      ? this.lines.find((l) => toText(l.poCode).toLowerCase() === po)
      : undefined;
    if (byPo) {
      return byPo;
    }
    return [...this.lines].sort(
      (a, b) => this.remaining(b) - this.remaining(a),
    )[0];
  }
}
