// com/tbcpl/workforce/common/enums/DepartmentType.java
package com.tbcpl.workforce.common.enums;

import java.util.List;

/**
 * Enum for department types.
 * PH_OPS added as a new department alongside existing ones.
 */
public enum DepartmentType {

    HR("HR"),
    ACCOUNTS("Accounts"),
    ADMIN("Admin"),
    OPERATION("Operation"),
    PH_OPS("Ph Ops");

    private final String displayName;

    DepartmentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the roles allowed for this department.
     * Used to drive the role dropdown on the frontend (add/edit employee form).
     */
    public List<RoleType> getAllowedRoles() {
        return switch (this) {
            case HR        -> List.of(RoleType.EXECUTIVE, RoleType.ASSOCIATE, RoleType.MANAGER);
            case ACCOUNTS  -> List.of(RoleType.ASSOCIATE, RoleType.EXECUTIVE);
            case OPERATION -> List.of(RoleType.COORDINATOR, RoleType.ASSOCIATE,
                    RoleType.SR_ASSOCIATE, RoleType.ASSISTANT_MANAGER,
                    RoleType.FIELD_ASSOCIATE);
            case ADMIN     -> List.of(RoleType.GLOBAL_ADMIN, RoleType.ADMIN);
            case PH_OPS    -> List.of(RoleType.PH_ASSOCIATE, RoleType.PH_HEAD);
        };
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
     * Parse from string (case-insensitive). Supports both name and displayName.
     */
    public static DepartmentType fromString(String value) {
        for (DepartmentType type : values()) {
            if (type.name().equalsIgnoreCase(value) || type.displayName.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid department type: " + value);
    }
}