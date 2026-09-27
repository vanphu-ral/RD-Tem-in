package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.DeliveryNotificationAsserts.*;
import static com.mycompany.myapp.domain.DeliveryNotificationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeliveryNotificationMapperTest {

  private DeliveryNotificationMapper deliveryNotificationMapper;

  @BeforeEach
  void setUp() {
    deliveryNotificationMapper = new DeliveryNotificationMapperImpl();
  }

  @Test
  void shouldConvertToDtoAndBack() {
    var expected = getDeliveryNotificationSample1();
    var actual = deliveryNotificationMapper.toEntity(
      deliveryNotificationMapper.toDto(expected)
    );
    assertDeliveryNotificationAllPropertiesEquals(expected, actual);
  }
}
