import { ScannedItem } from "../scan-item-dialog/scan-item-dialog.component";
import { CreateVendorTemDetailPayload } from "app/entities/list-material/services/info-tem-ncc.service";

export interface ScanItemColumnDef {
  key: string;
  label: string;
  width?: string;
  editable?: boolean;
  headerEditable?: boolean;
  required?: boolean;
}

/** Cột hiển thị danh sách scan/import (read-only). */
export const SCAN_ITEM_LIST_COLUMNS: ScanItemColumnDef[] = [
  { key: "reelId", label: "ReelID" },
  { key: "partNumber", label: "Part Number" },
  { key: "initialQuantity", label: "Số lượng" },
  { key: "manufacturingDate", label: "MFG" },
  { key: "lotNumber", label: "Số lô" },
  { key: "itemName", label: "Tên SP" },
  { key: "expirationDate", label: "HSD" },
  { key: "locationOverride", label: "Vị trí" },
  { key: "storageUnit", label: "Mã kho" },
  { key: "sapCode", label: "Mã SAP" },
  { key: "vendor", label: "Vendor" },
  { key: "userData1", label: "Userdata1" },
  { key: "userData2", label: "Userdata2" },
  { key: "userData3", label: "Userdata3" },
  { key: "userData4", label: "Userdata4" },
  { key: "userData5", label: "PO" },
  { key: "msl", label: "MSL" },
];

/** Cột dialog tổng hợp — ReelID, Part, Tên SP, SAP, Vendor chỉ đọc và đứng đầu. */
export const SCAN_AGGREGATE_COLUMNS: ScanItemColumnDef[] = [
  {
    key: "reelId",
    label: "ReelID",
    width: "9%",
    editable: false,
    headerEditable: false,
  },
  {
    key: "partNumber",
    label: "Part Number",
    width: "7%",
    editable: false,
    headerEditable: false,
  },
  {
    key: "itemName",
    label: "Tên SP",
    width: "10%",
    editable: false,
    headerEditable: false,
  },
  {
    key: "sapCode",
    label: "Mã SAP",
    width: "6%",
    editable: false,
    headerEditable: false,
    required: true,
  },
  {
    key: "vendor",
    label: "Vendor",
    width: "5%",
    editable: false,
    headerEditable: false,
  },
  {
    key: "initialQuantity",
    label: "SL",
    width: "4%",
    editable: true,
    headerEditable: true,
  },
  {
    key: "manufacturingDate",
    label: "MFG",
    width: "5%",
    editable: true,
    headerEditable: true,
  },
  {
    key: "lotNumber",
    label: "Số lô",
    width: "6%",
    editable: true,
    headerEditable: true,
  },
  {
    key: "expirationDate",
    label: "HSD",
    width: "5%",
    editable: true,
    headerEditable: true,
    required: true,
  },
  {
    key: "locationOverride",
    label: "Vị trí",
    width: "5%",
    editable: true,
    headerEditable: true,
    required: true,
  },
  {
    key: "storageUnit",
    label: "Mã kho",
    width: "5%",
    editable: true,
    headerEditable: true,
    required: true,
  },
  {
    key: "userData1",
    label: "UD1",
    width: "4%",
    editable: true,
    headerEditable: true,
  },
  {
    key: "userData2",
    label: "UD2",
    width: "4%",
    editable: true,
    headerEditable: true,
  },
  {
    key: "userData3",
    label: "UD3",
    width: "4%",
    editable: true,
    headerEditable: true,
  },
  {
    key: "userData4",
    label: "UD4",
    width: "5%",
    editable: true,
    headerEditable: true,
  },
  {
    key: "userData5",
    label: "PO",
    width: "5%",
    editable: false,
    headerEditable: false,
  },
  {
    key: "msl",
    label: "MSL",
    width: "3%",
    editable: true,
    headerEditable: true,
  },
];

