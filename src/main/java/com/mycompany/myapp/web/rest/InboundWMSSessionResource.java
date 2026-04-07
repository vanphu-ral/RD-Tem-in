package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.partner3.InboundWMSSessionRepository;
import com.mycompany.myapp.service.InboundWMSSessionService;
import com.mycompany.myapp.service.criteria.InboundWMSSessionCriteria;
import com.mycompany.myapp.service.dto.InboundWMSPalletDTO;
import com.mycompany.myapp.service.dto.InboundWMSSessionDTO;
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
 * REST controller for managing
 * {@link com.mycompany.myapp.domain.InboundWMSSession}.
 */
@RestController
@RequestMapping("/api/inbound-wms-sessions")
public class InboundWMSSessionResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        InboundWMSSessionResource.class
    );

    private static final String ENTITY_NAME = "inboundWMSSession";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InboundWMSSessionService inboundWMSSessionService;

    private final InboundWMSSessionRepository inboundWMSSessionRepository;

    public InboundWMSSessionResource(
        InboundWMSSessionService inboundWMSSessionService,
        InboundWMSSessionRepository inboundWMSSessionRepository
    ) {
        this.inboundWMSSessionService = inboundWMSSessionService;
        this.inboundWMSSessionRepository = inboundWMSSessionRepository;
    }

    /**
     * {@code POST  /inbound-wms-sessions} : Create a new inboundWMSSession.
     *
     * @param inboundWMSSessionDTO the inboundWMSSessionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new inboundWMSSessionDTO, or with status
     *         {@code 400 (Bad Request)} if the inboundWMSSession has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InboundWMSSessionDTO> createInboundWMSSession(
        @Valid @RequestBody InboundWMSSessionDTO inboundWMSSessionDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to save InboundWMSSession : {}",
            inboundWMSSessionDTO
        );
        if (inboundWMSSessionDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new inboundWMSSession cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        inboundWMSSessionDTO = inboundWMSSessionService.save(
            inboundWMSSessionDTO
        );
        return ResponseEntity.created(
            new URI("/api/inbound-wms-sessions/" + inboundWMSSessionDTO.getId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    inboundWMSSessionDTO.getId().toString()
                )
            )
            .body(inboundWMSSessionDTO);
    }

    /**
     * {@code PUT  /inbound-wms-sessions/:id} : Updates an existing
     * inboundWMSSession.
     *
     * @param id                   the id of the inboundWMSSessionDTO to save.
     * @param inboundWMSSessionDTO the inboundWMSSessionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated inboundWMSSessionDTO,
     *         or with status {@code 400 (Bad Request)} if the inboundWMSSessionDTO
     *         is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         inboundWMSSessionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InboundWMSSessionDTO> updateInboundWMSSession(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InboundWMSSessionDTO inboundWMSSessionDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to update InboundWMSSession : {}, {}",
            id,
            inboundWMSSessionDTO
        );
        if (inboundWMSSessionDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, inboundWMSSessionDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!inboundWMSSessionRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        inboundWMSSessionDTO = inboundWMSSessionService.update(
            inboundWMSSessionDTO
        );
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    inboundWMSSessionDTO.getId().toString()
                )
            )
            .body(inboundWMSSessionDTO);
    }

    /**
     * {@code PATCH  /inbound-wms-sessions/:id} : Partial updates given fields of an
     * existing inboundWMSSession, field will ignore if it is null
     *
     * @param id                   the id of the inboundWMSSessionDTO to save.
     * @param inboundWMSSessionDTO the inboundWMSSessionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated inboundWMSSessionDTO,
     *         or with status {@code 400 (Bad Request)} if the inboundWMSSessionDTO
     *         is not valid,
     *         or with status {@code 404 (Not Found)} if the inboundWMSSessionDTO is
     *         not found,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         inboundWMSSessionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<InboundWMSSessionDTO> partialUpdateInboundWMSSession(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InboundWMSSessionDTO inboundWMSSessionDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update InboundWMSSession partially : {}, {}",
            id,
            inboundWMSSessionDTO
        );
        if (inboundWMSSessionDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, inboundWMSSessionDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!inboundWMSSessionRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<InboundWMSSessionDTO> result =
            inboundWMSSessionService.partialUpdate(inboundWMSSessionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                inboundWMSSessionDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /inbound-wms-sessions} : get all the inboundWMSSessions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of inboundWMSSessions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<InboundWMSSessionDTO>> getAllInboundWMSSessions(
        InboundWMSSessionCriteria criteria,
        Pageable pageable
    ) {
        LOG.debug("REST request to get a page of InboundWMSSessions");
        Page<InboundWMSSessionDTO> page = inboundWMSSessionService.findAll(
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
     * {@code GET  /inbound-wms-sessions/:id} : get the "id" inboundWMSSession.
     *
     * @param id the id of the inboundWMSSessionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the inboundWMSSessionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InboundWMSSessionDTO> getInboundWMSSession(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to get InboundWMSSession : {}", id);
        Optional<InboundWMSSessionDTO> inboundWMSSessionDTO =
            inboundWMSSessionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(inboundWMSSessionDTO);
    }

    /**
     * {@code GET  /inbound-wms-sessions/detail/:sessionId} : get the
     * inboundWMSSession with all its inboundWMSPallets.
     *
     * @param sessionId the id of the inboundWMSSession.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the
     *         inboundWMSSession with pallets in body.
     */
    @GetMapping("/detail/{sessionId}")
    public ResponseEntity<InboundWMSSessionDTO> getInboundWMSSessionWithPallets(
        @PathVariable("sessionId") Long sessionId
    ) {
        LOG.debug(
            "REST request to get InboundWMSSession with pallets by SessionId : {}",
            sessionId
        );
        Optional<InboundWMSSessionDTO> session =
            inboundWMSSessionService.findOneWithPallets(sessionId);
        return ResponseUtil.wrapOrNotFound(session);
    }

    /**
     * {@code POST  /inbound-wms-sessions/:id/submit-warehouse-entry-approval} :
     * submit warehouse entry approval for the session.
     *
     * @param id the id of the inboundWMSSession.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     */
    @PostMapping("/{id}/submit-warehouse-entry-approval")
    public ResponseEntity<Void> submitWarehouseEntryApproval(
        @PathVariable("id") Long id
    ) {
        LOG.debug(
            "REST request to submit warehouse entry approval for session : {}",
            id
        );
        inboundWMSSessionService.submitWarehouseEntryApproval(id);
        return ResponseEntity.ok().build();
    }

    /**
     * {@code DELETE  /inbound-wms-sessions/:id} : delete the "id"
     * inboundWMSSession.
     *
     * @param id the id of the inboundWMSSessionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInboundWMSSession(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to delete InboundWMSSession : {}", id);
        inboundWMSSessionService.delete(id);
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
