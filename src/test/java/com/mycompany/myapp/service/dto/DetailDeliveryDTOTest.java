package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DetailDeliveryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DetailDeliveryDTO.class);
        DetailDeliveryDTO detailDeliveryDTO1 = new DetailDeliveryDTO();
        detailDeliveryDTO1.setLineId(UUID.randomUUID());
        DetailDeliveryDTO detailDeliveryDTO2 = new DetailDeliveryDTO();
        assertThat(detailDeliveryDTO1).isNotEqualTo(detailDeliveryDTO2);
        detailDeliveryDTO2.setLineId(detailDeliveryDTO1.getLineId());
        assertThat(detailDeliveryDTO1).isEqualTo(detailDeliveryDTO2);
        detailDeliveryDTO2.setLineId(UUID.randomUUID());
        assertThat(detailDeliveryDTO1).isNotEqualTo(detailDeliveryDTO2);
        detailDeliveryDTO1.setLineId(null);
        assertThat(detailDeliveryDTO1).isNotEqualTo(detailDeliveryDTO2);
    }
}
