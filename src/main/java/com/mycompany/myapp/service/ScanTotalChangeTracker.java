package com.mycompany.myapp.service;

import com.mycompany.myapp.config.ApplicationProperties;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Danh sách (trong bộ nhớ) các đơn giao hàng có thùng vừa thay đổi (tạo / sửa / xoá) — chờ gửi
 * tổng SL thực nhận sang gateway. Không lưu DB.
 * <p>
 * Giới hạn bộ nhớ:
 * <ul>
 *   <li>Gửi thành công → bỏ đơn khỏi danh sách ngay.</li>
 *   <li>Gửi lỗi liên tục quá {@code pendingTtlHours} → bỏ (log ERROR).</li>
 *   <li>Vượt {@code maxPending} đơn → bỏ đơn đánh dấu cũ nhất (log WARN).</li>
 * </ul>
 * Mỗi đơn ~100 byte → 5000 đơn chỉ vài trăm KB.
 */
@Component
public class ScanTotalChangeTracker {

    private static final Logger LOG = LoggerFactory.getLogger(
        ScanTotalChangeTracker.class
    );

    /** Trạng thái chờ gửi của 1 đơn */
    public static class Pending {

        private final Instant firstMarkedAt;
        private volatile Instant lastMarkedAt;
        private volatile int failedAttempts;
        private volatile String lastError;

        Pending(Instant now) {
            this.firstMarkedAt = now;
            this.lastMarkedAt = now;
        }

        public Instant getFirstMarkedAt() {
            return firstMarkedAt;
        }

        public Instant getLastMarkedAt() {
            return lastMarkedAt;
        }

        public int getFailedAttempts() {
            return failedAttempts;
        }

        public String getLastError() {
            return lastError;
        }
    }

    private final Map<Long, Pending> pending = new ConcurrentHashMap<>();

    private final ApplicationProperties.ScanTotalGateway config;

    public ScanTotalChangeTracker(ApplicationProperties applicationProperties) {
        this.config = applicationProperties.getScanTotalGateway();
    }

    /**
     * Đánh dấu sau khi transaction hiện tại commit (job không đọc phải dữ liệu chưa commit rồi
     * bỏ đơn khỏi hàng chờ). Không có transaction → đánh dấu ngay.
     */
    public void markChangedAfterCommit(Long deliveryNotificationId) {
        if (deliveryNotificationId == null) {
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        markChanged(deliveryNotificationId);
                    }
                }
            );
        } else {
            markChanged(deliveryNotificationId);
        }
    }

    /** Đánh dấu đơn có thay đổi (bỏ qua id null) */
    public void markChanged(Long deliveryNotificationId) {
        if (deliveryNotificationId == null) {
            return;
        }
        Instant now = Instant.now();
        pending.compute(deliveryNotificationId, (id, current) -> {
            if (current == null) {
                return new Pending(now);
            }
            current.lastMarkedAt = now;
            return current;
        });
        enforceMaxSize();
    }

    /**
     * Gửi thành công → bỏ đơn khỏi danh sách, trừ khi đơn lại vừa thay đổi sau thời điểm bắt đầu
     * gửi (giữ lại để lần sau gửi tổng mới).
     */
    public void markSent(Long deliveryNotificationId, Instant syncStartedAt) {
        pending.computeIfPresent(deliveryNotificationId, (id, current) ->
            current.lastMarkedAt.isAfter(syncStartedAt) ? current : null
        );
    }

    /** Gửi lỗi → giữ lại để job gửi lại, ghi nhận lỗi */
    public void markFailed(Long deliveryNotificationId, String error) {
        pending.computeIfPresent(deliveryNotificationId, (id, current) -> {
            current.failedAttempts++;
            current.lastError = error;
            return current;
        });
    }

    public Pending get(Long deliveryNotificationId) {
        return pending.get(deliveryNotificationId);
    }

    public int size() {
        return pending.size();
    }

    /** Ảnh chụp danh sách đơn đang chờ gửi (cho job) */
    public List<Long> snapshot() {
        return new ArrayList<>(pending.keySet());
    }

    /** Bỏ các đơn gửi lỗi liên tục quá TTL */
    public void evictExpired() {
        Instant limit = Instant.now().minus(
            Duration.ofHours(Math.max(1, config.getPendingTtlHours()))
        );
        pending.forEach((id, p) -> {
            if (p.failedAttempts > 0 && p.firstMarkedAt.isBefore(limit)) {
                if (pending.remove(id, p)) {
                    LOG.error(
                        "[scan-total] Bỏ đơn {} khỏi hàng chờ: gửi lỗi {} lần từ {} (lỗi cuối: {})",
                        id,
                        p.failedAttempts,
                        p.firstMarkedAt,
                        p.lastError
                    );
                }
            }
        });
    }

    private void enforceMaxSize() {
        int max = Math.max(100, config.getMaxPending());
        if (pending.size() <= max) {
            return;
        }
        pending
            .entrySet()
            .stream()
            .sorted(Comparator.comparing(e -> e.getValue().lastMarkedAt))
            .limit((long) pending.size() - max)
            .map(Map.Entry::getKey)
            .forEach(id -> {
                if (pending.remove(id) != null) {
                    LOG.warn(
                        "[scan-total] Hàng chờ vượt {} đơn — bỏ đơn {} (cũ nhất)",
                        max,
                        id
                    );
                }
            });
    }
}
