package com.tbcpl.workforce.auth.security;

import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.common.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for JWT token generation and validation
 */
@Component
@Slf4j
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil() {
        // Create secret key from constant
        this.secretKey = Keys.hmacShaKeyFor(
                SecurityConstants.JWT_SECRET.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Generate JWT token for employee
     */
    public String generateToken(Employee employee) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.CLAIM_EMP_ID, employee.getEmpId());
        claims.put(SecurityConstants.CLAIM_EMAIL, employee.getEmail());
        claims.put(SecurityConstants.CLAIM_DEPARTMENT, employee.getDepartment().getDepartmentName());
        claims.put(SecurityConstants.CLAIM_ROLE, employee.getRole().getRoleName());
        claims.put(SecurityConstants.CLAIM_FULL_NAME, employee.getFullName());

        return createToken(claims, employee.getEmail());
    }

    /**
     * Create JWT token with claims
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + SecurityConstants.JWT_EXPIRATION_MS);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extract username (email) from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract employee ID from token
     */
    public String extractEmpId(String token) {
        return extractClaim(token, claims -> claims.get(SecurityConstants.CLAIM_EMP_ID, String.class));
    }

    /**
     * Extract department from token
     */
    public String extractDepartment(String token) {
        return extractClaim(token, claims -> claims.get(SecurityConstants.CLAIM_DEPARTMENT, String.class));
    }

    /**
     * Extract role from token
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get(SecurityConstants.CLAIM_ROLE, String.class));
    }

    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Check if token is expired
     */
    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.error("Error checking token expiration", e);
            return true;
        }
    }

    /**
     * Validate token
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("Error validating token", e);
            return false;
        }
    }

    /**
     * Validate token (without username check)
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Error validating token", e);
            return false;
        }
    }
}
