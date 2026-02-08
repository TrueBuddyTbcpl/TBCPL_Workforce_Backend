package com.tbcpl.workforce.auth.controller;

import com.tbcpl.workforce.auth.dto.request.RoleRequest;
import com.tbcpl.workforce.auth.dto.response.RoleResponse;
import com.tbcpl.workforce.auth.service.RoleService;
import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Role management
 * Only ADMIN can create/update/delete
 * HR and ADMIN can view
 */
@RestController
@RequestMapping(ApiEndpoints.AUTH_BASE)
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final RoleService roleService;

    /**
     * Create new role
     * Only ADMIN can access
     */
    @PostMapping(ApiEndpoints.ROLES)
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(
            @Valid @RequestBody RoleRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication.getName();
        log.info("Create role request by: {}", createdBy);

        RoleResponse response = roleService.createRole(request, createdBy);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Role created successfully", response));
    }

    /**
     * Get all roles
     * HR and ADMIN can access
     */
    @GetMapping(ApiEndpoints.ROLES)
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        log.info("Get all roles request");

        List<RoleResponse> roles = roleService.getAllRoles();

        return ResponseEntity.ok(
                ApiResponse.success("Roles retrieved successfully", roles)
        );
    }

    /**
     * Get role by ID
     * HR and ADMIN can access
     */
    @GetMapping(ApiEndpoints.ROLE_BY_ID)
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable Long id) {
        log.info("Get role by ID: {}", id);

        RoleResponse role = roleService.getRoleById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Role retrieved successfully", role)
        );
    }

    /**
     * Update role
     * Only ADMIN can access
     */
    @PutMapping(ApiEndpoints.ROLE_BY_ID)
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequest request,
            Authentication authentication
    ) {
        String updatedBy = authentication.getName();
        log.info("Update role ID: {} by: {}", id, updatedBy);

        RoleResponse response = roleService.updateRole(id, request, updatedBy);

        return ResponseEntity.ok(
                ApiResponse.success("Role updated successfully", response)
        );
    }

    /**
     * Delete role (soft delete)
     * Only ADMIN can access
     */
    @DeleteMapping(ApiEndpoints.ROLE_BY_ID)
    public ResponseEntity<ApiResponse<String>> deleteRole(@PathVariable Long id) {
        log.info("Delete role ID: {}", id);

        roleService.deleteRole(id);

        return ResponseEntity.ok(
                ApiResponse.success("Role deleted successfully", null)
        );
    }
}
