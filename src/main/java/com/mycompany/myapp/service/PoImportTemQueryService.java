package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.repository.partner5.PoImportTemRepository;
import com.mycompany.myapp.service.criteria.PoImportTemCriteria;
import com.mycompany.myapp.service.dto.PoImportTemDTO;
import com.mycompany.myapp.service.mapper.PoImportTemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link PoImportTem} entities in the
 * database.
 * The main input is a {@link PoImportTemCriteria} which gets converted to
 * {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PoImportTemDTO} which fulfills the
 * criteria.
 */

@Service
@Transactional(readOnly = true)
public class PoImportTemQueryService extends QueryService<PoImportTem> {

    private static final Logger LOG = LoggerFactory.getLogger(
        PoImportTemQueryService.class
    );

    private final PoImportTemRepository poImportTemRepository;

    private final PoImportTemMapper poImportTemMapper;

    public PoImportTemQueryService(
        PoImportTemRepository poImportTemRepository,
        PoImportTemMapper poImportTemMapper
    ) {
        this.poImportTemRepository = poImportTemRepository;
        this.poImportTemMapper = poImportTemMapper;
    }

    /**
     * Return a {@link Page} of {@link PoImportTemDTO} which matches the criteria
     * from the database.
     *
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PoImportTemDTO> findByCriteria(
        PoImportTemCriteria criteria,
        Pageable page
    ) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PoImportTem> specification = createSpecification(
            criteria
        );
        return poImportTemRepository
            .findAll(specification, page)
            .map(poImportTemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PoImportTemCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PoImportTem> specification = createSpecification(
            criteria
        );
        return poImportTemRepository.count(specification);
    }

    /**
     * Function to convert {@link PoImportTemCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PoImportTem> createSpecification(
        PoImportTemCriteria criteria
    ) {
        Specification<PoImportTem> specification = Specification.where(null);
        if (Boolean.TRUE.equals(criteria.getDistinct())) {
            specification = specification.and(distinct(criteria.getDistinct()));
        }
        if (criteria.getId() != null) {
            specification = specification.and(
                buildRangeSpecification(criteria.getId(), PoImportTem_.id)
            );
        }
        if (criteria.getPoNumber() != null) {
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPoNumber(),
                    PoImportTem_.poNumber
                )
            );
        }
        if (criteria.getVendorCode() != null) {
            specification = specification.and(
                buildStringSpecification(
                    criteria.getVendorCode(),
                    PoImportTem_.vendorCode
                )
            );
        }
        if (criteria.getVendorName() != null) {
            specification = specification.and(
                buildStringSpecification(
                    criteria.getVendorCode(),
                    PoImportTem_.vendorName
                )
            );
        }
        if (criteria.getEntryDate() != null) {
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getEntryDate(),
                    PoImportTem_.entryDate
                )
            );
        }
        if (criteria.storageUnit() != null) {
            specification = specification.and(
                buildStringSpecification(
                    criteria.getStorageUnit(),
                    PoImportTem_.storageUnit
                )
            );
        }
        if (criteria.getQuantityContainer() != null) {
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getQuantityContainer(),
                    PoImportTem_.quantityContainer
                )
            );
        }
        if (criteria.getTotalQuantity() != null) {
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getTotalQuantity(),
                    PoImportTem_.totalQuantity
                )
            );
        }
        if (criteria.getStatus() != null) {
            specification = specification.and(
                buildStringSpecification(
                    criteria.getStatus(),
                    PoImportTem_.status
                )
            );
        }
        if (criteria.getCreatedBy() != null) {
            specification = specification.and(
                buildStringSpecification(
                    criteria.getCreatedBy(),
                    PoImportTem_.createdBy
                )
            );
        }
        if (criteria.getCreatedAt() != null) {
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getCreatedAt(),
                    PoImportTem_.createdAt
                )
            );
        }
        if (criteria.getUpdatedBy() != null) {
            specification = specification.and(
                buildStringSpecification(
                    criteria.getUpdatedBy(),
                    PoImportTem_.updatedBy
                )
            );
        }
        if (criteria.getUpdatedAt() != null) {
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getUpdatedAt(),
                    PoImportTem_.updatedAt
                )
            );
        }
        if (criteria.getDeletedBy() != null) {
            specification = specification.and(
                buildStringSpecification(
                    criteria.getDeletedBy(),
                    PoImportTem_.deletedBy
                )
            );
        }
        if (criteria.getDeletedAt() != null) {
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getDeletedAt(),
                    PoImportTem_.deletedAt
                )
            );
        }
        return specification;
    }
}
