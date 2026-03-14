package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AttributesTypeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    public static AttributesType getAttributesTypeSample1() {
        return new AttributesType().id(1L).description("description1");
    }

    public static AttributesType getAttributesTypeSample2() {
        return new AttributesType().id(2L).description("description2");
    }

    public static AttributesType getAttributesTypeRandomSampleGenerator() {
        return new AttributesType()
            .id(longCount.incrementAndGet())
            .description(UUID.randomUUID().toString());
    }
}
