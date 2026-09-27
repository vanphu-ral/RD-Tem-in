package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SapPor1R1DTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(SapPor1R1DTO.class);
    SapPor1R1DTO sapPor1R1DTO1 = new SapPor1R1DTO();
    sapPor1R1DTO1.setId(1L);
    SapPor1R1DTO sapPor1R1DTO2 = new SapPor1R1DTO();
    assertThat(sapPor1R1DTO1).isNotEqualTo(sapPor1R1DTO2);
    sapPor1R1DTO2.setId(sapPor1R1DTO1.getId());
    assertThat(sapPor1R1DTO1).isEqualTo(sapPor1R1DTO2);
    sapPor1R1DTO2.setId(2L);
    assertThat(sapPor1R1DTO1).isNotEqualTo(sapPor1R1DTO2);
    sapPor1R1DTO1.setId(null);
    assertThat(sapPor1R1DTO1).isNotEqualTo(sapPor1R1DTO2);
  }
}
