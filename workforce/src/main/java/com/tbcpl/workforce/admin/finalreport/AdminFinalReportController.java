package com.tbcpl.workforce.admin.finalreport;

import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.operation.finalreport.dto.response.FinalReportListItemResponse;
import com.tbcpl.workforce.operation.finalreport.service.FinalReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/finalreports")
@Slf4j
public class AdminFinalReportController {

    private final FinalReportService finalReportService;

    public AdminFinalReportController(FinalReportService finalReportService) {
        this.finalReportService = finalReportService;
    }

    /**
     * GET /api/v1/admin/finalreports
     * Returns all final reports (non-deleted) for admin view.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<FinalReportListItemResponse>>> getAllReports(
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "100") int size) {
        log.info("Admin GET /finalreports - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(
                "Final reports fetched successfully",
                finalReportService.getAllReports(pageable)
        ));
    }
}
