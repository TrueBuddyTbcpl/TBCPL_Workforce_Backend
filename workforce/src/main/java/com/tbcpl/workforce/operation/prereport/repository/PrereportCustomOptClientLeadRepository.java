package com.tbcpl.workforce.operation.prereport.repository;

import com.tbcpl.workforce.operation.prereport.entity.PrereportCustomOptClientLead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrereportCustomOptClientLeadRepository
        extends JpaRepository<PrereportCustomOptClientLead, Long> {

    // Replace the existing method with this lead_type-aware version:
    List<PrereportCustomOptClientLead> findByStepNumberAndLeadTypeAndDeletedFalseOrderByCreatedAtAsc(
            Integer stepNumber, String leadType);

}
