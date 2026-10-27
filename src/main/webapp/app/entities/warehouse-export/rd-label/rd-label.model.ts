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
