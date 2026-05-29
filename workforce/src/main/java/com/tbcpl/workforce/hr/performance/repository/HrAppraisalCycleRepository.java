package com.tbcpl.workforce.hr.performance.repository;

import com.tbcpl.workforce.hr.performance.entity.HrAppraisalCycle;
import com.tbcpl.workforce.hr.performance.entity.enums.AppraisalCycleType;
import com.tbcpl.workforce.hr.performance.entity.enums.AppraisalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HrAppraisalCycleRepository
        extends JpaRepository<HrAppraisalCycle, Long> {

    Page<HrAppraisalCycle> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<HrAppraisalCycle> findByStatusAndIsActiveTrueOrderByCreatedAtDesc(
            AppraisalStatus status, Pageable pageable);

    List<HrAppraisalCycle> findByAppraisalYearAndIsActiveTrueOrderByPeriodStartDateAsc(
            Integer year);

    List<HrAppraisalCycle> findByCycleTypeAndIsActiveTrueOrderByCreatedAtDesc(
            AppraisalCycleType type);
}