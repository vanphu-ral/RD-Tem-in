package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.ProductionTeam;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductionTeam entity using
 * partner3 datasource.
 */
@SuppressWarnings("unused")
@Repository
public interface Partner3ProductionTeamRepository
    extends JpaRepository<ProductionTeam, Long> {}
