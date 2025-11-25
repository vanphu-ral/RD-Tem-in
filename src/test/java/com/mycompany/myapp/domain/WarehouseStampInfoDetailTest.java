package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.WarehouseStampInfoDetailTestSamples.*;
import static com.mycompany.myapp.domain.WarehouseStampInfoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WarehouseStampInfoDetailTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WarehouseStampInfoDetail.class);
        WarehouseStampInfoDetail warehouseStampInfoDetail1 =
            getWarehouseStampInfoDetailSample1();
        WarehouseStampInfoDetail warehouseStampInfoDetail2 =
            new WarehouseStampInfoDetail();
        assertThat(warehouseStampInfoDetail1).isNotEqualTo(
            warehouseStampInfoDetail2
        );

        warehouseStampInfoDetail2.setId(warehouseStampInfoDetail1.getId());
        assertThat(warehouseStampInfoDetail1).isEqualTo(
            warehouseStampInfoDetail2
        );

        warehouseStampInfoDetail2 = getWarehouseStampInfoDetailSample2();
        assertThat(warehouseStampInfoDetail1).isNotEqualTo(
            warehouseStampInfoDetail2
        );
    }

    @Test
    void maLenhSanXuatTest() {
        WarehouseStampInfoDetail warehouseStampInfoDetail =
            getWarehouseStampInfoDetailRandomSampleGenerator();
        WarehouseStampInfo warehouseStampInfoBack =
            getWarehouseStampInfoRandomSampleGenerator();

        warehouseStampInfoDetail.setMaLenhSanXuat(warehouseStampInfoBack);
        assertThat(warehouseStampInfoDetail.getMaLenhSanXuat()).isEqualTo(
            warehouseStampInfoBack
        );

        warehouseStampInfoDetail.maLenhSanXuat(null);
        assertThat(warehouseStampInfoDetail.getMaLenhSanXuat()).isNull();
    }
}
