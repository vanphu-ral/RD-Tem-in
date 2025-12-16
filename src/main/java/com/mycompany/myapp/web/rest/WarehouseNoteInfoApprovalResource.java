package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.WarehouseNoteInfoApprovalService;
import com.mycompany.myapp.service.dto.WarehouseNoteInfoApprovalDTO;
import com.mycompany.myapp.service.dto.WarehouseNoteInfoApprovalRequestDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing
 * {@link com.mycompany.myapp.domain.WarehouseNoteInfoApproval} in partner3
 * PostgreSQL
 * database.
 */
@RestController
@RequestMapping("/api")
@Transactional("partner3TransactionManager")
public class WarehouseNoteInfoApprovalResource {

    private final Logger log = LoggerFactory.getLogger(
        WarehouseNoteInfoApprovalResource.class
    );

    private static final String ENTITY_NAME = "warehouseNoteInfoApproval";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WarehouseNoteInfoApprovalService warehouseNoteInfoApprovalService;

    public WarehouseNoteInfoApprovalResource(
        WarehouseNoteInfoApprovalService warehouseNoteInfoApprovalService
    ) {
        this.warehouseNoteInfoApprovalService =
            warehouseNoteInfoApprovalService;
    }

    /**
     * {@code POST  /warehouse-note-infos-approval} : Create a new
     * warehouseNoteInfoApproval with nested reelIds in partner3 database.
     *
     * @param requestDTO the warehouseNoteInfoApprovalRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new warehouseNoteInfoApprovalDTO, or with status
     *         {@code 400 (Bad Request)} if the request is invalid.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/warehouse-note-infos-approval")
    public ResponseEntity<
        WarehouseNoteInfoApprovalDTO
    > createWarehouseNoteInfoApproval(
        @Valid @RequestBody WarehouseNoteInfoApprovalRequestDTO requestDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to save WarehouseNoteInfoApproval : {}",
            requestDTO
        );

        if (requestDTO == null) {
            throw new BadRequestAlertException(
                "warehouseNoteInfoApproval is required",
                ENTITY_NAME,
                "missing"
            );
        }

        // Validate that at least the main fields are provided
        if (
            requestDTO.getMaLenhSanXuat() == null ||
            requestDTO.getMaLenhSanXuat().isEmpty()
        ) {
            throw new BadRequestAlertException(
                "maLenhSanXuat is required",
                ENTITY_NAME,
                "maLenhSanXuatRequired"
            );
        }

        WarehouseNoteInfoApprovalDTO result =
            warehouseNoteInfoApprovalService.create(requestDTO);

        if (result == null || result.getId() == null) {
            throw new BadRequestAlertException(
                "Failed to create warehouseNoteInfoApproval",
                ENTITY_NAME,
                "createfailed"
            );
        }

        return ResponseEntity.created(
            new URI("/api/warehouse-note-infos-approval/" + result.getId())
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
     * {@code GET  /warehouse-note-infos-approval/:id} : get the "id"
     * warehouseNoteInfoApproval.
     *
     * @param id the id of the warehouseNoteInfoApprovalDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the warehouseNoteInfoApprovalDTO, or with status
     *         {@code 404 (Not Found)}.
     */
    @GetMapping("/warehouse-note-infos-approval/{id}")
    public ResponseEntity<
        WarehouseNoteInfoApprovalDTO
    > getWarehouseNoteInfoApproval(@PathVariable("id") Long id) {
        log.debug("REST request to get WarehouseNoteInfoApproval : {}", id);
        var warehouseNoteInfoApprovalDTO =
            warehouseNoteInfoApprovalService.findOne(id);
        return warehouseNoteInfoApprovalDTO
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
