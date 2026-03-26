package com.mycompany.myapp.repository.partner5;

import com.mycompany.myapp.domain.ImportVendorTemTransactions;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ImportVendorTemTransactions entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ImportVendorTemTransactionsRepository
    extends
        JpaRepository<ImportVendorTemTransactions, Long>,
        JpaSpecificationExecutor<ImportVendorTemTransactions> {
    @Modifying
    @Query(
        "DELETE FROM ImportVendorTemTransactions t WHERE t.poImportTemId = :poImportTemId"
    )
    void deleteByPoImportTemId(@Param("poImportTemId") Long poImportTemId);

    /**
     * Dùng cho API Chi tiết (Get by ID).
     * EntityGraph sẽ ép Hibernate thực hiện LEFT JOIN để lấy luôn poDetails
     * và vendorTemDetails trong 1 câu Query duy nhất.
     */
    @EntityGraph(attributePaths = { "poDetails", "poDetails.vendorTemDetails" })
    Optional<ImportVendorTemTransactions> findOneWithDetailsById(Long id);

    /**
     * Tìm theo poNumber (giữ nguyên hàm bạn đang có).
     */
    List<ImportVendorTemTransactions> findByPoNumber(String poNumber);

    /**
     * Dùng cho API Danh sách.
     * KHÔNG dùng EntityGraph ở đây để tránh lỗi phân trang trên RAM.
     * Lỗi Lazy ở đây sẽ được xử lý bằng @BatchSize trong file Entity.
     */
    @Override
    Page<ImportVendorTemTransactions> findAll(
        Specification<ImportVendorTemTransactions> spec,
        Pageable pageable
    );
}
