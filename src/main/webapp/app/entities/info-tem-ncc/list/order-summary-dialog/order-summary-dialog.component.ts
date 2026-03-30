import { Component, Inject, OnInit } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { SessionItem, TemNccItem } from "../info-tem-ncc.component";
import {
  ManagerTemNccService,
  PoImportTem,
  ImportVendorTemTransaction,
} from "app/entities/list-material/services/info-tem-ncc.service";
import { StatusBadgeService } from "app/entities/list-material/services/status-badge.service";

export interface OrderSummaryData {
  item: TemNccItem;
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
    public statusBadgeService: StatusBadgeService,
  ) {
    this.item = data.item;
  }

  get totalSessions(): number {
    return this.item.sessions?.length ?? 0;
  }

  get totalItemCount(): number {
    return (
      this.item.sessions?.reduce((sum, s) => sum + (s.itemCount ?? 0), 0) ?? 0
    );
  }

  get uniqueWarehouses(): string[] {
    const set = new Set(
      (this.item.sessions ?? []).map((s) => s.warehouse).filter(Boolean),
    );
    return Array.from(set);
  }

  get totalOrderQty(): number {
    return (
      this.item.sessions?.reduce((sum, s) => sum + (s.totalQty ?? 0), 0) ?? 0
    );
  }

  get totalScannedQty(): number {
    return (
      this.item.sessions?.reduce((sum, s) => sum + (s.totalScanQty ?? 0), 0) ??
      0
    );
  }

  // dung SessionItem thay vi SessionSummary
  get sessions(): SessionItem[] {
    return this.item.sessions ?? [];
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
