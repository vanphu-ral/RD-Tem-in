package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AttributesTypeCriteriaTest {

    @Test
    void newAttributesTypeCriteriaHasAllFiltersNullTest() {
        var attributesTypeCriteria = new AttributesTypeCriteria();
        assertThat(attributesTypeCriteria).is(
            criteriaFiltersAre(Objects::isNull)
        );
    }

    @Test
    void attributesTypeCriteriaFluentMethodsCreatesFiltersTest() {
        var attributesTypeCriteria = new AttributesTypeCriteria();

        setAllFilters(attributesTypeCriteria);

        assertThat(attributesTypeCriteria).is(
            criteriaFiltersAre(Objects::nonNull)
        );
    }

    @Test
    void attributesTypeCriteriaCopyCreatesNullFilterTest() {
        var attributesTypeCriteria = new AttributesTypeCriteria();
        var copy = attributesTypeCriteria.copy();

        assertThat(attributesTypeCriteria).satisfies(
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
            criteria -> assertThat(criteria).isEqualTo(attributesTypeCriteria)
        );
    }

    @Test
    void attributesTypeCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var attributesTypeCriteria = new AttributesTypeCriteria();
        setAllFilters(attributesTypeCriteria);

        var copy = attributesTypeCriteria.copy();

        assertThat(attributesTypeCriteria).satisfies(
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
            criteria -> assertThat(criteria).isEqualTo(attributesTypeCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var attributesTypeCriteria = new AttributesTypeCriteria();

        assertThat(attributesTypeCriteria).hasToString(
            "AttributesTypeCriteria{}"
        );
    }

    private static void setAllFilters(
        AttributesTypeCriteria attributesTypeCriteria
    ) {
        attributesTypeCriteria.id();
        attributesTypeCriteria.description();
        attributesTypeCriteria.rdMaterialAttributeId();
        attributesTypeCriteria.distinct();
    }

    private static Condition<AttributesTypeCriteria> criteriaFiltersAre(
        Function<Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getRdMaterialAttributeId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AttributesTypeCriteria> copyFiltersAre(
        AttributesTypeCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(
                    criteria.getDescription(),
                    copy.getDescription()
                ) &&
                condition.apply(
                    criteria.getRdMaterialAttributeId(),
                    copy.getRdMaterialAttributeId()
                ) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
