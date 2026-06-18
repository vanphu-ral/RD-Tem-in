package com.mycompany.myapp.service;

import com.mycompany.myapp.repository.partner4.SapOitmRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.PostConstruct;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WarehouseSummarySapCacheService {

    private static final Logger LOG = LoggerFactory.getLogger(
        WarehouseSummarySapCacheService.class
    );

    private final SapOitmRepository sapOitmRepository;
    private volatile Map<String, String> itemCodeToGroupName = Map.of();
    private volatile boolean loaded;

    public WarehouseSummarySapCacheService(
        SapOitmRepository sapOitmRepository
    ) {
        this.sapOitmRepository = sapOitmRepository;
    }

    @PostConstruct
    public void warmUpInBackground() {
        CompletableFuture.runAsync(this::ensureLoaded);
    }

    public String resolveGroupName(String itemCode) {
        if (itemCode == null || itemCode.isBlank()) {
            return "-";
        }
        ensureLoaded();
        return itemCodeToGroupName.getOrDefault(itemCode.trim(), "-");
    }

    private void ensureLoaded() {
        if (loaded) {
            return;
        }
        synchronized (this) {
            if (loaded) {
                return;
            }
            loadCache();
            loaded = true;
        }
    }

    private void loadCache() {
        try {
            List<Object[]> rows = sapOitmRepository.findAllItemGroupNames();
            Map<String, String> map = new HashMap<>(
                Math.max(16, rows.size() * 2)
            );
            for (Object[] row : rows) {
                if (row == null || row.length < 1 || row[0] == null) {
                    continue;
                }
                String code = String.valueOf(row[0]).trim();
                if (code.isEmpty()) {
                    continue;
                }
                String groupName = "-";
                if (row.length > 1 && row[1] != null) {
                    String rawGroup = String.valueOf(row[1]).trim();
                    if (!rawGroup.isEmpty()) {
                        groupName = rawGroup;
                    }
                }
                map.merge(code, groupName, (existing, incoming) ->
                    "-".equals(existing) ? incoming : existing
                );
            }
            itemCodeToGroupName = Collections.unmodifiableMap(map);
            LOG.info(
                "Loaded {} SAP_OITM item group mappings for warehouse summary",
                itemCodeToGroupName.size()
            );
        } catch (Exception ex) {
            LOG.warn(
                "Failed to preload SAP_OITM group names, warehouse summary will use fallback",
                ex
            );
            itemCodeToGroupName = Map.of();
        }
    }
}
