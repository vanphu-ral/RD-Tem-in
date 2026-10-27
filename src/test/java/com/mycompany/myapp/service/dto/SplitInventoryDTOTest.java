package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SplitInventoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SplitInventoryDTO.class);
        SplitInventoryDTO splitInventoryDTO1 = new SplitInventoryDTO();
        splitInventoryDTO1.setId(1L);
        SplitInventoryDTO splitInventoryDTO2 = new SplitInventoryDTO();
        assertThat(splitInventoryDTO1).isNotEqualTo(splitInventoryDTO2);
        splitInventoryDTO2.setId(splitInventoryDTO1.getId());
        assertThat(splitInventoryDTO1).isEqualTo(splitInventoryDTO2);
        splitInventoryDTO2.setId(2L);
        assertThat(splitInventoryDTO1).isNotEqualTo(splitInventoryDTO2);
        splitInventoryDTO1.setId(null);
        assertThat(splitInventoryDTO1).isNotEqualTo(splitInventoryDTO2);
    }
}
