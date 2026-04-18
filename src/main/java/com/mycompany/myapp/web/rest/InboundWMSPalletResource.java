package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.partner3.InboundWMSPalletRepository;
import com.mycompany.myapp.service.InboundWMSPalletService;
import com.mycompany.myapp.service.dto.InboundWMSPalletDTO;
import com.mycompany.myapp.service.dto.InboundWMSPalletScanRequestDTO;
import com.mycompany.myapp.service.dto.InboundWMSPalletScanResponseDTO;
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
 * REST controller for managing
 * {@link com.mycompany.myapp.domain.InboundWMSPallet}.
 */
@RestController
@RequestMapping("/api/inbound-wms-pallets")
public class InboundWMSPalletResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        InboundWMSPalletResource.class
    );

    private static final String ENTITY_NAME = "inboundWMSPallet";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InboundWMSPalletService inboundWMSPalletService;

    private final InboundWMSPalletRepository inboundWMSPalletRepository;

    public InboundWMSPalletResource(
        InboundWMSPalletService inboundWMSPalletService,
        InboundWMSPalletRepository inboundWMSPalletRepository
    ) {
        this.inboundWMSPalletService = inboundWMSPalletService;
        this.inboundWMSPalletRepository = inboundWMSPalletRepository;
    }

    /**
     * {@code POST  /inbound-wms-pallets} : Create a new inboundWMSPallet.
     *
     * @param inboundWMSPalletDTO the inboundWMSPalletDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new inboundWMSPalletDTO, or with status
     *         {@code 400 (Bad Request)} if the inboundWMSPallet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InboundWMSPalletDTO> createInboundWMSPallet(
        @Valid @RequestBody InboundWMSPalletDTO inboundWMSPalletDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to save InboundWMSPallet : {}",
            inboundWMSPalletDTO
        );
        if (inboundWMSPalletDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new inboundWMSPallet cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        inboundWMSPalletDTO = inboundWMSPalletService.save(inboundWMSPalletDTO);
        return ResponseEntity.created(
            new URI("/api/inbound-wms-pallets/" + inboundWMSPalletDTO.getId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    inboundWMSPalletDTO.getId().toString()
                )
            )
            .body(inboundWMSPalletDTO);
    }

    /**
     * {@code PUT  /inbound-wms-pallets/:id} : Updates an existing inboundWMSPallet.
     *
     * @param id                  the id of the inboundWMSPalletDTO to save.
     * @param inboundWMSPalletDTO the inboundWMSPalletDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated inboundWMSPalletDTO,
     *         or with status {@code 400 (Bad Request)} if the inboundWMSPalletDTO
     *         is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         inboundWMSPalletDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InboundWMSPalletDTO> updateInboundWMSPallet(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InboundWMSPalletDTO inboundWMSPalletDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to update InboundWMSPallet : {}, {}",
            id,
            inboundWMSPalletDTO
        );
        if (inboundWMSPalletDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, inboundWMSPalletDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!inboundWMSPalletRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        inboundWMSPalletDTO = inboundWMSPalletService.update(
            inboundWMSPalletDTO
        );
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    inboundWMSPalletDTO.getId().toString()
                )
            )
            .body(inboundWMSPalletDTO);
    }

    /**
     * {@code PATCH  /inbound-wms-pallets/:id} : Partial updates given fields of an
     * existing inboundWMSPallet, field will ignore if it is null
     *
     * @param id                  the id of the inboundWMSPalletDTO to save.
     * @param inboundWMSPalletDTO the inboundWMSPalletDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated inboundWMSPalletDTO,
     *         or with status {@code 400 (Bad Request)} if the inboundWMSPalletDTO
     *         is not valid,
     *         or with status {@code 404 (Not Found)} if the inboundWMSPalletDTO is
     *         not found,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         inboundWMSPalletDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<InboundWMSPalletDTO> partialUpdateInboundWMSPallet(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InboundWMSPalletDTO inboundWMSPalletDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update InboundWMSPallet partially : {}, {}",
            id,
            inboundWMSPalletDTO
        );
        if (inboundWMSPalletDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, inboundWMSPalletDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!inboundWMSPalletRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<InboundWMSPalletDTO> result =
            inboundWMSPalletService.partialUpdate(inboundWMSPalletDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                inboundWMSPalletDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /inbound-wms-pallets} : get all the inboundWMSPallets.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of inboundWMSPallets in body.
     */
    @GetMapping("")
    public List<InboundWMSPalletDTO> getAllInboundWMSPallets() {
        LOG.debug("REST request to get all InboundWMSPallets");
        return inboundWMSPalletService.findAll();
    }

    /**
     * {@code POST  /inbound-wms-pallets/scan} : Scan and save pallet information.
     *
     * @param requestDTO the request data.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the response.
     */
    @PostMapping("/scan")
    public ResponseEntity<InboundWMSPalletScanResponseDTO> scanInboundWMSPallet(
        @Valid @RequestBody InboundWMSPalletScanRequestDTO requestDTO
    ) {
        LOG.debug("REST request to scan InboundWMSPallet : {}", requestDTO);
        InboundWMSPalletScanResponseDTO result =
            inboundWMSPalletService.scanAndSave(requestDTO);
        if (result.getWarehouseNoteInfo() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * {@code DELETE  /inbound-wms-pallets/:id} : delete the "id" inboundWMSPallet.
     *
     * @param id the id of the inboundWMSPalletDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInboundWMSPallet(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to delete InboundWMSPallet : {}", id);
        inboundWMSPalletService.delete(id);
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
