package com.mycompany.myapp.domain;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Comparator;

/**
 * Utility class for test assertions.
 */
public class AssertUtils {

    /**
     * Comparator for ZonedDateTime that compares by instant (milliseconds since
     * epoch).
     * This allows comparing two ZonedDateTime objects in different timezones
     * as equal if they represent the same point in time.
     */
    public static final Comparator<ZonedDateTime> zonedDataTimeSameInstant =
        Comparator.nullsFirst(
            Comparator.comparingLong(zdt -> zdt.toInstant().toEpochMilli())
        );

    /**
     * Comparator for BigDecimal that compares by decimal value.
     * This handles precision issues correctly when comparing monetary values.
     */
    public static final Comparator<BigDecimal> bigDecimalCompareTo =
        Comparator.nullsFirst((b1, b2) -> {
            if (b1 == null || b2 == null) {
                return b1 == null ? (b2 == null ? 0 : -1) : 1;
            }
            return b1.stripTrailingZeros().compareTo(b2.stripTrailingZeros());
        });
}
