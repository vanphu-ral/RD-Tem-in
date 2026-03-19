package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.SapPoInfo;
import com.mycompany.myapp.repository.partner6.SapPoInfoRepository;
import com.mycompany.myapp.service.SapPoInfoQueryService;
import com.mycompany.myapp.service.SapPoInfoService;
import com.mycompany.myapp.service.criteria.SapPoInfoCriteria;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.SapPoInfo}.
 */
@RestController
@RequestMapping("/api/sap-po-infos")
public class SapPoInfoResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        SapPoInfoResource.class
    );

    private static final String ENTITY_NAME = "sapPoInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SapPoInfoService sapPoInfoService;

    private final SapPoInfoRepository sapPoInfoRepository;

    private final SapPoInfoQueryService sapPoInfoQueryService;

    public SapPoInfoResource(
        SapPoInfoService sapPoInfoService,
        SapPoInfoRepository sapPoInfoRepository,
        SapPoInfoQueryService sapPoInfoQueryService
    ) {
        this.sapPoInfoService = sapPoInfoService;
        this.sapPoInfoRepository = sapPoInfoRepository;
        this.sapPoInfoQueryService = sapPoInfoQueryService;
    }

    /**
     * {@code POST  /sap-po-infos} : Create a new sapPoInfo.
     *
     * @param sapPoInfo the sapPoInfo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new sapPoInfo, or with status {@code 400 (Bad Request)} if
     *         the sapPoInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SapPoInfo> createSapPoInfo(
        @Valid @RequestBody SapPoInfo sapPoInfo
    ) throws URISyntaxException {
        LOG.debug("REST request to save SapPoInfo : {}", sapPoInfo);
        if (sapPoInfo.getId() != null) {
            throw new BadRequestAlertException(
                "A new sapPoInfo cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        sapPoInfo = sapPoInfoService.save(sapPoInfo);
        return ResponseEntity.created(
            new URI("/api/sap-po-infos/" + sapPoInfo.getId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    sapPoInfo.getId().toString()
                )
            )
            .body(sapPoInfo);
    }

    /**
     * {@code PUT  /sap-po-infos/:id} : Updates an existing sapPoInfo.
     *
     * @param id        the id of the sapPoInfo to save.
     * @param sapPoInfo the sapPoInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated sapPoInfo,
     *         or with status {@code 400 (Bad Request)} if the sapPoInfo is not
     *         valid,
     *         or with status {@code 500 (Internal Server Error)} if the sapPoInfo
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SapPoInfo> updateSapPoInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SapPoInfo sapPoInfo
    ) throws URISyntaxException {
        LOG.debug("REST request to update SapPoInfo : {}, {}", id, sapPoInfo);
        if (sapPoInfo.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, sapPoInfo.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!sapPoInfoRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        sapPoInfo = sapPoInfoService.update(sapPoInfo);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    sapPoInfo.getId().toString()
                )
            )
            .body(sapPoInfo);
    }

    /**
     * {@code PATCH  /sap-po-infos/:id} : Partial updates given fields of an
     * existing sapPoInfo, field will ignore if it is null
     *
     * @param id        the id of the sapPoInfo to save.
     * @param sapPoInfo the sapPoInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated sapPoInfo,
     *         or with status {@code 400 (Bad Request)} if the sapPoInfo is not
     *         valid,
     *         or with status {@code 404 (Not Found)} if the sapPoInfo is not found,
     *         or with status {@code 500 (Internal Server Error)} if the sapPoInfo
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<SapPoInfo> partialUpdateSapPoInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SapPoInfo sapPoInfo
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update SapPoInfo partially : {}, {}",
            id,
            sapPoInfo
        );
        if (sapPoInfo.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, sapPoInfo.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!sapPoInfoRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<SapPoInfo> result = sapPoInfoService.partialUpdate(sapPoInfo);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                sapPoInfo.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /sap-po-infos} : get all the sapPoInfos.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of sapPoInfos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SapPoInfo>> getAllSapPoInfos(
        SapPoInfoCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get SapPoInfos by criteria: {}", criteria);

        Page<SapPoInfo> page = sapPoInfoQueryService.findByCriteria(
            criteria,
            pageable
        );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sap-po-infos/count} : count all the sapPoInfos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
     *         in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSapPoInfos(SapPoInfoCriteria criteria) {
        LOG.debug("REST request to count SapPoInfos by criteria: {}", criteria);
        return ResponseEntity.ok().body(
            sapPoInfoQueryService.countByCriteria(criteria)
        );
    }

    /**
     * {@code GET  /sap-po-infos/:id} : get the "id" sapPoInfo.
     *
     * @param id the id of the sapPoInfo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the sapPoInfo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SapPoInfo> getSapPoInfo(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SapPoInfo : {}", id);
        Optional<SapPoInfo> sapPoInfo = sapPoInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sapPoInfo);
    }

    /**
     * {@code DELETE  /sap-po-infos/:id} : delete the "id" sapPoInfo.
     *
     * @param id the id of the sapPoInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSapPoInfo(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SapPoInfo : {}", id);
        sapPoInfoService.delete(id);
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
