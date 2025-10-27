package com.mycompany.panacimmc.repository;

import com.mycompany.panacimmc.domain.Location;
import com.mycompany.panacimmc.domain.LocationResponse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    //    @Query(
    //        value = "SELECT l.Location_Id AS id, l.Location_Name AS locationName, l.Location_FullName AS locationFullName " +
    //        "FROM Location l " +
    //        "INNER JOIN ( " +
    //        "   SELECT MIN(Location_Id) AS minId " +
    //        "   FROM Location " +
    //        "   GROUP BY Location_Name " +
    //        ") sub ON l.Location_Id = sub.minId",
    //        nativeQuery = true
    //    )
    //    List<LocationResponse> getFullLocation();
    @Query(
        value = "select " +
            "Location_Id as id" +
            ",Location_Name as locationName " +
            ",Location_FullName as locationFullName" +
            " from Location ;",
        nativeQuery = true
    )
    public List<LocationResponse> getFullLocation();

    @Query(
        value = "SELECT TOP 1 1 FROM Location WHERE Location_Name = :locationName",
        nativeQuery = true
    )
    Integer checkLocationExists(@Param("locationName") String locationName);
}
