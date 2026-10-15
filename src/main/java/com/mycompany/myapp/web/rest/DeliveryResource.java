package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.DeliveryRepository;
import com.mycompany.myapp.service.DeliveryQueryService;
import com.mycompany.myapp.service.DeliveryService;
import com.mycompany.myapp.service.criteria.DeliveryCriteria;
import com.mycompany.myapp.service.dto.DeliveryDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Delivery}.
 */
@RestController
@RequestMapping("/api/deliveries")
public class DeliveryResource {

    private final Logger log = LoggerFactory.getLogger(DeliveryResource.class);

    private static final String ENTITY_NAME = "delivery";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DeliveryService deliveryService;

    private final DeliveryRepository deliveryRepository;

    private final DeliveryQueryService deliveryQueryService;

    public DeliveryResource(
        DeliveryService deliveryService,
        DeliveryRepository deliveryRepository,
        DeliveryQueryService deliveryQueryService
    ) {
        this.deliveryService = deliveryService;
        this.deliveryRepository = deliveryRepository;
        this.deliveryQueryService = deliveryQueryService;
    }

    /**
     * {@code POST  /deliveries} : Create a new delivery.
     *
     * @param deliveryDTO the deliveryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new deliveryDTO, or with status {@code 400 (Bad Request)} if the delivery has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DeliveryDTO> createDelivery(
        @Valid @RequestBody DeliveryDTO deliveryDTO
    ) throws URISyntaxException {
        log.debug("REST request to save Delivery : {}", deliveryDTO);
        if (deliveryDTO.getDeliveryId() != null) {
            throw new BadRequestAlertException(
                "A new delivery cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        deliveryDTO = deliveryService.save(deliveryDTO);
        return ResponseEntity.created(
            new URI("/api/deliveries/" + deliveryDTO.getDeliveryId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    deliveryDTO.getDeliveryId().toString()
                )
            )
            .body(deliveryDTO);
    }

    /**
     * {@code PUT  /deliveries/:id} : Updates an existing delivery.
     *
     * @param id the id of the deliveryDTO to save.
     * @param deliveryDTO the deliveryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deliveryDTO,
     * or with status {@code 400 (Bad Request)} if the deliveryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the deliveryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DeliveryDTO> updateDelivery(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody DeliveryDTO deliveryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Delivery : {}, {}", id, deliveryDTO);
        if (deliveryDTO.getDeliveryId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, deliveryDTO.getDeliveryId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!deliveryRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        deliveryDTO = deliveryService.update(deliveryDTO);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    deliveryDTO.getDeliveryId().toString()
                )
            )
            .body(deliveryDTO);
    }

    /**
     * {@code PATCH  /deliveries/:id} : Partial updates given fields of an existing delivery, field will ignore if it is null
     *
     * @param id the id of the deliveryDTO to save.
     * @param deliveryDTO the deliveryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deliveryDTO,
     * or with status {@code 400 (Bad Request)} if the deliveryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the deliveryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the deliveryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<DeliveryDTO> partialUpdateDelivery(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody DeliveryDTO deliveryDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to partial update Delivery partially : {}, {}",
            id,
            deliveryDTO
        );
        if (deliveryDTO.getDeliveryId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, deliveryDTO.getDeliveryId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!deliveryRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<DeliveryDTO> result = deliveryService.partialUpdate(
            deliveryDTO
        );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                deliveryDTO.getDeliveryId().toString()
            )
        );
    }

    /**
     * {@code GET  /deliveries} : get all the deliveries.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of deliveries in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DeliveryDTO>> getAllDeliveries(
        DeliveryCriteria criteria,
        @ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Deliveries by criteria: {}", criteria);

        Page<DeliveryDTO> page = deliveryQueryService.findByCriteria(
            criteria,
            pageable
        );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /deliveries/count} : count all the deliveries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDeliveries(DeliveryCriteria criteria) {
        log.debug("REST request to count Deliveries by criteria: {}", criteria);
        return ResponseEntity.ok().body(
            deliveryQueryService.countByCriteria(criteria)
        );
    }

    /**
     * {@code GET  /deliveries/:id} : get the "id" delivery.
     *
     * @param id the id of the deliveryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the deliveryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryDTO> getDelivery(
        @PathVariable("id") UUID id
    ) {
        log.debug("REST request to get Delivery : {}", id);
        Optional<DeliveryDTO> deliveryDTO = deliveryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(deliveryDTO);
    }

    /**
     * {@code DELETE  /deliveries/:id} : delete the "id" delivery.
     *
     * @param id the id of the deliveryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable("id") UUID id) {
        log.debug("REST request to delete Delivery : {}", id);
        deliveryService.delete(id);
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
