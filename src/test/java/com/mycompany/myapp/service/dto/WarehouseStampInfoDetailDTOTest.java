package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WarehouseStampInfoDetailDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WarehouseStampInfoDetailDTO.class);
        WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO1 =
            new WarehouseStampInfoDetailDTO();
        warehouseStampInfoDetailDTO1.setId(1L);
        WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO2 =
            new WarehouseStampInfoDetailDTO();
        assertThat(warehouseStampInfoDetailDTO1).isNotEqualTo(
            warehouseStampInfoDetailDTO2
        );
        warehouseStampInfoDetailDTO2.setId(
            warehouseStampInfoDetailDTO1.getId()
        );
        assertThat(warehouseStampInfoDetailDTO1).isEqualTo(
            warehouseStampInfoDetailDTO2
        );
        warehouseStampInfoDetailDTO2.setId(2L);
        assertThat(warehouseStampInfoDetailDTO1).isNotEqualTo(
            warehouseStampInfoDetailDTO2
        );
        warehouseStampInfoDetailDTO1.setId(null);
        assertThat(warehouseStampInfoDetailDTO1).isNotEqualTo(
            warehouseStampInfoDetailDTO2
        );
    }
}
