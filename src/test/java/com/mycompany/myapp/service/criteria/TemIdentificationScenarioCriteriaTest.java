package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TemIdentificationScenarioCriteriaTest {

    @Test
    void newTemIdentificationScenarioCriteriaHasAllFiltersNullTest() {
        var temIdentificationScenarioCriteria =
            new TemIdentificationScenarioCriteria();
        assertThat(temIdentificationScenarioCriteria).is(
            criteriaFiltersAre(Objects::isNull)
        );
    }

    @Test
    void temIdentificationScenarioCriteriaFluentMethodsCreatesFiltersTest() {
        var temIdentificationScenarioCriteria =
            new TemIdentificationScenarioCriteria();

        setAllFilters(temIdentificationScenarioCriteria);

        assertThat(temIdentificationScenarioCriteria).is(
            criteriaFiltersAre(Objects::nonNull)
        );
    }

    @Test
    void temIdentificationScenarioCriteriaCopyCreatesNullFilterTest() {
        var temIdentificationScenarioCriteria =
            new TemIdentificationScenarioCriteria();
        var copy = temIdentificationScenarioCriteria.copy();

        assertThat(temIdentificationScenarioCriteria).satisfies(
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
                    temIdentificationScenarioCriteria
                )
        );
    }

    @Test
    void temIdentificationScenarioCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var temIdentificationScenarioCriteria =
            new TemIdentificationScenarioCriteria();
        setAllFilters(temIdentificationScenarioCriteria);

        var copy = temIdentificationScenarioCriteria.copy();

        assertThat(temIdentificationScenarioCriteria).satisfies(
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
                    temIdentificationScenarioCriteria
                )
        );
    }

    @Test
    void toStringVerifier() {
        var temIdentificationScenarioCriteria =
            new TemIdentificationScenarioCriteria();

        assertThat(temIdentificationScenarioCriteria).hasToString(
            "TemIdentificationScenarioCriteria{}"
        );
    }

    private static void setAllFilters(
        TemIdentificationScenarioCriteria temIdentificationScenarioCriteria
    ) {
        temIdentificationScenarioCriteria.id();
        temIdentificationScenarioCriteria.vendorCode();
        temIdentificationScenarioCriteria.vendorName();
        temIdentificationScenarioCriteria.mappingConfig();
        temIdentificationScenarioCriteria.createdBy();
        temIdentificationScenarioCriteria.createdAt();
        temIdentificationScenarioCriteria.updatedBy();
        temIdentificationScenarioCriteria.updatedAt();
        temIdentificationScenarioCriteria.distinct();
    }

    private static Condition<
        TemIdentificationScenarioCriteria
    > criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getVendorCode()) &&
                condition.apply(criteria.getVendorName()) &&
                condition.apply(criteria.getMappingConfig()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedBy()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TemIdentificationScenarioCriteria> copyFiltersAre(
        TemIdentificationScenarioCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(
                    criteria.getVendorCode(),
                    copy.getVendorCode()
                ) &&
                condition.apply(
                    criteria.getVendorName(),
                    copy.getVendorName()
                ) &&
                condition.apply(
                    criteria.getMappingConfig(),
                    copy.getMappingConfig()
                ) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedBy(), copy.getUpdatedBy()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
