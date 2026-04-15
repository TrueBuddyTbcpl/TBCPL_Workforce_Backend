package com.tbcpl.workforce.operation.customoption.service.impl;

import com.tbcpl.workforce.common.constants.DropdownFieldNames;
import com.tbcpl.workforce.operation.customoption.dto.request.CustomOptionRequest;
import com.tbcpl.workforce.operation.customoption.service.CustomDropdownOptionService;
import com.tbcpl.workforce.operation.customoption.service.OpDropdownService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpDropdownServiceImpl implements OpDropdownService {

    private final CustomDropdownOptionService customDropdownOptionService;

    // ─────────────────────────────────────────────────────────────────────────
    // Static labels — mirrors your existing Java enums exactly.
    // Keys MUST match DropdownFieldNames constants (case-sensitive).
    // Update this map whenever you add/remove enum constants.
    // ─────────────────────────────────────────────────────────────────────────
    private static final Map<String, List<String>> STATIC_OPTIONS = new LinkedHashMap<>();

    static {
        STATIC_OPTIONS.put(DropdownFieldNames.GENDER,
                List.of("MALE", "FEMALE", "TRANSGENDER"));

        STATIC_OPTIONS.put(DropdownFieldNames.RELATIONSHIP_NATURE,
                List.of("OWNER", "PARTNER", "DIRECTOR", "EMPLOYEE",
                        "INVESTOR", "CONSULTANT", "AGENT", "VENDOR", "CUSTOMER", "NA"));

        STATIC_OPTIONS.put(DropdownFieldNames.FIR_STATUS,
                List.of("PENDING", "CHARGESHEETED", "ACQUITTED", "CONVICTED",
                        "BAILED", "ABSCONDING", "CLOSED", "NA"));

        STATIC_OPTIONS.put(DropdownFieldNames.RAIDING_AUTHORITY,
                List.of("POLICE", "CUSTOMS", "ED", "CBI", "EOW",
                        "LOCAL_AUTHORITY", "COURT", "NA"));

        STATIC_OPTIONS.put(DropdownFieldNames.VEHICLE_OWNERSHIP_TYPE,
                List.of("SELF_OWNED", "FINANCED", "LEASED", "RENTED",
                        "COMPANY_OWNED", "NA"));

        STATIC_OPTIONS.put(DropdownFieldNames.ASSOCIATE_ROLE,
                List.of("ASSOCIATE", "EMPLOYEE", "NA"));

        STATIC_OPTIONS.put(DropdownFieldNames.RETAILER_TYPE,
                List.of("WHOLESALE", "RETAIL", "ONLINE", "NA"));

        STATIC_OPTIONS.put(DropdownFieldNames.SUPPLIER_TYPE,
                List.of("DOMESTIC", "INTERNATIONAL", "NA"));

        STATIC_OPTIONS.put(DropdownFieldNames.MANUFACTURER_TYPE,
                List.of("SMALL_SCALE", "LARGE_SCALE", "COTTAGE", "NA"));

        // ── Business Activity Status (retailerStatus / supplierStatus / manufacturerStatus)
        STATIC_OPTIONS.put(DropdownFieldNames.RETAILER_STATUS,
                List.of("INDIVIDUAL", "ENTITY"));

        STATIC_OPTIONS.put(DropdownFieldNames.SUPPLIER_STATUS,
                List.of("INDIVIDUAL", "ENTITY"));

        STATIC_OPTIONS.put(DropdownFieldNames.MANUFACTURER_STATUS,
                List.of("INDIVIDUAL", "ENTITY"));

        // ── Business Activities — Entity Type (what frontend sends as "businessEntityStatus")
        STATIC_OPTIONS.put(DropdownFieldNames.BUSINESS_ENTITY_STATUS,
                List.of("INDIVIDUAL", "ENTITY"));

        // ── Business Activities — Authorization Status (what frontend sends as "authorizationStatus")
        STATIC_OPTIONS.put(DropdownFieldNames.AUTHORIZATION_STATUS,
                List.of("AUTHORIZED", "UNAUTHORIZED"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Called by OpProfileServiceImpl when user selects "OTHER"
    // Delegates to existing CustomDropdownOptionService (already idempotent)
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void persistCustomOption(String fieldName, String value, String empId) {
        if (fieldName == null || fieldName.isBlank() || value == null || value.isBlank()) return;

        String normalizedField = fieldName.trim();
        String normalizedValue = value.trim();

        CustomOptionRequest request = new CustomOptionRequest();
        request.setFieldName(normalizedField);
        request.setValue(normalizedValue);
        request.setCreatedBy(empId);

        customDropdownOptionService.saveOption(request);
        log.info("persistCustomOption: field='{}' value='{}' by='{}'",
                normalizedField, normalizedValue, empId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Called by OpDropdownController to serve the frontend dropdown.
    // Returns: static enum values + custom DB values + "OTHER" (always last).
    //
    // FIX: fieldName is now normalized (trimmed + lowercased lookup fallback)
    //      so frontend casing mismatches no longer silently break the list.
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<String> getOptionsForField(String fieldName) {

        if (fieldName == null || fieldName.isBlank()) {
            log.warn("getOptionsForField called with blank fieldName — returning [OTHER] only");
            return List.of("OTHER");
        }

        String normalizedField = fieldName.trim();

        // ── 1. Lookup static enum values (exact match first)
        List<String> staticValues = STATIC_OPTIONS.get(normalizedField);

        // ── 1b. Fallback: case-insensitive scan (catches frontend casing bugs)
        if (staticValues == null) {
            staticValues = STATIC_OPTIONS.entrySet().stream()
                    .filter(e -> e.getKey().equalsIgnoreCase(normalizedField))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(List.of());

            if (!staticValues.isEmpty()) {
                log.warn("getOptionsForField: fieldName='{}' matched via case-insensitive fallback. " +
                        "Fix the frontend to send the exact key.", normalizedField);
            } else {
                log.warn("getOptionsForField: No static mapping found for fieldName='{}'. " +
                        "Returning custom options only. Add it to STATIC_OPTIONS if needed.", normalizedField);
            }
        }

        List<String> result = new ArrayList<>(staticValues);

        // ── 2. Append custom DB values (skip duplicates, case-insensitive)
        try {
            customDropdownOptionService.getOptions(normalizedField)
                    .stream()
                    .map(opt -> opt.getValue() != null ? opt.getValue().trim() : null)
                    .filter(v -> v != null && !v.isBlank())
                    .filter(v -> result.stream().noneMatch(existing -> existing.equalsIgnoreCase(v)))
                    .forEach(result::add);
        } catch (Exception e) {
            // Never let a DB failure break the dropdown — static options still served
            log.error("getOptionsForField: Failed to load custom options for field='{}': {}",
                    normalizedField, e.getMessage());
        }

        // ── 3. "OTHER" always last — never duplicated
        result.removeIf("OTHER"::equalsIgnoreCase);
        result.add("OTHER");

        log.debug("getOptionsForField: field='{}' → {} options ({}static, {}custom, +OTHER)",
                normalizedField,
                result.size(),
                staticValues.size(),
                result.size() - staticValues.size() - 1);

        return result;
    }
}