package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.ScanTotalChangeTracker;
import com.mycompany.myapp.service.ScanTotalSyncService;
import com.mycompany.myapp.service.dto.ScanTotalSyncResultDTO;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Gửi tổng SL thực nhận của đơn giao hàng sang gateway (tiến trình ngầm).
 * <ul>
 *   <li>{@code POST /api/delivery-notifications/{id}/scan-total-sync} — FE gọi khi đóng màn scan /
 *       đăng xuất / đóng tab. Chỉ gửi khi đơn có thay đổi chưa gửi ({@code force=true} → gửi luôn).</li>
 *   <li>{@code GET  /api/delivery-notifications/{id}/scan-total-sync} — tra cứu: payload sẽ gửi,
 *       trạng thái hàng chờ, kết quả gửi gần nhất.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/delivery-notifications")
public class ScanTotalSyncResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        ScanTotalSyncResource.class
    );

    private final ScanTotalSyncService scanTotalSyncService;

    public ScanTotalSyncResource(ScanTotalSyncService scanTotalSyncService) {
        this.scanTotalSyncService = scanTotalSyncService;
    }

    @PostMapping("/{id}/scan-total-sync")
    public ResponseEntity<ScanTotalSyncResultDTO> sync(
        @PathVariable("id") Long id,
        @RequestParam(value = "force", defaultValue = "false") boolean force,
        @RequestParam(value = "trigger", defaultValue = "FE") String trigger
    ) {
        LOG.debug(
            "REST request to sync scan total of DeliveryNotification {} (force={}, trigger={})",
            id,
            force,
            trigger
        );
        if (!force && scanTotalSyncService.pending(id) == null) {
            ScanTotalSyncResultDTO skipped = new ScanTotalSyncResultDTO();
            skipped.setStatus(ScanTotalSyncService.STATUS_SKIPPED);
            skipped.setDeliveryNotificationId(id);
            skipped.setTrigger(trigger);
            skipped.setSyncedAt(Instant.now().toString());
            skipped.setMessage("Không có thay đổi chưa gửi");
            return ResponseEntity.ok(skipped);
        }
        return ResponseEntity.ok(scanTotalSyncService.sync(id, trigger));
    }

    @GetMapping("/{id}/scan-total-sync")
    public ResponseEntity<Map<String, Object>> status(
        @PathVariable("id") Long id
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("deliveryNotificationId", id);
        body.put(
            "gatewayConfigured",
            scanTotalSyncService.isGatewayConfigured()
        );
        ScanTotalChangeTracker.Pending pending = scanTotalSyncService.pending(
            id
        );
        Map<String, Object> pendingInfo = null;
        if (pending != null) {
            pendingInfo = new LinkedHashMap<>();
            pendingInfo.put(
                "firstMarkedAt",
                pending.getFirstMarkedAt().toString()
            );
            pendingInfo.put(
                "lastMarkedAt",
                pending.getLastMarkedAt().toString()
            );
            pendingInfo.put("failedAttempts", pending.getFailedAttempts());
            pendingInfo.put("lastError", pending.getLastError());
        }
        body.put("pending", pendingInfo);
        body.put("pendingTotal", scanTotalSyncService.pendingCount());
        body.put("lastResult", scanTotalSyncService.lastResult(id));
        body.put("preview", scanTotalSyncService.preview(id).orElse(null));
        return ResponseEntity.ok(body);
    }
}
