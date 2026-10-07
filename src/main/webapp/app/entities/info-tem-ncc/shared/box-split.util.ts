/**
 * Tách thùng thừa SL: phần vừa đủ giữ ReelID gốc vào PO, phần dư thành thùng mới.
 */

/** yyyyMMddHHmmss theo giờ máy */
export function formatSplitTimestamp(date: Date): string {
  const pad = (n: number): string => String(n).padStart(2, "0");
  return (
    `${date.getFullYear()}${pad(date.getMonth() + 1)}${pad(date.getDate())}` +
    `${pad(date.getHours())}${pad(date.getMinutes())}${pad(date.getSeconds())}`
  );
}

/**
 * ReelID thùng tách: 14 ký tự đầu của ReelID gốc là thời điểm tạo tem (yyyyMMddHHmmss)
 * → thay bằng thời điểm tách, giữ nguyên phần sau.
 * Vd 20261005116745000500020441 tách lúc 07/10/2026 09:30:15 → 20261007093015000500020441.
 * ReelID gốc không bắt đầu bằng 14 chữ số → thêm hậu tố "-" + thời điểm tách.
 * Trùng ReelID đã có (taken) → cộng thêm 1 giây cho đến khi không trùng.
 */
export function buildSplitReelId(
  originalReelId: string,
  at: Date,
  taken: ReadonlySet<string>,
): string {
  const original = originalReelId.trim();
  const build = (d: Date): string => {
    const ts = formatSplitTimestamp(d);
    return /^\d{14}/.test(original)
      ? `${ts}${original.slice(14)}`
      : `${original}-${ts}`;
  };
  let time = at.getTime();
  let candidate = build(new Date(time));
  while (taken.has(candidate) || candidate === original) {
    time += 1000;
    candidate = build(new Date(time));
  }
  return candidate;
}

/**
 * Mã QR cho thùng tách: thay ReelID gốc bằng ReelID mới; thay SL nếu trong QR có đúng
 * 1 đoạn (ngăn cách bởi ký tự không phải chữ / số / dấu chấm) bằng SL gốc.
 * Trả về cả cờ quantityReplaced để báo khi không thay được SL trên QR.
 */
export function buildSplitQrCode(
  originalQr: string,
  originalReelId: string,
  newReelId: string,
  originalQty: number,
  newQty: number,
): { qrCode: string; quantityReplaced: boolean } {
  let qr = originalQr || "";
  if (originalReelId) {
    qr = qr.split(originalReelId).join(newReelId);
  }
  const qtyText = String(originalQty);
  const tokenRe = /[A-Za-z0-9.]+/g;
  const hits: number[] = [];
  let m: RegExpExecArray | null;
  while ((m = tokenRe.exec(qr)) !== null) {
    if (m[0] === qtyText) {
      hits.push(m.index);
    }
  }
  if (hits.length !== 1) {
    return { qrCode: qr, quantityReplaced: false };
  }
  const at = hits[0];
  return {
    qrCode: `${qr.slice(0, at)}${newQty}${qr.slice(at + qtyText.length)}`,
    quantityReplaced: true,
  };
}
