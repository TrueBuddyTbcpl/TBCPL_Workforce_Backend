package com.tbcpl.workforce.ttr.repository;

import com.tbcpl.workforce.ttr.entity.Ttr;
import com.tbcpl.workforce.ttr.entity.enums.TtrModuleType;
import com.tbcpl.workforce.ttr.entity.enums.TtrStatus;
import com.tbcpl.workforce.ttr.entity.enums.TtrType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TtrRepository extends JpaRepository<Ttr, Long> {

    Optional<Ttr> findByTtrNumber(String ttrNumber);

    boolean existsByTtrNumber(String ttrNumber);

    // ── Paginated list queries ────────────────────────────────────────────────

    Page<Ttr> findByIsActiveTrueAndParentTtrIsNull(Pageable pageable);

    Page<Ttr> findByIsActiveTrueAndDepartmentId(Long departmentId, Pageable pageable);

    Page<Ttr> findByIsActiveTrueAndStatus(TtrStatus status, Pageable pageable);

    Page<Ttr> findByIsActiveTrueAndDepartmentIdAndStatus(
            Long departmentId, TtrStatus status, Pageable pageable);

    Page<Ttr> findByIsActiveTrueAndAssignedEmpId(String empId, Pageable pageable);

    // ── Children ──────────────────────────────────────────────────────────────

    @Query("SELECT t FROM Ttr t WHERE t.parentTtr.id = :parentId AND t.isActive = true ORDER BY t.createdAt ASC")
    List<Ttr> findChildrenByParentId(@Param("parentId") Long parentId);

    @Query("SELECT COUNT(t) FROM Ttr t WHERE t.parentTtr.id = :parentId AND t.isActive = true AND t.status <> 'S5_CLOSED'")
    long countOpenChildrenByParentId(@Param("parentId") Long parentId);

    // ── TTR number generation ─────────────────────────────────────────────────

    @Query("SELECT MAX(CAST(SUBSTRING(t.ttrNumber, 4) AS int)) FROM Ttr t WHERE t.ttrNumber LIKE 'TTR%'")
    Integer findMaxTtrSequence();

    // ── Module-based lookup ───────────────────────────────────────────────────

    Page<Ttr> findByIsActiveTrueAndModuleTypeAndLinkedItemId(
            TtrModuleType moduleType, Long linkedItemId, Pageable pageable);

    // ── Dashboard metrics ─────────────────────────────────────────────────────

    long countByDepartmentIdAndIsActiveTrue(Long departmentId);

    long countByDepartmentIdAndStatusAndIsActiveTrue(Long departmentId, TtrStatus status);

    long countByIsActiveTrue();

    long countByStatusAndIsActiveTrue(TtrStatus status);

    // ── Creator / assigned queries ────────────────────────────────────────────

    @Query("SELECT t FROM Ttr t WHERE t.createdBy = :empId AND t.isActive = true ORDER BY t.createdAt DESC")
    Page<Ttr> findByCreatedBy(@Param("empId") String empId, Pageable pageable);

    // TtrRepository.java
    @Query("""
    SELECT t FROM Ttr t
    WHERE (:departmentId  IS NULL OR t.departmentId  = :departmentId)
      AND (:status        IS NULL OR t.status        = :status)
      AND (:assignedEmpId IS NULL OR t.assignedEmpId = :assignedEmpId)
      AND (:ttrType       IS NULL OR t.ttrType       = :ttrType)
      AND t.isActive = true
    ORDER BY t.createdAt DESC
    """)
    Page<Ttr> findAllWithFilters(
            @Param("departmentId")  Long departmentId,
            @Param("status")        TtrStatus status,
            @Param("assignedEmpId") String assignedEmpId,
            @Param("ttrType")       TtrType ttrType,
            Pageable pageable
    );
}