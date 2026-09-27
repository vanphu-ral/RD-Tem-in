package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DeliveryNotificationDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(DeliveryNotificationDTO.class);
    DeliveryNotificationDTO deliveryNotificationDTO1 =
      new DeliveryNotificationDTO();
    deliveryNotificationDTO1.setId(1L);
    DeliveryNotificationDTO deliveryNotificationDTO2 =
      new DeliveryNotificationDTO();
    assertThat(deliveryNotificationDTO1).isNotEqualTo(deliveryNotificationDTO2);
    deliveryNotificationDTO2.setId(deliveryNotificationDTO1.getId());
    assertThat(deliveryNotificationDTO1).isEqualTo(deliveryNotificationDTO2);
    deliveryNotificationDTO2.setId(2L);
    assertThat(deliveryNotificationDTO1).isNotEqualTo(deliveryNotificationDTO2);
    deliveryNotificationDTO1.setId(null);
    assertThat(deliveryNotificationDTO1).isNotEqualTo(deliveryNotificationDTO2);
  }
}
