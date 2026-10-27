package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.SplitInventoryRepository;
import com.mycompany.myapp.service.SplitInventoryQueryService;
import com.mycompany.myapp.service.SplitInventoryService;
import com.mycompany.myapp.service.criteria.SplitInventoryCriteria;
import com.mycompany.myapp.service.dto.SplitInventoryDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.SplitInventory}.
 */
@RestController
@RequestMapping("/api/split-inventories")
public class SplitInventoryResource {

    private final Logger log = LoggerFactory.getLogger(
        SplitInventoryResource.class
    );

    private static final String ENTITY_NAME = "splitInventory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SplitInventoryService splitInventoryService;

    private final SplitInventoryRepository splitInventoryRepository;

    private final SplitInventoryQueryService splitInventoryQueryService;

    public SplitInventoryResource(
        SplitInventoryService splitInventoryService,
        SplitInventoryRepository splitInventoryRepository,
        SplitInventoryQueryService splitInventoryQueryService
    ) {
        this.splitInventoryService = splitInventoryService;
        this.splitInventoryRepository = splitInventoryRepository;
        this.splitInventoryQueryService = splitInventoryQueryService;
    }

    /**
     * {@code POST  /split-inventories} : Create a new splitInventory.
     *
     * @param splitInventoryDTO the splitInventoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new splitInventoryDTO, or with status {@code 400 (Bad Request)} if the splitInventory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SplitInventoryDTO> createSplitInventory(
        @Valid @RequestBody SplitInventoryDTO splitInventoryDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to save SplitInventory : {}",
            splitInventoryDTO
        );
        if (splitInventoryDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new splitInventory cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        splitInventoryDTO = splitInventoryService.save(splitInventoryDTO);
        return ResponseEntity.created(
            new URI("/api/split-inventories/" + splitInventoryDTO.getId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    splitInventoryDTO.getId().toString()
                )
            )
            .body(splitInventoryDTO);
    }

    /**
     * {@code PUT  /split-inventories/:id} : Updates an existing splitInventory.
     *
     * @param id the id of the splitInventoryDTO to save.
     * @param splitInventoryDTO the splitInventoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated splitInventoryDTO,
     * or with status {@code 400 (Bad Request)} if the splitInventoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the splitInventoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SplitInventoryDTO> updateSplitInventory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SplitInventoryDTO splitInventoryDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to update SplitInventory : {}, {}",
            id,
            splitInventoryDTO
        );
        if (splitInventoryDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, splitInventoryDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!splitInventoryRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        splitInventoryDTO = splitInventoryService.update(splitInventoryDTO);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    splitInventoryDTO.getId().toString()
                )
            )
            .body(splitInventoryDTO);
    }

    /**
     * {@code PATCH  /split-inventories/:id} : Partial updates given fields of an existing splitInventory, field will ignore if it is null
     *
     * @param id the id of the splitInventoryDTO to save.
     * @param splitInventoryDTO the splitInventoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated splitInventoryDTO,
     * or with status {@code 400 (Bad Request)} if the splitInventoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the splitInventoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the splitInventoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<SplitInventoryDTO> partialUpdateSplitInventory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SplitInventoryDTO splitInventoryDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to partial update SplitInventory partially : {}, {}",
            id,
            splitInventoryDTO
        );
        if (splitInventoryDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, splitInventoryDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!splitInventoryRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<SplitInventoryDTO> result =
            splitInventoryService.partialUpdate(splitInventoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                splitInventoryDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /split-inventories} : get all the splitInventories.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of splitInventories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SplitInventoryDTO>> getAllSplitInventories(
        SplitInventoryCriteria criteria,
        Pageable pageable
    ) {
        log.debug(
            "REST request to get SplitInventories by criteria: {}",
            criteria
        );

        Page<SplitInventoryDTO> page =
            splitInventoryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /split-inventories/count} : count all the splitInventories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSplitInventories(
        SplitInventoryCriteria criteria
    ) {
        log.debug(
            "REST request to count SplitInventories by criteria: {}",
            criteria
        );
        return ResponseEntity.ok().body(
            splitInventoryQueryService.countByCriteria(criteria)
        );
    }

    /**
     * {@code GET  /split-inventories/:id} : get the "id" splitInventory.
     *
     * @param id the id of the splitInventoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the splitInventoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SplitInventoryDTO> getSplitInventory(
        @PathVariable("id") Long id
    ) {
        log.debug("REST request to get SplitInventory : {}", id);
        Optional<SplitInventoryDTO> splitInventoryDTO =
            splitInventoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(splitInventoryDTO);
    }

    /**
     * {@code DELETE  /split-inventories/:id} : delete the "id" splitInventory.
     *
     * @param id the id of the splitInventoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSplitInventory(
        @PathVariable("id") Long id
    ) {
        log.debug("REST request to delete SplitInventory : {}", id);
        splitInventoryService.delete(id);
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
