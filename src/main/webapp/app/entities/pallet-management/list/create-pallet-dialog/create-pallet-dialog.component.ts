import { Component, Inject, OnInit } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { Observable, forkJoin, take } from "rxjs";
import { AccountService } from "app/core/auth/account.service";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { PalletMngtService } from "../pallet-mngt.service";
import {
  buildPalletCode,
  CreatePalletDialogResult,
  PalletItem,
  PalletMngtCreatePayload,
} from "../pallet-management.model";

export interface CreatePalletDialogData {
  nextSequence: number;
}

@Component({
  selector: "jhi-create-pallet-dialog",
  templateUrl: "./create-pallet-dialog.component.html",
  styleUrls: ["./create-pallet-dialog.component.scss"],
  providers: [PalletMngtService],
  standalone: false,
})
export class CreatePalletDialogComponent implements OnInit {
  useDefaultCode = true;
  prefix = "P";
  sequence = "00001";
  codeDate: Date | null = new Date();
  quantity = 1;
  note = "";
  isSaving = false;
  /** Đang lấy danh sách pallet để tính STT tiếp theo */
  isLoadingSequence = false;
  /** Đã lấy được STT từ danh sách pallet — chưa có thì không cho lưu */
  sequenceReady = false;

  private createBy = "unknown";
  /** Mã pallet đã tồn tại — chặn tạo trùng */
  private existingCodes = new Set<string>();
  /** STT lớn nhất theo ngày trong mã (key = DDMMYYYY) */
  private maxSequenceByDate = new Map<string, number>();

  constructor(
    private dialogRef: MatDialogRef<
      CreatePalletDialogComponent,
      CreatePalletDialogResult | null
    >,
    @Inject(MAT_DIALOG_DATA) public data: CreatePalletDialogData,
    private palletMngtService: PalletMngtService,
    private accountService: AccountService,
    private notificationService: NotificationService,
  ) {}

  ngOnInit(): void {
    const next = Math.max(1, this.data?.nextSequence ?? 1);
    this.sequence = String(next).padStart(5, "0").slice(-5);
    this.codeDate = new Date();
    this.applyDefaultMode();
    this.loadNextSequence();
    this.accountService
      .getAuthenticationState()
      .pipe(take(1))
      .subscribe((account) => {
        const login = account?.login;
        if (typeof login === "string" && login.length > 0) {
          this.createBy = login;
        }
      });
  }

  onDefaultToggle(): void {
    this.applyDefaultMode();
    this.recomputeSequence();
  }

  /**
   * STT đếm riêng theo ngày trong mã (tiền tố + STT 5 số + DDMMYYYY):
   * STT = STT lớn nhất của ngày đang chọn + 1; ngày chưa có pallet → 00001.
   */
  recomputeSequence(): void {
    if (!this.sequenceReady) {
      return;
    }
    const dateKey = this.toDateKey(this.resolveDate());
    const next = (this.maxSequenceByDate.get(dateKey) ?? 0) + 1;
    this.sequence = String(next).padStart(5, "0").slice(-5);
  }

  get previewCode(): string {
    return buildPalletCode(
      this.resolvePrefix(),
      this.resolveSequence(),
      this.resolveDate(),
    );
  }

  onCancel(): void {
    if (this.isSaving) {
      return;
    }
    this.dialogRef.close(null);
  }

  onSave(): void {
    const qty = Number(this.quantity);
    if (
      !Number.isFinite(qty) ||
      qty < 1 ||
      this.isSaving ||
      this.isLoadingSequence
    ) {
      return;
    }
    if (!this.sequenceReady) {
      this.notificationService.warning(
        "Chưa kiểm tra được STT pallet hiện có — đang thử lại.",
      );
      this.loadNextSequence();
      return;
    }

    const prefix = this.resolvePrefix();
    const sequenceStart = this.resolveSequence();
    const codeDate = this.resolveDate();
    const count = Math.floor(qty);
    const lastSequence = sequenceStart + count - 1;
    if (lastSequence > 99999) {
      this.notificationService.error(
        `STT vượt quá 99999 (${sequenceStart} + ${count} pallet) — không tạo được mã.`,
      );
      return;
    }
    const duplicated: string[] = [];
    for (let i = 0; i < count; i++) {
      const code = buildPalletCode(prefix, sequenceStart + i, codeDate);
      if (this.existingCodes.has(code.toUpperCase())) {
        duplicated.push(code);
      }
    }
    if (duplicated.length) {
      this.notificationService.error(
        `Mã pallet đã tồn tại: ${duplicated.slice(0, 3).join(", ")}${duplicated.length > 3 ? "..." : ""}. Đã cập nhật lại STT, vui lòng kiểm tra và lưu lại.`,
      );
      this.loadNextSequence();
      return;
    }
    const now = new Date().toISOString();
    const note = this.note.trim();
    const payloads: PalletMngtCreatePayload[] = [];
    for (let i = 0; i < count; i++) {
      payloads.push({
        serialPallet: buildPalletCode(prefix, sequenceStart + i, codeDate),
        status: "UNUSED",
        note,
        createAt: now,
        createBy: this.createBy,
        updatedAt: now,
        updatedBy: this.createBy,
      });
    }

    const requests: Array<Observable<PalletItem>> = [];
    for (const payload of payloads) {
      requests.push(this.postPallet(payload));
    }

    this.isSaving = true;
    forkJoin(requests).subscribe({
      next: (rows) => {
        this.isSaving = false;
        const created = this.withDistinctIds(rows);
        this.dialogRef.close({
          created,
          quantity: count,
          sequenceStart,
          saveAndPrint: true,
        });
      },
      error: () => {
        this.isSaving = false;
        this.notificationService.error("Không tạo được pallet.");
      },
    });
  }

