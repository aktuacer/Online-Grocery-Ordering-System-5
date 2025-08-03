package com.grocery.ordering.controller;

import com.grocery.ordering.dto.ApiResponse;
import com.grocery.ordering.dto.LoginRequest;
import com.grocery.ordering.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Authentication operations
 * Handles login, logout, and session management
 * 
 * @author Chirag Singhal (chirag127)
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * User login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@Valid @RequestBody LoginRequest loginRequest, 
                                                    HttpServletRequest request) {
        try {
            AuthService.AuthResult authResult = authService.login(
                loginRequest.getUsername(), 
                loginRequest.getPassword(), 
                loginRequest.getUserType()
            );

            if (authResult.isSuccess()) {
                // Create session
                HttpSession session = request.getSession(true);
                session.setAttribute("userType", authResult.getUserType());
                session.setAttribute("authenticated", true);
                
                if ("ADMIN".equals(authResult.getUserType())) {
                    session.setAttribute("adminUser", authResult.getAdminUser());
                    session.setAttribute("username", authResult.getAdminUser().getUsername());
                } else {
                    session.setAttribute("customer", authResult.getCustomer());
                    session.setAttribute("customerId", authResult.getCustomer().getCustomerId());
                }

                // Prepare response data
                Object userData = "ADMIN".equals(authResult.getUserType()) ? 
                                authResult.getAdminUser() : authResult.getCustomer();

                return ResponseEntity.ok(ApiResponse.success(authResult.getMessage(), 
                    new LoginResponse(authResult.getUserType(), userData)));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(authResult.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Login failed: " + e.getMessage()));
        }
    }

    /**
     * User logout endpoint
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            return ResponseEntity.ok(ApiResponse.success("Logout successful", "User logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Logout failed: " + e.getMessage()));
        }
    }

    /**
     * Check authentication status
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Object>> getAuthStatus(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null && Boolean.TRUE.equals(session.getAttribute("authenticated"))) {
                String userType = (String) session.getAttribute("userType");
                Object userData = null;
                
                if ("ADMIN".equals(userType)) {
                    userData = session.getAttribute("adminUser");
                } else {
                    userData = session.getAttribute("customer");
                }

                return ResponseEntity.ok(ApiResponse.success("User is authenticated", 
                    new LoginResponse(userType, userData)));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User is not authenticated"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to check authentication status: " + e.getMessage()));
        }
    }

    /**
     * Validate admin credentials for menu access
     */
    @PostMapping("/validate-admin")
    public ResponseEntity<ApiResponse<String>> validateAdmin(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            boolean isValid = authService.validateAdminCredentials(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );

            if (isValid) {
                return ResponseEntity.ok(ApiResponse.success("Admin credentials validated", "Valid"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Please Enter Correct UserName and Password"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Validation failed: " + e.getMessage()));
        }
    }

    /**
     * Change password endpoint
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody ChangePasswordRequest request, 
                                                             HttpServletRequest httpRequest) {
        try {
            HttpSession session = httpRequest.getSession(false);
            if (session == null || !Boolean.TRUE.equals(session.getAttribute("authenticated"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
            }

            String userType = (String) session.getAttribute("userType");
            boolean success = false;

            if ("ADMIN".equals(userType)) {
                String username = (String) session.getAttribute("username");
                success = authService.changeAdminPassword(username, request.getOldPassword(), request.getNewPassword());
            } else {
                String customerId = (String) session.getAttribute("customerId");
                success = authService.changeCustomerPassword(customerId, request.getOldPassword(), request.getNewPassword());
            }

            if (success) {
                return ResponseEntity.ok(ApiResponse.success("Password changed successfully", "Success"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to change password"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Password change failed: " + e.getMessage()));
        }
    }

    /**
     * Login response DTO
     */
    public static class LoginResponse {
        private String userType;
        private Object userData;

        public LoginResponse(String userType, Object userData) {
            this.userType = userType;
            this.userData = userData;
        }

        // Getters and setters
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
        public Object getUserData() { return userData; }
        public void setUserData(Object userData) { this.userData = userData; }
    }

    /**
     * Change password request DTO
     */
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;

        // Getters and setters
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
