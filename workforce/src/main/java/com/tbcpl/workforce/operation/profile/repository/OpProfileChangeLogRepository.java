package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileChangeLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpProfileChangeLogRepository extends JpaRepository<OpProfileChangeLog, Long> {
    Page<OpProfileChangeLog> findByProfileIdOrderByChangedAtDesc(Long profileId, Pageable pageable);
}
