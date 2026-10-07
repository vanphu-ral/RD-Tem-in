package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DeliveryNotificationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );
    private static final AtomicInteger intCount = new AtomicInteger(
        random.nextInt() + (2 * Short.MAX_VALUE)
    );

    public static DeliveryNotification getDeliveryNotificationSample1() {
        return new DeliveryNotification()
            .id(1L)
            .deliveryNotificationCode("deliveryNotificationCode1")
            .invoiceNumber("invoiceNumber1")
            .contractCode("contractCode1")
            .vendorName("vendorName1")
            .contNo("contNo1")
            .numberOfPo(1)
            .numberOfItem(1)
            .status("status1")
            .source("source1")
            .createdBy("createBy1")
            .deletedBy("deletedBy1");
    }

    public static DeliveryNotification getDeliveryNotificationSample2() {
        return new DeliveryNotification()
            .id(2L)
            .deliveryNotificationCode("deliveryNotificationCode2")
            .invoiceNumber("invoiceNumber2")
            .contractCode("contractCode2")
            .vendorName("vendorName2")
            .contNo("contNo2")
            .numberOfPo(2)
            .numberOfItem(2)
            .status("status2")
            .source("source2")
            .createdBy("createBy2")
            .deletedBy("deletedBy2");
    }

    public static DeliveryNotification getDeliveryNotificationRandomSampleGenerator() {
        return new DeliveryNotification()
            .id(longCount.incrementAndGet())
            .deliveryNotificationCode(UUID.randomUUID().toString())
            .invoiceNumber(UUID.randomUUID().toString())
            .contractCode(UUID.randomUUID().toString())
            .vendorName(UUID.randomUUID().toString())
            .contNo(UUID.randomUUID().toString())
            .numberOfPo(intCount.incrementAndGet())
            .numberOfItem(intCount.incrementAndGet())
            .status(UUID.randomUUID().toString())
            .source(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .deletedBy(UUID.randomUUID().toString());
    }
}
