package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.GenTemConfig;
import com.mycompany.myapp.domain.PalletInforDetail;
import com.mycompany.myapp.domain.SerialBoxPalletMapping;
import com.mycompany.myapp.domain.WarehouseNoteInfo;
import com.mycompany.myapp.domain.WarehouseNoteInfoDetail;
import com.mycompany.myapp.repository.partner3.Partner3GenTemConfigRepository;
import com.mycompany.myapp.repository.partner3.Partner3PalletInforDetailRepository;
import com.mycompany.myapp.repository.partner3.Partner3SerialBoxPalletMappingRepository;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoDetailRepository;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoRepository;
import com.mycompany.myapp.service.dto.GenTemConfigDTO;
import com.mycompany.myapp.service.dto.PalletInforDetailDTO;
import com.mycompany.myapp.service.dto.SerialBoxPalletMappingDTO;
import com.mycompany.myapp.service.dto.WarehouseNoteInfoWithChildrenDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDetailDTO;
import com.mycompany.myapp.service.mapper.GenTemConfigMapper;
import com.mycompany.myapp.service.mapper.PalletInforDetailMapper;
import com.mycompany.myapp.service.mapper.SerialBoxPalletMappingMapper;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoDetailMapper;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoMapper;
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
 * {@link com.mycompany.myapp.domain.WarehouseNoteInfo}.
 */
@Service
@Transactional("partner3TransactionManager")
public class WarehouseStampInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(
        WarehouseStampInfoService.class
    );

    private final Partner3WarehouseStampInfoRepository warehouseStampInfoRepository;
    private final Partner3WarehouseStampInfoDetailRepository warehouseStampInfoDetailRepository;
    private final Partner3SerialBoxPalletMappingRepository serialBoxPalletMappingRepository;
    private final Partner3GenTemConfigRepository genTemConfigRepository;
    private final Partner3PalletInforDetailRepository palletInforDetailRepository;

    private final WarehouseStampInfoMapper warehouseStampInfoMapper;
    private final WarehouseStampInfoDetailMapper warehouseStampInfoDetailMapper;
    private final SerialBoxPalletMappingMapper serialBoxPalletMappingMapper;
    private final GenTemConfigMapper genTemConfigMapper;
    private final PalletInforDetailMapper palletInforDetailMapper;

    public WarehouseStampInfoService(
        Partner3WarehouseStampInfoRepository warehouseStampInfoRepository,
        Partner3WarehouseStampInfoDetailRepository warehouseStampInfoDetailRepository,
        Partner3SerialBoxPalletMappingRepository serialBoxPalletMappingRepository,
        Partner3GenTemConfigRepository genTemConfigRepository,
        Partner3PalletInforDetailRepository palletInforDetailRepository,
        WarehouseStampInfoMapper warehouseStampInfoMapper,
        WarehouseStampInfoDetailMapper warehouseStampInfoDetailMapper,
        SerialBoxPalletMappingMapper serialBoxPalletMappingMapper,
        GenTemConfigMapper genTemConfigMapper,
        PalletInforDetailMapper palletInforDetailMapper
    ) {
        this.warehouseStampInfoRepository = warehouseStampInfoRepository;
        this.warehouseStampInfoDetailRepository =
            warehouseStampInfoDetailRepository;
        this.serialBoxPalletMappingRepository =
            serialBoxPalletMappingRepository;
        this.genTemConfigRepository = genTemConfigRepository;
        this.palletInforDetailRepository = palletInforDetailRepository;
        this.warehouseStampInfoMapper = warehouseStampInfoMapper;
        this.warehouseStampInfoDetailMapper = warehouseStampInfoDetailMapper;
        this.serialBoxPalletMappingMapper = serialBoxPalletMappingMapper;
        this.genTemConfigMapper = genTemConfigMapper;
        this.palletInforDetailMapper = palletInforDetailMapper;
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
            "Request to save WarehouseNoteInfo : {}",
            warehouseStampInfoDTO
        );
        if (warehouseStampInfoDTO.getEntryTime() == null) {
            warehouseStampInfoDTO.setEntryTime(Instant.now());
        }
        if (warehouseStampInfoDTO.getTimeUpdate() == null) {
            warehouseStampInfoDTO.setTimeUpdate(Instant.now());
        }
        WarehouseNoteInfo warehouseStampInfo =
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
            "Request to update WarehouseNoteInfo : {}",
            warehouseStampInfoDTO
        );
        WarehouseNoteInfo warehouseStampInfo =
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
            "Request to partially update WarehouseNoteInfo : {}",
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
        LOG.debug("Request to get WarehouseNoteInfo : {}", id);
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
     * Get all warehouseStampInfos where trangThai is not 'Bản nháp'.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WarehouseStampInfoDTO> findByTrangThaiNotDraft() {
        LOG.debug(
            "Request to get WarehouseStampInfos where trangThai != 'Bản nháp'"
        );
        return warehouseStampInfoRepository
            .findByTrangThaiNotDraft()
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
        LOG.debug("Request to delete WarehouseNoteInfo : {}", id);
        warehouseStampInfoRepository.deleteById(id);
    }

    /**
     * Get WarehouseNoteInfo with all child tables data by id.
     *
     * @param id the id of the WarehouseNoteInfo.
     * @return the combined entity with all children.
     */
    @Transactional(readOnly = true)
    public Optional<WarehouseNoteInfoWithChildrenDTO> findOneWithChildren(
        Long id
    ) {
        LOG.debug("Request to get WarehouseNoteInfo with children : {}", id);

        return warehouseStampInfoRepository
            .findById(id)
            .map(warehouseNoteInfo -> {
                // Map parent entity
                WarehouseStampInfoDTO parentDTO =
                    warehouseStampInfoMapper.toDto(warehouseNoteInfo);

                // Fetch and map warehouse_note_info_detail
                List<WarehouseNoteInfoDetail> details =
                    warehouseStampInfoDetailRepository.findByMaLenhSanXuatId(
                        id
                    );
                List<WarehouseStampInfoDetailDTO> detailDTOs = details
                    .stream()
                    .map(warehouseStampInfoDetailMapper::toDto)
                    .collect(Collectors.toList());

                // Fetch and map serial_box_pallet_mapping
                List<SerialBoxPalletMapping> serialMappings =
                    serialBoxPalletMappingRepository.findByMaLenhSanXuatId(id);
                List<SerialBoxPalletMappingDTO> serialMappingDTOs =
                    serialMappings
                        .stream()
                        .map(serialBoxPalletMappingMapper::toDto)
                        .collect(Collectors.toList());

                // Fetch and map pallet_infor_detail
                List<PalletInforDetail> palletDetails =
                    palletInforDetailRepository.findByMaLenhSanXuatId(id);
                List<PalletInforDetailDTO> palletDetailDTOs = palletDetails
                    .stream()
                    .map(palletInforDetailMapper::toDto)
                    .collect(Collectors.toList());

                // Create and return combined DTO
                return new WarehouseNoteInfoWithChildrenDTO(
                    parentDTO,
                    detailDTOs,
                    serialMappingDTOs,
                    palletDetailDTOs
                );
            });
    }
}
