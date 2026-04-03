package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.InboundWMSPalletTestSamples.*;
import static com.mycompany.myapp.domain.InboundWMSSessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InboundWMSSessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InboundWMSSession.class);
        InboundWMSSession inboundWMSSession1 = getInboundWMSSessionSample1();
        InboundWMSSession inboundWMSSession2 = new InboundWMSSession();
        assertThat(inboundWMSSession1).isNotEqualTo(inboundWMSSession2);

        inboundWMSSession2.setId(inboundWMSSession1.getId());
        assertThat(inboundWMSSession1).isEqualTo(inboundWMSSession2);

        inboundWMSSession2 = getInboundWMSSessionSample2();
        assertThat(inboundWMSSession1).isNotEqualTo(inboundWMSSession2);
    }

    @Test
    void inboundWMSPalletTest() {
        InboundWMSSession inboundWMSSession =
            getInboundWMSSessionRandomSampleGenerator();
        InboundWMSPallet inboundWMSPalletBack =
            getInboundWMSPalletRandomSampleGenerator();

        inboundWMSSession.addInboundWMSPallet(inboundWMSPalletBack);
        assertThat(inboundWMSSession.getInboundWMSPallets()).containsOnly(
            inboundWMSPalletBack
        );
        assertThat(inboundWMSPalletBack.getInboundWMSSession()).isEqualTo(
            inboundWMSSession
        );

        inboundWMSSession.removeInboundWMSPallet(inboundWMSPalletBack);
        assertThat(inboundWMSSession.getInboundWMSPallets()).doesNotContain(
            inboundWMSPalletBack
        );
        assertThat(inboundWMSPalletBack.getInboundWMSSession()).isNull();

        inboundWMSSession.inboundWMSPallets(
            new HashSet<>(Set.of(inboundWMSPalletBack))
        );
        assertThat(inboundWMSSession.getInboundWMSPallets()).containsOnly(
            inboundWMSPalletBack
        );
        assertThat(inboundWMSPalletBack.getInboundWMSSession()).isEqualTo(
            inboundWMSSession
        );

        inboundWMSSession.setInboundWMSPallets(new HashSet<>());
        assertThat(inboundWMSSession.getInboundWMSPallets()).doesNotContain(
            inboundWMSPalletBack
        );
        assertThat(inboundWMSPalletBack.getInboundWMSSession()).isNull();
    }
}
