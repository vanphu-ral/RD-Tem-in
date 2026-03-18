package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.SapOcrdTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SapOcrdTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SapOcrd.class);
        SapOcrd sapOcrd1 = getSapOcrdSample1();
        SapOcrd sapOcrd2 = new SapOcrd();
        assertThat(sapOcrd1).isNotEqualTo(sapOcrd2);

        sapOcrd2.setId(sapOcrd1.getId());
        assertThat(sapOcrd1).isEqualTo(sapOcrd2);

        sapOcrd2 = getSapOcrdSample2();
        assertThat(sapOcrd1).isNotEqualTo(sapOcrd2);
    }
}
