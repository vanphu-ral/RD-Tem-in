package com.mycompany.myapp.service;

import com.mycompany.myapp.config.ApplicationProperties;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Proxy gửi nhập kho SAP — FE gọi same-origin, BE forward sang SAP API.
 */
@Service
public class PostGoodsReceiptPoService {

    private static final Logger log = LoggerFactory.getLogger(
        PostGoodsReceiptPoService.class
    );

    private final RestTemplate restTemplate;
    private final ApplicationProperties applicationProperties;
    private final GatewayTokenProvider gatewayTokenProvider;

    public PostGoodsReceiptPoService(
        RestTemplate restTemplate,
        ApplicationProperties applicationProperties,
        GatewayTokenProvider gatewayTokenProvider
    ) {
        this.restTemplate = restTemplate;
        this.applicationProperties = applicationProperties;
        this.gatewayTokenProvider = gatewayTokenProvider;
    }

    public ResponseEntity<Object> forwardToSap(Map<String, Object> payload) {
        return forward(
            applicationProperties.getSap().getGoodsReceiptPoUrl(),
            payload,
            null
        );
    }

    /** Gửi SAP qua gateway; chưa cấu hình URL → 503, FE không đánh dấu thùng đã gửi */
    public ResponseEntity<Object> forwardToGateway(
        Map<String, Object> payload
    ) {
        String targetUrl = applicationProperties
            .getSap()
            .getGatewayGoodsReceiptUrl();
        if (targetUrl == null || targetUrl.isBlank()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                "Chưa cấu hình endpoint gateway gửi SAP."
            );
        }
        ResponseEntity<Object> res;
        try {
            res = forward(
                targetUrl,
                payload,
                gatewayTokenProvider.getAccessToken()
            );
            if (res.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                // Token bị thu hồi / hết hạn sớm → đăng nhập lại, gửi lại 1 lần
                gatewayTokenProvider.invalidate();
                res = forward(
                    targetUrl,
                    payload,
                    gatewayTokenProvider.getAccessToken()
                );
            }
        } catch (IllegalStateException | RestClientException ex) {
            log.error("Gateway GRPO failed: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                "Gửi SAP qua gateway thất bại: " + ex.getMessage()
            );
        }
        return res;
    }

    private ResponseEntity<Object> forward(
        String targetUrl,
        Map<String, Object> payload,
        String bearerToken
    ) {
        log.debug("Forwarding PostGoodsReceiptPO to {}", targetUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (bearerToken != null) {
            headers.setBearerAuth(bearerToken);
        }
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(
            payload,
            headers
        );

        try {
            // SAP API trả text/plain — không dùng Object.class (thiếu converter).
            ResponseEntity<String> response = restTemplate.postForEntity(
                targetUrl,
                entity,
                String.class
            );
            return ResponseEntity.status(response.getStatusCode()).body(
                response.getBody()
            );
        } catch (HttpStatusCodeException ex) {
            log.error(
                "PostGoodsReceiptPO SAP error {}: {}",
                ex.getStatusCode(),
                ex.getResponseBodyAsString()
            );
            return ResponseEntity.status(ex.getStatusCode()).body(
                extractSapErrorMessage(ex)
            );
        } catch (RestClientException ex) {
            log.error(
                "PostGoodsReceiptPO proxy failed: {}",
                ex.getMessage(),
                ex
            );
            throw new IllegalStateException(
                "Gửi SAP thất bại: " + ex.getMessage(),
                ex
            );
        }
    }

    private static String extractSapErrorMessage(HttpStatusCodeException ex) {
        String body = ex.getResponseBodyAsString();
        if (body != null && !body.isBlank()) {
            return body;
        }
        return ex.getMessage();
    }
}
