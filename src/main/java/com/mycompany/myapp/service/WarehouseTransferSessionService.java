package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.WarehouseTransferSessionDTO;
import com.mycompany.myapp.service.dto.WarehouseTransferSessionDetailDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.WarehouseTransferSession}.
 */
public interface WarehouseTransferSessionService {
    /**
     * Save a warehouseTransferSession.
     *
     * @param warehouseTransferSessionDTO the entity to save.
     * @return the persisted entity.
     */
    WarehouseTransferSessionDTO save(
        WarehouseTransferSessionDTO warehouseTransferSessionDTO
    );

    /**
     * Updates a warehouseTransferSession.
     *
     * @param warehouseTransferSessionDTO the entity to update.
     * @return the persisted entity.
     */
    WarehouseTransferSessionDTO update(
        WarehouseTransferSessionDTO warehouseTransferSessionDTO
    );

    /**
     * Partially updates a warehouseTransferSession.
     *
     * @param warehouseTransferSessionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<WarehouseTransferSessionDTO> partialUpdate(
        WarehouseTransferSessionDTO warehouseTransferSessionDTO
    );

    /**
     * Get the "id" warehouseTransferSession.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<WarehouseTransferSessionDTO> findOne(Long id);

    /**
     * Get detailed information for a warehouseTransferSession.
     *
     * @param id the id of the entity.
     * @return the detailed entity.
     */
    Optional<WarehouseTransferSessionDetailDTO> findDetailById(Long id);

    /**
     * Delete the "id" warehouseTransferSession.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
