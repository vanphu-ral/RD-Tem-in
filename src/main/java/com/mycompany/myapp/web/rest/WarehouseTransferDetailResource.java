package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.WarehouseTransferDetailRepository;
import com.mycompany.myapp.service.WarehouseTransferDetailService;
import com.mycompany.myapp.service.dto.WarehouseTransferDetailDTO;
import com.mycompany.myapp.service.dto.WarehouseTransferSummaryDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.WarehouseTransferDetail}.
 */
@RestController
@RequestMapping("/api/warehouse-transfer-details")
public class WarehouseTransferDetailResource {

    private final Logger log = LoggerFactory.getLogger(
        WarehouseTransferDetailResource.class
    );

    private static final String ENTITY_NAME = "warehouseTransferDetail";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WarehouseTransferDetailService warehouseTransferDetailService;

    private final WarehouseTransferDetailRepository warehouseTransferDetailRepository;

    public WarehouseTransferDetailResource(
        WarehouseTransferDetailService warehouseTransferDetailService,
        WarehouseTransferDetailRepository warehouseTransferDetailRepository
    ) {
        this.warehouseTransferDetailService = warehouseTransferDetailService;
        this.warehouseTransferDetailRepository =
            warehouseTransferDetailRepository;
    }

    /**
     * {@code POST  /warehouse-transfer-details} : Create a new warehouseTransferDetail.
     *
     * @param warehouseTransferDetailDTO the warehouseTransferDetailDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new warehouseTransferDetailDTO, or with status {@code 400 (Bad Request)} if the warehouseTransferDetail has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<
        WarehouseTransferDetailDTO
    > createWarehouseTransferDetail(
        @Valid @RequestBody WarehouseTransferDetailDTO warehouseTransferDetailDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to save WarehouseTransferDetail : {}",
            warehouseTransferDetailDTO
        );
        if (warehouseTransferDetailDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new warehouseTransferDetail cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        warehouseTransferDetailDTO = warehouseTransferDetailService.save(
            warehouseTransferDetailDTO
        );
        return ResponseEntity.created(
            new URI(
                "/api/warehouse-transfer-details/" +
                warehouseTransferDetailDTO.getId()
            )
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    warehouseTransferDetailDTO.getId().toString()
                )
            )
            .body(warehouseTransferDetailDTO);
    }

    /**
     * {@code PUT  /warehouse-transfer-details/:id} : Updates an existing warehouseTransferDetail.
     *
     * @param id the id of the warehouseTransferDetailDTO to save.
     * @param warehouseTransferDetailDTO the warehouseTransferDetailDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated warehouseTransferDetailDTO,
     * or with status {@code 400 (Bad Request)} if the warehouseTransferDetailDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the warehouseTransferDetailDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<
        WarehouseTransferDetailDTO
    > updateWarehouseTransferDetail(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WarehouseTransferDetailDTO warehouseTransferDetailDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to update WarehouseTransferDetail : {}, {}",
            id,
            warehouseTransferDetailDTO
        );
        if (warehouseTransferDetailDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, warehouseTransferDetailDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!warehouseTransferDetailRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        warehouseTransferDetailDTO = warehouseTransferDetailService.update(
            warehouseTransferDetailDTO
        );
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    warehouseTransferDetailDTO.getId().toString()
                )
            )
            .body(warehouseTransferDetailDTO);
    }

    /**
     * {@code PATCH  /warehouse-transfer-details/:id} : Partial updates given fields of an existing warehouseTransferDetail, field will ignore if it is null
     *
     * @param id the id of the warehouseTransferDetailDTO to save.
     * @param warehouseTransferDetailDTO the warehouseTransferDetailDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated warehouseTransferDetailDTO,
     * or with status {@code 400 (Bad Request)} if the warehouseTransferDetailDTO is not valid,
     * or with status {@code 404 (Not Found)} if the warehouseTransferDetailDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the warehouseTransferDetailDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<
        WarehouseTransferDetailDTO
    > partialUpdateWarehouseTransferDetail(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WarehouseTransferDetailDTO warehouseTransferDetailDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to partial update WarehouseTransferDetail partially : {}, {}",
            id,
            warehouseTransferDetailDTO
        );
        if (warehouseTransferDetailDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, warehouseTransferDetailDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!warehouseTransferDetailRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<WarehouseTransferDetailDTO> result =
            warehouseTransferDetailService.partialUpdate(
                warehouseTransferDetailDTO
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                warehouseTransferDetailDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /warehouse-transfer-details} : get all the warehouseTransferDetails.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of warehouseTransferDetails in body.
     */
    @GetMapping("")
    public List<WarehouseTransferDetailDTO> getAllWarehouseTransferDetails() {
        log.debug("REST request to get all WarehouseTransferDetails");
        return warehouseTransferDetailService.findAll();
    }

    /**
     * {@code GET  /warehouse-transfer-details/:id} : get the "id" warehouseTransferDetail.
     *
     * @param id the id of the warehouseTransferDetailDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the warehouseTransferDetailDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<
        WarehouseTransferDetailDTO
    > getWarehouseTransferDetail(@PathVariable("id") Long id) {
        log.debug("REST request to get WarehouseTransferDetail : {}", id);
        Optional<WarehouseTransferDetailDTO> warehouseTransferDetailDTO =
            warehouseTransferDetailService.findOne(id);
        return ResponseUtil.wrapOrNotFound(warehouseTransferDetailDTO);
    }

    /**
     * {@code DELETE  /warehouse-transfer-details/:id} : delete the "id" warehouseTransferDetail.
     *
     * @param id the id of the warehouseTransferDetailDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouseTransferDetail(
        @PathVariable("id") Long id
    ) {
        log.debug("REST request to delete WarehouseTransferDetail : {}", id);
        warehouseTransferDetailService.delete(id);
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
     * {@code GET  /warehouse-transfer-details/summary/:owtqDocEntry} : Get summary for a specific owtqDocEntry.
     *
     * @param owtqDocEntry the owtqDocEntry to retrieve summary for.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the warehouseTransferSummaryDTO.
     */
    @GetMapping("/summary/{owtqDocEntry}")
    public ResponseEntity<
        WarehouseTransferSummaryDTO
    > getWarehouseTransferSummary(
        @PathVariable("owtqDocEntry") Long owtqDocEntry
    ) {
        log.debug(
            "REST request to get WarehouseTransfer summary : {}",
            owtqDocEntry
        );
        WarehouseTransferSummaryDTO result =
            warehouseTransferDetailService.getSummaryByOwtqDocEntry(
                owtqDocEntry
            );
        return ResponseEntity.ok().body(result);
    }
}
