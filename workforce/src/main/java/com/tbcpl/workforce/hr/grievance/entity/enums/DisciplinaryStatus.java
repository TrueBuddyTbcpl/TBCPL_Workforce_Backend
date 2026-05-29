package com.tbcpl.workforce.hr.grievance.entity.enums;

public enum DisciplinaryStatus {
    INITIATED,         // Action record created
    NOTICE_ISSUED,     // Show cause / warning issued to employee
    RESPONSE_RECEIVED, // Employee has responded
    UNDER_INQUIRY,     // Formal inquiry in progress
    ACTION_TAKEN,      // Final action executed
    WITHDRAWN,         // Action withdrawn / revoked
    CLOSED             // Case closed
}