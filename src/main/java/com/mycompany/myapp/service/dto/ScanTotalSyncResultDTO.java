package com.mycompany.myapp.service.dto;

import java.io.Serializable;

/** Kết quả 1 lần gửi tổng SL thực nhận của 1 đơn sang gateway (giữ trong bộ nhớ để tra cứu) */
public class ScanTotalSyncResultDTO implements Serializable {

    /** SENT = gateway nhận OK; DRY_RUN = chưa cấu hình gateway, chỉ ghi log; FAILED; SKIPPED */
    private String status;

    private Long deliveryNotificationId;

    /** Ai / cái gì kích hoạt: FE (đóng màn scan...) hoặc JOB / STARTUP */
    private String trigger;

    /** ISO-8601, UTC */
    private String syncedAt;

    private int lineCount;

    private long totalQuantityReceived;

    /** HTTP status gateway trả về (null nếu không gọi) */
    private Integer gatewayStatus;

    /** Body / lỗi gateway trả về (cắt ngắn) */
    private String message;

    private ScanTotalPayloadDTO payload;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDeliveryNotificationId() {
        return deliveryNotificationId;
    }

    public void setDeliveryNotificationId(Long deliveryNotificationId) {
        this.deliveryNotificationId = deliveryNotificationId;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getSyncedAt() {
        return syncedAt;
    }

    public void setSyncedAt(String syncedAt) {
        this.syncedAt = syncedAt;
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public long getTotalQuantityReceived() {
        return totalQuantityReceived;
    }

    public void setTotalQuantityReceived(long totalQuantityReceived) {
        this.totalQuantityReceived = totalQuantityReceived;
    }

    public Integer getGatewayStatus() {
        return gatewayStatus;
    }

    public void setGatewayStatus(Integer gatewayStatus) {
        this.gatewayStatus = gatewayStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ScanTotalPayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(ScanTotalPayloadDTO payload) {
        this.payload = payload;
    }
}
