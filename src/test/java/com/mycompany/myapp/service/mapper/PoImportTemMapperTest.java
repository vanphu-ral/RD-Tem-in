package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.PoImportTemAsserts.*;
import static com.mycompany.myapp.domain.PoImportTemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PoImportTemMapperTest {

    private PoImportTemMapper poImportTemMapper;

    @BeforeEach
    void setUp() {
        poImportTemMapper = new PoImportTemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPoImportTemSample1();
        var actual = poImportTemMapper.toEntity(
            poImportTemMapper.toDto(expected)
        );
        assertPoImportTemAllPropertiesEquals(expected, actual);
    }
}
