package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.SplitInventory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SplitInventory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SplitInventoryRepository
    extends
        JpaRepository<SplitInventory, Long>,
        JpaSpecificationExecutor<SplitInventory> {}
