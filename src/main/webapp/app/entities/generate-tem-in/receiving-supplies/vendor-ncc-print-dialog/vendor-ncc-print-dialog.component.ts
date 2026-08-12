import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  HostListener,
  Inject,
  OnDestroy,
  OnInit,
  ViewChild,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatSelectModule } from "@angular/material/select";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatDividerModule } from "@angular/material/divider";
import { QRCodeComponent } from "angularx-qrcode";
import {
  exportVendorNccLabelsToPdfSequential,
  getVendorNccPaperDimensions,
  loadVendorNccPaperSizePreference,
  runVendorNccLabelPrint,
  saveVendorNccPaperSizePreference,
  VENDOR_NCC_SHIPPING_ICONS_SRC,
  VendorNccLabelData,
  VendorNccPaperSize,
} from "../vendor-ncc-label-print.util";

export interface VendorNccPrintDialogData {
  labels: VendorNccLabelData[];
  requestLabel?: string;
}

/** 96dpi: 1mm ≈ 3.7795px — dùng để ước lượng khổ tem trên màn. */
const MM_TO_PX = 96 / 25.4;
const PREVIEW_PAD_PX = 40;
const PREVIEW_SCALE_MIN = 0.4;
const PREVIEW_SCALE_MAX = 4;

@Component({
  selector: "jhi-vendor-ncc-print-dialog",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatSelectModule,
    MatProgressBarModule,
    MatDividerModule,
    QRCodeComponent,
  ],
  templateUrl: "./vendor-ncc-print-dialog.component.html",
  styleUrls: ["./vendor-ncc-print-dialog.component.scss"],
})
export class VendorNccPrintDialogComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  readonly previewContainerId = "vendorNccPrintPreview";
  readonly printAllContainerId = "vendorNccPrintAll";
  readonly shippingIconsSrc = VENDOR_NCC_SHIPPING_ICONS_SRC;

  @ViewChild("previewViewport") previewViewport?: ElementRef<HTMLElement>;

  paperSize: VendorNccPaperSize = "A5";
  allLabels: VendorNccLabelData[] = [];
  previewIndex = 0;
  /** Scale chỉ cho preview UI — in/PDF dùng kích thước thật. */
  previewScale = 1;
  /** Tạm render toàn bộ tem khi In trực tiếp (clone DOM). */
  printAllMode = false;
  isExportingPdf = false;
  isPreparingPrint = false;
  pdfProgress = 0;

  private resizeObserver: ResizeObserver | null = null;
  private scaleRaf = 0;

  constructor(
    public dialogRef: MatDialogRef<VendorNccPrintDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: VendorNccPrintDialogData,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.paperSize = loadVendorNccPaperSizePreference();
    this.allLabels = [...(this.data.labels ?? [])];
    this.previewIndex = 0;
  }

  ngAfterViewInit(): void {
    this.bindPreviewViewportObserver();
    this.scheduleUpdatePreviewScale();
  }

  ngOnDestroy(): void {
    this.resizeObserver?.disconnect();
    this.resizeObserver = null;
    if (this.scaleRaf) {
      cancelAnimationFrame(this.scaleRaf);
    }
  }

  @HostListener("window:resize")
  onWindowResize(): void {
    this.scheduleUpdatePreviewScale();
  }

  get totalLabels(): number {
    return this.allLabels.length;
  }

  get currentLabel(): VendorNccLabelData | null {
    if (!this.allLabels.length) {
      return null;
    }
    const idx = Math.min(
      Math.max(this.previewIndex, 0),
      this.allLabels.length - 1,
    );
    return this.allLabels[idx] ?? null;
  }

  get previewPositionLabel(): string {
    if (!this.totalLabels) {
      return "0 / 0";
    }
    return `${this.previewIndex + 1} / ${this.totalLabels}`;
  }

  get isStripLayout(): boolean {
    return this.paperSize === "40x100";
  }

  get qrWidth(): number {
    switch (this.paperSize) {
      case "40x100":
        return 120;
      case "100x100":
        return 200;
      case "A5":
      default:
        return 180;
    }
  }

  get paperSizeHint(): string {
    const dim = getVendorNccPaperDimensions(this.paperSize);
    return `${dim.widthMm}×${dim.heightMm} mm`;
  }

  get labelWidthPx(): number {
    return getVendorNccPaperDimensions(this.paperSize).widthMm * MM_TO_PX;
  }

  get labelHeightPx(): number {
    return getVendorNccPaperDimensions(this.paperSize).heightMm * MM_TO_PX;
  }

  get previewStageWidthPx(): number {
    return this.labelWidthPx * this.previewScale;
  }

  get previewStageHeightPx(): number {
    return this.labelHeightPx * this.previewScale;
  }

  trackByLabelId(_index: number, label: VendorNccLabelData): string {
    return label.id;
  }

  onPaperSizeChange(size: VendorNccPaperSize): void {
    this.paperSize = size;
    saveVendorNccPaperSizePreference(size);
    this.scheduleUpdatePreviewScale();
  }

  prevLabel(): void {
    if (this.previewIndex <= 0) {
      return;
    }
    this.previewIndex -= 1;
  }

  nextLabel(): void {
    if (this.previewIndex >= this.totalLabels - 1) {
      return;
    }
    this.previewIndex += 1;
  }

  async printLabels(): Promise<void> {
    if (!this.allLabels.length || this.isPreparingPrint || this.isExportingPdf) {
      return;
    }

    this.isPreparingPrint = true;
    this.printAllMode = true;
    this.cdr.detectChanges();

    try {
      const waitMs = Math.min(3000, 400 + this.allLabels.length * 45);
      await this.delay(waitMs);
      runVendorNccLabelPrint(this.printAllContainerId, this.paperSize, () => {
        this.finishPrintAllMode();
      });
      setTimeout(() => this.finishPrintAllMode(), 10000);
    } catch {
      this.finishPrintAllMode();
    }
  }

  async exportPdf(): Promise<void> {
    if (!this.allLabels.length || this.isExportingPdf || this.isPreparingPrint) {
      return;
    }

    const savedIndex = this.previewIndex;
    const savedScale = this.previewScale;
    this.isExportingPdf = true;
    this.pdfProgress = 0;
    this.printAllMode = false;
    // Capture ở scale 1 để html2canvas / PDF đúng kích thước vật lý
    this.previewScale = 1;
    this.cdr.detectChanges();

    try {
      const date = new Date().toISOString().split("T")[0];
      const requestLabel = this.data.requestLabel ?? "ncc";
      await exportVendorNccLabelsToPdfSequential(
        this.paperSize,
        this.allLabels.length,
        `tem_ncc_${requestLabel}_${date}.pdf`,
        async (index: number) => {
          this.previewIndex = index;
          this.cdr.detectChanges();
          await this.delay(50);
          return document.querySelector(
            `#${this.previewContainerId} .vendor-ncc-label-page`,
          ) as HTMLElement | null;
        },
        (percent: number) => {
          this.pdfProgress = percent;
          this.cdr.markForCheck();
        },
      );
    } finally {
      this.previewIndex = savedIndex;
      this.previewScale = savedScale;
      this.isExportingPdf = false;
      this.pdfProgress = 0;
      this.cdr.detectChanges();
      this.scheduleUpdatePreviewScale();
    }
  }

  close(): void {
    this.dialogRef.close();
  }

  private finishPrintAllMode(): void {
    if (!this.printAllMode && !this.isPreparingPrint) {
      return;
    }
    this.printAllMode = false;
    this.isPreparingPrint = false;
    this.cdr.markForCheck();
    this.scheduleUpdatePreviewScale();
  }

  private bindPreviewViewportObserver(): void {
    const el = this.previewViewport?.nativeElement;
    if (!el || typeof ResizeObserver === "undefined") {
      return;
    }
    this.resizeObserver?.disconnect();
    this.resizeObserver = new ResizeObserver(() => {
      this.scheduleUpdatePreviewScale();
    });
    this.resizeObserver.observe(el);
  }

  private scheduleUpdatePreviewScale(): void {
    if (this.isExportingPdf) {
      return;
    }
    if (this.scaleRaf) {
      cancelAnimationFrame(this.scaleRaf);
    }
    this.scaleRaf = requestAnimationFrame(() => {
      this.scaleRaf = 0;
      this.updatePreviewScale();
    });
  }

  private updatePreviewScale(): void {
    const viewport = this.previewViewport?.nativeElement;
    if (!viewport || this.printAllMode) {
      return;
    }

    const availW = viewport.clientWidth - PREVIEW_PAD_PX;
    const availH = viewport.clientHeight - PREVIEW_PAD_PX;
    if (availW <= 0 || availH <= 0) {
      return;
    }

    const scale = Math.min(
      availW / this.labelWidthPx,
      availH / this.labelHeightPx,
      PREVIEW_SCALE_MAX,
    );
    const next = Math.max(
      PREVIEW_SCALE_MIN,
      Math.round(scale * 1000) / 1000,
    );
    if (Math.abs(next - this.previewScale) < 0.01) {
      return;
    }
    this.previewScale = next;
    this.cdr.markForCheck();
  }

  private delay(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}
