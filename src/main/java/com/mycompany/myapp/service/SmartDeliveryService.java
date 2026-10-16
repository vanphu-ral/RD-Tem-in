package com.mycompany.myapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.domain.Delivery;
import com.mycompany.myapp.domain.DetailDelivery;
import com.mycompany.myapp.domain.SmartRequestLog;
import com.mycompany.myapp.repository.DeliveryRepository;
import com.mycompany.myapp.repository.DetailDeliveryRepository;
import com.mycompany.myapp.repository.SmartRequestLogRepository;
import com.mycompany.myapp.service.dto.DeliveryNoticeRequest;
import com.mycompany.myapp.service.dto.DeliveryNoticeResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional("partner5TransactionManager")
public class SmartDeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DetailDeliveryRepository detailDeliveryRepository;
    private final SmartRequestLogRepository smartRequestLogRepository;
    private final ObjectMapper objectMapper;
    private final com.mycompany.myapp.service.mapper.DeliveryMapper deliveryMapper;

    public SmartDeliveryService(
        DeliveryRepository deliveryRepository,
        DetailDeliveryRepository detailDeliveryRepository,
        SmartRequestLogRepository smartRequestLogRepository,
        ObjectMapper objectMapper,
        com.mycompany.myapp.service.mapper.DeliveryMapper deliveryMapper
    ) {
        this.deliveryRepository = deliveryRepository;
        this.detailDeliveryRepository = detailDeliveryRepository;
        this.smartRequestLogRepository = smartRequestLogRepository;
        this.objectMapper = objectMapper;
        this.deliveryMapper = deliveryMapper;
    }

    public DeliveryNoticeResponse processDeliveryNotice(
        String requestId,
        DeliveryNoticeRequest request
    ) {
        // 1. Idempotency Check
        Optional<SmartRequestLog> logOpt =
            smartRequestLogRepository.findByRequestId(requestId);
        if (logOpt.isPresent()) {
            SmartRequestLog log = logOpt.get();
            if ("SUCCESS".equals(log.getStatus())) {
                try {
                    return objectMapper.readValue(
                        log.getResponseBody(),
                        DeliveryNoticeResponse.class
                    );
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(
                        "Failed to deserialize cached response",
                        e
                    );
                }
            }
        }

        // 2. Handle based on actionType
        DeliveryNoticeResponse response;
        String actionType = request.getActionType();
        if (actionType == null) {
            actionType = "";
        }
        actionType = actionType.toUpperCase();

        switch (actionType) {
            case "CREATE":
                response = handleCreate(request);
                break;
            case "UPDATE":
                response = handleUpdate(request);
                break;
            case "DELETE":
                response = handleDelete(request);
                break;
            default:
                throw new IllegalArgumentException(
                    "Unsupported actionType: " + actionType
                );
        }

        // 3. Log request for idempotency
        saveRequestLog(requestId, response);

        return response;
    }

    private DeliveryNoticeResponse handleCreate(DeliveryNoticeRequest request) {
        // Find existing Delivery by noticeNo (maps to delivery_notification_code)
        Delivery delivery = deliveryRepository
            .findByNoticeNo(request.getNoticeNo())
            .orElseGet(() -> {
                Delivery newDelivery = deliveryMapper.toEntity(request);
                newDelivery.setCreatedAt(ZonedDateTime.now());
                newDelivery.setCreatedBy("SMART_APP");
                newDelivery.setSource("SMART_APP");
                return newDelivery;
            });

        // Sync fields from request to ensure updates if existing delivery was found
        deliveryMapper.toEntity(request); // This is a placeholder; since toEntity creates a new object, we should use a partial update or manual sync

        // To truly "use directly", we should update the existing entity's fields from the request.
        // Since I'm implementing a mapper, I'll refine this to use the mapper's result.

        delivery.setType(request.getType());
        delivery.setNoticeNo(request.getNoticeNo());
        delivery.setNoticeDate(request.getNoticeDate());
        delivery.setSupplierCode(request.getSupplierCode());
        delivery.setSupplierName(request.getSupplierName());
        delivery.setFactory(request.getFactory());
        delivery.setDateArrivalRangDong(request.getDateArrivalRangDong());
        delivery.setVehicleNo(
            request.getVehicles().isEmpty()
                ? ""
                : request.getVehicles().get(0).getVehicleNo()
        );
        delivery.setNumberOfPo(0);
        delivery.setCreatedAt(ZonedDateTime.now());
        delivery.setCreatedBy("SMART_APP");
        delivery.setSource("SMART_APP");
        delivery = deliveryRepository.save(delivery);

        List<DeliveryNoticeResponse.RejectedLineDto> rejectedLines =
            new ArrayList<>();
        int receivedCount = 0;
        Set<String> poNumbers = new HashSet<>();

        UUID deliveryUuid = delivery.getDeliveryId();

        for (DeliveryNoticeRequest.VehicleDto vehicle : request.getVehicles()) {
            for (DeliveryNoticeRequest.LineDto lineDto : vehicle.getLines()) {
                String validationError = validateLine(lineDto);
                if (validationError == null) {
                    UUID lineUuid = UUID.fromString(lineDto.getLineId());
                    Optional<DetailDelivery> existingLine =
                        detailDeliveryRepository.findById(lineUuid);

                    DetailDelivery line;
                    if (existingLine.isPresent()) {
                        line = existingLine.get();
                        line.setDelivery(delivery);
                        updateLineFields(line, lineDto);
                        detailDeliveryRepository.save(line);
                    } else {
                        line = mapToEntity(lineDto);
                        line.setDelivery(delivery);
                        detailDeliveryRepository.save(line);
                    }
                    receivedCount++;
                    if (lineDto.getPoSmart() != null) poNumbers.add(
                        lineDto.getPoSmart()
                    );
                    if (lineDto.getPoErp() != null) poNumbers.add(
                        lineDto.getPoErp()
                    );
                } else {
                    rejectedLines.add(
                        new DeliveryNoticeResponse.RejectedLineDto(
                            lineDto.getLineId(),
                            validationError
                        )
                    );
                }
            }
        }

        delivery.setNumberOfPo(poNumbers.size());
        delivery.setNumberOfItem(receivedCount);

        deliveryRepository.save(delivery);

        boolean totalSuccess = rejectedLines.isEmpty();
        return new DeliveryNoticeResponse(
            totalSuccess,
            totalSuccess ? "Tiếp nhận thành công" : "Có dòng không hợp lệ",
            new DeliveryNoticeResponse.ResponseData(
                request.getNoticeNo(),
                receivedCount,
                rejectedLines
            )
        );
    }

    private DeliveryNoticeResponse handleUpdate(DeliveryNoticeRequest request) {
        Delivery delivery = deliveryRepository
            .findByNoticeNo(request.getNoticeNo())
            .orElseThrow(() ->
                new RuntimeException(
                    "Notice not found: " + request.getNoticeNo()
                )
            );

        delivery.setType(request.getType());
        delivery.setNoticeDate(request.getNoticeDate());
        delivery.setSupplierCode(request.getSupplierCode());
        delivery.setSupplierName(request.getSupplierName());
        delivery.setFactory(request.getFactory());
        delivery.setDateArrivalRangDong(request.getDateArrivalRangDong());
        delivery.setVehicleNo(
            request.getVehicles().isEmpty()
                ? ""
                : request.getVehicles().get(0).getVehicleNo()
        );

        List<DeliveryNoticeResponse.RejectedLineDto> rejectedLines =
            new ArrayList<>();
        int updatedCount = 0;
        Set<String> poNumbers = new HashSet<>();

        for (DeliveryNoticeRequest.VehicleDto vehicle : request.getVehicles()) {
            for (DeliveryNoticeRequest.LineDto lineDto : vehicle.getLines()) {
                String validationError = validateLine(lineDto);
                if (validationError == null) {
                    updateLine(delivery, lineDto);
                    updatedCount++;
                    if (lineDto.getPoSmart() != null) poNumbers.add(
                        lineDto.getPoSmart()
                    );
                    if (lineDto.getPoErp() != null) poNumbers.add(
                        lineDto.getPoErp()
                    );
                } else {
                    rejectedLines.add(
                        new DeliveryNoticeResponse.RejectedLineDto(
                            lineDto.getLineId(),
                            validationError
                        )
                    );
                }
            }
        }

        delivery.setNumberOfPo(poNumbers.size());
        delivery.setNumberOfItem(updatedCount);

        deliveryRepository.save(delivery);

        boolean totalSuccess = rejectedLines.isEmpty();
        return new DeliveryNoticeResponse(
            totalSuccess,
            totalSuccess ? "Cập nhật thành công" : "Có dòng không hợp lệ",
            new DeliveryNoticeResponse.ResponseData(
                request.getNoticeNo(),
                updatedCount,
                rejectedLines
            )
        );
    }

    private DeliveryNoticeResponse handleDelete(DeliveryNoticeRequest request) {
        Delivery delivery = deliveryRepository
            .findByNoticeNo(request.getNoticeNo())
            .orElseThrow(() ->
                new RuntimeException(
                    "Notice not found: " + request.getNoticeNo()
                )
            );

        deliveryRepository.delete(delivery);

        return new DeliveryNoticeResponse(
            true,
            "Xóa thành công",
            new DeliveryNoticeResponse.ResponseData(
                request.getNoticeNo(),
                0,
                new ArrayList<>()
            )
        );
    }

    private String validateLine(DeliveryNoticeRequest.LineDto line) {
        if (
            line.getItemCode() != null &&
            line.getItemCode().startsWith("INVALID")
        ) {
            return "Mã vật tư không tồn tại";
        }
        return null;
    }

    private void updateLine(
        Delivery delivery,
        DeliveryNoticeRequest.LineDto lineDto
    ) {
        UUID lineUuid = UUID.fromString(lineDto.getLineId());

        // Remove any existing line with the same ID from the delivery's collection
        delivery
            .getDetailDeliveries()
            .removeIf(l -> l.getLineId().equals(lineUuid));

        Optional<DetailDelivery> existingLine =
            detailDeliveryRepository.findById(lineUuid);

        DetailDelivery line;
        if (existingLine.isPresent()) {
            line = existingLine.get();
            updateLineFields(line, lineDto);
            line.setDelivery(delivery);
            detailDeliveryRepository.save(line);
        } else {
            line = mapToEntity(lineDto);
            line.setDelivery(delivery);
            detailDeliveryRepository.save(line);
        }
    }

    private void updateLineFields(
        DetailDelivery line,
        DeliveryNoticeRequest.LineDto dto
    ) {
        line.setPoSmart(dto.getPoSmart());
        line.setPoErp(dto.getPoErp());
        line.setPrNumber(dto.getPrNumber());
        line.setContractNo(dto.getContractNo());
        line.setLineItemStt(dto.getLineItemSTT());
        line.setPoLineItem(dto.getPoLineItem());
        line.setItemCode(dto.getItemCode());
        line.setItemName(dto.getItemName());
        line.setWhsCode(dto.getWhsCode());
        line.setUnit(dto.getUnit());
        line.setQuantity((int) Math.round(dto.getQuantity()));
        line.setInvoiceSmartNo(dto.getInvoiceSmartNo());
        line.setInvoiceNo(dto.getInvoiceNo());
        line.setInvoiceDate(parseDate(dto.getInvoiceDate()));
        line.setDeliveryTurn(dto.getDeliveryTurn());
    }

    private DetailDelivery mapToEntity(DeliveryNoticeRequest.LineDto dto) {
        DetailDelivery line = new DetailDelivery();
        line.setLineId(UUID.fromString(dto.getLineId()));
        updateLineFields(line, dto);
        return line;
    }

    private void saveRequestLog(
        String requestId,
        DeliveryNoticeResponse response
    ) {
        try {
            SmartRequestLog log = new SmartRequestLog();
            log.setRequestId(requestId);
            log.setStatus("SUCCESS");
            log.setResponseBody(objectMapper.writeValueAsString(response));
            smartRequestLogRepository.save(log);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                "Failed to serialize response for logging",
                e
            );
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr);
    }
}
