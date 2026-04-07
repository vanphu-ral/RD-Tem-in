package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.InboundWMSSession;
import com.mycompany.myapp.service.dto.InboundWMSSessionDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { InboundWMSPalletMapper.class })
public interface InboundWMSSessionMapper
    extends EntityMapper<InboundWMSSessionDTO, InboundWMSSession> {
    @Mapping(target = "inboundWMSPallets", ignore = true)
    InboundWMSSessionDTO toDto(InboundWMSSession s);

    InboundWMSSession toEntity(InboundWMSSessionDTO inboundWMSSessionDTO);
}
