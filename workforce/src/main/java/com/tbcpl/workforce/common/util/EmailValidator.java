package com.tbcpl.workforce.common.util;

import com.tbcpl.workforce.common.constants.EmailDomainConstants;
import org.springframework.stereotype.Component;

@Component
public class EmailValidator {

    private static final java.util.regex.Pattern EMAIL_PATTERN =
            java.util.regex.Pattern.compile(
                    "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    /**
     * Validates full email format AND domain restriction.
     */
    public void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        String trimmed = email.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        String domain = trimmed.substring(trimmed.indexOf('@') + 1);
        if (!EmailDomainConstants.ALLOWED_DOMAINS.contains(domain)) {
            throw new IllegalArgumentException(
                    "Email domain not allowed. Accepted: " +
                            EmailDomainConstants.ALLOWED_DOMAINS_DISPLAY);
        }
    }
}
