package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WarehouseTransferSessionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WarehouseTransferSessionDTO.class);
        WarehouseTransferSessionDTO warehouseTransferSessionDTO1 =
            new WarehouseTransferSessionDTO();
        warehouseTransferSessionDTO1.setId(1L);
        WarehouseTransferSessionDTO warehouseTransferSessionDTO2 =
            new WarehouseTransferSessionDTO();
        assertThat(warehouseTransferSessionDTO1).isNotEqualTo(
            warehouseTransferSessionDTO2
        );
        warehouseTransferSessionDTO2.setId(
            warehouseTransferSessionDTO1.getId()
        );
        assertThat(warehouseTransferSessionDTO1).isEqualTo(
            warehouseTransferSessionDTO2
        );
        warehouseTransferSessionDTO2.setId(2L);
        assertThat(warehouseTransferSessionDTO1).isNotEqualTo(
            warehouseTransferSessionDTO2
        );
        warehouseTransferSessionDTO1.setId(null);
        assertThat(warehouseTransferSessionDTO1).isNotEqualTo(
            warehouseTransferSessionDTO2
        );
    }
}
