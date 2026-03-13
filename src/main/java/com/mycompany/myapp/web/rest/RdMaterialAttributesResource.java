package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.partner5.RdMaterialAttributesRepository;
import com.mycompany.myapp.service.RdMaterialAttributesQueryService;
import com.mycompany.myapp.service.RdMaterialAttributesService;
import com.mycompany.myapp.service.criteria.RdMaterialAttributesCriteria;
import com.mycompany.myapp.service.dto.RdMaterialAttributesDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.RdMaterialAttributes}.
 */
@RestController
@RequestMapping("/api/rd-material-attributes")
public class RdMaterialAttributesResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        RdMaterialAttributesResource.class
    );

    private static final String ENTITY_NAME = "rdMaterialAttributes";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RdMaterialAttributesService rdMaterialAttributesService;

    private final RdMaterialAttributesRepository rdMaterialAttributesRepository;

    private final RdMaterialAttributesQueryService rdMaterialAttributesQueryService;

    public RdMaterialAttributesResource(
        RdMaterialAttributesService rdMaterialAttributesService,
        RdMaterialAttributesRepository rdMaterialAttributesRepository,
        RdMaterialAttributesQueryService rdMaterialAttributesQueryService
    ) {
        this.rdMaterialAttributesService = rdMaterialAttributesService;
        this.rdMaterialAttributesRepository = rdMaterialAttributesRepository;
        this.rdMaterialAttributesQueryService =
            rdMaterialAttributesQueryService;
    }

    /**
     * {@code POST  /rd-material-attributes} : Create a new rdMaterialAttributes.
     *
     * @param rdMaterialAttributesDTO the rdMaterialAttributesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rdMaterialAttributesDTO, or with status {@code 400 (Bad Request)} if the rdMaterialAttributes has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RdMaterialAttributesDTO> createRdMaterialAttributes(
        @Valid @RequestBody RdMaterialAttributesDTO rdMaterialAttributesDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to save RdMaterialAttributes : {}",
            rdMaterialAttributesDTO
        );
        if (rdMaterialAttributesDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new rdMaterialAttributes cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        rdMaterialAttributesDTO = rdMaterialAttributesService.save(
            rdMaterialAttributesDTO
        );
        return ResponseEntity.created(
            new URI(
                "/api/rd-material-attributes/" + rdMaterialAttributesDTO.getId()
            )
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    rdMaterialAttributesDTO.getId().toString()
                )
            )
            .body(rdMaterialAttributesDTO);
    }

    /**
     * {@code PUT  /rd-material-attributes/:id} : Updates an existing rdMaterialAttributes.
     *
     * @param id the id of the rdMaterialAttributesDTO to save.
     * @param rdMaterialAttributesDTO the rdMaterialAttributesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rdMaterialAttributesDTO,
     * or with status {@code 400 (Bad Request)} if the rdMaterialAttributesDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rdMaterialAttributesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RdMaterialAttributesDTO> updateRdMaterialAttributes(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RdMaterialAttributesDTO rdMaterialAttributesDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to update RdMaterialAttributes : {}, {}",
            id,
            rdMaterialAttributesDTO
        );
        if (rdMaterialAttributesDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, rdMaterialAttributesDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!rdMaterialAttributesRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        rdMaterialAttributesDTO = rdMaterialAttributesService.update(
            rdMaterialAttributesDTO
        );
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    rdMaterialAttributesDTO.getId().toString()
                )
            )
            .body(rdMaterialAttributesDTO);
    }

    /**
     * {@code PATCH  /rd-material-attributes/:id} : Partial updates given fields of an existing rdMaterialAttributes, field will ignore if it is null
     *
     * @param id the id of the rdMaterialAttributesDTO to save.
     * @param rdMaterialAttributesDTO the rdMaterialAttributesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rdMaterialAttributesDTO,
     * or with status {@code 400 (Bad Request)} if the rdMaterialAttributesDTO is not valid,
     * or with status {@code 404 (Not Found)} if the rdMaterialAttributesDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the rdMaterialAttributesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<
        RdMaterialAttributesDTO
    > partialUpdateRdMaterialAttributes(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RdMaterialAttributesDTO rdMaterialAttributesDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update RdMaterialAttributes partially : {}, {}",
            id,
            rdMaterialAttributesDTO
        );
        if (rdMaterialAttributesDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, rdMaterialAttributesDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!rdMaterialAttributesRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<RdMaterialAttributesDTO> result =
            rdMaterialAttributesService.partialUpdate(rdMaterialAttributesDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                rdMaterialAttributesDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /rd-material-attributes} : get all the rdMaterialAttributes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rdMaterialAttributes in body.
     */
    @GetMapping("")
    public ResponseEntity<
        List<RdMaterialAttributesDTO>
    > getAllRdMaterialAttributes(RdMaterialAttributesCriteria criteria) {
        LOG.debug(
            "REST request to get RdMaterialAttributes by criteria: {}",
            criteria
        );

        List<RdMaterialAttributesDTO> entityList =
            rdMaterialAttributesQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /rd-material-attributes/count} : count all the rdMaterialAttributes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countRdMaterialAttributes(
        RdMaterialAttributesCriteria criteria
    ) {
        LOG.debug(
            "REST request to count RdMaterialAttributes by criteria: {}",
            criteria
        );
        return ResponseEntity.ok().body(
            rdMaterialAttributesQueryService.countByCriteria(criteria)
        );
    }

    /**
     * {@code GET  /rd-material-attributes/:id} : get the "id" rdMaterialAttributes.
     *
     * @param id the id of the rdMaterialAttributesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rdMaterialAttributesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RdMaterialAttributesDTO> getRdMaterialAttributes(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to get RdMaterialAttributes : {}", id);
        Optional<RdMaterialAttributesDTO> rdMaterialAttributesDTO =
            rdMaterialAttributesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rdMaterialAttributesDTO);
    }

    /**
     * {@code DELETE  /rd-material-attributes/:id} : delete the "id" rdMaterialAttributes.
     *
     * @param id the id of the rdMaterialAttributesDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRdMaterialAttributes(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to delete RdMaterialAttributes : {}", id);
        rdMaterialAttributesService.delete(id);
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
