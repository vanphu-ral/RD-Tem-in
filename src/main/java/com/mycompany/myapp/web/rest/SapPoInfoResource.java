package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.SapPoInfoAggregateService;
import com.mycompany.myapp.service.dto.PoInfoResponseDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing SAP PO Info.
 * Provides GET endpoint to retrieve PO information by OPOR_DocEntry.
 */
@RestController
@RequestMapping("/api/sap-po-info")
public class SapPoInfoResource {

    private final Logger log = LoggerFactory.getLogger(SapPoInfoResource.class);

    private static final String ENTITY_NAME = "sapPoInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SapPoInfoAggregateService sapPoInfoAggregateService;

    public SapPoInfoResource(
        SapPoInfoAggregateService sapPoInfoAggregateService
    ) {
        this.sapPoInfoAggregateService = sapPoInfoAggregateService;
    }

    /**
     * {@code GET /sap-po-info/:oporDocEntry} : Get PO information by OPOR_DocEntry.
     *
     * @param oporDocEntry the OPOR_DocEntry to search for
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the PO info response containing poInfo (header) and poDetails (lines),
     *         or with status {@code 404 (Not Found)} if no records found.
     */
    @GetMapping("/{oporDocEntry}")
    public ResponseEntity<PoInfoResponseDTO> getPoInfoByOporDocEntry(
        @PathVariable String oporDocEntry
    ) {
        log.debug(
            "REST request to get SapPoInfo by OPOR_DocEntry : {}",
            oporDocEntry
        );

        if (oporDocEntry == null || oporDocEntry.trim().isEmpty()) {
            throw new BadRequestAlertException(
                "OPOR_DocEntry cannot be empty",
                ENTITY_NAME,
                "oporDocEntryempty"
            );
        }

        PoInfoResponseDTO result =
            sapPoInfoAggregateService.getPoInfoByOporDocEntry(oporDocEntry);

        if (result.getPoInfo() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }
}
