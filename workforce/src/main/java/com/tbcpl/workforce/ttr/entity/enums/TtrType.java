package com.tbcpl.workforce.ttr.entity.enums;

public enum TtrType {

    CUSTOM,     // One-time ad-hoc task created manually
    RECURRING;  // Predefined task — auto-resets to S1_OPENED after completion

    public String getDisplayName() {
        return switch (this) {
            case CUSTOM    -> "Custom Task";
            case RECURRING -> "Recurring Task";
        };
    }
}