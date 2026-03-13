package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PoDetailTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );
    private static final AtomicInteger intCount = new AtomicInteger(
        random.nextInt() + (2 * Short.MAX_VALUE)
    );

    public static PoDetail getPoDetailSample1() {
        return new PoDetail()
            .id(1L)
            .sapCode("sapCode1")
            .sapName("sapName1")
            .quantityContainer(1)
            .totalQuantity(1)
            .partNumber("partNumber1");
    }

    public static PoDetail getPoDetailSample2() {
        return new PoDetail()
            .id(2L)
            .sapCode("sapCode2")
            .sapName("sapName2")
            .quantityContainer(2)
            .totalQuantity(2)
            .partNumber("partNumber2");
    }

    public static PoDetail getPoDetailRandomSampleGenerator() {
        return new PoDetail()
            .id(longCount.incrementAndGet())
            .sapCode(UUID.randomUUID().toString())
            .sapName(UUID.randomUUID().toString())
            .quantityContainer(intCount.incrementAndGet())
            .totalQuantity(intCount.incrementAndGet())
            .partNumber(UUID.randomUUID().toString());
    }
}
