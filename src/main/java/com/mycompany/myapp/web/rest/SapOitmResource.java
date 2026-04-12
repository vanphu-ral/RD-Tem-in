package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.partner4.SapOitm;
import com.mycompany.myapp.repository.partner4.SapOitmRepository;
import com.mycompany.myapp.service.SapOitmService;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.partner4.SapOitm}.
 */
@RestController
@RequestMapping("/api/sap-oitms")
public class SapOitmResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        SapOitmResource.class
    );

    private static final String ENTITY_NAME = "sapOitm";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SapOitmService sapOitmService;

    private final SapOitmRepository sapOitmRepository;

    public SapOitmResource(
        SapOitmService sapOitmService,
        SapOitmRepository sapOitmRepository
    ) {
        this.sapOitmService = sapOitmService;
        this.sapOitmRepository = sapOitmRepository;
    }

    /**
     * {@code POST  /sap-oitms} : Create a new sapOitm.
     *
     * @param sapOitm the sapOitm to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sapOitm, or with status {@code 400 (Bad Request)} if the sapOitm has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SapOitm> createSapOitm(
        @Valid @RequestBody SapOitm sapOitm
    ) throws URISyntaxException {
        LOG.debug("REST request to save SapOitm : {}", sapOitm);
        if (sapOitm.getId() != null) {
            throw new BadRequestAlertException(
                "A new sapOitm cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        sapOitm = sapOitmService.save(sapOitm);
        return ResponseEntity.created(
            new URI("/api/sap-oitms/" + sapOitm.getId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    sapOitm.getId().toString()
                )
            )
            .body(sapOitm);
    }

    /**
     * {@code PUT  /sap-oitms/:id} : Updates an existing sapOitm.
     *
     * @param id the id of the sapOitm to save.
     * @param sapOitm the sapOitm to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sapOitm,
     * or with status {@code 400 (Bad Request)} if the sapOitm is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sapOitm couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SapOitm> updateSapOitm(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SapOitm sapOitm
    ) throws URISyntaxException {
        LOG.debug("REST request to update SapOitm : {}, {}", id, sapOitm);
        if (sapOitm.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, sapOitm.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!sapOitmRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        sapOitm = sapOitmService.update(sapOitm);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    sapOitm.getId().toString()
                )
            )
            .body(sapOitm);
    }

    /**
     * {@code PATCH  /sap-oitms/:id} : Partial updates given fields of an existing sapOitm, field will ignore if it is null
     *
     * @param id the id of the sapOitm to save.
     * @param sapOitm the sapOitm to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sapOitm,
     * or with status {@code 400 (Bad Request)} if the sapOitm is not valid,
     * or with status {@code 404 (Not Found)} if the sapOitm is not found,
     * or with status {@code 500 (Internal Server Error)} if the sapOitm couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<SapOitm> partialUpdateSapOitm(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SapOitm sapOitm
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update SapOitm partially : {}, {}",
            id,
            sapOitm
        );
        if (sapOitm.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, sapOitm.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!sapOitmRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<SapOitm> result = sapOitmService.partialUpdate(sapOitm);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                sapOitm.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /sap-oitms} : get all the sapOitms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sapOitms in body.
     */
    @GetMapping("")
    public List<SapOitm> getAllSapOitms() {
        LOG.debug("REST request to get all SapOitms");
        return sapOitmService.findAll();
    }

    /**
     * {@code GET  /sap-oitms/:id} : get the "id" sapOitm.
     *
     * @param id the id of the sapOitm to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sapOitm, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SapOitm> getSapOitm(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SapOitm : {}", id);
        Optional<SapOitm> sapOitm = sapOitmService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sapOitm);
    }

    /**
     * {@code DELETE  /sap-oitms/:id} : delete the "id" sapOitm.
     *
     * @param id the id of the sapOitm to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSapOitm(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SapOitm : {}", id);
        sapOitmService.delete(id);
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
