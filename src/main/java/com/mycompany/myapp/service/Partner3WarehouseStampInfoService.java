package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.WarehouseNoteInfo;
import com.mycompany.myapp.domain.WarehouseNoteInfoDetail;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoDetailRepository;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoRepository;
import com.mycompany.myapp.service.dto.WarehouseNoteInfoWithChildrenDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDetailDTO;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoDetailMapper;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoMapper;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing
 * {@link com.mycompany.myapp.domain.WarehouseNoteInfo} using partner3
 * datasource.
 */
@Service
@Transactional("partner3TransactionManager")
public class Partner3WarehouseStampInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(
        Partner3WarehouseStampInfoService.class
    );

    private final Partner3WarehouseStampInfoRepository partner3WarehouseStampInfoRepository;

    private final Partner3WarehouseStampInfoDetailRepository partner3WarehouseStampInfoDetailRepository;

    private final WarehouseStampInfoMapper warehouseStampInfoMapper;

    private final WarehouseStampInfoDetailMapper warehouseStampInfoDetailMapper;

    public Partner3WarehouseStampInfoService(
        Partner3WarehouseStampInfoRepository partner3WarehouseStampInfoRepository,
        Partner3WarehouseStampInfoDetailRepository partner3WarehouseStampInfoDetailRepository,
        WarehouseStampInfoMapper warehouseStampInfoMapper,
        WarehouseStampInfoDetailMapper warehouseStampInfoDetailMapper
    ) {
        this.partner3WarehouseStampInfoRepository =
            partner3WarehouseStampInfoRepository;
        this.partner3WarehouseStampInfoDetailRepository =
            partner3WarehouseStampInfoDetailRepository;
        this.warehouseStampInfoMapper = warehouseStampInfoMapper;
        this.warehouseStampInfoDetailMapper = warehouseStampInfoDetailMapper;
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
        return partner3WarehouseStampInfoRepository
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
        return partner3WarehouseStampInfoRepository
            .findByApproverBy(approverBy)
            .stream()
            .map(warehouseStampInfoMapper::toDto)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get all warehouseStampInfos where trangThai is not 'Bản nháp'.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WarehouseStampInfoDTO> findByTrangThaiNotDraft() {
        LOG.debug(
            "Request to get WarehouseStampInfos where trangThai != 'Bản nháp'"
        );
        return partner3WarehouseStampInfoRepository
            .findByTrangThaiNotDraft()
            .stream()
            .map(warehouseStampInfoMapper::toDto)
            .sorted(
                Comparator.comparing(
                    WarehouseStampInfoDTO::getTimeUpdate,
                    Comparator.nullsLast(Comparator.reverseOrder())
                )
            )
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get one warehouseStampInfo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WarehouseStampInfoDTO> findOne(Long id) {
        LOG.debug("Request to get WarehouseNoteInfo : {}", id);
        return partner3WarehouseStampInfoRepository
            .findById(id)
            .map(warehouseStampInfoMapper::toDto);
    }

    /**
     * Get the total quantity from all warehouseStampInfos where trangThai is not
     * 'Bản nháp'.
     *
     * @return the total quantity.
     */
    @Transactional(readOnly = true)
    public Integer getTotalQuantity() {
        LOG.debug("Request to get total quantity from WarehouseStampInfos");
        return partner3WarehouseStampInfoRepository
            .findByTrangThaiNotDraft()
            .stream()
            .mapToInt(warehouseNoteInfo ->
                warehouseNoteInfo.getTotalQuantity() != null
                    ? warehouseNoteInfo.getTotalQuantity()
                    : 0
            )
            .sum();
    }

    /**
     * Update a warehouseStampInfo.
     *
     * @param warehouseStampInfoDTO the entity to update.
     * @return the persisted entity.
     */
    public WarehouseStampInfoDTO update(
        WarehouseStampInfoDTO warehouseStampInfoDTO
    ) {
        LOG.debug(
            "Request to update WarehouseNoteInfo : {}",
            warehouseStampInfoDTO
        );
        // Set timeUpdate to now
        warehouseStampInfoDTO.setTimeUpdate(Instant.now());
        WarehouseNoteInfo warehouseStampInfo =
            warehouseStampInfoMapper.toEntity(warehouseStampInfoDTO);
        warehouseStampInfo = partner3WarehouseStampInfoRepository.save(
            warehouseStampInfo
        );
        return warehouseStampInfoMapper.toDto(warehouseStampInfo);
    }

    /**
     * Get all warehouseStampInfos by workOrderCode.
     *
     * @param workOrderCode the workOrderCode to filter by.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WarehouseStampInfoDTO> findByWorkOrderCode(
        String workOrderCode
    ) {
        LOG.debug(
            "Request to get WarehouseStampInfos by workOrderCode : {}",
            workOrderCode
        );
        return partner3WarehouseStampInfoRepository
            .findByWorkOrderCode(workOrderCode)
            .stream()
            .map(warehouseStampInfoMapper::toDto)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Create a warehouseStampInfo with children.
     *
     * @param warehouseNoteInfoWithChildrenDTO the DTO with info and details.
     * @return the persisted DTO.
     */
    public WarehouseNoteInfoWithChildrenDTO create(
        WarehouseNoteInfoWithChildrenDTO warehouseNoteInfoWithChildrenDTO
    ) {
        LOG.debug(
            "Request to create WarehouseNoteInfo with children : {}",
            warehouseNoteInfoWithChildrenDTO
        );

        // Set default timestamps if not provided
        WarehouseStampInfoDTO dto =
            warehouseNoteInfoWithChildrenDTO.getWarehouseNoteInfo();
        if (dto.getEntryTime() == null) {
            dto.setEntryTime(Instant.now());
        }
        if (dto.getTimeUpdate() == null) {
            dto.setTimeUpdate(Instant.now());
        }

        // Save the main WarehouseNoteInfo
        WarehouseNoteInfo warehouseNoteInfo = warehouseStampInfoMapper.toEntity(
            warehouseNoteInfoWithChildrenDTO.getWarehouseNoteInfo()
        );
        warehouseNoteInfo = partner3WarehouseStampInfoRepository.save(
            warehouseNoteInfo
        );

        // Save the details
        if (
            warehouseNoteInfoWithChildrenDTO.getWarehouseNoteInfoDetails() !=
            null
        ) {
            for (WarehouseStampInfoDetailDTO detailDTO : warehouseNoteInfoWithChildrenDTO.getWarehouseNoteInfoDetails()) {
                WarehouseNoteInfoDetail detail =
                    warehouseStampInfoDetailMapper.toEntity(detailDTO);
                detail.setmaLenhSanXuatId(warehouseNoteInfo.getId());
                partner3WarehouseStampInfoDetailRepository.save(detail);
            }
        }

        // Return the DTO with the saved info
        WarehouseStampInfoDTO savedInfoDTO = warehouseStampInfoMapper.toDto(
            warehouseNoteInfo
        );
        warehouseNoteInfoWithChildrenDTO.setWarehouseNoteInfo(savedInfoDTO);
        return warehouseNoteInfoWithChildrenDTO;
    }
}
