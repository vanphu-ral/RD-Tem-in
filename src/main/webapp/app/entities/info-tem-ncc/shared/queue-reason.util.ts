/**
 * Lý do thùng nằm trong "Hàng chờ vật tư" — lưu ở trường comments của vendor-label-info
 * lúc scan (không gửi đi PanaCIM / SAP).
 *  - overflow: vật tư có trong PO của đơn nhưng mọi PO đều không còn đủ SL
 *  - noPo: vật tư không thuộc PO nào trong đơn (hoặc đơn chưa có PO)
 *  - poMismatch: PO trên tem (QR) không khớp PO nào của vật tư này trong đơn
 */
export type QueueReason = "overflow" | "noPo" | "poMismatch";

const PREFIX = "HANG_CHO:";
const CODES: Record<QueueReason, string> = {
  overflow: `${PREFIX}THUA_SL`,
  noPo: `${PREFIX}THIEU_PO`,
  poMismatch: `${PREFIX}SAI_PO`,
};

export const QUEUE_REASON_LABELS: Record<QueueReason, string> = {
  overflow: "Thừa SL",
  noPo: "Thiếu PO",
  poMismatch: "Sai PO",
};

export const QUEUE_REASON_HINTS: Record<QueueReason, string> = {
  overflow: "Các PO cùng vật tư đã đủ số lượng — thùng dư",
  noPo: "Vật tư không thuộc PO nào trong đơn",
  poMismatch: "PO trên tem không khớp PO nào của vật tư này trong đơn",
};

/** Giá trị comments lưu khi đưa thùng vào hàng chờ */
export function queueReasonCode(reason: QueueReason): string {
  return CODES[reason];
}

/**
 * Lý do của 1 thùng trong hàng chờ (sapPor1Id rỗng). Thùng cũ chưa có mã lý do
 * (trước đây chỉ thùng không khớp PO mới vào hàng chờ) → noPo.
 */
export function queueReasonOf(rec: {
  comments?: string | null;
  sapPor1Id?: number | null;
}): QueueReason | null {
  if (rec.sapPor1Id !== null && rec.sapPor1Id !== undefined) {
    return null;
  }
  if (rec.comments === CODES.overflow) {
    return "overflow";
  }
  return rec.comments === CODES.poMismatch ? "poMismatch" : "noPo";
}

/** Thùng đã được gán PO → bỏ mã lý do hàng chờ khỏi comments (giữ ghi chú khác) */
export function clearQueueReason(
  comments: string | null | undefined,
): string | null {
  const value = comments ?? null;
  return value?.startsWith(PREFIX) ? null : value;
}

/** Đếm số thùng theo lý do → [{reason, count}] theo thứ tự overflow, poMismatch, noPo */
export function countQueueReasons(
  records: Array<{ comments?: string | null; sapPor1Id?: number | null }>,
): Array<{ reason: QueueReason; count: number }> {
  const counts: Record<QueueReason, number> = {
    overflow: 0,
    noPo: 0,
    poMismatch: 0,
  };
  for (const rec of records) {
    const reason = queueReasonOf(rec);
    if (reason) {
      counts[reason]++;
    }
  }
  return (["overflow", "poMismatch", "noPo"] as QueueReason[])
    .filter((r) => counts[r] > 0)
    .map((reason) => ({ reason, count: counts[reason] }));
}
