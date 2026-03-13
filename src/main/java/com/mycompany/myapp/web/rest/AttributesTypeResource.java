package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.partner5.AttributesTypeRepository;
import com.mycompany.myapp.service.AttributesTypeQueryService;
import com.mycompany.myapp.service.AttributesTypeService;
import com.mycompany.myapp.service.criteria.AttributesTypeCriteria;
import com.mycompany.myapp.service.dto.AttributesTypeDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.AttributesType}.
 */
@RestController
@RequestMapping("/api/attributes-types")
public class AttributesTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        AttributesTypeResource.class
    );

    private static final String ENTITY_NAME = "attributesType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AttributesTypeService attributesTypeService;

    private final AttributesTypeRepository attributesTypeRepository;

    private final AttributesTypeQueryService attributesTypeQueryService;

    public AttributesTypeResource(
        AttributesTypeService attributesTypeService,
        AttributesTypeRepository attributesTypeRepository,
        AttributesTypeQueryService attributesTypeQueryService
    ) {
        this.attributesTypeService = attributesTypeService;
        this.attributesTypeRepository = attributesTypeRepository;
        this.attributesTypeQueryService = attributesTypeQueryService;
    }

    /**
     * {@code POST  /attributes-types} : Create a new attributesType.
     *
     * @param attributesTypeDTO the attributesTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new attributesTypeDTO, or with status {@code 400 (Bad Request)} if the attributesType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AttributesTypeDTO> createAttributesType(
        @RequestBody AttributesTypeDTO attributesTypeDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to save AttributesType : {}",
            attributesTypeDTO
        );
        if (attributesTypeDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new attributesType cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        attributesTypeDTO = attributesTypeService.save(attributesTypeDTO);
        return ResponseEntity.created(
            new URI("/api/attributes-types/" + attributesTypeDTO.getId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    attributesTypeDTO.getId().toString()
                )
            )
            .body(attributesTypeDTO);
    }

    /**
     * {@code PUT  /attributes-types/:id} : Updates an existing attributesType.
     *
     * @param id the id of the attributesTypeDTO to save.
     * @param attributesTypeDTO the attributesTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attributesTypeDTO,
     * or with status {@code 400 (Bad Request)} if the attributesTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the attributesTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AttributesTypeDTO> updateAttributesType(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AttributesTypeDTO attributesTypeDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to update AttributesType : {}, {}",
            id,
            attributesTypeDTO
        );
        if (attributesTypeDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, attributesTypeDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!attributesTypeRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        attributesTypeDTO = attributesTypeService.update(attributesTypeDTO);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    attributesTypeDTO.getId().toString()
                )
            )
            .body(attributesTypeDTO);
    }

    /**
     * {@code PATCH  /attributes-types/:id} : Partial updates given fields of an existing attributesType, field will ignore if it is null
     *
     * @param id the id of the attributesTypeDTO to save.
     * @param attributesTypeDTO the attributesTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attributesTypeDTO,
     * or with status {@code 400 (Bad Request)} if the attributesTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the attributesTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the attributesTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<AttributesTypeDTO> partialUpdateAttributesType(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AttributesTypeDTO attributesTypeDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update AttributesType partially : {}, {}",
            id,
            attributesTypeDTO
        );
        if (attributesTypeDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, attributesTypeDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!attributesTypeRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<AttributesTypeDTO> result =
            attributesTypeService.partialUpdate(attributesTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                attributesTypeDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /attributes-types} : get all the attributesTypes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of attributesTypes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AttributesTypeDTO>> getAllAttributesTypes(
        AttributesTypeCriteria criteria
    ) {
        LOG.debug(
            "REST request to get AttributesTypes by criteria: {}",
            criteria
        );

        List<AttributesTypeDTO> entityList =
            attributesTypeQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /attributes-types/count} : count all the attributesTypes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAttributesTypes(
        AttributesTypeCriteria criteria
    ) {
        LOG.debug(
            "REST request to count AttributesTypes by criteria: {}",
            criteria
        );
        return ResponseEntity.ok().body(
            attributesTypeQueryService.countByCriteria(criteria)
        );
    }

    /**
     * {@code GET  /attributes-types/:id} : get the "id" attributesType.
     *
     * @param id the id of the attributesTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the attributesTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AttributesTypeDTO> getAttributesType(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to get AttributesType : {}", id);
        Optional<AttributesTypeDTO> attributesTypeDTO =
            attributesTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(attributesTypeDTO);
    }

    /**
     * {@code DELETE  /attributes-types/:id} : delete the "id" attributesType.
     *
     * @param id the id of the attributesTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttributesType(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to delete AttributesType : {}", id);
        attributesTypeService.delete(id);
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
