import { Component, Inject } from "@angular/core";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from "@angular/material/dialog";
import { Observable } from "rxjs";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { resolveHttpErrorMessage } from "app/entities/generate-tem-in/service/receiving-supplies.service";
import { VendorLabelInfoDto } from "../../services/info-tem-ncc.service";
import {
  SendBoxEntry,
  SendResult,
  VendorLabelSendService,
} from "../../services/vendor-label-send.service";
import { openSendConfirm } from "../../shared/send-confirm-dialog/send-confirm-dialog.component";

/** 1 dòng = 1 thùng */
export interface SendSystemLot {
  id: string;
  lotNumber: string;
  reelId: string;
  quantity: number;
  /** Đã gửi SAP (sapSendStatus) */
  sent: boolean;
  /** Đã gửi PanaCIM (panaSendStatus) */
  panaSent?: boolean;
  selected: boolean;
  /** Bản ghi thùng gốc — dùng để gửi */
  record?: VendorLabelInfoDto;
}

export interface SendSystemMaterial {
  id: string;
  materialName: string;
  materialCode: string;
  partNumber?: string;
  warehouseCode: string;
  receivedQty: number;
  poQty: number;
  boxCount: number;
  status: "importing" | "waiting" | "enough";
  selected: boolean;
  expanded: boolean;
  lots: SendSystemLot[];
}

export interface SendSystemDialogData {
  poCode: string;
  warehouseKeeper: string;
  vendorName: string;
  vehicleNumber: string;
  materialCount: number;
  materials: SendSystemMaterial[];
}

export interface SendSystemDialogResult {
  /** Có gửi thành công ít nhất 1 lần → trang ngoài tải lại dữ liệu */
  changed: boolean;
}

@Component({
  selector: "jhi-send-system-dialog",
  templateUrl: "./send-system-dialog.component.html",
  styleUrls: ["./send-system-dialog.component.scss"],
  standalone: false,
})
export class SendSystemDialogComponent {
  materials: SendSystemMaterial[] = [];
  isSending = false;

  private changed = false;

  constructor(
    private dialogRef: MatDialogRef<
      SendSystemDialogComponent,
      SendSystemDialogResult
    >,
    @Inject(MAT_DIALOG_DATA) public data: SendSystemDialogData,
    private notificationService: NotificationService,
    private vendorLabelSendService: VendorLabelSendService,
    private dialog: MatDialog,
  ) {
    this.materials = (data?.materials ?? []).map((m) => ({
      ...m,
      lots: m.lots.map((l) => ({ ...l })),
    }));
    for (const m of this.materials) {
      this.syncMaterialSelected(m);
    }
  }

  onBack(): void {
    if (this.isSending) {
      return;
    }
    this.dialogRef.close({ changed: this.changed });
  }

  toggleMaterial(mat: SendSystemMaterial): void {
    mat.expanded = !mat.expanded;
  }

  /** Tổng SL các thùng trong vật tư */
  boxQty(mat: SendSystemMaterial): number {
    return mat.lots.reduce((s, l) => s + (l.quantity || 0), 0);
  }

  sentCount(mat: SendSystemMaterial): number {
    return mat.lots.filter((l) => l.sent).length;
  }

  /** Thùng còn gửi được (chưa gửi SAP hoặc chưa gửi PanaCIM) */
  isLotSelectable(lot: SendSystemLot): boolean {
    return !lot.sent || !lot.panaSent;
  }

  canSelect(mat: SendSystemMaterial): boolean {
    return mat.lots.some((l) => this.isLotSelectable(l));
  }

  onMaterialCheck(mat: SendSystemMaterial, checked: boolean): void {
    mat.selected = checked;
    for (const lot of mat.lots) {
      if (this.isLotSelectable(lot)) {
        lot.selected = checked;
      }
    }
  }

  onLotCheck(
    mat: SendSystemMaterial,
    lot: SendSystemLot,
    checked: boolean,
  ): void {
    lot.selected = checked;
    this.syncMaterialSelected(mat);
  }

