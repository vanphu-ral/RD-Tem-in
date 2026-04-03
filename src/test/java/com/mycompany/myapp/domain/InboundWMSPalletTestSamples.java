package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InboundWMSPalletTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );
    private static final AtomicInteger intCount = new AtomicInteger(
        random.nextInt() + (2 * Short.MAX_VALUE)
    );

    public static InboundWMSPallet getInboundWMSPalletSample1() {
        return new InboundWMSPallet()
            .id(1L)
            .inboundWMSSessionId(1)
            .warehouseNoteInfoId(1)
            .serialPallet("serialPallet1")
            .wmsSendStatus("wmsSendStatus1")
            .createdBy("createdBy1");
    }

    public static InboundWMSPallet getInboundWMSPalletSample2() {
        return new InboundWMSPallet()
            .id(2L)
            .inboundWMSSessionId(2)
            .warehouseNoteInfoId(2)
            .serialPallet("serialPallet2")
            .wmsSendStatus("wmsSendStatus2")
            .createdBy("createdBy2");
    }

    public static InboundWMSPallet getInboundWMSPalletRandomSampleGenerator() {
        return new InboundWMSPallet()
            .id(longCount.incrementAndGet())
            .inboundWMSSessionId(intCount.incrementAndGet())
            .warehouseNoteInfoId(intCount.incrementAndGet())
            .serialPallet(UUID.randomUUID().toString())
            .wmsSendStatus(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString());
    }
}
