package com.mycompany.panacimmc.repository;

import com.mycompany.panacimmc.domain.Location;
import com.mycompany.panacimmc.domain.LocationResponse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    @Query(
        value = "select " +
        "Location_Id as id" +
        ",Location_Name as locationName " +
        ",Location_FullName as locationFullName" +
        " from Location ;",
        nativeQuery = true
    )
    public List<LocationResponse> getFullLocation();
}
