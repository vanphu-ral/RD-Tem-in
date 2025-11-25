package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WarehouseStampInfoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WarehouseStampInfoDTO.class);
        WarehouseStampInfoDTO warehouseStampInfoDTO1 =
            new WarehouseStampInfoDTO();
        warehouseStampInfoDTO1.setId(1L);
        WarehouseStampInfoDTO warehouseStampInfoDTO2 =
            new WarehouseStampInfoDTO();
        assertThat(warehouseStampInfoDTO1).isNotEqualTo(warehouseStampInfoDTO2);
        warehouseStampInfoDTO2.setId(warehouseStampInfoDTO1.getId());
        assertThat(warehouseStampInfoDTO1).isEqualTo(warehouseStampInfoDTO2);
        warehouseStampInfoDTO2.setId(2L);
        assertThat(warehouseStampInfoDTO1).isNotEqualTo(warehouseStampInfoDTO2);
        warehouseStampInfoDTO1.setId(null);
        assertThat(warehouseStampInfoDTO1).isNotEqualTo(warehouseStampInfoDTO2);
    }
}
