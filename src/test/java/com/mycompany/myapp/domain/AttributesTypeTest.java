package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.AttributesTypeTestSamples.*;
import static com.mycompany.myapp.domain.RdMaterialAttributesTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AttributesTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AttributesType.class);
        AttributesType attributesType1 = getAttributesTypeSample1();
        AttributesType attributesType2 = new AttributesType();
        assertThat(attributesType1).isNotEqualTo(attributesType2);

        attributesType2.setId(attributesType1.getId());
        assertThat(attributesType1).isEqualTo(attributesType2);

        attributesType2 = getAttributesTypeSample2();
        assertThat(attributesType1).isNotEqualTo(attributesType2);
    }

    @Test
    void rdMaterialAttributeTest() {
        AttributesType attributesType =
            getAttributesTypeRandomSampleGenerator();
        RdMaterialAttributes rdMaterialAttributesBack =
            getRdMaterialAttributesRandomSampleGenerator();

        attributesType.addRdMaterialAttribute(rdMaterialAttributesBack);
        assertThat(attributesType.getRdMaterialAttributes()).containsOnly(
            rdMaterialAttributesBack
        );
        assertThat(rdMaterialAttributesBack.getAttributesType()).isEqualTo(
            attributesType
        );

        attributesType.removeRdMaterialAttribute(rdMaterialAttributesBack);
        assertThat(attributesType.getRdMaterialAttributes()).doesNotContain(
            rdMaterialAttributesBack
        );
        assertThat(rdMaterialAttributesBack.getAttributesType()).isNull();

        attributesType.rdMaterialAttributes(
            new HashSet<>(Set.of(rdMaterialAttributesBack))
        );
        assertThat(attributesType.getRdMaterialAttributes()).containsOnly(
            rdMaterialAttributesBack
        );
        assertThat(rdMaterialAttributesBack.getAttributesType()).isEqualTo(
            attributesType
        );

        attributesType.setRdMaterialAttributes(new HashSet<>());
        assertThat(attributesType.getRdMaterialAttributes()).doesNotContain(
            rdMaterialAttributesBack
        );
        assertThat(rdMaterialAttributesBack.getAttributesType()).isNull();
    }
}
