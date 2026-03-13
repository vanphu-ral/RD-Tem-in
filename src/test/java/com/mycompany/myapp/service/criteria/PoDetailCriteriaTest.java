package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PoDetailCriteriaTest {

    @Test
    void newPoDetailCriteriaHasAllFiltersNullTest() {
        var poDetailCriteria = new PoDetailCriteria();
        assertThat(poDetailCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void poDetailCriteriaFluentMethodsCreatesFiltersTest() {
        var poDetailCriteria = new PoDetailCriteria();

        setAllFilters(poDetailCriteria);

        assertThat(poDetailCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void poDetailCriteriaCopyCreatesNullFilterTest() {
        var poDetailCriteria = new PoDetailCriteria();
        var copy = poDetailCriteria.copy();

        assertThat(poDetailCriteria).satisfies(
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
            criteria -> assertThat(criteria).isEqualTo(poDetailCriteria)
        );
    }

    @Test
    void poDetailCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var poDetailCriteria = new PoDetailCriteria();
        setAllFilters(poDetailCriteria);

        var copy = poDetailCriteria.copy();

        assertThat(poDetailCriteria).satisfies(
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
            criteria -> assertThat(criteria).isEqualTo(poDetailCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var poDetailCriteria = new PoDetailCriteria();

        assertThat(poDetailCriteria).hasToString("PoDetailCriteria{}");
    }

    private static void setAllFilters(PoDetailCriteria poDetailCriteria) {
        poDetailCriteria.id();
        poDetailCriteria.sapCode();
        poDetailCriteria.sapName();
        poDetailCriteria.quantityContainer();
        poDetailCriteria.totalQuantity();
        poDetailCriteria.partNumber();
        poDetailCriteria.vendorTemDetailId();
        poDetailCriteria.importVendorTemTransactionsId();
        poDetailCriteria.distinct();
    }

    private static Condition<PoDetailCriteria> criteriaFiltersAre(
        Function<Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getSapCode()) &&
                condition.apply(criteria.getSapName()) &&
                condition.apply(criteria.getQuantityContainer()) &&
                condition.apply(criteria.getTotalQuantity()) &&
                condition.apply(criteria.getPartNumber()) &&
                condition.apply(criteria.getVendorTemDetailId()) &&
                condition.apply(criteria.getImportVendorTemTransactionsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PoDetailCriteria> copyFiltersAre(
        PoDetailCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getSapCode(), copy.getSapCode()) &&
                condition.apply(criteria.getSapName(), copy.getSapName()) &&
                condition.apply(
                    criteria.getQuantityContainer(),
                    copy.getQuantityContainer()
                ) &&
                condition.apply(
                    criteria.getTotalQuantity(),
                    copy.getTotalQuantity()
                ) &&
                condition.apply(
                    criteria.getPartNumber(),
                    copy.getPartNumber()
                ) &&
                condition.apply(
                    criteria.getVendorTemDetailId(),
                    copy.getVendorTemDetailId()
                ) &&
                condition.apply(
                    criteria.getImportVendorTemTransactionsId(),
                    copy.getImportVendorTemTransactionsId()
                ) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
