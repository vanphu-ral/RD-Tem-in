package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PalletMngtDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(PalletMngtDTO.class);
    PalletMngtDTO palletMngtDTO1 = new PalletMngtDTO();
    palletMngtDTO1.setId(1L);
    PalletMngtDTO palletMngtDTO2 = new PalletMngtDTO();
    assertThat(palletMngtDTO1).isNotEqualTo(palletMngtDTO2);
    palletMngtDTO2.setId(palletMngtDTO1.getId());
    assertThat(palletMngtDTO1).isEqualTo(palletMngtDTO2);
    palletMngtDTO2.setId(2L);
    assertThat(palletMngtDTO1).isNotEqualTo(palletMngtDTO2);
    palletMngtDTO1.setId(null);
    assertThat(palletMngtDTO1).isNotEqualTo(palletMngtDTO2);
  }
}
