package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class WarehouseTransferSessionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    public static WarehouseTransferSession getWarehouseTransferSessionSample1() {
        return new WarehouseTransferSession()
            .id(1L)
            .status("status1")
            .note("note1")
            .createdBy("createdBy1")
            .updatedBy("updatedBy1")
            .deletedBy("deletedBy1");
    }

    public static WarehouseTransferSession getWarehouseTransferSessionSample2() {
        return new WarehouseTransferSession()
            .id(2L)
            .status("status2")
            .note("note2")
            .createdBy("createdBy2")
            .updatedBy("updatedBy2")
            .deletedBy("deletedBy2");
    }

    public static WarehouseTransferSession getWarehouseTransferSessionRandomSampleGenerator() {
        return new WarehouseTransferSession()
            .id(longCount.incrementAndGet())
            .status(UUID.randomUUID().toString())
            .note(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .updatedBy(UUID.randomUUID().toString())
            .deletedBy(UUID.randomUUID().toString());
    }
}
