package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.SplitInventoryDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.SplitInventory}.
 */
public interface SplitInventoryService {
    /**
     * Save a splitInventory.
     *
     * @param splitInventoryDTO the entity to save.
     * @return the persisted entity.
     */
    SplitInventoryDTO save(SplitInventoryDTO splitInventoryDTO);

    /**
     * Updates a splitInventory.
     *
     * @param splitInventoryDTO the entity to update.
     * @return the persisted entity.
     */
    SplitInventoryDTO update(SplitInventoryDTO splitInventoryDTO);

    /**
     * Partially updates a splitInventory.
     *
     * @param splitInventoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SplitInventoryDTO> partialUpdate(
        SplitInventoryDTO splitInventoryDTO
    );

    /**
     * Get the "id" splitInventory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SplitInventoryDTO> findOne(Long id);

    /**
     * Delete the "id" splitInventory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
