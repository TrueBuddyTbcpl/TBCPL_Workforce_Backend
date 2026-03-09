package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpProfileContactInfoRepository extends JpaRepository<OpProfileContactInfo, Long> {
    Optional<OpProfileContactInfo> findByProfileId(Long profileId);
    boolean existsByProfileId(Long profileId);
}
