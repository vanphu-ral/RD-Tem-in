import { MatDialog } from "@angular/material/dialog";
import { VendorNccPrintDialogComponent } from "app/entities/generate-tem-in/receiving-supplies/vendor-ncc-print-dialog/vendor-ncc-print-dialog.component";
import type { VendorNccLabelData } from "app/entities/generate-tem-in/receiving-supplies/vendor-ncc-label-print.util";
import { toText, VendorLabelInfoDto } from "../services/info-tem-ncc.service";

/** Thông tin vật tư / đơn in kèm tem (thùng không có thì lấy ở đây) */
export interface SplitLabelContext {
  sapCode?: string;
  partNumber?: string;
  materialName?: string;
  vendorName?: string;
  invoiceNumber?: string;
  contractCode?: string;
  operator?: string;
}

/** 1 thùng (vendor-label-info) → dữ liệu tem NCC */
export function toVendorNccLabel(
  rec: VendorLabelInfoDto,
  ctx: SplitLabelContext,
): VendorNccLabelData {
  const mfg = toText(rec.manufacturingDate);
  const mfgText = /^\d{8}$/.test(mfg)
    ? `${mfg.slice(6, 8)}/${mfg.slice(4, 6)}/${mfg.slice(0, 4)}`
    : mfg;
  return {
    id: String(rec.id ?? rec.reelId),
    reelId: toText(rec.reelId),
    qrCode: toText(rec.vendorQrCode) || toText(rec.reelId),
    sapCode: toText(rec.sapCode) || (ctx.sapCode ?? ""),
    partNumber: toText(rec.partNumber) || (ctx.partNumber ?? ""),
    vendorItemName: toText(ctx.materialName) || toText(rec.spMaterialName),
    quantity: Number(rec.initialQuantity ?? 0),
    weight: "",
    meas: "",
    poNo: "",
    invoiceNo: ctx.invoiceNumber ?? "",
    boxNo: "",
    vendorName: toText(ctx.vendorName) || toText(rec.vendor),
    madeIn: "",
    color: "",
    operator: ctx.operator ?? "",
    grossNetWeight: "",
    contractNo: ctx.contractCode ?? "",
    batchNo: toText(rec.lot),
    mfgDate: mfgText,
  };
}

/** Mở dialog "In tem nhà cung cấp" (generate-tem-in) cho các thùng */
export function openVendorNccLabelPrint(
  dialog: MatDialog,
  records: VendorLabelInfoDto[],
  ctx: SplitLabelContext,
): void {
  const labels = records.map((r) => toVendorNccLabel(r, ctx));
  if (!labels.length) {
    return;
  }
  dialog.open(VendorNccPrintDialogComponent, {
    width: "100vw",
    height: "100vh",
    maxWidth: "100vw",
    maxHeight: "100vh",
    panelClass: "vendor-ncc-print-dialog-panel",
    data: { labels, requestLabel: "tach-thung" },
  });
}
