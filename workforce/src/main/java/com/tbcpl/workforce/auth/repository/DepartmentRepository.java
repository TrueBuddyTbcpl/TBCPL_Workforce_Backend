package com.tbcpl.workforce.auth.repository;

import com.tbcpl.workforce.auth.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Department entity
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * Find department by name (case-insensitive)
     */
    Optional<Department> findByDepartmentNameIgnoreCase(String departmentName);

    /**
     * Check if department name exists (case-insensitive)
     */
    boolean existsByDepartmentNameIgnoreCase(String departmentName);

    /**
     * Find all active departments
     */
    List<Department> findByIsActiveTrue();

    /**
     * Find all departments ordered by name
     */
    List<Department> findAllByOrderByDepartmentNameAsc();

    /**
     * Count employees assigned to a department
     */
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId")
    long countEmployeesByDepartmentId(Long departmentId);

    /**
     * Check if department is assigned to any employee
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Employee e WHERE e.department.id = :departmentId")
    boolean isDepartmentInUse(Long departmentId);
}
