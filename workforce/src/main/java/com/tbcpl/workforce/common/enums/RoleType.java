// com/tbcpl/workforce/common/enums/RoleType.java
package com.tbcpl.workforce.common.enums;

import java.util.List;

/**
 * Enum for system-defined role types.
 *
 * DB stores values as uppercase with underscores (e.g., "SR_ASSOCIATE").
 * Frontend receives human-readable display names via getDisplayName().
 *
 * SUPER_ADMIN and MANAGER are system-only roles — they are NOT exposed
 * in the add/edit employee department-role dropdown.
 */
public enum RoleType {

    // ── System-only roles (not in department dropdown) ──────────────────────
    SUPER_ADMIN("Super Admin"),
    MANAGER("Manager"),

    // ── Admin department ─────────────────────────────────────────────────────
    GLOBAL_ADMIN("Global Admin"),
    ADMIN("Admin"),

    // ── HR & Accounts department ─────────────────────────────────────────────
    EXECUTIVE("Executive"),
    ASSOCIATE("Associate"),

    // ── Operation department ─────────────────────────────────────────────────
    SR_ASSOCIATE("Sr. Associate"),
    ASSISTANT_MANAGER("Assistant Manager"),
    COORDINATOR("Coordinator"),
    FIELD_ASSOCIATE("Field Associate"),

    // ── PH_OPS department ────────────────────────────────────────────────────
    PH_HEAD("PH Head"),
    PH_ASSOCIATE("PH Associate");

    private final String displayName;

    RoleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the DB-stored value (uppercase with underscore).
     * e.g., SR_ASSOCIATE, PH_HEAD
     */
    public String getDbValue() {
        return this.name();
    }

    /**
     * System-only roles — excluded from all department-role dropdowns.
     * Used during validation to prevent assignment via the public form.
     */
    public static List<RoleType> getSystemOnlyRoles() {
        return List.of(SUPER_ADMIN, MANAGER);
    }

    /**
     * Roles eligible as Reporting Managers across the system.
     */
    public static List<RoleType> getManagerRoles() {
        return List.of(SUPER_ADMIN, GLOBAL_ADMIN, ADMIN, MANAGER);
    }

    /**
     * Roles that ADMIN can assign (excludes SUPER_ADMIN).
     */
    public static List<RoleType> getAdminAssignableRoles() {
        return List.of(GLOBAL_ADMIN, ADMIN, EXECUTIVE, ASSOCIATE,
                SR_ASSOCIATE, ASSISTANT_MANAGER, COORDINATOR,
                FIELD_ASSOCIATE, PH_HEAD, PH_ASSOCIATE);
    }

    /**
     * Roles that SUPER_ADMIN can assign (all roles).
     */
    public static List<RoleType> getSuperAdminAssignableRoles() {
        return List.of(values()); // All roles
    }

    /**
     * Parse from DB string value (case-insensitive).
     */
    public static RoleType fromDbValue(String value) {
        for (RoleType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown role type: " + value);
    }
}