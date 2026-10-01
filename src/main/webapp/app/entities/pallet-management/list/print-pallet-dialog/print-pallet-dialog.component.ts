import {
  AfterViewInit,
  Component,
  ElementRef,
  Inject,
  OnInit,
  ViewChild,
} from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import JsBarcode from "jsbarcode";
import {
  PalletItem,
  PalletLabelSize,
  PrintPalletDialogData,
} from "../pallet-management.model";
import { exportPalletLabelsPdf, printPalletLabels } from "../pallet-print.util";

@Component({
  selector: "jhi-print-pallet-dialog",
  templateUrl: "./print-pallet-dialog.component.html",
  styleUrls: ["./print-pallet-dialog.component.scss"],
  standalone: false,
})
export class PrintPalletDialogComponent implements OnInit, AfterViewInit {
  labelSize: PalletLabelSize = "100x100";
  labelsPerRow = 2;
  selectedIds = new Set<number>();
  isPrinting = false;

  @ViewChild("barcodePreview") barcodePreview?: ElementRef<SVGSVGElement>;

  private lastBarcodeValue = "";

  constructor(
    private dialogRef: MatDialogRef<PrintPalletDialogComponent, void>,
    @Inject(MAT_DIALOG_DATA) public data: PrintPalletDialogData,
  ) {}

  ngOnInit(): void {
    const pre = this.data.preselectedIds ?? [];
    if (pre.length) {
      pre.forEach((id) => this.selectedIds.add(id));
    } else {
      this.data.pallets.forEach((p) => this.selectedIds.add(p.id));
    }
    this.applySizeDefaults();
  }

  ngAfterViewInit(): void {
    this.scheduleBarcodeRender();
  }

  get pallets(): PalletItem[] {
    return this.data.pallets ?? [];
  }

  get selectedCount(): number {
    return this.selectedIds.size;
  }

  get allSelected(): boolean {
    return (
      this.pallets.length > 0 && this.selectedCount === this.pallets.length
    );
  }

  get isQrMode(): boolean {
    return this.labelSize === "100x100";
  }

  get showLabelsPerRow(): boolean {
    return this.labelSize === "100x100";
  }

  get previewCode(): string {
    const firstSelected = this.pallets.find((p) => this.selectedIds.has(p.id));
    return (
      (firstSelected ?? this.pallets[0])?.serialPallet ?? "P00000000000000"
    );
  }

  statusLabel(status: string | null): string {
    const raw = (status ?? "").trim();
    const key = raw.toUpperCase();
    if (key === "IN_USE" || key === "USING") {
      return "Đang sử dụng";
    }
    if (key === "UNUSED" || key === "NEW") {
      return "Chưa sử dụng";
    }
    return raw.length > 0 ? raw : "—";
  }

  isInUse(status: string | null): boolean {
    const key = (status ?? "").trim().toUpperCase();
    return key === "IN_USE" || key === "USING";
  }

  isUnused(status: string | null): boolean {
    const key = (status ?? "").trim().toUpperCase();
    return key === "UNUSED" || key === "NEW";
  }

  selectSize(size: PalletLabelSize): void {
    if (this.labelSize === size) {
      return;
    }
    this.labelSize = size;
    this.applySizeDefaults();
    this.lastBarcodeValue = "";
    this.scheduleBarcodeRender();
  }

  decreasePerRow(): void {
    if (!this.showLabelsPerRow) {
      return;
    }
    this.labelsPerRow = Math.max(1, this.labelsPerRow - 1);
  }

  increasePerRow(): void {
    if (!this.showLabelsPerRow) {
      return;
    }
    this.labelsPerRow = Math.min(2, this.labelsPerRow + 1);
  }

  toggleAll(checked: boolean): void {
    this.selectedIds.clear();
    if (checked) {
      this.pallets.forEach((p) => this.selectedIds.add(p.id));
    }
    this.scheduleBarcodeRender();
  }

  deselectAll(): void {
    this.selectedIds.clear();
    this.scheduleBarcodeRender();
  }

  toggleOne(id: number, checked: boolean): void {
    if (checked) {
      this.selectedIds.add(id);
    } else {
      this.selectedIds.delete(id);
    }
    this.scheduleBarcodeRender();
  }

  isSelected(id: number): boolean {
    return this.selectedIds.has(id);
  }

  formatCreatedAt(value: string | null): string {
    const m = /^(\d{4})-(\d{2})-(\d{2})/.exec(value ?? "");
    if (!m) {
      return value ?? "—";
    }
    return `${m[3]}/${m[2]}/${m[1]}`;
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  /** Xuất PDF: tạo file .pdf và tải xuống luôn (không mở hộp thoại in) */
  async onExportPdf(): Promise<void> {
    if (!this.selectedCount || this.isPrinting) {
      return;
    }
    const selected = this.pallets.filter((p) => this.selectedIds.has(p.id));
    const fileName =
      selected.length === 1
        ? `tem-pallet-${selected[0].serialPallet}.pdf`
        : `tem-pallet-${selected.length}-${new Date().toISOString().slice(0, 10)}.pdf`;
    this.isPrinting = true;
    try {
      await exportPalletLabelsPdf(
        {
          pallets: selected,
          labelSize: this.labelSize,
          labelsPerRow: this.showLabelsPerRow ? this.labelsPerRow : 1,
        },
        fileName,
      );
    } finally {
      this.isPrinting = false;
    }
  }

  async onPrint(): Promise<void> {
    await this.printSelected();
  }

  private async printSelected(): Promise<void> {
    if (!this.selectedCount || this.isPrinting) {
      return;
    }
    const selected = this.pallets.filter((p) => this.selectedIds.has(p.id));
    this.isPrinting = true;
    try {
      await printPalletLabels({
        pallets: selected,
        labelSize: this.labelSize,
        labelsPerRow: this.showLabelsPerRow ? this.labelsPerRow : 1,
      });
    } finally {
      this.isPrinting = false;
    }
  }

  private applySizeDefaults(): void {
    if (this.labelSize === "40x100") {
      this.labelsPerRow = 1;
    } else {
      this.labelsPerRow = Math.min(2, Math.max(1, this.labelsPerRow || 2));
    }
  }

  private scheduleBarcodeRender(): void {
    if (this.labelSize !== "40x100") {
      return;
    }
    // Đợi *ngIf tạo xong SVG — chỉ render 1 lần khi cần, tránh nhấp nháy.
    setTimeout(() => this.renderBarcodePreview(), 0);
  }

  private renderBarcodePreview(): void {
    const el = this.barcodePreview?.nativeElement;
    const value = this.previewCode;
    if (!el || !value) {
      return;
    }
    if (this.lastBarcodeValue === value && el.childNodes.length > 0) {
      return;
    }
    try {
      JsBarcode(el, value, {
        format: "CODE128",
        lineColor: "#111",
        width: 1.4,
        height: 48,
        displayValue: false,
        margin: 4,
      });
      this.lastBarcodeValue = value;
    } catch {
      // ignore
    }
  }
}
