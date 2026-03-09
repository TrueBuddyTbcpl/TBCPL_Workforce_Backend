package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileStepStatus;
import com.tbcpl.workforce.operation.profile.enums.StepStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OpProfileStepStatusRepository extends JpaRepository<OpProfileStepStatus, Long> {

    List<OpProfileStepStatus> findByProfileIdOrderByStepNumber(Long profileId);

    Optional<OpProfileStepStatus> findByProfileIdAndStepNumber(Long profileId, Integer stepNumber);

    @Query("SELECT COUNT(s) FROM OpProfileStepStatus s WHERE s.profile.id = :profileId " +
            "AND s.status = :status")
    long countByProfileIdAndStatus(@Param("profileId") Long profileId,
                                   @Param("status") StepStatus status);
}
