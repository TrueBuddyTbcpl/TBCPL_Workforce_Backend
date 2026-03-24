package com.tbcpl.workforce.admin.proposal.repository;

import com.tbcpl.workforce.admin.proposal.entity.Proposal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    @Query("SELECT p FROM Proposal p WHERE p.deleted = false ORDER BY p.createdAt DESC")
    Page<Proposal> findAllActive(Pageable pageable);

    @Query("SELECT p FROM Proposal p WHERE p.proposalId = :id AND p.deleted = false")
    Optional<Proposal> findActiveById(@Param("id") Long id);

    @Query("SELECT MAX(p.proposalCode) FROM Proposal p WHERE p.proposalCode LIKE :prefix%")
    Optional<String> findMaxProposalCodeByPrefix(@Param("prefix") String prefix);

    boolean existsByProposalCodeAndDeletedFalse(String proposalCode);
}
