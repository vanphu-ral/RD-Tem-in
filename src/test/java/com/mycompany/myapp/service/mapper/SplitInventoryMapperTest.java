package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.SplitInventoryAsserts.*;
import static com.mycompany.myapp.domain.SplitInventoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SplitInventoryMapperTest {

    private SplitInventoryMapper splitInventoryMapper;

    @BeforeEach
    void setUp() {
        splitInventoryMapper = new SplitInventoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSplitInventorySample1();
        var actual = splitInventoryMapper.toEntity(
            splitInventoryMapper.toDto(expected)
        );
        assertSplitInventoryAllPropertiesEquals(expected, actual);
    }
}
