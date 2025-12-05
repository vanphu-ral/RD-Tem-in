package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.SerialBoxPalletMappingRepository;
import com.mycompany.myapp.service.SerialBoxPalletMappingService;
import com.mycompany.myapp.service.dto.SerialBoxPalletMappingDTO;
import com.mycompany.myapp.service.dto.SerialBoxPalletMappingInsertDTO;
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
 * REST controller for managing
 * {@link com.mycompany.myapp.domain.SerialBoxPalletMapping}.
 */
@RestController
@RequestMapping("/api/serial-box-pallet-mappings")
public class SerialBoxPalletMappingResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        SerialBoxPalletMappingResource.class
    );

    private static final String ENTITY_NAME = "serialBoxPalletMapping";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SerialBoxPalletMappingService serialBoxPalletMappingService;

    private final SerialBoxPalletMappingRepository serialBoxPalletMappingRepository;

    public SerialBoxPalletMappingResource(
        SerialBoxPalletMappingService serialBoxPalletMappingService,
        SerialBoxPalletMappingRepository serialBoxPalletMappingRepository
    ) {
        this.serialBoxPalletMappingService = serialBoxPalletMappingService;
        this.serialBoxPalletMappingRepository =
            serialBoxPalletMappingRepository;
    }

    /**
     * {@code POST  /serial-box-pallet-mappings} : Create a new
     * serialBoxPalletMapping.
     *
     * @param serialBoxPalletMappingDTO the serialBoxPalletMappingDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new serialBoxPalletMappingDTO, or with status
     *         {@code 400 (Bad Request)} if the serialBoxPalletMapping has already
     *         an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<
        SerialBoxPalletMappingDTO
    > createSerialBoxPalletMapping(
        @Valid @RequestBody SerialBoxPalletMappingDTO serialBoxPalletMappingDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to save SerialBoxPalletMapping : {}",
            serialBoxPalletMappingDTO
        );
        if (serialBoxPalletMappingDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new serialBoxPalletMapping cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        serialBoxPalletMappingDTO = serialBoxPalletMappingService.save(
            serialBoxPalletMappingDTO
        );
        return ResponseEntity.created(
            new URI(
                "/api/serial-box-pallet-mappings/" +
                serialBoxPalletMappingDTO.getId()
            )
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    serialBoxPalletMappingDTO.getId().toString()
                )
            )
            .body(serialBoxPalletMappingDTO);
    }

    /**
     * {@code POST /serial-box-pallet-mappings/ma-lenh-san-xuat/{maLenhSanXuatId}} :
     * Create a new
     * serialBoxPalletMapping with maLenhSanXuat.
     *
     * @param maLenhSanXuatId the id of maLenhSanXuat.
     * @param insertDTO       the insert data.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new serialBoxPalletMappingDTO.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ma-lenh-san-xuat/{maLenhSanXuatId}")
    public ResponseEntity<
        SerialBoxPalletMappingDTO
    > createSerialBoxPalletMappingWithMaLenhSanXuat(
        @PathVariable Long maLenhSanXuatId,
        @Valid @RequestBody SerialBoxPalletMappingInsertDTO insertDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to save SerialBoxPalletMapping with maLenhSanXuatId : {} and data : {}",
            maLenhSanXuatId,
            insertDTO
        );

        SerialBoxPalletMappingDTO result =
            serialBoxPalletMappingService.insertWithMaLenhSanXuat(
                maLenhSanXuatId,
                insertDTO
            );
        return ResponseEntity.created(
            new URI("/api/serial-box-pallet-mappings/" + result.getId())
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
     * {@code PUT  /serial-box-pallet-mappings/:id} : Updates an existing
     * serialBoxPalletMapping.
     *
     * @param id                        the id of the serialBoxPalletMappingDTO to
     *                                  save.
     * @param serialBoxPalletMappingDTO the serialBoxPalletMappingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated serialBoxPalletMappingDTO,
     *         or with status {@code 400 (Bad Request)} if the
     *         serialBoxPalletMappingDTO is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         serialBoxPalletMappingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<
        SerialBoxPalletMappingDTO
    > updateSerialBoxPalletMapping(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SerialBoxPalletMappingDTO serialBoxPalletMappingDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to update SerialBoxPalletMapping : {}, {}",
            id,
            serialBoxPalletMappingDTO
        );
        if (serialBoxPalletMappingDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, serialBoxPalletMappingDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!serialBoxPalletMappingRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        serialBoxPalletMappingDTO = serialBoxPalletMappingService.update(
            serialBoxPalletMappingDTO
        );
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    serialBoxPalletMappingDTO.getId().toString()
                )
            )
            .body(serialBoxPalletMappingDTO);
    }

    /**
     * {@code PATCH  /serial-box-pallet-mappings/:id} : Partial updates given fields
     * of an existing serialBoxPalletMapping, field will ignore if it is null
     *
     * @param id                        the id of the serialBoxPalletMappingDTO to
     *                                  save.
     * @param serialBoxPalletMappingDTO the serialBoxPalletMappingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated serialBoxPalletMappingDTO,
     *         or with status {@code 400 (Bad Request)} if the
     *         serialBoxPalletMappingDTO is not valid,
     *         or with status {@code 404 (Not Found)} if the
     *         serialBoxPalletMappingDTO is not found,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         serialBoxPalletMappingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<
        SerialBoxPalletMappingDTO
    > partialUpdateSerialBoxPalletMapping(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SerialBoxPalletMappingDTO serialBoxPalletMappingDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update SerialBoxPalletMapping partially : {}, {}",
            id,
            serialBoxPalletMappingDTO
        );
        if (serialBoxPalletMappingDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, serialBoxPalletMappingDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!serialBoxPalletMappingRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<SerialBoxPalletMappingDTO> result =
            serialBoxPalletMappingService.partialUpdate(
                serialBoxPalletMappingDTO
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                serialBoxPalletMappingDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /serial-box-pallet-mappings} : get all the
     * serialBoxPalletMappings.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of serialBoxPalletMappings in body.
     */
    @GetMapping("")
    public ResponseEntity<
        List<SerialBoxPalletMappingDTO>
    > getAllSerialBoxPalletMappings(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of SerialBoxPalletMappings");
        Page<SerialBoxPalletMappingDTO> page =
            serialBoxPalletMappingService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /serial-box-pallet-mappings/:id} : get the "id"
     * serialBoxPalletMapping.
     *
     * @param id the id of the serialBoxPalletMappingDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the serialBoxPalletMappingDTO, or with status
     *         {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SerialBoxPalletMappingDTO> getSerialBoxPalletMapping(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to get SerialBoxPalletMapping : {}", id);
        Optional<SerialBoxPalletMappingDTO> serialBoxPalletMappingDTO =
            serialBoxPalletMappingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(serialBoxPalletMappingDTO);
    }

    /**
     * {@code GET  /serial-box-pallet-mappings/serial-pallet/:serialPallet} : get
     * all serialBoxPalletMappings by serialPallet.
     *
     * @param serialPallet the serial pallet to retrieve mappings for.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the list of serialBoxPalletMappingDTOs.
     */
    @GetMapping("/serial-pallet/{serialPallet}")
    public ResponseEntity<
        List<SerialBoxPalletMappingDTO>
    > getSerialBoxPalletMappingsBySerialPallet(
        @PathVariable String serialPallet
    ) {
        LOG.debug(
            "REST request to get SerialBoxPalletMappings by serialPallet : {}",
            serialPallet
        );
        List<SerialBoxPalletMappingDTO> serialBoxPalletMappingDTOs =
            serialBoxPalletMappingService.findBySerialPallet(serialPallet);
        return ResponseEntity.ok().body(serialBoxPalletMappingDTOs);
    }

    /**
     * {@code GET  /serial-box-pallet-mappings/ma-lenh-san-xuat/:maLenhSanXuatId} :
     * get all serialBoxPalletMappings by maLenhSanXuatId.
     *
     * @param maLenhSanXuatId the id of maLenhSanXuat to retrieve mappings for.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the list of serialBoxPalletMappingDTOs.
     */
    @GetMapping("/ma-lenh-san-xuat/{maLenhSanXuatId}")
    public ResponseEntity<
        List<SerialBoxPalletMappingDTO>
    > getSerialBoxPalletMappingsByMaLenhSanXuatId(
        @PathVariable Long maLenhSanXuatId
    ) {
        LOG.debug(
            "REST request to get SerialBoxPalletMappings by maLenhSanXuatId : {}",
            maLenhSanXuatId
        );
        List<SerialBoxPalletMappingDTO> serialBoxPalletMappingDTOs =
            serialBoxPalletMappingService.findByMaLenhSanXuatId(
                maLenhSanXuatId
            );
        return ResponseEntity.ok().body(serialBoxPalletMappingDTOs);
    }

    /**
     * {@code DELETE  /serial-box-pallet-mappings/:id} : delete the "id"
     * serialBoxPalletMapping.
     *
     * @param id the id of the serialBoxPalletMappingDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSerialBoxPalletMapping(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to delete SerialBoxPalletMapping : {}", id);
        serialBoxPalletMappingService.delete(id);
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
