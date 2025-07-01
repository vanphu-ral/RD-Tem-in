package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.scanMachines;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface scanMachinesRepository extends JpaRepository<scanMachines, Long> {
    @Query(value = " select * from scan_machines where group_id =?1", nativeQuery = true)
    public List<scanMachines> listMachines(Long groupId);

    @Modifying
    @Query(value = "insert into scan_machines (machine_name,group_id) values(?1,?2) ", nativeQuery = true)
    public void postListMachines(String machineName, Integer groupId);

    @Modifying
    @Query(value = " update scan_machines set machine_name=?1, group_id =?2 where machine_id = ?3 ;", nativeQuery = true)
    public void putListMachines(String machineName, Integer groupId, Integer machineId);

    @Query(value = "select * from scan_machines ", nativeQuery = true)
    public List<scanMachines> listAllMachines();
}
