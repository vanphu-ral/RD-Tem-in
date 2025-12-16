package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.GenTemConfig;
import com.mycompany.myapp.repository.partner3.Partner3GenTemConfigRepository;
import com.mycompany.myapp.service.dto.GenTemConfigDTO;
import com.mycompany.myapp.service.dto.GenTemConfigSimpleDTO;
import com.mycompany.myapp.service.mapper.GenTemConfigMapper;
import com.mycompany.myapp.service.mapper.GenTemConfigSimpleMapper;
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
 * {@link com.mycompany.myapp.domain.GenTemConfig}.
 */
@Service
@Transactional("partner3TransactionManager")
public class GenTemConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(
        GenTemConfigService.class
    );

    private final Partner3GenTemConfigRepository genTemConfigRepository;

    private final GenTemConfigMapper genTemConfigMapper;

    private final GenTemConfigSimpleMapper genTemConfigSimpleMapper;

    public GenTemConfigService(
        Partner3GenTemConfigRepository genTemConfigRepository,
        GenTemConfigMapper genTemConfigMapper,
        GenTemConfigSimpleMapper genTemConfigSimpleMapper
    ) {
        this.genTemConfigRepository = genTemConfigRepository;
        this.genTemConfigMapper = genTemConfigMapper;
        this.genTemConfigSimpleMapper = genTemConfigSimpleMapper;
    }

    /**
     * Save a genTemConfig.
     *
     * @param genTemConfigDTO the entity to save.
     * @return the persisted entity.
     */
    public GenTemConfigDTO save(GenTemConfigDTO genTemConfigDTO) {
        LOG.debug("Request to save GenTemConfig : {}", genTemConfigDTO);
        GenTemConfig genTemConfig = genTemConfigMapper.toEntity(
            genTemConfigDTO
        );
        genTemConfig = genTemConfigRepository.save(genTemConfig);
        return genTemConfigMapper.toDto(genTemConfig);
    }

    /**
     * Update a genTemConfig.
     *
     * @param genTemConfigDTO the entity to save.
     * @return the persisted entity.
     */
    public GenTemConfigDTO update(GenTemConfigDTO genTemConfigDTO) {
        LOG.debug("Request to update GenTemConfig : {}", genTemConfigDTO);
        GenTemConfig genTemConfig = genTemConfigMapper.toEntity(
            genTemConfigDTO
        );
        genTemConfig = genTemConfigRepository.save(genTemConfig);
        return genTemConfigMapper.toDto(genTemConfig);
    }

    /**
     * Partially update a genTemConfig.
     *
     * @param genTemConfigDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<GenTemConfigDTO> partialUpdate(
        GenTemConfigDTO genTemConfigDTO
    ) {
        LOG.debug(
            "Request to partially update GenTemConfig : {}",
            genTemConfigDTO
        );

        return genTemConfigRepository
            .findById(genTemConfigDTO.getId())
            .map(existingGenTemConfig -> {
                genTemConfigMapper.partialUpdate(
                    existingGenTemConfig,
                    genTemConfigDTO
                );

                return existingGenTemConfig;
            })
            .map(genTemConfigRepository::save)
            .map(genTemConfigMapper::toDto);
    }

    /**
     * Get all the genTemConfigs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<GenTemConfigDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all GenTemConfigs");
        return genTemConfigRepository
            .findAll(pageable)
            .map(genTemConfigMapper::toDto);
    }

    /**
     * Get one genTemConfig by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GenTemConfigDTO> findOne(Long id) {
        LOG.debug("Request to get GenTemConfig : {}", id);
        return genTemConfigRepository
            .findById(id)
            .map(genTemConfigMapper::toDto);
    }

    /**
     * Delete the genTemConfig by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete GenTemConfig : {}", id);
        genTemConfigRepository.deleteById(id);
    }

    /**
     * Get all genTemConfigs by ma_lenh_san_xuat_id.
     *
     * @param maLenhSanXuatId the id of the WarehouseNoteInfo.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<GenTemConfigDTO> findByMaLenhSanXuatId(Long maLenhSanXuatId) {
        LOG.debug(
            "Request to get GenTemConfigs by maLenhSanXuatId : {}",
            maLenhSanXuatId
        );
        return genTemConfigRepository
            .findByMaLenhSanXuatId(maLenhSanXuatId)
            .stream()
            .map(genTemConfigMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Save a genTemConfig using simple DTO.
     *
     * @param genTemConfigSimpleDTO the entity to save.
     * @return the persisted entity.
     */
    public GenTemConfigSimpleDTO saveSimple(
        GenTemConfigSimpleDTO genTemConfigSimpleDTO
    ) {
        LOG.debug(
            "Request to save GenTemConfig (simple) : {}",
            genTemConfigSimpleDTO
        );
        GenTemConfig genTemConfig = genTemConfigSimpleMapper.toEntity(
            genTemConfigSimpleDTO
        );
        genTemConfig = genTemConfigRepository.save(genTemConfig);
        return genTemConfigSimpleMapper.toDto(genTemConfig);
    }

    /**
     * Get all genTemConfigs by ma_lenh_san_xuat_id using simple DTO.
     *
     * @param maLenhSanXuatId the id of the WarehouseNoteInfo.
     * @return the list of simple entities.
     */
    @Transactional(readOnly = true)
    public List<GenTemConfigSimpleDTO> findSimpleByMaLenhSanXuatId(
        Long maLenhSanXuatId
    ) {
        LOG.debug(
            "Request to get GenTemConfigs (simple) by maLenhSanXuatId : {}",
            maLenhSanXuatId
        );
        return genTemConfigRepository
            .findByMaLenhSanXuatId(maLenhSanXuatId)
            .stream()
            .map(genTemConfigSimpleMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Upsert a genTemConfig using simple DTO.
     * If a record with the given ma_lenh_san_xuat_id exists, update it.
     * Otherwise, insert a new record.
     *
     * @param genTemConfigSimpleDTO the entity to upsert.
     * @return the persisted entity.
     */
    public GenTemConfigSimpleDTO upsertSimple(
        GenTemConfigSimpleDTO genTemConfigSimpleDTO
    ) {
        LOG.debug(
            "Request to upsert GenTemConfig (simple) : {}",
            genTemConfigSimpleDTO
        );

        if (genTemConfigSimpleDTO.getMaLenhSanXuatId() == null) {
            throw new IllegalArgumentException(
                "ma_lenh_san_xuat_id is required for upsert operation"
            );
        }

        // Find existing record by ma_lenh_san_xuat_id
        List<GenTemConfig> existingRecords =
            genTemConfigRepository.findByMaLenhSanXuatId(
                Long.valueOf(genTemConfigSimpleDTO.getMaLenhSanXuatId())
            );

        GenTemConfig genTemConfig;
        if (!existingRecords.isEmpty()) {
            // Update existing record (take the first one if multiple exist)
            genTemConfig = existingRecords.get(0);
            // Map the DTO fields to the existing entity
            if (genTemConfigSimpleDTO.getTpNk() != null) {
                genTemConfig.setTpNk(genTemConfigSimpleDTO.getTpNk());
            }
            if (genTemConfigSimpleDTO.getRank() != null) {
                genTemConfig.setRank(genTemConfigSimpleDTO.getRank());
            }
            if (genTemConfigSimpleDTO.getMfg() != null) {
                genTemConfig.setMfg(genTemConfigSimpleDTO.getMfg());
            }
            if (genTemConfigSimpleDTO.getQuantityPerBox() != null) {
                genTemConfig.setQuantityPerBox(
                    genTemConfigSimpleDTO.getQuantityPerBox()
                );
            }
            if (genTemConfigSimpleDTO.getNote() != null) {
                genTemConfig.setNote(genTemConfigSimpleDTO.getNote());
            }
            if (genTemConfigSimpleDTO.getNumBoxPerPallet() != null) {
                genTemConfig.setNumBoxPerPallet(
                    genTemConfigSimpleDTO.getNumBoxPerPallet()
                );
            }
            if (genTemConfigSimpleDTO.getBranch() != null) {
                genTemConfig.setBranch(genTemConfigSimpleDTO.getBranch());
            }
            if (genTemConfigSimpleDTO.getGroupName() != null) {
                genTemConfig.setGroupName(genTemConfigSimpleDTO.getGroupName());
            }
            if (genTemConfigSimpleDTO.getCustomerName() != null) {
                genTemConfig.setCustomerName(
                    genTemConfigSimpleDTO.getCustomerName()
                );
            }
            if (genTemConfigSimpleDTO.getPoNumber() != null) {
                genTemConfig.setPoNumber(genTemConfigSimpleDTO.getPoNumber());
            }
            if (genTemConfigSimpleDTO.getDateCode() != null) {
                genTemConfig.setDateCode(genTemConfigSimpleDTO.getDateCode());
            }
            if (genTemConfigSimpleDTO.getItemNoSku() != null) {
                genTemConfig.setItemNoSku(genTemConfigSimpleDTO.getItemNoSku());
            }
            if (genTemConfigSimpleDTO.getQdsxNo() != null) {
                genTemConfig.setQdsxNo(genTemConfigSimpleDTO.getQdsxNo());
            }
            if (genTemConfigSimpleDTO.getProductionDate() != null) {
                genTemConfig.setProductionDate(
                    genTemConfigSimpleDTO.getProductionDate()
                );
            }
            if (genTemConfigSimpleDTO.getInspectorName() != null) {
                genTemConfig.setInspectorName(
                    genTemConfigSimpleDTO.getInspectorName()
                );
            }
            if (genTemConfigSimpleDTO.getInspectionResult() != null) {
                genTemConfig.setInspectionResult(
                    genTemConfigSimpleDTO.getInspectionResult()
                );
            }
            if (genTemConfigSimpleDTO.getUpdatedAt() != null) {
                genTemConfig.setUpdatedAt(genTemConfigSimpleDTO.getUpdatedAt());
            }
            if (genTemConfigSimpleDTO.getUpdatedBy() != null) {
                genTemConfig.setUpdatedBy(genTemConfigSimpleDTO.getUpdatedBy());
            }
            LOG.debug(
                "Updating existing GenTemConfig with id: {}",
                genTemConfig.getId()
            );
        } else {
            // Insert new record
            genTemConfig = genTemConfigSimpleMapper.toEntity(
                genTemConfigSimpleDTO
            );
            LOG.debug("Inserting new GenTemConfig");
        }

        genTemConfig = genTemConfigRepository.save(genTemConfig);
        return genTemConfigSimpleMapper.toDto(genTemConfig);
    }
}
