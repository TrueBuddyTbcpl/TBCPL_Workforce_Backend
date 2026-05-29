package com.tbcpl.workforce.hr.performance.repository;

import com.tbcpl.workforce.hr.performance.entity.HrEmployeeAppraisal;
import com.tbcpl.workforce.hr.performance.entity.enums.AppraisalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HrEmployeeAppraisalRepository
        extends JpaRepository<HrEmployeeAppraisal, Long> {

    // Fetch with cycle and KRA ratings to avoid N+1
    @Query("SELECT DISTINCT ea FROM HrEmployeeAppraisal ea " +
            "LEFT JOIN FETCH ea.kraRatings kr " +
            "LEFT JOIN FETCH kr.kraTemplate " +
            "WHERE ea.id = :id AND ea.isActive = true")
    Optional<HrEmployeeAppraisal> findByIdWithKraRatings(@Param("id") Long id);

    Optional<HrEmployeeAppraisal> findByEmpIdAndAppraisalCycleIdAndIsActiveTrue(
            String empId, Long cycleId);

    Page<HrEmployeeAppraisal> findByAppraisalCycleIdAndIsActiveTrueOrderByEmpIdAsc(
            Long cycleId, Pageable pageable);

    Page<HrEmployeeAppraisal> findByEmpIdAndIsActiveTrueOrderByCreatedAtDesc(
            String empId, Pageable pageable);

    Page<HrEmployeeAppraisal> findByStatusAndIsActiveTrueOrderByCreatedAtDesc(
            AppraisalStatus status, Pageable pageable);

    // Manager can see all direct reports pending review
    List<HrEmployeeAppraisal> findByManagerEmpIdAndStatusAndIsActiveTrue(
            String managerEmpId, AppraisalStatus status);

    boolean existsByEmpIdAndAppraisalCycleIdAndIsActiveTrue(
            String empId, Long cycleId);
}