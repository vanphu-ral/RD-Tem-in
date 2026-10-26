package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.ScanTotalQueryService;
import com.mycompany.myapp.service.dto.ScanTotalPayloadDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.ResponseUtil;

/**
 * API cho gateway gọi lấy tổng SL thực nhận theo phiếu lịch giao hàng:
 * {@code GET /api/scan-totals/{deliveryId}} — mọi dòng PO của phiếu, mỗi dòng kèm
 * quantityReceived (tổng cộng dồn các thùng đã scan, tính tại thời điểm gọi).
 */
@RestController
@RequestMapping("/api/scan-totals")
public class ScanTotalResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        ScanTotalResource.class
    );

    private static final String ENTITY_NAME = "scanTotal";

    private final ScanTotalQueryService scanTotalQueryService;

    public ScanTotalResource(ScanTotalQueryService scanTotalQueryService) {
        this.scanTotalQueryService = scanTotalQueryService;
    }

    /**
     * @param deliveryId GUID phiếu lịch giao hàng.
     * @return 200 + dữ liệu; 404 nếu phiếu không có dòng PO nào; 400 nếu deliveryId sai định dạng.
     */
    @GetMapping("/{deliveryId}")
    public ResponseEntity<ScanTotalPayloadDTO> getByDeliveryId(
        @PathVariable("deliveryId") String deliveryId
    ) {
        LOG.debug(
            "REST request to get scan totals of deliveryId {}",
            deliveryId
        );
        UUID id;
        try {
            id = UUID.fromString(deliveryId.trim());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestAlertException(
                "deliveryId không đúng định dạng GUID",
                ENTITY_NAME,
                "deliveryIdInvalid"
            );
        }
        return ResponseUtil.wrapOrNotFound(
            scanTotalQueryService.findByDeliveryId(id)
        );
    }
}
