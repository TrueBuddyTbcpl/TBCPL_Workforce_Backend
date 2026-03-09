// com/tbcpl/workforce/common/enums/RoleType.java
package com.tbcpl.workforce.common.enums;

/**
 * Enum for system-defined role types
 * DB stores these as uppercase strings (e.g., "SUPER_ADMIN")
 * Frontend displays them in a human-readable format via getDisplayName()
 */
public enum RoleType {

    SUPER_ADMIN("Super Admin"),
    ADMIN("Admin"),
    MANAGER("Manager"),
    HR_MANAGER("HR Manager"),
    ASSOCIATE("Associate");

    private final String displayName;

    RoleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Returns the DB-stored value (uppercase with underscore) */
    public String getDbValue() {
        return this.name(); // e.g., "SUPER_ADMIN"
    }

    /**
     * Roles that are eligible as Reporting Managers
     * Used in the "Assign Reporting Manager" dropdown
     */
    public static java.util.List<RoleType> getManagerRoles() {
        return java.util.List.of(SUPER_ADMIN, ADMIN, MANAGER, HR_MANAGER);
    }

    /**
     * Roles that ADMIN can assign (excludes SUPER_ADMIN)
     */
    public static java.util.List<RoleType> getAdminAssignableRoles() {
        return java.util.List.of(ADMIN, MANAGER, HR_MANAGER, ASSOCIATE);
    }

    /**
     * Roles that SUPER_ADMIN can assign (all roles)
     */
    public static java.util.List<RoleType> getSuperAdminAssignableRoles() {
        return java.util.List.of(SUPER_ADMIN, ADMIN, MANAGER, HR_MANAGER, ASSOCIATE);
    }

    /** Parse from DB string value (case-insensitive) */
    public static RoleType fromDbValue(String value) {
        for (RoleType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown role type: " + value);
    }
}
