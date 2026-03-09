package com.tbcpl.workforce.operation.cases.repository;

import com.tbcpl.workforce.operation.cases.entity.CaseDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CaseDocumentRepository extends JpaRepository<CaseDocument, Long> {

    @Query("SELECT d FROM CaseDocument d WHERE d.caseEntity.id = :caseId ORDER BY d.uploadedAt DESC")
    List<CaseDocument> findByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT COUNT(d) FROM CaseDocument d WHERE d.caseEntity.id = :caseId")
    long countByCaseId(@Param("caseId") Long caseId);
}
