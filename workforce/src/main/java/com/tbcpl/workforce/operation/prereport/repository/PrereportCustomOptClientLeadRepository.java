// PrereportCustomOptClientLeadRepository.java
package com.tbcpl.workforce.operation.prereport.repository;

import com.tbcpl.workforce.operation.prereport.entity.PrereportCustomOptClientLead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrereportCustomOptClientLeadRepository
        extends JpaRepository<PrereportCustomOptClientLead, Long> {

    // Existing
    List<PrereportCustomOptClientLead> findByStepNumberAndLeadTypeAndDeletedFalseOrderByCreatedAtAsc(
            Integer stepNumber, String leadType);

    // Existing
    List<PrereportCustomOptClientLead> findByFieldKeyAndLeadTypeAndDeletedFalseOrderByCreatedAtAsc(
            String fieldKey, String leadType);

    // ✅ ADD THIS — fetches ALL options for a leadType across ALL steps for PDF
    List<PrereportCustomOptClientLead> findByLeadTypeAndDeletedFalse(String leadType);
}