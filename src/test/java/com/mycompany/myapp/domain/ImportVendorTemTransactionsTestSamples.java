package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ImportVendorTemTransactionsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(
        random.nextInt() + (2 * Integer.MAX_VALUE)
    );

    public static ImportVendorTemTransactions getImportVendorTemTransactionsSample1() {
        return new ImportVendorTemTransactions()
            .id(1L)
            .poNumber("poNumber1")
            .vendorCode("vendorCode1")
            .vendorName("vendorName1")
            .storageUnit("storageUnit1")
            .importTemProfile("importTemProfile1")
            .status("status1")
            .createdBy("createdBy1")
            .updatedBy("updatedBy1")
            .deletedBy("deletedBy1");
    }

    public static ImportVendorTemTransactions getImportVendorTemTransactionsSample2() {
        return new ImportVendorTemTransactions()
            .id(2L)
            .poNumber("poNumber2")
            .vendorCode("vendorCode2")
            .vendorName("vendorName2")
            .storageUnit("storageUnit2")
            .importTemProfile("importTemProfile2")
            .status("status2")
            .createdBy("createdBy2")
            .updatedBy("updatedBy2")
            .deletedBy("deletedBy2");
    }

    public static ImportVendorTemTransactions getImportVendorTemTransactionsRandomSampleGenerator() {
        return new ImportVendorTemTransactions()
            .id(longCount.incrementAndGet())
            .poNumber(UUID.randomUUID().toString())
            .vendorCode(UUID.randomUUID().toString())
            .vendorName(UUID.randomUUID().toString())
            .storageUnit(UUID.randomUUID().toString())
            .importTemProfile(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .updatedBy(UUID.randomUUID().toString())
            .deletedBy(UUID.randomUUID().toString());
    }
}
