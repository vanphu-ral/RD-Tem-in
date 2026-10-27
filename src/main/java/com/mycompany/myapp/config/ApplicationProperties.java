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
    private final Panacim panacim = new Panacim();

    public Sap getSap() {
        return sap;
    }

    public Panacim getPanacim() {
        return panacim;
    }

    public static class Panacim {

        /**
         * URL PanaCIM MesApi ExecuteTransaction (server-to-server, không gọi từ browser) —
         * dùng cho tách tem (transaction SplitQuantity).
         */
        private String mesApiUrl =
            "http://172.168.6.3/PanaCIMMC/MesApi/ExecuteTransaction";

        public String getMesApiUrl() {
            return mesApiUrl;
        }

        public void setMesApiUrl(String mesApiUrl) {
            this.mesApiUrl = mesApiUrl;
        }

        /** Header UserName bắt buộc của MesApi (thiếu → PanaCIM báo lỗi) */
        private String userName = "RD00698";

        /** Header UserId của MesApi (gửi kèm, có thể để trống) */
        private String userId = "";

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
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
