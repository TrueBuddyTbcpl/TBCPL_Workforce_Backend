package com.tbcpl.workforce.auth.repository;

import com.tbcpl.workforce.auth.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Employee entity
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Find employee by empId (e.g., 2026/001)
     */
    Optional<Employee> findByEmpId(String empId);

    /**
     * Find employee by email (case-insensitive)
     */
    Optional<Employee> findByEmailIgnoreCase(String email);

    /**
     * Find employee by email with department and role eagerly fetched
     * USE THIS FOR JWT AUTHENTICATION to avoid LazyInitializationException
     */
    @Query("SELECT e FROM Employee e " +
            "JOIN FETCH e.department " +
            "JOIN FETCH e.role " +
            "WHERE LOWER(e.email) = LOWER(:email)")
    Optional<Employee> findByEmailWithDepartmentAndRole(@Param("email") String email);

    /**
     * Find employee by empId with department and role eagerly fetched
     */
    @Query("SELECT e FROM Employee e " +
            "JOIN FETCH e.department " +
            "JOIN FETCH e.role " +
            "WHERE e.empId = :empId")
    Optional<Employee> findByEmpIdWithDepartmentAndRole(@Param("empId") String empId);

    /**
     * Check if email exists (case-insensitive)
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Check if empId exists
     */
    boolean existsByEmpId(String empId);

    /**
     * Find all active employees
     */
    Page<Employee> findByIsActiveTrue(Pageable pageable);

    /**
     * Find employees by department
     */
    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId AND e.isActive = true")
    Page<Employee> findByDepartmentId(@Param("departmentId") Long departmentId, Pageable pageable);

    /**
     * Find employees by role
     */
    @Query("SELECT e FROM Employee e WHERE e.role.id = :roleId AND e.isActive = true")
    Page<Employee> findByRoleId(@Param("roleId") Long roleId, Pageable pageable);

    /**
     * Find employees by department and role
     */
    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId AND e.role.id = :roleId AND e.isActive = true")
    Page<Employee> findByDepartmentIdAndRoleId(
            @Param("departmentId") Long departmentId,
            @Param("roleId") Long roleId,
            Pageable pageable
    );

    /**
     * Search employees by name (first, middle, or last name)
     */
    @Query("SELECT e FROM Employee e WHERE " +
            "(LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.middleName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
            "e.isActive = true")
    Page<Employee> searchByName(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Get the highest employee number for current year
     * Used for generating next empId
     */
    @Query("SELECT MAX(CAST(SUBSTRING(e.empId, 6, 3) AS int)) FROM Employee e WHERE e.empId LIKE :yearPrefix")
    Integer findMaxEmployeeNumberByYear(@Param("yearPrefix") String yearPrefix);

    /**
     * Find employees with password expiring soon (within X days)
     */
    @Query("SELECT e FROM Employee e WHERE " +
            "e.lastPasswordChangeDate IS NOT NULL AND " +
            "DATEDIFF(CURRENT_DATE, e.lastPasswordChangeDate) >= :warningDays AND " +
            "DATEDIFF(CURRENT_DATE, e.lastPasswordChangeDate) < :expiryDays AND " +
            "e.isActive = true")
    Page<Employee> findEmployeesWithPasswordExpiringSoon(
            @Param("warningDays") int warningDays,
            @Param("expiryDays") int expiryDays,
            Pageable pageable
    );

    /**
     * Count active employees
     */
    long countByIsActiveTrue();

    /**
     * Count employees by department
     */
    long countByDepartmentIdAndIsActiveTrue(Long departmentId);

    /**
     * Count employees by role
     */
    long countByRoleIdAndIsActiveTrue(Long roleId);
}
