package com.tbcpl.workforce.common.constants;

/**
 * API endpoint path constants
 */
public final class ApiEndpoints {

    private ApiEndpoints() {
        // Prevent instantiation
    }

    // Base paths
    public static final String API_VERSION = "/api/v1";
    public static final String AUTH_BASE = API_VERSION + "/auth";

    // Authentication endpoints
    public static final String AUTH_LOGIN = "/login";
    public static final String AUTH_LOGOUT = "/logout";
    public static final String AUTH_CHANGE_PASSWORD = "/change-password";
    public static final String AUTH_RESET_PASSWORD = "/reset-password";
    public static final String AUTH_REFRESH_TOKEN = "/refresh-token";
    public static final String AUTH_PROFILE = "/profile";

    // Department endpoints
    public static final String DEPARTMENTS = "/departments";
    public static final String DEPARTMENT_BY_ID = "/departments/{id}";




    // Role endpoints
    public static final String ROLES = "/roles";
    public static final String ROLE_BY_ID = "/roles/{id}";

    // Employee endpoints
    public static final String EMPLOYEES = "/employees";
    public static final String EMPLOYEE_BY_ID = "/employees/empId/{empId:.+}";
    public static final String EMPLOYEE_BY_DATABASE_ID = "/employees/id/{id}";

    public static final String EMPLOYEE_BY_EMP_ID = "/employees/empId/{empId}";
    public static final String EMPLOYEES_FILTER = "/employees/filter";
    public static final String EMPLOYEES_BY_DEPARTMENT = "/employees/department/{departmentId}";
    public static final String EMPLOYEES_BY_ROLE = "/employees/role/{roleId}";

    // Login attempt endpoints
    public static final String LOGIN_ATTEMPTS = "/login-attempts";
    public static final String LOGIN_ATTEMPTS_BLOCKED = "/login-attempts/blocked";
    public static final String LOGIN_ATTEMPTS_BY_EMPLOYEE = "/login-attempts/employee/{empId}";

    // Public endpoints (no authentication required)
    public static final String[] PUBLIC_ENDPOINTS = {
            AUTH_BASE + AUTH_LOGIN
    };
}
