package com.mycompany.myapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to JHipster.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Sap sap = new Sap();

    private final ScanTotalGateway scanTotalGateway = new ScanTotalGateway();

    public Sap getSap() {
        return sap;
    }

    public ScanTotalGateway getScanTotalGateway() {
        return scanTotalGateway;
    }

    /**
     * Gửi tổng SL thực nhận (quantityReceived) của từng dòng PO sang gateway —
     * {@code application.scan-total-gateway.*}.
     */
    public static class ScanTotalGateway {

        /** false (hoặc url trống) → chỉ ghi log payload (dry-run), không gọi gateway */
        private boolean enabled = false;

        /** URL gateway nhận payload (POST JSON) */
        private String url = "";

        /** Timeout kết nối / đọc khi gọi gateway (ms) */
        private int timeoutMs = 10000;

        /** Chu kỳ job gửi bù các đơn còn thay đổi chưa gửi (ms) — mặc định 5 phút */
        private long jobIntervalMs = 300000;

        /** Đơn gửi lỗi liên tục quá số giờ này → bỏ khỏi bộ nhớ (ghi log ERROR) */
        private int pendingTtlHours = 24;

        /** Số đơn tối đa giữ trong bộ nhớ chờ gửi (vượt → bỏ đơn cũ nhất) */
        private int maxPending = 5000;

        /** Khởi động BE: nạp lại các đơn có thùng tạo / sửa trong số giờ gần nhất */
        private int startupSeedHours = 2;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getTimeoutMs() {
            return timeoutMs;
        }

        public void setTimeoutMs(int timeoutMs) {
            this.timeoutMs = timeoutMs;
        }

        public long getJobIntervalMs() {
            return jobIntervalMs;
        }

        public void setJobIntervalMs(long jobIntervalMs) {
            this.jobIntervalMs = jobIntervalMs;
        }

        public int getPendingTtlHours() {
            return pendingTtlHours;
        }

        public void setPendingTtlHours(int pendingTtlHours) {
            this.pendingTtlHours = pendingTtlHours;
        }

        public int getMaxPending() {
            return maxPending;
        }

        public void setMaxPending(int maxPending) {
            this.maxPending = maxPending;
        }

        public int getStartupSeedHours() {
            return startupSeedHours;
        }

        public void setStartupSeedHours(int startupSeedHours) {
            this.startupSeedHours = startupSeedHours;
        }
    }

    public static class Sap {

        /**
         * URL SAP API PostGoodsReceiptPO (server-to-server, không gọi từ browser).
         */
        private String goodsReceiptPoUrl =
            "http://192.168.68.3:8082/api/PostGoodsReceiptPO";

        public String getGoodsReceiptPoUrl() {
            return goodsReceiptPoUrl;
        }

        public void setGoodsReceiptPoUrl(String goodsReceiptPoUrl) {
            this.goodsReceiptPoUrl = goodsReceiptPoUrl;
        }
    }
}
