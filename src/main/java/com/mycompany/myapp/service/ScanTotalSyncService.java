package com.mycompany.myapp.service;

import com.mycompany.myapp.config.ApplicationProperties;
import com.mycompany.myapp.repository.VendorLabelInfoRepository;
import com.mycompany.myapp.service.dto.ScanTotalPayloadDTO;
import com.mycompany.myapp.service.dto.ScanTotalSyncResultDTO;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Gửi tổng SL thực nhận (quantityReceived = tổng initial_quantity các thùng) của từng dòng PO
 * của 1 đơn giao hàng sang gateway. Tổng là cộng dồn mọi lần scan → gửi lặp vô hại.
 * <p>
 * Kích hoạt: FE (đóng màn scan / đăng xuất / đóng tab) gọi API; job mỗi
 * {@code jobIntervalMs} gửi bù các đơn còn thay đổi trong {@link ScanTotalChangeTracker}.
 */
@Service
public class ScanTotalSyncService {

    private static final Logger LOG = LoggerFactory.getLogger(
        ScanTotalSyncService.class
    );

    public static final String STATUS_SENT = "SENT";
    public static final String STATUS_DRY_RUN = "DRY_RUN";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_SKIPPED = "SKIPPED";

    /** Số kết quả gần nhất giữ trong bộ nhớ để tra cứu */
    private static final int MAX_RESULTS = 500;

    private final VendorLabelInfoRepository vendorLabelInfoRepository;
    private final ScanTotalQueryService scanTotalQueryService;
    private final ScanTotalChangeTracker tracker;
    private final ScanTotalGatewayClient gatewayClient;
    private final ApplicationProperties.ScanTotalGateway config;
    private final TransactionTemplate readOnlyTx;

    /** Đơn đang gửi (chống gửi song song cùng 1 đơn) */
    private final Set<Long> running = ConcurrentHashMap.newKeySet();

