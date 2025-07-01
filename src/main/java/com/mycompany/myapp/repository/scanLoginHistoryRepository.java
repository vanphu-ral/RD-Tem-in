package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.scanGroupMachines;
import com.mycompany.myapp.domain.scanLoginHistory;
import com.mycompany.myapp.domain.scanMachines;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface scanLoginHistoryRepository extends JpaRepository<scanLoginHistory, Long> {
    @Query(value = "select * from scan_loginHistory where order_id =?1 ;", nativeQuery = true)
    public List<scanLoginHistory> listLoginByWorkOrder(Long orderId);

    @Modifying
    @Query(value = "insert into  scan_loginHistory (username,time_login,order_id) values(?1,?2,?3) ;", nativeQuery = true)
    public void saveLoginHistoryInfo(String username, String timeLogin, Long orderId);
}
