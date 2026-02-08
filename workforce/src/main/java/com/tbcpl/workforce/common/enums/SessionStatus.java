package com.tbcpl.workforce.common.enums;

/**
 * Enum for employee session status
 */
public enum SessionStatus {
    ACTIVE("Session is active"),
    EXPIRED("Session has expired"),
    LOGGED_OUT("User logged out"),
    FORCE_LOGOUT("Forced logout due to new login");

    private final String description;

    SessionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if session is currently active
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * Check if session can be reused
     */
    public boolean isReusable() {
        return this == ACTIVE;
    }

    /**
     * Check if session is terminated
     */
    public boolean isTerminated() {
        return this == EXPIRED || this == LOGGED_OUT || this == FORCE_LOGOUT;
    }
}
