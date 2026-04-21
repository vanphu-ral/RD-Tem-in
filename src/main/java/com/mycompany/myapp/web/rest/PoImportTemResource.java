package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.partner5.PoImportTemRepository;
import com.mycompany.myapp.service.PoImportTemQueryService;
import com.mycompany.myapp.service.PoImportTemService;
import com.mycompany.myapp.service.criteria.PoImportTemCriteria;
import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDTO;
import com.mycompany.myapp.service.dto.PaginationResponse;
import com.mycompany.myapp.service.dto.PaginationResponse;
import com.mycompany.myapp.service.dto.PoImportRequestDTO;
import com.mycompany.myapp.service.dto.PoImportResponseDTO;
import com.mycompany.myapp.service.dto.PoImportTemDTO;
import com.mycompany.myapp.service.dto.PoImportTemDetailDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.checkerframework.checker.units.qual.m;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.PoImportTem}.
 */
@RestController
@RequestMapping("/api/po-import-tems")
public class PoImportTemResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        PoImportTemResource.class
    );

    private static final String ENTITY_NAME = "poImportTem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PoImportTemService poImportTemService;

    private final PoImportTemRepository poImportTemRepository;

    private final PoImportTemQueryService poImportTemQueryService;

    public PoImportTemResource(
        PoImportTemService poImportTemService,
        PoImportTemRepository poImportTemRepository,
        PoImportTemQueryService poImportTemQueryService
    ) {
        this.poImportTemService = poImportTemService;
        this.poImportTemRepository = poImportTemRepository;
        this.poImportTemQueryService = poImportTemQueryService;
    }

    /**
     * {@code POST  /po-import-tems} : Create a new poImportTem.
     *
     * @param poImportTemDTO the poImportTemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new poImportTemDTO, or with status {@code 400 (Bad Request)}
     *         if the poImportTem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PoImportTemDTO> createPoImportTem(
        @Valid @RequestBody PoImportTemDTO poImportTemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save PoImportTem : {}", poImportTemDTO);
        if (poImportTemDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new poImportTem cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        poImportTemDTO = poImportTemService.save(poImportTemDTO);
        return ResponseEntity.created(
            new URI("/api/po-import-tems/" + poImportTemDTO.getId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    poImportTemDTO.getId().toString()
                )
            )
            .body(poImportTemDTO);
    }

    /**
     * {@code PUT  /po-import-tems/:id} : Updates an existing poImportTem.
     *
     * @param id             the id of the poImportTemDTO to save.
     * @param poImportTemDTO the poImportTemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated poImportTemDTO,
     *         or with status {@code 400 (Bad Request)} if the poImportTemDTO is not
     *         valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         poImportTemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PoImportTemDTO> updatePoImportTem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PoImportTemDTO poImportTemDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to update PoImportTem : {}, {}",
            id,
            poImportTemDTO
        );
        if (poImportTemDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, poImportTemDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!poImportTemRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        poImportTemDTO = poImportTemService.update(poImportTemDTO);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    poImportTemDTO.getId().toString()
                )
            )
            .body(poImportTemDTO);
    }

    /**
     * {@code PATCH  /po-import-tems/:id} : Partial updates given fields of an
     * existing poImportTem, field will ignore if it is null
     *
     * @param id             the id of the poImportTemDTO to save.
     * @param poImportTemDTO the poImportTemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated poImportTemDTO,
     *         or with status {@code 400 (Bad Request)} if the poImportTemDTO is not
     *         valid,
     *         or with status {@code 404 (Not Found)} if the poImportTemDTO is not
     *         found,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         poImportTemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<PoImportTemDTO> partialUpdatePoImportTem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PoImportTemDTO poImportTemDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update PoImportTem partially : {}, {}",
            id,
            poImportTemDTO
        );
        if (poImportTemDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, poImportTemDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!poImportTemRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<PoImportTemDTO> result = poImportTemService.partialUpdate(
            poImportTemDTO
        );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                poImportTemDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /po-import-tems} : get all the poImportTems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of poImportTems in body.
     */
    @GetMapping("")
    public ResponseEntity<
        PaginationResponse<PoImportTemDTO>
    > getAllPoImportTems(
        PoImportTemCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get PoImportTems by criteria: {}", criteria);

        Page<PoImportTemDTO> page = poImportTemQueryService.findByCriteria(
            criteria,
            pageable
        );
        // HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
        //                 ServletUriComponentsBuilder.fromCurrentRequest(),
        //                 page);
        PaginationResponse.Pagination pagination =
            new PaginationResponse.Pagination(
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                page.hasNext(),
                page.hasPrevious()
            );
        PaginationResponse<PoImportTemDTO> response = new PaginationResponse<>(
            page.getContent(),
            pagination
        );
        return ResponseEntity.ok(response);
    }

    /**
     * {@code GET  /po-import-tems/count} : count all the poImportTems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
     *         in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPoImportTems(
        PoImportTemCriteria criteria
    ) {
        LOG.debug(
            "REST request to count PoImportTems by criteria: {}",
            criteria
        );
        return ResponseEntity.ok().body(
            poImportTemQueryService.countByCriteria(criteria)
        );
    }

    /**
     * {@code GET  /po-import-tems/:id} : get the "id" poImportTem.
     *
     * @param id the id of the poImportTemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the poImportTemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PoImportTemDTO> getPoImportTem(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to get PoImportTem : {}", id);
        Optional<PoImportTemDTO> poImportTemDTO = poImportTemService.findOne(
            id
        );
        return ResponseUtil.wrapOrNotFound(poImportTemDTO);
    }

    /**
     * {@code GET  /po-import-tems/:id}/detail : get the full detail of poImportTem
     * with all nested relationships.
     *
     * @param id the id of the poImportTem to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the poImportTemDetailDTO including importVendorTemTransactions ->
     *         poDetails -> vendorTemDetails, or with status
     *         {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<PoImportTemDetailDTO> getPoImportTemDetail(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to get PoImportTem detail : {}", id);
        Optional<PoImportTemDetailDTO> poImportTemDetailDTO =
            poImportTemService.findDetailById(id);
        return ResponseUtil.wrapOrNotFound(poImportTemDetailDTO);
    }

    /**
     * {@code DELETE  /po-import-tems/:id} : delete the "id" poImportTem.
     *
     * @param id the id of the poImportTemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoImportTem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PoImportTem : {}", id);
        poImportTemService.delete(id);
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
     * {@code POST  /po-import-tems/process} : Process PO Import request.
     *
     * Case 1: If poNumber is null, creates a parent record in po_import_tem table
     * and a child record in import_vendor_tem_transactions table.
     *
     * Case 2: If poNumber exists, searches import_vendor_tem_transactions for that
     * poNumber. If records exist and were created today and storage unit same, returns all information
     * including subclasses (poDetails).
     *
     * @param request the PO import request payload.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the PoImportResponseDTO containing either newly created records (Case
     *         1)
     *         or retrieved data (Case 2).
     */
    @PostMapping("/process")
    public ResponseEntity<PoImportResponseDTO> processPoImport(
        @Valid @RequestBody PoImportRequestDTO request
    ) {
        LOG.debug("REST request to process PO Import : {}", request);
        PoImportResponseDTO response = poImportTemService.processPoImport(
            request
        );
        return ResponseEntity.ok(response);
    }

    /**
     * {@code POST  /po-import-tems/update-transaction} : Update PO information and supplement po_detail
     * for an existing ImportVendorTemTransactions by fetching from partner6 WorkZone (SAP PO Info).
     *
     * @param transactionDTO the ImportVendorTemTransactionsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated transaction with poDetails.
     */
    @PostMapping("/update-transaction")
    public ResponseEntity<PoImportResponseDTO> updateImportVendorTemTransaction(
        @Valid @RequestBody ImportVendorTemTransactionsDTO transactionDTO
    ) {
        LOG.debug(
            "REST request to update ImportVendorTemTransaction : {}",
            transactionDTO
        );
        PoImportResponseDTO response =
            poImportTemService.processImportVendorTemTransactionUpdate(
                transactionDTO
            );
        return ResponseEntity.ok(response);
    }
}
