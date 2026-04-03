package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.InboundWMSPallet;
import com.mycompany.myapp.domain.InboundWMSSession;
import com.mycompany.myapp.service.dto.InboundWMSPalletDTO;
import com.mycompany.myapp.service.dto.InboundWMSSessionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InboundWMSPallet} and its DTO {@link InboundWMSPalletDTO}.
 */
@Mapper(componentModel = "spring")
public interface InboundWMSPalletMapper
    extends EntityMapper<InboundWMSPalletDTO, InboundWMSPallet> {
    @Mapping(
        target = "inboundWMSSession",
        source = "inboundWMSSession",
        qualifiedByName = "inboundWMSSessionId"
    )
    InboundWMSPalletDTO toDto(InboundWMSPallet s);

    @Named("inboundWMSSessionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InboundWMSSessionDTO toDtoInboundWMSSessionId(
        InboundWMSSession inboundWMSSession
    );
}
