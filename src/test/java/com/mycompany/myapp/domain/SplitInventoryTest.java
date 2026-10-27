package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.SplitInventoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SplitInventoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SplitInventory.class);
        SplitInventory splitInventory1 = getSplitInventorySample1();
        SplitInventory splitInventory2 = new SplitInventory();
        assertThat(splitInventory1).isNotEqualTo(splitInventory2);

        splitInventory2.setId(splitInventory1.getId());
        assertThat(splitInventory1).isEqualTo(splitInventory2);

        splitInventory2 = getSplitInventorySample2();
        assertThat(splitInventory1).isNotEqualTo(splitInventory2);
    }
}
