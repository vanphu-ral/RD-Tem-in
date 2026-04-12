package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.SapOitmTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.domain.partner4.SapOitm;
import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SapOitmTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SapOitm.class);
        SapOitm sapOitm1 = getSapOitmSample1();
        SapOitm sapOitm2 = new SapOitm();
        assertThat(sapOitm1).isNotEqualTo(sapOitm2);

        sapOitm2.setId(sapOitm1.getId());
        assertThat(sapOitm1).isEqualTo(sapOitm2);

        sapOitm2 = getSapOitmSample2();
        assertThat(sapOitm1).isNotEqualTo(sapOitm2);
    }
}
