package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ScanPalletTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    public static ScanPallet getScanPalletSample1() {
        return new ScanPallet()
            .id(1L)
            .serialPallet("serialPallet1")
            .businessSourceId(1L)
            .businessType("businessType1");
    }

    public static ScanPallet getScanPalletSample2() {
        return new ScanPallet()
            .id(2L)
            .serialPallet("serialPallet2")
            .businessSourceId(2L)
            .businessType("businessType2");
    }

    public static ScanPallet getScanPalletRandomSampleGenerator() {
        return new ScanPallet()
            .id(longCount.incrementAndGet())
            .serialPallet(UUID.randomUUID().toString())
            .businessSourceId(longCount.incrementAndGet())
            .businessType(UUID.randomUUID().toString());
    }
}
