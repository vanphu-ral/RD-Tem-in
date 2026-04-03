package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.repository.partner5.ImportVendorTemTransactionsRepository;
import com.mycompany.myapp.service.criteria.ImportVendorTemTransactionsCriteria;
import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDTO;
import com.mycompany.myapp.service.mapper.ImportVendorTemTransactionsMapper;
import java.util.List;
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
 * Service for executing complex queries for {@link ImportVendorTemTransactions}
 * entities in the database.
 * The main input is a {@link ImportVendorTemTransactionsCriteria} which gets
 * converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ImportVendorTemTransactionsDTO} which
 * fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ImportVendorTemTransactionsQueryService
    extends QueryService<ImportVendorTemTransactions> {

    private static final Logger LOG = LoggerFactory.getLogger(
        ImportVendorTemTransactionsQueryService.class
    );

    private final ImportVendorTemTransactionsRepository importVendorTemTransactionsRepository;

    private final ImportVendorTemTransactionsMapper importVendorTemTransactionsMapper;

    public ImportVendorTemTransactionsQueryService(
        ImportVendorTemTransactionsRepository importVendorTemTransactionsRepository,
        ImportVendorTemTransactionsMapper importVendorTemTransactionsMapper
    ) {
        this.importVendorTemTransactionsRepository =
            importVendorTemTransactionsRepository;
        this.importVendorTemTransactionsMapper =
            importVendorTemTransactionsMapper;
    }

    /**
     * Return a {@link Page} of {@link ImportVendorTemTransactionsDTO} which matches
     * the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ImportVendorTemTransactionsDTO> findByCriteria(
        ImportVendorTemTransactionsCriteria criteria,
        Pageable page
    ) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ImportVendorTemTransactions> specification =
            createSpecification(criteria);

        // 1. Lấy trang dữ liệu (lúc này poDetails vẫn là Proxy/rỗng)
        Page<ImportVendorTemTransactions> result =
            importVendorTemTransactionsRepository.findAll(specification, page);

        // 2. "Chạm" vào danh sách poDetails để kích hoạt Hibernate nạp dữ liệu.
        // Nhờ có @BatchSize(size = 20) trong Entity, dòng này sẽ CHỈ sinh ra
        // đúng 1 câu lệnh SQL "SELECT ... WHERE id IN (...)" cho toàn bộ 20 bản ghi.
        result
            .getContent()
            .forEach(transaction -> {
                if (transaction.getPoDetails() != null) {
                    transaction.getPoDetails().size(); // Lấy size() để ép Hibernate load dữ liệu
                }
            });

        // 3. Bây giờ map sang DTO sẽ an toàn 100% vì dữ liệu đã nằm trong RAM
        return result.map(importVendorTemTransactionsMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ImportVendorTemTransactionsCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ImportVendorTemTransactions> specification =
            createSpecification(criteria);
        return importVendorTemTransactionsRepository.count(specification);
    }

    /**
     * Function to convert {@link ImportVendorTemTransactionsCriteria} to a
     * {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ImportVendorTemTransactions> createSpecification(
        ImportVendorTemTransactionsCriteria criteria
    ) {
        Specification<ImportVendorTemTransactions> specification =
            Specification.where(null);
        if (Boolean.TRUE.equals(criteria.getDistinct())) {
            specification = specification.and(distinct(criteria.getDistinct()));
        }
        if (criteria.getId() != null) {
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getId(),
                    ImportVendorTemTransactions_.id
                )
            );
        }
        if (criteria.getPoNumber() != null) {
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPoNumber(),
                    ImportVendorTemTransactions_.poNumber
                )
            );
        }
        if (criteria.getVendorCode() != null) {
            specification = specification.and(
                buildStringSpecification(
                    criteria.getVendorCode(),
                    ImportVendorTemTransactions_.vendorCode
                )
            );
        }
        if (criteria.getVendorName() != null) {
            specification = specification.and(
                buildStringSpecification(
                    criteria.getVendorName(),
                    ImportVendorTemTransactions_.vendorName
                )
            );
        }
        if (criteria.getEntryDate() != null) {
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getEntryDate(),
                    ImportVendorTemTransactions_.entryDate
                )
            );
        }
        if (criteria.getStorageUnit() != null) {
            specification = specification.and(
                buildStringSpecification(
                    criteria.getStorageUnit(),
                    ImportVendorTemTransactions_.storageUnit
                )
            );
        }
        if (criteria.getTemIdentificationScenarioId() != null) {
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getTemIdentificationScenarioId(),
                    ImportVendorTemTransactions_.temIdentificationScenarioId
                )
            );
        }
        if (criteria.status() != null) {
            specification = specification.and(
                buildStringSpecification(
                    criteria.status(),
                    ImportVendorTemTransactions_.status
                )
            );
        }
        if (criteria.panaSendStatus() != null) {
            specification = specification.and(
                buildSpecification(
                    criteria.panaSendStatus(),
                    ImportVendorTemTransactions_.panaSendStatus
                )
            );
        }
        if (criteria.getCreatedBy() != null) {
            specification = specification.and(
                buildStringSpecification(
                    criteria.getCreatedBy(),
                    ImportVendorTemTransactions_.createdBy
                )
            );
        }
        if (criteria.getCreatedAt() != null) {
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getCreatedAt(),
                    ImportVendorTemTransactions_.createdAt
                )
            );
        }
        return specification;
    }
}
