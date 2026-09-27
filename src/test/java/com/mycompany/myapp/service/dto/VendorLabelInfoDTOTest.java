package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VendorLabelInfoDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(VendorLabelInfoDTO.class);
    VendorLabelInfoDTO vendorLabelInfoDTO1 = new VendorLabelInfoDTO();
    vendorLabelInfoDTO1.setId(1L);
    VendorLabelInfoDTO vendorLabelInfoDTO2 = new VendorLabelInfoDTO();
    assertThat(vendorLabelInfoDTO1).isNotEqualTo(vendorLabelInfoDTO2);
    vendorLabelInfoDTO2.setId(vendorLabelInfoDTO1.getId());
    assertThat(vendorLabelInfoDTO1).isEqualTo(vendorLabelInfoDTO2);
    vendorLabelInfoDTO2.setId(2L);
    assertThat(vendorLabelInfoDTO1).isNotEqualTo(vendorLabelInfoDTO2);
    vendorLabelInfoDTO1.setId(null);
    assertThat(vendorLabelInfoDTO1).isNotEqualTo(vendorLabelInfoDTO2);
  }
}
