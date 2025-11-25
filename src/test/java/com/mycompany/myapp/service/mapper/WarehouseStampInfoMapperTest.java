package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.WarehouseStampInfoAsserts.*;
import static com.mycompany.myapp.domain.WarehouseStampInfoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WarehouseStampInfoMapperTest {

    private WarehouseStampInfoMapper warehouseStampInfoMapper;

    @BeforeEach
    void setUp() {
        warehouseStampInfoMapper = new WarehouseStampInfoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWarehouseStampInfoSample1();
        var actual = warehouseStampInfoMapper.toEntity(
            warehouseStampInfoMapper.toDto(expected)
        );
        assertWarehouseStampInfoAllPropertiesEquals(expected, actual);
    }
}
