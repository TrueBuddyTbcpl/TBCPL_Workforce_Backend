package com.tbcpl.workforce.hr.payroll.entity.enums;

public enum PayrollStatus {
    DRAFT,      // Input added, not yet sent to Accounts
    SUBMITTED,  // Submitted to Accounts for processing
    PROCESSED,  // Accounts has processed and disbursed
    CANCELLED   // Cancelled / reversed
}