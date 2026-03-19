package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PoImportTemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PoImportTemDTO.class);
        PoImportTemDTO poImportTemDTO1 = new PoImportTemDTO();
        poImportTemDTO1.setId(1L);
        PoImportTemDTO poImportTemDTO2 = new PoImportTemDTO();
        assertThat(poImportTemDTO1).isNotEqualTo(poImportTemDTO2);
        poImportTemDTO2.setId(poImportTemDTO1.getId());
        assertThat(poImportTemDTO1).isEqualTo(poImportTemDTO2);
        poImportTemDTO2.setId(2L);
        assertThat(poImportTemDTO1).isNotEqualTo(poImportTemDTO2);
        poImportTemDTO1.setId(null);
        assertThat(poImportTemDTO1).isNotEqualTo(poImportTemDTO2);
    }
}
