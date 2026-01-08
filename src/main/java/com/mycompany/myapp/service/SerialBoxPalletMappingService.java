package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.SerialBoxPalletMapping;
import com.mycompany.myapp.domain.WarehouseNoteInfo;
import com.mycompany.myapp.repository.partner3.Partner3SerialBoxPalletMappingRepository;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.dto.SerialBoxPalletMappingDTO;
import com.mycompany.myapp.service.dto.SerialBoxPalletMappingInsertDTO;
import com.mycompany.myapp.service.mapper.SerialBoxPalletMappingMapper;
import java.time.Instant;
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
 * {@link com.mycompany.myapp.domain.SerialBoxPalletMapping}.
 */
@Service
@Transactional("partner3TransactionManager")
public class SerialBoxPalletMappingService {

    private static final Logger LOG = LoggerFactory.getLogger(
        SerialBoxPalletMappingService.class
    );

    private final Partner3SerialBoxPalletMappingRepository serialBoxPalletMappingRepository;

    private final SerialBoxPalletMappingMapper serialBoxPalletMappingMapper;

    private final Partner3WarehouseStampInfoRepository partner3WarehouseStampInfoRepository;

    public SerialBoxPalletMappingService(
        Partner3SerialBoxPalletMappingRepository serialBoxPalletMappingRepository,
        SerialBoxPalletMappingMapper serialBoxPalletMappingMapper,
        Partner3WarehouseStampInfoRepository partner3WarehouseStampInfoRepository
    ) {
        this.serialBoxPalletMappingRepository =
            serialBoxPalletMappingRepository;
        this.serialBoxPalletMappingMapper = serialBoxPalletMappingMapper;
        this.partner3WarehouseStampInfoRepository =
            partner3WarehouseStampInfoRepository;
    }

    /**
     * Save a serialBoxPalletMapping.
     *
     * @param serialBoxPalletMappingDTO the entity to save.
     * @return the persisted entity.
     */
    public SerialBoxPalletMappingDTO save(
        SerialBoxPalletMappingDTO serialBoxPalletMappingDTO
    ) {
        LOG.debug(
            "Request to save SerialBoxPalletMapping : {}",
            serialBoxPalletMappingDTO
        );
        SerialBoxPalletMapping serialBoxPalletMapping =
            serialBoxPalletMappingMapper.toEntity(serialBoxPalletMappingDTO);
        if (serialBoxPalletMappingDTO.getMaLenhSanXuatId() != null) {
            WarehouseNoteInfo warehouseStampInfo =
                partner3WarehouseStampInfoRepository
                    .findById(serialBoxPalletMappingDTO.getMaLenhSanXuatId())
                    .orElseThrow(() ->
                        new RuntimeException(
                            "WarehouseNoteInfo not found with id: " +
                            serialBoxPalletMappingDTO.getMaLenhSanXuatId()
                        )
                    );
            serialBoxPalletMapping.setMaLenhSanXuat(warehouseStampInfo);
        }
        serialBoxPalletMapping = serialBoxPalletMappingRepository.save(
            serialBoxPalletMapping
        );
        return serialBoxPalletMappingMapper.toDto(serialBoxPalletMapping);
    }

    /**
     * Insert a new SerialBoxPalletMapping with maLenhSanXuat.
     *
     * @param maLenhSanXuatId the id of WarehouseNoteInfo.
     * @param insertDTO       the insert data.
     * @return the persisted entity.
     */
    public SerialBoxPalletMappingDTO insertWithMaLenhSanXuat(
        Long maLenhSanXuatId,
        SerialBoxPalletMappingInsertDTO insertDTO
    ) {
        LOG.debug(
            "Request to insert SerialBoxPalletMapping with maLenhSanXuatId : {} and data : {}",
            maLenhSanXuatId,
            insertDTO
        );

        SerialBoxPalletMapping serialBoxPalletMapping =
            new SerialBoxPalletMapping();
        serialBoxPalletMapping.setSerialBox(insertDTO.getSerialBox());
        serialBoxPalletMapping.setSerialPallet(insertDTO.getSerialPallet());
        serialBoxPalletMapping.setStatus(insertDTO.getStatus());
        serialBoxPalletMapping.setUpdatedAt(Instant.now());
        // Use updatedBy from payload if provided, otherwise use current user or
        // "system"
        String updatedBy = insertDTO.getUpdatedBy();
        if (updatedBy == null || updatedBy.trim().isEmpty()) {
            updatedBy = SecurityUtils.getCurrentUserLogin().orElse("system");
        }
        serialBoxPalletMapping.setUpdatedBy(updatedBy);

        if (maLenhSanXuatId != null) {
            WarehouseNoteInfo warehouseStampInfo =
                partner3WarehouseStampInfoRepository
                    .findById(maLenhSanXuatId)
                    .orElseThrow(() ->
                        new RuntimeException(
                            "WarehouseNoteInfo not found with id: " +
                            maLenhSanXuatId
                        )
                    );
            serialBoxPalletMapping.setMaLenhSanXuat(warehouseStampInfo);
        }

        serialBoxPalletMapping = serialBoxPalletMappingRepository.save(
            serialBoxPalletMapping
        );
        return serialBoxPalletMappingMapper.toDto(serialBoxPalletMapping);
    }

