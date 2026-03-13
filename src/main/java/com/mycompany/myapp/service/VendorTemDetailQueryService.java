package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.VendorTemDetail;
import com.mycompany.myapp.repository.partner5.VendorTemDetailRepository;
import com.mycompany.myapp.service.criteria.VendorTemDetailCriteria;
import com.mycompany.myapp.service.dto.VendorTemDetailDTO;
import com.mycompany.myapp.service.mapper.VendorTemDetailMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link VendorTemDetail} entities in the database.
 * The main input is a {@link VendorTemDetailCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link VendorTemDetailDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VendorTemDetailQueryService extends QueryService<VendorTemDetail> {

    private static final Logger LOG = LoggerFactory.getLogger(
        VendorTemDetailQueryService.class
    );

    private final VendorTemDetailRepository vendorTemDetailRepository;

    private final VendorTemDetailMapper vendorTemDetailMapper;

    public VendorTemDetailQueryService(
        VendorTemDetailRepository vendorTemDetailRepository,
        VendorTemDetailMapper vendorTemDetailMapper
    ) {
        this.vendorTemDetailRepository = vendorTemDetailRepository;
        this.vendorTemDetailMapper = vendorTemDetailMapper;
    }

    /**
     * Return a {@link List} of {@link VendorTemDetailDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<VendorTemDetailDTO> findByCriteria(
        VendorTemDetailCriteria criteria
    ) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<VendorTemDetail> specification =
            createSpecification(criteria);
        return vendorTemDetailMapper.toDto(
            vendorTemDetailRepository.findAll(specification)
        );
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VendorTemDetailCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<VendorTemDetail> specification =
            createSpecification(criteria);
        return vendorTemDetailRepository.count(specification);
    }

    /**
     * Function to convert {@link VendorTemDetailCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<VendorTemDetail> createSpecification(
        VendorTemDetailCriteria criteria
    ) {
        Specification<VendorTemDetail> specification = Specification.where(
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
                buildRangeSpecification(criteria.getId(), VendorTemDetail_.id)
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getReelId(),
                    VendorTemDetail_.reelId
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPartNumber(),
                    VendorTemDetail_.partNumber
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getVendor(),
                    VendorTemDetail_.vendor
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getLot(),
                    VendorTemDetail_.lot
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getUserData1(),
                    VendorTemDetail_.userData1
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getUserData2(),
                    VendorTemDetail_.userData2
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getUserData3(),
                    VendorTemDetail_.userData3
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getUserData4(),
                    VendorTemDetail_.userData4
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getUserData5(),
                    VendorTemDetail_.userData5
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getInitialQuantity(),
                    VendorTemDetail_.initialQuantity
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getMsdLevel(),
                    VendorTemDetail_.msdLevel
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getMsdInitialFloorTime(),
                    VendorTemDetail_.msdInitialFloorTime
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getMsdBagSealDate(),
                    VendorTemDetail_.msdBagSealDate
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getMarketUsage(),
                    VendorTemDetail_.marketUsage
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getQuantityOverride(),
                    VendorTemDetail_.quantityOverride
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getShelfTime(),
                    VendorTemDetail_.shelfTime
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getSpMaterialName(),
                    VendorTemDetail_.spMaterialName
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getWarningLimit(),
                    VendorTemDetail_.warningLimit
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getMaximumLimit(),
                    VendorTemDetail_.maximumLimit
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getComments(),
                    VendorTemDetail_.comments
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getWarmupTime(),
                    VendorTemDetail_.warmupTime
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getStorageUnit(),
                    VendorTemDetail_.storageUnit
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getSubStorageUnit(),
                    VendorTemDetail_.subStorageUnit
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getLocationOverride(),
                    VendorTemDetail_.locationOverride
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getExpirationDate(),
                    VendorTemDetail_.expirationDate
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getManufacturingDate(),
                    VendorTemDetail_.manufacturingDate
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPartClass(),
                    VendorTemDetail_.partClass
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getSapCode(),
                    VendorTemDetail_.sapCode
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getVendorQrCode(),
                    VendorTemDetail_.vendorQrCode
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getStatus(),
                    VendorTemDetail_.status
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getCreatedBy(),
                    VendorTemDetail_.createdBy
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getCreatedAt(),
                    VendorTemDetail_.createdAt
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getUpdatedBy(),
                    VendorTemDetail_.updatedBy
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getUpdatedAt(),
                    VendorTemDetail_.updatedAt
                )
            );
            specification = specification.and(
                buildSpecification(criteria.getPoDetailId(), root ->
                    root
                        .join(VendorTemDetail_.poDetail, JoinType.LEFT)
                        .get(PoDetail_.id)
                )
            );
        }
        return specification;
    }
}
