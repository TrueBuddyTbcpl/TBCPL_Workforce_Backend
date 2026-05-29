package com.tbcpl.workforce.hr.leave.repository;

import com.tbcpl.workforce.hr.leave.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    Optional<LeaveBalance> findByEmpIdAndLeaveTypeIdAndBalanceYear(
            String empId, Long leaveTypeId, Integer year);

    List<LeaveBalance> findByEmpIdAndBalanceYearAndIsActiveTrue(
            String empId, Integer year);

    List<LeaveBalance> findByEmpIdAndIsActiveTrue(String empId);

    boolean existsByEmpIdAndLeaveTypeIdAndBalanceYear(
            String empId, Long leaveTypeId, Integer year);

    // Fetch balance with leave type details — avoids N+1
    @Query("SELECT lb FROM LeaveBalance lb " +
            "JOIN FETCH lb.leaveType lt " +
            "WHERE lb.empId = :empId AND lb.balanceYear = :year " +
            "AND lb.isActive = true " +
            "ORDER BY lt.leaveTypeName ASC")
    List<LeaveBalance> findByEmpIdAndYearWithLeaveType(
            @Param("empId") String empId,
            @Param("year")  Integer year
    );

    // Used during year-end carry-forward processing
    @Query("SELECT lb FROM LeaveBalance lb " +
            "JOIN FETCH lb.leaveType lt " +
            "WHERE lb.balanceYear = :year AND lb.isActive = true " +
            "AND lt.isCarryForwardAllowed = true")
    List<LeaveBalance> findAllEligibleForCarryForward(@Param("year") Integer year);
}