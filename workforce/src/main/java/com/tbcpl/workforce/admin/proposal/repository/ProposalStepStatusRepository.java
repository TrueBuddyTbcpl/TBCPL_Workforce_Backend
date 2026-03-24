package com.tbcpl.workforce.admin.proposal.repository;

import com.tbcpl.workforce.admin.proposal.entity.ProposalStepStatus;
import com.tbcpl.workforce.admin.proposal.entity.enums.StepName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProposalStepStatusRepository extends JpaRepository<ProposalStepStatus, Long> {
    List<ProposalStepStatus> findByProposal_ProposalId(Long proposalId);
    Optional<ProposalStepStatus> findByProposal_ProposalIdAndStepName(Long proposalId, StepName stepName);
}
