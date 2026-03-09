package com.tbcpl.workforce.operation.cases.repository;

import com.tbcpl.workforce.operation.cases.entity.Case;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {

    Optional<Case> findByCaseNumberAndIsDeletedFalse(String caseNumber);

    Optional<Case> findByPrereportIdAndIsDeletedFalse(Long prereportId);

    boolean existsByPrereportIdAndIsDeletedFalse(Long prereportId);

    @Query("SELECT c FROM Case c WHERE c.isDeleted = false ORDER BY c.createdAt DESC")
    Page<Case> findAllActiveCases(Pageable pageable);

    @Query("SELECT c FROM Case c WHERE c.clientId = :clientId AND c.isDeleted = false ORDER BY c.createdAt DESC")
    Page<Case> findByClientId(@Param("clientId") Long clientId, Pageable pageable);

    @Query("SELECT c FROM Case c WHERE c.status = :status AND c.isDeleted = false ORDER BY c.createdAt DESC")
    Page<Case> findByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Case c WHERE YEAR(c.createdAt) = :year")
    Long countByYear(@Param("year") int year);
}
