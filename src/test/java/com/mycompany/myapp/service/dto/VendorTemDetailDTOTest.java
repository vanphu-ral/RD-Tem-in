package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VendorTemDetailDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VendorTemDetailDTO.class);
        VendorTemDetailDTO vendorTemDetailDTO1 = new VendorTemDetailDTO();
        vendorTemDetailDTO1.setId(1L);
        VendorTemDetailDTO vendorTemDetailDTO2 = new VendorTemDetailDTO();
        assertThat(vendorTemDetailDTO1).isNotEqualTo(vendorTemDetailDTO2);
        vendorTemDetailDTO2.setId(vendorTemDetailDTO1.getId());
        assertThat(vendorTemDetailDTO1).isEqualTo(vendorTemDetailDTO2);
        vendorTemDetailDTO2.setId(2L);
        assertThat(vendorTemDetailDTO1).isNotEqualTo(vendorTemDetailDTO2);
        vendorTemDetailDTO1.setId(null);
        assertThat(vendorTemDetailDTO1).isNotEqualTo(vendorTemDetailDTO2);
    }
}
