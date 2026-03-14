package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ImportVendorTemTransactionsTestSamples.*;
import static com.mycompany.myapp.domain.PoDetailTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ImportVendorTemTransactionsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ImportVendorTemTransactions.class);
        ImportVendorTemTransactions importVendorTemTransactions1 =
            getImportVendorTemTransactionsSample1();
        ImportVendorTemTransactions importVendorTemTransactions2 =
            new ImportVendorTemTransactions();
        assertThat(importVendorTemTransactions1).isNotEqualTo(
            importVendorTemTransactions2
        );

        importVendorTemTransactions2.setId(
            importVendorTemTransactions1.getId()
        );
        assertThat(importVendorTemTransactions1).isEqualTo(
            importVendorTemTransactions2
        );

        importVendorTemTransactions2 = getImportVendorTemTransactionsSample2();
        assertThat(importVendorTemTransactions1).isNotEqualTo(
            importVendorTemTransactions2
        );
    }

    @Test
    void poDetailTest() {
        ImportVendorTemTransactions importVendorTemTransactions =
            getImportVendorTemTransactionsRandomSampleGenerator();
        PoDetail poDetailBack = getPoDetailRandomSampleGenerator();

        importVendorTemTransactions.addPoDetail(poDetailBack);
        assertThat(importVendorTemTransactions.getPoDetails()).containsOnly(
            poDetailBack
        );
        assertThat(poDetailBack.getImportVendorTemTransactions()).isEqualTo(
            importVendorTemTransactions
        );

        importVendorTemTransactions.removePoDetail(poDetailBack);
        assertThat(importVendorTemTransactions.getPoDetails()).doesNotContain(
            poDetailBack
        );
        assertThat(poDetailBack.getImportVendorTemTransactions()).isNull();

        importVendorTemTransactions.poDetails(
            new HashSet<>(Set.of(poDetailBack))
        );
        assertThat(importVendorTemTransactions.getPoDetails()).containsOnly(
            poDetailBack
        );
        assertThat(poDetailBack.getImportVendorTemTransactions()).isEqualTo(
            importVendorTemTransactions
        );

        importVendorTemTransactions.setPoDetails(new HashSet<>());
        assertThat(importVendorTemTransactions.getPoDetails()).doesNotContain(
            poDetailBack
        );
        assertThat(poDetailBack.getImportVendorTemTransactions()).isNull();
    }
}
