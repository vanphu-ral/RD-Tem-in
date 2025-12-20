package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.PalletInforDetail;
import com.mycompany.myapp.domain.SerialBoxPalletMapping;
import com.mycompany.myapp.domain.WarehouseNoteInfoDetail;
import com.mycompany.myapp.repository.PalletInforDetailRepository;
import com.mycompany.myapp.repository.WarehouseStampInfoDetailRepository;
import com.mycompany.myapp.repository.partner3.Partner3PalletInforDetailRepository;
import com.mycompany.myapp.repository.partner3.Partner3SerialBoxPalletMappingRepository;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoDetailRepository;
import com.mycompany.myapp.service.dto.CombinedPalletWarehouseDTO;
import com.mycompany.myapp.service.dto.ListPalletInfoResponseDTO;
import com.mycompany.myapp.service.dto.MaxSerialResponseDTO;
import com.mycompany.myapp.service.dto.PalletInforDetailDTO;
import com.mycompany.myapp.service.dto.PalletWithBoxesDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDetailDTO;
import com.mycompany.myapp.service.mapper.PalletInforDetailMapper;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoDetailMapper;
import java.time.Instant;
import java.util.ArrayList;
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

    private final Partner3SerialBoxPalletMappingRepository serialBoxPalletMappingRepository;

    public PalletInforDetailService(
        Partner3PalletInforDetailRepository palletInforDetailRepository,
        PalletInforDetailMapper palletInforDetailMapper,
        Partner3WarehouseStampInfoDetailRepository warehouseStampInfoDetailRepository,
        WarehouseStampInfoDetailMapper warehouseStampInfoDetailMapper,
        PalletInforDetailRepository mainPalletRepository,
        WarehouseStampInfoDetailRepository mainWarehouseRepository,
        Partner3SerialBoxPalletMappingRepository serialBoxPalletMappingRepository
    ) {
        this.palletInforDetailRepository = palletInforDetailRepository;
        this.palletInforDetailMapper = palletInforDetailMapper;
        this.warehouseStampInfoDetailRepository =
            warehouseStampInfoDetailRepository;
        this.warehouseStampInfoDetailMapper = warehouseStampInfoDetailMapper;
        this.mainPalletRepository = mainPalletRepository;
        this.mainWarehouseRepository = mainWarehouseRepository;
        this.serialBoxPalletMappingRepository =
            serialBoxPalletMappingRepository;
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
        try {
            // Try to parse the id as a numeric value
            Long entityId = Long.valueOf(id);
            return palletInforDetailRepository
                .findById(entityId)
                .map(palletInforDetailMapper::toDto);
        } catch (NumberFormatException e) {
            // If the id is not numeric, try to find by serialPallet
            LOG.warn(
                "Invalid numeric id format for findOne: {}. Attempting to find by serialPallet.",
                id
            );
            return palletInforDetailRepository
                .findBySerialPallet(id)
                .map(palletInforDetailMapper::toDto);
        }
    }

    /**
     * Delete the palletInforDetail by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete PalletInforDetail : {}", id);
        try {
            Long entityId = Long.valueOf(id);
            palletInforDetailRepository.deleteById(entityId);
        } catch (NumberFormatException e) {
            LOG.warn("Invalid id format for deletion: {}", id);
        }
    }

    /**
     * Batch delete palletInforDetails by list of DTOs.
     *
     * @param palletInforDetailDTOs the list of entities to delete.
     */
    public void batchDelete(List<PalletInforDetailDTO> palletInforDetailDTOs) {
        LOG.debug(
            "Request to batch delete PalletInforDetails : {}",
            palletInforDetailDTOs
        );

        for (PalletInforDetailDTO dto : palletInforDetailDTOs) {
            if (dto.getId() != null) {
                palletInforDetailRepository.deleteById(dto.getId());
            }
        }
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

                // Set created_at timestamp for new records
                if (warehouseDTO.getCreatedAt() == null) {
                    warehouseDTO.setCreatedAt(Instant.now());
                }

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
                if (dto.getWmsSendStatus() != null) {
                    existingPallet.setWmsSendStatus(dto.getWmsSendStatus());
                }
                if (dto.getPrintStatus() != null) {
                    existingPallet.setPrintStatus(dto.getPrintStatus());
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

    /**
     * Get list of pallets with their associated boxes by work order code.
     * Filters warehouse notes that are not deleted (deleted_by IS NULL).
     *
     * @param workOrderCode the work order code
     * @return the response containing list of pallets with boxes
     */
    @Transactional(readOnly = true)
    public ListPalletInfoResponseDTO getListPalletInfoDetailByWorkOrderCode(
        String workOrderCode
    ) {
        LOG.debug(
            "Request to get pallet info detail by work order code: {}",
            workOrderCode
        );

        // Get all pallets for the work order code (from non-deleted warehouse notes)
        List<PalletInforDetail> pallets =
            palletInforDetailRepository.findByWorkOrderCode(workOrderCode);

        // Build the response with pallets and their boxes
        List<PalletWithBoxesDTO> palletWithBoxesList = pallets
            .stream()
            .map(pallet -> {
                // Convert pallet to DTO
                PalletInforDetailDTO palletDTO = palletInforDetailMapper.toDto(
                    pallet
                );

                // Get all box mappings for this pallet
                List<SerialBoxPalletMapping> mappings =
                    serialBoxPalletMappingRepository.findBySerialPallet(
                        pallet.getSerialPallet()
                    );

                // Get the serial_box values from mappings
                List<String> serialBoxes = mappings
                    .stream()
                    .map(SerialBoxPalletMapping::getSerialBox)
                    .collect(Collectors.toList());

                // Get box details from warehouse_note_info_detail using reel_id (serial_box)
                List<WarehouseStampInfoDetailDTO> boxList = new ArrayList<>();
                for (String serialBox : serialBoxes) {
                    warehouseStampInfoDetailRepository
                        .findByReelId(serialBox)
                        .ifPresent(box ->
                            boxList.add(
                                warehouseStampInfoDetailMapper.toDto(box)
                            )
                        );
                }

                return new PalletWithBoxesDTO(palletDTO, boxList);
            })
            .collect(Collectors.toList());

        return new ListPalletInfoResponseDTO(palletWithBoxesList);
    }
}
