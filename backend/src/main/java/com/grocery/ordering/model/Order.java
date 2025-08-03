package com.grocery.ordering.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order entity representing customer orders
 * 
 * @author Chirag Singhal (chirag127)
 */
public class Order {
    
    private Integer orderId;
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    @NotNull(message = "Product ID is required")
    private Integer productId;
    
    private LocalDateTime orderDate;
    
    @NotNull(message = "Order amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Order amount must be greater than 0")
    private BigDecimal orderAmount;
    
    @NotNull(message = "Quantity ordered is required")
    @Min(value = 1, message = "Quantity ordered must be at least 1")
    private Integer quantityOrdered;
    
    private OrderStatus status;
    
    // Additional fields for order details
    private String customerName;
    private String productName;

    // Default constructor
    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    // Constructor with essential fields
    public Order(String customerId, Integer productId, BigDecimal orderAmount, Integer quantityOrdered) {
        this.customerId = customerId;
        this.productId = productId;
        this.orderAmount = orderAmount;
        this.quantityOrdered = quantityOrdered;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    // Constructor with all fields
    public Order(Integer orderId, String customerId, Integer productId, LocalDateTime orderDate,
                BigDecimal orderAmount, Integer quantityOrdered, OrderStatus status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.productId = productId;
        this.orderDate = orderDate;
        this.orderAmount = orderAmount;
        this.quantityOrdered = quantityOrdered;
        this.status = status;
    }

    // Getters and Setters
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Integer getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(Integer quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customerId='" + customerId + '\'' +
                ", productId=" + productId +
                ", orderDate=" + orderDate +
                ", orderAmount=" + orderAmount +
                ", quantityOrdered=" + quantityOrdered +
                ", status=" + status +
                ", customerName='" + customerName + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }

    /**
     * Enum for order status
     */
    public enum OrderStatus {
        PENDING("PENDING"),
        CONFIRMED("CONFIRMED"),
        SHIPPED("SHIPPED"),
        DELIVERED("DELIVERED"),
        CANCELLED("CANCELLED");

        private final String value;

        OrderStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static OrderStatus fromString(String value) {
            for (OrderStatus status : OrderStatus.values()) {
                if (status.value.equalsIgnoreCase(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown order status: " + value);
        }
    }
}
