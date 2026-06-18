package com.mycompany.myapp.service;

import com.mycompany.panacimmc.domain.WarehouseAreaItemSummaryResponse;
import com.mycompany.panacimmc.domain.WarehouseSummaryStatsCombined;
import com.mycompany.panacimmc.repository.InventoryRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;

@Service
public class WarehouseSummaryDataCacheService {

    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final InventoryRepository inventoryRepository;

    private volatile CachedSnapshot snapshot;

    public WarehouseSummaryDataCacheService(
        InventoryRepository inventoryRepository
    ) {
        this.inventoryRepository = inventoryRepository;
    }

    public CachedSnapshot getOrLoadFullSnapshot() {
        CachedSnapshot current = snapshot;
        if (current != null && !isExpired(current)) {
            return current;
        }
        synchronized (this) {
            current = snapshot;
            if (current != null && !isExpired(current)) {
                return current;
            }
            CompletableFuture<
                List<WarehouseAreaItemSummaryResponse>
            > rawFuture = CompletableFuture.supplyAsync(() ->
                inventoryRepository.getWarehouseSummaryByAreaAndItem()
            );
            CompletableFuture<WarehouseSummaryStatsCombined> statsFuture =
                CompletableFuture.supplyAsync(() ->
                    inventoryRepository.getWarehouseSummaryStatsCombined()
                );
            snapshot = new CachedSnapshot(
                rawFuture.join(),
                statsFuture.join(),
                Instant.now()
            );
            return snapshot;
        }
    }

    public void invalidate() {
        snapshot = null;
    }

    private boolean isExpired(CachedSnapshot cache) {
        return (
            Duration.between(cache.getLoadedAt(), Instant.now()).compareTo(
                CACHE_TTL
            ) >
            0
        );
    }

    public static final class CachedSnapshot {

        private final List<WarehouseAreaItemSummaryResponse> rawRows;
        private final WarehouseSummaryStatsCombined stats;
        private final Instant loadedAt;

        public CachedSnapshot(
            List<WarehouseAreaItemSummaryResponse> rawRows,
            WarehouseSummaryStatsCombined stats,
            Instant loadedAt
        ) {
            this.rawRows = rawRows;
            this.stats = stats;
            this.loadedAt = loadedAt;
        }

        public List<WarehouseAreaItemSummaryResponse> getRawRows() {
            return rawRows;
        }

        public WarehouseSummaryStatsCombined getStats() {
            return stats;
        }

        public Instant getLoadedAt() {
            return loadedAt;
        }
    }
}
