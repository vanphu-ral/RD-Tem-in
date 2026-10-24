package com.mycompany.myapp.service.smart;

import com.mycompany.myapp.domain.DeliveryNotification;
import com.mycompany.myapp.domain.SapPor1R1;
import com.mycompany.myapp.repository.DeliveryNotificationRepository;
import com.mycompany.myapp.repository.SapPor1R1Repository;
import com.mycompany.myapp.service.dto.smart.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SmartDeliveryService {

    private final DeliveryNotificationRepository deliveryNotificationRepository;
    private final SapPor1R1Repository sapPor1R1Repository;
    private final Set<String> processedRequests = Collections.synchronizedSet(
        new HashSet<>()
    );

    public SmartDeliveryService(
        DeliveryNotificationRepository deliveryNotificationRepository,
        SapPor1R1Repository sapPor1R1Repository
    ) {
        this.deliveryNotificationRepository = deliveryNotificationRepository;
        this.sapPor1R1Repository = sapPor1R1Repository;
    }

    @Transactional
    public ApiResponse<ApiResponse.NoticeData> processDeliveryNotice(
        String requestId,
        DeliveryNoticeRequest request
    ) {
        if (processedRequests.contains(requestId)) {
            return new ApiResponse<>(
                true,
                "Request already processed",
                new ApiResponse.NoticeData(
                    request.getNoticeNo(),
                    0,
                    Collections.emptyList()
                )
            );
        }

        String action = request.getActionType();
        String noticeNo = request.getNoticeNo();

        if ("DELETE".equalsIgnoreCase(action)) {
            handleDelete(noticeNo);
            processedRequests.add(requestId);
            return new ApiResponse<>(
                true,
                "Deleted successfully",
                new ApiResponse.NoticeData(noticeNo, 0, Collections.emptyList())
            );
        }

        if (
            "CREATE".equalsIgnoreCase(action) ||
            "UPDATE".equalsIgnoreCase(action)
        ) {
            ApiResponse<ApiResponse.NoticeData> result = handleCreateOrUpdate(
                request
            );
            if (
                result.isSuccess() || !result.getData().getRejected().isEmpty()
            ) {
                processedRequests.add(requestId);
            }
            return result;
        }

        return new ApiResponse<>(false, "Invalid actionType", null);
    }

    private ApiResponse<ApiResponse.NoticeData> handleCreateOrUpdate(
        DeliveryNoticeRequest request
    ) {
        DeliveryNotification notification = deliveryNotificationRepository
            .findOneByDeliveryNotificationCode(request.getNoticeNo())
            .orElse(new DeliveryNotification());

        notification.setDeliveryNotificationCode(request.getNoticeNo());
        notification.setVendorCode(request.getSupplierCode());
        notification.setVendorName(request.getSupplierName());
        notification.setFactory(request.getFactory());
        notification.setNoticeDate(request.getNoticeDate());
        notification.setEntryDate(request.getDateArrivalRangDong());
        notification.setType(request.getType());
        notification.setSource("SMART-SYSTEM");
        notification.setStatus("new");

        if (!request.getVehicles().isEmpty()) {
            notification.setContNo(request.getVehicles().get(0).getVehicleNo());
        }

        Set<String> uniquePo = new HashSet<>();
        Set<String> uniqueItems = new HashSet<>();

        for (VehicleDto vehicle : request.getVehicles()) {
            for (LineDto line : vehicle.getLines()) {
                if (line.getPoErp() != null) uniquePo.add(line.getPoErp());
                if (line.getItemCode() != null) uniqueItems.add(
                    line.getItemCode()
                );
            }
        }
        notification.setNumberOfPo(uniquePo.size());
        notification.setNumberOfItem(uniqueItems.size());

        deliveryNotificationRepository.save(notification);

        List<ApiResponse.RejectedLine> rejected = new ArrayList<>();
        int receivedCount = 0;

        for (VehicleDto vehicle : request.getVehicles()) {
            for (LineDto line : vehicle.getLines()) {
                if (validateLine(line)) {
                    saveOrUpdateLine(notification, line);
                    receivedCount++;
                } else {
                    rejected.add(
                        new ApiResponse.RejectedLine(
                            line.getLineId().toString(),
                            "Mã vật tư không tồn tại"
                        )
                    );
                }
            }
        }

        boolean success = rejected.isEmpty();
        return new ApiResponse<>(
            success,
            success ? "Tiếp nhận thành công" : "Có dòng không hợp lệ",
            new ApiResponse.NoticeData(
                request.getNoticeNo(),
                receivedCount,
                rejected
            )
        );
    }

    private void handleDelete(String noticeNo) {
        deliveryNotificationRepository.deleteByDeliveryNotificationCode(
            noticeNo
        );
        sapPor1R1Repository.deleteByDeliveryNotification_DeliveryNotificationCode(
            noticeNo
        );
    }

    private boolean validateLine(LineDto line) {
        return line.getItemCode() != null && !line.getItemCode().isBlank();
    }

    private void saveOrUpdateLine(
        DeliveryNotification notification,
        LineDto lineDto
    ) {
        SapPor1R1 line = sapPor1R1Repository
            .findOneByLineId(lineDto.getLineId())
            .orElse(new SapPor1R1());

        line.setLineId(lineDto.getLineId());
        line.setDeliveryId(UUID.fromString(lineDto.getDeliveryId()));
        line.setPoSmart(lineDto.getPoSmart());
        line.setDocEntry(lineDto.getPoErp());
        line.setPrNumber(lineDto.getPrNumber());
        line.setContractNo(lineDto.getContractNo());
        line.setItemCode(lineDto.getItemCode());
        line.setDscription(lineDto.getItemName());
        line.setWhsCode(lineDto.getWhsCode());
        line.setUnitMsr(lineDto.getUnit());
        line.setQuantity(lineDto.getQuantity());
        line.setInvoiceSmartNo(lineDto.getInvoiceSmartNo());
        line.setInvoiceNo(lineDto.getInvoiceNo());
        line.setInvoiceDate(lineDto.getInvoiceDate());
        line.setDeliveryTurn(lineDto.getDeliveryTurn());
        line.setDeliveryNotification(notification);

        sapPor1R1Repository.save(line);
    }
}
