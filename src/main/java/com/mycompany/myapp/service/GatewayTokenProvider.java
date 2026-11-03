package com.mycompany.myapp.service;

import com.mycompany.myapp.config.ApplicationProperties;
import java.time.Instant;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Token đăng nhập gateway (gửi SAP GRPO): POST {@code gateway-auth-url} với {username, password}
 * → {@code access_token} (Bearer, {@code expires_in} giây). Giữ trong bộ nhớ, lấy lại khi gần hết hạn
 * hoặc khi gateway trả 401 ({@link #invalidate()}).
 */
@Service
public class GatewayTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(
        GatewayTokenProvider.class
    );

    /** Lấy token mới trước khi hết hạn bấy nhiêu giây */
    private static final long EXPIRY_MARGIN_SECONDS = 60;

    private final RestTemplate restTemplate;
    private final ApplicationProperties.Sap config;

    private String accessToken;
    private Instant expiresAt = Instant.EPOCH;

    public GatewayTokenProvider(
        RestTemplate restTemplate,
        ApplicationProperties applicationProperties
    ) {
        this.restTemplate = restTemplate;
        this.config = applicationProperties.getSap();
    }

    public synchronized String getAccessToken() {
        if (
            accessToken == null ||
            Instant.now().plusSeconds(EXPIRY_MARGIN_SECONDS).isAfter(expiresAt)
        ) {
            login();
        }
        return accessToken;
    }

    public synchronized void invalidate() {
        accessToken = null;
        expiresAt = Instant.EPOCH;
    }

    private void login() {
        String url = config.getGatewayAuthUrl();
        if (url == null || url.isBlank()) {
            throw new IllegalStateException(
                "Chưa cấu hình API đăng nhập gateway."
            );
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = Map.of(
            "username",
            config.getGatewayAuthUsername(),
            "password",
            config.getGatewayAuthPassword()
        );
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            new HttpEntity<>(body, headers),
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        Map<String, Object> res = response.getBody();
        Object token = res == null ? null : res.get("access_token");
        if (token == null || token.toString().isBlank()) {
            throw new IllegalStateException(
                "Đăng nhập gateway không trả access_token."
            );
        }
        Object expiresIn = res.get("expires_in");
        long seconds = expiresIn instanceof Number
            ? ((Number) expiresIn).longValue()
            : 1800L;
        accessToken = token.toString();
        expiresAt = Instant.now().plusSeconds(seconds);
        log.debug("Gateway token refreshed, expires in {}s", seconds);
    }
}
