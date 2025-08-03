package com.grocery.ordering.repository;

import com.grocery.ordering.model.Order;
import com.grocery.ordering.model.Order.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Order entity with JDBC implementation
 * Implements SQL injection prevention using prepared statements
 * 
 * @author Chirag Singhal (chirag127)
 */
@Repository
public class OrderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Order> orderRowMapper = new OrderRowMapper();
    private final RowMapper<Order> orderDetailRowMapper = new OrderDetailRowMapper();

    /**
     * Save order with SQL injection prevention
     */
    public Order save(Order order) {
        String sql = "INSERT INTO orders (customer_id, product_id, order_date, order_amount, quantity_ordered, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, order.getCustomerId());
            ps.setInt(2, order.getProductId());
            ps.setObject(3, order.getOrderDate());
            ps.setBigDecimal(4, order.getOrderAmount());
            ps.setInt(5, order.getQuantityOrdered());
            ps.setString(6, order.getStatus().getValue());
            return ps;
        }, keyHolder);
        
        if (keyHolder.getKey() != null) {
            order.setOrderId(keyHolder.getKey().intValue());
        }
        
        return order;
    }

    /**
     * Find order by ID with SQL injection prevention
     */
    public Optional<Order> findById(Integer orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try {
            Order order = jdbcTemplate.queryForObject(sql, orderRowMapper, orderId);
            return Optional.ofNullable(order);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Find orders by customer ID with SQL injection prevention
     */
    public List<Order> findByCustomerId(String customerId) {
        String sql = "SELECT o.*, c.full_name as customer_name, p.product_name " +
                    "FROM orders o " +
                    "JOIN customers c ON o.customer_id = c.customer_id " +
                    "JOIN products p ON o.product_id = p.product_id " +
                    "WHERE o.customer_id = ? " +
                    "ORDER BY o.order_date DESC";
        return jdbcTemplate.query(sql, orderDetailRowMapper, customerId);
    }

    /**
     * Get customer order details with full information
     */
    public List<Order> getCustomerOrderDetails(String customerId) {
        String sql = "SELECT * FROM customer_order_summary WHERE customer_id = ? ORDER BY order_date DESC";
        return jdbcTemplate.query(sql, orderDetailRowMapper, customerId);
    }

    /**
     * Find all orders with customer and product details
     */
    public List<Order> findAllWithDetails() {
        String sql = "SELECT o.*, c.full_name as customer_name, p.product_name " +
                    "FROM orders o " +
                    "JOIN customers c ON o.customer_id = c.customer_id " +
                    "JOIN products p ON o.product_id = p.product_id " +
                    "ORDER BY o.order_date DESC";
        return jdbcTemplate.query(sql, orderDetailRowMapper);
    }

    /**
     * Update order status with SQL injection prevention
     */
    public boolean updateOrderStatus(Integer orderId, OrderStatus status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, status.getValue(), orderId);
        return rowsAffected > 0;
    }

    /**
     * Find orders by status
     */
    public List<Order> findByStatus(OrderStatus status) {
        String sql = "SELECT o.*, c.full_name as customer_name, p.product_name " +
                    "FROM orders o " +
                    "JOIN customers c ON o.customer_id = c.customer_id " +
                    "JOIN products p ON o.product_id = p.product_id " +
                    "WHERE o.status = ? " +
                    "ORDER BY o.order_date DESC";
        return jdbcTemplate.query(sql, orderDetailRowMapper, status.getValue());
    }

    /**
     * Find orders by date range
     */
    public List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT o.*, c.full_name as customer_name, p.product_name " +
                    "FROM orders o " +
                    "JOIN customers c ON o.customer_id = c.customer_id " +
                    "JOIN products p ON o.product_id = p.product_id " +
                    "WHERE o.order_date BETWEEN ? AND ? " +
                    "ORDER BY o.order_date DESC";
        return jdbcTemplate.query(sql, orderDetailRowMapper, startDate, endDate);
    }

    /**
     * Delete order by ID
     */
    public boolean deleteById(Integer orderId) {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, orderId);
        return rowsAffected > 0;
    }

    /**
     * Check if order exists
     */
    public boolean existsById(Integer orderId) {
        String sql = "SELECT COUNT(*) FROM orders WHERE order_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, orderId);
        return count != null && count > 0;
    }

    /**
     * Row mapper for Order entity
     */
    private static class OrderRowMapper implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setOrderId(rs.getInt("order_id"));
            order.setCustomerId(rs.getString("customer_id"));
            order.setProductId(rs.getInt("product_id"));
            order.setOrderAmount(rs.getBigDecimal("order_amount"));
            order.setQuantityOrdered(rs.getInt("quantity_ordered"));
            
            // Handle timestamp conversion
            if (rs.getTimestamp("order_date") != null) {
                order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
            }
            
            // Handle status conversion
            String statusValue = rs.getString("status");
            if (statusValue != null) {
                order.setStatus(OrderStatus.fromString(statusValue));
            }
            
            return order;
        }
    }

    /**
     * Row mapper for Order entity with customer and product details
     */
    private static class OrderDetailRowMapper implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setOrderId(rs.getInt("order_id"));
            order.setCustomerId(rs.getString("customer_id"));
            order.setProductId(rs.getInt("product_id"));
            order.setOrderAmount(rs.getBigDecimal("order_amount"));
            order.setQuantityOrdered(rs.getInt("quantity_ordered"));
            
            // Handle timestamp conversion
            if (rs.getTimestamp("order_date") != null) {
                order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
            }
            
            // Handle status conversion
            String statusValue = rs.getString("status");
            if (statusValue != null) {
                order.setStatus(OrderStatus.fromString(statusValue));
            }
            
            // Additional details
            try {
                order.setCustomerName(rs.getString("customer_name"));
                order.setProductName(rs.getString("product_name"));
            } catch (SQLException e) {
                // These fields might not be present in all queries
            }
            
            return order;
        }
    }
}
