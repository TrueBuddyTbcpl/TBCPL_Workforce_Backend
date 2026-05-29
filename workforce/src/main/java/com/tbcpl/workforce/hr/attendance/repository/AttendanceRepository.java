package com.tbcpl.workforce.hr.attendance.repository;

import com.tbcpl.workforce.hr.attendance.entity.Attendance;
import com.tbcpl.workforce.hr.attendance.entity.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmpIdAndAttendanceDate(String empId, LocalDate date);

    boolean existsByEmpIdAndAttendanceDate(String empId, LocalDate date);

    Page<Attendance> findByEmpIdAndIsActiveTrue(String empId, Pageable pageable);

    // Monthly attendance for an employee
    @Query("SELECT a FROM Attendance a WHERE a.empId = :empId " +
            "AND YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month " +
            "AND a.isActive = true ORDER BY a.attendanceDate ASC")
    List<Attendance> findMonthlyAttendance(
            @Param("empId")  String empId,
            @Param("year")   int year,
            @Param("month")  int month
    );

    // Count by status for a given month (for payroll input)
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.empId = :empId " +
            "AND YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month " +
            "AND a.status = :status AND a.isActive = true")
    long countByEmpIdAndYearMonthAndStatus(
            @Param("empId")   String empId,
            @Param("year")    int year,
            @Param("month")   int month,
            @Param("status")  AttendanceStatus status
    );

    // All attendance in a date range — used for bulk reports
    @Query("SELECT a FROM Attendance a WHERE a.empId = :empId " +
            "AND a.attendanceDate BETWEEN :from AND :to " +
            "AND a.isActive = true ORDER BY a.attendanceDate ASC")
    List<Attendance> findByEmpIdAndDateRange(
            @Param("empId") String empId,
            @Param("from")  LocalDate from,
            @Param("to")    LocalDate to
    );

    Page<Attendance> findByIsActiveTrue(Pageable pageable);
}