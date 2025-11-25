package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GenTemConfigDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GenTemConfigDTO.class);
        GenTemConfigDTO genTemConfigDTO1 = new GenTemConfigDTO();
        genTemConfigDTO1.setId(1L);
        GenTemConfigDTO genTemConfigDTO2 = new GenTemConfigDTO();
        assertThat(genTemConfigDTO1).isNotEqualTo(genTemConfigDTO2);
        genTemConfigDTO2.setId(genTemConfigDTO1.getId());
        assertThat(genTemConfigDTO1).isEqualTo(genTemConfigDTO2);
        genTemConfigDTO2.setId(2L);
        assertThat(genTemConfigDTO1).isNotEqualTo(genTemConfigDTO2);
        genTemConfigDTO1.setId(null);
        assertThat(genTemConfigDTO1).isNotEqualTo(genTemConfigDTO2);
    }
}
