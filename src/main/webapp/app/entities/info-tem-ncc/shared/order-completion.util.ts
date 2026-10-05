import {
  DeliveryNotificationDetailDto,
  DeliveryNotificationDto,
  toText,
  VendorLabelInfoDto,
} from "../services/info-tem-ncc.service";

/** Trạng thái đơn đã hoàn thành (list hiển thị "Đã hoàn thành" khi status = COMPLETED) */
export const DELIVERY_STATUS_COMPLETED = "COMPLETED";

/** Cờ đã gửi có thể là boolean / string / number — rỗng, false, 0 = chưa gửi */
function isFlagOn(value: unknown): boolean {
  const t = toText(value).toLowerCase();
  return t !== "" && t !== "false" && t !== "0";
}

/**
 * Lý do đơn chưa hoàn thành được (rỗng = đủ điều kiện):
 *  - đơn chưa có PO / vật tư
 *  - vật tư nào chưa nhận đủ SL theo PO
 *  - thùng nào chưa gửi SAP / chưa gửi PanaCIM
 *  - còn thùng trong "Hàng chờ vật tư" (chưa có PO)
 * `allBoxes` = mọi thùng của đơn (GET vendor-label-infos theo đơn) — để bắt thùng hàng chờ.
 */
export function orderCompletionBlockers(
  detail: DeliveryNotificationDetailDto,
  allBoxes: VendorLabelInfoDto[],
): string[] {
  const reasons: string[] = [];
  const lines = detail.sapPor1R1List ?? [];
  if (!lines.length) {
    return ["Đơn chưa có PO / vật tư nào."];
  }

  const lineIds = new Set(lines.map((l) => l.id));
  const boxesOfLine = new Map<number, VendorLabelInfoDto[]>();
  const queued: VendorLabelInfoDto[] = [];
  const source = allBoxes.length
    ? allBoxes
    : lines.flatMap((l) => l.vendorLabelInfoList ?? []);
  for (const b of source) {
    const lineId = b.sapPor1Id ?? null;
    if (lineId !== null && lineIds.has(lineId)) {
      boxesOfLine.set(lineId, [...(boxesOfLine.get(lineId) ?? []), b]);
    } else {
      queued.push(b);
    }
  }

  // 1. Đủ số lượng theo từng dòng PO
  for (const l of lines) {
    const need = Number(l.quantity ?? 0);
    const got = (boxesOfLine.get(l.id) ?? []).reduce(
      (s, b) => s + Number(b.initialQuantity ?? 0),
      0,
    );
    if (need > 0 && got < need) {
      const name = toText(l.dscription);
      reasons.push(
        `PO ${toText(l.docEntry) || "—"} · ${toText(l.itemCode) || "—"}${name ? ` (${name})` : ""}: mới nhận ${got}/${need}.`,
      );
    }
  }

  // 2. Đã gửi SAP / PanaCIM (thùng đã gán PO)
  const assigned = [...boxesOfLine.values()].flat();
  const noSap = assigned.filter((b) => !isFlagOn(b.sapSendStatus)).length;
  const noPana = assigned.filter((b) => !isFlagOn(b.panaSendStatus)).length;
  if (noSap) {
    reasons.push(`Còn ${noSap}/${assigned.length} thùng chưa gửi SAP.`);
  }
  if (noPana) {
    reasons.push(`Còn ${noPana}/${assigned.length} thùng chưa gửi PanaCIM.`);
  }

  // 3. Thùng chưa có PO
  if (queued.length) {
    reasons.push(
      `Còn ${queued.length} thùng trong "Hàng chờ vật tư" (chưa có PO) — gán PO hoặc xóa trước.`,
    );
  }
  return reasons;
}

/**
 * Payload PUT /delivery-notifications/{id} đánh dấu đơn đã hoàn thành.
 * Chỉ gửi các trường backend nhận (DTO không cho trường lạ — vd sapPor1R1List, vendorCode).
 */
export function buildCompletedOrderPayload(
  detail: DeliveryNotificationDetailDto,
): DeliveryNotificationDto {
  const payload: DeliveryNotificationDto = {
    id: detail.id,
    deliveryNotificationCode: detail.deliveryNotificationCode,
    invoiceNumber: detail.invoiceNumber,
    contractCode: detail.contractCode,
    vendorName: detail.vendorName,
    contNo: detail.contNo,
    entryDate: detail.entryDate,
    numberOfPo: detail.numberOfPo,
    status: DELIVERY_STATUS_COMPLETED,
    source: detail.source ?? null,
    deletedAt: detail.deletedAt,
    deletedBy: detail.deletedBy,
    createdBy: detail.createdBy,
    createdAt: detail.createdAt,
  };
  // Backend đã có numberOfItem (trả về trong GET) → gửi lại giữ nguyên
  if (detail.numberOfItem !== undefined) {
    payload.numberOfItem = detail.numberOfItem;
  }
  return payload;
}

/** Đơn đã ở trạng thái hoàn thành */
export function isOrderCompleted(detail: DeliveryNotificationDto): boolean {
  return toText(detail.status).toUpperCase() === DELIVERY_STATUS_COMPLETED;
}
