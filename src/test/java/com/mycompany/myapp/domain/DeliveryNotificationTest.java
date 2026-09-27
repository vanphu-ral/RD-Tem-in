package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.DeliveryNotificationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DeliveryNotificationTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(DeliveryNotification.class);
    DeliveryNotification deliveryNotification1 =
      getDeliveryNotificationSample1();
    DeliveryNotification deliveryNotification2 = new DeliveryNotification();
    assertThat(deliveryNotification1).isNotEqualTo(deliveryNotification2);

    deliveryNotification2.setId(deliveryNotification1.getId());
    assertThat(deliveryNotification1).isEqualTo(deliveryNotification2);

    deliveryNotification2 = getDeliveryNotificationSample2();
    assertThat(deliveryNotification1).isNotEqualTo(deliveryNotification2);
  }
}
