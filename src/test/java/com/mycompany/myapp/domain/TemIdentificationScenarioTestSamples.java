package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TemIdentificationScenarioTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    public static TemIdentificationScenario getTemIdentificationScenarioSample1() {
        return new TemIdentificationScenario()
            .id(1L)
            .vendorCode("vendorCode1")
            .vendorName("vendorName1")
            .mappingConfig("mappingConfig1")
            .createdBy("createdBy1")
            .updatedBy("updatedBy1");
    }

    public static TemIdentificationScenario getTemIdentificationScenarioSample2() {
        return new TemIdentificationScenario()
            .id(2L)
            .vendorCode("vendorCode2")
            .vendorName("vendorName2")
            .mappingConfig("mappingConfig2")
            .createdBy("createdBy2")
            .updatedBy("updatedBy2");
    }

    public static TemIdentificationScenario getTemIdentificationScenarioRandomSampleGenerator() {
        return new TemIdentificationScenario()
            .id(longCount.incrementAndGet())
            .vendorCode(UUID.randomUUID().toString())
            .vendorName(UUID.randomUUID().toString())
            .mappingConfig(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .updatedBy(UUID.randomUUID().toString());
    }
}
