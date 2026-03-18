package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDTO;
import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDetailDTO;
import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsWithPoDetailsRequestDTO;
import java.util.Optional;

/**
 * Service Interface for managing
 * {@link com.mycompany.myapp.domain.ImportVendorTemTransactions}.
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
     * Get the "id" importVendorTemTransactions with all related data (poDetails and
     * vendorTemDetails).
     *
     * @param id the id of the entity.
     * @return the entity with all related data.
     */
    Optional<ImportVendorTemTransactionsDetailDTO> findOneWithDetails(Long id);

    /**
     * Create a new importVendorTemTransactions along with poDetails.
     * This method saves both the transaction and its associated poDetails in a
     * single
     * request, linking them together.
     *
     * @param requestDTO the request containing transaction and poDetails data.
     * @return the persisted entity with all related data.
     */
    ImportVendorTemTransactionsDetailDTO createWithPoDetails(
        ImportVendorTemTransactionsWithPoDetailsRequestDTO requestDTO
    );

    /**
     * Delete the "id" importVendorTemTransactions.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
