package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ScanPalletDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScanPalletDTO.class);
        ScanPalletDTO scanPalletDTO1 = new ScanPalletDTO();
        scanPalletDTO1.setId(1L);
        ScanPalletDTO scanPalletDTO2 = new ScanPalletDTO();
        assertThat(scanPalletDTO1).isNotEqualTo(scanPalletDTO2);
        scanPalletDTO2.setId(scanPalletDTO1.getId());
        assertThat(scanPalletDTO1).isEqualTo(scanPalletDTO2);
        scanPalletDTO2.setId(2L);
        assertThat(scanPalletDTO1).isNotEqualTo(scanPalletDTO2);
        scanPalletDTO1.setId(null);
        assertThat(scanPalletDTO1).isNotEqualTo(scanPalletDTO2);
    }
}
