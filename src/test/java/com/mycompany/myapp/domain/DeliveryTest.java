package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.DeliveryTestSamples.*;
import static com.mycompany.myapp.domain.DetailDeliveryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DeliveryTest {

    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Delivery.class);
        Delivery delivery1 = getDeliverySample1();
        Delivery delivery2 = new Delivery();
        assertThat(delivery1).isNotEqualTo(delivery2);

        delivery2.setDeliveryId(delivery1.getDeliveryId());
        assertThat(delivery1).isEqualTo(delivery2);

        delivery2 = getDeliverySample2();
        assertThat(delivery1).isNotEqualTo(delivery2);
    }

    @Test
    void detailDeliveriesTest() throws Exception {
        Delivery delivery = getDeliveryRandomSampleGenerator();
        DetailDelivery detailDeliveryBack =
            getDetailDeliveryRandomSampleGenerator();

        delivery.addDetailDeliveries(detailDeliveryBack);
        assertThat(delivery.getDetailDeliveries()).containsOnly(
            detailDeliveryBack
        );
        assertThat(detailDeliveryBack.getDelivery()).isEqualTo(delivery);

        delivery.removeDetailDeliveries(detailDeliveryBack);
        assertThat(delivery.getDetailDeliveries()).doesNotContain(
            detailDeliveryBack
        );
        assertThat(detailDeliveryBack.getDelivery()).isNull();

        delivery.detailDeliveries(new HashSet<>(Set.of(detailDeliveryBack)));
        assertThat(delivery.getDetailDeliveries()).containsOnly(
            detailDeliveryBack
        );
        assertThat(detailDeliveryBack.getDelivery()).isEqualTo(delivery);

        delivery.setDetailDeliveries(new HashSet<>());
        assertThat(delivery.getDetailDeliveries()).doesNotContain(
            detailDeliveryBack
        );
        assertThat(detailDeliveryBack.getDelivery()).isNull();
    }
}
