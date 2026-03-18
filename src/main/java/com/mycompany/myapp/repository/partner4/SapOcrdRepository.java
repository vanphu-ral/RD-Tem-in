package com.mycompany.myapp.repository.partner4;

import com.mycompany.myapp.domain.SapOcrd;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SapOcrd entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SapOcrdRepository extends JpaRepository<SapOcrd, Long> {}
