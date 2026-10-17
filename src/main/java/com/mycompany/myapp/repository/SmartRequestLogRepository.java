package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.SmartRequestLog;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmartRequestLogRepository
    extends JpaRepository<SmartRequestLog, UUID> {
    Optional<SmartRequestLog> findByRequestId(String requestId);
}
