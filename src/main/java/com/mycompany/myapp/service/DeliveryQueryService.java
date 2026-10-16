package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Delivery;
import com.mycompany.myapp.repository.DeliveryRepository;
import com.mycompany.myapp.service.criteria.DeliveryCriteria;
import com.mycompany.myapp.service.dto.DeliveryDTO;
import com.mycompany.myapp.service.mapper.DeliveryMapper;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Delivery} entities in the database.
 * The main input is a {@link DeliveryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DeliveryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DeliveryQueryService extends QueryService<Delivery> {

    private final Logger log = LoggerFactory.getLogger(
        DeliveryQueryService.class
    );

    private final DeliveryRepository deliveryRepository;

    private final DeliveryMapper deliveryMapper;

    public DeliveryQueryService(
        DeliveryRepository deliveryRepository,
        DeliveryMapper deliveryMapper
    ) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryMapper = deliveryMapper;
    }

    /**
     * Return a {@link Page} of {@link DeliveryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DeliveryDTO> findByCriteria(
        DeliveryCriteria criteria,
        Pageable page
    ) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Delivery> specification = createSpecification(
            criteria
        );
        return deliveryRepository
            .findAll(specification, page)
            .map(deliveryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DeliveryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Delivery> specification = createSpecification(
            criteria
        );
        return deliveryRepository.count(specification);
    }

    /**
     * Function to convert {@link DeliveryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Delivery> createSpecification(
        DeliveryCriteria criteria
    ) {
        Specification<Delivery> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(
                    distinct(criteria.getDistinct())
                );
            }
            if (criteria.getDeliveryId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getDeliveryId(),
                        Delivery_.deliveryId
                    )
                );
            }
            if (criteria.getType() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getType(), Delivery_.type)
                );
            }
            if (criteria.getNoticeNo() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getNoticeNo(),
                        Delivery_.noticeNo
                    )
                );
            }
            if (criteria.getNoticeDate() != null) {
                specification = specification.and(
                    buildRangeSpecification(
                        criteria.getNoticeDate(),
                        Delivery_.noticeDate
                    )
                );
            }
            if (criteria.getSupplierCode() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getSupplierCode(),
                        Delivery_.supplierCode
                    )
                );
            }
            if (criteria.getSupplierName() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getSupplierName(),
                        Delivery_.supplierName
                    )
                );
            }
            if (criteria.getFactory() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getFactory(),
                        Delivery_.factory
                    )
                );
            }
            if (criteria.getDateArrivalRangDong() != null) {
                specification = specification.and(
                    buildRangeSpecification(
                        criteria.getDateArrivalRangDong(),
                        Delivery_.dateArrivalRangDong
                    )
                );
            }
            if (criteria.getVehicleNo() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getVehicleNo(),
                        Delivery_.vehicleNo
                    )
                );
            }
            if (criteria.getNumberOfPo() != null) {
                specification = specification.and(
                    buildRangeSpecification(
                        criteria.getNumberOfPo(),
                        Delivery_.numberOfPo
                    )
                );
            }
            if (criteria.getNumberOfItem() != null) {
                specification = specification.and(
                    buildRangeSpecification(
                        criteria.getNumberOfItem(),
                        Delivery_.numberOfItem
                    )
                );
            }
            if (criteria.getSource() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getSource(),
                        Delivery_.source
                    )
                );
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(
                    buildRangeSpecification(
                        criteria.getCreatedAt(),
                        Delivery_.createdAt
                    )
                );
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getCreatedBy(),
                        Delivery_.createdBy
                    )
                );
            }
            if (criteria.getDeletedAt() != null) {
                specification = specification.and(
                    buildRangeSpecification(
                        criteria.getDeletedAt(),
                        Delivery_.deletedAt
                    )
                );
            }
            if (criteria.getDeletedBy() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getDeletedBy(),
                        Delivery_.deletedBy
                    )
                );
            }
            if (criteria.getDetailDeliveriesId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getDetailDeliveriesId(),
                        (Root<Delivery> root) ->
                            root
                                .join(Delivery_.detailDeliveries, JoinType.LEFT)
                                .get(DetailDelivery_.lineId)
                    )
                );
            }
        }
        return specification;
    }
}
