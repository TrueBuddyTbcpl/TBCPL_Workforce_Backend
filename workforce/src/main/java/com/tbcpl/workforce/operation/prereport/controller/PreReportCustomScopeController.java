package com.tbcpl.workforce.operation.prereport.controller;

import com.tbcpl.workforce.operation.prereport.dto.request.CustomScopeCreateRequest;
import com.tbcpl.workforce.operation.prereport.dto.response.CustomScopeResponse;
import com.tbcpl.workforce.operation.prereport.service.PreReportCustomScopeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/operation/prereport/custom-scopes")
@Slf4j
public class PreReportCustomScopeController {

    private final PreReportCustomScopeService customScopeService;

    public PreReportCustomScopeController(PreReportCustomScopeService customScopeService) {
        this.customScopeService = customScopeService;
    }

    @GetMapping
    public ResponseEntity<List<CustomScopeResponse>> getAllActiveScopes() {
        log.info("GET /api/v1/operation/prereport/custom-scopes - Fetching all active custom scopes");

        List<CustomScopeResponse> response = customScopeService.getAllActiveScopes();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CustomScopeResponse> createCustomScope(
            @Valid @RequestBody CustomScopeCreateRequest request) {
        log.info("POST /api/v1/operation/prereport/custom-scopes - Creating custom scope");

        // TODO: Extract createdBy from security context after JWT implementation
        String createdBy = "system"; // Placeholder

        CustomScopeResponse response = customScopeService.createCustomScope(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{scopeId}/deactivate")
    public ResponseEntity<Void> deactivateCustomScope(@PathVariable Long scopeId) {
        log.info("PATCH /api/v1/operation/prereport/custom-scopes/{}/deactivate - Deactivating custom scope", scopeId);

        customScopeService.deactivateCustomScope(scopeId);
        return ResponseEntity.noContent().build();
    }
}
