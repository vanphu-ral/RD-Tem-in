package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PoImportTemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );
    private static final AtomicInteger intCount = new AtomicInteger(
        random.nextInt() + (2 * Short.MAX_VALUE)
    );

    public static PoImportTem getPoImportTemSample1() {
        return new PoImportTem()
            .id(1L)
            .poNumber("poNumber1")
            .vendorCode("vendorCode1")
            .vendorName("vendorName1")
            .quantityContainer(1)
            .totalQuantity(1)
            .status("status1")
            .createdBy("createdBy1")
            .updatedBy("updatedBy1")
            .deletedBy("deletedBy1");
    }

    public static PoImportTem getPoImportTemSample2() {
        return new PoImportTem()
            .id(2L)
            .poNumber("poNumber2")
            .vendorCode("vendorCode2")
            .vendorName("vendorName2")
            .quantityContainer(2)
            .totalQuantity(2)
            .status("status2")
            .createdBy("createdBy2")
            .updatedBy("updatedBy2")
            .deletedBy("deletedBy2");
    }

    public static PoImportTem getPoImportTemRandomSampleGenerator() {
        return new PoImportTem()
            .id(longCount.incrementAndGet())
            .poNumber(UUID.randomUUID().toString())
            .vendorCode(UUID.randomUUID().toString())
            .vendorName(UUID.randomUUID().toString())
            .quantityContainer(intCount.incrementAndGet())
            .totalQuantity(intCount.incrementAndGet())
            .status(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .updatedBy(UUID.randomUUID().toString())
            .deletedBy(UUID.randomUUID().toString());
    }
}
