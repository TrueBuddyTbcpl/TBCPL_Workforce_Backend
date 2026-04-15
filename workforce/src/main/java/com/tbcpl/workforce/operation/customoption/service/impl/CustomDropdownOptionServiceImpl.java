package com.tbcpl.workforce.operation.customoption.service.impl;

import com.tbcpl.workforce.operation.customoption.dto.request.CustomOptionRequest;
import com.tbcpl.workforce.operation.customoption.dto.response.CustomOptionResponse;
import com.tbcpl.workforce.operation.customoption.entity.OpCustomDropdownOption;
import com.tbcpl.workforce.operation.customoption.repository.OpCustomDropdownOptionRepository;
import com.tbcpl.workforce.operation.customoption.service.CustomDropdownOptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SuppressWarnings("unused")
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomDropdownOptionServiceImpl implements CustomDropdownOptionService {

    private final OpCustomDropdownOptionRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<CustomOptionResponse> getOptions(String fieldName) {
        return repository.findByFieldNameOrderByCreatedAtAsc(fieldName)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CustomOptionResponse saveOption(CustomOptionRequest request) {
        String trimmedValue = request.getValue().trim();
        String fieldName    = request.getFieldName().trim();

        // Idempotent — if already exists, return existing (no duplicate, no crash)
        return repository.findByFieldNameOrderByCreatedAtAsc(fieldName)
                .stream()
                .filter(o -> o.getValue().equalsIgnoreCase(trimmedValue)) // ← was .equals(), now case-insensitive
                .findFirst()
                .map(existing -> {
                    log.info("Custom option already exists: fieldName={}, value={}", fieldName, trimmedValue);
                    return toResponse(existing);
                })
                .orElseGet(() -> {
                    // Not found — persist new custom option
                    OpCustomDropdownOption saved = repository.save(
                            OpCustomDropdownOption.builder()
                                    .fieldName(fieldName)
                                    .value(trimmedValue)
                                    .createdBy(request.getCreatedBy())
                                    .build()
                    );
                    log.info("New custom dropdown option saved: fieldName={}, value={}", fieldName, trimmedValue);
                    return toResponse(saved);
                });
    }

    private CustomOptionResponse toResponse(OpCustomDropdownOption o) {
        return CustomOptionResponse.builder()
                .id(o.getId())
                .fieldName(o.getFieldName())
                .value(o.getValue())
                .build();
    }
}