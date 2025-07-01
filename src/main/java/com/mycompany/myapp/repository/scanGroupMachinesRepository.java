package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.scanGroupMachines;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface scanGroupMachinesRepository extends JpaRepository<scanGroupMachines, Long> {
    @Query(value = " select * from [ProfileProductions].[dbo].[Scan_groupMachines] as a ;", nativeQuery = true)
    public List<scanGroupMachines> groupMachinesList();

    @Modifying
    @Query(value = "insert into Scan_groupMachines (group_name,create_at,username,group_status) values(?1,?2,?3,?4) ", nativeQuery = true)
    public void insertGroupMachines(String groupName, String createAt, String username, Integer groupStatus);

    @Modifying
    @Query(
        value = "update Scan_groupMachines set group_name =?1 , update_at=?2,username =?3, group_status =?4 where group_id =?5",
        nativeQuery = true
    )
    public void putGroupMachines(String groupName, String updateAt, String username, Integer groupStatus, Long groupId);

    @Query(value = " select group_id from Scan_groupMachines where group_name = ?1 ;", nativeQuery = true)
    public Long getAllByGroupName(String groupName);
}
