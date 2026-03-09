package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileGeographicExposure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpProfileGeographicExposureRepository extends JpaRepository<OpProfileGeographicExposure, Long> {
    Optional<OpProfileGeographicExposure> findByProfileId(Long profileId);
    boolean existsByProfileId(Long profileId);
}
