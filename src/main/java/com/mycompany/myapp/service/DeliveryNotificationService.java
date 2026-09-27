package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.DeliveryNotification;
import com.mycompany.myapp.repository.DeliveryNotificationRepository;
import com.mycompany.myapp.service.dto.DeliveryNotificationDTO;
import com.mycompany.myapp.service.mapper.DeliveryNotificationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.DeliveryNotification}.
 */
@Service
@Transactional("partner5TransactionManager")
public class DeliveryNotificationService {

  private static final Logger LOG = LoggerFactory.getLogger(
    DeliveryNotificationService.class
  );

  private final DeliveryNotificationRepository deliveryNotificationRepository;

  private final DeliveryNotificationMapper deliveryNotificationMapper;

  public DeliveryNotificationService(
    DeliveryNotificationRepository deliveryNotificationRepository,
    DeliveryNotificationMapper deliveryNotificationMapper
  ) {
    this.deliveryNotificationRepository = deliveryNotificationRepository;
    this.deliveryNotificationMapper = deliveryNotificationMapper;
  }

  /**
   * Save a deliveryNotification.
   *
   * @param deliveryNotificationDTO the entity to save.
   * @return the persisted entity.
   */
  public DeliveryNotificationDTO save(
    DeliveryNotificationDTO deliveryNotificationDTO
  ) {
    LOG.debug(
      "Request to save DeliveryNotification : {}",
      deliveryNotificationDTO
    );
    DeliveryNotification deliveryNotification =
      deliveryNotificationMapper.toEntity(deliveryNotificationDTO);
    deliveryNotification = deliveryNotificationRepository.save(
      deliveryNotification
    );
    return deliveryNotificationMapper.toDto(deliveryNotification);
  }

  /**
   * Update a deliveryNotification.
   *
   * @param deliveryNotificationDTO the entity to save.
   * @return the persisted entity.
   */
  public DeliveryNotificationDTO update(
    DeliveryNotificationDTO deliveryNotificationDTO
  ) {
    LOG.debug(
      "Request to update DeliveryNotification : {}",
      deliveryNotificationDTO
    );
    DeliveryNotification deliveryNotification =
      deliveryNotificationMapper.toEntity(deliveryNotificationDTO);
    deliveryNotification = deliveryNotificationRepository.save(
      deliveryNotification
    );
    return deliveryNotificationMapper.toDto(deliveryNotification);
  }

  /**
   * Partially update a deliveryNotification.
   *
   * @param deliveryNotificationDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<DeliveryNotificationDTO> partialUpdate(
    DeliveryNotificationDTO deliveryNotificationDTO
  ) {
    LOG.debug(
      "Request to partially update DeliveryNotification : {}",
      deliveryNotificationDTO
    );

    return deliveryNotificationRepository
      .findById(deliveryNotificationDTO.getId())
      .map(existingDeliveryNotification -> {
        deliveryNotificationMapper.partialUpdate(
          existingDeliveryNotification,
          deliveryNotificationDTO
        );

        return existingDeliveryNotification;
      })
      .map(deliveryNotificationRepository::save)
      .map(deliveryNotificationMapper::toDto);
  }

  /**
   * Get one deliveryNotification by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(value = "partner5TransactionManager", readOnly = true)
  public Optional<DeliveryNotificationDTO> findOne(Long id) {
    LOG.debug("Request to get DeliveryNotification : {}", id);
    return deliveryNotificationRepository
      .findById(id)
      .map(deliveryNotificationMapper::toDto);
  }

  /**
   * Delete the deliveryNotification by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    LOG.debug("Request to delete DeliveryNotification : {}", id);
    deliveryNotificationRepository.deleteById(id);
  }
}
