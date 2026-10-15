package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.DetailDelivery;
import com.mycompany.myapp.repository.DetailDeliveryRepository;
import com.mycompany.myapp.service.dto.DetailDeliveryDTO;
import com.mycompany.myapp.service.mapper.DetailDeliveryMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.DetailDelivery}.
 */
@Service
@Transactional
public class DetailDeliveryService {

    private final Logger log = LoggerFactory.getLogger(
        DetailDeliveryService.class
    );

    private final DetailDeliveryRepository detailDeliveryRepository;

    private final DetailDeliveryMapper detailDeliveryMapper;

    public DetailDeliveryService(
        DetailDeliveryRepository detailDeliveryRepository,
        DetailDeliveryMapper detailDeliveryMapper
    ) {
        this.detailDeliveryRepository = detailDeliveryRepository;
        this.detailDeliveryMapper = detailDeliveryMapper;
    }

    /**
     * Save a detailDelivery.
     *
     * @param detailDeliveryDTO the entity to save.
     * @return the persisted entity.
     */
    public DetailDeliveryDTO save(DetailDeliveryDTO detailDeliveryDTO) {
        log.debug("Request to save DetailDelivery : {}", detailDeliveryDTO);
        DetailDelivery detailDelivery = detailDeliveryMapper.toEntity(
            detailDeliveryDTO
        );
        detailDelivery = detailDeliveryRepository.save(detailDelivery);
        return detailDeliveryMapper.toDto(detailDelivery);
    }

    /**
     * Update a detailDelivery.
     *
     * @param detailDeliveryDTO the entity to save.
     * @return the persisted entity.
     */
    public DetailDeliveryDTO update(DetailDeliveryDTO detailDeliveryDTO) {
        log.debug("Request to update DetailDelivery : {}", detailDeliveryDTO);
        DetailDelivery detailDelivery = detailDeliveryMapper.toEntity(
            detailDeliveryDTO
        );
        detailDelivery = detailDeliveryRepository.save(detailDelivery);
        return detailDeliveryMapper.toDto(detailDelivery);
    }

    /**
     * Partially update a detailDelivery.
     *
     * @param detailDeliveryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DetailDeliveryDTO> partialUpdate(
        DetailDeliveryDTO detailDeliveryDTO
    ) {
        log.debug(
            "Request to partially update DetailDelivery : {}",
            detailDeliveryDTO
        );

        return detailDeliveryRepository
            .findById(detailDeliveryDTO.getLineId())
            .map(existingDetailDelivery -> {
                detailDeliveryMapper.partialUpdate(
                    existingDetailDelivery,
                    detailDeliveryDTO
                );

                return existingDetailDelivery;
            })
            .map(detailDeliveryRepository::save)
            .map(detailDeliveryMapper::toDto);
    }

    /**
     * Get all the detailDeliveries.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<DetailDeliveryDTO> findAll() {
        log.debug("Request to get all DetailDeliveries");
        return detailDeliveryRepository
            .findAll()
            .stream()
            .map(detailDeliveryMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the detailDeliveries with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<DetailDeliveryDTO> findAllWithEagerRelationships(
        Pageable pageable
    ) {
        return detailDeliveryRepository
            .findAllWithEagerRelationships(pageable)
            .map(detailDeliveryMapper::toDto);
    }

    /**
     * Get one detailDelivery by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DetailDeliveryDTO> findOne(UUID id) {
        log.debug("Request to get DetailDelivery : {}", id);
        return detailDeliveryRepository
            .findOneWithEagerRelationships(id)
            .map(detailDeliveryMapper::toDto);
    }

    /**
     * Delete the detailDelivery by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete DetailDelivery : {}", id);
        detailDeliveryRepository.deleteById(id);
    }
}
