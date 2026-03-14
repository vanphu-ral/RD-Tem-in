package com.mycompany.myapp.repository.partner5;

import com.mycompany.myapp.domain.PoDetail;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PoDetail entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PoDetailRepository
    extends JpaRepository<PoDetail, Long>, JpaSpecificationExecutor<PoDetail> {}
