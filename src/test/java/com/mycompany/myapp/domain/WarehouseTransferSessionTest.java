package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.WarehouseTransferDetailTestSamples.*;
import static com.mycompany.myapp.domain.WarehouseTransferSessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WarehouseTransferSessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WarehouseTransferSession.class);
        WarehouseTransferSession warehouseTransferSession1 =
            getWarehouseTransferSessionSample1();
        WarehouseTransferSession warehouseTransferSession2 =
            new WarehouseTransferSession();
        assertThat(warehouseTransferSession1).isNotEqualTo(
            warehouseTransferSession2
        );

        warehouseTransferSession2.setId(warehouseTransferSession1.getId());
        assertThat(warehouseTransferSession1).isEqualTo(
            warehouseTransferSession2
        );

        warehouseTransferSession2 = getWarehouseTransferSessionSample2();
        assertThat(warehouseTransferSession1).isNotEqualTo(
            warehouseTransferSession2
        );
    }

    @Test
    void detailsTest() throws Exception {
        WarehouseTransferSession warehouseTransferSession =
            getWarehouseTransferSessionRandomSampleGenerator();
        WarehouseTransferDetail warehouseTransferDetailBack =
            getWarehouseTransferDetailRandomSampleGenerator();

        warehouseTransferSession.addDetails(warehouseTransferDetailBack);
        assertThat(warehouseTransferSession.getDetails()).containsOnly(
            warehouseTransferDetailBack
        );
        assertThat(
            warehouseTransferDetailBack.getWarehouseTransferSession()
        ).isEqualTo(warehouseTransferSession);

        warehouseTransferSession.removeDetails(warehouseTransferDetailBack);
        assertThat(warehouseTransferSession.getDetails()).doesNotContain(
            warehouseTransferDetailBack
        );
        assertThat(
            warehouseTransferDetailBack.getWarehouseTransferSession()
        ).isNull();

        warehouseTransferSession.details(
            new HashSet<>(Set.of(warehouseTransferDetailBack))
        );
        assertThat(warehouseTransferSession.getDetails()).containsOnly(
            warehouseTransferDetailBack
        );
        assertThat(
            warehouseTransferDetailBack.getWarehouseTransferSession()
        ).isEqualTo(warehouseTransferSession);

        warehouseTransferSession.setDetails(new HashSet<>());
        assertThat(warehouseTransferSession.getDetails()).doesNotContain(
            warehouseTransferDetailBack
        );
        assertThat(
            warehouseTransferDetailBack.getWarehouseTransferSession()
        ).isNull();
    }
}
