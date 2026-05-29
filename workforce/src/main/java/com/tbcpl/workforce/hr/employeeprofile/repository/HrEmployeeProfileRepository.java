package com.tbcpl.workforce.hr.employeeprofile.repository;

import com.tbcpl.workforce.hr.employeeprofile.entity.HrEmployeeProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface HrEmployeeProfileRepository extends JpaRepository<HrEmployeeProfile, Long> {

    Optional<HrEmployeeProfile> findByEmpId(String empId);

    boolean existsByEmpId(String empId);

    boolean existsByPanNumber(String panNumber);

    boolean existsByAadharNumber(String aadharNumber);

    boolean existsByPanNumberAndIdNot(String panNumber, Long id);

    boolean existsByAadharNumberAndIdNot(String aadharNumber, Long id);

    Page<HrEmployeeProfile> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT p FROM HrEmployeeProfile p WHERE p.isActive = true AND " +
            "(:empId IS NULL OR LOWER(p.empId) LIKE LOWER(CONCAT('%', :empId, '%')))")
    Page<HrEmployeeProfile> searchByEmpId(
            @Param("empId") String empId,
            Pageable pageable
    );

    Optional<HrEmployeeProfile> findByEmpIdAndIsActiveTrue(String empId);

    List<HrEmployeeProfile> findByEmpIdInAndIsActiveTrue(Collection<String> empIds);
}