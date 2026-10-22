package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.OwtqWarehouseTransferSessionAsserts.*;
import static com.mycompany.myapp.domain.OwtqWarehouseTransferSessionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OwtqWarehouseTransferSessionMapperTest {

    private OwtqWarehouseTransferSessionMapper owtqWarehouseTransferSessionMapper;

    @BeforeEach
    void setUp() {
        owtqWarehouseTransferSessionMapper =
            new OwtqWarehouseTransferSessionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOwtqWarehouseTransferSessionSample1();
        var actual = owtqWarehouseTransferSessionMapper.toEntity(
            owtqWarehouseTransferSessionMapper.toDto(expected)
        );
        assertOwtqWarehouseTransferSessionAllPropertiesEquals(expected, actual);
    }
}
