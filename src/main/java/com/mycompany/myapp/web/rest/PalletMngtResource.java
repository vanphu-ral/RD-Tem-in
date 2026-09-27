package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.PalletMngtRepository;
import com.mycompany.myapp.service.PalletMngtService;
import com.mycompany.myapp.service.dto.PalletMngtDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.PalletMngt}.
 */
@RestController
@RequestMapping("/api/pallet-mngts")
public class PalletMngtResource {

  private static final Logger LOG = LoggerFactory.getLogger(
    PalletMngtResource.class
  );

  private static final String ENTITY_NAME = "palletMngt";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final PalletMngtService palletMngtService;

  private final PalletMngtRepository palletMngtRepository;

  public PalletMngtResource(
    PalletMngtService palletMngtService,
    PalletMngtRepository palletMngtRepository
  ) {
    this.palletMngtService = palletMngtService;
    this.palletMngtRepository = palletMngtRepository;
  }

  /**
   * {@code POST  /pallet-mngts} : Create a new palletMngt.
   *
   * @param palletMngtDTO the palletMngtDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new palletMngtDTO, or with status {@code 400 (Bad Request)} if the palletMngt has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("")
  public ResponseEntity<PalletMngtDTO> createPalletMngt(
    @Valid @RequestBody PalletMngtDTO palletMngtDTO
  ) throws URISyntaxException {
    LOG.debug("REST request to save PalletMngt : {}", palletMngtDTO);
    if (palletMngtDTO.getId() != null) {
      throw new BadRequestAlertException(
        "A new palletMngt cannot already have an ID",
        ENTITY_NAME,
        "idexists"
      );
    }
    palletMngtDTO = palletMngtService.save(palletMngtDTO);
    return ResponseEntity.created(
      new URI("/api/pallet-mngts/" + palletMngtDTO.getId())
    )
      .headers(
        HeaderUtil.createEntityCreationAlert(
          applicationName,
          false,
          ENTITY_NAME,
          palletMngtDTO.getId().toString()
        )
      )
      .body(palletMngtDTO);
  }

  /**
   * {@code PUT  /pallet-mngts/:id} : Updates an existing palletMngt.
   *
   * @param id the id of the palletMngtDTO to save.
   * @param palletMngtDTO the palletMngtDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated palletMngtDTO,
   * or with status {@code 400 (Bad Request)} if the palletMngtDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the palletMngtDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/{id}")
  public ResponseEntity<PalletMngtDTO> updatePalletMngt(
    @PathVariable(value = "id", required = false) final Long id,
    @Valid @RequestBody PalletMngtDTO palletMngtDTO
  ) throws URISyntaxException {
    LOG.debug("REST request to update PalletMngt : {}, {}", id, palletMngtDTO);
    if (palletMngtDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, palletMngtDTO.getId())) {
      throw new BadRequestAlertException(
        "Invalid ID",
        ENTITY_NAME,
        "idinvalid"
      );
    }

    if (!palletMngtRepository.existsById(id)) {
      throw new BadRequestAlertException(
        "Entity not found",
        ENTITY_NAME,
        "idnotfound"
      );
    }

    palletMngtDTO = palletMngtService.update(palletMngtDTO);
    return ResponseEntity.ok()
      .headers(
        HeaderUtil.createEntityUpdateAlert(
          applicationName,
          false,
          ENTITY_NAME,
          palletMngtDTO.getId().toString()
        )
      )
      .body(palletMngtDTO);
  }

  /**
   * {@code PATCH  /pallet-mngts/:id} : Partial updates given fields of an existing palletMngt, field will ignore if it is null
   *
   * @param id the id of the palletMngtDTO to save.
   * @param palletMngtDTO the palletMngtDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated palletMngtDTO,
   * or with status {@code 400 (Bad Request)} if the palletMngtDTO is not valid,
   * or with status {@code 404 (Not Found)} if the palletMngtDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the palletMngtDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(
    value = "/{id}",
    consumes = { "application/json", "application/merge-patch+json" }
  )
  public ResponseEntity<PalletMngtDTO> partialUpdatePalletMngt(
    @PathVariable(value = "id", required = false) final Long id,
    @NotNull @RequestBody PalletMngtDTO palletMngtDTO
  ) throws URISyntaxException {
    LOG.debug(
      "REST request to partial update PalletMngt partially : {}, {}",
      id,
      palletMngtDTO
    );
    if (palletMngtDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, palletMngtDTO.getId())) {
      throw new BadRequestAlertException(
        "Invalid ID",
        ENTITY_NAME,
        "idinvalid"
      );
    }

    if (!palletMngtRepository.existsById(id)) {
      throw new BadRequestAlertException(
        "Entity not found",
        ENTITY_NAME,
        "idnotfound"
      );
    }

    Optional<PalletMngtDTO> result = palletMngtService.partialUpdate(
      palletMngtDTO
    );

    return ResponseUtil.wrapOrNotFound(
      result,
      HeaderUtil.createEntityUpdateAlert(
        applicationName,
        false,
        ENTITY_NAME,
        palletMngtDTO.getId().toString()
      )
    );
  }

  /**
   * {@code GET  /pallet-mngts} : get all the palletMngts.
   *
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of palletMngts in body.
   */
  @GetMapping("")
  public List<PalletMngtDTO> getAllPalletMngts() {
    LOG.debug("REST request to get all PalletMngts");
    return palletMngtService.findAll();
  }

  /**
   * {@code GET  /pallet-mngts/:id} : get the "id" palletMngt.
   *
   * @param id the id of the palletMngtDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the palletMngtDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/{id}")
  public ResponseEntity<PalletMngtDTO> getPalletMngt(
    @PathVariable("id") Long id
  ) {
    LOG.debug("REST request to get PalletMngt : {}", id);
    Optional<PalletMngtDTO> palletMngtDTO = palletMngtService.findOne(id);
    return ResponseUtil.wrapOrNotFound(palletMngtDTO);
  }

  /**
   * {@code DELETE  /pallet-mngts/:id} : delete the "id" palletMngt.
   *
   * @param id the id of the palletMngtDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePalletMngt(@PathVariable("id") Long id) {
    LOG.debug("REST request to delete PalletMngt : {}", id);
    palletMngtService.delete(id);
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
