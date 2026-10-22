package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.OwtqWarehouseTransferSessionRepository;
import com.mycompany.myapp.service.OwtqWarehouseTransferSessionService;
import com.mycompany.myapp.service.dto.OwtqWarehouseTransferSessionDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.OwtqWarehouseTransferSession}.
 */
@RestController
@RequestMapping("/api/owtq-warehouse-transfer-sessions")
public class OwtqWarehouseTransferSessionResource {

    private final Logger log = LoggerFactory.getLogger(
        OwtqWarehouseTransferSessionResource.class
    );

    private static final String ENTITY_NAME = "owtqWarehouseTransferSession";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OwtqWarehouseTransferSessionService owtqWarehouseTransferSessionService;

    private final OwtqWarehouseTransferSessionRepository owtqWarehouseTransferSessionRepository;

    public OwtqWarehouseTransferSessionResource(
        OwtqWarehouseTransferSessionService owtqWarehouseTransferSessionService,
        OwtqWarehouseTransferSessionRepository owtqWarehouseTransferSessionRepository
    ) {
        this.owtqWarehouseTransferSessionService =
            owtqWarehouseTransferSessionService;
        this.owtqWarehouseTransferSessionRepository =
            owtqWarehouseTransferSessionRepository;
    }

    /**
     * {@code POST  /owtq-warehouse-transfer-sessions} : Create a new owtqWarehouseTransferSession.
     *
     * @param owtqWarehouseTransferSessionDTO the owtqWarehouseTransferSessionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new owtqWarehouseTransferSessionDTO, or with status {@code 400 (Bad Request)} if the owtqWarehouseTransferSession has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<
        OwtqWarehouseTransferSessionDTO
    > createOwtqWarehouseTransferSession(
        @RequestBody OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to save OwtqWarehouseTransferSession : {}",
            owtqWarehouseTransferSessionDTO
        );
        if (owtqWarehouseTransferSessionDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new owtqWarehouseTransferSession cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        owtqWarehouseTransferSessionDTO =
            owtqWarehouseTransferSessionService.save(
                owtqWarehouseTransferSessionDTO
            );
        return ResponseEntity.created(
            new URI(
                "/api/owtq-warehouse-transfer-sessions/" +
                owtqWarehouseTransferSessionDTO.getId()
            )
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    owtqWarehouseTransferSessionDTO.getId().toString()
                )
            )
            .body(owtqWarehouseTransferSessionDTO);
    }

    /**
     * {@code PUT  /owtq-warehouse-transfer-sessions/:id} : Updates an existing owtqWarehouseTransferSession.
     *
     * @param id the id of the owtqWarehouseTransferSessionDTO to save.
     * @param owtqWarehouseTransferSessionDTO the owtqWarehouseTransferSessionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated owtqWarehouseTransferSessionDTO,
     * or with status {@code 400 (Bad Request)} if the owtqWarehouseTransferSessionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the owtqWarehouseTransferSessionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<
        OwtqWarehouseTransferSessionDTO
    > updateOwtqWarehouseTransferSession(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to update OwtqWarehouseTransferSession : {}, {}",
            id,
            owtqWarehouseTransferSessionDTO
        );
        if (owtqWarehouseTransferSessionDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, owtqWarehouseTransferSessionDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!owtqWarehouseTransferSessionRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        owtqWarehouseTransferSessionDTO =
            owtqWarehouseTransferSessionService.update(
                owtqWarehouseTransferSessionDTO
            );
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    owtqWarehouseTransferSessionDTO.getId().toString()
                )
            )
            .body(owtqWarehouseTransferSessionDTO);
    }

    /**
     * {@code PATCH  /owtq-warehouse-transfer-sessions/:id} : Partial updates given fields of an existing owtqWarehouseTransferSession, field will ignore if it is null
     *
     * @param id the id of the owtqWarehouseTransferSessionDTO to save.
     * @param owtqWarehouseTransferSessionDTO the owtqWarehouseTransferSessionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated owtqWarehouseTransferSessionDTO,
     * or with status {@code 400 (Bad Request)} if the owtqWarehouseTransferSessionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the owtqWarehouseTransferSessionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the owtqWarehouseTransferSessionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<
        OwtqWarehouseTransferSessionDTO
    > partialUpdateOwtqWarehouseTransferSession(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to partial update OwtqWarehouseTransferSession partially : {}, {}",
            id,
            owtqWarehouseTransferSessionDTO
        );
        if (owtqWarehouseTransferSessionDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, owtqWarehouseTransferSessionDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!owtqWarehouseTransferSessionRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<OwtqWarehouseTransferSessionDTO> result =
            owtqWarehouseTransferSessionService.partialUpdate(
                owtqWarehouseTransferSessionDTO
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                owtqWarehouseTransferSessionDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /owtq-warehouse-transfer-sessions} : get all the owtqWarehouseTransferSessions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of owtqWarehouseTransferSessions in body.
     */
    @GetMapping("")
    public List<
        OwtqWarehouseTransferSessionDTO
    > getAllOwtqWarehouseTransferSessions() {
        log.debug("REST request to get all OwtqWarehouseTransferSessions");
        return owtqWarehouseTransferSessionService.findAll();
    }

    /**
     * {@code GET  /owtq-warehouse-transfer-sessions/:id} : get the "id" owtqWarehouseTransferSession.
     *
     * @param id the id of the owtqWarehouseTransferSessionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the owtqWarehouseTransferSessionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<
        OwtqWarehouseTransferSessionDTO
    > getOwtqWarehouseTransferSession(@PathVariable("id") Long id) {
        log.debug("REST request to get OwtqWarehouseTransferSession : {}", id);
        Optional<
            OwtqWarehouseTransferSessionDTO
        > owtqWarehouseTransferSessionDTO =
            owtqWarehouseTransferSessionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(owtqWarehouseTransferSessionDTO);
    }

    /**
     * {@code DELETE  /owtq-warehouse-transfer-sessions/:id} : delete the "id" owtqWarehouseTransferSession.
     *
     * @param id the id of the owtqWarehouseTransferSessionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwtqWarehouseTransferSession(
        @PathVariable("id") Long id
    ) {
        log.debug(
            "REST request to delete OwtqWarehouseTransferSession : {}",
            id
        );
        owtqWarehouseTransferSessionService.delete(id);
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
