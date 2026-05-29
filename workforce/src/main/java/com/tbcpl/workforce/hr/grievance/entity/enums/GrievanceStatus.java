package com.tbcpl.workforce.hr.grievance.entity.enums;

public enum GrievanceStatus {
    SUBMITTED,       // Employee raised, not yet assigned
    UNDER_REVIEW,    // Assigned to HR, investigation in progress
    PENDING_INFO,    // HR waiting for additional info from employee
    ESCALATED,       // Escalated to senior HR / management
    RESOLVED,        // Resolved successfully
    CLOSED,          // Closed without resolution or by employee request
    REJECTED         // Rejected as invalid/duplicate
}