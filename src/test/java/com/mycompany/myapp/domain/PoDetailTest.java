package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ImportVendorTemTransactionsTestSamples.*;
import static com.mycompany.myapp.domain.PoDetailTestSamples.*;
import static com.mycompany.myapp.domain.VendorTemDetailTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PoDetailTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PoDetail.class);
        PoDetail poDetail1 = getPoDetailSample1();
        PoDetail poDetail2 = new PoDetail();
        assertThat(poDetail1).isNotEqualTo(poDetail2);

        poDetail2.setId(poDetail1.getId());
        assertThat(poDetail1).isEqualTo(poDetail2);

        poDetail2 = getPoDetailSample2();
        assertThat(poDetail1).isNotEqualTo(poDetail2);
    }

    @Test
    void vendorTemDetailTest() {
        PoDetail poDetail = getPoDetailRandomSampleGenerator();
        VendorTemDetail vendorTemDetailBack =
            getVendorTemDetailRandomSampleGenerator();

        poDetail.addVendorTemDetail(vendorTemDetailBack);
        assertThat(poDetail.getVendorTemDetails()).containsOnly(
            vendorTemDetailBack
        );
        assertThat(vendorTemDetailBack.getPoDetail()).isEqualTo(poDetail);

        poDetail.removeVendorTemDetail(vendorTemDetailBack);
        assertThat(poDetail.getVendorTemDetails()).doesNotContain(
            vendorTemDetailBack
        );
        assertThat(vendorTemDetailBack.getPoDetail()).isNull();

        poDetail.vendorTemDetails(new HashSet<>(Set.of(vendorTemDetailBack)));
        assertThat(poDetail.getVendorTemDetails()).containsOnly(
            vendorTemDetailBack
        );
        assertThat(vendorTemDetailBack.getPoDetail()).isEqualTo(poDetail);

        poDetail.setVendorTemDetails(new HashSet<>());
        assertThat(poDetail.getVendorTemDetails()).doesNotContain(
            vendorTemDetailBack
        );
        assertThat(vendorTemDetailBack.getPoDetail()).isNull();
    }

    @Test
    void importVendorTemTransactionsTest() {
        PoDetail poDetail = getPoDetailRandomSampleGenerator();
        ImportVendorTemTransactions importVendorTemTransactionsBack =
            getImportVendorTemTransactionsRandomSampleGenerator();

        poDetail.setImportVendorTemTransactions(
            importVendorTemTransactionsBack
        );
        assertThat(poDetail.getImportVendorTemTransactions()).isEqualTo(
            importVendorTemTransactionsBack
        );

        poDetail.importVendorTemTransactions(null);
        assertThat(poDetail.getImportVendorTemTransactions()).isNull();
    }
}
