package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.InboundWMSSession;
import com.mycompany.myapp.service.dto.InboundWMSSessionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InboundWMSSession} and its DTO {@link InboundWMSSessionDTO}.
 */
@Mapper(componentModel = "spring")
public interface InboundWMSSessionMapper
    extends EntityMapper<InboundWMSSessionDTO, InboundWMSSession> {}
