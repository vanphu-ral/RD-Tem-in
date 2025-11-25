package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.GenTemConfig;
import com.mycompany.myapp.repository.partner3.Partner3GenTemConfigRepository;
import com.mycompany.myapp.service.dto.GenTemConfigDTO;
import com.mycompany.myapp.service.mapper.GenTemConfigMapper;
import java.util.Optional;
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

    public GenTemConfigService(
        Partner3GenTemConfigRepository genTemConfigRepository,
        GenTemConfigMapper genTemConfigMapper
    ) {
        this.genTemConfigRepository = genTemConfigRepository;
        this.genTemConfigMapper = genTemConfigMapper;
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
}