  /** Lấy danh sách pallet → ghi nhận STT lớn nhất của từng ngày → tính STT */
  private loadNextSequence(): void {
    this.isLoadingSequence = true;
    this.sequenceReady = false;
    this.palletMngtService.getAll().subscribe({
      next: (rows) => {
        this.existingCodes.clear();
        this.maxSequenceByDate.clear();
        for (const row of rows ?? []) {
          const code = String(row?.serialPallet ?? "")
            .trim()
            .toUpperCase();
          if (!code) {
            continue;
          }
          this.existingCodes.add(code);
          const match = /(\d{5})(\d{8})$/.exec(code);
          if (match) {
            const dateKey = match[2];
            const seq = Number(match[1]);
            this.maxSequenceByDate.set(
              dateKey,
              Math.max(this.maxSequenceByDate.get(dateKey) ?? 0, seq),
            );
          }
        }
        this.isLoadingSequence = false;
        this.sequenceReady = true;
        this.recomputeSequence();
      },
      error: () => {
        this.isLoadingSequence = false;
        this.notificationService.error(
          "Không lấy được danh sách pallet để tính STT — vui lòng thử lại.",
        );
      },
    });
  }

  private postPallet(payload: PalletMngtCreatePayload): Observable<PalletItem> {
    return new Observable<PalletItem>((subscriber) => {
      const request = this.palletMngtService.createPallet(payload);
      const subscription = request.subscribe({
        next: (row) => {
          subscriber.next(this.toPalletItem(row, payload));
          subscriber.complete();
        },
        error: (err: unknown) => {
          subscriber.error(err);
        },
      });
      return () => {
        subscription.unsubscribe();
      };
    });
  }

  private toPalletItem(
    row: PalletItem | null,
    payload: PalletMngtCreatePayload,
  ): PalletItem {
    const id = row && typeof row.id === "number" ? row.id : 0;
    return {
      id,
      serialPallet:
        row && typeof row.serialPallet === "string"
          ? row.serialPallet
          : payload.serialPallet,
      locationName: row ? row.locationName : null,
      numberOfBox: row ? row.numberOfBox : null,
      totalQuantity: row ? row.totalQuantity : null,
      status: row && row.status ? row.status : payload.status,
      note:
        row && row.note !== undefined && row.note !== null
          ? row.note
          : payload.note,
      createAt: row && row.createAt ? row.createAt : payload.createAt,
      createBy: row && row.createBy ? row.createBy : payload.createBy,
      updatedAt: row && row.updatedAt ? row.updatedAt : payload.updatedAt,
      updatedBy: row && row.updatedBy ? row.updatedBy : payload.updatedBy,
    };
  }

  private resolvePrefix(): string {
    const raw = this.prefix.trim();
    return raw.length > 0 ? raw : "P";
  }

  private resolveSequence(): number {
    const sequence = Number(this.sequence);
    return Number.isFinite(sequence) && sequence > 0 ? sequence : 1;
  }

  private resolveDate(): Date {
    return this.codeDate ?? new Date();
  }

  /** DDMMYYYY — trùng với phần ngày trong mã pallet */
  private toDateKey(date: Date): string {
    const dd = String(date.getDate()).padStart(2, "0");
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    return `${dd}${mm}${date.getFullYear()}`;
  }

  private applyDefaultMode(): void {
    if (this.useDefaultCode) {
      this.prefix = "P";
      this.codeDate = new Date();
    }
  }

  private withDistinctIds(rows: PalletItem[]): PalletItem[] {
    return rows.map((row, index) => ({
      ...row,
      id: row.id > 0 ? row.id : -(index + 1),
    }));
  }
}
