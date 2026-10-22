package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.WarehouseTransferSessionAsserts.*;
import static com.mycompany.myapp.domain.WarehouseTransferSessionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WarehouseTransferSessionMapperTest {

    private WarehouseTransferSessionMapper warehouseTransferSessionMapper;

    @BeforeEach
    void setUp() {
        warehouseTransferSessionMapper =
            new WarehouseTransferSessionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWarehouseTransferSessionSample1();
        var actual = warehouseTransferSessionMapper.toEntity(
            warehouseTransferSessionMapper.toDto(expected)
        );
        assertWarehouseTransferSessionAllPropertiesEquals(expected, actual);
    }
}
