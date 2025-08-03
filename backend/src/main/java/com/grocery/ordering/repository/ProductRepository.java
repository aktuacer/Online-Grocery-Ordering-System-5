package com.grocery.ordering.repository;

import com.grocery.ordering.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Product entity with JDBC implementation
 * Implements SQL injection prevention using prepared statements
 * 
 * @author Chirag Singhal (chirag127)
 */
@Repository
public class ProductRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Product> productRowMapper = new ProductRowMapper();

    /**
     * Save product with SQL injection prevention
     */
    public Product save(Product product) {
        String sql = "INSERT INTO products (product_name, price, quantity, reserved, customer_id, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        LocalDateTime now = LocalDateTime.now();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, product.getProductName());
            ps.setBigDecimal(2, product.getPrice());
            ps.setInt(3, product.getQuantity());
            ps.setInt(4, product.getReserved() != null ? product.getReserved() : 0);
            ps.setString(5, product.getCustomerId());
            ps.setObject(6, product.getCreatedAt());
            ps.setObject(7, product.getUpdatedAt());
            return ps;
        }, keyHolder);
        
        if (keyHolder.getKey() != null) {
            product.setProductId(keyHolder.getKey().intValue());
        }
        
        return product;
    }

    /**
     * Find product by ID with SQL injection prevention
     */
    public Optional<Product> findById(Integer productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try {
            Product product = jdbcTemplate.queryForObject(sql, productRowMapper, productId);
            return Optional.ofNullable(product);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Update product with SQL injection prevention
     */
    public Product update(Product product) {
        String sql = "UPDATE products SET product_name = ?, price = ?, quantity = ?, reserved = ?, customer_id = ?, updated_at = ? " +
                    "WHERE product_id = ?";
        
        product.setUpdatedAt(LocalDateTime.now());
        
        int rowsAffected = jdbcTemplate.update(sql,
                product.getProductName(),
                product.getPrice(),
                product.getQuantity(),
                product.getReserved(),
                product.getCustomerId(),
                product.getUpdatedAt(),
                product.getProductId());
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Product not found with ID: " + product.getProductId());
        }
        
        return product;
    }

    /**
     * Search products by name with SQL injection prevention (case-insensitive)
     */
    public List<Product> searchByName(String name) {
        String sql = "SELECT * FROM products WHERE LOWER(product_name) LIKE LOWER(?) AND quantity > 0 ORDER BY product_name";
        return jdbcTemplate.query(sql, productRowMapper, "%" + name + "%");
    }

    /**
     * Find all products
     */
    public List<Product> findAll() {
        String sql = "SELECT * FROM products ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, productRowMapper);
    }

    /**
     * Find available products (quantity > 0)
     */
    public List<Product> findAvailableProducts() {
        String sql = "SELECT * FROM products WHERE quantity > 0 ORDER BY product_name";
        return jdbcTemplate.query(sql, productRowMapper);
    }

    /**
     * Delete product by ID with SQL injection prevention
     */
    public boolean deleteById(Integer productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, productId);
        return rowsAffected > 0;
    }

    /**
     * Check if product exists by ID
     */
    public boolean existsById(Integer productId) {
        String sql = "SELECT COUNT(*) FROM products WHERE product_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, productId);
        return count != null && count > 0;
    }

    /**
     * Update product quantity with SQL injection prevention
     */
    public boolean updateQuantity(Integer productId, Integer newQuantity) {
        String sql = "UPDATE products SET quantity = ?, updated_at = ? WHERE product_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, newQuantity, LocalDateTime.now(), productId);
        return rowsAffected > 0;
    }

    /**
     * Reserve product quantity for order
     */
    public boolean reserveQuantity(Integer productId, Integer quantityToReserve) {
        String sql = "UPDATE products SET reserved = reserved + ?, updated_at = ? WHERE product_id = ? AND (quantity - reserved) >= ?";
        int rowsAffected = jdbcTemplate.update(sql, quantityToReserve, LocalDateTime.now(), productId, quantityToReserve);
        return rowsAffected > 0;
    }

    /**
     * Release reserved quantity
     */
    public boolean releaseReservedQuantity(Integer productId, Integer quantityToRelease) {
        String sql = "UPDATE products SET reserved = GREATEST(0, reserved - ?), updated_at = ? WHERE product_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, quantityToRelease, LocalDateTime.now(), productId);
        return rowsAffected > 0;
    }

    /**
     * Row mapper for Product entity
     */
    private static class ProductRowMapper implements RowMapper<Product> {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            Product product = new Product();
            product.setProductId(rs.getInt("product_id"));
            product.setProductName(rs.getString("product_name"));
            product.setPrice(rs.getBigDecimal("price"));
            product.setQuantity(rs.getInt("quantity"));
            product.setReserved(rs.getInt("reserved"));
            product.setCustomerId(rs.getString("customer_id"));
            
            // Handle timestamp conversion
            if (rs.getTimestamp("created_at") != null) {
                product.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            if (rs.getTimestamp("updated_at") != null) {
                product.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
            
            return product;
        }
    }
}
