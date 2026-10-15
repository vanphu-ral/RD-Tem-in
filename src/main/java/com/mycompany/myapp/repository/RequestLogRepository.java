package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.RequestLog;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestLogRepository extends JpaRepository<RequestLog, UUID> {
    Optional<RequestLog> findByRequestId(String requestId);
}
