package com.mycompany.myapp.repository.partner6;

import com.mycompany.myapp.domain.SapPoInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SapPoInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SapPoInfoRepository
    extends
        JpaRepository<SapPoInfo, Long>, JpaSpecificationExecutor<SapPoInfo> {}
