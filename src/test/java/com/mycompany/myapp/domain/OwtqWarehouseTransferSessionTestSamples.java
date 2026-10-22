package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class OwtqWarehouseTransferSessionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    public static OwtqWarehouseTransferSession getOwtqWarehouseTransferSessionSample1() {
        return new OwtqWarehouseTransferSession()
            .id(1L)
            .warehouseTransferSessionId(1L)
            .owtqDocEntry(1L);
    }

    public static OwtqWarehouseTransferSession getOwtqWarehouseTransferSessionSample2() {
        return new OwtqWarehouseTransferSession()
            .id(2L)
            .warehouseTransferSessionId(2L)
            .owtqDocEntry(2L);
    }

    public static OwtqWarehouseTransferSession getOwtqWarehouseTransferSessionRandomSampleGenerator() {
        return new OwtqWarehouseTransferSession()
            .id(longCount.incrementAndGet())
            .warehouseTransferSessionId(longCount.incrementAndGet())
            .owtqDocEntry(longCount.incrementAndGet());
    }
}
