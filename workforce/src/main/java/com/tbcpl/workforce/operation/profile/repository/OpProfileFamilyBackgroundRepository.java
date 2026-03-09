package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileFamilyBackground;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpProfileFamilyBackgroundRepository extends JpaRepository<OpProfileFamilyBackground, Long> {
    Optional<OpProfileFamilyBackground> findByProfileId(Long profileId);
    boolean existsByProfileId(Long profileId);
}
