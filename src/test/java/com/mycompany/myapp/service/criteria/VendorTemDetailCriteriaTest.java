package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class VendorTemDetailCriteriaTest {

    @Test
    void newVendorTemDetailCriteriaHasAllFiltersNullTest() {
        var vendorTemDetailCriteria = new VendorTemDetailCriteria();
        assertThat(vendorTemDetailCriteria).is(
            criteriaFiltersAre(Objects::isNull)
        );
    }

    @Test
    void vendorTemDetailCriteriaFluentMethodsCreatesFiltersTest() {
        var vendorTemDetailCriteria = new VendorTemDetailCriteria();

        setAllFilters(vendorTemDetailCriteria);

        assertThat(vendorTemDetailCriteria).is(
            criteriaFiltersAre(Objects::nonNull)
        );
    }

    @Test
    void vendorTemDetailCriteriaCopyCreatesNullFilterTest() {
        var vendorTemDetailCriteria = new VendorTemDetailCriteria();
        var copy = vendorTemDetailCriteria.copy();

        assertThat(vendorTemDetailCriteria).satisfies(
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
                assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(vendorTemDetailCriteria)
        );
    }

    @Test
    void vendorTemDetailCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var vendorTemDetailCriteria = new VendorTemDetailCriteria();
        setAllFilters(vendorTemDetailCriteria);

        var copy = vendorTemDetailCriteria.copy();

        assertThat(vendorTemDetailCriteria).satisfies(
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
                assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(vendorTemDetailCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var vendorTemDetailCriteria = new VendorTemDetailCriteria();

        assertThat(vendorTemDetailCriteria).hasToString(
            "VendorTemDetailCriteria{}"
        );
    }

    private static void setAllFilters(
        VendorTemDetailCriteria vendorTemDetailCriteria
    ) {
        vendorTemDetailCriteria.id();
        vendorTemDetailCriteria.reelId();
        vendorTemDetailCriteria.partNumber();
        vendorTemDetailCriteria.vendor();
        vendorTemDetailCriteria.lot();
        vendorTemDetailCriteria.userData1();
        vendorTemDetailCriteria.userData2();
        vendorTemDetailCriteria.userData3();
        vendorTemDetailCriteria.userData4();
        vendorTemDetailCriteria.userData5();
        vendorTemDetailCriteria.initialQuantity();
        vendorTemDetailCriteria.msdLevel();
        vendorTemDetailCriteria.msdInitialFloorTime();
        vendorTemDetailCriteria.msdBagSealDate();
        vendorTemDetailCriteria.marketUsage();
        vendorTemDetailCriteria.quantityOverride();
        vendorTemDetailCriteria.shelfTime();
        vendorTemDetailCriteria.spMaterialName();
        vendorTemDetailCriteria.warningLimit();
        vendorTemDetailCriteria.maximumLimit();
        vendorTemDetailCriteria.comments();
        vendorTemDetailCriteria.warmupTime();
        vendorTemDetailCriteria.storageUnit();
        vendorTemDetailCriteria.subStorageUnit();
        vendorTemDetailCriteria.locationOverride();
        vendorTemDetailCriteria.expirationDate();
        vendorTemDetailCriteria.manufacturingDate();
        vendorTemDetailCriteria.partClass();
        vendorTemDetailCriteria.sapCode();
        vendorTemDetailCriteria.vendorQrCode();
        vendorTemDetailCriteria.status();
        vendorTemDetailCriteria.createdBy();
        vendorTemDetailCriteria.createdAt();
        vendorTemDetailCriteria.updatedBy();
        vendorTemDetailCriteria.updatedAt();
        vendorTemDetailCriteria.poDetailId();
        vendorTemDetailCriteria.distinct();
    }

    private static Condition<VendorTemDetailCriteria> criteriaFiltersAre(
        Function<Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReelId()) &&
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
                condition.apply(criteria.getPoDetailId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<VendorTemDetailCriteria> copyFiltersAre(
        VendorTemDetailCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReelId(), copy.getReelId()) &&
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
                    criteria.getPoDetailId(),
                    copy.getPoDetailId()
                ) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
