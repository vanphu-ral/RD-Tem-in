import {
  Component,
  OnInit,
  OnDestroy,
  ViewChild,
  ElementRef,
  AfterViewInit,
} from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";

export interface ScannedItem {
  id: string;
  scannedAt: Date;
  index: number;
  lotNumber: string;
  reelId: string;
  partNumber: string;
  vendor: string;
  boxCount: string;
  totalQty: string;
  initialQuantity: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  msl: string;
  storageUnit: string;
  manufacturingDate: string;
  expirationDate: string;
}

@Component({
  selector: "jhi-scan-dialog",
  templateUrl: "./scan-item-dialog.component.html",
  styleUrls: ["./scan-item-dialog.component.scss"],
})
export class ScanItemDialogComponent
  implements OnInit, OnDestroy, AfterViewInit
{
  @ViewChild("scanInput") scanInputRef!: ElementRef<HTMLInputElement>;

  scanInput = "";
  scannedList: ScannedItem[] = [];
  errorMessage = "";
  isInputFocused = false;
  lastScannedCode = "";
  duplicateHighlightId: string | null = null;

  lotColumns = [
    { key: "lotNumber", label: "Lot", minWidth: 130 },
    { key: "reelId", label: "ReelId", minWidth: 190 },
    { key: "partNumber", label: "Part Number", minWidth: 140 },
    { key: "vendor", label: "Vendor", minWidth: 110 },
    { key: "boxCount", label: "Số thùng", minWidth: 90 },
    { key: "totalQty", label: "Tổng SL", minWidth: 90 },
    { key: "initialQuantity", label: "Quantity", minWidth: 100 },
    { key: "userData1", label: "Userdata1", minWidth: 110 },
    { key: "userData2", label: "Userdata2", minWidth: 110 },
    { key: "userData3", label: "Userdata3", minWidth: 110 },
    { key: "userData4", label: "Userdata4", minWidth: 110 },
    { key: "userData5", label: "Userdata5", minWidth: 110 },
    { key: "msl", label: "MSL", minWidth: 80 },
    { key: "storageUnit", label: "StorageUnit", minWidth: 120 },
    { key: "manufacturingDate", label: "ManufacturingDate", minWidth: 150 },
    { key: "expirationDate", label: "ExpirationDate", minWidth: 150 },
  ];

  private inputBuffer = "";
  private bufferTimer: any = null;
  private readonly BUFFER_DELAY_MS = 100;

  constructor(private dialogRef: MatDialogRef<ScanItemDialogComponent>) {}

  ngOnInit(): void {
    this.dialogRef.disableClose = true;
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.focusInput();
    }, 200);
  }

  ngOnDestroy(): void {
    if (this.bufferTimer) {
      clearTimeout(this.bufferTimer);
    }
  }

  focusInput(): void {
    this.scanInputRef?.nativeElement?.focus();
  }

  onInputFocus(): void {
    this.isInputFocused = true;
  }

  onInputBlur(): void {
    this.isInputFocused = false;
  }

  onScanInputChange(value: string): void {
    this.scanInput = value;
    this.errorMessage = "";
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === "Enter") {
      event.preventDefault();
      this.submitScan();
    }
  }

  submitScan(): void {
    const rawCode = this.scanInput.trim();

    if (!rawCode) {
      this.errorMessage = "Vui lòng nhập mã để scan.";
      return;
    }

    const duplicate = this.scannedList.find(
      (item) => item.reelId === rawCode || item.lotNumber === rawCode,
    );
    if (duplicate) {
      this.errorMessage = `Mã "${rawCode}" đã được scan rồi.`;
      this.highlightDuplicate(duplicate.id);
      this.scanInput = "";
      return;
    }

    const parts = rawCode.includes("|") ? rawCode.split("|") : [rawCode];
    const keys = this.lotColumns.map((c) => c.key);
    const fieldMap: Record<string, string> = {};
    keys.forEach((key, idx) => {
      fieldMap[key] = parts[idx] ?? "";
    });

    const newItem: ScannedItem = {
      id: this.generateId(),
      scannedAt: new Date(),
      index: this.scannedList.length + 1,
      ...(fieldMap as any),
    };

    this.scannedList = [newItem, ...this.scannedList];
    this.lastScannedCode = rawCode;
    this.scanInput = "";
    this.errorMessage = "";

    setTimeout(() => {
      this.focusInput();
    }, 50);
  }

  removeItem(id: string): void {
    this.scannedList = this.scannedList
      .filter((item) => item.id !== id)
      .map((item, idx) => ({
        ...item,
        index: this.scannedList.length - 1 - idx,
      }));
  }

  clearAll(): void {
    this.scannedList = [];
    this.lastScannedCode = "";
    this.errorMessage = "";
    this.focusInput();
  }

  onConfirm(): void {
    this.dialogRef.close(this.scannedList);
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  get totalCount(): number {
    return this.scannedList.length;
  }

  private highlightDuplicate(id: string): void {
    this.duplicateHighlightId = id;
    setTimeout(() => {
      this.duplicateHighlightId = null;
    }, 2000);
  }

  private generateId(): string {
    return (
      Math.random().toString(36).substring(2, 10) + Date.now().toString(36)
    );
  }
}
