package com.tbcpl.workforce.admin.proposal.repository;

import com.tbcpl.workforce.admin.proposal.entity.ProposalScopeOfWork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProposalScopeRepository extends JpaRepository<ProposalScopeOfWork, Long> {
    Optional<ProposalScopeOfWork> findByProposal_ProposalId(Long proposalId);
}
