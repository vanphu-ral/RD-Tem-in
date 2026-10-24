package com.mycompany.myapp.web.rest.smart;

import com.mycompany.myapp.service.dto.smart.ApiResponse;
import com.mycompany.myapp.service.dto.smart.DeliveryNoticeRequest;
import com.mycompany.myapp.service.smart.SmartDeliveryService;
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

    @PostMapping("")
    public ResponseEntity<
        ApiResponse<ApiResponse.NoticeData>
    > receiveDeliveryNotice(
        @RequestHeader("Authorization") String authHeader,
        @RequestHeader("X-Request-Id") String requestId,
        @Valid @RequestBody DeliveryNoticeRequest request
    ) {
        // Simple check for Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        ApiResponse<ApiResponse.NoticeData> response =
            smartDeliveryService.processDeliveryNotice(requestId, request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            // 422 Unprocessable Entity is appropriate for partial success/validation errors
            return ResponseEntity.status(422).body(response);
        }
    }
}
