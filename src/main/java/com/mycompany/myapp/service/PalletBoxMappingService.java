package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.PalletBoxMapping;
import com.mycompany.myapp.repository.PalletBoxMappingRepository;
import com.mycompany.myapp.service.dto.PalletBoxMappingDTO;
import com.mycompany.myapp.service.mapper.PalletBoxMappingMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.PalletBoxMapping}.
 */
@Service
@Transactional("partner5TransactionManager")
public class PalletBoxMappingService {

  private static final Logger LOG = LoggerFactory.getLogger(
    PalletBoxMappingService.class
  );

  private final PalletBoxMappingRepository palletBoxMappingRepository;

  private final PalletBoxMappingMapper palletBoxMappingMapper;

  public PalletBoxMappingService(
    PalletBoxMappingRepository palletBoxMappingRepository,
    PalletBoxMappingMapper palletBoxMappingMapper
  ) {
    this.palletBoxMappingRepository = palletBoxMappingRepository;
    this.palletBoxMappingMapper = palletBoxMappingMapper;
  }

  /**
   * Save a palletBoxMapping.
   *
   * @param palletBoxMappingDTO the entity to save.
   * @return the persisted entity.
   */
  public PalletBoxMappingDTO save(PalletBoxMappingDTO palletBoxMappingDTO) {
    LOG.debug("Request to save PalletBoxMapping : {}", palletBoxMappingDTO);
    PalletBoxMapping palletBoxMapping = palletBoxMappingMapper.toEntity(
      palletBoxMappingDTO
    );
    palletBoxMapping = palletBoxMappingRepository.save(palletBoxMapping);
    return palletBoxMappingMapper.toDto(palletBoxMapping);
  }

  /**
   * Update a palletBoxMapping.
   *
   * @param palletBoxMappingDTO the entity to save.
   * @return the persisted entity.
   */
  public PalletBoxMappingDTO update(PalletBoxMappingDTO palletBoxMappingDTO) {
    LOG.debug("Request to update PalletBoxMapping : {}", palletBoxMappingDTO);
    PalletBoxMapping palletBoxMapping = palletBoxMappingMapper.toEntity(
      palletBoxMappingDTO
    );
    palletBoxMapping = palletBoxMappingRepository.save(palletBoxMapping);
    return palletBoxMappingMapper.toDto(palletBoxMapping);
  }

  /**
   * Partially update a palletBoxMapping.
   *
   * @param palletBoxMappingDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<PalletBoxMappingDTO> partialUpdate(
    PalletBoxMappingDTO palletBoxMappingDTO
  ) {
    LOG.debug(
      "Request to partially update PalletBoxMapping : {}",
      palletBoxMappingDTO
    );

    return palletBoxMappingRepository
      .findById(palletBoxMappingDTO.getId())
      .map(existingPalletBoxMapping -> {
        palletBoxMappingMapper.partialUpdate(
          existingPalletBoxMapping,
          palletBoxMappingDTO
        );

        return existingPalletBoxMapping;
      })
      .map(palletBoxMappingRepository::save)
      .map(palletBoxMappingMapper::toDto);
  }

  /**
   * Get all the palletBoxMappings.
   *
   * @return the list of entities.
   */
  @Transactional(value = "partner5TransactionManager", readOnly = true)
  public List<PalletBoxMappingDTO> findAll() {
    LOG.debug("Request to get all PalletBoxMappings");
    return palletBoxMappingRepository
      .findAll()
      .stream()
      .map(palletBoxMappingMapper::toDto)
      .collect(Collectors.toCollection(LinkedList::new));
  }

  /**
   * Get one palletBoxMapping by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(value = "partner5TransactionManager", readOnly = true)
  public Optional<PalletBoxMappingDTO> findOne(Long id) {
    LOG.debug("Request to get PalletBoxMapping : {}", id);
    return palletBoxMappingRepository
      .findById(id)
      .map(palletBoxMappingMapper::toDto);
  }

  /**
   * Delete the palletBoxMapping by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    LOG.debug("Request to delete PalletBoxMapping : {}", id);
    palletBoxMappingRepository.deleteById(id);
  }
}
