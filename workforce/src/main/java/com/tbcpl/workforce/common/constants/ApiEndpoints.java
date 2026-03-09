package com.tbcpl.workforce.common.constants;

/**
 * Centralized API endpoint constants for the entire application.
 * All controllers must reference these constants — no hardcoded strings.
 */
public final class ApiEndpoints {

    private ApiEndpoints() {}

    // ─── API version prefix ───────────────────────────────────────────────────
    public static final String API_V1 = "/api/v1";

    // ─── Department-based base paths ──────────────────────────────────────────
    public static final String AUTH_BASE      = API_V1 + "/auth";
    public static final String ADMIN_BASE     = API_V1 + "/admin";
    public static final String HR_BASE        = API_V1 + "/hr";
    public static final String OPERATION_BASE = API_V1 + "/operation";
    public static final String ACCOUNTS_BASE  = API_V1 + "/accounts";

    // ─── Auth endpoints ───────────────────────────────────────────────────────
    public static final String AUTH_LOGIN           = "/login";
    public static final String AUTH_LOGOUT          = "/logout";
    public static final String AUTH_CHANGE_PASSWORD = "/change-password";
    public static final String AUTH_RESET_PASSWORD  = "/reset-password";
    public static final String AUTH_PROFILE         = "/profile";
    public static final String AUTH_VERIFY_EMAIL    = "/verify-email";       // ← NEW
    public static final String AUTH_RESEND_VERIFY   = "/resend-verification/{empId}"; // ← NEW

    // ─── Role endpoints ───────────────────────────────────────────────────────
    public static final String ROLES            = "/roles";
    public static final String ROLE_BY_ID       = "/roles/{id}";
    public static final String ROLES_ASSIGNABLE = "/roles/assignable";       // ← NEW

    // ─── Department endpoints ─────────────────────────────────────────────────
    public static final String DEPARTMENTS      = "/departments";
    public static final String DEPARTMENT_BY_ID = "/departments/{id}";

    // ─── Employee endpoints ───────────────────────────────────────────────────
    public static final String EMPLOYEES              = "/employees";
    public static final String EMPLOYEE_BY_ID         = "/employees/{id}";
    public static final String EMPLOYEE_BY_EMP_ID     = "/employees/emp/{empId}";
    public static final String EMPLOYEE_PROFILE_PHOTO = "/employees/{id}/profile-photo"; // ← NEW
    public static final String EMPLOYEE_REPORTING_MGRS = "/employees/reporting-managers"; // ← NEW

    // ─── Login Attempt Log endpoints ──────────────────────────────────────────
    public static final String LOGIN_ATTEMPTS         = "/login-attempts";
    public static final String LOGIN_ATTEMPT_BY_ID    = "/login-attempts/{id}";
    public static final String LOGIN_ATTEMPTS_BLOCKED    = "/login-attempts/blocked";
    public static final String LOGIN_ATTEMPTS_BY_EMPLOYEE = "/login-attempts/employee/{empId}";

    public static final String PASSWORD_BASE          = "/password";
    public static final String PASSWORD_CHANGE        = "/password/change";
    public static final String PASSWORD_RESET_REQUEST = "/password/reset-request";
    public static final String PASSWORD_RESET_CONFIRM = "/password/reset-confirm";
    public static final String PASSWORD_ADMIN_RESET   = "/password/admin-reset";
}
