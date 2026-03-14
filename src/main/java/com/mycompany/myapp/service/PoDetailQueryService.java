package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.PoDetail;
import com.mycompany.myapp.repository.partner5.PoDetailRepository;
import com.mycompany.myapp.service.criteria.PoDetailCriteria;
import com.mycompany.myapp.service.dto.PoDetailDTO;
import com.mycompany.myapp.service.mapper.PoDetailMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link PoDetail} entities in the database.
 * The main input is a {@link PoDetailCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PoDetailDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PoDetailQueryService extends QueryService<PoDetail> {

    private static final Logger LOG = LoggerFactory.getLogger(
        PoDetailQueryService.class
    );

    private final PoDetailRepository poDetailRepository;

    private final PoDetailMapper poDetailMapper;

    public PoDetailQueryService(
        PoDetailRepository poDetailRepository,
        PoDetailMapper poDetailMapper
    ) {
        this.poDetailRepository = poDetailRepository;
        this.poDetailMapper = poDetailMapper;
    }

    /**
     * Return a {@link List} of {@link PoDetailDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PoDetailDTO> findByCriteria(PoDetailCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<PoDetail> specification = createSpecification(
            criteria
        );
        return poDetailMapper.toDto(poDetailRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PoDetailCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PoDetail> specification = createSpecification(
            criteria
        );
        return poDetailRepository.count(specification);
    }

    /**
     * Function to convert {@link PoDetailCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PoDetail> createSpecification(
        PoDetailCriteria criteria
    ) {
        Specification<PoDetail> specification = Specification.where(null);
        if (criteria != null) {
            // Add distinct if specified
            if (Boolean.TRUE.equals(criteria.getDistinct())) {
                specification = specification.and(
                    distinct(criteria.getDistinct())
                );
            }

            // Add all other specifications using chained .and() calls
            specification = specification.and(
                buildRangeSpecification(criteria.getId(), PoDetail_.id)
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getSapCode(),
                    PoDetail_.sapCode
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getSapName(),
                    PoDetail_.sapName
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getQuantityContainer(),
                    PoDetail_.quantityContainer
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getTotalQuantity(),
                    PoDetail_.totalQuantity
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPartNumber(),
                    PoDetail_.partNumber
                )
            );
            specification = specification.and(
                buildSpecification(criteria.getVendorTemDetailId(), root ->
                    root
                        .join(PoDetail_.vendorTemDetails, JoinType.LEFT)
                        .get(VendorTemDetail_.id)
                )
            );
            specification = specification.and(
                buildSpecification(
                    criteria.getImportVendorTemTransactionsId(),
                    root ->
                        root
                            .join(
                                PoDetail_.importVendorTemTransactions,
                                JoinType.LEFT
                            )
                            .get(ImportVendorTemTransactions_.id)
                )
            );
        }
        return specification;
    }
}
