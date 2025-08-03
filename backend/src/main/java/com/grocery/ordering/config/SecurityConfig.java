package com.grocery.ordering.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for the Online Grocery Ordering System
 * Implements comprehensive security measures including CORS, CSRF protection,
 * security headers, and session management
 *
 * @author Chirag Singhal (chirag127)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure security filter chain with comprehensive security measures
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CORS configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // CSRF protection - disabled for API endpoints but can be enabled for JSP pages
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**") // Disable CSRF for API endpoints
                .csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse())
            )

            // Session management
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .and()
                .sessionFixation().migrateSession()
                .invalidSessionUrl("/login")
            )

            // Authorization rules
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/api/auth/login", "/api/auth/logout", "/api/auth/status").permitAll()
                .requestMatchers("/api/customers/register", "/api/customers/check-email").permitAll()

                // Admin endpoints
                .requestMatchers("/admin/**", "/jsp/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/products").hasRole("ADMIN") // POST, PUT, DELETE
                .requestMatchers("/api/customers/search", "/api/customers").hasRole("ADMIN") // Admin customer management
                .requestMatchers("/api/orders/statistics", "/api/orders/status/**", "/api/orders/date-range").hasRole("ADMIN")

                // Customer endpoints
                .requestMatchers("/api/customers/{customerId}").hasAnyRole("ADMIN", "CUSTOMER")
                .requestMatchers("/api/orders/customer/**").hasAnyRole("ADMIN", "CUSTOMER")
                .requestMatchers("/api/orders/{orderId}").hasAnyRole("ADMIN", "CUSTOMER")

                // Authenticated endpoints
                .requestMatchers("/api/products/**", "/api/orders/**").authenticated()
                .requestMatchers("/api/auth/change-password").authenticated()

                // All other requests require authentication
                .anyRequest().authenticated()
            )

            // Security headers
            .headers(headers -> headers
                .frameOptions().deny()
                .contentTypeOptions().and()
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                )
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                .and()
                .addHeaderWriter((request, response) -> {
                    response.setHeader("X-Content-Type-Options", "nosniff");
                    response.setHeader("X-Frame-Options", "DENY");
                    response.setHeader("X-XSS-Protection", "1; mode=block");
                    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                    response.setHeader("Pragma", "no-cache");
                    response.setHeader("Expires", "0");
                })
            )

            // Form login configuration
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )

            // Logout configuration
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .permitAll()
            )

            // Exception handling
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/access-denied")
                .authenticationEntryPoint((request, response, authException) -> {
                    if (request.getRequestURI().startsWith("/api/")) {
                        response.setStatus(401);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"Authentication required\"}");
                    } else {
                        response.sendRedirect("/login");
                    }
                })
            );

        return http.build();
    }

    /**
     * CORS configuration to allow Angular frontend communication
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow specific origins (Angular development server)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200",
            "http://127.0.0.1:4200"
        ));

        // Allow specific HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // Allow specific headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "X-CSRF-TOKEN"
        ));

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Expose headers to the client
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "X-CSRF-TOKEN"
        ));

        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
