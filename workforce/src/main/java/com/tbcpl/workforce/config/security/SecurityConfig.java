package com.tbcpl.workforce.config.security;

import com.tbcpl.workforce.auth.security.JwtAuthenticationFilter;
import com.tbcpl.workforce.common.constants.ApiEndpoints;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security Configuration
 * Configures JWT-based authentication, authorization, and CORS
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configure security filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS with custom configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Disable CSRF (using JWT, stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // ✅ CRITICAL: Allow all OPTIONS requests for CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ============================================
                        // PUBLIC ENDPOINTS (No Authentication) - TESTING
                        // ============================================
                        .requestMatchers(HttpMethod.POST, ApiEndpoints.AUTH_BASE + ApiEndpoints.AUTH_LOGIN).permitAll()

                        // TEMPORARY: Allow operation module without authentication for frontend testing
                        .requestMatchers("/api/v1/operation/**").permitAll()

                        // ============================================
                        // AUTH MODULE ENDPOINTS
                        // ============================================

                        // Department endpoints (ADMIN only for POST/PUT/DELETE, HR can GET)
                        .requestMatchers(HttpMethod.POST, ApiEndpoints.AUTH_BASE + ApiEndpoints.DEPARTMENTS).hasAuthority("DEPARTMENT_ADMIN")
                        .requestMatchers(HttpMethod.PUT, ApiEndpoints.AUTH_BASE + "/departments/**").hasAuthority("DEPARTMENT_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, ApiEndpoints.AUTH_BASE + "/departments/**").hasAuthority("DEPARTMENT_ADMIN")
                        .requestMatchers(HttpMethod.GET, ApiEndpoints.AUTH_BASE + ApiEndpoints.DEPARTMENTS).hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")
                        .requestMatchers(HttpMethod.GET, ApiEndpoints.AUTH_BASE + "/departments/**").hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")

                        // Role endpoints (ADMIN only for POST/PUT/DELETE, HR can GET)
                        .requestMatchers(HttpMethod.POST, ApiEndpoints.AUTH_BASE + ApiEndpoints.ROLES).hasAuthority("DEPARTMENT_ADMIN")
                        .requestMatchers(HttpMethod.PUT, ApiEndpoints.AUTH_BASE + "/roles/**").hasAuthority("DEPARTMENT_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, ApiEndpoints.AUTH_BASE + "/roles/**").hasAuthority("DEPARTMENT_ADMIN")
                        .requestMatchers(HttpMethod.GET, ApiEndpoints.AUTH_BASE + ApiEndpoints.ROLES).hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")
                        .requestMatchers(HttpMethod.GET, ApiEndpoints.AUTH_BASE + "/roles/**").hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")

                        // ✅ SPECIFIC RULE FIRST: Get employee by ID (any authenticated user)
                        .requestMatchers(HttpMethod.GET, ApiEndpoints.AUTH_BASE + "/employees/*").authenticated()

                        // ✅ SPECIFIC RULE: Get employee by empId (any authenticated user)
                        .requestMatchers(HttpMethod.GET, ApiEndpoints.AUTH_BASE + "/employees/empId/**").authenticated()

                        // ✅ GENERAL RULE AFTER: Employee management endpoints (HR and ADMIN can manage)
                        .requestMatchers(HttpMethod.POST, ApiEndpoints.AUTH_BASE + ApiEndpoints.EMPLOYEES).hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")
                        .requestMatchers(HttpMethod.PUT, ApiEndpoints.AUTH_BASE + ApiEndpoints.EMPLOYEES + "/**").hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")
                        .requestMatchers(HttpMethod.DELETE, ApiEndpoints.AUTH_BASE + ApiEndpoints.EMPLOYEES + "/**").hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")

                        // Login attempt endpoints (HR and ADMIN can view)
                        .requestMatchers(ApiEndpoints.AUTH_BASE + ApiEndpoints.LOGIN_ATTEMPTS + "/**").hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")

                        // Password reset (HR and ADMIN only)
                        .requestMatchers(HttpMethod.POST, ApiEndpoints.AUTH_BASE + ApiEndpoints.AUTH_RESET_PASSWORD).hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")

                        // Change password (any authenticated user)
                        .requestMatchers(HttpMethod.POST, ApiEndpoints.AUTH_BASE + ApiEndpoints.AUTH_CHANGE_PASSWORD).authenticated()

                        // Get profile (any authenticated user)
                        .requestMatchers(HttpMethod.GET, ApiEndpoints.AUTH_BASE + ApiEndpoints.AUTH_PROFILE).authenticated()

                        // Logout (any authenticated user)
                        .requestMatchers(HttpMethod.POST, ApiEndpoints.AUTH_BASE + ApiEndpoints.AUTH_LOGOUT).authenticated()

                        // ============================================
                        // OTHER DEPARTMENT MODULE ENDPOINTS
                        // ============================================

                        // HR module - HR and ADMIN can access
                        .requestMatchers("/api/v1/hr/**").hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")

                        // Accounts module - ACCOUNTS and ADMIN can access
                        .requestMatchers("/api/v1/accounts/**").hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_ACCOUNTS")

                        // Admin module - ADMIN only
                        .requestMatchers("/api/v1/admin/**").hasAuthority("DEPARTMENT_ADMIN")

                        // ============================================
                        // DEFAULT: All other requests must be authenticated
                        // ============================================
                        .anyRequest().authenticated()
                )

                // Stateless session management (JWT-based)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS Configuration
     * Allows frontend applications to access backend APIs
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow frontend origins (development and production)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",      // Vite (React/Vue) default
                "http://localhost:3000",      // Create React App default
                "http://localhost:4200",      // Angular default
                "http://localhost:8081",      // Alternative port
                "http://127.0.0.1:5173",      // Alternative localhost
                "http://127.0.0.1:3000"       // Alternative localhost
                // Add production URL here when deploying
                // "https://yourproductiondomain.com"
        ));

        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "PATCH",
                "OPTIONS"
        ));

        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Allow credentials (cookies, authorization headers, etc.)
        configuration.setAllowCredentials(true);

        // Expose headers to frontend JavaScript
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Content-Disposition",
                "Content-Length"
        ));

        // Cache preflight response for 1 hour (3600 seconds)
        configuration.setMaxAge(3600L);

        // Register CORS configuration for all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * Authentication manager bean
     * Required for authentication process
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
