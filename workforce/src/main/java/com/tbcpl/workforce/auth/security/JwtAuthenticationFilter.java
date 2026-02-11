package com.tbcpl.workforce.auth.security;

import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.service.EmployeeService;
import com.tbcpl.workforce.auth.service.EmployeeSessionService;
import com.tbcpl.workforce.common.constants.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 * Intercepts all requests and validates JWT token
 * Runs once per request
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final EmployeeService employeeService;
    private final EmployeeSessionService sessionService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Extract Authorization header
        final String authHeader = request.getHeader(SecurityConstants.JWT_HEADER_STRING);

        // Skip if no authorization header or doesn't start with "Bearer "
        if (authHeader == null || !authHeader.startsWith(SecurityConstants.JWT_TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract JWT token (remove "Bearer " prefix)
            final String jwt = authHeader.substring(7);
            final String username = jwtUtil.extractUsername(jwt);
            final String empId = jwtUtil.extractEmpId(jwt); // ✅ Extract empId from JWT

            // Validate token and authenticate
            if (username != null && empId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Validate token
                if (!jwtUtil.validateToken(jwt, username)) {
                    log.warn("Invalid JWT token for user: {}", username);
                    filterChain.doFilter(request, response);
                    return;
                }

                // Check if session is still valid
                if (!sessionService.isSessionValid(jwt)) {
                    log.warn("Session expired or invalid for user: {}", username);
                    filterChain.doFilter(request, response);
                    return;
                }

                // Load employee details
                Employee employee = employeeService.getEmployeeEntityByEmail(username);

                // Create CustomUserDetails with empId as principal
                CustomUserDetails userDetails = new CustomUserDetails(employee);

                // ✅ CHANGED: Use empId as principal instead of userDetails
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        empId, // Use empId as principal (authentication.getName() will return this)
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Update session activity
                sessionService.updateSessionActivity(jwt);

                log.debug("✅ User authenticated: {} (empId: {}) with authorities: {}", username, empId, userDetails.getAuthorities());
            }

        } catch (Exception e) {
            log.error("❌ Cannot set user authentication", e);
        }

        filterChain.doFilter(request, response);
    }
}
