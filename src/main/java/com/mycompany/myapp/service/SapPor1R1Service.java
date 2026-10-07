package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.SapPor1R1;
import com.mycompany.myapp.repository.SapPor1R1Repository;
import com.mycompany.myapp.service.dto.SapPor1R1DTO;
import com.mycompany.myapp.service.mapper.SapPor1R1Mapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.SapPor1R1}.
 */
@Service
@Transactional("partner5TransactionManager")
public class SapPor1R1Service {

    private static final Logger LOG = LoggerFactory.getLogger(
        SapPor1R1Service.class
    );

    private final SapPor1R1Repository sapPor1R1Repository;

    private final SapPor1R1Mapper sapPor1R1Mapper;

    public SapPor1R1Service(
        SapPor1R1Repository sapPor1R1Repository,
        SapPor1R1Mapper sapPor1R1Mapper
    ) {
        this.sapPor1R1Repository = sapPor1R1Repository;
        this.sapPor1R1Mapper = sapPor1R1Mapper;
    }

    /**
     * Save a sapPor1R1.
     *
     * @param sapPor1R1DTO the entity to save.
     * @return the persisted entity.
     */
    public SapPor1R1DTO save(SapPor1R1DTO sapPor1R1DTO) {
        LOG.debug("Request to save SapPor1R1 : {}", sapPor1R1DTO);
        SapPor1R1 sapPor1R1 = sapPor1R1Mapper.toEntity(sapPor1R1DTO);
        sapPor1R1 = sapPor1R1Repository.save(sapPor1R1);
        return sapPor1R1Mapper.toDto(sapPor1R1);
    }

    /**
     * Save a list of sapPor1R1s.
     *
     * @param sapPor1R1DTOs the list of entities to save.
     * @return the persisted entities.
     */
    public List<SapPor1R1DTO> saveAll(List<SapPor1R1DTO> sapPor1R1DTOs) {
        LOG.debug("Request to save SapPor1R1s : {}", sapPor1R1DTOs);
        List<SapPor1R1> sapPor1R1s = sapPor1R1Mapper.toEntity(sapPor1R1DTOs);
        sapPor1R1s = sapPor1R1Repository.saveAll(sapPor1R1s);
        return sapPor1R1Mapper.toDto(sapPor1R1s);
    }

    /**
     * Update a sapPor1R1.
     *
     * @param sapPor1R1DTO the entity to save.
     * @return the persisted entity.
     */
    public SapPor1R1DTO update(SapPor1R1DTO sapPor1R1DTO) {
        LOG.debug("Request to update SapPor1R1 : {}", sapPor1R1DTO);
        SapPor1R1 sapPor1R1 = sapPor1R1Mapper.toEntity(sapPor1R1DTO);
        sapPor1R1 = sapPor1R1Repository.save(sapPor1R1);
        return sapPor1R1Mapper.toDto(sapPor1R1);
    }

    /**
     * Partially update a sapPor1R1.
     *
     * @param sapPor1R1DTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SapPor1R1DTO> partialUpdate(SapPor1R1DTO sapPor1R1DTO) {
        LOG.debug("Request to partially update SapPor1R1 : {}", sapPor1R1DTO);

        return sapPor1R1Repository
            .findById(sapPor1R1DTO.getId())
            .map(existingSapPor1R1 -> {
                sapPor1R1Mapper.partialUpdate(existingSapPor1R1, sapPor1R1DTO);

                return existingSapPor1R1;
            })
            .map(sapPor1R1Repository::save)
            .map(sapPor1R1Mapper::toDto);
    }

    /**
     * Get all the sapPor1R1s.
     *
     * @return the list of entities.
     */
    @Transactional(value = "partner5TransactionManager", readOnly = true)
    public List<SapPor1R1DTO> findAll() {
        LOG.debug("Request to get all SapPor1R1s");
        return sapPor1R1Repository
            .findAll()
            .stream()
            .map(sapPor1R1Mapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one sapPor1R1 by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(value = "partner5TransactionManager", readOnly = true)
    public Optional<SapPor1R1DTO> findOne(Long id) {
        LOG.debug("Request to get SapPor1R1 : {}", id);
        return sapPor1R1Repository.findById(id).map(sapPor1R1Mapper::toDto);
    }

    /**
     * Delete the sapPor1R1 by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SapPor1R1 : {}", id);
        sapPor1R1Repository.deleteById(id);
    }
}
