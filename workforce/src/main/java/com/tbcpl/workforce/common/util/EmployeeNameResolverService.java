package com.tbcpl.workforce.common.util;

import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Shared utility to bulk-resolve createdBy / updatedBy values to employee full names.
 * Handles BOTH:
 *   - Numeric DB IDs stored as strings (e.g., "6")    ← operation/hr/accounts modules
 *   - Email strings (e.g., "admin@tbcpl.co.in")       ← auth module entities
 * Single DB round-trip per call — zero N+1.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeNameResolverService {

    private final EmployeeRepository employeeRepository;

    /**
     * Bulk resolve a collection of createdBy/updatedBy values.
     *
     * @param values Collection of raw values (numeric IDs or emails)
     * @return Map of: rawValue → fullName (falls back to rawValue if employee not found)
     */
    @Transactional(readOnly = true)
    public Map<String, String> resolve(Collection<String> values) {
        if (values == null || values.isEmpty()) return Collections.emptyMap();

        Set<Long>   ids    = new HashSet<>();
        Set<String> emails = new HashSet<>();

        for (String v : values) {
            if (v == null || v.isBlank()) continue;
            try {
                ids.add(Long.parseLong(v.trim()));
            } catch (NumberFormatException e) {
                emails.add(v.trim().toLowerCase());
            }
        }

        Map<String, String> result = new HashMap<>();

        // Resolve numeric IDs → fullName (one query for all IDs)
        if (!ids.isEmpty()) {
            employeeRepository.findAllById(ids).forEach(emp ->
                    result.put(String.valueOf(emp.getId()), emp.getFullName())
            );
        }

        // Resolve emails → fullName (one query for all emails)
        if (!emails.isEmpty()) {
            employeeRepository.findByEmailIgnoreCaseIn(emails).forEach(emp ->
                    result.put(emp.getEmail().toLowerCase(), emp.getFullName())
            );
        }

        return result;
    }

    /**
     * Convenience method for resolving a single value.
     */
    @Transactional(readOnly = true)
    public String resolveOne(String value) {
        if (value == null || value.isBlank()) return value;
        String key = isEmail(value) ? value.toLowerCase() : value.trim();
        return resolve(Set.of(value)).getOrDefault(key, value);
    }

    private boolean isEmail(String value) {
        return value.contains("@");
    }
}