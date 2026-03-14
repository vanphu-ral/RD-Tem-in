import {
  Component,
  Inject,
  OnInit,
  ViewChildren,
  QueryList,
  ElementRef,
  AfterViewChecked,
} from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";

// ==================== INTERFACES ====================

export interface LotDetailRow {
  reelId: string;
  partNumber: string;
  vendor: string;
  lot: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  initialQuantity: number | string;
  msl: string;
  storageUnit: string;
  manufacturingDate: string;
  expirationDate: string;
  sapCode: string;
  [key: string]: any;
}

export interface ColumnDef {
  key: string;
  label: string;
  minWidth: number;
  editable: boolean;
}

export interface LotDetailDialogData {
  partNumber: string;
  manufacturingDate: string;
  rows: LotDetailRow[];
}

interface EditingCell {
  row: number;
  col: string;
}

// ==================== COMPONENT ====================

@Component({
  selector: "jhi-lot-detail-dialog",
  templateUrl: "./lot-detail-dialog.component.html",
  styleUrls: ["./lot-detail-dialog.component.scss"],
})
export class LotDetailDialogComponent implements OnInit, AfterViewChecked {
  columns: ColumnDef[] = [
    { key: "sapCode", label: "Mã SAP", minWidth: 120, editable: true },
    {
      key: "productName",
      label: "Tên hàng hóa",
      minWidth: 220,
      editable: true,
    },
    { key: "reelId", label: "ReelId", minWidth: 190, editable: true },
    { key: "partNumber", label: "Part Number", minWidth: 140, editable: true },
    { key: "lot", label: "Lot", minWidth: 100, editable: true },
    { key: "vendor", label: "Vendor", minWidth: 110, editable: true },
    {
      key: "initialQuantity",
      label: "Quantity",
      minWidth: 100,
      editable: true,
    },
    { key: "userData1", label: "Userdata1", minWidth: 110, editable: true },
    { key: "userData2", label: "Userdata2", minWidth: 110, editable: true },
    { key: "userData3", label: "Userdata3", minWidth: 110, editable: true },
    { key: "userData4", label: "Userdata4", minWidth: 110, editable: true },
    { key: "userData5", label: "Userdata5", minWidth: 110, editable: true },
    { key: "msl", label: "MSL", minWidth: 80, editable: true },
    { key: "storageUnit", label: "StorageUnit", minWidth: 120, editable: true },
    {
      key: "manufacturingDate",
      label: "ManufacturingDate",
      minWidth: 150,
      editable: true,
    },
    {
      key: "expirationDate",
      label: "ExpirationDate",
      minWidth: 150,
      editable: true,
    },
  ];

  rows: LotDetailRow[] = [];

  /** Holds bulk-apply values per column key */
  bulkValues: { [key: string]: any } = {};

  editingCell: EditingCell | null = null;
  @ViewChildren("cellInput") cellInputs!: QueryList<
    ElementRef<HTMLInputElement>
  >;
  private pendingFocus = false;
  private originalValue: any = null;

  constructor(
    public dialogRef: MatDialogRef<LotDetailDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: LotDetailDialogData,
  ) {}

  ngOnInit(): void {
    this.rows = this.data.rows.map((r) => ({ ...r }));

    // Mock data for testing — xóa khi có data thật
    if (this.rows.length === 0) {
      this.rows = [
        {
          sapCode: "00098081",
          productName: "Module DR-ML MXL1142 24W-TC (Yankon)",
          reelId: "26022808503400029",
          partNumber: "00098081_V1.1",
          lot: "NXS-Part",
          vendor: "RD",
          initialQuantity: 10000,
          userData1: "10000",
          userData2: "10000",
          userData3: "10000",
          userData4: "10000",
          userData5: "10000",
          msl: "2a",
          storageUnit: "EA",
          manufacturingDate: "20250603",
          expirationDate: "20271203",
        },
        {
          sapCode: "00098081",
          productName: "Module DR-ML MXL1142 24W-TC (Yankon)",
          reelId: "26022808503400030",
          partNumber: "00098081_V1.1",
          lot: "NXS-Part",
          vendor: "RD",
          initialQuantity: 10000,
          userData1: "10000",
          userData2: "",
          userData3: "",
          userData4: "10000",
          userData5: "",
          msl: "2a",
          storageUnit: "EA",
          manufacturingDate: "20250603",
          expirationDate: "20271203",
        },
        {
          sapCode: "00098081",
          productName: "Module DR-ML MXL1142 24W-TC (Yankon)",
          reelId: "26022808503400028",
          partNumber: "00098081_V1.1",
          lot: "NXS-Part",
          vendor: "RD",
          initialQuantity: 10000,
          userData1: "",
          userData2: "10000",
          userData3: "10000",
          userData4: "",
          userData5: "10000",
          msl: "3",
          storageUnit: "EA",
          manufacturingDate: "20250501",
          expirationDate: "20271101",
        },
      ];
    }

    this.columns.forEach((col) => (this.bulkValues[col.key] = ""));
  }

  ngAfterViewChecked(): void {
    if (this.pendingFocus && this.cellInputs.length > 0) {
      const input = this.cellInputs.last?.nativeElement;
      if (input) {
        input.focus();
        input.select();
        this.pendingFocus = false;
      }
    }
  }

  // ==================== BULK APPLY ====================

  applyColumn(key: string): void {
    const value = this.bulkValues[key];
    if (value === "" || value === null || value === undefined) {
      return;
    }
    this.rows = this.rows.map((row) => ({ ...row, [key]: value }));
  }

  // ==================== INLINE EDIT ====================

  startEdit(rowIndex: number, colKey: string): void {
    this.originalValue = this.rows[rowIndex][colKey];
    this.editingCell = { row: rowIndex, col: colKey };
    this.pendingFocus = true;
  }

  stopEdit(): void {
    this.editingCell = null;
    this.originalValue = null;
  }

  cancelEdit(rowIndex: number, colKey: string): void {
    if (this.originalValue !== null) {
      this.rows[rowIndex][colKey] = this.originalValue;
    }
    this.stopEdit();
  }

  moveEditNext(rowIndex: number, colKey: string, event: KeyboardEvent): void {
    event.preventDefault();
    const editableCols = this.columns.filter((c) => c.editable);
    const editableIdx = editableCols.findIndex(
      (c) => (c.key as string) === colKey,
    );

    if (editableIdx < editableCols.length - 1) {
      this.startEdit(rowIndex, editableCols[editableIdx + 1].key as string);
    } else if (rowIndex < this.rows.length - 1) {
      this.startEdit(rowIndex + 1, editableCols[0].key as string);
    } else {
      this.stopEdit();
    }
  }

  // ==================== DIALOG ACTIONS ====================

  onSave(): void {
    this.dialogRef.close(this.rows);
  }

  onClose(): void {
    this.dialogRef.close(null);
  }
}
