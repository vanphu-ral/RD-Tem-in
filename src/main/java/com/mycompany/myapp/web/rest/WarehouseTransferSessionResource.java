package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.WarehouseTransferSessionRepository;
import com.mycompany.myapp.service.WarehouseTransferSessionQueryService;
import com.mycompany.myapp.service.WarehouseTransferSessionService;
import com.mycompany.myapp.service.criteria.WarehouseTransferSessionCriteria;
import com.mycompany.myapp.service.dto.WarehouseTransferSessionDTO;
import com.mycompany.myapp.service.dto.WarehouseTransferSessionDetailDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.WarehouseTransferSession}.
 */
@RestController
@RequestMapping("/api/warehouse-transfer-sessions")
public class WarehouseTransferSessionResource {

    private final Logger log = LoggerFactory.getLogger(
        WarehouseTransferSessionResource.class
    );

    private static final String ENTITY_NAME = "warehouseTransferSession";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WarehouseTransferSessionService warehouseTransferSessionService;

    private final WarehouseTransferSessionRepository warehouseTransferSessionRepository;

    private final WarehouseTransferSessionQueryService warehouseTransferSessionQueryService;

    public WarehouseTransferSessionResource(
        WarehouseTransferSessionService warehouseTransferSessionService,
        WarehouseTransferSessionRepository warehouseTransferSessionRepository,
        WarehouseTransferSessionQueryService warehouseTransferSessionQueryService
    ) {
        this.warehouseTransferSessionService = warehouseTransferSessionService;
        this.warehouseTransferSessionRepository =
            warehouseTransferSessionRepository;
        this.warehouseTransferSessionQueryService =
            warehouseTransferSessionQueryService;
    }

    /**
     * {@code POST  /warehouse-transfer-sessions} : Create a new warehouseTransferSession.
     *
     * @param warehouseTransferSessionDTO the warehouseTransferSessionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new warehouseTransferSessionDTO, or with status {@code 400 (Bad Request)} if the warehouseTransferSession has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<
        WarehouseTransferSessionDTO
    > createWarehouseTransferSession(
        @Valid @RequestBody WarehouseTransferSessionDTO warehouseTransferSessionDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to save WarehouseTransferSession : {}",
            warehouseTransferSessionDTO
        );
        if (warehouseTransferSessionDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new warehouseTransferSession cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        warehouseTransferSessionDTO = warehouseTransferSessionService.save(
            warehouseTransferSessionDTO
        );
        return ResponseEntity.created(
            new URI(
                "/api/warehouse-transfer-sessions/" +
                warehouseTransferSessionDTO.getId()
            )
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    warehouseTransferSessionDTO.getId().toString()
                )
            )
            .body(warehouseTransferSessionDTO);
    }

    /**
     * {@code PUT  /warehouse-transfer-sessions/:id} : Updates an existing warehouseTransferSession.
     *
     * @param id the id of the warehouseTransferSessionDTO to save.
     * @param warehouseTransferSessionDTO the warehouseTransferSessionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated warehouseTransferSessionDTO,
     * or with status {@code 400 (Bad Request)} if the warehouseTransferSessionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the warehouseTransferSessionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<
        WarehouseTransferSessionDTO
    > updateWarehouseTransferSession(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WarehouseTransferSessionDTO warehouseTransferSessionDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to update WarehouseTransferSession : {}, {}",
            id,
            warehouseTransferSessionDTO
        );
        if (warehouseTransferSessionDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, warehouseTransferSessionDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!warehouseTransferSessionRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        warehouseTransferSessionDTO = warehouseTransferSessionService.update(
            warehouseTransferSessionDTO
        );
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    warehouseTransferSessionDTO.getId().toString()
                )
            )
            .body(warehouseTransferSessionDTO);
    }

    /**
     * {@code PATCH  /warehouse-transfer-sessions/:id} : Partial updates given fields of an existing warehouseTransferSession, field will ignore if it is null
     *
     * @param id the id of the warehouseTransferSessionDTO to save.
     * @param warehouseTransferSessionDTO the warehouseTransferSessionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated warehouseTransferSessionDTO,
     * or with status {@code 400 (Bad Request)} if the warehouseTransferSessionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the warehouseTransferSessionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the warehouseTransferSessionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<
        WarehouseTransferSessionDTO
    > partialUpdateWarehouseTransferSession(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WarehouseTransferSessionDTO warehouseTransferSessionDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to partial update WarehouseTransferSession partially : {}, {}",
            id,
            warehouseTransferSessionDTO
        );
        if (warehouseTransferSessionDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, warehouseTransferSessionDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!warehouseTransferSessionRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<WarehouseTransferSessionDTO> result =
            warehouseTransferSessionService.partialUpdate(
                warehouseTransferSessionDTO
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                warehouseTransferSessionDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /warehouse-transfer-sessions} : get all the warehouseTransferSessions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of warehouseTransferSessions in body.
     */
    @GetMapping("")
    public ResponseEntity<
        List<WarehouseTransferSessionDTO>
    > getAllWarehouseTransferSessions(
        WarehouseTransferSessionCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug(
            "REST request to get WarehouseTransferSessions by criteria: {}",
            criteria
        );

        if (criteria.getDeletedAt() == null) {
            criteria.setDeletedAt(
                new tech.jhipster.service.filter.InstantFilter()
            );
            criteria.deletedAt().setSpecified(false);
        }

        Pageable sortedPageable = pageable;
        if (pageable.getSort().isUnsorted()) {
            sortedPageable = org.springframework.data.domain.PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                org.springframework.data.domain.Sort.by(
                    org.springframework.data.domain.Sort.Order.desc(
                        "createdAt"
                    ),
                    org.springframework.data.domain.Sort.Order.desc("id")
                )
            );
        }

