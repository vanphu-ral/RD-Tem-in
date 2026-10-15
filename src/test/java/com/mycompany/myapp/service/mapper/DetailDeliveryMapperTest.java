package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.DetailDeliveryAsserts.*;
import static com.mycompany.myapp.domain.DetailDeliveryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DetailDeliveryMapperTest {

    private DetailDeliveryMapper detailDeliveryMapper;

    @BeforeEach
    void setUp() {
        detailDeliveryMapper = new DetailDeliveryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDetailDeliverySample1();
        var actual = detailDeliveryMapper.toEntity(
            detailDeliveryMapper.toDto(expected)
        );
        assertDetailDeliveryAllPropertiesEquals(expected, actual);
    }
}
