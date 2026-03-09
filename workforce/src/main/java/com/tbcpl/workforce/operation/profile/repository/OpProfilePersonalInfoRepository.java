package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfilePersonalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpProfilePersonalInfoRepository extends JpaRepository<OpProfilePersonalInfo, Long> {
    Optional<OpProfilePersonalInfo> findByProfileId(Long profileId);
    boolean existsByProfileId(Long profileId);
}
