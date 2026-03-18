package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.RdMaterialAttributesTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RdMaterialAttributesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RdMaterialAttributes.class);
        RdMaterialAttributes rdMaterialAttributes1 =
            getRdMaterialAttributesSample1();
        RdMaterialAttributes rdMaterialAttributes2 = new RdMaterialAttributes();
        assertThat(rdMaterialAttributes1).isNotEqualTo(rdMaterialAttributes2);

        rdMaterialAttributes2.setId(rdMaterialAttributes1.getId());
        assertThat(rdMaterialAttributes1).isEqualTo(rdMaterialAttributes2);

        rdMaterialAttributes2 = getRdMaterialAttributesSample2();
        assertThat(rdMaterialAttributes1).isNotEqualTo(rdMaterialAttributes2);
    }

    @Test
    void attributesTypeTest() {
        RdMaterialAttributes rdMaterialAttributes =
            getRdMaterialAttributesRandomSampleGenerator();

        String attributesType = "TEST_TYPE";
        rdMaterialAttributes.setAttributesType(attributesType);
        assertThat(rdMaterialAttributes.getAttributesType()).isEqualTo(
            attributesType
        );

        rdMaterialAttributes.attributesType(null);
        assertThat(rdMaterialAttributes.getAttributesType()).isNull();
    }
}
