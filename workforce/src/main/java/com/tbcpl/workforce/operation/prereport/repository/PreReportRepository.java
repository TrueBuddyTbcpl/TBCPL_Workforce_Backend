package com.tbcpl.workforce.operation.prereport.repository;

import com.tbcpl.workforce.operation.prereport.entity.PreReport;
import com.tbcpl.workforce.operation.prereport.entity.enums.LeadType;
import com.tbcpl.workforce.operation.prereport.entity.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreReportRepository extends JpaRepository<PreReport, Long> {

    Optional<PreReport> findByReportIdAndIsDeletedFalse(String reportId);

    @Query("SELECT p FROM PreReport p WHERE p.isDeleted = false ORDER BY p.createdAt DESC")
    Page<PreReport> findAllActiveReports(Pageable pageable);


    @Query("SELECT p FROM PreReport p WHERE p.client.clientId = :clientId AND p.isDeleted = false ORDER BY p.createdAt DESC")
    Page<PreReport> findByClientId(@Param("clientId") Long clientId, Pageable pageable);

    @Query("SELECT p FROM PreReport p WHERE p.leadType = :leadType AND p.isDeleted = false ORDER BY p.createdAt DESC")
    Page<PreReport> findByLeadType(@Param("leadType") LeadType leadType, Pageable pageable);

    @Query("SELECT p FROM PreReport p WHERE p.reportStatus = :status AND p.isDeleted = false ORDER BY p.createdAt DESC")
    Page<PreReport> findByReportStatus(@Param("status") ReportStatus status, Pageable pageable);

    @Query("SELECT p FROM PreReport p WHERE p.createdBy = :createdBy AND p.isDeleted = false ORDER BY p.createdAt DESC")
    Page<PreReport> findByCreatedBy(@Param("createdBy") String createdBy, Pageable pageable);

    @Query("SELECT COUNT(p) FROM PreReport p WHERE YEAR(p.createdAt) = :year")
    Long countByYear(@Param("year") int year);

    boolean existsByReportId(String reportId);
}