        Page<WarehouseTransferSessionDTO> page =
            warehouseTransferSessionQueryService.findByCriteria(
                criteria,
                sortedPageable
            );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /warehouse-transfer-sessions/count} : count all the warehouseTransferSessions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countWarehouseTransferSessions(
        WarehouseTransferSessionCriteria criteria
    ) {
        log.debug(
            "REST request to count WarehouseTransferSessions by criteria: {}",
            criteria
        );
        return ResponseEntity.ok().body(
            warehouseTransferSessionQueryService.countByCriteria(criteria)
        );
    }

    /**
     * {@code GET  /warehouse-transfer-sessions/:id} : get the "id" warehouseTransferSession.
     *
     * @param id the id of the warehouseTransferSessionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the warehouseTransferSessionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<
        WarehouseTransferSessionDTO
    > getWarehouseTransferSession(@PathVariable("id") Long id) {
        log.debug("REST request to get WarehouseTransferSession : {}", id);
        Optional<WarehouseTransferSessionDTO> warehouseTransferSessionDTO =
            warehouseTransferSessionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(warehouseTransferSessionDTO);
    }

    /**
     * {@code GET  /warehouse-transfer-sessions/:id/detail} : get the "id" warehouseTransferSession detail.
     *
     * @param id the id of the warehouseTransferSessionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the warehouseTransferSessionDetailDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<
        WarehouseTransferSessionDetailDTO
    > getWarehouseTransferSessionDetail(@PathVariable("id") Long id) {
        log.debug(
            "REST request to get WarehouseTransferSession detail : {}",
            id
        );
        Optional<WarehouseTransferSessionDetailDTO> result =
            warehouseTransferSessionService.findDetailById(id);
        return ResponseUtil.wrapOrNotFound(result);
    }

    /**
     * {@code DELETE  /warehouse-transfer-sessions/:id} : delete the "id" warehouseTransferSession.
     *
     * @param id the id of the warehouseTransferSessionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouseTransferSession(
        @PathVariable("id") Long id
    ) {
        log.debug("REST request to delete WarehouseTransferSession : {}", id);
        warehouseTransferSessionService.delete(id);
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
