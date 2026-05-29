package com.tbcpl.workforce.common.controller;

import com.tbcpl.workforce.admin.dto.ClientResponseDTO;
import com.tbcpl.workforce.admin.service.ClientService;
import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Common dropdown controller accessible by all departments.
 * Provides shared reference data like clients, used across multiple modules.
 */
@RestController
@RequestMapping(ApiEndpoints.COMMON_BASE)
@RequiredArgsConstructor
@Slf4j
public class CommonDropdownController {

    private final ClientService clientService;

    /**
     * Get all clients for dropdown.
     * Accessible by all authenticated users across all departments.
     *
     * @return List of clients with id, clientId, and clientName
     */
    @GetMapping(ApiEndpoints.COMMON_DROPDOWN_CLIENTS)
    public ResponseEntity<ApiResponse<List<ClientResponseDTO>>> getClientDropdown() {
        log.info("GET /api/v1/common/dropdowns/clients - Get clients dropdown (accessible to all departments)");
        List<ClientResponseDTO> clients = clientService.getAllClients();
        return ResponseEntity.ok(ApiResponse.success("Clients retrieved successfully", clients));
    }
}