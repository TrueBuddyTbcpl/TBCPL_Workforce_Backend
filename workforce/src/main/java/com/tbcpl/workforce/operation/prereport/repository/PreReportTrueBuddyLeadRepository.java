package com.tbcpl.workforce.operation.prereport.repository;

import com.tbcpl.workforce.operation.prereport.entity.PreReportTrueBuddyLead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreReportTrueBuddyLeadRepository extends JpaRepository<PreReportTrueBuddyLead, Long> {

    Optional<PreReportTrueBuddyLead> findByPrereportId(Long prereportId);

    boolean existsByPrereportId(Long prereportId);

    void deleteByPrereportId(Long prereportId);
}
