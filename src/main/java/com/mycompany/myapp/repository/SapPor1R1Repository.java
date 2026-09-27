package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.SapPor1R1;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SapPor1R1 entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SapPor1R1Repository extends JpaRepository<SapPor1R1, Long> {}
