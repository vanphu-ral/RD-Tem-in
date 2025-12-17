package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.GenTemConfig;
import com.mycompany.myapp.domain.PalletInforDetail;
import com.mycompany.myapp.domain.ReelIdInWarehouseNoteInfoApproval;
import com.mycompany.myapp.domain.SerialBoxPalletMapping;
import com.mycompany.myapp.domain.WarehouseNoteInfoApproval;
import com.mycompany.myapp.domain.WarehouseNoteInfoDetail;
import com.mycompany.myapp.repository.partner3.Partner3GenTemConfigRepository;
import com.mycompany.myapp.repository.partner3.Partner3PalletInforDetailRepository;
import com.mycompany.myapp.repository.partner3.Partner3ReelIdInWarehouseNoteInfoApprovalRepository;
import com.mycompany.myapp.repository.partner3.Partner3SerialBoxPalletMappingRepository;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseNoteInfoApprovalRepository;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoDetailRepository;
import com.mycompany.myapp.service.dto.GenTemConfigDTO;
import com.mycompany.myapp.service.dto.PalletInforDetailDTO;
import com.mycompany.myapp.service.dto.ReelIdInWarehouseNoteInfoApprovalDTO;
import com.mycompany.myapp.service.dto.ReelIdRequestDTO;
import com.mycompany.myapp.service.dto.SerialBoxPalletMappingDTO;
import com.mycompany.myapp.service.dto.WarehouseNoteInfoApprovalDTO;
import com.mycompany.myapp.service.dto.WarehouseNoteInfoApprovalRequestDTO;
import com.mycompany.myapp.service.dto.WarehouseNoteInfoApprovalWithChildrenDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDetailDTO;
import com.mycompany.myapp.service.mapper.GenTemConfigMapper;
import com.mycompany.myapp.service.mapper.PalletInforDetailMapper;
import com.mycompany.myapp.service.mapper.SerialBoxPalletMappingMapper;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoDetailMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link WarehouseNoteInfoApproval}.
 */
@Service
@Transactional("partner3TransactionManager")
public class WarehouseNoteInfoApprovalService {

    private final Logger log = LoggerFactory.getLogger(
        WarehouseNoteInfoApprovalService.class
    );

    private final Partner3WarehouseNoteInfoApprovalRepository warehouseNoteInfoApprovalRepository;
    private final Partner3ReelIdInWarehouseNoteInfoApprovalRepository reelIdRepository;
    private final Partner3WarehouseStampInfoDetailRepository warehouseStampInfoDetailRepository;
    private final Partner3SerialBoxPalletMappingRepository serialBoxPalletMappingRepository;
    private final Partner3GenTemConfigRepository genTemConfigRepository;
    private final Partner3PalletInforDetailRepository palletInforDetailRepository;

    private final WarehouseStampInfoDetailMapper warehouseStampInfoDetailMapper;
    private final SerialBoxPalletMappingMapper serialBoxPalletMappingMapper;
    private final GenTemConfigMapper genTemConfigMapper;
    private final PalletInforDetailMapper palletInforDetailMapper;

    public WarehouseNoteInfoApprovalService(
        Partner3WarehouseNoteInfoApprovalRepository warehouseNoteInfoApprovalRepository,
        Partner3ReelIdInWarehouseNoteInfoApprovalRepository reelIdRepository,
        Partner3WarehouseStampInfoDetailRepository warehouseStampInfoDetailRepository,
        Partner3SerialBoxPalletMappingRepository serialBoxPalletMappingRepository,
        Partner3GenTemConfigRepository genTemConfigRepository,
        Partner3PalletInforDetailRepository palletInforDetailRepository,
        WarehouseStampInfoDetailMapper warehouseStampInfoDetailMapper,
        SerialBoxPalletMappingMapper serialBoxPalletMappingMapper,
        GenTemConfigMapper genTemConfigMapper,
        PalletInforDetailMapper palletInforDetailMapper
    ) {
        this.warehouseNoteInfoApprovalRepository =
            warehouseNoteInfoApprovalRepository;
        this.reelIdRepository = reelIdRepository;
        this.warehouseStampInfoDetailRepository =
            warehouseStampInfoDetailRepository;
        this.serialBoxPalletMappingRepository =
            serialBoxPalletMappingRepository;
        this.genTemConfigRepository = genTemConfigRepository;
        this.palletInforDetailRepository = palletInforDetailRepository;
        this.warehouseStampInfoDetailMapper = warehouseStampInfoDetailMapper;
        this.serialBoxPalletMappingMapper = serialBoxPalletMappingMapper;
        this.genTemConfigMapper = genTemConfigMapper;
        this.palletInforDetailMapper = palletInforDetailMapper;
    }

