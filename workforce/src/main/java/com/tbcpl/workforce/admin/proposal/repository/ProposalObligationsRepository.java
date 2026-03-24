package com.tbcpl.workforce.admin.proposal.repository;

import com.tbcpl.workforce.admin.proposal.entity.ProposalSpecialObligations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProposalObligationsRepository extends JpaRepository<ProposalSpecialObligations, Long> {
    Optional<ProposalSpecialObligations> findByProposal_ProposalId(Long proposalId);
}
