package com.mycompany.myapp.service;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;

final class WarehouseSearchTextUtils {

    private WarehouseSearchTextUtils() {}

    static String normalizeForSearch(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
            .replaceAll("\\p{M}+", "")
            .toLowerCase(Locale.ROOT)
            .trim();
    }

    static String[] splitSearchTokens(String searchText) {
        String normalized = normalizeForSearch(searchText);
        if (normalized.isEmpty()) {
            return new String[0];
        }
        return Arrays.stream(normalized.split("\\s+"))
            .filter(token -> !token.isEmpty())
            .toArray(String[]::new);
    }

    static boolean matchesAllTokens(String value, String[] tokens) {
        if (tokens.length == 0) {
            return false;
        }
        String normalized = normalizeForSearch(value);
        return matchesAllTokensPreNormalized(normalized, tokens);
    }

    static boolean matchesAllTokensPreNormalized(
        String normalizedValue,
        String[] tokens
    ) {
        if (normalizedValue == null || normalizedValue.isEmpty()) {
            return false;
        }
        for (String token : tokens) {
            if (!normalizedValue.contains(token)) {
                return false;
            }
        }
        return true;
    }
}
