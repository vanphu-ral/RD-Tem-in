package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.SapPoInfoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SapPoInfoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SapPoInfo.class);
        SapPoInfo sapPoInfo1 = getSapPoInfoSample1();
        SapPoInfo sapPoInfo2 = new SapPoInfo();
        assertThat(sapPoInfo1).isNotEqualTo(sapPoInfo2);

        sapPoInfo2.setId(sapPoInfo1.getId());
        assertThat(sapPoInfo1).isEqualTo(sapPoInfo2);

        sapPoInfo2 = getSapPoInfoSample2();
        assertThat(sapPoInfo1).isNotEqualTo(sapPoInfo2);
    }
}
