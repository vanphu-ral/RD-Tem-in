package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.partner5.TemIdentificationScenarioRepository;
import com.mycompany.myapp.service.TemIdentificationScenarioQueryService;
import com.mycompany.myapp.service.TemIdentificationScenarioService;
import com.mycompany.myapp.service.criteria.TemIdentificationScenarioCriteria;
import com.mycompany.myapp.service.dto.TemIdentificationScenarioDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.TemIdentificationScenario}.
 */
@RestController
@RequestMapping("/api/tem-identification-scenarios")
public class TemIdentificationScenarioResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        TemIdentificationScenarioResource.class
    );

    private static final String ENTITY_NAME = "temIdentificationScenario";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TemIdentificationScenarioService temIdentificationScenarioService;

    private final TemIdentificationScenarioRepository temIdentificationScenarioRepository;

    private final TemIdentificationScenarioQueryService temIdentificationScenarioQueryService;

    public TemIdentificationScenarioResource(
        TemIdentificationScenarioService temIdentificationScenarioService,
        TemIdentificationScenarioRepository temIdentificationScenarioRepository,
        TemIdentificationScenarioQueryService temIdentificationScenarioQueryService
    ) {
        this.temIdentificationScenarioService =
            temIdentificationScenarioService;
        this.temIdentificationScenarioRepository =
            temIdentificationScenarioRepository;
        this.temIdentificationScenarioQueryService =
            temIdentificationScenarioQueryService;
    }

    /**
     * {@code POST  /tem-identification-scenarios} : Create a new temIdentificationScenario.
     *
     * @param temIdentificationScenarioDTO the temIdentificationScenarioDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new temIdentificationScenarioDTO, or with status {@code 400 (Bad Request)} if the temIdentificationScenario has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<
        TemIdentificationScenarioDTO
    > createTemIdentificationScenario(
        @Valid @RequestBody TemIdentificationScenarioDTO temIdentificationScenarioDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to save TemIdentificationScenario : {}",
            temIdentificationScenarioDTO
        );
        if (temIdentificationScenarioDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new temIdentificationScenario cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        temIdentificationScenarioDTO = temIdentificationScenarioService.save(
            temIdentificationScenarioDTO
        );
        return ResponseEntity.created(
            new URI(
                "/api/tem-identification-scenarios/" +
                temIdentificationScenarioDTO.getId()
            )
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    temIdentificationScenarioDTO.getId().toString()
                )
            )
            .body(temIdentificationScenarioDTO);
    }

    /**
     * {@code PUT  /tem-identification-scenarios/:id} : Updates an existing temIdentificationScenario.
     *
     * @param id the id of the temIdentificationScenarioDTO to save.
     * @param temIdentificationScenarioDTO the temIdentificationScenarioDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated temIdentificationScenarioDTO,
     * or with status {@code 400 (Bad Request)} if the temIdentificationScenarioDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the temIdentificationScenarioDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<
        TemIdentificationScenarioDTO
    > updateTemIdentificationScenario(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TemIdentificationScenarioDTO temIdentificationScenarioDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to update TemIdentificationScenario : {}, {}",
            id,
            temIdentificationScenarioDTO
        );
        if (temIdentificationScenarioDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, temIdentificationScenarioDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!temIdentificationScenarioRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        temIdentificationScenarioDTO = temIdentificationScenarioService.update(
            temIdentificationScenarioDTO
        );
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    temIdentificationScenarioDTO.getId().toString()
                )
            )
            .body(temIdentificationScenarioDTO);
    }

    /**
     * {@code PATCH  /tem-identification-scenarios/:id} : Partial updates given fields of an existing temIdentificationScenario, field will ignore if it is null
     *
     * @param id the id of the temIdentificationScenarioDTO to save.
     * @param temIdentificationScenarioDTO the temIdentificationScenarioDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated temIdentificationScenarioDTO,
     * or with status {@code 400 (Bad Request)} if the temIdentificationScenarioDTO is not valid,
     * or with status {@code 404 (Not Found)} if the temIdentificationScenarioDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the temIdentificationScenarioDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<
        TemIdentificationScenarioDTO
    > partialUpdateTemIdentificationScenario(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TemIdentificationScenarioDTO temIdentificationScenarioDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update TemIdentificationScenario partially : {}, {}",
            id,
            temIdentificationScenarioDTO
        );
        if (temIdentificationScenarioDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, temIdentificationScenarioDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!temIdentificationScenarioRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<TemIdentificationScenarioDTO> result =
            temIdentificationScenarioService.partialUpdate(
                temIdentificationScenarioDTO
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                temIdentificationScenarioDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /tem-identification-scenarios} : get all the temIdentificationScenarios.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of temIdentificationScenarios in body.
     */
    @GetMapping("")
    public ResponseEntity<
        List<TemIdentificationScenarioDTO>
    > getAllTemIdentificationScenarios(
        TemIdentificationScenarioCriteria criteria
    ) {
        LOG.debug(
            "REST request to get TemIdentificationScenarios by criteria: {}",
            criteria
        );

        List<TemIdentificationScenarioDTO> entityList =
            temIdentificationScenarioQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /tem-identification-scenarios/count} : count all the temIdentificationScenarios.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTemIdentificationScenarios(
        TemIdentificationScenarioCriteria criteria
    ) {
        LOG.debug(
            "REST request to count TemIdentificationScenarios by criteria: {}",
            criteria
        );
        return ResponseEntity.ok().body(
            temIdentificationScenarioQueryService.countByCriteria(criteria)
        );
    }

    /**
     * {@code GET  /tem-identification-scenarios/:id} : get the "id" temIdentificationScenario.
     *
     * @param id the id of the temIdentificationScenarioDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the temIdentificationScenarioDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<
        TemIdentificationScenarioDTO
    > getTemIdentificationScenario(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TemIdentificationScenario : {}", id);
        Optional<TemIdentificationScenarioDTO> temIdentificationScenarioDTO =
            temIdentificationScenarioService.findOne(id);
        return ResponseUtil.wrapOrNotFound(temIdentificationScenarioDTO);
    }

    /**
     * {@code DELETE  /tem-identification-scenarios/:id} : delete the "id" temIdentificationScenario.
     *
     * @param id the id of the temIdentificationScenarioDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemIdentificationScenario(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to delete TemIdentificationScenario : {}", id);
        temIdentificationScenarioService.delete(id);
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
