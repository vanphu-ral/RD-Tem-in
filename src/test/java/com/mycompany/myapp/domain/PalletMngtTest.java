package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.PalletMngtTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PalletMngtTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(PalletMngt.class);
    PalletMngt palletMngt1 = getPalletMngtSample1();
    PalletMngt palletMngt2 = new PalletMngt();
    assertThat(palletMngt1).isNotEqualTo(palletMngt2);

    palletMngt2.setId(palletMngt1.getId());
    assertThat(palletMngt1).isEqualTo(palletMngt2);

    palletMngt2 = getPalletMngtSample2();
    assertThat(palletMngt1).isNotEqualTo(palletMngt2);
  }
}
