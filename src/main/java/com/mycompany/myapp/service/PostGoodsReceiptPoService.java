package com.mycompany.myapp.service;

import com.mycompany.myapp.config.ApplicationProperties;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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

    public PostGoodsReceiptPoService(
        RestTemplate restTemplate,
        ApplicationProperties applicationProperties
    ) {
        this.restTemplate = restTemplate;
        this.applicationProperties = applicationProperties;
    }

    public ResponseEntity<Object> forwardToSap(Map<String, Object> payload) {
        String targetUrl = applicationProperties
            .getSap()
            .getGoodsReceiptPoUrl();
        log.debug("Forwarding PostGoodsReceiptPO to {}", targetUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
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
