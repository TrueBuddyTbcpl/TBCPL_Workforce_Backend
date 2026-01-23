package com.tbcpl.workforce.operation.prereport.service;

import com.tbcpl.workforce.operation.prereport.dto.request.CustomScopeCreateRequest;
import com.tbcpl.workforce.operation.prereport.dto.response.CustomScopeResponse;
import com.tbcpl.workforce.operation.prereport.entity.PreReportCustomScope;
import com.tbcpl.workforce.operation.prereport.repository.PreReportCustomScopeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PreReportCustomScopeService {

    private final PreReportCustomScopeRepository customScopeRepository;

    public PreReportCustomScopeService(PreReportCustomScopeRepository customScopeRepository) {
        this.customScopeRepository = customScopeRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomScopeResponse> getAllActiveScopes() {
        log.info("Fetching all active custom scopes");

        List<PreReportCustomScope> scopes = customScopeRepository.findAllActiveScopes();

        return scopes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomScopeResponse createCustomScope(CustomScopeCreateRequest request, String createdBy) {
        log.info("Creating custom scope: {}", request.getScopeName());

        if (customScopeRepository.existsByScopeNameIgnoreCase(request.getScopeName())) {
            log.warn("Custom scope already exists: {}", request.getScopeName());
            throw new RuntimeException("Custom scope already exists with name: " + request.getScopeName());
        }

        PreReportCustomScope customScope = PreReportCustomScope.builder()
                .scopeName(request.getScopeName())
                .createdBy(createdBy)
                .isActive(true)
                .build();

        PreReportCustomScope savedScope = customScopeRepository.save(customScope);

        log.info("Custom scope created successfully with ID: {}", savedScope.getId());
        return mapToResponse(savedScope);
    }

    @Transactional
    public void deactivateCustomScope(Long scopeId) {
        log.info("Deactivating custom scope with ID: {}", scopeId);

        PreReportCustomScope customScope = customScopeRepository.findById(scopeId)
                .orElseThrow(() -> new RuntimeException("Custom scope not found with ID: " + scopeId));

        customScope.setIsActive(false);
        customScopeRepository.save(customScope);

        log.info("Custom scope deactivated successfully: {}", scopeId);
    }

    private CustomScopeResponse mapToResponse(PreReportCustomScope customScope) {
        return CustomScopeResponse.builder()
                .id(customScope.getId())
                .scopeName(customScope.getScopeName())
                .createdBy(customScope.getCreatedBy())
                .createdAt(customScope.getCreatedAt())
                .isActive(customScope.getIsActive())
                .build();
    }
}
