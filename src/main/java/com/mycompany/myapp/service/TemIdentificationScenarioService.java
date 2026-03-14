package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.TemIdentificationScenarioDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TemIdentificationScenario}.
 */
public interface TemIdentificationScenarioService {
    /**
     * Save a temIdentificationScenario.
     *
     * @param temIdentificationScenarioDTO the entity to save.
     * @return the persisted entity.
     */
    TemIdentificationScenarioDTO save(
        TemIdentificationScenarioDTO temIdentificationScenarioDTO
    );

    /**
     * Updates a temIdentificationScenario.
     *
     * @param temIdentificationScenarioDTO the entity to update.
     * @return the persisted entity.
     */
    TemIdentificationScenarioDTO update(
        TemIdentificationScenarioDTO temIdentificationScenarioDTO
    );

    /**
     * Partially updates a temIdentificationScenario.
     *
     * @param temIdentificationScenarioDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TemIdentificationScenarioDTO> partialUpdate(
        TemIdentificationScenarioDTO temIdentificationScenarioDTO
    );

    /**
     * Get the "id" temIdentificationScenario.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TemIdentificationScenarioDTO> findOne(Long id);

    /**
     * Delete the "id" temIdentificationScenario.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
