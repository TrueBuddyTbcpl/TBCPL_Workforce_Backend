// com.tbcpl.workforce.common.enums.ReportType.java
package com.tbcpl.workforce.common.enums;

import lombok.Getter;

@Getter
public enum ReportType {

    PRE_REPORT("Preliminary Lead Assessment Report"),
    FINAL_REPORT("Final Investigation Report"),
    PROPOSAL("Project Proposal"),
    LETTER_OF_AUTHORITY("Letter of Authority");

    private final String displayName;

    ReportType(String displayName) {
        this.displayName = displayName;
    }
}