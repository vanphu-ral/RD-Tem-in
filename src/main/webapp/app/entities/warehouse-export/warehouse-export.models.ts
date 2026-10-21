/** 1 vật tư trong yêu cầu lĩnh vật tư */
export interface ExportRequestItem {
  materialCode: string;
  materialName: string;
  requiredQty: number;
  scannedQty: number;
  warehouseCode: string;
  /** Vị trí kho gợi ý lấy hàng */
  location?: string;
  /** Khuyến nghị Planning: các thùng (ReelID) được chỉ định cho vật tư này */
  reels?: ExportPlannedReel[];
  /** Khuyến nghị Planning: partNumberCode (vd 520) — đối chiếu với partId của kho */
  partCode?: string;
  /** Khuyến nghị Planning: part thay thế có cuộn được chỉ định cho part này */
  altPartCodes?: string[];
  /** Xuất chuyển kho: part number NCC (uPartNumber của WTQ1, nhiều mã cách nhau dấu phẩy) */
  partNumbers?: string;
  /** Đơn vị tính (uomCode) */
  uom?: string;
  /** Xuất chuyển kho: lineNum WTQ1 (dòng đầu tiên của mã khi gộp nhiều dòng) */
  lineNum?: number;
  /** Xuất chuyển kho: mã kho SAP chuyển đến (whsCode của WTQ1, vd 05) */
  toWhsCode?: string;
}

/** Thùng được Planning khuyến nghị xuất (materialId = ReelID) */
export interface ExportPlannedReel {
  reelId: string;
  /** dnlnvlDetailDetailId — id cập nhật trạng thái scan (PATCH /dnlnvl-detail-details) */
  detailDetailId?: number;
  /** partNumberCode thực của cuộn (khác part của dòng = part thay thế) */
  partCode?: string;
  /** SL theo Planning (materialQuantity) */
  quantity: number;
  /** Đã scan thùng này (scan trước đó theo BE hoặc vừa scan ở màn hình này) */
  scanned: boolean;
  /** BE đã ghi nhận scan từ trước (dnlnvl-detail-details.scanCheck = 1) */
  scannedBefore?: boolean;
  /**
   * Thông tin thùng trong kho (/api/inventory/{reelId}):
   * undefined = đang tra, null = không có trong kho
   */
  inventory?: ExportReelInventory | null;
  /** Tra tồn kho lỗi */
  inventoryError?: boolean;
}

/** Thông tin 1 thùng trong kho — GET /api/inventory/{reelId} */
export interface ExportReelInventory {
  /** Mã part (partId, vd 7271) */
  partId: string;
  /** Part number (vd BP2866FJN) */
  partNumber: string;
  /** SL tồn khả dụng (availableQuantity) */
  availableQty: number;
  /** SL hiện tại (quantity) */
  quantity: number;
  /** SL ban đầu của thùng (initialQuantity) */
  initialQty: number;
  /** Trạng thái tính toán (calculatedStatus, vd Available / Consumed) */
  status: string;
  location: string;
  /** id vị trí — tra kho (khu vực) chứa vị trí */
  locationId: number;
  /** Mã SAP (phần trước "-" của userData4, vd 00075389-07092026 → 00075389) */
  sapCode: string;
  lot: string;
  /** dd/MM/yyyy */
  expirationDate: string;
  /** dd/MM/yyyy */
  manufacturingDate: string;
  /** inventoryId — id cập nhật tồn kho (POST api/request) */
  inventoryId: number | null;
  /** HSD gốc từ /inventory (epoch) — gửi lại khi cập nhật */
  expirationRaw: string;
  /** Bản ghi gốc /api/inventory/{reelId} — lưu thùng vào phiên (warehouse-transfer-details) */
  raw?: Record<string, unknown>;
}

