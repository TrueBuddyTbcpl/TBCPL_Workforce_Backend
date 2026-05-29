package com.tbcpl.workforce.hr.attendance.controller;

import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.hr.attendance.dto.request.HolidayRequest;
import com.tbcpl.workforce.hr.attendance.dto.response.HolidayResponse;
import com.tbcpl.workforce.hr.attendance.service.HolidayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping(ApiEndpoints.HR_BASE)
@RequiredArgsConstructor
@Slf4j
public class HolidayController {

    private final HolidayService holidayService;

    /** POST /api/v1/hr/holidays */
    @PostMapping(ApiEndpoints.HR_HOLIDAYS)
    public ResponseEntity<ApiResponse<HolidayResponse>> createHoliday(
            @Valid @RequestBody HolidayRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication.getName();
        log.info("Create holiday: {} by: {}", request.getHolidayName(), createdBy);
        HolidayResponse response = holidayService.createHoliday(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Holiday created successfully", response));
    }

    /** GET /api/v1/hr/holidays?year=2026&location=Delhi */
    @GetMapping(ApiEndpoints.HR_HOLIDAYS)
    public ResponseEntity<ApiResponse<List<HolidayResponse>>> getHolidays(
            @RequestParam(defaultValue = "0")   int    year,
            @RequestParam(required = false)     String location
    ) {
        int targetYear = year == 0 ? Year.now().getValue() : year;
        log.info("Get holidays - year:{} location:{}", targetYear, location);

        List<HolidayResponse> holidays = (location != null && !location.isBlank())
                ? holidayService.getHolidaysByYearAndLocation(targetYear, location)
                : holidayService.getHolidaysByYear(targetYear);

        return ResponseEntity.ok(ApiResponse.success("Holidays retrieved successfully", holidays));
    }

    /** GET /api/v1/hr/holidays/{id} */
    @GetMapping(ApiEndpoints.HR_HOLIDAY_BY_ID)
    public ResponseEntity<ApiResponse<HolidayResponse>> getHolidayById(
            @PathVariable Long id
    ) {
        log.info("Get holiday by ID: {}", id);
        return ResponseEntity.ok(
                ApiResponse.success("Holiday retrieved successfully",
                        holidayService.getHolidayById(id)));
    }

    /** PUT /api/v1/hr/holidays/{id} */
    @PutMapping(ApiEndpoints.HR_HOLIDAY_BY_ID)
    public ResponseEntity<ApiResponse<HolidayResponse>> updateHoliday(
            @PathVariable Long id,
            @Valid @RequestBody HolidayRequest request,
            Authentication authentication
    ) {
        String updatedBy = authentication.getName();
        log.info("Update holiday ID: {} by: {}", id, updatedBy);
        HolidayResponse response = holidayService.updateHoliday(id, request, updatedBy);
        return ResponseEntity.ok(ApiResponse.success("Holiday updated successfully", response));
    }

    /** DELETE /api/v1/hr/holidays/{id} */
    @DeleteMapping(ApiEndpoints.HR_HOLIDAY_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteHoliday(
            @PathVariable Long id
    ) {
        log.info("Soft delete holiday ID: {}", id);
        holidayService.deleteHoliday(id);
        return ResponseEntity.ok(ApiResponse.success("Holiday deleted successfully"));
    }
}