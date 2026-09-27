package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.VendorLabelInfoRepository;
import com.mycompany.myapp.service.VendorLabelInfoService;
import com.mycompany.myapp.service.dto.VendorLabelInfoDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.VendorLabelInfo}.
 */
@RestController
@RequestMapping("/api/vendor-label-infos")
public class VendorLabelInfoResource {

  private static final Logger LOG = LoggerFactory.getLogger(
    VendorLabelInfoResource.class
  );

  private static final String ENTITY_NAME = "vendorLabelInfo";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final VendorLabelInfoService vendorLabelInfoService;

  private final VendorLabelInfoRepository vendorLabelInfoRepository;

  public VendorLabelInfoResource(
    VendorLabelInfoService vendorLabelInfoService,
    VendorLabelInfoRepository vendorLabelInfoRepository
  ) {
    this.vendorLabelInfoService = vendorLabelInfoService;
    this.vendorLabelInfoRepository = vendorLabelInfoRepository;
  }

  /**
   * {@code POST  /vendor-label-infos} : Create a new vendorLabelInfo.
   *
   * @param vendorLabelInfoDTO the vendorLabelInfoDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vendorLabelInfoDTO, or with status {@code 400 (Bad Request)} if the vendorLabelInfo has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("")
  public ResponseEntity<VendorLabelInfoDTO> createVendorLabelInfo(
    @Valid @RequestBody VendorLabelInfoDTO vendorLabelInfoDTO
  ) throws URISyntaxException {
    LOG.debug("REST request to save VendorLabelInfo : {}", vendorLabelInfoDTO);
    if (vendorLabelInfoDTO.getId() != null) {
      throw new BadRequestAlertException(
        "A new vendorLabelInfo cannot already have an ID",
        ENTITY_NAME,
        "idexists"
      );
    }
    vendorLabelInfoDTO = vendorLabelInfoService.save(vendorLabelInfoDTO);
    return ResponseEntity.created(
      new URI("/api/vendor-label-infos/" + vendorLabelInfoDTO.getId())
    )
      .headers(
        HeaderUtil.createEntityCreationAlert(
          applicationName,
          false,
          ENTITY_NAME,
          vendorLabelInfoDTO.getId().toString()
        )
      )
      .body(vendorLabelInfoDTO);
  }

  /**
   * {@code PUT  /vendor-label-infos/:id} : Updates an existing vendorLabelInfo.
   *
   * @param id the id of the vendorLabelInfoDTO to save.
   * @param vendorLabelInfoDTO the vendorLabelInfoDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vendorLabelInfoDTO,
   * or with status {@code 400 (Bad Request)} if the vendorLabelInfoDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the vendorLabelInfoDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/{id}")
  public ResponseEntity<VendorLabelInfoDTO> updateVendorLabelInfo(
    @PathVariable(value = "id", required = false) final Long id,
    @Valid @RequestBody VendorLabelInfoDTO vendorLabelInfoDTO
  ) throws URISyntaxException {
    LOG.debug(
      "REST request to update VendorLabelInfo : {}, {}",
      id,
      vendorLabelInfoDTO
    );
    if (vendorLabelInfoDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, vendorLabelInfoDTO.getId())) {
      throw new BadRequestAlertException(
        "Invalid ID",
        ENTITY_NAME,
        "idinvalid"
      );
    }

    if (!vendorLabelInfoRepository.existsById(id)) {
      throw new BadRequestAlertException(
        "Entity not found",
        ENTITY_NAME,
        "idnotfound"
      );
    }

    vendorLabelInfoDTO = vendorLabelInfoService.update(vendorLabelInfoDTO);
    return ResponseEntity.ok()
      .headers(
        HeaderUtil.createEntityUpdateAlert(
          applicationName,
          false,
          ENTITY_NAME,
          vendorLabelInfoDTO.getId().toString()
        )
      )
      .body(vendorLabelInfoDTO);
  }

  /**
   * {@code PATCH  /vendor-label-infos/:id} : Partial updates given fields of an existing vendorLabelInfo, field will ignore if it is null
   *
   * @param id the id of the vendorLabelInfoDTO to save.
   * @param vendorLabelInfoDTO the vendorLabelInfoDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vendorLabelInfoDTO,
   * or with status {@code 400 (Bad Request)} if the vendorLabelInfoDTO is not valid,
   * or with status {@code 404 (Not Found)} if the vendorLabelInfoDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the vendorLabelInfoDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(
    value = "/{id}",
    consumes = { "application/json", "application/merge-patch+json" }
  )
  public ResponseEntity<VendorLabelInfoDTO> partialUpdateVendorLabelInfo(
    @PathVariable(value = "id", required = false) final Long id,
    @NotNull @RequestBody VendorLabelInfoDTO vendorLabelInfoDTO
  ) throws URISyntaxException {
    LOG.debug(
      "REST request to partial update VendorLabelInfo partially : {}, {}",
      id,
      vendorLabelInfoDTO
    );
    if (vendorLabelInfoDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, vendorLabelInfoDTO.getId())) {
      throw new BadRequestAlertException(
        "Invalid ID",
        ENTITY_NAME,
        "idinvalid"
      );
    }

    if (!vendorLabelInfoRepository.existsById(id)) {
      throw new BadRequestAlertException(
        "Entity not found",
        ENTITY_NAME,
        "idnotfound"
      );
    }

    Optional<VendorLabelInfoDTO> result = vendorLabelInfoService.partialUpdate(
      vendorLabelInfoDTO
    );

    return ResponseUtil.wrapOrNotFound(
      result,
      HeaderUtil.createEntityUpdateAlert(
        applicationName,
        false,
        ENTITY_NAME,
        vendorLabelInfoDTO.getId().toString()
      )
    );
  }

  /**
   * {@code GET  /vendor-label-infos} : get all the vendorLabelInfos.
   *
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vendorLabelInfos in body.
   */
  @GetMapping("")
  public List<VendorLabelInfoDTO> getAllVendorLabelInfos() {
    LOG.debug("REST request to get all VendorLabelInfos");
    return vendorLabelInfoService.findAll();
  }

  /**
   * {@code GET  /vendor-label-infos/:id} : get the "id" vendorLabelInfo.
   *
   * @param id the id of the vendorLabelInfoDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vendorLabelInfoDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/{id}")
  public ResponseEntity<VendorLabelInfoDTO> getVendorLabelInfo(
    @PathVariable("id") Long id
  ) {
    LOG.debug("REST request to get VendorLabelInfo : {}", id);
    Optional<VendorLabelInfoDTO> vendorLabelInfoDTO =
      vendorLabelInfoService.findOne(id);
    return ResponseUtil.wrapOrNotFound(vendorLabelInfoDTO);
  }

  /**
   * {@code DELETE  /vendor-label-infos/:id} : delete the "id" vendorLabelInfo.
   *
   * @param id the id of the vendorLabelInfoDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteVendorLabelInfo(
    @PathVariable("id") Long id
  ) {
    LOG.debug("REST request to delete VendorLabelInfo : {}", id);
    vendorLabelInfoService.delete(id);
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
