package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ScanPalletRepository;
import com.mycompany.myapp.service.ScanPalletService;
import com.mycompany.myapp.service.dto.ScanPalletDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.ScanPallet}.
 */
@RestController
@RequestMapping("/api/scan-pallets")
public class ScanPalletResource {

    private final Logger log = LoggerFactory.getLogger(
        ScanPalletResource.class
    );

    private static final String ENTITY_NAME = "scanPallet";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ScanPalletService scanPalletService;

    private final ScanPalletRepository scanPalletRepository;

    public ScanPalletResource(
        ScanPalletService scanPalletService,
        ScanPalletRepository scanPalletRepository
    ) {
        this.scanPalletService = scanPalletService;
        this.scanPalletRepository = scanPalletRepository;
    }

    /**
     * {@code POST  /scan-pallets} : Create a new scanPallet.
     *
     * @param scanPalletDTO the scanPalletDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new scanPalletDTO, or with status {@code 400 (Bad Request)} if the scanPallet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ScanPalletDTO> createScanPallet(
        @Valid @RequestBody ScanPalletDTO scanPalletDTO
    ) throws URISyntaxException {
        log.debug("REST request to save ScanPallet : {}", scanPalletDTO);
        if (scanPalletDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new scanPallet cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        scanPalletDTO = scanPalletService.save(scanPalletDTO);
        return ResponseEntity.created(
            new URI("/api/scan-pallets/" + scanPalletDTO.getId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    scanPalletDTO.getId().toString()
                )
            )
            .body(scanPalletDTO);
    }

    /**
     * {@code PUT  /scan-pallets/:id} : Updates an existing scanPallet.
     *
     * @param id the id of the scanPalletDTO to save.
     * @param scanPalletDTO the scanPalletDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scanPalletDTO,
     * or with status {@code 400 (Bad Request)} if the scanPalletDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the scanPalletDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ScanPalletDTO> updateScanPallet(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ScanPalletDTO scanPalletDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to update ScanPallet : {}, {}",
            id,
            scanPalletDTO
        );
        if (scanPalletDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, scanPalletDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!scanPalletRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        scanPalletDTO = scanPalletService.update(scanPalletDTO);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    scanPalletDTO.getId().toString()
                )
            )
            .body(scanPalletDTO);
    }

    /**
     * {@code PATCH  /scan-pallets/:id} : Partial updates given fields of an existing scanPallet, field will ignore if it is null
     *
     * @param id the id of the scanPalletDTO to save.
     * @param scanPalletDTO the scanPalletDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scanPalletDTO,
     * or with status {@code 400 (Bad Request)} if the scanPalletDTO is not valid,
     * or with status {@code 404 (Not Found)} if the scanPalletDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the scanPalletDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<ScanPalletDTO> partialUpdateScanPallet(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ScanPalletDTO scanPalletDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to partial update ScanPallet partially : {}, {}",
            id,
            scanPalletDTO
        );
        if (scanPalletDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, scanPalletDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!scanPalletRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<ScanPalletDTO> result = scanPalletService.partialUpdate(
            scanPalletDTO
        );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                scanPalletDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /scan-pallets} : get all the scanPallets.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of scanPallets in body.
     */
    @GetMapping("")
    public List<ScanPalletDTO> getAllScanPallets() {
        log.debug("REST request to get all ScanPallets");
        return scanPalletService.findAll();
    }

    /**
     * {@code GET  /scan-pallets/:id} : get the "id" scanPallet.
     *
     * @param id the id of the scanPalletDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scanPalletDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScanPalletDTO> getScanPallet(
        @PathVariable("id") Long id
    ) {
        log.debug("REST request to get ScanPallet : {}", id);
        Optional<ScanPalletDTO> scanPalletDTO = scanPalletService.findOne(id);
        return ResponseUtil.wrapOrNotFound(scanPalletDTO);
    }

    /**
     * {@code DELETE  /scan-pallets/:id} : delete the "id" scanPallet.
     *
     * @param id the id of the scanPalletDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScanPallet(@PathVariable("id") Long id) {
        log.debug("REST request to delete ScanPallet : {}", id);
        scanPalletService.delete(id);
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
