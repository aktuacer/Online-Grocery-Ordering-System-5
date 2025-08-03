package com.grocery.ordering;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Main Spring Boot Application Class for Online Grocery Ordering System
 * 
 * @author Chirag Singhal (chirag127)
 * @version 1.0.0
 */
@SpringBootApplication
public class OnlineGroceryOrderingSystemApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(OnlineGroceryOrderingSystemApplication.class, args);
        System.out.println("=================================================");
        System.out.println("Online Grocery Ordering System Started Successfully!");
        System.out.println("Backend API: http://localhost:8080");
        System.out.println("Admin Dashboard: http://localhost:8080/admin");
        System.out.println("API Documentation: http://localhost:8080/api/docs");
        System.out.println("=================================================");
    }

    /**
     * Password encoder bean for secure password hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS configuration for cross-origin requests
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:4200", "http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
