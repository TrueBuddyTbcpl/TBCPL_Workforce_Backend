package com.tbcpl.workforce.common.util;

import com.tbcpl.workforce.common.exception.InvalidPasswordException; // ← CHANGED
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class PasswordValidator {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&#^()_+\\-=\\[\\]{};':\",./<>?]{8,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 50;

    public void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            log.error("Password validation failed: Password is null or empty");
            throw new InvalidPasswordException("Password is required"); // ← CHANGED
        }

        if (password.length() < MIN_LENGTH) {
            log.error("Password validation failed: Length is less than {}", MIN_LENGTH);
            throw new InvalidPasswordException(                         // ← CHANGED
                    "Password must be at least " + MIN_LENGTH + " characters long");
        }

        if (password.length() > MAX_LENGTH) {
            log.error("Password validation failed: Length exceeds {}", MAX_LENGTH);
            throw new InvalidPasswordException(                         // ← CHANGED
                    "Password must not exceed " + MAX_LENGTH + " characters");
        }

        if (!pattern.matcher(password).matches()) {
            log.error("Password validation failed: Does not meet alphanumeric requirement");
            throw new InvalidPasswordException(                         // ← CHANGED
                    "Password must contain at least one letter and one number. " +
                            "Special characters (@$!%*?&) are allowed");
        }

        log.info("Password validation successful");
    }

    public boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) return false;
        return password.length() >= MIN_LENGTH
                && password.length() <= MAX_LENGTH
                && pattern.matcher(password).matches();
    }

    public PasswordStrength getPasswordStrength(String password) {
        if (password == null || password.length() < MIN_LENGTH) return PasswordStrength.WEAK;

        int score = 0;
        if (password.length() >= 12) score++;
        if (password.length() >= 16) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[@$!%*?&].*")) score++;

        if (score <= 3) return PasswordStrength.WEAK;
        if (score <= 5) return PasswordStrength.MEDIUM;
        return PasswordStrength.STRONG;
    }

    public enum PasswordStrength {
        WEAK, MEDIUM, STRONG
    }
}
