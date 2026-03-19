package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.PoDetailDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.PoDetail}.
 */
public interface PoDetailService {
    /**
     * Save a poDetail.
     *
     * @param poDetailDTO the entity to save.
     * @return the persisted entity.
     */
    PoDetailDTO save(PoDetailDTO poDetailDTO);

    /**
     * Updates a poDetail.
     *
     * @param poDetailDTO the entity to update.
     * @return the persisted entity.
     */
    PoDetailDTO update(PoDetailDTO poDetailDTO);

    /**
     * Partially updates a poDetail.
     *
     * @param poDetailDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PoDetailDTO> partialUpdate(PoDetailDTO poDetailDTO);

    /**
     * Get the "id" poDetail.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PoDetailDTO> findOne(Long id);

    /**
     * Delete the "id" poDetail.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Save multiple poDetails.
     *
     * @param poDetailDTOs the list of entities to save.
     * @return the list of persisted entities.
     */
    List<PoDetailDTO> saveAll(List<PoDetailDTO> poDetailDTOs);
}
