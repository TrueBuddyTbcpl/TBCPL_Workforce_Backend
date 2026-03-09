package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfilePhysicalAttributes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpProfilePhysicalAttributesRepository extends JpaRepository<OpProfilePhysicalAttributes, Long> {
    Optional<OpProfilePhysicalAttributes> findByProfileId(Long profileId);
    boolean existsByProfileId(Long profileId);
}
