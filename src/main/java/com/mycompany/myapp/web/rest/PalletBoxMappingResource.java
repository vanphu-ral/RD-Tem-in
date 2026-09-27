package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.PalletBoxMappingRepository;
import com.mycompany.myapp.service.PalletBoxMappingService;
import com.mycompany.myapp.service.dto.PalletBoxMappingDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.PalletBoxMapping}.
 */
@RestController
@RequestMapping("/api/pallet-box-mappings")
public class PalletBoxMappingResource {

  private static final Logger LOG = LoggerFactory.getLogger(
    PalletBoxMappingResource.class
  );

  private static final String ENTITY_NAME = "palletBoxMapping";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final PalletBoxMappingService palletBoxMappingService;

  private final PalletBoxMappingRepository palletBoxMappingRepository;

  public PalletBoxMappingResource(
    PalletBoxMappingService palletBoxMappingService,
    PalletBoxMappingRepository palletBoxMappingRepository
  ) {
    this.palletBoxMappingService = palletBoxMappingService;
    this.palletBoxMappingRepository = palletBoxMappingRepository;
  }

  /**
   * {@code POST  /pallet-box-mappings} : Create a new palletBoxMapping.
   *
   * @param palletBoxMappingDTO the palletBoxMappingDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new palletBoxMappingDTO, or with status {@code 400 (Bad Request)} if the palletBoxMapping has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("")
  public ResponseEntity<PalletBoxMappingDTO> createPalletBoxMapping(
    @Valid @RequestBody PalletBoxMappingDTO palletBoxMappingDTO
  ) throws URISyntaxException {
    LOG.debug(
      "REST request to save PalletBoxMapping : {}",
      palletBoxMappingDTO
    );
    if (palletBoxMappingDTO.getId() != null) {
      throw new BadRequestAlertException(
        "A new palletBoxMapping cannot already have an ID",
        ENTITY_NAME,
        "idexists"
      );
    }
    palletBoxMappingDTO = palletBoxMappingService.save(palletBoxMappingDTO);
    return ResponseEntity.created(
      new URI("/api/pallet-box-mappings/" + palletBoxMappingDTO.getId())
    )
      .headers(
        HeaderUtil.createEntityCreationAlert(
          applicationName,
          false,
          ENTITY_NAME,
          palletBoxMappingDTO.getId().toString()
        )
      )
      .body(palletBoxMappingDTO);
  }

  /**
   * {@code PUT  /pallet-box-mappings/:id} : Updates an existing palletBoxMapping.
   *
   * @param id the id of the palletBoxMappingDTO to save.
   * @param palletBoxMappingDTO the palletBoxMappingDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated palletBoxMappingDTO,
   * or with status {@code 400 (Bad Request)} if the palletBoxMappingDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the palletBoxMappingDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/{id}")
  public ResponseEntity<PalletBoxMappingDTO> updatePalletBoxMapping(
    @PathVariable(value = "id", required = false) final Long id,
    @Valid @RequestBody PalletBoxMappingDTO palletBoxMappingDTO
  ) throws URISyntaxException {
    LOG.debug(
      "REST request to update PalletBoxMapping : {}, {}",
      id,
      palletBoxMappingDTO
    );
    if (palletBoxMappingDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, palletBoxMappingDTO.getId())) {
      throw new BadRequestAlertException(
        "Invalid ID",
        ENTITY_NAME,
        "idinvalid"
      );
    }

    if (!palletBoxMappingRepository.existsById(id)) {
      throw new BadRequestAlertException(
        "Entity not found",
        ENTITY_NAME,
        "idnotfound"
      );
    }

    palletBoxMappingDTO = palletBoxMappingService.update(palletBoxMappingDTO);
    return ResponseEntity.ok()
      .headers(
        HeaderUtil.createEntityUpdateAlert(
          applicationName,
          false,
          ENTITY_NAME,
          palletBoxMappingDTO.getId().toString()
        )
      )
      .body(palletBoxMappingDTO);
  }

  /**
   * {@code PATCH  /pallet-box-mappings/:id} : Partial updates given fields of an existing palletBoxMapping, field will ignore if it is null
   *
   * @param id the id of the palletBoxMappingDTO to save.
   * @param palletBoxMappingDTO the palletBoxMappingDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated palletBoxMappingDTO,
   * or with status {@code 400 (Bad Request)} if the palletBoxMappingDTO is not valid,
   * or with status {@code 404 (Not Found)} if the palletBoxMappingDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the palletBoxMappingDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(
    value = "/{id}",
    consumes = { "application/json", "application/merge-patch+json" }
  )
  public ResponseEntity<PalletBoxMappingDTO> partialUpdatePalletBoxMapping(
    @PathVariable(value = "id", required = false) final Long id,
    @NotNull @RequestBody PalletBoxMappingDTO palletBoxMappingDTO
  ) throws URISyntaxException {
    LOG.debug(
      "REST request to partial update PalletBoxMapping partially : {}, {}",
      id,
      palletBoxMappingDTO
    );
    if (palletBoxMappingDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, palletBoxMappingDTO.getId())) {
      throw new BadRequestAlertException(
        "Invalid ID",
        ENTITY_NAME,
        "idinvalid"
      );
    }

    if (!palletBoxMappingRepository.existsById(id)) {
      throw new BadRequestAlertException(
        "Entity not found",
        ENTITY_NAME,
        "idnotfound"
      );
    }

    Optional<PalletBoxMappingDTO> result =
      palletBoxMappingService.partialUpdate(palletBoxMappingDTO);

    return ResponseUtil.wrapOrNotFound(
      result,
      HeaderUtil.createEntityUpdateAlert(
        applicationName,
        false,
        ENTITY_NAME,
        palletBoxMappingDTO.getId().toString()
      )
    );
  }

  /**
   * {@code GET  /pallet-box-mappings} : get all the palletBoxMappings.
   *
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of palletBoxMappings in body.
   */
  @GetMapping("")
  public List<PalletBoxMappingDTO> getAllPalletBoxMappings() {
    LOG.debug("REST request to get all PalletBoxMappings");
    return palletBoxMappingService.findAll();
  }

  /**
   * {@code GET  /pallet-box-mappings/:id} : get the "id" palletBoxMapping.
   *
   * @param id the id of the palletBoxMappingDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the palletBoxMappingDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/{id}")
  public ResponseEntity<PalletBoxMappingDTO> getPalletBoxMapping(
    @PathVariable("id") Long id
  ) {
    LOG.debug("REST request to get PalletBoxMapping : {}", id);
    Optional<PalletBoxMappingDTO> palletBoxMappingDTO =
      palletBoxMappingService.findOne(id);
    return ResponseUtil.wrapOrNotFound(palletBoxMappingDTO);
  }

  /**
   * {@code DELETE  /pallet-box-mappings/:id} : delete the "id" palletBoxMapping.
   *
   * @param id the id of the palletBoxMappingDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePalletBoxMapping(
    @PathVariable("id") Long id
  ) {
    LOG.debug("REST request to delete PalletBoxMapping : {}", id);
    palletBoxMappingService.delete(id);
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
