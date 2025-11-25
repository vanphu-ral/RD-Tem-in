package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class WarehouseStampInfoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );
    private static final AtomicInteger intCount = new AtomicInteger(
        random.nextInt() + (2 * Short.MAX_VALUE)
    );

    public static WarehouseStampInfo getWarehouseStampInfoSample1() {
        return new WarehouseStampInfo()
            .id(1L)
            .maLenhSanXuat("maLenhSanXuat1")
            .sapCode("sapCode1")
            .sapName("sapName1")
            .workOrderCode("workOrderCode1")
            .version("version1")
            .storageCode("storageCode1")
            .totalQuantity(1)
            .createBy("createBy1")
            .trangThai("trangThai1")
            .comment("comment1")
            .groupName("groupName1")
            .comment2("comment21")
            .approverBy("approverBy1")
            .branch("branch1")
            .productType("productType1")
            .deletedBy("deletedBy1");
    }

    public static WarehouseStampInfo getWarehouseStampInfoSample2() {
        return new WarehouseStampInfo()
            .id(2L)
            .maLenhSanXuat("maLenhSanXuat2")
            .sapCode("sapCode2")
            .sapName("sapName2")
            .workOrderCode("workOrderCode2")
            .version("version2")
            .storageCode("storageCode2")
            .totalQuantity(2)
            .createBy("createBy2")
            .trangThai("trangThai2")
            .comment("comment2")
            .groupName("groupName2")
            .comment2("comment22")
            .approverBy("approverBy2")
            .branch("branch2")
            .productType("productType2")
            .deletedBy("deletedBy2");
    }

    public static WarehouseStampInfo getWarehouseStampInfoRandomSampleGenerator() {
        return new WarehouseStampInfo()
            .id(longCount.incrementAndGet())
            .maLenhSanXuat(UUID.randomUUID().toString())
            .sapCode(UUID.randomUUID().toString())
            .sapName(UUID.randomUUID().toString())
            .workOrderCode(UUID.randomUUID().toString())
            .version(UUID.randomUUID().toString())
            .storageCode(UUID.randomUUID().toString())
            .totalQuantity(intCount.incrementAndGet())
            .createBy(UUID.randomUUID().toString())
            .trangThai(UUID.randomUUID().toString())
            .comment(UUID.randomUUID().toString())
            .groupName(UUID.randomUUID().toString())
            .comment2(UUID.randomUUID().toString())
            .approverBy(UUID.randomUUID().toString())
            .branch(UUID.randomUUID().toString())
            .productType(UUID.randomUUID().toString())
            .deletedBy(UUID.randomUUID().toString());
    }
}
