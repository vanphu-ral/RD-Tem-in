package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.SapPoInfo;
import com.mycompany.myapp.repository.partner6.SapPoInfoRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/po-info")
public class PoInfo {

    private final Logger log = LoggerFactory.getLogger(PoInfo.class);

    private static final String ENTITY_NAME = "sapPoInfo";

    private final SapPoInfoRepository sapPoInfoRepository;

    public PoInfo(SapPoInfoRepository sapPoInfoRepository) {
        this.sapPoInfoRepository = sapPoInfoRepository;
    }

    /**
     * {@code GET /po-info/docentry/:docentry} : Get all SapPoInfo records by OPOR_DocEntry.
     *
     * @param docentry the OPOR_DocEntry to search for
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of SapPoInfo.
     */
    @GetMapping("/docentry/{docentry}")
    public ResponseEntity<List<SapPoInfo>> getByDocEntry(
        @PathVariable String docentry
    ) {
        log.debug(
            "REST request to get SapPoInfo by OPOR_DocEntry : {}",
            docentry
        );

        if (docentry == null || docentry.trim().isEmpty()) {
            throw new BadRequestAlertException(
                "OPOR_DocEntry cannot be empty",
                ENTITY_NAME,
                "docentryempty"
            );
        }

        List<SapPoInfo> sapPoInfoList = sapPoInfoRepository.findByOporDocEntry(
            docentry
        );

        return ResponseEntity.ok(sapPoInfoList);
    }
}
