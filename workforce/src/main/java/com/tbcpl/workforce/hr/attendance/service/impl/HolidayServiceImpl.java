package com.tbcpl.workforce.hr.attendance.service.impl;

import com.tbcpl.workforce.common.exception.DuplicateResourceException;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.EmployeeNameResolverService;
import com.tbcpl.workforce.hr.attendance.dto.request.HolidayRequest;
import com.tbcpl.workforce.hr.attendance.dto.response.HolidayResponse;
import com.tbcpl.workforce.hr.attendance.entity.Holiday;
import com.tbcpl.workforce.hr.attendance.repository.HolidayRepository;
import com.tbcpl.workforce.hr.attendance.service.HolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository           holidayRepository;
    private final EmployeeNameResolverService  nameResolver;

    @Override
    @Transactional
    public HolidayResponse createHoliday(HolidayRequest request, String createdBy) {
        log.info("Creating holiday: {} on {}", request.getHolidayName(), request.getHolidayDate());

        String location = request.getLocation();
        if (holidayRepository.existsByHolidayDateAndLocation(request.getHolidayDate(), location)) {
            throw new DuplicateResourceException(
                    "Holiday already exists for date: " + request.getHolidayDate()
                            + (location != null ? " at location: " + location : " (all locations)"));
        }

        Holiday holiday = Holiday.builder()
                .holidayName(request.getHolidayName().trim())
                .holidayDate(request.getHolidayDate())
                .holidayYear(request.getHolidayDate().getYear())
                .description(request.getDescription())
                .location(location)
                .isOptional(request.getIsOptional() != null ? request.getIsOptional() : false)
                .isActive(true)
                .createdBy(createdBy)
                .build();

        Holiday saved = holidayRepository.save(holiday);
        log.info("Holiday created with ID: {}", saved.getId());
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HolidayResponse getHolidayById(Long id) {
        Holiday h = findById(id);
        return mapToResponse(h, resolveCreatedBy(h.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HolidayResponse> getHolidaysByYear(Integer year) {
        log.info("Fetching holidays for year: {}", year);
        List<Holiday> holidays =
                holidayRepository.findByHolidayYearAndIsActiveTrueOrderByHolidayDateAsc(year);
        Map<String, String> nameMap = batchResolve(
                holidays.stream().map(Holiday::getCreatedBy).collect(Collectors.toSet()));
        return holidays.stream()
                .map(h -> mapToResponse(h, nameMap))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HolidayResponse> getHolidaysByYearAndLocation(Integer year, String location) {
        log.info("Fetching holidays for year: {} location: {}", year, location);
        List<Holiday> holidays =
                holidayRepository.findByHolidayYearAndLocationAndIsActiveTrueOrderByHolidayDateAsc(
                        year, location);
        Map<String, String> nameMap = batchResolve(
                holidays.stream().map(Holiday::getCreatedBy).collect(Collectors.toSet()));
        return holidays.stream()
                .map(h -> mapToResponse(h, nameMap))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HolidayResponse updateHoliday(Long id, HolidayRequest request, String updatedBy) {
        log.info("Updating holiday ID: {}", id);
        Holiday holiday = findById(id);

        String location = request.getLocation();
        if (holidayRepository.existsByHolidayDateAndLocationAndIdNot(
                request.getHolidayDate(), location, id)) {
            throw new DuplicateResourceException(
                    "Holiday already exists for date: " + request.getHolidayDate());
        }

        holiday.setHolidayName(request.getHolidayName().trim());
        holiday.setHolidayDate(request.getHolidayDate());
        holiday.setHolidayYear(request.getHolidayDate().getYear());
        holiday.setDescription(request.getDescription());
        holiday.setLocation(location);
        if (request.getIsOptional() != null) holiday.setIsOptional(request.getIsOptional());
        holiday.setCreatedBy(updatedBy);

        Holiday saved = holidayRepository.save(holiday);
        log.info("Holiday ID: {} updated", id);
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public void deleteHoliday(Long id) {
        log.info("Soft deleting holiday ID: {}", id);
        Holiday holiday = findById(id);
        holiday.setIsActive(false);
        holidayRepository.save(holiday);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Holiday findById(Long id) {
        return holidayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Holiday not found with ID: " + id));
    }

    private Map<String, String> resolveCreatedBy(String createdBy) {
        if (createdBy == null || createdBy.isBlank()) return Collections.emptyMap();
        return nameResolver.resolve(Set.of(createdBy));
    }

    private Map<String, String> batchResolve(Set<String> values) {
        Set<String> filtered = values.stream()
                .filter(v -> v != null && !v.isBlank())
                .collect(Collectors.toSet());
        return filtered.isEmpty() ? Collections.emptyMap() : nameResolver.resolve(filtered);
    }

    private HolidayResponse mapToResponse(Holiday h, Map<String, String> nameMap) {
        String raw = h.getCreatedBy();
        String resolvedName = raw == null ? null :
                nameMap.getOrDefault(raw.contains("@") ? raw.toLowerCase() : raw, raw);

        return HolidayResponse.builder()
                .id(h.getId())
                .holidayName(h.getHolidayName())
                .holidayDate(h.getHolidayDate())
                .holidayYear(h.getHolidayYear())
                .description(h.getDescription())
                .location(h.getLocation())
                .isOptional(h.getIsOptional())
                .isActive(h.getIsActive())
                .createdAt(h.getCreatedAt())
                .updatedAt(h.getUpdatedAt())
                .createdBy(resolvedName)
                .build();
    }
}