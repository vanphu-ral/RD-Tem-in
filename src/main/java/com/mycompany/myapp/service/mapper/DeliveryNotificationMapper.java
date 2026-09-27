package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.DeliveryNotification;
import com.mycompany.myapp.service.dto.DeliveryNotificationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DeliveryNotification} and its DTO {@link DeliveryNotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface DeliveryNotificationMapper
  extends EntityMapper<DeliveryNotificationDTO, DeliveryNotification> {}
