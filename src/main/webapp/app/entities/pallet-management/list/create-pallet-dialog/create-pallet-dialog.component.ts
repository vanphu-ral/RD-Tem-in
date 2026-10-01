import { Component, Inject, OnInit } from "@angular/core";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from "@angular/material/dialog";
import { PrintPalletDialogComponent } from "../print-pallet-dialog/print-pallet-dialog.component";
import { Observable, forkJoin, take } from "rxjs";
import { AccountService } from "app/core/auth/account.service";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { PalletMngtService } from "../pallet-mngt.service";
import {
  buildPalletCode,
  CreatePalletDialogResult,
  PalletItem,
  PalletMngtCreatePayload,
  PrintPalletDialogData,
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
  /** Đang tải danh sách pallet để mở dialog In */
  isOpeningPrint = false;
  /** Pallet đã lưu trong lần mở dialog này — nút In dùng danh sách này */
  savedPallets: PalletItem[] = [];
  /** Đang lấy danh sách pallet để tính STT tiếp theo */
  isLoadingSequence = false;
  /** Đã lấy được STT từ danh sách pallet — chưa có thì không cho lưu */
  sequenceReady = false;

  private createBy = "unknown";
  /** STT đầu của lần lưu đầu tiên (trả về trang ngoài) */
  private lastSequenceStart: number | null = null;
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
    private dialog: MatDialog,
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
    // Đã lưu pallet → trả kết quả để trang ngoài tải lại danh sách (không in)
    this.dialogRef.close(this.buildResult(false));
  }

  /**
   * In: mở dialog In ngay trên dialog này.
   * Đã lưu pallet → chỉ các pallet vừa lưu; chưa lưu → toàn bộ pallet (như nút In ở ngoài).
   */
  onPrint(): void {
    if (this.isSaving || this.isOpeningPrint) {
      return;
    }
    if (this.savedPallets.length) {
      this.openPrintDialog([]);
      return;
    }
    this.isOpeningPrint = true;
    this.palletMngtService.getAll().subscribe({
      next: (rows) => {
        this.isOpeningPrint = false;
        this.openPrintDialog(rows ?? []);
      },
      error: () => {
        this.isOpeningPrint = false;
        // Không tải được danh sách → vẫn in được các pallet vừa lưu
        this.openPrintDialog([]);
      },
    });
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
        this.savedPallets = [...this.savedPallets, ...created];
        this.lastSequenceStart = this.lastSequenceStart ?? sequenceStart;
        this.notificationService.success(
          `Lưu thành công ${created.length} pallet: ${created
            .slice(0, 3)
            .map((p) => p.serialPallet)
            .join(", ")}${created.length > 3 ? "..." : ""}.`,
        );
        // Lấy lại STT để lần lưu tiếp không trùng mã
        this.loadNextSequence();
      },
      error: () => {
        this.isSaving = false;
        this.notificationService.error("Lưu pallet thất bại.");
      },
    });
  }

  private openPrintDialog(all: PalletItem[]): void {
    const savedIds = new Set(this.savedPallets.map((p) => p.id));
    const pallets = [
      ...this.savedPallets,
      ...all.filter((p) => !savedIds.has(p.id)),
    ];
    if (!pallets.length) {
      this.notificationService.warning("Chưa có pallet nào để in.");
      return;
    }
    this.dialog.open(PrintPalletDialogComponent, {
      width: "980px",
      maxWidth: "96vw",
      maxHeight: "92vh",
      disableClose: true,
      panelClass: "print-pallet-dialog-panel",
      data: {
        pallets,
        preselectedIds: this.savedPallets.map((p) => p.id),
      } as PrintPalletDialogData,
    });
  }

  private buildResult(print: boolean): CreatePalletDialogResult | null {
    if (!this.savedPallets.length) {
      return null;
    }
    return {
      created: this.savedPallets,
      quantity: this.savedPallets.length,
      sequenceStart: this.lastSequenceStart ?? 1,
      saveAndPrint: print,
    };
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
