package com.tbcpl.workforce.hr.performance.entity.enums;

public enum AppraisalStatus {
    DRAFT,               // HR created cycle, not yet pushed to employees
    SELF_REVIEW_PENDING, // Waiting for employee self-assessment
    SELF_REVIEW_DONE,    // Employee submitted self-assessment
    MANAGER_REVIEW_PENDING,
    MANAGER_REVIEW_DONE,
    HR_REVIEW_PENDING,
    COMPLETED,           // Final rating set and locked
    CANCELLED
}