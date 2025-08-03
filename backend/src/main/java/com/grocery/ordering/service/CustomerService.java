package com.grocery.ordering.service;

import com.grocery.ordering.model.Customer;
import com.grocery.ordering.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Customer operations
 * Implements business logic and validation
 * 
 * @author Chirag Singhal (chirag127)
 */
@Service
@Transactional
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register a new customer with validation
     */
    public Customer registerCustomer(Customer customer) {
        // Validate email uniqueness
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Email already exists: " + customer.getEmail());
        }

        // Validate contact number format
        if (!customer.getContactNumber().matches("^\\d{10}$")) {
            throw new RuntimeException("Contact number must be exactly 10 digits");
        }

        // Validate password strength
        validatePassword(customer.getPassword());

        // Encrypt password
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));

        // Generate customer ID and save
        return customerRepository.save(customer);
    }

    /**
     * Update customer details with validation
     */
    public Customer updateCustomer(String customerId, Customer updatedCustomer) {
        Optional<Customer> existingCustomerOpt = customerRepository.findByCustomerId(customerId);
        if (existingCustomerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found with ID: " + customerId);
        }

        Customer existingCustomer = existingCustomerOpt.get();

        // Check if email is being changed and if new email already exists
        if (!existingCustomer.getEmail().equals(updatedCustomer.getEmail())) {
            if (customerRepository.existsByEmail(updatedCustomer.getEmail())) {
                throw new RuntimeException("Email already exists: " + updatedCustomer.getEmail());
            }
        }

        // Validate contact number if changed
        if (!updatedCustomer.getContactNumber().matches("^\\d{10}$")) {
            throw new RuntimeException("Contact number must be exactly 10 digits");
        }

        // Update fields
        existingCustomer.setFullName(updatedCustomer.getFullName());
        existingCustomer.setEmail(updatedCustomer.getEmail());
        existingCustomer.setAddress(updatedCustomer.getAddress());
        existingCustomer.setContactNumber(updatedCustomer.getContactNumber());

        // Update password if provided
        if (updatedCustomer.getPassword() != null && !updatedCustomer.getPassword().isEmpty()) {
            validatePassword(updatedCustomer.getPassword());
            existingCustomer.setPassword(passwordEncoder.encode(updatedCustomer.getPassword()));
        }

        return customerRepository.update(existingCustomer);
    }

    /**
     * Find customer by ID
     */
    @Transactional(readOnly = true)
    public Optional<Customer> findByCustomerId(String customerId) {
        return customerRepository.findByCustomerId(customerId);
    }

    /**
     * Find customer by email
     */
    @Transactional(readOnly = true)
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    /**
     * Search customers by name (admin only)
     */
    @Transactional(readOnly = true)
    public List<Customer> searchCustomersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Search name cannot be empty");
        }
        
        List<Customer> customers = customerRepository.searchByName(name.trim());
        
        if (customers.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }

        // Mask passwords for security
        customers.forEach(customer -> customer.setPassword("********"));
        
        return customers;
    }

    /**
     * Get all customers (admin only)
     */
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        // Mask passwords for security
        customers.forEach(customer -> customer.setPassword("********"));
        return customers;
    }

    /**
     * Authenticate customer login
     */
    @Transactional(readOnly = true)
    public Optional<Customer> authenticateCustomer(String email, String password) {
        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            if (passwordEncoder.matches(password, customer.getPassword())) {
                // Don't return password in response
                customer.setPassword(null);
                return Optional.of(customer);
            }
        }
        
        return Optional.empty();
    }

    /**
     * Delete customer (admin only)
     */
    public boolean deleteCustomer(String customerId) {
        if (!customerRepository.findByCustomerId(customerId).isPresent()) {
            throw new RuntimeException("Customer not found with ID: " + customerId);
        }
        return customerRepository.deleteByCustomerId(customerId);
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
     * Check if customer exists
     */
    @Transactional(readOnly = true)
    public boolean customerExists(String customerId) {
        return customerRepository.findByCustomerId(customerId).isPresent();
    }

    /**
     * Check if email exists
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return customerRepository.existsByEmail(email);
    }
}
