package com.grocery.ordering.service;

import com.grocery.ordering.model.Order;
import com.grocery.ordering.model.Order.OrderStatus;
import com.grocery.ordering.model.Product;
import com.grocery.ordering.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Order operations
 * Implements business logic and validation
 * 
 * @author Chirag Singhal (chirag127)
 */
@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CustomerService customerService;

    /**
     * Create a new order with validation
     */
    public Order createOrder(Order order) {
        // Validate customer exists
        if (!customerService.customerExists(order.getCustomerId())) {
            throw new RuntimeException("Customer not found with ID: " + order.getCustomerId());
        }

        // Validate product exists
        Optional<Product> productOpt = productService.findById(order.getProductId());
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found with ID: " + order.getProductId());
        }

        Product product = productOpt.get();

        // Validate quantity
        if (order.getQuantityOrdered() <= 0) {
            throw new RuntimeException("Quantity ordered must be greater than 0");
        }

        // Check product availability
        if (!productService.isProductAvailable(order.getProductId(), order.getQuantityOrdered())) {
            int availableQuantity = productService.getAvailableQuantity(order.getProductId());
            throw new RuntimeException("Insufficient quantity available. Available: " + availableQuantity + 
                                     ", Requested: " + order.getQuantityOrdered());
        }

        // Calculate order amount if not provided
        if (order.getOrderAmount() == null) {
            BigDecimal totalAmount = product.getPrice().multiply(new BigDecimal(order.getQuantityOrdered()));
            order.setOrderAmount(totalAmount);
        }

        // Validate order amount
        if (order.getOrderAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Order amount must be greater than 0");
        }

        // Reserve product quantity
        if (!productService.reserveProductQuantity(order.getProductId(), order.getQuantityOrdered())) {
            throw new RuntimeException("Failed to reserve product quantity");
        }

        try {
            // Create the order
            Order savedOrder = orderRepository.save(order);
            return savedOrder;
        } catch (Exception e) {
            // Release reserved quantity if order creation fails
            productService.releaseReservedQuantity(order.getProductId(), order.getQuantityOrdered());
            throw new RuntimeException("Failed to create order: " + e.getMessage());
        }
    }

    /**
     * Get customer order details
     */
    @Transactional(readOnly = true)
    public List<Order> getCustomerOrderDetails(String customerId) {
        if (!customerService.customerExists(customerId)) {
            throw new RuntimeException("Customer not found with ID: " + customerId);
        }
        
        return orderRepository.getCustomerOrderDetails(customerId);
    }

    /**
     * Find order by ID
     */
    @Transactional(readOnly = true)
    public Optional<Order> findById(Integer orderId) {
        return orderRepository.findById(orderId);
    }

    /**
     * Find orders by customer ID
     */
    @Transactional(readOnly = true)
    public List<Order> findByCustomerId(String customerId) {
        if (!customerService.customerExists(customerId)) {
            throw new RuntimeException("Customer not found with ID: " + customerId);
        }
        
        return orderRepository.findByCustomerId(customerId);
    }

    /**
     * Get all orders with details (admin only)
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrdersWithDetails() {
        return orderRepository.findAllWithDetails();
    }

    /**
     * Update order status
     */
    public boolean updateOrderStatus(Integer orderId, OrderStatus newStatus) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }

        Order order = orderOpt.get();
        OrderStatus currentStatus = order.getStatus();

        // Validate status transition
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        // Handle quantity release for cancelled orders
        if (newStatus == OrderStatus.CANCELLED && currentStatus != OrderStatus.CANCELLED) {
            productService.releaseReservedQuantity(order.getProductId(), order.getQuantityOrdered());
        }

        return orderRepository.updateOrderStatus(orderId, newStatus);
    }

    /**
     * Find orders by status
     */
    @Transactional(readOnly = true)
    public List<Order> findByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Find orders by date range
     */
    @Transactional(readOnly = true)
    public List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new RuntimeException("Start date cannot be after end date");
        }
        
        return orderRepository.findByDateRange(startDate, endDate);
    }

    /**
     * Cancel order
     */
    public boolean cancelOrder(Integer orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }

        Order order = orderOpt.get();
        
        // Check if order can be cancelled
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel order with status: " + order.getStatus());
        }

        // Release reserved quantity
        productService.releaseReservedQuantity(order.getProductId(), order.getQuantityOrdered());

        // Update status to cancelled
        return orderRepository.updateOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    /**
     * Delete order (admin only)
     */
    public boolean deleteOrder(Integer orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }

        Order order = orderOpt.get();
        
        // Release reserved quantity if order is not delivered or cancelled
        if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.CANCELLED) {
            productService.releaseReservedQuantity(order.getProductId(), order.getQuantityOrdered());
        }

        return orderRepository.deleteById(orderId);
    }

    /**
     * Check if order exists
     */
    @Transactional(readOnly = true)
    public boolean orderExists(Integer orderId) {
        return orderRepository.existsById(orderId);
    }

    /**
     * Validate status transition
     */
    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == newStatus) {
            return true;
        }

        return switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED;
            case SHIPPED -> newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.CANCELLED;
            case DELIVERED -> false; // Cannot change from delivered
            case CANCELLED -> false; // Cannot change from cancelled
        };
    }

    /**
     * Get order statistics (admin only)
     */
    @Transactional(readOnly = true)
    public OrderStatistics getOrderStatistics() {
        List<Order> allOrders = orderRepository.findAllWithDetails();
        
        long totalOrders = allOrders.size();
        long pendingOrders = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count();
        long confirmedOrders = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.CONFIRMED).count();
        long shippedOrders = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.SHIPPED).count();
        long deliveredOrders = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();
        long cancelledOrders = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count();
        
        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(Order::getOrderAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OrderStatistics(totalOrders, pendingOrders, confirmedOrders, 
                                 shippedOrders, deliveredOrders, cancelledOrders, totalRevenue);
    }

    /**
     * Inner class for order statistics
     */
    public static class OrderStatistics {
        private final long totalOrders;
        private final long pendingOrders;
        private final long confirmedOrders;
        private final long shippedOrders;
        private final long deliveredOrders;
        private final long cancelledOrders;
        private final BigDecimal totalRevenue;

        public OrderStatistics(long totalOrders, long pendingOrders, long confirmedOrders,
                             long shippedOrders, long deliveredOrders, long cancelledOrders,
                             BigDecimal totalRevenue) {
            this.totalOrders = totalOrders;
            this.pendingOrders = pendingOrders;
            this.confirmedOrders = confirmedOrders;
            this.shippedOrders = shippedOrders;
            this.deliveredOrders = deliveredOrders;
            this.cancelledOrders = cancelledOrders;
            this.totalRevenue = totalRevenue;
        }

        // Getters
        public long getTotalOrders() { return totalOrders; }
        public long getPendingOrders() { return pendingOrders; }
        public long getConfirmedOrders() { return confirmedOrders; }
        public long getShippedOrders() { return shippedOrders; }
        public long getDeliveredOrders() { return deliveredOrders; }
        public long getCancelledOrders() { return cancelledOrders; }
        public BigDecimal getTotalRevenue() { return totalRevenue; }
    }
}
