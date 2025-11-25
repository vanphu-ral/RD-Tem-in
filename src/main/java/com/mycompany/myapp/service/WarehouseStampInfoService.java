package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.WarehouseStampInfo;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoRepository;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDTO;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing
 * {@link com.mycompany.myapp.domain.WarehouseStampInfo}.
 */
@Service
@Transactional("partner3TransactionManager")
public class WarehouseStampInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(
        WarehouseStampInfoService.class
    );

    private final Partner3WarehouseStampInfoRepository warehouseStampInfoRepository;

    private final WarehouseStampInfoMapper warehouseStampInfoMapper;

    public WarehouseStampInfoService(
        Partner3WarehouseStampInfoRepository warehouseStampInfoRepository,
        WarehouseStampInfoMapper warehouseStampInfoMapper
    ) {
        this.warehouseStampInfoRepository = warehouseStampInfoRepository;
        this.warehouseStampInfoMapper = warehouseStampInfoMapper;
    }

    /**
     * Save a warehouseStampInfo.
     *
     * @param warehouseStampInfoDTO the entity to save.
     * @return the persisted entity.
     */
    public WarehouseStampInfoDTO save(
        WarehouseStampInfoDTO warehouseStampInfoDTO
    ) {
        LOG.debug(
            "Request to save WarehouseStampInfo : {}",
            warehouseStampInfoDTO
        );
        WarehouseStampInfo warehouseStampInfo =
            warehouseStampInfoMapper.toEntity(warehouseStampInfoDTO);
        warehouseStampInfo = warehouseStampInfoRepository.save(
            warehouseStampInfo
        );
        return warehouseStampInfoMapper.toDto(warehouseStampInfo);
    }

    /**
     * Update a warehouseStampInfo.
     *
     * @param warehouseStampInfoDTO the entity to save.
     * @return the persisted entity.
     */
    public WarehouseStampInfoDTO update(
        WarehouseStampInfoDTO warehouseStampInfoDTO
    ) {
        LOG.debug(
            "Request to update WarehouseStampInfo : {}",
            warehouseStampInfoDTO
        );
        WarehouseStampInfo warehouseStampInfo =
            warehouseStampInfoMapper.toEntity(warehouseStampInfoDTO);
        warehouseStampInfo = warehouseStampInfoRepository.save(
            warehouseStampInfo
        );
        return warehouseStampInfoMapper.toDto(warehouseStampInfo);
    }

    /**
     * Partially update a warehouseStampInfo.
     *
     * @param warehouseStampInfoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WarehouseStampInfoDTO> partialUpdate(
        WarehouseStampInfoDTO warehouseStampInfoDTO
    ) {
        LOG.debug(
            "Request to partially update WarehouseStampInfo : {}",
            warehouseStampInfoDTO
        );

        return warehouseStampInfoRepository
            .findById(warehouseStampInfoDTO.getId())
            .map(existingWarehouseStampInfo -> {
                warehouseStampInfoMapper.partialUpdate(
                    existingWarehouseStampInfo,
                    warehouseStampInfoDTO
                );

                return existingWarehouseStampInfo;
            })
            .map(warehouseStampInfoRepository::save)
            .map(warehouseStampInfoMapper::toDto);
    }

    /**
     * Get all the warehouseStampInfos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WarehouseStampInfoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all WarehouseStampInfos");
        return warehouseStampInfoRepository
            .findAll(pageable)
            .map(warehouseStampInfoMapper::toDto);
    }

    /**
     * Get one warehouseStampInfo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WarehouseStampInfoDTO> findOne(Long id) {
        LOG.debug("Request to get WarehouseStampInfo : {}", id);
        return warehouseStampInfoRepository
            .findById(id)
            .map(warehouseStampInfoMapper::toDto);
    }

    /**
     * Get all warehouseStampInfos by createBy.
     *
     * @param createBy the createBy to filter by.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WarehouseStampInfoDTO> findByCreateBy(String createBy) {
        LOG.debug(
            "Request to get WarehouseStampInfos by createBy : {}",
            createBy
        );
        return warehouseStampInfoRepository
            .findBycreateBy(createBy)
            .stream()
            .map(warehouseStampInfoMapper::toDto)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get all warehouseStampInfos by approverBy.
     *
     * @param approverBy the approverBy to filter by.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WarehouseStampInfoDTO> findByApproverBy(String approverBy) {
        LOG.debug(
            "Request to get WarehouseStampInfos by approverBy : {}",
            approverBy
        );
        return warehouseStampInfoRepository
            .findByApproverBy(approverBy)
            .stream()
            .map(warehouseStampInfoMapper::toDto)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Delete the warehouseStampInfo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete WarehouseStampInfo : {}", id);
        warehouseStampInfoRepository.deleteById(id);
    }
}
