package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.WarehouseStampInfoRepository;
import com.mycompany.myapp.service.Partner3WarehouseStampInfoService;
import com.mycompany.myapp.service.WarehouseStampInfoService;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDTO;
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
 * {@link com.mycompany.myapp.domain.WarehouseStampInfo}.
 */
@RestController
@RequestMapping("/api/warehouse-stamp-infos")
public class WarehouseStampInfoResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        WarehouseStampInfoResource.class
    );

    private static final String ENTITY_NAME = "warehouseStampInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WarehouseStampInfoService warehouseStampInfoService;

    private final Partner3WarehouseStampInfoService partner3WarehouseStampInfoService;

    private final WarehouseStampInfoRepository warehouseStampInfoRepository;

    public WarehouseStampInfoResource(
        WarehouseStampInfoService warehouseStampInfoService,
        Partner3WarehouseStampInfoService partner3WarehouseStampInfoService,
        WarehouseStampInfoRepository warehouseStampInfoRepository
    ) {
        this.warehouseStampInfoService = warehouseStampInfoService;
        this.partner3WarehouseStampInfoService =
            partner3WarehouseStampInfoService;
        this.warehouseStampInfoRepository = warehouseStampInfoRepository;
    }

    /**
     * {@code POST  /warehouse-stamp-infos} : Create a new warehouseStampInfo.
     *
     * @param warehouseStampInfoDTO the warehouseStampInfoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new warehouseStampInfoDTO, or with status
     *         {@code 400 (Bad Request)} if the warehouseStampInfo has already an
     *         ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WarehouseStampInfoDTO> createWarehouseStampInfo(
        @Valid @RequestBody WarehouseStampInfoDTO warehouseStampInfoDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to save WarehouseStampInfo : {}",
            warehouseStampInfoDTO
        );
        if (warehouseStampInfoDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new warehouseStampInfo cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        warehouseStampInfoDTO = warehouseStampInfoService.save(
            warehouseStampInfoDTO
        );
        return ResponseEntity.created(
            new URI(
                "/api/warehouse-stamp-infos/" + warehouseStampInfoDTO.getId()
            )
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    warehouseStampInfoDTO.getId().toString()
                )
            )
            .body(warehouseStampInfoDTO);
    }

    /**
     * {@code PUT  /warehouse-stamp-infos/:id} : Updates an existing
     * warehouseStampInfo.
     *
     * @param id                    the id of the warehouseStampInfoDTO to save.
     * @param warehouseStampInfoDTO the warehouseStampInfoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated warehouseStampInfoDTO,
     *         or with status {@code 400 (Bad Request)} if the warehouseStampInfoDTO
     *         is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         warehouseStampInfoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WarehouseStampInfoDTO> updateWarehouseStampInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WarehouseStampInfoDTO warehouseStampInfoDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to update WarehouseStampInfo : {}, {}",
            id,
            warehouseStampInfoDTO
        );
        if (warehouseStampInfoDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, warehouseStampInfoDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!warehouseStampInfoRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        warehouseStampInfoDTO = warehouseStampInfoService.update(
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
            .body(warehouseStampInfoDTO);
    }

    /**
     * {@code PATCH  /warehouse-stamp-infos/:id} : Partial updates given fields of
     * an existing warehouseStampInfo, field will ignore if it is null
     *
     * @param id                    the id of the warehouseStampInfoDTO to save.
     * @param warehouseStampInfoDTO the warehouseStampInfoDTO to update.
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
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<
        WarehouseStampInfoDTO
    > partialUpdateWarehouseStampInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WarehouseStampInfoDTO warehouseStampInfoDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update WarehouseStampInfo partially : {}, {}",
            id,
            warehouseStampInfoDTO
        );
        if (warehouseStampInfoDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, warehouseStampInfoDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!warehouseStampInfoRepository.existsById(id)) {
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
     * {@code GET  /warehouse-stamp-infos} : get all the warehouseStampInfos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of warehouseStampInfos in body.
     */
    @GetMapping("")
    public ResponseEntity<
        List<WarehouseStampInfoDTO>
    > getAllWarehouseStampInfos(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of WarehouseStampInfos");
        Page<WarehouseStampInfoDTO> page = warehouseStampInfoService.findAll(
            pageable
        );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /warehouse-stamp-infos/create-by/:createBy} : get all
     * warehouseStampInfos by createBy.
     *
     * @param createBy the createBy to filter by.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of warehouseStampInfos in body.
     */
    @GetMapping("/create-by/{createBy}")
    public ResponseEntity<
        List<WarehouseStampInfoDTO>
    > getWarehouseStampInfosByCreateBy(@PathVariable String createBy) {
        LOG.debug(
            "REST request to get WarehouseStampInfos by createBy : {}",
            createBy
        );
        List<WarehouseStampInfoDTO> warehouseStampInfoDTOs =
            partner3WarehouseStampInfoService.findByCreateBy(createBy);
        return ResponseEntity.ok().body(warehouseStampInfoDTOs);
    }

    /**
     * {@code GET  /warehouse-stamp-infos/approver-by/:approverBy} : get all
     * warehouseStampInfos by approverBy.
     *
     * @param approverBy the approverBy to filter by.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of warehouseStampInfos in body.
     */
    @GetMapping("/approver-by/{approverBy}")
    public ResponseEntity<
        List<WarehouseStampInfoDTO>
    > getWarehouseStampInfosByApproverBy(@PathVariable String approverBy) {
        LOG.debug(
            "REST request to get WarehouseStampInfos by approverBy : {}",
            approverBy
        );
        List<WarehouseStampInfoDTO> warehouseStampInfoDTOs =
            partner3WarehouseStampInfoService.findByApproverBy(approverBy);
        return ResponseEntity.ok().body(warehouseStampInfoDTOs);
    }

    /**
     * {@code GET  /warehouse-stamp-infos/:id} : get the "id" warehouseStampInfo.
     *
     * @param id the id of the warehouseStampInfoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the warehouseStampInfoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseStampInfoDTO> getWarehouseStampInfo(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to get WarehouseStampInfo : {}", id);
        Optional<WarehouseStampInfoDTO> warehouseStampInfoDTO =
            partner3WarehouseStampInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(warehouseStampInfoDTO);
    }

    /**
     * {@code DELETE  /warehouse-stamp-infos/:id} : delete the "id"
     * warehouseStampInfo.
     *
     * @param id the id of the warehouseStampInfoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouseStampInfo(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to delete WarehouseStampInfo : {}", id);
        warehouseStampInfoService.delete(id);
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
