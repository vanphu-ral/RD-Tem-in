package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.SapOcrd;
import com.mycompany.myapp.service.dto.SapOcrdDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SapOcrd} and its DTO {@link SapOcrdDTO}.
 */
@Mapper(componentModel = "spring")
public interface SapOcrdMapper extends EntityMapper<SapOcrdDTO, SapOcrd> {}
