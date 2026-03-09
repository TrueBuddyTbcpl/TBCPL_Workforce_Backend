package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileBusinessActivities;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpProfileBusinessActivitiesRepository extends JpaRepository<OpProfileBusinessActivities, Long> {
    Optional<OpProfileBusinessActivities> findByProfileId(Long profileId);
    boolean existsByProfileId(Long profileId);
}
