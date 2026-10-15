package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.DeliveryTestSamples.*;
import static com.mycompany.myapp.domain.DetailDeliveryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DetailDeliveryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DetailDelivery.class);
        DetailDelivery detailDelivery1 = getDetailDeliverySample1();
        DetailDelivery detailDelivery2 = new DetailDelivery();
        assertThat(detailDelivery1).isNotEqualTo(detailDelivery2);

        detailDelivery2.setLineId(detailDelivery1.getLineId());
        assertThat(detailDelivery1).isEqualTo(detailDelivery2);

        detailDelivery2 = getDetailDeliverySample2();
        assertThat(detailDelivery1).isNotEqualTo(detailDelivery2);
    }

    @Test
    void deliveryTest() throws Exception {
        DetailDelivery detailDelivery =
            getDetailDeliveryRandomSampleGenerator();
        Delivery deliveryBack = getDeliveryRandomSampleGenerator();

        detailDelivery.setDelivery(deliveryBack);
        assertThat(detailDelivery.getDelivery()).isEqualTo(deliveryBack);

        detailDelivery.delivery(null);
        assertThat(detailDelivery.getDelivery()).isNull();
    }
}
