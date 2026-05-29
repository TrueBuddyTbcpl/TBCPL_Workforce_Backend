package com.tbcpl.workforce.hr.grievance.repository;

import com.tbcpl.workforce.hr.grievance.entity.HrGrievance;
import com.tbcpl.workforce.hr.grievance.entity.enums.GrievancePriority;
import com.tbcpl.workforce.hr.grievance.entity.enums.GrievanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HrGrievanceRepository extends JpaRepository<HrGrievance, Long> {

    // Fetch with remarks in one query — avoids N+1
    @Query("SELECT DISTINCT g FROM HrGrievance g " +
            "LEFT JOIN FETCH g.remarks r " +
            "WHERE g.id = :id AND g.isActive = true")
    Optional<HrGrievance> findByIdWithRemarks(@Param("id") Long id);

    Optional<HrGrievance> findByTicketNumberAndIsActiveTrue(String ticketNumber);

    boolean existsByTicketNumber(String ticketNumber);

    Page<HrGrievance> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<HrGrievance> findByEmpIdAndIsActiveTrueOrderByCreatedAtDesc(
            String empId, Pageable pageable);

    Page<HrGrievance> findByStatusAndIsActiveTrueOrderByCreatedAtDesc(
            GrievanceStatus status, Pageable pageable);

    Page<HrGrievance> findByPriorityAndIsActiveTrueOrderByCreatedAtDesc(
            GrievancePriority priority, Pageable pageable);

    Page<HrGrievance> findByAssignedToAndIsActiveTrueOrderByCreatedAtDesc(
            String assignedTo, Pageable pageable);

    // Count open grievances for dashboard KPI
    long countByStatusInAndIsActiveTrue(List<GrievanceStatus> statuses);

    // Last ticket number by prefix for sequence generation
    @Query("SELECT g.ticketNumber FROM HrGrievance g " +
            "WHERE g.ticketNumber LIKE :prefix% " +
            "ORDER BY g.id DESC")
    List<String> findLastTicketByPrefix(@Param("prefix") String prefix);
}