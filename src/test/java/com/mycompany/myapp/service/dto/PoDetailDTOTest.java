package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PoDetailDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PoDetailDTO.class);
        PoDetailDTO poDetailDTO1 = new PoDetailDTO();
        poDetailDTO1.setId(1L);
        PoDetailDTO poDetailDTO2 = new PoDetailDTO();
        assertThat(poDetailDTO1).isNotEqualTo(poDetailDTO2);
        poDetailDTO2.setId(poDetailDTO1.getId());
        assertThat(poDetailDTO1).isEqualTo(poDetailDTO2);
        poDetailDTO2.setId(2L);
        assertThat(poDetailDTO1).isNotEqualTo(poDetailDTO2);
        poDetailDTO1.setId(null);
        assertThat(poDetailDTO1).isNotEqualTo(poDetailDTO2);
    }
}
