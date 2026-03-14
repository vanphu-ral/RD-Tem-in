package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.PoDetailAsserts.*;
import static com.mycompany.myapp.domain.PoDetailTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PoDetailMapperTest {

    private PoDetailMapper poDetailMapper;

    @BeforeEach
    void setUp() {
        poDetailMapper = new PoDetailMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPoDetailSample1();
        var actual = poDetailMapper.toEntity(poDetailMapper.toDto(expected));
        assertPoDetailAllPropertiesEquals(expected, actual);
    }
}
