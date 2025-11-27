package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.PalletInforDetail;
import com.mycompany.myapp.domain.WarehouseNoteInfoDetail;
import com.mycompany.myapp.repository.PalletInforDetailRepository;
import com.mycompany.myapp.repository.WarehouseStampInfoDetailRepository;
import com.mycompany.myapp.repository.partner3.Partner3PalletInforDetailRepository;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoDetailRepository;
import com.mycompany.myapp.service.dto.CombinedPalletWarehouseDTO;
import com.mycompany.myapp.service.dto.MaxSerialResponseDTO;
import com.mycompany.myapp.service.dto.PalletInforDetailDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDetailDTO;
import com.mycompany.myapp.service.mapper.PalletInforDetailMapper;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoDetailMapper;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing
 * {@link com.mycompany.myapp.domain.PalletInforDetail}.
 */
@Service
@Transactional
public class PalletInforDetailService {

    private static final Logger LOG = LoggerFactory.getLogger(
        PalletInforDetailService.class
    );

    private final Partner3PalletInforDetailRepository palletInforDetailRepository;

    private final PalletInforDetailMapper palletInforDetailMapper;

    private final Partner3WarehouseStampInfoDetailRepository warehouseStampInfoDetailRepository;

    private final WarehouseStampInfoDetailMapper warehouseStampInfoDetailMapper;

    private final PalletInforDetailRepository mainPalletRepository;

    private final WarehouseStampInfoDetailRepository mainWarehouseRepository;

    public PalletInforDetailService(
        Partner3PalletInforDetailRepository palletInforDetailRepository,
        PalletInforDetailMapper palletInforDetailMapper,
        Partner3WarehouseStampInfoDetailRepository warehouseStampInfoDetailRepository,
        WarehouseStampInfoDetailMapper warehouseStampInfoDetailMapper,
        PalletInforDetailRepository mainPalletRepository,
        WarehouseStampInfoDetailRepository mainWarehouseRepository
    ) {
        this.palletInforDetailRepository = palletInforDetailRepository;
        this.palletInforDetailMapper = palletInforDetailMapper;
        this.warehouseStampInfoDetailRepository =
            warehouseStampInfoDetailRepository;
        this.warehouseStampInfoDetailMapper = warehouseStampInfoDetailMapper;
        this.mainPalletRepository = mainPalletRepository;
        this.mainWarehouseRepository = mainWarehouseRepository;
    }

    /**
     * Save a palletInforDetail.
     *
     * @param palletInforDetailDTO the entity to save.
     * @return the persisted entity.
     */
    public PalletInforDetailDTO save(
        PalletInforDetailDTO palletInforDetailDTO
    ) {
        LOG.debug(
            "Request to save PalletInforDetail : {}",
            palletInforDetailDTO
        );
        PalletInforDetail palletInforDetail = palletInforDetailMapper.toEntity(
            palletInforDetailDTO
        );
        palletInforDetail = palletInforDetailRepository.save(palletInforDetail);
        return palletInforDetailMapper.toDto(palletInforDetail);
    }

    /**
     * Update a palletInforDetail.
     *
     * @param palletInforDetailDTO the entity to save.
     * @return the persisted entity.
     */
    public PalletInforDetailDTO update(
        PalletInforDetailDTO palletInforDetailDTO
    ) {
        LOG.debug(
            "Request to update PalletInforDetail : {}",
            palletInforDetailDTO
        );
        PalletInforDetail palletInforDetail = palletInforDetailMapper.toEntity(
            palletInforDetailDTO
        );
        palletInforDetail = palletInforDetailRepository.save(palletInforDetail);
        return palletInforDetailMapper.toDto(palletInforDetail);
    }

