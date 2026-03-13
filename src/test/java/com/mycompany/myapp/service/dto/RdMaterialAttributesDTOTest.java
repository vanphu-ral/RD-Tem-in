package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RdMaterialAttributesDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RdMaterialAttributesDTO.class);
        RdMaterialAttributesDTO rdMaterialAttributesDTO1 =
            new RdMaterialAttributesDTO();
        rdMaterialAttributesDTO1.setId(1L);
        RdMaterialAttributesDTO rdMaterialAttributesDTO2 =
            new RdMaterialAttributesDTO();
        assertThat(rdMaterialAttributesDTO1).isNotEqualTo(
            rdMaterialAttributesDTO2
        );
        rdMaterialAttributesDTO2.setId(rdMaterialAttributesDTO1.getId());
        assertThat(rdMaterialAttributesDTO1).isEqualTo(
            rdMaterialAttributesDTO2
        );
        rdMaterialAttributesDTO2.setId(2L);
        assertThat(rdMaterialAttributesDTO1).isNotEqualTo(
            rdMaterialAttributesDTO2
        );
        rdMaterialAttributesDTO1.setId(null);
        assertThat(rdMaterialAttributesDTO1).isNotEqualTo(
            rdMaterialAttributesDTO2
        );
    }
}
