package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InboundWMSSessionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    public static InboundWMSSession getInboundWMSSessionSample1() {
        return new InboundWMSSession()
            .id(1L)
            .status("status1")
            .createdBy("createdBy1");
    }

    public static InboundWMSSession getInboundWMSSessionSample2() {
        return new InboundWMSSession()
            .id(2L)
            .status("status2")
            .createdBy("createdBy2");
    }

    public static InboundWMSSession getInboundWMSSessionRandomSampleGenerator() {
        return new InboundWMSSession()
            .id(longCount.incrementAndGet())
            .status(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString());
    }
}
