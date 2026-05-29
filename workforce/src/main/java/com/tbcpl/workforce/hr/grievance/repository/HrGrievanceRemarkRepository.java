package com.tbcpl.workforce.hr.grievance.repository;

import com.tbcpl.workforce.hr.grievance.entity.HrGrievanceRemark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HrGrievanceRemarkRepository
        extends JpaRepository<HrGrievanceRemark, Long> {

    // All public remarks for employee view (exclude internal)
    List<HrGrievanceRemark> findByGrievanceIdAndIsInternalFalseAndIsActiveTrueOrderByCreatedAtAsc(
            Long grievanceId);

    // All remarks for HR view (include internal)
    List<HrGrievanceRemark> findByGrievanceIdAndIsActiveTrueOrderByCreatedAtAsc(
            Long grievanceId);
}