package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.WarehouseStampInfoDetailRepository;
import com.mycompany.myapp.service.WarehouseStampInfoDetailService;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDetailDTO;
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
import org.springdoc.api.annotations.ParameterObject;
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
 * {@link com.mycompany.myapp.domain.WarehouseNoteInfoDetail}.
 */
@RestController
@RequestMapping("/api/warehouse-stamp-info-details")
public class WarehouseStampInfoDetailResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        WarehouseStampInfoDetailResource.class
    );

    private static final String ENTITY_NAME = "warehouseStampInfoDetail";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WarehouseStampInfoDetailService warehouseStampInfoDetailService;

    private final WarehouseStampInfoDetailRepository warehouseStampInfoDetailRepository;

    public WarehouseStampInfoDetailResource(
        WarehouseStampInfoDetailService warehouseStampInfoDetailService,
        WarehouseStampInfoDetailRepository warehouseStampInfoDetailRepository
    ) {
        this.warehouseStampInfoDetailService = warehouseStampInfoDetailService;
        this.warehouseStampInfoDetailRepository =
            warehouseStampInfoDetailRepository;
    }

    /**
     * {@code POST  /warehouse-note-info-details} : Create a new
     * warehouseStampInfoDetail.
     *
     * @param warehouseStampInfoDetailDTO the warehouseStampInfoDetailDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new warehouseStampInfoDetailDTO, or with status
     *         {@code 400 (Bad Request)} if the warehouseStampInfoDetail has already
     *         an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<
        WarehouseStampInfoDetailDTO
    > createWarehouseStampInfoDetail(
        @Valid @RequestBody WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to save WarehouseNoteInfoDetail : {}",
            warehouseStampInfoDetailDTO
        );
        if (warehouseStampInfoDetailDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new warehouseStampInfoDetail cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        warehouseStampInfoDetailDTO = warehouseStampInfoDetailService.save(
            warehouseStampInfoDetailDTO
        );
        return ResponseEntity.created(
            new URI(
                "/api/warehouse-stamp-info-details/" +
                warehouseStampInfoDetailDTO.getId()
            )
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    warehouseStampInfoDetailDTO.getId().toString()
                )
            )
            .body(warehouseStampInfoDetailDTO);
    }

    /**
     * {@code PUT  /warehouse-note-info-details/:id} : Updates an existing
     * warehouseStampInfoDetail.
     *
     * @param id                          the id of the warehouseStampInfoDetailDTO
     *                                    to save.
     * @param warehouseStampInfoDetailDTO the warehouseStampInfoDetailDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated warehouseStampInfoDetailDTO,
     *         or with status {@code 400 (Bad Request)} if the
     *         warehouseStampInfoDetailDTO is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         warehouseStampInfoDetailDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<
        WarehouseStampInfoDetailDTO
    > updateWarehouseStampInfoDetail(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to update WarehouseNoteInfoDetail : {}, {}",
            id,
            warehouseStampInfoDetailDTO
        );
        if (warehouseStampInfoDetailDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, warehouseStampInfoDetailDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!warehouseStampInfoDetailRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        warehouseStampInfoDetailDTO = warehouseStampInfoDetailService.update(
            warehouseStampInfoDetailDTO
        );
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    warehouseStampInfoDetailDTO.getId().toString()
                )
            )
            .body(warehouseStampInfoDetailDTO);
    }

    /**
     * {@code PUT  /warehouse-note-info-details} : Batch update warehouse stamp info
     * details.
     *
     * @param warehouseStampInfoDetailDTOs the list of warehouseStampInfoDetailDTOs
     *                                     to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the list of updated warehouseStampInfoDetailDTOs.
     */
    @PutMapping("")
    public ResponseEntity<
        List<WarehouseStampInfoDetailDTO>
    > batchUpdateWarehouseStampInfoDetails(
        @Valid @RequestBody List<
            WarehouseStampInfoDetailDTO
        > warehouseStampInfoDetailDTOs
    ) {
        LOG.debug(
            "REST request to batch update WarehouseStampInfoDetails : {}",
            warehouseStampInfoDetailDTOs
        );

        List<WarehouseStampInfoDetailDTO> updatedDTOs =
            warehouseStampInfoDetailService.batchUpdate(
                warehouseStampInfoDetailDTOs
            );

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
     * {@code PUT  /warehouse-note-info-details/update/:id} : Updates an existing
     * warehouseStampInfoDetail (partner3 database compatible endpoint).
     * This endpoint mirrors the logic of /api/chi-tiet-lenh-san-xuat/update/{id}
     *
     * @param id                          the id of the warehouseStampInfoDetailDTO
     *                                    to update.
     * @param warehouseStampInfoDetailDTO the warehouseStampInfoDetailDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated warehouseStampInfoDetailDTO,
     *         or with status {@code 400 (Bad Request)} if the
     *         warehouseStampInfoDetailDTO is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         warehouseStampInfoDetailDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<
        WarehouseStampInfoDetailDTO
    > updateWarehouseStampInfoDetailById(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to update WarehouseNoteInfoDetail by id : {}, {}",
            id,
            warehouseStampInfoDetailDTO
        );
        if (warehouseStampInfoDetailDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, warehouseStampInfoDetailDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!warehouseStampInfoDetailRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        warehouseStampInfoDetailDTO = warehouseStampInfoDetailService.update(
            warehouseStampInfoDetailDTO
        );
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    warehouseStampInfoDetailDTO.getId().toString()
                )
            )
            .body(warehouseStampInfoDetailDTO);
    }

    /**
     * {@code PATCH  /warehouse-note-info-details/:id} : Partial updates given
     * fields of an existing warehouseStampInfoDetail, field will ignore if it is
     * null
     *
     * @param id                          the id of the warehouseStampInfoDetailDTO
     *                                    to save.
     * @param warehouseStampInfoDetailDTO the warehouseStampInfoDetailDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated warehouseStampInfoDetailDTO,
     *         or with status {@code 400 (Bad Request)} if the
     *         warehouseStampInfoDetailDTO is not valid,
     *         or with status {@code 404 (Not Found)} if the
     *         warehouseStampInfoDetailDTO is not found,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         warehouseStampInfoDetailDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<
        WarehouseStampInfoDetailDTO
    > partialUpdateWarehouseStampInfoDetail(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update WarehouseNoteInfoDetail partially : {}, {}",
            id,
            warehouseStampInfoDetailDTO
        );
        if (warehouseStampInfoDetailDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, warehouseStampInfoDetailDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!warehouseStampInfoDetailRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<WarehouseStampInfoDetailDTO> result =
            warehouseStampInfoDetailService.partialUpdate(
                warehouseStampInfoDetailDTO
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                warehouseStampInfoDetailDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /warehouse-note-info-details} : get all the
     * warehouseStampInfoDetails.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of warehouseStampInfoDetails in body.
     */
    @GetMapping("")
    public ResponseEntity<
        List<WarehouseStampInfoDetailDTO>
    > getAllWarehouseStampInfoDetails(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of WarehouseStampInfoDetails");
        Page<WarehouseStampInfoDetailDTO> page =
            warehouseStampInfoDetailService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /warehouse-note-info-details/detail/:id} : get the "id"
     * warehouseStampInfoDetail.
     *
     * @param id the id of the warehouseStampInfoDetailDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the warehouseStampInfoDetailDTO, or with status
     *         {@code 404 (Not Found)}.
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<
        WarehouseStampInfoDetailDTO
    > getWarehouseStampInfoDetail(@PathVariable("id") Long id) {
        LOG.debug("REST request to get WarehouseNoteInfoDetail : {}", id);
        Optional<WarehouseStampInfoDetailDTO> warehouseStampInfoDetailDTO =
            warehouseStampInfoDetailService.findOne(id);
        return ResponseUtil.wrapOrNotFound(warehouseStampInfoDetailDTO);
    }

    /**
     * {@code GET  /warehouse-note-info-details/:ma_lenh_san_xuat_id} : get all the
     * warehouseStampInfoDetails by maLenhSanXuatId.
     *
     * @param maLenhSanXuatId the id of the WarehouseNoteInfo to retrieve details
     *                        for.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of warehouseStampInfoDetails in body.
     */
    @GetMapping("/{ma_lenh_san_xuat_id}")
    public ResponseEntity<
        List<WarehouseStampInfoDetailDTO>
    > getWarehouseNoteInfoDetailsByMaLenhSanXuatId(
        @PathVariable("ma_lenh_san_xuat_id") Long maLenhSanXuatId
    ) {
        LOG.debug(
            "REST request to get WarehouseNoteInfoDetails by maLenhSanXuatId : {}",
            maLenhSanXuatId
        );
        List<WarehouseStampInfoDetailDTO> result =
            warehouseStampInfoDetailService.findByMaLenhSanXuatId(
                maLenhSanXuatId
            );
        return ResponseEntity.ok(result);
    }

    /**
     * {@code DELETE  /warehouse-note-info-details/:id} : delete the "id"
     * warehouseStampInfoDetail.
     *
     * @param id the id of the warehouseStampInfoDetailDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouseStampInfoDetail(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to delete WarehouseNoteInfoDetail : {}", id);
        warehouseStampInfoDetailService.delete(id);
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
     * {@code DELETE  /warehouse-note-info-details} : batch delete warehouse stamp
     * info
     * details.
     *
     * @param warehouseStampInfoDetailDTOs the list of warehouseStampInfoDetailDTOs
     *                                     to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("")
    public ResponseEntity<Void> batchDeleteWarehouseStampInfoDetails(
        @RequestBody List<
            WarehouseStampInfoDetailDTO
        > warehouseStampInfoDetailDTOs
    ) {
        LOG.debug(
            "REST request to batch delete WarehouseStampInfoDetails : {}",
            warehouseStampInfoDetailDTOs
        );

        warehouseStampInfoDetailService.batchDelete(
            warehouseStampInfoDetailDTOs
        );

        return ResponseEntity.noContent()
            .headers(
                HeaderUtil.createAlert(
                    applicationName,
                    "Batch delete completed successfully. Deleted " +
                    warehouseStampInfoDetailDTOs.size() +
                    " records.",
                    ENTITY_NAME
                )
            )
            .build();
    }
}
