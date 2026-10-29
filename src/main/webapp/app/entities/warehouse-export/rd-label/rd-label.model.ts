/** Tem in theo mẫu Rạng Đông (giống Preview In Tem ở màn Nhận vật tư) */
export interface SplitPrintLabel {
  id: string;
  qrCode: string;
  reelId: string;
  sapCode: string;
  productName: string;
  partNumber: string;
  lot: string;
  initialQuantity: number | null;
  vendor: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData5: string;
  storageUnit: string;
  /** yyyy-MM-dd */
  manufacturingDate: string;
  /** yyyy-MM-dd */
  expirationDate: string;
  /** yyyy-MM-dd */
  arrivalDate: string;
}

/**
 * Ngày từ tồn kho PanaCIM / form (epoch giây hoặc ms, ISO, dd/MM/yyyy) → yyyy-MM-dd; không nhận ra → "".
 */
export function toLabelDate(value: unknown): string {
  const text =
    value === null || value === undefined ? "" : String(value).trim();
  if (!text) {
    return "";
  }
  const pad = (n: number): string => String(n).padStart(2, "0");
  const ymd = (d: Date): string =>
    `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
  // yyyyMMdd (vendor-label-info / tem NCC)
  const compact = /^(\d{4})(\d{2})(\d{2})$/.exec(text);
  if (compact) {
    return `${compact[1]}-${compact[2]}-${compact[3]}`;
  }
  if (/^\d{9,13}$/.test(text)) {
    const n = Number(text);
    return ymd(new Date(text.length <= 10 ? n * 1000 : n));
  }
  const dmy = /^(\d{1,2})\/(\d{1,2})\/(\d{4})/.exec(text);
  if (dmy) {
    return `${dmy[3]}-${pad(Number(dmy[2]))}-${pad(Number(dmy[1]))}`;
  }
  const iso = /^(\d{4})-(\d{2})-(\d{2})/.exec(text);
  if (iso) {
    return `${iso[1]}-${iso[2]}-${iso[3]}`;
  }
  const parsed = new Date(text);
  return Number.isNaN(parsed.getTime()) ? "" : ymd(parsed);
}

/** Số trường của chuỗi QR tem Rạng Đông (ngăn cách bởi #) */
const RD_QR_FIELD_COUNT = 15;
const RD_QR_REEL_INDEX = 0;
const RD_QR_QTY_INDEX = 9;

/** Thông tin dựng chuỗi QR tem Rạng Đông */
export interface RdQrFields {
  reelId: string;
  partNumber: string;
  vendor: string;
  lot: string;
  userData1: string;
  userData2: string;
  userData3: string;
  /** Mã vật tư + "-" + ngày (yyMMdd); rỗng → tự ghép từ sapCode + ngày SX */
  userData4: string;
  /** Mã PO */
  userData5: string;
  quantity: number;
  /** MSL, rỗng → 1 */
  msl: string;
  location: string;
  /** Ngày SX / HSD: epoch, ISO, yyyyMMdd... */
  manufacturingDate: unknown;
  expirationDate: unknown;
  /** Mã vật tư */
  sapCode: string;
}

/** Ngày → yyyyMMdd (không nhận ra → "") */
function qrDate(value: unknown): string {
  return toLabelDate(value).replace(/-/g, "");
}

/**
 * Chuỗi QR tem Rạng Đông:
 * reelId#partNumber#vendor#lot#userData1#userData2#userData3#userData4#userData5#quantity#msl#location#mfgDate#expDate#sapCode
 * (vd 01020260619161737#1N4148W#V900000139#20260508#NO#NO#NO#00095820-260518#40574#3000#1#RD#20260508#20280508#00095820)
 */
export function buildRdQrCode(f: RdQrFields): string {
  const text = (v: unknown): string =>
    (v === null || v === undefined ? "" : String(v)).replace(/#/g, "").trim();
  const no = (v: string): string => text(v) || "NO";
  const mfg = qrDate(f.manufacturingDate);
  const sap = text(f.sapCode);
  const userData4 =
    text(f.userData4) || (sap && mfg ? `${sap}-${mfg.slice(2)}` : sap);
  return [
    text(f.reelId),
    text(f.partNumber),
    text(f.vendor),
    text(f.lot),
    no(f.userData1),
    no(f.userData2),
    no(f.userData3),
    userData4,
    text(f.userData5),
    String(f.quantity),
    text(f.msl) || "1",
    text(f.location),
    mfg,
    qrDate(f.expirationDate),
    sap,
  ].join("#");
}

/**
 * Tem tách từ chuỗi QR đã scan của thùng gốc: giữ nguyên các trường, thay ReelID + số lượng.
 * Chuỗi không đúng định dạng QR Rạng Đông → null.
 */
export function rebuildRdQrCode(
  sourceQr: string | null | undefined,
  reelId: string,
  quantity: number,
): string | null {
  const parts = (sourceQr ?? "").trim().split("#");
  if (parts.length < RD_QR_FIELD_COUNT) {
    return null;
  }
  parts[RD_QR_REEL_INDEX] = reelId;
  parts[RD_QR_QTY_INDEX] = String(quantity);
  return parts.join("#");
}
