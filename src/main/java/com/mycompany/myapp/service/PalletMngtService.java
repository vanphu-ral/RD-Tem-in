package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.PalletMngt;
import com.mycompany.myapp.repository.PalletMngtRepository;
import com.mycompany.myapp.service.dto.PalletMngtDTO;
import com.mycompany.myapp.service.mapper.PalletMngtMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.PalletMngt}.
 */
@Service
@Transactional("partner5TransactionManager")
public class PalletMngtService {

  private static final Logger LOG = LoggerFactory.getLogger(
    PalletMngtService.class
  );

  private final PalletMngtRepository palletMngtRepository;

  private final PalletMngtMapper palletMngtMapper;

  public PalletMngtService(
    PalletMngtRepository palletMngtRepository,
    PalletMngtMapper palletMngtMapper
  ) {
    this.palletMngtRepository = palletMngtRepository;
    this.palletMngtMapper = palletMngtMapper;
  }

  /**
   * Save a palletMngt.
   *
   * @param palletMngtDTO the entity to save.
   * @return the persisted entity.
   */
  public PalletMngtDTO save(PalletMngtDTO palletMngtDTO) {
    LOG.debug("Request to save PalletMngt : {}", palletMngtDTO);
    PalletMngt palletMngt = palletMngtMapper.toEntity(palletMngtDTO);
    palletMngt = palletMngtRepository.save(palletMngt);
    return palletMngtMapper.toDto(palletMngt);
  }

  /**
   * Update a palletMngt.
   *
   * @param palletMngtDTO the entity to save.
   * @return the persisted entity.
   */
  public PalletMngtDTO update(PalletMngtDTO palletMngtDTO) {
    LOG.debug("Request to update PalletMngt : {}", palletMngtDTO);
    PalletMngt palletMngt = palletMngtMapper.toEntity(palletMngtDTO);
    palletMngt = palletMngtRepository.save(palletMngt);
    return palletMngtMapper.toDto(palletMngt);
  }

  /**
   * Partially update a palletMngt.
   *
   * @param palletMngtDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<PalletMngtDTO> partialUpdate(PalletMngtDTO palletMngtDTO) {
    LOG.debug("Request to partially update PalletMngt : {}", palletMngtDTO);

    return palletMngtRepository
      .findById(palletMngtDTO.getId())
      .map(existingPalletMngt -> {
        palletMngtMapper.partialUpdate(existingPalletMngt, palletMngtDTO);

        return existingPalletMngt;
      })
      .map(palletMngtRepository::save)
      .map(palletMngtMapper::toDto);
  }

  /**
   * Get all the palletMngts.
   *
   * @return the list of entities.
   */
  @Transactional(value = "partner5TransactionManager", readOnly = true)
  public List<PalletMngtDTO> findAll() {
    LOG.debug("Request to get all PalletMngts");
    return palletMngtRepository
      .findAll()
      .stream()
      .map(palletMngtMapper::toDto)
      .collect(Collectors.toCollection(LinkedList::new));
  }

  /**
   * Get one palletMngt by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(value = "partner5TransactionManager", readOnly = true)
  public Optional<PalletMngtDTO> findOne(Long id) {
    LOG.debug("Request to get PalletMngt : {}", id);
    return palletMngtRepository.findById(id).map(palletMngtMapper::toDto);
  }

  /**
   * Delete the palletMngt by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    LOG.debug("Request to delete PalletMngt : {}", id);
    palletMngtRepository.deleteById(id);
  }
}
