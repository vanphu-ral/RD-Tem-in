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

    public Sap getSap() {
        return sap;
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
