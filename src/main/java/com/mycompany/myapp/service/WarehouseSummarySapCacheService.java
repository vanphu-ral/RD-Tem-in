package com.mycompany.myapp.service;

import com.mycompany.myapp.repository.partner4.SapOitmRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
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
    private volatile Map<String, String> itemCodeToItemName = Map.of();
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

    public String resolveItemName(String itemCode) {
        if (itemCode == null || itemCode.isBlank()) {
            return "";
        }
        ensureLoaded();
        return itemCodeToItemName.getOrDefault(itemCode.trim(), "");
    }

    public List<String> getDistinctGroupNames() {
        ensureLoaded();
        return itemCodeToGroupName
            .values()
            .stream()
            .filter(
                name -> name != null && !name.isBlank() && !"-".equals(name)
            )
            .map(String::trim)
            .distinct()
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .collect(Collectors.toList());
    }

    public List<String> findItemCodesByNameContaining(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return List.of();
        }
        ensureLoaded();
        String needle = searchText.trim().toLowerCase(Locale.ROOT);
        return itemCodeToItemName
            .entrySet()
            .stream()
            .filter(
                entry ->
                    entry.getValue() != null &&
                    entry.getValue().toLowerCase(Locale.ROOT).contains(needle)
            )
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    public List<String> findItemCodesByGroupNameContaining(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return List.of();
        }
        ensureLoaded();
        String needle = searchText.trim().toLowerCase(Locale.ROOT);
        return itemCodeToGroupName
            .entrySet()
            .stream()
            .filter(
                entry ->
                    entry.getValue() != null &&
                    !"-".equals(entry.getValue()) &&
                    entry.getValue().toLowerCase(Locale.ROOT).contains(needle)
            )
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
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
            Map<String, String> groupMap = new HashMap<>(
                Math.max(16, rows.size() * 2)
            );
            Map<String, String> nameMap = new HashMap<>(
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
                groupMap.merge(code, groupName, (existing, incoming) ->
                    "-".equals(existing) ? incoming : existing
                );
                if (row.length > 2 && row[2] != null) {
                    String rawName = String.valueOf(row[2]).trim();
                    if (!rawName.isEmpty()) {
                        nameMap.putIfAbsent(code, rawName);
                    }
                }
            }
            itemCodeToGroupName = Collections.unmodifiableMap(groupMap);
            itemCodeToItemName = Collections.unmodifiableMap(nameMap);
            LOG.info(
                "Loaded {} SAP_OITM item mappings for warehouse summary ({} with item names)",
                itemCodeToGroupName.size(),
                itemCodeToItemName.size()
            );
        } catch (Exception ex) {
            LOG.warn(
                "Failed to preload SAP_OITM data, warehouse summary will use fallback",
                ex
            );
            itemCodeToGroupName = Map.of();
            itemCodeToItemName = Map.of();
        }
    }
}