export const SCAN_ITEM_MISSING_FIELDS: {
  key: string;
  label: string;
}[] = [
  { key: "expirationDate", label: "HSD" },
  { key: "locationOverride", label: "Vị trí" },
  { key: "storageUnit", label: "Mã kho" },
  { key: "sapCode", label: "SAP" },
];

/**
 * Trường bắt buộc trước khi gửi SAP / PanaCIM —
 * chuẩn theo các cột dialog tổng hợp (đầy đủ mới được gửi).
 */
export const SEND_REQUIRED_FIELDS: { key: string; label: string }[] = [
  { key: "reelId", label: "ReelID" },
  { key: "partNumber", label: "Part Number" },
  { key: "itemName", label: "Tên SP" },
  { key: "sapCode", label: "Mã SAP" },
  { key: "vendor", label: "Vendor" },
  { key: "initialQuantity", label: "SL" },
  { key: "manufacturingDate", label: "MFG" },
  { key: "lotNumber", label: "Số lô" },
  { key: "expirationDate", label: "HSD" },
  { key: "locationOverride", label: "Vị trí" },
  { key: "storageUnit", label: "Mã kho" },
  { key: "userData1", label: "UD1" },
  { key: "userData2", label: "UD2" },
  { key: "userData3", label: "UD3" },
  { key: "userData4", label: "UD4" },
  { key: "userData5", label: "PO" },
  { key: "msl", label: "MSL" },
];

export const DEFAULT_USERDATA = "NO";
export const DEFAULT_MSL = "1";

export interface AggregateEditRow {
  id: string;
  reelId: string;
  partNumber: string;
  initialQuantity: number | string;
  manufacturingDate: string;
  lotNumber: string;
  itemName: string;
  expirationDate: string;
  locationOverride: string;
  storageUnit: string;
  sapCode: string;
  vendor: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  msl: string;
  [key: string]: string | number;
}

export function getScanItemFieldValue(
  item: Partial<ScannedItem> | AggregateEditRow,
  key: string,
): string | number {
  if (key === "lotNumber") {
    return (
      (item as ScannedItem).lot ?? (item as AggregateEditRow).lotNumber ?? ""
    );
  }
  if (key === "itemName") {
    return (
      (item as ScannedItem).spMaterialName ??
      (item as AggregateEditRow).itemName ??
      ""
    );
  }
  if (key === "msl") {
    return (
      (item as ScannedItem).msdLevel ?? (item as AggregateEditRow).msl ?? ""
    );
  }
  const value = (item as Record<string, unknown>)[key];
  if (value == null) {
    return "";
  }
  return typeof value === "number" ? value : String(value);
}

export function setScanItemFieldValue(
  item: Partial<ScannedItem> & Partial<AggregateEditRow>,
  key: string,
  value: string | number,
): void {
  if (key === "lotNumber") {
    item.lot = String(value);
    item.lotNumber = String(value);
    return;
  }
  if (key === "itemName") {
    item.spMaterialName = String(value);
    item.itemName = String(value);
    return;
  }
  if (key === "msl") {
    item.msdLevel = String(value);
    item.msl = String(value);
    return;
  }
  if (key === "initialQuantity") {
    const n = Number(value);
    item.initialQuantity = Number.isFinite(n) ? n : 0;
    return;
  }
  (item as Record<string, string | number>)[key] = value;
}

export function scannedItemToEditRow(item: ScannedItem): AggregateEditRow {
  return {
    id: item.id,
    reelId: item.reelId ?? "",
    partNumber: item.partNumber ?? "",
    initialQuantity: item.initialQuantity ?? 0,
    manufacturingDate: item.manufacturingDate ?? "",
    lotNumber: item.lot ?? "",
    itemName: item.spMaterialName ?? "",
    expirationDate: item.expirationDate ?? "",
    locationOverride: item.locationOverride ?? "",
    storageUnit: item.storageUnit ?? "",
    sapCode: item.sapCode ?? "",
    vendor: item.vendor ?? "",
    userData1: item.userData1 ?? "",
    userData2: item.userData2 ?? "",
    userData3: item.userData3 ?? "",
    userData4: item.userData4 ?? "",
    userData5: item.userData5 ?? "",
    msl: item.msdLevel ?? "",
  };
}

