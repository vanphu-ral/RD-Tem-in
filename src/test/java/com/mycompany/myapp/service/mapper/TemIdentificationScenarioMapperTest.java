package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TemIdentificationScenarioAsserts.*;
import static com.mycompany.myapp.domain.TemIdentificationScenarioTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TemIdentificationScenarioMapperTest {

    private TemIdentificationScenarioMapper temIdentificationScenarioMapper;

    @BeforeEach
    void setUp() {
        temIdentificationScenarioMapper =
            new TemIdentificationScenarioMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTemIdentificationScenarioSample1();
        var actual = temIdentificationScenarioMapper.toEntity(
            temIdentificationScenarioMapper.toDto(expected)
        );
        assertTemIdentificationScenarioAllPropertiesEquals(expected, actual);
    }
}
