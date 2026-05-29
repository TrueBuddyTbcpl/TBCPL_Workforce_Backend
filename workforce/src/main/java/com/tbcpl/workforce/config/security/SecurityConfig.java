package com.tbcpl.workforce.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // ── CORS preflight ────────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ── Public auth endpoints ─────────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST,
                                ApiEndpoints.AUTH_BASE + ApiEndpoints.AUTH_LOGIN).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                ApiEndpoints.AUTH_BASE + ApiEndpoints.AUTH_VERIFY_EMAIL).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                ApiEndpoints.AUTH_BASE + "/resend-verification").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                ApiEndpoints.AUTH_BASE + "/resend-verification/**").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                ApiEndpoints.AUTH_BASE + ApiEndpoints.PASSWORD_RESET_CONFIRM).permitAll()

                        // ── TEMPORARY: Operation module open for frontend testing ──────────────
                        .requestMatchers("/api/v1/operation/**").permitAll()

                        // ── Common endpoints (accessible by all authenticated users) ───────────
                        .requestMatchers("/api/v1/common/**").authenticated()

                        // ── Department endpoints ──────────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST,
                                ApiEndpoints.AUTH_BASE + ApiEndpoints.DEPARTMENTS)
                        .hasAuthority("DEPARTMENT_ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                ApiEndpoints.AUTH_BASE + "/departments/**")
                        .hasAuthority("DEPARTMENT_ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                ApiEndpoints.AUTH_BASE + "/departments/**")
                        .hasAuthority("DEPARTMENT_ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                ApiEndpoints.AUTH_BASE + ApiEndpoints.DEPARTMENTS)
                        .hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")
                        .requestMatchers(HttpMethod.GET,
                                ApiEndpoints.AUTH_BASE + "/departments/**")
                        .hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")

                        // ── Role endpoints ────────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST,
                                ApiEndpoints.AUTH_BASE + ApiEndpoints.ROLES)
                        .hasAuthority("DEPARTMENT_ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                ApiEndpoints.AUTH_BASE + "/roles/**")
                        .hasAuthority("DEPARTMENT_ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                ApiEndpoints.AUTH_BASE + "/roles/**")
                        .hasAuthority("DEPARTMENT_ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                ApiEndpoints.AUTH_BASE + ApiEndpoints.ROLES)
                        .hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")
                        .requestMatchers(HttpMethod.GET,
                                ApiEndpoints.AUTH_BASE + "/roles/**")
                        .hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")

                        // ── Employee endpoints ────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                ApiEndpoints.AUTH_BASE + "/employees/*").authenticated()
                        .requestMatchers(HttpMethod.GET,
                                ApiEndpoints.AUTH_BASE + "/employees/empId/**").authenticated()
                        .requestMatchers(HttpMethod.POST,
                                ApiEndpoints.AUTH_BASE + ApiEndpoints.EMPLOYEES)
                        .hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")
                        .requestMatchers(HttpMethod.PUT,
                                ApiEndpoints.AUTH_BASE + ApiEndpoints.EMPLOYEES + "/**")
                        .hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")
                        .requestMatchers(HttpMethod.DELETE,
                                ApiEndpoints.AUTH_BASE + ApiEndpoints.EMPLOYEES + "/**")
                        .hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")

                        // ── Login attempt endpoints ───────────────────────────────────────────
                        .requestMatchers(
                                ApiEndpoints.AUTH_BASE + ApiEndpoints.LOGIN_ATTEMPTS + "/**")
                        .hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")

                        // ── Password endpoints ────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST,
                                ApiEndpoints.AUTH_BASE + ApiEndpoints.AUTH_RESET_PASSWORD)
                        .hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")
                        .requestMatchers(HttpMethod.POST,
                                ApiEndpoints.AUTH_BASE + ApiEndpoints.AUTH_CHANGE_PASSWORD)
                        .authenticated()
                        .requestMatchers(HttpMethod.POST,
                                ApiEndpoints.AUTH_BASE + ApiEndpoints.PASSWORD_CHANGE)
                        .authenticated()
                        .requestMatchers(HttpMethod.POST,
                                ApiEndpoints.AUTH_BASE + ApiEndpoints.PASSWORD_RESET_REQUEST)
                        .authenticated()
                        .requestMatchers(HttpMethod.POST,
                                ApiEndpoints.AUTH_BASE + ApiEndpoints.PASSWORD_ADMIN_RESET)
                        .hasAuthority("DEPARTMENT_ADMIN")

                        // ── Profile & Logout ──────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                ApiEndpoints.AUTH_BASE + ApiEndpoints.AUTH_PROFILE).authenticated()
                        .requestMatchers(HttpMethod.POST,
                                ApiEndpoints.AUTH_BASE + ApiEndpoints.AUTH_LOGOUT).authenticated()

                        // ── HR module ─────────────────────────────────────────────────────────
                        .requestMatchers("/api/v1/hr/**")
                        .hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_HR")

                        // ── Accounts module ───────────────────────────────────────────────────
                        .requestMatchers("/api/v1/accounts/**")
                        .hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_ACCOUNTS")

                        // ── Admin module (proposals, clients, etc.) ───────────────────────────
                        .requestMatchers("/api/v1/admin/**")
                        .hasAuthority("DEPARTMENT_ADMIN")

                        // ── LOA Assets ────────────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/grnd-operation/loa/assets").authenticated()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/grnd-operation/loa/assets/**")
                        .hasAuthority("DEPARTMENT_ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/grnd-operation/loa/assets/**")
                        .hasAuthority("DEPARTMENT_ADMIN")

                        // ── GRND-OPERATION LOA endpoints ──────────────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/grnd-operation/loa/*/preview").authenticated()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/grnd-operation/loa/*/send-mail")
                        .hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_OPERATION")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/grnd-operation/loa/dropdown/**")
                        .hasAuthority("DEPARTMENT_ADMIN")
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/grnd-operation/loa")
                        .hasAuthority("DEPARTMENT_ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/grnd-operation/loa/**")
                        .hasAuthority("DEPARTMENT_ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/grnd-operation/loa/**")
                        .hasAnyAuthority("DEPARTMENT_ADMIN", "DEPARTMENT_OPERATION")

                        // ── Default: all other requests must be authenticated ─────────────────
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "https://tbcontrolcenter.com",
                "https://www.tbcontrolcenter.com",
                "https://tbcpl-workforce.onrender.com",
                "http://localhost:3000"
        ));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Content-Disposition",
                "Content-Length"
        ));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}