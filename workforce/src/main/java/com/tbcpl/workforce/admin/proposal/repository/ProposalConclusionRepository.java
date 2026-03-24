package com.tbcpl.workforce.admin.proposal.repository;

import com.tbcpl.workforce.admin.proposal.entity.ProposalConclusion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProposalConclusionRepository extends JpaRepository<ProposalConclusion, Long> {
    Optional<ProposalConclusion> findByProposal_ProposalId(Long proposalId);
}
