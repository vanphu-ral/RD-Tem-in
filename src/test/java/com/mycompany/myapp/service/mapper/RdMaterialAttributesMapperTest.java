package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.RdMaterialAttributesAsserts.*;
import static com.mycompany.myapp.domain.RdMaterialAttributesTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RdMaterialAttributesMapperTest {

    private RdMaterialAttributesMapper rdMaterialAttributesMapper;

    @BeforeEach
    void setUp() {
        rdMaterialAttributesMapper = new RdMaterialAttributesMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRdMaterialAttributesSample1();
        var actual = rdMaterialAttributesMapper.toEntity(
            rdMaterialAttributesMapper.toDto(expected)
        );
        assertRdMaterialAttributesAllPropertiesEquals(expected, actual);
    }
}
