package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.ScanService;
import com.mycompany.myapp.service.dto.ScanResponseDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for scanning pallet and warehouse information.
 */
@RestController
@RequestMapping("/api")
public class ScanResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        ScanResource.class
    );

    private static final String ENTITY_NAME = "scan";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ScanService scanService;

    public ScanResource(ScanService scanService) {
        this.scanService = scanService;
    }

    /**
     * {@code GET  /scan} : Scan by serialPallet or reelId.
     *
     * @param serialPallet the serial pallet to scan (optional)
     * @param reelId       the reel ID to scan (optional)
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the scan response,
     *         or with status {@code 404 (Not Found)} if no data found,
     *         or with status {@code 400 (Bad Request)} if both or neither
     *         parameters are provided.
     */
    @GetMapping("/scan")
    public ResponseEntity<ScanResponseDTO> scan(
        @RequestParam(
            value = "serialPallet",
            required = false
        ) String serialPallet,
        @RequestParam(value = "reelId", required = false) String reelId
    ) {
        LOG.debug(
            "REST request to scan with serialPallet: {}, reelId: {}",
            serialPallet,
            reelId
        );

        // Validate that exactly one parameter is provided
        if (
            (serialPallet == null && reelId == null) ||
            (serialPallet != null && reelId != null)
        ) {
            throw new BadRequestAlertException(
                "Either serialPallet or reelId must be provided, but not both",
                ENTITY_NAME,
                "invalidparams"
            );
        }

        Optional<ScanResponseDTO> result;
        if (serialPallet != null) {
            result = scanService.scanBySerialPallet(serialPallet);
        } else {
            result = scanService.scanByReelId(reelId);
        }

        return ResponseUtil.wrapOrNotFound(result);
    }
}
