package com.tbcpl.workforce.operation.customoption.controller;

import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.operation.customoption.service.OpDropdownService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Serves MERGED dropdown options (static enum + custom DB + "OTHER")
 * to the frontend for every profile step dropdown.
 *
 * Separate from CustomDropdownOptionController which handles raw CRUD.
 *
 * GET /api/v1/operation/dropdowns/{fieldName}
 * e.g. /api/v1/operation/dropdowns/gender
 *      /api/v1/operation/dropdowns/firStatus
 */
@RestController
@RequestMapping("/api/v1/operation/dropdowns")
@RequiredArgsConstructor
public class OpDropdownController {

    private final OpDropdownService opDropdownService;

    @GetMapping("/{fieldName}")
    public ResponseEntity<ApiResponse<List<String>>> getOptions(
            @PathVariable String fieldName) {
        List<String> options = opDropdownService.getOptionsForField(fieldName);
        return ResponseEntity.ok(
                ApiResponse.success("Options fetched successfully", options));
    }
}