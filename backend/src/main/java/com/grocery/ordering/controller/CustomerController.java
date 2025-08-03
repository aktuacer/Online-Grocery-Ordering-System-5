package com.grocery.ordering.controller;

import com.grocery.ordering.dto.ApiResponse;
import com.grocery.ordering.model.Customer;
import com.grocery.ordering.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Customer operations
 * Handles customer registration, updates, and search
 * 
 * @author Chirag Singhal (chirag127)
 */
@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * Customer registration endpoint (US002)
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Customer>> registerCustomer(@Valid @RequestBody Customer customer) {
        try {
            Customer registeredCustomer = customerService.registerCustomer(customer);
            // Don't return password in response
            registeredCustomer.setPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer registered successfully", registeredCustomer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Customer registration failed: " + e.getMessage()));
        }
    }

    /**
     * Update customer details endpoint (US003)
     */
    @PutMapping("/{customerId}")
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(@PathVariable String customerId,
                                                               @Valid @RequestBody Customer customer,
                                                               HttpServletRequest request) {
        try {
            // Check authentication and authorization
            if (!isAuthorized(request, customerId, "CUSTOMER")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied"));
            }

            Customer updatedCustomer = customerService.updateCustomer(customerId, customer);
            // Don't return password in response
            updatedCustomer.setPassword(null);
            return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", updatedCustomer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Customer update failed: " + e.getMessage()));
        }
    }

    /**
     * Get customer details by ID
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<Customer>> getCustomer(@PathVariable String customerId,
                                                            HttpServletRequest request) {
        try {
            // Check authentication and authorization
            if (!isAuthorized(request, customerId, "CUSTOMER")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied"));
            }

            Optional<Customer> customerOpt = customerService.findByCustomerId(customerId);
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                // Don't return password in response
                customer.setPassword(null);
                return ResponseEntity.ok(ApiResponse.success("Customer found", customer));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Customer not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve customer: " + e.getMessage()));
        }
    }

    /**
     * Search customers by name endpoint (US005) - Admin only
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Customer>>> searchCustomers(@RequestParam String name,
                                                                      HttpServletRequest request) {
        try {
            // Check admin authentication
            if (!isAdminAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required"));
            }

            List<Customer> customers = customerService.searchCustomersByName(name);
            return ResponseEntity.ok(ApiResponse.success("Customers found", customers));
        } catch (Exception e) {
            if (e.getMessage().contains("Customer not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Customer search failed: " + e.getMessage()));
        }
    }

    /**
     * Get all customers endpoint - Admin only
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers(HttpServletRequest request) {
        try {
            // Check admin authentication
            if (!isAdminAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required"));
            }

            List<Customer> customers = customerService.getAllCustomers();
            return ResponseEntity.ok(ApiResponse.success("All customers retrieved", customers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve customers: " + e.getMessage()));
        }
    }

    /**
     * Delete customer endpoint - Admin only
     */
    @DeleteMapping("/{customerId}")
    public ResponseEntity<ApiResponse<String>> deleteCustomer(@PathVariable String customerId,
                                                             HttpServletRequest request) {
        try {
            // Check admin authentication
            if (!isAdminAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required"));
            }

            boolean deleted = customerService.deleteCustomer(customerId);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully", "Deleted"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Customer not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Customer deletion failed: " + e.getMessage()));
        }
    }

    /**
     * Check if email exists endpoint
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(@RequestParam String email) {
        try {
            boolean exists = customerService.emailExists(email);
            return ResponseEntity.ok(ApiResponse.success("Email check completed", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Email check failed: " + e.getMessage()));
        }
    }

    /**
     * Check if user is authorized to access customer data
     */
    private boolean isAuthorized(HttpServletRequest request, String customerId, String requiredUserType) {
        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute("authenticated"))) {
            return false;
        }

        String userType = (String) session.getAttribute("userType");
        
        // Admin can access any customer data
        if ("ADMIN".equals(userType)) {
            return true;
        }

        // Customer can only access their own data
        if (requiredUserType.equals(userType)) {
            String sessionCustomerId = (String) session.getAttribute("customerId");
            return customerId.equals(sessionCustomerId);
        }

        return false;
    }

    /**
     * Check if user is authenticated as admin
     */
    private boolean isAdminAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute("authenticated"))) {
            return false;
        }

        String userType = (String) session.getAttribute("userType");
        return "ADMIN".equals(userType);
    }
}