    /**
     * Partially update a palletInforDetail.
     *
     * @param palletInforDetailDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PalletInforDetailDTO> partialUpdate(
        PalletInforDetailDTO palletInforDetailDTO
    ) {
        LOG.debug(
            "Request to partially update PalletInforDetail : {}",
            palletInforDetailDTO
        );

        return palletInforDetailRepository
            .findBySerialPallet(palletInforDetailDTO.getSerialPallet())
            .map(existingPalletInforDetail -> {
                palletInforDetailMapper.partialUpdate(
                    existingPalletInforDetail,
                    palletInforDetailDTO
                );

                return existingPalletInforDetail;
            })
            .map(palletInforDetailRepository::save)
            .map(palletInforDetailMapper::toDto);
    }

    /**
     * Get all the palletInforDetails.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PalletInforDetailDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PalletInforDetails");
        return palletInforDetailRepository
            .findAll(pageable)
            .map(palletInforDetailMapper::toDto);
    }

    /**
     * Get one palletInforDetail by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PalletInforDetailDTO> findOne(String id) {
        LOG.debug("Request to get PalletInforDetail : {}", id);
        return palletInforDetailRepository
            .findBySerialPallet(id)
            .map(palletInforDetailMapper::toDto);
    }

    /**
     * Delete the palletInforDetail by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete PalletInforDetail : {}", id);
        palletInforDetailRepository
            .findBySerialPallet(id)
            .ifPresent(palletInforDetailRepository::delete);
    }

    /**
     * Save combined pallet and warehouse information.
     *
     * @param maLenhSanXuatId the ma_lenh_san_xuat_id to associate with the records.
     * @param combinedDTO     the combined data to save.
     * @return the saved combined data.
     */
    public CombinedPalletWarehouseDTO saveCombinedData(
        Long maLenhSanXuatId,
        CombinedPalletWarehouseDTO combinedDTO
    ) {
        LOG.debug(
            "Request to save combined data for maLenhSanXuatId: {}, data: {}",
            maLenhSanXuatId,
            combinedDTO
        );

        // Save PalletInforDetail list if provided
        if (
            combinedDTO.getPalletInforDetail() != null &&
            !combinedDTO.getPalletInforDetail().isEmpty()
        ) {
            for (
                int i = 0;
                i < combinedDTO.getPalletInforDetail().size();
                i++
            ) {
                PalletInforDetailDTO palletDTO = combinedDTO
                    .getPalletInforDetail()
                    .get(i);

                // Set ma_lenh_san_xuat_id from path parameter
                palletDTO.setMaLenhSanXuatId(maLenhSanXuatId);

                // Set updated timestamp
                if (palletDTO.getUpdatedAt() == null) {
                    palletDTO.setUpdatedAt(Instant.now());
                }

                PalletInforDetail palletEntity =
                    palletInforDetailMapper.toEntity(palletDTO);
                palletEntity = palletInforDetailRepository.save(palletEntity);

                // Update the DTO with the saved entity's ID
                PalletInforDetailDTO savedDTO = palletInforDetailMapper.toDto(
                    palletEntity
                );
                combinedDTO.getPalletInforDetail().set(i, savedDTO);
            }
        }

        // Save WarehouseNoteInfoDetail list if provided
        if (
            combinedDTO.getWarehouseNoteInfoDetail() != null &&
            !combinedDTO.getWarehouseNoteInfoDetail().isEmpty()
        ) {
            for (
                int i = 0;
                i < combinedDTO.getWarehouseNoteInfoDetail().size();
                i++
            ) {
                WarehouseStampInfoDetailDTO warehouseDTO = combinedDTO
                    .getWarehouseNoteInfoDetail()
                    .get(i);

                // Set ma_lenh_san_xuat_id from path parameter
                warehouseDTO.setMaLenhSanXuatId(maLenhSanXuatId);

                WarehouseNoteInfoDetail warehouseEntity =
                    warehouseStampInfoDetailMapper.toEntity(warehouseDTO);
                warehouseEntity = warehouseStampInfoDetailRepository.save(
                    warehouseEntity
                );

                // Update the DTO with the saved entity's ID
                WarehouseStampInfoDetailDTO savedDTO =
                    warehouseStampInfoDetailMapper.toDto(warehouseEntity);
                combinedDTO.getWarehouseNoteInfoDetail().set(i, savedDTO);
            }
        }

        return combinedDTO;
    }

