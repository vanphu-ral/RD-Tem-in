package com.mycompany.myapp.repository.partner5;

import com.mycompany.myapp.domain.PoDetail;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PoDetail entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PoDetailRepository
    extends JpaRepository<PoDetail, Long>, JpaSpecificationExecutor<PoDetail> {
    /**
     * Find all poDetails by import vendor tem transactions id.
     *
     * @param importVendorTemTransactionsId the id of the import vendor tem
     *                                      transactions.
     * @return the list of poDetails.
     */
    List<PoDetail> findByImportVendorTemTransactionsId(
        Long importVendorTemTransactionsId
    );
}
