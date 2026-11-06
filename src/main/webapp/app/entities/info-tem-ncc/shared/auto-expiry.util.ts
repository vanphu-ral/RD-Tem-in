import { toText, VendorLabelInfoDto } from "../services/info-tem-ncc.service";

/** HSD mặc định = NSX + 2 năm */
const AUTO_EXPIRY_YEARS = 2;

/** yyyyMMdd / yyyy-MM-dd / dd/MM/yyyy → Date */
function parseDate(value: unknown): Date | null {
  const raw = toText(value);
  const slash = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(raw);
  if (slash) {
    return new Date(Number(slash[3]), Number(slash[2]) - 1, Number(slash[1]));
  }
  const digits = raw.replace(/\D/g, "");
  if (digits.length >= 8 && Number(digits.slice(0, 4)) > 1900) {
    return new Date(
      Number(digits.slice(0, 4)),
      Number(digits.slice(4, 6)) - 1,
      Number(digits.slice(6, 8)),
    );
  }
  return null;
}

/**
 * HSD tự điền (chưa lưu) cho thùng chưa có HSD: NSX + 2 năm, tính theo NSX sớm nhất
 * của cùng mã vật tư + lô (SAP: 1 lô chỉ 1 HSD). Lô không có NSX → bỏ qua.
 * Trả về key = id thùng, value = HSD yyyyMMdd.
 */
export function autoExpiryDates(
  boxes: VendorLabelInfoDto[],
): Map<number, string> {
  const lotKey = (b: VendorLabelInfoDto): string =>
    `${toText(b.sapCode).toLowerCase()}|${toText(b.lot)}`;
  const earliestMfg = new Map<string, Date>();
  for (const b of boxes) {
    const mfg = parseDate(b.manufacturingDate);
    const key = lotKey(b);
    const current = earliestMfg.get(key);
    if (mfg && (!current || mfg < current)) {
      earliestMfg.set(key, mfg);
    }
  }
  const result = new Map<number, string>();
  for (const b of boxes) {
    const mfg = earliestMfg.get(lotKey(b));
    if (!b.id || toText(b.expirationDate) || !mfg) {
      continue;
    }
    const d = new Date(mfg);
    d.setFullYear(d.getFullYear() + AUTO_EXPIRY_YEARS);
    const pad = (n: number): string => String(n).padStart(2, "0");
    result.set(
      b.id,
      `${d.getFullYear()}${pad(d.getMonth() + 1)}${pad(d.getDate())}`,
    );
  }
  return result;
}
