package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.WarehouseNoteInfoDetail;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoDetailRepository;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDetailDTO;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoDetailMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing
 * {@link com.mycompany.myapp.domain.WarehouseNoteInfoDetail}.
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
            "Request to save WarehouseNoteInfoDetail : {}",
            warehouseStampInfoDetailDTO
        );

        // Set created_at timestamp for new records
        if (warehouseStampInfoDetailDTO.getCreatedAt() == null) {
            warehouseStampInfoDetailDTO.setCreatedAt(Instant.now());
        }

        WarehouseNoteInfoDetail warehouseStampInfoDetail =
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
            "Request to update WarehouseNoteInfoDetail : {}",
            warehouseStampInfoDetailDTO
        );
        WarehouseNoteInfoDetail warehouseStampInfoDetail =
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
            "Request to partially update WarehouseNoteInfoDetail : {}",
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
        LOG.debug("Request to get WarehouseNoteInfoDetail : {}", id);
        return warehouseStampInfoDetailRepository
            .findById(id)
            .map(warehouseStampInfoDetailMapper::toDto);
    }

    /**
     * Get all warehouseStampInfoDetails by maLenhSanXuatId.
     *
     * @param maLenhSanXuatId the id of the WarehouseNoteInfo.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WarehouseStampInfoDetailDTO> findByMaLenhSanXuatId(
        Long maLenhSanXuatId
    ) {
        LOG.debug(
            "Request to get WarehouseNoteInfoDetails by maLenhSanXuatId : {}",
            maLenhSanXuatId
        );
        return warehouseStampInfoDetailRepository
            .findByMaLenhSanXuatId(maLenhSanXuatId)
            .stream()
            .map(warehouseStampInfoDetailMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Delete the warehouseStampInfoDetail by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete WarehouseNoteInfoDetail : {}", id);
        warehouseStampInfoDetailRepository.deleteById(id);
    }

    /**
     * Batch delete warehouseStampInfoDetails by list of DTOs.
     *
     * @param warehouseStampInfoDetailDTOs the list of entities to delete.
     */
    public void batchDelete(
        List<WarehouseStampInfoDetailDTO> warehouseStampInfoDetailDTOs
    ) {
        LOG.debug(
            "Request to batch delete WarehouseStampInfoDetails : {}",
            warehouseStampInfoDetailDTOs
        );

        for (WarehouseStampInfoDetailDTO dto : warehouseStampInfoDetailDTOs) {
            if (dto.getId() != null) {
                warehouseStampInfoDetailRepository.deleteById(dto.getId());
            }
        }
    }

    /**
     * Batch update warehouse stamp info details by ID.
     *
     * @param warehouseStampInfoDetailDTOs the list of entities to update.
     * @return the list of updated entities.
     */
    public java.util.List<WarehouseStampInfoDetailDTO> batchUpdate(
        java.util.List<WarehouseStampInfoDetailDTO> warehouseStampInfoDetailDTOs
    ) {
        LOG.debug(
            "Request to batch update WarehouseStampInfoDetails : {}",
            warehouseStampInfoDetailDTOs
        );

        java.util.List<WarehouseStampInfoDetailDTO> updatedDTOs =
            new java.util.ArrayList<>();

        for (WarehouseStampInfoDetailDTO dto : warehouseStampInfoDetailDTOs) {
            if (dto.getId() == null) {
                LOG.warn(
                    "WarehouseNoteInfoDetail with null ID, skipping update"
                );
                continue;
            }

            // Find existing warehouse detail by ID
            Optional<WarehouseNoteInfoDetail> existingDetailOpt =
                warehouseStampInfoDetailRepository.findById(dto.getId());

            if (existingDetailOpt.isPresent()) {
                WarehouseNoteInfoDetail existingDetail =
                    existingDetailOpt.get();

                // Update all provided fields
                if (dto.getReelId() != null) {
                    existingDetail.setReelId(dto.getReelId());
                }
                if (dto.getPartNumber() != null) {
                    existingDetail.setPartNumber(dto.getPartNumber());
                }
                if (dto.getVendor() != null) {
                    existingDetail.setVendor(dto.getVendor());
                }
                if (dto.getLot() != null) {
                    existingDetail.setLot(dto.getLot());
                }
                if (dto.getUserData1() != null) {
                    existingDetail.setUserData1(dto.getUserData1());
                }
                if (dto.getUserData2() != null) {
                    existingDetail.setUserData2(dto.getUserData2());
                }
                if (dto.getUserData3() != null) {
                    existingDetail.setUserData3(dto.getUserData3());
                }
                if (dto.getUserData4() != null) {
                    existingDetail.setUserData4(dto.getUserData4());
                }
                if (dto.getUserData5() != null) {
                    existingDetail.setUserData5(dto.getUserData5());
                }
                if (dto.getInitialQuantity() != null) {
                    existingDetail.setInitialQuantity(dto.getInitialQuantity());
                }
                if (dto.getMsdLevel() != null) {
                    existingDetail.setMsdLevel(dto.getMsdLevel());
                }
                if (dto.getMsdInitialFloorTime() != null) {
                    existingDetail.setMsdInitialFloorTime(
                        dto.getMsdInitialFloorTime()
                    );
                }
                if (dto.getMsdBagSealDate() != null) {
                    existingDetail.setMsdBagSealDate(dto.getMsdBagSealDate());
                }
                if (dto.getMarketUsage() != null) {
                    existingDetail.setMarketUsage(dto.getMarketUsage());
                }
                if (dto.getQuantityOverride() != null) {
                    existingDetail.setQuantityOverride(
                        dto.getQuantityOverride()
                    );
                }
                if (dto.getShelfTime() != null) {
                    existingDetail.setShelfTime(dto.getShelfTime());
                }
                if (dto.getSpMaterialName() != null) {
                    existingDetail.setSpMaterialName(dto.getSpMaterialName());
                }
                if (dto.getWarningLimit() != null) {
                    existingDetail.setWarningLimit(dto.getWarningLimit());
                }
                if (dto.getMaximumLimit() != null) {
                    existingDetail.setMaximumLimit(dto.getMaximumLimit());
                }
                if (dto.getComments() != null) {
                    existingDetail.setComments(dto.getComments());
                }
                if (dto.getWarmupTime() != null) {
                    existingDetail.setWarmupTime(dto.getWarmupTime());
                }
                if (dto.getStorageUnit() != null) {
                    existingDetail.setStorageUnit(dto.getStorageUnit());
                }
                if (dto.getSubStorageUnit() != null) {
                    existingDetail.setSubStorageUnit(dto.getSubStorageUnit());
                }
                if (dto.getLocationOverride() != null) {
                    existingDetail.setLocationOverride(
                        dto.getLocationOverride()
                    );
                }
                if (dto.getExpirationDate() != null) {
                    existingDetail.setExpirationDate(dto.getExpirationDate());
                }
                if (dto.getManufacturingDate() != null) {
                    existingDetail.setManufacturingDate(
                        dto.getManufacturingDate()
                    );
                }
                if (dto.getPartClass() != null) {
                    existingDetail.setPartClass(dto.getPartClass());
                }
                if (dto.getSapCode() != null) {
                    existingDetail.setSapCode(dto.getSapCode());
                }
                if (dto.getMaLenhSanXuatId() != null) {
                    existingDetail.setmaLenhSanXuatId(dto.getMaLenhSanXuatId());
                }
                if (dto.getLenhSanXuatId() != null) {
                    existingDetail.setLenhSanXuatId(dto.getLenhSanXuatId());
                }
                if (dto.getTrangThai() != null) {
                    existingDetail.setTrangThai(dto.getTrangThai());
                }
                if (dto.getChecked() != null) {
                    existingDetail.setChecked(dto.getChecked());
                }
                if (dto.getCreatedAt() != null) {
                    existingDetail.setCreatedAt(dto.getCreatedAt());
                }
                if (dto.getWmsSendStatus() != null) {
                    existingDetail.setWmsSendStatus(dto.getWmsSendStatus());
                }

                // Save the updated entity
                WarehouseNoteInfoDetail savedDetail =
                    warehouseStampInfoDetailRepository.save(existingDetail);
                updatedDTOs.add(
                    warehouseStampInfoDetailMapper.toDto(savedDetail)
                );
            } else {
                LOG.warn(
                    "WarehouseNoteInfoDetail with ID {} not found, skipping update",
                    dto.getId()
                );
            }
        }

        return updatedDTOs;
    }
}
