package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ScanPalletTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ScanPalletTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScanPallet.class);
        ScanPallet scanPallet1 = getScanPalletSample1();
        ScanPallet scanPallet2 = new ScanPallet();
        assertThat(scanPallet1).isNotEqualTo(scanPallet2);

        scanPallet2.setId(scanPallet1.getId());
        assertThat(scanPallet1).isEqualTo(scanPallet2);

        scanPallet2 = getScanPalletSample2();
        assertThat(scanPallet1).isNotEqualTo(scanPallet2);
    }
}
