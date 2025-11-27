package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.Partner3branchService;
import com.mycompany.myapp.service.dto.branchDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.branch}.
 */
@RestController
@RequestMapping("/api/branchs")
public class branchResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        branchResource.class
    );

    private static final String ENTITY_NAME = "branch";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final Partner3branchService partner3branchService;

    public branchResource(Partner3branchService partner3branchService) {
        this.partner3branchService = partner3branchService;
    }

    /**
     * {@code GET  /branchs} : get all the branchs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of branchs in body.
     */
    @GetMapping("")
    public List<branchDTO> getAllbranchs() {
        LOG.debug("REST request to get all branch");
        return partner3branchService.findAll();
    }

    /**
     * {@code GET  /branchs/:id} : get the "id" branch.
     *
     * @param id the id of the branchDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the branchDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<branchDTO> getbranch(@PathVariable("id") Long id) {
        LOG.debug("REST request to get branch : {}", id);
        Optional<branchDTO> branchDTO = partner3branchService.findOne(id);
        return ResponseUtil.wrapOrNotFound(branchDTO);
    }
}
