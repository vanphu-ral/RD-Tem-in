package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.SerialBoxPalletMappingAsserts.*;
import static com.mycompany.myapp.domain.SerialBoxPalletMappingTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SerialBoxPalletMappingMapperTest {

    private SerialBoxPalletMappingMapper serialBoxPalletMappingMapper;

    @BeforeEach
    void setUp() {
        serialBoxPalletMappingMapper = new SerialBoxPalletMappingMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSerialBoxPalletMappingSample1();
        var actual = serialBoxPalletMappingMapper.toEntity(
            serialBoxPalletMappingMapper.toDto(expected)
        );
        assertSerialBoxPalletMappingAllPropertiesEquals(expected, actual);
    }
}
