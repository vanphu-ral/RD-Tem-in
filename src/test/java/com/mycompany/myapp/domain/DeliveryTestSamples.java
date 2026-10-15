package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DeliveryTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(
        random.nextInt() + (2 * Short.MAX_VALUE)
    );

    public static Delivery getDeliverySample1() {
        return new Delivery()
            .deliveryId(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .type("type1")
            .noticeNo("noticeNo1")
            .supplierCode("supplierCode1")
            .supplierName("supplierName1")
            .factory("factory1")
            .vehicleNo("vehicleNo1")
            .numberOfPo(1)
            .createdBy("createdBy1")
            .deletedBy("deletedBy1");
    }

    public static Delivery getDeliverySample2() {
        return new Delivery()
            .deliveryId(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .type("type2")
            .noticeNo("noticeNo2")
            .supplierCode("supplierCode2")
            .supplierName("supplierName2")
            .factory("factory2")
            .vehicleNo("vehicleNo2")
            .numberOfPo(2)
            .createdBy("createdBy2")
            .deletedBy("deletedBy2");
    }

    public static Delivery getDeliveryRandomSampleGenerator() {
        return new Delivery()
            .deliveryId(UUID.randomUUID())
            .type(UUID.randomUUID().toString())
            .noticeNo(UUID.randomUUID().toString())
            .supplierCode(UUID.randomUUID().toString())
            .supplierName(UUID.randomUUID().toString())
            .factory(UUID.randomUUID().toString())
            .vehicleNo(UUID.randomUUID().toString())
            .numberOfPo(intCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .deletedBy(UUID.randomUUID().toString());
    }
}
