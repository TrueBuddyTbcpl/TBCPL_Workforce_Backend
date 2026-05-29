package com.tbcpl.workforce.hr.payroll.repository;

import com.tbcpl.workforce.hr.payroll.entity.HrSalaryComponent;
import com.tbcpl.workforce.hr.payroll.entity.enums.SalaryComponentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HrSalaryComponentRepository extends JpaRepository<HrSalaryComponent, Long> {

    List<HrSalaryComponent> findBySalaryStructureIdAndIsActiveTrueOrderByDisplayOrderAsc(
            Long structureId);

    List<HrSalaryComponent> findBySalaryStructureIdAndComponentTypeAndIsActiveTrue(
            Long structureId, SalaryComponentType componentType);

    void deleteAllBySalaryStructureId(Long structureId);
}