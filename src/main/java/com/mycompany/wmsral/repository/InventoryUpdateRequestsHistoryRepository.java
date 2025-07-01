package com.mycompany.wmsral.repository;

import com.mycompany.wmsral.domain.InventoryUpdateRequestsHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InventoryUpdateRequestsHistoryRepository extends JpaRepository<InventoryUpdateRequestsHistory,Long> {
    @Query(value = "select * from inventory_update_requests_history where " +
        " requested_time between ?1 and ?2 ;",nativeQuery = true)
    public List<InventoryUpdateRequestsHistory> getByRequestedTimeRange(String startTime, String endTime);
    public List<InventoryUpdateRequestsHistory> findAllByRequestCode(String requestCode);
    // This interface extends InventoryUpdateRequestsDetailRepository
    // and can be used to add additional methods specific to history requests
    // if needed in the future.
}
