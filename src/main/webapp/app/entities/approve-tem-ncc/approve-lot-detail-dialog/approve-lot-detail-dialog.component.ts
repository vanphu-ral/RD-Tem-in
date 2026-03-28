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
import { AccountService } from "app/core/auth/account.service";
import {
  CreateVendorTemDetailPayload,
  ManagerTemNccService,
} from "app/entities/list-material/services/info-tem-ncc.service";
import { NotificationService } from "app/entities/list-material/services/notification.service";

// ==================== INTERFACES ====================

export interface LotDetailRow {
  id: number;
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
  vendorQrCode?: string;
  status?: string;
  createdBy?: string;
  createdAt?: string;
  updatedBy?: string;
  poDetailId: number;
  importVendorTemTransactionsId: number;
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
  templateUrl: "./approve-lot-detail-dialog.component.html",
  styleUrls: ["./approve-lot-detail-dialog.component.scss"],
})
export class ApproveLotDetailDialogComponent
  implements OnInit, AfterViewChecked
{
  columns: ColumnDef[] = [
    { key: "reelId", label: "ReelId", minWidth: 190, editable: true },
    { key: "sapCode", label: "Mã SAP", minWidth: 120, editable: true },
    {
      key: "productName",
      label: "Tên hàng hóa",
      minWidth: 220,
      editable: true,
    },
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
    public dialogRef: MatDialogRef<ApproveLotDetailDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: LotDetailDialogData,
    private managerTemNccService: ManagerTemNccService,
    private accountService: AccountService,
    private notificationService: NotificationService,
  ) {}

  ngOnInit(): void {
    this.rows = this.data.rows.map((r) => ({ ...r }));

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
    const now = new Date().toISOString();

    const payload: CreateVendorTemDetailPayload[] = this.rows.map((row) => ({
      id: row.id,
      reelId: row.reelId ?? "",
      partNumber: row.partNumber ?? "",
      vendor: row.vendor ?? "",
      lot: row.lot ?? "",
      userData1: row.userData1 ?? "",
      userData2: row.userData2 ?? "",
      userData3: row.userData3 ?? "",
      userData4: row.userData4 ?? "",
      userData5: row.userData5 ?? "",
      initialQuantity: Number(row.initialQuantity) || 0,
      msdLevel: row.msl ?? "",
      msdInitialFloorTime: row.msdInitialFloorTime ?? "",
      msdBagSealDate: row.msdBagSealDate ?? "",
      marketUsage: row.marketUsage ?? "",
      quantityOverride: Number(row.quantityOverride) || 0,
      shelfTime: row.shelfTime ?? "",
      spMaterialName: row.spMaterialName ?? "",
      warningLimit: row.warningLimit ?? "",
      maximumLimit: row.maximumLimit ?? "",
      comments: row.comments ?? "",
      warmupTime: row.warmupTime ?? "",
      storageUnit: row.storageUnit ?? "",
      subStorageUnit: row.subStorageUnit ?? "",
      locationOverride: row.locationOverride ?? "",
      expirationDate: row.expirationDate ?? "",
      manufacturingDate: row.manufacturingDate ?? "",
      partClass: row.partClass ?? "",
      sapCode: row.sapCode ?? "",
      vendorQrCode: row.vendorQrCode ?? "",
      status: row.status ?? "NEW",
      createdBy: row.createdBy ?? "",
      createdAt: row.createdAt ?? now,
      updatedBy: row.updatedBy ?? "",
      updatedAt: now,
      poDetailId: row.poDetailId,
      importVendorTemTransactionsId: row.importVendorTemTransactionsId,
    }));

    this.managerTemNccService.batchUpdateVendorTemDetails(payload).subscribe({
      next: () => {
        this.notificationService.success("Cập nhật vật tư thành công.");
        this.dialogRef.close(true);
      },
      error: () => {
        this.notificationService.error("Cập nhật vật tư thất bại.");
      },
    });
  }

  onClose(): void {
    this.dialogRef.close(null);
  }
}
