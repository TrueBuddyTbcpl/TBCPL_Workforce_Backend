// com/tbcpl/workforce/auth/repository/EmailVerificationTokenRepository.java
package com.tbcpl.workforce.auth.repository;

import com.tbcpl.workforce.auth.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    /** Find latest unused, unexpired token for an employee */
    @Query("""
    SELECT t FROM EmailVerificationToken t
    WHERE t.employee.id = :employeeId
      AND t.used = false
      AND t.expiresAt > :now
    ORDER BY t.createdAt DESC
    """)
    Optional<EmailVerificationToken> findValidTokenByEmployeeId(
            @Param("employeeId") Long employeeId,
            @Param("now") LocalDateTime now);

    /** Invalidate all previous tokens for employee before issuing a new one */
    @Modifying
    @Transactional
    @Query("""
    UPDATE EmailVerificationToken t 
    SET t.used = true 
    WHERE t.employee.id = :employeeId 
    AND t.used = false
    """)
    void invalidateAllTokensForEmployee(@Param("employeeId") Long employeeId);

    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.employee.id = :employeeId")
    void deleteByEmployeeId(@Param("employeeId") Long employeeId);

    /** Cleanup expired tokens (run via scheduled task) */
    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.expiresAt < :cutoff")
    int deleteExpiredTokens(@Param("cutoff") LocalDateTime cutoff);
}
