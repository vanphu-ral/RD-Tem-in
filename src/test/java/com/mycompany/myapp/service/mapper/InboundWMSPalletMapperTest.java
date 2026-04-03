package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.InboundWMSPalletAsserts.*;
import static com.mycompany.myapp.domain.InboundWMSPalletTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InboundWMSPalletMapperTest {

    private InboundWMSPalletMapper inboundWMSPalletMapper;

    @BeforeEach
    void setUp() {
        inboundWMSPalletMapper = new InboundWMSPalletMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInboundWMSPalletSample1();
        var actual = inboundWMSPalletMapper.toEntity(
            inboundWMSPalletMapper.toDto(expected)
        );
        assertInboundWMSPalletAllPropertiesEquals(expected, actual);
    }
}
