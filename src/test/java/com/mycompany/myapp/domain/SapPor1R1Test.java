package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.DeliveryNotificationTestSamples.*;
import static com.mycompany.myapp.domain.SapPor1R1TestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SapPor1R1Test {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(SapPor1R1.class);
    SapPor1R1 sapPor1R11 = getSapPor1R1Sample1();
    SapPor1R1 sapPor1R12 = new SapPor1R1();
    assertThat(sapPor1R11).isNotEqualTo(sapPor1R12);

    sapPor1R12.setId(sapPor1R11.getId());
    assertThat(sapPor1R11).isEqualTo(sapPor1R12);

    sapPor1R12 = getSapPor1R1Sample2();
    assertThat(sapPor1R11).isNotEqualTo(sapPor1R12);
  }

  @Test
  void deliveryNotificationTest() {
    SapPor1R1 sapPor1R1 = getSapPor1R1RandomSampleGenerator();
    DeliveryNotification deliveryNotificationBack =
      getDeliveryNotificationRandomSampleGenerator();

    sapPor1R1.setDeliveryNotification(deliveryNotificationBack);
    assertThat(sapPor1R1.getDeliveryNotification()).isEqualTo(
      deliveryNotificationBack
    );

    sapPor1R1.deliveryNotification(null);
    assertThat(sapPor1R1.getDeliveryNotification()).isNull();
  }
}
