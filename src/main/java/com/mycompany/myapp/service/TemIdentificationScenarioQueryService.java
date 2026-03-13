package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.TemIdentificationScenario;
import com.mycompany.myapp.repository.partner5.TemIdentificationScenarioRepository;
import com.mycompany.myapp.service.criteria.TemIdentificationScenarioCriteria;
import com.mycompany.myapp.service.dto.TemIdentificationScenarioDTO;
import com.mycompany.myapp.service.mapper.TemIdentificationScenarioMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TemIdentificationScenario} entities in the database.
 * The main input is a {@link TemIdentificationScenarioCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TemIdentificationScenarioDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TemIdentificationScenarioQueryService
    extends QueryService<TemIdentificationScenario> {

    private static final Logger LOG = LoggerFactory.getLogger(
        TemIdentificationScenarioQueryService.class
    );

    private final TemIdentificationScenarioRepository temIdentificationScenarioRepository;

    private final TemIdentificationScenarioMapper temIdentificationScenarioMapper;

    public TemIdentificationScenarioQueryService(
        TemIdentificationScenarioRepository temIdentificationScenarioRepository,
        TemIdentificationScenarioMapper temIdentificationScenarioMapper
    ) {
        this.temIdentificationScenarioRepository =
            temIdentificationScenarioRepository;
        this.temIdentificationScenarioMapper = temIdentificationScenarioMapper;
    }

    /**
     * Return a {@link List} of {@link TemIdentificationScenarioDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TemIdentificationScenarioDTO> findByCriteria(
        TemIdentificationScenarioCriteria criteria
    ) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<TemIdentificationScenario> specification =
            createSpecification(criteria);
        return temIdentificationScenarioMapper.toDto(
            temIdentificationScenarioRepository.findAll(specification)
        );
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TemIdentificationScenarioCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TemIdentificationScenario> specification =
            createSpecification(criteria);
        return temIdentificationScenarioRepository.count(specification);
    }

    /**
     * Function to convert {@link TemIdentificationScenarioCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TemIdentificationScenario> createSpecification(
        TemIdentificationScenarioCriteria criteria
    ) {
        List<Specification<TemIdentificationScenario>> specs =
            new java.util.ArrayList<>();
        if (Boolean.TRUE.equals(criteria.getDistinct())) {
            specs.add(distinct(criteria.getDistinct()));
        }
        if (criteria.getId() != null) {
            specs.add(
                buildRangeSpecification(
                    criteria.getId(),
                    TemIdentificationScenario_.id
                )
            );
        }
        if (criteria.getVendorCode() != null) {
            specs.add(
                buildStringSpecification(
                    criteria.getVendorCode(),
                    TemIdentificationScenario_.vendorCode
                )
            );
        }
        if (criteria.getVendorName() != null) {
            specs.add(
                buildStringSpecification(
                    criteria.getVendorName(),
                    TemIdentificationScenario_.vendorName
                )
            );
        }
        if (criteria.getMappingConfig() != null) {
            specs.add(
                buildStringSpecification(
                    criteria.getMappingConfig(),
                    TemIdentificationScenario_.mappingConfig
                )
            );
        }
        if (criteria.getCreatedBy() != null) {
            specs.add(
                buildStringSpecification(
                    criteria.getCreatedBy(),
                    TemIdentificationScenario_.createdBy
                )
            );
        }
        if (criteria.getCreatedAt() != null) {
            specs.add(
                buildRangeSpecification(
                    criteria.getCreatedAt(),
                    TemIdentificationScenario_.createdAt
                )
            );
        }
        if (criteria.getUpdatedBy() != null) {
            specs.add(
                buildStringSpecification(
                    criteria.getUpdatedBy(),
                    TemIdentificationScenario_.updatedBy
                )
            );
        }
        if (criteria.getUpdatedAt() != null) {
            specs.add(
                buildRangeSpecification(
                    criteria.getUpdatedAt(),
                    TemIdentificationScenario_.updatedAt
                )
            );
        }
        Specification<TemIdentificationScenario> finalSpec = specs
            .stream()
            .reduce(Specification::and)
            .orElse(null);

        return finalSpec;
    }
}
