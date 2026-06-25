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
    private volatile Map<String, String> itemCodeToNormalizedName = Map.of();
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
        return itemCodeToGroupName.getOrDefault(
            normalizeItemCode(itemCode),
            "-"
        );
    }

    public String resolveItemName(String itemCode) {
        if (itemCode == null || itemCode.isBlank()) {
            return "";
        }
        ensureLoaded();
        return itemCodeToItemName.getOrDefault(normalizeItemCode(itemCode), "");
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
        String[] tokens = WarehouseSearchTextUtils.splitSearchTokens(
            searchText
        );
        if (tokens.length == 0) {
            return List.of();
        }

        List<String> matchedCodes = findItemCodesInCacheByName(tokens);
        if (matchedCodes.isEmpty() && itemCodeToItemName.isEmpty()) {
            matchedCodes = findItemCodesInDatabaseByName(tokens);
        }
        return normalizeItemCodes(matchedCodes);
    }

    public List<String> findItemCodesByGroupNameContaining(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return List.of();
        }
        ensureLoaded();
        String[] tokens = WarehouseSearchTextUtils.splitSearchTokens(
            searchText
        );
        if (tokens.length == 0) {
            return List.of();
        }
        return normalizeItemCodes(
            itemCodeToGroupName
                .entrySet()
                .stream()
                .filter(
                    entry ->
                        entry.getValue() != null &&
                        !"-".equals(entry.getValue()) &&
                        WarehouseSearchTextUtils.matchesAllTokens(
                            entry.getValue(),
                            tokens
                        )
                )
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
        );
    }

    private List<String> findItemCodesInCacheByName(String[] tokens) {
        return itemCodeToNormalizedName
            .entrySet()
            .stream()
            .filter(entry ->
                WarehouseSearchTextUtils.matchesAllTokensPreNormalized(
                    entry.getValue(),
                    tokens
                )
            )
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    private List<String> findItemCodesInDatabaseByName(String[] tokens) {
        try {
            String pattern = "%" + tokens[0] + "%";
            List<String> candidates =
                sapOitmRepository.findItemCodesByItemNameLike(pattern);
            if (candidates == null || candidates.isEmpty()) {
                return List.of();
            }
            if (tokens.length == 1) {
                return candidates;
            }

            Map<String, String> namesByCode = new HashMap<>();
            for (String code : candidates) {
                String normalizedCode = normalizeItemCode(code);
                if (normalizedCode.isEmpty()) {
                    continue;
                }
                String cachedName = itemCodeToItemName.get(normalizedCode);
                if (cachedName != null && !cachedName.isBlank()) {
                    namesByCode.put(normalizedCode, cachedName);
                }
            }

            List<String> missingCodes = candidates
                .stream()
                .map(this::normalizeItemCode)
                .filter(
                    code -> !code.isEmpty() && !namesByCode.containsKey(code)
                )
                .distinct()
                .collect(Collectors.toList());

            if (!missingCodes.isEmpty()) {
                List<Object[]> rows =
                    sapOitmRepository.findItemNamesByItemCodesIn(missingCodes);
                for (Object[] row : rows) {
                    if (row == null || row.length < 2 || row[0] == null) {
                        continue;
                    }
                    String code = normalizeItemCode(String.valueOf(row[0]));
                    String name = row[1] == null
                        ? ""
                        : String.valueOf(row[1]).trim();
                    if (!code.isEmpty() && !name.isEmpty()) {
                        namesByCode.put(code, name);
                    }
                }
            }

            return candidates
                .stream()
                .filter(code -> {
                    String name = namesByCode.get(normalizeItemCode(code));
                    return (
                        name != null &&
                        WarehouseSearchTextUtils.matchesAllTokens(name, tokens)
                    );
                })
                .collect(Collectors.toList());
        } catch (Exception ex) {
            LOG.warn(
                "Fallback SAP_OITM name lookup failed for warehouse summary search",
                ex
            );
            return List.of();
        }
    }

    private List<String> normalizeItemCodes(List<String> codes) {
        return WarehouseItemCodeFilterUtils.dedupeItemCodes(
            codes == null
                ? List.of()
                : codes
                    .stream()
                    .map(this::normalizeItemCode)
                    .filter(code -> !code.isEmpty())
                    .collect(Collectors.toList())
        );
    }

    private String normalizeItemCode(String itemCode) {
        if (itemCode == null) {
            return "";
        }
        return itemCode.trim().toUpperCase(Locale.ROOT);
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
            Map<String, String> normalizedNameMap = new HashMap<>(
                Math.max(16, rows.size() * 2)
            );
            for (Object[] row : rows) {
                if (row == null || row.length < 1 || row[0] == null) {
                    continue;
                }
                String code = normalizeItemCode(String.valueOf(row[0]));
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
                        normalizedNameMap.putIfAbsent(
                            code,
                            WarehouseSearchTextUtils.normalizeForSearch(rawName)
                        );
                    }
                }
            }
            itemCodeToGroupName = Collections.unmodifiableMap(groupMap);
            itemCodeToItemName = Collections.unmodifiableMap(nameMap);
            itemCodeToNormalizedName = Collections.unmodifiableMap(
                normalizedNameMap
            );
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
            itemCodeToNormalizedName = Map.of();
        }
    }
}
