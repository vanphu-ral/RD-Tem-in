import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  Inject,
  OnDestroy,
  ViewChild,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { MatIconModule } from "@angular/material/icon";
import { Observable } from "rxjs";
import { QRCodeComponent } from "angularx-qrcode";
import { RdLabelComponent } from "../rd-label/rd-label.component";
import { SplitPrintLabel } from "../rd-label/rd-label.model";
import { formatSplitTimestamp } from "app/entities/info-tem-ncc/shared/box-split.util";

/**
 * ReelID tem tách (đưa vào materialIdsToSplit của PanaCIM): yyyyMMddHHmmss + STT 4 chữ số
 * (0001, 0002…); trùng ReelID đã có (taken) → tăng STT.
 */
export function buildPanaSplitReelId(
  at: Date,
  taken: ReadonlySet<string>,
): string {
  const ts = formatSplitTimestamp(at);
  let seq = 1;
  let candidate = `${ts}${String(seq).padStart(4, "0")}`;
  while (taken.has(candidate) && seq < 9999) {
    seq++;
    candidate = `${ts}${String(seq).padStart(4, "0")}`;
  }
  return candidate;
}

/** Dữ liệu mở dialog tách tem (Xuất chuyển kho) */
export interface SplitReelDialogData {
  reelId: string;
  materialCode: string;
  materialName: string;
  partNumber: string;
  lot: string;
  mfgDate: string;
  /** Vị trí hiện tại của thùng (tem tách ở lại đây) */
  location: string;
  /** SL trong thùng (tồn của tem gốc) */
  boxQty: number;
  /** SL xuất gợi ý điền sẵn */
  suggestedQty: number;
  /** SL còn cần của mã vật tư trên các yêu cầu (hiển thị gợi ý) */
  needQty: number;
  /** ReelID đang có (tránh sinh trùng) */
  takenReelIds: string[];
  /** Thông tin tem mẫu Rạng Đông theo tem gốc (ReelID / SL thay bằng tem tách khi preview) */
  rdLabel: SplitPrintLabel;
}

/** Kết quả: SL xuất theo tem gốc + tem tách cho phần dư (null nếu xuất cả thùng) */
export interface SplitReelDialogResult {
  exportQty: number;
  remainderQty: number;
  newReelId: string | null;
  /** true = in tem tách ngay; false = đưa vào hàng chờ in */
  printNow: boolean;
  /** Mẫu tem khi in ngay: "rd" = tem Rạng Đông (như Nhận vật tư), "vendor" = tem NCC */
  printTemplate: SplitPrintTemplate;
}

export type SplitPrintTemplate = "rd" | "vendor";

/** Tem RD gốc rộng 600px (như Preview In Tem); preview phóng theo bề rộng cột, tối đa 1.3× */
const RD_LABEL_WIDTH = 600;
const RD_PREVIEW_MAX_SCALE = 1.3;

/**
 * Tách tem khi xuất chuyển kho: tem gốc đi theo SL xuất đi, phần dư ở lại kho xuất
 * được sinh tem mới (ReelID tách) ở vị trí cũ. Chọn in tem ngay hoặc lưu hàng chờ in.
 */
@Component({
  selector: "jhi-split-reel-dialog",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatIconModule,
    QRCodeComponent,
    RdLabelComponent,
  ],
  templateUrl: "./split-reel-dialog.component.html",
  styleUrls: ["./split-reel-dialog.component.scss"],
})
export class SplitReelDialogComponent implements OnDestroy {
  exportQty: number;
  printNow = true;
  printTemplate: SplitPrintTemplate = "rd";
  /** ReelID tách sinh 1 lần khi mở dialog (giữ nguyên khi đổi SL) */
  readonly newReelId: string;
  /** Tem Rạng Đông xem trước (tạo lại khi đổi SL để không render lại mỗi vòng CD) */
  previewLabel!: SplitPrintLabel;
  /** Tỉ lệ phóng tem preview theo bề rộng khung */
  previewScale = 1;
  private stageObserver?: ResizeObserver;

  constructor(
    private cdr: ChangeDetectorRef,
    private dialogRef: MatDialogRef<
      SplitReelDialogComponent,
      SplitReelDialogResult
    >,
    @Inject(MAT_DIALOG_DATA) public data: SplitReelDialogData,
  ) {
    this.exportQty = Math.min(
      Math.max(1, Math.floor(data.suggestedQty)),
      data.boxQty,
    );
    this.newReelId = buildPanaSplitReelId(
      new Date(),
      new Set(data.takenReelIds),
    );
    this.refreshPreview();
  }

  get remainderQty(): number {
    const qty = Number(this.exportQty);
    return Number.isFinite(qty) ? Math.max(0, this.data.boxQty - qty) : 0;
  }

  get error(): string | null {
    const qty = Number(this.exportQty);
    if (!Number.isFinite(qty) || qty <= 0) {
      return "SL xuất phải lớn hơn 0.";
    }
    if (!Number.isInteger(qty)) {
      return "SL xuất phải là số nguyên.";
    }
    if (qty > this.data.boxQty) {
      return `Không được lớn hơn SL trong thùng (${this.data.boxQty.toLocaleString("vi-VN")}).`;
    }
    return null;
  }

  /** Khung preview nằm trong *ngIf → theo dõi lại mỗi khi xuất hiện */
  @ViewChild("rdStage")
  set rdStage(el: ElementRef<HTMLElement> | undefined) {
    this.stageObserver?.disconnect();
    this.stageObserver = undefined;
    if (!el || typeof ResizeObserver === "undefined") {
      return;
    }
    this.stageObserver = new ResizeObserver(([entry]) => {
      const width = entry.contentRect.width;
      const scale = Math.min(RD_PREVIEW_MAX_SCALE, width / RD_LABEL_WIDTH);
      if (width > 0 && Math.abs(scale - this.previewScale) > 0.005) {
        this.previewScale = scale;
        this.cdr.detectChanges();
      }
    });
    this.stageObserver.observe(el.nativeElement);
  }

  ngOnDestroy(): void {
    this.stageObserver?.disconnect();
  }

  refreshPreview(): void {
    this.previewLabel = {
      ...this.data.rdLabel,
      id: this.newReelId,
      qrCode: this.newReelId,
      reelId: this.newReelId,
      initialQuantity: this.remainderQty,
    };
  }

  confirm(): void {
    if (this.error) {
      return;
    }
    const remainder = this.remainderQty;
    this.dialogRef.close({
      exportQty: Number(this.exportQty),
      remainderQty: remainder,
      newReelId: remainder > 0 ? this.newReelId : null,
      printNow: this.printNow,
      printTemplate: this.printTemplate,
    });
  }

  cancel(): void {
    this.dialogRef.close();
  }
}

/** Mở dialog tách tem → kết quả (undefined nếu hủy) */
export function openSplitReelDialog(
  dialog: MatDialog,
  data: SplitReelDialogData,
): Observable<SplitReelDialogResult | undefined> {
  return dialog
    .open<SplitReelDialogComponent, SplitReelDialogData, SplitReelDialogResult>(
      SplitReelDialogComponent,
      {
        width: "92vw",
        maxWidth: "1280px",
        autoFocus: false,
        disableClose: true,
        panelClass: "split-reel-dialog-panel",
        data,
      },
    )
    .afterClosed();
}
