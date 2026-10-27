package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.ScanTotalQueryService;
import com.mycompany.myapp.service.dto.ScanTotalPayloadDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.ResponseUtil;

/**
 * API cho gateway gọi lấy tổng SL thực nhận theo đơn thông báo giao hàng:
 * {@code GET /api/scan-totals/{noticeNo}} (hoặc {@code GET /api/scan-totals?noticeNo=...}) — noticeNo là
 * mã thông báo giao hàng (delivery_notification_code); trả trạng thái đơn + mọi dòng PO của đơn, mỗi
 * dòng kèm quantityReceived (tổng cộng dồn các thùng đã scan, tính tại thời điểm gọi).
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
     * @param noticeNo mã thông báo giao hàng.
     * @return 200 + dữ liệu; 404 nếu không có đơn (chưa xoá) mang mã này; 400 nếu noticeNo rỗng.
     */
    @GetMapping("/{noticeNo}")
    public ResponseEntity<ScanTotalPayloadDTO> getByNoticeNo(
        @PathVariable("noticeNo") String noticeNo
    ) {
        return findByNoticeNo(noticeNo);
    }

    /** Như trên, noticeNo truyền qua query param (mã có ký tự đặc biệt như "/") */
    @GetMapping
    public ResponseEntity<ScanTotalPayloadDTO> getByNoticeNoParam(
        @RequestParam(value = "noticeNo", required = false) String noticeNo
    ) {
        return findByNoticeNo(noticeNo);
    }

    private ResponseEntity<ScanTotalPayloadDTO> findByNoticeNo(
        String noticeNo
    ) {
        LOG.debug("REST request to get scan totals of noticeNo {}", noticeNo);
        if (noticeNo == null || noticeNo.isBlank()) {
            throw new BadRequestAlertException(
                "noticeNo không được để trống",
                ENTITY_NAME,
                "noticeNoRequired"
            );
        }
        return ResponseUtil.wrapOrNotFound(
            scanTotalQueryService.findByNoticeNo(noticeNo.trim())
        );
    }
}
