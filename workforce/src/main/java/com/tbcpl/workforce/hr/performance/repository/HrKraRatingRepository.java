package com.tbcpl.workforce.hr.performance.repository;

import com.tbcpl.workforce.hr.performance.entity.HrKraRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HrKraRatingRepository extends JpaRepository<HrKraRating, Long> {

    List<HrKraRating> findByEmployeeAppraisalIdAndIsActiveTrueOrderByIdAsc(
            Long appraisalId);

    void deleteAllByEmployeeAppraisalId(Long appraisalId);
}