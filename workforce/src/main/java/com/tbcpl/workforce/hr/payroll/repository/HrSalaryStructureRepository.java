package com.tbcpl.workforce.hr.payroll.repository;

import com.tbcpl.workforce.hr.payroll.entity.HrSalaryStructure;
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
public interface HrSalaryStructureRepository extends JpaRepository<HrSalaryStructure, Long> {

    // Fetch current active structure with components (no N+1)
    @Query("SELECT DISTINCT ss FROM HrSalaryStructure ss " +
            "LEFT JOIN FETCH ss.components c " +
            "WHERE ss.empId = :empId " +
            "AND ss.isActive = true " +
            "AND ss.effectiveTo IS NULL " +
            "ORDER BY ss.effectiveFrom DESC")
    Optional<HrSalaryStructure> findCurrentStructureByEmpId(@Param("empId") String empId);

    // Fetch all structures for an employee (history)
    @Query("SELECT DISTINCT ss FROM HrSalaryStructure ss " +
            "LEFT JOIN FETCH ss.components " +
            "WHERE ss.empId = :empId AND ss.isActive = true " +
            "ORDER BY ss.effectiveFrom DESC")
    List<HrSalaryStructure> findAllByEmpIdWithComponents(@Param("empId") String empId);

    // Check if an active structure exists with no end date
    boolean existsByEmpIdAndEffectiveToIsNullAndIsActiveTrue(String empId);

    // For revision: find the currently open structure to close it
    @Query("SELECT ss FROM HrSalaryStructure ss " +
            "WHERE ss.empId = :empId " +
            "AND ss.effectiveTo IS NULL " +
            "AND ss.isActive = true")
    Optional<HrSalaryStructure> findOpenStructureByEmpId(@Param("empId") String empId);

    // ✅ Fix — use @Query to bypass derived method name parsing
    @Query("SELECT s FROM HrSalaryStructure s " +
            "WHERE s.empId = :empId AND s.isActive = true " +
            "ORDER BY s.createdAt DESC " +
            "LIMIT 1")
    Optional<HrSalaryStructure> findLatestByEmpId(@Param("empId") String empId);

    Page<HrSalaryStructure> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
}