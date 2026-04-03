package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InboundWMSPalletDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InboundWMSPalletDTO.class);
        InboundWMSPalletDTO inboundWMSPalletDTO1 = new InboundWMSPalletDTO();
        inboundWMSPalletDTO1.setId(1L);
        InboundWMSPalletDTO inboundWMSPalletDTO2 = new InboundWMSPalletDTO();
        assertThat(inboundWMSPalletDTO1).isNotEqualTo(inboundWMSPalletDTO2);
        inboundWMSPalletDTO2.setId(inboundWMSPalletDTO1.getId());
        assertThat(inboundWMSPalletDTO1).isEqualTo(inboundWMSPalletDTO2);
        inboundWMSPalletDTO2.setId(2L);
        assertThat(inboundWMSPalletDTO1).isNotEqualTo(inboundWMSPalletDTO2);
        inboundWMSPalletDTO1.setId(null);
        assertThat(inboundWMSPalletDTO1).isNotEqualTo(inboundWMSPalletDTO2);
    }
}
