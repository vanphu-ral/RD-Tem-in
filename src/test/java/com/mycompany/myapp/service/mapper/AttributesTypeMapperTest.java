package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.AttributesTypeAsserts.*;
import static com.mycompany.myapp.domain.AttributesTypeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AttributesTypeMapperTest {

    private AttributesTypeMapper attributesTypeMapper;

    @BeforeEach
    void setUp() {
        attributesTypeMapper = new AttributesTypeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAttributesTypeSample1();
        var actual = attributesTypeMapper.toEntity(
            attributesTypeMapper.toDto(expected)
        );
        assertAttributesTypeAllPropertiesEquals(expected, actual);
    }
}
