package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TemIdentificationScenarioDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TemIdentificationScenarioDTO.class);
        TemIdentificationScenarioDTO temIdentificationScenarioDTO1 =
            new TemIdentificationScenarioDTO();
        temIdentificationScenarioDTO1.setId(1L);
        TemIdentificationScenarioDTO temIdentificationScenarioDTO2 =
            new TemIdentificationScenarioDTO();
        assertThat(temIdentificationScenarioDTO1).isNotEqualTo(
            temIdentificationScenarioDTO2
        );
        temIdentificationScenarioDTO2.setId(
            temIdentificationScenarioDTO1.getId()
        );
        assertThat(temIdentificationScenarioDTO1).isEqualTo(
            temIdentificationScenarioDTO2
        );
        temIdentificationScenarioDTO2.setId(2L);
        assertThat(temIdentificationScenarioDTO1).isNotEqualTo(
            temIdentificationScenarioDTO2
        );
        temIdentificationScenarioDTO1.setId(null);
        assertThat(temIdentificationScenarioDTO1).isNotEqualTo(
            temIdentificationScenarioDTO2
        );
    }
}
