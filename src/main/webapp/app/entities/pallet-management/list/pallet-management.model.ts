/** Bản ghi GET /api/pallet-mngts */
export interface PalletItem {
  id: number;
  serialPallet: string;
  locationName: string | null;
  numberOfBox: number | null;
  totalQuantity: number | null;
  status: string | null;
  note: string | null;
  createAt: string | null;
  createBy: string | null;
  updatedAt: string | null;
  updatedBy: string | null;
}

/** Thùng trong pallet — GET /api/pallet-mngts/serial-pallet/{serialPallet} */
export interface PalletBoxItem {
  reelId: string;
  productName: string;
  partNumber: string;
  lotNumber: string;
  quantity: number | null;
  productionDate: string;
  expiryDate: string;
}

export type PalletLabelSize = "100x100" | "40x100";
export type PalletLabelType = "QR" | "BARCODE";

export interface PrintPalletDialogData {
  pallets: PalletItem[];
  /** Pre-select these ids when mở từ nút in 1 dòng */
  preselectedIds?: number[];
}

export interface PrintPalletDialogResult {
  labelSize: PalletLabelSize;
  labelsPerRow: number;
  palletIds: number[];
  action: "pdf" | "print";
}

export interface CreatePalletDialogResult {
  created: PalletItem[];
  quantity: number;
  sequenceStart: number;
  saveAndPrint: boolean;
}

/** Payload PUT /api/pallet-mngts/{id} */
export interface PalletMngtUpdatePayload {
  id: number;
  serialPallet: string;
  locationName: string | null;
  status: string;
  note: string | null;
  createAt: string | null;
  createBy: string | null;
  updatedAt: string;
  updatedBy: string;
}

export interface PalletMngtCreatePayload {
  serialPallet: string;
  status: string;
  note: string;
  createAt: string;
  createBy: string;
  updatedAt: string;
  updatedBy: string;
}

/** Mã pallet = tiền tố + STT 5 số + ngày DDMMYYYY. Ví dụ: P0000122092026 */
export function buildPalletCode(
  prefix: string,
  sequence: number,
  date: Date,
): string {
  const seq = String(Math.max(0, sequence)).padStart(5, "0").slice(-5);
  const dd = String(date.getDate()).padStart(2, "0");
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const yyyy = String(date.getFullYear());
  return `${(prefix || "P").trim()}${seq}${dd}${mm}${yyyy}`;
}

export function formatPalletDateTime(date: Date = new Date()): string {
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const dd = String(date.getDate()).padStart(2, "0");
  const hh = String(date.getHours()).padStart(2, "0");
  const mi = String(date.getMinutes()).padStart(2, "0");
  const ss = String(date.getSeconds()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd} ${hh}:${mi}:${ss}`;
}

export function toCodeDateDigits(date: Date): string {
  const dd = String(date.getDate()).padStart(2, "0");
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const yyyy = String(date.getFullYear());
  return `${dd}${mm}${yyyy}`;
}
