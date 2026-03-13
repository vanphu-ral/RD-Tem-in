package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.VendorTemDetailDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.VendorTemDetail}.
 */
public interface VendorTemDetailService {
    /**
     * Save a vendorTemDetail.
     *
     * @param vendorTemDetailDTO the entity to save.
     * @return the persisted entity.
     */
    VendorTemDetailDTO save(VendorTemDetailDTO vendorTemDetailDTO);

    /**
     * Updates a vendorTemDetail.
     *
     * @param vendorTemDetailDTO the entity to update.
     * @return the persisted entity.
     */
    VendorTemDetailDTO update(VendorTemDetailDTO vendorTemDetailDTO);

    /**
     * Partially updates a vendorTemDetail.
     *
     * @param vendorTemDetailDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<VendorTemDetailDTO> partialUpdate(
        VendorTemDetailDTO vendorTemDetailDTO
    );

    /**
     * Get the "id" vendorTemDetail.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VendorTemDetailDTO> findOne(Long id);

    /**
     * Delete the "id" vendorTemDetail.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
