package com.tbcpl.workforce.hr.recruitment.repository;

import com.tbcpl.workforce.hr.recruitment.entity.HrCandidate;
import com.tbcpl.workforce.hr.recruitment.entity.enums.OfferStatus;
import com.tbcpl.workforce.hr.recruitment.entity.enums.RecruitmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HrCandidateRepository extends JpaRepository<HrCandidate, Long> {

    Page<HrCandidate> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<HrCandidate> findByJobRequisitionIdAndIsActiveTrueOrderByCreatedAtDesc(
            Long requisitionId, Pageable pageable);

    Page<HrCandidate> findByStatusAndIsActiveTrueOrderByCreatedAtDesc(
            RecruitmentStatus status, Pageable pageable);

    Page<HrCandidate> findByOfferStatusAndIsActiveTrueOrderByCreatedAtDesc(
            OfferStatus offerStatus, Pageable pageable);

    boolean existsByEmailAndJobRequisitionIdAndIsActiveTrue(
            String email, Long requisitionId);

    // Check duplicate candidate for same requisition
    boolean existsByEmailAndJobRequisitionId(String email, Long requisitionId);

    // Fetch with requisition to avoid N+1
    @Query("SELECT c FROM HrCandidate c " +
            "JOIN FETCH c.jobRequisition jr " +
            "WHERE c.id = :id AND c.isActive = true")
    java.util.Optional<HrCandidate> findByIdWithRequisition(@Param("id") Long id);

    // Pipeline funnel counts per requisition
    @Query("SELECT c.status, COUNT(c) FROM HrCandidate c " +
            "WHERE c.jobRequisition.id = :requisitionId AND c.isActive = true " +
            "GROUP BY c.status")
    List<Object[]> countCandidatesByStatusForRequisition(
            @Param("requisitionId") Long requisitionId);
}