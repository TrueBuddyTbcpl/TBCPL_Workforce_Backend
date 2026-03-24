package com.tbcpl.workforce.admin.proposal.repository;

import com.tbcpl.workforce.admin.proposal.entity.ProposalBackground;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProposalBackgroundRepository extends JpaRepository<ProposalBackground, Long> {
    Optional<ProposalBackground> findByProposal_ProposalId(Long proposalId);
}
