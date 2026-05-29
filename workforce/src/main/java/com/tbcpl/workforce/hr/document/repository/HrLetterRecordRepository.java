package com.tbcpl.workforce.hr.document.repository;

import com.tbcpl.workforce.hr.document.entity.HrLetterRecord;
import com.tbcpl.workforce.hr.document.entity.enums.LetterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HrLetterRecordRepository
        extends JpaRepository<HrLetterRecord, Long> {

    Optional<HrLetterRecord> findByReferenceNumberAndIsActiveTrue(String referenceNumber);

    boolean existsByReferenceNumber(String referenceNumber);

    List<HrLetterRecord> findByEmpIdAndIsActiveTrueOrderByIssuedDateDesc(String empId);

    Page<HrLetterRecord> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<HrLetterRecord> findByLetterTypeAndIsActiveTrueOrderByCreatedAtDesc(
            LetterType letterType, Pageable pageable);

    Page<HrLetterRecord> findByEmpIdAndIsActiveTrueOrderByCreatedAtDesc(
            String empId, Pageable pageable);

    // Last reference number for given prefix — for sequence generation
    @Query("SELECT lr.referenceNumber FROM HrLetterRecord lr " +
            "WHERE lr.referenceNumber LIKE :prefix% " +
            "ORDER BY lr.id DESC")
    List<String> findLastReferenceByPrefix(@Param("prefix") String prefix);
}