package com.tbcpl.workforce.common.constants;

/**
 * Security-related constants for JWT and authentication
 */
public final class SecurityConstants {

    private SecurityConstants() {
        // Prevent instantiation
    }

    // JWT Configuration
    public static final String JWT_SECRET = "TBCPLWorkforceSecretKey2026VeryLongSecretKeyForHS512AlgorithmMinimum512Bits";
    public static final long JWT_EXPIRATION_MS = 8 * 60 * 60 * 1000; // 8 hours
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String JWT_HEADER_STRING = "Authorization";

    // JWT Claims
    public static final String CLAIM_EMP_ID = "empId";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_DEPARTMENT = "department";
    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_FULL_NAME = "fullName";

    // Session Configuration
    public static final int SESSION_TIMEOUT_HOURS = 8;
    public static final int PASSWORD_EXPIRY_MONTHS = 2;
    public static final int PASSWORD_EXPIRY_DAYS = 60;

    // Security Messages
    public static final String INVALID_TOKEN_MESSAGE = "Invalid or expired token";
    public static final String UNAUTHORIZED_MESSAGE = "You do not have permission to access this resource";
    public static final String DUPLICATE_SESSION_MESSAGE = "You are already logged in on another device. You need to logout first";
    public static final String SESSION_EXPIRED_MESSAGE = "Your session has expired. Please login again";
}