export function applyEditRowToScannedItem(
  row: AggregateEditRow,
  item: ScannedItem,
): ScannedItem {
  return {
    ...item,
    reelId: row.reelId,
    partNumber: row.partNumber,
    initialQuantity: Number(row.initialQuantity) || 0,
    manufacturingDate: row.manufacturingDate,
    lot: row.lotNumber,
    spMaterialName: row.itemName,
    expirationDate: row.expirationDate,
    locationOverride: row.locationOverride,
    storageUnit: row.storageUnit,
    sapCode: row.sapCode,
    vendor: row.vendor,
    userData1: row.userData1,
    userData2: row.userData2,
    userData3: row.userData3,
    userData4: row.userData4,
    userData5: row.userData5,
    msdLevel: row.msl,
  };
}

export function buildDefaultUserData4(row: {
  sapCode?: string;
  manufacturingDate?: string;
}): string {
  const sap = (row.sapCode ?? "").trim();
  const mfg = (row.manufacturingDate ?? "").replace(/\D/g, "");
  if (sap && mfg.length === 8) {
    return `${sap}-${mfg.slice(6, 8)}${mfg.slice(4, 6)}${mfg.slice(0, 4)}`;
  }
  return sap;
}

export function toDateInputValue(value: string | null | undefined): string {
  const digits = String(value ?? "").replace(/\D/g, "");
  if (digits.length !== 8) {
    return "";
  }
  return `${digits.slice(0, 4)}-${digits.slice(4, 6)}-${digits.slice(6, 8)}`;
}

export function fromDateInputValue(value: string | null | undefined): string {
  const digits = String(value ?? "").replace(/\D/g, "");
  return digits.length === 8 ? digits : "";
}

/** Payload PUT /vendor-tem-details/batch — cùng format nút "Lưu vật tư" (add-info-tem-ncc). */
export function buildVendorTemBatchUpdatePayload(
  vendorTemDetailId: number,
  row: AggregateEditRow,
  options: {
    vendor: string;
    vendorQrCode: string;
    status: string;
    createdBy: string;
    createdAt: string;
    poDetailId: number;
    importVendorTemTransactionsId: number;
    currentUser: string;
  },
): CreateVendorTemDetailPayload {
  const now = new Date().toISOString();
  return {
    id: vendorTemDetailId,
    reelId: row.reelId ?? "",
    partNumber: row.partNumber ?? "",
    vendor: options.vendor ?? "",
    lot: row.lotNumber ?? "",
    userData1: row.userData1 ?? "",
    userData2: row.userData2 ?? "",
    userData3: row.userData3 ?? "",
    userData4: row.userData4 ?? "",
    userData5: row.userData5 ?? "",
    initialQuantity: Number(row.initialQuantity) || 0,
    msdLevel: row.msl ?? "",
    msdInitialFloorTime: "",
    msdBagSealDate: "",
    marketUsage: "",
    quantityOverride: 0,
    shelfTime: "",
    spMaterialName: "",
    warningLimit: "",
    maximumLimit: "",
    comments: "",
    warmupTime: "",
    storageUnit: row.storageUnit ?? "",
    subStorageUnit: "",
    locationOverride: row.locationOverride ?? "",
    expirationDate: row.expirationDate ?? "",
    manufacturingDate: row.manufacturingDate ?? "",
    partClass: "",
    sapCode: row.sapCode ?? "",
    vendorQrCode: options.vendorQrCode ?? "",
    status: options.status ?? "NEW",
    createdBy: options.createdBy || options.currentUser,
    createdAt: options.createdAt || now,
    updatedBy: options.currentUser,
    updatedAt: now,
    poDetailId: options.poDetailId,
    importVendorTemTransactionsId: options.importVendorTemTransactionsId,
  };
}
