package com.tbcpl.workforce.operation.controller;

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

@RestController
@RequestMapping(ApiEndpoints.OPERATION_BASE)
@RequiredArgsConstructor
@Slf4j
public class OperationDropdownController {

    private final ClientService clientService;

    @GetMapping(ApiEndpoints.OPERATION_DROPDOWN_CLIENTS)
    public ResponseEntity<ApiResponse<List<ClientResponseDTO>>> getClientDropdown() {
        log.info("GET /api/v1/operation/dropdowns/clients - Get clients dropdown for operation");
        List<ClientResponseDTO> clients = clientService.getAllClients();
        return ResponseEntity.ok(ApiResponse.success("Clients retrieved successfully", clients));
    }
}