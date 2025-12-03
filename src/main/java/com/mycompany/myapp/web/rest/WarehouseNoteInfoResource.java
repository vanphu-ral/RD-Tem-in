package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.Partner3WarehouseStampInfoService;
import com.mycompany.myapp.service.WarehouseStampInfoService;
import com.mycompany.myapp.service.dto.WarehouseNoteInfoWithChildrenDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing
 * {@link com.mycompany.myapp.domain.WarehouseNoteInfo} in partner3 PostgreSQL
 * database.
 */
@RestController
@RequestMapping("/api")
@Transactional("partner3TransactionManager")
public class WarehouseNoteInfoResource {

    private final Logger log = LoggerFactory.getLogger(
        WarehouseNoteInfoResource.class
    );

    private static final String ENTITY_NAME = "warehouseNoteInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final Partner3WarehouseStampInfoService partner3WarehouseStampInfoService;
    private final WarehouseStampInfoService warehouseStampInfoService;

    public WarehouseNoteInfoResource(
        Partner3WarehouseStampInfoService partner3WarehouseStampInfoService,
        WarehouseStampInfoService warehouseStampInfoService
    ) {
        this.partner3WarehouseStampInfoService =
            partner3WarehouseStampInfoService;
        this.warehouseStampInfoService = warehouseStampInfoService;
    }

    /**
     * {@code POST  /warehouse-note-infos} : Create a new warehouseNoteInfo with
     * details in partner3 database.
     *
     * @param warehouseNoteInfoWithChildrenDTO the warehouseNoteInfoWithChildrenDTO
     *                                         to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new warehouseNoteInfoWithChildrenDTO, or with status
     *         {@code 400 (Bad Request)} if the warehouseNoteInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/warehouse-note-infos")
    public ResponseEntity<
        WarehouseNoteInfoWithChildrenDTO
    > createWarehouseNoteInfo(
        @RequestBody WarehouseStampInfoDTO warehouseStampInfoDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to save WarehouseNoteInfo : {}",
            warehouseStampInfoDTO
        );

        if (warehouseStampInfoDTO == null) {
            throw new BadRequestAlertException(
                "warehouseNoteInfo is required",
                ENTITY_NAME,
                "missing"
            );
        }

        if (warehouseStampInfoDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new warehouseNoteInfo cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }

        // Wrap the flat DTO into the nested structure
        WarehouseNoteInfoWithChildrenDTO warehouseNoteInfoWithChildrenDTO =
            new WarehouseNoteInfoWithChildrenDTO();
        warehouseNoteInfoWithChildrenDTO.setWarehouseNoteInfo(
            warehouseStampInfoDTO
        );

        WarehouseNoteInfoWithChildrenDTO result =
            partner3WarehouseStampInfoService.create(
                warehouseNoteInfoWithChildrenDTO
            );

        // Null check before accessing nested properties
        if (result.getWarehouseNoteInfo() == null) {
            throw new BadRequestAlertException(
                "Failed to create warehouseNoteInfo",
                ENTITY_NAME,
                "createfailed"
            );
        }

        return ResponseEntity.created(
            new URI(
                "/api/warehouse-note-infos/" +
                result.getWarehouseNoteInfo().getId()
            )
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    result.getWarehouseNoteInfo().getId().toString()
                )
            )
            .body(result);
    }

    /**
     * {@code PUT  /warehouse-note-infos/:id} : Updates an existing
     * warehouseNoteInfo.
     *
     * @param id                    the id of the warehouseNoteInfo to save.
     * @param warehouseStampInfoDTO the warehouseStampInfoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated warehouseStampInfoDTO,
     *         or with status {@code 400 (Bad Request)} if the warehouseStampInfoDTO
     *         is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         warehouseStampInfoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/warehouse-note-infos/{id}")
    public ResponseEntity<WarehouseStampInfoDTO> updateWarehouseNoteInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody WarehouseStampInfoDTO warehouseStampInfoDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to update WarehouseNoteInfo : {}, {}",
            id,
            warehouseStampInfoDTO
        );
        if (warehouseStampInfoDTO.getId() == null) {
            warehouseStampInfoDTO.setId(id);
        }
        if (warehouseStampInfoDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!java.util.Objects.equals(id, warehouseStampInfoDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        WarehouseStampInfoDTO result = partner3WarehouseStampInfoService.update(
            warehouseStampInfoDTO
        );
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    warehouseStampInfoDTO.getId().toString()
                )
            )
            .body(result);
    }

    /**
     * {@code PATCH  /warehouse-note-infos/:id} : Partial updates given fields of
     * an existing warehouseNoteInfo, field will ignore if it is null
     *
     * @param id                    the id of the warehouseNoteInfoDTO to save.
     * @param warehouseStampInfoDTO the warehouseNoteInfoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated warehouseStampInfoDTO,
     *         or with status {@code 400 (Bad Request)} if the warehouseStampInfoDTO
     *         is not valid,
     *         or with status {@code 404 (Not Found)} if the warehouseStampInfoDTO
     *         is not found,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         warehouseStampInfoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/warehouse-note-infos/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<WarehouseStampInfoDTO> partialUpdateWarehouseNoteInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WarehouseStampInfoDTO warehouseStampInfoDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to partial update WarehouseNoteInfo partially : {}, {}",
            id,
            warehouseStampInfoDTO
        );
        if (warehouseStampInfoDTO.getId() == null) {
            warehouseStampInfoDTO.setId(id);
        }
        if (warehouseStampInfoDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!java.util.Objects.equals(id, warehouseStampInfoDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!partner3WarehouseStampInfoService.findOne(id).isPresent()) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<WarehouseStampInfoDTO> result =
            warehouseStampInfoService.partialUpdate(warehouseStampInfoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                warehouseStampInfoDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /warehouse-note-infos/:id/with-children} : get the "id"
     * warehouseNoteInfo with all child tables data.
     *
     * @param id the id of the warehouseNoteInfo to retrieve with children.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the warehouseNoteInfoWithChildrenDTO, or with status
     *         {@code 404 (Not Found)}.
     */
    @GetMapping("/warehouse-note-infos/{id}/with-children")
    public ResponseEntity<
        WarehouseNoteInfoWithChildrenDTO
    > getWarehouseNoteInfoWithChildren(@PathVariable("id") Long id) {
        log.debug(
            "REST request to get WarehouseNoteInfo with children : {}",
            id
        );
        Optional<
            WarehouseNoteInfoWithChildrenDTO
        > warehouseNoteInfoWithChildrenDTO =
            warehouseStampInfoService.findOneWithChildren(id);
        return ResponseUtil.wrapOrNotFound(warehouseNoteInfoWithChildrenDTO);
    }

