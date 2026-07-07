import { ChangeDetectorRef, Component, Inject, OnInit } from "@angular/core";
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
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatDividerModule } from "@angular/material/divider";
import { QRCodeComponent } from "angularx-qrcode";
import {
  DEFAULT_VENDOR_NCC_HANDLING_ICONS,
  exportVendorNccLabelsToPdf,
  loadVendorNccPaperSizePreference,
  runVendorNccLabelPrint,
  saveVendorNccPaperSizePreference,
  VENDOR_NCC_HANDLING_ICONS,
  VendorNccHandlingIconId,
  VendorNccHandlingIconOption,
  VendorNccLabelData,
  VendorNccPaperSize,
} from "../vendor-ncc-label-print.util";

export interface VendorNccPrintDialogData {
  labels: VendorNccLabelData[];
  requestLabel?: string;
}

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
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatProgressBarModule,
    MatDividerModule,
    QRCodeComponent,
  ],
  templateUrl: "./vendor-ncc-print-dialog.component.html",
  styleUrls: ["./vendor-ncc-print-dialog.component.scss"],
})
export class VendorNccPrintDialogComponent implements OnInit {
  readonly handlingIconOptions = VENDOR_NCC_HANDLING_ICONS;
  readonly previewContainerId = "vendorNccPrintPreview";

  paperSize: VendorNccPaperSize = "A5";
  selectedHandlingIcons: VendorNccHandlingIconId[] = [
    ...DEFAULT_VENDOR_NCC_HANDLING_ICONS,
  ];
  displayLabels: VendorNccLabelData[] = [];
  isExportingPdf = false;
  pdfProgress = 0;

  constructor(
    public dialogRef: MatDialogRef<VendorNccPrintDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: VendorNccPrintDialogData,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.paperSize = loadVendorNccPaperSizePreference();
    this.displayLabels = [...this.data.labels];
  }

  get totalLabels(): number {
    return this.data.labels.length;
  }

  trackByLabelId(_index: number, label: VendorNccLabelData): string {
    return label.id;
  }

  onPaperSizeChange(size: VendorNccPaperSize): void {
    this.paperSize = size;
    saveVendorNccPaperSizePreference(size);
  }

  isHandlingIconSelected(id: VendorNccHandlingIconId): boolean {
    return this.selectedHandlingIcons.includes(id);
  }

  toggleHandlingIcon(id: VendorNccHandlingIconId, checked: boolean): void {
    if (checked) {
      if (this.selectedHandlingIcons.length >= 4) {
        return;
      }
      if (!this.selectedHandlingIcons.includes(id)) {
        this.selectedHandlingIcons = [...this.selectedHandlingIcons, id];
      }
      return;
    }
    this.selectedHandlingIcons = this.selectedHandlingIcons.filter(
      (item) => item !== id,
    );
  }

  getActiveHandlingIcons(): VendorNccHandlingIconOption[] {
    return VENDOR_NCC_HANDLING_ICONS.filter(
      (icon: VendorNccHandlingIconOption) =>
        this.selectedHandlingIcons.includes(icon.id),
    );
  }

  printLabels(): void {
    if (!this.displayLabels.length) {
      return;
    }
    runVendorNccLabelPrint(this.previewContainerId, this.paperSize);
  }

  async exportPdf(): Promise<void> {
    if (!this.displayLabels.length || this.isExportingPdf) {
      return;
    }
    this.isExportingPdf = true;
    this.pdfProgress = 0;
    this.cdr.markForCheck();

    try {
      const date = new Date().toISOString().split("T")[0];
      const requestLabel = this.data.requestLabel ?? "ncc";
      await exportVendorNccLabelsToPdf(
        this.previewContainerId,
        this.paperSize,
        `tem_ncc_${requestLabel}_${date}.pdf`,
        (percent: number) => {
          this.pdfProgress = percent;
          this.cdr.markForCheck();
        },
      );
    } finally {
      this.isExportingPdf = false;
      this.pdfProgress = 0;
      this.cdr.markForCheck();
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}
