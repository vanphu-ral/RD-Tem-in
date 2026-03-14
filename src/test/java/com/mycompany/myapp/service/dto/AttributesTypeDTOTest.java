package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AttributesTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AttributesTypeDTO.class);
        AttributesTypeDTO attributesTypeDTO1 = new AttributesTypeDTO();
        attributesTypeDTO1.setId(1L);
        AttributesTypeDTO attributesTypeDTO2 = new AttributesTypeDTO();
        assertThat(attributesTypeDTO1).isNotEqualTo(attributesTypeDTO2);
        attributesTypeDTO2.setId(attributesTypeDTO1.getId());
        assertThat(attributesTypeDTO1).isEqualTo(attributesTypeDTO2);
        attributesTypeDTO2.setId(2L);
        assertThat(attributesTypeDTO1).isNotEqualTo(attributesTypeDTO2);
        attributesTypeDTO1.setId(null);
        assertThat(attributesTypeDTO1).isNotEqualTo(attributesTypeDTO2);
    }
}
