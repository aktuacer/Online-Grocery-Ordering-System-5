package com.grocery.ordering.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for login requests
 * 
 * @author Chirag Singhal (chirag127)
 */
public class LoginRequest {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    private String userType; // "ADMIN" or "CUSTOMER"

    // Default constructor
    public LoginRequest() {}

    // Constructor
    public LoginRequest(String username, String password, String userType) {
        this.username = username;
        this.password = password;
        this.userType = userType;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "username='" + username + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}
