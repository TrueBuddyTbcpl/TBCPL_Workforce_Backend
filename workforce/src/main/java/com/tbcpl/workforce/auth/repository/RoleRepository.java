package com.tbcpl.workforce.auth.repository;

import com.tbcpl.workforce.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Role entity
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by name (case-insensitive)
     */
    Optional<Role> findByRoleNameIgnoreCase(String roleName);

    /**
     * Check if role name exists (case-insensitive)
     */
    boolean existsByRoleNameIgnoreCase(String roleName);

    /**
     * Find all active roles
     */
    List<Role> findByIsActiveTrue();

    /**
     * Find all roles ordered by name
     */
    List<Role> findAllByOrderByRoleNameAsc();

    /**
     * Count employees assigned to a role
     */
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.role.id = :roleId")
    long countEmployeesByRoleId(Long roleId);

    /**
     * Check if role is assigned to any employee
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Employee e WHERE e.role.id = :roleId")
    boolean isRoleInUse(Long roleId);
}
