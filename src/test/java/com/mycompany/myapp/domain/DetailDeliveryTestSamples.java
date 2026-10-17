package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DetailDeliveryTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(
        random.nextInt() + (2 * Short.MAX_VALUE)
    );
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    public static DetailDelivery getDetailDeliverySample1() {
        return new DetailDelivery()
            .lineId(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .poSmart("poSmart1")
            .poErp("poErp1")
            .prNumber("prNumber1")
            .contractNo("contractNo1")
            .lineItemStt("lineItemStt1")
            .poLineItem("poLineItem1")
            .itemCode("itemCode1")
            .itemName("itemName1")
            .whsCode("whsCode1")
            .unit("unit1")
            .quantity(1)
            .invoiceSmartNo("invoiceSmartNo1")
            .invoiceNo("invoiceNo1")
            .deliveryTurn(1);
    }

    public static DetailDelivery getDetailDeliverySample2() {
        return new DetailDelivery()
            .lineId(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .poSmart("poSmart2")
            .poErp("poErp2")
            .prNumber("prNumber2")
            .contractNo("contractNo2")
            .lineItemStt("lineItemStt2")
            .poLineItem("poLineItem2")
            .itemCode("itemCode2")
            .itemName("itemName2")
            .whsCode("whsCode2")
            .unit("unit2")
            .quantity(2)
            .invoiceSmartNo("invoiceSmartNo2")
            .invoiceNo("invoiceNo2")
            .deliveryTurn(2);
    }

    public static DetailDelivery getDetailDeliveryRandomSampleGenerator() {
        return new DetailDelivery()
            .lineId(UUID.randomUUID())
            .poSmart(UUID.randomUUID().toString())
            .poErp(UUID.randomUUID().toString())
            .prNumber(UUID.randomUUID().toString())
            .contractNo(UUID.randomUUID().toString())
            .lineItemStt(UUID.randomUUID().toString())
            .poLineItem(UUID.randomUUID().toString())
            .itemCode(UUID.randomUUID().toString())
            .itemName(UUID.randomUUID().toString())
            .whsCode(UUID.randomUUID().toString())
            .unit(UUID.randomUUID().toString())
            .quantity(intCount.incrementAndGet())
            .invoiceSmartNo(UUID.randomUUID().toString())
            .invoiceNo(UUID.randomUUID().toString())
            .deliveryTurn(intCount.incrementAndGet());
    }
}
