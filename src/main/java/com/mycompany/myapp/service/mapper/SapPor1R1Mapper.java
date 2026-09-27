package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.DeliveryNotification;
import com.mycompany.myapp.domain.SapPor1R1;
import com.mycompany.myapp.service.dto.DeliveryNotificationDTO;
import com.mycompany.myapp.service.dto.SapPor1R1DTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SapPor1R1} and its DTO {@link SapPor1R1DTO}.
 */
@Mapper(componentModel = "spring")
public interface SapPor1R1Mapper extends EntityMapper<SapPor1R1DTO, SapPor1R1> {
  @Mapping(
    target = "deliveryNotification",
    source = "deliveryNotification",
    qualifiedByName = "deliveryNotificationId"
  )
  SapPor1R1DTO toDto(SapPor1R1 s);

  @Named("deliveryNotificationId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  DeliveryNotificationDTO toDtoDeliveryNotificationId(
    DeliveryNotification deliveryNotification
  );
}
