package com.mycompany.panacimmc.repository;

import com.mycompany.panacimmc.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location,Long> {
    @Query(value = "select * from Location where Location_AreaName is not null ;",nativeQuery = true)
    public List<Location> getFullLocation();
}
