package com.tbcpl.workforce.admin.proposal.repository;

import com.tbcpl.workforce.admin.proposal.entity.ProposalConfidentiality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProposalConfidentialityRepository extends JpaRepository<ProposalConfidentiality, Long> {
    Optional<ProposalConfidentiality> findByProposal_ProposalId(Long proposalId);
}
