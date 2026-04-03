package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.InboundWMSPalletTestSamples.*;
import static com.mycompany.myapp.domain.InboundWMSSessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InboundWMSPalletTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InboundWMSPallet.class);
        InboundWMSPallet inboundWMSPallet1 = getInboundWMSPalletSample1();
        InboundWMSPallet inboundWMSPallet2 = new InboundWMSPallet();
        assertThat(inboundWMSPallet1).isNotEqualTo(inboundWMSPallet2);

        inboundWMSPallet2.setId(inboundWMSPallet1.getId());
        assertThat(inboundWMSPallet1).isEqualTo(inboundWMSPallet2);

        inboundWMSPallet2 = getInboundWMSPalletSample2();
        assertThat(inboundWMSPallet1).isNotEqualTo(inboundWMSPallet2);
    }

    @Test
    void inboundWMSSessionTest() {
        InboundWMSPallet inboundWMSPallet =
            getInboundWMSPalletRandomSampleGenerator();
        InboundWMSSession inboundWMSSessionBack =
            getInboundWMSSessionRandomSampleGenerator();

        inboundWMSPallet.setInboundWMSSession(inboundWMSSessionBack);
        assertThat(inboundWMSPallet.getInboundWMSSession()).isEqualTo(
            inboundWMSSessionBack
        );

        inboundWMSPallet.inboundWMSSession(null);
        assertThat(inboundWMSPallet.getInboundWMSSession()).isNull();
    }
}
