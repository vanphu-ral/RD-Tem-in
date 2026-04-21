package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDTO;
import com.mycompany.myapp.service.dto.PoImportRequestDTO;
import com.mycompany.myapp.service.dto.PoImportResponseDTO;
import com.mycompany.myapp.service.dto.PoImportTemDTO;
import com.mycompany.myapp.service.dto.PoImportTemDetailDTO;
import java.util.Optional;

/**
 * Service Interface for managing
 * {@link com.mycompany.myapp.domain.PoImportTem}.
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

    /**
     * Process PO Import request.
     * Supports two cases:
     * - Case 1: poNumber is null - creates parent record in po_import_tem and child
     * in import_vendor_tem_transactions
     * - Case 2: poNumber exists - searches for existing records and returns data if
     * created today
     *
     * @param request the PO import request payload
     * @return the response containing either newly created records (Case 1) or
     *         retrieved data (Case 2)
     */
    PoImportResponseDTO processPoImport(PoImportRequestDTO request);

    /**
     * Get the full detail of poImportTem with all nested relationships.
     *
     * @param id the id of the poImportTem.
     * @return the entity with all nested data (importVendorTemTransactions ->
     *         poDetails -> vendorTemDetails).
     */
    Optional<PoImportTemDetailDTO> findDetailById(Long id);

    /**
     * Process update for an existing ImportVendorTemTransactions.
     * Updates PO information and supplements po_detail by fetching from partner6 WorkZone (SAP PO Info).
     *
     * @param transactionDTO the ImportVendorTemTransactions to update
     * @return the response containing updated transaction with poDetails
     */
    PoImportResponseDTO processImportVendorTemTransactionUpdate(
        ImportVendorTemTransactionsDTO transactionDTO
    );
}
