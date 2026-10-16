package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.DeliveryTestSamples.*;
import static com.mycompany.myapp.domain.DetailDeliveryTestSamples.*;
import static com.mycompany.myapp.domain.PalletBoxMappingTestSamples.*;
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
    void deliveryTest() {
        VendorLabelInfo vendorLabelInfo =
            getVendorLabelInfoRandomSampleGenerator();
        Delivery deliveryBack = getDeliveryRandomSampleGenerator();

        vendorLabelInfo.setDelivery(deliveryBack);
        assertThat(vendorLabelInfo.getDelivery()).isEqualTo(deliveryBack);

        vendorLabelInfo.delivery(null);
        assertThat(vendorLabelInfo.getDelivery()).isNull();
    }

    @Test
    void detailDeliveryTest() {
        VendorLabelInfo vendorLabelInfo =
            getVendorLabelInfoRandomSampleGenerator();
        DetailDelivery detailDeliveryBack =
            getDetailDeliveryRandomSampleGenerator();

        vendorLabelInfo.setDetailDelivery(detailDeliveryBack);
        assertThat(vendorLabelInfo.getDetailDelivery()).isEqualTo(
            detailDeliveryBack
        );

        vendorLabelInfo.detailDelivery(null);
        assertThat(vendorLabelInfo.getDetailDelivery()).isNull();
    }

    @Test
    void palletBoxMappingTest() {
        VendorLabelInfo vendorLabelInfo =
            getVendorLabelInfoRandomSampleGenerator();
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
