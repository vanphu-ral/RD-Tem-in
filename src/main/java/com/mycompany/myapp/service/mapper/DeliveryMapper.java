package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Delivery;
import com.mycompany.myapp.service.dto.DeliveryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Delivery} and its DTO {@link DeliveryDTO}.
 */
@Mapper(componentModel = "spring")
public interface DeliveryMapper extends EntityMapper<DeliveryDTO, Delivery> {
    @Mapping(target = "deliveryId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "numberOfPo", ignore = true)
    @Mapping(target = "numberOfItem", ignore = true)
    @Mapping(target = "detailDeliveries", ignore = true)
    @Mapping(
        target = "vehicleNo",
        expression = "java(request.getVehicles() != null && !request.getVehicles().isEmpty() ? request.getVehicles().get(0).getVehicleNo() : \"\")"
    )
    Delivery toEntity(
        com.mycompany.myapp.service.dto.DeliveryNoticeRequest request
    );
}
