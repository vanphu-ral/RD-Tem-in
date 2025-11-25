package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.GenTemConfigAsserts.*;
import static com.mycompany.myapp.domain.GenTemConfigTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GenTemConfigMapperTest {

    private GenTemConfigMapper genTemConfigMapper;

    @BeforeEach
    void setUp() {
        genTemConfigMapper = new GenTemConfigMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getGenTemConfigSample1();
        var actual = genTemConfigMapper.toEntity(
            genTemConfigMapper.toDto(expected)
        );
        assertGenTemConfigAllPropertiesEquals(expected, actual);
    }
}
