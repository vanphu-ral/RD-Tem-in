package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.Partner3ProductionTeamService;
import com.mycompany.myapp.service.dto.ProductionTeamDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
 * {@link com.mycompany.myapp.domain.ProductionTeam}.
 */
@RestController
@RequestMapping("/api")
public class ProductionTeamResource {

    private final Logger log = LoggerFactory.getLogger(
        ProductionTeamResource.class
    );

    private static final String ENTITY_NAME = "productionTeam";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final Partner3ProductionTeamService partner3ProductionTeamService;

    public ProductionTeamResource(
        Partner3ProductionTeamService partner3ProductionTeamService
    ) {
        this.partner3ProductionTeamService = partner3ProductionTeamService;
    }

    /**
     * {@code POST  /production-teams} : Create a new productionTeam.
     *
     * @param productionTeamDTO the productionTeamDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new productionTeamDTO, or with status
     *         {@code 400 (Bad Request)} if the productionTeam has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/production-teams")
    public ResponseEntity<ProductionTeamDTO> createProductionTeam(
        @RequestBody ProductionTeamDTO productionTeamDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to save ProductionTeam : {}",
            productionTeamDTO
        );
        if (productionTeamDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new productionTeam cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        ProductionTeamDTO result = partner3ProductionTeamService.save(
            productionTeamDTO
        );
        return ResponseEntity.created(
            new URI("/api/production-teams/" + result.getId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    result.getId().toString()
                )
            )
            .body(result);
    }

    /**
     * {@code PUT  /production-teams/:id} : Updates an existing productionTeam.
     *
     * @param id                the id of the productionTeamDTO to save.
     * @param productionTeamDTO the productionTeamDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated productionTeamDTO,
     *         or with status {@code 400 (Bad Request)} if the productionTeamDTO is
     *         not valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         productionTeamDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/production-teams/{id}")
    public ResponseEntity<ProductionTeamDTO> updateProductionTeam(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProductionTeamDTO productionTeamDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to update ProductionTeam : {}, {}",
            id,
            productionTeamDTO
        );
        if (productionTeamDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, productionTeamDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!partner3ProductionTeamService.exists(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        ProductionTeamDTO result = partner3ProductionTeamService.update(
            productionTeamDTO
        );
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    productionTeamDTO.getId().toString()
                )
            )
            .body(result);
    }

    /**
     * {@code PATCH  /production-teams/:id} : Partial updates given fields of an
     * existing productionTeam, field will ignore if it is null
     *
     * @param id                the id of the productionTeamDTO to save.
     * @param productionTeamDTO the productionTeamDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated productionTeamDTO,
     *         or with status {@code 400 (Bad Request)} if the productionTeamDTO is
     *         not valid,
     *         or with status {@code 404 (Not Found)} if the productionTeamDTO is
     *         not found,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         productionTeamDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/production-teams/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<ProductionTeamDTO> partialUpdateProductionTeam(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProductionTeamDTO productionTeamDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to partial update ProductionTeam partially : {}, {}",
            id,
            productionTeamDTO
        );
        if (productionTeamDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, productionTeamDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        Optional<ProductionTeamDTO> result =
            partner3ProductionTeamService.partialUpdate(productionTeamDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                productionTeamDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /production-teams} : get all the productionTeams.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of productionTeams in body.
     */
    @GetMapping("/production-teams")
    public ResponseEntity<List<ProductionTeamDTO>> getAllProductionTeams(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of ProductionTeams");
        Page<ProductionTeamDTO> page = partner3ProductionTeamService.findAll(
            pageable
        );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /production-teams/:id} : get the "id" productionTeam.
     *
     * @param id the id of the productionTeamDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the productionTeamDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/production-teams/{id}")
    public ResponseEntity<ProductionTeamDTO> getProductionTeam(
        @PathVariable("id") Long id
    ) {
        log.debug("REST request to get ProductionTeam : {}", id);
        Optional<ProductionTeamDTO> productionTeamDTO =
            partner3ProductionTeamService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productionTeamDTO);
    }

    /**
     * {@code DELETE  /production-teams/:id} : delete the "id" productionTeam.
     *
     * @param id the id of the productionTeamDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/production-teams/{id}")
    public ResponseEntity<Void> deleteProductionTeam(
        @PathVariable("id") Long id
    ) {
        log.debug("REST request to delete ProductionTeam : {}", id);
        partner3ProductionTeamService.delete(id);
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
     * {@code SEARCH  /production-teams/_search?query=:query} : search for the
     * productionTeam corresponding
     * to the query.
     *
     * @param query    the query of the productionTeam search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/production-teams/_search")
    public ResponseEntity<List<ProductionTeamDTO>> searchProductionTeams(
        @RequestParam("query") String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug(
            "REST request to search for a page of ProductionTeams for query {}",
            query
        );
        throw new UnsupportedOperationException(
            "Search functionality not implemented yet"
        );
    }
}
