package com.mycompany.myapp.repository.partner5;

import com.mycompany.myapp.domain.ImportVendorTemTransactions;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ImportVendorTemTransactions entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ImportVendorTemTransactionsRepository
    extends
        JpaRepository<ImportVendorTemTransactions, Long>,
        JpaSpecificationExecutor<ImportVendorTemTransactions> {}
