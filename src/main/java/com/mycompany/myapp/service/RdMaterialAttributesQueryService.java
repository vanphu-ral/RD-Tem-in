package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.RdMaterialAttributes;
import com.mycompany.myapp.repository.partner5.RdMaterialAttributesRepository;
import com.mycompany.myapp.service.criteria.RdMaterialAttributesCriteria;
import com.mycompany.myapp.service.dto.RdMaterialAttributesDTO;
import com.mycompany.myapp.service.mapper.RdMaterialAttributesMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link RdMaterialAttributes} entities in the database.
 * The main input is a {@link RdMaterialAttributesCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link RdMaterialAttributesDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RdMaterialAttributesQueryService
    extends QueryService<RdMaterialAttributes> {

    private static final Logger LOG = LoggerFactory.getLogger(
        RdMaterialAttributesQueryService.class
    );

    private final RdMaterialAttributesRepository rdMaterialAttributesRepository;

    private final RdMaterialAttributesMapper rdMaterialAttributesMapper;

    public RdMaterialAttributesQueryService(
        RdMaterialAttributesRepository rdMaterialAttributesRepository,
        RdMaterialAttributesMapper rdMaterialAttributesMapper
    ) {
        this.rdMaterialAttributesRepository = rdMaterialAttributesRepository;
        this.rdMaterialAttributesMapper = rdMaterialAttributesMapper;
    }

    /**
     * Return a {@link List} of {@link RdMaterialAttributesDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<RdMaterialAttributesDTO> findByCriteria(
        RdMaterialAttributesCriteria criteria
    ) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<RdMaterialAttributes> specification =
            createSpecification(criteria);
        return rdMaterialAttributesMapper.toDto(
            rdMaterialAttributesRepository.findAll(specification)
        );
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RdMaterialAttributesCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<RdMaterialAttributes> specification =
            createSpecification(criteria);
        return rdMaterialAttributesRepository.count(specification);
    }

    /**
     * Function to convert {@link RdMaterialAttributesCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RdMaterialAttributes> createSpecification(
        RdMaterialAttributesCriteria criteria
    ) {
        Specification<RdMaterialAttributes> specification = Specification.where(
            null
        );
        if (criteria != null) {
            // Add distinct if specified
            if (Boolean.TRUE.equals(criteria.getDistinct())) {
                specification = specification.and(
                    distinct(criteria.getDistinct())
                );
            }

            // Add all other specifications using chained .and() calls
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getId(),
                    RdMaterialAttributes_.id
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getAttributes(),
                    RdMaterialAttributes_.attributes
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getDescription(),
                    RdMaterialAttributes_.description
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getAttributesTypeId(),
                    RdMaterialAttributes_.attributesTypeId
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getCreatedBy(),
                    RdMaterialAttributes_.createdBy
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getCreatedAt(),
                    RdMaterialAttributes_.createdAt
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getUpdatedBy(),
                    RdMaterialAttributes_.updatedBy
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getUpdatedAt(),
                    RdMaterialAttributes_.updatedAt
                )
            );
        }
        return specification;
    }
}
