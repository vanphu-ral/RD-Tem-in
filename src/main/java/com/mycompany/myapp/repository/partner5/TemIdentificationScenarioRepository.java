package com.mycompany.myapp.repository.partner5;

import com.mycompany.myapp.domain.TemIdentificationScenario;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TemIdentificationScenario entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TemIdentificationScenarioRepository
    extends
        JpaRepository<TemIdentificationScenario, Long>,
        JpaSpecificationExecutor<TemIdentificationScenario> {}
