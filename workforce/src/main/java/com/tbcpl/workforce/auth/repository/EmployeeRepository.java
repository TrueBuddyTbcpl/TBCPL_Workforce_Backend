package com.tbcpl.workforce.auth.repository;

import com.tbcpl.workforce.auth.entity.Employee;
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
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmpId(String empId);

    Optional<Employee> findByEmailIgnoreCase(String email);

    @Query("SELECT e FROM Employee e " +
            "JOIN FETCH e.department " +
            "JOIN FETCH e.role " +
            "WHERE LOWER(e.email) = LOWER(:email)")
    Optional<Employee> findByEmailWithDepartmentAndRole(@Param("email") String email);

    @Query("SELECT e FROM Employee e " +
            "JOIN FETCH e.department " +
            "JOIN FETCH e.role " +
            "WHERE e.empId = :empId")
    Optional<Employee> findByEmpIdWithDepartmentAndRole(@Param("empId") String empId);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmpId(String empId);

    Page<Employee> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId AND e.isActive = true")
    Page<Employee> findByDepartmentId(@Param("departmentId") Long departmentId, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.role.id = :roleId AND e.isActive = true")
    Page<Employee> findByRoleId(@Param("roleId") Long roleId, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId " +
            "AND e.role.id = :roleId AND e.isActive = true")
    Page<Employee> findByDepartmentIdAndRoleId(
            @Param("departmentId") Long departmentId,
            @Param("roleId") Long roleId,
            Pageable pageable
    );

    @Query("SELECT e FROM Employee e WHERE " +
            "(LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.middleName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
            "e.isActive = true")
    Page<Employee> searchByName(@Param("searchTerm") String searchTerm, Pageable pageable);

    // ── NEW: Name-only search ─────────────────────────────────────────────────
    @Query("SELECT e FROM Employee e WHERE e.isActive = true AND (" +
            "LOWER(e.firstName) LIKE :name OR " +
            "LOWER(e.lastName)  LIKE :name OR " +
            "LOWER(e.empId)     LIKE :name OR " +
            "LOWER(CONCAT(e.firstName, ' ', e.lastName)) LIKE :name)")
    Page<Employee> findByName(
            @Param("name") String name,
            Pageable pageable
    );

    // ── NEW: Name + Department search ────────────────────────────────────────
    @Query("SELECT e FROM Employee e WHERE e.isActive = true AND " +
            "e.department.id = :deptId AND (" +
            "LOWER(e.firstName) LIKE :name OR " +
            "LOWER(e.lastName)  LIKE :name OR " +
            "LOWER(e.empId)     LIKE :name OR " +
            "LOWER(CONCAT(e.firstName, ' ', e.lastName)) LIKE :name)")
    Page<Employee> findByNameAndDepartment(
            @Param("name") String name,
            @Param("deptId") Long deptId,
            Pageable pageable
    );

    // ── NEW: Name + Role search ───────────────────────────────────────────────
    @Query("SELECT e FROM Employee e WHERE e.isActive = true AND " +
            "e.role.id = :roleId AND (" +
            "LOWER(e.firstName) LIKE :name OR " +
            "LOWER(e.lastName)  LIKE :name OR " +
            "LOWER(e.empId)     LIKE :name OR " +
            "LOWER(CONCAT(e.firstName, ' ', e.lastName)) LIKE :name)")
    Page<Employee> findByNameAndRole(
            @Param("name") String name,
            @Param("roleId") Long roleId,
            Pageable pageable
    );

    // ── NEW: Name + Department + Role search ─────────────────────────────────
    @Query("SELECT e FROM Employee e WHERE e.isActive = true AND " +
            "e.department.id = :deptId AND e.role.id = :roleId AND (" +
            "LOWER(e.firstName) LIKE :name OR " +
            "LOWER(e.lastName)  LIKE :name OR " +
            "LOWER(e.empId)     LIKE :name OR " +
            "LOWER(CONCAT(e.firstName, ' ', e.lastName)) LIKE :name)")
    Page<Employee> findByNameAndDepartmentAndRole(
            @Param("name") String name,
            @Param("deptId") Long deptId,
            @Param("roleId") Long roleId,
            Pageable pageable
    );

    @Query("SELECT MAX(CAST(SUBSTRING(e.empId, 6, 3) AS int)) FROM Employee e " +
            "WHERE e.empId LIKE :yearPrefix")
    Integer findMaxEmployeeNumberByYear(@Param("yearPrefix") String yearPrefix);

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

    @Query("SELECT e FROM Employee e " +
            "JOIN FETCH e.department d " +
            "JOIN FETCH e.role r " +
            "WHERE LOWER(d.departmentName) = LOWER(:departmentName) " +
            "AND e.isActive = true " +
            "ORDER BY e.firstName ASC")
    List<Employee> findActiveEmployeesByDepartmentName(
            @Param("departmentName") String departmentName
    );

    @Query("SELECT e FROM Employee e " +
            "JOIN FETCH e.role r " +
            "JOIN FETCH e.department d " +
            "WHERE UPPER(r.roleName) IN :roleNames AND e.isActive = true " +
            "ORDER BY e.firstName ASC")
    List<Employee> findActiveEmployeesByRoleNames(
            @Param("roleNames") List<String> roleNames
    );

    /**
     * Fetch all active employees by role name (case-insensitive).
     * Used by grnd_operation LOA module to populate FIELD_ASSOCIATE dropdown.
     */
    @Query("SELECT e FROM Employee e JOIN FETCH e.department JOIN FETCH e.role " +
            "WHERE UPPER(e.role.roleName) = UPPER(:roleName) AND e.isActive = true " +
            "ORDER BY e.firstName ASC")
    List<Employee> findActiveEmployeesByRoleName(@Param("roleName") String roleName);

    List<Employee> findByEmailIgnoreCaseIn(Collection<String> emails);
    List<Employee> findAllByEmpIdIn(List<String> empIds);

    long countByIsActiveTrue();

    long countByDepartmentIdAndIsActiveTrue(Long departmentId);

    long countByRoleIdAndIsActiveTrue(Long roleId);
}
