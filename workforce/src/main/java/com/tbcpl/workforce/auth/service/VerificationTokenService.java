package com.tbcpl.workforce.auth.service;

import com.tbcpl.workforce.auth.entity.EmailVerificationToken;
import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.repository.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Handles token persistence in its own Spring proxy bean.
 * Extracted from EmailVerificationService to avoid @Async + @Transactional
 * self-invocation proxy bypass issue.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationTokenService {

    private final EmailVerificationTokenRepository tokenRepository;

    private static final int TOKEN_EXPIRY_HOURS = 24;

    @Transactional
    public String createAndSaveToken(Employee employee) {
        // Delete previous tokens for this employee
        tokenRepository.deleteByEmployeeId(employee.getId());

        String token = UUID.randomUUID().toString();
        tokenRepository.save(EmailVerificationToken.builder()
                .token(token)
                .employee(employee)
                .expiresAt(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS))
                .used(false)
                .build());

        log.info("Verification token created for employee ID: {}", employee.getId());
        return token;
    }
}
