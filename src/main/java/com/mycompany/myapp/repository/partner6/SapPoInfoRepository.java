package com.mycompany.myapp.repository.partner6;

import com.mycompany.myapp.domain.SapPoInfo;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SapPoInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SapPoInfoRepository
    extends
        JpaRepository<SapPoInfo, Long>, JpaSpecificationExecutor<SapPoInfo> {
    /**
     * Find all SapPoInfo records by OPOR_DocEntry.
     *
     * @param oporDocEntry the OPOR_DocEntry value to search for
     * @return list of SapPoInfo entities matching the criteria
     */
    List<SapPoInfo> findByOporDocEntry(String oporDocEntry);
}
