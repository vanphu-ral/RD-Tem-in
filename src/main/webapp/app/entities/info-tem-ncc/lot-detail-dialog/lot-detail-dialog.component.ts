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
import { buildDefaultUserData4 } from "../shared/scan-item-columns.util";

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
  sapName?: string;
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
  /** Mã PO — dùng tự điền cột Mã PO (userData5). */
  poCode?: string;
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
    { key: "reelId", label: "ReelId", minWidth: 0, editable: true },
    { key: "sapCode", label: "Mã SAP", minWidth: 0, editable: true },
    { key: "sapName", label: "Tên hàng hóa", minWidth: 0, editable: false },
    { key: "partNumber", label: "Part Number", minWidth: 0, editable: true },
    { key: "lot", label: "Lot", minWidth: 0, editable: true },
    { key: "vendor", label: "Vendor", minWidth: 0, editable: true },
    {
      key: "initialQuantity",
      label: "Quantity",
      minWidth: 0,
      editable: true,
    },
    { key: "userData1", label: "Rank Áp", minWidth: 0, editable: true },
    { key: "userData2", label: "Rank màu", minWidth: 0, editable: true },
    { key: "userData3", label: "Rank Quang", minWidth: 0, editable: true },
    { key: "userData4", label: "User Data 4", minWidth: 0, editable: true },
    { key: "userData5", label: "Mã PO", minWidth: 0, editable: true },
    { key: "msl", label: "MSL", minWidth: 0, editable: true },
    { key: "storageUnit", label: "Kho", minWidth: 0, editable: true },
    {
      key: "manufacturingDate",
      label: "NSX",
      minWidth: 0,
      editable: true,
    },
    {
      key: "expirationDate",
      label: "HSD",
      minWidth: 0,
      editable: true,
    },
  ];

  rows: LotDetailRow[] = [];

  get colWidthPercent(): number {
    return 100 / Math.max(1, this.columns.length);
  }

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
    private managerTemNccService: ManagerTemNccService,
    private accountService: AccountService,
    private notificationService: NotificationService,
  ) {}

  ngOnInit(): void {
    const poCode = (this.data.poCode ?? "").trim();
    this.rows = this.data.rows.map((r) => {
      const row = { ...r };
      row.userData1 = (row.userData1 ?? "").trim() || "NO";
      row.userData2 = (row.userData2 ?? "").trim() || "NO";
      row.userData3 = (row.userData3 ?? "").trim() || "NO";
      row.userData4 = (row.userData4 ?? "").trim() || this.buildUserData4(row);
      row.userData5 = (row.userData5 ?? "").trim() || poCode;
      row.msl = (row.msl ?? "").trim() || "1";
      return row;
    });

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
    // Chặn số âm cho cột số lượng
    if (key === "initialQuantity" && Number(value) < 0) {
      this.notificationService.warning("Không được nhập số âm.");
      this.bulkValues[key] = 0;
      return;
    }
    this.rows = this.rows.map((row) => {
      const next = { ...row, [key]: value };
      if (key === "sapCode" || key === "manufacturingDate") {
        next.userData4 = this.buildUserData4(next);
      }
      return next;
    });
  }

  // ==================== INLINE EDIT ====================

  startEdit(rowIndex: number, colKey: string): void {
    this.originalValue = this.rows[rowIndex][colKey];
    this.editingCell = { row: rowIndex, col: colKey };
    this.pendingFocus = true;
  }
  onQtyInput(event: Event, row: LotDetailRow, field: string): void {
    const val = Number((event.target as HTMLInputElement).value);
    if (val < 0) {
      (event.target as HTMLInputElement).value = "0";
      row[field] = 0;
      this.notificationService.warning("Không được nhập số âm.");
    }
  }
  stopEdit(): void {
    if (this.editingCell) {
      const row = this.rows[this.editingCell.row];
      if (
        this.editingCell.col === "sapCode" ||
        this.editingCell.col === "manufacturingDate"
      ) {
        row.userData4 = this.buildUserData4(row);
      }
    }
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
    const invalidRow = this.rows.find((row) => Number(row.initialQuantity) < 0);
    if (invalidRow) {
      this.notificationService.warning(
        "Không được nhập số âm cho trường Quantity.",
      );
      return;
    }

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
  /** User Data 4 = Mã SAP + "-" + MFGDate (ddMMyyyy). */
  private buildUserData4(row: {
    sapCode?: string;
    manufacturingDate?: string;
  }): string {
    const sap = (row.sapCode ?? "").trim();
    const digits = String(row.manufacturingDate ?? "").replace(/\D/g, "");
    let ymd = digits;
    if (digits.length === 8 && Number(digits.slice(0, 4)) <= 1900) {
      // ddMMyyyy -> yyyyMMdd
      ymd = `${digits.slice(4, 8)}${digits.slice(2, 4)}${digits.slice(0, 2)}`;
    }
    return buildDefaultUserData4({
      sapCode: sap,
      manufacturingDate: ymd,
    });
  }
}