/** Yêu cầu lĩnh vật tư */
export interface ExportRequest {
  code: string;
  items: ExportRequestItem[];
  /** Khuyến nghị Planning: mã SAP thành phẩm (phần đầu profileCode) */
  sapCode?: string;
  /** Khuyến nghị Planning: lot sản xuất / WO SAP */
  lotNumber?: string;
  sapWo?: string;
  /** Khuyến nghị Planning: tổng hợp theo WO từ /dnlnvl/full (BE tính) */
  planningSummary?: ExportPlanningSummary;
}

/** Khuyến nghị Planning: SL cần / số cuộn đã scan / SL đã scan của cả WO */
export interface ExportPlanningSummary {
  /** requiredQuantity */
  requiredQty: number;
  /** scannedCount — số thùng/cuộn đã scan */
  scannedCount: number;
  /** scannedQuantity */
  scannedQty: number;
}

/** Thùng đã scan để xuất */
export interface ExportScannedBox {
  reelId: string;
  materialCode: string;
  quantity: number;
  lot: string;
  /** dd/MM/yyyy */
  mfgDate: string;
  palletCode: string;
  location: string;
  /** Khuyến nghị Planning: SL khuyến nghị của thùng (materialQuantity) */
  plannedQty?: number;
  /** Khuyến nghị Planning: thùng không có trong danh sách khuyến nghị (bổ sung cùng part) */
  supplementary?: boolean;
  /** Thông tin tồn kho (/api/inventory/{reelId}) */
  inventoryQty?: number;
  expirationDate?: string;
  inventoryStatus?: string;
  /** Part number NCC của thùng (partNumber) */
  partNumber?: string;
  /** inventoryId / HSD gốc — dùng khi Xác nhận xuất (cập nhật SL + vị trí) */
  inventoryId?: number;
  expirationRaw?: string;
  /**
   * Đã Gửi — Khuyến nghị Planning: PATCH scanCheck = 1 (mới được Xác nhận xuất);
   * Xuất chuyển kho theo phiên: đã lưu vào phiên (POST /warehouse-transfer-details)
   */
  sent?: boolean;
  /** Bản ghi gốc /api/inventory/{reelId} */
  inventoryRaw?: Record<string, unknown>;
  /** Xuất chuyển kho: mã kho SAP chuyển đến (whsCode của dòng vật tư, vd 05) */
  destWhsCode?: string;
  /** Xuất chuyển kho: vị trí hiện tại của thùng (location = vị trí mới cần chuyển đến) */
  sourceLocation?: string;
}

/** Pallet đã scan để xuất (kèm các thùng trên pallet) */
export interface ExportScannedPallet {
  palletCode: string;
  location: string;
  boxes: ExportScannedBox[];
  /** Tổng số ReelID trên pallet (kể cả thùng không tra được trong kho) */
  reelCount?: number;
}

/** Lịch sử xuất kho — 1 vật tư trong phiếu xuất */
export interface ExportHistoryItem {
  materialCode: string;
  materialName: string;
  requiredQty: number;
  exportedQty: number;
  warehouseCode: string;
  boxes: ExportScannedBox[];
}

/** Lịch sử xuất kho — 1 phiếu xuất (theo mã yêu cầu) */
export interface ExportHistoryRecord {
  id: number;
  requestCode: string;
  /** ISO datetime */
  exportedAt: string;
  operator: string;
  warehouse: string;
  items: ExportHistoryItem[];
}

export interface ExportHistoryFilter {
  requestCode: string;
  /** yyyy-MM-dd */
  fromDate: string;
  /** yyyy-MM-dd */
  toDate: string;
  warehouse: string;
  operator: string;
}

export interface ExportHistoryPage {
  items: ExportHistoryRecord[];
  total: number;
  /** Thống kê theo bộ lọc (toàn bộ, không chỉ trang hiện tại) */
  slipCount: number;
  requestCount: number;
  totalQty: number;
}

/** Thông tin gửi duyệt khi xác nhận xuất kho */
export interface ExportApproval {
  /** Mã kho duyệt (vd 01) */
  warehouseCode: string;
  approver: string;
  warehouseManager: string;
}
