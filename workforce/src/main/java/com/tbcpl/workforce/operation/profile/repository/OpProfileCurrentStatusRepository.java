package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileCurrentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpProfileCurrentStatusRepository extends JpaRepository<OpProfileCurrentStatus, Long> {
    Optional<OpProfileCurrentStatus> findByProfileId(Long profileId);
    boolean existsByProfileId(Long profileId);
}
