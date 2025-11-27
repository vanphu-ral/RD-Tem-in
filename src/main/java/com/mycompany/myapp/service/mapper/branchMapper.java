package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.branch;
import com.mycompany.myapp.service.dto.branchDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link branch} and its DTO {@link branchDTO}.
 */
@Mapper(componentModel = "spring")
public interface branchMapper extends EntityMapper<branchDTO, branch> {}
