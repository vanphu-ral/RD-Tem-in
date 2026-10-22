package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.WarehouseTransferDetailDTO;
import com.mycompany.myapp.service.dto.WarehouseTransferSummaryDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.WarehouseTransferDetail}.
 */
public interface WarehouseTransferDetailService {
    /**
     * Save a warehouseTransferDetail.
     *
     * @param warehouseTransferDetailDTO the entity to save.
     * @return the persisted entity.
     */
    WarehouseTransferDetailDTO save(
        WarehouseTransferDetailDTO warehouseTransferDetailDTO
    );

    /**
     * Updates a warehouseTransferDetail.
     *
     * @param warehouseTransferDetailDTO the entity to update.
     * @return the persisted entity.
     */
    WarehouseTransferDetailDTO update(
        WarehouseTransferDetailDTO warehouseTransferDetailDTO
    );

    /**
     * Partially updates a warehouseTransferDetail.
     *
     * @param warehouseTransferDetailDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<WarehouseTransferDetailDTO> partialUpdate(
        WarehouseTransferDetailDTO warehouseTransferDetailDTO
    );

    /**
     * Get all the warehouseTransferDetails.
     *
     * @return the list of entities.
     */
    List<WarehouseTransferDetailDTO> findAll();

    /**
     * Get the "id" warehouseTransferDetail.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<WarehouseTransferDetailDTO> findOne(Long id);

    /**
     * Delete the "id" warehouseTransferDetail.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    WarehouseTransferSummaryDTO getSummaryByOwtqDocEntry(Long owtqDocEntry);
}
