package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.WarehouseTransferDetailAsserts.*;
import static com.mycompany.myapp.domain.WarehouseTransferDetailTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WarehouseTransferDetailMapperTest {

    private WarehouseTransferDetailMapper warehouseTransferDetailMapper;

    @BeforeEach
    void setUp() {
        warehouseTransferDetailMapper = new WarehouseTransferDetailMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWarehouseTransferDetailSample1();
        var actual = warehouseTransferDetailMapper.toEntity(
            warehouseTransferDetailMapper.toDto(expected)
        );
        assertWarehouseTransferDetailAllPropertiesEquals(expected, actual);
    }
}
