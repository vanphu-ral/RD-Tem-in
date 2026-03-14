package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.PoDetailTestSamples.*;
import static com.mycompany.myapp.domain.VendorTemDetailTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VendorTemDetailTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VendorTemDetail.class);
        VendorTemDetail vendorTemDetail1 = getVendorTemDetailSample1();
        VendorTemDetail vendorTemDetail2 = new VendorTemDetail();
        assertThat(vendorTemDetail1).isNotEqualTo(vendorTemDetail2);

        vendorTemDetail2.setId(vendorTemDetail1.getId());
        assertThat(vendorTemDetail1).isEqualTo(vendorTemDetail2);

        vendorTemDetail2 = getVendorTemDetailSample2();
        assertThat(vendorTemDetail1).isNotEqualTo(vendorTemDetail2);
    }

    @Test
    void poDetailTest() {
        VendorTemDetail vendorTemDetail =
            getVendorTemDetailRandomSampleGenerator();
        PoDetail poDetailBack = getPoDetailRandomSampleGenerator();

        vendorTemDetail.setPoDetail(poDetailBack);
        assertThat(vendorTemDetail.getPoDetail()).isEqualTo(poDetailBack);

        vendorTemDetail.poDetail(null);
        assertThat(vendorTemDetail.getPoDetail()).isNull();
    }
}
