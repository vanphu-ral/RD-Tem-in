package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PalletBoxMappingDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(PalletBoxMappingDTO.class);
    PalletBoxMappingDTO palletBoxMappingDTO1 = new PalletBoxMappingDTO();
    palletBoxMappingDTO1.setId(1L);
    PalletBoxMappingDTO palletBoxMappingDTO2 = new PalletBoxMappingDTO();
    assertThat(palletBoxMappingDTO1).isNotEqualTo(palletBoxMappingDTO2);
    palletBoxMappingDTO2.setId(palletBoxMappingDTO1.getId());
    assertThat(palletBoxMappingDTO1).isEqualTo(palletBoxMappingDTO2);
    palletBoxMappingDTO2.setId(2L);
    assertThat(palletBoxMappingDTO1).isNotEqualTo(palletBoxMappingDTO2);
    palletBoxMappingDTO1.setId(null);
    assertThat(palletBoxMappingDTO1).isNotEqualTo(palletBoxMappingDTO2);
  }
}
