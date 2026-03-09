package com.tbcpl.workforce.common.constants;

/**
 * Validation error messages
 */
public final class ValidationMessages {

    private ValidationMessages() {
        // Prevent instantiation
    }

    // Employee Validation
    public static final String EMPLOYEE_NOT_FOUND = "Employee not found with ID: %s";
    public static final String EMPLOYEE_EMAIL_EXISTS = "Email already exists: %s";
    public static final String EMPLOYEE_EMPID_EXISTS = "Employee ID already exists: %s";
    public static final String EMPLOYEE_FIRST_NAME_REQUIRED = "First name is required";
    public static final String EMPLOYEE_LAST_NAME_REQUIRED = "Last name is required";
    public static final String EMPLOYEE_EMAIL_REQUIRED = "Email is required";
    public static final String EMPLOYEE_PASSWORD_REQUIRED = "Password is required";
    public static final String EMPLOYEE_DEPARTMENT_REQUIRED = "Department is required";
    public static final String EMPLOYEE_ROLE_REQUIRED = "Role is required";

    // Department Validation
    public static final String DEPARTMENT_NOT_FOUND = "Department not found with ID: %s";
    public static final String DEPARTMENT_NAME_EXISTS = "Department already exists: %s";
    public static final String DEPARTMENT_NAME_REQUIRED = "Department name is required";
    public static final String DEPARTMENT_NAME_LENGTH = "Department name must be between 2 and 50 characters";
    public static final String DEPARTMENT_IN_USE = "Cannot delete department. It is assigned to %d employee(s)";

    // Role Validation
    public static final String ROLE_NOT_FOUND = "Role not found with ID: %s";
    public static final String ROLE_NAME_EXISTS = "Role already exists: %s";
    public static final String ROLE_NAME_REQUIRED = "Role name is required";
    public static final String ROLE_NAME_LENGTH = "Role name must be between 2 and 50 characters";
    public static final String ROLE_IN_USE = "Cannot delete role. It is assigned to %d employee(s)";

    // Authentication Validation
    public static final String INVALID_CREDENTIALS = "Invalid email or password";
    public static final String EMAIL_FORMAT_INVALID = "Email must end with @gnsp.co.in";
    public static final String PASSWORD_FORMAT_INVALID = "Password must be alphanumeric with minimum 8 characters";
    public static final String CURRENT_PASSWORD_INCORRECT = "Current password is incorrect";
    public static final String PASSWORD_EXPIRED = "Your password has expired. Please update your password";
    public static final String PASSWORD_EXPIRY_WARNING = "Your password will expire in %d days. Please update your password";

    // Authorization Validation
    public static final String UNAUTHORIZED_DEPARTMENT_MANAGEMENT = "Only ADMIN can manage departments";
    public static final String UNAUTHORIZED_ROLE_MANAGEMENT = "Only ADMIN can manage roles";
    public static final String UNAUTHORIZED_EMPLOYEE_MANAGEMENT = "Only HR or ADMIN can manage employees";
    public static final String UNAUTHORIZED_PASSWORD_RESET = "Only HR or ADMIN can reset employee passwords";
    public static final String UNAUTHORIZED_LOGIN_ATTEMPTS_VIEW = "Only HR or ADMIN can view login attempts";

    // Session Validation
    public static final String DUPLICATE_SESSION = "You are already logged in on another device. You need to logout first";
    public static final String SESSION_EXPIRED = "Your session has expired. Please login again";
    public static final String DEVICE_ID_REQUIRED = "Device identifier is required";

    // General Validation
    public static final String FIELD_REQUIRED = "%s is required";
    public static final String FIELD_LENGTH = "%s must be between %d and %d characters";
    public static final String FIELD_INVALID_FORMAT = "Invalid %s format";
}
