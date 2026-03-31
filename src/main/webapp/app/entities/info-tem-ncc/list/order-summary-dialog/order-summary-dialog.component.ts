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

  get sapSummary(): {
    sapCode: string;
    sapName: string;
    orderQty: number;
    scannedQty: number;
    itemCount: number;
    sessionCount: number;
  }[] {
    const raw = this.item._raw;
    if (!raw?.importVendorTemTransactions) {
      return [];
    }

    const sapMap = new Map<
      string,
      {
        sapName: string;
        orderQty: number;
        scannedQty: number;
        itemCount: number;
        sessionIds: Set<number>;
      }
    >();

    raw.importVendorTemTransactions.forEach((t) => {
      (t.poDetails ?? []).forEach((pd) => {
        const key = pd.sapCode;
        if (!sapMap.has(key)) {
          sapMap.set(key, {
            sapName: pd.sapName,
            orderQty: pd.totalQuantity ?? 0,
            scannedQty: 0,
            itemCount: 0,
            sessionIds: new Set(),
          });
        }
        const entry = sapMap.get(key)!;
        const details = pd.vendorTemDetails ?? [];
        entry.scannedQty += details.reduce(
          (s, v) => s + (v.initialQuantity ?? 0),
          0,
        );
        entry.itemCount += details.length;
        if (details.length > 0) {
          entry.sessionIds.add(t.id);
        }
      });
    });

    return Array.from(sapMap.entries()).map(([sapCode, v]) => ({
      sapCode,
      sapName: v.sapName,
      orderQty: v.orderQty,
      scannedQty: v.scannedQty,
      itemCount: v.itemCount,
      sessionCount: v.sessionIds.size,
    }));
  }

  get totalOrderQty(): number {
    // Lấy từ 1 transaction đầu tiên vì orderQty không nhân bội
    const raw = this.item._raw;
    const firstTransaction = raw?.importVendorTemTransactions?.[0];
    return (firstTransaction?.poDetails ?? []).reduce(
      (sum, pd) => sum + (pd.totalQuantity ?? 0),
      0,
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
