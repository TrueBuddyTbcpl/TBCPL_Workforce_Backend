package com.tbcpl.workforce.admin.proposal.repository;

import com.tbcpl.workforce.admin.proposal.entity.ProposalProfessionalFee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProposalFeeRepository extends JpaRepository<ProposalProfessionalFee, Long> {
    Optional<ProposalProfessionalFee> findByProposal_ProposalId(Long proposalId);
}
