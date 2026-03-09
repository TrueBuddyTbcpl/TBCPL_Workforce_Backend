package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpProfileAddressRepository extends JpaRepository<OpProfileAddress, Long> {
    Optional<OpProfileAddress> findByProfileId(Long profileId);
    boolean existsByProfileId(Long profileId);
}
