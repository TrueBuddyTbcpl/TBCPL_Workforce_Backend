package com.tbcpl.workforce.operation.prereport.controller;

import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.operation.prereport.dto.request.CustomOptClientLeadRequest;
import com.tbcpl.workforce.operation.prereport.dto.response.CustomOptClientLeadResponse;
import com.tbcpl.workforce.operation.prereport.service.PrereportCustomOptClientLeadService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/operation/prereport/custom-options")
@Slf4j
public class PrereportCustomOptClientLeadController {

    private final PrereportCustomOptClientLeadService service;

    public PrereportCustomOptClientLeadController(
            PrereportCustomOptClientLeadService service) {
        this.service = service;
    }

    // Existing — get by step number (Client Lead)
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomOptClientLeadResponse>>> getByStep(
            @RequestParam Integer stepNumber,
            @RequestParam String leadType) {
        log.info("GET /custom-options?stepNumber={}&leadType={}", stepNumber, leadType);
        return ResponseEntity.ok(
                ApiResponse.success("Custom options fetched",
                        service.getOptionsByStep(stepNumber, leadType)));
    }

    // ← ADD: get by field key (TrueBuddy Lead)
    @GetMapping("/by-field")
    public ResponseEntity<ApiResponse<List<CustomOptClientLeadResponse>>> getByFieldKey(
            @RequestParam String fieldKey,
            @RequestParam String leadType) {
        log.info("GET /custom-options/by-field?fieldKey={}&leadType={}", fieldKey, leadType);
        return ResponseEntity.ok(
                ApiResponse.success("Custom options fetched",
                        service.getOptionsByFieldKey(fieldKey, leadType)));
    }

    // Existing — create
    @PostMapping
    public ResponseEntity<ApiResponse<CustomOptClientLeadResponse>> create(
            @Valid @RequestBody CustomOptClientLeadRequest request) {
        log.info("POST /custom-options - step: {}", request.getStepNumber());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Custom option created", service.createOption(request)));
    }

    // Existing — delete
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        log.info("DELETE /custom-options/{}", id);
        service.deleteOption(id);
        return ResponseEntity.ok(ApiResponse.success("Custom option deleted"));
    }
}