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
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Chỉ đọc: tổng SL thực nhận (quantityReceived = tổng initial_quantity các thùng đã scan) của
 * từng dòng PO thuộc 1 phiếu lịch giao hàng (deliveryId) — tính từ DB tại thời điểm gọi, là tổng
 * cộng dồn mọi lần scan (xoá thùng → tổng giảm theo).
 */
@Service
@Transactional(value = "partner5TransactionManager", readOnly = true)
public class ScanTotalQueryService {

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
     * Mọi dòng PO của phiếu lịch giao hàng: dòng của các đơn (chưa xoá) mang deliveryId này +
     * dòng PO mang thẳng deliveryId (gộp, không trùng). Không có dòng nào → empty.
     */
    public Optional<ScanTotalPayloadDTO> findByDeliveryId(UUID deliveryId) {
        LOG.debug("Request to get scan totals of deliveryId {}", deliveryId);
        Map<Long, SapPor1R1> lines = new LinkedHashMap<>();
        List<DeliveryNotification> deliveries =
            deliveryNotificationRepository.findByDeliveryIdAndDeletedAtIsNull(
                deliveryId
            );
        List<Long> deliveryNotificationIds = deliveries
            .stream()
            .map(DeliveryNotification::getId)
            .collect(Collectors.toList());
        if (!deliveryNotificationIds.isEmpty()) {
            sapPor1R1Repository
                .findByDeliveryNotificationIdInOrderByIdAsc(
                    deliveryNotificationIds
                )
                .forEach(line -> lines.put(line.getId(), line));
        }
        sapPor1R1Repository
            .findByDeliveryIdOrderByIdAsc(deliveryId)
            .forEach(line -> lines.putIfAbsent(line.getId(), line));
        if (lines.isEmpty()) {
            return Optional.empty();
        }

        Map<Long, Long> received = new HashMap<>();
        for (Object[] row : vendorLabelInfoRepository.sumInitialQuantityBySapPor1Ids(
            lines.keySet()
        )) {
            received.put(
                ((Number) row[0]).longValue(),
                row[1] == null ? 0L : ((Number) row[1]).longValue()
            );
        }

        ScanTotalPayloadDTO payload = new ScanTotalPayloadDTO();
        payload.setDeliveryId(deliveryId.toString());
        // Thông tin đơn: lấy đơn đầu tiên (id nhỏ nhất) mang deliveryId này; không có → null
        deliveries
            .stream()
            .min(java.util.Comparator.comparing(DeliveryNotification::getId))
            .ifPresent(dn -> {
                payload.setInvoiceNumber(dn.getInvoiceNumber());
                payload.setContractCode(dn.getContractCode());
                payload.setType(dn.getType());
                payload.setVendorCode(dn.getVendorCode());
                payload.setVendorName(dn.getVendorName());
                payload.setNoticeDate(text(dn.getNoticeDate()));
                payload.setFactory(dn.getFactory());
                payload.setSource(dn.getSource());
            });
        payload.setGeneratedAt(Instant.now().toString());
        for (SapPor1R1 line : lines.values()) {
            payload
                .getLines()
                .add(toLine(line, received.getOrDefault(line.getId(), 0L)));
        }
        return Optional.of(payload);
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
