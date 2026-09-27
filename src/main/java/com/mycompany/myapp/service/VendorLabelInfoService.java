package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.VendorLabelInfo;
import com.mycompany.myapp.repository.VendorLabelInfoRepository;
import com.mycompany.myapp.service.dto.VendorLabelInfoDTO;
import com.mycompany.myapp.service.mapper.VendorLabelInfoMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.VendorLabelInfo}.
 */
@Service
@Transactional("partner5TransactionManager")
public class VendorLabelInfoService {

  private static final Logger LOG = LoggerFactory.getLogger(
    VendorLabelInfoService.class
  );

  private final VendorLabelInfoRepository vendorLabelInfoRepository;

  private final VendorLabelInfoMapper vendorLabelInfoMapper;

  public VendorLabelInfoService(
    VendorLabelInfoRepository vendorLabelInfoRepository,
    VendorLabelInfoMapper vendorLabelInfoMapper
  ) {
    this.vendorLabelInfoRepository = vendorLabelInfoRepository;
    this.vendorLabelInfoMapper = vendorLabelInfoMapper;
  }

  /**
   * Save a vendorLabelInfo.
   *
   * @param vendorLabelInfoDTO the entity to save.
   * @return the persisted entity.
   */
  public VendorLabelInfoDTO save(VendorLabelInfoDTO vendorLabelInfoDTO) {
    LOG.debug("Request to save VendorLabelInfo : {}", vendorLabelInfoDTO);
    VendorLabelInfo vendorLabelInfo = vendorLabelInfoMapper.toEntity(
      vendorLabelInfoDTO
    );
    vendorLabelInfo = vendorLabelInfoRepository.save(vendorLabelInfo);
    return vendorLabelInfoMapper.toDto(vendorLabelInfo);
  }

  /**
   * Update a vendorLabelInfo.
   *
   * @param vendorLabelInfoDTO the entity to save.
   * @return the persisted entity.
   */
  public VendorLabelInfoDTO update(VendorLabelInfoDTO vendorLabelInfoDTO) {
    LOG.debug("Request to update VendorLabelInfo : {}", vendorLabelInfoDTO);
    VendorLabelInfo vendorLabelInfo = vendorLabelInfoMapper.toEntity(
      vendorLabelInfoDTO
    );
    vendorLabelInfo = vendorLabelInfoRepository.save(vendorLabelInfo);
    return vendorLabelInfoMapper.toDto(vendorLabelInfo);
  }

  /**
   * Partially update a vendorLabelInfo.
   *
   * @param vendorLabelInfoDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<VendorLabelInfoDTO> partialUpdate(
    VendorLabelInfoDTO vendorLabelInfoDTO
  ) {
    LOG.debug(
      "Request to partially update VendorLabelInfo : {}",
      vendorLabelInfoDTO
    );

    return vendorLabelInfoRepository
      .findById(vendorLabelInfoDTO.getId())
      .map(existingVendorLabelInfo -> {
        vendorLabelInfoMapper.partialUpdate(
          existingVendorLabelInfo,
          vendorLabelInfoDTO
        );

        return existingVendorLabelInfo;
      })
      .map(vendorLabelInfoRepository::save)
      .map(vendorLabelInfoMapper::toDto);
  }

  /**
   * Get all the vendorLabelInfos.
   *
   * @return the list of entities.
   */
  @Transactional(value = "partner5TransactionManager", readOnly = true)
  public List<VendorLabelInfoDTO> findAll() {
    LOG.debug("Request to get all VendorLabelInfos");
    return vendorLabelInfoRepository
      .findAll()
      .stream()
      .map(vendorLabelInfoMapper::toDto)
      .collect(Collectors.toCollection(LinkedList::new));
  }

  /**
   * Get one vendorLabelInfo by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(value = "partner5TransactionManager", readOnly = true)
  public Optional<VendorLabelInfoDTO> findOne(Long id) {
    LOG.debug("Request to get VendorLabelInfo : {}", id);
    return vendorLabelInfoRepository
      .findById(id)
      .map(vendorLabelInfoMapper::toDto);
  }

  /**
   * Delete the vendorLabelInfo by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    LOG.debug("Request to delete VendorLabelInfo : {}", id);
    vendorLabelInfoRepository.deleteById(id);
  }
}
