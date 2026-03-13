package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.AttributesType;
import com.mycompany.myapp.repository.partner5.AttributesTypeRepository;
import com.mycompany.myapp.service.criteria.AttributesTypeCriteria;
import com.mycompany.myapp.service.dto.AttributesTypeDTO;
import com.mycompany.myapp.service.mapper.AttributesTypeMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link AttributesType} entities in the database.
 * The main input is a {@link AttributesTypeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AttributesTypeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AttributesTypeQueryService extends QueryService<AttributesType> {

    private static final Logger LOG = LoggerFactory.getLogger(
        AttributesTypeQueryService.class
    );

    private final AttributesTypeRepository attributesTypeRepository;

    private final AttributesTypeMapper attributesTypeMapper;

    public AttributesTypeQueryService(
        AttributesTypeRepository attributesTypeRepository,
        AttributesTypeMapper attributesTypeMapper
    ) {
        this.attributesTypeRepository = attributesTypeRepository;
        this.attributesTypeMapper = attributesTypeMapper;
    }

    /**
     * Return a {@link List} of {@link AttributesTypeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AttributesTypeDTO> findByCriteria(
        AttributesTypeCriteria criteria
    ) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<AttributesType> specification = createSpecification(
            criteria
        );
        return attributesTypeMapper.toDto(
            attributesTypeRepository.findAll(specification)
        );
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AttributesTypeCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AttributesType> specification = createSpecification(
            criteria
        );
        return attributesTypeRepository.count(specification);
    }

    /**
     * Function to convert {@link AttributesTypeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AttributesType> createSpecification(
        AttributesTypeCriteria criteria
    ) {
        List<Specification<AttributesType>> specs = new java.util.ArrayList<>();
        if (Boolean.TRUE.equals(criteria.getDistinct())) {
            specs.add(distinct(criteria.getDistinct()));
        }
        if (criteria.getId() != null) {
            specs.add(
                buildRangeSpecification(criteria.getId(), AttributesType_.id)
            );
        }
        if (criteria.getDescription() != null) {
            specs.add(
                buildStringSpecification(
                    criteria.getDescription(),
                    AttributesType_.description
                )
            );
        }
        if (criteria.getRdMaterialAttributeId() != null) {
            specs.add(
                buildSpecification(criteria.getRdMaterialAttributeId(), root ->
                    root
                        .join(
                            AttributesType_.rdMaterialAttributes,
                            JoinType.LEFT
                        )
                        .get(RdMaterialAttributes_.id)
                )
            );
        }
        Specification<AttributesType> finalSpec = specs
            .stream()
            .reduce(Specification::and)
            .orElse(null);

        return finalSpec;
    }
}
