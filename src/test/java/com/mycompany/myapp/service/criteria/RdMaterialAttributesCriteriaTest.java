package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class RdMaterialAttributesCriteriaTest {

    @Test
    void newRdMaterialAttributesCriteriaHasAllFiltersNullTest() {
        var rdMaterialAttributesCriteria = new RdMaterialAttributesCriteria();
        assertThat(rdMaterialAttributesCriteria).is(
            criteriaFiltersAre(Objects::isNull)
        );
    }

    @Test
    void rdMaterialAttributesCriteriaFluentMethodsCreatesFiltersTest() {
        var rdMaterialAttributesCriteria = new RdMaterialAttributesCriteria();

        setAllFilters(rdMaterialAttributesCriteria);

        assertThat(rdMaterialAttributesCriteria).is(
            criteriaFiltersAre(Objects::nonNull)
        );
    }

    @Test
    void rdMaterialAttributesCriteriaCopyCreatesNullFilterTest() {
        var rdMaterialAttributesCriteria = new RdMaterialAttributesCriteria();
        var copy = rdMaterialAttributesCriteria.copy();

        assertThat(rdMaterialAttributesCriteria).satisfies(
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
                assertThat(criteria).isEqualTo(rdMaterialAttributesCriteria)
        );
    }

    @Test
    void rdMaterialAttributesCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var rdMaterialAttributesCriteria = new RdMaterialAttributesCriteria();
        setAllFilters(rdMaterialAttributesCriteria);

        var copy = rdMaterialAttributesCriteria.copy();

        assertThat(rdMaterialAttributesCriteria).satisfies(
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
                assertThat(criteria).isEqualTo(rdMaterialAttributesCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var rdMaterialAttributesCriteria = new RdMaterialAttributesCriteria();

        assertThat(rdMaterialAttributesCriteria).hasToString(
            "RdMaterialAttributesCriteria{}"
        );
    }

    private static void setAllFilters(
        RdMaterialAttributesCriteria rdMaterialAttributesCriteria
    ) {
        rdMaterialAttributesCriteria.id();
        rdMaterialAttributesCriteria.attributes();
        rdMaterialAttributesCriteria.description();
        rdMaterialAttributesCriteria.attributesTypeId();
        rdMaterialAttributesCriteria.createdBy();
        rdMaterialAttributesCriteria.createdAt();
        rdMaterialAttributesCriteria.updatedBy();
        rdMaterialAttributesCriteria.updatedAt();
        rdMaterialAttributesCriteria.attributesTypeId();
        rdMaterialAttributesCriteria.distinct();
    }

    private static Condition<RdMaterialAttributesCriteria> criteriaFiltersAre(
        Function<Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getAttributes()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getAttributesTypeId()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedBy()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getAttributesTypeId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<RdMaterialAttributesCriteria> copyFiltersAre(
        RdMaterialAttributesCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(
                    criteria.getAttributes(),
                    copy.getAttributes()
                ) &&
                condition.apply(
                    criteria.getDescription(),
                    copy.getDescription()
                ) &&
                condition.apply(
                    criteria.getAttributesTypeId(),
                    copy.getAttributesTypeId()
                ) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedBy(), copy.getUpdatedBy()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(
                    criteria.getAttributesTypeId(),
                    copy.getAttributesTypeId()
                ) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
