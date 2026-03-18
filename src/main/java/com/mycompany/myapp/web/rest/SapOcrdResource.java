package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.partner4.SapOcrdRepository;
import com.mycompany.myapp.service.SapOcrdService;
import com.mycompany.myapp.service.dto.SapOcrdDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.SapOcrd}.
 */
@RestController
@RequestMapping("/api/sap-ocrds")
public class SapOcrdResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        SapOcrdResource.class
    );

    private static final String ENTITY_NAME = "sapOcrd";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SapOcrdService sapOcrdService;

    private final SapOcrdRepository sapOcrdRepository;

    public SapOcrdResource(
        SapOcrdService sapOcrdService,
        SapOcrdRepository sapOcrdRepository
    ) {
        this.sapOcrdService = sapOcrdService;
        this.sapOcrdRepository = sapOcrdRepository;
    }

    /**
     * {@code POST  /sap-ocrds} : Create a new sapOcrd.
     *
     * @param sapOcrdDTO the sapOcrdDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sapOcrdDTO, or with status {@code 400 (Bad Request)} if the sapOcrd has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SapOcrdDTO> createSapOcrd(
        @Valid @RequestBody SapOcrdDTO sapOcrdDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save SapOcrd : {}", sapOcrdDTO);
        if (sapOcrdDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new sapOcrd cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        sapOcrdDTO = sapOcrdService.save(sapOcrdDTO);
        return ResponseEntity.created(
            new URI("/api/sap-ocrds/" + sapOcrdDTO.getId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    sapOcrdDTO.getId().toString()
                )
            )
            .body(sapOcrdDTO);
    }

    /**
     * {@code PUT  /sap-ocrds/:id} : Updates an existing sapOcrd.
     *
     * @param id the id of the sapOcrdDTO to save.
     * @param sapOcrdDTO the sapOcrdDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sapOcrdDTO,
     * or with status {@code 400 (Bad Request)} if the sapOcrdDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sapOcrdDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SapOcrdDTO> updateSapOcrd(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SapOcrdDTO sapOcrdDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SapOcrd : {}, {}", id, sapOcrdDTO);
        if (sapOcrdDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, sapOcrdDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!sapOcrdRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        sapOcrdDTO = sapOcrdService.update(sapOcrdDTO);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    sapOcrdDTO.getId().toString()
                )
            )
            .body(sapOcrdDTO);
    }

    /**
     * {@code PATCH  /sap-ocrds/:id} : Partial updates given fields of an existing sapOcrd, field will ignore if it is null
     *
     * @param id the id of the sapOcrdDTO to save.
     * @param sapOcrdDTO the sapOcrdDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sapOcrdDTO,
     * or with status {@code 400 (Bad Request)} if the sapOcrdDTO is not valid,
     * or with status {@code 404 (Not Found)} if the sapOcrdDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the sapOcrdDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<SapOcrdDTO> partialUpdateSapOcrd(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SapOcrdDTO sapOcrdDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update SapOcrd partially : {}, {}",
            id,
            sapOcrdDTO
        );
        if (sapOcrdDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, sapOcrdDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!sapOcrdRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<SapOcrdDTO> result = sapOcrdService.partialUpdate(sapOcrdDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                sapOcrdDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /sap-ocrds} : get all the sapOcrds.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sapOcrds in body.
     */
    @GetMapping("")
    public List<SapOcrdDTO> getAllSapOcrds() {
        LOG.debug("REST request to get all SapOcrds");
        return sapOcrdService.findAll();
    }

    /**
     * {@code GET  /sap-ocrds/:id} : get the "id" sapOcrd.
     *
     * @param id the id of the sapOcrdDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sapOcrdDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SapOcrdDTO> getSapOcrd(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SapOcrd : {}", id);
        Optional<SapOcrdDTO> sapOcrdDTO = sapOcrdService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sapOcrdDTO);
    }

    /**
     * {@code DELETE  /sap-ocrds/:id} : delete the "id" sapOcrd.
     *
     * @param id the id of the sapOcrdDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSapOcrd(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SapOcrd : {}", id);
        sapOcrdService.delete(id);
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
