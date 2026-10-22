package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class WarehouseTransferSessionCriteriaTest {

    @Test
    void newWarehouseTransferSessionCriteriaHasAllFiltersNullTest() {
        var warehouseTransferSessionCriteria =
            new WarehouseTransferSessionCriteria();
        assertThat(warehouseTransferSessionCriteria).is(
            criteriaFiltersAre(filter -> filter == null)
        );
    }

    @Test
    void warehouseTransferSessionCriteriaFluentMethodsCreatesFiltersTest() {
        var warehouseTransferSessionCriteria =
            new WarehouseTransferSessionCriteria();

        setAllFilters(warehouseTransferSessionCriteria);

        assertThat(warehouseTransferSessionCriteria).is(
            criteriaFiltersAre(filter -> filter != null)
        );
    }

    @Test
    void warehouseTransferSessionCriteriaCopyCreatesNullFilterTest() {
        var warehouseTransferSessionCriteria =
            new WarehouseTransferSessionCriteria();
        var copy = warehouseTransferSessionCriteria.copy();

        assertThat(warehouseTransferSessionCriteria).satisfies(
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
            criteria ->
                assertThat(criteria).isEqualTo(warehouseTransferSessionCriteria)
        );
    }

    @Test
    void warehouseTransferSessionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var warehouseTransferSessionCriteria =
            new WarehouseTransferSessionCriteria();
        setAllFilters(warehouseTransferSessionCriteria);

        var copy = warehouseTransferSessionCriteria.copy();

        assertThat(warehouseTransferSessionCriteria).satisfies(
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
            criteria ->
                assertThat(criteria).isEqualTo(warehouseTransferSessionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var warehouseTransferSessionCriteria =
            new WarehouseTransferSessionCriteria();

        assertThat(warehouseTransferSessionCriteria).hasToString(
            "WarehouseTransferSessionCriteria{}"
        );
    }

    private static void setAllFilters(
        WarehouseTransferSessionCriteria warehouseTransferSessionCriteria
    ) {
        warehouseTransferSessionCriteria.id();
        warehouseTransferSessionCriteria.status();
        warehouseTransferSessionCriteria.note();
        warehouseTransferSessionCriteria.createdAt();
        warehouseTransferSessionCriteria.createdBy();
        warehouseTransferSessionCriteria.updatedAt();
        warehouseTransferSessionCriteria.updatedBy();
        warehouseTransferSessionCriteria.deletedAt();
        warehouseTransferSessionCriteria.deletedBy();
        warehouseTransferSessionCriteria.detailsId();
        warehouseTransferSessionCriteria.distinct();
    }

    private static Condition<
        WarehouseTransferSessionCriteria
    > criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getNote()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getUpdatedBy()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getDetailsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<WarehouseTransferSessionCriteria> copyFiltersAre(
        WarehouseTransferSessionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getNote(), copy.getNote()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getUpdatedBy(), copy.getUpdatedBy()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getDetailsId(), copy.getDetailsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
