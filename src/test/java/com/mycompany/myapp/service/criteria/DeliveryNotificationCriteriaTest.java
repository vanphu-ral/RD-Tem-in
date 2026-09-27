package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DeliveryNotificationCriteriaTest {

  @Test
  void newDeliveryNotificationCriteriaHasAllFiltersNullTest() {
    var deliveryNotificationCriteria = new DeliveryNotificationCriteria();
    assertThat(deliveryNotificationCriteria).is(
      criteriaFiltersAre(Objects::isNull)
    );
  }

  @Test
  void deliveryNotificationCriteriaFluentMethodsCreatesFiltersTest() {
    var deliveryNotificationCriteria = new DeliveryNotificationCriteria();

    setAllFilters(deliveryNotificationCriteria);

    assertThat(deliveryNotificationCriteria).is(
      criteriaFiltersAre(Objects::nonNull)
    );
  }

  @Test
  void deliveryNotificationCriteriaCopyCreatesNullFilterTest() {
    var deliveryNotificationCriteria = new DeliveryNotificationCriteria();
    var copy = deliveryNotificationCriteria.copy();

    assertThat(deliveryNotificationCriteria).satisfies(
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
      criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
      criteria -> assertThat(criteria).isEqualTo(deliveryNotificationCriteria)
    );
  }

  @Test
  void deliveryNotificationCriteriaCopyDuplicatesEveryExistingFilterTest() {
    var deliveryNotificationCriteria = new DeliveryNotificationCriteria();
    setAllFilters(deliveryNotificationCriteria);

    var copy = deliveryNotificationCriteria.copy();

    assertThat(deliveryNotificationCriteria).satisfies(
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
      criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
      criteria -> assertThat(criteria).isEqualTo(deliveryNotificationCriteria)
    );
  }

  @Test
  void toStringVerifier() {
    var deliveryNotificationCriteria = new DeliveryNotificationCriteria();

    assertThat(deliveryNotificationCriteria).hasToString(
      "DeliveryNotificationCriteria{}"
    );
  }

  private static void setAllFilters(
    DeliveryNotificationCriteria deliveryNotificationCriteria
  ) {
    deliveryNotificationCriteria.id();
    deliveryNotificationCriteria.deliveryNotificationCode();
    deliveryNotificationCriteria.invoiceNumber();
    deliveryNotificationCriteria.contractCode();
    deliveryNotificationCriteria.vendorName();
    deliveryNotificationCriteria.contNo();
    deliveryNotificationCriteria.entryDate();
    deliveryNotificationCriteria.numberOfPo();
    deliveryNotificationCriteria.status();
    deliveryNotificationCriteria.createdAt();
    deliveryNotificationCriteria.createdBy();
    deliveryNotificationCriteria.deletedAt();
    deliveryNotificationCriteria.deletedBy();
    deliveryNotificationCriteria.distinct();
  }

  private static Condition<DeliveryNotificationCriteria> criteriaFiltersAre(
    Function<Object, Boolean> condition
  ) {
    return new Condition<>(
      criteria ->
        condition.apply(criteria.getId()) &&
        condition.apply(criteria.getDeliveryNotificationCode()) &&
        condition.apply(criteria.getInvoiceNumber()) &&
        condition.apply(criteria.getContractCode()) &&
        condition.apply(criteria.getVendorName()) &&
        condition.apply(criteria.getContNo()) &&
        condition.apply(criteria.getEntryDate()) &&
        condition.apply(criteria.getNumberOfPo()) &&
        condition.apply(criteria.getStatus()) &&
        condition.apply(criteria.getCreatedAt()) &&
        condition.apply(criteria.getCreatedBy()) &&
        condition.apply(criteria.getDeletedAt()) &&
        condition.apply(criteria.getDeletedBy()) &&
        condition.apply(criteria.getDistinct()),
      "every filter matches"
    );
  }

  private static Condition<DeliveryNotificationCriteria> copyFiltersAre(
    DeliveryNotificationCriteria copy,
    BiFunction<Object, Object, Boolean> condition
  ) {
    return new Condition<>(
      criteria ->
        condition.apply(criteria.getId(), copy.getId()) &&
        condition.apply(
          criteria.getDeliveryNotificationCode(),
          copy.getDeliveryNotificationCode()
        ) &&
        condition.apply(criteria.getInvoiceNumber(), copy.getInvoiceNumber()) &&
        condition.apply(criteria.getContractCode(), copy.getContractCode()) &&
        condition.apply(criteria.getVendorName(), copy.getVendorName()) &&
        condition.apply(criteria.getContNo(), copy.getContNo()) &&
        condition.apply(criteria.getEntryDate(), copy.getEntryDate()) &&
        condition.apply(criteria.getNumberOfPo(), copy.getNumberOfPo()) &&
        condition.apply(criteria.getStatus(), copy.getStatus()) &&
        condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
        condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
        condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
        condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
        condition.apply(criteria.getDistinct(), copy.getDistinct()),
      "every filter matches"
    );
  }
}
