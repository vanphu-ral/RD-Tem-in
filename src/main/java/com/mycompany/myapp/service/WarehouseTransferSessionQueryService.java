package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.WarehouseTransferSession;
import com.mycompany.myapp.repository.OwtqWarehouseTransferSessionRepository;
import com.mycompany.myapp.repository.WarehouseTransferSessionRepository;
import com.mycompany.myapp.service.criteria.WarehouseTransferSessionCriteria;
import com.mycompany.myapp.service.dto.WarehouseTransferSessionDTO;
import com.mycompany.myapp.service.mapper.WarehouseTransferSessionMapper;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link WarehouseTransferSession} entities in the database.
 * The main input is a {@link WarehouseTransferSessionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link WarehouseTransferSessionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WarehouseTransferSessionQueryService
    extends QueryService<WarehouseTransferSession> {

    private final Logger log = LoggerFactory.getLogger(
        WarehouseTransferSessionQueryService.class
    );

    private final WarehouseTransferSessionRepository warehouseTransferSessionRepository;

    private final OwtqWarehouseTransferSessionRepository owtqWarehouseTransferSessionRepository;

    private final WarehouseTransferSessionMapper warehouseTransferSessionMapper;

    public WarehouseTransferSessionQueryService(
        WarehouseTransferSessionRepository warehouseTransferSessionRepository,
        OwtqWarehouseTransferSessionRepository owtqWarehouseTransferSessionRepository,
        WarehouseTransferSessionMapper warehouseTransferSessionMapper
    ) {
        this.warehouseTransferSessionRepository =
            warehouseTransferSessionRepository;
        this.owtqWarehouseTransferSessionRepository =
            owtqWarehouseTransferSessionRepository;
        this.warehouseTransferSessionMapper = warehouseTransferSessionMapper;
    }

    /**
     * Return a {@link Page} of {@link WarehouseTransferSessionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WarehouseTransferSessionDTO> findByCriteria(
        WarehouseTransferSessionCriteria criteria,
        Pageable page
    ) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<WarehouseTransferSession> specification =
            createSpecification(criteria);
        return warehouseTransferSessionRepository
            .findAll(specification, page)
            .map(session -> {
                WarehouseTransferSessionDTO dto =
                    warehouseTransferSessionMapper.toDto(session);
                java.util.List<Long> owtqList =
                    owtqWarehouseTransferSessionRepository
                        .findByWarehouseTransferSession_Id(session.getId())
                        .stream()
                        .map(OwtqWarehouseTransferSession::getOwtqDocEntry)
                        .collect(java.util.stream.Collectors.toList());
                dto.setOwtqList(owtqList);
                return dto;
            });
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WarehouseTransferSessionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<WarehouseTransferSession> specification =
            createSpecification(criteria);
        return warehouseTransferSessionRepository.count(specification);
    }

    /**
     * Function to convert {@link WarehouseTransferSessionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<WarehouseTransferSession> createSpecification(
        WarehouseTransferSessionCriteria criteria
    ) {
        Specification<WarehouseTransferSession> specification =
            Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(
                    distinct(criteria.getDistinct())
                );
            }
            if (criteria.getId() != null) {
                specification = specification.and(
                    buildRangeSpecification(
                        criteria.getId(),
                        WarehouseTransferSession_.id
                    )
                );
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getStatus(),
                        WarehouseTransferSession_.status
                    )
                );
            }
            if (criteria.getNote() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getNote(),
                        WarehouseTransferSession_.note
                    )
                );
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(
                    buildRangeSpecification(
                        criteria.getCreatedAt(),
                        WarehouseTransferSession_.createdAt
                    )
                );
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getCreatedBy(),
                        WarehouseTransferSession_.createdBy
                    )
                );
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(
                    buildRangeSpecification(
                        criteria.getUpdatedAt(),
                        WarehouseTransferSession_.updatedAt
                    )
                );
            }
            if (criteria.getUpdatedBy() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getUpdatedBy(),
                        WarehouseTransferSession_.updatedBy
                    )
                );
            }
            if (criteria.getDeletedAt() != null) {
                specification = specification.and(
                    buildRangeSpecification(
                        criteria.getDeletedAt(),
                        WarehouseTransferSession_.deletedAt
                    )
                );
            }
            if (criteria.getDeletedBy() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getDeletedBy(),
                        WarehouseTransferSession_.deletedBy
                    )
                );
            }
            if (criteria.getDetailsId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getDetailsId(), root ->
                        root
                            .join(
                                WarehouseTransferSession_.details,
                                JoinType.LEFT
                            )
                            .get(WarehouseTransferDetail_.id)
                    )
                );
            }
        }
        return specification;
    }
}
