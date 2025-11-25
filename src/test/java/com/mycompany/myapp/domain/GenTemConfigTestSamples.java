package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class GenTemConfigTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );
    private static final AtomicInteger intCount = new AtomicInteger(
        random.nextInt() + (2 * Short.MAX_VALUE)
    );

    public static GenTemConfig getGenTemConfigSample1() {
        return new GenTemConfig()
            .id(1L)
            .tpNk("tpNk1")
            .rank("rank1")
            .mfg("mfg1")
            .quantityPerBox(1)
            .note("note1")
            .numBoxPerPallet(1)
            .branch("branch1")
            .groupName("groupName1")
            .customerName("customerName1")
            .poNumber("poNumber1")
            .dateCode("dateCode1")
            .itemNoSku("itemNoSku1")
            .qdsxNo("qdsxNo1")
            .inspectorName("inspectorName1")
            .inspectionResult("inspectionResult1")
            .updatedBy("updatedBy1");
    }

    public static GenTemConfig getGenTemConfigSample2() {
        return new GenTemConfig()
            .id(2L)
            .tpNk("tpNk2")
            .rank("rank2")
            .mfg("mfg2")
            .quantityPerBox(2)
            .note("note2")
            .numBoxPerPallet(2)
            .branch("branch2")
            .groupName("groupName2")
            .customerName("customerName2")
            .poNumber("poNumber2")
            .dateCode("dateCode2")
            .itemNoSku("itemNoSku2")
            .qdsxNo("qdsxNo2")
            .inspectorName("inspectorName2")
            .inspectionResult("inspectionResult2")
            .updatedBy("updatedBy2");
    }

    public static GenTemConfig getGenTemConfigRandomSampleGenerator() {
        return new GenTemConfig()
            .id(longCount.incrementAndGet())
            .tpNk(UUID.randomUUID().toString())
            .rank(UUID.randomUUID().toString())
            .mfg(UUID.randomUUID().toString())
            .quantityPerBox(intCount.incrementAndGet())
            .note(UUID.randomUUID().toString())
            .numBoxPerPallet(intCount.incrementAndGet())
            .branch(UUID.randomUUID().toString())
            .groupName(UUID.randomUUID().toString())
            .customerName(UUID.randomUUID().toString())
            .poNumber(UUID.randomUUID().toString())
            .dateCode(UUID.randomUUID().toString())
            .itemNoSku(UUID.randomUUID().toString())
            .qdsxNo(UUID.randomUUID().toString())
            .inspectorName(UUID.randomUUID().toString())
            .inspectionResult(UUID.randomUUID().toString())
            .updatedBy(UUID.randomUUID().toString());
    }
}
