package com.tbcpl.workforce.common.util;

import com.tbcpl.workforce.common.exception.InvalidCredentialsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Utility class to validate email addresses
 * Enforces @gnsp.co.in domain requirement
 */
@Component
@Slf4j
public class EmailValidator {

    private static final String ALLOWED_DOMAIN = "@gnsp.co.in";
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._-]+@gnsp\\.co\\.in$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    /**
     * Validate if email ends with @gnsp.co.in and has valid format
     * Format: username.year@gnsp.co.in (e.g., john.2026@gnsp.co.in)
     *
     * @param email Email address to validate
     * @throws InvalidCredentialsException if email is invalid
     */
    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            log.error("Email validation failed: Email is null or empty");
            throw new InvalidCredentialsException("Email is required");
        }

        if (!email.endsWith(ALLOWED_DOMAIN)) {
            log.error("Email validation failed: {} does not end with {}", email, ALLOWED_DOMAIN);
            throw new InvalidCredentialsException("Email must end with " + ALLOWED_DOMAIN);
        }

        if (!pattern.matcher(email).matches()) {
            log.error("Email validation failed: {} has invalid format", email);
            throw new InvalidCredentialsException("Invalid email format. Expected format: username.year@gnsp.co.in");
        }

        log.info("Email validation successful: {}", email);
    }

    /**
     * Check if email is valid without throwing exception
     *
     * @param email Email address to check
     * @return true if valid, false otherwise
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.endsWith(ALLOWED_DOMAIN) && pattern.matcher(email).matches();
    }

    /**
     * Extract username from email
     * Example: "john.2026@gnsp.co.in" → "john.2026"
     *
     * @param email Email address
     * @return Username part before @
     */
    public String extractUsername(String email) {
        if (email == null || !email.contains("@")) {
            return null;
        }
        return email.substring(0, email.indexOf("@"));
    }
}
