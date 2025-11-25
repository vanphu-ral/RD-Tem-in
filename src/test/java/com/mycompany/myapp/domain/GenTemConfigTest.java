package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.GenTemConfigTestSamples.*;
import static com.mycompany.myapp.domain.WarehouseStampInfoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GenTemConfigTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GenTemConfig.class);
        GenTemConfig genTemConfig1 = getGenTemConfigSample1();
        GenTemConfig genTemConfig2 = new GenTemConfig();
        assertThat(genTemConfig1).isNotEqualTo(genTemConfig2);

        genTemConfig2.setId(genTemConfig1.getId());
        assertThat(genTemConfig1).isEqualTo(genTemConfig2);

        genTemConfig2 = getGenTemConfigSample2();
        assertThat(genTemConfig1).isNotEqualTo(genTemConfig2);
    }

    @Test
    void maLenhSanXuatTest() {
        GenTemConfig genTemConfig = getGenTemConfigRandomSampleGenerator();
        WarehouseStampInfo warehouseStampInfoBack =
            getWarehouseStampInfoRandomSampleGenerator();

        genTemConfig.setMaLenhSanXuat(warehouseStampInfoBack);
        assertThat(genTemConfig.getMaLenhSanXuat()).isEqualTo(
            warehouseStampInfoBack
        );

        genTemConfig.maLenhSanXuat(null);
        assertThat(genTemConfig.getMaLenhSanXuat()).isNull();
    }
}