    /**
     * Create a new WarehouseNoteInfoApproval with nested ReelIds.
     *
     * @param requestDTO the request DTO containing approval info and reel IDs
     * @return the created WarehouseNoteInfoApprovalDTO
     */
    public WarehouseNoteInfoApprovalDTO create(
        WarehouseNoteInfoApprovalRequestDTO requestDTO
    ) {
        log.debug("Creating WarehouseNoteInfoApproval: {}", requestDTO);

        // Create the main approval entity
        WarehouseNoteInfoApproval approval = new WarehouseNoteInfoApproval();
        approval.setWarehouseNoteInfoId(requestDTO.getWarehouseNoteInfoId());
        approval.setMaLenhSanXuat(requestDTO.getMaLenhSanXuat());
        approval.setSapCode(requestDTO.getSapCode());
        approval.setSapName(requestDTO.getSapName());
        approval.setWorkOrderCode(requestDTO.getWorkOrderCode());
        approval.setVersion(requestDTO.getVersion());
        approval.setStorageCode(requestDTO.getStorageCode());
        approval.setTotalQuantity(requestDTO.getTotalQuantity());
        approval.setCreateBy(requestDTO.getCreateBy());
        approval.setEntryTime(Instant.now());
        approval.setTrangThai(requestDTO.getTrangThai());
        approval.setComment(requestDTO.getComment());
        approval.setTimeUpdate(Instant.now());
        approval.setGroupName(requestDTO.getGroupName());
        approval.setComment2(requestDTO.getComment2());
        approval.setApproverBy(requestDTO.getApproverBy());
        approval.setBranch(requestDTO.getBranch());
        approval.setProductType(requestDTO.getProductType());
        approval.setDestinationWarehouse(requestDTO.getDestinationWarehouse());

        // Save the main approval entity
        WarehouseNoteInfoApproval savedApproval =
            warehouseNoteInfoApprovalRepository.save(approval);
        log.debug(
            "Saved WarehouseNoteInfoApproval with id: {}",
            savedApproval.getId()
        );

        // Create and save ReelIds
        if (
            requestDTO.getListWarehouseNoteDetail() != null &&
            !requestDTO.getListWarehouseNoteDetail().isEmpty()
        ) {
            for (ReelIdRequestDTO reelIdRequest : requestDTO.getListWarehouseNoteDetail()) {
                ReelIdInWarehouseNoteInfoApproval reelId =
                    new ReelIdInWarehouseNoteInfoApproval();
                reelId.setId(reelIdRequest.getId());
                reelId.setCreateAt(
                    reelIdRequest.getCreateAt() != null
                        ? reelIdRequest.getCreateAt()
                        : Instant.now()
                );
                reelId.setCreateBy(reelIdRequest.getCreateBy());
                reelId.setStatus("pending"); // Set default status
                reelId.setWarehouseNoteInfoApproval(savedApproval);

                reelIdRepository.save(reelId);
                log.debug(
                    "Saved ReelId: {} for WarehouseNoteInfoApproval: {}",
                    reelId.getId(),
                    savedApproval.getId()
                );
            }
        }

        return convertToDTO(savedApproval);
    }

    /**
     * Find a WarehouseNoteInfoApproval by ID.
     *
     * @param id the ID of the approval
     * @return an Optional containing the WarehouseNoteInfoApprovalDTO if found
     */
    @Transactional(readOnly = true)
    public Optional<WarehouseNoteInfoApprovalDTO> findOne(Long id) {
        log.debug("Finding WarehouseNoteInfoApproval with id: {}", id);
        return warehouseNoteInfoApprovalRepository
            .findById(id)
            .map(this::convertToDTO);
    }

