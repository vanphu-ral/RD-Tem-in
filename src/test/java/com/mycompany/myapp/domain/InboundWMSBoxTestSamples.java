package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InboundWMSBoxTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );
    private static final AtomicInteger intCount = new AtomicInteger(
        random.nextInt() + (2 * Short.MAX_VALUE)
    );

    public static InboundWMSBox getInboundWMSBoxSample1() {
        return new InboundWMSBox()
            .id(1L)
            .warehouseNoteInfoId(1)
            .serialBox("serialBox1")
            .createdBy("createdBy1");
    }

    public static InboundWMSBox getInboundWMSBoxSample2() {
        return new InboundWMSBox()
            .id(2L)
            .warehouseNoteInfoId(2)
            .serialBox("serialBox2")
            .createdBy("createdBy2");
    }

    public static InboundWMSBox getInboundWMSBoxRandomSampleGenerator() {
        return new InboundWMSBox()
            .id(longCount.incrementAndGet())
            .warehouseNoteInfoId(intCount.incrementAndGet())
            .serialBox(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString());
    }
}
