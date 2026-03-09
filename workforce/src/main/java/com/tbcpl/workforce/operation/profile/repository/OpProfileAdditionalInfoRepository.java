package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileAdditionalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpProfileAdditionalInfoRepository extends JpaRepository<OpProfileAdditionalInfo, Long> {
    Optional<OpProfileAdditionalInfo> findByProfileId(Long profileId);
    boolean existsByProfileId(Long profileId);
}
