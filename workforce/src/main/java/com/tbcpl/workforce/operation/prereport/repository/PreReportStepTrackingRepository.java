package com.tbcpl.workforce.operation.prereport.repository;

import com.tbcpl.workforce.operation.prereport.entity.PreReportStepTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreReportStepTrackingRepository extends JpaRepository<PreReportStepTracking, Long> {

    List<PreReportStepTracking> findByPrereportIdOrderByStepNumberAsc(Long prereportId);

    Optional<PreReportStepTracking> findByPrereportIdAndStepNumber(Long prereportId, Integer stepNumber);

    void deleteByPrereportId(Long prereportId);
}
