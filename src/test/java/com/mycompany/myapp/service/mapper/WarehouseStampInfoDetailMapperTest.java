package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.WarehouseStampInfoDetailAsserts.*;
import static com.mycompany.myapp.domain.WarehouseStampInfoDetailTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WarehouseStampInfoDetailMapperTest {

    private WarehouseStampInfoDetailMapper warehouseStampInfoDetailMapper;

    @BeforeEach
    void setUp() {
        warehouseStampInfoDetailMapper =
            new WarehouseStampInfoDetailMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWarehouseStampInfoDetailSample1();
        var actual = warehouseStampInfoDetailMapper.toEntity(
            warehouseStampInfoDetailMapper.toDto(expected)
        );
        assertWarehouseStampInfoDetailAllPropertiesEquals(expected, actual);
    }
}
