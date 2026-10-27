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
 * Proxy PanaCIM MesApi ExecuteTransaction — FE gọi same-origin, BE forward nguyên payload
 * sang PanaCIM (tránh CORS / không lộ địa chỉ PanaCIM ra trình duyệt).
 */
@Service
public class PanacimMesApiService {

    private static final Logger log = LoggerFactory.getLogger(
        PanacimMesApiService.class
    );

    private final RestTemplate restTemplate;
    private final ApplicationProperties applicationProperties;

    public PanacimMesApiService(
        RestTemplate restTemplate,
        ApplicationProperties applicationProperties
    ) {
        this.restTemplate = restTemplate;
        this.applicationProperties = applicationProperties;
    }

    public ResponseEntity<String> executeTransaction(
        Map<String, Object> payload
    ) {
        String targetUrl = applicationProperties.getPanacim().getMesApiUrl();
        log.debug(
            "Forwarding PanaCIM transaction {} to {}",
            payload.get("transaction"),
            targetUrl
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ApplicationProperties.Panacim panacim =
            applicationProperties.getPanacim();
        // MesApi bắt buộc có header UserName / UserId
        headers.set(
            "UserName",
            panacim.getUserName() != null ? panacim.getUserName() : ""
        );
        headers.set(
            "UserId",
            panacim.getUserId() != null ? panacim.getUserId() : ""
        );
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(
            payload,
            headers
        );

        try {
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
                "PanaCIM transaction error {}: {}",
                ex.getStatusCode(),
                ex.getResponseBodyAsString()
            );
            String body = ex.getResponseBodyAsString();
            return ResponseEntity.status(ex.getStatusCode()).body(
                body != null && !body.isBlank() ? body : ex.getMessage()
            );
        } catch (RestClientException ex) {
            log.error("PanaCIM proxy failed: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                "Không gọi được PanaCIM: " + ex.getMessage()
            );
        }
    }
}
