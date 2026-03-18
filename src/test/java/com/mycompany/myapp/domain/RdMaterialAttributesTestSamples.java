package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RdMaterialAttributesTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );
    private static final AtomicInteger intCount = new AtomicInteger(
        random.nextInt() + (2 * Short.MAX_VALUE)
    );

    public static RdMaterialAttributes getRdMaterialAttributesSample1() {
        return new RdMaterialAttributes()
            .id(1L)
            .attributes("attributes1")
            .description("description1")
            .attributesType("TYPE1")
            .createdBy("createdBy1")
            .updatedBy("updatedBy1");
    }

    public static RdMaterialAttributes getRdMaterialAttributesSample2() {
        return new RdMaterialAttributes()
            .id(2L)
            .attributes("attributes2")
            .description("description2")
            .attributesType("TYPE2")
            .createdBy("createdBy2")
            .updatedBy("updatedBy2");
    }

    public static RdMaterialAttributes getRdMaterialAttributesRandomSampleGenerator() {
        return new RdMaterialAttributes()
            .id(longCount.incrementAndGet())
            .attributes(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .attributesType(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .updatedBy(UUID.randomUUID().toString());
    }
}
