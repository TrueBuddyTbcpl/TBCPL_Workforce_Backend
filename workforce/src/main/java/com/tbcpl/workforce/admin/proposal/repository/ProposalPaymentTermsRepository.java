package com.tbcpl.workforce.admin.proposal.repository;

import com.tbcpl.workforce.admin.proposal.entity.ProposalPaymentTerms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProposalPaymentTermsRepository extends JpaRepository<ProposalPaymentTerms, Long> {
    Optional<ProposalPaymentTerms> findByProposal_ProposalId(Long proposalId);
}
