package com.tbcpl.workforce.operation.prereport.repository;

import com.tbcpl.workforce.operation.prereport.entity.PreReportClientLead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreReportClientLeadRepository extends JpaRepository<PreReportClientLead, Long> {

    Optional<PreReportClientLead> findByPrereportId(Long prereportId);

    boolean existsByPrereportId(Long prereportId);

    void deleteByPrereportId(Long prereportId);
}
