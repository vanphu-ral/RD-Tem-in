package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.PoImportTem;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PoImportTem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PoImportTemRepository
    extends
        JpaRepository<PoImportTem, Long>,
        JpaSpecificationExecutor<PoImportTem> {}
