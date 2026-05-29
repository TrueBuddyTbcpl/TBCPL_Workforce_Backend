package com.tbcpl.workforce.hr.recruitment.repository;

import com.tbcpl.workforce.hr.recruitment.entity.HrJobRequisition;
import com.tbcpl.workforce.hr.recruitment.entity.enums.RecruitmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HrJobRequisitionRepository extends JpaRepository<HrJobRequisition, Long> {

    Optional<HrJobRequisition> findByRequisitionCodeAndIsActiveTrue(String code);

    boolean existsByRequisitionCode(String code);

    Page<HrJobRequisition> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<HrJobRequisition> findByStatusAndIsActiveTrueOrderByCreatedAtDesc(
            RecruitmentStatus status, Pageable pageable);

    Page<HrJobRequisition> findByDepartmentAndIsActiveTrueOrderByCreatedAtDesc(
            String department, Pageable pageable);

    // Count open positions remaining
    @Query("SELECT SUM(jr.numberOfPositions - jr.filledPositions) FROM HrJobRequisition jr " +
            "WHERE jr.status = 'OPEN' AND jr.isActive = true")
    Long countTotalOpenPositions();

    // Last requisition code for sequence generation
    @Query("SELECT jr.requisitionCode FROM HrJobRequisition jr " +
            "WHERE jr.requisitionCode LIKE :prefix% " +
            "ORDER BY jr.id DESC")
    java.util.List<String> findLastCodeByPrefix(@Param("prefix") String prefix);
}