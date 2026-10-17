package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VendorLabelInfoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VendorLabelInfoDTO.class);
        VendorLabelInfoDTO vendorLabelInfoDTO1 = new VendorLabelInfoDTO();
        vendorLabelInfoDTO1.setId(
            UUID.fromString("11111111-1111-1111-1111-111111111111")
        );
        VendorLabelInfoDTO vendorLabelInfoDTO2 = new VendorLabelInfoDTO();
        assertThat(vendorLabelInfoDTO1).isNotEqualTo(vendorLabelInfoDTO2);
        vendorLabelInfoDTO2.setId(vendorLabelInfoDTO1.getId());
        assertThat(vendorLabelInfoDTO1).isEqualTo(vendorLabelInfoDTO2);
        vendorLabelInfoDTO2.setId(
            UUID.fromString("22222222-2222-2222-2222-222222222222")
        );
        assertThat(vendorLabelInfoDTO1).isNotEqualTo(vendorLabelInfoDTO2);
        vendorLabelInfoDTO1.setId(null);
        assertThat(vendorLabelInfoDTO1).isNotEqualTo(vendorLabelInfoDTO2);
    }
}
