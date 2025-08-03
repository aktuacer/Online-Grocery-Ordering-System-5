package com.grocery.ordering.service;

import com.grocery.ordering.model.AdminUser;
import com.grocery.ordering.model.Customer;
import com.grocery.ordering.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service layer for Authentication operations
 * Implements secure login and session management
 * 
 * @author Chirag Singhal (chirag127)
 */
@Service
@Transactional
public class AuthService {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Authenticate admin user
     */
    @Transactional(readOnly = true)
    public Optional<AdminUser> authenticateAdmin(String username, String password) {
        Optional<AdminUser> adminOpt = adminUserRepository.findByUsername(username);
        
        if (adminOpt.isPresent()) {
            AdminUser admin = adminOpt.get();
            if (passwordEncoder.matches(password, admin.getPassword())) {
                // Don't return password in response
                admin.setPassword(null);
                return Optional.of(admin);
            }
        }
        
        return Optional.empty();
    }

    /**
     * Authenticate customer user
     */
    @Transactional(readOnly = true)
    public Optional<Customer> authenticateCustomer(String email, String password) {
        return customerService.authenticateCustomer(email, password);
    }

    /**
     * Generic login method that determines user type and authenticates
     */
    @Transactional(readOnly = true)
    public AuthResult login(String username, String password, String userType) {
        if (userType == null || userType.trim().isEmpty()) {
            // Try to determine user type based on username format
            if (username.contains("@")) {
                userType = "CUSTOMER";
            } else {
                userType = "ADMIN";
            }
        }

        userType = userType.toUpperCase();

        switch (userType) {
            case "ADMIN":
                Optional<AdminUser> adminOpt = authenticateAdmin(username, password);
                if (adminOpt.isPresent()) {
                    return new AuthResult(true, "Admin login successful", adminOpt.get(), null, "ADMIN");
                }
                break;
                
            case "CUSTOMER":
                Optional<Customer> customerOpt = authenticateCustomer(username, password);
                if (customerOpt.isPresent()) {
                    return new AuthResult(true, "Customer login successful", null, customerOpt.get(), "CUSTOMER");
                }
                break;
                
            default:
                return new AuthResult(false, "Invalid user type", null, null, null);
        }

        return new AuthResult(false, "Please Enter Correct UserName and Password", null, null, null);
    }

    /**
     * Validate admin credentials for menu access
     */
    @Transactional(readOnly = true)
    public boolean validateAdminCredentials(String username, String password) {
        return authenticateAdmin(username, password).isPresent();
    }

    /**
     * Change admin password
     */
    public boolean changeAdminPassword(String username, String oldPassword, String newPassword) {
        Optional<AdminUser> adminOpt = authenticateAdmin(username, oldPassword);
        if (adminOpt.isEmpty()) {
            throw new RuntimeException("Invalid current password");
        }

        // Validate new password
        validatePassword(newPassword);

        String encodedPassword = passwordEncoder.encode(newPassword);
        return adminUserRepository.updatePassword(username, encodedPassword);
    }

    /**
     * Change customer password
     */
    public boolean changeCustomerPassword(String customerId, String oldPassword, String newPassword) {
        Optional<Customer> customerOpt = customerService.findByCustomerId(customerId);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }

        Customer customer = customerOpt.get();
        
        // Verify old password
        if (!passwordEncoder.matches(oldPassword, customer.getPassword())) {
            throw new RuntimeException("Invalid current password");
        }

        // Validate new password
        validatePassword(newPassword);

        // Update password
        customer.setPassword(newPassword);
        customerService.updateCustomer(customerId, customer);
        
        return true;
    }

    /**
     * Validate password strength
     */
    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);

        if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
            throw new RuntimeException("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
        }
    }

    /**
     * Authentication result class
     */
    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final AdminUser adminUser;
        private final Customer customer;
        private final String userType;

        public AuthResult(boolean success, String message, AdminUser adminUser, Customer customer, String userType) {
            this.success = success;
            this.message = message;
            this.adminUser = adminUser;
            this.customer = customer;
            this.userType = userType;
        }

        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public AdminUser getAdminUser() { return adminUser; }
        public Customer getCustomer() { return customer; }
        public String getUserType() { return userType; }
    }
}
