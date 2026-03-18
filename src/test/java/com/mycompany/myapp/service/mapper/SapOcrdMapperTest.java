package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.SapOcrdAsserts.*;
import static com.mycompany.myapp.domain.SapOcrdTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SapOcrdMapperTest {

    private SapOcrdMapper sapOcrdMapper;

    @BeforeEach
    void setUp() {
        sapOcrdMapper = new SapOcrdMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSapOcrdSample1();
        var actual = sapOcrdMapper.toEntity(sapOcrdMapper.toDto(expected));
        assertSapOcrdAllPropertiesEquals(expected, actual);
    }
}
