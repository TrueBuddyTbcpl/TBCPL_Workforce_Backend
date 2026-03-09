package com.tbcpl.workforce.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Utility class to generate Employee ID in format: YYYY/NNN
 * Example: 2026/001, 2026/002, etc.
 */
@Component
@Slf4j
public class EmpIdGenerator {

    /**
     * Generate next employee ID based on current year and last employee number
     * Format: YYYY/NNN (e.g., 2026/001)
     *
     * @param lastEmpNumber The last employee number used (e.g., 5 for 2026/005)
     * @return Next employee ID (e.g., 2026/006)
     */
    public String generateEmpId(Integer lastEmpNumber) {
        int currentYear = LocalDate.now().getYear();
        int nextNumber = (lastEmpNumber == null) ? 1 : lastEmpNumber + 1;

        String empId = String.format("%d/%03d", currentYear, nextNumber);
        log.info("Generated Employee ID: {}", empId);

        return empId;
    }

    /**
     * Extract year from employee ID
     * Example: "2026/001" → 2026
     *
     * @param empId Employee ID
     * @return Year as integer
     */
    public int extractYear(String empId) {
        if (empId == null || !empId.matches("\\d{4}/\\d{3}")) {
            throw new IllegalArgumentException("Invalid employee ID format. Expected: YYYY/NNN");
        }
        return Integer.parseInt(empId.substring(0, 4));
    }

    /**
     * Extract sequence number from employee ID
     * Example: "2026/001" → 1
     *
     * @param empId Employee ID
     * @return Sequence number as integer
     */
    public int extractSequenceNumber(String empId) {
        if (empId == null || !empId.matches("\\d{4}/\\d{3}")) {
            throw new IllegalArgumentException("Invalid employee ID format. Expected: YYYY/NNN");
        }
        return Integer.parseInt(empId.substring(5));
    }

    /**
     * Validate employee ID format
     *
     * @param empId Employee ID to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidEmpIdFormat(String empId) {
        if (empId == null) {
            return false;
        }
        return empId.matches("\\d{4}/\\d{3}");
    }
}
