package com.mycompany.myapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.config.ApplicationProperties;
import com.mycompany.myapp.service.dto.ScanTotalPayloadDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Gọi gateway nhận tổng SL thực nhận. Chưa bật / chưa có URL
 * ({@code application.scan-total-gateway.enabled|url}) → dry-run: chỉ ghi log payload.
 * TODO: bổ sung xác thực / định dạng body theo tài liệu gateway khi có.
 */
@Component
public class ScanTotalGatewayClient {

    private static final Logger LOG = LoggerFactory.getLogger(
        ScanTotalGatewayClient.class
    );

    private static final int MAX_MESSAGE = 1000;

    /** Kết quả gọi gateway */
    public static class Result {

        public final boolean ok;
        public final boolean dryRun;
        public final Integer status;
        public final String message;

        Result(boolean ok, boolean dryRun, Integer status, String message) {
            this.ok = ok;
            this.dryRun = dryRun;
            this.status = status;
            this.message = message;
        }
    }

    private final ApplicationProperties.ScanTotalGateway config;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public ScanTotalGatewayClient(
        ApplicationProperties applicationProperties,
        ObjectMapper objectMapper
    ) {
        this.config = applicationProperties.getScanTotalGateway();
        this.objectMapper = objectMapper;
        SimpleClientHttpRequestFactory factory =
            new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(config.getTimeoutMs());
        factory.setReadTimeout(config.getTimeoutMs());
        this.restTemplate = new RestTemplate(factory);
    }

    public boolean isConfigured() {
        return (
            config.isEnabled() &&
            config.getUrl() != null &&
            !config.getUrl().isBlank()
        );
    }

    public Result send(ScanTotalPayloadDTO payload) {
        String body;
        try {
            body = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            return new Result(
                false,
                false,
                null,
                "Lỗi dựng JSON: " + e.getMessage()
            );
        }
        if (!isConfigured()) {
            LOG.info(
                "[scan-total] DRY-RUN (chưa cấu hình gateway) đơn {}: {}",
                payload.getDeliveryNotificationId(),
                body
            );
            return new Result(
                true,
                true,
                null,
                "Chưa cấu hình gateway — chỉ ghi log"
            );
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                config.getUrl(),
                new HttpEntity<>(body, headers),
                String.class
            );
            int status = response.getStatusCodeValue();
            boolean ok = response.getStatusCode().is2xxSuccessful();
            LOG.info(
                "[scan-total] Gửi gateway đơn {} → HTTP {}",
                payload.getDeliveryNotificationId(),
                status
            );
            return new Result(ok, false, status, cut(response.getBody()));
        } catch (HttpStatusCodeException ex) {
            LOG.warn(
                "[scan-total] Gateway lỗi đơn {} → HTTP {}: {}",
                payload.getDeliveryNotificationId(),
                ex.getRawStatusCode(),
                cut(ex.getResponseBodyAsString())
            );
            return new Result(
                false,
                false,
                ex.getRawStatusCode(),
                cut(ex.getResponseBodyAsString())
            );
        } catch (RestClientException ex) {
            LOG.warn(
                "[scan-total] Không gọi được gateway đơn {}: {}",
                payload.getDeliveryNotificationId(),
                ex.getMessage()
            );
            return new Result(false, false, null, cut(ex.getMessage()));
        }
    }

    private static String cut(String value) {
        if (value == null) {
            return null;
        }
        return value.length() > MAX_MESSAGE
            ? value.substring(0, MAX_MESSAGE) + "…"
            : value;
    }
}
