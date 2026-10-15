package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Delivery;
import com.mycompany.myapp.service.dto.DeliveryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Delivery} and its DTO {@link DeliveryDTO}.
 */
@Mapper(componentModel = "spring")
public interface DeliveryMapper extends EntityMapper<DeliveryDTO, Delivery> {}
