package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OwtqWarehouseTransferSessionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OwtqWarehouseTransferSessionDTO.class);
        OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO1 =
            new OwtqWarehouseTransferSessionDTO();
        owtqWarehouseTransferSessionDTO1.setId(1L);
        OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO2 =
            new OwtqWarehouseTransferSessionDTO();
        assertThat(owtqWarehouseTransferSessionDTO1).isNotEqualTo(
            owtqWarehouseTransferSessionDTO2
        );
        owtqWarehouseTransferSessionDTO2.setId(
            owtqWarehouseTransferSessionDTO1.getId()
        );
        assertThat(owtqWarehouseTransferSessionDTO1).isEqualTo(
            owtqWarehouseTransferSessionDTO2
        );
        owtqWarehouseTransferSessionDTO2.setId(2L);
        assertThat(owtqWarehouseTransferSessionDTO1).isNotEqualTo(
            owtqWarehouseTransferSessionDTO2
        );
        owtqWarehouseTransferSessionDTO1.setId(null);
        assertThat(owtqWarehouseTransferSessionDTO1).isNotEqualTo(
            owtqWarehouseTransferSessionDTO2
        );
    }
}
