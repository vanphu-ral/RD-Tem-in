package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WarehouseTransferDetailDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WarehouseTransferDetailDTO.class);
        WarehouseTransferDetailDTO warehouseTransferDetailDTO1 =
            new WarehouseTransferDetailDTO();
        warehouseTransferDetailDTO1.setId(1L);
        WarehouseTransferDetailDTO warehouseTransferDetailDTO2 =
            new WarehouseTransferDetailDTO();
        assertThat(warehouseTransferDetailDTO1).isNotEqualTo(
            warehouseTransferDetailDTO2
        );
        warehouseTransferDetailDTO2.setId(warehouseTransferDetailDTO1.getId());
        assertThat(warehouseTransferDetailDTO1).isEqualTo(
            warehouseTransferDetailDTO2
        );
        warehouseTransferDetailDTO2.setId(2L);
        assertThat(warehouseTransferDetailDTO1).isNotEqualTo(
            warehouseTransferDetailDTO2
        );
        warehouseTransferDetailDTO1.setId(null);
        assertThat(warehouseTransferDetailDTO1).isNotEqualTo(
            warehouseTransferDetailDTO2
        );
    }
}
