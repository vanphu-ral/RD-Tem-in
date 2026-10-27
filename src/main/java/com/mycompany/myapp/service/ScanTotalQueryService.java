package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.DeliveryNotification;
import com.mycompany.myapp.domain.SapPor1R1;
import com.mycompany.myapp.repository.DeliveryNotificationRepository;
import com.mycompany.myapp.repository.SapPor1R1Repository;
import com.mycompany.myapp.repository.VendorLabelInfoRepository;
import com.mycompany.myapp.service.dto.ScanTotalLineDTO;
import com.mycompany.myapp.service.dto.ScanTotalPayloadDTO;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Chỉ đọc: tổng SL thực nhận (quantityReceived = tổng initial_quantity các thùng đã scan) của
 * từng dòng PO thuộc 1 đơn thông báo giao hàng (noticeNo = delivery_notification_code) — tính từ DB
 * tại thời điểm gọi, là tổng cộng dồn mọi lần scan (xoá thùng → tổng giảm theo).
 */
@Service
@Transactional(value = "partner5TransactionManager", readOnly = true)
public class ScanTotalQueryService {

    /** Đơn mới tạo (chưa scan) / đã có thùng scan vào — khớp trạng thái web lưu */
    private static final String STATUS_NEW = "New";
    private static final String STATUS_SCANNING = "SCANNING";

    private static final Logger LOG = LoggerFactory.getLogger(
        ScanTotalQueryService.class
    );

    private final DeliveryNotificationRepository deliveryNotificationRepository;
    private final SapPor1R1Repository sapPor1R1Repository;
    private final VendorLabelInfoRepository vendorLabelInfoRepository;

    public ScanTotalQueryService(
        DeliveryNotificationRepository deliveryNotificationRepository,
        SapPor1R1Repository sapPor1R1Repository,
        VendorLabelInfoRepository vendorLabelInfoRepository
    ) {
        this.deliveryNotificationRepository = deliveryNotificationRepository;
        this.sapPor1R1Repository = sapPor1R1Repository;
        this.vendorLabelInfoRepository = vendorLabelInfoRepository;
    }

    /**
     * Đơn (chưa xoá) mang mã thông báo noticeNo: dòng PO của đơn + dòng PO chưa gắn đơn nào nhưng
     * mang deliveryId của đơn (gộp, không trùng). Không có đơn → empty; đơn chưa có dòng → lines rỗng.
     */
    public Optional<ScanTotalPayloadDTO> findByNoticeNo(String noticeNo) {
        LOG.debug("Request to get scan totals of noticeNo {}", noticeNo);
        List<DeliveryNotification> deliveries =
            deliveryNotificationRepository.findByDeliveryNotificationCodeAndDeletedAtIsNullOrderByIdAsc(
                noticeNo
            );
        if (deliveries.isEmpty()) {
            return Optional.empty();
        }
        DeliveryNotification dn = deliveries.get(0);

        Map<Long, SapPor1R1> lines = new LinkedHashMap<>();
        sapPor1R1Repository
            .findByDeliveryNotificationIdOrderByIdAsc(dn.getId())
            .forEach(line -> lines.put(line.getId(), line));
        if (dn.getDeliveryId() != null) {
            sapPor1R1Repository
                .findByDeliveryIdOrderByIdAsc(dn.getDeliveryId())
                .stream()
                .filter(line -> line.getDeliveryNotification() == null)
                .forEach(line -> lines.putIfAbsent(line.getId(), line));
        }

        Map<Long, Long> received = new HashMap<>();
        if (!lines.isEmpty()) {
            for (Object[] row : vendorLabelInfoRepository.sumInitialQuantityBySapPor1Ids(
                lines.keySet()
            )) {
                received.put(
                    ((Number) row[0]).longValue(),
                    row[1] == null ? 0L : ((Number) row[1]).longValue()
                );
            }
        }

        ScanTotalPayloadDTO payload = new ScanTotalPayloadDTO();
        payload.setNoticeNo(dn.getDeliveryNotificationCode());
        payload.setStatus(statusOf(dn, received));
        payload.setDeliveryId(text(dn.getDeliveryId()));
        payload.setInvoiceNumber(dn.getInvoiceNumber());
        payload.setContractCode(dn.getContractCode());
        payload.setType(dn.getType());
        payload.setVendorCode(dn.getVendorCode());
        payload.setVendorName(dn.getVendorName());
        payload.setNoticeDate(text(dn.getNoticeDate()));
        payload.setFactory(dn.getFactory());
        payload.setSource(dn.getSource());
        payload.setGeneratedAt(Instant.now().toString());
        for (SapPor1R1 line : lines.values()) {
            payload
                .getLines()
                .add(toLine(line, received.getOrDefault(line.getId(), 0L)));
        }
        return Optional.of(payload);
    }

    /**
     * Trạng thái đơn đã lưu; đơn cũ còn New (rỗng) nhưng đã có thùng scan (tạo trước khi web lưu
     * SCANNING) → trả SCANNING.
     */
    private static String statusOf(
        DeliveryNotification dn,
        Map<Long, Long> received
    ) {
        String status = dn.getStatus() == null ? "" : dn.getStatus().trim();
        boolean isNew = status.isEmpty() || STATUS_NEW.equalsIgnoreCase(status);
        if (!isNew) {
            return status;
        }
        boolean scanned = received
            .values()
            .stream()
            .anyMatch(qty -> qty > 0);
        return scanned ? STATUS_SCANNING : STATUS_NEW;
    }

    private static ScanTotalLineDTO toLine(SapPor1R1 line, long received) {
        ScanTotalLineDTO dto = new ScanTotalLineDTO();
        dto.setLineId(text(line.getLineId()));
        dto.setDeliveryId(text(line.getDeliveryId()));
        dto.setPoSmart(line.getPoSmart());
        dto.setPoErp(line.getDocEntry());
        dto.setPrNumber(line.getPrNumber());
        dto.setContractNo(line.getContractNo());
        dto.setPoLineItem(line.getLineNum());
        dto.setItemCode(line.getItemCode());
        dto.setItemName(line.getDscription());
        dto.setUnit(line.getUomCode());
        dto.setQuantity(line.getQuantity());
        dto.setQuantityReceived(received);
        // Chưa có: GRPO (sinh khi gửi SAP), thời gian duyệt nhập kho
        dto.setGrpoErp(null);
        dto.setGrpoItemLine(null);
        dto.setTimeApproved(null);
        dto.setWhsCode(line.getWhsCode());
        dto.setInvoiceSmartNo(line.getInvoiceSmartNo());
        dto.setInvoiceNo(line.getInvoiceNo());
        dto.setInvoiceDate(text(line.getInvoiceDate()));
        dto.setDeliveryTurn(line.getDeliveryTurn());
        return dto;
    }

    private static String text(Object value) {
        return value == null ? null : value.toString();
    }
}
