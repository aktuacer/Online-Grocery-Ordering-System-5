package com.grocery.ordering.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product entity representing product information
 * 
 * @author Chirag Singhal (chirag127)
 */
public class Product {
    
    private Integer productId;
    
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String productName;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
    
    @Min(value = 0, message = "Reserved quantity cannot be negative")
    private Integer reserved = 0;
    
    private String customerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Product() {}

    // Constructor with essential fields
    public Product(String productName, BigDecimal price, Integer quantity) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.reserved = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with all fields
    public Product(Integer productId, String productName, BigDecimal price, 
                  Integer quantity, Integer reserved, String customerId) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.reserved = reserved != null ? reserved : 0;
        this.customerId = customerId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getReserved() {
        return reserved;
    }

    public void setReserved(Integer reserved) {
        this.reserved = reserved;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Check if product is available for ordering
     */
    public boolean isAvailable() {
        return quantity != null && quantity > 0 && (reserved == null || quantity > reserved);
    }

    /**
     * Get available quantity for ordering
     */
    public int getAvailableQuantity() {
        if (quantity == null) return 0;
        return Math.max(0, quantity - (reserved != null ? reserved : 0));
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", reserved=" + reserved +
                ", customerId='" + customerId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
