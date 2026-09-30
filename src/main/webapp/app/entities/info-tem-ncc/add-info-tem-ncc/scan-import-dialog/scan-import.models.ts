/** Tab chính: Scan | Import */
export type ScanImportMode = "scan" | "import";

/** Tab danh sách: Thùng | Pallet */
export type ScanListTab = "box" | "pallet";

export interface ScanBoxRow {
  id: string;
  time: string;
  lot: string;
  reelId: string;
  partNumber: string;
  vendor: string;
  quantity: number;
  mfgDate: string;
  hsd: string;
  location: string;
  /** id bản ghi vendor-label-info sau khi lưu thành công */
  dbId?: number;
  sapCode?: string;
}

export interface ScanPalletRow {
  id: string;
  time: string;
  palletCode: string;
  createdBy: string;
  boxCount: number;
  materialTypeCount: number;
  totalQty: number;
  note: string;
  warehouseCode: string;
  locationLabel: string;
  usageLabel: string;
  statusLabel: string;
  boxes: ScanBoxRow[];
}

export interface ScanImportDialogData {
  poCode?: string;
  vendorCode?: string;
  warehouse?: string;
  vehicleNumber?: string;
  contractCode?: string;
  /** Ví dụ kịch bản scan hiển thị trên thanh info */
  scenarioCode?: string;
  mappingConfig?: unknown;
  /** id đơn (delivery notification) — gửi kèm khi lưu vendor-label-infos */
  deliveryNotificationId?: number | null;
  /** Ngày về — fallback ngày sản xuất khi QR không có */
  arrivalDate?: string | Date | null;
  /** ReelID đã có trong đơn — chặn scan trùng */
  existingReelIds?: string[];
  /** id = sapPor1Id của dòng vật tư trong đơn */
  parentItems?: Array<{
    id: number;
    partNumber: string;
    sapCode: string;
    orderQty: number;
    materialName: string;
    poCode?: string;
  }>;
}

export interface ScanImportDialogResult {
  mode: ScanImportMode;
  boxRows: ScanBoxRow[];
  palletRows: ScanPalletRow[];
}

/** Mobile — Chi tiết thông tin: bước điều hướng */
export type MobileInfoStep = "scan" | "pos" | "materials" | "lots";

export interface MobileInfoBox {
  id: string;
  code: string;
  quantity: number;
  vendor: string;
  mfgDate: string;
  palletCode: string;
  missingInfo: boolean;
}

export interface MobileInfoLot {
  id: string;
  lotNumber: string;
  boxCount: number;
  totalQty: number;
  complete: boolean;
  boxes: MobileInfoBox[];
  /** Form fields */
  quantity: number | null;
  po: string;
  location: string;
  warehouseCode: string;
  mfgDate: string;
  userData4: string;
  msl: string;
  rankAp: string;
  rankQuang: string;
  rankMau: string;
  hsd: string;
  expiryMode: "month" | "year";
  expiryOffset: number | null;
}

export interface MobileInfoMaterial {
  id: string;
  materialCode: string;
  reelHint: string;
  materialName: string;
  boxCount: number;
  warehouseCode: string;
  poQty: number;
  location: string;
  receivedQty: number;
  lotCount: number;
  complete: boolean;
  lots: MobileInfoLot[];
}

export interface MobileInfoPo {
  id: string;
  poCode: string;
  warehouseKeeper: string;
  vendorName: string;
  vehicleNumber: string;
  materialCount: number;
  boxCount: number;
  receivedQty: number;
  totalQty: number;
  status: "waiting" | "importing" | "done";
  materials: MobileInfoMaterial[];
}
