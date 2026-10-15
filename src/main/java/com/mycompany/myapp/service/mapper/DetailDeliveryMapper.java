package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Delivery;
import com.mycompany.myapp.domain.DetailDelivery;
import com.mycompany.myapp.service.dto.DeliveryDTO;
import com.mycompany.myapp.service.dto.DetailDeliveryDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DetailDelivery} and its DTO {@link DetailDeliveryDTO}.
 */
@Mapper(componentModel = "spring")
public interface DetailDeliveryMapper
    extends EntityMapper<DetailDeliveryDTO, DetailDelivery> {
    @Mapping(
        target = "delivery",
        source = "delivery",
        qualifiedByName = "deliveryDeliveryId"
    )
    DetailDeliveryDTO toDto(DetailDelivery s);

    @Named("deliveryDeliveryId")
    @BeanMapping(ignoreByDefault = true)
    // @Mapping(target = "id", source = "id")
    @Mapping(target = "deliveryId", source = "deliveryId")
    DeliveryDTO toDtoDeliveryDeliveryId(Delivery delivery);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