    /**
     * Get WarehouseNoteInfoApproval with all child tables data by id.
     *
     * @param id the id of the WarehouseNoteInfoApproval.
     * @return the combined entity with all children.
     */
    @Transactional(readOnly = true)
    public Optional<
        WarehouseNoteInfoApprovalWithChildrenDTO
    > findOneWithChildren(Long id) {
        log.debug(
            "Request to get WarehouseNoteInfoApproval with children : {}",
            id
        );

        return warehouseNoteInfoApprovalRepository
            .findById(id)
            .map(warehouseNoteInfoApproval -> {
                // Map parent entity
                WarehouseNoteInfoApprovalDTO parentDTO = convertToDTO(
                    warehouseNoteInfoApproval
                );

                // Fetch reel IDs for this approval
                List<ReelIdInWarehouseNoteInfoApproval> reelIds =
                    reelIdRepository.findByWarehouseNoteInfoApprovalId(id);
                log.debug("Fetched reelIds: {}", reelIds);
                List<Long> reelIdList = reelIds
                    .stream()
                    .map(ReelIdInWarehouseNoteInfoApproval::getId)
                    .collect(Collectors.toList());
                log.debug("Reel ID list: {}", reelIdList);

                // Map reelIds to DTOs and set in parentDTO
                Set<ReelIdInWarehouseNoteInfoApprovalDTO> reelIdDTOs = reelIds
                    .stream()
                    .map(reel -> {
                        ReelIdInWarehouseNoteInfoApprovalDTO dto =
                            new ReelIdInWarehouseNoteInfoApprovalDTO();
                        dto.setId(reel.getId());
                        dto.setWarehouseNoteInfoApprovalId(
                            reel.getWarehouseNoteInfoApproval().getId()
                        );
                        dto.setCreateAt(reel.getCreateAt());
                        dto.setCreateBy(reel.getCreateBy());
                        dto.setStatus(reel.getStatus());
                        return dto;
                    })
                    .collect(Collectors.toSet());
                log.debug("Reel ID DTOs: {}", reelIdDTOs);
                parentDTO.setReelIds(reelIdDTOs);

                // Fetch warehouse_note_info_details and filter by IDs
                List<WarehouseNoteInfoDetail> filteredDetails =
                    warehouseStampInfoDetailRepository.findByIdIn(reelIdList);
                List<WarehouseStampInfoDetailDTO> filteredDetailDTOs =
                    filteredDetails
                        .stream()
                        .map(warehouseStampInfoDetailMapper::toDto)
                        .collect(Collectors.toList());
                log.debug(
                    "Filtered warehouse_note_info_details: {}",
                    filteredDetailDTOs
                );

                // Fetch and map serial_box_pallet_mapping
                List<SerialBoxPalletMapping> serialMappings =
                    serialBoxPalletMappingRepository.findByMaLenhSanXuatId(
                        warehouseNoteInfoApproval.getId()
                    );
                List<SerialBoxPalletMappingDTO> serialMappingDTOs =
                    serialMappings
                        .stream()
                        .map(serialBoxPalletMappingMapper::toDto)
                        .collect(Collectors.toList());

                // Fetch and map pallet_infor_detail using warehouseNoteInfoId (Long)
                // Filter by reel IDs to only get pallet details associated with the approved
                // reels
                List<PalletInforDetail> palletDetails =
                    palletInforDetailRepository.findByMaLenhSanXuatIdAndIdIn(
                        warehouseNoteInfoApproval.getWarehouseNoteInfoId(),
                        reelIdList
                    );
                List<PalletInforDetailDTO> palletDetailDTOs = palletDetails
                    .stream()
                    .map(palletInforDetailMapper::toDto)
                    .collect(Collectors.toList());

                // Create and return combined DTO
                return new WarehouseNoteInfoApprovalWithChildrenDTO(
                    parentDTO,
                    filteredDetailDTOs,
                    serialMappingDTOs,
                    palletDetailDTOs
                );
            });
    }

