package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SerialBoxPalletMappingTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    public static SerialBoxPalletMapping getSerialBoxPalletMappingSample1() {
        return new SerialBoxPalletMapping()
            .id(1L)
            .serialBox("serialBox1")
            .serialPallet("serialPallet1")
            .updatedBy("updatedBy1");
    }

    public static SerialBoxPalletMapping getSerialBoxPalletMappingSample2() {
        return new SerialBoxPalletMapping()
            .id(2L)
            .serialBox("serialBox2")
            .serialPallet("serialPallet2")
            .updatedBy("updatedBy2");
    }

    public static SerialBoxPalletMapping getSerialBoxPalletMappingRandomSampleGenerator() {
        return new SerialBoxPalletMapping()
            .id(longCount.incrementAndGet())
            .serialBox(UUID.randomUUID().toString())
            .serialPallet(UUID.randomUUID().toString())
            .updatedBy(UUID.randomUUID().toString());
    }
}
