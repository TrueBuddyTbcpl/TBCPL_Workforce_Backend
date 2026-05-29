package com.tbcpl.workforce.hr.grievance.repository;

import com.tbcpl.workforce.hr.grievance.entity.HrDisciplinaryAction;
import com.tbcpl.workforce.hr.grievance.entity.enums.DisciplinaryActionType;
import com.tbcpl.workforce.hr.grievance.entity.enums.DisciplinaryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HrDisciplinaryActionRepository
        extends JpaRepository<HrDisciplinaryAction, Long> {

    Optional<HrDisciplinaryAction> findByCaseReferenceAndIsActiveTrue(String caseReference);

    boolean existsByCaseReference(String caseReference);

    Page<HrDisciplinaryAction> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<HrDisciplinaryAction> findByEmpIdAndIsActiveTrueOrderByCreatedAtDesc(
            String empId, Pageable pageable);

    Page<HrDisciplinaryAction> findByStatusAndIsActiveTrueOrderByCreatedAtDesc(
            DisciplinaryStatus status, Pageable pageable);

    Page<HrDisciplinaryAction> findByActionTypeAndIsActiveTrueOrderByCreatedAtDesc(
            DisciplinaryActionType actionType, Pageable pageable);

    // All active disciplinary cases for an employee (for HR record check)
    List<HrDisciplinaryAction> findByEmpIdAndIsActiveTrueOrderByCreatedAtDesc(String empId);

    // Count active cases (not closed/withdrawn) — used for risk assessment
    @Query("SELECT COUNT(d) FROM HrDisciplinaryAction d " +
            "WHERE d.empId = :empId AND d.isActive = true " +
            "AND d.status NOT IN ('WITHDRAWN', 'CLOSED')")
    long countActiveCasesByEmpId(@Param("empId") String empId);

    // Last case reference for sequence generation
    @Query("SELECT d.caseReference FROM HrDisciplinaryAction d " +
            "WHERE d.caseReference LIKE :prefix% " +
            "ORDER BY d.id DESC")
    List<String> findLastCaseRefByPrefix(@Param("prefix") String prefix);
}