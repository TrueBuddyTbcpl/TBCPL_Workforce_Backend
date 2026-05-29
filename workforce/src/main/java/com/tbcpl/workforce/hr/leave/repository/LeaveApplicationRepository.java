package com.tbcpl.workforce.hr.leave.repository;

import com.tbcpl.workforce.hr.leave.entity.LeaveApplication;
import com.tbcpl.workforce.hr.attendance.entity.enums.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {

    Page<LeaveApplication> findByEmpIdAndIsActiveTrueOrderByCreatedAtDesc(
            String empId, Pageable pageable);

    Page<LeaveApplication> findByStatusAndIsActiveTrueOrderByCreatedAtDesc(
            LeaveStatus status, Pageable pageable);

    Page<LeaveApplication> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    // Check for overlapping leave dates for the same employee
    @Query("SELECT COUNT(la) > 0 FROM LeaveApplication la " +
            "WHERE la.empId = :empId " +
            "AND la.status NOT IN ('REJECTED', 'CANCELLED') " +
            "AND la.isActive = true " +
            "AND la.fromDate <= :toDate AND la.toDate >= :fromDate")
    boolean hasOverlappingLeave(
            @Param("empId")    String empId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate")   LocalDate toDate
    );

    // Exclude current application when checking overlaps on update
    @Query("SELECT COUNT(la) > 0 FROM LeaveApplication la " +
            "WHERE la.empId = :empId AND la.id <> :excludeId " +
            "AND la.status NOT IN ('REJECTED', 'CANCELLED') " +
            "AND la.isActive = true " +
            "AND la.fromDate <= :toDate AND la.toDate >= :fromDate")
    boolean hasOverlappingLeaveExcluding(
            @Param("empId")     String empId,
            @Param("fromDate")  LocalDate fromDate,
            @Param("toDate")    LocalDate toDate,
            @Param("excludeId") Long excludeId
    );

    // Fetch with leave type to avoid N+1
    @Query("SELECT la FROM LeaveApplication la " +
            "JOIN FETCH la.leaveType lt " +
            "WHERE la.empId = :empId AND la.isActive = true " +
            "ORDER BY la.createdAt DESC")
    List<LeaveApplication> findByEmpIdWithLeaveType(@Param("empId") String empId);

    // Pending applications for a specific employee
    List<LeaveApplication> findByEmpIdAndStatusAndIsActiveTrue(
            String empId, LeaveStatus status);

    // Count pending applications across all employees (dashboard metric)
    long countByStatusAndIsActiveTrue(LeaveStatus status);
}