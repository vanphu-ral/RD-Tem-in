package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.ReelIdInWarehouseNoteInfoApproval;
import com.mycompany.myapp.domain.WarehouseNoteInfoApproval;
import com.mycompany.myapp.repository.partner3.Partner3ReelIdInWarehouseNoteInfoApprovalRepository;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseNoteInfoApprovalRepository;
import com.mycompany.myapp.service.dto.ReelIdRequestDTO;
import com.mycompany.myapp.service.dto.WarehouseNoteInfoApprovalDTO;
import com.mycompany.myapp.service.dto.WarehouseNoteInfoApprovalRequestDTO;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public WarehouseNoteInfoApprovalService(
        Partner3WarehouseNoteInfoApprovalRepository warehouseNoteInfoApprovalRepository,
        Partner3ReelIdInWarehouseNoteInfoApprovalRepository reelIdRepository
    ) {
        this.warehouseNoteInfoApprovalRepository =
            warehouseNoteInfoApprovalRepository;
        this.reelIdRepository = reelIdRepository;
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
                reelId.setWarehouseNoteInfoApprovalId(savedApproval.getId());
                reelId.setCreateAt(
                    reelIdRequest.getCreateAt() != null
                        ? reelIdRequest.getCreateAt()
                        : Instant.now()
                );
                reelId.setCreateBy(reelIdRequest.getCreateBy());
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
