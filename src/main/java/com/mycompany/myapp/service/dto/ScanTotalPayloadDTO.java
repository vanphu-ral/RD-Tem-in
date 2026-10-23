package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Body gửi gateway cho 1 đơn giao hàng: thông tin đơn + mọi dòng PO kèm tổng SL thực nhận.
 * Khi có tài liệu gateway đầy đủ (phần header) → chỉnh lại các trường ngoài {@code lines}.
 */
public class ScanTotalPayloadDTO implements Serializable {

    /** Id đơn giao hàng (delivery_notification.id) — nội bộ RD */
    private Long deliveryNotificationId;

    /** Mã thông báo giao hàng (delivery_notification.delivery_notification_code) */
    private String deliveryNotificationCode;

    /** Phiếu lịch giao hàng (GUID) — delivery_notification.delivery_id */
    private String deliveryId;

    /** Thời điểm dựng payload (ISO-8601, UTC) */
    private String sentAt;

    private List<ScanTotalLineDTO> lines = new ArrayList<>();

    public Long getDeliveryNotificationId() {
        return deliveryNotificationId;
    }

    public void setDeliveryNotificationId(Long deliveryNotificationId) {
        this.deliveryNotificationId = deliveryNotificationId;
    }

    public String getDeliveryNotificationCode() {
        return deliveryNotificationCode;
    }

    public void setDeliveryNotificationCode(String deliveryNotificationCode) {
        this.deliveryNotificationCode = deliveryNotificationCode;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public List<ScanTotalLineDTO> getLines() {
        return lines;
    }

    public void setLines(List<ScanTotalLineDTO> lines) {
        this.lines = lines;
    }
}
