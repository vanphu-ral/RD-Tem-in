package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SapOcrdTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    public static SapOcrd getSapOcrdSample1() {
        return new SapOcrd()
            .id(1L)
            .cardCode("cardCode1")
            .cardType("cardType1")
            .cardName("cardName1")
            .cardFName("cardFName1")
            .groupCode("groupCode1")
            .currency("currency1")
            .licTradNum("licTradNum1")
            .dddId("dddId1")
            .eMail("eMail1");
    }

    public static SapOcrd getSapOcrdSample2() {
        return new SapOcrd()
            .id(2L)
            .cardCode("cardCode2")
            .cardType("cardType2")
            .cardName("cardName2")
            .cardFName("cardFName2")
            .groupCode("groupCode2")
            .currency("currency2")
            .licTradNum("licTradNum2")
            .dddId("dddId2")
            .eMail("eMail2");
    }

    public static SapOcrd getSapOcrdRandomSampleGenerator() {
        return new SapOcrd()
            .id(longCount.incrementAndGet())
            .cardCode(UUID.randomUUID().toString())
            .cardType(UUID.randomUUID().toString())
            .cardName(UUID.randomUUID().toString())
            .cardFName(UUID.randomUUID().toString())
            .groupCode(UUID.randomUUID().toString())
            .currency(UUID.randomUUID().toString())
            .licTradNum(UUID.randomUUID().toString())
            .dddId(UUID.randomUUID().toString())
            .eMail(UUID.randomUUID().toString());
    }
}