    /**
     * Update a WarehouseNoteInfoApproval with nested ReelIds.
     *
     * @param id         the ID of the approval to update
     * @param requestDTO the request DTO containing updated approval info and reel
     *                   IDs
     * @return the updated WarehouseNoteInfoApprovalDTO
     */
    public WarehouseNoteInfoApprovalDTO update(
        Long id,
        WarehouseNoteInfoApprovalRequestDTO requestDTO
    ) {
        log.debug("Updating WarehouseNoteInfoApproval with id: {}", id);

        // Find existing approval
        WarehouseNoteInfoApproval existingApproval =
            warehouseNoteInfoApprovalRepository
                .findById(id)
                .orElseThrow(() ->
                    new RuntimeException(
                        "WarehouseNoteInfoApproval not found with id: " + id
                    )
                );

        // Update the main approval entity
        existingApproval.setWarehouseNoteInfoId(
            requestDTO.getWarehouseNoteInfoId()
        );
        existingApproval.setMaLenhSanXuat(requestDTO.getMaLenhSanXuat());
        existingApproval.setSapCode(requestDTO.getSapCode());
        existingApproval.setSapName(requestDTO.getSapName());
        existingApproval.setWorkOrderCode(requestDTO.getWorkOrderCode());
        existingApproval.setVersion(requestDTO.getVersion());
        existingApproval.setStorageCode(requestDTO.getStorageCode());
        existingApproval.setTotalQuantity(requestDTO.getTotalQuantity());
        existingApproval.setCreateBy(requestDTO.getCreateBy());
        existingApproval.setTrangThai(requestDTO.getTrangThai());
        existingApproval.setComment(requestDTO.getComment());
        existingApproval.setTimeUpdate(Instant.now());
        existingApproval.setGroupName(requestDTO.getGroupName());
        existingApproval.setComment2(requestDTO.getComment2());
        existingApproval.setApproverBy(requestDTO.getApproverBy());
        existingApproval.setBranch(requestDTO.getBranch());
        existingApproval.setProductType(requestDTO.getProductType());
        existingApproval.setDestinationWarehouse(
            requestDTO.getDestinationWarehouse()
        );

        // Save the updated main approval entity
        WarehouseNoteInfoApproval updatedApproval =
            warehouseNoteInfoApprovalRepository.save(existingApproval);
        log.debug(
            "Updated WarehouseNoteInfoApproval with id: {}",
            updatedApproval.getId()
        );

        // Update ReelIds - first delete existing ones and then create new ones
        if (requestDTO.getListWarehouseNoteDetail() != null) {
            // Delete existing reel IDs
            reelIdRepository.deleteByWarehouseNoteInfoApprovalId(id);
            log.debug(
                "Deleted existing ReelIds for WarehouseNoteInfoApproval: {}",
                id
            );

            // Create and save new ReelIds
            for (ReelIdRequestDTO reelIdRequest : requestDTO.getListWarehouseNoteDetail()) {
                ReelIdInWarehouseNoteInfoApproval reelId =
                    new ReelIdInWarehouseNoteInfoApproval();
                reelId.setId(reelIdRequest.getId());
                reelId.setCreateAt(
                    reelIdRequest.getCreateAt() != null
                        ? reelIdRequest.getCreateAt()
                        : Instant.now()
                );
                reelId.setCreateBy(reelIdRequest.getCreateBy());
                reelId.setStatus("pending"); // Set default status
                reelId.setWarehouseNoteInfoApproval(updatedApproval);

                reelIdRepository.save(reelId);
                log.debug(
                    "Saved ReelId: {} for WarehouseNoteInfoApproval: {}",
                    reelId.getId(),
                    updatedApproval.getId()
                );
            }
        }

        return convertToDTO(updatedApproval);
    }

    /**
     * Get all WarehouseNoteInfoApprovals.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WarehouseNoteInfoApprovalDTO> findAll(Pageable pageable) {
        log.debug("Request to get all WarehouseNoteInfoApprovals");
        return warehouseNoteInfoApprovalRepository
            .findAll(pageable)
            .map(this::convertToDTO);
    }

    /**
     * Convert WarehouseNoteInfoApproval entity to DTO.
     *
     * @param entity the entity to convert
     * @return the DTO
     */
    private WarehouseNoteInfoApprovalDTO convertToDTO(
        WarehouseNoteInfoApproval entity
    ) {
        WarehouseNoteInfoApprovalDTO dto = new WarehouseNoteInfoApprovalDTO();
        dto.setId(entity.getId());
        dto.setWarehouseNoteInfoId(entity.getWarehouseNoteInfoId());
        dto.setMaLenhSanXuat(entity.getMaLenhSanXuat());
        dto.setSapCode(entity.getSapCode());
        dto.setSapName(entity.getSapName());
        dto.setWorkOrderCode(entity.getWorkOrderCode());
        dto.setVersion(entity.getVersion());
        dto.setStorageCode(entity.getStorageCode());
        dto.setTotalQuantity(entity.getTotalQuantity());
        dto.setCreateBy(entity.getCreateBy());
        dto.setEntryTime(entity.getEntryTime());
        dto.setTrangThai(entity.getTrangThai());
        dto.setComment(entity.getComment());
        dto.setTimeUpdate(entity.getTimeUpdate());
        dto.setGroupName(entity.getGroupName());
        dto.setComment2(entity.getComment2());
        dto.setApproverBy(entity.getApproverBy());
        dto.setBranch(entity.getBranch());
        dto.setProductType(entity.getProductType());
        dto.setDeletedAt(entity.getDeletedAt());
        dto.setDeletedBy(entity.getDeletedBy());
        dto.setDestinationWarehouse(entity.getDestinationWarehouse());
        return dto;
    }
}
