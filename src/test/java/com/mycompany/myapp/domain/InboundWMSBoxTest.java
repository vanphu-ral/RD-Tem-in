package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.InboundWMSBoxTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InboundWMSBoxTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InboundWMSBox.class);
        InboundWMSBox inboundWMSBox1 = getInboundWMSBoxSample1();
        InboundWMSBox inboundWMSBox2 = new InboundWMSBox();
        assertThat(inboundWMSBox1).isNotEqualTo(inboundWMSBox2);

        inboundWMSBox2.setId(inboundWMSBox1.getId());
        assertThat(inboundWMSBox1).isEqualTo(inboundWMSBox2);

        inboundWMSBox2 = getInboundWMSBoxSample2();
        assertThat(inboundWMSBox1).isNotEqualTo(inboundWMSBox2);
    }
}
