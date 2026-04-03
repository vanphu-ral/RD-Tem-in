package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InboundWMSSessionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InboundWMSSessionDTO.class);
        InboundWMSSessionDTO inboundWMSSessionDTO1 = new InboundWMSSessionDTO();
        inboundWMSSessionDTO1.setId(1L);
        InboundWMSSessionDTO inboundWMSSessionDTO2 = new InboundWMSSessionDTO();
        assertThat(inboundWMSSessionDTO1).isNotEqualTo(inboundWMSSessionDTO2);
        inboundWMSSessionDTO2.setId(inboundWMSSessionDTO1.getId());
        assertThat(inboundWMSSessionDTO1).isEqualTo(inboundWMSSessionDTO2);
        inboundWMSSessionDTO2.setId(2L);
        assertThat(inboundWMSSessionDTO1).isNotEqualTo(inboundWMSSessionDTO2);
        inboundWMSSessionDTO1.setId(null);
        assertThat(inboundWMSSessionDTO1).isNotEqualTo(inboundWMSSessionDTO2);
    }
}
