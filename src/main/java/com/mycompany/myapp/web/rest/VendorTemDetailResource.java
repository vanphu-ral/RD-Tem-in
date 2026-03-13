package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.partner5.VendorTemDetailRepository;
import com.mycompany.myapp.service.VendorTemDetailQueryService;
import com.mycompany.myapp.service.VendorTemDetailService;
import com.mycompany.myapp.service.criteria.VendorTemDetailCriteria;
import com.mycompany.myapp.service.dto.VendorTemDetailDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.VendorTemDetail}.
 */
@RestController
@RequestMapping("/api/vendor-tem-details")
public class VendorTemDetailResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        VendorTemDetailResource.class
    );

    private static final String ENTITY_NAME = "vendorTemDetail";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VendorTemDetailService vendorTemDetailService;

    private final VendorTemDetailRepository vendorTemDetailRepository;

    private final VendorTemDetailQueryService vendorTemDetailQueryService;

    public VendorTemDetailResource(
        VendorTemDetailService vendorTemDetailService,
        VendorTemDetailRepository vendorTemDetailRepository,
        VendorTemDetailQueryService vendorTemDetailQueryService
    ) {
        this.vendorTemDetailService = vendorTemDetailService;
        this.vendorTemDetailRepository = vendorTemDetailRepository;
        this.vendorTemDetailQueryService = vendorTemDetailQueryService;
    }

    /**
     * {@code POST  /vendor-tem-details} : Create a new vendorTemDetail.
     *
     * @param vendorTemDetailDTO the vendorTemDetailDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vendorTemDetailDTO, or with status {@code 400 (Bad Request)} if the vendorTemDetail has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<VendorTemDetailDTO> createVendorTemDetail(
        @Valid @RequestBody VendorTemDetailDTO vendorTemDetailDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to save VendorTemDetail : {}",
            vendorTemDetailDTO
        );
        if (vendorTemDetailDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new vendorTemDetail cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        vendorTemDetailDTO = vendorTemDetailService.save(vendorTemDetailDTO);
        return ResponseEntity.created(
            new URI("/api/vendor-tem-details/" + vendorTemDetailDTO.getId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    vendorTemDetailDTO.getId().toString()
                )
            )
            .body(vendorTemDetailDTO);
    }

    /**
     * {@code PUT  /vendor-tem-details/:id} : Updates an existing vendorTemDetail.
     *
     * @param id the id of the vendorTemDetailDTO to save.
     * @param vendorTemDetailDTO the vendorTemDetailDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vendorTemDetailDTO,
     * or with status {@code 400 (Bad Request)} if the vendorTemDetailDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vendorTemDetailDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VendorTemDetailDTO> updateVendorTemDetail(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VendorTemDetailDTO vendorTemDetailDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to update VendorTemDetail : {}, {}",
            id,
            vendorTemDetailDTO
        );
        if (vendorTemDetailDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, vendorTemDetailDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!vendorTemDetailRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        vendorTemDetailDTO = vendorTemDetailService.update(vendorTemDetailDTO);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    vendorTemDetailDTO.getId().toString()
                )
            )
            .body(vendorTemDetailDTO);
    }

    /**
     * {@code PATCH  /vendor-tem-details/:id} : Partial updates given fields of an existing vendorTemDetail, field will ignore if it is null
     *
     * @param id the id of the vendorTemDetailDTO to save.
     * @param vendorTemDetailDTO the vendorTemDetailDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vendorTemDetailDTO,
     * or with status {@code 400 (Bad Request)} if the vendorTemDetailDTO is not valid,
     * or with status {@code 404 (Not Found)} if the vendorTemDetailDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the vendorTemDetailDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<VendorTemDetailDTO> partialUpdateVendorTemDetail(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VendorTemDetailDTO vendorTemDetailDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update VendorTemDetail partially : {}, {}",
            id,
            vendorTemDetailDTO
        );
        if (vendorTemDetailDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, vendorTemDetailDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!vendorTemDetailRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<VendorTemDetailDTO> result =
            vendorTemDetailService.partialUpdate(vendorTemDetailDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                vendorTemDetailDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /vendor-tem-details} : get all the vendorTemDetails.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vendorTemDetails in body.
     */
    @GetMapping("")
    public ResponseEntity<List<VendorTemDetailDTO>> getAllVendorTemDetails(
        VendorTemDetailCriteria criteria
    ) {
        LOG.debug(
            "REST request to get VendorTemDetails by criteria: {}",
            criteria
        );

        List<VendorTemDetailDTO> entityList =
            vendorTemDetailQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /vendor-tem-details/count} : count all the vendorTemDetails.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countVendorTemDetails(
        VendorTemDetailCriteria criteria
    ) {
        LOG.debug(
            "REST request to count VendorTemDetails by criteria: {}",
            criteria
        );
        return ResponseEntity.ok().body(
            vendorTemDetailQueryService.countByCriteria(criteria)
        );
    }

    /**
     * {@code GET  /vendor-tem-details/:id} : get the "id" vendorTemDetail.
     *
     * @param id the id of the vendorTemDetailDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vendorTemDetailDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VendorTemDetailDTO> getVendorTemDetail(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to get VendorTemDetail : {}", id);
        Optional<VendorTemDetailDTO> vendorTemDetailDTO =
            vendorTemDetailService.findOne(id);
        return ResponseUtil.wrapOrNotFound(vendorTemDetailDTO);
    }

    /**
     * {@code DELETE  /vendor-tem-details/:id} : delete the "id" vendorTemDetail.
     *
     * @param id the id of the vendorTemDetailDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendorTemDetail(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to delete VendorTemDetail : {}", id);
        vendorTemDetailService.delete(id);
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
