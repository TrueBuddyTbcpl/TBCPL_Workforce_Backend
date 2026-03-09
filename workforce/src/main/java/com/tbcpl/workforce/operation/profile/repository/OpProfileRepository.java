package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfile;
import com.tbcpl.workforce.operation.profile.enums.ProfileStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OpProfileRepository extends JpaRepository<OpProfile, Long> {

    @Query("SELECT p FROM OpProfile p WHERE p.isDeleted = false ORDER BY p.createdAt DESC")
    Page<OpProfile> findAllActive(Pageable pageable);

    @Query("SELECT p FROM OpProfile p WHERE p.isDeleted = false AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.profileNumber) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<OpProfile> searchByNameOrProfileNumber(@Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM OpProfile p WHERE p.isDeleted = false AND p.status = :status")
    Page<OpProfile> findByStatus(@Param("status") ProfileStatus status, Pageable pageable);

    Optional<OpProfile> findByProfileNumberAndIsDeletedFalse(String profileNumber);

    boolean existsByProfileNumber(String profileNumber);

    // For profile number generation — get latest profile number
    @Query("SELECT p.profileNumber FROM OpProfile p ORDER BY p.id DESC LIMIT 1")
    Optional<String> findLatestProfileNumber();
}
