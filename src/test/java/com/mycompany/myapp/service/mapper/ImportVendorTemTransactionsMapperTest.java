package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.ImportVendorTemTransactionsAsserts.*;
import static com.mycompany.myapp.domain.ImportVendorTemTransactionsTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImportVendorTemTransactionsMapperTest {

    private ImportVendorTemTransactionsMapper importVendorTemTransactionsMapper;

    @BeforeEach
    void setUp() {
        importVendorTemTransactionsMapper =
            new ImportVendorTemTransactionsMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getImportVendorTemTransactionsSample1();
        var actual = importVendorTemTransactionsMapper.toEntity(
            importVendorTemTransactionsMapper.toDto(expected)
        );
        assertImportVendorTemTransactionsAllPropertiesEquals(expected, actual);
    }
}
