package com.tbcpl.workforce.common.util;

import com.tbcpl.workforce.common.exception.InvalidCredentialsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Utility class to validate password strength
 * Enforces alphanumeric requirement with special characters allowed
 */
@Component
@Slf4j
public class PasswordValidator {

    // At least 8 characters, must contain at least one letter and one number
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 50;

    /**
     * Validate password against security requirements
     * - Minimum 8 characters
     * - Must contain at least one letter
     * - Must contain at least one number
     * - May contain special characters: @$!%*?&
     *
     * @param password Password to validate
     * @throws InvalidCredentialsException if password is invalid
     */
    public void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            log.error("Password validation failed: Password is null or empty");
            throw new InvalidCredentialsException("Password is required");
        }

        if (password.length() < MIN_LENGTH) {
            log.error("Password validation failed: Length is less than {}", MIN_LENGTH);
            throw new InvalidCredentialsException("Password must be at least " + MIN_LENGTH + " characters long");
        }

        if (password.length() > MAX_LENGTH) {
            log.error("Password validation failed: Length exceeds {}", MAX_LENGTH);
            throw new InvalidCredentialsException("Password must not exceed " + MAX_LENGTH + " characters");
        }

        if (!pattern.matcher(password).matches()) {
            log.error("Password validation failed: Does not meet alphanumeric requirement");
            throw new InvalidCredentialsException(
                    "Password must contain at least one letter and one number. Special characters (@$!%*?&) are allowed"
            );
        }

        log.info("Password validation successful");
    }

    /**
     * Check if password is valid without throwing exception
     *
     * @param password Password to check
     * @return true if valid, false otherwise
     */
    public boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return password.length() >= MIN_LENGTH
                && password.length() <= MAX_LENGTH
                && pattern.matcher(password).matches();
    }

    /**
     * Get password strength level (for UI feedback)
     *
     * @param password Password to evaluate
     * @return Strength level: WEAK, MEDIUM, STRONG
     */
    public PasswordStrength getPasswordStrength(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return PasswordStrength.WEAK;
        }

        int score = 0;

        // Check length
        if (password.length() >= 12) score++;
        if (password.length() >= 16) score++;

        // Check for uppercase
        if (password.matches(".*[A-Z].*")) score++;

        // Check for lowercase
        if (password.matches(".*[a-z].*")) score++;

        // Check for digits
        if (password.matches(".*\\d.*")) score++;

        // Check for special characters
        if (password.matches(".*[@$!%*?&].*")) score++;

        if (score <= 3) return PasswordStrength.WEAK;
        if (score <= 5) return PasswordStrength.MEDIUM;
        return PasswordStrength.STRONG;
    }

    public enum PasswordStrength {
        WEAK, MEDIUM, STRONG
    }
}
