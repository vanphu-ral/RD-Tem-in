package com.mycompany.myapp.repository.partner5;

import com.mycompany.myapp.domain.RdMaterialAttributes;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RdMaterialAttributes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RdMaterialAttributesRepository
    extends
        JpaRepository<RdMaterialAttributes, Long>,
        JpaSpecificationExecutor<RdMaterialAttributes> {}
