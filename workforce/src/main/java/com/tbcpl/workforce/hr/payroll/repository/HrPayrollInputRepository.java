package com.tbcpl.workforce.hr.payroll.repository;

import com.tbcpl.workforce.hr.payroll.entity.HrPayrollInput;
import com.tbcpl.workforce.hr.payroll.entity.enums.PayrollStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HrPayrollInputRepository extends JpaRepository<HrPayrollInput, Long> {

    List<HrPayrollInput> findByEmpIdAndPayrollMonthAndPayrollYearAndIsActiveTrue(
            String empId, Integer month, Integer year);

    Page<HrPayrollInput> findByPayrollMonthAndPayrollYearAndIsActiveTrueOrderByEmpIdAsc(
            Integer month, Integer year, Pageable pageable);

    Page<HrPayrollInput> findByEmpIdAndIsActiveTrueOrderByPayrollYearDescPayrollMonthDesc(
            String empId, Pageable pageable);

    Page<HrPayrollInput> findByStatusAndIsActiveTrueOrderByCreatedAtDesc(
            PayrollStatus status, Pageable pageable);

    // All draft inputs for a given month+year — used for bulk submission to Accounts
    @Query("SELECT pi FROM HrPayrollInput pi " +
            "WHERE pi.payrollMonth = :month AND pi.payrollYear = :year " +
            "AND pi.status = 'DRAFT' AND pi.isActive = true " +
            "ORDER BY pi.empId ASC")
    List<HrPayrollInput> findDraftInputsForMonth(
            @Param("month") Integer month,
            @Param("year")  Integer year
    );


    boolean existsByEmpIdAndPayrollMonthAndPayrollYearAndInputTypeAndIsActiveTrue(
            String empId, Integer month, Integer year,
            com.tbcpl.workforce.hr.payroll.entity.enums.PayrollInputType inputType);
}