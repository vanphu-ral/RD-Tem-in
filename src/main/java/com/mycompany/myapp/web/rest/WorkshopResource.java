package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.Partner3WorkshopService;
import com.mycompany.myapp.service.dto.WorkshopDTO;
import com.mycompany.myapp.service.dto.WorkshopHierarchyDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Workshop}.
 */
@RestController
@RequestMapping("/api/workshops")
public class WorkshopResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        WorkshopResource.class
    );

    private static final String ENTITY_NAME = "workshop";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final Partner3WorkshopService partner3WorkshopService;

    public WorkshopResource(Partner3WorkshopService partner3WorkshopService) {
        this.partner3WorkshopService = partner3WorkshopService;
    }

    /**
     * {@code GET  /workshops} : get all the workshops.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of workshops in body.
     */
    @GetMapping("")
    public List<WorkshopDTO> getAllWorkshops() {
        LOG.debug("REST request to get all Workshops");
        return partner3WorkshopService.findAll();
    }

    /**
     * {@code GET  /workshops/:id} : get the "id" workshop.
     *
     * @param id the id of the workshopDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the workshopDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkshopDTO> getWorkshop(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to get Workshop : {}", id);
        Optional<WorkshopDTO> workshopDTO = partner3WorkshopService.findOne(id);
        return ResponseUtil.wrapOrNotFound(workshopDTO);
    }

    /**
     * {@code GET  /workshops/hierarchy} : get all workshops with hierarchy.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of workshop hierarchy in body.
     */
    @GetMapping("/hierarchy")
    public List<WorkshopHierarchyDTO> getAllWorkshopsWithHierarchy() {
        LOG.debug("REST request to get all Workshops with hierarchy");
        return partner3WorkshopService.findAllWithHierarchy();
    }
}
