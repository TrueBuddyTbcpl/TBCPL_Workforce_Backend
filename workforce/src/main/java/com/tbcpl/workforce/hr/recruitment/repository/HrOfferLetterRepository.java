package com.tbcpl.workforce.hr.recruitment.repository;

import com.tbcpl.workforce.hr.recruitment.entity.HrOfferLetter;
import com.tbcpl.workforce.hr.recruitment.entity.enums.OfferLetterStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HrOfferLetterRepository extends JpaRepository<HrOfferLetter, Long> {

    // Fetch with candidate and job requisition — avoids N+1
    @Query("SELECT o FROM HrOfferLetter o " +
            "JOIN FETCH o.candidate c " +
            "JOIN FETCH o.jobRequisition r " +
            "WHERE o.id = :id AND o.isActive = true")
    Optional<HrOfferLetter> findByIdWithDetails(@Param("id") Long id);

    Page<HrOfferLetter> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<HrOfferLetter> findByStatusAndIsActiveTrueOrderByCreatedAtDesc(
            OfferLetterStatus status, Pageable pageable);

    Page<HrOfferLetter> findByCandidateIdAndIsActiveTrueOrderByCreatedAtDesc(
            Long candidateId, Pageable pageable);

    Page<HrOfferLetter> findByJobRequisitionIdAndIsActiveTrueOrderByCreatedAtDesc(
            Long jobRequisitionId, Pageable pageable);

    boolean existsByCandidateIdAndStatusAndIsActiveTrue(
            Long candidateId, OfferLetterStatus status);
}