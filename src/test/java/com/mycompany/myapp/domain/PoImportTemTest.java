package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.PoImportTemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PoImportTemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PoImportTem.class);
        PoImportTem poImportTem1 = getPoImportTemSample1();
        PoImportTem poImportTem2 = new PoImportTem();
        assertThat(poImportTem1).isNotEqualTo(poImportTem2);

        poImportTem2.setId(poImportTem1.getId());
        assertThat(poImportTem1).isEqualTo(poImportTem2);

        poImportTem2 = getPoImportTemSample2();
        assertThat(poImportTem1).isNotEqualTo(poImportTem2);
    }
}
