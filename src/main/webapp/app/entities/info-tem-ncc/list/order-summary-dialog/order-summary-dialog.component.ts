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

  // get totalSessions(): number {
  //   return this.item.sessions?.length ?? 0;
  // }

  // get totalItemCount(): number {
  //   return (
  //     this.item.sessions?.reduce((sum, s) => sum + (s.itemCount ?? 0), 0) ?? 0
  //   );
  // }

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
        reelIds: Set<string>;
        sessionIds: Set<number>;
      }
    >();

    const firstTransaction = raw.importVendorTemTransactions[0];
    (firstTransaction?.poDetails ?? []).forEach((pd) => {
      sapMap.set(pd.sapCode, {
        sapName: pd.sapName,
        orderQty: pd.totalQuantity ?? 0,
        scannedQty: 0,
        reelIds: new Set(),
        sessionIds: new Set(),
      });
    });

    raw.importVendorTemTransactions.forEach((t) => {
      (t.poDetails ?? []).forEach((pd) => {
        if (!sapMap.has(pd.sapCode)) {
          sapMap.set(pd.sapCode, {
            sapName: pd.sapName,
            orderQty: pd.totalQuantity ?? 0,
            scannedQty: 0,
            reelIds: new Set(),
            sessionIds: new Set(),
          });
        }
        const entry = sapMap.get(pd.sapCode)!;
        const details = pd.vendorTemDetails ?? [];

        details.forEach((v) => {
          const rid = (v.reelId ?? "").trim();
          if (rid && !entry.reelIds.has(rid)) {
            entry.reelIds.add(rid);
            entry.scannedQty += v.initialQuantity ?? 0;
          }
        });

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
      itemCount: v.reelIds.size,
      sessionCount: v.sessionIds.size,
    }));
  }

  get totalItemCount(): number {
    return this.sapSummary.reduce((sum, s) => sum + s.itemCount, 0);
  }

  get totalScannedQty(): number {
    return this.sapSummary.reduce((sum, s) => sum + s.scannedQty, 0);
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

  get scanDateSummary(): { date: string; qty: number }[] {
    const raw = this.item._raw;
    if (!raw?.importVendorTemTransactions) {
      return [];
    }

    const dateMap = new Map<string, number>();

    raw.importVendorTemTransactions.forEach((t) => {
      const dateKey = t.createdAt
        ? new Date(t.createdAt).toLocaleDateString("vi-VN", {
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
          })
        : "Không rõ";

      if (!dateMap.has(dateKey)) {
        dateMap.set(dateKey, 0);
      }

      const qty = (t.poDetails ?? [])
        .flatMap((pd) => pd.vendorTemDetails ?? [])
        .reduce((sum, v) => sum + (v.initialQuantity ?? 0), 0);

      dateMap.set(dateKey, dateMap.get(dateKey)! + qty);
    });

    return Array.from(dateMap.entries())
      .map(([date, qty]) => ({ date, qty }))
      .sort((a, b) => {
        const parse = (s: string): number => {
          const [d, m, y] = s.split("/");
          return new Date(+y, +m - 1, +d).getTime();
        };
        return parse(a.date) - parse(b.date);
      });
  }

  get totalSessions(): number {
    const raw = this.item._raw;
    if (!raw?.importVendorTemTransactions) {
      return 0;
    }

    const uniqueDates = new Set(
      raw.importVendorTemTransactions.map((t) =>
        t.createdAt
          ? new Date(t.createdAt).toLocaleDateString("vi-VN", {
              day: "2-digit",
              month: "2-digit",
              year: "numeric",
            })
          : "Không rõ",
      ),
    );

    return uniqueDates.size;
  }

  get totalScanDateQty(): number {
    return this.scanDateSummary.reduce((sum, d) => sum + d.qty, 0);
  }

  // get totalScannedQty(): number {
  //   return (
  //     this.item.sessions?.reduce((sum, s) => sum + (s.totalScanQty ?? 0), 0) ??
  //     0
  //   );
  // }

  // dung SessionItem thay vi SessionSummary
  get sessions(): SessionItem[] {
    return this.item.sessions ?? [];
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
