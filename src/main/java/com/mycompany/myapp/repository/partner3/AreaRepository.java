package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Area entity.
 */
@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {}