    /**
     * {@code GET  /warehouse-note-infos} : get all the warehouseNoteInfos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of warehouseNoteInfos in body.
     */
    @GetMapping("/warehouse-note-infos")
    public ResponseEntity<List<WarehouseStampInfoDTO>> getAllWarehouseNoteInfos(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of WarehouseNoteInfos");
        // Sort by timeUpdate descending to show newly updated records first
        Pageable sortedPageable =
            org.springframework.data.domain.PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                org.springframework.data.domain.Sort.by(
                    org.springframework.data.domain.Sort.Direction.DESC,
                    "timeUpdate"
                )
            );
        Page<WarehouseStampInfoDTO> page = warehouseStampInfoService.findAll(
            sortedPageable
        );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /warehouse-note-infos/:id} : get the "id" warehouseNoteInfo.
     *
     * @param id the id of the warehouseStampInfoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the warehouseStampInfoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/warehouse-note-infos/{id}")
    public ResponseEntity<WarehouseStampInfoDTO> getWarehouseNoteInfo(
        @PathVariable("id") Long id
    ) {
        log.debug("REST request to get WarehouseNoteInfo : {}", id);
        Optional<WarehouseStampInfoDTO> warehouseStampInfoDTO =
            partner3WarehouseStampInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(warehouseStampInfoDTO);
    }

    /**
     * {@code GET  /warehouse-note-infos/create-by/:createBy} : get all
     * warehouseNoteInfos by createBy.
     *
     * @param createBy the createBy to filter by.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of warehouseNoteInfos in body.
     */
    @GetMapping("/warehouse-note-infos/create-by/{createBy}")
    public ResponseEntity<
        List<WarehouseStampInfoDTO>
    > getWarehouseNoteInfosByCreateBy(@PathVariable String createBy) {
        log.debug(
            "REST request to get WarehouseNoteInfos by createBy : {}",
            createBy
        );
        List<WarehouseStampInfoDTO> warehouseStampInfoDTOs =
            partner3WarehouseStampInfoService.findByCreateBy(createBy);
        return ResponseEntity.ok().body(warehouseStampInfoDTOs);
    }

    /**
     * {@code GET  /warehouse-note-infos/approver-by/:approverBy} : get all
     * warehouseNoteInfos by approverBy.
     *
     * @param approverBy the approverBy to filter by.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of warehouseNoteInfos in body.
     */
    @GetMapping("/warehouse-note-infos/approver-by/{approverBy}")
    public ResponseEntity<
        List<WarehouseStampInfoDTO>
    > getWarehouseNoteInfosByApproverBy(@PathVariable String approverBy) {
        log.debug(
            "REST request to get WarehouseNoteInfos by approverBy : {}",
            approverBy
        );
        List<WarehouseStampInfoDTO> warehouseStampInfoDTOs =
            partner3WarehouseStampInfoService.findByApproverBy(approverBy);
        return ResponseEntity.ok().body(warehouseStampInfoDTOs);
    }

    /**
     * {@code GET  /warehouse-note-infos/not-draft} : get all warehouseNoteInfos
     * where trangThai is not 'Bản nháp'.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of warehouseNoteInfos in body.
     */
    @GetMapping("/warehouse-note-infos/not-draft")
    public ResponseEntity<
        List<WarehouseStampInfoDTO>
    > getWarehouseNoteInfosNotDraft() {
        log.debug(
            "REST request to get WarehouseNoteInfos where trangThai != 'Bản nháp'"
        );
        List<WarehouseStampInfoDTO> warehouseStampInfoDTOs =
            partner3WarehouseStampInfoService.findByTrangThaiNotDraft();
        return ResponseEntity.ok().body(warehouseStampInfoDTOs);
    }
}
