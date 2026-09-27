package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.DeliveryNotificationTestSamples.*;
import static com.mycompany.myapp.domain.PalletBoxMappingTestSamples.*;
import static com.mycompany.myapp.domain.SapPor1R1TestSamples.*;
import static com.mycompany.myapp.domain.VendorLabelInfoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VendorLabelInfoTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(VendorLabelInfo.class);
    VendorLabelInfo vendorLabelInfo1 = getVendorLabelInfoSample1();
    VendorLabelInfo vendorLabelInfo2 = new VendorLabelInfo();
    assertThat(vendorLabelInfo1).isNotEqualTo(vendorLabelInfo2);

    vendorLabelInfo2.setId(vendorLabelInfo1.getId());
    assertThat(vendorLabelInfo1).isEqualTo(vendorLabelInfo2);

    vendorLabelInfo2 = getVendorLabelInfoSample2();
    assertThat(vendorLabelInfo1).isNotEqualTo(vendorLabelInfo2);
  }

  @Test
  void deliveryNotificationTest() {
    VendorLabelInfo vendorLabelInfo = getVendorLabelInfoRandomSampleGenerator();
    DeliveryNotification deliveryNotificationBack =
      getDeliveryNotificationRandomSampleGenerator();

    vendorLabelInfo.setDeliveryNotification(deliveryNotificationBack);
    assertThat(vendorLabelInfo.getDeliveryNotification()).isEqualTo(
      deliveryNotificationBack
    );

    vendorLabelInfo.deliveryNotification(null);
    assertThat(vendorLabelInfo.getDeliveryNotification()).isNull();
  }

  @Test
  void sapPor1Test() {
    VendorLabelInfo vendorLabelInfo = getVendorLabelInfoRandomSampleGenerator();
    SapPor1R1 sapPor1R1Back = getSapPor1R1RandomSampleGenerator();

    vendorLabelInfo.setSapPor1(sapPor1R1Back);
    assertThat(vendorLabelInfo.getSapPor1()).isEqualTo(sapPor1R1Back);

    vendorLabelInfo.sapPor1(null);
    assertThat(vendorLabelInfo.getSapPor1()).isNull();
  }

  @Test
  void palletBoxMappingTest() {
    VendorLabelInfo vendorLabelInfo = getVendorLabelInfoRandomSampleGenerator();
    PalletBoxMapping palletBoxMappingBack =
      getPalletBoxMappingRandomSampleGenerator();

    vendorLabelInfo.setPalletBoxMapping(palletBoxMappingBack);
    assertThat(vendorLabelInfo.getPalletBoxMapping()).isEqualTo(
      palletBoxMappingBack
    );

    vendorLabelInfo.palletBoxMapping(null);
    assertThat(vendorLabelInfo.getPalletBoxMapping()).isNull();
  }
}
