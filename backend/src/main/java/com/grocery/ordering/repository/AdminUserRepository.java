package com.grocery.ordering.repository;

import com.grocery.ordering.model.AdminUser;
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

/**
 * Repository for AdminUser entity with JDBC implementation
 * Implements SQL injection prevention using prepared statements
 * 
 * @author Chirag Singhal (chirag127)
 */
@Repository
public class AdminUserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<AdminUser> adminUserRowMapper = new AdminUserRowMapper();

    /**
     * Find admin user by username with SQL injection prevention
     */
    public Optional<AdminUser> findByUsername(String username) {
        String sql = "SELECT * FROM admin_users WHERE username = ?";
        try {
            AdminUser adminUser = jdbcTemplate.queryForObject(sql, adminUserRowMapper, username);
            return Optional.ofNullable(adminUser);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Find admin user by ID with SQL injection prevention
     */
    public Optional<AdminUser> findById(Integer id) {
        String sql = "SELECT * FROM admin_users WHERE id = ?";
        try {
            AdminUser adminUser = jdbcTemplate.queryForObject(sql, adminUserRowMapper, id);
            return Optional.ofNullable(adminUser);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Save admin user with SQL injection prevention
     */
    public AdminUser save(AdminUser adminUser) {
        String sql = "INSERT INTO admin_users (username, password, email, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        LocalDateTime now = LocalDateTime.now();
        adminUser.setCreatedAt(now);
        adminUser.setUpdatedAt(now);
        
        jdbcTemplate.update(sql,
                adminUser.getUsername(),
                adminUser.getPassword(),
                adminUser.getEmail(),
                adminUser.getCreatedAt(),
                adminUser.getUpdatedAt());
        
        return adminUser;
    }

    /**
     * Update admin user with SQL injection prevention
     */
    public AdminUser update(AdminUser adminUser) {
        String sql = "UPDATE admin_users SET username = ?, password = ?, email = ?, updated_at = ? WHERE id = ?";
        
        adminUser.setUpdatedAt(LocalDateTime.now());
        
        int rowsAffected = jdbcTemplate.update(sql,
                adminUser.getUsername(),
                adminUser.getPassword(),
                adminUser.getEmail(),
                adminUser.getUpdatedAt(),
                adminUser.getId());
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Admin user not found with ID: " + adminUser.getId());
        }
        
        return adminUser;
    }

    /**
     * Find all admin users
     */
    public List<AdminUser> findAll() {
        String sql = "SELECT * FROM admin_users ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, adminUserRowMapper);
    }

    /**
     * Check if username exists
     */
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM admin_users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    /**
     * Check if email exists
     */
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM admin_users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    /**
     * Delete admin user by ID
     */
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM admin_users WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    /**
     * Update password with SQL injection prevention
     */
    public boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE admin_users SET password = ?, updated_at = ? WHERE username = ?";
        int rowsAffected = jdbcTemplate.update(sql, newPassword, LocalDateTime.now(), username);
        return rowsAffected > 0;
    }

    /**
     * Row mapper for AdminUser entity
     */
    private static class AdminUserRowMapper implements RowMapper<AdminUser> {
        @Override
        public AdminUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            AdminUser adminUser = new AdminUser();
            adminUser.setId(rs.getInt("id"));
            adminUser.setUsername(rs.getString("username"));
            adminUser.setPassword(rs.getString("password"));
            adminUser.setEmail(rs.getString("email"));
            
            // Handle timestamp conversion
            if (rs.getTimestamp("created_at") != null) {
                adminUser.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            if (rs.getTimestamp("updated_at") != null) {
                adminUser.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
            
            return adminUser;
        }
    }
}