  materialStatusLabel(status: SendSystemMaterial["status"]): string {
    if (status === "importing") {
      return "Đang nhập";
    }
    if (status === "enough") {
      return "Đủ SL";
    }
    return "Chưa nhập";
  }

  progressPercent(mat: SendSystemMaterial): number {
    if (!mat.poQty) {
      return 0;
    }
    return Math.min(100, Math.round((mat.receivedQty / mat.poQty) * 100));
  }

  /** Gửi SAP các thùng đã chọn, chưa gửi SAP (post-goods-receipt-po) */
  onSendSap(): void {
    this.sendTo(
      "SAP",
      (lot) => !lot.sent,
      (entries) => this.vendorLabelSendService.sendSap(entries),
      (lot) => (lot.sent = true),
    );
  }

  /** Gửi PanaCIM các thùng đã chọn, chưa gửi PanaCIM (CSV → /api/csv-upload) */
  onSendPanacim(): void {
    this.sendTo(
      "PanaCIM",
      (lot) => !lot.panaSent,
      (entries) =>
        this.vendorLabelSendService.sendPanacim(entries, this.data.poCode),
      (lot) => (lot.panaSent = true),
    );
  }

  onSendWms(): void {
    this.notificationService.info("Gửi WMS TQ — chưa có API, sẽ nối sau.");
  }

  trackMaterial(_: number, row: SendSystemMaterial): string {
    return row.id;
  }

  trackLot(_: number, row: SendSystemLot): string {
    return row.id;
  }

  private sendTo(
    target: string,
    notSentYet: (lot: SendSystemLot) => boolean,
    send: (entries: SendBoxEntry[]) => Observable<SendResult>,
    markSent: (lot: SendSystemLot) => void,
  ): void {
    if (this.isSending) {
      return;
    }
    const picked: SendSystemLot[] = [];
    const entries: SendBoxEntry[] = [];
    let skipped = 0;
    for (const mat of this.materials) {
      for (const lot of mat.lots) {
        if (!lot.selected || !lot.record?.id) {
          continue;
        }
        // Thùng đã gửi hệ thống này (sap/panaSendStatus = true) → bỏ qua
        if (!notSentYet(lot)) {
          skipped++;
          continue;
        }
        picked.push(lot);
        entries.push({
          record: lot.record,
          sapCode: mat.materialCode,
          partNumber: mat.partNumber ?? "",
          poCode: this.data.poCode,
          vendorCode: this.data.vendorName,
        });
      }
    }
    if (!entries.length) {
      this.notificationService.warning(
        skipped
          ? `Các thùng đã chọn đều đã gửi ${target}.`
          : `Chọn ít nhất một thùng chưa gửi ${target}.`,
      );
      return;
    }
    openSendConfirm(
      this.dialog,
      target,
      this.data.poCode,
      entries,
      skipped,
    ).subscribe((ok) => {
      if (!ok) {
        return;
      }
      this.isSending = true;
      send(entries).subscribe({
        next: (res) => {
          this.isSending = false;
          this.changed = true;
          for (const lot of picked) {
            markSent(lot);
            lot.selected = false;
          }
          for (const mat of this.materials) {
            this.syncMaterialSelected(mat);
          }
          if (res.statusSaved) {
            this.notificationService.success(
              `Đã gửi ${target} thành công (${res.count} thùng).`,
            );
          } else {
            this.notificationService.warning(
              `Đã gửi ${target} (${res.count} thùng) nhưng lưu trạng thái đã gửi thất bại.`,
            );
          }
        },
        error: (err: unknown) => {
          this.isSending = false;
          this.notificationService.error(
            resolveHttpErrorMessage(err, `Gửi ${target} thất bại.`),
          );
        },
      });
    });
  }

  private syncMaterialSelected(mat: SendSystemMaterial): void {
    const selectable = mat.lots.filter((l) => this.isLotSelectable(l));
    mat.selected = selectable.length > 0 && selectable.every((l) => l.selected);
  }
}
