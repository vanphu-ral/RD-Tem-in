package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.ImportVendorTemTransactions}.
 */
public interface ImportVendorTemTransactionsService {
    /**
     * Save a importVendorTemTransactions.
     *
     * @param importVendorTemTransactionsDTO the entity to save.
     * @return the persisted entity.
     */
    ImportVendorTemTransactionsDTO save(
        ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO
    );

    /**
     * Updates a importVendorTemTransactions.
     *
     * @param importVendorTemTransactionsDTO the entity to update.
     * @return the persisted entity.
     */
    ImportVendorTemTransactionsDTO update(
        ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO
    );

    /**
     * Partially updates a importVendorTemTransactions.
     *
     * @param importVendorTemTransactionsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ImportVendorTemTransactionsDTO> partialUpdate(
        ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO
    );

    /**
     * Get the "id" importVendorTemTransactions.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ImportVendorTemTransactionsDTO> findOne(Long id);

    /**
     * Delete the "id" importVendorTemTransactions.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
