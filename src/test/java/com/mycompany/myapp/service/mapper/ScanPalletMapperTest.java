package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.ScanPalletAsserts.*;
import static com.mycompany.myapp.domain.ScanPalletTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScanPalletMapperTest {

    private ScanPalletMapper scanPalletMapper;

    @BeforeEach
    void setUp() {
        scanPalletMapper = new ScanPalletMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getScanPalletSample1();
        var actual = scanPalletMapper.toEntity(
            scanPalletMapper.toDto(expected)
        );
        assertScanPalletAllPropertiesEquals(expected, actual);
    }
}
