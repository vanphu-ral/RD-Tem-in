package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.SmartDeliveryService;
import com.mycompany.myapp.service.dto.DeliveryNoticeRequest;
import com.mycompany.myapp.service.dto.DeliveryNoticeResponse;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/smart/delivery-notices")
public class SmartDeliveryResource {

    private final SmartDeliveryService smartDeliveryService;

    public SmartDeliveryResource(SmartDeliveryService smartDeliveryService) {
        this.smartDeliveryService = smartDeliveryService;
    }

    @PostMapping
    public ResponseEntity<DeliveryNoticeResponse> receiveNotice(
        @RequestHeader("Authorization") String authHeader,
        @RequestHeader("X-Request-Id") String requestId,
        @Valid @RequestBody DeliveryNoticeRequest request
    ) {
        // Simple Authorization check (in real project, handled by Spring Security/JWT filter)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        DeliveryNoticeResponse response =
            smartDeliveryService.processDeliveryNotice(requestId, request);
        return ResponseEntity.ok(response);
    }
}
