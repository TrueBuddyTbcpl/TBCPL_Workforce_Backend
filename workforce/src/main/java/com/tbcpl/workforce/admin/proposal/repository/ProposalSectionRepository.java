package com.tbcpl.workforce.admin.proposal.repository;

import com.tbcpl.workforce.admin.proposal.entity.ProposalSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProposalSectionRepository extends JpaRepository<ProposalSection, Long> {

    @Query("""
            SELECT s FROM ProposalSection s
            WHERE s.proposal.id = :proposalId
            ORDER BY s.displayOrder ASC
            """)
    List<ProposalSection> findAllByProposalId(@Param("proposalId") Long proposalId);

    @Query("""
            SELECT s FROM ProposalSection s
            WHERE s.id = :sectionId
            AND s.proposal.id = :proposalId
            """)
    Optional<ProposalSection> findByIdAndProposalId(
            @Param("sectionId")   Long sectionId,
            @Param("proposalId")  Long proposalId
    );

    @Query("""
            SELECT COALESCE(MAX(s.displayOrder), 0)
            FROM ProposalSection s
            WHERE s.proposal.id = :proposalId
            """)
    int findMaxDisplayOrder(@Param("proposalId") Long proposalId);

    @Modifying
    @Query("""
            UPDATE ProposalSection s
            SET s.displayOrder = :displayOrder
            WHERE s.id = :sectionId
            """)
    void updateDisplayOrder(
            @Param("sectionId")    Long sectionId,
            @Param("displayOrder") int  displayOrder
    );
}