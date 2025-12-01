package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.partner3.Partner3PalletInforDetailRepository;
import com.mycompany.myapp.service.PalletInforDetailService;
import com.mycompany.myapp.service.Partner3MigrationService;
import com.mycompany.myapp.service.dto.CombinedPalletWarehouseDTO;
import com.mycompany.myapp.service.dto.MaxSerialResponseDTO;
import com.mycompany.myapp.service.dto.PalletInforDetailDTO;
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
 * REST controller for managing
 * {@link com.mycompany.myapp.domain.PalletInforDetail}.
 */
@RestController
@RequestMapping("/api/pallet-infor-details")
public class PalletInforDetailResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        PalletInforDetailResource.class
    );

    private static final String ENTITY_NAME = "palletInforDetail";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PalletInforDetailService palletInforDetailService;

    private final Partner3PalletInforDetailRepository palletInforDetailRepository;

    private final Partner3MigrationService partner3MigrationService;

    public PalletInforDetailResource(
        PalletInforDetailService palletInforDetailService,
        Partner3PalletInforDetailRepository palletInforDetailRepository,
        Partner3MigrationService partner3MigrationService
    ) {
        this.palletInforDetailService = palletInforDetailService;
        this.palletInforDetailRepository = palletInforDetailRepository;
        this.partner3MigrationService = partner3MigrationService;
    }

    /**
     * {@code POST  /pallet-infor-details} : Create a new palletInforDetail.
     *
     * @param palletInforDetailDTO the palletInforDetailDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new palletInforDetailDTO, or with status
     *         {@code 400 (Bad Request)} if the palletInforDetail has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PalletInforDetailDTO> createPalletInforDetail(
        @Valid @RequestBody PalletInforDetailDTO palletInforDetailDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to save PalletInforDetail : {}",
            palletInforDetailDTO
        );
        if (
            palletInforDetailDTO.getSerialPallet() != null &&
            palletInforDetailRepository
                .findBySerialPallet(palletInforDetailDTO.getSerialPallet())
                .isPresent()
        ) {
            throw new BadRequestAlertException(
                "A new palletInforDetail cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        palletInforDetailDTO = palletInforDetailService.save(
            palletInforDetailDTO
        );
        return ResponseEntity.created(
            new URI(
                "/api/pallet-infor-details/" +
                palletInforDetailDTO.getSerialPallet()
            )
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    palletInforDetailDTO.getSerialPallet()
                )
            )
            .body(palletInforDetailDTO);
    }

    /**
     * {@code POST /pallet-infor-details/combined/{maLenhSanXuatId}} : Create or
     * update combined pallet and warehouse information.
     *
     * @param maLenhSanXuatId the ma_lenh_san_xuat_id to associate with the records.
     * @param combinedDTO     the combined data containing pallet and warehouse
     *                        information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the saved combined data.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/combined/{maLenhSanXuatId}")
    public ResponseEntity<CombinedPalletWarehouseDTO> saveCombinedData(
        @PathVariable("maLenhSanXuatId") Long maLenhSanXuatId,
        @Valid @RequestBody CombinedPalletWarehouseDTO combinedDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to save combined pallet and warehouse data for maLenhSanXuatId: {}, data: {}",
            maLenhSanXuatId,
            combinedDTO
        );

        CombinedPalletWarehouseDTO result =
            palletInforDetailService.saveCombinedData(
                maLenhSanXuatId,
                combinedDTO
            );

        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createAlert(
                    applicationName,
                    "Combined data saved successfully",
                    ENTITY_NAME
                )
            )
            .body(result);
    }

    /**
     * {@code POST  /pallet-infor-details/init-partner3-db} : Initialize partner3
     * database with migrations.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     */
    @PostMapping("/init-partner3-db")
    public ResponseEntity<Void> initPartner3Database() {
        LOG.debug("REST request to initialize partner3 database");

        try {
            partner3MigrationService.runMigrations();
        } catch (Exception e) {
            LOG.warn(
                "Liquibase migration failed, trying manual table creation",
                e
            );
            partner3MigrationService.createPalletTableManually();
        }

        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createAlert(
                    applicationName,
                    "Partner3 database initialized successfully",
                    ENTITY_NAME
                )
            )
            .build();
    }

    /**
     * {@code PUT  /pallet-infor-details/:id} : Updates an existing
     * palletInforDetail.
     *
     * @param id                   the id of the palletInforDetailDTO to save.
     * @param palletInforDetailDTO the palletInforDetailDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated palletInforDetailDTO,
     *         or with status {@code 400 (Bad Request)} if the palletInforDetailDTO
     *         is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         palletInforDetailDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PalletInforDetailDTO> updatePalletInforDetail(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody PalletInforDetailDTO palletInforDetailDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to update PalletInforDetail : {}, {}",
            id,
            palletInforDetailDTO
        );
        if (palletInforDetailDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        try {
            Long entityId = Long.valueOf(id);
            if (!Objects.equals(entityId, palletInforDetailDTO.getId())) {
                throw new BadRequestAlertException(
                    "Invalid ID",
                    ENTITY_NAME,
                    "idinvalid"
                );
            }
        } catch (NumberFormatException e) {
            throw new BadRequestAlertException(
                "Invalid ID format",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        try {
            Long entityId = Long.valueOf(id);
            if (!palletInforDetailRepository.findById(entityId).isPresent()) {
                throw new BadRequestAlertException(
                    "Entity not found",
                    ENTITY_NAME,
                    "idnotfound"
                );
            }
        } catch (NumberFormatException e) {
            throw new BadRequestAlertException(
                "Invalid ID format",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        palletInforDetailDTO = palletInforDetailService.update(
            palletInforDetailDTO
        );
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    palletInforDetailDTO.getSerialPallet()
                )
            )
            .body(palletInforDetailDTO);
    }

    /**
     * {@code PUT  /pallet-infor-details} : Batch update pallet information details.
     *
     * @param palletInforDetailDTOs the list of palletInforDetailDTOs to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the list of updated palletInforDetailDTOs.
     */
    @PutMapping("")
    public ResponseEntity<
        List<PalletInforDetailDTO>
    > batchUpdatePalletInforDetails(
        @Valid @RequestBody List<PalletInforDetailDTO> palletInforDetailDTOs
    ) {
        LOG.debug(
            "REST request to batch update PalletInforDetails : {}",
            palletInforDetailDTOs
        );

        List<PalletInforDetailDTO> updatedDTOs =
            palletInforDetailService.batchUpdate(palletInforDetailDTOs);

        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createAlert(
                    applicationName,
                    "Batch update completed successfully. Updated " +
                    updatedDTOs.size() +
                    " records.",
                    ENTITY_NAME
                )
            )
            .body(updatedDTOs);
    }

    /**
     * {@code PATCH  /pallet-infor-details/:id} : Partial updates given fields of an
     * existing palletInforDetail, field will ignore if it is null
     *
     * @param id                   the id of the palletInforDetailDTO to save.
     * @param palletInforDetailDTO the palletInforDetailDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated palletInforDetailDTO,
     *         or with status {@code 400 (Bad Request)} if the palletInforDetailDTO
     *         is not valid,
     *         or with status {@code 404 (Not Found)} if the palletInforDetailDTO is
     *         not found,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         palletInforDetailDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<PalletInforDetailDTO> partialUpdatePalletInforDetail(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody PalletInforDetailDTO palletInforDetailDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update PalletInforDetail partially : {}, {}",
            id,
            palletInforDetailDTO
        );
        if (palletInforDetailDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        try {
            Long entityId = Long.valueOf(id);
            if (!Objects.equals(entityId, palletInforDetailDTO.getId())) {
                throw new BadRequestAlertException(
                    "Invalid ID",
                    ENTITY_NAME,
                    "idinvalid"
                );
            }
        } catch (NumberFormatException e) {
            throw new BadRequestAlertException(
                "Invalid ID format",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        try {
            Long entityId = Long.valueOf(id);
            if (!palletInforDetailRepository.findById(entityId).isPresent()) {
                throw new BadRequestAlertException(
                    "Entity not found",
                    ENTITY_NAME,
                    "idnotfound"
                );
            }
        } catch (NumberFormatException e) {
            throw new BadRequestAlertException(
                "Invalid ID format",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        Optional<PalletInforDetailDTO> result =
            palletInforDetailService.partialUpdate(palletInforDetailDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                palletInforDetailDTO.getSerialPallet()
            )
        );
    }

    /**
     * {@code GET  /pallet-infor-details} : get all the palletInforDetails.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of palletInforDetails in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PalletInforDetailDTO>> getAllPalletInforDetails(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of PalletInforDetails");
        Page<PalletInforDetailDTO> page = palletInforDetailService.findAll(
            pageable
        );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pallet-infor-details/:id} : get the "id" palletInforDetail.
     *
     * @param id the id of the palletInforDetailDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the palletInforDetailDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PalletInforDetailDTO> getPalletInforDetail(
        @PathVariable("id") String id
    ) {
        LOG.debug("REST request to get PalletInforDetail : {}", id);
        Optional<PalletInforDetailDTO> palletInforDetailDTO =
            palletInforDetailService.findOne(id);
        return ResponseUtil.wrapOrNotFound(palletInforDetailDTO);
    }

    /**
     * {@code DELETE  /pallet-infor-details/:id} : delete the "id"
     * palletInforDetail.
     *
     * @param id the id of the palletInforDetailDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePalletInforDetail(
        @PathVariable("id") String id
    ) {
        LOG.debug("REST request to delete PalletInforDetail : {}", id);
        palletInforDetailService.delete(id);
        return ResponseEntity.noContent()
            .headers(
                HeaderUtil.createEntityDeletionAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    id
                )
            )
            .build();
    }

    /**
     * {@code DELETE  /pallet-infor-details} : batch delete pallet information details.
     *
     * @param palletInforDetailDTOs the list of palletInforDetailDTOs to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("")
    public ResponseEntity<Void> batchDeletePalletInforDetails(
        @RequestBody List<PalletInforDetailDTO> palletInforDetailDTOs
    ) {
        LOG.debug(
            "REST request to batch delete PalletInforDetails : {}",
            palletInforDetailDTOs
        );

        palletInforDetailService.batchDelete(palletInforDetailDTOs);

        return ResponseEntity.noContent()
            .headers(
                HeaderUtil.createAlert(
                    applicationName,
                    "Batch delete completed successfully. Deleted " +
                    palletInforDetailDTOs.size() +
                    " records.",
                    ENTITY_NAME
                )
            )
            .build();
    }

    /**
     * {@code GET  /pallet-infor-details/max-serials} : get the max serial numbers
     * starting with 'B'.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the max serial numbers.
     */
    @GetMapping("/max-serials")
    public ResponseEntity<MaxSerialResponseDTO> getMaxSerials() {
        LOG.debug("REST request to get max serials starting with B");
        MaxSerialResponseDTO result = palletInforDetailService.getMaxSerials();
        return ResponseEntity.ok().body(result);
    }
}
