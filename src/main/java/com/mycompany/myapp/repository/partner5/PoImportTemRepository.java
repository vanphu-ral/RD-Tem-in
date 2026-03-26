package com.mycompany.myapp.repository.partner5;

import com.mycompany.myapp.domain.PoImportTem;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PoImportTem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PoImportTemRepository
    extends
        JpaRepository<PoImportTem, Long>,
        JpaSpecificationExecutor<PoImportTem> {
    @EntityGraph(
        attributePaths = {
            "importVendorTemTransactions",
            "importVendorTemTransactions.poDetails",
            "importVendorTemTransactions.poDetails.vendorTemDetails",
        }
    )
    Optional<PoImportTem> findWithTransactionsById(Long id);

    /**
     * Find by id with all nested relationships for detail view.
     * Fetches: importVendorTemTransactions -> poDetails -> vendorTemDetails
     */
    @Query(
        "SELECT p FROM PoImportTem p " +
        "LEFT JOIN FETCH p.importVendorTemTransactions t " +
        "LEFT JOIN FETCH t.poDetails pd " +
        "LEFT JOIN FETCH pd.vendorTemDetails " +
        "WHERE p.id = :id"
    )
    Optional<PoImportTem> findDetailById(@Param("id") Long id);

    // @EntityGraph(attributePaths = "importVendorTemTransactions")
    // Page<PoImportTem> findAllWithTransactions(org.springframework.data.jpa.domain.Specification<PoImportTem> spec,
    //                 Pageable pageable);

    // @EntityGraph(attributePaths = "importVendorTemTransactions")
    @Override
    Page<PoImportTem> findAll(
        org.springframework.data.jpa.domain.Specification<PoImportTem> spec,
        Pageable pageable
    );
}
