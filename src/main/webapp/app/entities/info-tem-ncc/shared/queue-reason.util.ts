/**
 * Lý do thùng nằm trong "Hàng chờ vật tư" — lưu ở trường status của vendor-label-info
 * lúc scan (không gửi đi PanaCIM / SAP).
 *  - overflow: vật tư có trong PO của đơn nhưng mọi PO đều không còn đủ SL
 *  - noPo: vật tư không thuộc PO nào trong đơn (hoặc đơn chưa có PO)
 *  - poMismatch: PO trên tem (QR) không khớp PO nào của vật tư này trong đơn
 *  - splitRemainder: phần dư tách ra từ thùng Thừa SL (thùng mới, chờ nhập đơn sau)
 */
export type QueueReason = "overflow" | "noPo" | "poMismatch" | "splitRemainder";

const CODES: Record<QueueReason, string> = {
  overflow: "THUA_SL",
  noPo: "THIEU_PO",
  poMismatch: "SAI_PO",
  splitRemainder: "DU_TACH",
};

/** Dữ liệu cũ: mã lý do từng lưu ở comments với tiền tố này */
const LEGACY_COMMENT_PREFIX = "HANG_CHO:";

export const QUEUE_REASON_LABELS: Record<QueueReason, string> = {
  overflow: "Thừa SL",
  noPo: "Thiếu PO",
  poMismatch: "Sai PO",
  splitRemainder: "Dư tách",
};

export const QUEUE_REASON_HINTS: Record<QueueReason, string> = {
  overflow: "Các PO cùng vật tư đã đủ số lượng — thùng dư",
  noPo: "Vật tư không thuộc PO nào trong đơn",
  poMismatch: "PO trên tem không khớp PO nào của vật tư này trong đơn",
  splitRemainder:
    "Phần dư tách ra từ thùng thừa SL — thùng mới, chờ nhập vào đơn sau",
};

/** Giá trị status lưu khi đưa thùng vào hàng chờ */
export function queueReasonCode(reason: QueueReason): string {
  return CODES[reason];
}

/** Mã (status / mã cũ ở comments) → lý do */
function reasonFromCode(code: string | null | undefined): QueueReason | null {
  const value = (code ?? "").replace(LEGACY_COMMENT_PREFIX, "");
  const found = (Object.keys(CODES) as QueueReason[]).find(
    (r) => CODES[r] === value,
  );
  return found ?? null;
}

/**
 * Lý do của 1 thùng trong hàng chờ (sapPor1Id rỗng): đọc từ status, thùng cũ đọc mã ở
 * comments; không có mã nào (trước đây chỉ thùng không khớp PO mới vào hàng chờ) → noPo.
 */
export function queueReasonOf(rec: {
  status?: string | null;
  comments?: string | null;
  sapPor1Id?: number | null;
}): QueueReason | null {
  if (rec.sapPor1Id !== null && rec.sapPor1Id !== undefined) {
    return null;
  }
  return reasonFromCode(rec.status) ?? reasonFromCode(rec.comments) ?? "noPo";
}

/**
 * Thùng đã được gán PO → bỏ mã lý do hàng chờ khỏi status (và mã cũ ở comments),
 * giữ nguyên giá trị khác của 2 trường.
 */
export function clearQueueReason(rec: {
  status?: string | null;
  comments?: string | null;
}): { status: string | null; comments: string | null } {
  const status = rec.status ?? null;
  const comments = rec.comments ?? null;
  return {
    status: reasonFromCode(status) ? null : status,
    comments: comments?.startsWith(LEGACY_COMMENT_PREFIX) ? null : comments,
  };
}

/** Đếm số thùng theo lý do → [{reason, count}] theo thứ tự overflow, splitRemainder, poMismatch, noPo */
export function countQueueReasons(
  records: Array<{
    status?: string | null;
    comments?: string | null;
    sapPor1Id?: number | null;
  }>,
): Array<{ reason: QueueReason; count: number }> {
  const counts: Record<QueueReason, number> = {
    overflow: 0,
    noPo: 0,
    poMismatch: 0,
    splitRemainder: 0,
  };
  for (const rec of records) {
    const reason = queueReasonOf(rec);
    if (reason) {
      counts[reason]++;
    }
  }
  return (["overflow", "splitRemainder", "poMismatch", "noPo"] as QueueReason[])
    .filter((r) => counts[r] > 0)
    .map((reason) => ({ reason, count: counts[reason] }));
}