    /**
     * Update a serialBoxPalletMapping.
     *
     * @param serialBoxPalletMappingDTO the entity to save.
     * @return the persisted entity.
     */
    public SerialBoxPalletMappingDTO update(
        SerialBoxPalletMappingDTO serialBoxPalletMappingDTO
    ) {
        LOG.debug(
            "Request to update SerialBoxPalletMapping : {}",
            serialBoxPalletMappingDTO
        );
        SerialBoxPalletMapping serialBoxPalletMapping =
            serialBoxPalletMappingMapper.toEntity(serialBoxPalletMappingDTO);
        serialBoxPalletMapping = serialBoxPalletMappingRepository.save(
            serialBoxPalletMapping
        );
        return serialBoxPalletMappingMapper.toDto(serialBoxPalletMapping);
    }

    /**
     * Partially update a serialBoxPalletMapping.
     *
     * @param serialBoxPalletMappingDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SerialBoxPalletMappingDTO> partialUpdate(
        SerialBoxPalletMappingDTO serialBoxPalletMappingDTO
    ) {
        LOG.debug(
            "Request to partially update SerialBoxPalletMapping : {}",
            serialBoxPalletMappingDTO
        );

        return serialBoxPalletMappingRepository
            .findById(serialBoxPalletMappingDTO.getId())
            .map(existingSerialBoxPalletMapping -> {
                serialBoxPalletMappingMapper.partialUpdate(
                    existingSerialBoxPalletMapping,
                    serialBoxPalletMappingDTO
                );

                return existingSerialBoxPalletMapping;
            })
            .map(serialBoxPalletMappingRepository::save)
            .map(serialBoxPalletMappingMapper::toDto);
    }

    /**
     * Get all the serialBoxPalletMappings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SerialBoxPalletMappingDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SerialBoxPalletMappings");
        return serialBoxPalletMappingRepository
            .findAll(pageable)
            .map(serialBoxPalletMappingMapper::toDto);
    }

    /**
     * Get one serialBoxPalletMapping by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SerialBoxPalletMappingDTO> findOne(Long id) {
        LOG.debug("Request to get SerialBoxPalletMapping : {}", id);
        return serialBoxPalletMappingRepository
            .findById(id)
            .map(serialBoxPalletMappingMapper::toDto);
    }

    /**
     * Get all serialBoxPalletMappings by serialPallet.
     *
     * @param serialPallet the serial pallet.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SerialBoxPalletMappingDTO> findBySerialPallet(
        String serialPallet
    ) {
        LOG.debug(
            "Request to get SerialBoxPalletMappings by serialPallet : {}",
            serialPallet
        );
        return serialBoxPalletMappingRepository
            .findBySerialPallet(serialPallet)
            .stream()
            .map(serialBoxPalletMappingMapper::toDto)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get all serialBoxPalletMappings by maLenhSanXuatId.
     *
     * @param maLenhSanXuatId the id of maLenhSanXuat.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SerialBoxPalletMappingDTO> findByMaLenhSanXuatId(
        Long maLenhSanXuatId
    ) {
        LOG.debug(
            "Request to get SerialBoxPalletMappings by maLenhSanXuatId : {}",
            maLenhSanXuatId
        );
        return serialBoxPalletMappingRepository
            .findByMaLenhSanXuatId(maLenhSanXuatId)
            .stream()
            .map(serialBoxPalletMappingMapper::toDto)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Delete the serialBoxPalletMapping by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SerialBoxPalletMapping : {}", id);
        serialBoxPalletMappingRepository.deleteById(id);
    }
}
