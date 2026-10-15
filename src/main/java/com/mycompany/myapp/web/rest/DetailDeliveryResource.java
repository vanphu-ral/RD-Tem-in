package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.DetailDeliveryRepository;
import com.mycompany.myapp.service.DetailDeliveryService;
import com.mycompany.myapp.service.dto.DetailDeliveryDTO;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.DetailDelivery}.
 */
@RestController
@RequestMapping("/api/detail-deliveries")
public class DetailDeliveryResource {

    private final Logger log = LoggerFactory.getLogger(
        DetailDeliveryResource.class
    );

    private static final String ENTITY_NAME = "detailDelivery";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DetailDeliveryService detailDeliveryService;

    private final DetailDeliveryRepository detailDeliveryRepository;

    public DetailDeliveryResource(
        DetailDeliveryService detailDeliveryService,
        DetailDeliveryRepository detailDeliveryRepository
    ) {
        this.detailDeliveryService = detailDeliveryService;
        this.detailDeliveryRepository = detailDeliveryRepository;
    }

    /**
     * {@code POST  /detail-deliveries} : Create a new detailDelivery.
     *
     * @param detailDeliveryDTO the detailDeliveryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new detailDeliveryDTO, or with status {@code 400 (Bad Request)} if the detailDelivery has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DetailDeliveryDTO> createDetailDelivery(
        @Valid @RequestBody DetailDeliveryDTO detailDeliveryDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to save DetailDelivery : {}",
            detailDeliveryDTO
        );
        if (detailDeliveryDTO.getLineId() != null) {
            throw new BadRequestAlertException(
                "A new detailDelivery cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        detailDeliveryDTO = detailDeliveryService.save(detailDeliveryDTO);
        return ResponseEntity.created(
            new URI("/api/detail-deliveries/" + detailDeliveryDTO.getLineId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    detailDeliveryDTO.getLineId().toString()
                )
            )
            .body(detailDeliveryDTO);
    }

    /**
     * {@code PUT  /detail-deliveries/:id} : Updates an existing detailDelivery.
     *
     * @param id the id of the detailDeliveryDTO to save.
     * @param detailDeliveryDTO the detailDeliveryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated detailDeliveryDTO,
     * or with status {@code 400 (Bad Request)} if the detailDeliveryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the detailDeliveryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DetailDeliveryDTO> updateDetailDelivery(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody DetailDeliveryDTO detailDeliveryDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to update DetailDelivery : {}, {}",
            id,
            detailDeliveryDTO
        );
        if (detailDeliveryDTO.getLineId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, detailDeliveryDTO.getLineId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!detailDeliveryRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        detailDeliveryDTO = detailDeliveryService.update(detailDeliveryDTO);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    detailDeliveryDTO.getLineId().toString()
                )
            )
            .body(detailDeliveryDTO);
    }

    /**
     * {@code PATCH  /detail-deliveries/:id} : Partial updates given fields of an existing detailDelivery, field will ignore if it is null
     *
     * @param id the id of the detailDeliveryDTO to save.
     * @param detailDeliveryDTO the detailDeliveryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated detailDeliveryDTO,
     * or with status {@code 400 (Bad Request)} if the detailDeliveryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the detailDeliveryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the detailDeliveryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<DetailDeliveryDTO> partialUpdateDetailDelivery(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody DetailDeliveryDTO detailDeliveryDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to partial update DetailDelivery partially : {}, {}",
            id,
            detailDeliveryDTO
        );
        if (detailDeliveryDTO.getLineId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, detailDeliveryDTO.getLineId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!detailDeliveryRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<DetailDeliveryDTO> result =
            detailDeliveryService.partialUpdate(detailDeliveryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                detailDeliveryDTO.getLineId().toString()
            )
        );
    }

    /**
     * {@code GET  /detail-deliveries} : get all the detailDeliveries.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of detailDeliveries in body.
     */
    @GetMapping("")
    public List<DetailDeliveryDTO> getAllDetailDeliveries(
        @RequestParam(
            name = "eagerload",
            required = false,
            defaultValue = "true"
        ) boolean eagerload
    ) {
        log.debug("REST request to get all DetailDeliveries");
        return detailDeliveryService.findAll();
    }

    /**
     * {@code GET  /detail-deliveries/:id} : get the "id" detailDelivery.
     *
     * @param id the id of the detailDeliveryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the detailDeliveryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DetailDeliveryDTO> getDetailDelivery(
        @PathVariable("id") UUID id
    ) {
        log.debug("REST request to get DetailDelivery : {}", id);
        Optional<DetailDeliveryDTO> detailDeliveryDTO =
            detailDeliveryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(detailDeliveryDTO);
    }

    /**
     * {@code DELETE  /detail-deliveries/:id} : delete the "id" detailDelivery.
     *
     * @param id the id of the detailDeliveryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetailDelivery(
        @PathVariable("id") UUID id
    ) {
        log.debug("REST request to delete DetailDelivery : {}", id);
        detailDeliveryService.delete(id);
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
