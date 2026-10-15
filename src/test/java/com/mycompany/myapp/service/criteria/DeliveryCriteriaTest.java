package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DeliveryCriteriaTest {

    @Test
    void newDeliveryCriteriaHasAllFiltersNullTest() {
        var deliveryCriteria = new DeliveryCriteria();
        assertThat(deliveryCriteria).is(
            criteriaFiltersAre(filter -> filter == null)
        );
    }

    @Test
    void deliveryCriteriaFluentMethodsCreatesFiltersTest() {
        var deliveryCriteria = new DeliveryCriteria();

        setAllFilters(deliveryCriteria);

        assertThat(deliveryCriteria).is(
            criteriaFiltersAre(filter -> filter != null)
        );
    }

    @Test
    void deliveryCriteriaCopyCreatesNullFilterTest() {
        var deliveryCriteria = new DeliveryCriteria();
        var copy = deliveryCriteria.copy();

        assertThat(deliveryCriteria).satisfies(
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
            criteria -> assertThat(criteria).isEqualTo(deliveryCriteria)
        );
    }

    @Test
    void deliveryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var deliveryCriteria = new DeliveryCriteria();
        setAllFilters(deliveryCriteria);

        var copy = deliveryCriteria.copy();

        assertThat(deliveryCriteria).satisfies(
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
            criteria -> assertThat(criteria).isEqualTo(deliveryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var deliveryCriteria = new DeliveryCriteria();

        assertThat(deliveryCriteria).hasToString("DeliveryCriteria{}");
    }

    private static void setAllFilters(DeliveryCriteria deliveryCriteria) {
        deliveryCriteria.deliveryId();
        deliveryCriteria.type();
        deliveryCriteria.noticeNo();
        deliveryCriteria.noticeDate();
        deliveryCriteria.supplierCode();
        deliveryCriteria.supplierName();
        deliveryCriteria.factory();
        deliveryCriteria.dateArrivalRangDong();
        deliveryCriteria.vehicleNo();
        deliveryCriteria.numberOfPo();
        deliveryCriteria.createdAt();
        deliveryCriteria.createdBy();
        deliveryCriteria.deletedAt();
        deliveryCriteria.deletedBy();
        deliveryCriteria.detailDeliveriesId();
        deliveryCriteria.distinct();
    }

    private static Condition<DeliveryCriteria> criteriaFiltersAre(
        Function<Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getDeliveryId()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getNoticeNo()) &&
                condition.apply(criteria.getNoticeDate()) &&
                condition.apply(criteria.getSupplierCode()) &&
                condition.apply(criteria.getSupplierName()) &&
                condition.apply(criteria.getFactory()) &&
                condition.apply(criteria.getDateArrivalRangDong()) &&
                condition.apply(criteria.getVehicleNo()) &&
                condition.apply(criteria.getNumberOfPo()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getDetailDeliveriesId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DeliveryCriteria> copyFiltersAre(
        DeliveryCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(
                    criteria.getDeliveryId(),
                    copy.getDeliveryId()
                ) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getNoticeNo(), copy.getNoticeNo()) &&
                condition.apply(
                    criteria.getNoticeDate(),
                    copy.getNoticeDate()
                ) &&
                condition.apply(
                    criteria.getSupplierCode(),
                    copy.getSupplierCode()
                ) &&
                condition.apply(
                    criteria.getSupplierName(),
                    copy.getSupplierName()
                ) &&
                condition.apply(criteria.getFactory(), copy.getFactory()) &&
                condition.apply(
                    criteria.getDateArrivalRangDong(),
                    copy.getDateArrivalRangDong()
                ) &&
                condition.apply(criteria.getVehicleNo(), copy.getVehicleNo()) &&
                condition.apply(
                    criteria.getNumberOfPo(),
                    copy.getNumberOfPo()
                ) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(
                    criteria.getDetailDeliveriesId(),
                    copy.getDetailDeliveriesId()
                ) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
