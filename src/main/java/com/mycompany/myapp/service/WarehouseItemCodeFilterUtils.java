package com.mycompany.myapp.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

final class WarehouseItemCodeFilterUtils {

    static final char CSV_SEPARATOR = '\u001F';

    private WarehouseItemCodeFilterUtils() {}

    static List<String> dedupeItemCodes(List<String> itemCodes) {
        if (itemCodes == null || itemCodes.isEmpty()) {
            return List.of();
        }
        Set<String> unique = new LinkedHashSet<>();
        for (String code : itemCodes) {
            String normalized = Optional.ofNullable(code).orElse("").trim();
            if (!normalized.isEmpty()) {
                unique.add(normalized);
            }
        }
        return new ArrayList<>(unique);
    }

    static String encodeItemCodesCsv(List<String> itemCodes) {
        return String.join(String.valueOf(CSV_SEPARATOR), itemCodes);
    }

    static String normalizeItemCodeKey(String itemCode) {
        if (itemCode == null) {
            return "";
        }
        return itemCode.trim().toUpperCase(Locale.ROOT);
    }
}
