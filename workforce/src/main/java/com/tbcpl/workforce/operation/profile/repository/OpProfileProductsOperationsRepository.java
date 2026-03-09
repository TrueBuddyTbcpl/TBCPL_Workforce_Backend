package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileProductsOperations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpProfileProductsOperationsRepository extends JpaRepository<OpProfileProductsOperations, Long> {
    Optional<OpProfileProductsOperations> findByProfileId(Long profileId);
    boolean existsByProfileId(Long profileId);
}
