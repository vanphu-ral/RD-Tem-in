package com.mycompany.myapp.repository.partner5;

import com.mycompany.myapp.domain.AttributesType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AttributesType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttributesTypeRepository
    extends
        JpaRepository<AttributesType, Long>,
        JpaSpecificationExecutor<AttributesType> {}
