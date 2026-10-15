package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DeliveryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DeliveryDTO.class);
        DeliveryDTO deliveryDTO1 = new DeliveryDTO();
        deliveryDTO1.setDeliveryId(UUID.randomUUID());
        DeliveryDTO deliveryDTO2 = new DeliveryDTO();
        assertThat(deliveryDTO1).isNotEqualTo(deliveryDTO2);
        deliveryDTO2.setDeliveryId(deliveryDTO1.getDeliveryId());
        assertThat(deliveryDTO1).isEqualTo(deliveryDTO2);
        deliveryDTO2.setDeliveryId(UUID.randomUUID());
        assertThat(deliveryDTO1).isNotEqualTo(deliveryDTO2);
        deliveryDTO1.setDeliveryId(null);
        assertThat(deliveryDTO1).isNotEqualTo(deliveryDTO2);
    }
}
