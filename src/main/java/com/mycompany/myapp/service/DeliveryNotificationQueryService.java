package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.DeliveryNotification;
import com.mycompany.myapp.repository.DeliveryNotificationRepository;
import com.mycompany.myapp.service.criteria.DeliveryNotificationCriteria;
import com.mycompany.myapp.service.dto.DeliveryNotificationDTO;
import com.mycompany.myapp.service.mapper.DeliveryNotificationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link DeliveryNotification} entities in the database.
 * The main input is a {@link DeliveryNotificationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DeliveryNotificationDTO} which fulfills the criteria.
 */
@Service
@Transactional(value = "partner5TransactionManager", readOnly = true)
public class DeliveryNotificationQueryService
  extends QueryService<DeliveryNotification> {

  private static final Logger LOG = LoggerFactory.getLogger(
    DeliveryNotificationQueryService.class
  );

  private final DeliveryNotificationRepository deliveryNotificationRepository;

  private final DeliveryNotificationMapper deliveryNotificationMapper;

  public DeliveryNotificationQueryService(
    DeliveryNotificationRepository deliveryNotificationRepository,
    DeliveryNotificationMapper deliveryNotificationMapper
  ) {
    this.deliveryNotificationRepository = deliveryNotificationRepository;
    this.deliveryNotificationMapper = deliveryNotificationMapper;
  }

  /**
   * Return a {@link Page} of {@link DeliveryNotificationDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(value = "partner5TransactionManager", readOnly = true)
  public Page<DeliveryNotificationDTO> findByCriteria(
    DeliveryNotificationCriteria criteria,
    Pageable page
  ) {
    LOG.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<DeliveryNotification> specification =
      createSpecification(criteria);
    return deliveryNotificationRepository
      .findAll(specification, page)
      .map(deliveryNotificationMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(value = "partner5TransactionManager", readOnly = true)
  public long countByCriteria(DeliveryNotificationCriteria criteria) {
    LOG.debug("count by criteria : {}", criteria);
    final Specification<DeliveryNotification> specification =
      createSpecification(criteria);
    return deliveryNotificationRepository.count(specification);
  }

  /**
   * Function to convert {@link DeliveryNotificationCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<DeliveryNotification> createSpecification(
    DeliveryNotificationCriteria criteria
  ) {
    Specification<DeliveryNotification> specification = Specification.where(
      null
    );
     if (criteria != null) {
      if (Boolean.TRUE.equals(criteria.getDistinct())) {
        specification = specification.and(distinct(criteria.getDistinct()));
      }
      specification = specification.and(
        buildRangeSpecification(criteria.id(), DeliveryNotification_.id)
      );
      specification = specification.and(
        buildStringSpecification(
          criteria.deliveryNotificationCode(),
          DeliveryNotification_.deliveryNotificationCode
        )
      );

      specification = specification.and(
        buildStringSpecification(
          criteria.invoiceNumber(),
          DeliveryNotification_.invoiceNumber
        )
      );
      specification = specification.and(
        buildStringSpecification(
          criteria.contractCode(),
          DeliveryNotification_.contractCode
        )
      );
      specification = specification.and(
        buildStringSpecification(
          criteria.vendorName(),
          DeliveryNotification_.vendorName
        )
      );
      specification = specification.and(
        buildStringSpecification(
          criteria.contNo(),
          DeliveryNotification_.contNo
        )
      );
      specification = specification.and(
        buildRangeSpecification(
          criteria.entryDate(),
          DeliveryNotification_.entryDate
        )
      );
      specification = specification.and(
        buildRangeSpecification(
          criteria.numberOfPo(),
          DeliveryNotification_.numberOfPo
        )
      );
      specification = specification.and(
        buildStringSpecification(criteria.status(), DeliveryNotification_.status)
      );
      specification = specification.and(
        buildRangeSpecification(
          criteria.createdAt(),
          DeliveryNotification_.createdAt
        )
      );
      specification = specification.and(
        buildStringSpecification(
          criteria.createdBy(),
          DeliveryNotification_.createdBy
        )
      );
      specification = specification.and(
        buildRangeSpecification(
          criteria.deletedAt(),
          DeliveryNotification_.deletedAt
        )
      );
      specification = specification.and(
        buildStringSpecification(
          criteria.deletedBy(),
          DeliveryNotification_.deletedBy
        )
      );
    }
    return specification;
  }
}
