package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.OwtqWarehouseTransferSessionTestSamples.*;
import static com.mycompany.myapp.domain.WarehouseTransferSessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OwtqWarehouseTransferSessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OwtqWarehouseTransferSession.class);
        OwtqWarehouseTransferSession owtqWarehouseTransferSession1 =
            getOwtqWarehouseTransferSessionSample1();
        OwtqWarehouseTransferSession owtqWarehouseTransferSession2 =
            new OwtqWarehouseTransferSession();
        assertThat(owtqWarehouseTransferSession1).isNotEqualTo(
            owtqWarehouseTransferSession2
        );

        owtqWarehouseTransferSession2.setId(
            owtqWarehouseTransferSession1.getId()
        );
        assertThat(owtqWarehouseTransferSession1).isEqualTo(
            owtqWarehouseTransferSession2
        );

        owtqWarehouseTransferSession2 =
            getOwtqWarehouseTransferSessionSample2();
        assertThat(owtqWarehouseTransferSession1).isNotEqualTo(
            owtqWarehouseTransferSession2
        );
    }

    @Test
    void warehouseTransferSessionTest() throws Exception {
        OwtqWarehouseTransferSession owtqWarehouseTransferSession =
            getOwtqWarehouseTransferSessionRandomSampleGenerator();
        WarehouseTransferSession warehouseTransferSessionBack =
            getWarehouseTransferSessionRandomSampleGenerator();

        owtqWarehouseTransferSession.setWarehouseTransferSession(
            warehouseTransferSessionBack
        );
        assertThat(
            owtqWarehouseTransferSession.getWarehouseTransferSession()
        ).isEqualTo(warehouseTransferSessionBack);

        owtqWarehouseTransferSession.warehouseTransferSession(null);
        assertThat(
            owtqWarehouseTransferSession.getWarehouseTransferSession()
        ).isNull();
    }
}
