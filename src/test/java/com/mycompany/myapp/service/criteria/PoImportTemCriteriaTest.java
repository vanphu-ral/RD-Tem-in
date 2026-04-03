package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PoImportTemCriteriaTest {

    @Test
    void newPoImportTemCriteriaHasAllFiltersNullTest() {
        var poImportTemCriteria = new PoImportTemCriteria();
        assertThat(poImportTemCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void poImportTemCriteriaFluentMethodsCreatesFiltersTest() {
        var poImportTemCriteria = new PoImportTemCriteria();

        setAllFilters(poImportTemCriteria);

        assertThat(poImportTemCriteria).is(
            criteriaFiltersAre(Objects::nonNull)
        );
    }

    @Test
    void poImportTemCriteriaCopyCreatesNullFilterTest() {
        var poImportTemCriteria = new PoImportTemCriteria();
        var copy = poImportTemCriteria.copy();

        assertThat(poImportTemCriteria).satisfies(
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
            criteria -> assertThat(criteria).isEqualTo(poImportTemCriteria)
        );
    }

    @Test
    void poImportTemCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var poImportTemCriteria = new PoImportTemCriteria();
        setAllFilters(poImportTemCriteria);

        var copy = poImportTemCriteria.copy();

        assertThat(poImportTemCriteria).satisfies(
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
            criteria -> assertThat(criteria).isEqualTo(poImportTemCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var poImportTemCriteria = new PoImportTemCriteria();

        assertThat(poImportTemCriteria).hasToString("PoImportTemCriteria{}");
    }

    private static void setAllFilters(PoImportTemCriteria poImportTemCriteria) {
        poImportTemCriteria.id();
        poImportTemCriteria.poNumber();
        poImportTemCriteria.vendorCode();
        poImportTemCriteria.vendorName();
        poImportTemCriteria.entryDate();
        poImportTemCriteria.quantityContainer();
        poImportTemCriteria.totalQuantity();
        poImportTemCriteria.status();
        poImportTemCriteria.createdBy();
        poImportTemCriteria.createdAt();
        poImportTemCriteria.updatedBy();
        poImportTemCriteria.updatedAt();
        poImportTemCriteria.deletedBy();
        poImportTemCriteria.deletedAt();
        poImportTemCriteria.distinct();
    }

    private static Condition<PoImportTemCriteria> criteriaFiltersAre(
        Function<Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getPoNumber()) &&
                condition.apply(criteria.getVendorCode()) &&
                condition.apply(criteria.getVendorName()) &&
                condition.apply(criteria.getEntryDate()) &&
                condition.apply(criteria.getQuantityContainer()) &&
                condition.apply(criteria.getTotalQuantity()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedBy()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PoImportTemCriteria> copyFiltersAre(
        PoImportTemCriteria copy,
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
                condition.apply(
                    criteria.getQuantityContainer(),
                    copy.getQuantityContainer()
                ) &&
                condition.apply(
                    criteria.getTotalQuantity(),
                    copy.getTotalQuantity()
                ) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedBy(), copy.getUpdatedBy()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
