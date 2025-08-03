package com.grocery.ordering.repository;

import com.grocery.ordering.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Repository for Customer entity with JDBC implementation
 * Implements SQL injection prevention using prepared statements
 * 
 * @author Chirag Singhal (chirag127)
 */
@Repository
public class CustomerRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Customer> customerRowMapper = new CustomerRowMapper();

    /**
     * Generate unique 6-digit customer ID
     */
    public String generateCustomerId() {
        String customerId;
        do {
            Random random = new Random();
            int id = 100000 + random.nextInt(900000);
            customerId = "CUS" + String.valueOf(id).substring(0, 3);
        } while (findByCustomerId(customerId).isPresent());
        return customerId;
    }

    /**
     * Save customer with SQL injection prevention
     */
    public Customer save(Customer customer) {
        if (customer.getCustomerId() == null) {
            customer.setCustomerId(generateCustomerId());
        }
        
        String sql = "INSERT INTO customers (customer_id, full_name, email, password, address, contact_number, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        LocalDateTime now = LocalDateTime.now();
        customer.setCreatedAt(now);
        customer.setUpdatedAt(now);
        
        jdbcTemplate.update(sql,
                customer.getCustomerId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPassword(),
                customer.getAddress(),
                customer.getContactNumber(),
                customer.getCreatedAt(),
                customer.getUpdatedAt());
        
        return customer;
    }

    /**
     * Find customer by ID with SQL injection prevention
     */
    public Optional<Customer> findByCustomerId(String customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try {
            Customer customer = jdbcTemplate.queryForObject(sql, customerRowMapper, customerId);
            return Optional.ofNullable(customer);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Find customer by email with SQL injection prevention
     */
    public Optional<Customer> findByEmail(String email) {
        String sql = "SELECT * FROM customers WHERE email = ?";
        try {
            Customer customer = jdbcTemplate.queryForObject(sql, customerRowMapper, email);
            return Optional.ofNullable(customer);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Update customer with SQL injection prevention
     */
    public Customer update(Customer customer) {
        String sql = "UPDATE customers SET full_name = ?, email = ?, password = ?, address = ?, contact_number = ?, updated_at = ? " +
                    "WHERE customer_id = ?";
        
        customer.setUpdatedAt(LocalDateTime.now());
        
        int rowsAffected = jdbcTemplate.update(sql,
                customer.getFullName(),
                customer.getEmail(),
                customer.getPassword(),
                customer.getAddress(),
                customer.getContactNumber(),
                customer.getUpdatedAt(),
                customer.getCustomerId());
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Customer not found with ID: " + customer.getCustomerId());
        }
        
        return customer;
    }

    /**
     * Search customers by name with SQL injection prevention (case-insensitive)
     */
    public List<Customer> searchByName(String name) {
        String sql = "SELECT * FROM customers WHERE LOWER(full_name) LIKE LOWER(?) ORDER BY full_name";
        return jdbcTemplate.query(sql, customerRowMapper, "%" + name + "%");
    }

    /**
     * Find all customers
     */
    public List<Customer> findAll() {
        String sql = "SELECT * FROM customers ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, customerRowMapper);
    }

    /**
     * Check if email exists
     */
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM customers WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    /**
     * Delete customer by ID
     */
    public boolean deleteByCustomerId(String customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, customerId);
        return rowsAffected > 0;
    }

    /**
     * Row mapper for Customer entity
     */
    private static class CustomerRowMapper implements RowMapper<Customer> {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer customer = new Customer();
            customer.setCustomerId(rs.getString("customer_id"));
            customer.setFullName(rs.getString("full_name"));
            customer.setEmail(rs.getString("email"));
            customer.setPassword(rs.getString("password"));
            customer.setAddress(rs.getString("address"));
            customer.setContactNumber(rs.getString("contact_number"));
            
            // Handle timestamp conversion
            if (rs.getTimestamp("created_at") != null) {
                customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            if (rs.getTimestamp("updated_at") != null) {
                customer.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
            
            return customer;
        }
    }
}
