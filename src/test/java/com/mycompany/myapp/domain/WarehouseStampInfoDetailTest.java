package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.WarehouseStampInfoDetailTestSamples.*;
import static com.mycompany.myapp.domain.WarehouseStampInfoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WarehouseStampInfoDetailTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WarehouseNoteInfoDetail.class);
        WarehouseNoteInfoDetail warehouseStampInfoDetail1 =
            getWarehouseStampInfoDetailSample1();
        WarehouseNoteInfoDetail warehouseStampInfoDetail2 =
            new WarehouseNoteInfoDetail();
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
        WarehouseNoteInfoDetail warehouseStampInfoDetail =
            getWarehouseStampInfoDetailRandomSampleGenerator();
        WarehouseNoteInfo warehouseStampInfoBack =
            getWarehouseStampInfoRandomSampleGenerator();

        warehouseStampInfoDetail.setMaLenhSanXuat(warehouseStampInfoBack);
        assertThat(warehouseStampInfoDetail.getMaLenhSanXuat()).isEqualTo(
            warehouseStampInfoBack
        );

        warehouseStampInfoDetail.maLenhSanXuat(null);
        assertThat(warehouseStampInfoDetail.getMaLenhSanXuat()).isNull();
    }
}
