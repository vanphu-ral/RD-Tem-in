package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.SerialBoxPalletMappingTestSamples.*;
import static com.mycompany.myapp.domain.WarehouseStampInfoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SerialBoxPalletMappingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SerialBoxPalletMapping.class);
        SerialBoxPalletMapping serialBoxPalletMapping1 =
            getSerialBoxPalletMappingSample1();
        SerialBoxPalletMapping serialBoxPalletMapping2 =
            new SerialBoxPalletMapping();
        assertThat(serialBoxPalletMapping1).isNotEqualTo(
            serialBoxPalletMapping2
        );

        serialBoxPalletMapping2.setId(serialBoxPalletMapping1.getId());
        assertThat(serialBoxPalletMapping1).isEqualTo(serialBoxPalletMapping2);

        serialBoxPalletMapping2 = getSerialBoxPalletMappingSample2();
        assertThat(serialBoxPalletMapping1).isNotEqualTo(
            serialBoxPalletMapping2
        );
    }

    @Test
    void maLenhSanXuatTest() {
        SerialBoxPalletMapping serialBoxPalletMapping =
            getSerialBoxPalletMappingRandomSampleGenerator();
        WarehouseStampInfo warehouseStampInfoBack =
            getWarehouseStampInfoRandomSampleGenerator();

        serialBoxPalletMapping.setMaLenhSanXuat(warehouseStampInfoBack);
        assertThat(serialBoxPalletMapping.getMaLenhSanXuat()).isEqualTo(
            warehouseStampInfoBack
        );

        serialBoxPalletMapping.maLenhSanXuat(null);
        assertThat(serialBoxPalletMapping.getMaLenhSanXuat()).isNull();
    }
}
