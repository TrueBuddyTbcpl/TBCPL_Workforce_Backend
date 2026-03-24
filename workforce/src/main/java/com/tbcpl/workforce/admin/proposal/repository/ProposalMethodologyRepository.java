package com.tbcpl.workforce.admin.proposal.repository;

import com.tbcpl.workforce.admin.proposal.entity.ProposalApproachMethodology;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProposalMethodologyRepository extends JpaRepository<ProposalApproachMethodology, Long> {
    Optional<ProposalApproachMethodology> findByProposal_ProposalId(Long proposalId);
}
