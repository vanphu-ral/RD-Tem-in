package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.OwtqWarehouseTransferSessionDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.OwtqWarehouseTransferSession}.
 */
public interface OwtqWarehouseTransferSessionService {
    /**
     * Save a owtqWarehouseTransferSession.
     *
     * @param owtqWarehouseTransferSessionDTO the entity to save.
     * @return the persisted entity.
     */
    OwtqWarehouseTransferSessionDTO save(
        OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO
    );

    /**
     * Updates a owtqWarehouseTransferSession.
     *
     * @param owtqWarehouseTransferSessionDTO the entity to update.
     * @return the persisted entity.
     */
    OwtqWarehouseTransferSessionDTO update(
        OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO
    );

    /**
     * Partially updates a owtqWarehouseTransferSession.
     *
     * @param owtqWarehouseTransferSessionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<OwtqWarehouseTransferSessionDTO> partialUpdate(
        OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO
    );

    /**
     * Get all the owtqWarehouseTransferSessions.
     *
     * @return the list of entities.
     */
    List<OwtqWarehouseTransferSessionDTO> findAll();

    /**
     * Get the "id" owtqWarehouseTransferSession.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OwtqWarehouseTransferSessionDTO> findOne(Long id);

    /**
     * Delete the "id" owtqWarehouseTransferSession.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
