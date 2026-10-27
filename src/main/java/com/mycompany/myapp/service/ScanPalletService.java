package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.ScanPalletDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.ScanPallet}.
 */
public interface ScanPalletService {
    /**
     * Save a scanPallet.
     *
     * @param scanPalletDTO the entity to save.
     * @return the persisted entity.
     */
    ScanPalletDTO save(ScanPalletDTO scanPalletDTO);

    /**
     * Updates a scanPallet.
     *
     * @param scanPalletDTO the entity to update.
     * @return the persisted entity.
     */
    ScanPalletDTO update(ScanPalletDTO scanPalletDTO);

    /**
     * Partially updates a scanPallet.
     *
     * @param scanPalletDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ScanPalletDTO> partialUpdate(ScanPalletDTO scanPalletDTO);

    /**
     * Get all the scanPallets.
     *
     * @return the list of entities.
     */
    List<ScanPalletDTO> findAll();

    /**
     * Get the "id" scanPallet.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ScanPalletDTO> findOne(Long id);

    /**
     * Delete the "id" scanPallet.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
