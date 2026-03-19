import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { SessionItem, TemNccItem } from "../approve-tem-ncc.component";

export interface OrderSummaryData {
  item: TemNccItem;
}

export interface SessionSummary {
  importDate: string;
  warehouse: string;
  warehouseType: string;
  totalQty: number;
  itemCount: number;
}

@Component({
  selector: "jhi-order-summary-dialog",
  templateUrl: "./order-summary-dialog.component.html",
  styleUrls: ["./order-summary-dialog.component.scss"],
})
export class OrderSummaryDialogComponent {
  item: TemNccItem;

  constructor(
    private dialogRef: MatDialogRef<OrderSummaryDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: OrderSummaryData,
  ) {
    this.item = data.item;
  }

  get totalSessions(): number {
    return this.item.sessions?.length ?? 0;
  }

  get totalScannedQty(): number {
    return (
      this.item.sessions?.reduce((sum, s) => sum + (s.totalQty ?? 0), 0) ?? 0
    );
  }

  get totalItemCount(): number {
    return (
      this.item.sessions?.reduce((sum, s) => sum + (s.itemCount ?? 0), 0) ?? 0
    );
  }

  get uniqueWarehouses(): string[] {
    const set = new Set(this.item.sessions?.map((s) => s.warehouse) ?? []);
    return Array.from(set);
  }

  get sessions(): SessionSummary[] {
    return this.item.sessions ?? [];
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
