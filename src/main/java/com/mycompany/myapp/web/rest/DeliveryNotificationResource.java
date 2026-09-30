package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.DeliveryNotificationRepository;
import com.mycompany.myapp.service.DeliveryNotificationDetailService;
import com.mycompany.myapp.service.DeliveryNotificationQueryService;
import com.mycompany.myapp.service.DeliveryNotificationService;
import com.mycompany.myapp.service.criteria.DeliveryNotificationCriteria;
import com.mycompany.myapp.service.dto.DeliveryNotificationDTO;
import com.mycompany.myapp.service.dto.DeliveryNotificationDetailDTO;
import com.mycompany.myapp.service.dto.VendorLabelInfoDetailDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.DeliveryNotification}.
 */
@RestController
@RequestMapping("/api/delivery-notifications")
public class DeliveryNotificationResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        DeliveryNotificationResource.class
    );

    private static final String ENTITY_NAME = "deliveryNotification";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DeliveryNotificationService deliveryNotificationService;

    private final DeliveryNotificationRepository deliveryNotificationRepository;

    private final DeliveryNotificationQueryService deliveryNotificationQueryService;

    private final DeliveryNotificationDetailService deliveryNotificationDetailService;

    public DeliveryNotificationResource(
        DeliveryNotificationService deliveryNotificationService,
        DeliveryNotificationRepository deliveryNotificationRepository,
        DeliveryNotificationQueryService deliveryNotificationQueryService,
        DeliveryNotificationDetailService deliveryNotificationDetailService
    ) {
        this.deliveryNotificationService = deliveryNotificationService;
        this.deliveryNotificationRepository = deliveryNotificationRepository;
        this.deliveryNotificationQueryService =
            deliveryNotificationQueryService;
        this.deliveryNotificationDetailService =
            deliveryNotificationDetailService;
    }

    /**
     * {@code POST  /delivery-notifications} : Create a new deliveryNotification.
     *
     * @param deliveryNotificationDTO the deliveryNotificationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new deliveryNotificationDTO, or with status {@code 400 (Bad Request)} if the deliveryNotification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DeliveryNotificationDTO> createDeliveryNotification(
        @Valid @RequestBody DeliveryNotificationDTO deliveryNotificationDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to save DeliveryNotification : {}",
            deliveryNotificationDTO
        );
        if (deliveryNotificationDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new deliveryNotification cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        deliveryNotificationDTO = deliveryNotificationService.save(
            deliveryNotificationDTO
        );
        return ResponseEntity.created(
            new URI(
                "/api/delivery-notifications/" + deliveryNotificationDTO.getId()
            )
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    deliveryNotificationDTO.getId().toString()
                )
            )
            .body(deliveryNotificationDTO);
    }

    /**
     * {@code PUT  /delivery-notifications/:id} : Updates an existing deliveryNotification.
     *
     * @param id the id of the deliveryNotificationDTO to save.
     * @param deliveryNotificationDTO the deliveryNotificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deliveryNotificationDTO,
     * or with status {@code 400 (Bad Request)} if the deliveryNotificationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the deliveryNotificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DeliveryNotificationDTO> updateDeliveryNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DeliveryNotificationDTO deliveryNotificationDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to update DeliveryNotification : {}, {}",
            id,
            deliveryNotificationDTO
        );
        if (deliveryNotificationDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, deliveryNotificationDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!deliveryNotificationRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        deliveryNotificationDTO = deliveryNotificationService.update(
            deliveryNotificationDTO
        );
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    deliveryNotificationDTO.getId().toString()
                )
            )
            .body(deliveryNotificationDTO);
    }

    /**
     * {@code PATCH  /delivery-notifications/:id} : Partial updates given fields of an existing deliveryNotification, field will ignore if it is null
     *
     * @param id the id of the deliveryNotificationDTO to save.
     * @param deliveryNotificationDTO the deliveryNotificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deliveryNotificationDTO,
     * or with status {@code 400 (Bad Request)} if the deliveryNotificationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the deliveryNotificationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the deliveryNotificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<
        DeliveryNotificationDTO
    > partialUpdateDeliveryNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DeliveryNotificationDTO deliveryNotificationDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update DeliveryNotification partially : {}, {}",
            id,
            deliveryNotificationDTO
        );
        if (deliveryNotificationDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, deliveryNotificationDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!deliveryNotificationRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<DeliveryNotificationDTO> result =
            deliveryNotificationService.partialUpdate(deliveryNotificationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                deliveryNotificationDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /delivery-notifications} : get all the deliveryNotifications.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of deliveryNotifications in body.
     */
    @GetMapping("")
    public ResponseEntity<
        List<DeliveryNotificationDTO>
    > getAllDeliveryNotifications(
        DeliveryNotificationCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug(
            "REST request to get DeliveryNotifications by criteria: {}",
            criteria
        );

        Page<DeliveryNotificationDTO> page =
            deliveryNotificationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /delivery-notifications/count} : count all the deliveryNotifications.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDeliveryNotifications(
        DeliveryNotificationCriteria criteria
    ) {
        LOG.debug(
            "REST request to count DeliveryNotifications by criteria: {}",
            criteria
        );
        return ResponseEntity.ok().body(
            deliveryNotificationQueryService.countByCriteria(criteria)
        );
    }

    /**
     * {@code GET  /delivery-notifications/:id} : get the "id" deliveryNotification.
     *
     * @param id the id of the deliveryNotificationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the deliveryNotificationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryNotificationDTO> getDeliveryNotification(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to get DeliveryNotification : {}", id);
        Optional<DeliveryNotificationDTO> deliveryNotificationDTO =
            deliveryNotificationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(deliveryNotificationDTO);
    }

    /**
     * {@code GET  /delivery-notifications/:id/detail} : get the "id" deliveryNotification with all its SapPor1R1 lines and the VendorLabelInfo of every line (enriched with serialPallet).
     *
     * @param id the id of the deliveryNotificationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the deliveryNotification detail, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<
        DeliveryNotificationDetailDTO
    > getDeliveryNotificationDetail(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DeliveryNotification detail : {}", id);
        Optional<DeliveryNotificationDetailDTO> detail =
            deliveryNotificationDetailService.findOneWithDetail(id);
        return ResponseUtil.wrapOrNotFound(detail);
    }

    /**
     * {@code GET  /delivery-notifications/:id/vendor-label-infos} : get the flat list of
     * vendorLabelInfo linked to the delivery notification, enriched with the serialPallet
     * (resolved from pallet_box_mapping.reel_id_box = vendor_label_info.reel_id).
     *
     * @param id the id of the delivery notification.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of
     *         vendorLabelInfo (with serialPallet) in the body.
     */
    @GetMapping("/{id}/vendor-label-infos")
    public ResponseEntity<
        List<VendorLabelInfoDetailDTO>
    > getVendorLabelInfosWithPallet(@PathVariable("id") Long id) {
        LOG.debug(
            "REST request to get VendorLabelInfo list with serialPallet by deliveryNotification : {}",
            id
        );
        List<VendorLabelInfoDetailDTO> result =
            deliveryNotificationDetailService.findVendorLabelInfosWithPalletByDeliveryNotificationId(
                id
            );
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code DELETE  /delivery-notifications/:id} : delete the "id" deliveryNotification.
     *
     * @param id the id of the deliveryNotificationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeliveryNotification(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to delete DeliveryNotification : {}", id);
        deliveryNotificationService.delete(id);
        return ResponseEntity.noContent()
            .headers(
                HeaderUtil.createEntityDeletionAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    id.toString()
                )
            )
            .build();
    }
}
