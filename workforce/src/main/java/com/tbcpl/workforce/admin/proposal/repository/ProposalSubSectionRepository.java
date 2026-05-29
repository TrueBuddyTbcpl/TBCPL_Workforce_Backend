package com.tbcpl.workforce.admin.proposal.repository;

import com.tbcpl.workforce.admin.proposal.entity.ProposalSubSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProposalSubSectionRepository extends JpaRepository<ProposalSubSection, Long> {

    @Query("""
            SELECT ss FROM ProposalSubSection ss
            WHERE ss.section.id = :sectionId
            ORDER BY ss.displayOrder ASC
            """)
    List<ProposalSubSection> findAllBySectionId(@Param("sectionId") Long sectionId);

    @Query("""
            SELECT ss FROM ProposalSubSection ss
            WHERE ss.id = :subSectionId
            AND ss.section.id = :sectionId
            """)
    Optional<ProposalSubSection> findByIdAndSectionId(
            @Param("subSectionId") Long subSectionId,
            @Param("sectionId")    Long sectionId
    );

    @Query("""
            SELECT COALESCE(MAX(ss.displayOrder), 0)
            FROM ProposalSubSection ss
            WHERE ss.section.id = :sectionId
            """)
    int findMaxDisplayOrder(@Param("sectionId") Long sectionId);

    @Modifying
    @Query("""
            UPDATE ProposalSubSection ss
            SET ss.displayOrder = :displayOrder
            WHERE ss.id = :subSectionId
            """)
    void updateDisplayOrder(
            @Param("subSectionId")  Long subSectionId,
            @Param("displayOrder")  int  displayOrder
    );
}