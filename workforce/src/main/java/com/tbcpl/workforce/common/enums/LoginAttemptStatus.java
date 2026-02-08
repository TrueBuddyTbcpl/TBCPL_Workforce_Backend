package com.tbcpl.workforce.common.enums;

/**
 * Enum for login attempt status
 */
public enum LoginAttemptStatus {
    SUCCESS("Login successful"),
    BLOCKED("Login blocked - already logged in on another device"),
    FAILED("Login failed - invalid credentials"),
    SESSION_EXPIRED("Session expired"),
    PASSWORD_EXPIRED("Password expired");

    private final String description;

    LoginAttemptStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if status indicates a successful login
     */
    public boolean isSuccess() {
        return this == SUCCESS;
    }

    /**
     * Check if status indicates a blocked attempt
     */
    public boolean isBlocked() {
        return this == BLOCKED;
    }

    /**
     * Check if status indicates a failed attempt
     */
    public boolean isFailed() {
        return this == FAILED || this == BLOCKED;
    }
}
