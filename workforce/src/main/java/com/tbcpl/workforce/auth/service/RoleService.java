package com.tbcpl.workforce.auth.service;

import com.tbcpl.workforce.auth.dto.request.RoleRequest;
import com.tbcpl.workforce.auth.dto.response.RoleResponse;
import com.tbcpl.workforce.auth.entity.Role;
import com.tbcpl.workforce.auth.repository.RoleRepository;
import com.tbcpl.workforce.common.exception.DuplicateResourceException;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.constants.ValidationMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Role management
 * Only ADMIN can create/update/delete roles
 * HR can view roles
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;

    /**
     * Create new role
     * Only ADMIN can use this
     */
    @Transactional
    public RoleResponse createRole(RoleRequest request, String createdBy) {
        log.info("Creating role: {}", request.getRoleName());

        // Check if role already exists
        if (roleRepository.existsByRoleNameIgnoreCase(request.getRoleName())) {
            log.error("Role already exists: {}", request.getRoleName());
            throw new DuplicateResourceException(
                    String.format(ValidationMessages.ROLE_NAME_EXISTS, request.getRoleName())
            );
        }

        Role role = Role.builder()
                .roleName(request.getRoleName().trim())
                .createdBy(createdBy)
                .isActive(true)
                .build();

        Role savedRole = roleRepository.save(role);
        log.info("Role created successfully with ID: {}", savedRole.getId());

        return mapToResponse(savedRole);
    }

    /**
     * Get all roles
     */
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        log.info("Fetching all roles");
        return roleRepository.findAllByOrderByRoleNameAsc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all active roles
     */
    @Transactional(readOnly = true)
    public List<RoleResponse> getActiveRoles() {
        log.info("Fetching all active roles");
        return roleRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get role by ID
     */
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        log.info("Fetching role by ID: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role not found with ID: {}", id);
                    return new ResourceNotFoundException(
                            String.format(ValidationMessages.ROLE_NOT_FOUND, id)
                    );
                });
        return mapToResponse(role);
    }

    /**
     * Update role
     * Only ADMIN can use this
     */
    @Transactional
    public RoleResponse updateRole(Long id, RoleRequest request, String updatedBy) {
        log.info("Updating role ID: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role not found with ID: {}", id);
                    return new ResourceNotFoundException(
                            String.format(ValidationMessages.ROLE_NOT_FOUND, id)
                    );
                });

        // Check if new name already exists (excluding current role)
        roleRepository.findByRoleNameIgnoreCase(request.getRoleName())
                .ifPresent(existingRole -> {
                    if (!existingRole.getId().equals(id)) {
                        log.error("Role name already exists: {}", request.getRoleName());
                        throw new DuplicateResourceException(
                                String.format(ValidationMessages.ROLE_NAME_EXISTS, request.getRoleName())
                        );
                    }
                });

        role.setRoleName(request.getRoleName().trim());
        role.setCreatedBy(updatedBy); // Using createdBy field for last updated by

        Role updatedRole = roleRepository.save(role);
        log.info("Role updated successfully: {}", updatedRole.getId());

        return mapToResponse(updatedRole);
    }

    /**
     * Delete role (soft delete)
     * Only ADMIN can use this
     */
    @Transactional
    public void deleteRole(Long id) {
        log.info("Deleting role ID: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role not found with ID: {}", id);
                    return new ResourceNotFoundException(
                            String.format(ValidationMessages.ROLE_NOT_FOUND, id)
                    );
                });

        // Check if role is in use
        long employeeCount = roleRepository.countEmployeesByRoleId(id);
        if (employeeCount > 0) {
            log.error("Cannot delete role. It is assigned to {} employee(s)", employeeCount);
            throw new IllegalArgumentException(
                    String.format(ValidationMessages.ROLE_IN_USE, employeeCount)
            );
        }

        // Soft delete
        role.setIsActive(false);
        roleRepository.save(role);
        log.info("Role soft deleted successfully: {}", id);
    }

    /**
     * Get role entity by ID (for internal use)
     */
    @Transactional(readOnly = true)
    public Role getRoleEntityById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ValidationMessages.ROLE_NOT_FOUND, id)
                ));
    }

    /**
     * Map Role entity to RoleResponse DTO
     */
    private RoleResponse mapToResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .isActive(role.getIsActive())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .createdBy(role.getCreatedBy())
                .build();
    }
}
