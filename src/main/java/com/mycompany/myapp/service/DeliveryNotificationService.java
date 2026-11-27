package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.DeliveryNotification;
import com.mycompany.myapp.repository.DeliveryNotificationRepository;
import com.mycompany.myapp.service.criteria.DeliveryNotificationCriteria;
import com.mycompany.myapp.service.dto.DeliveryNotificationDTO;
import com.mycompany.myapp.service.mapper.DeliveryNotificationMapper;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.StringFilter;

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

    private final DeliveryNotificationQueryService deliveryNotificationQueryService;

    public DeliveryNotificationService(
        DeliveryNotificationRepository deliveryNotificationRepository,
        DeliveryNotificationMapper deliveryNotificationMapper,
        DeliveryNotificationQueryService deliveryNotificationQueryService
    ) {
        this.deliveryNotificationRepository = deliveryNotificationRepository;
        this.deliveryNotificationMapper = deliveryNotificationMapper;
        this.deliveryNotificationQueryService =
            deliveryNotificationQueryService;
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

    /**
     * Search deliveryNotifications with multiple optional filters.
     *
     * @param deliveryNotificationCode the deliveryNotificationCode to filter by.
     * @param invoiceNumber the invoiceNumber to filter by.
     * @param contractCode the contractCode to filter by.
     * @param vendorName the vendorName to filter by.
     * @param contNo the contNo to filter by.
     * @param entryDate the entryDate to filter by.
     * @param entryDateFrom entryDate lower bound (inclusive).
     * @param entryDateTo entryDate upper bound (inclusive).
     * @param numberOfPo the numberOfPo to filter by.
     * @param numberOfItem the numberOfItem to filter by.
     * @param status the status to filter by.
     * @param source the source to filter by.
     * @param createdAt the createdAt to filter by.
     * @param createdBy the createdBy to filter by.
     * @param deletedAt the deletedAt to filter by.
     * @param deletedBy the deletedBy to filter by.
     * @param receivingStation the receivingStation to filter by.
     * @param assignee the assignee to filter by.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(value = "partner5TransactionManager", readOnly = true)
    public Page<DeliveryNotificationDTO> searchDeliveryNotifications(
        String deliveryNotificationCode,
        String invoiceNumber,
        String contractCode,
        String vendorName,
        String contNo,
        Instant entryDate,
        Instant entryDateFrom,
        Instant entryDateTo,
        Integer numberOfPo,
        Integer numberOfItem,
        String status,
        String source,
        Instant createdAt,
        String createdBy,
        Instant deletedAt,
        String deletedBy,
        String receivingStation,
        String assignee,
        Pageable pageable
    ) {
        LOG.debug("Request to search DeliveryNotifications with filters");

        // Build criteria from individual parameters
        DeliveryNotificationCriteria criteria =
            new DeliveryNotificationCriteria();

        if (
            deliveryNotificationCode != null &&
            !deliveryNotificationCode.trim().isEmpty()
        ) {
            criteria.setDeliveryNotificationCode(new StringFilter());
            criteria
                .getDeliveryNotificationCode()
                .setContains(deliveryNotificationCode);
        }
        if (invoiceNumber != null && !invoiceNumber.trim().isEmpty()) {
            criteria.setInvoiceNumber(new StringFilter());
            criteria.getInvoiceNumber().setContains(invoiceNumber);
        }
        if (contractCode != null && !contractCode.trim().isEmpty()) {
            criteria.setContractCode(new StringFilter());
            criteria.getContractCode().setContains(contractCode);
        }
        if (vendorName != null && !vendorName.trim().isEmpty()) {
            criteria.setVendorName(new StringFilter());
            criteria.getVendorName().setContains(vendorName);
        }
        if (contNo != null && !contNo.trim().isEmpty()) {
            criteria.setContNo(new StringFilter());
            criteria.getContNo().setContains(contNo);
        }
        if (entryDate != null) {
            criteria.setEntryDate(new InstantFilter());
            criteria.getEntryDate().setEquals(entryDate);
        }
        if (entryDateFrom != null) {
            criteria.entryDate().setGreaterThanOrEqual(entryDateFrom);
        }
        if (entryDateTo != null) {
            criteria.entryDate().setLessThanOrEqual(entryDateTo);
        }
        if (numberOfPo != null) {
            criteria.setNumberOfPo(new IntegerFilter());
            criteria.getNumberOfPo().setEquals(numberOfPo);
        }
        if (numberOfItem != null) {
            criteria.setNumberOfItem(new IntegerFilter());
            criteria.getNumberOfItem().setEquals(numberOfItem);
        }
        if (status != null && !status.trim().isEmpty()) {
            criteria.setStatus(new StringFilter());
            criteria.getStatus().setContains(status);
        }
        if (source != null && !source.trim().isEmpty()) {
            criteria.setSource(new StringFilter());
            criteria.getSource().setContains(source);
        }
        if (createdAt != null) {
            criteria.setCreatedAt(new InstantFilter());
            criteria.getCreatedAt().setEquals(createdAt);
        }
        if (createdBy != null && !createdBy.trim().isEmpty()) {
            criteria.setCreatedBy(new StringFilter());
            criteria.getCreatedBy().setContains(createdBy);
        }
        if (deletedAt != null) {
            criteria.setDeletedAt(new InstantFilter());
            criteria.getDeletedAt().setEquals(deletedAt);
        }
        if (deletedBy != null && !deletedBy.trim().isEmpty()) {
            criteria.setDeletedBy(new StringFilter());
            criteria.getDeletedBy().setContains(deletedBy);
        }
        if (receivingStation != null && !receivingStation.trim().isEmpty()) {
            criteria.setReceivingStation(new StringFilter());
            criteria.getReceivingStation().setContains(receivingStation);
        }
        if (assignee != null && !assignee.trim().isEmpty()) {
            criteria.setAssignee(new StringFilter());
            criteria.getAssignee().setContains(assignee);
        }

        return deliveryNotificationQueryService.findByCriteria(
            criteria,
            pageable
        );
    }
}
