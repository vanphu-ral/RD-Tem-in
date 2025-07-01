package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ProfileCheckResponse;
import com.mycompany.myapp.domain.ScanPprofileCheck;
import com.mycompany.myapp.domain.TongHopResponse;
import com.mycompany.myapp.domain.scanGroupMachines;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface scanprofileCheckRepository extends JpaRepository<ScanPprofileCheck, Long> {
    @Query(value = "select * from Scan_profileCheck  where product_id =?1 ", nativeQuery = true)
    public List<ScanPprofileCheck> listProfileCheckByProduct(Long productId);

    @Query(
        value = "select \n" +
        "  mc.ma_thiet_bi as machineName,\n" +
        "  pc.position as position,\n" +
        "  pc.check_name as recordName,\n" +
        "  pc.check_value as recordValue\n" +
        "  from Scan_profileCheck as pc  \n" +
        "  inner join nhom_thiet_bi as mc on pc.machine_id = mc.id\n" +
        "  where product_id = ?1 ",
        nativeQuery = true
    )
    public List<TongHopResponse> listProfileCheck(Long productId);

    @Modifying
    @Query(
        value = "insert into Scan_profileCheck (product_id,check_name,check_value,check_status,position,version_id,machine_id) values(?1,?2,?3,?4,?5,?6,?7) ;",
        nativeQuery = true
    )
    public void insertScanProfileCheck(
        Long productId,
        String checkName,
        String checkValue,
        String checkStatus,
        Integer position,
        Long versionId,
        Integer machineId
    );

    @Modifying
    @Query(
        value = "update Scan_profileCheck set product_id = ?1 , check_name= ?2 , check_value= ?3 , check_status= ?4 ," +
        "position= ?5 , version_id= ?6 ,machine_id= ?7 where profile_id= ?8 ;",
        nativeQuery = true
    )
    public void updateScanProfileCheck(
        Long productId,
        String checkName,
        String checkValue,
        String checkStatus,
        Integer position,
        Long versionId,
        Integer machineId,
        Long profileId
    );

    @Query(
        value = "select" +
        " pc.profile_id as profileId,\n" +
        " pc.product_id as productId,\n" +
        " pc.check_name as checkName,\n" +
        " pc.check_value as checkValue,\n" +
        " pc.check_status as checkStatus,\n" +
        " pc.position as position,\n" +
        " pc.version_id as versionId,\n" +
        " pc.machine_id as machineId,\n" +
        " mc.ma_thiet_bi as machineName,\n" +
        " vs.version as version  " +
        " from Scan_profileCheck as pc  \n" +
        " inner join nhom_thiet_bi as mc on mc.id = pc.machine_id\n" +
        " inner join scan_product_versions as vs on vs.version_id = pc.version_id " +
        " where pc.product_id = ?1 ;",
        nativeQuery = true
    )
    public List<ProfileCheckResponse> getProfileCheckInfo(Long productId);

    @Query(
        value = "select" +
        " pc.profile_id as profileId,\n" +
        " pc.product_id as productId,\n" +
        " pc.check_name as checkName,\n" +
        " pc.check_value as checkValue,\n" +
        " pc.check_status as checkStatus,\n" +
        " pc.position as position,\n" +
        " pc.version_id as versionId,\n" +
        " pc.machine_id as machineId,\n" +
        " mc.ma_thiet_bi as machineName,\n" +
        " vs.version as version  " +
        " from Scan_profileCheck as pc  \n" +
        " inner join nhom_thiet_bi as mc on mc.id = pc.machine_id\n" +
        " inner join scan_product_versions as vs on vs.version_id = pc.version_id " +
        " where pc.product_id = ?1 and vs.version_id = ?2 ;",
        nativeQuery = true
    )
    public List<ProfileCheckResponse> getProfileCheckInfoWithVersionId(Long productId, Long versionId);

    @Modifying
    @Query(
        value = "" +
        "insert into Scan_profileCheck " +
        "(product_id,check_name,check_value,check_status,position,version_id,machine_id) " +
        "values(?1,?2,?3,?4,?5,?6,?7) ;",
        nativeQuery = true
    )
    public void insertProfileCheck(
        Long productId,
        String checkName,
        String checkValue,
        String checkStatus,
        Integer position,
        Long versionId,
        Integer machineId
    );
}
