import { Component, Inject, OnInit } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { SessionItem, TemNccItem } from "../info-tem-ncc.component";
import {
  ManagerTemNccService,
  PoImportTem,
  ImportVendorTemTransaction,
} from "app/entities/list-material/services/info-tem-ncc.service";

export interface OrderSummaryData {
  item: TemNccItem;
}

export interface SessionSummary {
  importDate: string;
  warehouse: string;
  status: string;
  totalQty: number;
  itemCount: number;
}

@Component({
  selector: "jhi-order-summary-dialog",
  templateUrl: "./order-summary-dialog.component.html",
  styleUrls: ["./order-summary-dialog.component.scss"],
})
export class OrderSummaryDialogComponent implements OnInit {
  item: TemNccItem;
  isLoading = false;
  private detailData: PoImportTem | null = null;

  constructor(
    private dialogRef: MatDialogRef<OrderSummaryDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: OrderSummaryData,
    private managerTemNccService: ManagerTemNccService,
  ) {
    this.item = data.item;
  }

  ngOnInit(): void {
    // nếu đã có detail từ cache (_raw có transactions) thì dùng luôn
    if (this.item._raw?.importVendorTemTransactions?.length) {
      this.detailData = this.item._raw;
      return;
    }
    this.loadDetail();
  }

  get totalSessions(): number {
    return this.detailData?.importVendorTemTransactions?.length ?? 0;
  }

  get sessions(): SessionSummary[] {
    return (this.detailData?.importVendorTemTransactions ?? []).map((t) => ({
      importDate: t.entryDate ?? t.createdAt,
      warehouse: t.storageUnit,
      status: t.status,
      totalQty: (t.poDetails ?? []).reduce(
        (sum, d) => sum + (d.totalQuantity ?? 0),
        0,
      ),
      itemCount: (t.poDetails ?? []).reduce(
        (sum, d) => sum + (d.vendorTemDetails?.length ?? 0),
        0,
      ),
    }));
  }

  get totalProductQty(): number {
    return this.sessions.reduce((sum, s) => sum + (s.totalQty ?? 0), 0);
  }

  get totalItemCount(): number {
    return this.sessions.reduce((sum, s) => sum + (s.itemCount ?? 0), 0);
  }

  get uniqueWarehouses(): string[] {
    const set = new Set(this.sessions.map((s) => s.warehouse).filter(Boolean));
    return Array.from(set);
  }

  onClose(): void {
    this.dialogRef.close();
  }

  private loadDetail(): void {
    this.isLoading = true;
    this.managerTemNccService.getPoImportTemDetail(this.item.id).subscribe({
      next: (detail) => {
        this.detailData = detail;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }
}
