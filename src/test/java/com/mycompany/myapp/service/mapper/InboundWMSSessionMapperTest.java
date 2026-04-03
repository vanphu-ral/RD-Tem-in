package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.InboundWMSSessionAsserts.*;
import static com.mycompany.myapp.domain.InboundWMSSessionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InboundWMSSessionMapperTest {

    private InboundWMSSessionMapper inboundWMSSessionMapper;

    @BeforeEach
    void setUp() {
        inboundWMSSessionMapper = new InboundWMSSessionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInboundWMSSessionSample1();
        var actual = inboundWMSSessionMapper.toEntity(
            inboundWMSSessionMapper.toDto(expected)
        );
        assertInboundWMSSessionAllPropertiesEquals(expected, actual);
    }
}
