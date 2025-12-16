package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.GenTemConfigRepository;
import com.mycompany.myapp.service.GenTemConfigService;
import com.mycompany.myapp.service.dto.GenTemConfigDTO;
import com.mycompany.myapp.service.dto.GenTemConfigSimpleDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.GenTemConfig}.
 */
@RestController
@RequestMapping("/api/gen-note-configs")
public class GenTemConfigResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        GenTemConfigResource.class
    );

    private static final String ENTITY_NAME = "genTemConfig";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GenTemConfigService genTemConfigService;

    private final GenTemConfigRepository genTemConfigRepository;

    public GenTemConfigResource(
        GenTemConfigService genTemConfigService,
        GenTemConfigRepository genTemConfigRepository
    ) {
        this.genTemConfigService = genTemConfigService;
        this.genTemConfigRepository = genTemConfigRepository;
    }

    /**
     * {@code POST  /gen-note-configs} : Create a new genTemConfig.
     *
     * @param genTemConfigDTO the genTemConfigDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new genTemConfigDTO, or with status
     *         {@code 400 (Bad Request)} if the genTemConfig has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<GenTemConfigDTO> createGenTemConfig(
        @Valid @RequestBody GenTemConfigDTO genTemConfigDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save GenTemConfig : {}", genTemConfigDTO);
        if (genTemConfigDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new genTemConfig cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        genTemConfigDTO = genTemConfigService.save(genTemConfigDTO);
        return ResponseEntity.created(
            new URI("/api/gen-note-configs/" + genTemConfigDTO.getId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    genTemConfigDTO.getId().toString()
                )
            )
            .body(genTemConfigDTO);
    }

    /**
     * {@code POST  /gen-note-configs/simple} : Create a new genTemConfig using
     * simple DTO.
     *
     * @param genTemConfigSimpleDTO the genTemConfigSimpleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new genTemConfigSimpleDTO, or with status
     *         {@code 400 (Bad Request)} if the genTemConfig has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/simple")
    public ResponseEntity<GenTemConfigSimpleDTO> createGenTemConfigSimple(
        @Valid @RequestBody GenTemConfigSimpleDTO genTemConfigSimpleDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to save GenTemConfig (simple) : {}",
            genTemConfigSimpleDTO
        );
        if (genTemConfigSimpleDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new genTemConfig cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        genTemConfigSimpleDTO = genTemConfigService.saveSimple(
            genTemConfigSimpleDTO
        );
        return ResponseEntity.created(
            new URI(
                "/api/gen-note-configs/simple/" + genTemConfigSimpleDTO.getId()
            )
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    genTemConfigSimpleDTO.getId().toString()
                )
            )
            .body(genTemConfigSimpleDTO);
    }

    /**
     * {@code PUT  /gen-note-configs/:id} : Updates an existing genTemConfig.
     *
     * @param id              the id of the genTemConfigDTO to save.
     * @param genTemConfigDTO the genTemConfigDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated genTemConfigDTO,
     *         or with status {@code 400 (Bad Request)} if the genTemConfigDTO is
     *         not valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         genTemConfigDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<GenTemConfigDTO> updateGenTemConfig(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GenTemConfigDTO genTemConfigDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to update GenTemConfig : {}, {}",
            id,
            genTemConfigDTO
        );
        if (genTemConfigDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, genTemConfigDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!genTemConfigRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        genTemConfigDTO = genTemConfigService.update(genTemConfigDTO);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    genTemConfigDTO.getId().toString()
                )
            )
            .body(genTemConfigDTO);
    }

    /**
     * {@code PATCH  /gen-note-configs/:id} : Partial updates given fields of an
     * existing genTemConfig, field will ignore if it is null
     *
     * @param id              the id of the genTemConfigDTO to save.
     * @param genTemConfigDTO the genTemConfigDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated genTemConfigDTO,
     *         or with status {@code 400 (Bad Request)} if the genTemConfigDTO is
     *         not valid,
     *         or with status {@code 404 (Not Found)} if the genTemConfigDTO is not
     *         found,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         genTemConfigDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<GenTemConfigDTO> partialUpdateGenTemConfig(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GenTemConfigDTO genTemConfigDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update GenTemConfig partially : {}, {}",
            id,
            genTemConfigDTO
        );
        if (genTemConfigDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, genTemConfigDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!genTemConfigRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<GenTemConfigDTO> result = genTemConfigService.partialUpdate(
            genTemConfigDTO
        );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                genTemConfigDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /gen-note-configs} : get all the genTemConfigs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of genTemConfigs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<GenTemConfigDTO>> getAllGenTemConfigs(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of GenTemConfigs");
        Page<GenTemConfigDTO> page = genTemConfigService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /gen-note-configs/:id} : get the "id" genTemConfig.
     *
     * @param id the id of the genTemConfigDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the genTemConfigDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GenTemConfigDTO> getGenTemConfig(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to get GenTemConfig : {}", id);
        Optional<GenTemConfigDTO> genTemConfigDTO = genTemConfigService.findOne(
            id
        );
        return ResponseUtil.wrapOrNotFound(genTemConfigDTO);
    }

    /**
     * {@code DELETE  /gen-note-configs/:id} : delete the "id" genTemConfig.
     *
     * @param id the id of the genTemConfigDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenTemConfig(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to delete GenTemConfig : {}", id);
        genTemConfigService.delete(id);
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
     * {@code GET  /gen-note-configs/by-ma-lenh-san-xuat/:maLenhSanXuatId} : get all
     * genTemConfigs by ma_lenh_san_xuat_id.
     *
     * @param maLenhSanXuatId the id of the WarehouseNoteInfo to filter by.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of genTemConfigs in body.
     */
    @GetMapping("/by-ma-lenh-san-xuat/{maLenhSanXuatId}")
    public ResponseEntity<
        List<GenTemConfigDTO>
    > getGenTemConfigsByMaLenhSanXuatId(
        @PathVariable("maLenhSanXuatId") Long maLenhSanXuatId
    ) {
        LOG.debug(
            "REST request to get GenTemConfigs by maLenhSanXuatId : {}",
            maLenhSanXuatId
        );
        List<GenTemConfigDTO> genTemConfigDTOs =
            genTemConfigService.findByMaLenhSanXuatId(maLenhSanXuatId);
        return ResponseEntity.ok().body(genTemConfigDTOs);
    }

    /**
     * {@code GET  /gen-note-configs/simple/by-ma-lenh-san-xuat/:maLenhSanXuatId} :
     * get all genTemConfigs by ma_lenh_san_xuat_id using simple DTO.
     *
     * @param maLenhSanXuatId the id of the WarehouseNoteInfo to filter by.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of genTemConfigs in body.
     */
    @GetMapping("/simple/by-ma-lenh-san-xuat/{maLenhSanXuatId}")
    public ResponseEntity<
        List<GenTemConfigSimpleDTO>
    > getGenTemConfigsSimpleByMaLenhSanXuatId(
        @PathVariable("maLenhSanXuatId") Long maLenhSanXuatId
    ) {
        LOG.debug(
            "REST request to get GenTemConfigs (simple) by maLenhSanXuatId : {}",
            maLenhSanXuatId
        );
        List<GenTemConfigSimpleDTO> genTemConfigSimpleDTOs =
            genTemConfigService.findSimpleByMaLenhSanXuatId(maLenhSanXuatId);
        return ResponseEntity.ok().body(genTemConfigSimpleDTOs);
    }

    /**
     * {@code PATCH  /gen-note-configs/simple/upsert} : Upsert a genTemConfig using
     * simple DTO.
     * If a record with the given ma_lenh_san_xuat_id exists, update it. Otherwise,
     * insert a new record.
     *
     * @param genTemConfigSimpleDTO the genTemConfigSimpleDTO to upsert.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the upserted genTemConfigSimpleDTO, or with status
     *         {@code 400 (Bad Request)} if the ma_lenh_san_xuat_id is not provided.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping("/simple/upsert")
    public ResponseEntity<GenTemConfigSimpleDTO> upsertGenTemConfigSimple(
        @Valid @RequestBody GenTemConfigSimpleDTO genTemConfigSimpleDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to upsert GenTemConfig (simple) : {}",
            genTemConfigSimpleDTO
        );

        if (genTemConfigSimpleDTO.getMaLenhSanXuatId() == null) {
            throw new BadRequestAlertException(
                "ma_lenh_san_xuat_id is required for upsert operation",
                ENTITY_NAME,
                "malenhsanxuatidnull"
            );
        }

        genTemConfigSimpleDTO = genTemConfigService.upsertSimple(
            genTemConfigSimpleDTO
        );

        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createAlert(
                    applicationName,
                    "GenTemConfig upserted successfully",
                    genTemConfigSimpleDTO.getId().toString()
                )
            )
            .body(genTemConfigSimpleDTO);
    }
}
