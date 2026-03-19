package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.PoImportTemDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.PoImportTem}.
 */
public interface PoImportTemService {
    /**
     * Save a poImportTem.
     *
     * @param poImportTemDTO the entity to save.
     * @return the persisted entity.
     */
    PoImportTemDTO save(PoImportTemDTO poImportTemDTO);

    /**
     * Updates a poImportTem.
     *
     * @param poImportTemDTO the entity to update.
     * @return the persisted entity.
     */
    PoImportTemDTO update(PoImportTemDTO poImportTemDTO);

    /**
     * Partially updates a poImportTem.
     *
     * @param poImportTemDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PoImportTemDTO> partialUpdate(PoImportTemDTO poImportTemDTO);

    /**
     * Get the "id" poImportTem.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PoImportTemDTO> findOne(Long id);

    /**
     * Delete the "id" poImportTem.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
