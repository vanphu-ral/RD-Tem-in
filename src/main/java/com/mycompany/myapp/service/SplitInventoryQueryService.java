package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.SplitInventory;
import com.mycompany.myapp.repository.SplitInventoryRepository;
import com.mycompany.myapp.service.criteria.SplitInventoryCriteria;
import com.mycompany.myapp.service.dto.SplitInventoryDTO;
import com.mycompany.myapp.service.mapper.SplitInventoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link SplitInventory} entities in the database.
 * The main input is a {@link SplitInventoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SplitInventoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SplitInventoryQueryService extends QueryService<SplitInventory> {

    private final Logger log = LoggerFactory.getLogger(
        SplitInventoryQueryService.class
    );

    private final SplitInventoryRepository splitInventoryRepository;

    private final SplitInventoryMapper splitInventoryMapper;

    public SplitInventoryQueryService(
        SplitInventoryRepository splitInventoryRepository,
        SplitInventoryMapper splitInventoryMapper
    ) {
        this.splitInventoryRepository = splitInventoryRepository;
        this.splitInventoryMapper = splitInventoryMapper;
    }

    /**
     * Return a {@link Page} of {@link SplitInventoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SplitInventoryDTO> findByCriteria(
        SplitInventoryCriteria criteria,
        Pageable page
    ) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SplitInventory> specification = createSpecification(
            criteria
        );
        return splitInventoryRepository
            .findAll(specification, page)
            .map(splitInventoryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SplitInventoryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SplitInventory> specification = createSpecification(
            criteria
        );
        return splitInventoryRepository.count(specification);
    }

    /**
     * Function to convert {@link SplitInventoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SplitInventory> createSpecification(
        SplitInventoryCriteria criteria
    ) {
        Specification<SplitInventory> specification = Specification.where(null);
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
                        SplitInventory_.id
                    )
                );
            }
            if (criteria.getBusinessSourceId() != null) {
                specification = specification.and(
                    buildRangeSpecification(
                        criteria.getBusinessSourceId(),
                        SplitInventory_.businessSourceId
                    )
                );
            }
            if (criteria.getBusinessType() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getBusinessType(),
                        SplitInventory_.businessType
                    )
                );
            }
            if (criteria.getReelId() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getReelId(),
                        SplitInventory_.reelId
                    )
                );
            }
            if (criteria.getParentMaterialIdentifier() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getParentMaterialIdentifier(),
                        SplitInventory_.parentMaterialIdentifier
                    )
                );
            }
            if (criteria.getPartNumber() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getPartNumber(),
                        SplitInventory_.partNumber
                    )
                );
            }
            if (criteria.getVendor() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getVendor(),
                        SplitInventory_.vendor
                    )
                );
            }
            if (criteria.getLot() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getLot(),
                        SplitInventory_.lot
                    )
                );
            }
            if (criteria.getUserData1() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getUserData1(),
                        SplitInventory_.userData1
                    )
                );
            }
            if (criteria.getUserData2() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getUserData2(),
                        SplitInventory_.userData2
                    )
                );
            }
            if (criteria.getUserData3() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getUserData3(),
                        SplitInventory_.userData3
                    )
                );
            }
            if (criteria.getUserData4() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getUserData4(),
                        SplitInventory_.userData4
                    )
                );
            }
            if (criteria.getUserData5() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getUserData5(),
                        SplitInventory_.userData5
                    )
                );
            }
            if (criteria.getInitialQuantity() != null) {
                specification = specification.and(
                    buildRangeSpecification(
                        criteria.getInitialQuantity(),
                        SplitInventory_.initialQuantity
                    )
                );
            }
            if (criteria.getMsdLevel() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getMsdLevel(),
                        SplitInventory_.msdLevel
                    )
                );
            }
            if (criteria.getMsdInitialFloorTime() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getMsdInitialFloorTime(),
                        SplitInventory_.msdInitialFloorTime
                    )
                );
            }
            if (criteria.getMsdBagSealDate() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getMsdBagSealDate(),
                        SplitInventory_.msdBagSealDate
                    )
                );
            }
            if (criteria.getMarketUsage() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getMarketUsage(),
                        SplitInventory_.marketUsage
                    )
                );
            }
            if (criteria.getQuantityOverride() != null) {
                specification = specification.and(
                    buildRangeSpecification(
                        criteria.getQuantityOverride(),
                        SplitInventory_.quantityOverride
                    )
                );
            }
            if (criteria.getShelfTime() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getShelfTime(),
                        SplitInventory_.shelfTime
                    )
                );
            }
            if (criteria.getSpMaterialName() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getSpMaterialName(),
                        SplitInventory_.spMaterialName
                    )
                );
            }
            if (criteria.getWarningLimit() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getWarningLimit(),
                        SplitInventory_.warningLimit
                    )
                );
            }
            if (criteria.getMaximumLimit() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getMaximumLimit(),
                        SplitInventory_.maximumLimit
                    )
                );
            }
            if (criteria.getComments() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getComments(),
                        SplitInventory_.comments
                    )
                );
            }
            if (criteria.getWarmupTime() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getWarmupTime(),
                        SplitInventory_.warmupTime
                    )
                );
            }
            if (criteria.getStorageUnit() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getStorageUnit(),
                        SplitInventory_.storageUnit
                    )
                );
            }
            if (criteria.getSubStorageUnit() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getSubStorageUnit(),
                        SplitInventory_.subStorageUnit
                    )
                );
            }
            if (criteria.getLocationOverride() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getLocationOverride(),
                        SplitInventory_.locationOverride
                    )
                );
            }
            if (criteria.getExpirationDate() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getExpirationDate(),
                        SplitInventory_.expirationDate
                    )
                );
            }
            if (criteria.getManufacturingDate() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getManufacturingDate(),
                        SplitInventory_.manufacturingDate
                    )
                );
            }
            if (criteria.getPartClass() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getPartClass(),
                        SplitInventory_.partClass
                    )
                );
            }
            if (criteria.getSapCode() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getSapCode(),
                        SplitInventory_.sapCode
                    )
                );
            }
            if (criteria.getVendorQrCode() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getVendorQrCode(),
                        SplitInventory_.vendorQrCode
                    )
                );
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getStatus(),
                        SplitInventory_.status
                    )
                );
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getCreatedBy(),
                        SplitInventory_.createdBy
                    )
                );
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(
                    buildRangeSpecification(
                        criteria.getCreatedAt(),
                        SplitInventory_.createdAt
                    )
                );
            }
            if (criteria.getUpdatedBy() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getUpdatedBy(),
                        SplitInventory_.updatedBy
                    )
                );
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(
                    buildRangeSpecification(
                        criteria.getUpdatedAt(),
                        SplitInventory_.updatedAt
                    )
                );
            }
            if (criteria.getPrintStatus() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getPrintStatus(),
                        SplitInventory_.printStatus
                    )
                );
            }
        }
        return specification;
    }
}
