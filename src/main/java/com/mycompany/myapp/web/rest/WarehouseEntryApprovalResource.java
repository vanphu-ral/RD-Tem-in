package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.InboundWMSSessionService;
import com.mycompany.myapp.service.dto.InboundWMSSessionDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for warehouse entry approval operations.
 */
@RestController
@RequestMapping("/api/warehouse-entry-approval")
public class WarehouseEntryApprovalResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        WarehouseEntryApprovalResource.class
    );

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InboundWMSSessionService inboundWMSSessionService;

    public WarehouseEntryApprovalResource(
        InboundWMSSessionService inboundWMSSessionService
    ) {
        this.inboundWMSSessionService = inboundWMSSessionService;
    }

    /**
     * {@code GET  /work-order-code/{workOrderCode}} : get grouped inbound WMS sessions by work order code.
     *
     * @param workOrderCode the work order code to filter by.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of grouped sessions in body.
     */
    @GetMapping("/work-order-code/{workOrderCode}")
    public ResponseEntity<
        List<InboundWMSSessionDTO>
    > getGroupedSessionsByWorkOrderCode(@PathVariable String workOrderCode) {
        LOG.debug(
            "REST request to get grouped InboundWMSSessions by workOrderCode : {}",
            workOrderCode
        );
        List<InboundWMSSessionDTO> result =
            inboundWMSSessionService.findGroupedSessionsByWorkOrderCode(
                workOrderCode
            );
        return ResponseEntity.ok(result);
    }
}
