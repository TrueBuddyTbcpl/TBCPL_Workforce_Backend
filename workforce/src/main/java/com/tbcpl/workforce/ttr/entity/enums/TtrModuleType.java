package com.tbcpl.workforce.ttr.entity.enums;

public enum TtrModuleType {

    PREREPORT,
    CASE,
    FINAL_REPORT;

    public String getDisplayName() {
        return switch (this) {
            case PREREPORT    -> "Pre-Report";
            case CASE         -> "Case";
            case FINAL_REPORT -> "Final Report";
        };
    }
}