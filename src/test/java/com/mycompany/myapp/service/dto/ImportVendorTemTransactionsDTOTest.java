package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ImportVendorTemTransactionsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ImportVendorTemTransactionsDTO.class);
        ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO1 =
            new ImportVendorTemTransactionsDTO();
        importVendorTemTransactionsDTO1.setId(1L);
        ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO2 =
            new ImportVendorTemTransactionsDTO();
        assertThat(importVendorTemTransactionsDTO1).isNotEqualTo(
            importVendorTemTransactionsDTO2
        );
        importVendorTemTransactionsDTO2.setId(
            importVendorTemTransactionsDTO1.getId()
        );
        assertThat(importVendorTemTransactionsDTO1).isEqualTo(
            importVendorTemTransactionsDTO2
        );
        importVendorTemTransactionsDTO2.setId(2L);
        assertThat(importVendorTemTransactionsDTO1).isNotEqualTo(
            importVendorTemTransactionsDTO2
        );
        importVendorTemTransactionsDTO1.setId(null);
        assertThat(importVendorTemTransactionsDTO1).isNotEqualTo(
            importVendorTemTransactionsDTO2
        );
    }
}
