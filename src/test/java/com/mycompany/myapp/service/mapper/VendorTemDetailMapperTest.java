package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.VendorTemDetailAsserts.*;
import static com.mycompany.myapp.domain.VendorTemDetailTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VendorTemDetailMapperTest {

    private VendorTemDetailMapper vendorTemDetailMapper;

    @BeforeEach
    void setUp() {
        vendorTemDetailMapper = new VendorTemDetailMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getVendorTemDetailSample1();
        var actual = vendorTemDetailMapper.toEntity(
            vendorTemDetailMapper.toDto(expected)
        );
        assertVendorTemDetailAllPropertiesEquals(expected, actual);
    }
}
