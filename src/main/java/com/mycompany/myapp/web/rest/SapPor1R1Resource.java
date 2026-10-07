package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.SapPor1R1Repository;
import com.mycompany.myapp.service.SapPor1R1Service;
import com.mycompany.myapp.service.dto.SapPor1R1DTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.SapPor1R1}.
 */
@RestController
@RequestMapping("/api/sap-por-1-r-1-s")
public class SapPor1R1Resource {

    private static final Logger LOG = LoggerFactory.getLogger(
        SapPor1R1Resource.class
    );

    private static final String ENTITY_NAME = "sapPor1R1";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SapPor1R1Service sapPor1R1Service;

    private final SapPor1R1Repository sapPor1R1Repository;

    public SapPor1R1Resource(
        SapPor1R1Service sapPor1R1Service,
        SapPor1R1Repository sapPor1R1Repository
    ) {
        this.sapPor1R1Service = sapPor1R1Service;
        this.sapPor1R1Repository = sapPor1R1Repository;
    }

    /**
     * {@code POST  /sap-por-1-r-1-s} : Create a new sapPor1R1.
     *
     * @param sapPor1R1DTO the sapPor1R1DTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sapPor1R1DTO, or with status {@code 400 (Bad Request)} if the sapPor1R1 has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SapPor1R1DTO> createSapPor1R1(
        @Valid @RequestBody SapPor1R1DTO sapPor1R1DTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save SapPor1R1 : {}", sapPor1R1DTO);
        if (sapPor1R1DTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new sapPor1R1 cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        sapPor1R1DTO = sapPor1R1Service.save(sapPor1R1DTO);
        return ResponseEntity.created(
            new URI("/api/sap-por-1-r-1-s/" + sapPor1R1DTO.getId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    sapPor1R1DTO.getId().toString()
                )
            )
            .body(sapPor1R1DTO);
    }

    /**
     * {@code POST  /sap-por-1-r-1-s/batch} : Create multiple new sapPor1R1s.
     *
     * @param sapPor1R1DTOs the list of sapPor1R1DTO to create.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the created sapPor1R1DTOs.
     */
    @PostMapping("/batch")
    public ResponseEntity<List<SapPor1R1DTO>> createSapPor1R1sBatch(
        @Valid @RequestBody List<SapPor1R1DTO> sapPor1R1DTOs
    ) {
        LOG.debug(
            "REST request to save multiple SapPor1R1 : {}",
            sapPor1R1DTOs
        );

        for (SapPor1R1DTO sapPor1R1DTO : sapPor1R1DTOs) {
            if (sapPor1R1DTO.getId() != null) {
                throw new BadRequestAlertException(
                    "A new sapPor1R1 cannot already have an ID",
                    ENTITY_NAME,
                    "idexists"
                );
            }
        }

        List<SapPor1R1DTO> result = sapPor1R1Service.saveAll(sapPor1R1DTOs);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    "entities created"
                )
            )
            .body(result);
    }

    /**
     * {@code PUT  /sap-por-1-r-1-s/:id} : Updates an existing sapPor1R1.
     *
     * @param id the id of the sapPor1R1DTO to save.
     * @param sapPor1R1DTO the sapPor1R1DTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sapPor1R1DTO,
     * or with status {@code 400 (Bad Request)} if the sapPor1R1DTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sapPor1R1DTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SapPor1R1DTO> updateSapPor1R1(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SapPor1R1DTO sapPor1R1DTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to update SapPor1R1 : {}, {}",
            id,
            sapPor1R1DTO
        );
        if (sapPor1R1DTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, sapPor1R1DTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!sapPor1R1Repository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        sapPor1R1DTO = sapPor1R1Service.update(sapPor1R1DTO);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    sapPor1R1DTO.getId().toString()
                )
            )
            .body(sapPor1R1DTO);
    }

    /**
     * {@code PATCH  /sap-por-1-r-1-s/:id} : Partial updates given fields of an existing sapPor1R1, field will ignore if it is null
     *
     * @param id the id of the sapPor1R1DTO to save.
     * @param sapPor1R1DTO the sapPor1R1DTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sapPor1R1DTO,
     * or with status {@code 400 (Bad Request)} if the sapPor1R1DTO is not valid,
     * or with status {@code 404 (Not Found)} if the sapPor1R1DTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the sapPor1R1DTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<SapPor1R1DTO> partialUpdateSapPor1R1(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SapPor1R1DTO sapPor1R1DTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update SapPor1R1 partially : {}, {}",
            id,
            sapPor1R1DTO
        );
        if (sapPor1R1DTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, sapPor1R1DTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!sapPor1R1Repository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<SapPor1R1DTO> result = sapPor1R1Service.partialUpdate(
            sapPor1R1DTO
        );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                sapPor1R1DTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /sap-por-1-r-1-s} : get all the sapPor1R1s.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sapPor1R1s in body.
     */
    @GetMapping("")
    public List<SapPor1R1DTO> getAllSapPor1R1s() {
        LOG.debug("REST request to get all SapPor1R1s");
        return sapPor1R1Service.findAll();
    }

    /**
     * {@code GET  /sap-por-1-r-1-s/:id} : get the "id" sapPor1R1.
     *
     * @param id the id of the sapPor1R1DTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sapPor1R1DTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SapPor1R1DTO> getSapPor1R1(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to get SapPor1R1 : {}", id);
        Optional<SapPor1R1DTO> sapPor1R1DTO = sapPor1R1Service.findOne(id);
        return ResponseUtil.wrapOrNotFound(sapPor1R1DTO);
    }

    /**
     * {@code DELETE  /sap-por-1-r-1-s/:id} : delete the "id" sapPor1R1.
     *
     * @param id the id of the sapPor1R1DTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSapPor1R1(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SapPor1R1 : {}", id);
        sapPor1R1Service.delete(id);
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
