package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SplitInventoryCriteriaTest {

    @Test
    void newSplitInventoryCriteriaHasAllFiltersNullTest() {
        var splitInventoryCriteria = new SplitInventoryCriteria();
        assertThat(splitInventoryCriteria).is(
            criteriaFiltersAre(filter -> filter == null)
        );
    }

    @Test
    void splitInventoryCriteriaFluentMethodsCreatesFiltersTest() {
        var splitInventoryCriteria = new SplitInventoryCriteria();

        setAllFilters(splitInventoryCriteria);

        assertThat(splitInventoryCriteria).is(
            criteriaFiltersAre(filter -> filter != null)
        );
    }

    @Test
    void splitInventoryCriteriaCopyCreatesNullFilterTest() {
        var splitInventoryCriteria = new SplitInventoryCriteria();
        var copy = splitInventoryCriteria.copy();

        assertThat(splitInventoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) ->
                        (a == null || a instanceof Boolean)
                            ? a == b
                            : (a != b && a.equals(b))
                    )
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria ->
                assertThat(criteria).is(
                    criteriaFiltersAre(filter -> filter == null)
                ),
            criteria -> assertThat(criteria).isEqualTo(splitInventoryCriteria)
        );
    }

    @Test
    void splitInventoryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var splitInventoryCriteria = new SplitInventoryCriteria();
        setAllFilters(splitInventoryCriteria);

        var copy = splitInventoryCriteria.copy();

        assertThat(splitInventoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) ->
                        (a == null || a instanceof Boolean)
                            ? a == b
                            : (a != b && a.equals(b))
                    )
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria ->
                assertThat(criteria).is(
                    criteriaFiltersAre(filter -> filter != null)
                ),
            criteria -> assertThat(criteria).isEqualTo(splitInventoryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var splitInventoryCriteria = new SplitInventoryCriteria();

        assertThat(splitInventoryCriteria).hasToString(
            "SplitInventoryCriteria{}"
        );
    }

    private static void setAllFilters(
        SplitInventoryCriteria splitInventoryCriteria
    ) {
        splitInventoryCriteria.id();
        splitInventoryCriteria.businessSourceId();
        splitInventoryCriteria.businessType();
        splitInventoryCriteria.reelId();
        splitInventoryCriteria.parentMaterialIdentifier();
        splitInventoryCriteria.partNumber();
        splitInventoryCriteria.vendor();
        splitInventoryCriteria.lot();
        splitInventoryCriteria.userData1();
        splitInventoryCriteria.userData2();
        splitInventoryCriteria.userData3();
        splitInventoryCriteria.userData4();
        splitInventoryCriteria.userData5();
        splitInventoryCriteria.initialQuantity();
        splitInventoryCriteria.msdLevel();
        splitInventoryCriteria.msdInitialFloorTime();
        splitInventoryCriteria.msdBagSealDate();
        splitInventoryCriteria.marketUsage();
        splitInventoryCriteria.quantityOverride();
        splitInventoryCriteria.shelfTime();
        splitInventoryCriteria.spMaterialName();
        splitInventoryCriteria.warningLimit();
        splitInventoryCriteria.maximumLimit();
        splitInventoryCriteria.comments();
        splitInventoryCriteria.warmupTime();
        splitInventoryCriteria.storageUnit();
        splitInventoryCriteria.subStorageUnit();
        splitInventoryCriteria.locationOverride();
        splitInventoryCriteria.expirationDate();
        splitInventoryCriteria.manufacturingDate();
        splitInventoryCriteria.partClass();
        splitInventoryCriteria.sapCode();
        splitInventoryCriteria.vendorQrCode();
        splitInventoryCriteria.status();
        splitInventoryCriteria.createdBy();
        splitInventoryCriteria.createdAt();
        splitInventoryCriteria.updatedBy();
        splitInventoryCriteria.updatedAt();
        splitInventoryCriteria.printStatus();
        splitInventoryCriteria.distinct();
    }

    private static Condition<SplitInventoryCriteria> criteriaFiltersAre(
        Function<Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getBusinessSourceId()) &&
                condition.apply(criteria.getBusinessType()) &&
                condition.apply(criteria.getReelId()) &&
                condition.apply(criteria.getParentMaterialIdentifier()) &&
                condition.apply(criteria.getPartNumber()) &&
                condition.apply(criteria.getVendor()) &&
                condition.apply(criteria.getLot()) &&
                condition.apply(criteria.getUserData1()) &&
                condition.apply(criteria.getUserData2()) &&
                condition.apply(criteria.getUserData3()) &&
                condition.apply(criteria.getUserData4()) &&
                condition.apply(criteria.getUserData5()) &&
                condition.apply(criteria.getInitialQuantity()) &&
                condition.apply(criteria.getMsdLevel()) &&
                condition.apply(criteria.getMsdInitialFloorTime()) &&
                condition.apply(criteria.getMsdBagSealDate()) &&
                condition.apply(criteria.getMarketUsage()) &&
                condition.apply(criteria.getQuantityOverride()) &&
                condition.apply(criteria.getShelfTime()) &&
                condition.apply(criteria.getSpMaterialName()) &&
                condition.apply(criteria.getWarningLimit()) &&
                condition.apply(criteria.getMaximumLimit()) &&
                condition.apply(criteria.getComments()) &&
                condition.apply(criteria.getWarmupTime()) &&
                condition.apply(criteria.getStorageUnit()) &&
                condition.apply(criteria.getSubStorageUnit()) &&
                condition.apply(criteria.getLocationOverride()) &&
                condition.apply(criteria.getExpirationDate()) &&
                condition.apply(criteria.getManufacturingDate()) &&
                condition.apply(criteria.getPartClass()) &&
                condition.apply(criteria.getSapCode()) &&
                condition.apply(criteria.getVendorQrCode()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedBy()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getPrintStatus()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SplitInventoryCriteria> copyFiltersAre(
        SplitInventoryCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(
                    criteria.getBusinessSourceId(),
                    copy.getBusinessSourceId()
                ) &&
                condition.apply(
                    criteria.getBusinessType(),
                    copy.getBusinessType()
                ) &&
                condition.apply(criteria.getReelId(), copy.getReelId()) &&
                condition.apply(
                    criteria.getParentMaterialIdentifier(),
                    copy.getParentMaterialIdentifier()
                ) &&
                condition.apply(
                    criteria.getPartNumber(),
                    copy.getPartNumber()
                ) &&
                condition.apply(criteria.getVendor(), copy.getVendor()) &&
                condition.apply(criteria.getLot(), copy.getLot()) &&
                condition.apply(criteria.getUserData1(), copy.getUserData1()) &&
                condition.apply(criteria.getUserData2(), copy.getUserData2()) &&
                condition.apply(criteria.getUserData3(), copy.getUserData3()) &&
                condition.apply(criteria.getUserData4(), copy.getUserData4()) &&
                condition.apply(criteria.getUserData5(), copy.getUserData5()) &&
                condition.apply(
                    criteria.getInitialQuantity(),
                    copy.getInitialQuantity()
                ) &&
                condition.apply(criteria.getMsdLevel(), copy.getMsdLevel()) &&
                condition.apply(
                    criteria.getMsdInitialFloorTime(),
                    copy.getMsdInitialFloorTime()
                ) &&
                condition.apply(
                    criteria.getMsdBagSealDate(),
                    copy.getMsdBagSealDate()
                ) &&
                condition.apply(
                    criteria.getMarketUsage(),
                    copy.getMarketUsage()
                ) &&
                condition.apply(
                    criteria.getQuantityOverride(),
                    copy.getQuantityOverride()
                ) &&
                condition.apply(criteria.getShelfTime(), copy.getShelfTime()) &&
                condition.apply(
                    criteria.getSpMaterialName(),
                    copy.getSpMaterialName()
                ) &&
                condition.apply(
                    criteria.getWarningLimit(),
                    copy.getWarningLimit()
                ) &&
                condition.apply(
                    criteria.getMaximumLimit(),
                    copy.getMaximumLimit()
                ) &&
                condition.apply(criteria.getComments(), copy.getComments()) &&
                condition.apply(
                    criteria.getWarmupTime(),
                    copy.getWarmupTime()
                ) &&
                condition.apply(
                    criteria.getStorageUnit(),
                    copy.getStorageUnit()
                ) &&
                condition.apply(
                    criteria.getSubStorageUnit(),
                    copy.getSubStorageUnit()
                ) &&
                condition.apply(
                    criteria.getLocationOverride(),
                    copy.getLocationOverride()
                ) &&
                condition.apply(
                    criteria.getExpirationDate(),
                    copy.getExpirationDate()
                ) &&
                condition.apply(
                    criteria.getManufacturingDate(),
                    copy.getManufacturingDate()
                ) &&
                condition.apply(criteria.getPartClass(), copy.getPartClass()) &&
                condition.apply(criteria.getSapCode(), copy.getSapCode()) &&
                condition.apply(
                    criteria.getVendorQrCode(),
                    copy.getVendorQrCode()
                ) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedBy(), copy.getUpdatedBy()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(
                    criteria.getPrintStatus(),
                    copy.getPrintStatus()
                ) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
