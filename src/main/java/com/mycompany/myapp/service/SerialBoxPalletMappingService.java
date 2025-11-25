package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.SerialBoxPalletMapping;
import com.mycompany.myapp.repository.partner3.Partner3SerialBoxPalletMappingRepository;
import com.mycompany.myapp.service.dto.SerialBoxPalletMappingDTO;
import com.mycompany.myapp.service.mapper.SerialBoxPalletMappingMapper;
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

    public SerialBoxPalletMappingService(
        Partner3SerialBoxPalletMappingRepository serialBoxPalletMappingRepository,
        SerialBoxPalletMappingMapper serialBoxPalletMappingMapper
    ) {
        this.serialBoxPalletMappingRepository =
            serialBoxPalletMappingRepository;
        this.serialBoxPalletMappingMapper = serialBoxPalletMappingMapper;
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
     * Delete the serialBoxPalletMapping by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SerialBoxPalletMapping : {}", id);
        serialBoxPalletMappingRepository.deleteById(id);
    }
}
