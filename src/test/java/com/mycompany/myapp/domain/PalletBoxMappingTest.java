package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.PalletBoxMappingTestSamples.*;
import static com.mycompany.myapp.domain.PalletMngtTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PalletBoxMappingTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(PalletBoxMapping.class);
    PalletBoxMapping palletBoxMapping1 = getPalletBoxMappingSample1();
    PalletBoxMapping palletBoxMapping2 = new PalletBoxMapping();
    assertThat(palletBoxMapping1).isNotEqualTo(palletBoxMapping2);

    palletBoxMapping2.setId(palletBoxMapping1.getId());
    assertThat(palletBoxMapping1).isEqualTo(palletBoxMapping2);

    palletBoxMapping2 = getPalletBoxMappingSample2();
    assertThat(palletBoxMapping1).isNotEqualTo(palletBoxMapping2);
  }

  @Test
  void palletMngtTest() {
    PalletBoxMapping palletBoxMapping =
      getPalletBoxMappingRandomSampleGenerator();
    PalletMngt palletMngtBack = getPalletMngtRandomSampleGenerator();

    palletBoxMapping.setPalletMngt(palletMngtBack);
    assertThat(palletBoxMapping.getPalletMngt()).isEqualTo(palletMngtBack);

    palletBoxMapping.palletMngt(null);
    assertThat(palletBoxMapping.getPalletMngt()).isNull();
  }
}
