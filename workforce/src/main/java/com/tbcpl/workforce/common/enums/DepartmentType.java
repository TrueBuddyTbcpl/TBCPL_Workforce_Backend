package com.tbcpl.workforce.common.enums;

/**
 * Enum for department types
 */
public enum DepartmentType {
    ADMIN("Admin"),
    HR("HR"),
    OPERATION("Operation"),
    ACCOUNTS("Accounts");

    private final String displayName;

    DepartmentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if department is ADMIN
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Check if department is HR
     */
    public boolean isHR() {
        return this == HR;
    }

    /**
     * Check if department can manage employees (HR or ADMIN)
     */
    public boolean canManageEmployees() {
        return this == HR || this == ADMIN;
    }

    /**
     * Get enum from string (case-insensitive)
     */
    public static DepartmentType fromString(String value) {
        for (DepartmentType type : DepartmentType.values()) {
            if (type.name().equalsIgnoreCase(value) || type.displayName.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid department type: " + value);
    }
}
