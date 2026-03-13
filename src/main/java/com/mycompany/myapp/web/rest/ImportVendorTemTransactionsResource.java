package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.partner5.ImportVendorTemTransactionsRepository;
import com.mycompany.myapp.service.ImportVendorTemTransactionsQueryService;
import com.mycompany.myapp.service.ImportVendorTemTransactionsService;
import com.mycompany.myapp.service.criteria.ImportVendorTemTransactionsCriteria;
import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.ImportVendorTemTransactions}.
 */
@RestController
@RequestMapping("/api/import-vendor-tem-transactions")
public class ImportVendorTemTransactionsResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        ImportVendorTemTransactionsResource.class
    );

    private static final String ENTITY_NAME = "importVendorTemTransactions";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ImportVendorTemTransactionsService importVendorTemTransactionsService;

    private final ImportVendorTemTransactionsRepository importVendorTemTransactionsRepository;

    private final ImportVendorTemTransactionsQueryService importVendorTemTransactionsQueryService;

    public ImportVendorTemTransactionsResource(
        ImportVendorTemTransactionsService importVendorTemTransactionsService,
        ImportVendorTemTransactionsRepository importVendorTemTransactionsRepository,
        ImportVendorTemTransactionsQueryService importVendorTemTransactionsQueryService
    ) {
        this.importVendorTemTransactionsService =
            importVendorTemTransactionsService;
        this.importVendorTemTransactionsRepository =
            importVendorTemTransactionsRepository;
        this.importVendorTemTransactionsQueryService =
            importVendorTemTransactionsQueryService;
    }

    /**
     * {@code POST  /import-vendor-tem-transactions} : Create a new importVendorTemTransactions.
     *
     * @param importVendorTemTransactionsDTO the importVendorTemTransactionsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new importVendorTemTransactionsDTO, or with status {@code 400 (Bad Request)} if the importVendorTemTransactions has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<
        ImportVendorTemTransactionsDTO
    > createImportVendorTemTransactions(
        @Valid @RequestBody ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to save ImportVendorTemTransactions : {}",
            importVendorTemTransactionsDTO
        );
        if (importVendorTemTransactionsDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new importVendorTemTransactions cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        importVendorTemTransactionsDTO =
            importVendorTemTransactionsService.save(
                importVendorTemTransactionsDTO
            );
        return ResponseEntity.created(
            new URI(
                "/api/import-vendor-tem-transactions/" +
                importVendorTemTransactionsDTO.getId()
            )
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    importVendorTemTransactionsDTO.getId().toString()
                )
            )
            .body(importVendorTemTransactionsDTO);
    }

    /**
     * {@code PUT  /import-vendor-tem-transactions/:id} : Updates an existing importVendorTemTransactions.
     *
     * @param id the id of the importVendorTemTransactionsDTO to save.
     * @param importVendorTemTransactionsDTO the importVendorTemTransactionsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated importVendorTemTransactionsDTO,
     * or with status {@code 400 (Bad Request)} if the importVendorTemTransactionsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the importVendorTemTransactionsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<
        ImportVendorTemTransactionsDTO
    > updateImportVendorTemTransactions(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to update ImportVendorTemTransactions : {}, {}",
            id,
            importVendorTemTransactionsDTO
        );
        if (importVendorTemTransactionsDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, importVendorTemTransactionsDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!importVendorTemTransactionsRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        importVendorTemTransactionsDTO =
            importVendorTemTransactionsService.update(
                importVendorTemTransactionsDTO
            );
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    importVendorTemTransactionsDTO.getId().toString()
                )
            )
            .body(importVendorTemTransactionsDTO);
    }

    /**
     * {@code PATCH  /import-vendor-tem-transactions/:id} : Partial updates given fields of an existing importVendorTemTransactions, field will ignore if it is null
     *
     * @param id the id of the importVendorTemTransactionsDTO to save.
     * @param importVendorTemTransactionsDTO the importVendorTemTransactionsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated importVendorTemTransactionsDTO,
     * or with status {@code 400 (Bad Request)} if the importVendorTemTransactionsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the importVendorTemTransactionsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the importVendorTemTransactionsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<
        ImportVendorTemTransactionsDTO
    > partialUpdateImportVendorTemTransactions(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update ImportVendorTemTransactions partially : {}, {}",
            id,
            importVendorTemTransactionsDTO
        );
        if (importVendorTemTransactionsDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, importVendorTemTransactionsDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!importVendorTemTransactionsRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<ImportVendorTemTransactionsDTO> result =
            importVendorTemTransactionsService.partialUpdate(
                importVendorTemTransactionsDTO
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                importVendorTemTransactionsDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /import-vendor-tem-transactions} : get all the importVendorTemTransactions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of importVendorTemTransactions in body.
     */
    @GetMapping("")
    public ResponseEntity<
        Page<ImportVendorTemTransactionsDTO>
    > getAllImportVendorTemTransactions(
        ImportVendorTemTransactionsCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug(
            "REST request to get ImportVendorTemTransactions by criteria: {}",
            criteria
        );

        Page<ImportVendorTemTransactionsDTO> page =
            importVendorTemTransactionsQueryService.findByCriteria(
                criteria,
                pageable
            );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page);
    }

    /**
     * {@code GET  /import-vendor-tem-transactions/count} : count all the importVendorTemTransactions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countImportVendorTemTransactions(
        ImportVendorTemTransactionsCriteria criteria
    ) {
        LOG.debug(
            "REST request to count ImportVendorTemTransactions by criteria: {}",
            criteria
        );
        return ResponseEntity.ok().body(
            importVendorTemTransactionsQueryService.countByCriteria(criteria)
        );
    }

    /**
     * {@code GET  /import-vendor-tem-transactions/:id} : get the "id" importVendorTemTransactions.
     *
     * @param id the id of the importVendorTemTransactionsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the importVendorTemTransactionsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<
        ImportVendorTemTransactionsDTO
    > getImportVendorTemTransactions(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ImportVendorTemTransactions : {}", id);
        Optional<
            ImportVendorTemTransactionsDTO
        > importVendorTemTransactionsDTO =
            importVendorTemTransactionsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(importVendorTemTransactionsDTO);
    }

    /**
     * {@code DELETE  /import-vendor-tem-transactions/:id} : delete the "id" importVendorTemTransactions.
     *
     * @param id the id of the importVendorTemTransactionsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImportVendorTemTransactions(
        @PathVariable("id") Long id
    ) {
        LOG.debug(
            "REST request to delete ImportVendorTemTransactions : {}",
            id
        );
        importVendorTemTransactionsService.delete(id);
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
