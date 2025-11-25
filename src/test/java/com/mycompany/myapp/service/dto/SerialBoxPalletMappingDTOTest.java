package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SerialBoxPalletMappingDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SerialBoxPalletMappingDTO.class);
        SerialBoxPalletMappingDTO serialBoxPalletMappingDTO1 =
            new SerialBoxPalletMappingDTO();
        serialBoxPalletMappingDTO1.setId(1L);
        SerialBoxPalletMappingDTO serialBoxPalletMappingDTO2 =
            new SerialBoxPalletMappingDTO();
        assertThat(serialBoxPalletMappingDTO1).isNotEqualTo(
            serialBoxPalletMappingDTO2
        );
        serialBoxPalletMappingDTO2.setId(serialBoxPalletMappingDTO1.getId());
        assertThat(serialBoxPalletMappingDTO1).isEqualTo(
            serialBoxPalletMappingDTO2
        );
        serialBoxPalletMappingDTO2.setId(2L);
        assertThat(serialBoxPalletMappingDTO1).isNotEqualTo(
            serialBoxPalletMappingDTO2
        );
        serialBoxPalletMappingDTO1.setId(null);
        assertThat(serialBoxPalletMappingDTO1).isNotEqualTo(
            serialBoxPalletMappingDTO2
        );
    }
}