    /** Kết quả gửi gần nhất theo đơn (LRU, tối đa MAX_RESULTS) */
    private final Map<Long, ScanTotalSyncResultDTO> lastResults =
        Collections.synchronizedMap(
            new LinkedHashMap<Long, ScanTotalSyncResultDTO>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(
                    Map.Entry<Long, ScanTotalSyncResultDTO> eldest
                ) {
                    return size() > MAX_RESULTS;
                }
            }
        );

    public ScanTotalSyncService(
        VendorLabelInfoRepository vendorLabelInfoRepository,
        ScanTotalQueryService scanTotalQueryService,
        ScanTotalChangeTracker tracker,
        ScanTotalGatewayClient gatewayClient,
        ApplicationProperties applicationProperties,
        @Qualifier(
            "partner5TransactionManager"
        ) PlatformTransactionManager transactionManager
    ) {
        this.vendorLabelInfoRepository = vendorLabelInfoRepository;
        this.scanTotalQueryService = scanTotalQueryService;
        this.tracker = tracker;
        this.gatewayClient = gatewayClient;
        this.config = applicationProperties.getScanTotalGateway();
        this.readOnlyTx = new TransactionTemplate(transactionManager);
        this.readOnlyTx.setReadOnly(true);
    }

    /**
     * Gửi ngay tổng SL thực nhận của 1 đơn. Đơn đang được gửi ở luồng khác → SKIPPED
     * (đơn vẫn trong hàng chờ nếu còn thay đổi).
     */
    public ScanTotalSyncResultDTO sync(
        Long deliveryNotificationId,
        String trigger
    ) {
        if (!running.add(deliveryNotificationId)) {
            return skipped(
                deliveryNotificationId,
                trigger,
                "Đơn đang được gửi ở tiến trình khác"
            );
        }
        Instant startedAt = Instant.now();
        try {
            Optional<ScanTotalPayloadDTO> payload = buildPayload(
                deliveryNotificationId
            );
            if (!payload.isPresent() || payload.get().getLines().isEmpty()) {
                // Đơn không tồn tại / đã xoá / không có dòng PO → không còn gì để gửi
                tracker.markSent(deliveryNotificationId, startedAt);
                return remember(
                    skipped(
                        deliveryNotificationId,
                        trigger,
                        "Đơn không tồn tại, đã xoá hoặc không có dòng PO"
                    )
                );
            }
            ScanTotalGatewayClient.Result res = gatewayClient.send(
                payload.get()
            );
            ScanTotalSyncResultDTO result = toResult(
                payload.get(),
                trigger,
                res
            );
            if (res.ok) {
                tracker.markSent(deliveryNotificationId, startedAt);
            } else {
                tracker.markChanged(deliveryNotificationId);
                tracker.markFailed(deliveryNotificationId, res.message);
            }
            return remember(result);
        } catch (RuntimeException ex) {
            LOG.error(
                "[scan-total] Lỗi gửi đơn {}: {}",
                deliveryNotificationId,
                ex.getMessage(),
                ex
            );
            tracker.markChanged(deliveryNotificationId);
            tracker.markFailed(deliveryNotificationId, ex.getMessage());
            ScanTotalSyncResultDTO result = new ScanTotalSyncResultDTO();
            result.setStatus(STATUS_FAILED);
            result.setDeliveryNotificationId(deliveryNotificationId);
            result.setTrigger(trigger);
            result.setSyncedAt(Instant.now().toString());
            result.setMessage(ex.getMessage());
            return remember(result);
        } finally {
            running.remove(deliveryNotificationId);
        }
    }

    /** Payload sẽ gửi (không gửi) — cho API tra cứu */
    public Optional<ScanTotalPayloadDTO> preview(Long deliveryNotificationId) {
        return buildPayload(deliveryNotificationId);
    }

    public ScanTotalSyncResultDTO lastResult(Long deliveryNotificationId) {
        return lastResults.get(deliveryNotificationId);
    }

    public ScanTotalChangeTracker.Pending pending(Long deliveryNotificationId) {
        return tracker.get(deliveryNotificationId);
    }

    public int pendingCount() {
        return tracker.size();
    }

    public boolean isGatewayConfigured() {
        return gatewayClient.isConfigured();
    }

    /** Job gửi bù: mọi đơn còn trong hàng chờ (có thay đổi chưa gửi / gửi lỗi) */
    @Scheduled(
        initialDelayString = "${application.scan-total-gateway.job-interval-ms:300000}",
        fixedDelayString = "${application.scan-total-gateway.job-interval-ms:300000}"
    )
    public void syncPendingJob() {
        tracker.evictExpired();
        List<Long> ids = tracker.snapshot();
        if (ids.isEmpty()) {
            return;
        }
        LOG.info("[scan-total] Job gửi bù {} đơn", ids.size());
        for (Long id : ids) {
            sync(id, "JOB");
        }
    }

    /** Khởi động BE: hàng chờ trong bộ nhớ đã mất → nạp các đơn có thùng tạo / sửa gần đây */
    @EventListener(ApplicationReadyEvent.class)
    public void seedOnStartup() {
        try {
            Instant since = Instant.now().minus(
                Duration.ofHours(Math.max(0, config.getStartupSeedHours()))
            );
            List<Long> ids = readOnlyTx.execute(status ->
                vendorLabelInfoRepository.findDeliveryNotificationIdsChangedSince(
                    since
                )
            );
            if (ids == null || ids.isEmpty()) {
                return;
            }
            ids.forEach(tracker::markChanged);
            LOG.info(
                "[scan-total] Khởi động: nạp {} đơn có thùng thay đổi từ {} vào hàng chờ",
                ids.size(),
                since
            );
        } catch (RuntimeException ex) {
            LOG.warn(
                "[scan-total] Không nạp được hàng chờ khi khởi động: {}",
                ex.getMessage()
            );
        }
    }

    private Optional<ScanTotalPayloadDTO> buildPayload(
        Long deliveryNotificationId
    ) {
        return scanTotalQueryService.findByDeliveryNotificationId(
            deliveryNotificationId
        );
    }

    private ScanTotalSyncResultDTO toResult(
        ScanTotalPayloadDTO payload,
        String trigger,
        ScanTotalGatewayClient.Result res
    ) {
        ScanTotalSyncResultDTO result = new ScanTotalSyncResultDTO();
        result.setStatus(
            res.dryRun ? STATUS_DRY_RUN : res.ok ? STATUS_SENT : STATUS_FAILED
        );
        result.setDeliveryNotificationId(payload.getDeliveryNotificationId());
        result.setTrigger(trigger);
        result.setSyncedAt(Instant.now().toString());
        result.setLineCount(payload.getLines().size());
        result.setTotalQuantityReceived(
            payload
                .getLines()
                .stream()
                .mapToLong(l ->
                    l.getQuantityReceived() == null
                        ? 0L
                        : l.getQuantityReceived()
                )
                .sum()
        );
        result.setGatewayStatus(res.status);
        result.setMessage(res.message);
        result.setPayload(payload);
        return result;
    }

    private static ScanTotalSyncResultDTO skipped(
        Long deliveryNotificationId,
        String trigger,
        String message
    ) {
        ScanTotalSyncResultDTO result = new ScanTotalSyncResultDTO();
        result.setStatus(STATUS_SKIPPED);
        result.setDeliveryNotificationId(deliveryNotificationId);
        result.setTrigger(trigger);
        result.setSyncedAt(Instant.now().toString());
        result.setMessage(message);
        return result;
    }

    private ScanTotalSyncResultDTO remember(ScanTotalSyncResultDTO result) {
        lastResults.put(result.getDeliveryNotificationId(), result);
        return result;
    }
}
