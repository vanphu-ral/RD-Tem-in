package com.mycompany.myapp.domain;

import java.time.ZonedDateTime;
import java.util.Comparator;

/**
 * Utility class for test assertions.
 */
public class AssertUtils {

    /**
     * Comparator for ZonedDateTime that compares by instant (milliseconds since epoch).
     * This allows comparing two ZonedDateTime objects in different timezones
     * as equal if they represent the same point in time.
     */
    public static final Comparator<ZonedDateTime> zonedDataTimeSameInstant =
        Comparator.nullsFirst(
            Comparator.comparingLong(zdt -> zdt.toInstant().toEpochMilli())
        );
}
