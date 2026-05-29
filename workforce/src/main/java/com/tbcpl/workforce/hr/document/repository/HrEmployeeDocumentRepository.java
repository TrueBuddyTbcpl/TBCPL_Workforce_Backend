package com.tbcpl.workforce.hr.document.repository;

import com.tbcpl.workforce.hr.document.entity.HrEmployeeDocument;
import com.tbcpl.workforce.hr.document.entity.enums.DocumentStatus;
import com.tbcpl.workforce.hr.document.entity.enums.DocumentType;
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
public interface HrEmployeeDocumentRepository
        extends JpaRepository<HrEmployeeDocument, Long> {

    List<HrEmployeeDocument> findByEmpIdAndIsActiveTrueOrderByCreatedAtDesc(String empId);

    Page<HrEmployeeDocument> findByStatusAndIsActiveTrueOrderByCreatedAtDesc(
            DocumentStatus status, Pageable pageable);

    Page<HrEmployeeDocument> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Optional<HrEmployeeDocument> findByEmpIdAndDocumentTypeAndIsActiveTrue(
            String empId, DocumentType documentType);

    // Documents expiring within given date range — for HR alerts
    @Query("SELECT d FROM HrEmployeeDocument d " +
            "WHERE d.expiryDate BETWEEN :from AND :to " +
            "AND d.isActive = true " +
            "AND d.status = 'VERIFIED' " +
            "ORDER BY d.expiryDate ASC")
    List<HrEmployeeDocument> findDocumentsExpiringBetween(
            @Param("from") LocalDate from,
            @Param("to")   LocalDate to
    );

    // ✅ Fix — use 'createdAt' which exists in the entity
    Page<HrEmployeeDocument> findByEmpIdAndIsActiveTrueOrderByCreatedAtDesc(
            String empId, Pageable pageable);

    // Count pending verifications
    long countByStatusAndIsActiveTrue(DocumentStatus status);

    // Check if mandatory document already uploaded
    boolean existsByEmpIdAndDocumentTypeAndIsActiveTrue(
            String empId, DocumentType documentType);
}