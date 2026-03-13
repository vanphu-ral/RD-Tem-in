package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.TemIdentificationScenarioTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TemIdentificationScenarioTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TemIdentificationScenario.class);
        TemIdentificationScenario temIdentificationScenario1 =
            getTemIdentificationScenarioSample1();
        TemIdentificationScenario temIdentificationScenario2 =
            new TemIdentificationScenario();
        assertThat(temIdentificationScenario1).isNotEqualTo(
            temIdentificationScenario2
        );

        temIdentificationScenario2.setId(temIdentificationScenario1.getId());
        assertThat(temIdentificationScenario1).isEqualTo(
            temIdentificationScenario2
        );

        temIdentificationScenario2 = getTemIdentificationScenarioSample2();
        assertThat(temIdentificationScenario1).isNotEqualTo(
            temIdentificationScenario2
        );
    }
}
