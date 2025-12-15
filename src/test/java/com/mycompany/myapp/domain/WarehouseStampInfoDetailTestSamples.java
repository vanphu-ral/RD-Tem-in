package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class WarehouseStampInfoDetailTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );
    private static final AtomicInteger intCount = new AtomicInteger(
        random.nextInt() + (2 * Short.MAX_VALUE)
    );

    public static WarehouseNoteInfoDetail getWarehouseStampInfoDetailSample1() {
        return new WarehouseNoteInfoDetail()
            .id(1L)
            .reelId("reelId1")
            .partNumber("partNumber1")
            .vendor("vendor1")
            .lot("lot1")
            .userData1("userData11")
            .userData2("userData21")
            .userData3("userData31")
            .userData4("userData41")
            .userData5("userData51")
            .initialQuantity(1)
            .msdLevel("msdLevel1")
            .msdInitialFloorTime("msdInitialFloorTime1")
            .msdBagSealDate("msdBagSealDate1")
            .marketUsage("marketUsage1")
            .quantityOverride(1)
            .shelfTime("shelfTime1")
            .spMaterialName("spMaterialName1")
            .warningLimit("warningLimit1")
            .maximumLimit("maximumLimit1")
            .comments("comments1")
            .warmupTime("warmupTime1")
            .storageUnit("storageUnit1")
            .subStorageUnit("subStorageUnit1")
            .locationOverride("locationOverride1")
            .expirationDate("expirationDate1")
            .manufacturingDate("manufacturingDate1")
            .partClass("partClass1")
            .sapCode("sapCode1")
            .lenhSanXuatId(1L)
            .trangThai("trangThai1")
            .checked(1)
            .tpNk("tpNk1")
            .rank("1")
            .note2("note21");
    }

    public static WarehouseNoteInfoDetail getWarehouseStampInfoDetailSample2() {
        return new WarehouseNoteInfoDetail()
            .id(2L)
            .reelId("reelId2")
            .partNumber("partNumber2")
            .vendor("vendor2")
            .lot("lot2")
            .userData1("userData12")
            .userData2("userData22")
            .userData3("userData32")
            .userData4("userData42")
            .userData5("userData52")
            .initialQuantity(2)
            .msdLevel("msdLevel2")
            .msdInitialFloorTime("msdInitialFloorTime2")
            .msdBagSealDate("msdBagSealDate2")
            .marketUsage("marketUsage2")
            .quantityOverride(2)
            .shelfTime("shelfTime2")
            .spMaterialName("spMaterialName2")
            .warningLimit("warningLimit2")
            .maximumLimit("maximumLimit2")
            .comments("comments2")
            .warmupTime("warmupTime2")
            .storageUnit("storageUnit2")
            .subStorageUnit("subStorageUnit2")
            .locationOverride("locationOverride2")
            .expirationDate("expirationDate2")
            .manufacturingDate("manufacturingDate2")
            .partClass("partClass2")
            .sapCode("sapCode2")
            .lenhSanXuatId(2L)
            .trangThai("trangThai2")
            .checked(2)
            .tpNk("tpNk2")
            .rank("2")
            .note2("note22");
    }

    public static WarehouseNoteInfoDetail getWarehouseStampInfoDetailRandomSampleGenerator() {
        return new WarehouseNoteInfoDetail()
            .id(longCount.incrementAndGet())
            .reelId(UUID.randomUUID().toString())
            .partNumber(UUID.randomUUID().toString())
            .vendor(UUID.randomUUID().toString())
            .lot(UUID.randomUUID().toString())
            .userData1(UUID.randomUUID().toString())
            .userData2(UUID.randomUUID().toString())
            .userData3(UUID.randomUUID().toString())
            .userData4(UUID.randomUUID().toString())
            .userData5(UUID.randomUUID().toString())
            .initialQuantity(intCount.incrementAndGet())
            .msdLevel(UUID.randomUUID().toString())
            .msdInitialFloorTime(UUID.randomUUID().toString())
            .msdBagSealDate(UUID.randomUUID().toString())
            .marketUsage(UUID.randomUUID().toString())
            .quantityOverride(intCount.incrementAndGet())
            .shelfTime(UUID.randomUUID().toString())
            .spMaterialName(UUID.randomUUID().toString())
            .warningLimit(UUID.randomUUID().toString())
            .maximumLimit(UUID.randomUUID().toString())
            .comments(UUID.randomUUID().toString())
            .warmupTime(UUID.randomUUID().toString())
            .storageUnit(UUID.randomUUID().toString())
            .subStorageUnit(UUID.randomUUID().toString())
            .locationOverride(UUID.randomUUID().toString())
            .expirationDate(UUID.randomUUID().toString())
            .manufacturingDate(UUID.randomUUID().toString())
            .partClass(UUID.randomUUID().toString())
            .sapCode(UUID.randomUUID().toString())
            .lenhSanXuatId(longCount.incrementAndGet())
            .trangThai(UUID.randomUUID().toString())
            .checked(intCount.incrementAndGet())
            .tpNk(UUID.randomUUID().toString())
            .rank(String.valueOf(intCount.incrementAndGet()))
            .note2(UUID.randomUUID().toString());
    }
}
