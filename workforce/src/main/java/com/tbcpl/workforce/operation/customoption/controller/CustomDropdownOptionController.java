package com.tbcpl.workforce.operation.customoption.controller;

import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.operation.customoption.dto.request.CustomOptionRequest;
import com.tbcpl.workforce.operation.customoption.dto.response.CustomOptionResponse;
import com.tbcpl.workforce.operation.customoption.service.CustomDropdownOptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/operation/dropdown-options")
@RequiredArgsConstructor
public class CustomDropdownOptionController {

    private final CustomDropdownOptionService service;

    // GET /api/v1/operation/dropdown-options/retailerStatus
    @GetMapping("/{fieldName}")
    public ResponseEntity<ApiResponse<List<CustomOptionResponse>>> getOptions(
            @PathVariable String fieldName) {
        return ResponseEntity.ok(ApiResponse.success(
                "Options fetched", service.getOptions(fieldName)));
    }

    // POST /api/v1/operation/dropdown-options
    @PostMapping
    public ResponseEntity<ApiResponse<CustomOptionResponse>> saveOption(
            @Valid @RequestBody CustomOptionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Option saved", service.saveOption(request)));
    }
}