package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.Workshop;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Workshop entity using
 * partner3 datasource.
 */
@SuppressWarnings("unused")
@Repository
public interface Partner3WorkshopRepository
    extends JpaRepository<Workshop, Long> {
    @Query(
        "SELECT DISTINCT w FROM Workshop w LEFT JOIN FETCH w.branchs b LEFT JOIN FETCH b.productionTeams"
    )
    List<Workshop> findAllWithHierarchy();
}
