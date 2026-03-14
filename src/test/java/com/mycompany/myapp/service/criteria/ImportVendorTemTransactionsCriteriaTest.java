package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ImportVendorTemTransactionsCriteriaTest {

    @Test
    void newImportVendorTemTransactionsCriteriaHasAllFiltersNullTest() {
        var importVendorTemTransactionsCriteria =
            new ImportVendorTemTransactionsCriteria();
        assertThat(importVendorTemTransactionsCriteria).is(
            criteriaFiltersAre(Objects::isNull)
        );
    }

    @Test
    void importVendorTemTransactionsCriteriaFluentMethodsCreatesFiltersTest() {
        var importVendorTemTransactionsCriteria =
            new ImportVendorTemTransactionsCriteria();

        setAllFilters(importVendorTemTransactionsCriteria);

        assertThat(importVendorTemTransactionsCriteria).is(
            criteriaFiltersAre(Objects::nonNull)
        );
    }

    @Test
    void importVendorTemTransactionsCriteriaCopyCreatesNullFilterTest() {
        var importVendorTemTransactionsCriteria =
            new ImportVendorTemTransactionsCriteria();
        var copy = importVendorTemTransactionsCriteria.copy();

        assertThat(importVendorTemTransactionsCriteria).satisfies(
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
            criteria ->
                assertThat(criteria).isEqualTo(
                    importVendorTemTransactionsCriteria
                )
        );
    }

    @Test
    void importVendorTemTransactionsCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var importVendorTemTransactionsCriteria =
            new ImportVendorTemTransactionsCriteria();
        setAllFilters(importVendorTemTransactionsCriteria);

        var copy = importVendorTemTransactionsCriteria.copy();

        assertThat(importVendorTemTransactionsCriteria).satisfies(
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
            criteria ->
                assertThat(criteria).isEqualTo(
                    importVendorTemTransactionsCriteria
                )
        );
    }

    @Test
    void toStringVerifier() {
        var importVendorTemTransactionsCriteria =
            new ImportVendorTemTransactionsCriteria();

        assertThat(importVendorTemTransactionsCriteria).hasToString(
            "ImportVendorTemTransactionsCriteria{}"
        );
    }

    private static void setAllFilters(
        ImportVendorTemTransactionsCriteria importVendorTemTransactionsCriteria
    ) {
        importVendorTemTransactionsCriteria.id();
        importVendorTemTransactionsCriteria.poNumber();
        importVendorTemTransactionsCriteria.vendorCode();
        importVendorTemTransactionsCriteria.vendorName();
        importVendorTemTransactionsCriteria.entryDate();
        importVendorTemTransactionsCriteria.storageUnit();
        importVendorTemTransactionsCriteria.importTemProfile();
        importVendorTemTransactionsCriteria.status();
        importVendorTemTransactionsCriteria.createdBy();
        importVendorTemTransactionsCriteria.createdAt();
        importVendorTemTransactionsCriteria.updatedBy();
        importVendorTemTransactionsCriteria.updatedAt();
        importVendorTemTransactionsCriteria.deletedBy();
        importVendorTemTransactionsCriteria.deletedAt();
        importVendorTemTransactionsCriteria.poDetailId();
        importVendorTemTransactionsCriteria.distinct();
    }

    private static Condition<
        ImportVendorTemTransactionsCriteria
    > criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getPoNumber()) &&
                condition.apply(criteria.getVendorCode()) &&
                condition.apply(criteria.getVendorName()) &&
                condition.apply(criteria.getEntryDate()) &&
                condition.apply(criteria.getStorageUnit()) &&
                condition.apply(criteria.getImportTemProfile()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedBy()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getPoDetailId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<
        ImportVendorTemTransactionsCriteria
    > copyFiltersAre(
        ImportVendorTemTransactionsCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getPoNumber(), copy.getPoNumber()) &&
                condition.apply(
                    criteria.getVendorCode(),
                    copy.getVendorCode()
                ) &&
                condition.apply(
                    criteria.getVendorName(),
                    copy.getVendorName()
                ) &&
                condition.apply(criteria.getEntryDate(), copy.getEntryDate()) &&
                condition.apply(
                    criteria.getStorageUnit(),
                    copy.getStorageUnit()
                ) &&
                condition.apply(
                    criteria.getImportTemProfile(),
                    copy.getImportTemProfile()
                ) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedBy(), copy.getUpdatedBy()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(
                    criteria.getPoDetailId(),
                    copy.getPoDetailId()
                ) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
