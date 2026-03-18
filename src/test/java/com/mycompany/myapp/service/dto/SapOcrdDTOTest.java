package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SapOcrdDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SapOcrdDTO.class);
        SapOcrdDTO sapOcrdDTO1 = new SapOcrdDTO();
        sapOcrdDTO1.setId(1L);
        SapOcrdDTO sapOcrdDTO2 = new SapOcrdDTO();
        assertThat(sapOcrdDTO1).isNotEqualTo(sapOcrdDTO2);
        sapOcrdDTO2.setId(sapOcrdDTO1.getId());
        assertThat(sapOcrdDTO1).isEqualTo(sapOcrdDTO2);
        sapOcrdDTO2.setId(2L);
        assertThat(sapOcrdDTO1).isNotEqualTo(sapOcrdDTO2);
        sapOcrdDTO1.setId(null);
        assertThat(sapOcrdDTO1).isNotEqualTo(sapOcrdDTO2);
    }
}
