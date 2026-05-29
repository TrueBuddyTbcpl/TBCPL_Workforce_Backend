package com.tbcpl.workforce.hr.payroll.entity.enums;

public enum SalaryComponentType {
    EARNING,    // Basic, HRA, DA, Special Allowance, etc.
    DEDUCTION,  // PF, ESI, PT, TDS, Loan Recovery, etc.
    BENEFIT     // Gratuity, Bonus (informational, not in-hand)
}