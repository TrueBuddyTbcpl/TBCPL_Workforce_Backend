package com.tbcpl.workforce.hr.attendance.service;

import com.tbcpl.workforce.hr.attendance.dto.request.HolidayRequest;
import com.tbcpl.workforce.hr.attendance.dto.response.HolidayResponse;

import java.util.List;

public interface HolidayService {

    HolidayResponse createHoliday(HolidayRequest request, String createdBy);

    HolidayResponse getHolidayById(Long id);

    List<HolidayResponse> getHolidaysByYear(Integer year);

    List<HolidayResponse> getHolidaysByYearAndLocation(Integer year, String location);

    HolidayResponse updateHoliday(Long id, HolidayRequest request, String updatedBy);

    void deleteHoliday(Long id);
}