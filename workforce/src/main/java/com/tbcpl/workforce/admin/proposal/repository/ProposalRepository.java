package com.tbcpl.workforce.admin.proposal.repository;

import com.tbcpl.workforce.admin.proposal.entity.Proposal;
import com.tbcpl.workforce.admin.proposal.entity.enums.ProposalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    @Query("SELECT p FROM Proposal p WHERE p.deleted = false ORDER BY p.createdAt DESC")
    Page<Proposal> findAllActive(Pageable pageable);

    @Query("SELECT p FROM Proposal p WHERE p.id = :id AND p.deleted = false")
    Optional<Proposal> findActiveById(@Param("id") Long id);

    @Query("SELECT p FROM Proposal p WHERE p.clientId = :clientId AND p.deleted = false ORDER BY p.createdAt DESC")
    Page<Proposal> findAllActiveByClientId(@Param("clientId") Long clientId, Pageable pageable);

    @Query("SELECT p FROM Proposal p WHERE p.status = :status AND p.deleted = false ORDER BY p.createdAt DESC")
    Page<Proposal> findAllActiveByStatus(@Param("status") ProposalStatus status, Pageable pageable);

    boolean existsByProposalCodeAndDeletedFalse(String proposalCode);

    long countByDeletedFalse();
}