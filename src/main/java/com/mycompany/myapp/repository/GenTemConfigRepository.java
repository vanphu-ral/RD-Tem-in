package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.GenTemConfig;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the GenTemConfig entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GenTemConfigRepository
    extends JpaRepository<GenTemConfig, Long> {}
