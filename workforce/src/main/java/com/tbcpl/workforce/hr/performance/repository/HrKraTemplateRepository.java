package com.tbcpl.workforce.hr.performance.repository;

import com.tbcpl.workforce.hr.performance.entity.HrKraTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HrKraTemplateRepository
        extends JpaRepository<HrKraTemplate, Long> {

    Page<HrKraTemplate> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    // Fetch KRAs relevant to a designation and/or department
    List<HrKraTemplate> findByDesignationAndIsActiveTrueAndStatusOrderByWeightageDesc(
            String designation,
            com.tbcpl.workforce.hr.performance.entity.enums.KraStatus status);

    List<HrKraTemplate> findByDepartmentAndIsActiveTrueAndStatusOrderByWeightageDesc(
            String department,
            com.tbcpl.workforce.hr.performance.entity.enums.KraStatus status);

    List<HrKraTemplate> findByDepartmentIsNullAndDesignationIsNullAndIsActiveTrueOrderByWeightageDesc();
}