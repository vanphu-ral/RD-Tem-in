package com.mycompany.myapp.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.domain.InboundWMSPallet;
import com.mycompany.myapp.service.dto.BoxInfoDTO;
import com.mycompany.myapp.service.dto.InboundWMSPalletDTO;
import java.util.List;
import org.mapstruct.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mapper for the entity {@link InboundWMSPallet} and its DTO {@link InboundWMSPalletDTO}.
 */
@Mapper(componentModel = "spring")
public interface InboundWMSPalletMapper
    extends EntityMapper<InboundWMSPalletDTO, InboundWMSPallet> {
    Logger log = LoggerFactory.getLogger(InboundWMSPalletMapper.class);

    @Mapping(target = "inboundWMSSessionId", source = "inboundWMSSessionId")
    @Mapping(target = "listBox", qualifiedByName = "stringToListBox")
    InboundWMSPalletDTO toDto(InboundWMSPallet s);

    @Mapping(target = "inboundWMSSession", ignore = true)
    @Mapping(target = "inboundWMSSessionId", source = "inboundWMSSessionId")
    @Mapping(target = "listBox", qualifiedByName = "listBoxToString")
    InboundWMSPallet toEntity(InboundWMSPalletDTO dto);

    @Mapping(target = "inboundWMSSession", ignore = true)
    @Mapping(target = "inboundWMSSessionId", source = "inboundWMSSessionId")
    @Mapping(target = "listBox", qualifiedByName = "listBoxToString")
    @BeanMapping(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    void partialUpdate(
        @MappingTarget InboundWMSPallet entity,
        InboundWMSPalletDTO dto
    );

    @Named("stringToListBox")
    default List<BoxInfoDTO> stringToListBox(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(
                json,
                new TypeReference<List<BoxInfoDTO>>() {}
            );
        } catch (JsonProcessingException e) {
            log.error("Error parsing listBox JSON", e);
            return null;
        }
    }

    @Named("listBoxToString")
    default String listBoxToString(List<BoxInfoDTO> listBox) {
        if (listBox == null) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(listBox);
        } catch (JsonProcessingException e) {
            log.error("Error serializing listBox to JSON", e);
            return null;
        }
    }
}