    /**
     * Batch update pallet information details by ID.
     *
     * @param palletInforDetailDTOs the list of entities to update.
     * @return the list of updated entities.
     */
    public java.util.List<PalletInforDetailDTO> batchUpdate(
        java.util.List<PalletInforDetailDTO> palletInforDetailDTOs
    ) {
        LOG.debug(
            "Request to batch update PalletInforDetails : {}",
            palletInforDetailDTOs
        );

        java.util.List<PalletInforDetailDTO> updatedDTOs =
            new java.util.ArrayList<>();

        for (PalletInforDetailDTO dto : palletInforDetailDTOs) {
            if (dto.getId() == null) {
                LOG.warn("PalletInforDetail with null ID, skipping update");
                continue;
            }

            // Find existing pallet by ID
            Optional<PalletInforDetail> existingPalletOpt =
                palletInforDetailRepository.findById(dto.getId());

            if (existingPalletOpt.isPresent()) {
                PalletInforDetail existingPallet = existingPalletOpt.get();

                // Update all provided fields
                if (dto.getSerialPallet() != null) {
                    existingPallet.setSerialPallet(dto.getSerialPallet());
                }
                if (dto.getMaLenhSanXuatId() != null) {
                    existingPallet.setMaLenhSanXuatId(dto.getMaLenhSanXuatId());
                }
                if (dto.getQuantityPerBox() != null) {
                    existingPallet.setQuantityPerBox(dto.getQuantityPerBox());
                }
                if (dto.getNote() != null) {
                    existingPallet.setNote(dto.getNote());
                }
                if (dto.getNumBoxPerPallet() != null) {
                    existingPallet.setNumBoxPerPallet(dto.getNumBoxPerPallet());
                }
                if (dto.getCustomerName() != null) {
                    existingPallet.setCustomerName(dto.getCustomerName());
                }
                if (dto.getPoNumber() != null) {
                    existingPallet.setPoNumber(dto.getPoNumber());
                }
                if (dto.getDateCode() != null) {
                    existingPallet.setDateCode(dto.getDateCode());
                }
                if (dto.getItemNoSku() != null) {
                    existingPallet.setItemNoSku(dto.getItemNoSku());
                }
                if (dto.getQdsxNo() != null) {
                    existingPallet.setQdsxNo(dto.getQdsxNo());
                }
                if (dto.getProductionDate() != null) {
                    existingPallet.setProductionDate(dto.getProductionDate());
                }
                if (dto.getInspectorName() != null) {
                    existingPallet.setInspectorName(dto.getInspectorName());
                }
                if (dto.getInspectionResult() != null) {
                    existingPallet.setInspectionResult(
                        dto.getInspectionResult()
                    );
                }
                if (dto.getScanProgress() != null) {
                    existingPallet.setScanProgress(dto.getScanProgress());
                }
                if (dto.getNumBoxActual() != null) {
                    existingPallet.setNumBoxActual(dto.getNumBoxActual());
                }
                if (dto.getUpdatedBy() != null) {
                    existingPallet.setUpdatedBy(dto.getUpdatedBy());
                }

                // Always update the timestamp
                existingPallet.setUpdatedAt(Instant.now());

                // Save the updated entity
                PalletInforDetail savedPallet =
                    palletInforDetailRepository.save(existingPallet);
                updatedDTOs.add(palletInforDetailMapper.toDto(savedPallet));
            } else {
                LOG.warn(
                    "PalletInforDetail with ID {} not found, skipping update",
                    dto.getId()
                );
            }
        }

        return updatedDTOs;
    }

    /**
     * Get max serial numbers starting with 'B'.
     *
     * @return the max serial numbers
     */
    @Transactional(readOnly = true)
    public MaxSerialResponseDTO getMaxSerials() {
        LOG.debug("Request to get max serials starting with B");

        String maxSerialPallet =
            palletInforDetailRepository.findMaxSerialPalletStartingWith("B");
        String maxSerialBox =
            warehouseStampInfoDetailRepository.findMaxReelIdStartingWith("B");

        return new MaxSerialResponseDTO(maxSerialPallet, maxSerialBox);
    }
}
