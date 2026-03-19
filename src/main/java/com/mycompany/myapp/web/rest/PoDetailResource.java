package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.partner5.PoDetailRepository;
import com.mycompany.myapp.service.PoDetailQueryService;
import com.mycompany.myapp.service.PoDetailService;
import com.mycompany.myapp.service.criteria.PoDetailCriteria;
import com.mycompany.myapp.service.dto.PoDetailDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.PoDetail}.
 */
@RestController
@RequestMapping("/api/po-details")
public class PoDetailResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        PoDetailResource.class
    );

    private static final String ENTITY_NAME = "poDetail";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PoDetailService poDetailService;

    private final PoDetailRepository poDetailRepository;

    private final PoDetailQueryService poDetailQueryService;

    public PoDetailResource(
        PoDetailService poDetailService,
        PoDetailRepository poDetailRepository,
        PoDetailQueryService poDetailQueryService
    ) {
        this.poDetailService = poDetailService;
        this.poDetailRepository = poDetailRepository;
        this.poDetailQueryService = poDetailQueryService;
    }

    /**
     * {@code POST  /po-details} : Create a new poDetail.
     *
     * @param poDetailDTO the poDetailDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new poDetailDTO, or with status {@code 400 (Bad Request)} if the poDetail has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PoDetailDTO> createPoDetail(
        @Valid @RequestBody PoDetailDTO poDetailDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save PoDetail : {}", poDetailDTO);
        if (poDetailDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new poDetail cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        poDetailDTO = poDetailService.save(poDetailDTO);
        return ResponseEntity.created(
            new URI("/api/po-details/" + poDetailDTO.getId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    poDetailDTO.getId().toString()
                )
            )
            .body(poDetailDTO);
    }

    /**
     * {@code PUT  /po-details/:id} : Updates an existing poDetail.
     *
     * @param id the id of the poDetailDTO to save.
     * @param poDetailDTO the poDetailDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated poDetailDTO,
     * or with status {@code 400 (Bad Request)} if the poDetailDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the poDetailDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PoDetailDTO> updatePoDetail(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PoDetailDTO poDetailDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PoDetail : {}, {}", id, poDetailDTO);
        if (poDetailDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, poDetailDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!poDetailRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        poDetailDTO = poDetailService.update(poDetailDTO);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    poDetailDTO.getId().toString()
                )
            )
            .body(poDetailDTO);
    }

    /**
     * {@code PATCH  /po-details/:id} : Partial updates given fields of an existing poDetail, field will ignore if it is null
     *
     * @param id the id of the poDetailDTO to save.
     * @param poDetailDTO the poDetailDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated poDetailDTO,
     * or with status {@code 400 (Bad Request)} if the poDetailDTO is not valid,
     * or with status {@code 404 (Not Found)} if the poDetailDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the poDetailDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<PoDetailDTO> partialUpdatePoDetail(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PoDetailDTO poDetailDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update PoDetail partially : {}, {}",
            id,
            poDetailDTO
        );
        if (poDetailDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, poDetailDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!poDetailRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<PoDetailDTO> result = poDetailService.partialUpdate(
            poDetailDTO
        );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                poDetailDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /po-details} : get all the poDetails.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of poDetails in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PoDetailDTO>> getAllPoDetails(
        PoDetailCriteria criteria
    ) {
        LOG.debug("REST request to get PoDetails by criteria: {}", criteria);

        List<PoDetailDTO> entityList = poDetailQueryService.findByCriteria(
            criteria
        );
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /po-details/count} : count all the poDetails.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPoDetails(PoDetailCriteria criteria) {
        LOG.debug("REST request to count PoDetails by criteria: {}", criteria);
        return ResponseEntity.ok().body(
            poDetailQueryService.countByCriteria(criteria)
        );
    }

    /**
     * {@code GET  /po-details/:id} : get the "id" poDetail.
     *
     * @param id the id of the poDetailDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the poDetailDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PoDetailDTO> getPoDetail(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to get PoDetail : {}", id);
        Optional<PoDetailDTO> poDetailDTO = poDetailService.findOne(id);
        return ResponseUtil.wrapOrNotFound(poDetailDTO);
    }

    /**
     * {@code DELETE  /po-details/:id} : delete the "id" poDetail.
     *
     * @param id the id of the poDetailDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoDetail(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PoDetail : {}", id);
        poDetailService.delete(id);
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

    /**
     * {@code POST  /po-details/batch} : Create multiple new poDetails.
     *
     * @param poDetailDTOs the list of poDetailDTOs to create.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the created poDetailDTOs.
     */
    @PostMapping("/batch")
    public ResponseEntity<List<PoDetailDTO>> createPoDetailsBatch(
        @Valid @RequestBody List<PoDetailDTO> poDetailDTOs
    ) {
        LOG.debug("REST request to save multiple PoDetails : {}", poDetailDTOs);

        // Validate that none of the items have an ID
        for (PoDetailDTO poDetailDTO : poDetailDTOs) {
            if (poDetailDTO.getId() != null) {
                throw new BadRequestAlertException(
                    "A new poDetail cannot already have an ID",
                    ENTITY_NAME,
                    "idexists"
                );
            }
        }

        List<PoDetailDTO> result = poDetailService.saveAll(poDetailDTOs);
        return ResponseEntity.ok().body(result);
    }
}
