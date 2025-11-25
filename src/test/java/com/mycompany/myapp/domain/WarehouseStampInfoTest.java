package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.GenTemConfigTestSamples.*;
import static com.mycompany.myapp.domain.SerialBoxPalletMappingTestSamples.*;
import static com.mycompany.myapp.domain.WarehouseStampInfoDetailTestSamples.*;
import static com.mycompany.myapp.domain.WarehouseStampInfoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WarehouseStampInfoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WarehouseStampInfo.class);
        WarehouseStampInfo warehouseStampInfo1 = getWarehouseStampInfoSample1();
        WarehouseStampInfo warehouseStampInfo2 = new WarehouseStampInfo();
        assertThat(warehouseStampInfo1).isNotEqualTo(warehouseStampInfo2);

        warehouseStampInfo2.setId(warehouseStampInfo1.getId());
        assertThat(warehouseStampInfo1).isEqualTo(warehouseStampInfo2);

        warehouseStampInfo2 = getWarehouseStampInfoSample2();
        assertThat(warehouseStampInfo1).isNotEqualTo(warehouseStampInfo2);
    }

    @Test
    void genTemConfigsTest() {
        WarehouseStampInfo warehouseStampInfo =
            getWarehouseStampInfoRandomSampleGenerator();
        GenTemConfig genTemConfigBack = getGenTemConfigRandomSampleGenerator();

        warehouseStampInfo.addGenTemConfigs(genTemConfigBack);
        assertThat(warehouseStampInfo.getGenTemConfigs()).containsOnly(
            genTemConfigBack
        );
        assertThat(genTemConfigBack.getMaLenhSanXuat()).isEqualTo(
            warehouseStampInfo
        );

        warehouseStampInfo.removeGenTemConfigs(genTemConfigBack);
        assertThat(warehouseStampInfo.getGenTemConfigs()).doesNotContain(
            genTemConfigBack
        );
        assertThat(genTemConfigBack.getMaLenhSanXuat()).isNull();

        warehouseStampInfo.genTemConfigs(
            new HashSet<>(Set.of(genTemConfigBack))
        );
        assertThat(warehouseStampInfo.getGenTemConfigs()).containsOnly(
            genTemConfigBack
        );
        assertThat(genTemConfigBack.getMaLenhSanXuat()).isEqualTo(
            warehouseStampInfo
        );

        warehouseStampInfo.setGenTemConfigs(new HashSet<>());
        assertThat(warehouseStampInfo.getGenTemConfigs()).doesNotContain(
            genTemConfigBack
        );
        assertThat(genTemConfigBack.getMaLenhSanXuat()).isNull();
    }

    @Test
    void serialMappingsTest() {
        WarehouseStampInfo warehouseStampInfo =
            getWarehouseStampInfoRandomSampleGenerator();
        SerialBoxPalletMapping serialBoxPalletMappingBack =
            getSerialBoxPalletMappingRandomSampleGenerator();

        warehouseStampInfo.addSerialMappings(serialBoxPalletMappingBack);
        assertThat(warehouseStampInfo.getSerialMappings()).containsOnly(
            serialBoxPalletMappingBack
        );
        assertThat(serialBoxPalletMappingBack.getMaLenhSanXuat()).isEqualTo(
            warehouseStampInfo
        );

        warehouseStampInfo.removeSerialMappings(serialBoxPalletMappingBack);
        assertThat(warehouseStampInfo.getSerialMappings()).doesNotContain(
            serialBoxPalletMappingBack
        );
        assertThat(serialBoxPalletMappingBack.getMaLenhSanXuat()).isNull();

        warehouseStampInfo.serialMappings(
            new HashSet<>(Set.of(serialBoxPalletMappingBack))
        );
        assertThat(warehouseStampInfo.getSerialMappings()).containsOnly(
            serialBoxPalletMappingBack
        );
        assertThat(serialBoxPalletMappingBack.getMaLenhSanXuat()).isEqualTo(
            warehouseStampInfo
        );

        warehouseStampInfo.setSerialMappings(new HashSet<>());
        assertThat(warehouseStampInfo.getSerialMappings()).doesNotContain(
            serialBoxPalletMappingBack
        );
        assertThat(serialBoxPalletMappingBack.getMaLenhSanXuat()).isNull();
    }

    @Test
    void detailsTest() {
        WarehouseStampInfo warehouseStampInfo =
            getWarehouseStampInfoRandomSampleGenerator();
        WarehouseStampInfoDetail warehouseStampInfoDetailBack =
            getWarehouseStampInfoDetailRandomSampleGenerator();

        warehouseStampInfo.addDetails(warehouseStampInfoDetailBack);
        assertThat(warehouseStampInfo.getDetails()).containsOnly(
            warehouseStampInfoDetailBack
        );
        assertThat(warehouseStampInfoDetailBack.getMaLenhSanXuat()).isEqualTo(
            warehouseStampInfo
        );

        warehouseStampInfo.removeDetails(warehouseStampInfoDetailBack);
        assertThat(warehouseStampInfo.getDetails()).doesNotContain(
            warehouseStampInfoDetailBack
        );
        assertThat(warehouseStampInfoDetailBack.getMaLenhSanXuat()).isNull();

        warehouseStampInfo.details(
            new HashSet<>(Set.of(warehouseStampInfoDetailBack))
        );
        assertThat(warehouseStampInfo.getDetails()).containsOnly(
            warehouseStampInfoDetailBack
        );
        assertThat(warehouseStampInfoDetailBack.getMaLenhSanXuat()).isEqualTo(
            warehouseStampInfo
        );

        warehouseStampInfo.setDetails(new HashSet<>());
        assertThat(warehouseStampInfo.getDetails()).doesNotContain(
            warehouseStampInfoDetailBack
        );
        assertThat(warehouseStampInfoDetailBack.getMaLenhSanXuat()).isNull();
    }
}
