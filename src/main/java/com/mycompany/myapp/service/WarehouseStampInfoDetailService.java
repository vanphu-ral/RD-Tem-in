package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.WarehouseStampInfoDetail;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoDetailRepository;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDetailDTO;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoDetailMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing
 * {@link com.mycompany.myapp.domain.WarehouseStampInfoDetail}.
 */
@Service
@Transactional("partner3TransactionManager")
public class WarehouseStampInfoDetailService {

    private static final Logger LOG = LoggerFactory.getLogger(
        WarehouseStampInfoDetailService.class
    );

    private final Partner3WarehouseStampInfoDetailRepository warehouseStampInfoDetailRepository;

    private final WarehouseStampInfoDetailMapper warehouseStampInfoDetailMapper;

    public WarehouseStampInfoDetailService(
        Partner3WarehouseStampInfoDetailRepository warehouseStampInfoDetailRepository,
        WarehouseStampInfoDetailMapper warehouseStampInfoDetailMapper
    ) {
        this.warehouseStampInfoDetailRepository =
            warehouseStampInfoDetailRepository;
        this.warehouseStampInfoDetailMapper = warehouseStampInfoDetailMapper;
    }

    /**
     * Save a warehouseStampInfoDetail.
     *
     * @param warehouseStampInfoDetailDTO the entity to save.
     * @return the persisted entity.
     */
    public WarehouseStampInfoDetailDTO save(
        WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO
    ) {
        LOG.debug(
            "Request to save WarehouseStampInfoDetail : {}",
            warehouseStampInfoDetailDTO
        );
        WarehouseStampInfoDetail warehouseStampInfoDetail =
            warehouseStampInfoDetailMapper.toEntity(
                warehouseStampInfoDetailDTO
            );
        warehouseStampInfoDetail = warehouseStampInfoDetailRepository.save(
            warehouseStampInfoDetail
        );
        return warehouseStampInfoDetailMapper.toDto(warehouseStampInfoDetail);
    }

    /**
     * Update a warehouseStampInfoDetail.
     *
     * @param warehouseStampInfoDetailDTO the entity to save.
     * @return the persisted entity.
     */
    public WarehouseStampInfoDetailDTO update(
        WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO
    ) {
        LOG.debug(
            "Request to update WarehouseStampInfoDetail : {}",
            warehouseStampInfoDetailDTO
        );
        WarehouseStampInfoDetail warehouseStampInfoDetail =
            warehouseStampInfoDetailMapper.toEntity(
                warehouseStampInfoDetailDTO
            );
        warehouseStampInfoDetail = warehouseStampInfoDetailRepository.save(
            warehouseStampInfoDetail
        );
        return warehouseStampInfoDetailMapper.toDto(warehouseStampInfoDetail);
    }

    /**
     * Partially update a warehouseStampInfoDetail.
     *
     * @param warehouseStampInfoDetailDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WarehouseStampInfoDetailDTO> partialUpdate(
        WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO
    ) {
        LOG.debug(
            "Request to partially update WarehouseStampInfoDetail : {}",
            warehouseStampInfoDetailDTO
        );

        return warehouseStampInfoDetailRepository
            .findById(warehouseStampInfoDetailDTO.getId())
            .map(existingWarehouseStampInfoDetail -> {
                warehouseStampInfoDetailMapper.partialUpdate(
                    existingWarehouseStampInfoDetail,
                    warehouseStampInfoDetailDTO
                );

                return existingWarehouseStampInfoDetail;
            })
            .map(warehouseStampInfoDetailRepository::save)
            .map(warehouseStampInfoDetailMapper::toDto);
    }

    /**
     * Get all the warehouseStampInfoDetails.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WarehouseStampInfoDetailDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all WarehouseStampInfoDetails");
        return warehouseStampInfoDetailRepository
            .findAll(pageable)
            .map(warehouseStampInfoDetailMapper::toDto);
    }

    /**
     * Get one warehouseStampInfoDetail by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WarehouseStampInfoDetailDTO> findOne(Long id) {
        LOG.debug("Request to get WarehouseStampInfoDetail : {}", id);
        return warehouseStampInfoDetailRepository
            .findById(id)
            .map(warehouseStampInfoDetailMapper::toDto);
    }

    /**
     * Delete the warehouseStampInfoDetail by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete WarehouseStampInfoDetail : {}", id);
        warehouseStampInfoDetailRepository.deleteById(id);
    }
}
