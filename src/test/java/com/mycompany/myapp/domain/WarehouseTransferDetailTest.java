package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.WarehouseTransferDetailTestSamples.*;
import static com.mycompany.myapp.domain.WarehouseTransferSessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WarehouseTransferDetailTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WarehouseTransferDetail.class);
        WarehouseTransferDetail warehouseTransferDetail1 =
            getWarehouseTransferDetailSample1();
        WarehouseTransferDetail warehouseTransferDetail2 =
            new WarehouseTransferDetail();
        assertThat(warehouseTransferDetail1).isNotEqualTo(
            warehouseTransferDetail2
        );

        warehouseTransferDetail2.setId(warehouseTransferDetail1.getId());
        assertThat(warehouseTransferDetail1).isEqualTo(
            warehouseTransferDetail2
        );

        warehouseTransferDetail2 = getWarehouseTransferDetailSample2();
        assertThat(warehouseTransferDetail1).isNotEqualTo(
            warehouseTransferDetail2
        );
    }

    @Test
    void warehouseTransferSessionTest() throws Exception {
        WarehouseTransferDetail warehouseTransferDetail =
            getWarehouseTransferDetailRandomSampleGenerator();
        WarehouseTransferSession warehouseTransferSessionBack =
            getWarehouseTransferSessionRandomSampleGenerator();

        warehouseTransferDetail.setWarehouseTransferSession(
            warehouseTransferSessionBack
        );
        assertThat(
            warehouseTransferDetail.getWarehouseTransferSession()
        ).isEqualTo(warehouseTransferSessionBack);

        warehouseTransferDetail.warehouseTransferSession(null);
        assertThat(
            warehouseTransferDetail.getWarehouseTransferSession()
        ).isNull();
    }
}
