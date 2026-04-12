package com.mycompany.myapp.domain;

import com.mycompany.myapp.domain.partner4.SapOitm;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SapOitmTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    public static SapOitm getSapOitmSample1() {
        return new SapOitm()
            .id(1L)
            .itemCode("itemCode1")
            .itemName("itemName1")
            .itmsGrpCod("itmsGrpCod1")
            .uPartNumber("uPartNumber1");
    }

    public static SapOitm getSapOitmSample2() {
        return new SapOitm()
            .id(2L)
            .itemCode("itemCode2")
            .itemName("itemName2")
            .itmsGrpCod("itmsGrpCod2")
            .uPartNumber("uPartNumber2");
    }

    public static SapOitm getSapOitmRandomSampleGenerator() {
        return new SapOitm()
            .id(longCount.incrementAndGet())
            .itemCode(UUID.randomUUID().toString())
            .itemName(UUID.randomUUID().toString())
            .itmsGrpCod(UUID.randomUUID().toString())
            .uPartNumber(UUID.randomUUID().toString());
    }
}
