package com.tbcpl.workforce.operation.finalreport.repository;

import com.tbcpl.workforce.operation.finalreport.entity.FinalReport;
import com.tbcpl.workforce.operation.finalreport.entity.enums.FinalReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinalReportRepository extends JpaRepository<FinalReport, Long> {

    Optional<FinalReport> findByCaseIdAndIsDeletedFalse(Long caseId);

    Optional<FinalReport> findByReportNumberAndIsDeletedFalse(String reportNumber);

    boolean existsByCaseIdAndIsDeletedFalse(Long caseId);

    @Query("SELECT r FROM FinalReport r WHERE r.isDeleted = false ORDER BY r.createdAt DESC")
    Page<FinalReport> findAllActive(Pageable pageable);

    @Query("SELECT r FROM FinalReport r WHERE r.reportStatus = :status AND r.isDeleted = false ORDER BY r.createdAt DESC")
    Page<FinalReport> findByStatus(@Param("status") FinalReportStatus status, Pageable pageable);

    @Query("SELECT COUNT(r) FROM FinalReport r WHERE YEAR(r.createdAt) = :year")
    Long countByYear(@Param("year") int year);
}
